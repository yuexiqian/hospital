<template>
  <div>
    <h2>就诊人管理</h2>
    <p class="tip">当前用户ID：{{ userId }}</p>

    <!-- 新增 / 编辑 -->
    <section class="section">
      <h3>新增就诊人</h3>
      <form class="form" @submit.prevent="handleSubmit">
        <div class="form-row">
          <label>姓名：</label>
          <input v-model="form.name" required />
        </div>
        <div class="form-row">
          <label>证件类型：</label>
          <select v-model="form.idType" required>
            <option value="">请选择</option>
            <option value="身份证">身份证</option>
            <option value="护照">护照</option>
            <option value="医保卡">医保卡</option>
          </select>
        </div>
        <div class="form-row">
          <label>证件号码：</label>
          <input v-model="form.idCard" required />
        </div>
        <div class="form-row">
          <label>联系电话：</label>
          <input v-model="form.phone" />
        </div>
        <div class="form-actions">
          <button type="submit" :disabled="adding">
            <!-- 根据是否在编辑状态切换文案 -->
            <span v-if="editingId">
              {{ adding ? '保存中...' : '保存修改' }}
            </span>
            <span v-else>
              {{ adding ? '提交中...' : '保存就诊人' }}
            </span>
          </button>

          <!-- 只有在编辑状态时出现"取消编辑" -->
          <button
            v-if="editingId"
            type="button"
            class="secondary"
            @click="cancelEdit"
          >
            取消编辑
          </button>
        </div>
        <p v-if="editingId" class="editing-tip">
          正在编辑：就诊人 ID = {{ editingId }}
        </p>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
        <p v-if="successMessage" class="success">{{ successMessage }}</p>
      </form>
    </section>

    <!-- 列表 -->
    <section class="section">
      <h3>就诊人列表</h3>
      <button class="refresh-btn" @click="loadPatients" :disabled="loading">
        {{ loading ? '加载中...' : '刷新列表' }}
      </button>

      <table v-if="patients.length" class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>姓名</th>
            <th>证件类型</th>
            <th>证件号码</th>
            <th>联系电话</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in patients" :key="p.patientId">
            <td>{{ p.patientId }}</td>
            <td>{{ p.name }}</td>
            <td>{{ p.idType }}</td>
            <td>{{ p.idCard }}</td>
            <td>{{ p.phone || '-' }}</td>
            <td>{{ p.createTime || '-' }}</td>
            <td>
              <button class="primary" @click="startEdit(p)">
                编辑
              </button>
              <button class="danger" @click="handleDelete(p.patientId)">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <p v-else class="empty">暂无就诊人，请先新增。</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const BASE_URL = 'http://localhost:8080'

const patients = ref([])
const loading = ref(false)
const adding = ref(false)

// 当前表单内容
const form = ref({
  name: '',
  idType: '',
  idCard: '',
  phone: ''
})

// 当前是否在编辑状态：null = 新增， 有值 = 编辑这一条
const editingId = ref(null)

const errorMessage = ref('')
const successMessage = ref('')

// 加载列表
async function loadPatients() {
  if (!props.userId) return
  loading.value = true
  errorMessage.value = ''
  try {
    const resp = await axios.get(`${BASE_URL}/api/patients`, {
      params: { userId: props.userId }
    })
    patients.value = resp.data
  } catch (e) {
    console.error(e)
    errorMessage.value = '加载就诊人列表失败'
  } finally {
    loading.value = false
  }
}

// 提交：新增 or 修改
async function handleSubmit() {
  if (!props.userId) {
    errorMessage.value = '当前用户ID为空，无法操作'
    return
  }
  adding.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    if (editingId.value) {
      // ⭐ 编辑模式：调用 PUT /api/patients/{id}?userId=...
      const resp = await axios.put(
        `${BASE_URL}/api/patients/${editingId.value}`,
        form.value,
        { params: { userId: props.userId } }
      )
      successMessage.value = `已修改就诊人：${resp.data.name}（ID=${editingId.value}）`
      // 退出编辑模式
      editingId.value = null
      form.value = { name: '', idType: '', idCard: '', phone: '' }
      await loadPatients()
    } else {
      // ⭐ 新增模式：调用 POST /api/patients?userId=...
      const resp = await axios.post(
        `${BASE_URL}/api/patients`,
        form.value,
        { params: { userId: props.userId } }
      )
      successMessage.value = `已保存就诊人：${resp.data.name}`
      form.value = { name: '', idType: '', idCard: '', phone: '' }
      await loadPatients()
    }
  } catch (e) {
    console.error(e)
    errorMessage.value = editingId.value
      ? '修改就诊人失败'
      : '新增就诊人失败'
  } finally {
    adding.value = false
  }
}

// 点击"编辑"按钮
function startEdit(p) {
  editingId.value = p.patientId
  // 把这一行数据填充到表单
  form.value = {
    name: p.name || '',
    idType: p.idType || '',
    idCard: p.idCard || '',
    phone: p.phone || ''
  }
  errorMessage.value = ''
  successMessage.value = ''
}

// 取消编辑，回到新增状态
function cancelEdit() {
  editingId.value = null
  form.value = { name: '', idType: '', idCard: '', phone: '' }
  errorMessage.value = ''
  successMessage.value = ''
}

