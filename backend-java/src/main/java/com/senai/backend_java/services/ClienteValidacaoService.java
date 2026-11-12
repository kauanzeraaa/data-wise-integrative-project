package com.senai.backend_java.services;

import static com.senai.backend_java.services.Colunas.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.senai.backend_java.dto.ClienteDTO;
import com.senai.backend_java.dto.UploadDTO.LinhaEnvio;

/**
 * Valida a planilha linha a linha, com as MESMAS regras do Pinia no front.
 *
 * <p>Cada linha termina com:
 * <ul>
 *   <li><b>erros</b>  – bloqueiam a linha (ela não segue para o Python);</li>
 *   <li><b>avisos</b> – só informam (ex.: "será padronizado"), a linha segue.</li>
 * </ul>
 * A padronização definitiva (IND. -> Indústria, ANA SOUZA -> Ana Souza...) é feita pelo ETL em Python;
 * aqui só avisamos o usuário de que ela vai acontecer.
 */
@Service
public class ClienteValidacaoService {

    /**
     * Resultado da validação de uma linha.
     *
     * @param numero  número da linha no Excel
     * @param valores coluna -> valor já limpo (espaços removidos, número/data normalizados)
     * @param erros   problemas que impedem a linha de ser gravada
     * @param avisos  observações que NÃO impedem a gravação
     */
    public record ResultadoLinha(int numero, Map<String, String> valores, List<String> erros, List<String> avisos) {
        /** Linha válida = nenhuma mensagem de erro. */
        public boolean valida() {
            return erros.isEmpty();
        }
    }

