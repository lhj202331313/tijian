<template>
  <div class="container page" v-loading="loading">
    <el-page-header @back="router.back()">
      <template #content>
        <span class="page-header-title">订单详情</span>
      </template>
    </el-page-header>

    <div v-if="order">
      <!-- 订单信息 -->
      <el-card class="info-card card-shadow">
        <div class="order-title">
          <h2>订单号：{{ order.orderNo }}</h2>
          <el-tag :type="statusTag(order.orderStatus)">
            {{ statusText(order.orderStatus) }}
          </el-tag>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="体检医院">
            {{ order.hospitalName }}
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
          <el-descriptions-item label="体检套餐">
            {{ order.setmealName }}
          </el-descriptions-item>
          <el-descriptions-item label="套餐类型">
            <el-tag size="small" type="info">
              {{ typeText(order.setmealType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="套餐价格">
            ¥{{ order.setmealPrice }}
          </el-descriptions-item>
          <el-descriptions-item label="体检日期">
            {{ order.orderDate }}
          </el-descriptions-item>
          <el-descriptions-item label="下单时间" :span="2">
            {{ order.createTime }}
          </el-descriptions-item>
          <el-descriptions-item v-if="order.cancelTime" label="取消时间" :span="2">
            {{ order.cancelTime }}
          </el-descriptions-item>
        </el-descriptions>
        <div class="action-bar">
          <el-button
            v-if="order.orderStatus === 0"
            type="danger"
            plain
            @click="onCancel"
          >
            取消订单
          </el-button>
          <el-button
            v-if="order.orderStatus === 1"
            type="primary"
            class="btn-gradient"
            @click="goReport"
          >
            查看体检报告
          </el-button>
          <el-button @click="router.push('/order')">返回订单列表</el-button>
        </div>
      </el-card>

      <!-- 套餐检查项 -->
      <h3 class="block-title">套餐检查项</h3>
      <el-table :data="checkitems" border stripe>
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="name" label="检查项目" min-width="200" />
        <el-table-column label="类型" width="160" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ itemTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, cancelOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const checkitems = ref([])
const loading = ref(false)

function statusText(s) {
  return ['待体检', '已完成', '已取消'][s] || '未知'
}
function statusTag(s) {
  return s === 0 ? 'warning' : s === 1 ? 'success' : 'info'
}
function typeText(t) {
  return t || '通用'
}
function itemTypeText(t) {
  return t || '其他'
}

function goReport() {
  router.push(`/report/${route.params.id}`)
}

async function onCancel() {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
      confirmButtonText: '确定取消',
      cancelButtonText: '再想想',
      type: 'warning'
    })
    const res = await cancelOrder(route.params.id)
    if (res && res.code === 200) {
      ElMessage.success('订单已取消')
      loadData()
    }
  } catch (e) {
    // 用户取消
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    if (res && res.code === 200 && res.data) {
      order.value = res.data
      checkitems.value = res.data.checkitemList || res.data.checkitems || []
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-header-title {
  font-size: 16px;
  font-weight: 600;
}

.info-card {
  margin: 20px 0;
}

.order-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.order-title h2 {
  font-size: 20px;
}

.action-bar {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}

.block-title {
  font-size: 18px;
  margin: 24px 0 16px;
}

.dept-tag {
  margin-left: 6px;
}

.muted {
  color: #909399;
}
</style>
