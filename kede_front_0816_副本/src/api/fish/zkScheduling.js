import request from '@/utils/request_jidi'

// 创建排班
export function createZkScheduling(data) {
  return request({
    url: '/fish/zk-scheduling/create',
    method: 'post',
    data: data
  })
}

// 更新排班
export function updateZkScheduling(data) {
  return request({
    url: '/fish/zk-scheduling/update',
    method: 'put',
    data: data
  })
}

// 删除排班
export function deleteZkScheduling(id) {
  return request({
    url: '/fish/zk-scheduling/delete?id=' + id,
    method: 'delete'
  })
}

// 获得排班
export function getZkScheduling(id) {
  return request({
    url: '/fish/zk-scheduling/get?id=' + id,
    method: 'get'
  })
}

// 获得排班分页
export function getZkSchedulingPage(query) {
  return request({
    url: '/fish/zk-scheduling/page',
    method: 'get',
    params: query
  })
}

// 导出排班 Excel
export function exportZkSchedulingExcel(query) {
  return request({
    url: '/fish/zk-scheduling/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
