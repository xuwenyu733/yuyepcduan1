import request from '@/utils/request_jidi'

// 创建ERP 产品库存
export function createStock(data) {
  return request({
    url: '/erp/stock/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 产品库存
export function updateStock(data) {
  return request({
    url: '/erp/stock/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 产品库存
export function deleteStock(id) {
  return request({
    url: '/erp/stock/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 产品库存
export function getStock(id) {
  return request({
    url: '/erp/stock/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 产品库存（按 productId + warehouseId）
export function getStockByProductWarehouse(productId, warehouseId) {
  return request({
    url: '/erp/stock/get',
    method: 'get',
    params: { productId, warehouseId }
  })
}

// 获得ERP 产品库存分页
export function getStockPage(query) {
  return request({
    url: '/erp/stock/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 产品库存 Excel
export function exportStockExcel(query) {
  return request({
    url: '/erp/stock/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
