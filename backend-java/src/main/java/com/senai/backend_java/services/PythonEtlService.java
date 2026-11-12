package com.senai.backend_java.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.senai.backend_java.config.ApiException;
import com.senai.backend_java.config.AppProperties;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component                                                                           // componente do Spring
public class PythonEtlService {                                                     // CHAMA A API DO PYTHON
    private static final Logger log = LoggerFactory.getLogger(PythonEtlService.class.getName()); // logger desta classe

    private final String url;                                                       // endereço do POST /etl (ETL_URL)
    private final String chave;                                                     // chave combinada com o Python (ETL_KEY; vazia = sem chave)
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();  // cliente HTTP (espera até 10 s para conectar)
    private final ObjectMapper json;                                                // lê e escreve JSON

    public PythonEtlService(AppProperties props, ObjectMapper json) {               // construtor: o Spring entrega as peças
        this.url = props.etl() == null || props.etl().url() == null || props.etl().url().isBlank()  // endereço não configurado?
                ? "http://localhost:8000/etl" : props.etl().url().trim();           // usa o padrão; senão o configurado
        this.chave = props.etl() == null || props.etl().key() == null ? "" : props.etl().key().trim();  // lê a chave (pode ser vazia)
        this.json = json;                                                           // guarda o leitor de JSON
    }                                                                               // fecha o bloco

    /** Envia as linhas válidas. O Python grava tudo no banco e só responde {ok: true}. */
    public void enviar(String arquivoNome, int recebidas, int invalidas, int comAviso,  // enviar: manda os dados ao Python
                           List<Map<String, Object>> linhas) {                      // (continuação) as linhas válidas
        Map<String, Object> pedido = new LinkedHashMap<>();                         // o pedido completo que vai ao Python
        pedido.put("arquivoNome", arquivoNome);                                     // nome do arquivo
        pedido.put("recebidas", recebidas);                                         // total de linhas da planilha (boas + ruins)
        pedido.put("invalidas", invalidas);                                         // quantas tinham erro (não são enviadas)
        pedido.put("comAviso", comAviso);                                           // quantas válidas têm aviso
        pedido.put("linhas", linhas);                                               // as linhas válidas
        log.info("Enviando {} linhas ao Python: POST {}", linhas.size(), url);      // escreve no terminal
        try {                                                                       // tenta montar o JSON
            HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(url))       // monta o pedido para a API do Python
                    .timeout(Duration.ofMinutes(5))                                 // espera a resposta por até 5 minutos
                    .header("Content-Type", "application/json; charset=utf-8")      // avisa que o corpo é JSON
                    .POST(HttpRequest.BodyPublishers.ofByteArray(json.writeValueAsBytes(pedido)));  // POST com o JSON no corpo
            chamar(req);                                                            // envia e espera o Python gravar (ele só responde ok)
        } catch (JacksonException e) {                                              // não conseguiu montar o JSON (Jackson 3 lança exceção NÃO checada)
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao montar o JSON: " + e.getMessage());  // erro 500
        }                                                                           // fecha o bloco
    }                                                                               // fecha o bloco

    private void chamar(HttpRequest.Builder req) {                              // chamar: envia o pedido e trata a resposta e os erros
        if (!chave.isEmpty()) req.header("X-ETL-Key", chave);                       // se há chave, envia no cabeçalho
        try {                                                                       // tenta falar com o Python
            HttpResponse<String> resp = http.send(req.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));  // ENVIA e espera a resposta
            if (resp.statusCode() != 200) {                                         // o Python respondeu erro?
                throw new ApiException(HttpStatus.BAD_GATEWAY,                      // erro 502 para a tela
                        "O serviço Python respondeu " + resp.statusCode() + ": " + resp.body());  // (continuação) mostra o motivo
            }                                                                       // fecha o bloco
        } catch (IOException e) {                                                   // não conseguiu falar com o Python (serviço desligado?)
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,                  // erro 503 para a tela
                    "Não foi possível falar com o serviço Python (ele está ligado? uvicorn api:app --port 8000).");  // (continuação) dica
        } catch (InterruptedException e) {                                          // thread interrompida
            Thread.currentThread().interrupt();                                     // restaura o sinal de interrupção
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "A chamada ao Python foi interrompida.");  // erro 503
        }                                                                           // fecha o bloco
    }                                                                               // fecha o bloco
}   