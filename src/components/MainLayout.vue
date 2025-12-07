<template>
  <!-- 有用户信息时显示正式布局 -->
  <div v-if="currentUser" class="layout">
    <!-- 顶部栏 -->
    <header class="topbar">
      <div class="topbar-left">
        <div class="brand">智慧门诊系统</div>
      </div>
      <div class="topbar-right">
        <div class="user-info">
          <div class="user-name">{{ currentUser.loginName }}</div>
          <div class="user-role">角色：{{ currentUser.role }}</div>
        </div>
        <button class="logout-btn" @click="logout">
          <span class="logout-icon">🚪</span>
          <span>退出登录</span>
        </button>
      </div>
    </header>

    <!-- 侧边栏 -->
    <aside class="sidebar">
      <nav class="menu">
        <button
          class="menu-item"
          :class="{ active: activeMenu === 'home' }"
          @click="activeMenu = 'home'"
        >
          <span class="menu-icon">🏠</span>
          <span class="menu-text">首页</span>
        </button>

        <button
          class="menu-item"
          :class="{ active: activeMenu === 'patients' }"
          @click="activeMenu = 'patients'"
        >
          <span class="menu-icon">👨‍⚕️</span>
          <span class="menu-text">就诊人管理</span>
        </button>

        <button class="menu-item" disabled>
          <span class="menu-icon">📋</span>
          <span class="menu-text">候诊队列（待实现）</span>
        </button>

        <!-- ⭐ 用药与费用：现在是可以点击的独立页面 -->
        <button
          class="menu-item"
          :class="{ active: activeMenu === 'medication' }"
          @click="activeMenu = 'medication'"
        >
          <span class="menu-icon">💊</span>
          <span class="menu-text">用药与费用</span>
        </button>
      </nav>
    </aside>

    <!-- 主内容 -->
    <main class="content">
      <!-- 首页：A 区 + B 区 都在这里 -->
      <section v-if="activeMenu === 'home'" class="section home-section">
        <!-- A 区：今日就诊概览 -->
        <HomeOverviewPanel :user-id="currentUser.userId" />

        <!-- 分割一下上下两块 -->
        <hr class="inner-divider" />

        <!-- B 区：智能分诊与挂号 -->
        <SmartTriagePanel :user-id="currentUser.userId" />
      </section>

      <!-- 就诊人管理 -->
      <section v-else-if="activeMenu === 'patients'" class="section full-width">
        <PatientManager :user-id="currentUser.userId" />
      </section>

      <!-- ⭐ 用药与费用独立页面 -->
      <section v-else-if="activeMenu === 'medication'" class="section full-width">
        <MedicationBillsPanel :user-id="currentUser.userId" />
      </section>

      <!-- 其他占位（理论上现在用不到） -->
      <section v-else class="section">
        <p>功能开发中...</p>
      </section>
    </main>
  </div>

  <!-- 无用户信息时占位 -->
  <div v-else class="content loading-screen">
    <div class="loading-spinner"></div>
    <p>正在跳转到登录页...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import HomeOverviewPanel from './HomeOverviewPanel.vue'
import PatientManager from './PatientManager.vue'
import SmartTriagePanel from './SmartTriagePanel.vue'
import MedicationBillsPanel from './MedicationBillsPanel.vue' // ⭐ 新增：用药与费用页面

const router = useRouter()

const currentUser = ref(null)
const activeMenu = ref('home')

// 页面加载时读取登录用户
onMounted(() => {
  const saved = localStorage.getItem('currentUser')
  if (!saved) {
    router.push('/login')
  } else {
    try {
      currentUser.value = JSON.parse(saved)
    } catch (e) {
      console.error(e)
      router.push('/login')
    }
  }
})

function logout() {
  localStorage.removeItem('currentUser')
  router.push('/login')
}
</script>

<style scoped>
/* 全局布局基准：统一内边距为2rem */
:root {
  --padding-base: 2rem;
}

.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  max-width: 1920px;
  margin: 0 auto;
  background: #f8fafc;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  color: #e2e8f0;
  padding: 1rem var(--padding-base);
  height: 70px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.topbar-left {
  display: flex;
  align-items: center;
}

.brand {
  font-size: 1.75rem;
  font-weight: 700;
  color: #ffffff;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.user-info {
  text-align: right;
  padding-right: 1.5rem;
  border-right: 1px solid #334155;
}

.user-name {
  font-weight: 600;
  font-size: 1.1rem;
  color: #ffffff;
  margin-bottom: 0.25rem;
}

.user-role {
  opacity: 0.9;
  font-size: 0.9rem;
  color: #94a3b8;
}

.logout-btn {
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: #fff;
  padding: 0.75rem 1.25rem;
  font-size: 0.95rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.3);
}

.logout-icon {
  font-size: 1.1rem;
}

.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  color: #e2e8f0;
  display: flex;
  flex-direction: column;
  padding: var(--padding-base); /* 侧边栏内边距与全局基准统一 */
  box-shadow: 4px 4px 15px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 70px; /* 顶部栏高度 */
  left: 0;
  bottom: 0;
  z-index: 99;
}

.menu {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.menu-item {
  text-align: left;
  padding: 1rem 1.25rem;
  border-radius: 12px;
  border: none;
  background: transparent;
  color: #cbd5e1;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  transition: all 0.3s ease;
}

.menu-item:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  transform: translateX(5px);
}

.menu-item.active {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.menu-item:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.menu-icon {
  font-size: 1.25rem;
}

.menu-text {
  font-weight: 500;
}

/* 主内容区：内边距与侧边栏完全一致，解决偏左问题 */
.content {
  flex: 1;
  background: #f8fafc;
  margin-left: 240px; 
  margin-top: 70px; 
  padding: var(--padding-base); /* 与侧边栏内边距统一 */
  min-height: calc(100vh - 70px); 
  overflow-y: auto;
  box-sizing: border-box;
  width: calc(100% - 240px);
}

.loading-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1.5rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 内容区块：宽度100% + 内边距与全局统一 */
.section {
  background: #ffffff;
  border-radius: 16px;
  padding: var(--padding-base); 
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 1.5rem;
  margin-top: 0; 
  box-sizing: border-box;
  width: 100%;
}

.home-section {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  margin-top: 0;
}

.full-width {
  width: 100%;
  box-sizing: border-box;
}

.inner-divider {
  margin: 1.5rem 0;
  border: none;
  border-top: 2px dashed #e2e8f0;
  opacity: 0.7;
}
</style>