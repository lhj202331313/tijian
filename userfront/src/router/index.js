import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/UserLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'hospital',
        name: 'HospitalList',
        component: () => import('@/views/HospitalList.vue'),
        meta: { title: '医院列表' }
      },
      {
        path: 'hospital/:id',
        name: 'HospitalDetail',
        component: () => import('@/views/HospitalDetail.vue'),
        meta: { title: '医院详情' }
      },
      {
        path: 'setmeal',
        name: 'SetmealList',
        component: () => import('@/views/SetmealList.vue'),
        meta: { title: '套餐列表' }
      },
      {
        path: 'setmeal/:id',
        name: 'SetmealDetail',
        component: () => import('@/views/SetmealDetail.vue'),
        meta: { title: '套餐详情' }
      },
      {
        path: 'booking',
        name: 'Booking',
        component: () => import('@/views/Booking.vue'),
        meta: { title: '预约下单', requiresAuth: true }
      },
      {
        path: 'order',
        name: 'OrderList',
        component: () => import('@/views/OrderList.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/OrderDetail.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'report/:orderId',
        name: 'Report',
        component: () => import('@/views/Report.vue'),
        meta: { title: '体检报告', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫：需登录的页面未登录跳登录页并带 redirect 参数
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 健诊通` : '健诊通'
  if (to.meta.requiresAuth) {
    const userStore = useUserStore()
    if (!userStore.token) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
