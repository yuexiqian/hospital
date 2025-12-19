<!-- src/views/doctor/DoctorWorkbenchPage.vue -->
<template>
  <div class="workbench-page">
    <div class="workbench-content">
      <!-- 头部区域 -->
      <header class="workbench-header">
        <div class="header-content">
          <h2>就诊工作台</h2>
          <div class="header-info">
            <div class="info-item">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M8 15C11.866 15 15 11.866 15 8C15 4.13401 11.866 1 8 1C4.13401 1 1 4.13401 1 8C1 11.866 4.13401 15 8 15Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M8 4V8L10.5 9.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>挂号号：{{ detail.registerId || '-' }}</span>
            </div>
            <div class="info-divider">·</div>
            <div class="info-item">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12.6667 2.66699H3.33333C2.59695 2.66699 2 3.26395 2 4.00033V12.0003C2 12.7367 2.59695 13.3337 3.33333 13.3337H12.6667C13.403 13.3337 14 12.7367 14 12.0003V4.00033C14 3.26395 13.403 2.66699 12.6667 2.66699Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M10.6667 1.33301V3.99967" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M5.33333 1.33301V3.99967" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 6.66699H14" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>日期：{{ formatDateTime() }}</span>
            </div>
          </div>
        </div>
        <button class="back-btn" @click="goBack">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 12L6 8L10 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          返回队列
        </button>
      </header>

      <main class="workbench-main">
        <!-- 患者基本信息 -->
        <section class="info-section">
          <div class="section-header">
            <div class="section-title">
              <div class="title-icon">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M10 17.5C14.1421 17.5 17.5 14.1421 17.5 10C17.5 5.85786 14.1421 2.5 10 2.5C5.85786 2.5 2.5 5.85786 2.5 10C2.5 14.1421 5.85786 17.5 10 17.5Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M10 10.8333C11.3807 10.8333 12.5 9.71405 12.5 8.33333C12.5 6.95262 11.3807 5.83333 10 5.83333C8.61929 5.83333 7.5 6.95262 7.5 8.33333C7.5 9.71405 8.61929 10.8333 10 10.8333Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M5.83301 15.8333C6.34119 14.7733 7.13087 13.8846 8.11326 13.2675C9.09566 12.6503 10.2318 12.3289 11.3897 12.3389C12.5477 12.349 13.6788 12.6901 14.6516 13.3243C15.6245 13.9585 16.3988 14.8605 16.888 15.9333" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <h3>患者信息</h3>
            </div>
            <div class="status-badge">
              <span class="status-dot"></span>
              {{ detail.visitStatus || '—' }}
            </div>
          </div>
          
          <div class="info-grid">
            <div class="info-item">
              <label>姓名</label>
              <div class="info-value">{{ detail.patientName || '—' }}</div>
            </div>
            <div class="info-item">
              <label>身份证号</label>
              <div class="info-value">{{ detail.idCard || '—' }}</div>
            </div>
            <div class="info-item">
              <label>联系电话</label>
              <div class="info-value">{{ detail.phone || '—' }}</div>
            </div>
            <div class="info-item">
              <label>科室</label>
              <div class="info-value">{{ detail.deptName || '—' }}</div>
            </div>
            <div class="info-item">
              <label>医生</label>
              <div class="info-value">{{ detail.doctorName || '—' }}</div>
            </div>
            <div class="info-item">
              <label>职称</label>
              <div class="info-value">{{ detail.doctorTitle || '—' }}</div>
            </div>
          </div>
        </section>

        <!-- 病历记录 -->
        <div class="two-column">
          <section class="form-section">
            <div class="section-header">
              <div class="section-title">
                <div class="title-icon">
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M16.6667 2.5H3.33333C2.87333 2.5 2.5 2.87333 2.5 3.33333V16.6667C2.5 17.1267 2.87333 17.5 3.33333 17.5H16.6667C17.1267 17.5 17.5 17.1267 17.5 16.6667V3.33333C17.5 2.87333 17.1267 2.5 16.6667 2.5Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M5.83301 5.83301H14.1663" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M5.83301 9.16699H14.1663" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M5.83301 12.5H10.833" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <h3>病历记录</h3>
              </div>
            </div>
            
            <div class="form-group">
              <label class="form-label">诊断</label>
              <input 
                v-model="form.diag" 
                class="form-input" 
                placeholder="请输入诊断结果"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">医生备注</label>
              <textarea
                v-model="form.remark"
                class="form-textarea"
                rows="4"
                placeholder="补充就诊建议、注意事项等"
              />
            </div>
          </section>

          <!-- 患者历史记录 -->
          <section class="history-section">
            <div class="section-header">
              <div class="section-title">
                <div class="title-icon">
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M10 17.5C14.1421 17.5 17.5 14.1421 17.5 10C17.5 5.85786 14.1421 2.5 10 2.5C5.85786 2.5 2.5 5.85786 2.5 10C2.5 14.1421 5.85786 17.5 10 17.5Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M10 5V10L13 11.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <h3>历史就诊记录</h3>
              </div>
            </div>
            
            <div class="table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th>序号</th>
                    <th>就诊时间</th>
                    <th>科室</th>
                    <th>医生</th>
                    <th>诊断</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="history.length === 0">
                    <td colspan="5">
                      <div class="empty-state">
                        <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <path d="M16 6H8C6.89543 6 6 6.89543 6 8V40C6 41.1046 6.89543 42 8 42H40C41.1046 42 42 41.1046 42 40V32" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          <path d="M32 6H40C41.1046 6 42 6.89543 42 8V16" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          <path d="M42 24L24 42" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          <path d="M24 24L42 42" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                        <p>暂无历史记录</p>
                      </div>
                    </td>
                  </tr>
                  <tr v-for="(h, idx) in history" :key="h.registerId">
                    <td class="index-cell">{{ idx + 1 }}</td>
                    <td>{{ formatHistoryTime(h.visitTime) }}</td>
                    <td>{{ h.deptName || '—' }}</td>
                    <td>{{ h.doctorName || '—' }}</td>
                    <td class="diagnosis-cell">{{ h.diag || '—' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        </div>

        <!-- 处方编辑 -->
        <section class="prescription-section">
          <div class="section-header">
            <div class="section-title">
              <div class="title-icon">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16.6667 2.5H3.33333C2.87333 2.5 2.5 2.87333 2.5 3.33333V16.6667C2.5 17.1267 2.87333 17.5 3.33333 17.5H16.6667C17.1267 17.5 17.5 17.1267 17.5 16.6667V3.33333C17.5 2.87333 17.1267 2.5 16.6667 2.5Z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M5.83301 5.83301H14.1663" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M8.33301 9.16699H14.1663" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M8.33301 12.5H14.1663" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M5.83301 12.5H6.66634" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M5.83301 9.16699H6.66634" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <h3>处方明细</h3>
            </div>
            
            <div class="search-box">
              <div class="search-input-wrapper">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M7.33333 12.6667C10.2789 12.6667 12.6667 10.2789 12.6667 7.33333C12.6667 4.38781 10.2789 2 7.33333 2C4.38781 2 2 4.38781 2 7.33333C2 10.2789 4.38781 12.6667 7.33333 12.6667Z" stroke="#64748B" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M14 14L11.1 11.1" stroke="#64748B" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <input
                  v-model="drugKeyword"
                  class="search-input"
                  placeholder="输入药品名/通用名搜索"
                  @keyup.enter="doSearchDrug"
                />
              </div>
              <button class="search-btn" @click="doSearchDrug">
                搜索药品
              </button>
            </div>
          </div>

          <!-- 搜索结果列表 -->
          <div v-if="drugList.length" class="search-results">
            <div class="results-header">
              <div>药品名</div>
              <div>规格</div>
              <div>推荐用法</div>
              <div>操作</div>
            </div>
            <div
              v-for="item in drugList"
              :key="item.drugId"
              class="result-item"
            >
              <div class="drug-name">{{ item.drugName }}</div>
              <div class="drug-spec">{{ item.spec || '—' }}</div>
              <div class="drug-usage">{{ item.dosageUsage || '—' }}</div>
              <div>
                <button class="select-btn" @click="addDrugFromSearch(item)">
                  选择
                </button>
              </div>
            </div>
          </div>

          <!-- 处方明细表 -->
          <div class="table-container">
            <table class="prescription-table">
              <thead>
                <tr>
                  <th>序号</th>
                  <th>药品名称</th>
                  <th>剂量/用法</th>
                  <th>频次</th>
                  <th>天数</th>
                  <th>数量</th>
                  <th>备注</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="items.length === 0">
                  <td colspan="8">
                    <div class="empty-state">
                      <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M16 6H8C6.89543 6 6 6.89543 6 8V40C6 41.1046 6.89543 42 8 42H40C41.1046 42 42 41.1046 42 40V32" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M16 16H32" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M16 24H28" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M16 32H24" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M36 10L42 4" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M36 4L42 10" stroke="#9CA3AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                      <p>暂无处方明细，可通过上方搜索选择药品</p>
                    </div>
                  </td>
                </tr>
                <tr v-for="(row, index) in items" :key="row._key">
                  <td class="index-cell">{{ index + 1 }}</td>
                  <td>
                    <input v-model="row.drugName" class="table-input" placeholder="药品名称" />
                  </td>
                  <td>
                    <input v-model="row.dosage" class="table-input" placeholder="用法/剂量" />
                  </td>
                  <td>
                    <input v-model="row.frequency" class="table-input small" placeholder="如 bid/tid" />
                  </td>
                  <td>
                    <input v-model.number="row.days" type="number" min="1" class="table-input small number" />
                  </td>
                  <td>
                    <input v-model.number="row.quantity" type="number" min="1" class="table-input small number" />
                  </td>
                  <td>
                    <input v-model="row.remark" class="table-input" placeholder="备注" />
                  </td>
                  <td>
                    <button class="delete-btn" @click="removeRow(index)">
                      <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M11 3.5L10.5 11.5C10.456 12.0523 10.0016 12.5 9.448 12.5H4.552C3.99845 12.5 3.544 12.0523 3.5 11.5L3 3.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M5.5 3.5V2.5C5.5 2.22386 5.72386 2 6 2H8C8.27614 2 8.5 2.22386 8.5 2.5V3.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M1.5 3.5H12.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M6 6V9.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M8 6V9.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                      删除
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </main>

      <!-- 底部操作按钮 -->
      <footer class="workbench-footer">
        <div class="footer-content">
          <button class="footer-btn secondary" @click="handleSave(false)">
            保存草稿
          </button>
          <button class="footer-btn primary" @click="handleSave(true)">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M13.3333 4L6 11.3333L2.66667 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            提交处方并结束就诊
          </button>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getVisitDetail,
  saveVisit,
  getPatientHistory,
  searchDrugs
} from '../../api/doctor'

const route = useRoute()
const router = useRouter()

// 同时兼容 /doctor/workbench/13 和 /doctor/workbench?registerId=13
const registerId = computed(() => {
  const p = route.params.registerId
  const q = route.query.registerId
  return Number(p || q || 0)
})

const loading = ref(false)
const detail = reactive({
  registerId: null,
  registerDate: '',
  registerTime: '',
  visitStatus: '',
  patientId: null,
  patientName: '',
  idCard: '',
  phone: '',
  deptName: '',
  doctorName: '',
  doctorTitle: '',
  diag: '',
  remark: '',
  prescriptions: []
})

const form = reactive({
  diag: '',
  remark: ''
})

// 单张处方的明细
const items = ref([])

// 历史记录
const history = ref([])

// 药品搜索
const drugKeyword = ref('')
const drugList = ref([])

// 格式化日期时间显示
const formatDateTime = () => {
  if (!detail.registerDate && !detail.registerTime) return '—'
  
  const date = detail.registerDate || ''
  const time = detail.registerTime || ''
  
  if (date && time) {
    return `${date} ${time}`
  } else if (date) {
    return date
  } else {
    return time
  }
}

// 格式化历史记录时间
const formatHistoryTime = (time) => {
  if (!time) return '—'
  return time.length > 10 ? time.substring(0, 10) : time
}

const normalizeItemsFromDetail = () => {
  items.value = []
  const first = detail.prescriptions && detail.prescriptions[0]
  if (first && first.items && first.items.length) {
    items.value = first.items.map(it => ({
      _key: String(it.itemId || Math.random()),
      itemId: it.itemId,
      drugId: it.drugId,
      drugName: it.drugName,
      dosage: it.dosage,
      frequency: it.frequency,
      days: it.days || 1,
      quantity: it.quantity || 1,
      remark: it.remark || ''
    }))
  }
}

const loadDetail = async () => {
  if (!registerId.value) {
    console.warn('DoctorWorkbench: no registerId, skip loadDetail', route.params, route.query)
    return
  }

  loading.value = true
  try {
    const res = await getVisitDetail(registerId.value)
    const data = res.data || res

    Object.assign(detail, data)
    form.diag = data.diag || ''
    form.remark = data.remark || ''

    normalizeItemsFromDetail()

    if (detail.patientId) {
      const hisRes = await getPatientHistory(detail.patientId)
      history.value = hisRes.data || hisRes
    }
  } catch (e) {
    console.error(e)
    window.alert('加载就诊详情失败')
  } finally {
    loading.value = false
  }
}

const addDrugFromSearch = (d) => {
  items.value.push({
    _key: Math.random().toString(36).slice(2),
    itemId: null,
    drugId: d.drugId,
    drugName: d.drugName,
    dosage: d.dosageUsage || '',
    frequency: '',
    days: 1,
    quantity: 1,
    remark: ''
  })
}

const doSearchDrug = async () => {
  if (!drugKeyword.value) {
    drugList.value = []
    return
  }
  try {
    const res = await searchDrugs(drugKeyword.value)
    drugList.value = res.data || res
  } catch (e) {
    console.error(e)
    window.alert('查询药品失败')
  }
}

const removeRow = (idx) => {
  items.value.splice(idx, 1)
}

const handleSave = async (submit) => {
  if (!registerId.value) {
    window.alert('当前页面缺少挂号号，请从候诊队列重新进入')
    return
  }

  if (submit && items.value.length === 0) {
    if (!window.confirm('当前处方没有任何明细，仍然要提交并结束就诊吗？')) {
      return
    }
  }

  const payload = {
    diag: form.diag,
    remark: form.remark,
    submitPrescription: submit,
    prescriptions: [
      {
        prescriptionId: detail.prescriptions?.[0]?.prescriptionId || null,
        status: submit ? 'SUBMITTED' : 'DRAFT',
        items: items.value.map(r => ({
          itemId: r.itemId || null,
          drugId: r.drugId,
          quantity: r.quantity,
          dosage: r.dosage,
          frequency: r.frequency,
          days: r.days,
          remark: r.remark,
          amount: 0
        }))
      }
    ]
  }

  try {
    await saveVisit(registerId.value, payload)
    window.alert(submit ? '已提交处方并结束就诊' : '草稿已保存')
    if (submit) {
      router.push('/doctor/queue')
    } else {
      loadDetail()
    }
  } catch (e) {
    console.error(e)
    window.alert('保存失败')
  }
}

const goBack = () => {
  router.push('/doctor/queue')
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.workbench-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 24px;
}

.workbench-content {
  max-width: 1200px;
  margin: 0 auto;
}

/* 头部区域 */
.workbench-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-content {
  text-align: center;
  flex: 1;
}

.workbench-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1e3a8a;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.header-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  font-size: 14px;
  color: #64748b;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-divider {
  color: #cbd5e1;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: white;
  color: #3b82f6;
  border: 1px solid #dbeafe;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.back-btn:hover {
  background: #eff6ff;
  border-color: #3b82f6;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

/* 主要内容区域 */
.workbench-main {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 
    0 1px 3px rgba(15, 23, 42, 0.05),
    0 10px 25px -5px rgba(15, 23, 42, 0.05);
  border: 1px solid #f1f5f9;
  margin-bottom: 32px;
}

/* 通用卡片样式 */
.info-section,
.form-section,
.history-section,
.prescription-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f1f5f9;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #eff6ff;
  border-radius: 8px;
  color: #3b82f6;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f0f9ff;
  color: #0c4a6e;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #0ea5e9;
  border-radius: 50%;
}

