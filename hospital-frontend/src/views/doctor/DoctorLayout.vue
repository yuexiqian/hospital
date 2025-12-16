<template>
  <div class="doctor-layout">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <div class="logo">医生端 · 门诊工作站</div>
      <nav class="menu">
        <button
          class="menu-item"
          :class="{ active: activeMenu === 'queue' }"
          @click="goMenu('queue')"
        >
          候诊队列
        </button>
        <button
          class="menu-item"
          :class="{ active: activeMenu === 'workbench' }"
          @click="goMenu('workbench')"
        >
          诊疗工作台
        </button>
        <button
          class="menu-item"
          :class="{ active: activeMenu === 'home' }"
          @click="goMenu('home')"
        >
          门诊概览
        </button>
      </nav>
    </aside>

    <!-- 右侧主区域 -->
    <main class="main">
      <header class="main-header">
        <div class="titles">
          <h1>门诊医生工作台</h1>
          <p>查看候诊队列，书写病历，开具处方</p>
        </div>
        <div class="user-info">
          <span>当前医生：{{ doctorName || '未登录' }}</span>
          <button class="logout" @click="logout">退出登录</button>
        </div>
      </header>

      <section class="content">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const doctorName = ref('')
const activeMenu = ref('queue')

const loadCurrentDoctor = () => {
  const str = localStorage.getItem('currentUser')
  if (!str) {
    doctorName.value = ''
    return
  }
  try {
    const user = JSON.parse(str)
    doctorName.value = user.name || user.loginName || ''
  } catch (e) {
    console.error('parse currentUser error', e)
    doctorName.value = ''
  }
}

// 根据当前路由，自动高亮左侧菜单
const syncActiveMenu = () => {
  const path = route.path
  if (path.startsWith('/doctor/queue')) {
    activeMenu.value = 'queue'
  } else if (path.startsWith('/doctor/workbench')) {
    // 具体某个病人的工作台：/doctor/workbench/:registerId
    activeMenu.value = 'workbench'
  } else if (path.startsWith('/doctor/overview')) {
    // ⚠️ 这里改成 overview，和路由保持一致
    activeMenu.value = 'home'
  } else {
    // 其他情况默认给候诊队列
    activeMenu.value = 'queue'
  }
}

onMounted(() => {
  loadCurrentDoctor()
  syncActiveMenu()
})

watch(
  () => route.path,
  () => syncActiveMenu()
)

const goMenu = (key) => {
  activeMenu.value = key
  switch (key) {
    case 'queue':
      router.push('/doctor/queue')
      break
    case 'workbench':
      // 这里没有具体病人的 registerId，统一回到队列，让医生从队列里点【就诊】进入工作台
      router.push('/doctor/queue')
      break
    case 'home':
      // ⚠️ 原来写的是 /doctor/home，这里要改成 /doctor/overview
      router.push('/doctor/overview')
      break
  }
}

const logout = () => {
  localStorage.removeItem('currentUser')
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.doctor-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

/* 左侧 */
.sidebar {
  width: 260px;
  background: #111827;
  color: #e5e7eb;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px 24px;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid rgba(55, 65, 81, 0.8);
}

.menu {
  padding: 16px 12px;
}

.menu-item {
  width: 100%;
  border: none;
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 999px;
  text-align: left;
  background: transparent;
  color: #e5e7eb;
  cursor: pointer;
  font-size: 14px;
}

.menu-item.active {
  background: #7c3aed;
  color: #fff;
}

/* 右侧 */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.main-header {
  padding: 16px 24px 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.main-header h1 {
  margin: 0;
  font-size: 20px;
  color: #111827;
}

.main-header p {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #4b5563;
}

.logout {
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid #ef4444;
  background: #fef2f2;
  color: #b91c1c;
  cursor: pointer;
  font-size: 13px;
}

.logout:hover {
  background: #fee2e2;
}

.content {
  padding: 16px 24px 24px;
}
</style>
