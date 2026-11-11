import request from '@/utils/request_jidi'

// 创建ERP 销售出库
export function createSaleOut(data) {
  return request({
    url: '/erp/sale-out/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 销售出库
export function updateSaleOut(data) {
  return request({
    url: '/erp/sale-out/update',
    method: 'put',
    data: data
  })
}
export function updateSaleOutStatus(data) {
  return request({
    url: '/erp/sale-out/update-status',
    method: 'put',
    params: data
  })
}

// 删除ERP 销售出库
export function deleteSaleOut(id) {
  return request({
    url: '/erp/sale-out/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 销售出库
export function getSaleOut(id) {
  return request({
    url: '/erp/sale-out/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 销售出库分页
export function getSaleOutPage(query) {
  return request({
    url: '/erp/sale-out/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 销售出库 Excel
export function exportSaleOutExcel(query) {
  return request({
    url: '/erp/sale-out/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
