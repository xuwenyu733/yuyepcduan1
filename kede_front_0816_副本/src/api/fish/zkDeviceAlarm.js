import request from '@/utils/request_jidi'
import { withUserDeptParams } from '@/utils/deptScope'

// 创建设备报警
export function createZkDeviceAlarm(data, config = {}) {
  return request({
    url: '/fish/zk-device-alarm/create',
    method: 'post',
    data: data,
    ...config
  })
}

// 更新设备报警
export function updateZkDeviceAlarm(data) {
  return request({
    url: '/fish/zk-device-alarm/update',
    method: 'put',
    data: data
  })
}

// 删除设备报警
export function deleteZkDeviceAlarm(id) {
  return request({
    url: '/fish/zk-device-alarm/delete?id=' + id,
    method: 'delete'
  })
}

// 获得设备报警
export function getZkDeviceAlarm(id) {
  return request({
    url: '/fish/zk-device-alarm/get?id=' + id,
    method: 'get'
  })
}

// 获得设备报警分页
export function getZkDeviceAlarmPage(query) {
  return request({
    url: '/fish/zk-device-alarm/page',
    method: 'get',
    params: withUserDeptParams(query)
  })
}

// 获得设备报警列表，用于首页大屏
export function getZkDeviceAlarmDashboardList(params = {}) {
  return request({
    url: '/fish/zk-device-alarm/list-for-dashboard',
    method: 'get',
    params: withUserDeptParams(params)
  })
}

// 导出设备报警 Excel
export function exportZkDeviceAlarmExcel(query) {
  return request({
    url: '/fish/zk-device-alarm/export-excel',
    method: 'get',
    params: withUserDeptParams(query),
    responseType: 'blob'
  })
}
