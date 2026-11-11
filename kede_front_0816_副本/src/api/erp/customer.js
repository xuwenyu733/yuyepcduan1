import request from '@/utils/request_jidi'

// 创建ERP 客户
export function createCustomer(data) {
  return request({
    url: '/erp/customer/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 客户
export function updateCustomer(data) {
  return request({
    url: '/erp/customer/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 客户
export function deleteCustomer(id) {
  return request({
    url: '/erp/customer/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 客户
export function getCustomer(id) {
  return request({
    url: '/erp/customer/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 客户分页
export function getCustomerPage(query) {
  return request({
    url: '/erp/customer/page',
    method: 'get',
    params: query
  })
}
export function getCustomerSimpleList(query) {
  return request({
    url: '/erp/customer/simple-list',
    method: 'get',
    params: query
  })
}

// 导出ERP 客户 Excel
export function exportCustomerExcel(query) {
  return request({
    url: '/erp/customer/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
