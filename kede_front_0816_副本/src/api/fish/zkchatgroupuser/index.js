import request from '@/utils/request'

// 创建群聊成员
export function createZkChatGroupUser(data) {
  return request({
    url: '/fish/zk-chat-group-user/create',
    method: 'post',
    data: data
  })
}

// 更新群聊成员
export function updateZkChatGroupUser(data) {
  return request({
    url: '/fish/zk-chat-group-user/update',
    method: 'put',
    data: data
  })
}

// 删除群聊成员
export function deleteZkChatGroupUser(id) {
  return request({
    url: '/fish/zk-chat-group-user/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除群聊成员 */
export function deleteZkChatGroupUserList(ids) {
  return request({
    url: `/fish/zk-chat-group-user/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得群聊成员
export function getZkChatGroupUser(id) {
  return request({
    url: '/fish/zk-chat-group-user/get?id=' + id,
    method: 'get'
  })
}

// 获得群聊成员分页
export function getZkChatGroupUserPage(params) {
  return request({
    url: '/fish/zk-chat-group-user/page',
    method: 'get',
    params
  })
}
// 导出群聊成员 Excel
export function exportZkChatGroupUserExcel(params) {
  return request({
    url: '/fish/zk-chat-group-user/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}