import request from '@/utils/request_jidi'

// 创建app记录
export function createZkAppLog(data) {
  return request({
    url: '/fish/zk-app-log/create',
    method: 'post',
    data: data
  })
}

// 更新app记录
export function updateZkAppLog(data) {
  return request({
    url: '/fish/zk-app-log/update',
    method: 'put',
    data: data
  })
}

// 删除app记录
export function deleteZkAppLog(id) {
  return request({
    url: '/fish/zk-app-log/delete?id=' + id,
    method: 'delete'
  })
}

// 获得app记录
export function getZkAppLog(id) {
  return request({
    url: '/fish/zk-app-log/get?id=' + id,
    method: 'get'
  })
}

// 获得app记录分页
export function getZkAppLogPage(query) {
  return request({
    url: '/fish/zk-app-log/page',
    method: 'get',
    params: query
  })
}

// 导出app记录 Excel
export function exportZkAppLogExcel(query) {
  return request({
    url: '/fish/zk-app-log/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
