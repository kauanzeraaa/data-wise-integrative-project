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

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router