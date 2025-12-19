<!-- src/views/doctor/DoctorQueuePage.vue -->
<template>
  <div class="doctor-queue-page">
    <div class="dq-content">
      <header class="dq-header">
        <div class="header-content">
          <h2>今日候诊队列</h2>
          <div class="header-subtitle">查看和管理今日就诊患者队列</div>
        </div>
      </header>

      <section class="dq-main">
        <div v-if="!doctorId" class="error-box">
          <div class="error-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 8V12M12 16H12.01M22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="error-details">
            <h3>登录信息异常</h3>
            <p>当前登录信息中缺少医生ID，请重新登录医生账号</p>
            <button @click="logout" class="error-btn">返回登录页面</button>
          </div>
        </div>

        <div v-else>
          <div class="dashboard-info">
            <div class="info-card">
              <div class="info-icon total">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M10 19C14.9706 19 19 14.9706 19 10C19 5.02944 14.9706 1 10 1C5.02944 1 1 5.02944 1 10C1 14.9706 5.02944 19 10 19Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M10 6V10L13 11" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="info-content">
                <div class="info-label">今日总候诊</div>
                <div class="info-value">{{ queueList.length }} 人</div>
              </div>
            </div>
            
            <div class="info-card">
              <div class="info-icon waiting">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 10H3M10 17V3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="info-content">
                <div class="info-label">等待就诊</div>
                <div class="info-value">{{ waitingCount }} 人</div>
              </div>
            </div>
            
            <div class="info-card">
              <div class="info-icon in-progress">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M10 17C13.866 17 17 13.866 17 10C17 6.13401 13.866 3 10 3C6.13401 3 3 6.13401 3 10C3 13.866 6.13401 17 10 17Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M10 6V10L13 11" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="info-content">
                <div class="info-label">就诊中</div>
                <div class="info-value">{{ visitingCount }} 人</div>
              </div>
            </div>
          </div>

          <div class="table-section">
            <div class="section-header">
              <h3>患者队列列表</h3>
              <button class="refresh-btn" @click="loadQueue">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M13.646 2.35399C12.675 1.38299 11.393 0.772995 10 0.772995C6.548 0.772995 3.75 3.57099 3.75 7.02299C3.75 10.475 6.548 13.273 10 13.273C12.634 13.273 14.892 11.385 15.5 8.89799" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M15.25 2.27295V5.89795H11.625" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                刷新队列
              </button>
            </div>

            <div class="table-container">
              <table class="queue-table">
                <thead>
                  <tr>
                    <th>序号</th>
                    <th>患者姓名</th>
                    <th>科室</th>
                    <th>挂号时间</th>
                    <th>状态</th>
                    <th>操作</th>
                  </tr>
                </thead>

                <tbody>
                  <tr v-for="(item, idx) in queueList" :key="item.id">
                    <td class="index-cell">
                      <span class="index-number">{{ idx + 1 }}</span>
                    </td>
                    <td class="patient-cell">
                      <div class="patient-name">{{ item.patientName }}</div>
                      <div class="patient-id">ID: {{ item.id }}</div>
                    </td>
                    <td>{{ item.deptName || '—' }}</td>
                    <td class="time-cell">{{ formatTime(item.registerTime) }}</td>
                    <td>
                      <span class="status-tag" :class="statusClass(item.queueStatus)">
                        {{ formatQueueStatus(item.queueStatus) }}
                      </span>
                    </td>
                    <td>
                      <button
                        class="action-btn"
                        :class="{
                          'btn-primary': canStartOrEnter(item.queueStatus) && !isFinishedLike(item.queueStatus),
                          'btn-secondary': isFinishedLike(item.queueStatus),
                          'btn-disabled': isForbidden(item.queueStatus)
                        }"
                        :disabled="isForbidden(item.queueStatus)"
                        @click="goVisit(item)"
                      >
                        <span v-if="!isForbidden(item.queueStatus)" class="btn-icon">
                          <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M7 12.3333C10.0376 12.3333 12.5 9.87095 12.5 6.83333C12.5 3.79571 10.0376 1.33333 7 1.33333C3.96243 1.33333 1.5 3.79571 1.5 6.83333C1.5 9.87095 3.96243 12.3333 7 12.3333Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M7 4.5V7.16667L8.83333 8" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                          </svg>
                        </span>
                        {{ actionText(item.queueStatus) }}
                      </button>
                    </td>
                  </tr>

                  <tr v-if="queueList.length === 0">
                    <td colspan="6">
                      <div class="empty-state">
                        <div class="empty-icon">
                          <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M52 24C52 35.0457 43.0457 44 32 44C20.9543 44 12 35.0457 12 24C12 12.9543 20.9543 4 32 4C43.0457 4 52 12.9543 52 24Z" fill="#F0F7FF"/>
                            <path d="M32 44V60M32 60H24M32 60H40" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M40 28C40 30.2091 38.2091 32 36 32C33.7909 32 32 30.2091 32 28C32 25.7909 33.7909 24 36 24C38.2091 24 40 25.7909 40 28Z" fill="#D1E0FF"/>
                            <path d="M24 28C24 30.2091 22.2091 32 20 32C17.7909 32 16 30.2091 16 28C16 25.7909 17.7909 24 20 24C22.2091 24 24 25.7909 24 28Z" fill="#D1E0FF"/>
                          </svg>
                        </div>
                        <h3>暂无候诊患者</h3>
                        <p>当前时间段没有患者候诊，请稍后再查看</p>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getDoctorQueue, startVisit } from '../../api/doctor'

