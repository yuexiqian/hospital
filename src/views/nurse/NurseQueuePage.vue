<template>
  <div class="nurse-layout">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <div class="logo">护士端 · 候诊队列管理</div>
      <nav class="menu">
        <button
          class="menu-item active"
        >
          候诊队列
        </button>
      </nav>
    </aside>

    <!-- 右侧主区域 -->
    <main class="main">
      <header class="main-header">
        <div class="user-info">
          <span>当前护士：{{ currentUser?.loginName || '未登录' }}</span>
          <button class="logout" @click="backToLogin">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M6 2H4C3.44772 2 3 2.44772 3 3V13C3 13.5523 3.44772 14 4 14H6" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M10 11L13 8L10 5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M13 8H7" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            退出登录
          </button>
        </div>
      </header>

      <section class="content">
        <div class="nurse-content">
          <!-- 页面标题 - 居中显示 -->
          <div class="page-header center">
            <h1>候诊队列管理</h1>
            <p class="page-subtitle">查看并管理今日候诊患者，支持叫号、加急、过号、分诊备注</p>
          </div>

          <!-- 统计卡片 -->
          <div class="stats-cards">
            <div class="stat-card">
              <div class="stat-icon total">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 10C17 13.866 13.866 17 10 17C6.13401 17 3 13.866 3 10C3 6.13401 6.13401 3 10 3C13.866 3 17 6.13401 17 10Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M10 6V10L12 11" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="stat-content">
                <div class="stat-label">总候诊人数</div>
                <div class="stat-value">{{ queueList.length }} 人</div>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon waiting">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 10H3M10 17V3" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="stat-content">
                <div class="stat-label">等待叫号</div>
                <div class="stat-value">{{ waitingCount }} 人</div>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon called">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 10C17 13.866 13.866 17 10 17C6.13401 17 3 13.866 3 10C3 6.13401 6.13401 3 10 3C13.866 3 17 6.13401 17 10Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M7 10L9 12L13 8" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="stat-content">
                <div class="stat-label">已叫号</div>
                <div class="stat-value">{{ calledCount }} 人</div>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon urgent">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M10 17C13.866 17 17 13.866 17 10C17 6.13401 13.866 3 10 3C6.13401 3 3 6.13401 3 10C3 13.866 6.13401 17 10 17Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M10 6V10L12 11" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M10 13H10.01" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="stat-content">
                <div class="stat-label">加急患者</div>
                <div class="stat-value">{{ urgentCount }} 人</div>
              </div>
            </div>
          </div>

          <!-- 主要内容区域 -->
          <div class="main-section">
            <!-- 左侧：队列表格 -->
            <div class="table-section">
              <div class="section-header">
                <h2>候诊队列</h2>
                <div class="legend">
                  <span class="legend-item">
                    <span class="dot dot-waiting"></span> 候诊
                  </span>
                  <span class="legend-item">
                    <span class="dot dot-called"></span> 已叫号
                  </span>
                  <span class="legend-item">
                    <span class="dot dot-urgent"></span> 加急
                  </span>
                  <span class="legend-item">
                    <span class="dot dot-missed"></span> 已过号
                  </span>
                </div>
              </div>

              <div class="table-container">
                <table class="queue-table">
                  <thead>
                    <tr>
                      <th>序号</th>
                      <th>患者信息</th>
                      <th>科室/医生</th>
                      <th>挂号时间</th>
                      <th>状态</th>
                      <th>优先级</th>
                      <th>备注</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-if="queueList.length === 0">
                      <td colspan="8">
                        <div class="empty-state">
                          <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M48 24C48 35.0457 39.0457 44 28 44C16.9543 44 8 35.0457 8 24C8 12.9543 16.9543 4 28 4C39.0457 4 48 12.9543 48 24Z" fill="#F0F7FF"/>
                            <path d="M28 44V60M28 60H20M28 60H36" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M36 28C36 30.2091 34.2091 32 32 32C29.7909 32 28 30.2091 28 28C28 25.7909 29.7909 24 32 24C34.2091 24 36 25.7909 36 28Z" fill="#D1E0FF"/>
                            <path d="M20 28C20 30.2091 18.2091 32 16 32C13.7909 32 12 30.2091 12 28C12 25.7909 13.7909 24 16 24C18.2091 24 20 25.7909 20 28Z" fill="#D1E0FF"/>
                          </svg>
                          <p>暂无候诊数据</p>
                        </div>
                      </td>
                    </tr>
                    <tr 
                      v-for="(row, index) in queueList" 
                      :key="row.id"
                      :class="{ 'urgent-row': row.queuePriority > 0 }"
                    >
                      <td class="index-cell">{{ index + 1 }}</td>
                      <td class="patient-cell">
                        <div class="patient-name">ID: {{ row.patientId }}</div>
                        <div class="patient-detail">
                          <span v-if="row.calledTimes > 0" class="call-count">
                            已叫 {{ row.calledTimes }} 次
                          </span>
                        </div>
                      </td>
                      <td class="dept-cell">
                        <div>{{ row.deptName || '-' }}</div>
                        <div class="doctor-name">{{ row.doctorName || '-' }}</div>
                      </td>
                      <td class="time-cell">{{ formatDateTime(row.registerTime) }}</td>
                      <td>
                        <span class="status-tag" :class="statusClass(row.queueStatus)">
                          {{ statusText(row.queueStatus) }}
                        </span>
                      </td>
                      <td>
                        <span v-if="row.queuePriority > 0" class="priority-tag">加急</span>
                        <span v-else class="priority-normal">普通</span>
                      </td>
                      <td class="note-cell">
                        <div 
                          v-if="row.triageNote" 
                          class="note-content"
                          @click="openTriageDialog(row)"
                        >
                          {{ shorten(row.triageNote, 20) }}
                        </div>
                        <button 
                          v-else 
                          class="add-note-btn"
                          @click="openTriageDialog(row)"
                        >
                          添加备注
                        </button>
                      </td>
                      <td>
                        <div class="action-group">
                          <button 
                            class="action-btn call-btn"
                            :disabled="row.queueStatus === 1 || row.queueStatus === 4"
                            @click="onCall(row)"
                          >
                            叫号
                          </button>
                          <button 
                            class="action-btn miss-btn"
                            @click="onNoShow(row)"
                          >
                            过号
                          </button>
                          <button 
                            v-if="row.queuePriority === 0"
                            class="action-btn urgent-btn"
                            @click="onSetPriority(row)"
                          >
                            加急
                          </button>
                          <button 
                            v-else
                            class="action-btn cancel-urgent-btn"
                            @click="onCancelPriority(row)"
                          >
                            取消加急
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- 右侧：筛选区域 -->
            <div class="filter-section">
              <div class="section-header">
                <h2>筛选条件</h2>
              </div>
              
              <div class="filter-form">
                <div class="filter-group">
                  <label class="filter-label">日期</label>
                  <div class="date-input-wrapper">
                    <input 
                      type="date" 
                      v-model="query.visitDate"
                      class="filter-input"
                    />
                  </div>
                </div>

                <div class="filter-group">
                  <label class="filter-label">科室</label>
                  <select v-model="query.deptId" class="filter-select">
                    <option value="">全部科室</option>
                    <option
                      v-for="dept in deptList"
                      :key="dept.id"
                      :value="dept.id"
                    >
                      {{ dept.name }}
                    </option>
                  </select>
                </div>

                <div class="filter-group">
                  <label class="filter-label">医生</label>
                  <select v-model="query.doctorId" class="filter-select">
                    <option value="">全部医生</option>
                    <option
                      v-for="doc in doctorList"
                      :key="doc.id"
                      :value="doc.id"
                    >
                      {{ doc.name }}
                    </option>
                  </select>
                </div>

                <div class="filter-group">
                  <label class="filter-label">队列状态</label>
                  <select v-model="query.queueStatus" class="filter-select">
                    <option value="">全部状态</option>
                    <option :value="0">候诊</option>
                    <option :value="1">已叫号</option>
                    <option :value="4">已过号</option>
                  </select>
                </div>

                <div class="filter-actions">
                  <button class="filter-btn primary" @click="loadQueue">
                    查询队列
                  </button>
                  <button class="filter-btn secondary" @click="resetQuery">
                    重置条件
                  </button>
                </div>

                <div class="filter-hint">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M8 14C11.3137 14 14 11.3137 14 8C14 4.68629 11.3137 2 8 2C4.68629 2 2 4.68629 2 8C2 11.3137 4.68629 14 8 14Z" stroke="#64748B" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M8 5V8" stroke="#64748B" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M8 11H8.01" stroke="#64748B" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>默认显示当日全部候诊患者</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 使用说明 -->
          <div class="help-section">
            <div class="help-header">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M10 17C13.866 17 17 13.866 17 10C17 6.13401 13.866 3 10 3C6.13401 3 3 6.13401 3 10C3 13.866 6.13401 17 10 17Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M10 6V10L12 11" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <h3>操作说明</h3>
            </div>
            <div class="help-content">
              <p>• <strong>叫号：</strong>通知患者就诊，系统会记录叫号时间与次数</p>
              <p>• <strong>过号：</strong>患者多次叫号未到诊，可标记为"已过号"状态</p>
              <p>• <strong>加急：</strong>为特殊患者设置优先就诊，自动排到队列前面</p>
              <p>• <strong>备注：</strong>添加分诊备注，如"高热优先"、"老年患者"等关键信息</p>
            </div>
          </div>
        </div>

        <!-- 分诊备注弹窗 -->
        <div v-if="triageDialog.visible" class="modal-overlay">
          <div class="modal">
            <div class="modal-header">
              <h3>分诊备注</h3>
              <button class="modal-close" @click="closeTriageDialog">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M15 5L5 15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M5 5L15 15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
            
            <div class="modal-body">
              <div class="patient-info">
                <span>挂号ID：{{ triageDialog.row?.id }}</span>
                <span>患者ID：{{ triageDialog.row?.patientId }}</span>
              </div>
              
              <div class="form-group">
                <label class="form-label">分诊备注</label>
                <textarea
                  v-model="triageDialog.form.triageNote"
                  rows="5"
                  class="form-textarea"
                  placeholder="请输入分诊备注，例如：高热39.5℃，疑似肺炎；老年患者，需要优先就诊；外伤出血，需紧急处理..."
                ></textarea>
              </div>
            </div>
            
            <div class="modal-footer">
              <button class="modal-btn secondary" @click="closeTriageDialog">
                取消
              </button>
              <button class="modal-btn primary" @click="submitTriage">
                保存备注
              </button>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
