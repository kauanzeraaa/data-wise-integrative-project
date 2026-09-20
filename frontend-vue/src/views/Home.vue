<script setup>
  import router from '../router'
  import Header from '../components/Header.vue'
</script>

<template>
  <Header />

  <main>

    <!-- Primeira Section: Hero -->
    <section class="flex flex-col lg:flex-row items-center justify-between px-8 md:px-[400px] py-16 lg:py-24 bg-[#fbfbfb] pt-12 gap-12">

      <!-- Coluna da Esquerda: Textos e Call to Action -->
      <div class="flex flex-col items-start w-full lg:w-[55%]">
        <h1 class="text-4xl md:text-5xl font-extrabold text-palette-chumbo leading-tight mb-6 max-w-[650px]">
          Transforme Seus <span class="text-palette-mid-green">Dados</span> Comerciais em Insights Estratégicos e Vendas
        </h1>

        <p class="text-base md:text-lg text-palette-gray font-medium max-w-lg mb-8 leading-relaxed">
          Deixe as planilhas manuais para trás. Analise padrões,
          identifique oportunidades e tome decisões inteligentes com
          nossa solução de Data Science para sua carteira de clientes.
        </p>

        <RouterLink
          to="/contact"
          class="bg-palette-mid-green text-white font-semibold px-8 py-3.5 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm"
        >
          Fale Conosco
        </RouterLink>
      </div>

      <!-- Coluna da Direita: Ilustração e Legenda -->
      <div class="w-full lg:w-[45%] flex justify-center lg:justify-end">
        <figure class="flex flex-col items-center text-center max-w-[500px]">
          <img
            src="../assets/hero-illustration.png"
            alt="Ilustração de equipe analisando gráficos e dados em telas grandes"
            class="w-full h-auto object-contain mb-6"
          >
          <figcaption class="text-sm text-palette-gray font-medium px-4">
            Deixe as planilhas manuais para trás e <br class="hidden sm:block">
            tome decisões inteligentes
          </figcaption>
        </figure>
      </div>

    </section>

    <!-- As próximas sections entrarão aqui embaixo -->

  </main>
</template>
