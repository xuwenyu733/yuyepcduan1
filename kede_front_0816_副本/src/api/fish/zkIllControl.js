import request from '@/utils/request_jidi'

// 创建疾病诊断与防治记录
export function createZkIllControl(data) {
  return request({
    url: '/fish/zk-ill-control/create',
    method: 'post',
    data: data
  })
}

// 更新疾病诊断与防治记录
export function updateZkIllControl(data) {
  return request({
    url: '/fish/zk-ill-control/update',
    method: 'put',
    data: data
  })
}

// 删除疾病诊断与防治记录
export function deleteZkIllControl(id) {
  return request({
    url: '/fish/zk-ill-control/delete?id=' + id,
    method: 'delete'
  })
}

// 获得疾病诊断与防治记录
export function getZkIllControl(id) {
  return request({
    url: '/fish/zk-ill-control/get?id=' + id,
    method: 'get'
  })
}

// 获得疾病诊断与防治记录分页
export function getZkIllControlPage(query) {
  return request({
    url: '/fish/zk-ill-control/page',
    method: 'get',
    params: query
  })
}

// 导出疾病诊断与防治记录 Excel
export function exportZkIllControlExcel(query) {
  return request({
    url: '/fish/zk-ill-control/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
