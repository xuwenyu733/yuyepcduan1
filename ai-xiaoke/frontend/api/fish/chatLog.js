import request from '@/utils/request_jidi'

// 创建聊天日志
export function createChatLog(data) {
  return request({
    url: '/fish/chat-log/create',
    method: 'post',
    data: data
  })
}

// 更新聊天日志
export function updateChatLog(data) {
  return request({
    url: '/fish/chat-log/update',
    method: 'put',
    data: data
  })
}

// 删除聊天日志
export function deleteChatLog(id) {
  return request({
    url: '/fish/chat-log/delete?id=' + id,
    method: 'delete'
  })
}

// 获得聊天日志
export function getChatLog(id) {
  return request({
    url: '/fish/chat-log/get?id=' + id,
    method: 'get'
  })
}

// 获得聊天日志分页
export function getChatLogPage(query) {
  return request({
    url: '/fish/chat-log/page',
    method: 'get',
    params: query
  })
}

// 导出聊天日志 Excel
export function exportChatLogExcel(query) {
  return request({
    url: '/fish/chat-log/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
