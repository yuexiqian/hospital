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

          <!-- 只有在编辑状态时出现“取消编辑” -->
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

// 点击“编辑”按钮
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
.tip {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}
.section {
  margin-top: 12px;
  padding: 12px 14px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
}
.form {
  max-width: 520px;
}
.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.form-row label {
  width: 90px;
  font-size: 14px;
}
.form-row input,
.form-row select {
  flex: 1;
  padding: 6px 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 14px;
}
.form-actions {
  margin-top: 8px;
  display: flex;
  gap: 8px;
  align-items: center;
}
button {
  padding: 6px 12px;
  font-size: 14px;
  border-radius: 6px;
  border: none;
  background-color: #409eff;
  color: #fff;
  cursor: pointer;
}
button.primary {
  background-color: #409eff;
}
button.secondary {
  background-color: #909399;
}
button.danger {
  background-color: #f56c6c;
}
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.refresh-btn {
  margin-bottom: 8px;
}
.table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 8px;
  font-size: 14px;
}
.table th,
.table td {
  border: 1px solid #eee;
  padding: 6px 8px;
}
.table th {
  background-color: #f5f7fa;
}
.error {
  margin-top: 6px;
  color: #e53935;
  font-size: 13px;
}
.success {
  margin-top: 6px;
  color: #43a047;
  font-size: 13px;
}
.empty {
  font-size: 13px;
  color: #777;
}
.editing-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #e6a23c;
}
</style>
