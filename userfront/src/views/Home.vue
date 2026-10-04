<template>
  <div class="home-page">
    <!-- Hero 横幅 -->
    <section class="hero">
      <div class="hero-content">
        <h1>健诊通 · 智能体检服务平台</h1>
        <p class="hero-desc">
          一站式体检预约 · 报告查询 · 健康管理，让体检更简单、更专业、更贴心
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" class="btn-gradient" @click="router.push('/hospital')">
            <el-icon><OfficeBuilding /></el-icon> 立即预约体检
          </el-button>
          <el-button size="large" @click="router.push('/setmeal')">
            <el-icon><List /></el-icon> 查看套餐
          </el-button>
        </div>
      </div>
    </section>

    <!-- 特色卡片 -->
    <section class="container section">
      <h2 class="section-title">我们的特色</h2>
      <el-row :gutter="20">
        <el-col v-for="f in features" :key="f.title" :xs="24" :sm="12" :md="6">
          <div class="feature-card card-shadow" @click="goFeature(f)">
            <el-icon class="feature-icon"><component :is="f.icon" /></el-icon>
            <h3>{{ f.title }}</h3>
            <p>{{ f.desc }}</p>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 热门医院 -->
    <section class="container section">
      <div class="section-header">
        <h2 class="section-title">热门医院</h2>
        <router-link to="/hospital" class="link-text">查看全部 →</router-link>
      </div>
      <el-row :gutter="20">
        <el-col
          v-for="h in hospitals.slice(0, 3)"
          :key="h.id"
          :xs="24"
          :sm="24"
          :md="8"
        >
          <el-card class="hospital-card card-shadow" shadow="hover" @click="goHospital(h.id)">
            <div class="hospital-name">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ h.name }}</span>
            </div>
            <div class="hospital-info">
              <p><el-icon><Location /></el-icon> {{ h.address }}</p>
              <p><el-icon><Phone /></el-icon> {{ h.phone }}</p>
              <p><el-icon><Clock /></el-icon> {{ h.businessHours }}</p>
            </div>
            <div class="hospital-stats">
              <el-tag size="small" type="success" effect="plain">
                <el-icon><List /></el-icon>
                {{ setmealCountByHospital(h.id) }} 个套餐可选
              </el-tag>
            </div>
            <div class="hospital-action">
              <el-button type="primary" size="small" class="btn-gradient">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="hospitals.length === 0" description="暂无医院数据" />
    </section>

    <!-- 热门套餐 -->
    <section class="container section">
      <div class="section-header">
        <h2 class="section-title">热门套餐</h2>
        <router-link to="/setmeal" class="link-text">查看全部 →</router-link>
      </div>
      <el-row :gutter="20">
        <el-col
          v-for="s in setmeals.slice(0, 4)"
          :key="s.id"
          :xs="24"
          :sm="12"
          :md="6"
        >
          <el-card class="setmeal-card card-shadow" shadow="hover" @click="goSetmeal(s.id)">
            <div class="setmeal-header">
              <span class="setmeal-name">{{ s.name }}</span>
              <el-tag size="small" :type="s.type === '个人体检' ? 'primary' : s.type === '团体体检' ? 'warning' : 'success'">{{ s.type || '通用' }}</el-tag>
            </div>
            <div class="setmeal-hospital" v-if="s.hospitalName">
              <el-icon><OfficeBuilding /></el-icon>
              <a class="hospital-link" @click.stop="goHospital(s.hospitalId)">{{ s.hospitalName }}</a>
            </div>
            <p class="setmeal-desc">{{ s.description }}</p>
            <div class="setmeal-footer">
              <span class="price">¥{{ s.price }}</span>
              <el-button type="primary" size="small" class="btn-gradient" @click.stop="goBooking(s)">立即预约</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="setmeals.length === 0" description="暂无套餐数据" />
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import {
  OfficeBuilding,
  Location,
  Phone,
  Clock,
  List,
  Calendar,
  Document,
  TrendCharts
} from '@element-plus/icons-vue'
import { getHospitalList } from '@/api/hospital'
import { getSetmealList } from '@/api/setmeal'

const router = useRouter()
const hospitals = ref([])
const setmeals = ref([])

const features = [
  {
    title: '在线预约',
    desc: '随时随地在线预约体检，告别排队等候',
    icon: markRaw(Calendar),
    path: '/hospital'
  },
  {
    title: '专业医院',
    desc: '甄选优质合作医院，体检质量有保障',
    icon: markRaw(OfficeBuilding),
    path: '/hospital'
  },
  {
    title: '报告查询',
    desc: '体检报告在线查看，结果一目了然',
    icon: markRaw(Document),
    path: '/order'
  },
  {
    title: '健康分析',
    desc: '智能健康数据分析，体检建议贴心专业',
    icon: markRaw(TrendCharts),
    path: '/order'
  }
]

function goFeature(f) {
  router.push(f.path)
}

function goHospital(id) {
  router.push(`/hospital/${id}`)
}

function goSetmeal(id) {
  router.push(`/setmeal/${id}`)
}

function goBooking(s) {
  router.push({
    path: '/booking',
    query: { setmealId: s.id, hospitalId: s.hospitalId }
  })
}

// 统计某医院的套餐数量（首页医院卡片展示）
function setmealCountByHospital(hospitalId) {
  return setmeals.value.filter(s => s.hospitalId === hospitalId).length
}

async function loadData() {
  try {
    const [hRes, sRes] = await Promise.all([getHospitalList(), getSetmealList()])
    if (hRes && hRes.code === 200) hospitals.value = hRes.data || []
    if (sRes && sRes.code === 200) setmeals.value = sRes.data || []
  } catch (e) {
    // 错误已由拦截器提示
  }
}

onMounted(loadData)
</script>

<style scoped>
.hero {
  background: linear-gradient(135deg, #1976d2 0%, #2dc4b6 100%);
  color: #fff;
  padding: 80px 20px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-content h1 {
  font-size: 38px;
  margin-bottom: 16px;
  font-weight: 700;
}

.hero-desc {
  font-size: 18px;
  margin-bottom: 32px;
  opacity: 0.95;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.section {
  padding: 40px 20px;
}

.section-title {
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 24px;
  text-align: center;
  color: var(--text-primary);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header .section-title {
  margin-bottom: 0;
  text-align: left;
}

.feature-card {
  background: #fff;
  padding: 32px 20px;
  text-align: center;
  margin-bottom: 20px;
  cursor: pointer;
  border-radius: 8px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.feature-icon {
  font-size: 44px;
  color: var(--secondary-color);
  margin-bottom: 12px;
}

.feature-card h3 {
  font-size: 18px;
  margin-bottom: 8px;
}

.feature-card p {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.6;
}

.hospital-card,
.setmeal-card {
  margin-bottom: 20px;
  cursor: pointer;
}

.hospital-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 12px;
}

.hospital-info p {
  color: var(--text-regular);
  font-size: 14px;
  margin: 6px 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.hospital-action {
  margin-top: 12px;
  text-align: right;
}

.hospital-stats {
  margin-top: 10px;
}

.setmeal-hospital {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.setmeal-hospital .hospital-link {
  color: var(--primary-color);
  cursor: pointer;
}

.setmeal-hospital .hospital-link:hover {
  text-decoration: underline;
}

.setmeal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
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

.setmeal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.setmeal-footer .price {
  color: var(--danger-color);
  font-size: 20px;
  font-weight: 700;
}
</style>
