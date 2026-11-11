import request from '@/utils/request'

// 创建群聊组
export function createZkChatGroup(data) {
  return request({
    url: '/fish/zk-chat-group/create',
    method: 'post',
    data: data
  })
}

// 更新群聊组
export function updateZkChatGroup(data) {
  return request({
    url: '/fish/zk-chat-group/update',
    method: 'put',
    data: data
  })
}

// 删除群聊组
export function deleteZkChatGroup(id) {
  return request({
    url: '/fish/zk-chat-group/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除群聊组 */
export function deleteZkChatGroupList(ids) {
  return request({
    url: `/fish/zk-chat-group/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得群聊组
export function getZkChatGroup(id) {
  return request({
    url: '/fish/zk-chat-group/get?id=' + id,
    method: 'get'
  })
}

// 获得群聊组分页  
export function getZkChatGroupPage(params) {
  return request({
    url: '/fish/zk-chat-group/page',
    method: 'get',
    params
  })
}


export function page4manager(params) {
  return request({
    url: '/fish/zk-chat-group/page4manager',
    method: 'get',
    params
  })
}

// 导出群聊组 Excel
export function exportZkChatGroupExcel(params) {
  return request({
    url: '/fish/zk-chat-group/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}