import { createRouter, createWebHistory } from 'vue-router'

import Home from  '../views/Home.vue'
import Login from  '../views/Login.vue'
import Contact from  '../views/Contact.vue'
import Solutions from '../views/Solution.vue'
import HowWorks from  '../views/HowWorks.vue'

const routes = [
    { path : '/', component: Home},
    { path : '/login', component: Login},
    { path : '/contact', component: Contact},
    { path : '/solutions', component: Solutions},
    { path : '/how-works', component: HowWorks}
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router