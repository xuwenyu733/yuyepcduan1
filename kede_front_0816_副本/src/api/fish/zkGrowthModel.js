import request from '@/utils/request_jidi'

// 创建生长模型
export function createZkGrowthModel(data) {
  return request({
    url: '/fish/zk-growth-model/create',
    method: 'post',
    data: data
  })
}

// 更新生长模型
export function updateZkGrowthModel(data) {
  return request({
    url: '/fish/zk-growth-model/update',
    method: 'put',
    data: data
  })
}

// 删除生长模型
export function deleteZkGrowthModel(id) {
  return request({
    url: '/fish/zk-growth-model/delete?id=' + id,
    method: 'delete'
  })
}

// 获得生长模型
export function getZkGrowthModel(id) {
  return request({
    url: '/fish/zk-growth-model/get?id=' + id,
    method: 'get'
  })
}

// 获得生长模型分页
export function getZkGrowthModelPage(query) {
  return request({
    url: '/fish/zk-growth-model/page',
    method: 'get',
    params: query
  })
}

// 导出生长模型 Excel
export function exportZkGrowthModelExcel(query) {
  return request({
    url: '/fish/zk-growth-model/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
