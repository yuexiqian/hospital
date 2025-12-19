<template>
  <div class="pharm-home">
    <!-- 标题栏 -->
    <header class="pharm-header">
      <div class="header-content">
        <h2>药师端 · 药品知识库管理</h2>
        <div class="header-subtitle">检索、查看并维护药品说明书信息</div>
      </div>
    </header>

    <!-- 搜索区域 -->
    <section class="search-section">
      <div class="search-container">
        <div class="search-group">
          <label class="search-label">关键字</label>
          <input
            v-model="keyword"
            class="search-input"
            placeholder="输入药品名 / 通用名 / 适应症 关键字"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
          <button class="reset-btn" @click="handleReset">重置</button>
        </div>
        <button class="new-drug-btn" @click="handleNewDrug">新建药品</button>
      </div>
    </section>

    <!-- 主内容区域 -->
    <section class="main-content">
      <!-- 左侧：药品列表 -->
      <div class="left-panel">
        <div class="panel-header">
          <h3>药品列表（{{ drugList.length }}）</h3>
        </div>
        <div class="table-container">
          <table class="drug-table">
            <thead>
              <tr>
                <th style="width: 60px">ID</th>
                <th>药品名</th>
                <th>通用名</th>
                <th>剂型</th>
                <th>规格</th>
                <th>分类</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="item in drugList"
                :key="item.id"
                :class="{ active: item.id === activeId }"
                @click="selectDrug(item)"
              >
                <td>{{ item.id }}</td>
                <td class="cell-ellipsis" :title="item.drugName">{{ item.drugName }}</td>
                <td class="cell-ellipsis" :title="item.genericName">{{ item.genericName }}</td>
                <td>{{ item.dosageForm }}</td>
                <td class="cell-ellipsis" :title="item.spec">{{ item.spec }}</td>
                <td>{{ item.category }}</td>
              </tr>
              <tr v-if="drugList.length === 0">
                <td colspan="6" class="empty-cell">
                  <div class="empty-state">
                    <div class="empty-icon">📋</div>
                    <div>暂无药品数据</div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 右侧：药品详情/编辑 -->
      <div class="right-panel">
        <div class="panel-header">
          <div class="panel-title">
            <h3>药品详情 / 编辑</h3>
            <span v-if="isNew" class="new-tag">新建</span>
          </div>
          <div v-if="!isNew" class="panel-subtitle">
            ID: {{ activeId || '-' }}
          </div>
        </div>

        <div class="form-container">
          <!-- 基础信息 -->
          <div class="form-section">
            <h4>基础信息</h4>
            <div class="form-grid">
              <div class="form-group">
                <label>药品名</label>
                <input v-model="form.drugName" placeholder="请输入药品名" />
              </div>
              <div class="form-group">
                <label>通用名</label>
                <input v-model="form.genericName" placeholder="请输入通用名" />
              </div>
              <div class="form-group">
                <label>英文名</label>
                <input v-model="form.englishName" placeholder="请输入英文名" />
              </div>
              <div class="form-group">
                <label>分类</label>
                <input v-model="form.category" placeholder="请输入药品分类" />
              </div>
              <div class="form-group">
                <label>剂型</label>
                <input v-model="form.dosageForm" placeholder="请输入剂型" />
              </div>
              <div class="form-group">
                <label>规格</label>
                <input v-model="form.spec" placeholder="请输入规格" />
              </div>
            </div>
          </div>

          <!-- 药品信息 -->
          <div class="form-section">
            <h4>药品信息</h4>
            <div class="form-grid full">
              <div class="form-group full">
                <label>适应症</label>
                <textarea v-model="form.indications" rows="3" placeholder="请输入适应症" />
              </div>
              <div class="form-group full">
                <label>用法用量</label>
                <textarea v-model="form.dosageUsage" rows="3" placeholder="请输入用法用量" />
              </div>
              <div class="form-group full">
                <label>不良反应</label>
                <textarea v-model="form.adverseReaction" rows="3" placeholder="请输入不良反应" />
              </div>
              <div class="form-group full">
                <label>禁忌</label>
                <textarea v-model="form.contraindication" rows="3" placeholder="请输入禁忌" />
              </div>
              <div class="form-group full">
                <label>注意事项</label>
                <textarea v-model="form.precautions" rows="3" placeholder="请输入注意事项" />
              </div>
              <div class="form-group full">
                <label>药物相互作用</label>
                <textarea v-model="form.interactions" rows="3" placeholder="请输入药物相互作用" />
              </div>
              <div class="form-group full">
                <label>贮藏条件</label>
                <textarea v-model="form.storage" rows="2" placeholder="请输入贮藏条件" />
              </div>
              <div class="form-group full">
                <label>参考来源</label>
                <textarea v-model="form.reference" rows="2" placeholder="请输入参考来源" />
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="form-actions">
            <div v-if="message" class="message" :class="{ error: isError }">
              {{ message }}
            </div>
            <div class="action-buttons">
              <button class="cancel-btn" @click="handleCancelEdit">取消</button>
              <button class="save-btn" @click="handleSave">保存</button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  getDrugInfoList,
  getDrugInfoDetail,
  updateDrugInfo,
  createDrugInfo
} from '../../api/pharmacist'

