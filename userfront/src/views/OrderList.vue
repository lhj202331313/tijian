<template>
  <div class="container page">
    <h1 class="page-title">我的订单</h1>

    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待体检" name="0" />
      <el-tab-pane label="已完成" name="1" />
      <el-tab-pane label="已取消" name="2" />
    </el-tabs>

    <div v-loading="loading">
      <div v-if="orders.length" class="order-list">
        <el-card
          v-for="o in orders"
          :key="o.id"
          class="order-card card-shadow"
          shadow="never"
        >
          <div class="order-head">
            <span class="order-no">订单号：{{ o.orderNo }}</span>
            <el-tag :type="statusTag(o.orderStatus)" size="small">
              {{ statusText(o.orderStatus) }}
            </el-tag>
          </div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="体检医院">
              {{ o.hospitalName }}
            </el-descriptions-item>
            <el-descriptions-item label="接待医生">
              <template v-if="o.doctorName">
                {{ o.doctorName }}
                <span class="dept-text">（{{ o.doctorDepartment }}）</span>
              </template>
              <span v-else class="muted">待分配</span>
            </el-descriptions-item>
            <el-descriptions-item label="体检套餐">
              {{ o.setmealName }}
            </el-descriptions-item>
            <el-descriptions-item label="套餐类型">
              <el-tag size="small" type="info">
                {{ typeText(o.setmealType) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="套餐价格">
              ¥{{ o.setmealPrice }}
            </el-descriptions-item>
            <el-descriptions-item label="体检日期">
              {{ o.orderDate }}
            </el-descriptions-item>
            <el-descriptions-item label="下单时间" :span="2">
              {{ o.createTime }}
            </el-descriptions-item>
            <el-descriptions-item v-if="o.cancelTime" label="取消时间" :span="2">
              {{ o.cancelTime }}
            </el-descriptions-item>
          </el-descriptions>
          <div class="order-foot">
            <el-button size="small" @click="goDetail(o.id)">查看详情</el-button>
            <el-button
              v-if="o.orderStatus === 1"
              type="primary"
              size="small"
              class="btn-gradient"
              @click="goReport(o.id)"
            >
              查看报告
            </el-button>
            <el-button
              v-if="o.orderStatus === 0"
              type="danger"
              size="small"
              plain
              @click="onCancel(o)"
            >
              取消订单
            </el-button>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="暂无订单数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, cancelOrder } from '@/api/order'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const activeTab = ref('')

function statusText(s) {
  return ['待体检', '已完成', '已取消'][s] || '未知'
}
function statusTag(s) {
  return s === 0 ? 'warning' : s === 1 ? 'success' : 'info'
}
function typeText(t) {
  return t || '通用'
}

function goDetail(id) {
  router.push(`/order/${id}`)
}

function goReport(orderId) {
  router.push(`/report/${orderId}`)
}

async function loadOrders() {
  loading.value = true
  try {
    const params = {}
    if (activeTab.value !== '') params.status = activeTab.value
    const res = await getOrderList(params)
    if (res && res.code === 200) orders.value = res.data || []
  } finally {
    loading.value = false
  }
}

function onTabChange() {
  loadOrders()
}

async function onCancel(o) {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单「${o.orderNo}」吗？`,
      '取消订单',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }
    )
    const res = await cancelOrder(o.id)
    if (res && res.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    }
  } catch (e) {
    // 用户取消操作
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.order-card {
  margin-bottom: 16px;
}

.order-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-weight: 600;
  color: var(--text-primary);
}

.order-foot {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 12px;
}

.dept-text {
  color: #909399;
  font-size: 12px;
}

.muted {
  color: #909399;
}
</style>
