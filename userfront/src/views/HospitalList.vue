<template>
  <div class="container page">
    <h1 class="page-title">合作医院</h1>
    <el-row :gutter="20" v-loading="loading">
      <el-col v-for="h in hospitals" :key="h.id" :xs="24" :sm="24" :md="12">
        <el-card class="hospital-card card-shadow" shadow="hover">
          <div class="hospital-head">
            <el-icon class="h-icon"><OfficeBuilding /></el-icon>
            <div>
              <h3>{{ h.name }}</h3>
              <el-tag size="small" :type="h.status === 1 ? 'success' : 'info'">
                {{ h.status === 1 ? '营业中' : '休息中' }}
              </el-tag>
            </div>
          </div>
          <div class="hospital-meta">
            <p><el-icon><Location /></el-icon><span>{{ h.address }}</span></p>
            <p><el-icon><Phone /></el-icon><span>{{ h.phone }}</span></p>
            <p><el-icon><Clock /></el-icon><span>{{ h.businessHours }}</span></p>
          </div>
          <div class="hospital-rule">
            <el-icon><InfoFilled /></el-icon>
            <span>{{ h.ruleDesc }}</span>
          </div>
          <div class="hospital-foot">
            <el-button type="primary" class="btn-gradient" @click="goDetail(h.id)">
              查看套餐
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!loading && hospitals.length === 0" description="暂无医院数据" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  OfficeBuilding,
  Location,
  Phone,
  Clock,
  InfoFilled
} from '@element-plus/icons-vue'
import { getHospitalList } from '@/api/hospital'

const router = useRouter()
const hospitals = ref([])
const loading = ref(false)

function goDetail(id) {
  router.push(`/hospital/${id}`)
}

async function loadData() {
  loading.value = true
  try {
    const res = await getHospitalList()
    if (res && res.code === 200) hospitals.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.hospital-card {
  margin-bottom: 20px;
}

.hospital-head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.h-icon {
  font-size: 32px;
  color: var(--primary-color);
}

.hospital-head h3 {
  font-size: 18px;
  margin-bottom: 4px;
}

.hospital-meta p {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-regular);
  font-size: 14px;
  margin: 6px 0;
}

.hospital-rule {
  display: flex;
  gap: 6px;
  background: #f5f7fa;
  padding: 10px 12px;
  border-radius: 6px;
  margin: 12px 0;
  color: var(--text-regular);
  font-size: 13px;
  line-height: 1.5;
}

.hospital-foot {
  text-align: right;
}
</style>
