<template>
  <div class="container page" v-loading="loading">
    <el-page-header @back="router.back()">
      <template #content>
        <span class="page-header-title">套餐详情</span>
      </template>
    </el-page-header>

    <div v-if="setmeal">
      <!-- 套餐信息 -->
      <el-card class="info-card card-shadow">
        <div class="setmeal-title">
          <h2>{{ setmeal.name }}</h2>
          <el-tag :type="typeTag(setmeal.type)">{{ typeText(setmeal.type) }}</el-tag>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="套餐价格">
            <span class="price">¥{{ setmeal.price }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="检查项目数">
            {{ setmeal.itemCount != null ? setmeal.itemCount : (checkitemList.length) }} 项
          </el-descriptions-item>
          <el-descriptions-item label="套餐描述" :span="2">
            {{ setmeal.description }}
          </el-descriptions-item>
        </el-descriptions>
        <div class="action-bar">
          <el-button
            type="primary"
            size="large"
            class="btn-gradient"
            @click="goBooking"
          >
            <el-icon><Calendar /></el-icon> 立即预约
          </el-button>
        </div>
      </el-card>

      <!-- 检查项列表 -->
      <h3 class="block-title">检查项目</h3>
      <el-table :data="checkitemList" border stripe>
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="name" label="检查项目名称" min-width="200" />
        <el-table-column label="项目类型" width="160" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="itemTypeTag(row.type)">
              {{ itemTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Calendar } from '@element-plus/icons-vue'
import { getSetmealDetail } from '@/api/setmeal'

const route = useRoute()
const router = useRouter()
const setmeal = ref(null)
const loading = ref(false)

// 后端返回字段名为 checkitemList，兼容旧字段 checkitems
const checkitemList = computed(() => {
  if (!setmeal.value) return []
  return setmeal.value.checkitemList || setmeal.value.checkitems || []
})

// 套餐类型：个人体检 / 团体体检
function typeText(t) {
  return t || '通用'
}
function typeTag(t) {
  if (t === '个人体检') return 'primary'
  if (t === '团体体检') return 'warning'
  return 'success'
}
// 检查项类型：一般 / 检验 / 影像 / 功能
function itemTypeText(t) {
  return t || '其他'
}
function itemTypeTag(t) {
  const map = { 一般: '', 检验: 'success', 影像: 'warning', 功能: 'info' }
  return map[t] || 'info'
}

function goBooking() {
  router.push({
    path: '/booking',
    query: {
      setmealId: setmeal.value.id,
      hospitalId: setmeal.value.hospitalId
    }
  })
}

async function loadData() {
  loading.value = true
  try {
    const res = await getSetmealDetail(route.params.id)
    if (res && res.code === 200) setmeal.value = res.data
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

.setmeal-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.setmeal-title h2 {
  font-size: 22px;
}

.price {
  color: var(--danger-color);
  font-size: 22px;
  font-weight: 700;
}

.action-bar {
  margin-top: 20px;
  text-align: center;
}

.block-title {
  font-size: 18px;
  margin: 24px 0 16px;
}
</style>
