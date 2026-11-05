<script setup>
import { ref, computed } from 'vue'
import AppHeader from '../components/AppHeader.vue'
import { useAuthStore } from '../store/authStore'

const auth = useAuthStore()

const nome = ref(auth.usuario?.nome || '')
const email = ref(auth.usuario?.email || '')
const mensagem = ref('')

const iniciais = computed(() =>
  (nome.value || email.value || '?').split(' ').filter(Boolean).slice(0, 2).map((p) => p[0].toUpperCase()).join('')
)

const classeInput = 'w-full h-10 px-3 rounded-lg bg-palette-offwhite text-sm text-palette-chumbo shadow-[0_2px_6px_rgba(0,0,0,0.12)] outline-none focus:ring-2 focus:ring-palette-mid-green transition'

// TODO: enviar para o backend (ex.: PUT /auth/me) quando a API estiver pronta
function salvar() {
  auth.usuario = { ...auth.usuario, nome: nome.value.trim(), email: email.value.trim().toLowerCase() }
  localStorage.setItem('usuario', JSON.stringify(auth.usuario))
  mensagem.value = 'Dados atualizados com sucesso!'
  setTimeout(() => (mensagem.value = ''), 3000)
}
</script>

<template>
  <AppHeader />

  <main class="bg-palette-offwhite min-h-screen px-6 py-10 md:px-[400px]">
    <h1 class="text-3xl font-extrabold text-palette-chumbo mb-8">Meu <span class="text-palette-mid-green">Perfil</span></h1>

    <section class="grid grid-cols-1 lg:grid-cols-3 gap-5">
      <!-- Cartão com avatar -->
      <div class="bg-palette-chumbo rounded-2xl overflow-hidden shadow-[0_10px_30px_rgba(38,53,31,0.09)]">
        <div class="h-20"></div>
        <div class="bg-white rounded-tl-[60px] px-6 pb-8 flex flex-col items-center text-center">
          <div class="-mt-10 w-20 h-20 rounded-full bg-palette-mid-green text-white text-2xl font-bold flex items-center justify-center border-4 border-white">
            {{ iniciais }}
          </div>
          <p class="mt-3 text-lg font-bold text-palette-chumbo">{{ nome || 'Usuário' }}</p>
          <p class="text-sm text-palette-gray">{{ email || 'e-mail não informado' }}</p>
        </div>
      </div>

      <!-- Formulário de dados -->
      <form @submit.prevent="salvar" class="lg:col-span-2 bg-white rounded-2xl p-8 shadow-[0_10px_30px_rgba(38,53,31,0.09)] flex flex-col">
        <h2 class="text-lg font-bold text-palette-chumbo mb-6">Informações pessoais</h2>

        <label for="nome" class="text-xs font-medium text-palette-gray mb-1.5">Nome completo</label>
        <input id="nome" v-model="nome" type="text" :class="classeInput" class="mb-5" />

        <label for="email" class="text-xs font-medium text-palette-gray mb-1.5">E-mail</label>
        <input id="email" v-model="email" type="email" :class="classeInput" class="mb-8" />

        <div class="flex items-center gap-4">
          <button type="submit"
            class="bg-palette-mid-green text-white font-semibold px-8 py-2.5 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm">
            Salvar alterações
          </button>
          <p v-if="mensagem" class="text-sm text-palette-mid-green font-medium">{{ mensagem }}</p>
        </div>
      </form>
    </section>
  </main>
</template>