// 脚本部分保持不变
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'

import { getDepts, getDoctors } from '../../api/triage'
import {
  fetchQueue,
  callPatient,
  markNoShow,
  setPriority,
  cancelPriority,
  saveTriage
} from '../../api/nurseQueue'

const router = useRouter()

const currentUser = ref(null)

const query = reactive({
  visitDate: '',
  deptId: '',
  doctorId: '',
  queueStatus: ''
})

const deptList = ref([])
const doctorList = ref([])

const queueList = ref([])

const triageDialog = reactive({
  visible: false,
  row: null,
  form: {
    triageNote: ''
  }
})

// 统计数据
const waitingCount = computed(() => {
  return queueList.value.filter(item => Number(item.queueStatus) === 0).length
})

const calledCount = computed(() => {
  return queueList.value.filter(item => Number(item.queueStatus) === 1).length
})

const urgentCount = computed(() => {
  return queueList.value.filter(item => Number(item.queuePriority) > 0).length
})

const today = () => {
  const d = new Date()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}

const formatDateTime = (str) => {
  if (!str) return '-'
  const date = new Date(str)
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit',
    hour12: false 
  })
}

const statusText = (qs) => {
  switch (Number(qs)) {
    case 0:
      return '候诊'
    case 1:
      return '已叫号'
    case 2:
      return '就诊中'
    case 3:
      return '已完成'
    case 4:
      return '已过号'
    case 9:
      return '已取消'
    default:
      return '未知'
  }
}

