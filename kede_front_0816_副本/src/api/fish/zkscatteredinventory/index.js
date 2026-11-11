import request from '@/utils/request'

// 创建零星库存
export function createZkScatteredInventory(data) {
  return request({
    url: '/fish/zk-scattered-inventory/create',
    method: 'post',
    data: data
  })
}

// 更新零星库存
export function updateZkScatteredInventory(data) {
  return request({
    url: '/fish/zk-scattered-inventory/update',
    method: 'put',
    data: data
  })
}

// 删除零星库存
export function deleteZkScatteredInventory(id) {
  return request({
    url: '/fish/zk-scattered-inventory/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除零星库存 */
export function deleteZkScatteredInventoryList(ids) {
  return request({
    url: `/fish/zk-scattered-inventory/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得零星库存
export function getZkScatteredInventory(id) {
  return request({
    url: '/fish/zk-scattered-inventory/get?id=' + id,
    method: 'get'
  })
}

// 获得零星库存分页
export function getZkScatteredInventoryPage(params) {
  return request({
    url: '/fish/zk-scattered-inventory/page',
    method: 'get',
    params
  })
}
// 导出零星库存 Excel
export function exportZkScatteredInventoryExcel(params) {
  return request({
    url: '/fish/zk-scattered-inventory/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}