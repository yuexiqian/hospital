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
      // 只有“今日坐诊 且 未满号”的医生允许选择
      if (!doc.availableToday || doc.full) {
        return;
      }
      this.$emit('select-doctor', doc);
    }
  }
};
</script>

<style scoped>
.intro-wrapper {
  padding: 16px 20px;
  margin-bottom: 16px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.06);
}

.intro-header {
  margin-bottom: 10px;
}

.intro-title {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 4px;
}

.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  background: #f3f4ff;
  color: #3056d3;
}

.tag.blue {
  background: #e0f2ff;
  color: #2563eb;
}

.dept-name {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.dept-type {
  font-size: 13px;
  color: #6b7280;
}

.intro-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 2px;
}

.intro-desc {
  font-size: 13px;
  color: #4b5563;
  margin: 2px 0;
}

.intro-desc.light {
  color: #6b7280;
}

.doctor-section {
  margin-top: 8px;
  border-top: 1px dashed #e5e7eb;
  padding-top: 8px;
}

.doctor-section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.doctor-section-header .tip {
  font-size: 12px;
  color: #9ca3af;
}

.doctor-list {
  display: flex;
  flex-wrap: nowrap;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.doctor-card {
  min-width: 220px;
  max-width: 260px;
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: all 0.15s ease;
}

.doctor-card:hover {
  box-shadow: 0 2px 6px rgba(15, 23, 42, 0.12);
  transform: translateY(-1px);
  border-color: #2563eb;
}

.doctor-card.active {
  border-color: #2563eb;
  background: #eff6ff;
}

.doctor-card.off {
  opacity: 0.55;
  cursor: not-allowed;
}

.doctor-main {
  margin-bottom: 6px;
}

.doctor-name-line {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.doctor-name {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
}

.doctor-title {
  font-size: 12px;
  padding: 0 6px;
  border-radius: 999px;
  background: #e5e7eb;
  color: #374151;
}

.doctor-specialty {
  font-size: 12px;
  color: #4b5563;
  margin-bottom: 2px;
}

.doctor-schedule {
  font-size: 12px;
  color: #6b7280;
}

.doctor-status {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 4px;
}

.status-badge {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 999px;
  background: #e5e7eb;
  color: #374151;
}

.status-badge.on {
  background: #dcfce7;
  color: #166534;
}

.status-badge.off {
  background: #e5e7eb;
  color: #6b7280;
}

.quota {
  font-size: 11px;
  color: #4b5563;
}

.doctor-empty {
  font-size: 13px;
  color: #9ca3af;
}
</style>
