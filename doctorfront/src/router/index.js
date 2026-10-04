import { createRouter, createWebHistory } from 'vue-router'
import { useDoctorStore } from '@/stores/doctor'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '医生登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/DoctorLayout.vue'),
    redirect: '/order',
    children: [
      {
        path: 'order',
        name: 'OrderList',
        component: () => import('@/views/OrderList.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/OrderDetail.vue'),
        meta: { title: '订单详情' }
      },
      {
        path: 'cireport/:orderId/:checkitemId',
        name: 'CiReportEdit',
        component: () => import('@/views/CiReportEdit.vue'),
        meta: { title: '分项报告录入' }
      },
      {
        path: 'overall/:orderId',
        name: 'OverallEdit',
        component: () => import('@/views/OverallEdit.vue'),
        meta: { title: '总检结论' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const doctorStore = useDoctorStore()
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 健诊通医生端`
  }
  // 公开路由直接放行
  if (to.meta.public) {
    next()
    return
  }
  // 需要登录的路由
  if (!doctorStore.isLogged) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
