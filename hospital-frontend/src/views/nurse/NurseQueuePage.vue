<!-- src/views/nurse/NurseQueuePage.vue -->
<template>
  <div class="nurse-page">
    <!-- 顶部栏 -->
    <header class="page-header">
      <div class="left">
        <h2>护士端 · 候诊队列管理</h2>
        <p class="sub">查看并管理今日候诊患者，支持叫号、加急、过号、分诊备注。</p>
      </div>
      <div class="right" v-if="currentUser">
        <span class="user-tag">当前护士：{{ currentUser.loginName }}</span>
        <button class="link-btn" @click="backToLogin">退出登录</button>
      </div>
    </header>

    <!-- 筛选区域 -->
    <section class="filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label>日期</label>
          <input type="date" v-model="query.visitDate" />
        </div>

        <div class="filter-item">
          <label>科室</label>
          <select v-model="query.deptId">
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

        <div class="filter-item">
          <label>医生</label>
          <select v-model="query.doctorId">
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

        <div class="filter-item">
          <label>队列状态</label>
          <select v-model="query.queueStatus">
            <option value="">全部</option>
            <option :value="0">候诊</option>
            <option :value="1">已叫号</option>
            <option :value="4">已过号</option>
          </select>
        </div>
      </div>

      <div class="filter-actions">
        <button class="primary-btn" @click="loadQueue">查询</button>
        <button class="ghost-btn" @click="resetQuery">重置</button>
        <span class="hint">默认只看当日全部候诊患者。</span>
      </div>
    </section>

    <!-- 队列表格 -->
    <section class="table-card">
      <div class="table-header">
        <h3>候诊队列（{{ queueList.length }} 人）</h3>
        <span class="legend">
          <span class="dot dot-green"></span> 候诊
          <span class="dot dot-orange"></span> 已叫号
          <span class="dot dot-red"></span> 已过号
        </span>
      </div>

      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>就诊人</th>
              <th>科室</th>
              <th>医生</th>
              <th>挂号时间</th>
              <th>队列状态</th>
              <th>优先级</th>
              <th>叫号次数</th>
              <th>分诊备注</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="queueList.length === 0">
              <td colspan="10" class="empty-tip">暂无候诊数据</td>
            </tr>
            <tr v-for="(row, index) in queueList" :key="row.id">
              <td>{{ index + 1 }}</td>
              <td>
                <div>{{ row.patientName || '-' }}</div>
                <div class="sub-small">ID：{{ row.patientId }}</div>
              </td>
              <td>{{ row.deptName || '-' }}</td>
              <td>{{ row.doctorName || '-' }}</td>
              <td>{{ formatDateTime(row.registerTime) }}</td>
              <td>
                <span class="status-tag" :class="statusClass(row.queueStatus)">
                  {{ statusText(row.queueStatus) }}
                </span>
              </td>
              <td>{{ row.queuePriority }}</td>
              <td>{{ row.calledTimes }}</td>
              <td class="note-cell">
                <span v-if="row.triageNote" class="note-text">
                  {{ shorten(row.triageNote, 12) }}
                </span>
                <span v-else class="note-empty">-</span>
              </td>
              <td>
                <div class="action-group">
                  <button class="mini-btn primary" @click="onCall(row)">叫号</button>
                  <button class="mini-btn warn" @click="onNoShow(row)">过号</button>
                  <button
                    v-if="row.queuePriority === 0"
                    class="mini-btn danger"
                    @click="onSetPriority(row)"
                  >
                    加急
                  </button>
                  <button
                    v-else
                    class="mini-btn ghost"
                    @click="onCancelPriority(row)"
                  >
                    取消加急
                  </button>
                  <button class="mini-btn" @click="openTriageDialog(row)">备注</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- 分诊备注弹窗 -->
    <div v-if="triageDialog.visible" class="dialog-mask">
      <div class="dialog">
        <h3>分诊备注</h3>
        <p class="dialog-sub">
          挂号ID：{{ triageDialog.row?.id }} ，
          患者：{{ triageDialog.row?.patientName || '-' }}
          （ID：{{ triageDialog.row?.patientId }}）
        </p>
        <textarea
          v-model="triageDialog.form.triageNote"
          rows="4"
          placeholder="例如：高热，疑似肺炎；老年患者优先等。"
        ></textarea>
        <div class="dialog-actions">
          <button class="ghost-btn" @click="closeTriageDialog">取消</button>
          <button class="primary-btn" @click="submitTriage">保存</button>
        </div>
      </div>
    </div>

    <!-- 底部提示 -->
    <footer class="page-footer">
      <p>说明：叫号会记录叫号时间与次数；多次叫号未到，可标记“过号”。加急患者会自动排在队列前面。</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

// 相对路径：从 views/nurse 回到 api
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

// 当前登录护士
const currentUser = ref(null)

// 筛选条件
const query = reactive({
  visitDate: '',
  deptId: '',
  doctorId: '',
  queueStatus: ''
})

// 下拉数据
const deptList = ref([])
const doctorList = ref([])

// 队列列表
const queueList = ref([])

// 分诊弹窗
const triageDialog = reactive({
  visible: false,
  row: null,
  form: {
    triageNote: ''
  }
})

// 工具：今天 yyyy-MM-dd
const today = () => {
  const d = new Date()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}

const formatDateTime = (str) => {
  if (!str) return '-'
  // 后端 LocalDateTime 默认会变成 "2025-12-08T13:20:30"
  return String(str).replace('T', ' ')
}

