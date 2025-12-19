<template>
  <div class="drug-lib-container">
    <!-- 居中页面标题 -->
    <div class="main-title-container">
      <h1 class="main-title">药品知识库</h1>
      <div class="title-subline">专业药品信息查询平台</div>
    </div>

    <!-- 顶部搜索栏 -->
    <div class="toolbar-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <div class="search-container">
            <svg class="search-icon" viewBox="0 0 24 24" width="18" height="18">
              <path fill="#64748b" d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
            <input
              v-model="keyword"
              type="text"
              class="search-input"
              placeholder="输入药品名 / 通用名 / 适应症关键词搜索"
              @keyup.enter="loadList"
            />
          </div>
          
          <div class="select-container">
            <svg class="select-icon" viewBox="0 0 24 24" width="16" height="16">
              <path fill="#64748b" d="M7 10l5 5 5-5z"/>
            </svg>
            <select v-model="category" class="select" @change="loadList">
              <option value="">全部分类</option>
              <option
                v-for="c in categories"
                :key="c"
                :value="c"
              >
                {{ c }}
              </option>
            </select>
          </div>
          
          <button class="btn btn-primary" @click="loadList">
            <svg class="btn-icon" viewBox="0 0 24 24" width="16" height="16">
              <path fill="white" d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
            搜索
          </button>
          <button class="btn btn-secondary" @click="resetFilter">
            <svg class="btn-icon" viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
            </svg>
            重置
          </button>
        </div>
        <div class="toolbar-right">
          <div class="result-counter">
            <span class="result-icon">📊</span>
            <span class="result-text">
              共 <span class="result-number">{{ list.length }}</span> 条结果
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主体：左边列表，右边详情 -->
    <div class="body">
      <!-- 左侧列表卡片 -->
      <div class="list-card">
        <div class="card-header">
          <h3 class="card-title">药品列表</h3>
          <div class="card-subtitle">点击查看详细用药信息</div>
        </div>
        
        <div class="card-content">
          <div v-if="loading" class="loading-state">
            <div class="spinner"></div>
            <div class="loading-text">正在加载药品数据...</div>
          </div>
          <div v-else-if="list.length === 0" class="empty-state">
            <div class="empty-icon">📚</div>
            <div class="empty-text">暂无药品记录</div>
            <div class="empty-subtext">请先在后端或数据库中新增药品信息</div>
          </div>
          <div v-else class="table-container">
            <table class="drug-table">
              <thead>
                <tr>
                  <th>药品名称</th>
                  <th>通用名</th>
                  <th>类别</th>
                  <th>剂型</th>
                  <th>规格</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="item in list"
                  :key="item.id"
                  :class="{ active: current && current.id === item.id }"
                  @click="choose(item)"
                >
                  <td>
                    <div class="drug-name-cell">
                      <span class="drug-icon">💊</span>
                      <span class="drug-name">{{ item.drugName || '-' }}</span>
                    </div>
                  </td>
                  <td>{{ item.genericName || '-' }}</td>
                  <td>
                    <span class="category-badge">{{ item.category || '-' }}</span>
                  </td>
                  <td>{{ item.dosageForm || '-' }}</td>
                  <td>{{ item.spec || '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 右侧详情卡片 -->
      <div class="detail-card">
        <div class="card-header">
          <h3 class="card-title">药品详情</h3>
          <div class="card-subtitle">用药信息与注意事项</div>
        </div>
        
        <div class="card-content">
          <div v-if="!current" class="empty-detail">
            <div class="empty-detail-icon">👈</div>
            <div class="empty-detail-text">请从左侧选择一个药品</div>
            <div class="empty-detail-subtext">查看详细用药信息与注意事项</div>
          </div>
          <div v-else class="detail-content">
            <!-- 药品基本信息 -->
            <div class="drug-header">
              <div class="drug-title">
                <h2 class="drug-name-main">{{ current.drugName }}</h2>
                <span v-if="current.genericName" class="drug-generic">
                  （通用名：{{ current.genericName }}）
                </span>
              </div>
              
              <div class="drug-meta-grid">
                <div v-if="current.englishName" class="meta-item">
                  <span class="meta-label">英文名</span>
                  <span class="meta-value">{{ current.englishName }}</span>
                </div>
                <div v-if="current.category" class="meta-item">
                  <span class="meta-label">类别</span>
                  <span class="meta-badge">{{ current.category }}</span>
                </div>
                <div v-if="current.dosageForm" class="meta-item">
                  <span class="meta-label">剂型</span>
                  <span class="meta-value">{{ current.dosageForm }}</span>
                </div>
                <div v-if="current.spec" class="meta-item">
                  <span class="meta-label">规格</span>
                  <span class="meta-value">{{ current.spec }}</span>
                </div>
              </div>
            </div>

            <!-- 详细信息 -->
            <div class="drug-details">
              <section v-if="current.indications" class="detail-section">
                <div class="section-header">
                  <span class="section-icon">🎯</span>
                  <h4>适应症</h4>
                </div>
                <div class="section-content">{{ current.indications }}</div>
              </section>

              <section v-if="current.dosageUsage" class="detail-section">
                <div class="section-header">
                  <span class="section-icon">📝</span>
                  <h4>用法用量</h4>
                </div>
                <div class="section-content">{{ current.dosageUsage }}</div>
              </section>

              <section v-if="current.adverseReaction" class="detail-section">
                <div class="section-header">
                  <span class="section-icon">⚠️</span>
                  <h4>不良反应</h4>
                </div>
                <div class="section-content">{{ current.adverseReaction }}</div>
              </section>

              <section v-if="current.contraindication" class="detail-section">
                <div class="section-header">
                  <span class="section-icon">🚫</span>
                  <h4>禁忌</h4>
                </div>
                <div class="section-content">{{ current.contraindication }}</div>
              </section>

              <section v-if="current.precautions" class="detail-section">
                <div class="section-header">
                  <span class="section-icon">📋</span>
                  <h4>注意事项</h4>
                </div>
                <div class="section-content">{{ current.precautions }}</div>
              </section>

              <section v-if="current.interactions" class="detail-section">
                <div class="section-header">
                  <span class="section-icon">🔄</span>
                  <h4>药物相互作用</h4>
                </div>
                <div class="section-content">{{ current.interactions }}</div>
              </section>

              <section v-if="current.storage" class="detail-section">
                <div class="section-header">
                  <span class="section-icon">❄️</span>
                  <h4>贮藏</h4>
                </div>
                <div class="section-content">{{ current.storage }}</div>
              </section>

              <div v-if="current.reference" class="reference-section">
                <div class="reference-header">参考资料</div>
                <div class="reference-content">{{ current.reference }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const keyword = ref('')
const category = ref('')
const list = ref([])
const loading = ref(false)
const current = ref(null)
const categories = ref([])

onMounted(() => {
  loadList()
})

function buildQuery() {
  const params = new URLSearchParams()
  if (keyword.value.trim()) {
    params.append('keyword', keyword.value.trim())
  }
  if (category.value) {
    params.append('category', category.value)
  }
  const qs = params.toString()
  return qs ? `/api/drugs?${qs}` : '/api/drugs'
}

async function loadList() {
  loading.value = true
  try {
    const url = buildQuery()
    console.log('📡 请求药品列表 URL =', url)
    const res = await fetch(url)
    const data = await res.json()
    console.log('📦 药品列表 =', data)

    list.value = Array.isArray(data) ? data : []

    // 自动收集分类（防止重复）
    const set = new Set()
    list.value.forEach(d => {
      if (d.category) set.add(d.category)
    })
    categories.value = Array.from(set)

    // 如果当前没有选中项，默认选中第一条
    if (!current.value && list.value.length > 0) {
      current.value = list.value[0]
    }
  } catch (e) {
    console.error('❌ 加载药品列表失败:', e)
  } finally {
    loading.value = false
  }
}

function resetFilter() {
  keyword.value = ''
  category.value = ''
  current.value = null
  loadList()
}

function choose(item) {
  current.value = item
}
</script>

<style scoped>
.drug-lib-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  padding: 24px;
}

/* 居中页面标题 */
.main-title-container {
  text-align: center;
  margin-bottom: 32px;
  padding: 20px 0;
}

.main-title {
  font-size: 36px;
  font-weight: 700;
  color: #1e40af;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 8px rgba(30, 64, 175, 0.15);
  letter-spacing: 1px;
  position: relative;
  display: inline-block;
}

.main-title::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
  border-radius: 2px;
}

.title-subline {
  font-size: 16px;
  color: #64748b;
  margin-top: 24px;
  font-weight: 400;
}

/* 工具栏卡片 */
.toolbar-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(30, 64, 175, 0.08);
  border: 1px solid rgba(59, 130, 246, 0.1);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

/* 搜索容器 */
.search-container {
  position: relative;
  flex: 1;
  max-width: 320px;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: 12px 16px 12px 40px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  font-size: 14px;
  background: white;
  transition: all 0.3s ease;
  font-family: inherit;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.15);
  transform: translateY(-1px);
}

