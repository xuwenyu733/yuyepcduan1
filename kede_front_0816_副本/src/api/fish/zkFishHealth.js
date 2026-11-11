import request from '@/utils/request_jidi'

// 创建鱼类健康评估
export function createZkFishHealth(data) {
  return request({
    url: '/fish/zk-fish-health/create',
    method: 'post',
    data: data
  })
}

// 更新鱼类健康评估
export function updateZkFishHealth(data) {
  return request({
    url: '/fish/zk-fish-health/update',
    method: 'put',
    data: data
  })
}

// 删除鱼类健康评估
export function deleteZkFishHealth(id) {
  return request({
    url: '/fish/zk-fish-health/delete?id=' + id,
    method: 'delete'
  })
}

// 获得鱼类健康评估
export function getZkFishHealth(id) {
  return request({
    url: '/fish/zk-fish-health/get?id=' + id,
    method: 'get'
  })
}

// 获得鱼类健康评估分页
export function getZkFishHealthPage(query) {
  return request({
    url: '/fish/zk-fish-health/page',
    method: 'get',
    params: query
  })
}

// 导出鱼类健康评估 Excel
export function exportZkFishHealthExcel(query) {
  return request({
    url: '/fish/zk-fish-health/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