// ====== 读取当前用户信息 ======
function readCurrentUser() {
  const str = localStorage.getItem('currentUser')
  if (!str) return {}
  try {
    return JSON.parse(str) || {}
  } catch (e) {
    console.error('解析用户信息失败', e)
    return {}
  }
}

const route = useRoute()
const router = useRouter()
const currentUser = ref(readCurrentUser())

// 获取医生ID
const doctorId = computed(() => {
  const fromQuery = route.query.doctorId
  if (fromQuery) return Number(fromQuery)
  if (currentUser.value && currentUser.value.doctorId) {
    return Number(currentUser.value.doctorId)
  }
  return null
})

const queueList = ref([])

// 统计信息
const waitingCount = computed(() => {
  return queueList.value.filter(item => {
    const status = Number(item.queueStatus)
    return status === 0 || status === 1
  }).length
})

const visitingCount = computed(() => {
  return queueList.value.filter(item => {
    return Number(item.queueStatus) === 2
  }).length
})

// 退出登录
const logout = () => {
  localStorage.removeItem('currentUser')
  localStorage.removeItem('token')
  router.push('/login')
}

// ===== 辅助函数 =====
const formatQueueStatus = (val) => {
  switch (Number(val)) {
    case 0: return '候诊中'
    case 1: return '已叫号'
    case 2: return '就诊中'
    case 3: return '已完成'
    case 4: return '已过号'
    case 9: return '已取消'
    default: return String(val ?? '-')
  }
}

const statusClass = (qs) => {
  qs = Number(qs)
  return {
    'status-waiting': qs === 0,
    'status-called': qs === 1,
    'status-visiting': qs === 2,
    'status-completed': qs === 3,
    'status-missed': qs === 4,
    'status-cancelled': qs === 9
  }
}

const formatTime = (time) => {
  if (!time) return '—'
  // 如果是完整的时间戳，只显示时间部分
  if (time.includes(' ')) {
    return time.split(' ')[1].substring(0, 5)
  }
  return time
}

// 完成/过号/取消
const isFinishedLike = (qs) => {
  qs = Number(qs)
  return qs === 3 || qs === 4 || qs === 9
}

// 不允许操作的状态
const isForbidden = (qs) => {
  qs = Number(qs)
  return qs === 4 || qs === 9
}

// 是否允许进入/开始就诊
const canStartOrEnter = (qs) => {
  qs = Number(qs)
  return qs === 0 || qs === 1 || qs === 2 || qs === 3
}

const actionText = (qs) => {
  qs = Number(qs)
  if (qs === 0 || qs === 1) return '开始就诊'
  if (qs === 2) return '继续就诊'
  if (qs === 3) return '查看记录'
  if (qs === 4) return '已过号'
  if (qs === 9) return '已取消'
  return '就诊'
}

// ===== 数据加载 =====
const loadQueue = async () => {
  if (!doctorId.value) {
    console.warn('缺少医生ID', currentUser.value, route.query)
    return
  }

  try {
    const today = new Date().toISOString().slice(0, 10)
    const resp = await getDoctorQueue({ doctorId: doctorId.value, date: today })

    // 处理响应数据
    let data = null
    if (Array.isArray(resp)) {
      data = resp
    } else if (resp?.code === 0) {
      data = resp.data || []
    } else if (resp?.data?.code === 0) {
      data = resp.data.data || []
    }

    queueList.value = data || []
  } catch (error) {
    console.error('加载队列失败:', error)
    queueList.value = []
  }
}

