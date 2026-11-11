import request from '@/utils/request_jidi'

// 创建鱼池健康
export function createZkPondHealth(data) {
  return request({
    url: '/fish/zk-pond-health/create',
    method: 'post',
    data: data
  })
}

// 更新鱼池健康
export function updateZkPondHealth(data) {
  return request({
    url: '/fish/zk-pond-health/update',
    method: 'put',
    data: data
  })
}

// 删除鱼池健康
export function deleteZkPondHealth(id) {
  return request({
    url: '/fish/zk-pond-health/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除鱼池健康 */
export function deleteZkPondHealthList(ids) {
  return request({
    url: `/fish/zk-pond-health/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得鱼池健康
export function getZkPondHealth(id) {
  return request({
    url: '/fish/zk-pond-health/get?id=' + id,
    method: 'get'
  })
}

// 获得鱼池健康分页
export function getZkPondHealthPage(params) {
  return request({
    url: '/fish/zk-pond-health/page',
    method: 'get',
    params
  })
}
// 导出鱼池健康 Excel
export function exportZkPondHealthExcel(params) {
  return request({
    url: '/fish/zk-pond-health/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}