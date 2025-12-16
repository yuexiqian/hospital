<!-- src/views/doctor/DoctorQueuePage.vue -->
<template>
  <div class="doctor-queue-page">
    <header class="dq-header">
      <h2>医生端 · 今日候诊队列</h2>
      <div class="right">
        <span class="doctor-name">
          当前医生：{{ currentDoctorName || '—' }}
        </span>
        <span class="today">日期：{{ today }}</span>
      </div>
    </header>

    <section class="dq-main">
      <div v-if="!doctorId" class="error-box">
        当前登录信息中缺少 doctorId，请返回登录页重新登录医生账号。
        <button @click="logout">返回登录</button>
      </div>

      <div v-else>
        <div class="toolbar">
          <button @click="loadQueue">刷新队列</button>
        </div>

        <table class="queue-table">
          <thead>
            <tr>
              <th>序号</th>
              <th>挂号ID</th>
              <th>患者姓名</th>
              <th>科室</th>
              <th>挂号时间</th>
              <th>队列状态</th>
              <th style="width: 110px">操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="(item, idx) in queueList" :key="item.id">
              <td>{{ idx + 1 }}</td>
              <td>{{ item.id }}</td>
              <td>{{ item.patientName }}</td>
              <td>{{ item.deptName || '-' }}</td>
              <td>{{ item.registerTime || '-' }}</td>
              <td>
                <span class="status-tag" :class="statusClass(item.queueStatus)">
                  {{ formatQueueStatus(item.queueStatus) }}
                </span>
              </td>
              <td>
                <button
                  class="action-btn"
                  :class="{ ghost: !canStartOrEnter(item.queueStatus), gray: isFinishedLike(item.queueStatus) }"
                  :disabled="isForbidden(item.queueStatus)"
                  @click="goVisit(item)"
                >
                  {{ actionText(item.queueStatus) }}
                </button>
              </td>
            </tr>

            <tr v-if="queueList.length === 0">
              <td colspan="7" class="empty-cell">当前暂无候诊病人</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getDoctorQueue, startVisit } from '../../api/doctor'

// ====== 读取 currentUser（兜底） ======
function readCurrentUser () {
  const str = localStorage.getItem('currentUser')
  if (!str) return {}
  try {
    return JSON.parse(str) || {}
  } catch (e) {
    console.error('parse currentUser error', e)
    return {}
  }
}

const route = useRoute()
const router = useRouter()
const currentUser = ref(readCurrentUser())

// 1) 优先从路由 ?doctorId=... 取
// 2) 兜底用 currentUser 里的 doctorId
const doctorId = computed(() => {
  const fromQuery = route.query.doctorId
  if (fromQuery) return Number(fromQuery)
  if (currentUser.value && currentUser.value.doctorId) {
    return Number(currentUser.value.doctorId)
  }
  return null
})

// 显示医生名字
const currentDoctorName = computed(() => {
  return currentUser.value?.name || currentUser.value?.loginName || ''
})

const today = new Date().toISOString().slice(0, 10)
const queueList = ref([])

// 退出登录
const logout = () => {
  localStorage.removeItem('currentUser')
  localStorage.removeItem('token')
  router.push('/login')
}

// ===== 状态文案（按你后端约定）=====
const formatQueueStatus = (val) => {
  switch (Number(val)) {
    case 0: return '候诊'
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
    green: qs === 0,
    orange: qs === 1,
    blue: qs === 2,
    gray: qs === 3,
    red: qs === 4 || qs === 9
  }
}

// 完成/过号/取消
const isFinishedLike = (qs) => {
  qs = Number(qs)
  return qs === 3 || qs === 4 || qs === 9
}

// 不允许操作（直接禁用按钮）：过号/取消
// 已完成我这里不禁用，改成“查看”（进工作台只读由你工作台决定）
const isForbidden = (qs) => {
  qs = Number(qs)
  return qs === 4 || qs === 9
}

// 是否允许进入/开始就诊
const canStartOrEnter = (qs) => {
  qs = Number(qs)
  // 0/1: 允许 startVisit 后进入
  // 2: 直接进入
  // 3: 查看（允许进入但不 startVisit）
  return qs === 0 || qs === 1 || qs === 2 || qs === 3
}

