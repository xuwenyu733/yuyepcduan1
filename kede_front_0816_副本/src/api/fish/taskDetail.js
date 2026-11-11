import request from '@/utils/request_jidi'

// 创建任务明细
export function createTaskDetail(data) {
  return request({
    url: '/fish/task-detail/create',
    method: 'post',
    data: data
  })
}

// 更新任务明细
export function updateTaskDetail(data) {
  return request({
    url: '/fish/task-detail/update',
    method: 'put',
    data: data
  })
}

// 删除任务明细
export function deleteTaskDetail(id) {
  return request({
    url: '/fish/task-detail/delete?id=' + id,
    method: 'delete'
  })
}

// 获得任务明细
export function getTaskDetail(id) {
  return request({
    url: '/fish/task-detail/get?id=' + id,
    method: 'get'
  })
}

// 获得任务明细分页
export function getTaskDetailPage(query) {
  return request({
    url: '/fish/task-detail/page',
    method: 'get',
    params: query
  })
}

// 导出任务明细 Excel
export function exportTaskDetailExcel(query) {
  return request({
    url: '/fish/task-detail/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
