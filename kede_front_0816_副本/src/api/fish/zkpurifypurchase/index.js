import request from '@/utils/request'

// 创建采购记录
export function createPurifyPurchase(data) {
  return request({
    url: '/fish/purify-purchase/create',
    method: 'post',
    data: data
  })
}

// 更新采购记录
export function updatePurifyPurchase(data) {
  return request({
    url: '/fish/purify-purchase/update',
    method: 'put',
    data: data
  })
}

// 删除采购记录
export function deletePurifyPurchase(id) {
  return request({
    url: '/fish/purify-purchase/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除采购记录 */
export function deletePurifyPurchaseList(ids) {
  return request({
    url: `/fish/purify-purchase/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得采购记录
export function getPurifyPurchase(id) {
  return request({
    url: '/fish/purify-purchase/get?id=' + id,
    method: 'get'
  })
}

// 获得采购记录分页
export function getPurifyPurchasePage(params) {
  return request({
    url: '/fish/purify-purchase/page',
    method: 'get',
    params
  })
}
// 导出采购记录 Excel
export function exportPurifyPurchaseExcel(params) {
  return request({
    url: '/fish/purify-purchase/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}