// ===== 操作处理 =====
const goVisit = async (row) => {
  if (!row || !row.id) return

  const qs = Number(row.queueStatus ?? 0)

  // 过号/取消：不允许操作
  if (qs === 4 || qs === 9) return

  // 已完成：直接进入查看模式
  if (qs === 3) {
    router.push(`/doctor/workbench/${row.id}`)
    return
  }

  // 就诊中：直接进入
  if (qs === 2) {
    router.push(`/doctor/workbench/${row.id}`)
    return
  }

  // 候诊/已叫号：先调用开始就诊接口
  if (qs === 0 || qs === 1) {
    try {
      await startVisit(row.id)
      router.push(`/doctor/workbench/${row.id}`)
    } catch (error) {
      console.error('开始就诊失败:', error)
      alert('开始就诊失败，请稍后重试')
    }
  }
}

onMounted(() => {
  loadQueue()
})
</script>

<style scoped>
.doctor-queue-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 24px;
}

.dq-content {
  max-width: 1200px;
  margin: 0 auto;
}

/* 头部区域 */
.dq-header {
  margin-bottom: 32px;
}

.header-content {
  text-align: center;
}

.dq-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1e3a8a;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.header-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

/* 主要内容区域 */
.dq-main {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 
    0 1px 3px rgba(15, 23, 42, 0.05),
    0 10px 25px -5px rgba(15, 23, 42, 0.05);
  border: 1px solid #f1f5f9;
}

/* 错误状态 */
.error-box {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 24px;
  background: #fef2f2;
  border-radius: 12px;
  border: 1px solid #fee2e2;
}

.error-icon {
  color: #dc2626;
  flex-shrink: 0;
}

.error-details h3 {
  font-size: 18px;
  font-weight: 600;
  color: #b91c1c;
  margin: 0 0 8px 0;
}

.error-details p {
  font-size: 15px;
  color: #7f1d1d;
  margin: 0 0 16px 0;
  line-height: 1.5;
}

.error-btn {
  padding: 10px 20px;
  background: #dc2626;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.error-btn:hover {
  background: #b91c1c;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.2);
}

/* 信息卡片 */
.dashboard-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.info-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px -5px rgba(30, 58, 138, 0.1);
  border-color: #dbeafe;
}

.info-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.info-icon.total {
  background: #eff6ff;
  color: #3b82f6;
}

.info-icon.waiting {
  background: #fef3c7;
  color: #f59e0b;
}

.info-icon.in-progress {
  background: #dcfce7;
  color: #10b981;
}

.info-content {
  flex: 1;
}

.info-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 4px;
}

.info-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

/* 表格区域 */
.table-section {
  background: #f8fafc;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e2e8f0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: white;
  color: #3b82f6;
  border: 1px solid #dbeafe;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  background: #eff6ff;
  border-color: #3b82f6;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

.refresh-btn svg {
  transition: transform 0.3s ease;
}

.refresh-btn:hover svg {
  transform: rotate(90deg);
}

/* 表格容器 */
.table-container {
  overflow-x: auto;
  border-radius: 8px;
  background: white;
  border: 1px solid #e2e8f0;
}

.queue-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  color: #475569;
}

.queue-table thead {
  background: #f1f5f9;
}

.queue-table th {
  padding: 16px 20px;
  font-weight: 600;
  text-align: left;
  color: #334155;
  white-space: nowrap;
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

.queue-table tbody tr:last-child td {
  border-bottom: none;
}

/* 特殊单元格样式 */
.index-cell {
  text-align: center;
}

.index-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: #eff6ff;
  color: #1e40af;
  border-radius: 6px;
  font-weight: 600;
  font-size: 13px;
}

.patient-cell {
  min-width: 180px;
}

.patient-name {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.patient-id {
  font-size: 13px;
  color: #64748b;
}

.time-cell {
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', monospace;
  color: #475569;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
  letter-spacing: 0.3px;
}

.status-waiting {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fde68a;
}

.status-called {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fde68a;
}

.status-visiting {
  background: #dbeafe;
  color: #1e40af;
  border: 1px solid #bfdbfe;
}

.status-completed {
  background: #dcfce7;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.status-missed {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.status-cancelled {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
}

/* 操作按钮 */
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-secondary {
  background: white;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.btn-disabled {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
  border: 1px solid #e2e8f0;
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  margin-bottom: 20px;
  opacity: 0.6;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: #475569;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
  max-width: 300px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .doctor-queue-page {
    padding: 16px;
  }
  
  .dq-main {
    padding: 20px;
  }
  
  .dashboard-info {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .table-section {
    padding: 16px;
  }
  
  .queue-table th,
  .queue-table td {
    padding: 12px 16px;
  }
  
  .dq-header h2 {
    font-size: 24px;
  }
  
  .info-value {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .refresh-btn {
    align-self: stretch;
    justify-content: center;
  }
}
</style>