<template>
  <div class="auth-container">
    <!-- 左侧背景区域（占满全屏） -->
    <div class="background-area"></div>
    
    <!-- 独立悬浮的登录窗口 -->
    <div class="auth-card">
      <div class="card-header">
        <div class="logo-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M19 3H5C3.89 3 3 3.9 3 5V19C3 20.1 3.89 21 5 21H19C20.11 21 21 20.1 21 19V5C21 3.9 20.11 3 19 3Z" stroke="#2563eb" stroke-width="2"/>
            <path d="M16 17L12 13L8 17" stroke="#2563eb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 13V7" stroke="#2563eb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h2 class="title">用户登录</h2>
      </div>

      <div class="tabs">
        <button
          :class="['tab-btn', activeTab === 'login' ? 'active' : '']"
          @click="activeTab = 'login'"
        >
          登录
        </button>
        <button
          :class="['tab-btn', activeTab === 'register' ? 'active' : '']"
          @click="activeTab = 'register'"
        >
          患者注册
        </button>
      </div>

      <!-- 登录表单 -->
      <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="auth-form">
        <div class="form-item">
          <label>账号（手机号 / 工号）</label>
          <div class="input-wrapper">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 11C17.6569 11 19 9.65685 19 8C19 6.34315 17.6569 5 16 5C14.3431 5 13 6.34315 13 8C13 9.65685 14.3431 11 16 11Z" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <input v-model="loginForm.loginName" placeholder="请输入账号" required />
          </div>
        </div>
        <div class="form-item">
          <label>密码</label>
          <div class="input-wrapper">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 15C13.6569 15 15 13.6569 15 12C15 10.3431 13.6569 9 12 9C10.3431 9 9 10.3431 9 12C9 13.6569 10.3431 15 12 15Z" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M19 9C19 11.6569 17.5368 14.069 15.3137 15.6863C13.0906 17.3036 10.3617 18 7 18C3.13401 18 0 14.866 0 11C0 7.13401 3.13401 4 7 4C10.3617 4 13.0906 4.69635 15.3137 6.31365C17.5368 7.93095 19 10.3431 19 13" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              required
            />
          </div>
        </div>
        
        <!-- 验证码组件 -->
        <div class="form-item">
          <label>验证码</label>
          <SlideCaptcha
            ref="captchaRef"
            @verified="onCaptchaVerified"
          />
        </div>
        
        <div class="form-extra">
          <label class="remember-me">
            <input type="checkbox" v-model="rememberMe" />
            <span>记住我</span>
          </label>
          <a href="#" class="forgot-link">忘记密码？</a>
        </div>
        <button type="submit" class="primary-btn" :disabled="!captchaVerified">
          <span>登 录</span>
          <svg class="btn-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 5L19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </form>

      <!-- 注册表单（仅患者） -->
      <form v-else @submit.prevent="handleRegister" class="auth-form">
        <div class="form-item">
          <label>手机号（作为登录账号）</label>
          <div class="input-wrapper">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" stroke="#94a3b8" stroke-width="2"/>
            </svg>
            <input
              v-model="registerForm.loginName"
              placeholder="请输入手机号"
              required
            />
          </div>
        </div>
        <div class="form-item">
          <label>联系电话</label>
          <div class="input-wrapper">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" stroke="#94a3b8" stroke-width="2"/>
            </svg>
            <input
              v-model="registerForm.phone"
              placeholder="请输入联系电话"
              required
            />
          </div>
        </div>
        <div class="form-item">
          <label>密码</label>
          <div class="input-wrapper">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 15C13.6569 15 15 13.6569 15 12C15 10.3431 13.6569 9 12 9C10.3431 9 9 10.3431 9 12C9 13.6569 10.3431 15 12 15Z" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M19 9C19 11.6569 17.5368 14.069 15.3137 15.6863C13.0906 17.3036 10.3617 18 7 18C3.13401 18 0 14.866 0 11C0 7.13401 3.13401 4 7 4C10.3617 4 13.0906 4.69635 15.3137 6.31365C17.5368 7.93095 19 10.3431 19 13" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              required
            />
          </div>
        </div>
        <div class="form-item">
          <label>确认密码</label>
          <div class="input-wrapper">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 15C13.6569 15 15 13.6569 15 12C15 10.3431 13.6569 9 12 9C10.3431 9 9 10.3431 9 12C9 13.6569 10.3431 15 12 15Z" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M19 9C19 11.6569 17.5368 14.069 15.3137 15.6863C13.0906 17.3036 10.3617 18 7 18C3.13401 18 0 14.866 0 11C0 7.13401 3.13401 4 7 4C10.3617 4 13.0906 4.69635 15.3137 6.31365C17.5368 7.93095 19 10.3431 19 13" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              required
            />
          </div>
        </div>
        <button type="submit" class="primary-btn">
          <span>注 册</span>
          <svg class="btn-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M5 12L10 17L19 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </form>

      <p v-if="message" class="msg" :class="{ error: isError }">
        {{ message }}
      </p>
      
      <div class="system-info">
        <p>智慧门诊系统 v1.0</p>
        <p>© 2023 智慧医疗科技</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import SlideCaptcha from '@/components/captcha/SlideCaptcha.vue'

