<!-- src/views/doctor/DoctorOverviewPage.vue -->
<template>
  <div class="overview-page">
    <header class="ov-header">
      <h2>门诊概览</h2>
      <p class="sub">
        这里先放一个占位页面，后面你可以加：今日接诊数、完成处方数、开出处方数等统计。
      </p>
    </header>

    <section class="card">
      <h3>今日统计（占位）</h3>
      <ul class="stat-list">
        <li>
          <span class="label">今日挂号总数</span>
          <span class="value">—</span>
        </li>
        <li>
          <span class="label">已完成就诊</span>
          <span class="value">—</span>
        </li>
        <li>
          <span class="label">开出处方数</span>
          <span class="value">—</span>
        </li>
      </ul>
      <p class="tip">
        🔧 目前只是一个占位页面，保证路由/菜单不报错。以后你可以在这里接后端接口做各种图表。
      </p>
    </section>
  </div>
</template>

<script setup>
// 暂时不需要逻辑
</script>

<style scoped>
.overview-page {
  padding: 16px 20px;
}

.ov-header {
  margin-bottom: 12px;
}

.ov-header h2 {
  margin: 0;
  font-size: 18px;
}

.ov-header .sub {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.card {
  background: #fff;
  border-radius: 10px;
  padding: 14px 16px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.08);
}

.card h3 {
  margin: 0 0 10px;
  font-size: 15px;
}

.stat-list {
  list-style: none;
  padding: 0;
  margin: 0 0 10px;
}

.stat-list li {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px dashed #e5e7eb;
  font-size: 13px;
}

.stat-list li:last-child {
  border-bottom: none;
}

.label {
  color: #4b5563;
}

.value {
  font-weight: 600;
  color: #111827;
}

.tip {
  margin: 0;
  font-size: 12px;
  color: #9ca3af;
}
</style>
