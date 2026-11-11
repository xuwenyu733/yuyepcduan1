import request from '@/utils/request'

// 创建工作绩效评分
export function createOaWorkPerformance(data) {
  return request({
    url: '/fish/oa-work-performance/create',
    method: 'post',
    data
  })
}

// 更新工作绩效评分
export function updateOaWorkPerformance(data) {
  return request({
    url: '/fish/oa-work-performance/update',
    method: 'put',
    data
  })
}

// 删除工作绩效评分
export function deleteOaWorkPerformance(id) {
  return request({
    url: '/fish/oa-work-performance/delete',
    method: 'delete',
    params: { id }
  })
}

// 获取工作绩效评分详情
export function getOaWorkPerformance(id) {
  return request({
    url: '/fish/oa-work-performance/get',
    method: 'get',
    params: { id }
  })
}

// 获取工作绩效评分分页
export function getOaWorkPerformancePage(params) {
  return request({
    url: '/fish/oa-work-performance/page',
    method: 'get',
    params
  })
}

// 获取可评分人员列表
export function getRateableUsers() {
  return request({
    url: '/fish/oa-work-performance/rateable-users',
    method: 'get'
  })
}



