<template>
  <div class="patient-manager">
    <!-- 添加中央标题 -->
    <div class="header-section">
      <h2 class="main-title">就诊人管理</h2>
      <p class="tip">当前用户ID：{{ userId }}</p>
    </div>

    <!-- 新增 / 编辑 -->
    <section class="section">
      <h3>{{ editingId ? '编辑就诊人' : '新增就诊人' }}</h3>
      <form class="form" @submit.prevent="handleSubmit">
        <div class="form-row">
          <label>姓名：</label>
          <input 
            v-model="form.name" 
            required 
            @input="validateNameDuplicate"
          />
          <span v-if="nameError" class="validation-error">{{ nameError }}</span>
        </div>
        <div class="form-row">
          <label>证件类型：</label>
          <select v-model="form.idType" required @change="clearIdCardValidation" :disabled="editingId !== null">
            <option value="">请选择</option>
            <option value="身份证">身份证</option>
            <option value="护照">护照</option>
            <option value="医保卡">医保卡</option>
          </select>
        </div>
        <div class="form-row">
          <label>证件号码：</label>
          <!-- 编辑状态显示脱敏后的证件号，新增状态显示输入框 -->
          <div v-if="editingId" class="masked-idcard">
            {{ maskIdCard(form.idType, form.idCard) }}
            <span class="mask-tip">证件号码不可修改</span>
          </div>
          <input 
            v-else
            v-model="form.idCard" 
            :placeholder="getIdCardPlaceholder()"
            required 
            @input="validateIdCardOnInput"
          />
          <span v-if="idCardError && !editingId" class="validation-error">{{ idCardError }}</span>
        </div>
        <div class="form-row">
          <label>联系电话：</label>
          <input 
            v-model="form.phone" 
            placeholder="选填，填写请输入11位手机号码"
            @input="validatePhoneOnInput"
          />
          <span v-if="phoneError" class="validation-error">{{ phoneError }}</span>
        </div>
        <div class="form-actions">
          <button type="submit" :disabled="adding || !isFormValid">
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
            <!-- 列表中显示脱敏后的证件号 -->
            <td>{{ maskIdCard(p.idType, p.idCard) }}</td>
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
import { ref, onMounted, watch, computed } from 'vue'
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

// 验证错误信息
const idCardError = ref('')
const nameError = ref('') // 姓名重复错误
const phoneError = ref('') // 手机号格式错误

// 当前是否在编辑状态：null = 新增， 有值 = 编辑这一条
const editingId = ref(null)

const errorMessage = ref('')
const successMessage = ref('')

// 表单验证状态
const isFormValid = computed(() => {
  // 1. 基本必填项验证（姓名和证件类型必填）
  if (!form.value.name || !form.value.idType) {
    return false
  }
  
  // 2. 新增状态专属验证
  if (editingId.value === null) {
    // 证件号必填 + 格式验证
    if (!form.value.idCard || !validateIdCard()) {
      return false
    }
    // 姓名唯一性验证
    if (!validateNameDuplicate()) {
      return false
    }
  }
  
  // 3. 手机号格式验证（选填但填了就要符合规则）
  if (!validatePhone()) {
    return false
  }
  
  // 4. 编辑状态：无需验证证件号和姓名唯一性
  return true
})

// 证件号脱敏处理函数
function maskIdCard(idType, idCard) {
  if (!idCard) return '-'
  
  switch (idType) {
    case '身份证':
      // 18位身份证：前6位 + 8个星号 + 后4位
      if (idCard.length === 18) {
        return idCard.replace(/^(\d{6})\d+(\d{4})$/, '$1********$2')
      }
      // 非标准长度身份证也做脱敏处理
      return idCard.replace(/^(.{6}).+(.{4})$/, '$1********$2')
      
    case '护照':
      // 9位护照（E开头）：E + 1位 + 6个星号 + 后2位
      if (idCard.length === 9) {
        return idCard.replace(/^(E\d)\d+(\d{2})$/, '$1******$2')
      }
      // 非标准护照脱敏
      return idCard.replace(/^(.{2}).+(.{2})$/, '$1*****$2')
      
    case '医保卡':
      // 医保卡：前4位 + 8个星号 + 后4位
      return idCard.replace(/^(.{4}).+(.{4})$/, '$1********$2')
      
    default:
      // 通用脱敏规则：前2位 + 5个星号 + 后2位
      return idCard.replace(/^(.{2}).+(.{2})$/, '$1*****$2')
  }
}

