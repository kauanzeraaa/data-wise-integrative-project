<script setup>
import router from '../router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'

const features = [
  {
    title: 'Geração Automática de Gráficos',
    description: 'Identificamos e sugerimos os melhores gráficos (coluna, pizza, dispersão) para cada conjunto de dados.',
    // Ícone: sparkles
    icon: [
      'M9.937 15.5A2 2 0 0 0 8.5 14.063l-6.135-1.582a.5.5 0 0 1 0-.962L8.5 9.936A2 2 0 0 0 9.937 8.5l1.582-6.135a.5.5 0 0 1 .963 0L14.063 8.5A2 2 0 0 0 15.5 9.937l6.135 1.581a.5.5 0 0 1 0 .964L15.5 14.063a2 2 0 0 0-1.437 1.437l-1.582 6.135a.5.5 0 0 1-.963 0z',
      'M20 3v4',
      'M22 5h-4',
    ],
  },
  {
    title: 'Filtros & Ordenação Dinâmicos',
    description: 'Segmente seus dados por data, região ou categoria com um clique diretamente no dashboard gerado.',
    // Ícone: filter
    icon: ['M22 3H2l8 9.46V19l4 2v-8.54L22 3z'],
  },
  {
    title: 'Exportação em PDF/PNG',
    description: 'Gere relatórios elegantes e prontos para apresentações em PDF de alta qualidade ou imagens PNG.',
    // Ícone: download
    icon: ['M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4', 'M7 10l5 5 5-5', 'M12 15V3'],
  },
]
</script>