const statusText = (qs) => {
  switch (qs) {
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
  return {
    green: qs === 0,
    orange: qs === 1,
    gray: qs === 2 || qs === 3,
    red: qs === 4 || qs === 9
  }
}

const shorten = (text, len) => {
  if (!text) return ''
  return text.length > len ? text.slice(0, len) + '…' : text
}

// 退出登录
const backToLogin = () => {
  localStorage.removeItem('currentUser')
  router.push('/login')
}

// 加载科室
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

// 根据科室加载医生
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

// 监听科室变化，刷新医生列表 + 队列
watch(
  () => query.deptId,
  () => {
    query.doctorId = ''
    loadDoctors()
    loadQueue()
  }
)

// 加载队列
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

// 操作按钮：叫号
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

// 操作按钮：过号
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

// 操作按钮：加急
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

// 操作按钮：取消加急
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

// 分诊备注弹窗
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

// 初始化
onMounted(() => {
  const saved = localStorage.getItem('currentUser')
  if (saved) {
    try {
      currentUser.value = JSON.parse(saved)
    } catch (e) {
      console.error(e)
    }
  }

  // 默认今天
  query.visitDate = today()

  loadDepts()
  loadQueue()
})
</script>

<style scoped>
.nurse-page {
  padding: 20px 24px 40px;
  max-width: 1100px;
  margin: 0 auto;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI',
    sans-serif;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.page-header .left h2 {
  margin: 0;
  font-size: 20px;
  color: #111827;
}

.page-header .sub {
  margin-top: 4px;
  font-size: 13px;
  color: #6b7280;
}

.page-header .right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-tag {
  font-size: 13px;
  color: #374151;
}

.link-btn {
  border: none;
  background: transparent;
  color: #2563eb;
  cursor: pointer;
  font-size: 13px;
}

/* 筛选卡片 */
.filter-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 14px 16px;
  box-shadow: 0 4px 10px rgba(148, 163, 184, 0.25);
  margin-bottom: 16px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 16px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  min-width: 180px;
}

.filter-item label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 3px;
}

.filter-item input,
.filter-item select {
  padding: 6px 8px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  font-size: 13px;
}

.filter-item input:focus,
.filter-item select:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.2);
}

.filter-actions {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.primary-btn,
.ghost-btn {
  border-radius: 999px;
  padding: 6px 16px;
  font-size: 13px;
  cursor: pointer;
  border: 1px solid transparent;
}

.primary-btn {
  background: #2563eb;
  color: #fff;
  border-color: #2563eb;
}

.primary-btn:hover {
  background: #1d4ed8;
}

.ghost-btn {
  background: #ffffff;
  color: #374151;
  border-color: #d1d5db;
}

.hint {
  font-size: 12px;
  color: #9ca3af;
}

/* 表格卡片 */
.table-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 12px 16px 16px;
  box-shadow: 0 4px 10px rgba(148, 163, 184, 0.25);
  margin-bottom: 12px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.table-header h3 {
  margin: 0;
  font-size: 15px;
  color: #111827;
}

.legend {
  font-size: 11px;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 999px;
}

.dot-green {
  background: #22c55e;
}
.dot-orange {
  background: #f97316;
}
.dot-red {
  background: #ef4444;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

thead th {
  text-align: left;
  padding: 6px 8px;
  border-bottom: 1px solid #e5e7eb;
  color: #6b7280;
  font-weight: 500;
}

tbody td {
  padding: 6px 8px;
  border-bottom: 1px solid #f3f4f6;
  color: #111827;
}

.empty-tip {
  text-align: center;
  color: #9ca3af;
  font-size: 13px;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  color: #fff;
}

.status-tag.green {
  background: #22c55e;
}
.status-tag.orange {
  background: #f97316;
}
.status-tag.gray {
  background: #6b7280;
}
.status-tag.red {
  background: #ef4444;
}

.note-cell {
  max-width: 160px;
}

.note-text {
  font-size: 12px;
  color: #374151;
}

.note-empty {
  font-size: 12px;
  color: #9ca3af;
}

/* 小号副标题（就诊人下面那个 ID） */
.sub-small {
  font-size: 11px;
  color: #9ca3af;
}

.action-group {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.mini-btn {
  border-radius: 999px;
  padding: 3px 8px;
  font-size: 11px;
  border: 1px solid #d1d5db;
  background: #ffffff;
  cursor: pointer;
}

.mini-btn.primary {
  background: #2563eb;
  color: #fff;
  border-color: #2563eb;
}
.mini-btn.warn {
  background: #f97316;
  color: #fff;
  border-color: #f97316;
}
.mini-btn.danger {
  background: #ef4444;
  color: #fff;
  border-color: #ef4444;
}
.mini-btn.ghost {
  background: #ffffff;
  color: #374151;
}

/* 弹窗 */
.dialog-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.dialog {
  width: 380px;
  background: #ffffff;
  border-radius: 12px;
  padding: 16px 18px 14px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.4);
}

.dialog h3 {
  margin: 0;
  font-size: 16px;
  color: #111827;
}

.dialog-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.dialog textarea {
  width: 100%;
  margin-top: 10px;
  padding: 8px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  resize: vertical;
  font-size: 13px;
}

.dialog textarea:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.2);
}

.dialog-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 底部说明 */
.page-footer {
  margin-top: 10px;
  font-size: 11px;
  color: #9ca3af;
}
</style>
