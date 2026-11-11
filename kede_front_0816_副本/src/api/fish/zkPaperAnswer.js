import request from '@/utils/request_jidi'

// 创建答题记录
export function createZkPaperAnswer(data) {
  return request({
    url: '/fish/zk-paper-answer/create',
    method: 'post',
    data: data
  })
}

// 更新答题记录
export function updateZkPaperAnswer(data) {
  return request({
    url: '/fish/zk-paper-answer/update',
    method: 'put',
    data: data
  })
}

// 删除答题记录
export function deleteZkPaperAnswer(id) {
  return request({
    url: '/fish/zk-paper-answer/delete?id=' + id,
    method: 'delete'
  })
}

// 获得答题记录
export function getZkPaperAnswer(id) {
  return request({
    url: '/fish/zk-paper-answer/get?id=' + id,
    method: 'get'
  })
}

// 获得答题记录分页
export function getZkPaperAnswerPage(query) {
  return request({
    url: '/fish/zk-paper-answer/page',
    method: 'get',
    params: query
  })
}

// 导出答题记录 Excel
export function exportZkPaperAnswerExcel(query) {
  return request({
    url: '/fish/zk-paper-answer/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
