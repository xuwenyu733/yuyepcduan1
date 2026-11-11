import request from '@/utils/request'

// 创建养殖品种
export function createZkBreedSpecies(data) {
  return request({
    url: '/fish/zk-breed-species/create',
    method: 'post',
    data: data
  })
}

// 更新养殖品种
export function updateZkBreedSpecies(data) {
  return request({
    url: '/fish/zk-breed-species/update',
    method: 'put',
    data: data
  })
}

// 删除养殖品种
export function deleteZkBreedSpecies(id) {
  return request({
    url: '/fish/zk-breed-species/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除养殖品种 */
export function deleteZkBreedSpeciesList(ids) {
  return request({
    url: `/fish/zk-breed-species/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得养殖品种
export function getZkBreedSpecies(id) {
  return request({
    url: '/fish/zk-breed-species/get?id=' + id,
    method: 'get'
  })
}

// 获得养殖品种分页
export function getZkBreedSpeciesPage(params) {
  return request({
    url: '/fish/zk-breed-species/page',
    method: 'get',
    params
  })
}
// 导出养殖品种 Excel
export function exportZkBreedSpeciesExcel(params) {
  return request({
    url: '/fish/zk-breed-species/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}