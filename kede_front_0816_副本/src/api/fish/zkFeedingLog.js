import request from '@/utils/request_jidi'

// 创建投喂记录
export function createZkFeedingLog(data) {
  return request({
    url: '/fish/zk-feeding-log/create',
    method: 'post',
    data: data
  })
}

// 更新投喂记录
export function updateZkFeedingLog(data) {
  return request({
    url: '/fish/zk-feeding-log/update',
    method: 'put',
    data: data
  })
}

// 删除投喂记录
export function deleteZkFeedingLog(id) {
  return request({
    url: '/fish/zk-feeding-log/delete?id=' + id,
    method: 'delete'
  })
}

// 获得投喂记录
export function getZkFeedingLog(id) {
  return request({
    url: '/fish/zk-feeding-log/get?id=' + id,
    method: 'get'
  })
}

// 获得投喂记录分页
export function getZkFeedingLogPage(query) {
  return request({
    url: '/fish/zk-feeding-log/page',
    method: 'get',
    params: query
  })
}

// 导出投喂记录 Excel
export function exportZkFeedingLogExcel(query) {
  return request({
    url: '/fish/zk-feeding-log/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
