import request from '@/utils/request'

// 创建饵料成分对比
export function createZkGrowBaitIngredients(data) {
  return request({
    url: '/fish/zk-grow-bait-ingredients/create',
    method: 'post',
    data: data
  })
}

// 更新饵料成分对比
export function updateZkGrowBaitIngredients(data) {
  return request({
    url: '/fish/zk-grow-bait-ingredients/update',
    method: 'put',
    data: data
  })
}

// 删除饵料成分对比
export function deleteZkGrowBaitIngredients(id) {
  return request({
    url: '/fish/zk-grow-bait-ingredients/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除饵料成分对比 */
export function deleteZkGrowBaitIngredientsList(ids) {
  return request({
    url: `/fish/zk-grow-bait-ingredients/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得饵料成分对比
export function getZkGrowBaitIngredients(id) {
  return request({
    url: '/fish/zk-grow-bait-ingredients/get?id=' + id,
    method: 'get'
  })
}

// 获得饵料成分对比分页
export function getZkGrowBaitIngredientsPage(params) {
  return request({
    url: '/fish/zk-grow-bait-ingredients/page',
    method: 'get',
    params
  })
}
// 导出饵料成分对比 Excel
export function exportZkGrowBaitIngredientsExcel(params) {
  return request({
    url: '/fish/zk-grow-bait-ingredients/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}