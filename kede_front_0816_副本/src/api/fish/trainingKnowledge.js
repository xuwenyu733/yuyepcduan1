import request from '@/utils/request_jidi'

// 创建培训知识
export function createTrainingKnowledge(data) {
  return request({
    url: '/fish/training-knowledge/create',
    method: 'post',
    data: data
  })
}

// 更新培训知识
export function updateTrainingKnowledge(data) {
  return request({
    url: '/fish/training-knowledge/update',
    method: 'put',
    data: data
  })
}

// 删除培训知识
export function deleteTrainingKnowledge(id) {
  return request({
    url: '/fish/training-knowledge/delete?id=' + id,
    method: 'delete'
  })
}

// 获得培训知识
export function getTrainingKnowledge(id) {
  return request({
    url: '/fish/training-knowledge/get?id=' + id,
    method: 'get'
  })
}

// 获得培训知识分页
export function getTrainingKnowledgePage(query) {
  return request({
    url: '/fish/training-knowledge/page',
    method: 'get',
    params: query
  })
}

// 导出培训知识 Excel
export function exportTrainingKnowledgeExcel(query) {
  return request({
    url: '/fish/training-knowledge/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
