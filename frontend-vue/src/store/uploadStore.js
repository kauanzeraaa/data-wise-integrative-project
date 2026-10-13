import { defineStore } from "pinia"
import * as XLSX from "xlsx"

// Colunas esperadas na planilha
const CAMPOS = [
    { chave: "codigo_cliente", rotulo: "Código Cliente", obrigatorio: true },
    { chave: "nome_cliente", rotulo: "Nome", obrigatorio: true },
    { chave: "nivel_cliente", rotulo: "Nível", obrigatorio: true },
    { chave: "cidade", rotulo: "Cidade", obrigatorio: true },
    { chave: "data_contratacao", rotulo: "Data Contratação", obrigatorio: true },
    { chave: "email", rotulo: "E-mail", obrigatorio: false },
]

const NIVEIS_VALIDOS = ["A", "B", "C"]
const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const REGEX_DATA = /^\d{2}\/\d{2}\/\d{4}$/

// Padroniza o nome de uma coluna: "Código Cliente" -> "codigo_cliente"
function normalizarTexto(texto) {
    return String(texto ?? "")
        .normalize("NFD").replace(/[\u0300-\u036f]/g, "") // remove acentos
        .trim()
        .toLowerCase()
        .replace(/\s+/g, "_")
}

// Nomes de coluna aceitos para cada campo (a chave ou o rótulo do campo)
// ex.: "nome_cliente" ou "Nome" -> nome_cliente | "E-mail" -> email
const ALIASES_COLUNAS = {}
CAMPOS.forEach((campo) => {
    ALIASES_COLUNAS[normalizarTexto(campo.chave)] = campo.chave
    ALIASES_COLUNAS[normalizarTexto(campo.rotulo)] = campo.chave
})

// Erro de conteúdo da planilha (mensagem pode ser exibida diretamente ao usuário)
class ErroValidacaoPlanilha extends Error {}

export function formatarTamanho(bytes) {
    if (!bytes) return "0 KB"
    const mb = bytes / (1024 * 1024)
    if (mb >= 1) return mb.toFixed(1).replace(".", ",") + " MB"
    const kb = bytes / 1024
    return kb.toFixed(1).replace(".", ",") + " KB"
}

export function formatarDataHora(data) {
    if (!data) return ""
    return new Date(data).toLocaleString("pt-BR")
}

// Pequena pausa só para a barra de progresso ficar visível
const esperar = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

