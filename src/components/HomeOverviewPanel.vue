<template>
  <div class="home-overview">
    <!-- 中央标题区域 -->
    <div class="header-section">
      <h2 class="main-title">今日就诊概览</h2>
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

      <!-- 已叫号提醒弹窗 -->
      <div v-if="showCalledNotice" class="notice-overlay">
        <div class="notice-modal">
          <div class="notice-header">
            <span class="notice-icon">🔔</span>
            <h3 class="notice-title">已叫号提醒</h3>
          </div>
          <div class="notice-body">
            <p class="notice-text">
              尊敬的 <strong>{{ currentPatient?.name }}</strong> 患者，您已叫号，请尽快前往就诊！
            </p>
            <div class="notice-info">
              <div class="notice-info-row">
                <span class="notice-label">科室：</span>
                <span class="notice-value">{{ queue.deptName }}</span>
              </div>
              <div class="notice-info-row">
                <span class="notice-label">医生：</span>
                <span class="notice-value">{{ queue.doctorName }}</span>
              </div>
              <div class="notice-info-row">
                <span class="notice-label">当前状态：</span>
                <span class="notice-value status">已叫号</span>
              </div>
            </div>
            <p class="notice-tip">请携带相关证件，尽快前往诊室就诊，以免过号。</p>
          </div>
          <div class="notice-footer">
            <button class="notice-btn confirm" @click="closeNotice">
              我知道了
            </button>
            <button class="notice-btn remind" @click="setReminder">
              5分钟后提醒我
            </button>
          </div>
        </div>
      </div>

      <!-- 就诊人选择区域：右对齐 + 修复下拉菜单 -->
      <div class="patient-selection-area">
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
            <div class="card-badge" :class="getQueueStatusBadgeClass(queue.queueStatus)">
              {{ getQueueStatusText(queue.queueStatus) }}
            </div>
          </div>

          <div v-if="queue.hasWaiting" class="card-body">
            <!-- 新增：队列状态显示 -->
            <div class="info-row">
              <span class="info-label">队列状态：</span>
              <span class="info-value status" :class="getQueueStatusClass(queue.queueStatus)">
                {{ getQueueStatusDescription(queue.queueStatus) }}
                <span v-if="queue.queueStatus === 1" class="call-notice" @click="showNotice">
                  🔔
                </span>
              </span>
            </div>
            
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
import { onMounted, ref, watch, onUnmounted, computed } from 'vue'
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
const showCalledNotice = ref(false)
const reminderTimer = ref(null)

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
const queue = ref({ 
  hasWaiting: false, 
  aheadCount: 0, 
  estimateWaitMin: 0,
  queueStatus: null,      // ✅ 新增队列状态字段
  queueStatusDesc: ''     // ✅ 新增队列状态描述
})

// ✅ 新增：判断是否显示已叫号弹窗
const shouldShowNotice = computed(() => {
  return queue.value.queueStatus === 1 // 状态为1表示已叫号
})

// ✅ 新增：显示弹窗
function showNotice() {
  if (shouldShowNotice.value) {
    showCalledNotice.value = true
  }
}

// ✅ 新增：关闭弹窗
function closeNotice() {
  showCalledNotice.value = false
}

// ✅ 新增：设置提醒
function setReminder() {
  closeNotice()
  
  // 5分钟后重新提醒
  if (reminderTimer.value) {
    clearTimeout(reminderTimer.value)
  }
  
  reminderTimer.value = setTimeout(() => {
    if (queue.value.queueStatus === 1) {
      showCalledNotice.value = true
    }
  }, 5 * 60 * 1000) // 5分钟
  
  // 显示提醒设置成功的提示
  alert('已设置5分钟后提醒，请及时前往就诊！')
}

// ✅ 新增：在组件卸载时清理定时器
onUnmounted(() => {
  if (reminderTimer.value) {
    clearTimeout(reminderTimer.value)
  }
})

