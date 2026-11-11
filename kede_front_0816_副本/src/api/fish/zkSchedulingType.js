import request from '@/utils/request_jidi'

// 创建检测配置
export function createZkSchedulingType(data) {
  return request({
    url: '/fish/zk-scheduling-type/create',
    method: 'post',
    data: data
  })
}

// 更新检测配置
export function updateZkSchedulingType(data) {
  return request({
    url: '/fish/zk-scheduling-type/update',
    method: 'put',
    data: data
  })
}

// 删除检测配置
export function deleteZkSchedulingType(id) {
  return request({
    url: '/fish/zk-scheduling-type/delete?id=' + id,
    method: 'delete'
  })
}

// 获得检测配置
export function getZkSchedulingType(id) {
  return request({
    url: '/fish/zk-scheduling-type/get?id=' + id,
    method: 'get'
  })
}

// 获得检测配置分页
export function getZkSchedulingTypePage(query) {
  return request({
    url: '/fish/zk-scheduling-type/page',
    method: 'get',
    params: query
  })
}

// 导出检测配置 Excel
export function exportZkSchedulingTypeExcel(query) {
  return request({
    url: '/fish/zk-scheduling-type/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
