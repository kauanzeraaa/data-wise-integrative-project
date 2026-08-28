<script setup>
  import router from '../router'
</script>

<template>
  <header class="flex justify-between items-center h-20 px-100">
    <img src="../assets/Logo/Logo-white.png" alt="Logo DataWise">
    <nav class=" space-x-12 ">
      <a href="home" class="">Home</a>
      <a href="solutions" class="">Soluções</a>
      <a href="how-works" class="">Como Funciona</a>
      <a href="contact" class="">Contato</a>
    </nav>

    <button class=" bg-palette-mid-green text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors duration-300">
        <RouterLink to="/login">Acessar Sistema</RouterLink>
    </button>
  </header>

  <section id="home" class=" bg-amber-100 h-100">
    <div>
        <p>Transforme Seus Dados Comerciais em Insights Estratégicos e Vendas</p>
        <p>Deixe as planilhas manuais para trás. Analise padrões, identifique oportunidades e tome decisões inteligentes com nossa solução de Data Science para sua carteira de clientes.</p>
    </div>

    <div>
        
    </div>

  </section>
</template>