const statusClass = (qs) => {
  qs = Number(qs)
  return {
    'status-waiting': qs === 0,
    'status-called': qs === 1,
    'status-in-progress': qs === 2 || qs === 3,
    'status-missed': qs === 4,
    'status-cancelled': qs === 9
  }
}

const shorten = (text, len) => {
  if (!text) return ''
  return text.length > len ? text.slice(0, len) + '…' : text
}

const backToLogin = () => {
  localStorage.removeItem('currentUser')
  router.push('/login')
}

const loadDepts = async () => {
  try {
    const resp = await getDepts()
    const { code, data, message } = resp.data
    if (code === 0) {
      deptList.value = data || []
    } else {
      alert(message || '加载科室失败')
    }
  } catch (e) {
    console.error(e)
    alert('加载科室失败')
  }
}

const loadDoctors = async () => {
  if (!query.deptId) {
    doctorList.value = []
    return
  }
  try {
    const resp = await getDoctors(query.deptId)
    const { code, data, message } = resp.data
    if (code === 0) {
      doctorList.value = data || []
    } else {
      alert(message || '加载医生失败')
    }
  } catch (e) {
    console.error(e)
    alert('加载医生失败')
  }
}

watch(
  () => query.deptId,
  () => {
    query.doctorId = ''
    loadDoctors()
    loadQueue()
  }
)

