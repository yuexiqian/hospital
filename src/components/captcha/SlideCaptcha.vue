<template>
  <div class="captcha-container">
    <!-- 滑动验证区域 -->
    <div class="captcha-wrapper" ref="wrapperRef">
      <!-- 背景提示文字 -->
      <div class="slider-background">
        <span class="background-text">
          <span v-for="i in 8" :key="i">»</span>
        </span>
        <span class="hint-text">向右滑动完成验证</span>
      </div>
      
      <!-- 滑块 -->
      <div 
        class="slider-thumb" 
        ref="thumbRef"
        :style="{ left: `${sliderPosition}px` }"
        @mousedown="startDrag"
        @touchstart="startDrag"
      >
        <div class="thumb-content">
          <svg class="thumb-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>
      
      <!-- 进度背景 -->
      <div 
        class="slider-progress" 
        :style="{ width: `${sliderPosition}px` }"
      ></div>
      
      <!-- 成功状态 -->
      <div class="success-state" :class="{ active: isSuccess }">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M5 12L10 17L19 8" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>
    
    <!-- 验证状态和操作 -->
    <div class="captcha-footer">
      <div v-if="message" class="captcha-message" :class="{ error: isError, success: isSuccess }">
        <span class="message-text">{{ message }}</span>
        <span v-if="!isVerified && !isDragging" class="hint-text">{{ getHintText() }}</span>
      </div>
      
      <button type="button" class="refresh-btn" @click="reset">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M23 4V10H17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M1 20V14H7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M3.51 9C4.01717 7.56678 4.87913 6.2854 6.01547 5.27542C7.1518 4.26543 8.52547 3.55976 10.0083 3.22426C11.4911 2.88875 13.0348 2.93434 14.4952 3.35677C15.9556 3.77921 17.2853 4.56471 18.36 5.64L23 10M1 14L5.64 18.36C6.71475 19.4353 8.04437 20.2208 9.50481 20.6432C10.9652 21.0657 12.5089 21.1113 13.9917 20.7757C15.4745 20.4402 16.8482 19.7346 17.9845 18.7246C19.1209 17.7146 19.9828 16.4332 20.49 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>刷新验证</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, defineExpose, defineEmits } from 'vue'

const emit = defineEmits(['verified', 'refreshed'])

// 组件引用
const wrapperRef = ref(null)
const thumbRef = ref(null)

// 组件状态
const sliderPosition = ref(0)
const maxPosition = ref(0)
const thumbWidth = ref(0)
const isDragging = ref(false)
const isVerified = ref(false)
const message = ref('向右滑动完成验证')
const isError = ref(false)
const isSuccess = ref(false)

// 行为分析数据
const behaviorData = reactive({
  startTime: 0,
  endTime: 0,
  positions: [],
  timestamps: [],
  dragEvents: [],
  failureCount: 0
})

// 配置
const CONFIG = {
  targetProgress: 0.85,  // 需要达到85%的进度
  minDuration: 300,      // 最短时间300ms
  maxDuration: 8000,     // 最长时间8秒
  maxJumpDistance: 15,   // 最大跳跃距离（像素）
  requiredPoints: 5,     // 最少需要记录的点数
  maxFailures: 3,        // 最大失败次数
  successThreshold: 0.6  // 行为评分阈值
}

// 获取提示文本
const getHintText = () => {
  if (behaviorData.failureCount >= 2) {
    return '请缓慢匀速拖动滑块'
  }
  return ''
}

// 初始化组件
const init = () => {
  if (wrapperRef.value && thumbRef.value) {
    thumbWidth.value = thumbRef.value.offsetWidth
    maxPosition.value = wrapperRef.value.offsetWidth - thumbWidth.value
    // 确保初始位置为0
    sliderPosition.value = 0
  }
}

// 重置状态
const reset = () => {
  sliderPosition.value = 0
  isDragging.value = false
  isVerified.value = false
  isError.value = false
  isSuccess.value = false
  message.value = '向右滑动完成验证'
  
  // 重置行为数据
  behaviorData.startTime = 0
  behaviorData.endTime = 0
  behaviorData.positions = []
  behaviorData.timestamps = []
  behaviorData.dragEvents = []
  
  emit('refreshed')
}

// 开始拖动
const startDrag = (e) => {
  if (isVerified.value) return
  
  e.preventDefault()
  isDragging.value = true
  
  // 记录开始时间
  behaviorData.startTime = Date.now()
  behaviorData.positions = [0]
  behaviorData.timestamps = [behaviorData.startTime]
  behaviorData.dragEvents = ['start']
  
  // 设置拖动样式
  if (thumbRef.value) {
    thumbRef.value.style.transition = 'none'
  }
  
  // 添加事件监听
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('touchmove', onDrag, { passive: false })
  document.addEventListener('mouseup', stopDrag)
  document.addEventListener('touchend', stopDrag)
  document.addEventListener('touchcancel', stopDrag)
}

