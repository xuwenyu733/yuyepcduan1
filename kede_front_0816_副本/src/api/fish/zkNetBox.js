import request from '@/utils/request_jidi'

// 创建网盘资源
export function createZkNetBox(data) {
  return request({
    url: '/fish/zk-net-box/create',
    method: 'post',
    data: data
  })
}

// 更新网盘资源
export function updateZkNetBox(data) {
  return request({
    url: '/fish/zk-net-box/update',
    method: 'put',
    data: data
  })
}

// 删除网盘资源
export function deleteZkNetBox(id) {
  return request({
    url: '/fish/zk-net-box/delete?id=' + id,
    method: 'delete'
  })
}

// 获得网盘资源
export function getZkNetBox(id) {
  return request({
    url: '/fish/zk-net-box/get?id=' + id,
    method: 'get'
  })
}

// 获得网盘资源分页
export function getZkNetBoxPage(query) {
  return request({
    url: '/fish/zk-net-box/page',
    method: 'get',
    params: query
  })
}

// 导出网盘资源 Excel
export function exportZkNetBoxExcel(query) {
  return request({
    url: '/fish/zk-net-box/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
