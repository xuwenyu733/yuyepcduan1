import request from '@/utils/request_jidi'

// 创建ERP 采购入库
export function createPurchaseIn(data) {
  return request({
    url: '/erp/purchase-in/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 采购入库
export function updatePurchaseIn(data) {
  return request({
    url: '/erp/purchase-in/update',
    method: 'put',
    data: data
  })
}
export function updatePurchaseInStatus(data) {
  return request({
    url: '/erp/purchase-in/update-status',
    method: 'put',
    params: data
  })
}

// 删除ERP 采购入库
export function deletePurchaseIn(id) {
  return request({
    url: '/erp/purchase-in/delete?ids=' + [id],
    method: 'delete'
  })
}

// 获得ERP 采购入库
export function getPurchaseIn(id) {
  return request({
    url: '/erp/purchase-in/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 采购入库分页 
export function getPurchaseInPage(query) {
  return request({
    url: '/erp/purchase-in/page',
    method: 'get',
    params: query
  })
}
export function listForBatch(query) {
  return request({
    url: '/erp/purchase-in/listForBatch',
    method: 'get',
    params: query
  })
}
export function listForCost(query) {
  return request({
    url: '/erp/purchase-in/listForCost',
    method: 'get',
    params: query
  })
}
export function listForCostPage(query) {
  return request({
    url: '/erp/purchase-in/listForCostPage',
    method: 'get',
    params: query
  })
}

// 导出ERP 采购入库 Excel
export function exportPurchaseInExcel(query) {
  return request({
    url: '/erp/purchase-in/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
