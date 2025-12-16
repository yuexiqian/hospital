<template>
  <div class="page">
    <h2 class="title">用药与费用</h2>

    <!-- 顶部 Tab -->
    <div class="tabs">
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'medication' }"
        @click="activeTab = 'medication'"
      >
        用药指导
      </button>
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'bills' }"
        @click="switchToBills"
      >
        费用信息
      </button>
    </div>

    <!-- ========== Tab1：用药指导（保留你原来的） ========== -->
    <section v-if="activeTab === 'medication'" class="block">
      <div v-if="loadingGuides" class="placeholder">正在加载用药指导...</div>
      <div v-else-if="guides.length === 0" class="placeholder">
        暂无用药指导记录。
      </div>

      <div
        v-for="item in guides"
        :key="item.id"
        class="guide-card"
        @click="openDetail(item)"
      >
        <div class="guide-head">
          <span class="guide-title">{{ item.title || '未命名用药指导' }}</span>
        </div>
        <div class="guide-meta">
          <span v-if="item.deptName">{{ item.deptName }}</span>
          <span v-if="item.deptName && item.doctorName"> · </span>
          <span v-if="item.doctorName">{{ item.doctorName }}</span>
          <span v-if="item.createTime">
            · {{ formatTime(item.createTime) }}
          </span>
        </div>
        <div class="guide-content">
          {{ preview(item.content) }}
        </div>
      </div>

      <div v-if="detailVisible" class="detail-mask" @click.self="closeDetail">
        <div class="detail-dialog">
          <div class="detail-header">
            <h3>{{ current?.title || '用药指导详情' }}</h3>
            <button class="detail-close" @click="closeDetail">×</button>
          </div>
          <p class="detail-meta">
            <span v-if="current?.deptName">{{ current.deptName }}</span>
            <span v-if="current?.deptName && current?.doctorName"> · </span>
            <span v-if="current?.doctorName">{{ current.doctorName }}</span>
            <span v-if="current?.createTime">
              · {{ formatTime(current.createTime) }}
            </span>
          </p>
          <pre class="detail-content">{{ current?.content }}</pre>
        </div>
      </div>
    </section>

    <!-- ========== Tab2：费用信息 ========== -->
    <section v-else class="block">
      <!-- 摘要 -->
      <div class="summary-card">
        <div class="summary-item">
          <div class="label">待缴费用</div>
          <div class="value">
            {{ summary.unpaidCount ?? 0 }} 笔，
            合计
            <span class="money">￥{{ formatMoney(summary.unpaidAmount) }}</span>
          </div>
        </div>
        <div class="summary-item">
          <div class="label">历史已缴</div>
          <div class="value">
            总额
            <span class="money">￥{{ formatMoney(summary.paidTotal) }}</span>
          </div>
        </div>
      </div>

      <!-- 待缴列表 + 明细 -->
      <div class="bills-layout">
        <!-- 左：待缴列表 -->
        <div class="bills-list">
          <div class="list-header">
            <span class="list-title">待缴费用列表</span>
            <button class="reload-btn" @click="loadBills">刷新</button>
          </div>

          <div v-if="loadingBills" class="placeholder small">
            正在加载费用信息...
          </div>

          <table v-else class="table">
            <thead>
              <tr>
                <th>项目</th>
                <th>类型</th>
                <th>科室</th>
                <th>医生</th>
                <th>金额</th>
                <th>产生时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="unpaidBills.length === 0">
                <td colspan="7" class="empty-cell">当前没有待缴费用</td>
              </tr>
              <tr
                v-for="bill in unpaidBills"
                :key="bill.id"
                :class="{ selected: bill.id === selectedBillId }"
              >
                <td>{{ bill.itemName }}</td>
                <td>{{ bill.category }}</td>
                <td>{{ bill.deptName || '-' }}</td>
                <td>{{ bill.doctorName || '-' }}</td>
                <td>￥{{ formatMoney(bill.amount) }}</td>
                <td>{{ formatTime(bill.createTime) }}</td>
                <td>
                  <button class="link-btn" @click="viewBillDetails(bill.id)">
                    明细
                  </button>
                  <button class="primary-btn" @click="handlePay(bill.id)">
                    去缴费
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 右：费用明细 -->
        <div class="bill-details">
          <div class="details-header">
            <span class="list-title">费用明细</span>
            <span v-if="billDetail">
              （{{ billDetail.itemName }}，合计￥{{
                formatMoney(billDetail.amount)
              }}）
            </span>
          </div>

          <div v-if="!billDetail" class="placeholder small">
            请在左侧选择一条待缴费用查看明细
          </div>

          <template v-else>
            <table class="table">
              <thead>
                <tr>
                  <th>类型</th>
                  <th>项目名称 / 药品名称</th>
                  <th>规格</th>
                  <th>单价</th>
                  <th>数量</th>
                  <th>单位</th>
                  <th>用药频次</th>
                  <th>用药天数</th>
                  <th>小计</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in billDetail.items" :key="item.id">
                  <td>{{ item.itemType }}</td>
                  <td>{{ item.itemName }}</td>
                  <td>{{ item.spec || '-' }}</td>
                  <td>￥{{ formatMoney(item.unitPrice) }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>{{ item.unit || '-' }}</td>
                  <!-- 这两个目前后端没字段，先预留 '-' -->
                  <td>-</td>
                  <td>-</td>
                  <td>￥{{ formatMoney(item.subtotalAmount) }}</td>
                </tr>
              </tbody>
            </table>

            <div class="details-summary">
              共 {{ billDetail.totalItems }} 项，
              合计
              <span class="money"
                >￥{{ formatMoney(billDetail.totalAmount) }}</span
              >
            </div>
          </template>
        </div>
      </div>

      <!-- 历史缴费记录 -->
      <div class="history-block">
        <div class="list-header">
          <span class="list-title">历史缴费记录</span>
        </div>
        <table class="table small-table">
          <thead>
            <tr>
              <th>项目</th>
              <th>类型</th>
              <th>金额</th>
              <th>支付方式</th>
              <th>支付时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="historyBills.length === 0">
              <td colspan="5" class="empty-cell">暂无历史缴费记录</td>
            </tr>
            <tr v-for="bill in historyBills" :key="bill.id">
              <td>{{ bill.itemName }}</td>
              <td>{{ bill.category }}</td>
              <td>￥{{ formatMoney(bill.amount) }}</td>
              <td>{{ bill.payMethod || '-' }}</td>
              <td>{{ formatTime(bill.paidTime) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

const props = defineProps({
  userId: {
    type: Number,
    required: true,
  },
})

// ========== 用药指导部分（你的原逻辑） ==========
const activeTab = ref('medication')
const guides = ref([])
const loadingGuides = ref(false)

const detailVisible = ref(false)
const current = ref(null)

onMounted(() => {
  loadGuides()
})

async function loadGuides() {
  loadingGuides.value = true
  try {
    const url = `/api/medication-guides?userId=${props.userId}`
    console.log('📡 请求用药指导 URL =', url)

    const res = await fetch(url)
    const text = await res.text()
    console.log('📡 响应状态 =', res.status)
    console.log('📡 响应原始内容 =', text)

    if (!res.ok) {
      return
    }

    const data = text ? JSON.parse(text) : []
    console.log('📦 解析后的 JSON =', data)

    guides.value = Array.isArray(data) ? data : []
    console.log('📊 guides.length =', guides.value.length)
  } catch (e) {
    console.error('❌ loadGuides error:', e)
  } finally {
    loadingGuides.value = false
  }
}

function preview(content = '') {
  if (!content) return ''
  return content.length > 80 ? content.slice(0, 80) + '...' : content
}

function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').slice(0, 16)
}

function openDetail(item) {
  current.value = item
  detailVisible.value = true
}

function closeDetail() {
  detailVisible.value = false
  current.value = null
}

// ========== 费用信息部分 ==========

const summary = ref({
  unpaidCount: 0,
  unpaidAmount: 0,
  paidTotal: 0,
})

const unpaidBills = ref([])
const historyBills = ref([])
const selectedBillId = ref(null)
const billDetail = ref(null)
const loadingBills = ref(false)

function formatMoney(v) {
  if (v == null) return '0.00'
  const num = typeof v === 'number' ? v : Number(v)
  if (Number.isNaN(num)) return '0.00'
  return num.toFixed(2)
}

// 点击 Tab 时加载费用信息
function switchToBills() {
  activeTab.value = 'bills'
  loadBills()
}

async function loadBills() {
  if (!props.userId) return
  loadingBills.value = true
  try {
    const summaryRes = await fetch(
      `/api/bills/summary?userId=${props.userId}`,
    )
    const unpaidRes = await fetch(`/api/bills/unpaid?userId=${props.userId}`)
    const historyRes = await fetch(`/api/bills/history?userId=${props.userId}`)

    if (!summaryRes.ok || !unpaidRes.ok || !historyRes.ok) {
      console.error('获取费用信息失败', summaryRes, unpaidRes, historyRes)
      return
    }

    summary.value = await summaryRes.json()
    unpaidBills.value = await unpaidRes.json()
    historyBills.value = await historyRes.json()

    if (unpaidBills.value.length > 0) {
      selectedBillId.value = unpaidBills.value[0].id
      await loadBillDetail(selectedBillId.value)
    } else {
      selectedBillId.value = null
      billDetail.value = null
    }
  } catch (e) {
    console.error('加载费用信息失败', e)
    alert('加载费用信息失败，请稍后重试')
  } finally {
    loadingBills.value = false
  }
}

async function loadBillDetail(billId) {
  try {
    const res = await fetch(`/api/bills/${billId}/details`)
    if (!res.ok) {
      console.error('获取费用明细失败', res)
      return
    }
    billDetail.value = await res.json()
  } catch (e) {
    console.error('加载费用明细失败', e)
  }
}

function viewBillDetails(billId) {
  selectedBillId.value = billId
  loadBillDetail(billId)
}

async function handlePay(billId) {
  if (!window.confirm('确认缴费？')) return
  try {
    const res = await fetch(`/api/bills/${billId}/pay`, { method: 'POST' })
    if (!res.ok) {
      alert('缴费失败，请稍后重试')
      return
    }
    alert('缴费成功')
    await loadBills()
  } catch (e) {
    console.error('缴费失败', e)
    alert('缴费失败，请稍后重试')
  }
}

// 如果 userId 变化且当前在费用 Tab，自动刷新
watch(
  () => props.userId,
  (val) => {
    if (val && activeTab.value === 'bills') {
      loadBills()
    }
  },
)
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 14px;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tab-btn {
  padding: 6px 14px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  background: #f9fafb;
  font-size: 14px;
  cursor: pointer;
}

.tab-btn.active {
  background: #2563eb;
  color: #fff;
  border-color: #2563eb;
}

.block {
  margin-top: 4px;
}

.placeholder {
  font-size: 13px;
  color: #9ca3af;
  padding: 16px 4px;
}

.placeholder.small {
  padding: 8px 4px;
}

.guide-card {
  background: #ffffff;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  padding: 12px 14px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: box-shadow 0.15s ease, transform 0.08s ease;
}

.guide-card:hover {
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
  transform: translateY(-1px);
}

.guide-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.guide-title {
  font-weight: 600;
  font-size: 15px;
}

.guide-meta {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
  margin-bottom: 6px;
}

.guide-content {
  font-size: 13px;
  color: #4b5563;
  line-height: 1.5;
}

.detail-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.detail-dialog {
  width: 640px;
  max-width: calc(100% - 40px);
  max-height: 80vh;
  background: #ffffff;
  border-radius: 12px;
  padding: 16px 18px;
  box-shadow: 0 10px 40px rgba(15, 23, 42, 0.35);
  display: flex;
  flex-direction: column;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-header h3 {
  font-size: 18px;
  font-weight: 600;
}

.detail-close {
  border: none;
  background: transparent;
  font-size: 22px;
  cursor: pointer;
}

.detail-meta {
  font-size: 12px;
  color: #9ca3af;
  margin: 6px 0 10px;
}

.detail-content {
  flex: 1;
  margin: 0;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  overflow-y: auto;
}

/* ===== 费用信息样式 ===== */

.summary-card {
  display: flex;
  gap: 24px;
  padding: 10px 12px;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 12px;
  border: 1px solid #e5e7eb;
}

.summary-item .label {
  font-size: 12px;
  color: #6b7280;
}

.summary-item .value {
  font-size: 14px;
  margin-top: 2px;
}

.money {
  color: #ef4444;
  font-weight: 600;
}

.bills-layout {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 14px;
  margin-bottom: 14px;
}

.bills-list,
.bill-details {
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  padding: 10px 12px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.list-title {
  font-weight: 500;
  font-size: 14px;
}

.reload-btn {
  border: 1px solid #d1d5db;
  background: #f9fafb;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  cursor: pointer;
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.table thead tr {
  background: #f3f4f6;
}

.table th,
.table td {
  padding: 6px 8px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
}

.table tbody tr.selected {
  background: #eff6ff;
}

.empty-cell {
  text-align: center;
  color: #9ca3af;
}

.link-btn {
  border: none;
  background: none;
  color: #2563eb;
  cursor: pointer;
  font-size: 12px;
  margin-right: 6px;
}

.primary-btn {
  border: none;
  border-radius: 4px;
  padding: 4px 10px;
  font-size: 12px;
  background: #22c55e;
  color: #ffffff;
  cursor: pointer;
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.details-summary {
  text-align: right;
  margin-top: 8px;
  font-size: 12px;
}

.history-block {
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  padding: 10px 12px;
}

.small-table th,
.small-table td {
  padding: 4px 6px;
}
</style>
