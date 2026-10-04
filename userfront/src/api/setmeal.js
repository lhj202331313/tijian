import request from './request'

// 套餐列表（可按 hospitalId、type 筛选）
export function getSetmealList(params) {
  return request({
    url: '/setmeal/list',
    method: 'get',
    params
  })
}

// 套餐详情（含检查项）
export function getSetmealDetail(id) {
  return request({
    url: `/setmeal/${id}`,
    method: 'get'
  })
}
