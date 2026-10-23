<script setup>
import { ref, computed } from 'vue'
import router from '../router'
import { useAuthStore } from '../store/authStore'

const auth = useAuthStore()

const nome = ref('')
const email = ref('')
const senha = ref('')
const confirmarSenha = ref('')
const mostrarSenha = ref(false)
const erroValidacao = ref('')
const sucesso = ref(false)

const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

// Força da senha (0 a 4) para a barra indicadora
const forcaSenha = computed(() => {
  const s = senha.value
  let pontos = 0
  if (s.length >= 8) pontos++
  if (/[A-Z]/.test(s) && /[a-z]/.test(s)) pontos++
  if (/\d/.test(s)) pontos++
  if (/[^A-Za-z0-9]/.test(s)) pontos++
  return pontos
})
const corForca = ['bg-red-400', 'bg-red-400', 'bg-palette-mid-yellow', 'bg-palette-light-green', 'bg-palette-mid-green']
const textoForca = ['Muito fraca', 'Fraca', 'Média', 'Boa', 'Forte']

const classeInput = 'w-full h-10 px-3 rounded-lg bg-palette-offwhite text-sm text-palette-chumbo shadow-[0_2px_6px_rgba(0,0,0,0.12)] outline-none focus:ring-2 focus:ring-palette-mid-green transition'

async function enviar() {
  erroValidacao.value = ''
  auth.erro = ''

  if (!nome.value.trim() || !email.value.trim() || !senha.value || !confirmarSenha.value) {
    erroValidacao.value = 'Preencha todos os campos.'
  } else if (!REGEX_EMAIL.test(email.value.trim())) {
    erroValidacao.value = 'Informe um e-mail válido.'
  } else if (senha.value.length < 8) {
    erroValidacao.value = 'A senha deve ter pelo menos 8 caracteres.'
  } else if (senha.value !== confirmarSenha.value) {
    erroValidacao.value = 'As senhas não coincidem.'
  }
  if (erroValidacao.value) return

  const ok = await auth.cadastrar(nome.value.trim(), email.value.trim().toLowerCase(), senha.value)
  if (ok) {
    sucesso.value = true
    setTimeout(() => router.push('/login'), 2000)
  }
}
</script>

<template>
  <main class="relative min-h-screen flex items-center justify-center bg-palette-offwhite px-4 py-12">

    <!-- Botão Voltar -->
    <RouterLink to="/login" aria-label="Voltar para o login"
      class="absolute top-4 left-4 p-2 rounded-full text-palette-chumbo hover:bg-black/5 transition-colors">
      <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"
        stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M19 12H5m0 0l7 7m-7-7l7-7" />
      </svg>
    </RouterLink>

    <!-- Card de Cadastro -->
    <section class="w-full max-w-[420px] rounded-[40px] bg-palette-chumbo overflow-hidden shadow-[0_10px_30px_rgba(38,53,31,0.18)]">

      <!-- Topo escuro com a logo -->
      <div class="flex items-center justify-center gap-2 h-24 pb-2">
        <svg class="h-9 w-auto" viewBox="0 0 34 36" aria-hidden="true">
          <rect x="0" y="22" width="8" height="14" fill="#c6d1a6" />
          <rect x="13" y="12" width="8" height="24" fill="#82bfa0" />
          <rect x="26" y="0" width="8" height="36" fill="#5f9c7d" />
        </svg>
        <span class="text-white text-3xl font-bold tracking-tight">DataWise</span>
      </div>

      <!-- Corpo branco com curva -->
      <div class="bg-white rounded-tl-[90px] px-10 pt-8 pb-10">
        <h1 class="text-center text-3xl font-extrabold text-black mb-2">CADASTRO</h1>
        <p class="text-center text-xs text-palette-gray mb-8">Crie sua conta e comece a transformar seus dados</p>

        <!-- Mensagem de sucesso -->
        <div v-if="sucesso" class="flex flex-col items-center text-center py-6">
          <div class="w-14 h-14 rounded-full bg-palette-mid-green/15 flex items-center justify-center mb-4">
            <svg class="w-7 h-7 text-palette-mid-green" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
              <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <p class="font-semibold text-palette-chumbo">Conta criada com sucesso!</p>
          <p class="text-xs text-palette-gray mt-1">Redirecionando para o login...</p>
        </div>

        <form v-else @submit.prevent="enviar" novalidate class="flex flex-col">
          <label for="nome" class="text-xs font-medium text-palette-gray mb-1.5">Nome completo</label>
          <input id="nome" v-model="nome" type="text" autocomplete="name" :class="classeInput" class="mb-5" />

          <label for="email" class="text-xs font-medium text-palette-gray mb-1.5">E-mail</label>
          <input id="email" v-model="email" type="email" autocomplete="email" :class="classeInput" class="mb-5" />

          <label for="senha" class="text-xs font-medium text-palette-gray mb-1.5">Senha</label>
          <div class="relative">
            <input id="senha" v-model="senha" :type="mostrarSenha ? 'text' : 'password'" autocomplete="new-password"
              :class="classeInput" class="pr-10" />
            <button type="button" @click="mostrarSenha = !mostrarSenha"
              :aria-label="mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'"
              class="absolute inset-y-0 right-0 px-3 text-gray-400 hover:text-palette-gray">
              <svg v-if="!mostrarSenha" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
              <svg v-else class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path d="M3 3l18 18M10.6 10.6a3 3 0 004.2 4.2M9.9 5.1A10 10 0 0112 5c6.5 0 10 7 10 7a17 17 0 01-3.2 4.2M6.6 6.6C3.9 8.3 2 12 2 12s3.5 7 10 7a9.7 9.7 0 005.4-1.6" />
              </svg>
            </button>
          </div>

          <!-- Indicador de força da senha -->
          <div class="h-6 mt-1.5 mb-2">
            <template v-if="senha">
              <div class="flex gap-1">
                <span v-for="i in 4" :key="i" class="h-1 flex-1 rounded-full transition-colors"
                  :class="i <= forcaSenha ? corForca[forcaSenha] : 'bg-gray-200'"></span>
              </div>
              <p class="text-[10px] text-palette-gray mt-1">Força: {{ textoForca[forcaSenha] }}</p>
            </template>
          </div>

          <label for="confirmar" class="text-xs font-medium text-palette-gray mb-1.5">Confirmar senha</label>
          <input id="confirmar" v-model="confirmarSenha" :type="mostrarSenha ? 'text' : 'password'"
            autocomplete="new-password" :class="classeInput" class="mb-10" />

          <div class="w-4/5 mx-auto">
            <button type="submit" :disabled="auth.carregando"
              class="w-full h-10 rounded-lg bg-palette-mid-green text-white font-semibold hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2">
              <span v-if="auth.carregando" class="w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin"></span>
              {{ auth.carregando ? 'Criando conta...' : 'Criar conta' }}
            </button>

            <p v-if="erroValidacao || auth.erro" role="alert" class="text-[11px] text-red-500 mt-1.5">
              {{ erroValidacao || auth.erro }}
            </p>
          </div>

          <p class="mt-6 text-center text-[11px] text-palette-gray">
            Já tem uma conta?
            <RouterLink to="/login" class="font-semibold text-palette-mid-green hover:underline">Entrar</RouterLink>
          </p>
        </form>
      </div>
    </section>
  </main>
</template>

