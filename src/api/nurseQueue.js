// src/api/nurseQueue.js
import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

// 查询候诊队列列表
export function fetchQueue(params) {
  return axios.get(`${BASE_URL}/nurse/queue/list`, {
    params
  })
}

// 护士叫号
export function callPatient(registerId, nurseId) {
  return axios.post(`${BASE_URL}/nurse/queue/${registerId}/call`, null, {
    params: { nurseId }
  })
}

// 标记过号
export function markNoShow(registerId) {
  return axios.post(`${BASE_URL}/nurse/queue/${registerId}/markNoShow`)
}

// 设置加急
export function setPriority(registerId, priority = 100) {
  return axios.post(`${BASE_URL}/nurse/queue/${registerId}/setPriority`, null, {
    params: { priority }
  })
}

// 取消加急
export function cancelPriority(registerId) {
  return axios.post(`${BASE_URL}/nurse/queue/${registerId}/cancelPriority`)
}

// 保存分诊备注
export function saveTriage(registerId, nurseId, triageNote) {
  return axios.post(`${BASE_URL}/nurse/queue/${registerId}/triage`, {
    nurseId,
    triageNote
  })
}
