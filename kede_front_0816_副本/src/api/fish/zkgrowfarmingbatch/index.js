import request from '@/utils/request'

// 创建养殖批次
export function createZkGrowFarmingBatch(data) {
  return request({
    url: '/fish/zk-grow-farming-batch/create',
    method: 'post',
    data: data
  })
}

// 更新养殖批次
export function updateZkGrowFarmingBatch(data) {
  return request({
    url: '/fish/zk-grow-farming-batch/update',
    method: 'put',
    data: data
  })
}

// 删除养殖批次
export function deleteZkGrowFarmingBatch(id) {
  return request({
    url: '/fish/zk-grow-farming-batch/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除养殖批次 */
export function deleteZkGrowFarmingBatchList(ids) {
  return request({
    url: `/fish/zk-grow-farming-batch/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得养殖批次
export function getZkGrowFarmingBatch(id) {
  return request({
    url: '/fish/zk-grow-farming-batch/get?id=' + id,
    method: 'get'
  })
}

// 获得养殖批次分页
export function getZkGrowFarmingBatchPage(params) {
  return request({
    url: '/fish/zk-grow-farming-batch/page',
    method: 'get',
    params
  })
}
// 导出养殖批次 Excel
export function exportZkGrowFarmingBatchExcel(params) {
  return request({
    url: '/fish/zk-grow-farming-batch/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}