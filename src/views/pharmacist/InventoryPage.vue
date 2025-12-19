<!-- InventoryPage.vue - 标题居中，统一风格 -->
<template>
  <div class="inv-page">
    <!-- 顶部标题栏 - 居中 -->
    <header class="inv-header">
      <div class="header-content">
        <h2>药师端 · 药品库存管理</h2>
        <div class="header-subtitle">查看当前库存、入库、停用/启用药品</div>
      </div>
    </header>

    <!-- 查询条件 -->
    <section class="search-section">
      <div class="search-container">
        <div class="search-group">
          <label class="search-label">关键字</label>
          <input
            v-model="keyword"
            class="search-input"
            placeholder="输入药品名 / 通用名"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
          <button class="reset-btn" @click="handleReset">重置</button>
        </div>
      </div>
    </section>

    <!-- 主体列表 -->
    <section class="table-section">
      <div class="section-header">
        <h3>库存列表（{{ inventoryList.length }} 条）</h3>
      </div>

      <div class="table-container">
        <table class="inv-table">
          <thead>
            <tr>
              <th style="width: 60px">ID</th>
              <th>药品名</th>
              <th>通用名</th>
              <th style="width: 90px">当前库存</th>
              <th style="width: 90px">单价(元)</th>
              <th>批号</th>
              <th style="width: 120px">有效期</th>
              <th style="width: 90px">状态</th>
              <th style="width: 180px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="item in inventoryList"
              :key="item.id"
            >
              <td>{{ item.id }}</td>
              <td class="cell-ellipsis" :title="item.drugName">{{ item.drugName }}</td>
              <td class="cell-ellipsis" :title="item.genericName">{{ item.genericName }}</td>
              <td>{{ item.stockQty }}</td>
              <td>{{ item.unitPrice != null ? (item.unitPrice.toFixed ? item.unitPrice.toFixed(2) : item.unitPrice) : '-' }}</td>
              <td class="cell-ellipsis" :title="item.batchNo">{{ item.batchNo || '-' }}</td>
              <td>{{ item.expireDate || '-' }}</td>
              <td>
                <span
                  class="status-tag"
                  :class="item.status === 'ACTIVE' ? 'status-active' : 'status-stopped'"
                >
                  {{ item.status === 'ACTIVE' ? '启用' : '停用' }}
                </span>
              </td>
              <td>
                <button class="action-btn primary" @click="handleInStock(item)">入库</button>
                <button class="action-btn warning" @click="handleOutStock(item)">盘亏</button>
                <button
                  class="action-btn secondary"
                  @click="handleToggleStatus(item)"
                >
                  {{ item.status === 'ACTIVE' ? '停用' : '启用' }}
                </button>
              </td>
            </tr>

            <tr v-if="inventoryList.length === 0">
              <td colspan="9">
                <div class="empty-state">
                  <div class="empty-icon">📦</div>
                  <div>暂无库存数据</div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="message" class="footer-message" :class="{ error: isError }">
        {{ message }}
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  getInventoryList,
  adjustInventory,
  updateInventoryStatus
} from '@/api/pharmacist'

// 登录用户
const currentUser = ref(null)

// 查询关键字
const keyword = ref('')

// 列表数据
const inventoryList = ref([])

// 消息
const message = ref('')
const isError = ref(false)

// 加载列表
const loadList = async () => {
  message.value = ''
  isError.value = false
  try {
    const resp = await getInventoryList(keyword.value || '')
    if (resp.data.code === 0) {
      inventoryList.value = resp.data.data || []
    } else {
      inventoryList.value = []
      isError.value = true
      message.value = resp.data.message || '加载库存列表失败'
    }
  } catch (e) {
    console.error(e)
    inventoryList.value = []
    isError.value = true
    message.value = '请求失败，无法加载库存列表'
  }
}

const handleSearch = async () => {
  await loadList()
}

const handleReset = async () => {
  keyword.value = ''
  await loadList()
}