// 删除
async function handleDelete(patientId) {
  if (!confirm(`确定删除就诊人 ID=${patientId} 吗？`)) return
  errorMessage.value = ''
  successMessage.value = ''
  try {
    await axios.delete(`${BASE_URL}/api/patients/${patientId}`, {
      params: { userId: props.userId }
    })
    successMessage.value = `已删除就诊人 ID=${patientId}`
    // 如果正在编辑的就是被删的那条，顺便退出编辑模式
    if (editingId.value === patientId) {
      cancelEdit()
    }
    await loadPatients()
  } catch (e) {
    console.error(e)
    errorMessage.value = '删除就诊人失败'
  }
}

// userId 变化或组件挂载时，自动加载列表
onMounted(loadPatients)
watch(
  () => props.userId,
  () => {
    loadPatients()
  }
)
</script>

<style scoped>
/* ========== 新增：全局盒模型修正（避免padding撑开宽度） ========== */
* {
  box-sizing: border-box;
}

h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.5rem 0;
}

h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 1rem 0;
}

.tip {
  font-size: 0.875rem;
  color: #64748b;
  margin-bottom: 1rem;
}

/* ========== 核心修改：section 与侧边栏/顶部栏对齐 ========== */
.section {
  margin-top: 0; /* 移除顶部间距，与主内容区顶部对齐 */
  margin-bottom: 1.5rem; /* 保持模块间间距，与全局风格统一 */
  padding: 1.5rem; /* 内边距与侧边栏内边距（1.5rem）一致 */
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  /* 新增：宽度适配主内容区，避免超出 */
  width: 100%;
}

.form {
  max-width: 520px;
  /* 新增：表单居中/对齐，避免左偏移 */
  width: 100%;
}

.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 0.75rem;
}

.form-row label {
  width: 90px;
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
}

/* 关键修改：把输入框/选择框改成白框黑字（保留） */
.form-row input,
.form-row select {
  flex: 1;
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0; /* 边框保持浅灰 */
  font-size: 0.875rem;
  color: #000; /* 字体改成黑色 */
  background-color: #fff; /* 背景改成白色 */
  transition: border-color 0.2s ease;
}

.form-row input:focus,
.form-row select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-actions {
  margin-top: 1rem;
  display: flex;
  gap: 0.75rem;
  align-items: center;
  /* 新增：按钮组与输入框左对齐 */
  padding-left: 0;
}

button {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;
}

button.primary {
  background-color: #3b82f6;
  color: #fff;
}

button.primary:hover {
  background-color: #2563eb;
  transform: translateY(-1px);
}

button.secondary {
  background-color: #94a3b8;
  color: #fff;
}

button.secondary:hover {
  background-color: #64748b;
  transform: translateY(-1px);
}

button.danger {
  background-color: #ef4444;
  color: #fff;
}

button.danger:hover {
  background-color: #dc2626;
  transform: translateY(-1px);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.refresh-btn {
  margin-bottom: 1rem;
  /* 新增：刷新按钮与section内边距对齐 */
  margin-left: 0;
  background-color: #10b981;
  color: #fff;
}

.refresh-btn:hover:not(:disabled) {
  background-color: #059669;
  transform: translateY(-1px);
}

/* ========== 核心修改：表格与侧边栏/表单对齐 ========== */
.table {
  width: 100%; /* 适配section宽度 */
  border-collapse: collapse;
  font-size: 0.875rem;
  margin-top: 1rem;
  /* 新增：表格内边距适配，避免内容偏移 */
  table-layout: fixed;
}

.table th,
.table td {
  border: 1px solid #f1f5f9;
  padding: 0.75rem; /* 与输入框内边距一致 */
  text-align: left;
}

.table th {
  background-color: #f8fafc;
  color: #475569;
  font-weight: 600;
}

.table td {
  color: #334155;
}

.table button {
  padding: 0.375rem 0.75rem;
  font-size: 0.8rem;
  margin-right: 0.5rem;
}

.error {
  margin-top: 0.75rem;
  color: #ef4444;
  font-size: 0.875rem;
  background: #fef2f2;
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border-left: 4px solid #ef4444;
  /* 新增：提示框与表单左对齐 */
  width: 100%;
  max-width: 520px;
}

.success {
  margin-top: 0.75rem;
  color: #10b981;
  font-size: 0.875rem;
  background: #f0fdf4;
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border-left: 4px solid #10b981;
  /* 新增：提示框与表单左对齐 */
  width: 100%;
  max-width: 520px;
}

.empty {
  font-size: 0.875rem;
  color: #94a3b8;
  text-align: center;
  padding: 2rem;
  /* 新增：空状态与表格宽度对齐 */
  width: 100%;
}

.editing-tip {
  margin-top: 0.5rem;
  font-size: 0.75rem;
  color: #f59e0b;
  /* 新增：编辑提示与标签左对齐 */
  padding-left: 90px;
}

/* ========== 新增：适配主内容区的外层容器（若需要） ========== */
:deep(.content) {
  /* 确保当前组件的内容与全局主内容区对齐 */
  padding: 0 !important;
  margin: 0 !important;
}
</style>