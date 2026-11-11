import request from '@/utils/request_jidi'

// 创建设备事件记录
export function createZkDeviceEvent(data) {
  return request({
    url: '/fish/zk-device-event/create',
    method: 'post',
    data: data
  })
}

// 更新设备事件记录
export function updateZkDeviceEvent(data) {
  return request({
    url: '/fish/zk-device-event/update',
    method: 'put',
    data: data
  })
}

// 删除设备事件记录
export function deleteZkDeviceEvent(id) {
  return request({
    url: '/fish/zk-device-event/delete?id=' + id,
    method: 'delete'
  })
}

// 获得设备事件记录
export function getZkDeviceEvent(id) {
  return request({
    url: '/fish/zk-device-event/get?id=' + id,
    method: 'get'
  })
}

// 获得设备事件记录分页
export function getZkDeviceEventPage(query) {
  return request({
    url: '/fish/zk-device-event/page',
    method: 'get',
    params: query
  })
}

// 导出设备事件记录 Excel
export function exportZkDeviceEventExcel(query) {
  return request({
    url: '/fish/zk-device-event/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
