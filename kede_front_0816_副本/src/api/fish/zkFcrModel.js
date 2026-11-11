import request from '@/utils/request_jidi'

// 创建饵料系数FCR模型
export function createZkFcrModel(data) {
  return request({
    url: '/fish/zk-fcr-model/create',
    method: 'post',
    data: data
  })
}

// 更新饵料系数FCR模型
export function updateZkFcrModel(data) {
  return request({
    url: '/fish/zk-fcr-model/update',
    method: 'put',
    data: data
  })
}

// 删除饵料系数FCR模型
export function deleteZkFcrModel(id) {
  return request({
    url: '/fish/zk-fcr-model/delete?id=' + id,
    method: 'delete'
  })
}

// 获得饵料系数FCR模型
export function getZkFcrModel(id) {
  return request({
    url: '/fish/zk-fcr-model/get?id=' + id,
    method: 'get'
  })
}

// 获得饵料系数FCR模型分页
export function getZkFcrModelPage(query) {
  return request({
    url: '/fish/zk-fcr-model/page',
    method: 'get',
    params: query
  })
}

// 导出饵料系数FCR模型 Excel
export function exportZkFcrModelExcel(query) {
  return request({
    url: '/fish/zk-fcr-model/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
