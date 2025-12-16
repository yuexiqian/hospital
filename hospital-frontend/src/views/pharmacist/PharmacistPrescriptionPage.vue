<template>
  <div class="page">
    <!-- 顶部标题 -->
    <header class="page-header">
      <div>
        <h1>处方发药</h1>
        <p class="sub">查看待发药处方，核对并确认发药</p>
      </div>
      <div class="header-right">
        <span class="tag">当前药师：{{ currentUser?.loginName || '-' }}</span>
      </div>
    </header>

    <!-- 筛选区 -->
    <section class="filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label>日期</label>
          <input
            v-model="queryDate"
            type="date"
            class="date-input"
            @change="loadList"
          />
        </div>

        <div class="filter-item">
          <label>状态</label>
          <div class="status-tabs">
            <button
              v-for="s in statusOptions"
              :key="s.value"
              :class="['status-btn', { active: queryStatus === s.value }]"
              @click="() => { queryStatus = s.value; loadList() }"
            >
              {{ s.label }}
            </button>
          </div>
        </div>

        <div class="filter-item" style="margin-left:auto;">
          <button class="ghost-btn" @click="handleRefresh">刷新</button>
        </div>
      </div>
    </section>

    <!-- 主体两列 -->
    <section class="main-grid">
      <!-- 左侧列表 -->
      <div class="left-card">
        <div class="card-header">
          <span>处方列表（{{ list.length }}）</span>
        </div>
        <div class="table-wrapper">
          <table class="presc-table">
            <thead>
              <tr>
                <th style="width: 70px">ID</th>
                <th>患者</th>
                <th>证件号</th>
                <th>科室 / 医生</th>
                <th>开立时间</th>
                <th style="width: 80px">状态</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="row in list"
                :key="row.id"
                :class="{ active: row.id === activeId }"
                @click="selectRow(row)"
              >
                <td>{{ row.id }}</td>
                <td>{{ row.patientName || '-' }}</td>
                <td class="cell-ellipsis" :title="row.patientIdCard">
                  {{ row.patientIdCard || '-' }}
                </td>
                <td>
                  <div>{{ row.deptName || '-' }}</div>
                  <div class="sub-small">{{ row.doctorName || '-' }}</div>
                </td>
                <td>{{ row.createTime }}</td>
                <td>
                  <span
                    :class="[
                      'status-tag',
                      row.status === 'DISPENSED' ? 'status-ok' : 'status-pending'
                    ]"
                  >
                    {{ formatStatus(row.status) }}
                  </span>
                </td>
              </tr>
              <tr v-if="list.length === 0">
                <td colspan="6" class="empty-cell">当前条件下暂无处方</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 右侧详情 -->
      <div class="right-card">
        <div class="card-header flex-between">
          <div>
            <span>处方详情</span>
            <span v-if="detail?.header?.id" class="id-text">
              #{{ detail.header.id }}
            </span>
          </div>
          <div v-if="detail?.header">
            <span
              class="status-tag"
              :class="detail.header.status === 'DISPENSED' ? 'status-ok' : 'status-pending'"
            >
              {{ formatStatus(detail.header.status) }}
            </span>
          </div>
        </div>

        <div v-if="!detail" class="empty-detail">
          请在左侧选择一张处方
        </div>

        <div v-else class="detail-body">
          <!-- 基本信息 -->
          <section class="section">
            <div class="section-title">基本信息</div>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">患者</span>
                <span class="value">{{ detail.header.patientName || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">证件号</span>
                <span class="value">{{ detail.header.patientIdCard || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">科室</span>
                <span class="value">{{ detail.header.deptName || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">医生</span>
                <span class="value">{{ detail.header.doctorName || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">诊断</span>
                <span class="value">{{ detail.diag || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">开立时间</span>
                <span class="value">{{ detail.header.createTime || '-' }}</span>
              </div>
            </div>
          </section>

          <!-- 明细 -->
          <section class="section">
            <div class="section-title">处方明细</div>
            <div class="table-wrapper items-wrapper">
              <table class="items-table">
                <thead>
                  <tr>
                    <th style="width: 60px">序号</th>
                    <th>药品</th>
                    <th>规格 / 剂型</th>
                    <th>用法</th>
                    <th style="width: 80px">天数</th>
                    <th style="width: 80px">数量</th>
                    <th style="width: 80px">单价</th>
                    <th style="width: 90px">金额</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, idx) in detail.items" :key="item.id || idx">
                    <td>{{ idx + 1 }}</td>
                    <td>{{ item.drugName || '-' }}</td>
                    <td>
                      <div>{{ item.spec || '-' }}</div>
                      <div class="sub-small">{{ item.dosageForm || '-' }}</div>
                    </td>
                    <td>
                      <div>{{ item.dosage || '-' }}</div>
                      <div class="sub-small">{{ item.frequency || '-' }}</div>
                    </td>
                    <td>{{ item.days ?? '-' }}</td>
                    <td>{{ item.quantity ?? '-' }}</td>
                    <td>{{ formatMoney(item.unitPrice) }}</td>
                    <td>{{ formatMoney(item.amount) }}</td>
                  </tr>
                  <tr v-if="!detail.items || detail.items.length === 0">
                    <td colspan="8" class="empty-cell">暂无处方明细</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="total-row">
              合计：<span class="total-amount">{{ formatMoney(detail.totalAmount) }}</span>
            </div>
          </section>

          <!-- 发药区域 -->
          <section class="section">
            <div class="section-title">发药操作</div>

            <div class="dispense-box">
              <textarea
                v-model="dispenseRemark"
                placeholder="备注（可选，例如核对情况、特殊说明等）"
                rows="2"
              />

              <div class="dispense-footer">
                <span class="msg" :class="{ error: isError }" v-if="message">
                  {{ message }}
                </span>

                <button
                  class="primary-btn"
                  :disabled="!canDispense || loadingDispense"
                  @click="handleDispense"
                >
                  {{ loadingDispense ? '发药中...' : '确认发药' }}
                </button>
              </div>
            </div>
          </section>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import dayjs from 'dayjs'
import {
  getPrescriptionList,
  getPrescriptionDetail,
  dispensePrescription
} from '../../api/pharmacist'

const currentUser = ref(null)

// 列表 & 查询条件
const list = ref([])
const activeId = ref(null)

const queryDate = ref(dayjs().format('YYYY-MM-DD'))
// 待发药状态：SUBMITTED
const queryStatus = ref('SUBMITTED')

const statusOptions = [
  { value: '',          label: '全部' },
  { value: 'SUBMITTED', label: '待发药' },
  { value: 'DISPENSED', label: '已发药' }
]

// 详情
const detail = ref(null)

// 发药备注 & 状态
const dispenseRemark = ref('')
const message = ref('')
const isError = ref(false)
const loadingDispense = ref(false)

const formatStatus = (s) => {
  if (s === 'DISPENSED') return '已发药'
  if (s === 'SUBMITTED') return '待发药'
  if (s === 'DRAFT') return '草稿'
  return s || '-'
}

const formatMoney = (v) => {
  if (v == null) return '-'
  const num = typeof v === 'number' ? v : Number(v)
  if (Number.isNaN(num)) return '-'
  return '￥' + num.toFixed(2)
}

// 能否发药：选中 && 状态为 SUBMITTED
const canDispense = computed(() => {
  return (
    detail.value &&
    detail.value.header &&
    detail.value.header.status === 'SUBMITTED'
  )
})

// 统一解析列表返回
function parseListResponse(resp) {
  const d = resp.data
  if (Array.isArray(d)) {
    return { ok: true, data: d }
  }
  if (d && typeof d === 'object') {
    if (Array.isArray(d.data) && (d.code === 0 || d.code == null)) {
      return { ok: true, data: d.data }
    }
    if (Array.isArray(d.data)) {
      return { ok: false, data: [], msg: d.message || '加载处方列表失败' }
    }
  }
  return { ok: false, data: [], msg: '加载处方列表失败' }
}

// 统一解析详情返回
function parseDetailResponse(resp) {
  const d = resp.data
  if (d && d.header) {
    return { ok: true, data: d }
  }
  if (d && typeof d === 'object' && d.code !== undefined) {
    if (d.code === 0 && d.data) {
      return { ok: true, data: d.data }
    }
    return { ok: false, data: null, msg: d.message || '加载处方详情失败' }
  }
  return { ok: false, data: null, msg: '加载处方详情失败' }
}

// 加载列表
const loadList = async () => {
  try {
    const resp = await getPrescriptionList({
      date: queryDate.value,
      status: queryStatus.value || undefined
    })
    const parsed = parseListResponse(resp)
    if (parsed.ok) {
      list.value = parsed.data
      message.value = ''
      isError.value = false
    } else {
      list.value = []
      isError.value = true
      message.value = parsed.msg
    }
  } catch (e) {
    console.error(e)
    list.value = []
    isError.value = true
    message.value = '请求失败，无法加载处方列表'
  }
}

// 加载详情
const loadDetail = async (id) => {
  try {
    const resp = await getPrescriptionDetail(id)
    const parsed = parseDetailResponse(resp)
    if (parsed.ok) {
      detail.value = parsed.data
      message.value = ''
      isError.value = false
      dispenseRemark.value = ''
    } else {
      detail.value = null
      isError.value = true
      message.value = parsed.msg
    }
  } catch (e) {
    console.error(e)
    detail.value = null
    isError.value = true
    message.value = '请求失败，无法加载处方详情'
  }
}

// 选中行
const selectRow = async (row) => {
  activeId.value = row.id
  await loadDetail(row.id)
}

const handleRefresh = async () => {
  await loadList()
  if (list.value.length > 0) {
    await selectRow(list.value[0])
  } else {
    activeId.value = null
    detail.value = null
  }
}

// 发药
const handleDispense = async () => {
  if (!canDispense.value || !detail.value?.header?.id) return

  const pharmacistId = currentUser.value?.userId
  const pharmacistName =
    currentUser.value?.loginName || currentUser.value?.name || ''

  if (!pharmacistId) {
    isError.value = true
    message.value = '当前登录信息缺失，无法发药'
    return
  }

  const confirmIt = window.confirm('确认已经核对完毕并发药？')
  if (!confirmIt) return

  loadingDispense.value = true
  message.value = ''
  isError.value = false

  try {
    const payload = {
      pharmacistId,
      pharmacistName,                 // ✅ 把药师姓名一起传给后端
      remark: dispenseRemark.value || ''
    }
    const resp = await dispensePrescription(detail.value.header.id, payload)
    const d = resp.data

    // 后端可能是 void / "" / {code:0} 三种之一
    const ok = d === '' || d == null || (typeof d === 'object' && d.code === 0)

    if (ok) {
      message.value = '发药成功'
      isError.value = false

      // 刷新列表和当前详情
      await loadList()
      const cur = list.value.find(
        (x) => x.id === detail.value.header.id
      )
      if (cur) {
        await loadDetail(cur.id)
      } else {
        detail.value = null
      }
    } else {
      isError.value = true
      message.value = (d && d.message) || '发药失败'
    }
  } catch (e) {
    console.error(e)
    isError.value = true
    message.value = '请求失败，发药未成功'
  } finally {
    loadingDispense.value = false
  }
}

onMounted(async () => {
  // 读登录用户
  const saved = localStorage.getItem('currentUser')
  if (saved) {
    try {
      currentUser.value = JSON.parse(saved)
    } catch (e) {
      console.error(e)
    }
  }

  await loadList()
  if (list.value.length > 0) {
    await selectRow(list.value[0])
  }
})
</script>

<style scoped>
.page {
  padding: 24px 28px;
  background: #f3f4f6;
  min-height: 100vh;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-header h1 {
  margin: 0 0 4px;
  font-size: 20px;
  color: #111827;
}

.sub {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
}

.header-right {
  font-size: 13px;
}

.tag {
  background: #eef2ff;
  color: #3730a3;
  padding: 4px 10px;
  border-radius: 999px;
}

.filter-card {
  background: #fff;
  border-radius: 14px;
  padding: 10px 14px;
  box-shadow: 0 4px 10px rgba(15, 23, 42, 0.08);
  margin-bottom: 14px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 18px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 13px;
  color: #4b5563;
}

.date-input {
  padding: 6px 10px;
  font-size: 13px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
}

.status-tabs {
  display: flex;
  gap: 6px;
}

.status-btn {
  padding: 5px 12px;
  border-radius: 999px;
  font-size: 13px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  color: #4b5563;
}

.status-btn.active {
  background: #2563eb;
  color: #fff;
  border-color: #2563eb;
}

.ghost-btn {
  padding: 6px 14px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid #d1d5db;
  font-size: 13px;
  cursor: pointer;
}

.main-grid {
  display: grid;
  grid-template-columns: 1.1fr 2fr;
  gap: 16px;
}

.left-card,
.right-card {
  background: #fff;
  border-radius: 16px;
  padding: 12px 14px;
  box-shadow: 0 5px 14px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
}

.card-header {
  font-size: 14px;
  color: #111827;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.flex-between {
  justify-content: space-between;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
}

.presc-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.presc-table th,
.presc-table td {
  padding: 6px 8px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
}

.presc-table thead th {
  background: #f9fafb;
  color: #6b7280;
  position: sticky;
  top: 0;
  z-index: 1;
}

.presc-table tbody tr {
  cursor: pointer;
}

.presc-table tbody tr:hover {
  background: #eef2ff;
}

.presc-table tbody tr.active {
  background: #e0ecff;
}

.cell-ellipsis {
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-cell {
  text-align: center;
  color: #9ca3af;
  padding: 14px 0;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  border: 1px solid transparent;
}

.status-pending {
  background: #fef3c7;
  color: #92400e;
  border-color: #f59e0b;
}

.status-ok {
  background: #dcfce7;
  color: #166534;
  border-color: #22c55e;
}

.id-text {
  margin-left: 8px;
  font-size: 12px;
  color: #6b7280;
}

.empty-detail {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 14px;
}

.detail-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.section {
  border-top: 1px dashed #e5e7eb;
  padding-top: 8px;
}

.section:first-of-type {
  border-top: none;
}

.section-title {
  font-size: 13px;
  color: #4b5563;
  margin-bottom: 6px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 6px 12px;
}

.info-item .label {
  font-size: 12px;
  color: #9ca3af;
}

.info-item .value {
  font-size: 13px;
  color: #111827;
}

.items-wrapper {
  max-height: 260px;
}

.items-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.items-table th,
.items-table td {
  padding: 5px 6px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
}

.sub-small {
  font-size: 11px;
  color: #9ca3af;
}

.total-row {
  text-align: right;
  margin-top: 4px;
  font-size: 13px;
  color: #374151;
}

.total-amount {
  color: #b91c1c;
  font-weight: 600;
}

.dispense-box {
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 8px 10px;
  background: #f9fafb;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.dispense-box textarea {
  width: 100%;
  resize: vertical;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  font-size: 13px;
  padding: 6px 8px;
  outline: none;
}

.dispense-box textarea:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.18);
}

.dispense-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.msg {
  font-size: 13px;
  color: #16a34a;
}

.msg.error {
  color: #dc2626;
}

.primary-btn {
  padding: 7px 16px;
  border-radius: 999px;
  border: none;
  background: #2563eb;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}

.primary-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}
</style>
