<template>
  <div class="triage-wrapper">
    <!-- 顶部：科室切换 + 科室/医生介绍 -->
    <div class="dept-intro-block" v-if="depts && depts.length">
      <div class="dept-tabs">
        <span class="tabs-label">科室：</span>
        <button
          v-for="d in depts"
          :key="d.id"
          type="button"
          class="dept-tab"
          :class="{ active: d.id === currentDeptId }"
          @click="switchDeptFromIntro(d.id)"
        >
          {{ d.name }}
        </button>
      </div>

      <DeptDoctorIntro
        v-if="overview"
        :dept="overview.dept"
        :doctors="overview.doctors"
        :selectedDoctorId="selectedDoctorId"
        @select-doctor="handleSelectDoctorFromIntro"
      />
    </div>

    <!-- 中部：左侧智能分诊 + 右侧分诊结果/直接挂号 -->
    <div class="triage-layout">
      <!-- 左侧：智能分诊 + 直接挂号入口 -->
      <div class="left-panel">
        <h2 class="panel-title">智能分诊</h2>

        <!-- 步骤一：选择不适部位 -->
        <div class="form-group">
          <label>① 不适部位</label>
          <div class="body-part-list">
            <button
              v-for="part in bodyParts"
              :key="part"
              type="button"
              class="tag-btn"
              :class="{ active: formTriage.bodyPart === part }"
              @click="formTriage.bodyPart = part"
            >
              {{ part }}
            </button>
          </div>
        </div>

        <!-- 步骤二：症状（多选） -->
        <div class="form-group">
          <label>② 主要症状（可多选）</label>
          <div class="body-part-list">
            <button
              v-for="sym in currentSymptomOptions"
              :key="sym"
              type="button"
              class="tag-btn"
              :class="{ active: formTriage.symptoms.includes(sym) }"
              @click="toggleSymptom(sym)"
            >
              {{ sym }}
            </button>
          </div>
          <textarea
            v-model="formTriage.extraDesc"
            placeholder="可补充症状描述（选填）"
            class="textarea"
          ></textarea>
        </div>

        <!-- 步骤三：病情程度与持续时间 -->
        <div class="form-row">
          <div class="form-group half">
            <label>③ 病情程度</label>
            <select v-model="formTriage.severity" class="input">
              <option disabled value="">请选择</option>
              <option>轻度</option>
              <option>中度</option>
              <option>重度</option>
            </select>
          </div>
          <div class="form-group half">
            <label>④ 持续时间</label>
            <select v-model="formTriage.duration" class="input">
              <option disabled value="">请选择</option>
              <option>&lt;24h</option>
              <option>1-3天</option>
              <option>&gt;3天</option>
            </select>
          </div>
        </div>

        <div class="form-group">
          <label>
            <input type="checkbox" v-model="formTriage.emergency" />
            是否疑似急诊（胸痛、呼吸困难、意识不清等）
          </label>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="button" class="primary-btn" @click="submitTriage">
            开始智能分诊
          </button>
        </div>

        <hr class="divider" />

        <!-- 直接挂号入口 -->
        <div class="direct-register">
          <p class="tip">
            已经咨询过护士或熟悉流程？可以跳过智能分诊，直接挂号。
          </p>
          <button type="button" class="secondary-btn" @click="switchToDirectMode">
            我已知道科室，直接挂号
          </button>
        </div>
      </div>

      <!-- 右侧：分诊结果 / 快速挂号 -->
      <div class="right-panel">
        <div class="mode-toggle">
          <button
            type="button"
            class="tab-btn"
            :class="{ active: mode === 'triage' }"
            @click="switchToTriageMode"
          >
            分诊结果
          </button>
          <button
            type="button"
            class="tab-btn"
            :class="{ active: mode === 'direct' }"
            @click="switchToDirectMode"
          >
            直接/快速挂号
          </button>
        </div>

        <!-- 模式 A：智能分诊结果 -->
        <div v-if="mode === 'triage'" class="result-panel">
          <h2 class="panel-title">分诊结果</h2>

          <p v-if="!triageResult" class="placeholder">
            请在左侧填写症状后，点击“开始智能分诊”，系统将为您推荐科室与医生。
          </p>

          <div v-else>
            <div class="card">
              <h3>推荐科室</h3>
              <p>主推荐科室：<strong>{{ triageResult.mainDeptName }}</strong></p>
              <p v-if="triageResult.backupDeptName">
                备选科室：{{ triageResult.backupDeptName }}
              </p>
              <p class="reason">推荐理由：{{ triageResult.reason }}</p>
            </div>

            <div class="card">
              <h3>科室候诊情况</h3>
              <table class="queue-table">
                <thead>
                  <tr>
                    <th>科室</th>
                    <th>候诊人数</th>
                    <th>预计等待时间</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="q in triageResult.queueInfo" :key="q.deptId">
                    <td>{{ q.deptName }}</td>
                    <td>{{ q.waitCount }} 人</td>
                    <td>{{ q.estimateWaitMin }} 分钟</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="card">
              <h3>推荐医生</h3>
              <p v-if="!triageResult.doctors || !triageResult.doctors.length">
                当前暂无推荐医生信息。
              </p>
              <ul v-else class="doctor-list">
                <li
                  v-for="doc in triageResult.doctors"
                  :key="doc.doctorId || doc.id"
                  :class="{
                    selected: selectedDoctorId === (doc.doctorId || doc.id)
                  }"
                  @click="selectDoctor(doc)"
                >
                  <div class="doctor-name">
                    {{ doc.doctorName || doc.name }}
                    <span class="doctor-title">{{ doc.title }}</span>
                  </div>
                  <div class="doctor-wait">
                    候诊人数：{{ doc.waitCount ?? 0 }} 人
                  </div>
                </li>
              </ul>
            </div>

            <div class="form-actions">
              <button
                type="button"
                class="primary-btn"
                :disabled="!triageResult"
                @click="oneClickRegister"
              >
                一键挂号到推荐科室
              </button>
              <button
                type="button"
                class="secondary-btn"
                @click="switchToDirectMode"
              >
                选择其他科室/医生挂号
              </button>
            </div>
          </div>
        </div>

        <!-- 模式 B：快速/直接挂号 -->
        <div v-else class="result-panel">
          <h2 class="panel-title">快速/直接挂号</h2>

          <div class="form-group">
            <label>科室</label>
            <select v-model="direct.deptId" class="input" @change="onDeptChange">
              <option disabled value="">请选择科室</option>
              <option v-for="d in depts" :key="d.id" :value="d.id">
                {{ d.name }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>医生</label>
            <select
              v-model="direct.doctorId"
              class="input"
              :disabled="!direct.deptId"
            >
              <option disabled value="">请选择医生</option>
              <option v-for="doc in doctors" :key="doc.id" :value="doc.id">
                {{ doc.name }}（{{ doc.title }}）
              </option>
            </select>
          </div>

          <div class="form-actions">
            <button
              type="button"
              class="primary-btn"
              :disabled="!direct.deptId || !direct.doctorId"
              @click="confirmDirectRegister"
            >
              确认挂号
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部：选择 / 新增就诊人 弹窗 -->
    <div v-if="showPatientDialog" class="dialog-mask">
      <div class="dialog">
        <h3>选择就诊人</h3>

        <div class="dialog-body">
          <div v-if="patients.length === 0" class="placeholder">
            当前暂无就诊人，请先新增。
          </div>

          <ul v-else class="patient-list">
            <li v-for="p in patients" :key="p.id || p.patientId">
              <label>
                <input
                  type="radio"
                  name="patient"
                  :value="p.id || p.patientId"
                  v-model="selectedPatientId"
                />
                {{ p.name }}（{{ p.phone }}）
              </label>
            </li>
          </ul>

          <hr />

          <h4>新增就诊人</h4>
          <div class="form-row">
            <div class="form-group half">
              <label>姓名</label>
              <input v-model="newPatient.name" class="input" />
            </div>
            <div class="form-group half">
              <label>电话</label>
              <input v-model="newPatient.phone" class="input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group half">
              <label>证件类型</label>
              <input
                v-model="newPatient.idType"
                class="input"
                placeholder="如：身份证"
              />
            </div>
            <div class="form-group half">
              <label>证件号</label>
              <input v-model="newPatient.idCard" class="input" />
            </div>
          </div>
        </div>

        <div class="dialog-actions">
          <button type="button" class="secondary-btn" @click="closePatientDialog">
            取消
          </button>
          <button type="button" class="secondary-btn" @click="saveNewPatient">
            保存并选中
          </button>
          <button
            type="button"
            class="primary-btn"
            :disabled="patients.length === 0"
            @click="submitRegister"
          >
            确认挂号
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getDepts,
  getDoctors,
  postTriageAdvise,
  getPatients,
  createPatient,
  createRegister,
  getDeptOverview
} from '@/api/triage';

