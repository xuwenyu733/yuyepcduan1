import request from '@/utils/request'

// 创建聊天消息
export function createZkChatMsg(data) {
  return request({
    url: '/fish/zk-chat-msg/create',
    method: 'post',
    data: data
  })
}

// 更新聊天消息
export function updateZkChatMsg(data) {
  return request({
    url: '/fish/zk-chat-msg/update',
    method: 'put',
    data: data
  })
}

// 删除聊天消息
export function deleteZkChatMsg(id) {
  return request({
    url: '/fish/zk-chat-msg/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除聊天消息 */
export function deleteZkChatMsgList(ids) {
  return request({
    url: `/fish/zk-chat-msg/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得聊天消息
export function getZkChatMsg(id) {
  return request({
    url: '/fish/zk-chat-msg/get?id=' + id,
    method: 'get'
  })
}

// 获得聊天消息分页
export function getZkChatMsgPage(params) {
  return request({
    url: '/fish/zk-chat-msg/page',
    method: 'get',
    params
  })
}
// 导出聊天消息 Excel
export function exportZkChatMsgExcel(params) {
  return request({
    url: '/fish/zk-chat-msg/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}