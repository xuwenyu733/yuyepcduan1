import request from '@/utils/request_jidi'

// 创建ERP 采购订单
export function createPurchaseOrder(data) {
  return request({
    url: '/erp/purchase-order/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 采购订单
export function updatePurchaseOrder(data) {
  return request({
    url: '/erp/purchase-order/update',
    method: 'put',
    data: data
  })
}
export function updatePurchaseOrderStatus(data) {
  return request({
    url: '/erp/purchase-order/update-status',
    method: 'put',
    params: data
  })
}

// 删除ERP 采购订单
export function deletePurchaseOrder(id) {
  return request({
    url: '/erp/purchase-order/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 采购订单
export function getPurchaseOrder(id) {
  return request({
    url: '/erp/purchase-order/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 采购订单分页
export function getPurchaseOrderPage(query) {
  return request({
    url: '/erp/purchase-order/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 采购订单 Excel
export function exportPurchaseOrderExcel(query) {
  return request({
    url: '/erp/purchase-order/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
