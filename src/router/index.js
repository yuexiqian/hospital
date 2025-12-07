// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../views/LoginPage.vue'
import MainLayout from '../components/MainLayout.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginPage },
  // 患者端主界面：里面的首页已经包含 A 区 + B 区
  { path: '/patient', component: MainLayout }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
