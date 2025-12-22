// src/api/admin.js
import axios from "axios";

/**
 * 统一处理返回：
 * - 如果后端返回 { code, msg/message, data }：code===0 -> 返回 data；否则抛错
 * - 如果后端直接返回对象/Page：直接返回
 */
function unwrap(promise) {
  return promise.then((res) => {
    const body = res.data;

    // 兼容：后端统一包装 {code, msg, data}
    if (body && typeof body === "object" && Object.prototype.hasOwnProperty.call(body, "code")) {
      if (body.code === 0) return body.data;
      const msg = body.msg || body.message || "请求失败";
      throw new Error(msg);
    }

    // 兼容：后端直接返回 Page / DTO
    return body;
  });
}

// ✅ 给所有请求自动加 Authorization（如果你有 token）
axios.interceptors.request.use((config) => {
  const token =
    localStorage.getItem("token") ||
    (() => {
      try {
        const u = JSON.parse(localStorage.getItem("currentUser") || "{}");
        return u.token;
      } catch {
        return null;
      }
    })();

  if (token) {
    config.headers = config.headers || {};
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

// ===================== Users =====================
export const adminGetUsers = (params) =>
  unwrap(axios.get("/api/admin/users", { params }));

export const adminCreateUser = (data) =>
  unwrap(axios.post("/api/admin/users", data));

export const adminUpdateUser = (userId, data) =>
  unwrap(axios.put(`/api/admin/users/${userId}`, data));

export const adminEnableUser = (userId) =>
  unwrap(axios.post(`/api/admin/users/${userId}/enable`));

export const adminDisableUser = (userId) =>
  unwrap(axios.post(`/api/admin/users/${userId}/disable`));

export const adminResetPassword = (userId, password) =>
  unwrap(
    axios.post(`/api/admin/users/${userId}/resetPassword`, null, {
      params: { password },
    })
  );

// ===================== Options =====================
export const adminOptionsDoctors = () =>
  unwrap(axios.get("/api/admin/options/doctors"));

export const adminOptionsNurses = () =>
  unwrap(axios.get("/api/admin/options/nurses"));

export const adminOptionsPharmacists = () =>
  unwrap(axios.get("/api/admin/options/pharmacists"));

// ===================== Registers（挂号监管） =====================
export const adminGetRegisters = (params) =>
  unwrap(axios.get("/api/admin/registers", { params }));

export const adminFixQueueStatus = (id, value) =>
  unwrap(
    axios.post(`/api/admin/registers/${id}/fixQueueStatus`, null, {
      params: { value },
    })
  );

// ✅ 保持你后端接口名：/setPriority
export const adminSetPriority = (id, value) =>
  unwrap(
    axios.post(`/api/admin/registers/${id}/setPriority`, null, {
      params: { value },
    })
  );

// ===================== Dashboard（统计） =====================
export const adminDashboardSummary = (params) =>
  unwrap(axios.get("/api/admin/dashboard/summary", { params }));

export const adminDashboardTrend = (params) =>
  unwrap(axios.get("/api/admin/dashboard/trend", { params }));
