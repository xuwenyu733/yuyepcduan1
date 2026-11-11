import request from '@/utils/request'

// 创建鱼池管理
export function createPurifyPool(data) {
  return request({
    url: '/fish/purify-pool/create',
    method: 'post',
    data: data
  })
}

// 更新鱼池管理
export function updatePurifyPool(data) {
  return request({
    url: '/fish/purify-pool/update',
    method: 'put',
    data: data
  })
}

// 删除鱼池管理
export function deletePurifyPool(id) {
  return request({
    url: '/fish/purify-pool/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除鱼池管理 */
export function deletePurifyPoolList(ids) {
  return request({
    url: `/fish/purify-pool/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得鱼池管理
export function getPurifyPool(id) {
  return request({
    url: '/fish/purify-pool/get?id=' + id,
    method: 'get'
  })
}

// 获得鱼池管理分页
export function getPurifyPoolPage(params) {
  return request({
    url: '/fish/purify-pool/page',
    method: 'get',
    params
  })
}
// 导出鱼池管理 Excel
export function exportPurifyPoolExcel(params) {
  return request({
    url: '/fish/purify-pool/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}