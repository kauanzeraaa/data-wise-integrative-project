import { createRouter, createWebHistory } from 'vue-router'

import Home from  '../views/Home.vue'
import Login from  '../views/Login.vue'
import Contato from  '../views/Contact.vue/index.js'
import Solucoes from  '../views/Solutions.vue/index.js'
import ComoFunciona from  '../views/HowWorks.vue/index.js'

const routes = [
    { path : '/', component: Home},
    { path : '/login', component: Login},
    { path : '/contact', component: Contato},
    { path : '/solutions', component: Solucoes},
    { path : '/how-works', component: ComoFunciona}
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router