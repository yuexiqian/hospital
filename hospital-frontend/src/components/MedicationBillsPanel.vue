<template>
  <div class="page">
    <h2 class="title">用药与费用</h2>

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
        @click="activeTab = 'bills'"
      >
        费用信息（待实现）
      </button>
    </div>

    <section v-if="activeTab === 'medication'" class="block">
      <div v-if="loading" class="placeholder">正在加载用药指导...</div>
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

    <section v-else class="block">
      <div class="placeholder">
        费用模块还没对接后端，这里先预留位置。<br />
        以后可以在此展示待缴费账单、历史费用明细等。
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const activeTab = ref('medication')
const guides = ref([])
const loading = ref(false)

const detailVisible = ref(false)
const current = ref(null)

onMounted(() => {
  loadGuides()
})

async function loadGuides() {
  loading.value = true
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
    loading.value = false
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
</style>
