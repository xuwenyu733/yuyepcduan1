import request from '@/utils/request'

// 创建聊天消息接受
export function createZkChatMsgReceive(data) {
  return request({
    url: '/fish/zk-chat-msg-receive/create',
    method: 'post',
    data: data
  })
}

// 更新聊天消息接受
export function updateZkChatMsgReceive(data) {
  return request({
    url: '/fish/zk-chat-msg-receive/update',
    method: 'put',
    data: data
  })
}

// 删除聊天消息接受
export function deleteZkChatMsgReceive(id) {
  return request({
    url: '/fish/zk-chat-msg-receive/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除聊天消息接受 */
export function deleteZkChatMsgReceiveList(ids) {
  return request({
    url: `/fish/zk-chat-msg-receive/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得聊天消息接受
export function getZkChatMsgReceive(id) {
  return request({
    url: '/fish/zk-chat-msg-receive/get?id=' + id,
    method: 'get'
  })
}

// 获得聊天消息接受分页
export function getZkChatMsgReceivePage(params) {
  return request({
    url: '/fish/zk-chat-msg-receive/page',
    method: 'get',
    params
  })
}
// 导出聊天消息接受 Excel
export function exportZkChatMsgReceiveExcel(params) {
  return request({
    url: '/fish/zk-chat-msg-receive/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}