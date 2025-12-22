<template>
  <div class="page-container">
    <!-- 居中标题+功能介绍（核心新增） -->
    <div class="page-header">
      <h2 class="page-title">数据看板</h2>
      <p class="page-desc">查看每日挂号数据、队列状态分布及近7天挂号趋势统计</p>
    </div>

    <!-- 原有搜索区域 -->
    <div class="card">
      <div class="row">
        <div class="field">
          <div class="label">日期</div>
          <input class="inp" type="date" v-model="date" @change="load" />
        </div>
        <div class="ops">
          <button class="btn primary" @click="load" :disabled="loading">
            {{ loading ? "加载中..." : "刷新" }}
          </button>
        </div>
      </div>
    </div>

    <!-- 原有数据卡片区域 -->
    <div class="grid">
      <div class="card">
        <div class="k">今日挂号</div>
        <div class="v">{{ summary.registerTotal }}</div>
      </div>
      <div class="card">
        <div class="k">今日已分诊</div>
        <div class="v">{{ summary.triaged }}</div>
      </div>
    </div>

    <div class="card">
      <div class="title">队列状态说明</div>
      <div class="mini">
        <div v-for="(txt, code) in STATUS_MAP" :key="code" class="mini-item">
          <span class="tag">{{ txt }}</span>
          <span class="muted">({{ code }})</span>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="title">按 queueStatus 分布</div>
      <div class="mini">
        <div v-for="x in summary.byQueueStatus" :key="x.queueStatus" class="mini-item">
          <span class="tag" :class="statusTagClass(x.queueStatus)">
            {{ statusText(x.queueStatus) }}
          </span>
          <span class="muted">({{ x.queueStatus }})</span>
          <span class="num">{{ x.cnt }}</span>
          <span class="muted" v-if="summary.registerTotal > 0">
            {{ percent(x.cnt, summary.registerTotal) }}
          </span>
        </div>
        <div v-if="summary.byQueueStatus?.length === 0" class="muted">暂无数据</div>
      </div>
    </div>

    <div class="card">
      <div class="title">科室 Top</div>
      <div class="mini">
        <div v-for="x in summary.byDept" :key="x.deptId" class="mini-item">
          <span class="muted">{{ x.deptName }} (#{{ x.deptId }})</span>
          <span class="num">{{ x.cnt }}</span>
          <span class="muted" v-if="summary.registerTotal > 0">
            {{ percent(x.cnt, summary.registerTotal) }}
          </span>
        </div>
        <div v-if="summary.byDept?.length === 0" class="muted">暂无数据</div>
      </div>
    </div>

    <div class="card">
      <div class="title">近 7 天挂号趋势</div>
      <div class="mini">
        <div v-for="x in trendFilled" :key="x.day" class="mini-item">
          <span class="muted">{{ x.day }}</span>
          <span class="num">{{ x.cnt }}</span>
        </div>
      </div>
    </div>

    <div v-if="errMsg" class="card err">
      {{ errMsg }}
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import { adminDashboardSummary, adminDashboardTrend } from "@/api/admin";

const loading = ref(false);
const errMsg = ref("");

const STATUS_MAP = {
  0: "候诊",
  1: "已叫号",
  2: "就诊中",
  3: "已完成",
  4: "过号",
  5: "已取消",
};

const todayStr = () => {
  const d = new Date();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${mm}-${dd}`;
};

const date = ref(todayStr());
const summary = ref({
  registerTotal: 0,
  triaged: 0,
  byQueueStatus: [],
  byDept: [],
});
const trend = ref([]);

function statusText(v) {
  const n = v === null || v === undefined ? null : Number(v);
  return n !== null && STATUS_MAP[n] ? STATUS_MAP[n] : "未知";
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

function percent(cnt, total) {
  if (!total) return "";
  const p = (Number(cnt || 0) / Number(total)) * 100;
  return `${p.toFixed(1)}%`;
}

function yyyyMMdd(d) {
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${mm}-${dd}`;
}

const trendFilled = computed(() => {
  const days = 7;
  const m = new Map();
  for (const x of trend.value || []) {
    if (!x) continue;
    const dayStr = String(x.day).slice(0, 10);
    m.set(dayStr, Number(x.cnt || 0));
  }
  const out = [];
  const base = new Date(date.value);
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(base);
    d.setDate(d.getDate() - i);
    const key = yyyyMMdd(d);
    out.push({ day: key, cnt: m.get(key) ?? 0 });
  }
  return out;
});

async function load() {
  loading.value = true;
  errMsg.value = "";
  try {
    summary.value = await adminDashboardSummary({ date: date.value });
    trend.value = await adminDashboardTrend({ days: 7 });
  } catch (e) {
    errMsg.value = e?.response?.data?.msg || e?.message || "加载失败";
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
/* 新增：居中标题样式 */
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

/* 原有样式保留 */
.page-container { display:flex; flex-direction:column; gap:14px; }
.card { background:#fff; border:1px solid #eef2f7; border-radius:16px; padding:14px; box-shadow:0 1px 2px rgba(16,24,40,.04); }
.row { display:flex; gap:12px; align-items:end; flex-wrap:wrap; }
.field { display:flex; flex-direction:column; gap:6px; }
.label { font-size:12px; color:#6b7280; }
.inp { height:36px; padding:0 10px; border-radius:10px; border:1px solid #e5e7eb; outline:none; min-width:180px; }
.ops { margin-left:auto; display:flex; gap:10px; }
.btn { height:36px; padding:0 12px; border-radius:10px; border:1px solid #e5e7eb; background:#fff; cursor:pointer; }
.btn.primary { background:#2563eb; color:#fff; border-color:#2563eb; }

.grid { display:grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap:14px; }
.k { color:#6b7280; font-size:12px; }
.v { font-size:28px; font-weight:800; margin-top:8px; }

.title { font-weight:800; margin-bottom:10px; }
.mini { display:flex; flex-wrap:wrap; gap:10px; }
.mini-item { border:1px solid #eef2f7; border-radius:12px; padding:10px 12px; display:flex; gap:10px; align-items:center; }
.muted { color:#6b7280; }
.num { font-weight:800; }

.tag { display:inline-flex; align-items:center; padding:4px 10px; border-radius:999px; font-size:12px; border:1px solid #e5e7eb; background:#f3f4f6; }
.tag-wait { background:#f3f4f6; }
.tag-call { background:#eff6ff; border-color:#bfdbfe; }
.tag-prog { background:#fef3c7; border-color:#fcd34d; }
.tag-done { background:#ecfdf5; border-color:#a7f3d0; }
.tag-noshow { background:#fff7ed; border-color:#fed7aa; }
.tag-cancel { background:#fef2f2; border-color:#fecaca; }
.tag-unk { background:#f3f4f6; }

.err { border-color:#fecaca; background:#fef2f2; color:#b91c1c; }
</style>