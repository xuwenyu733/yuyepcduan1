import request from '@/utils/request'

// 创建生长模型配置
export function createZkGrowModelConfig(data) {
  return request({
    url: '/fish/zk-grow-model-config/create',
    method: 'post',
    data: data
  })
}

// 更新生长模型配置
export function updateZkGrowModelConfig(data) {
  return request({
    url: '/fish/zk-grow-model-config/update',
    method: 'put',
    data: data
  })
}

// 删除生长模型配置
export function deleteZkGrowModelConfig(id) {
  return request({
    url: '/fish/zk-grow-model-config/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除生长模型配置 */
export function deleteZkGrowModelConfigList(ids) {
  return request({
    url: `/fish/zk-grow-model-config/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得生长模型配置
export function getZkGrowModelConfig(id) {
  return request({
    url: '/fish/zk-grow-model-config/get?id=' + id,
    method: 'get'
  })
}

// 获得生长模型配置分页
export function getZkGrowModelConfigPage(params) {
  return request({
    url: '/fish/zk-grow-model-config/page',
    method: 'get',
    params
  })
}
// 导出生长模型配置 Excel
export function exportZkGrowModelConfigExcel(params) {
  return request({
    url: '/fish/zk-grow-model-config/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}