import request from '@/utils/request_jidi'

// 创建OA工作计划
export function createOaWorkPlan (data) {
  return request({
    url: '/fish/oa-work-plan/create',
    method: 'post',
    data: data
  })
}

// 更新OA工作计划
export function updateOaWorkPlan (data) {
  return request({
    url: '/fish/oa-work-plan/update',
    method: 'put',
    data: data
  })
}

// 删除OA工作计划
export function deleteOaWorkPlan (id) {
  return request({
    url: '/fish/oa-work-plan/delete?id=' + id,
    method: 'delete'
  })
}

// 获得OA工作计划
export function getOaWorkPlan (id) {
  return request({
    url: '/fish/oa-work-plan/get?id=' + id,
    method: 'get'
  })
}

// 获得OA工作计划分页
export function getOaWorkPlanPage (query) {
  return request({
    url: '/fish/oa-work-plan/page',
    method: 'get',
    params: query
  })
}

// 审核OA工作计划
export function reviewOaWorkPlan (data) {
  return request({
    url: '/fish/oa-work-plan/review',
    method: 'post',
    data: data
  })
}

// 获得OA工作计划统计
export function getOaWorkPlanStatistics () {
  return request({
    url: '/fish/oa-work-plan/statistics',
    method: 'get'
  })
}

// 更新工作计划完成情况
export function completeOaWorkPlan (data) {
  return request({
    url: '/fish/oa-work-plan/complete',
    method: 'post',
    data: data
  })
}

// 评价工作计划
export function evaluateOaWorkPlan (data) {
  return request({
    url: '/fish/oa-work-plan/evaluate',
    method: 'post',
    data: data
  })
}

// 导出OA工作计划 Excel
export function exportOaWorkPlanExcel (query) {
  return request({
    url: '/fish/oa-work-plan/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 创建工作目标与计划
export function createZkWorkPlan(data) {
  return request({
    url: '/fish/zk-work-plan/create',
    method: 'post',
    data: data
  })
}

// 更新工作目标与计划
export function updateZkWorkPlan(data) {
  return request({
    url: '/fish/zk-work-plan/update',
    method: 'put',
    data: data
  })
}

// 删除工作目标与计划
export function deleteZkWorkPlan(id) {
  return request({
    url: '/fish/zk-work-plan/delete?id=' + id,
    method: 'delete'
  })
}

// 获得工作目标与计划
export function getZkWorkPlan(id) {
  return request({
    url: '/fish/zk-work-plan/get?id=' + id,
    method: 'get'
  })
}

// 获得工作目标与计划分页
export function getZkWorkPlanPage(query) {
  return request({
    url: '/fish/zk-work-plan/page',
    method: 'get',
    params: query
  })
}

// 导出工作目标与计划 Excel
export function exportZkWorkPlanExcel(query) {
  return request({
    url: '/fish/zk-work-plan/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
