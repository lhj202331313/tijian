import request from './request'

// 按医院查询在职接待医生
export function getDoctorsByHospital(hospitalId) {
  return request({
    url: '/doctor/list',
    method: 'get',
    params: { hospitalId }
  })
}
