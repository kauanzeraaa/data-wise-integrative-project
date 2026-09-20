import { createRouter, createWebHistory } from 'vue-router'

import Home from  '../views/Home.vue'
import Login from  '../views/Login.vue'
import Contact from  '../views/Contact.vue'
import Functionality from '../views/Functionality.vue'
import HowWorks from  '../views/HowWorks.vue'

const routes = [
    { path : '/', component: Home},
    { path : '/login', component: Login},
    { path : '/contact', component: Contact},
    { path : '/functionality', component: Functionality},
    { path : '/how-works', component: HowWorks}
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router