import { defineStore } from "pinia"

// URL base da API (defina VITE_API_URL no arquivo .env quando o backend estiver pronto)
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8000"

export const useAuthStore = defineStore("auth", {
    state: () => ({
        token: localStorage.getItem("token") || null,
        usuario: JSON.parse(localStorage.getItem("usuario") || "null"),
        carregando: false,
        erro: "",
    }),

    getters: {
        estaAutenticado: (state) => !!state.token,
    },

    actions: {
        // Envia as credenciais para o backend e salva o token retornado
        async login(email, senha) {
            this.carregando = true
            this.erro = ""

            try {
                const resposta = await fetch(`${API_URL}/auth/login`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, senha }),
                })

                if (!resposta.ok) {
                    this.erro = resposta.status === 401 || resposta.status === 404
                        ? "Usuário ou senha não encontrado"
                        : "Erro ao realizar login. Tente novamente."
                    return false
                }

                // Formato esperado: { token: "...", usuario: { ... } }
                const dados = await resposta.json()
                this.token = dados.token
                this.usuario = dados.usuario ?? null
                localStorage.setItem("token", this.token)
                localStorage.setItem("usuario", JSON.stringify(this.usuario))
                return true
            } catch {
                // Backend fora do ar / sem conexão
                this.erro = "Não foi possível conectar ao servidor."
                return false
            } finally {
                this.carregando = false
            }
        },

        // Cria uma nova conta no backend
        async cadastrar(nome, email, senha) {
            this.carregando = true
            this.erro = ""

            try {
                const resposta = await fetch(`${API_URL}/auth/register`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ nome, email, senha }),
                })

                if (!resposta.ok) {
                    this.erro = resposta.status === 409
                        ? "Este e-mail já está cadastrado."
                        : "Erro ao criar conta. Tente novamente."
                    return false
                }
                return true
            } catch {
                this.erro = "Não foi possível conectar ao servidor."
                return false
            } finally {
                this.carregando = false
            }
        },

        logout() {
            this.token = null
            this.usuario = null
            localStorage.removeItem("token")
            localStorage.removeItem("usuario")
        },
    },
})

