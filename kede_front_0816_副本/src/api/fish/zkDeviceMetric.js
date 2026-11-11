import request from '@/utils/request_jidi'

// 创建设备指标
export function createZkDeviceMetric(data) {
  return request({
    url: '/fish/zk-device-metric/create',
    method: 'post',
    data: data
  })
}

// 更新设备指标
export function updateZkDeviceMetric(data) {
  return request({
    url: '/fish/zk-device-metric/update',
    method: 'put',
    data: data
  })
}

// 删除设备指标
export function deleteZkDeviceMetric(id) {
  return request({
    url: '/fish/zk-device-metric/delete?id=' + id,
    method: 'delete'
  })
}

// 获得设备指标
export function getZkDeviceMetric(id) {
  return request({
    url: '/fish/zk-device-metric/get?id=' + id,
    method: 'get'
  })
}

// 获得设备指标分页
export function getZkDeviceMetricPage(query) {
  return request({
    url: '/fish/zk-device-metric/page',
    method: 'get',
    params: query
  })
}

// 导出设备指标 Excel
export function exportZkDeviceMetricExcel(query) {
  return request({
    url: '/fish/zk-device-metric/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
