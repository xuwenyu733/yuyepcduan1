import request from '@/utils/request_jidi'

// 创建ERP 供应商
export function createSupplier(data) {
  return request({
    url: '/erp/supplier/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 供应商
export function updateSupplier(data) {
  return request({
    url: '/erp/supplier/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 供应商
export function deleteSupplier(id) {
  return request({
    url: '/erp/supplier/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 供应商
export function getSupplier(id) {
  return request({
    url: '/erp/supplier/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 供应商分页 
export function getSupplierPage(query) {
  return request({
    url: '/erp/supplier/page',
    method: 'get',
    params: query
  })
}
// 获得ERP 供应商分页 listSimpleSuppliers
export function listSimpleSuppliers(query) {
  return request({
    url: '/erp/supplier/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 供应商 Excel
export function exportSupplierExcel(query) {
  return request({
    url: '/erp/supplier/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