/* 选择容器 */
.select-container {
  position: relative;
  min-width: 160px;
}

.select-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  z-index: 1;
}

.select {
  width: 100%;
  padding: 12px 36px 12px 16px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  font-size: 14px;
  background: white;
  cursor: pointer;
  appearance: none;
  font-family: inherit;
  transition: all 0.3s ease;
}

.select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.15);
}

/* 按钮 */
.btn {
  padding: 12px 20px;
  border-radius: 12px;
  border: none;
  font-size: 14px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-family: inherit;
  white-space: nowrap;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn:active {
  transform: translateY(0);
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
}

.btn-secondary {
  background: white;
  color: #64748b;
  border: 2px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.btn-icon {
  flex-shrink: 0;
}

/* 结果计数器 */
.result-counter {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border: 2px solid #e2e8f0;
}

.result-icon {
  font-size: 18px;
}

.result-text {
  font-size: 14px;
  color: #475569;
}

.result-number {
  font-weight: 700;
  color: #2563eb;
  font-size: 16px;
}

/* 主体布局 */
.body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  min-height: calc(100vh - 200px);
}

/* 卡片通用样式 */
.list-card,
.detail-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  border: 1px solid rgba(59, 130, 246, 0.1);
  box-shadow: 0 8px 32px rgba(30, 64, 175, 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.list-card:hover,
.detail-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(30, 64, 175, 0.12);
}

/* 卡片头部 */
.card-header {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, #f8fafc, #ffffff);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.card-subtitle {
  font-size: 14px;
  color: #64748b;
}

/* 卡片内容 */
.card-content {
  flex: 1;
  overflow: hidden;
  position: relative;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 16px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 14px;
  color: #64748b;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 12px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  opacity: 0.5;
  margin-bottom: 8px;
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  color: #475569;
}

.empty-subtext {
  font-size: 14px;
  color: #94a3b8;
}

/* 表格容器 */
.table-container {
  overflow: hidden;
  border-radius: 12px;
  margin: 8px;
}

/* 药品表格 */
.drug-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-size: 14px;
  background: white;
}