// 入库：delta > 0
const doAdjust = async (row, delta) => {
  if (!delta || isNaN(delta)) return

  try {
    const payload = {
      inventoryId: row.id,
      deltaQty: delta,
      remark: delta > 0 ? '前端入库' : '前端盘亏'
    }
    const resp = await adjustInventory(payload)
    if (resp.data.code === 0) {
      isError.value = false
      message.value = '操作成功'
      await loadList()
    } else {
      isError.value = true
      message.value = resp.data.message || '调整库存失败'
    }
  } catch (e) {
    console.error(e)
    isError.value = true
    message.value = '请求失败，库存未更新'
  }
}

const handleInStock = (row) => {
  const val = window.prompt(`请输入【${row.drugName}】入库数量（正整数）：`, '100')
  if (val == null) return
  const n = Number(val)
  if (!Number.isInteger(n) || n <= 0) {
    alert('请输入大于 0 的整数')
    return
  }
  doAdjust(row, n)
}

const handleOutStock = (row) => {
  const val = window.prompt(`请输入【${row.drugName}】盘亏数量（正整数）：`, '10')
  if (val == null) return
  const n = Number(val)
  if (!Number.isInteger(n) || n <= 0) {
    alert('请输入大于 0 的整数')
    return
  }
  doAdjust(row, -n)
}

// 启用 / 停用
const handleToggleStatus = async (row) => {
  const target = row.status === 'ACTIVE' ? 'STOPPED' : 'ACTIVE'
  const text = target === 'ACTIVE' ? '启用' : '停用'
  if (!window.confirm(`确定要${text}【${row.drugName}】吗？`)) {
    return
  }

  try {
    const resp = await updateInventoryStatus(row.id, target)
    if (resp.data.code === 0) {
      isError.value = false
      message.value = `${text}成功`
      await loadList()
    } else {
      isError.value = true
      message.value = resp.data.message || `${text}失败`
    }
  } catch (e) {
    console.error(e)
    isError.value = true
    message.value = '请求失败，状态未更新'
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
  await loadList()
})
</script>

<style scoped>
.inv-page {
  min-height: 100%;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

/* 标题栏 - 居中 */
.inv-header {
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
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.search-group {
  display: flex;
  align-items: center;
  gap: 12px;
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
.reset-btn {
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

/* 表格区域 */
.table-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.table-container {
  overflow-x: auto;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.inv-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  color: #475569;
}

.inv-table th {
  padding: 16px 20px;
  font-weight: 600;
  text-align: left;
  color: #334155;
  white-space: nowrap;
  border-bottom: 2px solid #e2e8f0;
  background: #f1f5f9;
}

.inv-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.inv-table tbody tr {
  transition: background-color 0.2s ease;
}

.inv-table tbody tr:hover {
  background-color: #f8fafc;
}

.cell-ellipsis {
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  color: #9ca3af;
}

.empty-icon {
  font-size: 32px;
  margin-bottom: 12px;
  opacity: 0.6;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
}

.status-active {
  background: #dcfce7;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.status-stopped {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
}

/* 操作按钮 */
.action-btn {
  padding: 8px 16px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
  margin-right: 8px;
}

.action-btn.primary {
  background: #3b82f6;
  color: white;
}

.action-btn.primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.action-btn.warning {
  background: #f97316;
  color: white;
}

.action-btn.warning:hover {
  background: #ea580c;
  transform: translateY(-1px);
}

.action-btn.secondary {
  background: white;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.action-btn.secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

/* 消息 */
.footer-message {
  margin-top: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  background: #dcfce7;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.footer-message.error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-group {
    flex-direction: column;
    align-items: stretch;
  }
  
  .inv-header h2 {
    font-size: 24px;
  }
  
  .header-subtitle {
    font-size: 14px;
  }
  
  .table-section {
    padding: 16px;
  }
  
  .inv-table th,
  .inv-table td {
    padding: 12px 16px;
  }
}
</style>