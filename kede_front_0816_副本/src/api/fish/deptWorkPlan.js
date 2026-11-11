import request from '@/utils/request_jidi'

// 创建部门工作计划
export function createDeptWorkPlan (data) {
  return request({
    url: '/fish/dept-work-plan/create',
    method: 'post',
    data: data
  })
}

// 更新部门工作计划
export function updateDeptWorkPlan (data) {
  return request({
    url: '/fish/dept-work-plan/update',
    method: 'put',
    data: data
  })
}

// 删除部门工作计划
export function deleteDeptWorkPlan (id) {
  return request({
    url: '/fish/dept-work-plan/delete?id=' + id,
    method: 'delete'
  })
}

// 获得部门工作计划
export function getDeptWorkPlan (id) {
  return request({
    url: '/fish/dept-work-plan/get?id=' + id,
    method: 'get'
  })
}

// 获得部门工作计划列表
export function getDeptWorkPlanList (ids) {
  return request({
    url: '/fish/dept-work-plan/list?ids=' + ids.join(','),
    method: 'get'
  })
}

// 获得部门工作计划分页
export function getDeptWorkPlanPage (params) {
  return request({
    url: '/fish/dept-work-plan/page',
    method: 'get',
    params: params
  })
}

