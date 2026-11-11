import request from '@/utils/request_jidi'

// 创建各阶段营养需求
export function createZkGrowNutritionalRequirements(data) {
  return request({
    url: '/fish/zk-grow-nutritional-requirements/create',
    method: 'post',
    data: data
  })
}

// 更新各阶段营养需求
export function updateZkGrowNutritionalRequirements(data) {
  return request({
    url: '/fish/zk-grow-nutritional-requirements/update',
    method: 'put',
    data: data
  })
}

// 删除各阶段营养需求
export function deleteZkGrowNutritionalRequirements(id) {
  return request({
    url: '/fish/zk-grow-nutritional-requirements/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除各阶段营养需求 */
export function deleteZkGrowNutritionalRequirementsList(ids) {
  return request({
    url: `/fish/zk-grow-nutritional-requirements/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得各阶段营养需求
export function getZkGrowNutritionalRequirements(id) {
  return request({
    url: '/fish/zk-grow-nutritional-requirements/get?id=' + id,
    method: 'get'
  })
}

// 获得各阶段营养需求分页
export function getZkGrowNutritionalRequirementsPage(params) {
  return request({
    url: '/fish/zk-grow-nutritional-requirements/page',
    method: 'get',
    params
  })
}
// 导出各阶段营养需求 Excel
export function exportZkGrowNutritionalRequirementsExcel(params) {
  return request({
    url: '/fish/zk-grow-nutritional-requirements/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}