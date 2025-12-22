<template>
  <div class="page">
    <!-- 标题行 -->
    <div class="header-row">
      <h2 class="title">{{ currentView === 'drug-knowledge' ? '药品知识库' : '用药与费用' }}</h2>
      
      <div class="header-actions">
        <!-- 只有在非药品知识库页面才显示就诊人选择器 -->
        <div v-if="currentView !== 'drug-knowledge'" class="patient-selector" ref="selectorRef">
          <button 
            class="patient-dropdown-btn" 
            @click="togglePatientDropdown"
          >
            <span class="current-patient">
              {{ currentPatient?.name || '选择就诊人' }}
              <span v-if="currentPatient?.id" class="patient-id-badge">
                (ID: {{ currentPatient.id }})
              </span>
            </span>
            <span class="dropdown-arrow">▼</span>
          </button>
          
          <!-- 下拉菜单 -->
          <div 
            v-show="showPatientDropdown" 
            class="patient-dropdown"
            @click.stop
          >
            <div 
              v-for="patient in patients" 
              :key="getPatientKey(patient)"
              class="dropdown-item"
              :class="{ active: isCurrentPatient(patient) }"
              @click="switchPatient(patient)"
            >
              <div class="patient-info">
                <span class="patient-name">{{ getPatientName(patient) }}</span>
                <span class="patient-id">
                  (ID: {{ getPatientId(patient) || '无' }})
                </span>
              </div>
              <div class="patient-meta">
                {{ getPatientGender(patient) || '未知' }} · 
                {{ getPatientAge(patient) || '未知' }}岁 · 
                {{ formatIdCard(getPatientIdCard(patient)) }}
              </div>
            </div>
          </div>
        </div>
        
        <!-- 切换按钮：在药品知识库页面显示返回按钮 -->
        <button 
          v-if="currentView === 'drug-knowledge'" 
          class="back-btn" 
          @click="goBackToMain"
        >
          返回用药与费用
        </button>
      </div>
    </div>

    <!-- 顶部 Tab（只有在非药品知识库页面显示） -->
    <div v-if="currentView !== 'drug-knowledge'" class="tabs">
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'medication' }"
        @click="switchToMedication"
      >
        用药指导
      </button>
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'bills' }"
        @click="switchToBillsTab"
      >
        费用信息
      </button>
      <!-- 药品知识库按钮 -->
      <button
        class="tab-btn drug-knowledge-tab"
        @click="showDrugKnowledge"
      >
        药品知识库
      </button>
    </div>

    <!-- ========== 药品知识库 ========== -->
    <section v-if="currentView === 'drug-knowledge'" class="block drug-knowledge-block">
      <div class="drug-lib-wrapper">
        <DrugKnowledgePanel />
      </div>
    </section>

    <!-- ========== 用药指导 ========== -->
    <section v-else-if="activeTab === 'medication'" class="block">
      <!-- 就诊人提示 -->
      <div v-if="currentPatient" class="patient-hint">
        当前就诊人：{{ currentPatient.name }} 
        <span v-if="currentPatient.gender">({{ currentPatient.gender }})</span>
        <span v-if="currentPatient.age">· {{ currentPatient.age }}岁</span>
      </div>
      
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

    <!-- ========== 费用信息 ========== -->
    <section v-else class="block">
      <!-- 就诊人提示 -->
      <div v-if="currentPatient" class="patient-hint">
        当前就诊人：{{ currentPatient.name }} 
        <span v-if="currentPatient.gender">({{ currentPatient.gender }})</span>
        <span v-if="currentPatient.age">· {{ currentPatient.age }}岁</span>
        <span style="color: #666; font-size: 12px; margin-left: 10px;">
          费用信息
        </span>
      </div>
      
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
        <div class="summary-item" v-if="currentPatient">
          <div class="label">就诊人</div>
          <div class="value">
            {{ currentPatient.name }}
            <span style="color: #666; font-size: 11px; margin-left: 4px;">
              (ID: {{ currentPatient.id }})
            </span>
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
              <span class="money">￥{{ formatMoney(billDetail.totalAmount) }}</span>
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
// 导入药品知识库组件
import DrugKnowledgePanel from './DrugKnowledgePanel.vue'

// 导入 API
import { getMedicationGuides, getMedicationGuidesByPatient } from '@/api/medication-guides'
import { getPatients } from '@/api/patients'
import { 
  getBillsSummary, 
  getUnpaidBills, 
  getHistoryBills, 
  getBillDetails, 
  payBill,
  getBillsSummaryByPatient,
  getUnpaidBillsByPatient,
  getHistoryBillsByPatient
} from '@/api/bills'