const actionText = (qs) => {
  qs = Number(qs)
  if (qs === 0 || qs === 1) return '开始就诊'
  if (qs === 2) return '继续就诊'
  if (qs === 3) return '查看'
  if (qs === 4) return '已过号'
  if (qs === 9) return '已取消'
  return '就诊'
}

// 加载队列
const loadQueue = async () => {
  if (!doctorId.value) {
    console.warn('DoctorQueuePage: doctorId is missing', currentUser.value, route.query)
    return
  }

  try {
    const resp = await getDoctorQueue({ doctorId: doctorId.value, date: today })

    // 兼容两种返回：resp 是数组 / resp.data 包 {code,data}
    if (Array.isArray(resp)) {
      queueList.value = resp
      return
    }

    const raw = (resp && Object.prototype.hasOwnProperty.call(resp, 'code'))
      ? resp
      : (resp && resp.data !== undefined ? resp.data : null)

    if (!raw) {
      console.error('队列接口返回空：', resp)
      queueList.value = []
      return
    }

    if (raw.code === 0) {
      queueList.value = raw.data || []
    } else {
      console.error('loadQueue error:', raw.message)
      queueList.value = []
    }
  } catch (e) {
    console.error('loadQueue request error:', e)
    queueList.value = []
  }
}

// 点击按钮
const goVisit = async (row) => {
  if (!row || !row.id) return

  const qs = Number(row.queueStatus ?? 0)

  // 过号/取消：直接拦住
  if (qs === 4 || qs === 9) return

  // 已完成：不调用 startVisit，直接进入（工作台是否只读由你工作台控制）
  if (qs === 3) {
    router.push(`/doctor/workbench/${row.id}`)
    return
  }

  // 就诊中：直接进入（避免重复 startVisit 导致你后端抛错）
  if (qs === 2) {
    router.push(`/doctor/workbench/${row.id}`)
    return
  }

  // 候诊/已叫号：先 startVisit，再进入
  if (qs === 0 || qs === 1) {
    try {
      await startVisit(row.id)
    } catch (e) {
      console.error('startVisit error:', e)
      window.alert('开始就诊失败，请稍后重试')
      return
    }
    router.push(`/doctor/workbench/${row.id}`)
  }
}

onMounted(() => {
  loadQueue()
})
</script>

<style scoped>
.doctor-queue-page {
  padding: 16px 20px;
}

.dq-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.dq-header h2 {
  font-size: 18px;
  margin: 0;
}

.dq-header .right {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #4b5563;
}

.dq-main {
  background: #fff;
  border-radius: 12px;
  padding: 12px 16px 16px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.08);
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}

.toolbar button {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid #2563eb;
  background: #2563eb;
  color: #fff;
  cursor: pointer;
  font-size: 13px;
}

.queue-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.queue-table thead {
  background: #eff6ff;
}

.queue-table th,
.queue-table td {
  padding: 6px 8px;
  border: 1px solid #e5e7eb;
  text-align: left;
}

.empty-cell {
  text-align: center;
  color: #9ca3af;
}

.error-box {
  padding: 16px;
  background: #fee2e2;
  color: #b91c1c;
  border-radius: 8px;
  font-size: 14px;
}

.error-box button {
  margin-left: 12px;
  padding: 4px 10px;
  border-radius: 8px;
  border: 1px solid #b91c1c;
  background: #ffffff;
  cursor: pointer;
  font-size: 13px;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  color: #fff;
}
.status-tag.green { background: #22c55e; }
.status-tag.orange { background: #f97316; }
.status-tag.blue { background: #2563eb; }
.status-tag.gray { background: #6b7280; }
.status-tag.red { background: #ef4444; }

.action-btn {
  padding: 4px 10px;
  border-radius: 6px;
  border: 1px solid #2563eb;
  background: #2563eb;
  color: #fff;
  font-size: 12px;
  cursor: pointer;
}
.action-btn.ghost {
  background: #ffffff;
  color: #2563eb;
}
.action-btn.gray {
  border-color: #9ca3af;
  background: #ffffff;
  color: #6b7280;
}
.action-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}
</style>
