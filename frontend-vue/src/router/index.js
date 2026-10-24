import { createRouter, createWebHistory } from 'vue-router'

import Home from  '../views/Home.vue'
import Login from  '../views/Login.vue'
import Cadastro from  '../views/Cadastro.vue'
import Contact from  '../views/Contact.vue'
import Functionality from '../views/Functionality.vue'
import Upload from '../views/Upload.vue'
import Relatorio from '../views/Relatorio.vue'

const routes = [
    { path : '/', component: Home},
    { path : '/login', component: Login},
    { path : '/cadastro', component: Cadastro},
    { path : '/contact', component: Contact},
    { path : '/functionality', component: Functionality},
    { path : '/uploads', component: Upload},
    { path : '/relatorio', component: Relatorio}
]

const HEADER_HEIGHT = 72 // altura do header fixo (h-18)

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
        // Voltar/avançar do navegador: restaura a posição anterior
        if (savedPosition) return savedPosition

        // Links com âncora (ex.: /#funcionalidades): rola até a section
        if (to.hash) {
            return { el: to.hash, top: HEADER_HEIGHT, behavior: 'smooth' }
        }

        return { top: 0 }
    }
})

export default router