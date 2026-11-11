import request from '@/utils/request'

// 创建生长模型鱼池
export function createZkGrowPool(data) {
  return request({
    url: '/fish/zk-grow-pool/create',
    method: 'post',
    data: data
  })
}

// 更新生长模型鱼池
export function updateZkGrowPool(data) {
  return request({
    url: '/fish/zk-grow-pool/update',
    method: 'put',
    data: data
  })
}

// 删除生长模型鱼池
export function deleteZkGrowPool(id) {
  return request({
    url: '/fish/zk-grow-pool/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除生长模型鱼池 */
export function deleteZkGrowPoolList(ids) {
  return request({
    url: `/fish/zk-grow-pool/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得生长模型鱼池
export function getZkGrowPool(id) {
  return request({
    url: '/fish/zk-grow-pool/get?id=' + id,
    method: 'get'
  })
}

// 获得生长模型鱼池分页
export function getZkGrowPoolPage(params) {
  return request({
    url: '/fish/zk-grow-pool/page',
    method: 'get',
    params
  })
}
// 导出生长模型鱼池 Excel
export function exportZkGrowPoolExcel(params) {
  return request({
    url: '/fish/zk-grow-pool/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}