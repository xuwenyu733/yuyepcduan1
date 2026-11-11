import request from '@/utils/request_jidi'

// 创建ERP 产品分类
export function createProductCategory(data) {
  return request({
    url: '/erp/product-category/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 产品分类
export function updateProductCategory(data) {
  return request({
    url: '/erp/product-category/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 产品分类
export function deleteProductCategory(id) {
  return request({
    url: '/erp/product-category/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 产品分类
export function getProductCategory(id) {
  return request({
    url: '/erp/product-category/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 产品分类分页
export function getProductCategoryPage(query) {
  return request({
    url: '/erp/product-category/list', // page
    method: 'get',
    params: query
  })
}

// 导出ERP 产品分类 Excel
export function exportProductCategoryExcel(query) {
  return request({
    url: '/erp/product-category/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