const loadQueue = async () => {
  try {
    const params = {}
    if (query.visitDate) params.visitDate = query.visitDate
    if (query.deptId) params.deptId = query.deptId
    if (query.doctorId) params.doctorId = query.doctorId
    if (query.queueStatus !== '') params.queueStatus = query.queueStatus

    const resp = await fetchQueue(params)
    const { code, data, message } = resp.data
    if (code === 0) {
      queueList.value = data || []
    } else {
      alert(message || '加载候诊队列失败')
    }
  } catch (e) {
    console.error(e)
    alert('加载候诊队列失败')
  }
}

const resetQuery = () => {
  query.visitDate = today()
  query.deptId = ''
  query.doctorId = ''
  query.queueStatus = ''
  loadQueue()
}

const onCall = async (row) => {
  if (!currentUser.value) {
    alert('请先登录')
    return
  }
  if (!confirm(`确认叫号：挂号ID ${row.id} ？`)) return
  try {
    await callPatient(row.id, currentUser.value.userId)
    await loadQueue()
  } catch (e) {
    console.error(e)
    alert('叫号失败')
  }
}

const onNoShow = async (row) => {
  if (!confirm(`确认标记过号：挂号ID ${row.id} ？`)) return
  try {
    await markNoShow(row.id)
    await loadQueue()
  } catch (e) {
    console.error(e)
    alert('标记过号失败')
  }
}

const onSetPriority = async (row) => {
  if (!confirm(`确认将挂号ID ${row.id} 设置为加急？`)) return
  try {
    await setPriority(row.id, 100)
    await loadQueue()
  } catch (e) {
    console.error(e)
    alert('设置加急失败')
  }
}

const onCancelPriority = async (row) => {
  if (!confirm(`确认取消挂号ID ${row.id} 的加急？`)) return
  try {
    await cancelPriority(row.id)
    await loadQueue()
  } catch (e) {
    console.error(e)
    alert('取消加急失败')
  }
}

const openTriageDialog = (row) => {
  triageDialog.row = row
  triageDialog.form.triageNote = row.triageNote || ''
  triageDialog.visible = true
}

const closeTriageDialog = () => {
  triageDialog.visible = false
  triageDialog.row = null
  triageDialog.form.triageNote = ''
}