const currentUser = ref(null)
const drugList = ref([])
const activeId = ref(null)
const isNew = ref(false)
const keyword = ref('')

// 表单数据
const emptyForm = () => ({
  drugName: '',
  genericName: '',
  englishName: '',
  category: '',
  dosageForm: '',
  spec: '',
  indications: '',
  dosageUsage: '',
  adverseReaction: '',
  contraindication: '',
  precautions: '',
  interactions: '',
  storage: '',
  reference: ''
})

const form = reactive(emptyForm())
const message = ref('')
const isError = ref(false)

// 加载药品列表
const loadList = async () => {
  try {
    const resp = await getDrugInfoList(keyword.value || '')
    if (resp.data.code === 0) {
      drugList.value = resp.data.data || []
    } else {
      drugList.value = []
      isError.value = true
      message.value = resp.data.message || '加载药品列表失败'
    }
  } catch (e) {
    console.error(e)
    isError.value = true
    message.value = '请求失败，无法加载药品列表'
  }
}

// 加载药品详情
const loadDetail = async (id) => {
  try {
    const resp = await getDrugInfoDetail(id)
    if (resp.data.code === 0 && resp.data.data) {
      const d = resp.data.data
      Object.assign(form, emptyForm(), d)
    } else {
      isError.value = true
      message.value = resp.data.message || '加载药品详情失败'
    }
  } catch (e) {
    console.error(e)
    isError.value = true
    message.value = '请求失败，无法加载药品详情'
  }
}

// 选择药品
const selectDrug = async (item) => {
  isNew.value = false
  activeId.value = item.id
  message.value = ''
  isError.value = false
  await loadDetail(item.id)
}

// 搜索
const handleSearch = async () => {
  await loadList()
  if (drugList.value.length > 0) {
    await selectDrug(drugList.value[0])
  } else {
    activeId.value = null
    Object.assign(form, emptyForm())
  }
}

// 重置
const handleReset = async () => {
  keyword.value = ''
  await handleSearch()
}

// 新建药品
const handleNewDrug = () => {
  isNew.value = true
  activeId.value = null
  message.value = ''
  isError.value = false
  Object.assign(form, emptyForm())
}

// 取消编辑
const handleCancelEdit = async () => {
  message.value = ''
  isError.value = false
  if (isNew.value) {
    Object.assign(form, emptyForm())
  } else if (activeId.value) {
    await loadDetail(activeId.value)
  }
}

