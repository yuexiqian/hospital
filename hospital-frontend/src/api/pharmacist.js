// src/api/pharmacist.js
import axios from 'axios'

// 简单的 axios 实例
const request = axios.create({
  baseURL: '',
  timeout: 10000
})

// 可选：带上 token
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

export function getDrugInfoList(keyword = '') {
  return request({
    url: '/api/pharmacist/drug-info',
    method: 'get',
    params: { keyword }
  })
}

export function getDrugInfoDetail(id) {
  return request({
    url: `/api/pharmacist/drug-info/${id}`,
    method: 'get'
  })
}

export function updateDrugInfo(id, data) {
  return request({
    url: `/api/pharmacist/drug-info/${id}`,
    method: 'put',
    data
  })
}

export function createDrugInfo(data) {
  return request({
    url: '/api/pharmacist/drug-info',
    method: 'post',
    data
  })
}

// ========== 库存管理 ==========

export function getInventoryList(keyword = '') {
  return request({
    url: '/api/pharmacist/inventory',
    method: 'get',
    params: { keyword }
  })
}

export function adjustInventory(payload) {
  return request({
    url: '/api/pharmacist/inventory/adjust',
    method: 'post',
    data: payload
  })
}

export function updateInventoryStatus(id, status) {
  return request({
    url: `/api/pharmacist/inventory/${id}/status`,
    method: 'post',
    params: { status }
  })
}

// ========== 处方发药 ==========

// 处方列表
// params: { date: 'yyyy-MM-dd', status: 'SUBMITTED' | 'DISPENSED' | '' }
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
// data: { pharmacistId?, remark? }
export function dispensePrescription(id, data) {
  return request({
    url: `/api/pharmacist/prescriptions/${id}/dispense`,
    method: 'post',
    data
  })
}
