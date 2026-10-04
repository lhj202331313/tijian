<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useDoctorStore } from '@/stores/doctor'
import { updateDoctorInfo } from '@/api/doctor'

const doctorStore = useDoctorStore()
const loading = ref(false)
const submitLoading = ref(false)
const editDialogVisible = ref(false)

// 医生信息（兜底空对象）
const doctor = computed(() => doctorStore.doctorInfo || {})

// 编辑表单
const editForm = reactive({
  realName: '',
  phone: '',
  department: ''
})

const editFormRef = ref(null)

const editRules = {
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  department: [{ required: true, message: '请输入科室', trigger: 'blur' }]
}

// 账号状态映射
const statusMap = {
  0: { text: '禁用', type: 'danger' },
  1: { text: '正常', type: 'success' }
}

// 格式化日期时间
function formatDateTime(val) {
  if (!val) return '-'
  const d = new Date(val)
  if (isNaN(d.getTime())) return val
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${day} ${hh}:${mm}`
}

// 拉取最新医生信息
async function loadProfile() {
  loading.value = true
  try {
    await doctorStore.fetchDoctorInfo()
  } catch (e) {
    ElMessage.warning('医生信息加载失败，显示本地缓存信息')
  } finally {
    loading.value = false
  }
}

// 打开编辑弹窗
function openEdit() {
  editForm.realName = doctor.value.realName || ''
  editForm.phone = doctor.value.phone || ''
  editForm.department = doctor.value.department || ''
  editDialogVisible.value = true
}

// 提交编辑
async function handleEditSubmit() {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await doctorStore.updateProfile({
        realName: editForm.realName,
        phone: editForm.phone,
        department: editForm.department
      })
      ElMessage.success('个人信息更新成功')
      editDialogVisible.value = false
    } catch (e) {
      // 错误提示已由拦截器处理
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  loadProfile()
})
</script>

<template>
  <div v-loading="loading" class="page-container">
    <el-card class="profile-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
          <el-button
            type="primary"
            size="small"
            class="header-action"
            @click="openEdit"
          >
            <el-icon><Edit /></el-icon> 编辑信息
          </el-button>
        </div>
      </template>

      <div class="profile-top">
        <el-avatar :size="80" class="profile-avatar">
          <el-icon :size="44"><UserFilled /></el-icon>
        </el-avatar>
        <div class="profile-name">
          <div class="name-text">{{ doctor.realName || doctor.username || '医生' }}</div>
          <div class="sub-text">
            <el-tag size="small" type="primary" effect="plain">
              {{ doctor.department || '未设置科室' }}
            </el-tag>
            <el-tag
              v-if="doctor.hospitalName"
              size="small"
              type="info"
              effect="plain"
              class="ml"
            >
              {{ doctor.hospitalName }}
            </el-tag>
          </div>
        </div>
      </div>

      <el-descriptions :column="2" border class="profile-desc">
        <el-descriptions-item label="姓名">
          {{ doctor.realName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="账号">
          {{ doctor.username || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="科室">
          {{ doctor.department || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="所属医院">
          {{ doctor.hospitalName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="医院ID">
          {{ doctor.hospitalId != null ? doctor.hospitalId : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ doctor.phone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="医生ID">
          {{ doctor.id != null ? doctor.id : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag
            v-if="doctor.status != null"
            :type="statusMap[doctor.status]?.type || 'info'"
            effect="light"
          >
            {{ statusMap[doctor.status]?.text || '未知' }}
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">
          {{ formatDateTime(doctor.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人信息"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="科室" prop="department">
          <el-input v-model="editForm.department" placeholder="请输入科室" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitLoading"
          @click="handleEditSubmit"
        >
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-card {
  max-width: 860px;
  margin: 0 auto;
  border-radius: 4px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #303133;
}
.header-action {
  margin-left: auto;
}
.profile-top {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 12px 0 20px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}
.profile-avatar {
  background-color: #1976d2;
  color: #fff;
}
.profile-name {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.name-text {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}
.sub-text {
  display: flex;
  align-items: center;
}
.ml {
  margin-left: 8px;
}
.profile-desc {
  margin-bottom: 16px;
}
</style>
