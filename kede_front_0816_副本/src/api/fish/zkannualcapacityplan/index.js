import request from '@/utils/request'

// 创建年度产能计划
export function createZkAnnualCapacityPlan(data) {
  return request({
    url: '/fish/zk-annual-capacity-plan/create',
    method: 'post',
    data: data
  })
}

// 更新年度产能计划
export function updateZkAnnualCapacityPlan(data) {
  return request({
    url: '/fish/zk-annual-capacity-plan/update',
    method: 'put',
    data: data
  })
}

// 删除年度产能计划
export function deleteZkAnnualCapacityPlan(id) {
  return request({
    url: '/fish/zk-annual-capacity-plan/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除年度产能计划 */
export function deleteZkAnnualCapacityPlanList(ids) {
  return request({
    url: `/fish/zk-annual-capacity-plan/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得年度产能计划
export function getZkAnnualCapacityPlan(id) {
  return request({
    url: '/fish/zk-annual-capacity-plan/get?id=' + id,
    method: 'get'
  })
}

// 获得年度产能计划分页
export function getZkAnnualCapacityPlanPage(params) {
  return request({
    url: '/fish/zk-annual-capacity-plan/page',
    method: 'get',
    params
  })
}
// 导出年度产能计划 Excel
export function exportZkAnnualCapacityPlanExcel(params) {
  return request({
    url: '/fish/zk-annual-capacity-plan/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}