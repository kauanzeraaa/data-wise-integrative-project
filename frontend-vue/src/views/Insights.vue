<script setup>
import { computed } from 'vue'
import AppHeader from '../components/AppHeader.vue'
import { useUploadStore } from '../store/uploadStore.js'

const store = useUploadStore()

// Gera insights simples a partir dos dados importados
// (quando o backend estiver pronto, estes dados podem vir da API de Data Science)
const insights = computed(() => {
  const clientes = store.dadosTratados
  if (!clientes.length) return []
  const total = clientes.length
  const lista = []

  // Cidade com maior concentração
  const cidades = {}
  clientes.forEach((c) => { if (c.cidade) cidades[c.cidade] = (cidades[c.cidade] || 0) + 1 })
  const [cidadeTop, qtdTop] = Object.entries(cidades).sort((a, b) => b[1] - a[1])[0] || []
  if (cidadeTop) {
    lista.push({
      tipo: 'oportunidade', titulo: `${cidadeTop} é seu principal mercado`,
      texto: `${qtdTop} clientes (${Math.round((qtdTop / total) * 100)}% da carteira) estão em ${cidadeTop}. Considere ações regionais de fidelização.`,
    })
  }

  // Participação de clientes nível C
  const nivelC = clientes.filter((c) => c.nivel_cliente === 'C').length
  if (nivelC / total >= 0.3) {
    lista.push({
      tipo: 'atencao', titulo: 'Muitos clientes no nível C',
      texto: `${nivelC} clientes (${Math.round((nivelC / total) * 100)}%) estão no nível C. Há espaço para campanhas de upgrade.`,
    })
  }

  // Clientes sem e-mail
  const semEmail = clientes.filter((c) => !c.email).length
  if (semEmail) {
    lista.push({
      tipo: 'atencao', titulo: 'Clientes sem e-mail cadastrado',
      texto: `${semEmail} clientes não possuem e-mail, o que limita ações de marketing digital.`,
    })
  }

  // Qualidade dos dados
  lista.push(store.erros.length
    ? { tipo: 'alerta', titulo: 'Qualidade dos dados', texto: `Foram encontrados ${store.erros.length} problemas na planilha. Corrija-os para análises mais precisas.` }
    : { tipo: 'oportunidade', titulo: 'Dados consistentes', texto: 'Nenhum problema encontrado na planilha. Ótimo trabalho!' })

  return lista
})

const estilos = {
  oportunidade: { borda: 'border-palette-mid-green', badge: 'bg-palette-mid-green/15 text-palette-mid-green', rotulo: 'Oportunidade' },
  atencao: { borda: 'border-palette-mid-yellow', badge: 'bg-palette-light-yellow text-palette-brown', rotulo: 'Atenção' },
  alerta: { borda: 'border-red-400', badge: 'bg-red-50 text-red-500', rotulo: 'Alerta' },
}
</script>

<template>
  <AppHeader />

  <main class="bg-palette-offwhite min-h-screen px-6 py-10 md:px-[400px]">
    <h1 class="text-3xl font-extrabold text-palette-chumbo">
      <span class="text-palette-mid-green">Insights</span> Estratégicos
    </h1>
    <p class="text-palette-gray font-medium mt-1 mb-8">Padrões e oportunidades identificados na sua carteira de clientes.</p>

    <div v-if="!insights.length" class="bg-white rounded-2xl shadow-[0_10px_30px_rgba(38,53,31,0.09)] p-12 text-center">
      <p class="text-lg font-semibold text-palette-chumbo mb-2">Ainda não há insights</p>
      <p class="text-palette-gray mb-6">Importe uma planilha para gerarmos análises sobre seus clientes.</p>
      <RouterLink to="/uploads"
        class="inline-block bg-palette-mid-green text-white font-semibold px-8 py-3 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm">
        Importar planilha
      </RouterLink>
    </div>

    <section v-else class="grid grid-cols-1 md:grid-cols-2 gap-5">
      <article v-for="insight in insights" :key="insight.titulo"
        class="bg-white rounded-2xl p-6 shadow-[0_10px_30px_rgba(38,53,31,0.09)] border-t-4" :class="estilos[insight.tipo].borda">
        <span class="inline-block text-xs font-semibold px-3 py-1 rounded-full mb-3" :class="estilos[insight.tipo].badge">
          {{ estilos[insight.tipo].rotulo }}
        </span>
        <h2 class="text-lg font-bold text-palette-chumbo mb-2">{{ insight.titulo }}</h2>
        <p class="text-sm text-palette-gray leading-relaxed">{{ insight.texto }}</p>
      </article>
    </section>
  </main>
</template>
