import request from '@/utils/request_jidi'

// 创建任务
export function createZkTask(data) {
  return request({
    url: '/fish/zk-task/create',
    method: 'post',
    data: data
  })
}

// 更新任务
export function updateZkTask(data) {
  return request({
    url: '/fish/zk-task/update',
    method: 'put',
    data: data
  })
}

// 删除任务
export function deleteZkTask(id) {
  return request({
    url: '/fish/zk-task/delete?id=' + id,
    method: 'delete'
  })
}

// 获得任务
export function getZkTask(id) {
  return request({
    url: '/fish/zk-task/get?id=' + id,
    method: 'get'
  })
}

// 获得任务分页
export function getZkTaskPage(query) {
  return request({
    url: '/fish/zk-task/page',
    method: 'get',
    params: query
  })
}

// 导出任务 Excel
export function exportZkTaskExcel(query) {
  return request({
    url: '/fish/zk-task/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