// 保存
const handleSave = async () => {
  message.value = ''
  isError.value = false

  if (!form.drugName || !form.genericName) {
    isError.value = true
    message.value = '请至少填写【药品名】和【通用名】'
    return
  }

  try {
    if (isNew.value) {
      const resp = await createDrugInfo(form)
      if (resp.data.code === 0 && resp.data.data) {
        const created = resp.data.data
        message.value = '新建成功'
        isError.value = false
        isNew.value = false
        await loadList()
        if (created.id) {
          const match = drugList.value.find(d => d.id === created.id)
          if (match) {
            await selectDrug(match)
          }
        }
      } else {
        isError.value = true
        message.value = resp.data.message || '新建失败'
      }
    } else {
      if (!activeId.value) {
        isError.value = true
        message.value = '请选择要编辑的药品'
        return
      }
      const resp = await updateDrugInfo(activeId.value, form)
      if (resp.data.code === 0) {
        message.value = '保存成功'
        isError.value = false
        await loadList()
        const match = drugList.value.find(d => d.id === activeId.value)
        if (match) {
          await selectDrug(match)
        }
      } else {
        isError.value = true
        message.value = resp.data.message || '保存失败'
      }
    }
  } catch (e) {
    console.error(e)
    isError.value = true
    message.value = '请求失败，保存未成功'
  }
}

onMounted(async () => {
  const saved = localStorage.getItem('currentUser')
  if (saved) {
    try {
      currentUser.value = JSON.parse(saved)
    } catch (e) {
      console.error(e)
    }
  }
  await handleSearch()
})
</script>

<style scoped>
.pharm-home {
  min-height: 100%;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

/* 标题栏 */
.pharm-header {
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

/* 搜索区域 */
.search-section {
  margin-bottom: 24px;
}

.search-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.search-group {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.search-label {
  font-size: 14px;
  color: #374151;
  white-space: nowrap;
}

.search-input {
  flex: 1;
  padding: 10px 16px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  font-size: 14px;
  outline: none;
  transition: all 0.2s ease;
}

.search-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-btn,
.reset-btn,
.new-drug-btn {
  padding: 10px 20px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
}

.search-btn {
  background: #3b82f6;
  color: white;
}

.search-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.reset-btn {
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
}

.reset-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.new-drug-btn {
  background: #10b981;
  color: white;
}

.new-drug-btn:hover {
  background: #059669;
  transform: translateY(-1px);
}

/* 主内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 24px;
}

.left-panel,
.right-panel {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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

.new-tag {
  display: inline-block;
  padding: 2px 8px;
  background: #f97316;
  color: white;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
}

.panel-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

/* 表格区域 */
.table-container {
  max-height: calc(100vh - 350px);
  overflow-y: auto;
}

.drug-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.drug-table th,
.drug-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.drug-table th {
  background: #f9fafb;
  color: #374151;
  font-weight: 600;
  position: sticky;
  top: 0;
  z-index: 1;
}

.drug-table tbody tr {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.drug-table tbody tr:hover {
  background-color: #f3f4f6;
}

.drug-table tbody tr.active {
  background-color: #eff6ff;
}

.cell-ellipsis {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-cell {
  text-align: center;
  padding: 48px 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #9ca3af;
}

.empty-icon {
  font-size: 32px;
  opacity: 0.5;
}

/* 表单区域 */
.form-container {
  padding: 20px;
  max-height: calc(100vh - 350px);
  overflow-y: auto;
}

.form-section {
  margin-bottom: 32px;
}

.form-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e5e7eb;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-grid.full {
  grid-template-columns: 1fr;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group.full {
  grid-column: 1 / -1;
}

.form-group label {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

/* 操作按钮 */
.form-actions {
  padding: 20px 0;
  border-top: 1px solid #e5e7eb;
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

.action-buttons {
  display: flex;
  gap: 12px;
}

.cancel-btn,
.save-btn {
  padding: 10px 24px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
}

.cancel-btn {
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
}

.cancel-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.save-btn {
  background: #3b82f6;
  color: white;
}

.save-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .table-container {
    max-height: 400px;
  }
  
  .form-container {
    max-height: none;
  }
}

@media (max-width: 768px) {
  .search-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-group {
    flex-direction: column;
    align-items: stretch;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .pharm-header h2 {
    font-size: 24px;
  }
  
  .header-subtitle {
    font-size: 14px;
  }
}
</style>