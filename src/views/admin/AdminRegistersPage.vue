<template>
  <div class="page">
    <!-- 居中标题+功能介绍 -->
    <div class="page-header">
      <h2 class="page-title">挂号监管</h2>
      <p class="page-desc">查询并管理所有挂号记录，支持状态修正、优先级调整及多条件筛选</p>
    </div>

    <div class="card">
      <div class="row">
        <div class="field">
          <div class="label">日期</div>
          <input class="inp" type="date" v-model="q.date" @change="load" />
        </div>

        <div class="field">
          <div class="label">科室ID</div>
          <input class="inp" v-model="q.deptId" placeholder="可空" />
        </div>

        <div class="field">
          <div class="label">医生ID</div>
          <input class="inp" v-model="q.doctorId" placeholder="可空" />
        </div>

        <div class="field">
          <div class="label">队列状态</div>
          <select class="inp" v-model="q.queueStatus">
            <option value="">全部</option>
            <option value="0">0-候诊</option>
            <option value="1">1-已叫号</option>
            <option value="2">2-就诊中</option>
            <option value="3">3-已完成</option>
            <option value="4">4-过号</option>
            <option value="5">5-取消</option>
          </select>
        </div>

        <div class="ops">
          <button class="btn primary" @click="load">查询</button>
          <button class="btn primary-outline" @click="resetQuery">重置</button>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="table-wrap">
        <table class="tbl">
          <thead>
            <tr>
              <th style="width:80px">ID</th>
              <th style="width:180px">用户(账号)</th>
              <th style="width:180px">就诊人</th>
              <th style="width:220px">科室</th>
              <th style="width:220px">医生</th>
              <th style="width:170px">挂号时间</th>
              <th style="width:90px">queueNo</th>
              <th style="width:140px">队列状态</th>
              <th style="width:140px">优先级</th>
              <th style="width:220px">操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="r in rows" :key="r.id">
              <td>{{ r.id }}</td>
              <td>
                <div class="cell-2">
                  <div class="main">{{ userLabel(r.userId) }}</div>
                  <div class="sub muted">userId={{ r.userId ?? "-" }}</div>
                </div>
              </td>
              <td>
                <div class="cell-2">
                  <div class="main">{{ r.patientName || `就诊人#${r.patientId ?? "-"}` }}</div>
                  <div class="sub muted">patientId={{ r.patientId ?? "-" }}</div>
                </div>
              </td>
              <td>{{ showDept(r) }}</td>
              <td>{{ showDoctor(r) }}</td>
              <td>{{ fmtTime(r.registerTime) }}</td>
              <td>{{ r.queueNo ?? "-" }}</td>
              <td>
                <span :class="['tag', statusTagClass(r.queueStatus)]">
                  {{ statusText(r.queueStatus) }}
                </span>
                <span class="muted" style="margin-left:8px">({{ r.queueStatus ?? "-" }})</span>
              </td>
              <td>
                <span :class="['tag', priorityTagClass(r.queuePriority)]">
                  {{ priorityText(r.queuePriority) }}
                </span>
                <span class="muted" style="margin-left:8px">({{ r.queuePriority ?? 0 }})</span>
              </td>
              <td class="actions">
                <button class="link primary" @click="fixStatus(r)">修正状态</button>
                <button class="link primary" @click="setPri(r)">调优先级</button>
              </td>
            </tr>
            <tr v-if="!loading && rows.length === 0">
              <td colspan="10" class="empty">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="pager">
        <div class="muted">总数：{{ total }}</div>
        <div class="pbtns">
          <button class="btn primary-outline" :disabled="page <= 0" @click="go(page - 1)">上一页</button>
          <div class="muted">第 {{ page + 1 }} 页</div>
          <button class="btn primary-outline" :disabled="(page + 1) * size >= total" @click="go(page + 1)">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import {
  adminGetRegisters,
  adminFixQueueStatus,
  adminSetPriority,
  adminGetUsers,
} from "@/api/admin";

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);

const userMap = ref(new Map());

const todayStr = () => {
  const d = new Date();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${mm}-${dd}`;
};

const q = reactive({
  date: todayStr(),
  deptId: "",
  doctorId: "",
  queueStatus: "",
});

function fmtTime(s) {
  if (!s) return "-";
  return String(s).replace("T", " ").slice(0, 19);
}

function resetQuery() {
  q.date = todayStr();
  q.deptId = "";
  q.doctorId = "";
  q.queueStatus = "";
  page.value = 0;
  load();
}

function go(p) {
  page.value = p;
  load();
}

function statusText(v) {
  const n = v === null || v === undefined ? null : Number(v);
  const map = {
    0: "候诊",
    1: "已叫号",
    2: "就诊中",
    3: "已完成",
    4: "过号",
    5: "已取消",
  };
  return n in map ? map[n] : "未知";
}

function statusTagClass(v) {
  const n = v === null || v === undefined ? -1 : Number(v);
  if (n === 0) return "tag-wait";
  if (n === 1) return "tag-call";
  if (n === 2) return "tag-prog";
  if (n === 3) return "tag-done";
  if (n === 4) return "tag-noshow";
  if (n === 5) return "tag-cancel";
  return "tag-unk";
}

function priorityText(v) {
  const n = v === null || v === undefined ? 0 : Number(v);
  if (n <= 0) return "普通";
  if (n >= 100) return "加急";
  return `优先(${n})`;
}

function priorityTagClass(v) {
  const n = v === null || v === undefined ? 0 : Number(v);
  if (n <= 0) return "tag-normal";
  if (n >= 100) return "tag-urgent";
  return "tag-priority";
}

function showDept(r) {
  const name = r.deptName || "-";
  const id = r.deptId ?? "-";
  return `${name} (${id})`;
}

function showDoctor(r) {
  const name = r.doctorName || "-";
  const id = r.doctorId ?? "-";
  return `${name} (${id})`;
}

function userLabel(userId) {
  if (userId === null || userId === undefined) return "-";
  const u = userMap.value.get(Number(userId));
  if (!u) return `账号#${userId}`;
  return u.name || u.loginName || `账号#${userId}`;
}

