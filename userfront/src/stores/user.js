import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo, updateUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 从 localStorage 初始化 token
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLogin = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')

  // 设置 token 并持久化
  function setToken(t) {
    token.value = t
    if (t) {
      localStorage.setItem('token', t)
    } else {
      localStorage.removeItem('token')
    }
  }

  // 设置用户信息并持久化
  function setUserInfo(info) {
    userInfo.value = info
    if (info) {
      localStorage.setItem('userInfo', JSON.stringify(info))
    } else {
      localStorage.removeItem('userInfo')
    }
  }

  // 登录
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    if (res && res.code === 200) {
      const data = res.data
      setToken(data.token)
      setUserInfo(data.userInfo)
      return true
    }
    return false
  }

  // 获取当前用户信息
  async function fetchUserInfo() {
    const res = await getUserInfo()
    if (res && res.code === 200) {
      setUserInfo(res.data)
      return res.data
    }
    return null
  }

  // 更新个人信息
  async function editUserInfo(form) {
    const res = await updateUserInfo(form)
    if (res && res.code === 200) {
      await fetchUserInfo()
      return true
    }
    return false
  }

  // 登出
  function logout() {
    setToken('')
    setUserInfo(null)
  }

  return {
    token,
    userInfo,
    isLogin,
    username,
    setToken,
    setUserInfo,
    login,
    fetchUserInfo,
    editUserInfo,
    logout
  }
})
