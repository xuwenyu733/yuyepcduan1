import request from '@/utils/request'

// 创建饵料营养成分
export function createZkGrowBaitNutrition(data) {
  return request({
    url: '/fish/zk-grow-bait-nutrition/create',
    method: 'post',
    data: data
  })
}

// 更新饵料营养成分
export function updateZkGrowBaitNutrition(data) {
  return request({
    url: '/fish/zk-grow-bait-nutrition/update',
    method: 'put',
    data: data
  })
}

// 删除饵料营养成分
export function deleteZkGrowBaitNutrition(id) {
  return request({
    url: '/fish/zk-grow-bait-nutrition/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除饵料营养成分 */
export function deleteZkGrowBaitNutritionList(ids) {
  return request({
    url: `/fish/zk-grow-bait-nutrition/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得饵料营养成分
export function getZkGrowBaitNutrition(id) {
  return request({
    url: '/fish/zk-grow-bait-nutrition/get?id=' + id,
    method: 'get'
  })
}

// 获得饵料营养成分分页
export function getZkGrowBaitNutritionPage(params) {
  return request({
    url: '/fish/zk-grow-bait-nutrition/page',
    method: 'get',
    params
  })
}
// 导出饵料营养成分 Excel
export function exportZkGrowBaitNutritionExcel(params) {
  return request({
    url: '/fish/zk-grow-bait-nutrition/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}