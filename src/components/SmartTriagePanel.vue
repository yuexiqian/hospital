<template>
  <div class="triage-wrapper">
    <!-- 添加中央标题 -->
    <div class="main-header">
      <h2 class="main-title">智能分诊与挂号</h2>
    </div>
    <!-- 顶部：科室切换 + 科室/医生介绍 -->
    <div class="dept-intro-block section" v-if="depts && depts.length">
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
      <div class="left-panel section">
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
      <div class="right-panel section">
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
        <div v-if="mode === 'triage'" class="result-panel triage-result-panel section">
          <h2 class="panel-title">分诊结果</h2>

          <p v-if="!triageResult" class="placeholder">
            请在左侧填写症状后，点击"开始智能分诊"，系统将为您推荐科室与医生。
          </p>

          <div v-else>
            <div class="card triage-card section">
              <h3>推荐科室</h3>
              <p>主推荐科室：<strong>{{ triageResult.mainDeptName }}</strong></p>
              <p v-if="triageResult.backupDeptName">
                备选科室：{{ triageResult.backupDeptName }}
              </p>
              <p class="reason">推荐理由：{{ triageResult.reason }}</p>
            </div>

            <div class="card triage-card section">
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

            <div class="card triage-card section">
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
        <div v-else class="result-panel section">
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
      <div class="dialog section">
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
              <select v-model="newPatient.idType" class="input">
                <option disabled value="">请选择</option>
                <option value="身份证">身份证</option>
                <option value="护照">护照</option>
                <option value="医保">医保</option>
              </select>
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
      // 根据你的科室表扩充不适部位选项
      bodyParts: ['胸部', '腹部', '头部', '四肢', '皮肤', '眼部', '耳鼻喉', '神经系统', '泌尿系统', '女性生殖系统'],
      
      // 智能分诊规则映射：症状 -> 科室推荐
      triageRules: {
        // 胸部症状 -> 呼吸内科、心血管内科
        '胸部': {
          '咳嗽': { main: '呼吸内科', backup: '全科门诊', reason: '咳嗽是呼吸系统常见症状，呼吸内科专门诊治此类问题' },
          '胸闷': { main: '心血管内科', backup: '呼吸内科', reason: '胸闷可能与心脏或肺部问题相关，心血管内科优先排查' },
          '呼吸困难': { main: '呼吸内科', backup: '心血管内科', reason: '呼吸困难常见于呼吸系统疾病，需呼吸内科专业评估' },
          '胸痛': { main: '心血管内科', backup: '急诊科', reason: '胸痛需排除心脏问题，心血管内科专业评估，严重时需急诊' },
          '发热': { main: '呼吸内科', backup: '全科门诊', reason: '发热常伴随呼吸道感染，呼吸内科专业诊治' }
        },
        // 腹部症状 -> 消化内科、全科门诊
        '腹部': {
          '腹痛': { main: '消化内科', backup: '全科门诊', reason: '腹痛是消化系统常见症状，消化内科专业诊治' },
          '恶心': { main: '消化内科', backup: '全科门诊', reason: '恶心常伴随消化道问题，消化内科专业评估' },
          '呕吐': { main: '消化内科', backup: '全科门诊', reason: '呕吐需消化内科检查以确定病因' },
          '腹泻': { main: '消化内科', backup: '全科门诊', reason: '腹泻常见于消化道感染或功能紊乱' },
          '便秘': { main: '消化内科', backup: '全科门诊', reason: '便秘需消化内科评估肠道功能' }
        },
        // 头部症状 -> 神经内科、心血管内科
        '头部': {
          '头痛': { main: '神经内科', backup: '全科门诊', reason: '头痛需神经内科专业评估神经系统问题' },
          '眩晕': { main: '神经内科', backup: '耳鼻喉科', reason: '眩晕可能涉及神经系统或耳部问题' },
          '恶心': { main: '神经内科', backup: '消化内科', reason: '伴随头痛的恶心可能为神经系统问题' },
          '视物模糊': { main: '眼科', backup: '神经内科', reason: '视物模糊需眼科检查视力问题' }
        },
        // 四肢症状 -> 骨科、神经内科
        '四肢': {
          '关节疼痛': { main: '骨科', backup: '全科门诊', reason: '关节疼痛需骨科专业评估骨骼关节问题' },
          '肿胀': { main: '骨科', backup: '心血管内科', reason: '四肢肿胀可能为骨科或循环系统问题' },
          '活动受限': { main: '骨科', backup: '神经内科', reason: '活动受限可能为骨骼或神经系统问题' }
        },
        // 皮肤症状 -> 皮肤科、全科门诊
        '皮肤': {
          '皮疹': { main: '皮肤科', backup: '全科门诊', reason: '皮疹需皮肤科专业诊断皮肤病类型' },
          '瘙痒': { main: '皮肤科', backup: '全科门诊', reason: '皮肤瘙痒常见于皮肤过敏或皮炎' },
          '红肿': { main: '皮肤科', backup: '全科门诊', reason: '皮肤红肿可能为炎症或过敏反应' },
          '过敏': { main: '皮肤科', backup: '全科门诊', reason: '皮肤过敏需皮肤科专业治疗' }
        },
        // 眼部症状 -> 眼科
        '眼部': {
          '视力下降': { main: '眼科', backup: '神经内科', reason: '视力下降需眼科专业检查眼部问题' },
          '眼睛疼痛': { main: '眼科', backup: '全科门诊', reason: '眼睛疼痛需眼科排除眼部疾病' },
          '流泪': { main: '眼科', backup: '全科门诊', reason: '异常流泪需眼科检查泪道功能' },
          '眼红': { main: '眼科', backup: '全科门诊', reason: '眼红可能为结膜炎等眼部疾病' }
        },
        // 耳鼻喉症状 -> 耳鼻喉科
        '耳鼻喉': {
          '耳鸣': { main: '耳鼻喉科', backup: '神经内科', reason: '耳鸣需耳鼻喉科检查耳部问题' },
          '听力下降': { main: '耳鼻喉科', backup: '神经内科', reason: '听力下降需耳鼻喉科专业评估' },
          '鼻塞': { main: '耳鼻喉科', backup: '全科门诊', reason: '鼻塞常见于鼻炎或鼻窦问题' },
          '喉咙痛': { main: '耳鼻喉科', backup: '全科门诊', reason: '喉咙痛需耳鼻喉科检查咽喉部' }
        },
        // 神经系统症状 -> 神经内科
        '神经系统': {
          '肢体麻木': { main: '神经内科', backup: '骨科', reason: '肢体麻木需神经内科评估神经系统功能' },
          '记忆力减退': { main: '神经内科', backup: '全科门诊', reason: '记忆力减退可能为神经系统问题' },
          '言语不清': { main: '神经内科', backup: '急诊科', reason: '言语不清需警惕脑血管问题' },
          '走路不稳': { main: '神经内科', backup: '骨科', reason: '走路不稳可能为神经系统或骨骼问题' }
        },
        // 泌尿系统症状 -> 肾内科、泌尿外科
        '泌尿系统': {
          '尿频': { main: '肾内科', backup: '泌尿外科', reason: '尿频可能为肾脏或泌尿系统问题' },
          '尿急': { main: '肾内科', backup: '泌尿外科', reason: '尿急需肾内科检查泌尿功能' },
          '尿痛': { main: '肾内科', backup: '泌尿外科', reason: '尿痛可能为尿路感染' },
          '血尿': { main: '肾内科', backup: '泌尿外科', reason: '血尿需紧急评估肾脏或泌尿系统' }
        },
        // 女性生殖系统症状 -> 妇产科
        '女性生殖系统': {
          '月经不调': { main: '妇产科', backup: '内分泌科', reason: '月经不调需妇产科专业评估' },
          '下腹痛': { main: '妇产科', backup: '消化内科', reason: '女性下腹痛需排除妇科问题' },
          '白带异常': { main: '妇产科', backup: '全科门诊', reason: '白带异常常见于妇科炎症' },
          '乳房肿块': { main: '妇产科', backup: '乳腺外科', reason: '乳房肿块需妇产科或乳腺专科评估' }
        }
      },
      
      // 扩充症状选项
      symptomOptions: {
        '胸部': ['咳嗽', '胸闷', '呼吸困难', '胸痛', '发热', '咳痰', '心悸', '气短'],
        '腹部': ['腹痛', '恶心', '呕吐', '腹泻', '便秘', '腹胀', '反酸', '烧心'],
        '头部': ['头痛', '眩晕', '恶心', '视物模糊', '头晕', '失眠', '记忆力减退'],
        '四肢': ['关节疼痛', '肿胀', '活动受限', '麻木', '无力', '肌肉酸痛'],
        '皮肤': ['皮疹', '瘙痒', '红肿', '过敏', '脱皮', '水泡', '色素沉着'],
        '眼部': ['视力下降', '眼睛疼痛', '流泪', '眼红', '异物感', '视物模糊'],
        '耳鼻喉': ['耳鸣', '听力下降', '鼻塞', '喉咙痛', '流鼻涕', '打喷嚏', '声音嘶哑'],
        '神经系统': ['肢体麻木', '记忆力减退', '言语不清', '走路不稳', '抽搐', '意识障碍'],
        '泌尿系统': ['尿频', '尿急', '尿痛', '血尿', '尿不尽', '腰痛'],
        '女性生殖系统': ['月经不调', '下腹痛', '白带异常', '乳房肿块', '异常出血', '外阴瘙痒']
      },
      
      // 紧急症状 - 需要急诊处理
      emergencySymptoms: ['胸痛', '呼吸困难', '意识不清', '严重外伤', '大量出血', '持续高热'],
      
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
    },
    
    // 检查是否有急诊症状
    hasEmergencySymptoms() {
      return this.formTriage.symptoms.some(symptom => 
        this.emergencySymptoms.includes(symptom)
      );
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
      
      // 自动检查是否含有急诊症状
      if (this.hasEmergencySymptoms) {
        this.formTriage.emergency = true;
      }
    },

    // 智能分诊逻辑
    async submitTriage() {
      if (!this.formTriage.bodyPart) {
        alert('请先选择不适部位');
        return;
      }
      
      if (this.formTriage.symptoms.length === 0) {
        alert('请至少选择一个主要症状');
        return;
      }
      
      // 检查是否含有急诊症状
      if (this.hasEmergencySymptoms || this.formTriage.emergency) {
        const confirmResult = confirm('您的症状可能为急诊情况，建议立即前往急诊科就诊。是否继续分诊？');
        if (!confirmResult) {
          return;
        }
      }
      
      try {
        // 本地智能分诊逻辑
        const triageResult = this.calculateTriageResult();
        
        if (triageResult) {
          this.triageResult = triageResult;
          this.mode = 'triage';
          this.selectedDoctorId = null;
          
          // 同时调用后端接口记录分诊结果
          await this.callTriageApi();
        } else {
          alert('无法确定推荐科室，请咨询导诊台或选择直接挂号。');
        }
      } catch (e) {
        console.error(e);
        alert('智能分诊处理异常');
      }
    },
    
    // 计算智能分诊结果
    calculateTriageResult() {
      const { bodyPart, symptoms } = this.formTriage;
      
      if (!bodyPart || symptoms.length === 0) return null;
      
      // 查找科室规则
      const bodyPartRules = this.triageRules[bodyPart];
      if (!bodyPartRules) return null;
      
      // 统计症状对应的科室
      const deptScores = {};
      
      symptoms.forEach(symptom => {
        const rule = bodyPartRules[symptom];
        if (rule) {
          // 主推荐科室加分
          deptScores[rule.main] = (deptScores[rule.main] || 0) + 2;
          // 备选科室加分
          deptScores[rule.backup] = (deptScores[rule.backup] || 0) + 1;
        }
      });
      
      // 找出得分最高的科室
      let mainDeptName = null;
      let mainDeptScore = 0;
      let backupDeptName = null;
      let backupDeptScore = 0;
      
      Object.entries(deptScores).forEach(([deptName, score]) => {
        if (score > mainDeptScore) {
          backupDeptScore = mainDeptScore;
          backupDeptName = mainDeptName;
          mainDeptScore = score;
          mainDeptName = deptName;
        } else if (score > backupDeptScore && deptName !== mainDeptName) {
          backupDeptScore = score;
          backupDeptName = deptName;
        }
      });
      
      if (!mainDeptName) return null;
      
      // 查找科室对应的deptId
      const mainDept = this.depts.find(d => d.name === mainDeptName);
      const backupDept = this.depts.find(d => d.name === backupDeptName);
      
      // 生成推荐理由
      const firstSymptom = symptoms[0];
      const rule = bodyPartRules[firstSymptom];
      const reason = rule ? rule.reason : `根据您的症状（${symptoms.join('、')}），推荐${mainDeptName}就诊`;
      
      // 模拟候诊情况
      const queueInfo = [
        {
          deptId: mainDept ? mainDept.id : null,
          deptName: mainDeptName,
          waitCount: Math.floor(Math.random() * 20) + 5,
          estimateWaitMin: Math.floor(Math.random() * 60) + 30
        }
      ];
      
      if (backupDeptName) {
        queueInfo.push({
          deptId: backupDept ? backupDept.id : null,
          deptName: backupDeptName,
          waitCount: Math.floor(Math.random() * 15) + 3,
          estimateWaitMin: Math.floor(Math.random() * 45) + 20
        });
      }
      
      // 获取推荐科室的医生
      const recommendedDoctors = [];
      if (mainDept) {
        // 这里需要根据实际接口获取医生，暂时模拟
        recommendedDoctors.push({
          doctorId: 5,
          doctorName: '陈华',
          title: '主任医师',
          waitCount: Math.floor(Math.random() * 10) + 1,
          specialty: '慢性阻塞性肺疾病、哮喘'
        });
        
        recommendedDoctors.push({
          doctorId: 6,
          doctorName: '林芳',
          title: '主任医师',
          waitCount: Math.floor(Math.random() * 8) + 2,
          specialty: '肺部感染、支气管扩张'
        });
      }
      
      return {
        mainDeptId: mainDept ? mainDept.id : null,
        mainDeptName,
        backupDeptId: backupDept ? backupDept.id : null,
        backupDeptName,
        reason,
        queueInfo,
        doctors: recommendedDoctors
      };
    },
    
    // 调用后端分诊API
    async callTriageApi() {
      try {
        const payload = { 
          ...this.formTriage,
          userId: this.userId,
          triageTime: new Date().toISOString()
        };
        const resp = await postTriageAdvise(payload);
        if (resp.data.code !== 0) {
          console.warn('智能分诊记录保存失败:', resp.data.msg);
        }
      } catch (e) {
        console.error('智能分诊API异常:', e);
        // 不干扰用户使用，仅记录错误
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
        alert('请先在"推荐医生"中选择一位医生');
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
        alert('挂号成功！可在首页和"候诊队列"查看排队情况。');
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
  gap: 1rem;
  background: transparent;
}

/* 通用section样式 - 核心修改 */
.section {
  background: rgba(240, 248, 255, 0.7) !important; /* 透明淡蓝色背景 */
  backdrop-filter: blur(5px) !important; /* 毛玻璃效果 */
  border: 1px solid rgba(77, 171, 247, 0.2) !important; /* 淡蓝色边框 */
  box-shadow: 0 4px 16px rgba(30, 111, 217, 0.08) !important; /* 蓝色系阴影 */
  border-radius: 12px;
  padding: 1rem;
}

.main-header {
  text-align: center;
  margin-bottom: 0.5rem;
  background: rgba(240, 248, 255, 0.7);
  border-radius: 12px;
  padding: 1rem;
  border: 1px solid rgba(77, 171, 247, 0.2);
  backdrop-filter: blur(5px);
  box-shadow: 0 4px 16px rgba(30, 111, 217, 0.08);
}

.main-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e6fd9;
  margin: 0;
  background: linear-gradient(135deg, #1e6fd9 0%, #0d4ba0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 中部左右布局 */
.triage-layout {
  display: flex;
  gap: 1rem;
}

.left-panel, .right-panel {
  flex: 1;
  border-radius: 12px;
  padding: 1.5rem;
}

.dept-tabs {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.tabs-label {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
}

/* ========== 科室选项键：白底黑字 ========== */
.dept-tab {
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #ffffff !important;
  font-size: 0.875rem;
  color: #000000 !important;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
}

.dept-tab.active {
  background: #2563eb !important;
  color: #ffffff !important;
  border-color: #2563eb;
}

.dept-tab:hover:not(.active) {
  border-color: #3b82f6;
  background: #ffffff !important;
  color: #000000 !important;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.1);
}

.panel-title {
  margin-bottom: 1rem;
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e40af;
}

.form-group {
  margin-bottom: 1rem;
}

.form-row {
  display: flex;
  gap: 0.75rem;
}

.form-row .half {
  flex: 1;
}

/* 核心修改：所有输入框/选择框/文本域改为白框黑字 */
.input,
select,
textarea {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-sizing: border-box;
  font-size: 0.875rem;
  color: #000000;
  background-color: #ffffff;
  transition: border-color 0.2s ease;
}

.input:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.textarea {
  min-height: 60px;
  resize: vertical;
}

.body-part-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag-btn {
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #f9fafb;
  cursor: pointer;
  font-size: 0.875rem;
  color: #64748b;
  transition: all 0.2s ease;
}

.tag-btn.active {
  background: #3b82f6;
  color: #fff;
  border-color: #3b82f6;
}

.tag-btn:hover {
  transform: translateY(-1px);
}

.form-actions {
  margin-top: 1rem;
  display: flex;
  gap: 0.75rem;
}

.primary-btn,
.secondary-btn,
.tab-btn {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: 1px solid transparent;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 600;
  transition: all 0.2s ease;
}

.primary-btn {
  background: #3b82f6;
  color: #fff;
  border-color: #3b82f6;
}

.primary-btn:hover:not(:disabled) {
  background: #2563eb;
  transform: translateY(-1px);
}

.primary-btn:disabled {
  background: #94a3b8;
  border-color: #94a3b8;
  cursor: not-allowed;
  transform: none;
}

.secondary-btn {
  background: #fff;
  color: #64748b;
  border-color: #e2e8f0;
}

.secondary-btn:hover {
  background: #f8fafc;
  transform: translateY(-1px);
}

.divider {
  margin: 1rem 0;
  border: none;
  border-top: 1px dashed #e2e8f0;
}

.tip {
  font-size: 0.875rem;
  color: #475569;
  margin-bottom: 0.75rem;
  line-height: 1.5;
}

.mode-toggle {
  margin-bottom: 1rem;
}

.tab-btn {
  margin-right: 0.5rem;
  background: #f8fafc;
  color: #64748b;
}

.tab-btn.active {
  background: #3b82f6;
  color: #fff;
  border-color: #3b82f6;
}

.tab-btn:hover {
  transform: translateY(-1px);
}

.placeholder {
  color: #475569;
  font-size: 0.875rem;
  text-align: center;
  padding: 1rem;
}

/* 分诊结果卡片样式优化 */
.triage-card {
  margin-bottom: 1rem;
}

.triage-card h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #1e40af;
  margin: 0 0 0.5rem 0;
}

.triage-card p {
  font-size: 0.875rem;
  color: #1e3a8a;
  margin: 0.25rem 0;
  line-height: 1.5;
}

.triage-card p.reason {
  color: #1e3a8a;
  font-style: italic;
}

.queue-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.queue-table th,
.queue-table td {
  border: 1px solid #f1f5f9;
  padding: 0.5rem 0.75rem;
  text-align: center;
}

.queue-table th {
  background-color: #f8fafc;
  color: #475569;
  font-weight: 600;
}

.queue-table td {
  color: #334155;
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
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  margin-bottom: 0.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.doctor-list li:hover {
  border-color: #3b82f6;
  background: #f0f9ff;
}

.doctor-list li.selected {
  border-color: #3b82f6;
  background: #eff6ff;
}

.doctor-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.doctor-title {
  font-size: 0.75rem;
  padding: 0.125rem 0.375rem;
  border-radius: 999px;
  background: #e2e8f0;
  color: #475569;
}

.doctor-wait {
  font-size: 0.75rem;
  color: #64748b;
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
  padding: 1.5rem;
}

.dialog h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 1rem 0;
}

.dialog h4 {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 1rem 0 0.75rem 0;
}

.dialog-body {
  max-height: 340px;
  overflow: auto;
  margin: 0.5rem 0;
}

.patient-list {
  list-style: none;
  padding: 0;
  margin: 0 0 1rem 0;
}

.patient-list li {
  margin-bottom: 0.5rem;
}

.patient-list label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #334155;
  cursor: pointer;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1rem;
}
</style>