/* 患者信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.info-value {
  font-size: 15px;
  color: #1e293b;
  font-weight: 500;
  padding: 8px 0;
}

/* 两栏布局 */
.two-column {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

@media (max-width: 1024px) {
  .two-column {
    grid-template-columns: 1fr;
  }
}

/* 表单样式 */
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

.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #1e293b;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
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
  min-height: 100px;
  font-family: inherit;
}

.form-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 搜索框 */
.search-box {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input-wrapper {
  position: relative;
  flex: 1;
}

.search-input-wrapper svg {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
}

.search-input {
  width: 100%;
  padding: 10px 14px 10px 36px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #1e293b;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-btn {
  padding: 10px 20px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.search-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

/* 搜索结果 */
.search-results {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
}

.results-header {
  display: grid;
  grid-template-columns: 2fr 2fr 3fr 1fr;
  padding: 12px 16px;
  background: #f1f5f9;
  font-weight: 600;
  color: #475569;
  font-size: 13px;
}

.result-item {
  display: grid;
  grid-template-columns: 2fr 2fr 3fr 1fr;
  padding: 12px 16px;
  border-bottom: 1px solid #e2e8f0;
  align-items: center;
}

.result-item:last-child {
  border-bottom: none;
}

.drug-name {
  font-weight: 500;
  color: #1e293b;
}

.drug-spec,
.drug-usage {
  color: #64748b;
  font-size: 13px;
}

.select-btn {
  padding: 6px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.select-btn:hover {
  background: #059669;
  transform: translateY(-1px);
}

/* 表格容器 */
.table-container {
  overflow-x: auto;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: white;
}

.data-table,
.prescription-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  color: #475569;
}

.data-table th,
.prescription-table th {
  padding: 16px 20px;
  font-weight: 600;
  text-align: left;
  color: #334155;
  white-space: nowrap;
  background: #f8fafc;
  border-bottom: 2px solid #e2e8f0;
}

.data-table td,
.prescription-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.data-table tbody tr:hover,
.prescription-table tbody tr:hover {
  background-color: #f8fafc;
}

.data-table tbody tr:last-child td,
.prescription-table tbody tr:last-child td {
  border-bottom: none;
}

/* 特殊单元格样式 */
.index-cell {
  text-align: center;
  font-weight: 600;
  color: #475569;
}

.diagnosis-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.table-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  color: #1e293b;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.table-input:focus {
  outline: none;
  border-color: #3b82f6;
}

.table-input.small {
  width: 80px;
}

.table-input.number {
  text-align: center;
}

.delete-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.delete-btn:hover {
  background: #fee2e2;
  border-color: #fca5a5;
  transform: translateY(-1px);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  color: #94a3b8;
}

.empty-state svg {
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

/* 底部按钮 */
.workbench-footer {
  background: white;
  border-radius: 16px;
  padding: 24px 32px;
  box-shadow: 
    0 1px 3px rgba(15, 23, 42, 0.05),
    0 10px 25px -5px rgba(15, 23, 42, 0.05);
  border: 1px solid #f1f5f9;
}

.footer-content {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

.footer-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.footer-btn.secondary {
  background: white;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.footer-btn.secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  transform: translateY(-1px);
}

.footer-btn.primary {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
}

.footer-btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .workbench-page {
    padding: 16px;
  }
  
  .workbench-main {
    padding: 20px;
  }
  
  .workbench-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .back-btn {
    align-self: stretch;
  }
  
  .header-info {
    flex-direction: column;
    gap: 8px;
  }
  
  .info-divider {
    display: none;
  }
  
  .search-box {
    flex-direction: column;
  }
  
  .search-input-wrapper {
    width: 100%;
  }
  
  .search-btn {
    width: 100%;
  }
  
  .footer-content {
    flex-direction: column;
  }
  
  .footer-btn {
    justify-content: center;
  }
  
  .results-header,
  .result-item {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .prescription-table th,
  .prescription-table td {
    padding: 12px;
  }
}
</style>