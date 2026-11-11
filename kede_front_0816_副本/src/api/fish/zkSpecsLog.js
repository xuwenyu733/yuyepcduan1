import request from '@/utils/request_jidi'
import { withUserDeptParams } from '@/utils/deptScope'

// 创建规格记录
export function createZkSpecsLog(data) {
  return request({
    url: '/fish/zk-specs-log/create',
    method: 'post',
    data: data
  })
}

// 更新规格记录
export function updateZkSpecsLog(data) {
  return request({
    url: '/fish/zk-specs-log/update',
    method: 'put',
    data: data
  })
}

// 删除规格记录
export function deleteZkSpecsLog(id) {
  return request({
    url: '/fish/zk-specs-log/delete?id=' + id,
    method: 'delete'
  })
}

// 获得规格记录
export function getZkSpecsLog(id) {
  return request({
    url: '/fish/zk-specs-log/get?id=' + id,
    method: 'get'
  })
}

// 获得规格记录分页
export function getZkSpecsLogPage(query) {
  return request({
    url: '/fish/zk-specs-log/page',
    method: 'get',
    params: query
  })
}

// 导出规格记录 Excel
export function exportZkSpecsLogExcel(query) {
  return request({
    url: '/fish/zk-specs-log/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 获得饵料系数模型列表
export function getFcrModelList(params = {}) {
  return request({
    url: '/fish/zk-specs-log/fcr-model-list',
    method: 'get',
    params: withUserDeptParams(params)
  })
}

