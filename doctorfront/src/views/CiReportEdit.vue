<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCiReportDetail, saveCiReport } from '@/api/cireport'
import { getOrderDetail } from '@/api/order'

const route = useRoute()
const router = useRouter()

const orderId = route.params.orderId
const checkitemId = route.params.checkitemId

const loading = ref(false)
const submitting = ref(false)
const order = ref({})
const report = ref(null)
// 指标明细列表（可编辑）
const details = ref([])
const checkitemName = ref('')

// 性别映射
const sexMap = { 0: '未知', 1: '男', 2: '女' }

// 是否已录入（用于按钮文案）
const hasReport = computed(() => !!report.value && !!report.value.id)

// 加载订单信息
async function loadOrder() {
  try {
    const res = await getOrderDetail(orderId)
    const data = res.data || {}
    // 归一化后端嵌套对象
    order.value = {
      ...data,
      userName: data.userName ?? data.user?.realName ?? data.user?.username,
      userPhone: data.userPhone ?? data.user?.phone,
      userSex: data.userSex ?? data.user?.sex,
      hospitalName: data.hospitalName ?? data.hospital?.name,
      setmealName: data.setmealName ?? data.setmeal?.name
    }
  } catch (e) {
    // 忽略，错误提示已处理
  }
}

// 加载检查项报告详情
async function loadReport() {
  loading.value = true
  try {
    const res = await getCiReportDetail(orderId, checkitemId)
    const data = res.data
    if (!data) {
      report.value = null
      details.value = []
      return
    }
    // 兼容两种返回：单报告对象(含details) 或 直接数组
    if (Array.isArray(data)) {
      report.value = { id: null, checkitemName: '' }
      details.value = data
      checkitemName.value = data[0]?.checkitemName || ''
    } else {
      report.value = data
      details.value = data.details || []
      checkitemName.value = data.checkitemName || ''
    }
  } catch (e) {
    // 若不存在报告，部分后端可能返回空 data，不报错
    report.value = null
    details.value = []
  } finally {
    loading.value = false
  }
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

// 保存
async function handleSave() {
  // 收集所有非空 resultValue
  const submitDetails = details.value
    .filter((d) => d.resultValue !== null && d.resultValue !== undefined && String(d.resultValue).trim() !== '')
    .map((d) => ({
      checkitemdetailedId: d.checkitemdetailedId ?? d.checkitemDetailedId,
      resultValue: String(d.resultValue).trim()
    }))

  if (submitDetails.length === 0) {
    ElMessage.warning('请至少录入一项检查指标结果')
    return
  }

  submitting.value = true
  try {
    await saveCiReport({
      orderId: orderId,
      checkitemId: checkitemId,
      details: submitDetails
    })
    ElMessage.success('报告保存成功')
    // 重新加载以获取异常判断与报告时间
    await loadReport()
  } catch (e) {
    // 错误提示已处理
  } finally {
    submitting.value = false
  }
}

// 返回
function goBack() {
  router.push(`/order/${orderId}`)
}

onMounted(async () => {
  await Promise.all([loadOrder(), loadReport()])
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

    <!-- 检查项信息 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Files /></el-icon>
          <span>检查项信息</span>
          <el-tag v-if="hasReport" type="success" effect="light" class="ml">
            已录入
          </el-tag>
          <el-tag v-else type="info" effect="plain" class="ml">
            未录入
          </el-tag>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="检查项名称">
          {{ checkitemName || checkitemId }}
        </el-descriptions-item>
        <el-descriptions-item label="报告时间">
          {{ formatDateTime(report?.reportTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 指标录入表格 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><EditPen /></el-icon>
          <span>检查指标录入</span>
        </div>
      </template>
      <el-table :data="details" border stripe style="width: 100%">
        <el-table-column label="指标名称" min-width="160">
          <template #default="{ row }">
            {{ row.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="单位" width="100" align="center">
          <template #default="{ row }">
            {{ row.unit || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="参考范围" min-width="160">
          <template #default="{ row }">
            {{ row.normalRange || row.normalrange || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="检查结果" min-width="220">
          <template #default="{ row }">
            <!-- 有单位的为数值类指标，用单行输入；无单位的为结论类（影像/功能），用多行文本 -->
            <el-input
              v-if="row.unit"
              v-model="row.resultValue"
              placeholder="请输入检查结果"
              clearable
            />
            <el-input
              v-else
              v-model="row.resultValue"
              type="textarea"
              :rows="2"
              :placeholder="`请输入${row.name || '检查'}结果/结论`"
            />
          </template>
        </el-table-column>
        <el-table-column label="是否异常" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.isAbnormal === true || row.isAbnormal === 1 || row.isAbnormal === '1'"
              type="danger"
              effect="light"
            >
              异常
            </el-tag>
            <el-tag
              v-else-if="row.resultValue !== null && row.resultValue !== undefined && String(row.resultValue).trim() !== '' && (row.isAbnormal === false || row.isAbnormal === 0 || row.isAbnormal === '0')"
              type="success"
              effect="light"
            >
              正常
            </el-tag>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="该检查项暂无指标明细" />
        </template>
      </el-table>

      <!-- 底部操作 -->
      <div class="form-footer">
        <el-button @click="goBack">返回</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSave">
          保存报告
        </el-button>
      </div>
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
}
.ml {
  margin-left: 8px;
}
.form-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
}
</style>
