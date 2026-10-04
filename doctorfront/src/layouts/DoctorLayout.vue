<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDoctorStore } from '@/stores/doctor'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const doctorStore = useDoctorStore()

// 当前激活的菜单
const activeMenu = computed(() => {
  // 订单详情/报告录入/总检 都归属订单管理高亮
  if (route.path.startsWith('/order') || route.path.startsWith('/cireport') || route.path.startsWith('/overall')) {
    return '/order'
  }
  return route.path
})

// 医生信息（兜底空值）
const doctor = computed(() => doctorStore.doctorInfo || {})

// 退出登录
async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    doctorStore.logout()
    router.replace('/login')
  } catch (e) {
    // 用户取消，不处理
  }
}

function handleSelect(index) {
  if (index === 'logout') {
    handleLogout()
    return
  }
  router.push(index)
}
</script>

<template>
  <el-container class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside width="220px" class="layout-aside">
      <div class="logo">
        <el-icon class="logo-icon"><FirstAidKit /></el-icon>
        <span class="logo-text">健诊通</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="layout-menu"
        background-color="#1565c0"
        text-color="#e3f2fd"
        active-text-color="#ffffff"
        @select="handleSelect"
      >
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="logout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部 -->
      <el-header class="layout-header">
        <div class="header-title">健诊通 · 医生端</div>
        <div class="header-doctor">
          <el-icon class="doctor-icon"><UserFilled /></el-icon>
          <span class="doctor-name">{{ doctor.realName || doctor.username || '医生' }}</span>
          <el-tag size="small" type="primary" effect="plain" class="doctor-tag">
            {{ doctor.department || '未设置科室' }}
          </el-tag>
          <el-tag v-if="doctor.hospitalName" size="small" type="info" effect="plain" class="doctor-tag">
            {{ doctor.hospitalName }}
          </el-tag>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #1565c0;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.logo-icon {
  font-size: 26px;
  color: #b3e5fc;
}

.layout-menu {
  border-right: none;
}

.layout-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

.layout-menu :deep(.el-menu-item.is-active) {
  background-color: #1976d2 !important;
}

.layout-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #1976d2;
}

.header-doctor {
  display: flex;
  align-items: center;
  gap: 8px;
}

.doctor-icon {
  font-size: 18px;
  color: #1976d2;
}

.doctor-name {
  font-weight: 600;
  color: #303133;
  margin-right: 4px;
}

.doctor-tag {
  margin-left: 4px;
}

.layout-main {
  background-color: #f5f7fa;
  padding: 16px;
  overflow-y: auto;
}
</style>
