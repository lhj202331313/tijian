import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getDoctorInfo, updateDoctorInfo } from '@/api/doctor'

export const useDoctorStore = defineStore('doctor', () => {
  // token
  const token = ref(localStorage.getItem('doctor_token') || '')

  // 医生信息
  const doctorInfo = ref(
    JSON.parse(localStorage.getItem('doctor_info') || 'null')
  )

  // 是否已登录
  const isLogged = computed(() => !!token.value)

  // 登录
  async function login(username, password) {
    const res = await loginApi({ username, password })
    const data = res.data
    if (data && data.token) {
      token.value = data.token
      localStorage.setItem('doctor_token', data.token)
      if (data.doctorInfo) {
        doctorInfo.value = data.doctorInfo
        localStorage.setItem('doctor_info', JSON.stringify(data.doctorInfo))
      }
    }
    return res
  }

  // 拉取最新医生信息
  async function fetchDoctorInfo() {
    const res = await getDoctorInfo()
    if (res.data) {
      doctorInfo.value = res.data
      localStorage.setItem('doctor_info', JSON.stringify(res.data))
    }
    return res
  }

  // 退出登录
  function logout() {
    token.value = ''
    doctorInfo.value = null
    localStorage.removeItem('doctor_token')
    localStorage.removeItem('doctor_info')
  }

  // 更新个人信息
  async function updateProfile(data) {
    const res = await updateDoctorInfo(data)
    if (res.data) {
      doctorInfo.value = res.data
      localStorage.setItem('doctor_info', JSON.stringify(res.data))
    }
    return res
  }

  return {
    token,
    doctorInfo,
    isLogged,
    login,
    fetchDoctorInfo,
    updateProfile,
    logout
  }
})
