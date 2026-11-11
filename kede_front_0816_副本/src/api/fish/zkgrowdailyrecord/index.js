import request from '@/utils/request'

// 创建每日生长与环境记录
export function createZkGrowDailyRecord(data) {
  return request({
    url: '/fish/zk-grow-daily-record/create',
    method: 'post',
    data: data
  })
}

// 更新每日生长与环境记录
export function updateZkGrowDailyRecord(data) {
  return request({
    url: '/fish/zk-grow-daily-record/update',
    method: 'put',
    data: data
  })
}

// 删除每日生长与环境记录
export function deleteZkGrowDailyRecord(id) {
  return request({
    url: '/fish/zk-grow-daily-record/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除每日生长与环境记录 */
export function deleteZkGrowDailyRecordList(ids) {
  return request({
    url: `/fish/zk-grow-daily-record/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得每日生长与环境记录
export function getZkGrowDailyRecord(id) {
  return request({
    url: '/fish/zk-grow-daily-record/get?id=' + id,
    method: 'get'
  })
}

// 获得每日生长与环境记录分页
export function getZkGrowDailyRecordPage(params) {
  return request({
    url: '/fish/zk-grow-daily-record/page',
    method: 'get',
    params
  })
}

// 获得生长模型概览数据 (对应前端红框模块)
export function getZkGrowDailyRecordSummary(params) {
  return request({
    url: '/fish/zk-grow-daily-record/get-summary',
    method: 'get',
    params
  })
}
// 导出每日生长与环境记录 Excel
export function exportZkGrowDailyRecordExcel(params) {
  return request({
    url: '/fish/zk-grow-daily-record/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 预测鱼类生长数据
export function predictZkGrowDailyRecord(params) {
  return request({
    url: '/fish/zk-grow-daily-record/predict',
    method: 'get',
    params
  })
}

// 获得生长大模型看板数据
export function getDashboardData(params, config = {}) {
  return request({
    url: '/fish/zk-grow-daily-record/dashboard',
    method: 'get',
    params,
    timeout: 90000,
    ...config
  })
}

// 获得养殖批次摘要列表
export function getBatchSummaryList() {
  return request({
    url: '/fish/zk-grow-daily-record/batch-summary-list',
    method: 'get'
  })
}

// 获得养殖鱼池摘要列表
export function getPondSummaryList() {
  return request({
    url: '/fish/zk-grow-daily-record/pond-summary-list',
    method: 'get'
  })
}