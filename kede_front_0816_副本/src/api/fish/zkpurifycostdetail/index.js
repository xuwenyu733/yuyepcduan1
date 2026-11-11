import request from '@/utils/request'

// 创建成本明细记录
export function createPurifyCostDetail(data) {
  return request({
    url: '/fish/purify-cost-detail/create',
    method: 'post',
    data: data
  })
}

// 更新成本明细记录
export function updatePurifyCostDetail(data) {
  return request({
    url: '/fish/purify-cost-detail/update',
    method: 'put',
    data: data
  })
}

// 删除成本明细记录
export function deletePurifyCostDetail(id) {
  return request({
    url: '/fish/purify-cost-detail/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除成本明细记录 */
export function deletePurifyCostDetailList(ids) {
  return request({
    url: `/fish/purify-cost-detail/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得成本明细记录
export function getPurifyCostDetail(id) {
  return request({
    url: '/fish/purify-cost-detail/get?id=' + id,
    method: 'get'
  })
}

// 获得成本明细记录分页
export function getPurifyCostDetailPage(params) {
  return request({
    url: '/fish/purify-cost-detail/page',
    method: 'get',
    params
  })
}
// 导出成本明细记录 Excel
export function exportPurifyCostDetailExcel(params) {
  return request({
    url: '/fish/purify-cost-detail/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}