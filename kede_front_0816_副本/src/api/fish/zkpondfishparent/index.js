import request from '@/utils/request_jidi'

// 创建鱼池操作父
export function createZkPondFishParent(data) {
  return request({
    url: '/fish/zk-pond-fish-parent/create',
    method: 'post',
    data: data
  })
}

// 更新鱼池操作父
export function updateZkPondFishParent(data) {
  return request({
    url: '/fish/zk-pond-fish-parent/update',
    method: 'put',
    data: data
  })
}

// 删除鱼池操作父
export function deleteZkPondFishParent(id) {
  return request({
    url: '/fish/zk-pond-fish-parent/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除鱼池操作父 */
export function deleteZkPondFishParentList(ids) {
  return request({
    url: `/fish/zk-pond-fish-parent/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得鱼池操作父
export function getZkPondFishParent(id) {
  return request({
    url: '/fish/zk-pond-fish-parent/get?id=' + id,
    method: 'get'
  })
}

// 获得鱼池操作父分页
export function getZkPondFishParentPage(params) {
  return request({
    url: '/fish/zk-pond-fish-parent/page',
    method: 'get',
    params
  })
}
// 导出鱼池操作父 Excel
export function exportZkPondFishParentExcel(params) {
  return request({
    url: '/fish/zk-pond-fish-parent/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}