const props = defineProps({
  userId: {
    type: Number,
    required: true,
  },
})

// ========== 新增：视图状态控制 ==========
const currentView = ref('main') // 'main' 或 'drug-knowledge'

// ========== 就诊人相关 ==========
const patients = ref([])
const currentPatient = ref(null)
const showPatientDropdown = ref(false)
const selectorRef = ref(null)

// ========== 用药指导部分 ==========
const activeTab = ref('medication')
const guides = ref([])
const loadingGuides = ref(false)

const detailVisible = ref(false)
const current = ref(null)

// ========== 新增：视图切换函数 ==========
function showDrugKnowledge() {
  console.log('切换到药品知识库')
  currentView.value = 'drug-knowledge'
}

function goBackToMain() {
  console.log('返回主页面')
  currentView.value = 'main'
}

function switchToMedication() {
  activeTab.value = 'medication'
  if (currentPatient.value?.id) {
    loadGuidesByPatient(currentPatient.value.id)
  }
}

function switchToBillsTab() {
  activeTab.value = 'bills'
  if (currentPatient.value?.id) {
    loadBillsByPatient(currentPatient.value.id)
  } else {
    loadBills()
  }
}

onMounted(async () => {
  console.log('🚀 组件挂载，userId:', props.userId)
  
  // 添加全局点击事件监听，点击外部关闭下拉菜单
  document.addEventListener('click', (event) => {
    if (selectorRef.value && !selectorRef.value.contains(event.target)) {
      showPatientDropdown.value = false
    }
  })
  
  await loadPatients()
  
  if (patients.value.length > 0) {
    // 确保就诊人对象有 id 字段
    const firstPatient = patients.value[0]
    console.log('👤 第一个就诊人:', firstPatient)
    
    const patientId = getPatientId(firstPatient)
    if (patientId) {
      currentPatient.value = createPatientObject(firstPatient)
      console.log('📋 加载第一个就诊人的数据，ID:', patientId)
      
      // 如果当前是费用Tab，加载费用数据
      if (activeTab.value === 'bills') {
        await loadBillsByPatient(patientId)
      } else {
        await loadGuidesByPatient(patientId)
      }
    } else {
      console.error('❌ 第一个就诊人没有有效的ID字段:', firstPatient)
      // 尝试使用其他可能的字段名
      const idField = Object.keys(firstPatient).find(key => 
        key.toLowerCase().includes('id') || key === 'patientId'
      )
      if (idField) {
        console.log('🔍 找到可能的ID字段:', idField, '=', firstPatient[idField])
        currentPatient.value = createPatientObject(firstPatient)
        if (activeTab.value === 'bills') {
          await loadBillsByPatient(firstPatient[idField])
        } else {
          await loadGuidesByPatient(firstPatient[idField])
        }
      } else {
        console.log('📭 无法获取就诊人ID，按用户ID加载数据')
        if (activeTab.value === 'bills') {
          await loadBills()
        } else {
          await loadGuides()
        }
      }
    }
  } else {
    console.log('📭 没有就诊人数据，按用户ID加载数据')
    if (activeTab.value === 'bills') {
      await loadBills()
    } else {
      await loadGuides()
    }
  }
})

// ========== 就诊人辅助函数 ==========

// 获取就诊人唯一标识键
function getPatientKey(patient) {
  return getPatientId(patient) || JSON.stringify(patient)
}

// 获取就诊人ID（支持多种字段名）
function getPatientId(patient) {
  if (!patient) return null
  return patient.id || patient.patientId || patient.patientID || patient.PatientId
}

// 获取就诊人姓名（支持多种字段名）
function getPatientName(patient) {
  if (!patient) return '未知'
  return patient.name || patient.patientName || patient.PatientName || '未知'
}

// 获取就诊人性别（支持多种字段名）
function getPatientGender(patient) {
  if (!patient) return ''
  return patient.gender || patient.sex || patient.Gender || ''
}

// 获取就诊人年龄（支持多种字段名）
function getPatientAge(patient) {
  if (!patient) return ''
  return patient.age || patient.Age || ''
}

// 获取就诊人身份证（支持多种字段名）
function getPatientIdCard(patient) {
  if (!patient) return ''
  return patient.idCard || patient.idcard || patient.identityCard || patient.IdCard || ''
}

// 格式化身份证显示
function formatIdCard(idCard) {
  if (!idCard) return '未知'
  return idCard.substring(0, 4) + '****'
}

// 检查是否为当前就诊人
function isCurrentPatient(patient) {
  if (!currentPatient.value || !patient) return false
  return getPatientId(currentPatient.value) === getPatientId(patient)
}

