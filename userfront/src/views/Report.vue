<template>
  <div class="container page" v-loading="loading">
    <el-page-header @back="router.back()">
      <template #content>
        <span class="page-header-title">体检报告</span>
      </template>
    </el-page-header>

    <template v-if="report">
      <!-- 订单信息卡片 -->
      <el-card class="info-card card-shadow">
        <div class="report-title">
          <el-icon class="r-icon"><Document /></el-icon>
          <h2>体检报告</h2>
          <el-tag :type="overallStatusTag" v-if="overall">
            {{ overall.resultStatus === 1 ? '体检异常' : '体检正常' }}
          </el-tag>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">
            {{ report.orderNo }}
          </el-descriptions-item>
          <el-descriptions-item label="体检医院">
            {{ report.hospitalName }}
          </el-descriptions-item>
          <el-descriptions-item label="体检套餐">
            {{ report.setmealName }}
          </el-descriptions-item>
          <el-descriptions-item label="体检日期">
            {{ report.orderDate }}
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag size="small" :type="statusTag(report.status)">
              {{ statusText(report.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 分项报告 -->
      <h3 class="block-title">分项检查报告</h3>
      <div v-if="report.items && report.items.length" class="report-items">
        <el-card
          v-for="item in report.items"
          :key="item.checkitemId"
          class="item-card card-shadow"
          shadow="never"
        >
          <template #header>
            <div class="item-head">
              <span class="item-name">
                <el-icon><Files /></el-icon>
                {{ item.checkitemName }}
              </span>
              <el-tag :type="item.resultStatus === 1 ? 'danger' : 'success'" size="small">
                {{ item.resultStatus === 1 ? '异常' : '正常' }}
              </el-tag>
            </div>
          </template>
          <el-table :data="item.details || []" border size="small" :row-class-name="rowClass">
            <el-table-column prop="name" label="指标名称" min-width="140" />
            <el-table-column prop="resultValue" label="结果" width="120" align="center">
              <template #default="{ row }">
                <span :class="row.isAbnormal ? 'text-danger' : 'text-success'">
                  {{ row.resultValue }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="90" align="center" />
            <el-table-column prop="normalRange" label="参考范围" min-width="140" />
            <el-table-column label="状态" width="90" align="center">
              <template #default="{ row }">
                <el-tag
                  :type="row.isAbnormal ? 'danger' : 'success'"
                  size="small"
                >
                  {{ row.isAbnormal ? '异常' : '正常' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div class="item-time" v-if="item.reportTime">
            报告时间：{{ item.reportTime }}
          </div>
        </el-card>
      </div>
      <el-empty v-else description="暂无分项报告数据" />

      <!-- 总检结论 -->
      <h3 class="block-title">总检结论</h3>
      <el-card v-if="overall" class="overall-card card-shadow">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="总检医生">
            {{ overall.doctorName || '—' }}
          </el-descriptions-item>
          <el-descriptions-item label="总检结果">
            <el-tag :type="overall.resultStatus === 1 ? 'danger' : 'success'">
              {{ overall.resultStatus === 1 ? '异常' : '正常' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发布状态">
            <el-tag size="small" type="success">
              {{ overall.status === 1 ? '已发布' : '未发布' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发布时间">
            {{ overall.publishTime || '—' }}
          </el-descriptions-item>
          <el-descriptions-item label="总检结论与建议" :span="2">
            <div class="overall-summary">{{ overall.summary }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-empty v-else description="总检结论尚未发布" />
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document, Files } from '@element-plus/icons-vue'
import { getReport, getOverallReport } from '@/api/report'

const route = useRoute()
const router = useRouter()
const report = ref(null)
const overall = ref(null)
const loading = ref(false)

function statusText(s) {
  return ['待体检', '已完成', '已取消'][s] || '未知'
}
function statusTag(s) {
  return s === 0 ? 'warning' : s === 1 ? 'success' : 'info'
}

const overallStatusTag = computed(() => {
  if (!overall.value) return 'info'
  return overall.value.resultStatus === 1 ? 'danger' : 'success'
})

// 异常行标红
function rowClass({ row }) {
  return row.isAbnormal ? 'row-abnormal' : 'row-normal'
}

async function loadData() {
  loading.value = true
  try {
    const orderId = route.params.orderId
    const [rRes, oRes] = await Promise.all([
      getReport(orderId),
      getOverallReport(orderId).catch(() => ({ code: -1 }))
    ])
    if (rRes && rRes.code === 200) report.value = rRes.data
    if (oRes && oRes.code === 200) overall.value = oRes.data
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

.report-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.r-icon {
  font-size: 28px;
  color: var(--primary-color);
}

.report-title h2 {
  font-size: 20px;
}

.block-title {
  font-size: 18px;
  margin: 24px 0 16px;
}

.item-card {
  margin-bottom: 20px;
}

.item-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-name {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  font-weight: 600;
}

.item-time {
  margin-top: 10px;
  color: var(--text-secondary);
  font-size: 13px;
  text-align: right;
}

.overall-card {
  margin-bottom: 20px;
}

.overall-summary {
  white-space: pre-wrap;
  line-height: 1.8;
  color: var(--text-primary);
}
</style>

<style>
/* 全局样式：异常行标红 */
.el-table .row-abnormal {
  background-color: #fff1f0 !important;
}
.el-table .row-normal {
  background-color: #f6ffed !important;
}
</style>
