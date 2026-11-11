import request from '@/utils/request_jidi'

// 创建会议用户
export function createZkMeetingUser(data) {
  return request({
    url: '/fish/zk-meeting-user/create',
    method: 'post',
    data: data
  })
}

// 更新会议用户
export function updateZkMeetingUser(data) {
  return request({
    url: '/fish/zk-meeting-user/update',
    method: 'put',
    data: data
  })
}

// 删除会议用户
export function deleteZkMeetingUser(id) {
  return request({
    url: '/fish/zk-meeting-user/delete?id=' + id,
    method: 'delete'
  })
}

// 获得会议用户
export function getZkMeetingUser(id) {
  return request({
    url: '/fish/zk-meeting-user/get?id=' + id,
    method: 'get'
  })
}

// 获得会议用户分页
export function getZkMeetingUserPage(query) {
  return request({
    url: '/fish/zk-meeting-user/page',
    method: 'get',
    params: query
  })
}

// 导出会议用户 Excel
export function exportZkMeetingUserExcel(query) {
  return request({
    url: '/fish/zk-meeting-user/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
