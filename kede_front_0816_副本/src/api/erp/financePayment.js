import request from '@/utils/request_jidi'

// 创建ERP 付款单
export function createFinancePayment(data) {
  return request({
    url: '/erp/finance-payment/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 付款单
export function updateFinancePayment(data) {
  return request({
    url: '/erp/finance-payment/update',
    method: 'put',
    data: data
  })
}
export function updateFinancePayStatus(data) {
  return request({
    url: '/erp/finance-payment/update-status',
    method: 'put',
    params: data
  })
}

// 删除ERP 付款单
export function deleteFinancePayment(id) {
  return request({
    url: '/erp/finance-payment/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 付款单
export function getFinancePayment(id) {
  return request({
    url: '/erp/finance-payment/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 付款单分页
export function getFinancePaymentPage(query) {
  return request({
    url: '/erp/finance-payment/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 付款单 Excel
export function exportFinancePaymentExcel(query) {
  return request({
    url: '/erp/finance-payment/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
