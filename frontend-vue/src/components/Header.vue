<template>
  <header class="flex justify-between items-center h-20 px-8 md:px-[400px] bg-palette-white">

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
        to="/solutions"
        class="hover:text-palette-mid-green transition-colors pb-1"
        exact-active-class="border-b-2 border-palette-mid-green text-palette-mid-green"
      >
        Soluções
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
      class="bg-palette-mid-green text-white font-semibold px-6 py-2.5 rounded-lg hover:bg-green-700 transition-colors duration-300 shadow-sm"
    >
      Acessar Sistema
    </RouterLink>

  </header>
</template>