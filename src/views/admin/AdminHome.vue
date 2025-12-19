<!-- src/views/admin/AdminLayout.vue -->
<template>
  <div class="shell">
    <!-- 左侧导航栏 -->
    <aside class="sider">
      <div class="brand">
        <div class="logo-text">管理端 · 系统管理</div>
      </div>

      <nav class="menu">
        <RouterLink
          class="menu-item"
          to="/admin/users"
          :class="{ active: isActive('/admin/users') }"
        >
          账号管理
        </RouterLink>
        <RouterLink
          class="menu-item"
          to="/admin/dashboard"
          :class="{ active: isActive('/admin/dashboard') }"
        >
          数据看板
        </RouterLink>
        <RouterLink
          class="menu-item"
          to="/admin/registers"
          :class="{ active: isActive('/admin/registers') }"
        >
          挂号监管
        </RouterLink>
        <RouterLink
          class="menu-item"
          to="/admin/depts"
          :class="{ active: isActive('/admin/depts') }"
        >
          科室配置
        </RouterLink>
        <RouterLink
          class="menu-item"
          to="/admin/settings"
          :class="{ active: isActive('/admin/settings') }"
        >
          系统参数
        </RouterLink>
      </nav>
    </aside>

    <!-- 右侧主区域 -->
    <main class="main">
      <!-- 顶部栏（仅保留统一标题+用户信息） -->
      <header class="main-header">
        <div class="title">管理端</div>
        <div class="user-info">
          <span>当前角色：{{ role || "-" }}</span>
          <button class="logout" @click="logout">退出登录</button>
        </div>
      </header>
      <section class="content">
        <RouterView />
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

// 侧边栏高亮逻辑
const isActive = (path) => {
  return route.path.startsWith(path);
};

// 当前用户角色
const role = computed(() => {
  try {
    return JSON.parse(localStorage.getItem("currentUser") || "{}").role;
  } catch {
    return "";
  }
});

// 退出登录
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("currentUser");
  router.replace("/login");
}
</script>

<style scoped>
/* 整体布局样式不变 */
.shell {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

.sider {
  width: 200px;
  background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%);
  color: #ffffff;
  display: flex;
  flex-direction: column;
}

.brand {
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.logo-text {
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.menu {
  padding: 16px 8px;
  flex: 1;
}
.menu-item {
  display: block;
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 6px;
  border-radius: 999px;
  text-align: left;
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s ease;
  text-decoration: none;
  white-space: nowrap;
}
.menu-item:hover {
  background: rgba(255, 255, 255, 0.2);
}
.menu-item.active {
  background: #ffffff;
  color: #1e40af;
  font-weight: 500;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.main-header {
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.title {
  font-weight: 800;
  color: #ffffff;
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