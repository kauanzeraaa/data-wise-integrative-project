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

export function formatarTamanho(bytes) {
    if (!bytes) return "0 KB"
    const mb = bytes / (1024 * 1024)
    if (mb >= 1) return mb.toFixed(1).replace(".", ",") + " MB"
    const kb = bytes / 1024
    return kb.toFixed(1).replace(".", ",") + " KB"
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

        // Ex.: { "Campo obrigatório vazio": 3, "E-mail inválido": 1 }
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
                id: Date.now(),
                nome: file.name.replace(/\.[^/.]+$/, ""),
                extensao: extensao,
                tamanhoBytes: file.size,
                status: "Carregando",
                progresso: 0
            })
            // Mantém apenas os 2 últimos arquivos visualmente, como no print
            if (this.historicoArquivos.length > 2) {
                this.historicoArquivos.pop()
            }
        },

        validarArquivo() {
            const nome = this.nomeArquivo.toLowerCase()
            const formatoValido = nome.endsWith(".xlsx") || nome.endsWith(".xls") || nome.endsWith(".csv")

            if (!formatoValido) {
                this.mensagemErro = "Formato inválido. Envie um arquivo .xlsx, .xls ou .csv."
                if (this.historicoArquivos.length > 0) {
                    this.historicoArquivos[0].status = "Erro"
                }
                return false
            }
            return true
        },

        // Fluxo principal: valida o arquivo, lê, trata e valida os dados
        async processarPlanilha(file) {
            this.selecionarArquivo(file)
            if (!this.validarArquivo()) return

            this.carregando = true
            try {
                this.progresso = 20
                this.historicoArquivos[0].progresso = 20
                const linhas = await this.lerPlanilha()
                await esperar(300)

                this.progresso = 50
                this.historicoArquivos[0].progresso = 50
                this.dadosOriginais = linhas
                this.dadosTratados = linhas.map((linha) => this.tratarLinha(linha))
                await esperar(300)

                this.progresso = 80
                this.historicoArquivos[0].progresso = 80
                this.validarDados()
                await esperar(300)

                this.progresso = 100
                this.historicoArquivos[0].progresso = 100
                this.historicoArquivos[0].status = "Completo"
            } catch (error) {
                this.mensagemErro = "Não foi possível ler a planilha."
                this.historicoArquivos[0].status = "Erro"
            } finally {
                this.carregando = false
            }
        },

        // Lê a primeira aba da planilha e transforma em lista de objetos
        async lerPlanilha() {
            const buffer = await this.arquivo.arrayBuffer()
            // dateNF: datas do Excel viram texto DD/MM/AAAA | raw: CSV é lido como texto puro
            const workbook = XLSX.read(buffer, { type: "array", dateNF: "dd/mm/yyyy", raw: true })
            const planilha = workbook.Sheets[workbook.SheetNames[0]]
            const linhas = XLSX.utils.sheet_to_json(planilha, { defval: "", raw: false })

            // Padroniza os nomes das colunas: "Código Cliente" -> "codigo_cliente"
            return linhas.map((linha) => {
                const nova = {}
                Object.keys(linha).forEach((coluna) => {
                    nova[this.normalizarNomeColuna(coluna)] = linha[coluna]
                })
                return nova
            })
        },

        normalizarNomeColuna(coluna) {
            return coluna
                .normalize("NFD").replace(/[\u0300-\u036f]/g, "") // remove acentos
                .trim()
                .toLowerCase()
                .replace(/\s+/g, "_")
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

                // 1. Campo obrigatório vazio
                if (campo.obrigatorio && valor === "") {
                    this.registrarErro(linha, campo.rotulo, "Campo obrigatório vazio", `O campo ${campo.rotulo} não foi preenchido.`)
                    return
                }

                // 2. Espaços em branco desnecessários
                if (valorOriginal !== "" && valorOriginal !== valorOriginal.trim().replace(/\s+/g, " ")) {
                    this.registrarErro(linha, campo.rotulo, "Espaços desnecessários", `"${valorOriginal}" possui espaços extras.`)
                }
            })

            // 3. E-mail inválido
            if (tratado.email && !REGEX_EMAIL.test(tratado.email)) {
                this.registrarErro(linha, "E-mail", "E-mail inválido", `"${tratado.email}" não é um e-mail válido.`)
            }

            // 4. Dado fora do padrão: nível deve ser A, B ou C
            if (tratado.nivel_cliente && !NIVEIS_VALIDOS.includes(tratado.nivel_cliente)) {
                this.registrarErro(linha, "Nível", "Dado fora do padrão", `Nível "${tratado.nivel_cliente}" inválido. Use A, B ou C.`)
            }

            // 5. Dado fora do padrão: data deve estar em DD/MM/AAAA
            if (tratado.data_contratacao && !REGEX_DATA.test(tratado.data_contratacao)) {
                this.registrarErro(linha, "Data Contratação", "Dado fora do padrão", `Data "${tratado.data_contratacao}" deve estar no formato DD/MM/AAAA.`)
            }

            // 6. Texto que precisa ser padronizado (tudo maiúsculo ou tudo minúsculo)
            ;["nome_cliente", "cidade"].forEach((chave) => {
                const texto = tratado[chave]
                if (texto.length > 1 && (texto === texto.toUpperCase() || texto === texto.toLowerCase())) {
                    const rotulo = chave === "cidade" ? "Cidade" : "Nome"
                    this.registrarErro(linha, rotulo, "Texto a padronizar", `"${texto}" deveria começar com letras maiúsculas (ex.: Campinas).`)
                }
            })
        },

        // 7. Registros duplicados (mesmo código de cliente)
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
