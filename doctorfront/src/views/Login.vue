<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useDoctorStore } from '@/stores/doctor'

const router = useRouter()
const route = useRoute()
const doctorStore = useDoctorStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 填充 demo 账号
function fillDemo() {
  loginForm.username = 'sunyisheng'
  loginForm.password = '123456'
}

async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await doctorStore.login(loginForm.username, loginForm.password)
      ElMessage.success('登录成功')
      const redirect = route.query.redirect || '/'
      router.replace(redirect)
    } catch (e) {
      // 错误提示已在拦截器中处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <el-card class="login-card" shadow="always">
      <div class="login-header">
        <el-icon class="login-logo"><FirstAidKit /></el-icon>
        <h2 class="login-title">健诊通 · 医生端</h2>
        <p class="login-subtitle">体检信息管理系统</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        class="login-form"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="'User'"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="'Lock'"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-tip">
        <el-alert
          title="演示账号：sunyisheng / 123456"
          type="info"
          :closable="false"
          show-icon
        />
        <el-button link type="primary" class="demo-btn" @click="fillDemo">
          <el-icon><MagicStick /></el-icon>
          一键填入演示账号
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.login-container {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1976d2 0%, #0097a7 50%, #26a69a 100%);
}

.login-bg::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(
      circle at 20% 30%,
      rgba(255, 255, 255, 0.12) 0,
      transparent 40%
    ),
    radial-gradient(
      circle at 80% 70%,
      rgba(255, 255, 255, 0.08) 0,
      transparent 40%
    );
}

.login-card {
  position: relative;
  z-index: 1;
  width: 400px;
  max-width: 92vw;
  border-radius: 12px;
  padding: 8px 16px 16px;
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-logo {
  font-size: 48px;
  color: #1976d2;
  margin-bottom: 8px;
}

.login-title {
  margin: 0;
  color: #1976d2;
  font-size: 24px;
  font-weight: 600;
}

.login-subtitle {
  margin: 6px 0 0;
  color: #909399;
  font-size: 13px;
}

.login-form {
  margin-top: 8px;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  letter-spacing: 4px;
}

.login-tip {
  margin-top: 16px;
}

.demo-btn {
  width: 100%;
  margin-top: 8px;
}
</style>
