// src/api/triage.js
import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

// 获取科室列表
export function getDepts() {
  return axios.get(`${BASE_URL}/depts`);
}

// 获取某科室全部医生
export function getDoctors(deptId) {
  return axios.get(`${BASE_URL}/doctors`, {
    params: { deptId }
  });
}

// 智能分诊接口
export function postTriageAdvise(payload) {
  return axios.post(`${BASE_URL}/triage/advise`, payload);
}

// 查询当前用户所有就诊人
export function getPatients(userId) {
  return axios.get(`${BASE_URL}/patients`, {
    params: { userId }
  });
}

// 新增就诊人
export function createPatient(userId, payload) {
  return axios.post(`${BASE_URL}/patients`, payload, {
    params: { userId }
  });
}

// 创建挂号
export function createRegister(userId, payload) {
  return axios.post(`${BASE_URL}/registers`, payload, {
    params: { userId }
  });
}
