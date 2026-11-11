import request from '@/utils/request_jidi'

// 创建试卷
export function createZkPaper(data) {
  return request({
    url: '/fish/zk-paper/create',
    method: 'post',
    data: data
  })
}

// 更新试卷
export function updateZkPaper(data) {
  return request({
    url: '/fish/zk-paper/update',
    method: 'put',
    data: data
  })
}

// 删除试卷
export function deleteZkPaper(id) {
  return request({
    url: '/fish/zk-paper/delete?id=' + id,
    method: 'delete'
  })
}

// 获得试卷
export function getZkPaper(id) {
  return request({
    url: '/fish/zk-paper/get?id=' + id,
    method: 'get'
  })
}

// 获得试卷分页
export function getZkPaperPage(query) {
  return request({
    url: '/fish/zk-paper/page',
    method: 'get',
    params: query
  })
}
export function listForNeedExam(query) {
  return request({
    url: '/fish/zk-paper/listForNeedExam',
    method: 'get',
    params: query
  })
}

// 导出试卷 Excel
export function exportZkPaperExcel(query) {
  return request({
    url: '/fish/zk-paper/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
