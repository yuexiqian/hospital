<!-- src/views/pharmacist/PharmacistHome.vue -->
<template>
  <div class="pharm-layout">
    <!-- 左侧侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo-circle">药</div>
        <div class="sidebar-title">
          <div class="main">药师端</div>
          <div class="sub">Pharmacist Console</div>
        </div>
      </div>

      <nav class="sidebar-menu">
        <!-- 当前页面：药品知识库 -->
        <button class="menu-item active">
          <span class="menu-dot"></span>
          <span>药品知识库</span>
        </button>

        <!-- 跳转到库存管理页面 -->
        <button class="menu-item" @click="goInventory">
          <span class="menu-dot"></span>
          <span>库存管理</span>
        </button>
      </nav>
    </aside>

    <!-- 右侧主内容 -->
    <div class="pharm-page">
      <!-- 顶部标题栏 -->
      <header class="pharm-header">
        <div class="title-block">
          <h1>药师端 · 药品知识库管理</h1>
          <p class="sub-title">检索、查看并维护药品说明书信息</p>
        </div>
        <div class="user-block">
          <span class="current-user">当前药师：{{ currentUser?.loginName || '-' }}</span>
          <button class="link-btn" @click="handleLogout">退出登录</button>
        </div>
      </header>

      <!-- 搜索区 -->
      <section class="search-card">
        <div class="search-row">
          <label class="search-label">关键字</label>
          <input
            v-model="keyword"
            class="search-input"
            placeholder="输入药品名 / 通用名 / 适应症 关键字后回车或点击搜索"
            @keyup.enter="handleSearch"
          />
          <button class="primary-btn" @click="handleSearch">搜索</button>
          <button class="ghost-btn" @click="handleReset">重置</button>

          <!-- 新建药品按钮 -->
          <button class="secondary-btn" style="margin-left:auto" @click="handleNewDrug">
            新建药品
          </button>
        </div>
      </section>

      <!-- 主体两列布局 -->
      <section class="main-grid">
        <!-- 左侧：药品列表 -->
        <div class="left-card">
          <div class="card-header">
            <span>药品列表（{{ drugList.length }}）</span>
          </div>
          <div class="table-wrapper">
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
                  <td colspan="6" class="empty-cell">暂无药品数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 右侧：详情 / 编辑 -->
        <div class="right-card">
          <div class="card-header flex-between">
            <div>
              <span>药品详情 / 编辑</span>
              <span v-if="isNew" class="tag-new">新建</span>
            </div>
            <div v-if="!isNew">
              <span class="small-text">ID: {{ activeId || '-' }}</span>
            </div>
          </div>

          <div class="form-grid">
            <div class="form-row">
              <div class="form-item">
                <label>药品名</label>
                <input v-model="form.drugName" />
              </div>
              <div class="form-item">
                <label>通用名</label>
                <input v-model="form.genericName" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-item">
                <label>英文名</label>
                <input v-model="form.englishName" />
              </div>
              <div class="form-item">
                <label>分类</label>
                <input v-model="form.category" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-item">
                <label>剂型</label>
                <input v-model="form.dosageForm" />
              </div>
              <div class="form-item">
                <label>规格</label>
                <input v-model="form.spec" />
              </div>
            </div>

            <div class="form-row full">
              <div class="form-item full">
                <label>适应症</label>
                <textarea v-model="form.indications" rows="3" />
              </div>
            </div>

            <div class="form-row full">
              <div class="form-item full">
                <label>用法用量</label>
                <textarea v-model="form.dosageUsage" rows="3" />
              </div>
            </div>

            <div class="form-row full">
              <div class="form-item full">
                <label>不良反应</label>
                <textarea v-model="form.adverseReaction" rows="3" />
              </div>
            </div>

            <div class="form-row full">
              <div class="form-item full">
                <label>禁忌</label>
                <textarea v-model="form.contraindication" rows="3" />
              </div>
            </div>

            <div class="form-row full">
              <div class="form-item full">
                <label>注意事项</label>
                <textarea v-model="form.precautions" rows="3" />
              </div>
            </div>

            <div class="form-row full">
              <div class="form-item full">
                <label>药物相互作用</label>
                <textarea v-model="form.interactions" rows="3" />
              </div>
            </div>

            <div class="form-row full">
              <div class="form-item full">
                <label>贮藏条件</label>
                <textarea v-model="form.storage" rows="2" />
              </div>
            </div>

            <div class="form-row full">
              <div class="form-item full">
                <label>参考来源</label>
                <textarea v-model="form.reference" rows="2" />
              </div>
            </div>

            <div class="form-actions">
              <span class="msg" :class="{ error: isError }" v-if="message">
                {{ message }}
              </span>
              <div class="btn-group">
                <button class="ghost-btn" @click="handleCancelEdit">取消</button>
                <button class="primary-btn" @click="handleSave">保存</button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getDrugInfoList,
  getDrugInfoDetail,
  updateDrugInfo,
  createDrugInfo
} from '../../api/pharmacist'

const router = useRouter()

// 当前登录用户
const currentUser = ref(null)

// 列表相关
const drugList = ref([])
const activeId = ref(null)

// 是否是“新建模式”
const isNew = ref(false)

// 搜索关键字
const keyword = ref('')

// 表单
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

// 消息
const message = ref('')
const isError = ref(false)

