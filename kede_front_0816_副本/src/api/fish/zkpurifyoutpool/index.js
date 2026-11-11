import request from '@/utils/request'

// 创建出池登记
export function createPurifyOutPool(data) {
  return request({
    url: '/fish/purify-out-pool/create',
    method: 'post',
    data: data
  })
}

// 更新出池登记
export function updatePurifyOutPool(data) {
  return request({
    url: '/fish/purify-out-pool/update',
    method: 'put',
    data: data
  })
}

// 删除出池登记
export function deletePurifyOutPool(id) {
  return request({
    url: '/fish/purify-out-pool/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除出池登记 */
export function deletePurifyOutPoolList(ids) {
  return request({
    url: `/fish/purify-out-pool/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得出池登记
export function getPurifyOutPool(id) {
  return request({
    url: '/fish/purify-out-pool/get?id=' + id,
    method: 'get'
  })
}

// 获得出池登记分页
export function getPurifyOutPoolPage(params) {
  return request({
    url: '/fish/purify-out-pool/page',
    method: 'get',
    params
  })
}
// 导出出池登记 Excel
export function exportPurifyOutPoolExcel(params) {
  return request({
    url: '/fish/purify-out-pool/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}