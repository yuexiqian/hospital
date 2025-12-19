<!-- PharmacistLayout.vue - 修改侧边栏 -->
<template>
  <div class="pharm-layout">
    <!-- 左侧导航栏 -->
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-text">药师端 · 门诊工作站</div>
      </div>
      <nav class="menu">
        <RouterLink
          to="/pharmacist/home"
          class="menu-item"
          :class="{ active: isActive('/pharmacist/home') }"
        >
          药品知识库
        </RouterLink>
        <RouterLink
          to="/pharmacist/inventory"
          class="menu-item"
          :class="{ active: isActive('/pharmacist/inventory') }"
        >
          库存管理
        </RouterLink>
        <RouterLink
          to="/pharmacist/prescriptions"
          class="menu-item"
          :class="{ active: isActive('/pharmacist/prescriptions') }"
        >
          处方调配
        </RouterLink>
      </nav>
    </aside>

    <!-- 右侧主区域 -->
    <main class="main">
      <header class="main-header">
        <div class="user-info">
          <span>当前药师：{{ currentUser?.loginName || '未登录' }}</span>
          <button class="logout" @click="handleLogout">退出登录</button>
        </div>
      </header>

      <section class="content">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'

const route = useRoute()
const router = useRouter()

// 登录用户
const currentUser = ref(null)

// 侧边栏高亮逻辑
const isActive = (path) => {
  return route.path.startsWith(path)
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('currentUser')
  router.push('/login')
}

// 加载当前用户
onMounted(() => {
  const saved = localStorage.getItem('currentUser')
  if (saved) {
    try {
      currentUser.value = JSON.parse(saved)
    } catch (e) {
      console.error(e)
    }
  }
})
</script>

<style scoped>
.pharm-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

/* 左侧侧边栏 - 进一步缩短 */
.sidebar {
  width: 200px; /* 从220px改为200px，进一步缩短 */
  background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%);
  color: #ffffff;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px 16px; /* 减少左右内边距 */
  display: flex;
  align-items: center;
  gap: 10px; /* 减少图标和文字之间的间距 */
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  font-size: 24px; /* 稍微缩小图标 */
}

.logo-text {
  font-size: 16px; /* 缩小字体大小 */
  font-weight: 600;
  white-space: nowrap; /* 防止文字换行 */
}

.menu {
  padding: 16px 8px; /* 减少左右内边距 */
  flex: 1;
}

/* 菜单项进一步缩短 */
.menu-item {
  display: block;
  width: 100%;
  border: none;
  padding: 10px 12px; /* 减少内边距：上下10px，左右12px */
  margin-bottom: 6px; /* 减少底部间距 */
  border-radius: 999px;
  text-align: left;
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  cursor: pointer;
  font-size: 13px; /* 缩小字体大小 */
  transition: all 0.2s ease;
  text-decoration: none;
  white-space: nowrap; /* 防止文字换行 */
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.2);
}

.menu-item.active {
  background: #ffffff;
  color: #1e40af;
  font-weight: 500;
}

/* 右侧主区域 */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.main-header {
  padding: 16px 24px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%);
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
  border-radius: 999px;
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
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>