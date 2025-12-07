<template>
  <div class="home-overview">
    <div class="header">
      <div class="header-top">
        <h2>今日就诊概览</h2>
        <!-- 就诊人切换按钮 -->
        <div class="patient-selector">
          <div class="current-patient" @click="togglePatientDropdown">
            <span class="patient-label">当前就诊人：</span>
            <span class="patient-name">{{ currentPatient?.name || '请选择' }}</span>
            <span class="dropdown-icon">{{ showPatientDropdown ? '▲' : '▼' }}</span>
          </div>
          
          <div v-if="showPatientDropdown" class="patient-dropdown">
            <div class="dropdown-list">
              <div 
                v-for="patient in allPatients" 
                :key="patient.patientId"
                class="dropdown-item"
                :class="{ active: patient.patientId === currentPatientId }"
                @click="selectPatient(patient)"
              >
                <span class="patient-item-name">{{ patient.name }}</span>
                <span class="patient-item-phone">{{ patient.phone || '无电话' }}</span>
                <span v-if="patient.patientId === currentPatientId" class="check-icon">✓</span>
              </div>
              <div v-if="allPatients.length === 0" class="dropdown-empty">
                暂无就诊人
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <p class="tip">
        通过接口 <code>GET /api/home/overview?userId={{ userId }}&patientId={{ currentPatientId }}</code> 获取数据，
        展示今日挂号、当前候诊信息。
      </p>
    </div>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>正在加载首页信息...</p>
    </div>

    <div v-else>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

      <!-- 第一行：今日挂号 + 当前候诊 -->
      <div class="card-row">
        <!-- 今日挂号 -->
        <section class="card today-card">
          <div class="card-header">
            <div class="card-title">
              <span class="card-icon">📅</span>
              <span>今日挂号</span>
              <span v-if="currentPatient" class="patient-tag">
                {{ currentPatient.name }}
              </span>
            </div>
            <div class="card-badge" :class="today.hasRegister ? 'success' : 'neutral'">
              {{ today.hasRegister ? '已挂号' : '未挂号' }}
            </div>
          </div>

          <div v-if="today.hasRegister" class="card-body">
            <div class="info-row">
              <span class="info-label">科室：</span>
              <span class="info-value highlight">{{ today.deptName || ('科室ID：' + today.deptId) }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">医生：</span>
              <span class="info-value highlight">{{ today.doctorName || ('医生ID：' + today.doctorId) }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">排队号：</span>
              <span class="info-value badge">{{ today.queueNo }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">状态：</span>
              <span class="info-value status" :class="getStatusClass(today.status)">{{ today.status }}</span>
            </div>
            <div class="info-row time">
              <span class="info-label">挂号时间：</span>
              <span class="info-value">{{ formatTime(today.registerTime) }}</span>
            </div>
          </div>

          <div v-else class="card-body empty">
            <div class="empty-icon">📝</div>
            <p class="empty-text">今天还没有挂号记录</p>
            <p class="empty-subtext">可以通过"智能分诊与挂号"进行挂号</p>
          </div>
        </section>

        <!-- 当前候诊 -->
        <section class="card queue-card">
          <div class="card-header">
            <div class="card-title">
              <span class="card-icon">⏱️</span>
              <span>当前候诊情况</span>
              <span v-if="currentPatient" class="patient-tag">
                {{ currentPatient.name }}
              </span>
            </div>
            <div class="card-badge" :class="queue.hasWaiting ? 'warning' : 'neutral'">
              {{ queue.hasWaiting ? '候诊中' : '无需候诊' }}
            </div>
          </div>

          <div v-if="queue.hasWaiting" class="card-body">
            <div class="info-row">
              <span class="info-label">前方人数：</span>
              <span class="info-value highlight large">{{ queue.aheadCount }} 人</span>
            </div>
            <div class="info-row">
              <span class="info-label">预计等待时间：</span>
              <span class="info-value highlight large">{{ queue.estimateWaitMin }} 分钟</span>
            </div>
            <div class="progress-container">
              <div class="progress-label">等待进度</div>
              <div class="progress-bar">
                <div 
                  class="progress-fill" 
                  :style="{ width: Math.min((queue.aheadCount * 10) / 120 * 100, 100) + '%' }"
                ></div>
              </div>
              <div class="progress-text">
                估算规则：每位患者 {{ PER_PATIENT_MINUTES }} 分钟
              </div>
            </div>
          </div>

          <div v-else class="card-body empty">
            <div class="empty-icon">✅</div>
            <p class="empty-text">当前没有处于等待中的挂号</p>
            <p class="empty-subtext">可能还未挂号，或者已经就诊完成</p>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const BASE_URL = 'http://localhost:8080'
const PER_PATIENT_MINUTES = 10

const loading = ref(false)
const errorMsg = ref('')
const showPatientDropdown = ref(false)

// 当前选择的就诊人ID
const currentPatientId = ref(null)
// 当前选择的就诊人详情
const currentPatient = ref(null)
// 所有就诊人列表
const allPatients = ref([])

const overview = ref({
  todayRegister: null,
  queueStatus: null,
})

const today = ref({ hasRegister: false })
const queue = ref({ hasWaiting: false, aheadCount: 0, estimateWaitMin: 0 })

// 获取就诊人列表
async function loadPatients() {
  if (!props.userId) return
  
  try {
    const resp = await axios.get(`${BASE_URL}/api/patients`, {
      params: { userId: props.userId }
    })
    
    // 处理响应数据格式
    let patientsData = []
    if (Array.isArray(resp.data)) {
      patientsData = resp.data
    } else if (resp.data && Array.isArray(resp.data.data)) {
      patientsData = resp.data.data
    }
    
    allPatients.value = patientsData
    
    // 如果还没有选择就诊人，且列表不为空，选择第一个
    if (!currentPatientId.value && patientsData.length > 0) {
      currentPatientId.value = patientsData[0].patientId
      currentPatient.value = patientsData[0]
    } else if (currentPatientId.value) {
      // 更新当前就诊人信息
      const found = patientsData.find(p => p.patientId === currentPatientId.value)
      if (found) {
        currentPatient.value = found
      } else if (patientsData.length > 0) {
        // 如果之前选择的就诊人不存在了，选择第一个
        currentPatientId.value = patientsData[0].patientId
        currentPatient.value = patientsData[0]
      }
    }
  } catch (e) {
    console.error('加载就诊人失败:', e)
  }
}

function formatTime(t) {
  if (!t) return '-'
  return String(t).replace('T', ' ').substring(0, 16)
}

function getStatusClass(status) {
  const statusMap = {
    '待就诊': 'pending',
    '就诊中': 'processing',
    '已完成': 'completed',
    '已取消': 'cancelled'
  }
  return statusMap[status] || 'default'
}

// 切换就诊人
function selectPatient(patient) {
  currentPatientId.value = patient.patientId
  currentPatient.value = patient
  showPatientDropdown.value = false
  loadOverview()
}

// 切换下拉菜单显示
function togglePatientDropdown() {
  showPatientDropdown.value = !showPatientDropdown.value
  // 每次打开下拉菜单时刷新就诊人列表
  if (showPatientDropdown.value) {
    loadPatients()
  }
}

// 点击外部关闭下拉菜单
function handleClickOutside(event) {
  const selector = document.querySelector('.patient-selector')
  if (selector && !selector.contains(event.target)) {
    showPatientDropdown.value = false
  }
}

async function loadOverview() {
  if (!props.userId || !currentPatientId.value) {
    errorMsg.value = '请先选择就诊人'
    return
  }
  
  loading.value = true
  errorMsg.value = ''

  try {
    const resp = await axios.get(`${BASE_URL}/api/home/overview`, {
      params: { 
        userId: props.userId,
        patientId: currentPatientId.value
      }
    })
    overview.value = resp.data || {}

    today.value = overview.value.todayRegister || { hasRegister: false }
    queue.value = overview.value.queueStatus || {
      hasWaiting: false,
      aheadCount: 0,
      estimateWaitMin: 0
    }
  } catch (e) {
    console.error(e)
    errorMsg.value = '加载首页信息失败，请检查后端接口是否正常。'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPatients().then(() => {
    if (currentPatientId.value) {
      loadOverview()
    }
  })
  
  // 添加点击外部关闭事件
  document.addEventListener('click', handleClickOutside)
})

watch(
  () => props.userId,
  () => {
    loadPatients().then(() => {
      if (currentPatientId.value) {
        loadOverview()
      }
    })
  }
)

// 组件卸载时移除事件监听器
import { onUnmounted } from 'vue'
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>
<style scoped>
.home-overview {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  width: 100%;
  padding-left: 0;
  box-sizing: border-box;
}

.header {
  margin-bottom: 0.5rem;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 就诊人选择器样式 */
.patient-selector {
  position: relative;
  min-width: 200px;
}

.current-patient {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.875rem;
  color: #334155;
}

.current-patient:hover {
  border-color: #3b82f6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

.patient-label {
  color: #64748b;
  font-weight: 500;
}

.patient-name {
  font-weight: 600;
  color: #1e293b;
}

.dropdown-icon {
  font-size: 0.75rem;
  color: #94a3b8;
  margin-left: auto;
}

/* 下拉菜单 */
.patient-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 0.25rem;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  z-index: 100;
  overflow: hidden;
}

.dropdown-list {
  max-height: 200px;
  overflow-y: auto;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: all 0.15s ease;
  border-bottom: 1px solid #f1f5f9;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background: #f8fafc;
}

.dropdown-item.active {
  background: #eff6ff;
}

.patient-item-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 0.875rem;
  margin-right: auto;
}

.patient-item-phone {
  font-size: 0.75rem;
  color: #64748b;
  margin-right: 0.5rem;
}

.check-icon {
  color: #3b82f6;
  font-weight: bold;
}

.dropdown-empty {
  padding: 1rem;
  text-align: center;
  color: #94a3b8;
  font-size: 0.875rem;
}

.patient-tag {
  font-size: 0.75rem;
  padding: 0.125rem 0.5rem;
  background: #e0f2fe;
  color: #0ea5e9;
  border-radius: 12px;
  margin-left: 0.5rem;
  font-weight: 500;
}

.tip {
  margin: 0;
  font-size: 0.875rem;
  color: #64748b;
  line-height: 1.5;
}

.tip code {
  background: #f1f5f9;
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
  font-family: 'SF Mono', Monaco, monospace;
  font-size: 0.85rem;
  color: #3b82f6;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: #f8fafc;
  border-radius: 12px;
  gap: 0.75rem;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  color: #ef4444;
  font-size: 0.875rem;
  padding: 0.75rem;
  background: #fef2f2;
  border-radius: 8px;
  border-left: 4px solid #ef4444;
}

/* 核心修改：取消最大宽度、移除自动外边距，让卡片占满父容器 */
.card-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
  width: 100%;
  /* 移除 max-width 和 margin: 0 auto，让模块贴近左右边界 */
}

.card {
  background: #ffffff;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
  /* 新增：让卡片内容更舒展 */
  width: 100%;
  box-sizing: border-box;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: #e2e8f0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #f1f5f9;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  font-size: 1.125rem;
  color: #1e293b;
}

.card-icon {
  font-size: 1.25rem;
}

.card-badge {
  padding: 0.25rem 0.625rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.card-badge.success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.card-badge.warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.card-badge.danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}

.card-badge.info {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
}

.card-badge.neutral {
  background: #e2e8f0;
  color: #64748b;
}

.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  padding: 0.375rem 0;
  border-bottom: 1px dashed #f1f5f9;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
}

.info-value {
  font-size: 0.875rem;
  color: #334155;
  font-weight: 600;
}

.info-value.highlight {
  color: #1e293b;
}

.info-value.large {
  font-size: 1rem;
}

.info-value.amount {
  color: #ef4444;
  font-size: 1.125rem;
}

.info-value.badge {
  background: #3b82f6;
  color: white;
  padding: 0.25rem 0.625rem;
  border-radius: 20px;
  font-size: 0.8rem;
}

.info-value.status {
  padding: 0.25rem 0.625rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.info-value.status.pending {
  background: #fef3c7;
  color: #d97706;
}

.info-value.status.processing {
  background: #dbeafe;
  color: #1d4ed8;
}

.info-value.status.completed {
  background: #d1fae5;
  color: #059669;
}

.info-value.status.cancelled {
  background: #f1f5f9;
  color: #64748b;
}

.info-row.time {
  margin-top: auto;
  padding-top: 0.75rem;
  border-top: 1px solid #f1f5f9;
  font-size: 0.8rem;
}

.progress-container {
  margin-top: 1.25rem;
  padding: 0.75rem;
  background: #f8fafc;
  border-radius: 10px;
  /* 新增：让进度条占满卡片宽度 */
  width: 100%;
  box-sizing: border-box;
}

.progress-label {
  font-size: 0.8rem;
  color: #64748b;
  margin-bottom: 0.375rem;
}

.progress-bar {
  height: 6px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.375rem;
  width: 100%;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.progress-text {
  font-size: 0.7rem;
  color: #94a3b8;
  text-align: right;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: 1.5rem 0.75rem;
  text-align: center;
}

.empty-icon {
  font-size: 2.5rem;
  margin-bottom: 0.75rem;
  opacity: 0.5;
}

.empty-text {
  font-size: 1rem;
  color: #64748b;
  margin-bottom: 0.375rem;
  font-weight: 600;
}

.empty-subtext {
  font-size: 0.8rem;
  color: #94a3b8;
  line-height: 1.4;
}

/* 卡片特定样式 */
.today-card {
  border-left: 6px solid #3b82f6;
}

.queue-card {
  border-left: 6px solid #10b981;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .header-top {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .patient-selector {
    width: 100%;
  }
  
  .card-row {
    grid-template-columns: 1fr;
  }
}
</style>