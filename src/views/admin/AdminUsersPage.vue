<template>
  <div class="page-container">
    <!-- 居中标题+功能介绍 -->
    <div class="page-header">
      <h2 class="page-title">账号管理</h2>
      <p class="page-desc">管理系统所有用户账号，包括创建、编辑、状态切换及密码重置</p>
    </div>

    <!-- 搜索区域 -->
    <div class="card">
      <div class="row">
        <div class="field">
          <div class="label">关键字</div>
          <input class="inp" v-model="q.keyword" placeholder="loginName 模糊" @keyup.enter="load"/>
        </div>
        <div class="field">
          <div class="label">角色</div>
          <select class="inp" v-model="q.role">
            <option value="">全部</option>
            <option value="ADMIN">ADMIN</option>
            <option value="DOCTOR">DOCTOR</option>
            <option value="NURSE">NURSE</option>
            <option value="PHARMACIST">PHARMACIST</option>
            <option value="PATIENT">PATIENT</option>
          </select>
        </div>
        <div class="field">
          <div class="label">状态</div>
          <select class="inp" v-model="q.status">
            <option value="">全部</option>
            <option :value="1">启用</option>
            <option :value="0">停用</option>
          </select>
        </div>
        <div class="ops">
          <button class="btn primary" @click="load">查询</button>
          <button class="btn primary-outline" @click="resetQuery">重置</button>
          <button class="btn primary" @click="openCreate">+ 新增账号</button>
        </div>
      </div>
    </div>

    <!-- 表格区域 -->
    <div class="card">
      <div class="table-wrap">
        <table class="tbl">
          <thead>
            <tr>
              <th style="width:80px">ID</th>
              <th>登录名</th>
              <th style="width:120px">角色</th>
              <th style="width:90px">状态</th>
              <th>绑定</th>
              <th style="width:170px">创建时间</th>
              <th style="width:260px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="u in rows" :key="u.userId">
              <td>{{ u.userId }}</td>
              <td>{{ u.loginName }}</td>
              <td>{{ u.role }}</td>
              <td>
                <span class="tag" :class="u.status===1?'ok':'bad'">{{ u.status===1?'启用':'停用' }}</span>
              </td>
              <td>
                <span v-if="u.bindType && u.bindType !== '-'">
                  {{ u.bindType }}：{{ u.bindName }} ({{ u.bindId }})
                </span>
                <span v-else class="muted">未绑定</span>
              </td>
              <td>{{ fmtTime(u.createTime) }}</td>
              <td class="actions">
                <button class="link primary" @click="openEdit(u)">编辑</button>
                <button class="link primary" @click="toggleStatus(u)">{{ u.status===1?'停用':'启用' }}</button>
                <button class="link danger" @click="resetPwd(u)">重置密码</button>
              </td>
            </tr>
            <tr v-if="!loading && rows.length===0">
              <td colspan="7" class="empty">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="pager">
        <div class="muted">总数：{{ total }}</div>
        <div class="pbtns">
          <button class="btn primary-outline" :disabled="page<=0" @click="go(page-1)">上一页</button>
          <div class="muted">第 {{ page+1 }} 页</div>
          <button class="btn primary-outline" :disabled="(page+1)*size>=total" @click="go(page+1)">下一页</button>
        </div>
      </div>
    </div>

    <!-- 弹窗区域 -->
    <div v-if="showModal" class="mask" @click.self="closeModal">
      <div class="modal">
        <div class="m-title">{{ formMode==='create' ? '新增账号' : '编辑账号' }}</div>
        <div class="m-grid">
          <div class="field">
            <div class="label">登录名</div>
            <input class="inp" v-model="form.loginName" :disabled="formMode==='edit'" placeholder="如 D0002 / N0002" />
          </div>
          <div class="field" v-if="formMode==='create'">
            <div class="label">密码</div>
            <input class="inp" v-model="form.password" placeholder="不填默认 123456" />
          </div>
          <div class="field">
            <div class="label">电话</div>
            <input class="inp" v-model="form.phone" placeholder="可空" />
          </div>
          <div class="field">
            <div class="label">角色</div>
            <select class="inp" v-model="form.role">
              <option value="ADMIN">ADMIN</option>
              <option value="DOCTOR">DOCTOR</option>
              <option value="NURSE">NURSE</option>
              <option value="PHARMACIST">PHARMACIST</option>
              <option value="PATIENT">PATIENT</option>
            </select>
          </div>
          <div class="field" v-if="formMode==='edit'">
            <div class="label">状态</div>
            <select class="inp" v-model.number="form.status">
              <option value="1">启用</option>
              <option value="0">停用</option>
            </select>
          </div>
          <div class="field">
            <div class="label">绑定类型</div>
            <select class="inp" v-model="form.bindType" @change="onBindTypeChange">
              <option value="NONE">不绑定/解绑</option>
              <option value="DOCTOR">医生</option>
              <option value="NURSE">护士</option>
              <option value="PHARMACIST">药师</option>
            </select>
          </div>
          <div class="field" v-if="form.bindType !== 'NONE'">
            <div class="label">绑定对象</div>
            <select class="inp" v-model.number="form.bindProfileId">
              <option :value="0">请选择</option>
              <option v-for="opt in currentOptions" :key="opt.id" :value="opt.id">
                {{ opt.name }}（id={{ opt.id }}，userId={{ opt.userId ?? '-' }}）
              </option>
            </select>
            <div class="hint">建议选 userId 为空的（未绑定）</div>
          </div>
        </div>
        <div class="m-ops">
          <button class="btn primary-outline" @click="closeModal">取消</button>
          <button class="btn primary" @click="submit">{{ formMode==='create' ? '创建' : '保存' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { adminGetUsers, adminCreateUser, adminUpdateUser, adminEnableUser, adminDisableUser, adminResetPassword, adminOptionsDoctors, adminOptionsNurses, adminOptionsPharmacists } from "@/api/admin";

const loading = ref(false);
const rows = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);

const q = reactive({
  keyword: "",
  role: "",
  status: ""
});

const showModal = ref(false);
const formMode = ref("create");
const editingId = ref(null);

const form = reactive({
  loginName: "",
  password: "",
  phone: "",
  role: "PATIENT",
  status: 1,
  bindType: "NONE",
  bindProfileId: 0
});

const options = reactive({
  doctors: [],
  nurses: [],
  pharmacists: []
});

const currentOptions = computed(() => {
  if (form.bindType === "DOCTOR") return options.doctors;
  if (form.bindType === "NURSE") return options.nurses;
  if (form.bindType === "PHARMACIST") return options.pharmacists;
  return [];
});

function fmtTime(s) {
  if (!s) return "-";
  return String(s).replace("T", " ").slice(0, 19);
}

function normalizeErr(e) {
  const msg = e?.response?.data?.msg || e?.response?.data?.message || e?.message || "操作失败";
  return msg;
}

function resetQuery() {
  q.keyword = "";
  q.role = "";
  q.status = "";
  page.value = 0;
  load();
}

function go(p) {
  page.value = p;
  load();
}

async function load() {
  loading.value = true;
  try {
    const params = {
      keyword: q.keyword || undefined,
      role: q.role || undefined,
      status: q.status === "" ? undefined : q.status,
      page: page.value,
      size: size.value
    };
    const pageObj = await adminGetUsers(params);
    rows.value = pageObj.content || [];
    total.value = pageObj.totalElements ?? 0;
  } catch (e) {
    alert(normalizeErr(e));
  } finally {
    loading.value = false;
  }
}

async function loadOptionsOnce() {
  try {
    if (options.doctors.length === 0) options.doctors = await adminOptionsDoctors();
    if (options.nurses.length === 0) options.nurses = await adminOptionsNurses();
    if (options.pharmacists.length === 0) options.pharmacists = await adminOptionsPharmacists();
  } catch (e) {
    alert("加载绑定选项失败：" + normalizeErr(e));
  }
}

function openCreate() {
  formMode.value = "create";
  editingId.value = null;
  form.loginName = "";
  form.password = "";
  form.phone = "";
  form.role = "PATIENT";
  form.status = 1;
  form.bindType = "NONE";
  form.bindProfileId = 0;
  showModal.value = true;
  loadOptionsOnce();
}

function openEdit(u) {
  formMode.value = "edit";
  editingId.value = u.userId;
  form.loginName = u.loginName;
  form.password = "";
  form.phone = u.phone || "";
  form.role = u.role || "PATIENT";
  form.status = u.status ?? 1;
  form.bindType = (u.bindType && u.bindType !== "-") ? u.bindType : "NONE";
  form.bindProfileId = (u.bindId ?? 0) || 0;
  showModal.value = true;
  loadOptionsOnce();
}

function closeModal() {
  showModal.value = false;
}

function onBindTypeChange() {
  form.bindProfileId = 0;
}

async function submit() {
  try {
    if (formMode.value === "create") {
      if (!form.loginName) return alert("登录名不能为空");
      const payload = {
        loginName: form.loginName,
        password: form.password || "",
        phone: form.phone || "",
        role: form.role,
        bindType: form.bindType,
        bindProfileId: form.bindType === "NONE" ? null : (form.bindProfileId || null)
      };
      if (payload.bindType !== "NONE" && !payload.bindProfileId) return alert("请选择绑定对象");
      await adminCreateUser(payload);
      closeModal();
      page.value = 0;
      load();
      return;
    }
    const payload = {
      phone: form.phone || "",
      role: form.role,
      status: form.status,
      bindType: form.bindType,
      bindProfileId: form.bindType === "NONE" ? null : (form.bindProfileId || null)
    };
    if (payload.bindType !== "NONE" && !payload.bindProfileId) return alert("请选择绑定对象");
    await adminUpdateUser(editingId.value, payload);
    closeModal();
    load();
  } catch (e) {
    alert(normalizeErr(e));
  }
}

async function toggleStatus(u) {
  try {
    if (u.status === 1) await adminDisableUser(u.userId);
    else await adminEnableUser(u.userId);
    load();
  } catch (e) {
    alert(normalizeErr(e));
  }
}

async function resetPwd(u) {
  const pwd = prompt(`给账号 ${u.loginName} 设置新密码（不填默认123456）：`) ?? "";
  try {
    await adminResetPassword(u.userId, pwd);
    alert("重置成功");
  } catch (e) {
    alert(normalizeErr(e));
  }
}

onMounted(load);
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
.page-container { display:flex; flex-direction:column; gap:14px; }
.card {
  background:#fff; border:1px solid #eef2f7;
  border-radius:16px; padding:14px;
  box-shadow: 0 1px 2px rgba(16,24,40,.04);
}
.row { display:flex; gap:12px; align-items:end; flex-wrap:wrap; }
.field { display:flex; flex-direction:column; gap:6px; }
.label { font-size:12px; color:#6b7280; }
.inp {
  height:36px; padding:0 10px; border-radius:10px;
  border:1px solid #e5e7eb; outline:none; min-width:180px;
}
.ops { margin-left:auto; display:flex; gap:10px; align-items:center; }

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
.tbl th, .tbl td { border-bottom:1px solid #eef2f7; padding:10px; text-align:left; }
.tbl th { font-size:12px; color:#6b7280; font-weight:700; }

/* 操作链接（蓝色） */
.actions { display:flex; gap:10px; }
.link.primary { 
  border:none; background:none; color:#2563eb; 
  cursor:pointer; padding:0; font-size:14px;
}
.link.danger { 
  border:none; background:none; color:#ef4444; 
  cursor:pointer; padding:0; font-size:14px;
}

/* 其他样式 */
.tag { padding:2px 8px; border-radius:999px; font-size:12px; }
.tag.ok { background:rgba(16,185,129,.12); color:#059669; }
.tag.bad { background:rgba(239,68,68,.12); color:#dc2626; }
.muted { color:#6b7280; }
.empty { text-align:center; color:#6b7280; padding:18px; }
.pager { display:flex; justify-content:space-between; align-items:center; padding-top:10px; }
.pbtns { display:flex; gap:10px; align-items:center; }
.mask {
  position:fixed; inset:0; background:rgba(0,0,0,.35);
  display:flex; align-items:center; justify-content:center;
}
.modal {
  width:min(920px, 92vw);
  background:#fff; border-radius:18px; padding:16px;
  border:1px solid #eef2f7;
}
.m-title { font-weight:800; color:#111827; margin-bottom:10px; }
.m-grid { display:grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap:12px; }
.m-ops { margin-top:14px; display:flex; justify-content:flex-end; gap:10px; }
.hint { margin-top:6px; font-size:12px; color:#6b7280; }
</style>