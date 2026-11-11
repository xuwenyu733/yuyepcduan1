import request from '@/utils/request'

// 创建鱼类品种
export function createZkGrowFishSpecies(data) {
  return request({
    url: '/fish/zk-grow-fish-species/create',
    method: 'post',
    data: data
  })
}

// 更新鱼类品种
export function updateZkGrowFishSpecies(data) {
  return request({
    url: '/fish/zk-grow-fish-species/update',
    method: 'put',
    data: data
  })
}

// 删除鱼类品种
export function deleteZkGrowFishSpecies(id) {
  return request({
    url: '/fish/zk-grow-fish-species/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除鱼类品种 */
export function deleteZkGrowFishSpeciesList(ids) {
  return request({
    url: `/fish/zk-grow-fish-species/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得鱼类品种
export function getZkGrowFishSpecies(id) {
  return request({
    url: '/fish/zk-grow-fish-species/get?id=' + id,
    method: 'get'
  })
}

// 获得鱼类品种分页
export function getZkGrowFishSpeciesPage(params) {
  return request({
    url: '/fish/zk-grow-fish-species/page',
    method: 'get',
    params
  })
}
// 导出鱼类品种 Excel
export function exportZkGrowFishSpeciesExcel(params) {
  return request({
    url: '/fish/zk-grow-fish-species/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}