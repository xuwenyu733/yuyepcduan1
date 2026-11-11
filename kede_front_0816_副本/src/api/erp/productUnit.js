import request from '@/utils/request_jidi'

// 创建ERP 产品单位
export function createProductUnit(data) {
  return request({
    url: '/erp/product-unit/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 产品单位
export function updateProductUnit(data) {
  return request({
    url: '/erp/product-unit/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 产品单位
export function deleteProductUnit(id) {
  return request({
    url: '/erp/product-unit/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 产品单位
export function getProductUnit(id) {
  return request({
    url: '/erp/product-unit/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 产品单位分页
export function getProductUnitPage(query) {
  return request({
    url: '/erp/product-unit/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 产品单位 Excel
export function exportProductUnitExcel(query) {
  return request({
    url: '/erp/product-unit/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