// 根据证件类型返回提示文本
function getIdCardPlaceholder() {
  switch (form.value.idType) {
    case '身份证':
      return '请输入18位身份证号码（前17位数字，最后一位数字或大写字母）'
    case '护照':
      return '请输入9位护照号码（E开头，后跟8位数字）'
    case '医保卡':
      return '请输入医保卡号码'
    default:
      return '请输入证件号码'
  }
}

// 清除证件号码验证错误
function clearIdCardValidation() {
  idCardError.value = ''
}

// ========== 新增：姓名唯一性验证 ==========
function validateNameDuplicate() {
  // 编辑状态不验证（允许编辑时保留原名）
  if (editingId.value !== null) {
    nameError.value = ''
    return true
  }
  
  const name = form.value.name.trim()
  if (!name) {
    nameError.value = ''
    return false
  }
  
  // 检查是否已有同名就诊人
  const isDuplicate = patients.value.some(p => p.name === name)
  if (isDuplicate) {
    nameError.value = '该姓名已存在，请勿重复添加'
    return false
  }
  
  nameError.value = ''
  return true
}

// ========== 新增：手机号格式验证 ==========
function validatePhone() {
  const phone = form.value.phone.trim()
  // 空值直接通过（选填）
  if (!phone) {
    phoneError.value = ''
    return true
  }
  
  // 11位纯数字验证
  const phoneRegex = /^\d{11}$/
  if (!phoneRegex.test(phone)) {
    phoneError.value = '手机号格式错误：必须是11位数字'
    return false
  }
  
  phoneError.value = ''
  return true
}

// 实时验证证件号码（仅新增状态）
function validateIdCardOnInput() {
  if (editingId.value !== null) return // 编辑状态不验证
  if (!form.value.idCard || !form.value.idType) {
    idCardError.value = ''
    return
  }
  
  validateIdCard()
}

// 实时验证姓名唯一性
function validateNameDuplicateOnInput() {
  validateNameDuplicate()
}

// 实时验证手机号
function validatePhoneOnInput() {
  validatePhone()
}

