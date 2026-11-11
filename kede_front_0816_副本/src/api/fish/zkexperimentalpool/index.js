import request from '@/utils/request'

// 创建实验鱼池
export function createZkExperimentalPool(data) {
  return request({
    url: '/fish/zk-experimental-pool/create',
    method: 'post',
    data: data
  })
}

// 更新实验鱼池
export function updateZkExperimentalPool(data) {
  return request({
    url: '/fish/zk-experimental-pool/update',
    method: 'put',
    data: data
  })
}

// 删除实验鱼池
export function deleteZkExperimentalPool(id) {
  return request({
    url: '/fish/zk-experimental-pool/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除实验鱼池 */
export function deleteZkExperimentalPoolList(ids) {
  return request({
    url: `/fish/zk-experimental-pool/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得实验鱼池
export function getZkExperimentalPool(id) {
  return request({
    url: '/fish/zk-experimental-pool/get?id=' + id,
    method: 'get'
  })
}

// 获得实验鱼池分页
export function getZkExperimentalPoolPage(params) {
  return request({
    url: '/fish/zk-experimental-pool/page',
    method: 'get',
    params
  })
}
// 导出实验鱼池 Excel
export function exportZkExperimentalPoolExcel(params) {
  return request({
    url: '/fish/zk-experimental-pool/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}