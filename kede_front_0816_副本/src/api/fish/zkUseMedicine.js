import request from '@/utils/request_jidi'

// 创建用药记录
export function createZkUseMedicine(data) {
  return request({
    url: '/fish/zk-use-medicine/create',
    method: 'post',
    data: data
  })
}

// 更新用药记录
export function updateZkUseMedicine(data) {
  return request({
    url: '/fish/zk-use-medicine/update',
    method: 'put',
    data: data
  })
}

// 删除用药记录
export function deleteZkUseMedicine(id) {
  return request({
    url: '/fish/zk-use-medicine/delete?id=' + id,
    method: 'delete'
  })
}

// 获得用药记录
export function getZkUseMedicine(id) {
  return request({
    url: '/fish/zk-use-medicine/get?id=' + id,
    method: 'get'
  })
}

// 获得用药记录分页
export function getZkUseMedicinePage(query) {
  return request({
    url: '/fish/zk-use-medicine/page',
    method: 'get',
    params: query
  })
}

// 导出用药记录 Excel
export function exportZkUseMedicineExcel(query) {
  return request({
    url: '/fish/zk-use-medicine/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
