<!-- PharmacistPrescriptionPage.vue - 标题居中，统一风格 -->
<template>
  <div class="presc-page">
    <!-- 顶部标题栏 - 居中 -->
    <header class="presc-header">
      <div class="header-content">
        <h2>处方发药</h2>
        <div class="header-subtitle">查看待发药处方，核对并确认发药</div>
      </div>
    </header>

    <!-- 筛选区 -->
    <section class="filter-section">
      <div class="filter-container">
        <div class="filter-group">
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

          <div class="filter-item" style="margin-left: auto;">
            <button class="refresh-btn" @click="handleRefresh">
              刷新
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 主内容区域 -->
    <section class="main-content">
      <!-- 左侧列表 -->
      <div class="left-panel">
        <div class="panel-header">
          <h3>处方列表（{{ list.length }}）</h3>
        </div>
        <div class="table-container">
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
                <td colspan="6">
                  <div class="empty-state">
                    <div class="empty-icon">📋</div>
                    <div>当前条件下暂无处方</div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 右侧详情 -->
      <div class="right-panel">
        <div class="panel-header">
          <div class="panel-title">
            <h3>处方详情</h3>
            <span v-if="detail?.header?.id" class="id-text">
              #{{ detail.header.id }}
            </span>
          </div>
          <div v-if="detail?.header">
            <span class="status-tag"
                  :class="detail.header.status === 'DISPENSED' ? 'status-ok' : 'status-pending'">
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
            <div class="table-container items-container">
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
                    <td colspan="8">
                      <div class="empty-state">
                        <div class="empty-icon">💊</div>
                        <div>暂无处方明细</div>
                      </div>
                    </td>
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
                rows="3"
              />

              <div class="dispense-footer">
                <div v-if="message" class="message" :class="{ error: isError }">
                  {{ message }}
                </div>
                <button
                  class="dispense-btn"
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

// 当前登录用户
const currentUser = ref(null)

// 列表 & 查询条件
const list = ref([])
const activeId = ref(null)

const queryDate = ref(dayjs().format('YYYY-MM-DD'))
const queryStatus = ref('PENDING')

const statusOptions = [
  { value: '', label: '全部' },
  { value: 'PENDING', label: '待发药' },
  { value: 'DISPENSED', label: '已发药' }
]

// 详情
const detail = ref(null)

// 发药备注
const dispenseRemark = ref('')

// 消息
const message = ref('')
const isError = ref(false)
const loadingDispense = ref(false)

const formatStatus = (s) => {
  if (s === 'DISPENSED') return '已发药'
  if (s === 'PENDING') return '待发药'
  return s || '-'
}

const formatMoney = (v) => {
  if (v == null) return '-'
  const num = typeof v === 'number' ? v : Number(v)
  if (Number.isNaN(num)) return '-'
  return '￥' + num.toFixed(2)
}

// 能否发药：选中 && 详情加载完成 && 状态为 PENDING
const canDispense = computed(() => {
  return (
    detail.value &&
    detail.value.header &&
    detail.value.header.status === 'PENDING'
  )
})

