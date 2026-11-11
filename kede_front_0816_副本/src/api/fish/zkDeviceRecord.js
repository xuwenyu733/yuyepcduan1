import request from '@/utils/request_jidi'
import { withUserDeptParams } from '@/utils/deptScope'

// 创建设备数据记录
export function createZkDeviceRecord(data) {
  return request({
    url: '/fish/zk-device-record/create',
    method: 'post',
    data: data
  })
}

// 更新设备数据记录
export function updateZkDeviceRecord(data) {
  return request({
    url: '/fish/zk-device-record/update',
    method: 'put',
    data: data
  })
}

// 删除设备数据记录
export function deleteZkDeviceRecord(id) {
  return request({
    url: '/fish/zk-device-record/delete?id=' + id,
    method: 'delete'
  })
}

// 获得设备数据记录
export function getZkDeviceRecord(id) {
  return request({
    url: '/fish/zk-device-record/get?id=' + id,
    method: 'get'
  })
}

// 获得设备数据记录分页
export function getZkDeviceRecordPage(query) {
  return request({
    url: '/fish/zk-device-record/page',
    method: 'get',
    params: withUserDeptParams(query || {})
  })
}

/** 水质页补数用：静默拉取最新一条，避免 404/空结果弹窗 */
export function getZkDeviceRecordLatestSilent(metricId) {
  return request({
    url: '/fish/zk-device-record/page',
    method: 'get',
    params: withUserDeptParams({ pageNo: 1, pageSize: 1, metricId }),
    silent: true,
    hideErrorMessage: true
  })
}

// 导出设备数据记录 Excel
export function exportZkDeviceRecordExcel(query) {
  return request({
    url: '/fish/zk-device-record/export-excel',
    method: 'get',
    params: withUserDeptParams(query || {}),
    responseType: 'blob'
  })
}
