import request from './request'

// 获取体检报告（分项报告）
export function getReport(orderId) {
  return request({
    url: `/report/${orderId}`,
    method: 'get'
  })
}

// 获取总检结论
export function getOverallReport(orderId) {
  return request({
    url: `/report/${orderId}/overall`,
    method: 'get'
  })
}