// ✅ 新增：在状态变为已叫号时自动显示弹窗
function checkAndShowNotice() {
  // 如果状态是已叫号，且之前没有显示过弹窗，则显示
  if (shouldShowNotice.value && !showCalledNotice.value) {
    // 延迟一下显示，让用户有更好的体验
    setTimeout(() => {
      showCalledNotice.value = true
    }, 500)
  }
}

// ✅ 修改：获取队列状态描述
function getQueueStatusDescription(queueStatus) {
  if (queueStatus === null || queueStatus === undefined) {
    return queue.queueStatusDesc || '无状态'
  }
  
  switch (queueStatus) {
    case 0: return "候诊中"
    case 1: return "已叫号"
    case 2: return "就诊中"
    case 3: return "已完成"
    case 4: return "已过号"
    case 9: return "已取消"
    default: return "未知状态"
  }
}

// ✅ 新增：获取队列状态对应的CSS类
function getQueueStatusClass(queueStatus) {
  if (queueStatus === null || queueStatus === undefined) {
    return 'default'
  }
  
  const statusMap = {
    0: 'waiting',       // 候诊中
    1: 'called',        // 已叫号
    2: 'consulting',    // 就诊中
    3: 'completed',     // 已完成
    4: 'missed',        // 已过号
    9: 'cancelled'      // 已取消
  }
  return statusMap[queueStatus] || 'default'
}

// ✅ 新增：获取队列状态对应的徽章类
function getQueueStatusBadgeClass(queueStatus) {
  if (queueStatus === null || queueStatus === undefined) {
    return 'neutral'
  }
  
  const badgeMap = {
    0: 'warning',     // 候诊中 - 黄色警告
    1: 'processing',  // 已叫号 - 蓝色处理中
    2: 'primary',     // 就诊中 - 主要蓝色
    3: 'success',     // 已完成 - 绿色成功
    4: 'danger',      // 已过号 - 红色危险
    9: 'neutral'      // 已取消 - 中性灰色
  }
  return badgeMap[queueStatus] || 'neutral'
}

// ✅ 新增：获取队列状态对应的文本
function getQueueStatusText(queueStatus) {
  if (queueStatus === null || queueStatus === undefined) {
    return '无需候诊'
  }
  
  switch (queueStatus) {
    case 0: return "候诊中"
    case 1: return "已叫号"
    case 2: return "就诊中"
    case 3: return "已完成"
    case 4: return "已过号"
    case 9: return "已取消"
    default: return "未知状态"
  }
}

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
      estimateWaitMin: 0,
      queueStatus: null,          // ✅ 新增
      queueStatusDesc: ''         // ✅ 新增
    }
    
    // ✅ 新增：检查是否需要显示已叫号弹窗
    checkAndShowNotice()
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
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  if (reminderTimer.value) {
    clearTimeout(reminderTimer.value)
  }
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
  background: transparent;
}

/* ========== 中央标题区域 ========== */
.header-section {
  text-align: center;
  margin: 0 auto 1.5rem auto;
  padding: 1rem;
  width: 100%;
  background: rgba(240, 248, 255, 0.7);
  border-radius: 12px;
  border: 1px solid rgba(77, 171, 247, 0.2);
  backdrop-filter: blur(5px);
  box-shadow: 0 4px 16px rgba(30, 111, 217, 0.08);
}

.main-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e6fd9;
  margin: 0 0 0.5rem 0;
  text-align: center;
  background: linear-gradient(135deg, #1e6fd9 0%, #0d4ba0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.tip {
  margin: 0;
  font-size: 0.875rem;
  color: #64748b;
  line-height: 1.5;
  text-align: center;
}

.tip code {
  background: rgba(30, 111, 217, 0.1);
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
  font-family: 'SF Mono', Monaco, monospace;
  font-size: 0.85rem;
  color: #1e6fd9;
}

