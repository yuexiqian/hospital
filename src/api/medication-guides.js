// src/api/medication-guides.js
import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

/**
 * GET /api/medication-guides?userId={userId}
 * 获取用户的用药指导列表（按用户ID）
 */
export function getMedicationGuides(userId) {
  return axios.get(`${BASE_URL}/medication-guides`, {
    params: { userId }
  })
}

/**
 * GET /api/medication-guides/patient/{patientId}
 * 获取用户的用药指导列表（按就诊人ID）
 */
export function getMedicationGuidesByPatient(patientId) {
  return axios.get(`${BASE_URL}/medication-guides/patient/${patientId}`)
}