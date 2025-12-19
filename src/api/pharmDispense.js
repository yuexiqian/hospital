// src/api/pharmDispense.js
import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/pharmacist'

// 处方列表
export function fetchPharmPrescriptions(params) {
  // params: { date, status }
  return axios.get(`${BASE_URL}/prescriptions`, { params })
}

// 处方详情
export function fetchPharmPrescriptionDetail(id) {
  return axios.get(`${BASE_URL}/prescriptions/${id}`)
}

// 发药
export function dispensePrescription(id, payload) {
  // payload: { pharmacistId, remark }
  return axios.post(`${BASE_URL}/prescriptions/${id}/dispense`, payload)
}
