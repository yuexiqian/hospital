<!-- src/views/doctor/DoctorWorkbenchPage.vue -->
<template>
  <div class="page">
    <div class="header">
      <div>
        <h2 class="title">就诊工作台</h2>
        <p class="sub">
          挂号号：{{ detail.registerId }}
          · 日期：{{ detail.registerDate || '-' }} {{ detail.registerTime || '' }}
        </p>
      </div>
      <button class="btn" @click="goBack">返回队列</button>
    </div>

    <!-- 患者基本信息 -->
    <section class="card">
      <h3>患者信息</h3>
      <div class="info-grid">
        <div><label>姓名：</label><span>{{ detail.patientName || '-' }}</span></div>
        <div><label>身份证：</label><span>{{ detail.idCard || '-' }}</span></div>
        <div><label>电话：</label><span>{{ detail.phone || '-' }}</span></div>
        <div><label>科室：</label><span>{{ detail.deptName || '-' }}</span></div>
        <div><label>医生：</label><span>{{ detail.doctorName || '-' }}</span></div>
        <div><label>状态：</label><span>{{ detail.visitStatus || '-' }}</span></div>
      </div>
    </section>

    <!-- 病历 -->
    <section class="card">
      <h3>病历记录</h3>
      <div class="form-row">
        <label>诊断：</label>
        <input v-model="form.diag" class="input" placeholder="请输入诊断" />
      </div>
      <div class="form-row">
        <label>医生备注：</label>
        <textarea
          v-model="form.remark"
          class="textarea"
          rows="3"
          placeholder="补充一些就诊建议、注意事项等"
        />
      </div>
    </section>

    <!-- 处方编辑 -->
    <section class="card">
      <div class="card-header">
        <h3>处方明细</h3>
        <div class="drug-search">
          <input
            v-model="drugKeyword"
            class="input"
            placeholder="输入药品名/通用名关键字"
            @keyup.enter="doSearchDrug"
          />
          <button class="btn" @click="doSearchDrug">搜索药品</button>
        </div>
      </div>

      <!-- 搜索结果列表 -->
      <div v-if="drugList.length" class="drug-result">
        <div class="drug-row header">
          <span>药品名</span>
          <span>规格</span>
          <span>推荐用法</span>
          <span style="width: 80px">操作</span>
        </div>
        <div
          v-for="item in drugList"
          :key="item.drugId"
          class="drug-row"
        >
          <span>{{ item.drugName }}</span>
          <span>{{ item.spec || '-' }}</span>
          <span>{{ item.dosageUsage || '-' }}</span>
          <span>
            <button class="btn small" @click="addDrugFromSearch(item)">选择</button>
          </span>
        </div>
      </div>

      <!-- 处方明细表 -->
      <table class="table">
        <thead>
          <tr>
            <th style="width: 40px">序</th>
            <th>药品名称</th>
            <th>剂量 / 用法</th>
            <th style="width: 80px">频次</th>
            <th style="width: 60px">天数</th>
            <th style="width: 60px">数量</th>
            <th>备注</th>
            <th style="width: 70px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="items.length === 0">
            <td colspan="8" class="center">暂无处方明细，可通过上方搜索选择药品</td>
          </tr>
          <tr v-for="(row, index) in items" :key="row._key">
            <td>{{ index + 1 }}</td>
            <td>
              <input v-model="row.drugName" class="input small" placeholder="药品名称" />
            </td>
            <td>
              <input v-model="row.dosage" class="input small" placeholder="用法/剂量" />
            </td>
            <td>
              <input v-model="row.frequency" class="input small" placeholder="如 bid/tid" />
            </td>
            <td>
              <input v-model.number="row.days" type="number" min="1" class="input small" />
            </td>
            <td>
              <input v-model.number="row.quantity" type="number" min="1" class="input small" />
            </td>
            <td>
              <input v-model="row.remark" class="input small" placeholder="备注" />
            </td>
            <td>
              <button class="btn danger small" @click="removeRow(index)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 患者历史记录 -->
    <section class="card">
      <h3>患者历史就诊记录</h3>
      <table class="table">
        <thead>
          <tr>
            <th style="width: 60px">序</th>
            <th>就诊时间</th>
            <th>科室</th>
            <th>医生</th>
            <th>诊断</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="history.length === 0">
            <td colspan="5" class="center">暂无历史记录</td>
          </tr>
          <tr v-for="(h, idx) in history" :key="h.registerId">
            <td>{{ idx + 1 }}</td>
            <td>{{ h.visitTime || '-' }}</td>
            <td>{{ h.deptName || '-' }}</td>
            <td>{{ h.doctorName || '-' }}</td>
            <td>{{ h.diag || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 底部操作按钮 -->
    <div class="footer">
      <button class="btn" @click="handleSave(false)">保存草稿</button>
      <button class="btn primary" @click="handleSave(true)">提交处方并结束就诊</button>
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
.page {
  padding-bottom: 60px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.title {
  margin: 0;
  font-size: 18px;
}

.sub {
  margin: 2px 0 0;
  font-size: 12px;
  color: #909399;
}

.card {
  background: #fff;
  border-radius: 8px;
  padding: 12px 16px 14px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 4px 16px;
  font-size: 13px;
}

.info-grid label {
  color: #909399;
  margin-right: 4px;
}

.form-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
  font-size: 13px;
}

.form-row label {
  width: 80px;
  padding-top: 4px;
  color: #606266;
}

.input,
.textarea {
  flex: 1;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  font-size: 13px;
}

.input.small {
  width: 100%;
  box-sizing: border-box;
}

.textarea {
  resize: vertical;
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.table th,
.table td {
  border: 1px solid #ebeef5;
  padding: 4px 6px;
}

.table th {
  background: #f5f7fa;
  text-align: left;
}

.center {
  text-align: center;
  color: #909399;
}

.footer {
  position: sticky;
  bottom: 0;
  margin-top: 8px;
  padding: 8px 0;
  text-align: right;
  background: #f3f4f6;
}

.btn {
  padding: 5px 12px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
  margin-left: 6px;
}

.btn.primary {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
}

.btn.danger {
  background: #f56c6c;
  border-color: #f56c6c;
  color: #fff;
}

.btn.small {
  padding: 2px 8px;
  font-size: 12px;
}

.drug-search {
  display: flex;
  gap: 6px;
  align-items: center;
}

.drug-result {
  margin-bottom: 8px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  font-size: 12px;
}

.drug-row {
  display: grid;
  grid-template-columns: 2fr 2fr 3fr 1fr;
  padding: 4px 8px;
  border-bottom: 1px solid #ebeef5;
}

.drug-row.header {
  background: #f5f7fa;
  font-weight: 600;
}

.drug-row:last-child {
  border-bottom: none;
}
</style>
