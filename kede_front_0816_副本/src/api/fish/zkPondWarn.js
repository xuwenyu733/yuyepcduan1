import request from '@/utils/request_jidi'

// 创建告警
export function createZkPondWarn(data) {
  return request({
    url: '/fish/zk-pond-warn/create',
    method: 'post',
    data: data
  })
}

// 更新告警
export function updateZkPondWarn(data) {
  return request({
    url: '/fish/zk-pond-warn/update',
    method: 'put',
    data: data
  })
}

// 删除告警
export function deleteZkPondWarn(id) {
  return request({
    url: '/fish/zk-pond-warn/delete?id=' + id,
    method: 'delete'
  })
}

// 获得告警
export function getZkPondWarn(id) {
  return request({
    url: '/fish/zk-pond-warn/get?id=' + id,
    method: 'get'
  })
}

// 获得告警分页
export function getZkPondWarnPage(query) {
  return request({
    url: '/fish/zk-pond-warn/page',
    method: 'get',
    params: query
  })
}

// 导出告警 Excel
export function exportZkPondWarnExcel(query) {
  return request({
    url: '/fish/zk-pond-warn/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