const submitTriage = async () => {
  if (!currentUser.value) {
    alert('请先登录')
    return
  }
  if (!triageDialog.row) return
  try {
    await saveTriage(
      triageDialog.row.id,
      currentUser.value.userId,
      triageDialog.form.triageNote
    )
    triageDialog.visible = false
    await loadQueue()
  } catch (e) {
    console.error(e)
    alert('保存分诊备注失败')
  }
}

onMounted(() => {
  const saved = localStorage.getItem('currentUser')
  if (saved) {
    try {
      currentUser.value = JSON.parse(saved)
    } catch (e) {
      console.error(e)
    }
  }

  query.visitDate = today()

  loadDepts()
  loadQueue()
})
</script>

<style scoped>
.nurse-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

/* 左侧导航 */
.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%);
  color: #ffffff;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px 24px;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.menu {
  padding: 16px 12px;
}

.menu-item {
  width: 100%;
  border: none;
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 999px;
  text-align: left;
  background: #ffffff;
  color: #1e40af;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

/* 右侧主区域 */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.main-header {
  padding: 16px 24px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #ffffff;
}

.logout {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s ease;
}

.logout:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
}

/* 主要内容区域 */
.content {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  flex: 1;
}

.nurse-content {
  max-width: 1400px;
  margin: 0 auto;
}

/* 页面头部 - 居中显示 */
.page-header.center {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1e3a8a;
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.page-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.5;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px -5px rgba(30, 58, 138, 0.1);
  border-color: #dbeafe;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon.total {
  background: #eff6ff;
  color: #3b82f6;
}

.stat-icon.waiting {
  background: #fef3c7;
  color: #f59e0b;
}

.stat-icon.called {
  background: #dcfce7;
  color: #10b981;
}

.stat-icon.urgent {
  background: #fee2e2;
  color: #ef4444;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

/* 主要内容区域 */
.main-section {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
  margin-bottom: 24px;
}

@media (max-width: 1024px) {
  .main-section {
    grid-template-columns: 1fr;
  }
}

/* 表格区域 */
.table-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 
    0 1px 3px rgba(15, 23, 42, 0.05),
    0 10px 25px -5px rgba(15, 23, 42, 0.05);
  border: 1px solid #f1f5f9;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f1f5f9;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.legend {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #64748b;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.dot-waiting {
  background: #10b981;
}

.dot-called {
  background: #f59e0b;
}

.dot-urgent {
  background: #ef4444;
}

.dot-missed {
  background: #94a3b8;
}

/* 表格样式 */
.table-container {
  overflow-x: auto;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: white;
}

.queue-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  color: #475569;
}

.queue-table th {
  padding: 16px 20px;
  font-weight: 600;
  text-align: left;
  color: #334155;
  white-space: nowrap;
  background: #f8fafc;
  border-bottom: 2px solid #e2e8f0;
}

.queue-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.queue-table tbody tr {
  transition: background-color 0.2s ease;
}

.queue-table tbody tr:hover {
  background-color: #f8fafc;
}

.queue-table tbody tr.urgent-row {
  background-color: #fef2f2;
}

.queue-table tbody tr:last-child td {
  border-bottom: none;
}

/* 特殊单元格样式 */
.index-cell {
  text-align: center;
  font-weight: 600;
  color: #475569;
}

.patient-cell {
  min-width: 120px;
}

