import request from '@/utils/request_jidi'

// 创建试题
export function createZkPaperQuestion(data) {
  return request({
    url: '/fish/zk-paper-question/create',
    method: 'post',
    data: data
  })
}

// 更新试题
export function updateZkPaperQuestion(data) {
  return request({
    url: '/fish/zk-paper-question/update',
    method: 'put',
    data: data
  })
}

// 删除试题
export function deleteZkPaperQuestion(id) {
  return request({
    url: '/fish/zk-paper-question/delete?id=' + id,
    method: 'delete'
  })
}

// 获得试题
export function getZkPaperQuestion(id) {
  return request({
    url: '/fish/zk-paper-question/get?id=' + id,
    method: 'get'
  })
}

// 获得试题分页
export function getZkPaperQuestionPage(query) {
  return request({
    url: '/fish/zk-paper-question/page',
    method: 'get',
    params: query
  })
}

// 导出试题 Excel
export function exportZkPaperQuestionExcel(query) {
  return request({
    url: '/fish/zk-paper-question/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