// 验证证件号码格式（仅新增状态）
function validateIdCard() {
  if (editingId.value !== null) return true // 编辑状态跳过验证
  if (!form.value.idType) {
    idCardError.value = '请先选择证件类型'
    return false
  }
  
  const idCard = form.value.idCard.trim()
  
  switch (form.value.idType) {
    case '身份证':
      // 身份证验证：18位，前17位为数字，最后一位为数字或大写字母
      const idCardRegex = /^\d{17}[0-9A-Z]$/
      if (!idCardRegex.test(idCard)) {
        idCardError.value = '身份证格式错误：必须是18位，前17位为数字，最后一位为数字或大写字母'
        return false
      }
      idCardError.value = ''
      return true
      
    case '护照':
      // 护照验证：9位，E开头，后跟8位数字
      const passportRegex = /^E\d{8}$/
      if (!passportRegex.test(idCard)) {
        idCardError.value = '护照格式错误：必须是9位，以E开头，后跟8位数字'
        return false
      }
      idCardError.value = ''
      return true
      
    case '医保卡':
      // 医保卡验证：简单非空验证，可以根据实际需求调整
      if (!idCard) {
        idCardError.value = '医保卡号码不能为空'
        return false
      }
      idCardError.value = ''
      return true
      
    default:
      idCardError.value = '请选择有效的证件类型'
      return false
  }
}

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
  
  // 表单验证（computed已经处理，这里做二次确认）
  if (!isFormValid.value) {
    // 收集所有错误信息
    const errors = []
    if (nameError.value) errors.push(nameError.value)
    if (idCardError.value) errors.push(idCardError.value)
    if (phoneError.value) errors.push(phoneError.value)
    
    if (errors.length) {
      errorMessage.value = errors.join('；')
    } else {
      errorMessage.value = '请填写所有必填项并确保格式正确'
    }
    return
  }
  
  adding.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    if (editingId.value) {
      // 编辑模式：移除证件号和证件类型字段，避免修改
      const submitData = { ...form.value }
      delete submitData.idCard // 不传递证件号
      delete submitData.idType // 不传递证件类型
      
      const resp = await axios.put(
        `${BASE_URL}/api/patients/${editingId.value}`,
        submitData,
        { params: { userId: props.userId } }
      )
      successMessage.value = `已修改就诊人：${resp.data.name}（ID=${editingId.value}）`
      // 退出编辑模式
      editingId.value = null
      form.value = { name: '', idType: '', idCard: '', phone: '' }
      idCardError.value = ''
      nameError.value = ''
      phoneError.value = ''
      await loadPatients()
    } else {
      // 新增模式：正常提交所有字段
      const resp = await axios.post(
        `${BASE_URL}/api/patients`,
        form.value,
        { params: { userId: props.userId } }
      )
      successMessage.value = `已保存就诊人：${resp.data.name}`
      form.value = { name: '', idType: '', idCard: '', phone: '' }
      idCardError.value = ''
      nameError.value = ''
      phoneError.value = ''
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
  // 填充表单数据（证件号保留原始值但前端显示脱敏）
  form.value = {
    name: p.name || '',
    idType: p.idType || '',
    idCard: p.idCard || '', // 保留原始值但输入框禁用
    phone: p.phone || ''
  }
  errorMessage.value = ''
  successMessage.value = ''
  idCardError.value = ''
  nameError.value = ''
  phoneError.value = ''
}

// 取消编辑，回到新增状态
function cancelEdit() {
  editingId.value = null
  form.value = { name: '', idType: '', idCard: '', phone: '' }
  errorMessage.value = ''
  successMessage.value = ''
  idCardError.value = ''
  nameError.value = ''
  phoneError.value = ''
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
/* ========== 全局盒模型修正（避免padding撑开宽度） ========== */
* {
  box-sizing: border-box;
}

.patient-manager {
  background: transparent; /* 移除白色背景 */
}

/* ========== 中央标题区域 ========== */
.header-section {
  text-align: center; /* 文字居中 */
  margin: 0 auto 1.5rem auto; /* 居中且下方有间距 */
  padding: 1rem;
  width: 100%;
  background: rgba(240, 248, 255, 0.7); /* 透明淡蓝色背景 */
  border-radius: 12px;
  border: 1px solid rgba(77, 171, 247, 0.2);
  backdrop-filter: blur(5px);
  box-shadow: 0 4px 16px rgba(30, 111, 217, 0.08);
}

.main-title {
  font-size: 1.5rem; /* 稍微减小字体大小 */
  font-weight: 700;
  color: #1e6fd9; /* 蓝色标题 */
  margin: 0 0 0.5rem 0;
  text-align: center;
  background: linear-gradient(135deg, #1e6fd9 0%, #0d4ba0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.tip {
  font-size: 0.875rem;
  color: #64748b;
  margin-bottom: 0;
  text-align: center;
}

/* ========== 模块标题 ========== */
h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e6fd9; /* 蓝色标题 */
  margin: 0 0 1rem 0;
}

/* ========== 核心修改：section 背景改为透明蓝色 ========== */
.section {
  margin-top: 0;
  margin-bottom: 1.5rem;
  padding: 1.5rem;
  background: rgba(240, 248, 255, 0.7); /* 透明淡蓝色背景 */
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(30, 111, 217, 0.08);
  width: 100%;
  border: 1px solid rgba(77, 171, 247, 0.2);
  backdrop-filter: blur(5px);
}

.form {
  max-width: 520px;
  width: 100%;
}

.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 0.75rem;
  position: relative;
}