// 创建标准化的就诊人对象
function createPatientObject(rawPatient) {
  return {
    id: getPatientId(rawPatient),
    name: getPatientName(rawPatient),
    gender: getPatientGender(rawPatient),
    age: getPatientAge(rawPatient),
    idCard: getPatientIdCard(rawPatient)
  }
}

// 加载就诊人列表
async function loadPatients() {
  try {
    console.log('🔄 开始加载就诊人，userId:', props.userId)
    const res = await getPatients(props.userId)
    console.log('✅ 就诊人接口响应:', res)
    console.log('📊 就诊人数据:', res.data)
    patients.value = res.data || []
    console.log('👥 就诊人列表:', patients.value)
  } catch (e) {
    console.error('❌ 加载就诊人列表失败:', e)
  }
}

// 切换就诊人
async function switchPatient(rawPatient) {
  console.log('🔄 切换就诊人，完整对象:', rawPatient)
  
  // 提取就诊人ID
  const patientId = getPatientId(rawPatient)
  
  if (!patientId) {
    console.error('❌ 无法获取就诊人ID，patient对象:', rawPatient)
    alert('无法获取就诊人信息，请检查数据格式')
    return
  }
  
  // 创建新的就诊人对象
  currentPatient.value = createPatientObject(rawPatient)
  
  // 关闭下拉菜单
  showPatientDropdown.value = false
  
  console.log('📋 加载就诊人ID:', patientId, '的数据')
  
  // 根据当前Tab加载相应的数据
  if (activeTab.value === 'medication') {
    await loadGuidesByPatient(patientId)
  } else if (activeTab.value === 'bills') {
    await loadBillsByPatient(patientId)
  }
}

// 切换就诊人下拉菜单（简化版）
function togglePatientDropdown() {
  showPatientDropdown.value = !showPatientDropdown.value
}

// ========== 用药指导加载函数 ==========
async function loadGuides() {
  loadingGuides.value = true
  try {
    const res = await getMedicationGuides(props.userId)
    guides.value = res.data || []
  } catch (e) {
    console.error('❌ loadGuides error:', e)
  } finally {
    loadingGuides.value = false
  }
}

