<template>
  <div class="container page" v-loading="loading">
    <el-page-header @back="router.back()">
      <template #content>
        <span class="page-header-title">医院详情</span>
      </template>
    </el-page-header>

    <div v-if="hospital">
      <!-- 医院信息卡片 -->
      <el-card class="info-card card-shadow">
        <div class="hospital-title">
          <el-icon class="h-icon"><OfficeBuilding /></el-icon>
          <h2>{{ hospital.name }}</h2>
          <el-tag :type="hospital.status === 1 ? 'success' : 'info'">
            {{ hospital.status === 1 ? '营业中' : '休息中' }}
          </el-tag>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="医院地址">
            <el-icon><Location /></el-icon> {{ hospital.address }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            <el-icon><Phone /></el-icon> {{ hospital.phone }}
          </el-descriptions-item>
          <el-descriptions-item label="营业时间">
            <el-icon><Clock /></el-icon> {{ hospital.businessHours }}
          </el-descriptions-item>
          <el-descriptions-item label="预约规则">
            {{ hospital.ruleDesc }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 院内套餐列表 -->
      <h3 class="block-title">本院体检套餐</h3>
      <el-row :gutter="20" v-if="setmeals.length">
        <el-col v-for="s in setmeals" :key="s.id" :xs="24" :sm="12" :md="8">
          <el-card class="setmeal-card card-shadow" shadow="hover">
            <div class="setmeal-head">
              <span class="setmeal-name">{{ s.name }}</span>
              <el-tag size="small" :type="s.type === '个人体检' ? 'primary' : s.type === '团体体检' ? 'warning' : 'success'">
                {{ s.type || '通用' }}
              </el-tag>
            </div>
            <p class="setmeal-desc">{{ s.description }}</p>
            <div class="setmeal-foot">
              <span class="price">¥{{ s.price }}</span>
              <div>
                <el-button size="small" @click="goSetmeal(s.id)">查看详情</el-button>
                <el-button type="primary" size="small" class="btn-gradient" @click="goBooking(s.id)">
                  立即预约
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="该医院暂无套餐" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  OfficeBuilding,
  Location,
  Phone,
  Clock
} from '@element-plus/icons-vue'
import { getHospitalDetail } from '@/api/hospital'

const route = useRoute()
const router = useRouter()
const hospital = ref(null)
const setmeals = ref([])
const loading = ref(false)

function goSetmeal(id) {
  router.push(`/setmeal/${id}`)
}

function goBooking(setmealId) {
  router.push({
    path: '/booking',
    query: { hospitalId: hospital.value.id, setmealId }
  })
}

async function loadData() {
  loading.value = true
  try {
    const res = await getHospitalDetail(route.params.id)
    if (res && res.code === 200 && res.data) {
      hospital.value = res.data
      setmeals.value = res.data.setmealList || []
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

.hospital-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.h-icon {
  font-size: 32px;
  color: var(--primary-color);
}

.hospital-title h2 {
  font-size: 22px;
}

.block-title {
  font-size: 18px;
  margin: 24px 0 16px;
}

.setmeal-card {
  margin-bottom: 20px;
}

.setmeal-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.setmeal-name {
  font-size: 16px;
  font-weight: 600;
}

.setmeal-desc {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.5;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.setmeal-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.price {
  color: var(--danger-color);
  font-size: 20px;
  font-weight: 700;
}
</style>
