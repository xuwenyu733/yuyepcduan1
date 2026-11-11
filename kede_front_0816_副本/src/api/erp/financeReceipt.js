import request from '@/utils/request_jidi'

// 创建ERP 收款单
export function createFinanceReceipt(data) {
  return request({
    url: '/erp/finance-receipt/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 收款单
export function updateFinanceReceipt(data) {
  return request({
    url: '/erp/finance-receipt/update',
    method: 'put',
    data: data
  })
}
export function updateFinanceReceiptStatus(data) {
  return request({
    url: '/erp/finance-receipt/update-status',
    method: 'put',
    params: data
  })
}

// 删除ERP 收款单
export function deleteFinanceReceipt(id) {
  return request({
    url: '/erp/finance-receipt/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 收款单
export function getFinanceReceipt(id) {
  return request({
    url: '/erp/finance-receipt/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 收款单分页
export function getFinanceReceiptPage(query) {
  return request({
    url: '/erp/finance-receipt/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 收款单 Excel
export function exportFinanceReceiptExcel(query) {
  return request({
    url: '/erp/finance-receipt/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
