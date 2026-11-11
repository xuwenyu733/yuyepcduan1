import request from '@/utils/request_jidi'

// 创建存塘系统
export function createZkPondSystem(data) {
  return request({
    url: '/fish/zk-pond-system/create',
    method: 'post',
    data: data
  })
}

// 更新存塘系统
export function updateZkPondSystem(data) {
  return request({
    url: '/fish/zk-pond-system/update',
    method: 'put',
    data: data
  })
}

// 删除存塘系统
export function deleteZkPondSystem(id) {
  return request({
    url: '/fish/zk-pond-system/delete?id=' + id,
    method: 'delete'
  })
}

// 获得存塘系统
export function getZkPondSystem(id) {
  return request({
    url: '/fish/zk-pond-system/get?id=' + id,
    method: 'get'
  })
}

// 获得存塘系统分页
export function getZkPondSystemPage(query) {
  return request({
    url: '/fish/zk-pond-system/page',
    method: 'get',
    params: query
  })
}

// 导出存塘系统 Excel
export function exportZkPondSystemExcel(query) {
  return request({
    url: '/fish/zk-pond-system/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
