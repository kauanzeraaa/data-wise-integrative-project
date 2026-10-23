<script setup>
import { ref } from 'vue'
import router from '../router'
import { useAuthStore } from '../store/authStore'

const auth = useAuthStore()

const email = ref('')
const senha = ref('')
const mostrarSenha = ref(false)
const erroValidacao = ref('')

async function enviar() {
  erroValidacao.value = ''
  auth.erro = ''

  if (!email.value.trim() || !senha.value) {
    erroValidacao.value = 'Preencha e-mail e senha.'
    return
  }

  const sucesso = await auth.login(email.value.trim(), senha.value)
  if (sucesso) router.push('/uploads')
}
</script>

<template>
  <main class="relative min-h-screen flex items-center justify-center bg-palette-offwhite px-4">

    <!-- Botão Voltar -->
    <RouterLink to="/" aria-label="Voltar para a página inicial"
      class="absolute top-4 left-4 p-2 rounded-full text-palette-chumbo hover:bg-black/5 transition-colors">
      <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"
        stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M19 12H5m0 0l7 7m-7-7l7-7" />
      </svg>
    </RouterLink>

    <!-- Card de Login -->
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
        <h1 class="text-center text-3xl font-extrabold text-black mb-10">LOGIN</h1>

        <form @submit.prevent="enviar" novalidate class="flex flex-col">
          <label for="email" class="text-xs font-medium text-palette-gray mb-1.5">E-mail</label>
          <input id="email" v-model="email" type="email" autocomplete="email"
            class="h-10 px-3 rounded-lg bg-palette-offwhite text-sm text-palette-chumbo shadow-[0_2px_6px_rgba(0,0,0,0.12)] outline-none focus:ring-2 focus:ring-palette-mid-green transition mb-7" />

          <label for="senha" class="text-xs font-medium text-palette-gray mb-1.5">Senha</label>
          <div class="relative mb-10">
            <input id="senha" v-model="senha" :type="mostrarSenha ? 'text' : 'password'" autocomplete="current-password"
              class="w-full h-10 pl-3 pr-10 rounded-lg bg-palette-offwhite text-sm text-palette-chumbo shadow-[0_2px_6px_rgba(0,0,0,0.12)] outline-none focus:ring-2 focus:ring-palette-mid-green transition" />
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

          <div class="w-4/5 mx-auto">
            <button type="submit" :disabled="auth.carregando"
              class="w-full h-10 rounded-lg bg-palette-mid-green text-white font-semibold hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2">
              <span v-if="auth.carregando" class="w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin"></span>
              {{ auth.carregando ? 'Entrando...' : 'Login' }}
            </button>

            <p v-if="erroValidacao || auth.erro" role="alert" class="text-[11px] text-red-500 mt-1.5">
              {{ erroValidacao || auth.erro }}
            </p>
          </div>

          <a href="#" class="mt-6 self-center text-[11px] text-palette-gray underline hover:text-palette-mid-green">
            Esqueci a senha
          </a>

          <p class="mt-3 text-center text-[11px] text-palette-gray">
            Não tem uma conta?
            <RouterLink to="/cadastro" class="font-semibold text-palette-mid-green hover:underline">Cadastre-se</RouterLink>
          </p>
        </form>
      </div>
    </section>
  </main>
</template>
