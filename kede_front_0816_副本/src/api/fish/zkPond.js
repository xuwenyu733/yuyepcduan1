import request from '@/utils/request_jidi'
import { withUserDeptParams } from '@/utils/deptScope'

// 创建存塘
export function createZkPond(data) {
  return request({
    url: '/fish/zk-pond/create',
    method: 'post',
    data: data
  })
}

// 更新存塘
export function updateZkPond(data) {
  return request({
    url: '/fish/zk-pond/update',
    method: 'put',
    data: data
  })
}

// 删除存塘
export function deleteZkPond(id) {
  return request({
    url: '/fish/zk-pond/delete?id=' + id,
    method: 'delete'
  })
}

// 获得存塘 
export function getZkPond(id) {
  return request({
    url: '/fish/zk-pond/get?id=' + id,
    method: 'get'
  })
}

// 获得存塘 getFishLog
export function getFishLogById(id) {
  return request({
    url: '/fish/zk-pond/getFishLog?id=' + id,
    method: 'get'
  })
}

// 获得存塘 getFishLog
export function getFishLogByQuery(query) {
  return request({
    url: '/fish/zk-pond/getFishLog',
    method: 'get',
    params: query
  })
}

// 获得存塘分页
export function getZkPondPage(query) {
  return request({
    url: '/fish/zk-pond/page',
    method: 'get',
    params: withUserDeptParams(query || {})
  })
}

// 导出存塘 Excel
export function exportZkPondExcel(query) {
  return request({
    url: '/fish/zk-pond/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
