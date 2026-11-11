import request from '@/utils/request_jidi'

// 创建会议记录
export function createZkMeetingRecord(data) {
  return request({
    url: '/fish/zk-meeting-record/create',
    method: 'post',
    data: data
  })
}

// 更新会议记录
export function updateZkMeetingRecord(data) {
  return request({
    url: '/fish/zk-meeting-record/update',
    method: 'put',
    data: data
  })
}

// 删除会议记录
export function deleteZkMeetingRecord(id) {
  return request({
    url: '/fish/zk-meeting-record/delete?id=' + id,
    method: 'delete'
  })
}

// 获得会议记录
export function getZkMeetingRecord(id) {
  return request({
    url: '/fish/zk-meeting-record/get?id=' + id,
    method: 'get'
  })
}

// 获得会议记录分页
export function getZkMeetingRecordPage(query) {
  return request({
    url: '/fish/zk-meeting-record/page',
    method: 'get',
    params: query
  })
}

// 导出会议记录 Excel
export function exportZkMeetingRecordExcel(query) {
  return request({
    url: '/fish/zk-meeting-record/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
