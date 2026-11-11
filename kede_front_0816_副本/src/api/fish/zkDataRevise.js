import request from '@/utils/request_jidi'

// 创建数据修正记录
export function createZkDataRevise(data) {
  return request({
    url: '/fish/zk-data-revise/create',
    method: 'post',
    data: data
  })
}

// 更新数据修正记录
export function updateZkDataRevise(data) {
  return request({
    url: '/fish/zk-data-revise/update',
    method: 'put',
    data: data
  })
}

// 删除数据修正记录
export function deleteZkDataRevise(id) {
  return request({
    url: '/fish/zk-data-revise/delete?id=' + id,
    method: 'delete'
  })
}

// 获得数据修正记录
export function getZkDataRevise(id) {
  return request({
    url: '/fish/zk-data-revise/get?id=' + id,
    method: 'get'
  })
}

// 获得数据修正记录分页
export function getZkDataRevisePage(query) {
  return request({
    url: '/fish/zk-data-revise/page',
    method: 'get',
    params: query
  })
}

// 导出数据修正记录 Excel
export function exportZkDataReviseExcel(query) {
  return request({
    url: '/fish/zk-data-revise/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
