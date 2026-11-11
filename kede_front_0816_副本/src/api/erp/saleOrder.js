import request from '@/utils/request_jidi'

// 创建ERP 销售订单
export function createSaleOrder(data) {
  return request({
    url: '/erp/sale-order/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 销售订单
export function updateSaleOrder(data) {
  return request({
    url: '/erp/sale-order/update',
    method: 'put',
    data: data
  })
}
export function updateSaleOrderStatus(data) {
  return request({
    url: '/erp/sale-order/update-status',
    method: 'put',
    params: data
  })
}

// 删除ERP 销售订单
export function deleteSaleOrder(id) {
  return request({
    url: '/erp/sale-order/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 销售订单
export function getSaleOrder(id) {
  return request({
    url: '/erp/sale-order/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 销售订单分页
export function getSaleOrderPage(query) {
  return request({
    url: '/erp/sale-order/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 销售订单 Excel
export function exportSaleOrderExcel(query) {
  return request({
    url: '/erp/sale-order/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
