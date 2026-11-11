import request from '@/utils/request_jidi'

// 创建设备属性记录
export function createZkDeviceProperties(data) {
  return request({
    url: '/fish/zk-device-properties/create',
    method: 'post',
    data: data
  })
}

// 更新设备属性记录
export function updateZkDeviceProperties(data) {
  return request({
    url: '/fish/zk-device-properties/update',
    method: 'put',
    data: data
  })
}

// 删除设备属性记录
export function deleteZkDeviceProperties(id) {
  return request({
    url: '/fish/zk-device-properties/delete?id=' + id,
    method: 'delete'
  })
}

// 获得设备属性记录
export function getZkDeviceProperties(id) {
  return request({
    url: '/fish/zk-device-properties/get?id=' + id,
    method: 'get'
  })
}

// 获得设备属性记录分页
export function getZkDevicePropertiesPage(query) {
  return request({
    url: '/fish/zk-device-properties/page',
    method: 'get',
    params: query
  })
}

// 导出设备属性记录 Excel
export function exportZkDevicePropertiesExcel(query) {
  return request({
    url: '/fish/zk-device-properties/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
