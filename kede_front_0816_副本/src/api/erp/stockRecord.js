import request from '@/utils/request_jidi'

// 创建ERP 产品库存明细
export function createStockRecord(data) {
  return request({
    url: '/erp/stock-record/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 产品库存明细
export function updateStockRecord(data) {
  return request({
    url: '/erp/stock-record/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 产品库存明细
export function deleteStockRecord(id) {
  return request({
    url: '/erp/stock-record/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 产品库存明细
export function getStockRecord(id) {
  return request({
    url: '/erp/stock-record/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 产品库存明细分页
export function getStockRecordPage(query) {
  return request({
    url: '/erp/stock-record/page',
    method: 'get',
    params: query
  })
}
export function getStockPage(query) {
  return request({
    url: '/erp/stock/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 产品库存明细 Excel
export function exportStockRecordExcel(query) {
  return request({
    url: '/erp/stock-record/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
