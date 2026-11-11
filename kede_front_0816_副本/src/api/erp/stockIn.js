import request from '@/utils/request_jidi'

// 创建ERP 其它入库单
export function createStockIn(data) {
  return request({
    url: '/erp/stock-in/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 其它入库单
export function updateStockIn(data) {
  return request({
    url: '/erp/stock-in/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 其它入库单
export function deleteStockIn(id) {
  return request({
    url: '/erp/stock-in/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 其它入库单
export function getStockIn(id) {
  return request({
    url: '/erp/stock-in/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 其它入库单分页
export function getStockInPage(query) {
  return request({
    url: '/erp/stock-in/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 其它入库单 Excel
export function exportStockInExcel(query) {
  return request({
    url: '/erp/stock-in/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