const router = useRouter()

// 当前是登录还是注册
const activeTab = ref('login')

// 登录表单
const loginForm = reactive({
  loginName: '',
  password: ''
})

// 注册表单（患者）
const registerForm = reactive({
  loginName: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

// 记住我选项
const rememberMe = ref(true)

// 提示信息
const message = ref('')
const isError = ref(false)

// 当前登录用户
const currentUser = ref(null)

// 验证码引用和状态
const captchaRef = ref(null)
const captchaVerified = ref(false)

// 后端接口基础地址
const BASE_URL = 'http://localhost:8080/api/auth'

// 验证码验证回调
const onCaptchaVerified = (isVerified) => {
  captchaVerified.value = isVerified
}

// 按角色跳转到各自首页
const goHomeByRole = (user) => {
  if (!user || !user.role) {
    router.push('/patient')
    return
  }
  switch (user.role) {
    case 'PATIENT':
      router.push('/patient')
      break
    case 'NURSE':
      router.push('/nurse/queue')
      break
    case 'DOCTOR':
      router.push('/doctor')
      break
    case 'PHARMACIST':
      router.push('/pharmacist')
      break
    case 'ADMIN':
      router.push('/admin')
      break
    default:
      router.push('/patient')
  }
}

// 页面加载时，如果已经登录过，就按角色直接跳转
onMounted(() => {
  const saved = localStorage.getItem('currentUser')
  if (saved) {
    try {
      currentUser.value = JSON.parse(saved)
      goHomeByRole(currentUser.value)
    } catch (e) {
      console.error(e)
    }
  }
})

// 登录
const handleLogin = async () => {
  message.value = ''
  isError.value = false

  // 检查验证码
  if (!captchaVerified.value) {
    isError.value = true
    message.value = '请先完成验证码验证'
    return
  }

  try {
    const resp = await axios.post(`${BASE_URL}/login`, {
      loginName: loginForm.loginName,
      password: loginForm.password
    })

    console.log('login resp:', resp.data)

    if (resp.data.code === 0) {
      const user = resp.data.data
      if (!user) {
        isError.value = true
        message.value = '登录失败：返回用户信息为空'
        return
      }
      // 保存用户信息到 localStorage
      localStorage.setItem('currentUser', JSON.stringify(user))
      currentUser.value = user

      message.value = '登录成功，正在进入系统...'
      isError.value = false

      goHomeByRole(user)
    } else {
      isError.value = true
      message.value = resp.data.message || '登录失败'
    }
  } catch (err) {
    isError.value = true
    message.value = '请求失败，请检查后端是否启动、接口路径是否正确'
    console.error(err)
  }
}

// 注册（患者）
const handleRegister = async () => {
  message.value = ''
  if (registerForm.password !== registerForm.confirmPassword) {
    isError.value = true
    message.value = '两次输入的密码不一致'
    return
  }

  try {
    const resp = await axios.post(`${BASE_URL}/register`, {
      loginName: registerForm.loginName,
      phone: registerForm.phone,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword
    })
    if (resp.data.code === 0) {
      isError.value = false
      message.value = '注册成功，请切换到登录标签进行登录'
      // 清空密码
      registerForm.password = ''
      registerForm.confirmPassword = ''
      activeTab.value = 'login'
    } else {
      isError.value = true
      message.value = resp.data.message || '注册失败'
    }
  } catch (err) {
    isError.value = true
    message.value = '请求失败，请检查后端是否启动、接口路径是否正确'
    console.error(err)
  }
}

// 如果需要，可以添加重置验证码的方法
const resetCaptcha = () => {
  if (captchaRef.value) {
    captchaRef.value.refresh()
    captchaVerified.value = false
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  width: 100%;
  position: relative;
  overflow: hidden;
}

/* 左侧背景区域（全屏） */
.background-area {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('@/assets/background.jpg') no-repeat center center;
  background-size: cover;
  z-index: 1;
}

/* 独立悬浮的登录窗口 - 修改为不透明背景 */
.auth-card {
  position: absolute;
  top: 50%;
  right: 10%; /* 放置在背景图片的右侧空白区域 */
  transform: translateY(-50%);
  width: 380px;
  padding: 30px;
  /* 修改为纯白色不透明背景 */
  background: #ffffff;
  backdrop-filter: none; /* 移除毛玻璃效果 */
  border-radius: 20px; /* 保持圆弧边角 */
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
  z-index: 2;
  transition: all 0.3s ease;
}

.auth-card:hover {
  box-shadow: 0 15px 45px rgba(0, 0, 0, 0.15);
}

/* 卡片头部 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: rgba(37, 99, 235, 0.2);
}

.title {
  text-align: center;
  margin: 0;
  font-size: 22px;
  color: #111827;
  font-weight: 600;
}

/* 标签切换 - 修改样式为白底黑字，点击时为蓝底白字 */
.tabs {
  display: flex;
  margin-bottom: 24px;
  border-radius: 999px;
  background: #ffffff; /* 改为白色背景 */
  padding: 4px;
  border: 1px solid #e5e7eb; /* 添加边框 */
}

.tab-btn {
  flex: 1;
  border: none;
  background: #ffffff; /* 白色背景 */
  padding: 10px 0;
  border-radius: 999px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  color: #374151; /* 深灰色文字 */
}

.tab-btn:hover {
  background: #f3f4f6; /* 悬停时浅灰色背景 */
  color: #374151;
}

.tab-btn.active {
  background: #2563eb; /* 点击时蓝色背景 */
  color: #ffffff; /* 点击时白色文字 */
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

/* 表单样式 */
.auth-form {
  width: 100%;
}

.form-item {
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
}

.form-item label {
  font-size: 14px;
  color: #1e293b;
  margin-bottom: 6px;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  color: #64748b;
  transition: all 0.3s ease;
}

.form-item input {
  width: 100%;
  padding: 14px 14px 14px 44px; /* 左侧留空间给图标 */
  border-radius: 12px;
  border: 1px solid #e5e7eb; /* 更明显的边框 */
  font-size: 15px;
  transition: all 0.3s ease;
  background: #f9fafb; /* 浅灰色背景 */
}

.form-item input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.2);
  background: #ffffff; /* 聚焦时变为纯白色 */
}

.form-item input:focus + .input-icon {
  color: #2563eb;
}

/* 表单附加选项 */
.form-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8px 0 16px;
  font-size: 14px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #334155;
  cursor: pointer;
}

.remember-me input {
  width: auto;
  margin: 0;
}

.forgot-link {
  color: #2563eb;
  text-decoration: none;
  transition: all 0.2s ease;
}

.forgot-link:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

/* 按钮样式优化 */
.primary-btn {
  width: 100%;
  border: none;
  margin-top: 16px;
  padding: 14px 0;
  border-radius: 12px;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.primary-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.3);
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.primary-btn:active:not(:disabled) {
  transform: translateY(0);
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

/* 提示信息 */
.msg {
  margin-top: 16px;
  font-size: 14px;
  text-align: center;
  padding: 12px;
  border-radius: 10px;
  background: rgba(240, 253, 244, 0.8);
  color: #16a34a;
  transition: all 0.3s ease;
}

.msg.error {
  background: rgba(254, 242, 242, 0.8);
  color: #dc2626;
}

/* 系统信息 */
.system-info {
  margin-top: 30px;
  text-align: center;
  font-size: 12px;
  color: #475569;
  border-top: 1px solid rgba(229, 231, 235, 0.5); /* 更明显的边框 */
  padding-top: 15px;
}

.system-info p {
  margin: 5px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .auth-card {
    right: 50%;
    transform: translate(50%, -50%);
    width: 90%;
    max-width: 380px;
  }
}
</style>