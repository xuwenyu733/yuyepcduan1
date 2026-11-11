import request from '@/utils/request_jidi'

// 创建时间字典
export function createZkDayRecord(data) {
  return request({
    url: '/fish/zk-day-record/create',
    method: 'post',
    data: data
  })
}

// 更新时间字典
export function updateZkDayRecord(data) {
  return request({
    url: '/fish/zk-day-record/update',
    method: 'put',
    data: data
  })
}

// 删除时间字典
export function deleteZkDayRecord(id) {
  return request({
    url: '/fish/zk-day-record/delete?id=' + id,
    method: 'delete'
  })
}

// 获得时间字典
export function getZkDayRecord(id) {
  return request({
    url: '/fish/zk-day-record/get?id=' + id,
    method: 'get'
  })
}

// 获得时间字典分页
export function getZkDayRecordPage(query) {
  return request({
    url: '/fish/zk-day-record/page',
    method: 'get',
    params: query
  })
}

// 导出时间字典 Excel
export function exportZkDayRecordExcel(query) {
  return request({
    url: '/fish/zk-day-record/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
