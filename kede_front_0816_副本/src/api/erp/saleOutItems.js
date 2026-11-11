import request from '@/utils/request_jidi'

// 创建ERP 销售出库项
export function createSaleOutItems(data) {
  return request({
    url: '/erp/sale-out-items/create',
    method: 'post',
    data: data
  })
}

// 更新ERP 销售出库项
export function updateSaleOutItems(data) {
  return request({
    url: '/erp/sale-out-items/update',
    method: 'put',
    data: data
  })
}

// 删除ERP 销售出库项
export function deleteSaleOutItems(id) {
  return request({
    url: '/erp/sale-out-items/delete?ids=' + id,
    method: 'delete'
  })
}

// 获得ERP 销售出库项
export function getSaleOutItems(id) {
  return request({
    url: '/erp/sale-out-items/get?id=' + id,
    method: 'get'
  })
}

// 获得ERP 销售出库项分页
export function getSaleOutPage(query) {
  return request({
    url: '/erp/sale-out-items/page',
    method: 'get',
    params: query
  })
}

// 导出ERP 销售出库项 Excel
export function exportSaleOutItemsExcel(query) {
  return request({
    url: '/erp/sale-out-items/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
