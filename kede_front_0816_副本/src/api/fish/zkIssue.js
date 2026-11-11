import request from '@/utils/request_jidi'

// 创建问题总结
export function createZkIssue(data) {
  return request({
    url: '/fish/zk-issue/create',
    method: 'post',
    data: data
  })
}

// 更新问题总结
export function updateZkIssue(data) {
  return request({
    url: '/fish/zk-issue/update',
    method: 'put',
    data: data
  })
}

// 删除问题总结
export function deleteZkIssue(id) {
  return request({
    url: '/fish/zk-issue/delete?id=' + id,
    method: 'delete'
  })
}

// 获得问题总结
export function getZkIssue(id) {
  return request({
    url: '/fish/zk-issue/get?id=' + id,
    method: 'get'
  })
}

// 获得问题总结分页
export function getZkIssuePage(query) {
  return request({
    url: '/fish/zk-issue/page',
    method: 'get',
    params: query
  })
}

// 导出问题总结 Excel
export function exportZkIssueExcel(query) {
  return request({
    url: '/fish/zk-issue/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
