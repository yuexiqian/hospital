<!-- src/views/pharmacist/InventoryPage.vue -->
<template>
  <div class="inv-page">
    <!-- 顶部标题栏 -->
    <header class="inv-header">
      <div class="title-block">
        <h1>药师端 · 药品库存管理</h1>
        <p class="sub-title">查看当前库存、入库、停用/启用药品</p>
      </div>
      <div class="user-block">
        <span class="current-user">当前药师：{{ currentUser?.loginName || '-' }}</span>
        <button class="link-btn" @click="handleLogout">退出登录</button>
      </div>
    </header>

    <!-- 查询条件 -->
    <section class="search-card">
      <div class="search-row">
        <label class="search-label">关键字</label>
        <input
          v-model="keyword"
          class="search-input"
          placeholder="输入药品名 / 通用名 后回车或点击搜索"
          @keyup.enter="handleSearch"
        />
        <button class="primary-btn" @click="handleSearch">搜索</button>
        <button class="ghost-btn" @click="handleReset">重置</button>
      </div>
    </section>

    <!-- 主体列表 -->
    <section class="main-card">
      <div class="card-header">
        <span>库存列表（{{ inventoryList.length }} 条）</span>
      </div>

      <div class="table-wrapper">
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
              <td>{{ item.unitPrice != null ? item.unitPrice.toFixed ? item.unitPrice.toFixed(2) : item.unitPrice : '-' }}</td>
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
                <button class="mini-btn" @click="handleInStock(item)">入库</button>
                <button class="mini-btn" @click="handleOutStock(item)">盘亏</button>
                <button
                  class="mini-btn mini-grey"
                  @click="handleToggleStatus(item)"
                >
                  {{ item.status === 'ACTIVE' ? '停用' : '启用' }}
                </button>
              </td>
            </tr>

            <tr v-if="inventoryList.length === 0">
              <td colspan="9" class="empty-cell">暂无库存数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="footer-msg" v-if="message">
        <span :class="['msg', { error: isError }]">
          {{ message }}
        </span>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getInventoryList,
  adjustInventory,
  updateInventoryStatus
} from '@/api/pharmacist'

const router = useRouter()

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
  await loadList()
})
</script>

<style scoped>
.inv-page {
  min-height: 100vh;
  padding: 32px 48px 40px;
  background: #f3f4f6;
  box-sizing: border-box;
}

.inv-header {
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

.ghost-btn {
  background: #fff;
  color: #374151;
  border: 1px solid #d1d5db;
}

.main-card {
  background: #fff;
  border-radius: 16px;
  padding: 14px 16px 10px;
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.08);
}

.card-header {
  font-size: 14px;
  color: #111827;
  margin-bottom: 10px;
}

.table-wrapper {
  max-height: calc(100vh - 260px);
  overflow: auto;
}

.inv-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.inv-table th,
.inv-table td {
  padding: 6px 8px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
  white-space: nowrap;
}

.inv-table thead th {
  background: #f9fafb;
  color: #6b7280;
  position: sticky;
  top: 0;
  z-index: 1;
}

.cell-ellipsis {
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty-cell {
  text-align: center;
  color: #9ca3af;
  padding: 16px 0;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
}

.status-active {
  background: #dcfce7;
  color: #16a34a;
}

.status-stopped {
  background: #e5e7eb;
  color: #4b5563;
}

.mini-btn {
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 999px;
  border: none;
  cursor: pointer;
  margin-right: 4px;
  background: #2563eb;
  color: #fff;
}

.mini-btn:hover {
  background: #1d4ed8;
}

.mini-grey {
  background: #6b7280;
}

.mini-grey:hover {
  background: #4b5563;
}

.footer-msg {
  margin-top: 8px;
}

.msg {
  font-size: 13px;
  color: #16a34a;
}

.msg.error {
  color: #dc2626;
}
</style>