async function loadUsersMap() {
  try {
    const pageObj = await adminGetUsers({ page: 0, size: 1000 });
    const list = pageObj?.content || pageObj?.items || pageObj || [];
    const m = new Map();
    for (const u of list) {
      if (u && (u.userId ?? u.id) != null) {
        m.set(Number(u.userId ?? u.id), u);
      }
    }
    userMap.value = m;
  } catch (e) {
    console.warn("loadUsersMap failed:", e?.message || e);
  }
}

async function load() {
  loading.value = true;
  try {
    const params = {
      date: q.date || undefined,
      deptId: q.deptId ? Number(q.deptId) : undefined,
      doctorId: q.doctorId ? Number(q.doctorId) : undefined,
      queueStatus: q.queueStatus !== "" ? Number(q.queueStatus) : undefined,
      page: page.value,
      size: size.value,
    };
    const pageObj = await adminGetRegisters(params);
    rows.value = pageObj.content || [];
    total.value = pageObj.totalElements ?? 0;
  } catch (e) {
    alert(e?.response?.data?.msg || e?.message || "加载失败");
  } finally {
    loading.value = false;
  }
}

async function fixStatus(r) {
  const hint = "0候诊/1已叫号/2就诊中/3已完成/4过号/5取消";
  const v = prompt(`把挂号 ${r.id} 的 queueStatus 改成（${hint}）：`, String(r.queueStatus ?? ""));
  if (v === null) return;
  const value = Number(v);
  if (Number.isNaN(value)) return alert("请输入数字");
  await adminFixQueueStatus(r.id, value);
  load();
}

async function setPri(r) {
  const v = prompt(`把挂号 ${r.id} 的 priority 改成（0普通，100加急）：`, String(r.queuePriority ?? 0));
  if (v === null) return;
  const value = Number(v);
  if (Number.isNaN(value)) return alert("请输入数字");
  await adminSetPriority(r.id, value);
  load();
}

onMounted(async () => {
  await loadUsersMap();
  await load();
});
</script>

<style scoped>
/* 标题样式 */
.page-header {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eef2f7;
}
.page-title {
  font-size: 24px;
  font-weight: 800;
  color: #1e40af;
  margin: 0 0 8px 0;
}
.page-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

/* 基础样式 */
.page { display:flex; flex-direction:column; gap:14px; }
.card { background:#fff; border:1px solid #eef2f7; border-radius:16px; padding:14px; box-shadow:0 1px 2px rgba(16,24,40,.04); }
.row { display:flex; gap:12px; align-items:end; flex-wrap:wrap; }
.field { display:flex; flex-direction:column; gap:6px; }
.label { font-size:12px; color:#6b7280; }
.inp { height:36px; padding:0 10px; border-radius:10px; border:1px solid #e5e7eb; outline:none; min-width:160px; background:#fff; }
.ops { margin-left:auto; display:flex; gap:10px; }

/* 按钮样式（统一蓝色） */
.btn { 
  height:36px; padding:0 12px; border-radius:10px; 
  cursor:pointer; font-size:14px;
}
.btn.primary { 
  background:#2563eb; color:#fff; border:1px solid #2563eb; 
}
.btn.primary-outline { 
  background:#fff; color:#2563eb; border:1px solid #2563eb; 
}
.btn:disabled { opacity:.5; cursor:not-allowed; }

/* 表格样式 */
.table-wrap { overflow:auto; }
.tbl { width:100%; border-collapse:collapse; }
.tbl th, .tbl td { border-bottom:1px solid #eef2f7; padding:10px; text-align:left; vertical-align:middle; }
.tbl th { font-size:12px; color:#6b7280; font-weight:700; }

/* 操作链接（蓝色） */
.actions { display:flex; gap:12px; }
.link.primary { 
  border:none; background:none; color:#2563eb; 
  cursor:pointer; padding:0; font-size:14px;
}

/* 其他样式 */
.empty { text-align:center; color:#6b7280; padding:18px; }
.pager { display:flex; justify-content:space-between; align-items:center; padding-top:10px; }
.pbtns { display:flex; gap:10px; align-items:center; }
.muted { color:#6b7280; }
.cell-2 { display:flex; flex-direction:column; gap:2px; }
.cell-2 .main { font-weight:700; }
.cell-2 .sub { font-size:12px; }
.tag { display:inline-flex; align-items:center; padding:4px 10px; border-radius:999px; font-size:12px; border:1px solid #e5e7eb; }
.tag-wait { background:#f3f4f6; }
.tag-call { background:#eff6ff; border-color:#bfdbfe; }
.tag-prog { background:#fef3c7; border-color:#fcd34d; }
.tag-done { background:#ecfdf5; border-color:#a7f3d0; }
.tag-noshow { background:#fff7ed; border-color:#fed7aa; }
.tag-cancel { background:#fef2f2; border-color:#fecaca; }
.tag-unk { background:#f3f4f6; }
.tag-normal { background:#f3f4f6; }
.tag-priority { background:#fef3c7; border-color:#fcd34d; }
.tag-urgent { background:#fee2e2; border-color:#fecaca; }
</style>