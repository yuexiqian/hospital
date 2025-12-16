<template>
  <div class="intro-wrapper" v-if="dept">
    <!-- 科室介绍 -->
    <div class="intro-header">
      <div class="intro-title">
        <span class="tag">科室介绍</span>
        <span class="dept-name">{{ dept.name }}</span>
        <span class="dept-type" v-if="dept.type">（{{ dept.type }}）</span>
      </div>

      <div class="intro-meta">
        <span v-if="dept.location">位置：{{ dept.location }}</span>
        <span v-if="dept.openingHours">时间：{{ dept.openingHours }}</span>
        <span v-if="dept.floor">楼层：{{ dept.floor }}F</span>
      </div>

      <p class="intro-desc" v-if="dept.description">{{ dept.description }}</p>
      <p class="intro-desc light" v-if="dept.specialty">擅长：{{ dept.specialty }}</p>
    </div>

    <!-- 医生介绍 -->
    <div class="doctor-section">
      <div class="doctor-section-header">
        <span class="tag blue">医生介绍</span>
        <span class="tip">点击医生卡片即可在下方挂号区选中该医生</span>
      </div>

      <div class="doctor-list" v-if="doctors && doctors.length">
        <div
          v-for="doc in doctors"
          :key="doc.id"
          class="doctor-card"
          :class="{
            off: !doc.availableToday,
            full: doc.full,
            active: doc.id === selectedDoctorId
          }"
          @click="handleClick(doc)"
        >
          <div class="doctor-main">
            <div class="doctor-name-line">
              <span class="doctor-name">{{ doc.name }}</span>
              <span class="doctor-title" v-if="doc.title">{{ doc.title }}</span>
            </div>

            <div class="doctor-specialty" v-if="doc.specialty">
              擅长：{{ doc.specialty }}
            </div>
            <div class="doctor-schedule" v-if="doc.schedule">
              坐诊：{{ doc.schedule }}
            </div>
          </div>

          <div class="doctor-status">
            <span class="status-badge" :class="doc.availableToday ? 'on' : 'off'">
              {{ doc.availableToday ? '今日坐诊' : (doc.full ? '号源已满' : '今日停诊') }}
            </span>

            <span v-if="doc.dailyQuota && doc.dailyQuota > 0" class="quota">
              {{ doc.full ? '号源已满' : `剩余 ${doc.remaining ?? 0}/${doc.dailyQuota}` }}
            </span>
          </div>
        </div>
      </div>

      <div class="doctor-empty" v-else>
        暂无医生信息。
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DeptDoctorIntro',
  props: {
    dept: {
      type: Object,
      default: null
    },
    doctors: {
      type: Array,
      default: () => []
    },
    selectedDoctorId: {
      type: [Number, String],
      default: null
    }
  },
  emits: ['select-doctor'],
  methods: {
    handleClick(doc) {
      // 只有"今日坐诊 且 未满号"的医生允许选择
      if (!doc.availableToday || doc.full) {
        return;
      }
      this.$emit('select-doctor', doc);
    }
  }
};
</script>

<style scoped>
/* ========== 核心修改：科室选项键 强制白底黑字 ========== */

.dept-tab {
  padding: 0.375rem 0.75rem;
  border-radius: 20px; /* 保持原圆角 */
  border: 1px solid #e2e8f0; /* 浅灰边框（白框） */
  background: #ffffff; /* 白底 */
  font-size: 0.875rem;
  color: #000000; /* 黑字 */
  cursor: pointer;
  transition: all 0.2s ease;
  margin-right: 0.5rem;
}

/* 选中态的科室按键（如“呼吸内科”） */
.dept-tab.active {
  background: #2563eb; /* 选中态保留蓝色背景（区分选中） */
  color: #ffffff; /* 选中态字体为白色 */
  border-color: #2563eb;
}

/* 悬停态（未选中） */
.dept-tab:hover:not(.active) {
  border-color: #3b82f6;
  transform: translateY(-1px);
}

/* ========== 原有样式（已优化白框黑字） ========== */
.intro-wrapper {
  padding: 1rem;
  margin-bottom: 1rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.intro-header {
  margin-bottom: 0.75rem;
}

.intro-title {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.tag {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
  background: #ffffff;
  color: #000000;
  border: 1px solid #e2e8f0;
}

.tag.blue {
  background: #ffffff;
  color: #000000;
  border: 1px solid #e2e8f0;
}

.dept-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: #000000;
}

.dept-type {
  font-size: 0.875rem;
  color: #000000;
}

.intro-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  font-size: 0.875rem;
  color: #000000;
  margin-bottom: 0.5rem;
}

.intro-desc {
  font-size: 0.875rem;
  color: #000000;
  margin: 0.25rem 0;
  line-height: 1.5;
}

.intro-desc.light {
  color: #000000;
}

.doctor-section {
  margin-top: 1rem;
  border-top: 1px dashed #e2e8f0;
  padding-top: 1rem;
}

.doctor-section-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.doctor-section-header .tip {
  font-size: 0.75rem;
  color: #94a3b8;
}

.doctor-list {
  display: flex;
  flex-wrap: nowrap;
  gap: 0.75rem;
  overflow-x: auto;
  padding-bottom: 0.5rem;
}

.doctor-card {
  min-width: 220px;
  max-width: 260px;
  padding: 0.75rem;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: all 0.15s ease;
}

.doctor-card:hover {
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.12);
  transform: translateY(-2px);
  border-color: #2563eb;
}

.doctor-card.active {
  border-color: #2563eb;
  background: #ffffff;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}

.doctor-card.off {
  opacity: 0.55;
  cursor: not-allowed;
}

.doctor-card.off:hover {
  transform: none;
  box-shadow: none;
  border-color: #e2e8f0;
}

.doctor-main {
  margin-bottom: 0.5rem;
}

.doctor-name-line {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.25rem;
}

.doctor-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: #000000;
}

.doctor-title {
  font-size: 0.75rem;
  padding: 0.125rem 0.375rem;
  border-radius: 999px;
  background: #e2e8f0;
  color: #475569;
}

.doctor-specialty {
  font-size: 0.75rem;
  color: #000000;
  margin-bottom: 0.125rem;
  line-height: 1.3;
}

.doctor-schedule {
  font-size: 0.75rem;
  color: #64748b;
}

.doctor-status {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 0.5rem;
}

.status-badge {
  font-size: 0.7rem;
  padding: 0.125rem 0.5rem;
  border-radius: 999px;
  background: #e2e8f0;
  color: #475569;
  font-weight: 600;
}

.status-badge.on {
  background: #dcfce7;
  color: #166534;
}

.status-badge.off {
  background: #e2e8f0;
  color: #64748b;
}

.quota {
  font-size: 0.7rem;
  color: #475563;
}

.doctor-empty {
  font-size: 0.875rem;
  color: #94a3b8;
  text-align: center;
  padding: 1rem;
}
</style>