// 根据就诊人ID加载用药指导
async function loadGuidesByPatient(patientId) {
  console.log('🔄 loadGuidesByPatient 调用，patientId:', patientId)
  
  if (!patientId) {
    console.error('❌ patientId 为空或 undefined!')
    return
  }
  
  loadingGuides.value = true
  try {
    const res = await getMedicationGuidesByPatient(patientId)
    console.log('✅ 用药指导接口响应:', res)
    guides.value = res.data || []
  } catch (e) {
    console.error('❌ loadGuidesByPatient error:', e)
    // 如果接口不存在，fallback到按用户加载
    if (e.response?.status === 404) {
      console.log('⚠️ 按就诊人接口404，回退到按用户查询')
      await loadGuides()
    }
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
  if (currentPatient.value?.id) {
    loadBillsByPatient(currentPatient.value.id)
  } else {
    loadBills()
  }
}

// 按用户ID加载费用信息（原有方法）
async function loadBills() {
  if (!props.userId) return
  loadingBills.value = true
  try {
    // 使用 Promise.all 并行请求
    const [summaryRes, unpaidRes, historyRes] = await Promise.all([
      getBillsSummary(props.userId),
      getUnpaidBills(props.userId),
      getHistoryBills(props.userId)
    ])

    summary.value = summaryRes.data
    unpaidBills.value = unpaidRes.data
    historyBills.value = historyRes.data

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

// 按就诊人ID加载费用信息（新增方法）
async function loadBillsByPatient(patientId) {
  console.log('🔄 loadBillsByPatient 调用，patientId:', patientId)
  
  if (!patientId) {
    console.error('❌ patientId 为空或 undefined!')
    // 回退到按用户加载
    await loadBills()
    return
  }
  
  loadingBills.value = true
  try {
    // 使用 Promise.all 并行请求
    const [summaryRes, unpaidRes, historyRes] = await Promise.all([
      getBillsSummaryByPatient(patientId),
      getUnpaidBillsByPatient(patientId),
      getHistoryBillsByPatient(patientId)
    ])

    console.log('✅ 费用接口响应: summary', summaryRes)
    console.log('✅ 费用接口响应: unpaid', unpaidRes)
    console.log('✅ 费用接口响应: history', historyRes)

    summary.value = summaryRes.data
    unpaidBills.value = unpaidRes.data
    historyBills.value = historyRes.data

    if (unpaidBills.value.length > 0) {
      selectedBillId.value = unpaidBills.value[0].id
      await loadBillDetail(selectedBillId.value)
    } else {
      selectedBillId.value = null
      billDetail.value = null
    }
  } catch (e) {
    console.error('❌ 按就诊人加载费用信息失败', e)
    
    // 如果按就诊人接口不存在，fallback到按用户加载
    if (e.response?.status === 404) {
      console.log('⚠️ 按就诊人接口404，回退到按用户查询')
      await loadBills()
    } else {
      alert('加载费用信息失败，请稍后重试')
    }
  } finally {
    loadingBills.value = false
  }
}

async function loadBillDetail(billId) {
  try {
    const res = await getBillDetails(billId)
    billDetail.value = res.data
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
    await payBill(billId)
    alert('缴费成功')
    // 缴费成功后重新加载数据
    if (currentPatient.value?.id) {
      await loadBillsByPatient(currentPatient.value.id)
    } else {
      await loadBills()
    }
  } catch (e) {
    console.error('缴费失败', e)
    alert('缴费失败，请稍后重试')
  }
}

// 如果 userId 变化且当前在费用 Tab，自动刷新
watch(
  () => props.userId,
  async (val) => {
    if (val) {
      await loadPatients()
      if (patients.value.length > 0 && !currentPatient.value) {
        const firstPatient = patients.value[0]
        const patientId = getPatientId(firstPatient)
        if (patientId) {
          currentPatient.value = createPatientObject(firstPatient)
        }
      }
      if (activeTab.value === 'bills' && currentView.value === 'main') {
        if (currentPatient.value?.id) {
          await loadBillsByPatient(currentPatient.value.id)
        } else {
          await loadBills()
        }
      }
    }
  },
)

// 监听当前就诊人变化
watch(
  () => currentPatient.value,
  async (patient) => {
    if (patient && currentView.value === 'main') {
      if (activeTab.value === 'medication') {
        await loadGuidesByPatient(patient.id)
      } else if (activeTab.value === 'bills') {
        await loadBillsByPatient(patient.id)
      }
    }
  }
)

// 监听Tab切换
watch(
  () => activeTab.value,
  async (newTab) => {
    if (newTab === 'bills' && currentPatient.value?.id && currentView.value === 'main') {
      await loadBillsByPatient(currentPatient.value.id)
    }
  }
)

// 监听视图切换，当从药品知识库返回时恢复之前的状态
watch(
  () => currentView.value,
  (newView) => {
    console.log('视图切换:', newView)
    if (newView === 'main') {
      // 视图切换回主页面时，保持原有的Tab状态
      console.log('当前Tab:', activeTab.value)
    }
  }
)
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 头部行样式 */
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  position: relative;
}

.title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

/* 头部操作容器 */
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 返回按钮样式 */
.back-btn {
  padding: 8px 16px;
  background: #6b7280;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: white;
  font-weight: 500;
  transition: background 0.2s ease;
}

.back-btn:hover {
  background: #4b5563;
}

/* 就诊人选择器 */
.patient-selector {
  position: relative;
}

.patient-dropdown-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #334155;
  transition: all 0.2s ease;
}

.patient-dropdown-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.current-patient {
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.patient-id-badge {
  font-size: 11px;
  opacity: 0.7;
  font-weight: normal;
}

.dropdown-arrow {
  font-size: 10px;
  color: #64748b;
}

/* 下拉菜单 */
.patient-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 4px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.15);
  min-width: 240px;
  max-height: 300px;
  overflow-y: auto;
  z-index: 100;
}

.dropdown-item {
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.2s ease;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background: #f8fafc;
}

.dropdown-item.active {
  background: #eff6ff;
}

.patient-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2px;
}

.patient-name {
  font-weight: 500;
  font-size: 14px;
  color: #1e293b;
}

.patient-id {
  font-size: 11px;
  color: #64748b;
  background: #f1f5f9;
  padding: 1px 4px;
  border-radius: 2px;
}

.patient-meta {
  font-size: 12px;
  color: #64748b;
}