// 拖动中
const onDrag = (e) => {
  if (!isDragging.value) return
  
  e.preventDefault()
  
  let clientX
  if (e.type.includes('touch')) {
    clientX = e.touches[0].clientX
  } else {
    clientX = e.clientX
  }
  
  // 计算滑块位置
  const wrapperRect = wrapperRef.value.getBoundingClientRect()
  let position = clientX - wrapperRect.left - (thumbWidth.value / 2)
  
  // 限制位置范围
  position = Math.max(0, Math.min(maxPosition.value, position))
  
  // 更新滑块位置
  sliderPosition.value = position
  
  // 记录行为数据
  const currentTime = Date.now()
  behaviorData.positions.push(position)
  behaviorData.timestamps.push(currentTime)
  behaviorData.dragEvents.push('drag')
  
  // 更新进度条样式
  updateProgressStyle()
  
  // 检查是否已经接近目标位置
  const progress = position / maxPosition.value
  if (progress >= CONFIG.targetProgress) {
    stopDrag()
    verify()
  }
}

// 停止拖动
const stopDrag = () => {
  if (!isDragging.value) return
  
  isDragging.value = false
  behaviorData.endTime = Date.now()
  behaviorData.dragEvents.push('end')
  
  // 恢复过渡效果
  if (thumbRef.value) {
    thumbRef.value.style.transition = 'left 0.2s ease'
  }
  
  // 移除事件监听
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchend', stopDrag)
  document.removeEventListener('touchcancel', stopDrag)
  
  // 如果没有到达目标位置，自动回弹
  const progress = sliderPosition.value / maxPosition.value
  if (progress < CONFIG.targetProgress) {
    // 回弹动画
    setTimeout(() => {
      sliderPosition.value = 0
      updateProgressStyle()
    }, 100)
    
    // 验证失败
    showError('请将滑块拖动到最右侧')
  } else {
    // 验证
    verify()
  }
}

// 更新进度条样式
const updateProgressStyle = () => {
  const progress = sliderPosition.value / maxPosition.value
  
  // 可以根据进度改变滑块颜色
  if (thumbRef.value) {
    const thumb = thumbRef.value
    if (progress < 0.3) {
      thumb.style.borderColor = '#2563eb'
      thumb.style.background = '#ffffff'
    } else if (progress < 0.7) {
      thumb.style.borderColor = '#1d4ed8'
      thumb.style.background = '#eff6ff'
    } else {
      thumb.style.borderColor = '#1e40af'
      thumb.style.background = '#dbeafe'
    }
  }
}

// 验证函数
const verify = () => {
  // 基本条件检查
  if (behaviorData.positions.length < CONFIG.requiredPoints) {
    showError('拖动距离太短，请重新尝试')
    return
  }
  
  const duration = behaviorData.endTime - behaviorData.startTime
  const finalPosition = behaviorData.positions[behaviorData.positions.length - 1]
  const progress = finalPosition / maxPosition.value
  
  // 1. 位置验证：是否达到目标位置
  if (progress < CONFIG.targetProgress) {
    showError(`请拖动到最右侧（当前：${Math.round(progress * 100)}%）`)
    return
  }
  
  // 2. 时间验证：拖动时间是否合理
  if (duration < CONFIG.minDuration) {
    showError('拖动速度太快，请稍慢一些')
    return
  }
  
  if (duration > CONFIG.maxDuration) {
    showError('拖动时间过长，请重新尝试')
    return
  }
  
  // 3. 连续性验证
  if (!checkContinuity()) {
    showError('拖动过程异常，请匀速拖动')
    return
  }
  
  // 4. 行为模式分析
  const behaviorScore = calculateBehaviorScore()
  if (behaviorScore < CONFIG.successThreshold) {
    showError('验证失败，请重新尝试')
    return
  }
  
  // 所有验证通过
  showSuccessState()
}

// 连续性检查
const checkContinuity = () => {
  const positions = behaviorData.positions
  if (positions.length < 2) return false
  
  for (let i = 1; i < positions.length; i++) {
    const diff = Math.abs(positions[i] - positions[i-1])
    if (diff > CONFIG.maxJumpDistance) {
      return false
    }
  }
  return true
}

// 计算行为评分
const calculateBehaviorScore = () => {
  let score = 1.0
  
  const duration = behaviorData.endTime - behaviorData.startTime
  const positions = behaviorData.positions
  
  // 1. 时间评分（最佳时间1-3秒）
  if (duration < 1000) {
    score *= 0.8  // 太快，减分
  } else if (duration > 5000) {
    score *= 0.7  // 太慢，减分
  }
  
  // 2. 拖动平滑度评分
  const smoothness = calculateSmoothness()
  score *= smoothness
  
  // 3. 速度变化评分
  const speedPattern = analyzeSpeedPattern()
  score *= speedPattern
  
  return Math.min(1.0, score)
}

// 计算平滑度
const calculateSmoothness = () => {
  const positions = behaviorData.positions
  if (positions.length < 3) return 0.5
  
  let smoothness = 1.0
  for (let i = 2; i < positions.length; i++) {
    const diff1 = positions[i-1] - positions[i-2]
    const diff2 = positions[i] - positions[i-1]
    
    // 检查方向变化
    if (diff1 > 0 && diff2 < 0) {
      smoothness -= 0.2  // 反向拖动，减分
    }
  }
  
  return Math.max(0.5, smoothness)
}

