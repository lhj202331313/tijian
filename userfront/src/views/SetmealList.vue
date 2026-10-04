<template>
  <div class="container page">
    <h1 class="page-title">体检套餐</h1>

    <!-- 筛选区 -->
    <div class="filter-bar card-shadow">
      <span class="filter-label">按医院筛选：</span>
      <el-select
        v-model="hospitalId"
        placeholder="全部医院"
        clearable
        style="width: 240px"
        @change="loadSetmeals"
      >
        <el-option label="全部医院" value="" />
        <el-option
          v-for="h in hospitals"
          :key="h.id"
          :label="h.name"
          :value="h.id"
        />
      </el-select>
    </div>

    <!-- 套餐卡片网格 -->
    <el-row :gutter="20" v-loading="loading">
      <el-col v-for="s in setmeals" :key="s.id" :xs="24" :sm="12" :md="8">
        <el-card class="setmeal-card card-shadow" shadow="hover">
          <div class="card-head">
            <span class="name">{{ s.name }}</span>
            <el-tag size="small" :type="typeTag(s.type)">
              {{ typeText(s.type) }}
            </el-tag>
          </div>
          <p class="desc">{{ s.description }}</p>
          <div class="card-meta">
            <span class="meta-item">
              <el-icon><Document /></el-icon>
              检查项 {{ s.checkitems ? s.checkitems.length : (s.itemCount || 0) }} 项
            </span>
          </div>
          <div class="card-foot">
            <span class="price">¥{{ s.price }}</span>
            <div class="actions">
              <el-button size="small" @click="goDetail(s.id)">查看详情</el-button>
              <el-button type="primary" size="small" class="btn-gradient" @click="goBooking(s)">
                立即预约
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!loading && setmeals.length === 0" description="暂无套餐数据" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Document } from '@element-plus/icons-vue'
import { getSetmealList } from '@/api/setmeal'
import { getHospitalList } from '@/api/hospital'

const router = useRouter()
const hospitals = ref([])
const setmeals = ref([])
const hospitalId = ref('')
const loading = ref(false)

function typeText(t) {
  if (t === '个人体检') return '个人体检'
  if (t === '团体体检') return '团体体检'
  return t || '通用'
}
function typeTag(t) {
  if (t === '个人体检') return 'primary'
  if (t === '团体体检') return 'warning'
  return 'success'
}

function goDetail(id) {
  router.push(`/setmeal/${id}`)
}

function goBooking(s) {
  // 跳转预约页，带上套餐 id；若套餐有 hospitalId 也带上
  router.push({
    path: '/booking',
    query: { setmealId: s.id, hospitalId: s.hospitalId }
  })
}

async function loadHospitals() {
  const res = await getHospitalList()
  if (res && res.code === 200) hospitals.value = res.data || []
}

async function loadSetmeals() {
  loading.value = true
  try {
    const params = {}
    if (hospitalId.value) params.hospitalId = hospitalId.value
    const res = await getSetmealList(params)
    if (res && res.code === 200) setmeals.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadHospitals()
  await loadSetmeals()
})
</script>

<style scoped>
.filter-bar {
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.filter-label {
  font-size: 14px;
  color: var(--text-regular);
  margin-right: 10px;
}

.setmeal-card {
  margin-bottom: 20px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.card-head .name {
  font-size: 16px;
  font-weight: 600;
}

.desc {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.5;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-meta {
  margin: 10px 0;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-regular);
}

.card-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions {
  display: flex;
  gap: 8px;
}

.price {
  color: var(--danger-color);
  font-size: 20px;
  font-weight: 700;
}
</style>
