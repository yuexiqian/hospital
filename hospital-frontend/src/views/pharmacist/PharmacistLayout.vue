<!-- src/views/pharmacist/PharmacistLayout.vue -->
<template>
  <div class="pharm-layout">
    <!-- 左侧侧边栏 -->
    <aside class="sidebar">
      <div class="logo">
        智慧门诊 · 药师端
      </div>

      <nav class="menu">
        <RouterLink
          to="/pharmacist/drug-lib"
          class="menu-item"
          :class="{ active: isActive('/pharmacist/drug-lib') }"
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

    <!-- 右侧内容区 -->
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()

// 用来控制高亮
const isActive = (path) => {
  return route.path.startsWith(path)
}
</script>

<style scoped>
.pharm-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

/* 左侧栏 */
.sidebar {
  width: 220px;
  background: #020617;
  color: #e5e7eb;
  display: flex;
  flex-direction: column;
  padding: 24px 18px;
  box-sizing: border-box;
}

.logo {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 32px;
}

.menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-item {
  padding: 10px 14px;
  border-radius: 999px;
  font-size: 14px;
  color: #e5e7eb;
  text-decoration: none;
  transition: all 0.15s ease;
}

.menu-item:hover {
  background: #111827;
}

.menu-item.active {
  background: #2563eb;
  color: #ffffff;
}

/* 右侧内容 */
.content {
  flex: 1;
  padding: 24px 32px;
  box-sizing: border-box;
}
</style>
