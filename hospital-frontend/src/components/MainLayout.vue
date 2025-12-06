<template>
  <!-- 有用户信息时显示正式布局 -->
  <div v-if="currentUser" class="layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="brand">智慧门诊系统</div>
      <div class="user">
        <div class="user-name">{{ currentUser.loginName }}</div>
        <div class="user-role">角色：{{ currentUser.role }}</div>
      </div>

      <nav class="menu">
        <button
          class="menu-item"
          :class="{ active: activeMenu === 'home' }"
          @click="activeMenu = 'home'"
        >
          首页
        </button>

        <button
          class="menu-item"
          :class="{ active: activeMenu === 'patients' }"
          @click="activeMenu = 'patients'"
        >
          就诊人管理
        </button>

        <button class="menu-item" disabled>
          候诊队列（待实现）
        </button>

        <!-- ⭐ 用药与费用：现在是可以点击的独立页面 -->
        <button
          class="menu-item"
          :class="{ active: activeMenu === 'medication' }"
          @click="activeMenu = 'medication'"
        >
          用药与费用
        </button>
      </nav>

      <button class="logout-btn" @click="logout">退出登录</button>
    </aside>

    <!-- 主内容 -->
    <main class="content">
      <!-- 首页：A 区 + B 区 都在这里 -->
      <section v-if="activeMenu === 'home'" class="section">
        <!-- A 区：今日就诊概览 -->
        <HomeOverviewPanel :user-id="currentUser.userId" />

        <!-- 分割一下上下两块 -->
        <hr class="inner-divider" />

        <!-- B 区：智能分诊与挂号 -->
        <SmartTriagePanel :user-id="currentUser.userId" />
      </section>

      <!-- 就诊人管理 -->
      <section v-else-if="activeMenu === 'patients'" class="section">
        <PatientManager :user-id="currentUser.userId" />
      </section>

      <!-- ⭐ 用药与费用独立页面 -->
      <section v-else-if="activeMenu === 'medication'" class="section">
        <MedicationBillsPanel :user-id="currentUser.userId" />
      </section>

      <!-- 其他占位（理论上现在用不到） -->
      <section v-else class="section">
        <p>功能开发中...</p>
      </section>
    </main>
  </div>

  <!-- 无用户信息时占位 -->
  <div v-else class="content">
    正在跳转到登录页...
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
.layout {
  display: flex;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.sidebar {
  width: 220px;
  background: #111827;
  color: #e5e7eb;
  display: flex;
  flex-direction: column;
  padding: 16px 12px;
}

.brand {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

.user {
  margin-bottom: 16px;
  font-size: 13px;
}

.user-name {
  font-weight: 500;
}

.user-role {
  opacity: 0.8;
  margin-top: 2px;
}

.menu {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  text-align: left;
  padding: 8px 10px;
  border-radius: 6px;
  border: none;
  background: transparent;
  color: #e5e7eb;
  cursor: pointer;
  font-size: 14px;
}

.menu-item.active {
  background: #2563eb;
}

.menu-item:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.logout-btn {
  border: none;
  border-radius: 6px;
  background: #ef4444;
  color: #fff;
  padding: 8px 10px;
  font-size: 14px;
  cursor: pointer;
}

.content {
  flex: 1;
  background: #f3f4f6;
  padding: 20px;
}

.section {
  background: #fff;
  border-radius: 10px;
  padding: 16px 18px;
  box-shadow: 0 1px 4px rgba(15, 23, 42, 0.08);
}

/* 首页内部 A 区 / B 区 的分隔线 */
.inner-divider {
  margin: 18px 0;
  border: none;
  border-top: 1px dashed #e5e7eb;
}
</style>
