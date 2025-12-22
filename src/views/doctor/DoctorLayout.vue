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
        <!-- 删除标题区域，只保留用户信息 -->
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
    activeMenu.value = 'workbench'
  } else if (path.startsWith('/doctor/overview')) {
    activeMenu.value = 'home'
  } else {
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
      router.push('/doctor/queue')
      break
    case 'home':
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
  background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%); /* 深蓝色渐变 */
  color: #ffffff;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px 24px;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.menu {
  padding: 16px 12px;
}

.menu-item {
  width: 100%;
  border: none;
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 999px; /* 保持圆形 */
  text-align: left;
  background: rgba(255, 255, 255, 0.1); /* 半透明白色背景 */
  color: #ffffff;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.2);
}

.menu-item.active {
  background: #ffffff;
  color: #1e40af; /* 蓝色文字 */
  font-weight: 500;
}

/* 右侧 */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.main-header {
  padding: 16px 24px;
  display: flex;
  justify-content: flex-end; /* 用户信息靠右 */
  align-items: center;
  background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%); /* 改为深蓝色渐变 */
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #ffffff;
}

.logout {
  padding: 6px 16px;
  border-radius: 999px; /* 圆形按钮 */
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s ease;
}

.logout:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
}

.content {
  padding: 16px 24px 24px;
}
</style>