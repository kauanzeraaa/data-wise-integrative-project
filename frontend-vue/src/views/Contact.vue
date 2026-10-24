<script setup>
import { reactive, ref } from 'vue'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import linkedinIcon from '../assets/home/linkedin-icon.png'
import githubIcon from '../assets/home/github-icon.png'

const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const subjects = ['Dúvida sobre o produto', 'Suporte técnico', 'Parcerias', 'Outro']

const form = reactive({ name: '', email: '', subject: '', message: '' })
const errors = reactive({})
const sent = ref(false)

const channels = [
  {
    label: 'LinkedIn',
    value: 'linkedin.com/in/kauanmpiacente',
    href: 'https://www.linkedin.com/in/kauanmpiacente/',
    icon: linkedinIcon,
  },
  {
    label: 'GitHub',
    value: 'github.com/kauanzeraaa',
    href: 'https://github.com/kauanzeraaa',
    icon: githubIcon,
  },
]

function validate() {
  Object.keys(errors).forEach((key) => delete errors[key])

  if (!form.name.trim()) errors.name = 'Informe seu nome.'
  if (!REGEX_EMAIL.test(form.email.trim())) errors.email = 'Informe um e-mail válido.'
  if (!form.subject) errors.subject = 'Selecione um assunto.'
  if (form.message.trim().length < 10) errors.message = 'A mensagem deve ter pelo menos 10 caracteres.'

  return Object.keys(errors).length === 0
}

function handleSubmit() {
  sent.value = false
  if (!validate()) return

  // TODO: integrar com o backend quando o endpoint de contato existir
  sent.value = true
  Object.assign(form, { name: '', email: '', subject: '', message: '' })
}

const inputClass = 'w-full px-4 py-2.5 text-sm text-palette-chumbo bg-white border rounded-lg outline-none transition-colors focus:border-palette-mid-green focus:ring-2 focus:ring-palette-mid-green/30'
</script>

<template>
  <Header />

  <main class="bg-palette-offwhite">
    <section aria-labelledby="contact-title" class="px-8 md:px-[400px] py-16 lg:py-24">

      <!-- Cabeçalho da página -->
      <header class="flex flex-col items-center text-center mb-12 lg:mb-16">
        <p class="flex items-center gap-2 text-xs font-bold uppercase tracking-wide text-[#4f9474] mb-4">
          <span class="w-2 h-2 rounded-[2px] bg-[#4f9474]" aria-hidden="true"></span>
          Contato
        </p>
        <h1 id="contact-title" class="text-3xl md:text-4xl font-extrabold text-palette-chumbo leading-tight mb-5">
          Vamos conversar sobre seus dados
        </h1>
        <p class="text-sm md:text-base text-palette-gray max-w-2xl leading-relaxed">
          Tem alguma dúvida, sugestão ou quer saber como o DataWise pode ajudar o seu negócio?
          Envie uma mensagem e retornaremos o mais breve possível.
        </p>
      </header>

      <div class="grid grid-cols-1 lg:grid-cols-[1fr_2fr] gap-6">

        <!-- Canais de contato -->
        <aside aria-labelledby="channels-title"
          class="p-6 bg-white border border-gray-200 rounded-xl">
          <h2 id="channels-title" class="text-base font-bold text-palette-chumbo mb-2">Outros canais</h2>
          <p class="text-sm text-palette-gray leading-relaxed mb-6">
            Prefere falar diretamente? Me encontre também nas redes abaixo.
          </p>

          <address class="not-italic">
            <ul class="flex flex-col gap-4">
              <li v-for="channel in channels" :key="channel.label">
                <a :href="channel.href" target="_blank" rel="noopener noreferrer"
                  class="flex items-center gap-3 group">
                  <img :src="channel.icon" alt="" aria-hidden="true" class="w-9 h-9">
                  <span class="flex flex-col">
                    <span class="text-sm font-semibold text-palette-chumbo">{{ channel.label }}</span>
                    <span class="text-xs text-palette-gray group-hover:text-palette-mid-green transition-colors">
                      {{ channel.value }}
                    </span>
                  </span>
                </a>
              </li>
            </ul>
          </address>
        </aside>

        <!-- Formulário -->
        <section aria-labelledby="form-title" class="p-6 bg-white border border-gray-200 rounded-xl">
          <h2 id="form-title" class="text-base font-bold text-palette-chumbo mb-6">Envie uma mensagem</h2>

          <p v-if="sent" role="status"
            class="mb-6 px-4 py-3 text-sm font-medium text-[#2f6b4f] bg-[#e6f3ec] border border-palette-mid-green rounded-lg">
            Mensagem enviada com sucesso! Em breve entraremos em contato.
          </p>

          <form novalidate class="grid grid-cols-1 md:grid-cols-2 gap-5" @submit.prevent="handleSubmit">
            <div class="flex flex-col gap-1.5">
              <label for="name" class="text-sm font-semibold text-palette-chumbo">Nome</label>
              <input id="name" v-model="form.name" type="text" autocomplete="name" placeholder="Seu nome"
                :class="[inputClass, errors.name ? 'border-red-400' : 'border-gray-200']"
                :aria-invalid="!!errors.name" :aria-describedby="errors.name ? 'name-error' : undefined">
              <p v-if="errors.name" id="name-error" class="text-xs text-red-500">{{ errors.name }}</p>
            </div>

            <div class="flex flex-col gap-1.5">
              <label for="email" class="text-sm font-semibold text-palette-chumbo">E-mail</label>
              <input id="email" v-model="form.email" type="email" autocomplete="email" placeholder="voce@empresa.com"
                :class="[inputClass, errors.email ? 'border-red-400' : 'border-gray-200']"
                :aria-invalid="!!errors.email" :aria-describedby="errors.email ? 'email-error' : undefined">
              <p v-if="errors.email" id="email-error" class="text-xs text-red-500">{{ errors.email }}</p>
            </div>

            <div class="flex flex-col gap-1.5 md:col-span-2">
              <label for="subject" class="text-sm font-semibold text-palette-chumbo">Assunto</label>
              <select id="subject" v-model="form.subject"
                :class="[inputClass, errors.subject ? 'border-red-400' : 'border-gray-200', !form.subject && 'text-gray-400']"
                :aria-invalid="!!errors.subject" :aria-describedby="errors.subject ? 'subject-error' : undefined">
                <option value="" disabled>Selecione um assunto</option>
                <option v-for="subject in subjects" :key="subject" :value="subject" class="text-palette-chumbo">
                  {{ subject }}
                </option>
              </select>
              <p v-if="errors.subject" id="subject-error" class="text-xs text-red-500">{{ errors.subject }}</p>
            </div>

            <div class="flex flex-col gap-1.5 md:col-span-2">
              <label for="message" class="text-sm font-semibold text-palette-chumbo">Mensagem</label>
              <textarea id="message" v-model="form.message" rows="5" placeholder="Como podemos ajudar?"
                :class="[inputClass, 'resize-y', errors.message ? 'border-red-400' : 'border-gray-200']"
                :aria-invalid="!!errors.message" :aria-describedby="errors.message ? 'message-error' : undefined"></textarea>
              <p v-if="errors.message" id="message-error" class="text-xs text-red-500">{{ errors.message }}</p>
            </div>

            <div class="md:col-span-2 flex justify-end">
              <button type="submit"
                class="bg-palette-mid-green text-white font-semibold px-8 py-3 rounded-lg hover:bg-[#6ba88a] transition-colors duration-300 shadow-sm cursor-pointer">
                Enviar mensagem
              </button>
            </div>
          </form>
        </section>

      </div>
    </section>
  </main>

  <Footer />
</template>