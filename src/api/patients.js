// src/api/patients.js
import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

/**
 * GET /api/patients?userId={userId}
 * 获取用户的就诊人列表
 */
export function getPatients(userId) {
  return axios.get(`${BASE_URL}/patients`, {
    params: { userId }
  })
}