/* ========== 已叫号提醒弹窗 ========== */
.notice-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.notice-modal {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.3s ease;
  overflow: hidden;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.notice-header {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  padding: 1.5rem;
  text-align: center;
  color: white;
}

.notice-icon {
  font-size: 2.5rem;
  display: block;
  margin-bottom: 0.5rem;
  animation: ring 2s infinite;
}

@keyframes ring {
  0% { transform: rotate(0deg); }
  5% { transform: rotate(15deg); }
  10% { transform: rotate(-15deg); }
  15% { transform: rotate(15deg); }
  20% { transform: rotate(-15deg); }
  25% { transform: rotate(0deg); }
  100% { transform: rotate(0deg); }
}

.notice-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
}

.notice-body {
  padding: 2rem;
}

.notice-text {
  font-size: 1.125rem;
  color: #1e293b;
  text-align: center;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.notice-text strong {
  color: #ef4444;
}

.notice-info {
  background: rgba(59, 130, 246, 0.1);
  border-radius: 12px;
  padding: 1.25rem;
  margin-bottom: 1.5rem;
}

.notice-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid rgba(59, 130, 246, 0.2);
}

.notice-info-row:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.notice-label {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
}

.notice-value {
  font-size: 0.875rem;
  color: #1e293b;
  font-weight: 600;
}

.notice-value.status {
  background: rgba(59, 130, 246, 0.2);
  color: #1d4ed8;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
}

.notice-tip {
  font-size: 0.875rem;
  color: #64748b;
  text-align: center;
  line-height: 1.6;
  margin: 0;
}

.notice-footer {
  display: flex;
  gap: 1rem;
  padding: 1.5rem;
  border-top: 1px solid #e2e8f0;
}

