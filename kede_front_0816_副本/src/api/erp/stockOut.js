import request from '@/utils/request_jidi'

// 创建ERP 其它入库单
export function createStockOut(data) {
  return request({
    url: '/erp/stock-out/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 其它入库单
export function updateStockOut(data) {
  return request({
    url: '/erp/stock-out/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 其它入库单
export function deleteStockOut(id) {
  return request({
    url: '/erp/stock-out/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 其它入库单
export function getStockOut(id) {
  return request({
    url: '/erp/stock-out/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 其它入库单分页
export function getStockOutPage(query) {
  return request({
    url: '/erp/stock-out/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 其它入库单 Excel
export function exportStockOutExcel(query) {
  return request({
    url: '/erp/stock-out/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