export const useUploadStore = defineStore("upload", {
    // ==================== STATE ====================
    // Informações armazenadas temporariamente
    state: () => ({
        arquivo: null,        // arquivo selecionado
        nomeArquivo: "",      // nome do arquivo analisado
        dataUpload: null,     // data e hora do upload
        historicoArquivos: [],// últimos arquivos carregados (para os cards)
        progresso: 0,         // valor da barra de progresso (0 a 100)
        carregando: false,    // indica se está processando
        mensagemErro: "",     // erro do arquivo (formato inválido, leitura...)
        dadosOriginais: [],   // linhas como vieram da planilha
        dadosTratados: [],    // linhas depois de limpas e padronizadas
        erros: [],            // lista de erros: { linha, campo, tipo, descricao }
    }),

    // ==================== GETTERS ====================
    // Informações calculadas a partir do state
    getters: {
        temDados: (state) => state.dadosTratados.length > 0,

        dataUploadFormatada: (state) => {
            if (!state.dataUpload) return ""
            return new Date(state.dataUpload).toLocaleString("pt-BR")
        },

        totalRegistros: (state) => state.dadosTratados.length,

        totalErros: (state) => state.erros.length,

        // Quantidade de linhas diferentes que tiveram pelo menos um erro
        registrosComErro: (state) => new Set(state.erros.map((erro) => erro.linha)).size,

        registrosValidos() {
            return this.totalRegistros - this.registrosComErro
        },

        // campo obrigatório vazio: 3, e-mail inválido: 1
        errosPorTipo: (state) => {
            const contagem = {}
            state.erros.forEach((erro) => {
                contagem[erro.tipo] = (contagem[erro.tipo] || 0) + 1
            })
            return contagem
        },
    },

    // ==================== ACTIONS ====================
    // Processos e validações executados
    actions: {
        limpar() {
            this.progresso = 0
            this.mensagemErro = ""
            this.dadosOriginais = []
            this.dadosTratados = []
            this.erros = []
        },

        selecionarArquivo(file) {
            this.limpar()
            this.arquivo = file
            this.nomeArquivo = file.name
            this.dataUpload = new Date()
            
            // Adiciona o arquivo no histórico de cards
            const extensao = file.name.includes(".") ? file.name.split(".").pop().toUpperCase() : "CSV"
            this.historicoArquivos.unshift({
                id: `${this.dataUpload.getTime()}-${Math.random().toString(36).slice(2, 8)}`,
                nome: file.name.replace(/\.[^/.]+$/, ""),
                extensao: extensao,
                tamanhoBytes: file.size,
                status: "Carregando",
                progresso: 0,
                dataUpload: this.dataUpload, // data/hora em que ESTE arquivo foi enviado
                mensagemErro: "",
            })
            // Mantém apenas os 2 últimos arquivos visualmente
            if (this.historicoArquivos.length > 2) {
                this.historicoArquivos.pop()
            }
            // Retorna o card (reativo) do arquivo atual
            return this.historicoArquivos[0]
        },

        // Marca o upload como erro e exibe a mensagem
        marcarErro(item, mensagem) {
            this.mensagemErro = mensagem
            this.dadosOriginais = []
            this.dadosTratados = []
            this.erros = []
            if (item) {
                item.status = "Erro"
                item.mensagemErro = mensagem
            }
        },

        validarArquivo(item) {
            const nome = this.nomeArquivo.toLowerCase()
            const formatoValido = nome.endsWith(".xlsx") || nome.endsWith(".xls") || nome.endsWith(".csv")

            if (!formatoValido) {
                this.marcarErro(item, "Formato inválido. Envie um arquivo .xlsx, .xls ou .csv.")
                return false
            }
            if (!this.arquivo || this.arquivo.size === 0) {
                this.marcarErro(item, "O arquivo está vazio.")
                return false
            }
            return true
        },

        // valida o arquivo, lê, trata e valida os dados
        async processarPlanilha(file) {
            const item = this.selecionarArquivo(file)
            if (!this.validarArquivo(item)) return

            this.carregando = true
            try {
                this.progresso = 20
                item.progresso = 20
                const linhas = await this.lerPlanilha()
                await esperar(300)

                this.progresso = 50
                item.progresso = 50
                this.dadosOriginais = linhas
                this.dadosTratados = linhas.map((linha) => this.tratarLinha(linha))
                await esperar(300)

                this.progresso = 80
                item.progresso = 80
                this.validarDados()
                await esperar(300)

                this.progresso = 100
                item.progresso = 100
                item.status = "Completo"
            } catch (error) {
                const mensagem = error instanceof ErroValidacaoPlanilha
                    ? error.message
                    : "Não foi possível ler a planilha. Verifique se o arquivo não está corrompido."
                this.marcarErro(item, mensagem)
            } finally {
                this.carregando = false
            }
        },

        // le a primeira aba da planilha, valida a estrutura e transforma em lista de objetos
        async lerPlanilha() {
            const buffer = await this.arquivo.arrayBuffer()
            // datas do excel viram texto DD/MM/AAAA (dateNF), CSV é lido como texto normal (raw: true)
            const workbook = XLSX.read(buffer, { type: "array", dateNF: "dd/mm/yyyy", raw: true })

            const nomeAba = workbook.SheetNames[0]
            const planilha = nomeAba ? workbook.Sheets[nomeAba] : null
            if (!planilha || !planilha["!ref"]) {
                throw new ErroValidacaoPlanilha("A planilha está vazia. Nenhum dado foi encontrado no arquivo.")
            }

            // Lê como matriz para obter o cabeçalho mesmo que não existam linhas de dados
            const matriz = XLSX.utils.sheet_to_json(planilha, { header: 1, defval: "", raw: false, blankrows: false })
            const cabecalho = (matriz[0] || []).map((coluna) => String(coluna).trim())
            if (matriz.length === 0 || cabecalho.every((coluna) => coluna === "")) {
                throw new ErroValidacaoPlanilha("A planilha está vazia. Nenhum dado foi encontrado no arquivo.")
            }

            // Verifica se todas as colunas obrigatórias existem (aceita a chave ou o rótulo)
            const colunasEncontradas = new Set(cabecalho.map((coluna) => this.normalizarNomeColuna(coluna)))
            const faltando = CAMPOS.filter((campo) => campo.obrigatorio && !colunasEncontradas.has(campo.chave))
            if (faltando.length > 0) {
                const nomes = faltando.map((campo) => `"${campo.rotulo}"`).join(", ")
                throw new ErroValidacaoPlanilha(
                    `Colunas obrigatórias ausentes ou com nome incorreto: ${nomes}. ` +
                    `Colunas esperadas: ${CAMPOS.filter((c) => c.obrigatorio).map((c) => c.rotulo).join(", ")}.`
                )
            }

            if (matriz.length < 2) {
                throw new ErroValidacaoPlanilha("A planilha possui apenas o cabeçalho, sem nenhum registro de cliente.")
            }

            const linhas = XLSX.utils.sheet_to_json(planilha, { defval: "", raw: false })

            // Padroniza os nomes das colunas: "Código Cliente" - "codigo_cliente"
            const normalizadas = linhas.map((linha) => {
                const nova = {}
                Object.keys(linha).forEach((coluna) => {
                    nova[this.normalizarNomeColuna(coluna)] = linha[coluna]
                })
                return nova
            })

            // Garante que exista pelo menos um registro com dados de cliente preenchidos
            const obrigatorios = CAMPOS.filter((campo) => campo.obrigatorio)
            const temDadosDeCliente = normalizadas.some((linha) =>
                obrigatorios.some((campo) => String(linha[campo.chave] ?? "").trim() !== "")
            )
            if (!temDadosDeCliente) {
                throw new ErroValidacaoPlanilha("O arquivo não contém dados de clientes: todas as linhas estão vazias.")
            }

            return normalizadas
        },

        // Converte o nome da coluna para a chave do campo (ou apenas padroniza se for desconhecida)
        normalizarNomeColuna(coluna) {
            const normalizada = normalizarTexto(coluna)
            return ALIASES_COLUNAS[normalizada] || normalizada
        },

        // Remove espaços extras e padroniza alguns campos
        tratarLinha(linha) {
            const limpar = (valor) => String(valor ?? "").trim().replace(/\s+/g, " ")

            return {
                codigo_cliente: limpar(linha.codigo_cliente).toUpperCase(),
                nome_cliente: limpar(linha.nome_cliente),
                nivel_cliente: limpar(linha.nivel_cliente).toUpperCase(),
                cidade: limpar(linha.cidade),
                data_contratacao: limpar(linha.data_contratacao),
                email: limpar(linha.email).toLowerCase(),
            }
        },

        // Executa todas as validações em cada linha
        validarDados() {
            this.erros = []

            this.dadosOriginais.forEach((original, indice) => {
                const tratado = this.dadosTratados[indice]
                const numeroLinha = indice + 2 // +2 porque a linha 1 é o cabeçalho
                this.validarLinha(original, tratado, numeroLinha)
            })

            this.verificarDuplicados()
            this.erros.sort((a, b) => a.linha - b.linha)
        },

        validarLinha(original, tratado, linha) {
            CAMPOS.forEach((campo) => {
                const valorOriginal = String(original[campo.chave] ?? "")
                const valor = tratado[campo.chave]

                // campo obrigatório vazio
                if (campo.obrigatorio && valor === "") {
                    this.registrarErro(linha, campo.rotulo, "Campo obrigatório vazio", `O campo ${campo.rotulo} não foi preenchido.`)
                    return
                }

                // espaços em branco desnecessários
                if (valorOriginal !== "" && valorOriginal !== valorOriginal.trim().replace(/\s+/g, " ")) {
                    this.registrarErro(linha, campo.rotulo, "Espaços desnecessários", `"${valorOriginal}" possui espaços extras.`)
                }
            })

            // e-mail inválido
            if (tratado.email && !REGEX_EMAIL.test(tratado.email)) {
                this.registrarErro(linha, "E-mail", "E-mail inválido", `"${tratado.email}" não é um e-mail válido.`)
            }

            // dado fora do padrão: nível deve ser A, B ou C
            if (tratado.nivel_cliente && !NIVEIS_VALIDOS.includes(tratado.nivel_cliente)) {
                this.registrarErro(linha, "Nível", "Dado fora do padrão", `Nível "${tratado.nivel_cliente}" inválido. Use A, B ou C.`)
            }

            // dado fora do padrão: data deve estar em DD/MM/AAAA
            if (tratado.data_contratacao && !REGEX_DATA.test(tratado.data_contratacao)) {
                this.registrarErro(linha, "Data Contratação", "Dado fora do padrão", `Data "${tratado.data_contratacao}" deve estar no formato DD/MM/AAAA.`)
            }

            // texto que precisa ser padronizado (tudo maiúsculo ou tudo minúsculo)
            ;["nome_cliente", "cidade"].forEach((chave) => {
                const texto = tratado[chave]
                if (texto.length > 1 && (texto === texto.toUpperCase() || texto === texto.toLowerCase())) {
                    const rotulo = chave === "cidade" ? "Cidade" : "Nome"
                    this.registrarErro(linha, rotulo, "Texto a padronizar", `"${texto}" deveria começar com letras maiúsculas (ex.: Campinas).`)
                }
            })
        },

        // registros duplicados (mesmo código de cliente)
        verificarDuplicados() {
            const codigosVistos = {}

            this.dadosTratados.forEach((registro, indice) => {
                const codigo = registro.codigo_cliente
                const linha = indice + 2
                if (!codigo) return

                if (codigosVistos[codigo]) {
                    this.registrarErro(linha, "Código Cliente", "Registro duplicado", `O código ${codigo} já aparece na linha ${codigosVistos[codigo]}.`)
                } else {
                    codigosVistos[codigo] = linha
                }
            })
        },

        registrarErro(linha, campo, tipo, descricao) {
            this.erros.push({ linha, campo, tipo, descricao })
        },
    },
})
