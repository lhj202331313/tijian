import request from './request'

// 医生登录
export function login(data) {
  return request({
    url: '/doctor/login',
    method: 'post',
    data
  })
}

// 获取当前医生信息
export function getDoctorInfo() {
  return request({
    url: '/doctor/info',
    method: 'get'
  })
}

// 更新个人信息（真实姓名、联系电话、科室）
export function updateDoctorInfo(data) {
  return request({
    url: '/doctor/info',
    method: 'put',
    data
  })
}
