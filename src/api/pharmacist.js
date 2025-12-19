// src/api/pharmacist.js
import axios from 'axios'

// 简单的 axios 实例
const request = axios.create({
  // 开发环境一般用 Vite 代理 /api 到后端，所以这里 baseURL 可以留空
  baseURL: '',
  timeout: 10000
})

// 可选：带上 token（如果你 currentUser 里有 token 字段的话）
request.interceptors.request.use(
  (config) => {
    const userStr = localStorage.getItem('currentUser')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        if (user.token) {
          config.headers['Authorization'] = `Bearer ${user.token}`
        }
      } catch (e) {
        console.error(e)
      }
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ========== 药品知识库 ==========

// 列表 / 搜索
export function getDrugInfoList(keyword = '') {
  return request({
    url: '/api/pharmacist/drug-info',
    method: 'get',
    params: { keyword }
  })
}

// 详情
export function getDrugInfoDetail(id) {
  return request({
    url: `/api/pharmacist/drug-info/${id}`,
    method: 'get'
  })
}

// 更新
export function updateDrugInfo(id, data) {
  return request({
    url: `/api/pharmacist/drug-info/${id}`,
    method: 'put',
    data
  })
}

// 新建
export function createDrugInfo(data) {
  return request({
    url: '/api/pharmacist/drug-info',
    method: 'post',
    data
  })
}

// ========== 库存管理 ==========

// 库存列表（带关键字）
export function getInventoryList(keyword = '') {
  return request({
    url: '/api/pharmacist/inventory',
    method: 'get',
    params: { keyword }
  })
}

// 调整库存
// payload: { inventoryId, deltaQty, remark }
export function adjustInventory(payload) {
  return request({
    url: '/api/pharmacist/inventory/adjust',
    method: 'post',
    data: payload
  })
}

// 启用 / 停用库存记录
// status: 'ACTIVE' | 'STOPPED'
export function updateInventoryStatus(id, status) {
  return request({
    url: `/api/pharmacist/inventory/${id}/status`,
    method: 'post',
    params: { status }
    // 如果后端是 @RequestBody 接收，就把上面改成：
    // data: { status }
  })
}

// ========== 处方发药 ==========

// 处方列表
// params: { date: 'yyyy-MM-dd', status: 'PENDING' | 'DISPENSED' | '' }
export function getPrescriptionList(params = {}) {
  return request({
    url: '/api/pharmacist/prescriptions',
    method: 'get',
    params
  })
}

// 处方详情
export function getPrescriptionDetail(id) {
  return request({
    url: `/api/pharmacist/prescriptions/${id}`,
    method: 'get'
  })
}

// 发药确认
// data: { pharmacistId, remark }
export function dispensePrescription(id, data) {
  return request({
    url: `/api/pharmacist/prescriptions/${id}/dispense`,
    method: 'post',
    data
  })
}
