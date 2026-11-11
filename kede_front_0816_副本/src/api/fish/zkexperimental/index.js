import request from '@/utils/request'

// 创建实验
export function createZkExperimental(data) {
  return request({
    url: '/fish/zk-experimental/create',
    method: 'post',
    data: data
  })
}

// 更新实验
export function updateZkExperimental(data) {
  return request({
    url: '/fish/zk-experimental/update',
    method: 'put',
    data: data
  })
}

// 删除实验
export function deleteZkExperimental(id) {
  return request({
    url: '/fish/zk-experimental/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除实验 */
export function deleteZkExperimentalList(ids) {
  return request({
    url: `/fish/zk-experimental/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得实验
export function getZkExperimental(id) {
  return request({
    url: '/fish/zk-experimental/get?id=' + id,
    method: 'get'
  })
}

// 获得实验分页
export function getZkExperimentalPage(params) {
  return request({
    url: '/fish/zk-experimental/page',
    method: 'get',
    params
  })
}
// 导出实验 Excel
export function exportZkExperimentalExcel(params) {
  return request({
    url: '/fish/zk-experimental/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}