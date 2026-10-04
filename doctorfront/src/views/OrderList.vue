<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderList, updateOrderStatus } from '@/api/order'

const router = useRouter()

// 筛选条件
const query = reactive({
  status: '',
  orderDate: '',
  page: 1,
  size: 10
})

// 状态下拉选项
const statusOptions = [
  { value: '', label: '全部' },
  { value: 0, label: '待体检' },
  { value: 1, label: '已完成' },
  { value: 2, label: '已取消' }
]

// 订单状态映射
const statusMap = {
  0: { text: '待体检', type: 'warning' },
  1: { text: '已完成', type: 'success' },
  2: { text: '已取消', type: 'info' }
}

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 加载列表
async function loadList() {
  loading.value = true
  try {
    const params = {
      page: query.page,
      size: query.size
    }
    if (query.status !== '' && query.status !== null) {
      params.status = query.status
    }
    if (query.orderDate) {
      // 格式化为 yyyy-MM-dd
      const d = new Date(query.orderDate)
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      params.orderDate = `${y}-${m}-${day}`
    }
    const res = await getOrderList(params)
    const data = res.data || {}
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) {
    // 错误提示已处理
  } finally {
    loading.value = false
  }
}

// 查询
function handleQuery() {
  query.page = 1
  loadList()
}

// 重置
function handleReset() {
  query.status = ''
  query.orderDate = ''
  query.page = 1
  loadList()
}

// 分页变化
function handleSizeChange(val) {
  query.size = val
  query.page = 1
  loadList()
}
function handleCurrentChange(val) {
  query.page = val
  loadList()
}

// 查看详情
function handleViewDetail(row) {
  router.push(`/order/${row.id}`)
}

// 完成体检
async function handleComplete(row) {
  try {
    await updateOrderStatus(row.id, 1)
    ElMessage.success('已完成体检')
    loadList()
  } catch (e) {
    // 错误提示已处理
  }
}

// 格式化日期
function formatDate(val) {
  if (!val) return '-'
  const d = new Date(val)
  if (isNaN(d.getTime())) return val
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

onMounted(() => {
  loadList()
})
</script>

<template>
  <div class="page-container">
    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="订单状态">
          <el-select
            v-model="query.status"
            placeholder="全部"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="体检日期">
          <el-date-picker
            v-model="query.orderDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleQuery">
            查询
          </el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="userName" label="用户姓名" min-width="100" />
        <el-table-column prop="userPhone" label="手机号" min-width="120" />
        <el-table-column prop="hospitalName" label="医院" min-width="140" />
        <el-table-column prop="setmealName" label="套餐" min-width="140" />
        <el-table-column label="接待医生" min-width="120">
          <template #default="{ row }">
            <template v-if="row.doctorName">
              {{ row.doctorName }}
              <div class="dept-sub">{{ row.doctorDepartment }}</div>
            </template>
            <span v-else class="muted">待分配</span>
          </template>
        </el-table-column>
        <el-table-column label="体检日期" min-width="110">
          <template #default="{ row }">
            {{ formatDate(row.orderDate) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.orderStatus]?.type || 'info'" effect="light">
              {{ statusMap[row.orderStatus]?.text || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              :icon="'View'"
              @click="handleViewDetail(row)"
            >
              查看详情
            </el-button>
            <el-popconfirm
              v-if="row.orderStatus === 0"
              title="确认该订单已完成体检吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleComplete(row)"
            >
              <template #reference>
                <el-button type="success" link :icon="'CircleCheck'">
                  完成体检
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无订单数据" />
        </template>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.filter-card {
  margin-bottom: 16px;
}
.filter-form {
  margin-bottom: -18px;
}
.table-card {
  border-radius: 4px;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.dept-sub {
  font-size: 12px;
  color: #909399;
  line-height: 1.3;
}
.muted {
  color: #909399;
}
</style>