.notice-btn {
  flex: 1;
  padding: 0.875rem 1.5rem;
  border: none;
  border-radius: 10px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.notice-btn.confirm {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.notice-btn.confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.notice-btn.remind {
  background: rgba(226, 232, 240, 0.8);
  color: #64748b;
}

.notice-btn.remind:hover {
  background: rgba(203, 213, 225, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* ========== 叫号提醒图标 ========== */
.call-notice {
  margin-left: 0.5rem;
  cursor: pointer;
  font-size: 1rem;
  animation: pulse 1.5s infinite;
  display: inline-block;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

/* ... 其余原有样式保持不变 ... */
.patient-selection-area {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1rem;
  padding-right: 1rem;
}

.patient-selector {
  position: relative;
  min-width: 280px;
}

.current-patient {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(77, 171, 247, 0.3);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.875rem;
  color: #334155;
  width: 100%;
}

.dropdown-icon {
  font-size: 0.75rem;
  color: #94a3b8;
  min-width: 12px;
  text-align: center;
}

.current-patient:hover {
  border-color: #1e6fd9;
  box-shadow: 0 2px 8px rgba(30, 111, 217, 0.1);
  transform: translateY(-1px);
}

.patient-label {
  color: #64748b;
  font-weight: 500;
}

.patient-name {
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}

.patient-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: auto;
  margin-top: 0.25rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(77, 171, 247, 0.2);
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  z-index: 100;
  overflow: hidden;
  min-width: 100%;
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
  border-bottom: 1px solid rgba(77, 171, 247, 0.1);
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background: rgba(30, 111, 217, 0.05);
}

.dropdown-item.active {
  background: rgba(30, 111, 217, 0.08);
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
  color: #1e6fd9;
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
  background: rgba(30, 111, 217, 0.1);
  color: #1e6fd9;
  border-radius: 12px;
  margin-left: 0.5rem;
  font-weight: 500;
}

/* ========== 加载和错误状态 ========== */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: rgba(240, 248, 255, 0.7);
  border-radius: 12px;
  gap: 0.75rem;
  border: 1px solid rgba(77, 171, 247, 0.2);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(77, 171, 247, 0.2);
  border-top: 3px solid #1e6fd9;
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
  background: rgba(254, 242, 242, 0.8);
  border-radius: 8px;
  border-left: 4px solid #ef4444;
  text-align: center;
}

/* ========== 卡片布局 ========== */
.card-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
  width: 100%;
}

/* ========== 卡片样式 ========== */
.card {
  background: rgba(240, 248, 255, 0.7);
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 16px rgba(30, 111, 217, 0.08);
  border: 1px solid rgba(77, 171, 247, 0.2);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  box-sizing: border-box;
  backdrop-filter: blur(5px);
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(30, 111, 217, 0.15);
  border-color: rgba(77, 171, 247, 0.4);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid rgba(77, 171, 247, 0.1);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  font-size: 1.125rem;
  color: #1e6fd9;
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
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-badge.success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.card-badge.warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.card-badge.neutral {
  background: rgba(226, 232, 240, 0.8);
  color: #64748b;
}

.card-badge.primary {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
}

.card-badge.processing {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: white;
}

.card-badge.danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
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
  border-bottom: 1px dashed rgba(77, 171, 247, 0.1);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 0.875rem;
  color: #475569;
  font-weight: 500;
}

.info-value {
  font-size: 0.875rem;
  color: #334155;
  font-weight: 600;
}

.info-value.highlight {
  color: #1e6fd9;
}

.info-value.large {
  font-size: 1rem;
}

.info-value.badge {
  background: rgba(30, 111, 217, 0.1);
  color: #1e6fd9;
  padding: 0.25rem 0.625rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.info-value.status {
  padding: 0.25rem 0.625rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.info-value.status.pending {
  background: rgba(254, 243, 199, 0.6);
  color: #d97706;
}

.info-value.status.processing {
  background: rgba(219, 234, 254, 0.6);
  color: #1d4ed8;
}

.info-value.status.completed {
  background: rgba(209, 250, 229, 0.6);
  color: #059669;
}

.info-value.status.cancelled {
  background: rgba(241, 245, 249, 0.6);
  color: #64748b;
}

/* 队列状态样式 */
.info-value.status.waiting {
  background: rgba(254, 243, 199, 0.6);
  color: #d97706;
}

.info-value.status.called {
  background: rgba(219, 234, 254, 0.6);
  color: #1d4ed8;
}

.info-value.status.consulting {
  background: rgba(196, 181, 253, 0.6);
  color: #7c3aed;
}

.info-value.status.completed {
  background: rgba(209, 250, 229, 0.6);
  color: #059669;
}

.info-value.status.missed {
  background: rgba(254, 226, 226, 0.6);
  color: #dc2626;
}

.info-value.status.cancelled {
  background: rgba(241, 245, 249, 0.6);
  color: #64748b;
}

.info-value.status.default {
  background: rgba(226, 232, 240, 0.6);
  color: #475569;
}

.info-row.time {
  margin-top: auto;
  padding-top: 0.75rem;
  border-top: 1px solid rgba(77, 171, 247, 0.1);
  font-size: 0.8rem;
}

/* ========== 进度条样式 ========== */
.progress-container {
  margin-top: 1.25rem;
  padding: 0.75rem;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  width: 100%;
  box-sizing: border-box;
  border: 1px solid rgba(77, 171, 247, 0.1);
}

.progress-label {
  font-size: 0.8rem;
  color: #64748b;
  margin-bottom: 0.375rem;
}

.progress-bar {
  height: 6px;
  background: rgba(226, 232, 240, 0.6);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.375rem;
  width: 100%;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1e6fd9 0%, #0d4ba0 100%);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.progress-text {
  font-size: 0.7rem;
  color: #94a3b8;
  text-align: right;
}

/* ========== 空状态 ========== */
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

/* ========== 卡片特定样式 ========== */
.today-card {
  border-left: 6px solid rgba(30, 111, 217, 0.6);
}

.queue-card {
  border-left: 6px solid rgba(16, 185, 129, 0.6);
}

/* ========== 响应式调整 ========== */
@media (max-width: 768px) {
  .header-section {
    padding: 0.75rem;
  }
  
  .main-title {
    font-size: 1.25rem;
  }
  
  .tip {
    font-size: 0.75rem;
  }
  
  .patient-selector {
    min-width: 100%;
  }
  
  .card-row {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .card {
    padding: 1rem;
  }
  
  .card-title {
    font-size: 1rem;
  }
  
  .notice-footer {
    flex-direction: column;
  }
  
  .notice-btn {
    width: 100%;
  }
}
</style>