import { createRouter, createWebHistory } from 'vue-router'

import Home from  '../views/Home.vue'
import Login from  '../views/Login.vue'
import Cadastro from  '../views/Cadastro.vue'
import Contact from  '../views/Contact.vue'
import Functionality from '../views/Functionality.vue'
import Upload from '../views/Upload.vue'
import Relatorio from '../views/Relatorio.vue'
import Dashboard from '../views/Dashboard.vue'
import Insights from '../views/Insights.vue'
import Perfil from '../views/Perfil.vue'
import { useAuthStore, MODO_SEM_BACKEND } from '../store/authStore'

const routes = [
    { path : '/', component: Home},
    { path : '/login', component: Login},
    { path : '/cadastro', component: Cadastro},
    { path : '/contact', component: Contact},
    { path : '/functionality', component: Functionality},
    // Área logada (requer autenticação)
    { path : '/dashboard', component: Dashboard, meta: { requerAuth: true }},
    { path : '/uploads', component: Upload, meta: { requerAuth: true }},
    { path : '/insights', component: Insights, meta: { requerAuth: true }},
    { path : '/perfil', component: Perfil, meta: { requerAuth: true }},
    { path : '/relatorio', component: Relatorio, meta: { requerAuth: true }}
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

// Protege as rotas da área logada.
// Enquanto o backend não estiver pronto (MODO_SEM_BACKEND), o acesso fica liberado sem login.
router.beforeEach((to) => {
    if (MODO_SEM_BACKEND) return true
    const auth = useAuthStore()
    if (to.meta.requerAuth && !auth.estaAutenticado) return '/login'
    if ((to.path === '/login' || to.path === '/cadastro') && auth.estaAutenticado) return '/dashboard'
})

export default router