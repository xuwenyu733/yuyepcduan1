import request from '@/utils/request_jidi'

// 创建成本
export function createZkProductCost(data) {
  return request({
    url: '/fish/zk-product-cost/create',
    method: 'post',
    data: data
  })
}

// 更新成本
export function updateZkProductCost(data) {
  return request({
    url: '/fish/zk-product-cost/update',
    method: 'put',
    data: data
  })
}

// 删除成本
export function deleteZkProductCost(id) {
  return request({
    url: '/fish/zk-product-cost/delete?id=' + id,
    method: 'delete'
  })
}

// 获得成本
export function getZkProductCost(id) {
  return request({
    url: '/fish/zk-product-cost/get?id=' + id,
    method: 'get'
  })
}

// 获得成本分页
export function getZkProductCostPage(query) {
  return request({
    url: '/fish/zk-product-cost/page',
    method: 'get',
    params: query
  })
}

// 导出成本 Excel
export function exportZkProductCostExcel(query) {
  return request({
    url: '/fish/zk-product-cost/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