.form-row label {
  width: 90px;
  font-size: 0.875rem;
  color: #475569;
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
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.form-row input:focus,
.form-row select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 脱敏证件号显示样式（和输入框样式保持一致） */
.masked-idcard {
  flex: 1;
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  font-size: 0.875rem;
  color: #000;
  background-color: #f8fafc;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mask-tip {
  font-size: 0.7rem;
  color: #94a3b8;
  margin-left: 0.5rem;
  white-space: nowrap;
}

/* 验证错误样式（通用） */
.validation-error {
  position: absolute;
  left: 90px;
  bottom: -18px;
  font-size: 0.75rem;
  color: #ef4444;
  background: rgba(254, 242, 242, 0.9);
  padding: 2px 6px;
  border-radius: 4px;
  white-space: nowrap;
  z-index: 1;
}

.form-actions {
  margin-top: 1.5rem;
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
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.2);
}

button.primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

button.secondary {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
  color: #fff;
}

button.secondary:hover:not(:disabled) {
  background: linear-gradient(135deg, #64748b 0%, #475569 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(100, 116, 139, 0.2);
}

button.danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: #fff;
}

button.danger:hover:not(:disabled) {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

.refresh-btn {
  margin-bottom: 1rem;
  /* 新增：刷新按钮与section内边距对齐 */
  margin-left: 0;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.2);
}

.refresh-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669 0%, #047857 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

/* ========== 表格样式 ========== */
.table {
  width: 100%; /* 适配section宽度 */
  border-collapse: collapse;
  font-size: 0.875rem;
  margin-top: 1rem;
  /* 新增：表格内边距适配，避免内容偏移 */
  table-layout: fixed;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.table th,
.table td {
  border: 1px solid rgba(77, 171, 247, 0.1);
  padding: 0.75rem; /* 与输入框内边距一致 */
  text-align: left;
}

.table th {
  background: rgba(30, 111, 217, 0.08);
  color: #1e6fd9;
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
  background: rgba(254, 242, 242, 0.8);
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border-left: 4px solid #ef4444;
  /* 新增：提示框与表单左对齐 */
  width: 100%;
  max-width: 520px;
  backdrop-filter: blur(5px);
}

.success {
  margin-top: 0.75rem;
  color: #10b981;
  font-size: 0.875rem;
  background: rgba(240, 253, 244, 0.8);
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border-left: 4px solid #10b981;
  /* 新增：提示框与表单左对齐 */
  width: 100%;
  max-width: 520px;
  backdrop-filter: blur(5px);
}

.empty {
  font-size: 0.875rem;
  color: #94a3b8;
  text-align: center;
  padding: 2rem;
  /* 新增：空状态与表格宽度对齐 */
  width: 100%;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 8px;
  margin-top: 1rem;
}

.editing-tip {
  margin-top: 0.5rem;
  font-size: 0.75rem;
  color: #f59e0b;
  /* 新增：编辑提示与标签左对齐 */
  padding-left: 90px;
  background: rgba(254, 243, 199, 0.3);
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  display: inline-block;
}

/* ========== 新增：适配主内容区的外层容器（若需要） ========== */
:deep(.content) {
  /* 确保当前组件的内容与全局主内容区对齐 */
  padding: 0 !important;
  margin: 0 !important;
}

/* ========== 响应式调整 ========== */
@media (max-width: 768px) {
  .header-section {
    padding: 0.75rem;
  }
  
  .main-title {
    font-size: 1.25rem;
  }
  
  .section {
    padding: 1rem;
  }
  
  .form-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }
  
  .form-row label {
    width: 100%;
    margin-bottom: 0.25rem;
  }
  
  /* 移动端调整验证错误提示位置 */
  .validation-error {
    position: static;
    margin-top: 0.25rem;
    margin-bottom: 0.5rem;
    width: 100%;
  }
  
  .masked-idcard {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }
  
  .mask-tip {
    margin-left: 0;
  }
  
  .table {
    font-size: 0.8rem;
  }
  
  .table th,
  .table td {
    padding: 0.5rem;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  button {
    width: 100%;
    justify-content: center;
  }
  
  .editing-tip {
    padding-left: 0;
    display: block;
    text-align: center;
  }
}
</style>