import request from '@/utils/request_jidi'

// 创建鱼苗统计
export function createZkFishOperationLog(data) {
  return request({
    url: '/fish/zk-fish-operation-log/create',
    method: 'post',
    data: data
  })
}

// 更新鱼苗统计
export function updateZkFishOperationLog(data) {
  return request({
    url: '/fish/zk-fish-operation-log/update',
    method: 'put',
    data: data
  })
}

// 删除鱼苗统计
export function deleteZkFishOperationLog(id) {
  return request({
    url: '/fish/zk-fish-operation-log/delete?id=' + id,
    method: 'delete'
  })
}

// 获得鱼苗统计
export function getZkFishOperationLog(id) {
  return request({
    url: '/fish/zk-fish-operation-log/get?id=' + id,
    method: 'get'
  })
}

// 获得鱼苗统计分页
export function getZkFishOperationLogPage(query, config = {}) {
  return request({
    url: '/fish/zk-fish-operation-log/page',
    method: 'get',
    params: query,
    ...config
  })
}

// 导出鱼苗统计 Excel
export function exportZkFishOperationLogExcel(query) {
  return request({
    url: '/fish/zk-fish-operation-log/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