/* 就诊人提示 */
.patient-hint {
  font-size: 13px;
  color: #475569;
  background: #f8fafc;
  padding: 8px 12px;
  border-radius: 6px;
  margin-bottom: 12px;
  border-left: 3px solid #3b82f6;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
/* 刷新按钮样式 */
.reload-btn {
  border: 1px solid #d1d5db;
  background: #ffffff; /* 改为白色背景 */
  color: #374151; /* 改为深灰色文字 */
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.reload-btn:hover {
  background: #2563eb; /* 悬停时蓝色背景 */
  color: #ffffff; /* 悬停时白色文字 */
  border-color: #2563eb;
}

/* 用药指导Tab按钮 */
.tab-btn {
  padding: 6px 14px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  background: #ffffff; /* 改为白色背景 */
  color: #374151; /* 改为深灰色文字 */
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn.active {
  background: #2563eb; /* 点击时为蓝色背景 */
  color: #ffffff; /* 点击时为白色文字 */
  border-color: #2563eb;
}
.tab-btn:hover {
  background: #f3f4f6; /* 悬停时浅灰色背景 */
}

/* 药品知识库Tab按钮样式 */
.drug-knowledge-tab {
  background: #10b981;
  color: white;
  border-color: #10b981;
}

.drug-knowledge-tab:hover {
  background: #0da271;
  border-color: #0da271;
}

.block {
  margin-top: 4px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* 药品知识库区块样式 */
.drug-knowledge-block {
  margin-top: 0;
  height: 100%;
}

/* ===== 药品知识库样式隔离 ===== */
.drug-lib-wrapper {
  height: 100%;
  min-height: 500px;
  display: flex;
  flex-direction: column;
  background: #f9fafb;
  border-radius: 12px;
  padding: 16px;
}

/* 使用 :deep() 穿透样式到子组件 */
.drug-lib-wrapper :deep(.drug-lib) {
  display: flex !important;
  flex-direction: column !important;
  height: 100% !important;
  gap: 16px !important;
  flex: 1 !important;
}

/* 重置按钮样式 */
.drug-lib-wrapper :deep(button) {
  all: unset !important;
  box-sizing: border-box !important;
  cursor: pointer !important;
  font-family: inherit !important;
}

/* 工具栏样式 */
.drug-lib-wrapper :deep(.toolbar) {
  display: flex !important;
  justify-content: space-between !important;
  align-items: center !important;
  padding: 12px 0 !important;
  background: white !important;
  margin-bottom: 16px !important;
}

/* 按钮特定样式 */
.drug-lib-wrapper :deep(.btn) {
  padding: 8px 16px !important;
  border-radius: 6px !important;
  border: 1px solid #d1d5db !important;
  background: #2563eb !important;
  color: #fff !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  cursor: pointer !important;
  box-shadow: none !important;
  transition: background-color 0.2s ease !important;
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  min-height: 36px !important;
}

.drug-lib-wrapper :deep(.btn:hover) {
  background: #1d4ed8 !important;
  transform: none !important;
  border-color: #1d4ed8 !important;
}

.drug-lib-wrapper :deep(.btn.plain) {
  background: #f3f4f6 !important;
  color: #374151 !important;
  border: 1px solid #d1d5db !important;
}

.drug-lib-wrapper :deep(.btn.plain:hover) {
  background: #e5e7eb !important;
}

/* 主体布局 */
.drug-lib-wrapper :deep(.body) {
  display: grid !important;
  grid-template-columns: 50% 50% !important;
  gap: 16px !important;
  flex: 1 !important;
  min-height: 0 !important;
}

/* 卡片样式 */
.drug-lib-wrapper :deep(.list-card),
.drug-lib-wrapper :deep(.detail-card) {
  background: #ffffff !important;
  border-radius: 10px !important;
  border: 1px solid #e5e7eb !important;
  padding: 16px !important;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1) !important;
  overflow: hidden !important;
  display: flex !important;
  flex-direction: column !important;
}

/* 表格样式 */
.drug-lib-wrapper :deep(.drug-table) {
  width: 100% !important;
  border-collapse: separate !important;
  border-spacing: 0 !important;
  font-size: 14px !important;
}

.drug-lib-wrapper :deep(.drug-table thead) {
  background: #f8fafc !important;
  position: sticky !important;
  top: 0 !important;
}

.drug-lib-wrapper :deep(.drug-table th) {
  padding: 12px 8px !important;
  border-bottom: 2px solid #e5e7eb !important;
  font-weight: 600 !important;
  color: #374151 !important;
  background: #f8fafc !important;
}

.drug-lib-wrapper :deep(.drug-table td) {
  padding: 10px 8px !important;
  border-bottom: 1px solid #f3f4f6 !important;
  color: #4b5563 !important;
}

/* 输入框样式 */
.drug-lib-wrapper :deep(.search-input) {
  width: 280px !important;
  padding: 8px 12px !important;
  border-radius: 6px !important;
  border: 1px solid #d1d5db !important;
  font-size: 14px !important;
  background: white !important;
}

.drug-lib-wrapper :deep(.select) {
  padding: 8px 12px !important;
  border-radius: 6px !important;
  border: 1px solid #d1d5db !important;
  font-size: 14px !important;
  background: white !important;
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

.summary-item {
  min-width: 120px;
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