<template>
  <Header />

  <main>

    <!-- Primeira Section: Hero -->
    <section
      class="flex flex-col lg:flex-row items-center justify-between px-8 md:px-[400px] py-16 lg:py-24 bg-[#f8fafc] pt-12 gap-12">

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

        <RouterLink to="/contact"
          class="bg-palette-mid-green text-white font-semibold px-8 py-3.5 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm">
          Fale Conosco
        </RouterLink>
      </div>

      <!-- Coluna da Direita: Ilustração e Legenda -->
      <div class="w-full lg:w-[45%] flex justify-center lg:justify-end">
        <figure class="flex flex-col items-center text-center max-w-[500px]">
          <img src="../assets/hero-illustration.png"
            alt="Ilustração de equipe analisando gráficos e dados em telas grandes"
            class="w-full h-auto object-contain mb-6">
          <figcaption class="text-sm text-palette-gray font-medium px-4">
            Deixe as planilhas manuais para trás e <br class="hidden sm:block">
            tome decisões inteligentes
          </figcaption>
        </figure>
      </div>

    </section>

    <section
      class="flex flex-col lg:flex-row items-center justify-between px-8 md:px-[400px] py-16 lg:py-24 bg-[#f8fafc] pt-12 gap-12">

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

        <RouterLink to="/contact"
          class="bg-palette-mid-green text-white font-semibold px-8 py-3.5 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm">
          Fale Conosco
        </RouterLink>
      </div>

      <!-- Coluna da Direita: Ilustração e Legenda -->
      <div class="w-full lg:w-[45%] flex justify-center lg:justify-end">
        <figure class="flex flex-col items-center text-center max-w-[500px]">
          <img src="../assets/hero-illustration.png"
            alt="Ilustração de equipe analisando gráficos e dados em telas grandes"
            class="w-full h-auto object-contain mb-6">
          <figcaption class="text-sm text-palette-gray font-medium px-4">
            Deixe as planilhas manuais para trás e <br class="hidden sm:block">
            tome decisões inteligentes
          </figcaption>
        </figure>
      </div>

    </section>

    <section
      class="flex flex-col lg:flex-row items-center justify-between px-8 md:px-[400px] py-16 lg:py-24 bg-[#f8fafc] pt-12 gap-12 ">

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

        <RouterLink to="/contact"
          class="bg-palette-mid-green text-white font-semibold px-8 py-3.5 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm">
          Fale Conosco
        </RouterLink>
      </div>

      <!-- Coluna da Direita: Ilustração e Legenda -->
      <div class="w-full lg:w-[45%] flex justify-center lg:justify-end">
        <figure class="flex flex-col items-center text-center max-w-[500px]">
          <img src="../assets/hero-illustration.png"
            alt="Ilustração de equipe analisando gráficos e dados em telas grandes"
            class="w-full h-auto object-contain mb-6">
          <figcaption class="text-sm text-palette-gray font-medium px-4">
            Deixe as planilhas manuais para trás e <br class="hidden sm:block">
            tome decisões inteligentes
          </figcaption>
        </figure>
      </div>

    </section>

    <section
      class="flex flex-col lg:flex-row items-center justify-between px-8 md:px-[400px] py-16 lg:py-24 bg-[#f8fafc] pt-12 gap-12 ">

      <!-- Coluna da Esquerda: Textos e Call to Action -->
      <div class="flex flex-col items-start w-full lg:w-[55%]">
        <h1 class="text-4xl md:text-5xl font-extrabold text-palette-chumbo leading-tight mb-6 max-w-[650px]">
          Transforme Seus <span class="text-palette-mid-green">Dados</span> Comerciais em Insights Estratégicos e Vendas
        </h1>

        <p class="text-base md:text-lg text-palette-gray font-medium max-w-lg mb-8 leading-relaxed ">
          Deixe as planilhas manuais para trás. Analise padrões,
          identifique oportunidades e tome decisões inteligentes com
          nossa solução de Data Science para sua carteira de clientes.
        </p>

        <RouterLink to="/contact"
          class="bg-palette-mid-green text-white font-semibold px-8 py-3.5 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm">
          Fale Conosco
        </RouterLink>
      </div>

      <!-- Coluna da Direita: Ilustração e Legenda -->
      <div class="w-full lg:w-[45%] flex justify-center lg:justify-end">
        <figure class="flex flex-col items-center text-center max-w-[500px]">
          <img src="../assets/hero-illustration.png"
            alt="Ilustração de equipe analisando gráficos e dados em telas grandes"
            class="w-full h-auto object-contain mb-6">
          <figcaption class="text-sm text-palette-gray font-medium px-4">
            Deixe as planilhas manuais para trás e <br class="hidden sm:block">
            tome decisões inteligentes
          </figcaption>
        </figure>
      </div>

    </section>

    <!-- Section: Funcionalidades -->
    <section id="funcionalidades" aria-labelledby="features-title"
      class="flex flex-col items-center px-8 md:px-[400px] py-16 lg:py-24 bg-white">

      <header class="flex flex-col items-center text-center mb-12 lg:mb-16">
        <p class="flex items-center gap-2 text-xs font-bold uppercase tracking-wide text-[#4f9474] mb-4">
          <span class="w-2 h-2 rounded-[2px] bg-[#4f9474]" aria-hidden="true"></span>
          Funcionalidades
        </p>
        <h2 id="features-title" class="text-3xl md:text-4xl font-extrabold text-palette-chumbo leading-tight mb-5">
          Tudo o que você precisa para analisar dados sem complicação
        </h2>
        <p class="text-sm md:text-base text-palette-gray max-w-2xl leading-relaxed">
          Construído especialmente para gestores, analistas de negócios e profissionais
          que não querem depender de TI para criar relatórios.
        </p>
      </header>

      <ul class="grid grid-cols-1 md:grid-cols-3 gap-4 w-full">
        <li v-for="feature in features" :key="feature.title">
          <article
            class="h-full p-4 bg-white border border-gray-200 rounded-xl hover:shadow-[0_10px_30px_rgba(38,53,31,0.09)] transition-shadow duration-300">
            <span class="flex items-center justify-center w-7 h-7 rounded-md bg-[#4f9474] text-white mb-5"
              aria-hidden="true">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="w-4 h-4">
                <path v-for="(d, i) in feature.icon" :key="i" :d="d" />
              </svg>
            </span>
            <h3 class="text-base font-bold text-palette-chumbo mb-2">{{ feature.title }}</h3>
            <p class="text-sm text-palette-gray leading-relaxed">{{ feature.description }}</p>
          </article>
        </li>
      </ul>
    </section>

  </main>

  <Footer />
</template>
