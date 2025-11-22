<template>
  <div class="home-overview">
    <div class="header">
      <h2>今日就诊概览（A 区）</h2>
      <p class="tip">
        通过接口 <code>GET /api/home/overview?userId={{ userId }}</code> 获取数据，
        展示今日挂号、当前候诊、最近用药指导和待缴费摘要。
      </p>
    </div>

    <div v-if="loading" class="loading">
      正在加载首页信息...
    </div>

    <div v-else>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

      <!-- 第一行：今日挂号 + 当前候诊 -->
      <div class="card-row">
        <!-- 今日挂号 -->
        <section class="card today-card">
          <div class="card-title">
            <span>今日挂号</span>
          </div>

          <div v-if="today.hasRegister" class="card-body">
            <p>
              科室：
              <strong>{{ today.deptName || ('科室ID：' + today.deptId) }}</strong>
            </p>
            <p>
              医生：
              <strong>{{ today.doctorName || ('医生ID：' + today.doctorId) }}</strong>
            </p>
            <p>排队号：<strong>{{ today.queueNo }}</strong></p>
            <p>状态：<strong>{{ today.status }}</strong></p>
            <p class="time">挂号时间：{{ formatTime(today.registerTime) }}</p>
          </div>

          <div v-else class="card-body empty">
            <p>今天还没有挂号记录</p>
            <p class="small">可以通过“首页下方的智能分诊与挂号(B 区)”进行挂号。</p>
          </div>
        </section>

        <!-- 当前候诊 -->
        <section class="card queue-card">
          <div class="card-title">
            <span>当前候诊情况</span>
          </div>

          <div v-if="queue.hasWaiting" class="card-body">
            <p>前方人数：<strong>{{ queue.aheadCount }}</strong> 人</p>
            <p>
              预计等待时间：
              <strong>{{ queue.estimateWaitMin }}</strong> 分钟
            </p>
            <p class="small">估算规则：每位患者 {{ PER_PATIENT_MINUTES }} 分钟。</p>
          </div>

          <div v-else class="card-body empty">
            <p>当前没有处于等待中的挂号。</p>
            <p class="small">可能还未挂号，或者已经就诊完成。</p>
          </div>
        </section>
      </div>

      <!-- 第二行：最近用药指导 + 待缴费摘要 -->
      <div class="card-row">
        <!-- 最近用药指导 -->
        <section class="card med-card">
          <div class="card-title">
            <span>最近用药指导</span>
          </div>

          <div v-if="medication.hasGuide" class="card-body">
            <p>标题：<strong>{{ medication.guideTitle }}</strong></p>
            <p>医生：<strong>{{ medication.doctorName }}</strong></p>
            <p class="time">
              指导时间：{{ formatTime(medication.createTime) }}
            </p>
          </div>

          <div v-else class="card-body empty">
            <p>暂无用药指导记录。</p>
            <p class="small">看诊后医生开具的用药指导会显示在这里。</p>
          </div>
        </section>

        <!-- 待缴费摘要 -->
        <section class="card bill-card">
          <div class="card-title">
            <span>待缴费摘要</span>
          </div>

          <div class="card-body">
            <p>
              待支付笔数：
              <strong>{{ bills.unpaidCount }}</strong> 笔
            </p>
            <p>
              待支付总额：
              <strong>￥{{ formatMoney(bills.unpaidAmount) }}</strong>
            </p>
            <p class="small">挂号费、检查费、处方费等会统计在这里。</p>
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

const overview = ref({
  todayRegister: null,
  queueStatus: null,
  medication: null,
  bills: null
})

const today = ref({ hasRegister: false })
const queue = ref({ hasWaiting: false, aheadCount: 0, estimateWaitMin: 0 })
const medication = ref({ hasGuide: false })
const bills = ref({ unpaidCount: 0, unpaidAmount: 0 })

function formatTime(t) {
  if (!t) return '-'
  return String(t).replace('T', ' ').substring(0, 16)
}

function formatMoney(v) {
  if (v == null) return '0.00'
  const n = Number(v)
  if (Number.isNaN(n)) return String(v)
  return n.toFixed(2)
}

async function loadOverview() {
  if (!props.userId) {
    errorMsg.value = '当前用户 ID 为空，无法加载首页信息'
    return
  }
  loading.value = true
  errorMsg.value = ''

  try {
    const resp = await axios.get(`${BASE_URL}/api/home/overview`, {
      params: { userId: props.userId }
    })
    overview.value = resp.data || {}

    today.value = overview.value.todayRegister || { hasRegister: false }
    queue.value = overview.value.queueStatus || {
      hasWaiting: false,
      aheadCount: 0,
      estimateWaitMin: 0
    }
    medication.value = overview.value.medication || { hasGuide: false }
    bills.value = overview.value.bills || {
      unpaidCount: 0,
      unpaidAmount: 0
    }
  } catch (e) {
    console.error(e)
    errorMsg.value = '加载首页 A 区信息失败，请检查后端接口是否正常。'
  } finally {
    loading.value = false
  }
}

onMounted(loadOverview)

watch(
  () => props.userId,
  () => {
    loadOverview()
  }
)
</script>

<style scoped>
/* 跟你之前版本一样，这里就不重复解释了 */
.home-overview {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.header {
  margin-bottom: 4px;
}
.header h2 {
  margin: 0 0 4px;
  font-size: 20px;
}
.tip {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
}
.loading {
  padding: 16px;
  background: #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
}
.error {
  color: #dc2626;
  font-size: 13px;
  margin-bottom: 8px;
}
.card-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
.card {
  background: #ffffff;
  border-radius: 10px;
  padding: 12px 14px;
  box-shadow: 0 1px 4px rgba(15, 23, 42, 0.08);
  font-size: 14px;
  display: flex;
  flex-direction: column;
}
.card-title {
  font-weight: 600;
  margin-bottom: 8px;
  display: flex;
  justify-content: space-between;
}
.card-body p {
  margin: 2px 0;
}
.card-body.empty {
  color: #6b7280;
}
.time {
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
}
.small {
  font-size: 12px;
  color: #9ca3af;
}
.today-card {
  border-left: 4px solid #2563eb;
}
.queue-card {
  border-left: 4px solid #10b981;
}
.med-card {
  border-left: 4px solid #f59e0b;
}
.bill-card {
  border-left: 4px solid #ef4444;
}
</style>