.patient-name {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.patient-detail {
  font-size: 13px;
  color: #64748b;
}

.call-count {
  display: inline-block;
  padding: 2px 8px;
  background: #fef3c7;
  color: #92400e;
  border-radius: 4px;
  font-size: 12px;
}

.dept-cell {
  min-width: 150px;
}

.doctor-name {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}

.time-cell {
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', monospace;
  color: #475569;
  white-space: nowrap;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
}

.status-waiting {
  background: #f0f9ff;
  color: #0369a1;
  border: 1px solid #bae6fd;
}

.status-called {
  background: #fffbeb;
  color: #92400e;
  border: 1px solid #fde68a;
}

.status-in-progress {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.status-missed {
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.status-cancelled {
  background: #f8fafc;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

/* 优先级标签 */
.priority-tag {
  display: inline-block;
  padding: 4px 12px;
  background: #fee2e2;
  color: #dc2626;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.priority-normal {
  display: inline-block;
  padding: 4px 12px;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

/* 备注单元格 */
.note-cell {
  max-width: 200px;
}

.note-content {
  padding: 8px 12px;
  background: #eff6ff;
  border-radius: 8px;
  color: #1e40af;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.note-content:hover {
  background: #dbeafe;
}

.add-note-btn {
  width: 100%;
  padding: 8px 12px;
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
  border-radius: 8px;
  color: #64748b;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-note-btn:hover {
  background: #f1f5f9;
  border-color: #94a3b8;
}

/* 操作按钮组 */
.action-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 100px;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.action-btn.call-btn {
  background: #3b82f6;
  color: white;
}

.action-btn.call-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.action-btn.call-btn:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
  transform: none;
}

.action-btn.miss-btn {
  background: #f59e0b;
  color: white;
}

.action-btn.miss-btn:hover {
  background: #d97706;
  transform: translateY(-1px);
}

.action-btn.urgent-btn {
  background: #ef4444;
  color: white;
}

.action-btn.urgent-btn:hover {
  background: #dc2626;
  transform: translateY(-1px);
}

.action-btn.cancel-urgent-btn {
  background: #f8fafc;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.action-btn.cancel-urgent-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

/* 筛选区域 */
.filter-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 
    0 1px 3px rgba(15, 23, 42, 0.05),
    0 10px 25px -5px rgba(15, 23, 42, 0.05);
  border: 1px solid #f1f5f9;
}

.filter-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

.filter-input,
.filter-select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #1e293b;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.filter-input:focus,
.filter-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 8px;
}

.filter-btn {
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
}

.filter-btn.primary {
  background: #3b82f6;
  color: white;
}

.filter-btn.primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.filter-btn.secondary {
  background: white;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.filter-btn.secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.filter-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  color: #64748b;
  font-size: 13px;
  margin-top: 8px;
}

/* 帮助区域 */
.help-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 
    0 1px 3px rgba(15, 23, 42, 0.05),
    0 10px 25px -5px rgba(15, 23, 42, 0.05);
  border: 1px solid #f1f5f9;
}

.help-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.help-header svg {
  color: #3b82f6;
}

.help-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.help-content {
  color: #475569;
  font-size: 14px;
  line-height: 1.6;
}

.help-content p {
  margin: 8px 0;
}

.help-content strong {
  color: #1e293b;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #94a3b8;
}

.empty-state svg {
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

/* 弹窗样式 */
.modal-overlay {
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
  padding: 20px;
}

.modal {
  width: 100%;
  max-width: 500px;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%);
  color: white;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.modal-close {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.1);
}

.modal-body {
  padding: 24px;
}

.patient-info {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #64748b;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #475569;
  font-weight: 500;
  margin-bottom: 8px;
}

.form-textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #1e293b;
  resize: vertical;
  transition: all 0.2s ease;
  box-sizing: border-box;
  min-height: 120px;
  font-family: inherit;
}

.form-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.modal-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.modal-btn.secondary {
  background: white;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.modal-btn.secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.modal-btn.primary {
  background: #3b82f6;
  color: white;
}

.modal-btn.primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nurse-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
  }
  
  .menu {
    display: flex;
    gap: 8px;
    padding: 12px;
  }
  
  .menu-item {
    flex: 1;
    text-align: center;
    margin-bottom: 0;
  }
  
  .content {
    padding: 16px;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .queue-table th,
  .queue-table td {
    padding: 12px;
  }
  
  .action-group {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .action-btn {
    flex: 1;
    min-width: 70px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
}
</style>