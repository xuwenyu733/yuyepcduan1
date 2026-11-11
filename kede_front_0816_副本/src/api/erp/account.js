import request from '@/utils/request_jidi'

// 创建ERP 结算账户
export function createAccount(data) {
  return request({
    url: '/erp/account/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 结算账户
export function updateAccount(data) {
  return request({
    url: '/erp/account/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 结算账户
export function deleteAccount(id) {
  return request({
    url: '/erp/account/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 结算账户
export function getAccount(id) {
  return request({
    url: '/erp/account/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 结算账户分页
export function getAccountPage(query) {
  return request({
    url: '/erp/account/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 结算账户 Excel
export function exportAccountExcel(query) {
  return request({
    url: '/erp/account/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
