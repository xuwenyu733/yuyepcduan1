import request from '@/utils/request'

// 创建鱼苗种类
export function createZkPondFishType(data) {
  return request({
    url: '/fish/zk-pond-fish-type/create',
    method: 'post',
    data: data
  })
}

// 更新鱼苗种类
export function updateZkPondFishType(data) {
  return request({
    url: '/fish/zk-pond-fish-type/update',
    method: 'put',
    data: data
  })
}

// 删除鱼苗种类
export function deleteZkPondFishType(id) {
  return request({
    url: '/fish/zk-pond-fish-type/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除鱼苗种类 */
export function deleteZkPondFishTypeList(ids) {
  return request({
    url: `/fish/zk-pond-fish-type/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得鱼苗种类
export function getZkPondFishType(id) {
  return request({
    url: '/fish/zk-pond-fish-type/get?id=' + id,
    method: 'get'
  })
}

// 获得鱼苗种类分页
export function getZkPondFishTypePage(params) {
  return request({
    url: '/fish/zk-pond-fish-type/page',
    method: 'get',
    params
  })
}
// 导出鱼苗种类 Excel
export function exportZkPondFishTypeExcel(params) {
  return request({
    url: '/fish/zk-pond-fish-type/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}