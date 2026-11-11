import request from '@/utils/request_jidi'

// 创建用药明细
export function createZkUseMedicineLog(data) {
  return request({
    url: '/fish/zk-use-medicine-log/create',
    method: 'post',
    data: data
  })
}

// 更新用药明细
export function updateZkUseMedicineLog(data) {
  return request({
    url: '/fish/zk-use-medicine-log/update',
    method: 'put',
    data: data
  })
}

// 删除用药明细
export function deleteZkUseMedicineLog(id) {
  return request({
    url: '/fish/zk-use-medicine-log/delete?id=' + id,
    method: 'delete'
  })
}

// 获得用药明细
export function getZkUseMedicineLog(id) {
  return request({
    url: '/fish/zk-use-medicine-log/get?id=' + id,
    method: 'get'
  })
}

// 获得用药明细分页
export function getZkUseMedicineLogPage(query) {
  return request({
    url: '/fish/zk-use-medicine-log/page',
    method: 'get',
    params: query
  })
}

// 导出用药明细 Excel
export function exportZkUseMedicineLogExcel(query) {
  return request({
    url: '/fish/zk-use-medicine-log/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
