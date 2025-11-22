<!-- src/views/LoginPage.vue -->
<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2 class="title">智慧门诊系统</h2>

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
      <form v-if="activeTab === 'login'" @submit.prevent="handleLogin">
        <div class="form-item">
          <label>账号（手机号 / 工号）</label>
          <input v-model="loginForm.loginName" placeholder="请输入账号" required />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            required
          />
        </div>
        <button type="submit" class="primary-btn">登 录</button>
      </form>

      <!-- 注册表单（仅患者） -->
      <form v-else @submit.prevent="handleRegister">
        <div class="form-item">
          <label>手机号（作为登录账号）</label>
          <input
            v-model="registerForm.loginName"
            placeholder="请输入手机号"
            required
          />
        </div>
        <div class="form-item">
          <label>联系电话</label>
          <input
            v-model="registerForm.phone"
            placeholder="请输入联系电话"
            required
          />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            required
          />
        </div>
        <div class="form-item">
          <label>确认密码</label>
          <input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            required
          />
        </div>
        <button type="submit" class="primary-btn">注 册</button>
      </form>

      <p v-if="message" class="msg" :class="{ error: isError }">
        {{ message }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

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

// 提示信息
const message = ref('')
const isError = ref(false)

// 当前登录用户（这里只用来判断是否已经登录，登录后直接跳转）
const currentUser = ref(null)

// 后端接口基础地址
const BASE_URL = 'http://localhost:8080/api/auth'

// 页面加载时，如果已经登录过，就直接跳到 /patient
onMounted(() => {
  const saved = localStorage.getItem('currentUser')
  if (saved) {
    try {
      currentUser.value = JSON.parse(saved)
      router.push('/patient')
    } catch (e) {
      console.error(e)
    }
  }
})

// 登录
const handleLogin = async () => {
  message.value = ''
  try {
    const resp = await axios.post(`${BASE_URL}/login`, {
      loginName: loginForm.loginName,
      password: loginForm.password
    })
    if (resp.data.code === 0) {
      const user = resp.data.data
      isError.value = false
      message.value = '登录成功，正在进入系统...'

      // 保存用户信息到 localStorage
      localStorage.setItem('currentUser', JSON.stringify(user))

      // 跳转到患者主界面
      router.push('/patient')
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

// 注册
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
</script>

<style scoped>
/* 直接沿用你原来的样式 */
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f3f4f6;
}

.auth-card {
  width: 380px;
  background: #ffffff;
  border-radius: 16px;
  padding: 24px 24px 18px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.18);
}

.title {
  text-align: center;
  margin-bottom: 16px;
  font-size: 18px;
  color: #111827;
}

.tabs {
  display: flex;
  margin-bottom: 16px;
  border-radius: 999px;
  background: #e5e7eb;
  padding: 3px;
}

.tab-btn {
  flex: 1;
  border: none;
  background: transparent;
  padding: 8px 0;
  border-radius: 999px;
  cursor: pointer;
  font-size: 14px;
}

.tab-btn.active {
  background: #2563eb;
  color: #fff;
  font-weight: 600;
}

.form-item {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
}

.form-item label {
  font-size: 13px;
  color: #374151;
  margin-bottom: 4px;
}

.form-item input {
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  font-size: 14px;
}

.form-item input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.2);
}

.primary-btn {
  width: 100%;
  border: none;
  margin-top: 8px;
  padding: 10px 0;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  font-size: 15px;
  cursor: pointer;
}

.primary-btn:hover {
  background: #1d4ed8;
}

.msg {
  margin-top: 10px;
  font-size: 13px;
  text-align: center;
  color: #16a34a;
}

.msg.error {
  color: #dc2626;
}
</style>
