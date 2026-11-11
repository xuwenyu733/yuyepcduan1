import request from '@/utils/request'

// 创建实验多媒体
export function createZkExperimentalMedia(data) {
  return request({
    url: '/fish/zk-experimental-media/create',
    method: 'post',
    data: data
  })
}

// 更新实验多媒体
export function updateZkExperimentalMedia(data) {
  return request({
    url: '/fish/zk-experimental-media/update',
    method: 'put',
    data: data
  })
}

// 删除实验多媒体
export function deleteZkExperimentalMedia(id) {
  return request({
    url: '/fish/zk-experimental-media/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除实验多媒体 */
export function deleteZkExperimentalMediaList(ids) {
  return request({
    url: `/fish/zk-experimental-media/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得实验多媒体
export function getZkExperimentalMedia(id) {
  return request({
    url: '/fish/zk-experimental-media/get?id=' + id,
    method: 'get'
  })
}

// 获得实验多媒体分页
export function getZkExperimentalMediaPage(params) {
  return request({
    url: '/fish/zk-experimental-media/page',
    method: 'get',
    params
  })
}
// 导出实验多媒体 Excel
export function exportZkExperimentalMediaExcel(params) {
  return request({
    url: '/fish/zk-experimental-media/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}