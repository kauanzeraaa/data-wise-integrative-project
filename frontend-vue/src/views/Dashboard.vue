<script setup>
import { computed } from 'vue'
import AppHeader from '../components/AppHeader.vue'
import { useAuthStore } from '../store/authStore'
import { useUploadStore } from '../store/uploadStore.js'

const auth = useAuthStore()
const store = useUploadStore()

const primeiroNome = computed(() => (auth.usuario?.nome || '').split(' ')[0])
const clientes = computed(() => store.dadosTratados)

// Contagem de clientes por nível (A, B, C)
const porNivel = computed(() => {
  const contagem = { A: 0, B: 0, C: 0 }
  clientes.value.forEach((c) => { if (contagem[c.nivel_cliente] !== undefined) contagem[c.nivel_cliente]++ })
  return contagem
})

// Top 5 cidades com mais clientes
const topCidades = computed(() => {
  const contagem = {}
  clientes.value.forEach((c) => { if (c.cidade) contagem[c.cidade] = (contagem[c.cidade] || 0) + 1 })
  return Object.entries(contagem).sort((a, b) => b[1] - a[1]).slice(0, 5)
})

const cards = computed(() => [
  { rotulo: 'Total de clientes', valor: clientes.value.length },
  { rotulo: 'Cidades atendidas', valor: new Set(clientes.value.map((c) => c.cidade).filter(Boolean)).size },
  { rotulo: 'Clientes nível A', valor: porNivel.value.A },
  { rotulo: 'Problemas encontrados', valor: store.erros.length, alerta: store.erros.length > 0 },
])

const percentual = (valor) => (clientes.value.length ? Math.round((valor / clientes.value.length) * 100) : 0)
const coresNivel = { A: 'bg-palette-mid-green', B: 'bg-palette-light-green', C: 'bg-palette-mid-yellow' }
</script>

<template>
  <AppHeader />

  <main class="bg-palette-offwhite min-h-screen px-6 py-10 md:px-[400px]">
    <h1 class="text-3xl font-extrabold text-palette-chumbo">
      Olá<span v-if="primeiroNome">, <span class="text-palette-mid-green">{{ primeiroNome }}</span></span>!
    </h1>
    <p class="text-palette-gray font-medium mt-1 mb-8">Aqui está um resumo da sua carteira de clientes.</p>

    <!-- Estado vazio -->
    <div v-if="!store.temDados" class="bg-white rounded-2xl shadow-[0_10px_30px_rgba(38,53,31,0.09)] p-12 text-center">
      <p class="text-lg font-semibold text-palette-chumbo mb-2">Nenhum dado importado ainda</p>
      <p class="text-palette-gray mb-6">Envie sua planilha de clientes para visualizar os indicadores.</p>
      <RouterLink to="/uploads"
        class="inline-block bg-palette-mid-green text-white font-semibold px-8 py-3 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm">
        Importar planilha
      </RouterLink>
    </div>

    <template v-else>
      <!-- Cards de indicadores -->
      <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
        <div v-for="card in cards" :key="card.rotulo"
          class="bg-white rounded-2xl p-6 shadow-[0_10px_30px_rgba(38,53,31,0.09)] border-l-4"
          :class="card.alerta ? 'border-red-400' : 'border-palette-mid-green'">
          <p class="text-sm text-palette-gray font-medium">{{ card.rotulo }}</p>
          <p class="text-3xl font-extrabold mt-2" :class="card.alerta ? 'text-red-500' : 'text-palette-chumbo'">{{ card.valor }}</p>
        </div>
      </section>

      <section class="grid grid-cols-1 lg:grid-cols-2 gap-5">
        <!-- Clientes por nível -->
        <div class="bg-white rounded-2xl p-6 shadow-[0_10px_30px_rgba(38,53,31,0.09)]">
          <h2 class="text-lg font-bold text-palette-chumbo mb-5">Clientes por nível</h2>
          <div v-for="(qtd, nivel) in porNivel" :key="nivel" class="mb-4">
            <div class="flex justify-between text-sm font-medium text-palette-gray mb-1">
              <span>Nível {{ nivel }}</span><span>{{ qtd }} ({{ percentual(qtd) }}%)</span>
            </div>
            <div class="h-3 bg-gray-100 rounded-full overflow-hidden">
              <div class="h-full rounded-full transition-all duration-500" :class="coresNivel[nivel]" :style="{ width: percentual(qtd) + '%' }"></div>
            </div>
          </div>
        </div>

        <!-- Top cidades -->
        <div class="bg-white rounded-2xl p-6 shadow-[0_10px_30px_rgba(38,53,31,0.09)]">
          <h2 class="text-lg font-bold text-palette-chumbo mb-5">Top 5 cidades</h2>
          <ol class="space-y-3">
            <li v-for="([cidade, qtd], i) in topCidades" :key="cidade" class="flex items-center gap-3">
              <span class="w-7 h-7 rounded-full bg-palette-mid-green/15 text-palette-mid-green text-sm font-bold flex items-center justify-center">{{ i + 1 }}</span>
              <span class="flex-1 font-medium text-palette-chumbo">{{ cidade }}</span>
              <span class="text-sm text-palette-gray">{{ qtd }} clientes</span>
            </li>
          </ol>
        </div>
      </section>
    </template>
  </main>
</template>
