// src/api/bills.js（修改版）
import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

/**
 * GET /api/bills/summary?userId=1
 */
export async function getBillsSummary(userId) {
  return axios.get(`${BASE_URL}/bills/summary`, {
    params: { userId }
  })
}

/**
 * GET /api/bills/summary-by-patient?patientId=1
 */
export async function getBillsSummaryByPatient(patientId) {
  return axios.get(`${BASE_URL}/bills/summary-by-patient`, {
    params: { patientId }
  })
}

/**
 * GET /api/bills/unpaid?userId=1
 */
export async function getUnpaidBills(userId) {
  return axios.get(`${BASE_URL}/bills/unpaid`, {
    params: { userId }
  })
}

/**
 * GET /api/bills/unpaid-by-patient?patientId=1
 */
export async function getUnpaidBillsByPatient(patientId) {
  return axios.get(`${BASE_URL}/bills/unpaid-by-patient`, {
    params: { patientId }
  })
}

/**
 * GET /api/bills/history?userId=1
 */
export async function getHistoryBills(userId) {
  return axios.get(`${BASE_URL}/bills/history`, {
    params: { userId }
  })
}

/**
 * GET /api/bills/history-by-patient?patientId=1
 */
export async function getHistoryBillsByPatient(patientId) {
  return axios.get(`${BASE_URL}/bills/history-by-patient`, {
    params: { patientId }
  })
}

/**
 * GET /api/bills/{billId}/details
 */
export async function getBillDetails(billId) {
  return axios.get(`${BASE_URL}/bills/${billId}/details`)
}

/**
 * POST /api/bills/{billId}/pay
 */
export async function payBill(billId) {
  return axios.post(`${BASE_URL}/bills/${billId}/pay`)
}