// 加载列表
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

// 加载详情
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

// 点击列表行
const selectDrug = async (item) => {
  isNew.value = false
  activeId.value = item.id
  message.value = ''
  isError.value = false
  await loadDetail(item.id)
}

// 搜索 / 重置
const handleSearch = async () => {
  await loadList()
  // 搜完默认选第一条
  if (drugList.value.length > 0) {
    await selectDrug(drugList.value[0])
  } else {
    activeId.value = null
    Object.assign(form, emptyForm())
  }
}

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

// 取消编辑：如果是新建就清空，如果是编辑就重新加载当前
const handleCancelEdit = async () => {
  message.value = ''
  isError.value = false
  if (isNew.value) {
    Object.assign(form, emptyForm())
  } else if (activeId.value) {
    await loadDetail(activeId.value)
  }
}

// 保存（新建 / 更新）
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
      // 新建
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
      // 更新
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

// 跳转库存管理
const goInventory = () => {
  router.push('/pharmacist/inventory')
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('currentUser')
  router.push('/login')
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
/* 整体布局：左侧侧边栏 + 右侧内容 */
.pharm-layout {
  display: flex;
  min-height: 100vh;
  background: #e5e7eb;
}

/* 侧边栏 */
.sidebar {
  width: 220px;
  background: #0f172a;
  color: #e5e7eb;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 24px;
}

.logo-circle {
  width: 36px;
  height: 36px;
  border-radius: 999px;
  background: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-title .main {
  font-size: 16px;
  font-weight: 600;
}

.sidebar-title .sub {
  font-size: 11px;
  color: #9ca3af;
}

.sidebar-menu {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 10px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: #e5e7eb;
  font-size: 13px;
  text-align: left;
  cursor: pointer;
}

.menu-item:hover {
  background: rgba(148, 163, 184, 0.25);
}

.menu-item.active {
  background: #1d4ed8;
}

.menu-dot {
  width: 6px;
  height: 6px;
  border-radius: 999px;
  background: #93c5fd;
}

/* 右侧主页面 */
.pharm-page {
  flex: 1;
  padding: 32px 40px 40px;
  background: #f3f4f6;
  box-sizing: border-box;
}

.pharm-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 20px;
}

.title-block h1 {
  font-size: 22px;
  color: #111827;
  margin: 0 0 4px;
}

.sub-title {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}

.user-block {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}

.current-user {
  color: #4b5563;
}

.link-btn {
  border: none;
  background: transparent;
  color: #2563eb;
  cursor: pointer;
  font-size: 13px;
}

/* 搜索区 */
.search-card {
  background: #fff;
  border-radius: 16px;
  padding: 14px 18px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
  margin-bottom: 18px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-label {
  font-size: 14px;
  color: #374151;
}

.search-input {
  flex: 1;
  padding: 8px 10px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.2);
}

.primary-btn,
.secondary-btn,
.ghost-btn {
  padding: 8px 18px;
  border-radius: 999px;
  font-size: 14px;
  border: none;
  cursor: pointer;
}

.primary-btn {
  background: #2563eb;
  color: #fff;
}

.primary-btn:hover {
  background: #1d4ed8;
}

.secondary-btn {
  background: #eef2ff;
  color: #3730a3;
}

.ghost-btn {
  background: #fff;
  color: #374151;
  border: 1px solid #d1d5db;
}

/* 主体两列 */
.main-grid {
  display: grid;
  grid-template-columns: 1.2fr 2fr;
  gap: 18px;
}

.left-card,
.right-card {
  background: #fff;
  border-radius: 16px;
  padding: 14px 16px;
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
}

.card-header {
  font-size: 14px;
  color: #111827;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.flex-between {
  justify-content: space-between;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
}

.drug-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.drug-table th,
.drug-table td {
  padding: 6px 8px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
  white-space: nowrap;
}

.drug-table thead th {
  background: #f9fafb;
  color: #6b7280;
  position: sticky;
  top: 0;
  z-index: 1;
}

.drug-table tbody tr {
  cursor: pointer;
}

.drug-table tbody tr:hover {
  background: #eef2ff;
}

.drug-table tbody tr.active {
  background: #e0ecff;
}

.cell-ellipsis {
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty-cell {
  text-align: center;
  color: #9ca3af;
  padding: 16px 0;
}

/* 表单区 */
.form-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-row {
  display: flex;
  gap: 12px;
}

.form-row.full {
  flex-direction: column;
}

.form-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-item.full {
  width: 100%;
}

.form-item label {
  font-size: 13px;
  color: #4b5563;
}

.form-item input,
.form-item textarea {
  font-size: 13px;
  border-radius: 10px;
  border: 1px solid #d1d5db;
  padding: 7px 9px;
  resize: vertical;
  outline: none;
}

.form-item input:focus,
.form-item textarea:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.18);
}

.form-actions {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.msg {
  font-size: 13px;
  color: #16a34a;
}

.msg.error {
  color: #dc2626;
}

.btn-group {
  display: flex;
  gap: 8px;
}

.small-text {
  font-size: 12px;
  color: #9ca3af;
}

.tag-new {
  display: inline-block;
  margin-left: 8px;
  padding: 2px 6px;
  border-radius: 999px;
  font-size: 11px;
  background: #f97316;
  color: #fff;
}
</style>
