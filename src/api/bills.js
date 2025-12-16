// src/api/bills.js
const BASE_URL = 'http://localhost:8080';

/**
 * GET /api/bills/summary?userId=1
 */
export async function getBillsSummary(userId) {
  const res = await fetch(`${BASE_URL}/api/bills/summary?userId=${userId}`);
  if (!res.ok) {
    throw new Error('获取费用摘要失败');
  }
  return await res.json();
}

/**
 * GET /api/bills/unpaid?userId=1
 */
export async function getUnpaidBills(userId) {
  const res = await fetch(`${BASE_URL}/api/bills/unpaid?userId=${userId}`);
  if (!res.ok) {
    throw new Error('获取待缴费用失败');
  }
  return await res.json();
}

/**
 * GET /api/bills/history?userId=1
 */
export async function getHistoryBills(userId) {
  const res = await fetch(`${BASE_URL}/api/bills/history?userId=${userId}`);
  if (!res.ok) {
    throw new Error('获取缴费记录失败');
  }
  return await res.json();
}

/**
 * GET /api/bills/{billId}/details
 */
export async function getBillDetails(billId) {
  const res = await fetch(`${BASE_URL}/api/bills/${billId}/details`);
  if (!res.ok) {
    throw new Error('获取费用明细失败');
  }
  return await res.json();
}

/**
 * POST /api/bills/{billId}/pay
 */
export async function payBill(billId) {
  const res = await fetch(`${BASE_URL}/api/bills/${billId}/pay`, {
    method: 'POST'
  });
  if (!res.ok) {
    throw new Error('缴费失败');
  }
}
