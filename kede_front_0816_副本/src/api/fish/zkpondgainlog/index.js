import request from '@/utils/request'

// 创建鱼生长
export function createZkPondGainLog(data) {
  return request({
    url: '/fish/zk-pond-gain-log/create',
    method: 'post',
    data: data
  })
}

// 更新鱼生长
export function updateZkPondGainLog(data) {
  return request({
    url: '/fish/zk-pond-gain-log/update',
    method: 'put',
    data: data
  })
}

// 删除鱼生长
export function deleteZkPondGainLog(id) {
  return request({
    url: '/fish/zk-pond-gain-log/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除鱼生长 */
export function deleteZkPondGainLogList(ids) {
  return request({
    url: `/fish/zk-pond-gain-log/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得鱼生长
export function getZkPondGainLog(id) {
  return request({
    url: '/fish/zk-pond-gain-log/get?id=' + id,
    method: 'get'
  })
}

// 获得鱼生长分页
export function getZkPondGainLogPage(params) {
  return request({
    url: '/fish/zk-pond-gain-log/page',
    method: 'get',
    params
  })
}
// 导出鱼生长 Excel
export function exportZkPondGainLogExcel(params) {
  return request({
    url: '/fish/zk-pond-gain-log/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}