<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderDetail } from '@/api/order'

const route = useRoute()
const router = useRouter()

const orderId = route.params.id
const loading = ref(false)
const order = ref({})
// 套餐检查项
const checkItems = ref([])
// 已录入的分项报告列表
const ciReports = ref([])

// 订单状态映射
const statusMap = {
  0: { text: '待体检', type: 'warning' },
  1: { text: '已完成', type: 'success' },
  2: { text: '已取消', type: 'info' }
}

// 性别映射
const sexMap = { 0: '未知', 1: '男', 2: '女' }

// 检查项类型映射
const typeMap = {
  0: '普通检查',
  1: '检验',
  2: '影像',
  3: '功能'
}

// 将已录入报告按 checkitemId 索引，便于判断某检查项是否已录入
const reportMap = computed(() => {
  const map = {}
  ;(ciReports.value || []).forEach((r) => {
    if (r.checkitemId != null) {
      map[r.checkitemId] = r
    }
  })
  return map
})

// 合并检查项与报告状态
const mergedCheckItems = computed(() => {
  return (checkItems.value || []).map((item) => {
    const report = reportMap.value[item.id] || reportMap.value[item.checkitemId]
    return {
      ...item,
      _report: report || null,
      _entered: !!report
    }
  })
})

// 加载详情
async function loadDetail() {
  loading.value = true
  try {
    const res = await getOrderDetail(orderId)
    const data = res.data || {}
    // 后端返回嵌套对象 user/hospital/setmeal，归一化为扁平字段供模板使用
    order.value = {
      ...data,
      userName: data.userName ?? data.user?.realName ?? data.user?.username,
      userPhone: data.userPhone ?? data.user?.phone,
      idCard: data.idCard ?? data.user?.idCard,
      userSex: data.userSex ?? data.user?.sex,
      hospitalName: data.hospitalName ?? data.hospital?.name,
      setmealName: data.setmealName ?? data.setmeal?.name,
      setmealType: data.setmealType ?? data.setmeal?.type,
      doctorName: data.doctorName ?? data.doctor?.realName,
      doctorDepartment: data.doctorDepartment ?? data.doctor?.department
    }
    checkItems.value = data.checkitems || data.checkItems || []
    ciReports.value = data.reports || data.cireports || data.ciReports || []
  } catch (e) {
    // 错误提示已处理
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

function formatDate(val) {
  if (!val) return '-'
  const d = new Date(val)
  if (isNaN(d.getTime())) return val
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

// 跳转分项报告录入/编辑
function goCiReportEdit(row) {
  router.push(`/cireport/${orderId}/${row.id}`)
}

// 跳转总检结论
function goOverallEdit() {
  router.push(`/overall/${orderId}`)
}

// 返回列表
function goBack() {
  router.push('/order')
}

onMounted(() => {
  loadDetail()
})
</script>

<template>
  <div v-loading="loading" class="page-container">
    <!-- 返回 -->
    <div class="page-header">
      <el-button :icon="'ArrowLeft'" @click="goBack">返回列表</el-button>
    </div>

    <!-- 订单信息 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Document /></el-icon>
          <span>订单信息</span>
          <el-tag
            v-if="order.orderStatus != null"
            :type="statusMap[order.orderStatus]?.type || 'info'"
            effect="light"
            class="status-tag"
          >
            {{ statusMap[order.orderStatus]?.text || '未知' }}
          </el-tag>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="订单号">
          {{ order.orderNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="用户姓名">
          {{ order.userName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="身份证号">
          {{ order.idCard || order.userIdCard || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ order.userPhone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ sexMap[order.userSex] || '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="体检日期">
          {{ formatDate(order.orderDate) }}
        </el-descriptions-item>
        <el-descriptions-item label="体检医院">
          {{ order.hospitalName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="体检套餐">
          {{ order.setmealName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="接待医生">
          <template v-if="order.doctorName">
            {{ order.doctorName }}
            <el-tag size="small" type="info" effect="plain" class="dept-tag">
              {{ order.doctorDepartment }}
            </el-tag>
          </template>
          <span v-else class="muted">待分配</span>
        </el-descriptions-item>
        <el-descriptions-item label="套餐类型">
          {{ order.setmealType || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(order.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="取消时间" v-if="order.cancelTime">
          {{ formatDateTime(order.cancelTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 套餐检查项 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Files /></el-icon>
          <span>套餐检查项</span>
        </div>
      </template>
      <el-table :data="mergedCheckItems" border stripe style="width: 100%">
        <el-table-column prop="name" label="检查项名称" min-width="160">
          <template #default="{ row }">
            {{ row.name || row.checkitemName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="类型" width="120" align="center">
          <template #default="{ row }">
            {{ row.type || row.checkitemType || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="报告状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row._entered" type="success" effect="light">已录入</el-tag>
            <el-tag v-else type="info" effect="plain">未录入</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报告时间" min-width="160">
          <template #default="{ row }">
            {{ row._report ? formatDateTime(row._report.reportTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!row._entered"
              type="primary"
              link
              :icon="'Edit'"
              @click="goCiReportEdit(row)"
            >
              录入报告
            </el-button>
            <el-button
              v-else
              type="primary"
              link
              :icon="'Edit'"
              @click="goCiReportEdit(row)"
            >
              查看/编辑报告
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无检查项数据" />
        </template>
      </el-table>
    </el-card>

    <!-- 总检结论入口 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Notebook /></el-icon>
          <span>总检结论</span>
        </div>
      </template>
      <el-button type="primary" :icon="'Edit'" @click="goOverallEdit">
        录入 / 编辑总检结论
      </el-button>
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
.status-tag {
  margin-left: 8px;
}
.dept-tag {
  margin-left: 6px;
}
.muted {
  color: #909399;
}
</style>