// 分析速度模式
const analyzeSpeedPattern = () => {
  const positions = behaviorData.positions
  const timestamps = behaviorData.timestamps
  
  if (positions.length < 3) return 0.5
  
  // 计算速度序列
  const speeds = []
  for (let i = 1; i < positions.length; i++) {
    const distance = positions[i] - positions[i-1]
    const timeDiff = timestamps[i] - timestamps[i-1]
    if (timeDiff > 0) {
      speeds.push(distance / timeDiff)
    }
  }
  
  // 检查速度变化
  let speedChanges = 0
  for (let i = 1; i < speeds.length; i++) {
    const speedDiff = Math.abs(speeds[i] - speeds[i-1])
    if (speedDiff > 0.03) {
      speedChanges++
    }
  }
  
  // 人类操作通常有2-5次速度变化
  if (speedChanges >= 2 && speedChanges <= 8) {
    return 1.0
  } else if (speedChanges > 0) {
    return 0.7
  } else {
    return 0.4  // 匀速，可能为机器人
  }
}

// 显示错误
const showError = (errorMsg) => {
  isError.value = true
  isSuccess.value = false
  message.value = errorMsg
  
  behaviorData.failureCount++
  
  // 2秒后重置状态
  setTimeout(() => {
    sliderPosition.value = 0
    updateProgressStyle()
    message.value = '向右滑动完成验证'
    isError.value = false
  }, 2000)
  
  emit('verified', false)
}

// 显示成功状态
const showSuccessState = () => {
  isVerified.value = true
  isError.value = false
  isSuccess.value = true
  message.value = '验证成功 ✓'
  
  // 重置失败计数
  behaviorData.failureCount = 0
  
  // 发射验证成功事件
  emit('verified', true)
  
  // 3秒后清除成功消息
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

// 暴露方法给父组件
defineExpose({
  reset,
  isVerified,
  getVerificationStatus: () => ({
    verified: isVerified.value,
    message: message.value
  })
})

// 初始化
onMounted(() => {
  // 等待DOM渲染完成
  setTimeout(() => {
    init()
  }, 100)
  
  // 监听窗口大小变化
  window.addEventListener('resize', init)
})
</script>

<style scoped>
.captcha-container {
  width: 100%;
}

.captcha-wrapper {
  position: relative;
  width: 100%;
  height: 56px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 2px solid #e5e7eb;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.captcha-wrapper:hover {
  border-color: #cbd5e1;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.1);
}

.slider-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  pointer-events: none;
  z-index: 1;
}

.background-text {
  color: #cbd5e1;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 4px;
  opacity: 0.7;
}

.hint-text {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.slider-thumb {
  position: absolute;
  top: 4px;
  left: 0;
  width: 48px;
  height: 48px;
  border-radius: 24px;
  background: #ffffff;
  border: 2px solid #2563eb;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2),
              0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: grab;
  z-index: 3;
  user-select: none;
  transition: left 0.2s ease, 
              border-color 0.3s ease,
              background 0.3s ease,
              transform 0.1s ease;
}

.slider-thumb:active {
  cursor: grabbing;
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3),
              0 3px 6px rgba(0, 0, 0, 0.15);
}

.thumb-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumb-icon {
  color: #2563eb;
  transition: transform 0.3s ease;
}

.slider-thumb:active .thumb-icon {
  transform: translateX(2px);
}

.slider-progress {
  position: absolute;
  top: 4px;
  left: 0;
  height: 48px;
  background: linear-gradient(90deg, #dbeafe 0%, #93c5fd 100%);
  border-radius: 24px;
  transition: width 0.1s ease;
  z-index: 2;
}

.success-state {
  position: absolute;
  top: 50%;
  right: 15px;
  transform: translateY(-50%) scale(0);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  z-index: 2;
}

.success-state.active {
  transform: translateY(-50%) scale(1);
  opacity: 1;
}

.captcha-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.captcha-message {
  flex: 1;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 13px;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.message-text {
  font-weight: 500;
}

.captcha-message.error {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  color: #dc2626;
  border: 1px solid #fecaca;
}

.captcha-message.success {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

.captcha-message .hint-text {
  font-size: 12px;
  color: #94a3b8;
  font-style: italic;
  font-weight: normal;
}

.captcha-message.error .hint-text {
  color: #ef4444;
}

.refresh-btn {
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  color: #64748b;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;
  white-space: nowrap;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.refresh-btn:hover {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  color: #475569;
  border-color: #cbd5e1;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.refresh-btn:active {
  transform: scale(0.98);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

@media (max-width: 768px) {
  .captcha-wrapper {
    height: 52px;
  }
  
  .slider-thumb {
    width: 44px;
    height: 44px;
    top: 4px;
  }
  
  .slider-progress {
    height: 44px;
    top: 4px;
  }
  
  .background-text {
    font-size: 16px;
    letter-spacing: 3px;
  }
  
  .hint-text {
    font-size: 13px;
  }
  
  .captcha-footer {
    flex-direction: column;
    gap: 8px;
    align-items: stretch;
  }
  
  .refresh-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>