import request from '@/utils/request_jidi'
import { withUserDeptParams } from '@/utils/deptScope'

// 创建设备
export function createZkDevice(data) {
  return request({
    url: '/fish/zk-device/create',
    method: 'post',
    data: data
  })
}

// 更新设备
export function updateZkDevice(data) {
  return request({
    url: '/fish/zk-device/update',
    method: 'put',
    data: data
  })
}

// 删除设备
export function deleteZkDevice(id) {
  return request({
    url: '/fish/zk-device/delete?id=' + id,
    method: 'delete'
  })
}

// 获得设备
export function getZkDevice(id) {
  return request({
    url: '/fish/zk-device/get?id=' + id,
    method: 'get'
  })
}

// 获得设备分页
export function getZkDevicePage(query) {
  return request({
    url: '/fish/zk-device/page',
    method: 'get',
    params: withUserDeptParams(query)
  })
}

// 导出设备 Excel
export function exportZkDeviceExcel(query) {
  return request({
    url: '/fish/zk-device/export-excel',
    method: 'get',
    params: withUserDeptParams(query),
    responseType: 'blob'
  })
}
