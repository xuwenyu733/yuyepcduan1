import request from '@/utils/request_jidi'

// 创建人工巡检
export function createZkManualInspection(data) {
  return request({
    url: '/fish/zk-manual-inspection/create',
    method: 'post',
    data: data
  })
}

// 更新人工巡检
export function updateZkManualInspection(data) {
  return request({
    url: '/fish/zk-manual-inspection/update',
    method: 'put',
    data: data
  })
}

// 删除人工巡检
export function deleteZkManualInspection(id) {
  return request({
    url: '/fish/zk-manual-inspection/delete?id=' + id,
    method: 'delete'
  })
}

// 获得人工巡检
export function getZkManualInspection(id) {
  return request({
    url: '/fish/zk-manual-inspection/get?id=' + id,
    method: 'get'
  })
}

// 获得人工巡检分页
export function getZkManualInspectionPage(query) {
  return request({
    url: '/fish/zk-manual-inspection/page',
    method: 'get',
    params: query
  })
}

// 导出人工巡检 Excel
export function exportZkManualInspectionExcel(query) {
  return request({
    url: '/fish/zk-manual-inspection/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
