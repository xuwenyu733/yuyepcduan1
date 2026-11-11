import request from '@/utils/request_jidi'

// 创建会议
export function createZkMeeting(data) {
  return request({
    url: '/fish/zk-meeting/create',
    method: 'post',
    data: data
  })
}

// 接受会议消息
export function receiveMsg(data) {
  return request({
    url: '/fish/zk-meeting/receiveMsg',
    method: 'post',
    data: data,
    silent: true,
    hideErrorMessage: true
  })
}

// 获取会议纪要及待办（AI 未启动时会失败，由页面自行提示，勿弹全局「系统异常」）
export function summaryPoint(data) {
  return request({
    url: '/fish/zk-meeting/summaryPoint',
    method: 'post',
    data: data,
    silent: true,
    hideErrorMessage: true
  })
}

// 更新会议
export function updateZkMeeting(data) {
  return request({
    url: '/fish/zk-meeting/update',
    method: 'put',
    data: data
  })
}

// 删除会议
export function deleteZkMeeting(id) {
  return request({
    url: '/fish/zk-meeting/delete?id=' + id,
    method: 'delete'
  })
}

// 获得会议
export function getZkMeeting(id) {
  return request({
    url: '/fish/zk-meeting/get?id=' + id,
    method: 'get'
  })
}

// 获得会议分页
export function getZkMeetingPage(query) {
  return request({
    url: '/fish/zk-meeting/page',
    method: 'get',
    params: query
  })
}

// 导出会议 Excel
export function exportZkMeetingExcel(query) {
  return request({
    url: '/fish/zk-meeting/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
