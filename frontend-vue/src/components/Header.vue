<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const FEATURES_HASH = '#funcionalidades'
const HEADER_HEIGHT = 72

const linkClass = 'hover:text-palette-mid-green transition-colors pb-1'
const activeClass = 'border-b-2 border-palette-mid-green text-palette-mid-green'

// Home e Funcionalidades ficam na mesma rota ("/"), então o estado ativo é decidido pelo hash
const isHomeActive = computed(() => route.path === '/' && route.hash !== FEATURES_HASH)
const isFeaturesActive = computed(() => route.path === '/' && route.hash === FEATURES_HASH)

// Se a URL já tiver o hash, o router não navega de novo: rola manualmente até a section
function scrollToFeatures(event) {
  if (!isFeaturesActive.value) return
  const section = document.querySelector(FEATURES_HASH)
  if (!section) return
  event.preventDefault()
  const top = section.getBoundingClientRect().top + window.scrollY - HEADER_HEIGHT
  window.scrollTo({ top, behavior: 'smooth' })
}
</script>

<template>
  <header class="sticky top-0 z-50 flex justify-between items-center h-18 px-8 md:px-[400px] bg-[#fff] border-[#fff] shadow-[0_10px_30px_rgba(38,53,31,0.09)]">

    <!-- Logo -->
    <RouterLink to="/" class="flex-shrink-0">
      <img src="../assets/Logo/Logo-white.png" alt="Logo DataWise" class="h-7 w-auto">
    </RouterLink>

    <!-- Navegação Central -->
    <nav class="hidden md:flex space-x-12 text-black font-medium">
      <RouterLink
        to="/"
        :class="[linkClass, isHomeActive && 'border-b-2 border-palette-mid-green']"
        :aria-current="isHomeActive ? 'page' : undefined"
      >
        Home
      </RouterLink>

      <RouterLink
        :to="{ path: '/', hash: FEATURES_HASH }"
        :class="[linkClass, isFeaturesActive && activeClass]"
        :aria-current="isFeaturesActive ? 'location' : undefined"
        @click="scrollToFeatures"
      >
        Funcionalidades
      </RouterLink>

      <RouterLink
        to="/contact"
        :class="linkClass"
        :exact-active-class="activeClass"
      >
        Contato
      </RouterLink>
    </nav>

    <!-- Botão de Login -->
    <RouterLink
      to="/login"
      class="bg-palette-mid-green text-white font-semibold px-4 py-1.5 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm"
    >
      Acessar Sistema
    </RouterLink>

  </header>
</template>