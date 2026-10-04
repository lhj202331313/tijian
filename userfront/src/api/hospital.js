import request from './request'

// 医院列表
export function getHospitalList() {
  return request({
    url: '/hospital/list',
    method: 'get'
  })
}

// 医院详情（含套餐）
export function getHospitalDetail(id) {
  return request({
    url: `/hospital/${id}`,
    method: 'get'
  })
}
