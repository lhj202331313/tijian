<template>
  <div class="container page" v-loading="loading">
    <h1 class="page-title">个人中心</h1>

    <el-row :gutter="20">
      <!-- 用户信息展示 -->
      <el-col :xs="24" :md="8">
        <el-card class="user-card card-shadow">
          <div class="avatar">
            <el-avatar :size="80" :icon="UserFilled" />
          </div>
          <h3 class="username">{{ userStore.userInfo?.username || '—' }}</h3>
          <el-descriptions :column="1" class="user-desc">
            <el-descriptions-item label="真实姓名">
              {{ userStore.userInfo?.realName || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ sexText(userStore.userInfo?.sex) }}
            </el-descriptions-item>
            <el-descriptions-item label="身份证号">
              {{ userStore.userInfo?.idCard || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ userStore.userInfo?.phone || '—' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 编辑表单 -->
      <el-col :xs="24" :md="16">
        <el-card class="edit-card card-shadow">
          <template #header>
            <span class="card-title">编辑个人信息</span>
          </template>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            size="large"
          >
            <el-form-item label="用户名">
              <el-input :model-value="userStore.userInfo?.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" clearable />
            </el-form-item>
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" clearable />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" clearable />
            </el-form-item>
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="form.sex">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
                <el-radio :label="0">未知</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                class="btn-gradient"
                :loading="submitting"
                @click="onSubmit"
              >
                保存修改
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)

const form = reactive({
  realName: '',
  idCard: '',
  phone: '',
  sex: 0
})

function sexText(s) {
  return ['未知', '男', '女'][s] || '未知'
}

const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}
const validateIdCard = (rule, value, callback) => {
  if (value && !/^\d{17}[\dXx]$/.test(value)) {
    callback(new Error('请输入正确的18位身份证号'))
  } else {
    callback()
  }
}

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idCard: [{ validator: validateIdCard, trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

async function fillForm() {
  loading.value = true
  try {
    const info = await userStore.fetchUserInfo()
    if (info) {
      form.realName = info.realName || ''
      form.idCard = info.idCard || ''
      form.phone = info.phone || ''
      form.sex = info.sex ?? 0
    }
  } finally {
    loading.value = false
  }
}

function resetForm() {
  const info = userStore.userInfo
  if (info) {
    form.realName = info.realName || ''
    form.idCard = info.idCard || ''
    form.phone = info.phone || ''
    form.sex = info.sex ?? 0
  }
}

async function onSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const ok = await userStore.editUserInfo({ ...form })
      if (ok) {
        ElMessage.success('个人信息已更新')
        await userStore.fetchUserInfo()
      }
    } finally {
      submitting.value = false
    }
  })
}

onMounted(fillForm)
</script>

<style scoped>
.user-card {
  text-align: center;
  margin-bottom: 20px;
}

.avatar {
  display: flex;
  justify-content: center;
  margin: 16px 0;
}

.avatar :deep(.el-avatar) {
  background: var(--primary-color);
}

.username {
  font-size: 20px;
  margin: 8px 0 16px;
}

.user-desc {
  text-align: left;
}

.edit-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
