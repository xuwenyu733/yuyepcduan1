import request from '@/utils/request_jidi'

// 创建fish系统日志
export function createFishSystemLog(data) {
  return request({
    url: '/zk/fish-system-log/create',
    method: 'post',
    data: data
  })
}

// 更新fish系统日志
export function updateFishSystemLog(data) {
  return request({
    url: '/zk/fish-system-log/update',
    method: 'put',
    data: data
  })
}

// 删除fish系统日志
export function deleteFishSystemLog(id) {
  return request({
    url: '/zk/fish-system-log/delete?id=' + id,
    method: 'delete'
  })
}

// 获得fish系统日志
export function getFishSystemLog(id) {
  return request({
    url: '/zk/fish-system-log/get?id=' + id,
    method: 'get'
  })
}

// 获得fish系统日志分页
export function getFishSystemLogPage(query) {
  return request({
    url: '/zk/fish-system-log/page',
    method: 'get',
    params: query
  })
}

// 导出fish系统日志 Excel
export function exportFishSystemLogExcel(query) {
  return request({
    url: '/zk/fish-system-log/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
