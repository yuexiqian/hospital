// src/api/doctor.js
import axios from 'axios'

// 公共封装
function request (config) {
  return axios(config).then(res => res.data)
}

// ===== 医生端候诊队列 =====

// 医生端候诊队列
// params: { doctorId, date?: 'yyyy-MM-dd' }
export function getDoctorQueue (params) {
  return request({
    url: '/api/doctor/queue',
    method: 'get',
    params
  })
}

// 开始就诊：把队列状态改为“就诊中”
export function startVisit (registerId) {
  return request({
    url: `/api/doctor/queue/${registerId}/start`,
    method: 'post'
  })
}

// 结束就诊：把队列状态改为“已完成”（一般在提交就诊时由后端调用）
export function finishVisit (registerId) {
  return request({
    url: `/api/doctor/queue/${registerId}/finish`,
    method: 'post'
  })
}

// ===== 就诊工作台 =====

// 获取某次就诊详情（病历 + 处方）
export function getVisitDetail (registerId) {
  return request({
    url: `/api/doctor/visit/${registerId}`,
    method: 'get'
  })
}

// 保存病历 + 处方（草稿 / 提交）
export function saveVisit (registerId, data) {
  return request({
    url: `/api/doctor/visit/${registerId}/save`,
    method: 'post',
    data
  })
}

// 某个患者的历史就诊
export function getPatientHistory (patientId) {
  return request({
    url: `/api/doctor/patient/${patientId}/history`,
    method: 'get'
  })
}

// 药品查询（医生端药品搜索）
export function searchDrugs (keyword) {
  return request({
    url: '/api/doctor/drug-lib',
    method: 'get',
    params: { keyword }
  })
}