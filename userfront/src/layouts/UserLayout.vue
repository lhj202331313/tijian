<template>
  <div class="layout-wrapper">
    <!-- 顶部导航 -->
    <header class="app-header">
      <div class="container header-inner">
        <div class="logo" @click="router.push('/')">
          <el-icon class="logo-icon"><FirstAidKit /></el-icon>
          <span class="logo-text">健诊通</span>
          <span class="logo-sub">用户端</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/hospital" class="nav-item">医院</router-link>
          <router-link to="/setmeal" class="nav-item">套餐</router-link>
          <router-link to="/order" class="nav-item">我的订单</router-link>
          <template v-if="userStore.isLogin">
            <router-link to="/profile" class="nav-item">个人中心</router-link>
            <el-dropdown @command="handleCommand">
              <span class="nav-item user-dropdown">
                {{ userStore.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="order">我的订单</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-item">登录</router-link>
          </template>
        </nav>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="app-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 底部 -->
    <footer class="app-footer">
      <div class="container">
        <p>健诊通 - 智能体检服务平台 © 2026 用户端</p>
        <p class="footer-sub">健康从体检开始，专业、便捷、贴心</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/')
      })
      .catch(() => {})
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'order') {
    router.push('/order')
  }
}
</script>

<style scoped>
.layout-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.logo-icon {
  font-size: 28px;
  color: var(--primary-color);
  margin-right: 8px;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: var(--primary-color);
}

.logo-sub {
  font-size: 12px;
  color: var(--secondary-color);
  margin-left: 6px;
  padding: 2px 6px;
  border: 1px solid var(--secondary-color);
  border-radius: 4px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  padding: 6px 14px;
  color: var(--text-regular);
  font-size: 15px;
  border-radius: 4px;
  transition: all 0.2s;
  text-decoration: none;
}

.nav-item:hover {
  color: var(--primary-color);
  background: rgba(25, 118, 210, 0.08);
  text-decoration: none;
}

.nav-item.router-link-exact-active,
.nav-item.router-link-active {
  color: var(--primary-color);
  font-weight: 600;
}

.user-dropdown {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  outline: none;
}

.app-main {
  flex: 1;
  width: 100%;
}

.footer-sub {
  font-size: 12px;
  margin-top: 4px;
  opacity: 0.7;
}

/* 路由过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
