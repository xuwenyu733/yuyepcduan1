import request from '@/utils/request'

// 创建实验数据采集
export function createZkExperimentalData(data) {
  return request({
    url: '/fish/zk-experimental-data/create',
    method: 'post',
    data: data
  })
}

// 更新实验数据采集
export function updateZkExperimentalData(data) {
  return request({
    url: '/fish/zk-experimental-data/update',
    method: 'put',
    data: data
  })
}

// 删除实验数据采集
export function deleteZkExperimentalData(id) {
  return request({
    url: '/fish/zk-experimental-data/delete?id=' + id,
    method: 'delete'
  })
}

/** 批量删除实验数据采集 */
export function deleteZkExperimentalDataList(ids) {
  return request({
    url: `/fish/zk-experimental-data/delete-list?ids=${ids.join(',')}`,
    method: 'delete'
  })
}

// 获得实验数据采集
export function getZkExperimentalData(id) {
  return request({
    url: '/fish/zk-experimental-data/get?id=' + id,
    method: 'get'
  })
}

// 获得实验数据采集分页
export function getZkExperimentalDataPage(params) {
  return request({
    url: '/fish/zk-experimental-data/page',
    method: 'get',
    params
  })
}
// 导出实验数据采集 Excel
export function exportZkExperimentalDataExcel(params) {
  return request({
    url: '/fish/zk-experimental-data/export-excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}