import DeptDoctorIntro from './DeptDoctorIntro.vue';

export default {
  name: 'SmartTriagePanel',
  components: {
    DeptDoctorIntro
  },
  props: {
    userId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      mode: 'triage',
      bodyParts: ['胸部', '腹部', '头部', '四肢', '皮肤', '其他'],
      symptomOptions: {
        胸部: ['咳嗽', '胸闷', '呼吸困难', '胸痛', '发热'],
        腹部: ['腹痛', '恶心', '呕吐', '腹泻', '便秘'],
        头部: ['头痛', '眩晕', '恶心', '视物模糊'],
        四肢: ['关节疼痛', '肿胀', '活动受限'],
        皮肤: ['皮疹', '瘙痒', '红肿', '过敏'],
        其他: ['乏力', '食欲下降', '体重下降']
      },
      formTriage: {
        bodyPart: '',
        symptoms: [],
        severity: '',
        duration: '',
        emergency: false,
        extraDesc: ''
      },
      triageResult: null,
      selectedDoctorId: null,

      depts: [],
      doctors: [],
      overview: null,        // 科室+医生介绍
      currentDeptId: null,   // 顶部介绍区域当前选中的科室

      direct: {
        deptId: '',
        doctorId: ''
      },

      showPatientDialog: false,
      patients: [],
      selectedPatientId: null,
      newPatient: {
        name: '',
        phone: '',
        idType: '',
        idCard: ''
      },
      pendingRegister: null
    };
  },
  computed: {
    currentSymptomOptions() {
      return this.symptomOptions[this.formTriage.bodyPart] || [];
    }
  },
  created() {
    this.loadDepts();
  },
  methods: {
    async loadDepts() {
      try {
        const resp = await getDepts();
        this.depts = (resp.data && resp.data.data) || [];

        // 默认选中第一个科室
        if (this.depts.length > 0 && !this.direct.deptId) {
          this.currentDeptId = this.depts[0].id;
          this.direct.deptId = this.depts[0].id;
          await this.onDeptChange();
        }
      } catch (e) {
        console.error(e);
        alert('加载科室失败');
      }
    },

    async onDeptChange() {
      this.direct.doctorId = '';
      this.selectedDoctorId = null;

      if (!this.direct.deptId) {
        this.doctors = [];
        this.overview = null;
        return;
      }

      try {
        const [docResp, overviewResp] = await Promise.all([
          getDoctors(this.direct.deptId),
          getDeptOverview(this.direct.deptId)
        ]);

        this.doctors = (docResp.data && docResp.data.data) || [];
        this.overview = overviewResp.data && overviewResp.data.data;

        // 如果已经有选中的医生，并且在新列表里，就自动选中
        if (this.selectedDoctorId) {
          const exists = this.doctors.some(
            (d) => d.id === this.selectedDoctorId
          );
          if (exists) {
            this.direct.doctorId = this.selectedDoctorId;
          }
        }
      } catch (e) {
        console.error(e);
        alert('加载医生或科室介绍失败');
      }
    },

    // 顶部科室 tab 切换
    async switchDeptFromIntro(deptId) {
      if (deptId === this.currentDeptId) return;
      this.currentDeptId = deptId;
      this.direct.deptId = deptId; // 同步到右侧挂号区
      await this.onDeptChange();
    },

    // 从上方医生介绍区域选择医生
    async handleSelectDoctorFromIntro(doc) {
      if (!this.overview || !this.overview.dept) return;

      this.mode = 'direct';
      this.selectedDoctorId = doc.id;

      const deptId = this.overview.dept.id;

      // 如果当前挂号区科室不是这个科室，切过去
      if (this.direct.deptId !== deptId) {
        this.direct.deptId = deptId;
        this.currentDeptId = deptId;
        await this.onDeptChange();
      }

      // 确保医生列表里有这个医生
      const exists = this.doctors.some((d) => d.id === doc.id);
      if (!exists) {
        await this.onDeptChange();
      }

      this.direct.doctorId = doc.id;
    },

    toggleSymptom(sym) {
      const idx = this.formTriage.symptoms.indexOf(sym);
      if (idx >= 0) {
        this.formTriage.symptoms.splice(idx, 1);
      } else {
        this.formTriage.symptoms.push(sym);
      }
    },

    async submitTriage() {
      if (!this.formTriage.bodyPart) {
        alert('请先选择不适部位');
        return;
      }
      try {
        const payload = { ...this.formTriage };
        const resp = await postTriageAdvise(payload);
        if (resp.data.code !== 0) {
          alert(resp.data.msg || '智能分诊失败');
          return;
        }
        this.triageResult = resp.data.data;
        this.mode = 'triage';
        this.selectedDoctorId = null;
      } catch (e) {
        console.error(e);
        alert('智能分诊接口异常');
      }
    },

    switchToDirectMode() {
      this.mode = 'direct';
    },

    switchToTriageMode() {
      this.mode = 'triage';
    },

    selectDoctor(doc) {
      const id = doc.doctorId || doc.id;
      this.selectedDoctorId = id;
      if (this.triageResult) {
        this.direct.deptId = this.triageResult.mainDeptId;
        this.direct.doctorId = id;
      }
    },

    oneClickRegister() {
      if (!this.triageResult) {
        alert('请先进行智能分诊');
        return;
      }
      const deptId = this.triageResult.mainDeptId;
      const doctorId = this.selectedDoctorId || null;
      if (!doctorId) {
        alert('请先在“推荐医生”中选择一位医生');
        return;
      }
      this.pendingRegister = {
        deptId,
        doctorId,
        source: 'INTELLIGENT_TRIAGE'
      };
      this.openPatientDialog();
    },

    confirmDirectRegister() {
      if (!this.direct.deptId || !this.direct.doctorId) {
        alert('请先选择科室和医生');
        return;
      }
      this.pendingRegister = {
        deptId: this.direct.deptId,
        doctorId: this.direct.doctorId,
        source: 'MANUAL_PATIENT'
      };
      this.openPatientDialog();
    },

    // 打开就诊人弹窗：加载列表 + 默认选中第一个
    async openPatientDialog() {
      try {
        const resp = await getPatients(this.userId);
        const raw = resp.data;
        if (Array.isArray(raw)) {
          this.patients = raw;
        } else if (raw && Array.isArray(raw.data)) {
          this.patients = raw.data;
        } else {
          this.patients = [];
        }
      } catch (e) {
        console.error(e);
        alert('加载就诊人失败');
        this.patients = [];
      }

      if (this.patients.length > 0) {
        const first = this.patients[0];
        this.selectedPatientId = first.id || first.patientId;
      } else {
        this.selectedPatientId = null;
      }

      this.newPatient = { name: '', phone: '', idType: '', idCard: '' };
      this.showPatientDialog = true;
    },

    closePatientDialog() {
      this.showPatientDialog = false;
      this.pendingRegister = null;
    },

    async saveNewPatient() {
      if (!this.newPatient.name || !this.newPatient.phone) {
        alert('新增就诊人至少填写姓名和电话');
        return;
      }
      try {
        const payload = { ...this.newPatient };
        const resp = await createPatient(this.userId, payload);
        const rawCreated = resp.data;
        const created =
          rawCreated && rawCreated.data ? rawCreated.data : rawCreated;

        const listResp = await getPatients(this.userId);
        const listRaw = listResp.data;
        if (Array.isArray(listRaw)) {
          this.patients = listRaw;
        } else if (listRaw && Array.isArray(listRaw.data)) {
          this.patients = listRaw.data;
        } else {
          this.patients = [];
        }

        this.selectedPatientId =
          created && created.id
            ? created.id
            : created && created.patientId
            ? created.patientId
            : null;

        alert('新增就诊人成功');
      } catch (e) {
        console.error(e);
        alert('新增就诊人失败');
      }
    },

    async submitRegister() {
      if (!this.pendingRegister) {
        alert('内部错误：缺少挂号信息');
        return;
      }

      if (
        this.selectedPatientId === null ||
        this.selectedPatientId === undefined ||
        this.selectedPatientId === ''
      ) {
        alert('请先选择一个就诊人');
        return;
      }

      try {
        const payload = {
          patientId: this.selectedPatientId,
          deptId: this.pendingRegister.deptId,
          doctorId: this.pendingRegister.doctorId,
          source: this.pendingRegister.source
        };
        const resp = await createRegister(this.userId, payload);
        if (resp.data.code !== 0) {
          alert(resp.data.msg || '挂号失败');
          return;
        }
        alert('挂号成功！可在首页和“候诊队列”查看排队情况。');
        this.showPatientDialog = false;
        this.pendingRegister = null;
      } catch (e) {
        console.error('挂号接口异常', e);
        alert('挂号接口异常');
      }
    }
  }
};
</script>