.drug-table thead {
  position: sticky;
  top: 0;
  background: #f8fafc;
  z-index: 10;
}

.drug-table thead tr {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.drug-table th {
  padding: 16px;
  border-bottom: 2px solid #e2e8f0;
  text-align: left;
  font-weight: 600;
  color: #334155;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.drug-table th:first-child {
  border-top-left-radius: 12px;
}

.drug-table th:last-child {
  border-top-right-radius: 12px;
}

.drug-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #f1f5f9;
  color: #475569;
  transition: all 0.2s ease;
}

.drug-table tbody tr {
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.drug-table tbody tr:hover {
  background: linear-gradient(to right, #f0f9ff, #ffffff);
  transform: translateX(4px);
}

.drug-table tbody tr.active {
  background: linear-gradient(to right, #dbeafe, #eff6ff);
  box-shadow: inset 4px 0 0 #3b82f6;
}

.drug-table tbody tr:last-child td {
  border-bottom: none;
}

/* 药品名称单元格 */
.drug-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.drug-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.drug-name {
  font-weight: 500;
  color: #1e293b;
}

/* 分类徽章 */
.category-badge {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
  color: #0369a1;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid #7dd3fc;
  white-space: nowrap;
}

/* 详情空状态 */
.empty-detail {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  gap: 12px;
  text-align: center;
  height: 100%;
}

.empty-detail-icon {
  font-size: 64px;
  opacity: 0.3;
  margin-bottom: 16px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(-10px); }
}

.empty-detail-text {
  font-size: 18px;
  font-weight: 500;
  color: #475569;
}

.empty-detail-subtext {
  font-size: 14px;
  color: #94a3b8;
}

/* 药品详情内容 */
.detail-content {
  padding: 8px;
  height: 100%;
  overflow-y: auto;
}

/* 药品头部信息 */
.drug-header {
  padding: 0 16px 24px;
  border-bottom: 2px solid #f1f5f9;
  margin-bottom: 24px;
}

.drug-title {
  margin-bottom: 20px;
}

.drug-name-main {
  font-size: 28px;
  font-weight: 700;
  color: #1e40af;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.drug-generic {
  font-size: 16px;
  color: #64748b;
  font-weight: 400;
}

/* 药品元数据网格 */
.drug-meta-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.meta-value {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

.meta-badge {
  display: inline-block;
  padding: 6px 12px;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #1e40af;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid #93c5fd;
}

/* 详情部分 */
.drug-details {
  padding: 0 16px;
}

.detail-section {
  margin-bottom: 24px;
  padding: 20px;
  background: linear-gradient(to right, #f8fafc, #ffffff);
  border-radius: 12px;
  border-left: 4px solid #3b82f6;
  transition: transform 0.2s ease;
}

.detail-section:hover {
  transform: translateX(4px);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.section-icon {
  font-size: 20px;
}

.section-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.section-content {
  font-size: 14px;
  line-height: 1.7;
  color: #475569;
  white-space: pre-wrap;
  padding-left: 32px;
}

/* 参考资料 */
.reference-section {
  margin-top: 32px;
  padding: 20px;
  background: linear-gradient(to right, #fef3c7, #fefce8);
  border-radius: 12px;
  border: 1px solid #fde68a;
}

.reference-header {
  font-size: 14px;
  font-weight: 600;
  color: #92400e;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.reference-header::before {
  content: "📖";
  font-size: 16px;
}

.reference-content {
  font-size: 14px;
  color: #78350f;
  line-height: 1.6;
  font-style: italic;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .body {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .toolbar-left {
    flex-wrap: wrap;
  }
  
  .search-container {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .drug-lib-container {
    padding: 16px;
  }
  
  .toolbar {
    flex-direction: column;
    gap: 16px;
  }
  
  .toolbar-left,
  .toolbar-right {
    width: 100%;
  }
  
  .drug-meta-grid {
    grid-template-columns: 1fr;
  }
  
  .main-title {
    font-size: 28px;
  }
  
  .title-subline {
    font-size: 14px;
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 动画效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>