import request from './request'

// 获取总检结论（草稿/已发布都返回）
export function getOverall(orderId) {
  return request({
    url: `/overall/${orderId}`,
    method: 'get'
  })
}

// 保存/更新总检结论
export function saveOverall(data) {
  return request({
    url: '/overall/save',
    method: 'post',
    data
  })
}

// 发布总检结论
export function publishOverall(id) {
  return request({
    url: `/overall/${id}/publish`,
    method: 'put'
  })
}