<style scoped>
.triage-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 顶部科室介绍块 */
.dept-intro-block {
  background: #ffffff;
  border-radius: 10px;
  padding: 12px 16px 8px;
  box-shadow: 0 1px 4px rgba(15, 23, 42, 0.06);
}

.dept-tabs {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.tabs-label {
  font-size: 13px;
  color: #6b7280;
}

.dept-tab {
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
  font-size: 13px;
  cursor: pointer;
}

.dept-tab.active {
  background: #2563eb;
  color: #ffffff;
  border-color: #2563eb;
}

/* 中部左右布局 */
.triage-layout {
  display: flex;
  gap: 16px;
}

.left-panel,
.right-panel {
  flex: 1;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.panel-title {
  margin-bottom: 12px;
  font-size: 18px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 12px;
}

.form-row {
  display: flex;
  gap: 12px;
}

.form-row .half {
  flex: 1;
}

.input,
select,
textarea {
  width: 100%;
  padding: 6px 8px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  box-sizing: border-box;
}

.textarea {
  min-height: 60px;
  resize: vertical;
}

.body-part-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-btn {
  padding: 4px 10px;
  border-radius: 16px;
  border: 1px solid #dcdfe6;
  background: #fafafa;
  cursor: pointer;
  font-size: 13px;
}

.tag-btn.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.form-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.primary-btn,
.secondary-btn,
.tab-btn {
  padding: 6px 14px;
  border-radius: 4px;
  border: 1px solid transparent;
  cursor: pointer;
  font-size: 14px;
}

.primary-btn {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.primary-btn:disabled {
  background: #c0c4cc;
  border-color: #c0c4cc;
  cursor: not-allowed;
}

.secondary-btn {
  background: #fff;
  color: #606266;
  border-color: #dcdfe6;
}

.divider {
  margin: 16px 0;
  border: none;
  border-top: 1px dashed #e4e7ed;
}

.tip {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.mode-toggle {
  margin-bottom: 12px;
}

.tab-btn {
  margin-right: 8px;
  background: #f5f7fa;
}

.tab-btn.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.placeholder {
  color: #909399;
  font-size: 13px;
}

.card {
  margin-bottom: 12px;
  padding: 10px;
  border-radius: 6px;
  background: #f9fafc;
}

.queue-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.queue-table th,
.queue-table td {
  border: 1px solid #ebeef5;
  padding: 4px 6px;
  text-align: center;
}

.doctor-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.doctor-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 8px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  margin-bottom: 6px;
  cursor: pointer;
}

.doctor-list li.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

/* 弹窗样式 */
.dialog-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.dialog {
  width: 520px;
  max-width: 90%;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}

.dialog-body {
  max-height: 340px;
  overflow: auto;
  margin: 8px 0;
}

.patient-list {
  list-style: none;
  padding: 0;
  margin: 0 0 10px 0;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}
</style>
