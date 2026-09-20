import { createRouter, createWebHistory } from 'vue-router'

import Home from  '../views/Home.vue'
import Login from  '../views/Login.vue'
import Contato from  '../views/Contato.vue'
import Solucoes from  '../views/Solucoes.vue'
import ComoFunciona from  '../views/ComoFunciona.vue'

const routes = [
    { path : '/', component: Home},
    { path : '/login', component: Login},
    { path : '/contato', component: Contato},
    { path : '/solucoes', component: Solucoes},
    { path : '/como-funciona', component: ComoFunciona}
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router