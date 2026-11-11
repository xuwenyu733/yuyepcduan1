import request from '@/utils/request_jidi'

// 创建生物资产评估
export function createZkBiologicalAsset(data) {
  return request({
    url: '/fish/zk-biological-asset/create',
    method: 'post',
    data: data
  })
}

// 更新生物资产评估
export function updateZkBiologicalAsset(data) {
  return request({
    url: '/fish/zk-biological-asset/update',
    method: 'put',
    data: data
  })
}

// 删除生物资产评估
export function deleteZkBiologicalAsset(id) {
  return request({
    url: '/fish/zk-biological-asset/delete?id=' + id,
    method: 'delete'
  })
}

// 获得生物资产评估
export function getZkBiologicalAsset(id) {
  return request({
    url: '/fish/zk-biological-asset/get?id=' + id,
    method: 'get'
  })
}

// 获得生物资产评估分页
export function getZkBiologicalAssetPage(query) {
  return request({
    url: '/fish/zk-biological-asset/page',
    method: 'get',
    params: query
  })
}

// 导出生物资产评估 Excel
export function exportZkBiologicalAssetExcel(query) {
  return request({
    url: '/fish/zk-biological-asset/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 获得生物资产评估仪表盘数据（可按统计周期 / 自定义时间回溯日快照）
export function getZkBiologicalAssetDashboard(params) {
  return request({
    url: '/fish/zk-biological-asset/dashboard',
    method: 'get',
    params: params || {}
  })
}