// 加载列表
const loadList = async () => {
  try {
    const resp = await getPrescriptionList({
      date: queryDate.value,
      status: queryStatus.value || undefined
    })
    if (resp.data.code === 0) {
      list.value = resp.data.data || []
    } else {
      list.value = []
      isError.value = true
      message.value = resp.data.message || '加载处方列表失败'
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
    if (resp.data.code === 0) {
      detail.value = resp.data.data
      message.value = ''
      isError.value = false
      dispenseRemark.value = ''
    } else {
      detail.value = null
      isError.value = true
      message.value = resp.data.message || '加载处方详情失败'
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

  if (!currentUser.value || !currentUser.value.userId) {
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
      pharmacistId: currentUser.value.userId,
      remark: dispenseRemark.value || ''
    }
    const resp = await dispensePrescription(detail.value.header.id, payload)
    if (resp.data.code === 0) {
      message.value = '发药成功'
      isError.value = false
      // 重新刷新列表和详情
      await loadList()
      const cur = list.value.find((x) => x.id === detail.value.header.id)
      if (cur) {
        await loadDetail(cur.id)
      }
    } else {
      isError.value = true
      message.value = resp.data.message || '发药失败'
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
.presc-page {
  min-height: 100%;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

/* 标题栏 - 居中 */
.presc-header {
  margin-bottom: 32px;
  text-align: center;
}

.header-content h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1e3a8a;
  margin: 0 0 8px 0;
}

.header-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

/* 筛选区域 */
.filter-section {
  margin-bottom: 24px;
}

.filter-container {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 24px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-item label {
  font-size: 14px;
  color: #374151;
  white-space: nowrap;
}

.date-input {
  padding: 10px 16px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  font-size: 14px;
  outline: none;
  transition: all 0.2s ease;
}

.date-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.status-tabs {
  display: flex;
  gap: 8px;
}

.status-btn {
  padding: 10px 20px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid #e5e7eb;
  background: white;
  cursor: pointer;
  color: #4b5563;
  transition: all 0.2s ease;
}

.status-btn.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

.refresh-btn {
  padding: 10px 20px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: 1px solid #d1d5db;
  background: white;
  color: #374151;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

/* 主内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 24px;
  min-height: calc(100vh - 200px);
}

.left-panel,
.right-panel {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.panel-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.id-text {
  font-size: 14px;
  color: #6b7280;
}

/* 表格容器 */
.table-container {
  flex: 1;
  overflow-y: auto;
}

.presc-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  color: #475569;
}

.presc-table th {
  padding: 16px 20px;
  font-weight: 600;
  text-align: left;
  color: #334155;
  white-space: nowrap;
  border-bottom: 2px solid #e2e8f0;
  background: #f1f5f9;
  position: sticky;
  top: 0;
}

.presc-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.presc-table tbody tr {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.presc-table tbody tr:hover {
  background-color: #f8fafc;
}

.presc-table tbody tr.active {
  background-color: #eff6ff;
}

.cell-ellipsis {
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sub-small {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  color: #9ca3af;
}

.empty-icon {
  font-size: 32px;
  margin-bottom: 12px;
  opacity: 0.6;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
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

/* 详情区域 */
.empty-detail {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 16px;
}

.detail-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  overflow-y: auto;
}

.section {
  border-top: 1px solid #e5e7eb;
  padding-top: 16px;
}

.section:first-child {
  border-top: none;
  padding-top: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 16px 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 14px;
  color: #6b7280;
}

.info-item .value {
  font-size: 14px;
  color: #111827;
  font-weight: 500;
}

/* 明细表格 */
.items-container {
  max-height: 300px;
}

.items-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.items-table th {
  padding: 12px 16px;
  font-weight: 600;
  text-align: left;
  color: #334155;
  white-space: nowrap;
  border-bottom: 2px solid #e2e8f0;
  background: #f1f5f9;
  position: sticky;
  top: 0;
}

.items-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
}

.total-row {
  margin-top: 16px;
  text-align: right;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.total-amount {
  color: #b91c1c;
  font-size: 18px;
}

/* 发药操作 */
.dispense-box {
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 20px;
  background: #f9fafb;
}

.dispense-box textarea {
  width: 100%;
  resize: vertical;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  font-size: 14px;
  padding: 12px;
  outline: none;
  margin-bottom: 16px;
  transition: all 0.2s ease;
}

.dispense-box textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.dispense-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message {
  font-size: 14px;
  font-weight: 500;
  color: #059669;
}

.message.error {
  color: #dc2626;
}

.dispense-btn {
  padding: 12px 32px;
  border-radius: 999px;
  font-size: 16px;
  font-weight: 600;
  background: #3b82f6;
  color: white;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dispense-btn:hover:not(:disabled) {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.dispense-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .left-panel,
  .right-panel {
    min-height: 400px;
  }
}

@media (max-width: 768px) {
  .filter-group {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .presc-header h2 {
    font-size: 24px;
  }
  
  .header-subtitle {
    font-size: 14px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .dispense-footer {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .dispense-btn {
    width: 100%;
  }
}
</style>