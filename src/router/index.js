// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";

// 登录页
import LoginPage from "../views/LoginPage.vue";

// 患者端
import MainLayout from "../components/MainLayout.vue";

// 护士端
import NurseQueuePage from "../views/nurse/NurseQueuePage.vue";

// 医生端
import DoctorLayout from "../views/doctor/DoctorLayout.vue";
import DoctorQueuePage from "../views/doctor/DoctorQueuePage.vue";
import DoctorWorkbenchPage from "../views/doctor/DoctorWorkbenchPage.vue";
import DoctorOverviewPage from "../views/doctor/DoctorOverviewPage.vue";

// 管理端
import AdminLayout from "../views/admin/AdminLayout.vue";
import AdminHome from "../views/admin/AdminHome.vue";
import AdminUsersPage from "../views/admin/AdminUsersPage.vue";


// ✅ 新增：这俩一定要有文件
import AdminDashboardPage from "../views/admin/AdminDashboardPage.vue";
import AdminRegistersPage from "../views/admin/AdminRegistersPage.vue";

// 药师端
import PharmacistLayout from "../views/pharmacist/PharmacistLayout.vue";
import PharmacistHome from "../views/pharmacist/PharmacistHome.vue";
import InventoryPage from "../views/pharmacist/InventoryPage.vue";
import PharmacistPrescriptionPage from "../views/pharmacist/PharmacistPrescriptionPage.vue";

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", component: LoginPage },

  // 患者端
  { path: "/patient", component: MainLayout, meta: { role: "PATIENT" } },

  // 护士端
  { path: "/nurse/queue", component: NurseQueuePage, meta: { role: "NURSE" } },

  // 医生端
  {
    path: "/doctor",
    component: DoctorLayout,
    meta: { role: "DOCTOR" },
    children: [
      { path: "", redirect: "/doctor/queue" },
      { path: "queue", component: DoctorQueuePage },
      { path: "workbench/:registerId", component: DoctorWorkbenchPage },
      { path: "overview", component: DoctorOverviewPage },
    ],
  },

  // 药师端
  {
    path: "/pharmacist",
    component: PharmacistLayout,
    meta: { role: "PHARMACIST" },
    children: [
      { path: "", redirect: "/pharmacist/home" },
      { path: "home", component: PharmacistHome },
      { path: "inventory", component: InventoryPage },
      { path: "prescriptions", component: PharmacistPrescriptionPage },
    ],
  },

  // ✅ 管理端（带布局+子路由）
  {
    path: "/admin",
    component: AdminLayout,
    meta: { role: "ADMIN" },
    children: [
      // ✅ 建议默认进账号管理（你也可以改成 dashboard）
      { path: "", redirect: "/admin/users" },

      { path: "home", component: AdminHome }, // 你想留就留
      { path: "users", component: AdminUsersPage },
      

      // ✅ 关键：把这俩加上
      { path: "dashboard", component: AdminDashboardPage },
      { path: "registers", component: AdminRegistersPage },
    ],
  },

  // 兜底 404
  { path: "/:pathMatch(.*)*", redirect: "/login" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 登录 + 角色守卫
router.beforeEach((to, from, next) => {
  if (to.path === "/login") return next();

  const userStr = localStorage.getItem("currentUser");
  if (!userStr) return next("/login");

  const user = JSON.parse(userStr);
  const needRole = to.meta && to.meta.role;

  if (needRole && user.role !== needRole) {
    switch (user.role) {
      case "PATIENT": return next("/patient");
      case "NURSE": return next("/nurse/queue");
      case "DOCTOR": return next("/doctor");
      case "PHARMACIST": return next("/pharmacist");
      case "ADMIN": return next("/admin");
      default: return next("/login");
    }
  }

  next();
});

export default router;
