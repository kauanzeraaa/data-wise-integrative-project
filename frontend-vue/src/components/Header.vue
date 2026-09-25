<template>
  <header class="sticky top-0 flex justify-between items-center h-18 px-8 md:px-[400px] bg-[#fff] border-[#fff] shadow-[0_10px_30px_rgba(38,53,31,0.09)]">

    <!-- Logo -->
    <RouterLink to="/" class="flex-shrink-0">
      <img src="../assets/Logo/Logo-white.png" alt="Logo DataWise" class="h-7 w-auto">
    </RouterLink>

    <!-- Navegação Central -->
    <nav class="hidden md:flex space-x-12 text-black font-medium">
      <RouterLink
        to="/"
        class="hover:text-palette-mid-green transition-colors pb-1"
        exact-active-class="border-b-2 border-palette-mid-green"
      >
        Home
      </RouterLink>

      <RouterLink
        to="/functionality"
        class="hover:text-palette-mid-green transition-colors pb-1"
        exact-active-class="border-b-2 border-palette-mid-green text-palette-mid-green"
      >
        Funcionalidades
      </RouterLink>

      <RouterLink
      to="/how-works"
        class="hover:text-palette-mid-green transition-colors pb-1"
        exact-active-class="border-b-2 border-palette-mid-green text-palette-mid-green"
      >
        Como Funciona
      </RouterLink>

      <RouterLink
        to="/contact"
        class="hover:text-palette-mid-green transition-colors pb-1"
        exact-active-class="border-b-2 border-palette-mid-green text-palette-mid-green"
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