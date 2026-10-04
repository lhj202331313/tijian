import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器：自动携带 token
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理 Result<T> 结构、401 跳登录、统一提示
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 非标准 Result 结构（如直接返回二进制）直接放行
    if (res === null || res === undefined) {
      return response
    }
    // 如果不是标准 Result（无 code 字段），直接返回原始数据
    if (res.code === undefined) {
      return response
    }
    if (res.code === 200) {
      return res
    }
    // 业务错误
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || 'Error'))
  },
  (error) => {
    const { response } = error
    if (response) {
      if (response.status === 401) {
        // 未授权，清除登录态并跳登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        ElMessage.error('登录已过期，请重新登录')
        const redirect = router.currentRoute.value.fullPath
        router.push({ path: '/login', query: { redirect } })
      } else if (response.status === 403) {
        ElMessage.error('没有权限访问该资源')
      } else if (response.data && response.data.message) {
        ElMessage.error(response.data.message)
      } else {
        ElMessage.error(`请求错误 (${response.status})`)
      }
    } else {
      ElMessage.error('网络异常，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default service
