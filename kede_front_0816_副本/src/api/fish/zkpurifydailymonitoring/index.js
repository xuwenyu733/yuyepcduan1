import request from '@/utils/request'

// 创建日常监测记录
export function createPurifyDailyMonitoring(data) {
  return request({
    url: '/fish/purify-daily-monitoring/create',
    method: 'post',
    data: data
  })
}

// 更新日常监测记录
export function updatePurifyDailyMonitoring(data) {
  return request({
    url: '/fish/purify-daily-monitoring/update',
    method: 'put',
    data: data
  })
}

// 删除日常监测记录
export function deletePurifyDailyMonitoring(id) {
  return request({
    url: '/fish/purify-daily-monitoring/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除日常监测记录 */
export function deletePurifyDailyMonitoringList(ids) {
  return request({
    url: `/fish/purify-daily-monitoring/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得日常监测记录
export function getPurifyDailyMonitoring(id) {
  return request({
    url: '/fish/purify-daily-monitoring/get?id=' + id,
    method: 'get'
  })
}

// 获得日常监测记录分页
export function getPurifyDailyMonitoringPage(params) {
  return request({
    url: '/fish/purify-daily-monitoring/page',
    method: 'get',
    params
  })
}
// 导出日常监测记录 Excel
export function exportPurifyDailyMonitoringExcel(params) {
  return request({
    url: '/fish/purify-daily-monitoring/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}