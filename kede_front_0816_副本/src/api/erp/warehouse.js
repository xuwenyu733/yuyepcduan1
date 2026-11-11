import request from '@/utils/request_jidi'

// 创建ERP 仓库
export function createWarehouse(data) {
  return request({
    url: '/erp/warehouse/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 仓库
export function updateWarehouse(data) {
  return request({
    url: '/erp/warehouse/update',
    method: 'put',
    data: data
  })
}

// 更新ERP 仓库
export function updateWarehouseDefaultStatus(data) {
  return request({
    url: '/erp/warehouse/update-default-status',
    method: 'put',
    params: data
  })
}

// 删除ERP 仓库
export function deleteWarehouse(id) {
  return request({
    url: '/erp/warehouse/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 仓库
export function getWarehouse(id) {
  return request({
    url: '/erp/warehouse/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 仓库分页
export function getWarehousePage(query) {
  return request({
    url: '/erp/warehouse/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 仓库 Excel
export function exportWarehouseExcel(query) {
  return request({
    url: '/erp/warehouse/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
