<script setup>
const currentYear = new Date().getFullYear()

const productLinks = [
  { label: 'Funcionalidades', to: { path: '/', hash: '#funcionalidades' } },
  { label: 'Showcase', to: '/' },
  { label: 'Atualizações', to: '/' },
]

const companyLinks = [
  { label: 'Sobre nós', to: '/' },
  { label: 'Segurança de Dados', to: '/' },
  { label: 'Termos de Uso', to: '/' },
  { label: 'Contato', to: '/contact' },
]
</script>

<template>
  <footer class="bg-gradient-to-b from-[#ececec] via-white to-white pt-20 pb-10 px-8 md:px-[400px]">

    <!-- Parte superior: marca + navegação -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-[2fr_1fr_1fr_1fr] gap-10 lg:gap-16 pb-16">

      <!-- Marca -->
      <section aria-labelledby="footer-brand">
        <h2 id="footer-brand" class="sr-only">DataWise</h2>
        <RouterLink to="/" class="inline-block mb-6">
          <img src="../assets/Logo/Logo-white.png" alt="Logo DataWise" class="h-7 w-auto">
        </RouterLink>
        <p class="text-sm text-palette-gray leading-relaxed max-w-[260px]">
          Transformando o caos das planilhas Excel em dashboards organizados e
          ricos para análises de negócios.
        </p>
      </section>

      <!-- Produto -->
      <nav aria-labelledby="footer-product">
        <h2 id="footer-product" class="text-sm font-bold text-palette-chumbo mb-5">Produto</h2>
        <ul class="flex flex-col gap-4">
          <li v-for="link in productLinks" :key="link.label">
            <RouterLink :to="link.to"
              class="text-sm text-palette-gray hover:text-palette-mid-green transition-colors">
              {{ link.label }}
            </RouterLink>
          </li>
        </ul>
      </nav>

      <!-- Empresa -->
      <nav aria-labelledby="footer-company">
        <h2 id="footer-company" class="text-sm font-bold text-palette-chumbo mb-5">Empresa</h2>
        <ul class="flex flex-col gap-4">
          <li v-for="link in companyLinks" :key="link.label">
            <RouterLink :to="link.to"
              class="text-sm text-palette-gray hover:text-palette-mid-green transition-colors">
              {{ link.label }}
            </RouterLink>
          </li>
        </ul>
      </nav>

      <!-- Redes sociais -->
      <section aria-labelledby="footer-social">
        <h2 id="footer-social" class="text-sm font-bold text-palette-chumbo mb-5">Siga-me</h2>
        <ul class="flex gap-3">
          <li>
            <a href="https://www.linkedin.com/in/kauanmpiacente/" target="_blank" rel="noopener noreferrer" aria-label="LinkedIn"
              class="block rounded-lg hover:opacity-80 transition-opacity duration-300">
              <img src="../assets/home/linkedin-icon.png" alt="" aria-hidden="true" class="w-9 h-9">
            </a>
          </li>
          <li>
            <a href="https://github.com/kauanzeraaa" target="_blank" rel="noopener noreferrer" aria-label="GitHub"
              class="block rounded-lg hover:opacity-80 transition-opacity duration-300">
              <img src="../assets/home/github-icon.png" alt="" aria-hidden="true" class="w-9 h-9">
            </a>
          </li>
        </ul>
      </section>
    </div>

    <!-- Parte inferior: direitos e créditos -->
    <div class="flex flex-col md:flex-row justify-between gap-4 pt-6 border-t border-palette-gray/60">
      <p><small class="text-xs text-palette-chumbo">
        &copy; {{ currentYear }} DataWise Tecnologia LTDA. Todos os direitos reservados.
      </small></p>
      <p><small class="text-xs text-palette-chumbo">
        Desenvolvido para o curso de Análise e Desenvolvimento de Sistemas pelo aluno Kauan Piacente.
      </small></p>
    </div>

  </footer>
</template>