    private static final Pattern RE_CODIGO = Pattern.compile("^[A-Za-z0-9_-]{1,60}$");           // letras, números, '-' e '_' (até 60)
    private static final Pattern RE_UF = Pattern.compile("^[A-Za-z]{2}$");                        // sigla com 2 letras
    private static final Pattern DATA_ISO = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2})([T ].*)?$"); // aaaa-mm-dd (ignora hora, se houver)
    private static final DateTimeFormatter BR = DateTimeFormatter.ofPattern("d/M/uuuu");          // dd/mm/aaaa (aceita dia/mês com 1 dígito)
    private static final BigDecimal LIMITE_FATURAMENTO = new BigDecimal("1000000000000");         // 1 trilhão: acima disso é erro de digitação
    private static final Set<String> NIVEIS = Set.of("A", "B", "C");                              // níveis aceitos
    private static final int ANO_MINIMO = 1990;                                                   // datas antes disso são consideradas inválidas

    /**
     * Confere todas as linhas recebidas.
     * A ordem importa: a detecção de duplicados lembra a PRIMEIRA linha em que cada código apareceu.
     *
     * @param linhas linhas cruas da planilha
     * @return um ResultadoLinha para cada linha, na mesma ordem
     */
    public List<ResultadoLinha> validar(List<LinhaEnvio> linhas) {
        List<ResultadoLinha> saida = new ArrayList<>();
        Map<String, Integer> codigos = new HashMap<>();          // CÓDIGO (maiúsculo) -> 1ª linha em que apareceu
        Map<String, Integer> conteudos = new HashMap<>();        // "nome|faturamento|data" -> 1ª linha em que apareceu
        Map<String, String> codigoDoConteudo = new HashMap<>();  // "nome|faturamento|data" -> código usado na 1ª vez
        LocalDate hoje = LocalDate.now();                        // referência para o aviso de "data no futuro"

        for (int i = 0; i < linhas.size(); i++) {
            LinhaEnvio linha = linhas.get(i);
            // número da linha no Excel: usa o que o front mandou; se não veio, calcula (+2 = cabeçalho + índice 0)
            int numero = linha.numeroLinha() == null ? i + 2 : linha.numeroLinha();
            List<String> erros = new ArrayList<>();
            List<String> avisos = new ArrayList<>();
            Map<String, String> valores = new LinkedHashMap<>(); // LinkedHashMap mantém a ordem das colunas
            Map<String, String> campos = paraMapa(linha);         // coluna -> texto, para percorrer as colunas em laço

            // 1) Limpeza: tira espaços das pontas e espaços repetidos; confere o tamanho máximo
            for (String campo : TODAS) {
                String bruto = campos.get(campo);
                String limpo = bruto == null ? "" : bruto.strip().replaceAll("\\s{2,}", " ");
                if (bruto != null && !limpo.equals(bruto)) {
                    avisos.add(campo + ": espaços extras removidos");
                }
                valores.put(campo, limpo);
                Integer max = TAMANHO_MAXIMO.get(campo);
                if (max != null && limpo.length() > max) {
                    erros.add(campo + ": excede " + max + " caracteres");
                    valores.put(campo, limpo.substring(0, max)); // corta só para não estourar mensagens/logs
                }
            }

            // 2) Campos obrigatórios
            for (String campo : OBRIGATORIAS) {
                if (valores.get(campo).isEmpty()) erros.add(campo + ": campo obrigatório vazio");
            }

            // 3) Código: formato e duplicidade (sem diferenciar maiúscula/minúscula: cli-1 == CLI-1)
            String codigo = valores.get(CODIGO);
            if (!codigo.isEmpty()) {
                if (!RE_CODIGO.matcher(codigo).matches()) {
                    erros.add(CODIGO + ": use apenas letras, números, '-' ou '_'");
                }
                Integer primeira = codigos.putIfAbsent(codigo.toUpperCase(Locale.ROOT), numero); // devolve a linha anterior, se já existia
                if (primeira != null) {
                    erros.add(CODIGO + ": código duplicado (já usado na linha " + primeira + ")");
                }
            }

            // 3b) Segmento: precisa ser reconhecido; se estiver escrito diferente do oficial, só avisa
            String segmento = valores.get(SEGMENTO);
            if (!segmento.isEmpty()) {
                String padrao = Padronizacao.segmento(segmento);
                if (padrao == null) {
                    erros.add(SEGMENTO + ": '" + segmento
                            + "' não reconhecido (use Indústria, Comércio, Serviços, Saúde, Educação ou Tecnologia)");
                } else if (!padrao.equals(segmento)) {
                    avisos.add(SEGMENTO + ": '" + segmento + "' será padronizado para " + padrao);
                }
            }

            // 3c) Consultor: avisa se o nome será ajustado (ex.: "ANA SOUZA" -> "Ana Souza")
            String consultor = valores.get(CONSULTOR);
            if (!consultor.isEmpty()) {
                String ajustado = Padronizacao.tituloNome(consultor);
                if (!ajustado.equals(consultor)) {
                    avisos.add(CONSULTOR + ": '" + consultor + "' será padronizado para " + ajustado);
                }
            }

            // 4) Nível: exatamente A, B ou C (maiúsculo). Fora do padrão = erro, igual ao @Pattern do ClienteDTO.
            String nivel = valores.get(NIVEL);
            if (!nivel.isEmpty() && !NIVEIS.contains(nivel)) {
                erros.add(NIVEL + ": '" + nivel + "' inválido (use A, B ou C em maiúsculo)");
            }

            // 5) Faturamento: número MAIOR que zero e abaixo do limite (igual ao @Positive do ClienteDTO).
            String fat = valores.get(FATURAMENTO);
            if (!fat.isEmpty()) {
                Optional<BigDecimal> valor = lerDecimal(fat);
                if (valor.isEmpty()) {
                    erros.add(FATURAMENTO + ": '" + fat + "' não é um número");
                } else if (valor.get().signum() <= 0) {
                    erros.add(FATURAMENTO + ": deve ser maior do que zero");
                } else if (valor.get().compareTo(LIMITE_FATURAMENTO) >= 0) {
                    erros.add(FATURAMENTO + ": valor acima do permitido");
                } else {
                    // normaliza para "1850000.00" (ponto decimal, 2 casas) — formato que o Python espera
                    valores.put(FATURAMENTO, valor.get().setScale(2, RoundingMode.HALF_UP).toPlainString());
                }
            }

            // 6) Serviços: lista separada por ';' — pelo menos um item não vazio
            String servicos = valores.get(SERVICOS);
            if (!servicos.isEmpty() && Arrays.stream(servicos.split(";")).allMatch(String::isBlank)) {
                erros.add(SERVICOS + ": nenhum serviço informado");
            }

            // 7) Data de contratação: dd/mm/aaaa ou aaaa-mm-dd; data futura só gera aviso
            String data = valores.get(DATA);
            if (!data.isEmpty()) {
                Optional<LocalDate> d = lerData(data);
                if (d.isEmpty() || d.get().getYear() < ANO_MINIMO) {
                    erros.add(DATA + ": '" + data + "' não é uma data válida (use dd/mm/aaaa ou aaaa-mm-dd)");
                } else {
                    if (d.get().isAfter(hoje)) avisos.add(DATA + ": data no futuro");
                    valores.put(DATA, d.get().toString()); // normaliza para aaaa-mm-dd
                }
            }

            // 8) UF: opcional, mas se vier precisa ter 2 letras
            String uf = valores.get(UF);
            if (!uf.isEmpty() && !RE_UF.matcher(uf).matches()) {
                erros.add(UF + ": '" + uf + "' inválida (use a sigla com 2 letras)");
            }

            // 9) Mesmo cliente (nome + faturamento + data) com OUTRO código: só aviso.
            //    Roda depois dos passos 5 e 7 para comparar valores já normalizados.
            if (!codigo.isEmpty() && !valores.get(NOME).isEmpty()) {
                String chave = valores.get(NOME).toLowerCase(Locale.ROOT) + "|" + valores.get(FATURAMENTO) + "|" + valores.get(DATA);
                Integer anterior = conteudos.putIfAbsent(chave, numero);
                if (anterior != null && !codigo.equalsIgnoreCase(codigoDoConteudo.get(chave))) {
                    avisos.add(NOME + ": possível duplicidade (mesmo nome, faturamento e data da linha " + anterior + ")");
                }
                codigoDoConteudo.putIfAbsent(chave, codigo);
            }

            saida.add(new ResultadoLinha(numero, valores, erros, avisos));
        }
        return saida;
    }

    /**
     * Converte texto em número aceitando formatos brasileiro e americano.
     * Exemplos aceitos: 1850000 | 1850000.50 | 1.850.000,50 | R$ 1.850.000,50 | 1,850,000.50
     *
     * @return número ou Optional.empty() se o texto não for numérico
     */
    static Optional<BigDecimal> lerDecimal(String texto) {
        String t = texto.replace("R$", "")
                .replace('\u00A0', ' ')           // espaço "não separável" (comum ao copiar do Excel)
                .replaceAll("\\s", "");
        if (t.isEmpty() || !t.matches("^-?[0-9.,]+$")) return Optional.empty();
        boolean ponto = t.contains("."), virgula = t.contains(",");
        if (ponto && virgula) {
            // os dois aparecem: o ÚLTIMO símbolo é o separador decimal
            if (t.lastIndexOf(',') > t.lastIndexOf('.')) t = t.replace(".", "").replace(',', '.');
            else t = t.replace(",", "");
        } else if (virgula) {
            // só vírgula: várias = separador de milhar; uma só = decimal
            t = t.chars().filter(c -> c == ',').count() > 1 ? t.replace(",", "") : t.replace(',', '.');
        } else if (ponto && t.matches("^-?\\d{1,3}(\\.\\d{3})+$")) {
            // só ponto em grupos de 3 (1.850.000): separador de milhar
            t = t.replace(".", "");
        }
        try {
            return Optional.of(new BigDecimal(t));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Lê datas em aaaa-mm-dd (com ou sem hora) ou dd/mm/aaaa.
     *
     * @return data ou Optional.empty() se inválida/inexistente (ex.: 31/02/2024)
     */
    static Optional<LocalDate> lerData(String texto) {
        try {
            var iso = DATA_ISO.matcher(texto);
            if (iso.matches()) return Optional.of(LocalDate.parse(iso.group(1)));
            return Optional.of(LocalDate.parse(texto, BR));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Usado por POST /api/clientes/validar: valida a lista inteira e devolve os clientes já padronizados.
     * Diferente do upload, aqui é "tudo ou nada": a primeira linha com erro aborta com 400.
     *
     * @throws IllegalArgumentException se alguma linha tiver erro (vira HTTP 400 no ErroHandler)
     */
    public List<ClienteDTO> validarEPreparar(List<ClienteDTO> clientes) {
        // 1) converte cada ClienteDTO para LinhaEnvio (tudo texto), o formato que o validador entende
        List<LinhaEnvio> linhas = new ArrayList<>(clientes.size());
        for (int i = 0; i < clientes.size(); i++) {
            ClienteDTO c = clientes.get(i);
            linhas.add(new LinhaEnvio(
                    i + 2,                                   // +2: linha 1 é o cabeçalho e o índice começa em 0
                    c.codigoCliente(),
                    c.nomeCliente(),
                    c.consultor(),
                    c.segmento(),
                    c.nivelCliente(),
                    // toPlainString evita notação científica: Double 1.0E7 viraria "1.0E7" e falharia na leitura
                    c.faturamentoAnual() == null ? null : BigDecimal.valueOf(c.faturamentoAnual()).toPlainString(),
                    c.servicosContratados(),
                    c.dataContratacao() == null ? null : c.dataContratacao().toString(),
                    c.cidade(),
                    c.uf()));
        }

        // 2) valida e monta a resposta com os valores padronizados
        List<ResultadoLinha> resultados = validar(linhas);
        List<ClienteDTO> preparados = new ArrayList<>(resultados.size());
        for (ResultadoLinha r : resultados) {
            if (!r.valida()) {
                throw new IllegalArgumentException("Linha " + r.numero() + ": " + String.join(" | ", r.erros()));
            }
            Map<String, String> v = r.valores();
            preparados.add(new ClienteDTO(
                    v.get(CODIGO),
                    v.get(NOME),
                    Padronizacao.tituloNome(v.get(CONSULTOR)),     // mesma regra anunciada no aviso do passo 3c
                    Padronizacao.segmento(v.get(SEGMENTO)),        // nunca é null aqui: segmento inválido já virou erro
                    v.get(NIVEL),
                    Double.valueOf(v.get(FATURAMENTO)),
                    v.get(SERVICOS),
                    LocalDate.parse(v.get(DATA)),
                    v.get(CIDADE).isEmpty() ? null : v.get(CIDADE),
                    v.get(UF).isEmpty() ? null : v.get(UF).toUpperCase(Locale.ROOT)));
        }
        return preparados;
    }

    /**
     * Converte uma LinhaEnvio em mapa coluna -> texto.
     * Assim a validação percorre as colunas num laço (TODAS, OBRIGATORIAS) em vez de repetir código por campo.
     * HashMap aceita valores null (célula vazia); Map.of() não aceitaria.
     */
    private static Map<String, String> paraMapa(LinhaEnvio e) {
        Map<String, String> m = new HashMap<>();
        m.put(CODIGO, e.codigoCliente());
        m.put(NOME, e.nomeCliente());
        m.put(CONSULTOR, e.consultor());
        m.put(SEGMENTO, e.segmento());
        m.put(NIVEL, e.nivelCliente());
        m.put(FATURAMENTO, e.faturamentoAnual());
        m.put(SERVICOS, e.servicosContratados());
        m.put(DATA, e.dataContratacao());
        m.put(CIDADE, e.cidade());
        m.put(UF, e.uf());
        return m;
    }
}
