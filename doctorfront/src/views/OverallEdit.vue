<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOverall, saveOverall, publishOverall } from '@/api/overall'
import { getOrderDetail } from '@/api/order'

const route = useRoute()
const router = useRouter()

const orderId = route.params.orderId
const loading = ref(false)
const submitting = ref(false)
const publishing = ref(false)

const order = ref({})
const overall = ref(null)

// 表单
const form = reactive({
  summary: '',
  resultStatus: 0 // 0 正常 / 1 异常
})

// 总检状态选项（医学结论状态）
const resultStatusOptions = [
  { value: 0, label: '正常' },
  { value: 1, label: '异常' }
]

// 文档发布状态：0 草稿 / 1 已发布
const docStatusMap = {
  0: { text: '草稿', type: 'warning' },
  1: { text: '已发布', type: 'success' }
}

// 性别映射
const sexMap = { 0: '未知', 1: '男', 2: '女' }

// 当前文档状态
const docStatus = computed(() => {
  if (!overall.value) return null
  return overall.value.status
})

// 是否已发布
const isPublished = computed(() => docStatus.value === 1)

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

// 加载订单信息
async function loadOrder() {
  try {
    const res = await getOrderDetail(orderId)
    order.value = res.data || {}
  } catch (e) {
    // 忽略
  }
}

// 加载总检结论
async function loadOverall() {
  loading.value = true
  try {
    const res = await getOverall(orderId)
    const data = res.data
    if (data) {
      overall.value = data
      form.summary = data.summary || ''
      form.resultStatus = data.resultStatus != null ? data.resultStatus : 0
    } else {
      overall.value = null
    }
  } catch (e) {
    // 若不存在结论，部分后端可能返回空 data
    overall.value = null
  } finally {
    loading.value = false
  }
}

// 保存草稿
async function handleSaveDraft() {
  if (!form.summary || form.summary.trim() === '') {
    ElMessage.warning('请输入总检结论与建议')
    return
  }
  submitting.value = true
  try {
    const res = await saveOverall({
      orderId: orderId,
      summary: form.summary.trim(),
      resultStatus: form.resultStatus
    })
    // 保存后刷新（拿到 id 用于后续发布）
    ElMessage.success('草稿保存成功')
    if (res.data && res.data.id) {
      overall.value = { ...overall.value, ...res.data }
    }
    await loadOverall()
  } catch (e) {
    // 错误提示已处理
  } finally {
    submitting.value = false
  }
}

// 发布：先保存再 PUT publish，发布前二次确认
async function handlePublish() {
  if (!form.summary || form.summary.trim() === '') {
    ElMessage.warning('请输入总检结论与建议')
    return
  }
  try {
    await ElMessageBox.confirm(
      '发布后总检结论将不可再编辑，确定要发布吗？',
      '发布确认',
      {
        confirmButtonText: '确认发布',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch (e) {
    // 用户取消
    return
  }

  publishing.value = true
  try {
    // 1. 先保存最新内容
    const saveRes = await saveOverall({
      orderId: orderId,
      summary: form.summary.trim(),
      resultStatus: form.resultStatus
    })
    // 取结论 id（优先最新返回，回退到已加载）
    let overallId = saveRes?.data?.id || overall.value?.id
    if (!overallId) {
      // 兜底：重新拉取以拿到 id
      await loadOverall()
      overallId = overall.value?.id
    }
    if (!overallId) {
      ElMessage.error('未能获取总检结论ID，发布失败')
      publishing.value = false
      return
    }
    // 2. 发布
    await publishOverall(overallId)
    ElMessage.success('总检结论发布成功')
    await loadOverall()
  } catch (e) {
    // 错误提示已处理
  } finally {
    publishing.value = false
  }
}

// 返回
function goBack() {
  router.push(`/order/${orderId}`)
}

onMounted(async () => {
  await Promise.all([loadOrder(), loadOverall()])
})
</script>

<template>
  <div v-loading="loading" class="page-container">
    <!-- 返回 -->
    <div class="page-header">
      <el-button :icon="'ArrowLeft'" @click="goBack">返回订单详情</el-button>
    </div>

    <!-- 订单信息 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Document /></el-icon>
          <span>订单信息</span>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="订单号">
          {{ order.orderNo || orderId }}
        </el-descriptions-item>
        <el-descriptions-item label="用户姓名">
          {{ order.userName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ order.userPhone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ sexMap[order.userSex] || '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="体检医院">
          {{ order.hospitalName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="体检套餐">
          {{ order.setmealName || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 总检结论编辑 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Notebook /></el-icon>
          <span>总检结论</span>
          <el-tag
            v-if="docStatus != null"
            :type="docStatusMap[docStatus]?.type || 'info'"
            effect="light"
            class="ml"
          >
            {{ docStatusMap[docStatus]?.text || '未知' }}
          </el-tag>
          <el-tag v-if="isPublished" type="info" effect="plain" class="ml">
            发布时间：{{ formatDateTime(overall?.publishTime) }}
          </el-tag>
          <el-tag v-else-if="overall" type="info" effect="plain" class="ml">
            创建时间：{{ formatDateTime(overall?.createTime) }}
          </el-tag>
        </div>
      </template>

      <el-form :model="form" label-width="120px" :disabled="isPublished">
        <el-form-item label="总检状态">
          <el-radio-group v-model="form.resultStatus">
            <el-radio
              v-for="item in resultStatusOptions"
              :key="item.value"
              :value="item.value"
            >
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="总检结论与建议">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="8"
            placeholder="请输入总检结论与建议..."
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item v-if="overall?.doctorName" label="主检医生">
          {{ overall.doctorName }}
        </el-form-item>
      </el-form>

      <!-- 底部操作 -->
      <div class="form-footer">
        <el-button @click="goBack">返回</el-button>
        <el-button
          type="primary"
          :loading="submitting"
          :disabled="isPublished"
          @click="handleSaveDraft"
        >
          保存草稿
        </el-button>
        <el-button
          type="success"
          :loading="publishing"
          :disabled="isPublished"
          @click="handlePublish"
        >
          发布总检结论
        </el-button>
      </div>

      <el-alert
        v-if="isPublished"
        title="该总检结论已发布，不可再编辑"
        type="success"
        :closable="false"
        show-icon
        class="mt"
      />
    </el-card>
  </div>
</template>

<style scoped>
.page-header {
  margin-bottom: 12px;
}
.info-card {
  margin-bottom: 16px;
  border-radius: 4px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #303133;
  flex-wrap: wrap;
}
.ml {
  margin-left: 8px;
}
.mt {
  margin-top: 12px;
}
.form-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
}
</style>
