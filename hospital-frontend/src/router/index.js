// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

// 登录页
import LoginPage from '../views/LoginPage.vue'

// 患者端
import MainLayout from '../components/MainLayout.vue'

// 护士端
import NurseQueuePage from '../views/nurse/NurseQueuePage.vue'

// 医生端：布局 + 子页面
import DoctorLayout from '../views/doctor/DoctorLayout.vue'
import DoctorQueuePage from '../views/doctor/DoctorQueuePage.vue'
import DoctorWorkbenchPage from '../views/doctor/DoctorWorkbenchPage.vue'
import DoctorOverviewPage from '../views/doctor/DoctorOverviewPage.vue'

// 管理端
import AdminHome from '../views/admin/AdminHome.vue'

// 药师端
import PharmacistLayout from '../views/pharmacist/PharmacistLayout.vue'
import PharmacistHome from '../views/pharmacist/PharmacistHome.vue'
import InventoryPage from '../views/pharmacist/InventoryPage.vue'
import PharmacistPrescriptionPage from '../views/pharmacist/PharmacistPrescriptionPage.vue'

const routes = [
  { path: '/', redirect: '/login' },

  { path: '/login', component: LoginPage },

  // 患者端
  { path: '/patient', component: MainLayout, meta: { role: 'PATIENT' } },

  // 护士端
  { path: '/nurse/queue', component: NurseQueuePage, meta: { role: 'NURSE' } },

  // ⭐ 医生端：只有这一套路由，千万不要再保留旧的 `/doctor/:tab?` 之类的
  {
    path: '/doctor',
    component: DoctorLayout,
    meta: { role: 'DOCTOR' },
    children: [
      // 默认重定向到候诊队列
      { path: '', redirect: '/doctor/queue' },

      // 候诊队列
      { path: 'queue', component: DoctorQueuePage },

      // 就诊工作台，带挂号 ID
      { path: 'workbench/:registerId', component: DoctorWorkbenchPage },

      // 门诊概览（占位）
      { path: 'overview', component: DoctorOverviewPage }
    ]
  },

  // 药师端（带子路由 + 侧边栏布局）
  {
    path: '/pharmacist',
    component: PharmacistLayout,
    meta: { role: 'PHARMACIST' },
    children: [
      { path: '', redirect: '/pharmacist/home' },
      { path: 'home', component: PharmacistHome },               // 药品知识库
      { path: 'inventory', component: InventoryPage },           // 库存管理
      { path: 'prescriptions', component: PharmacistPrescriptionPage } // 处方发药
    ]
  },

  // 管理员端
  { path: '/admin', component: AdminHome, meta: { role: 'ADMIN' } },

  // 兜底 404
  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 登录 + 角色守卫
router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next()
    return
  }

  const userStr = localStorage.getItem('currentUser')
  if (!userStr) {
    next('/login')
    return
  }

  const user = JSON.parse(userStr)
  const needRole = to.meta && to.meta.role

  if (needRole && user.role !== needRole) {
    // 角色不匹配：根据当前角色跳对应首页
    switch (user.role) {
      case 'PATIENT':
        next('/patient')
        break
      case 'NURSE':
        next('/nurse/queue')
        break
      case 'DOCTOR':
        next('/doctor')
        break
      case 'PHARMACIST':
        next('/pharmacist')
        break
      case 'ADMIN':
        next('/admin')
        break
      default:
        next('/login')
    }
    return
  }

  next()
})

export default router
