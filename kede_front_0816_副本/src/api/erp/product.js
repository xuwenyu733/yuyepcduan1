import request from '@/utils/request_jidi'

// 创建ERP 产品
export function createProduct(data) {
  return request({
    url: '/erp/product/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 产品
export function updateProduct(data) {
  return request({
    url: '/erp/product/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 产品
export function deleteProduct(id) {
  return request({
    url: '/erp/product/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 产品
export function getProduct(id) {
  return request({
    url: '/erp/product/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 产品分页
export function getProductPage(query) {
  return request({
    url: '/erp/product/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 产品 Excel
export function exportProductExcel(query) {
  return request({
    url: '/erp/product/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
