import request from './request'

// 某订单的分项报告列表
export function getCiReportList(orderId) {
  return request({
    url: '/cireport/list',
    method: 'get',
    params: { orderId }
  })
}

// 单个检查项的报告详情
export function getCiReportDetail(orderId, checkitemId) {
  return request({
    url: `/cireport/${orderId}/${checkitemId}`,
    method: 'get'
  })
}

// 录入/更新分项报告
export function saveCiReport(data) {
  return request({
    url: '/cireport/save',
    method: 'post',
    data
  })
}
