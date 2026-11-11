import request from '@/utils/request_jidi'

// 创建存塘鱼苗
export function createZkPondFish(data) {
  return request({
    url: '/fish/zk-pond-fish/create',
    method: 'post',
    data: data
  })
}

// 更新存塘鱼苗
export function updateZkPondFish(data) {
  return request({
    url: '/fish/zk-pond-fish/update',
    method: 'put',
    data: data
  })
}

// 删除存塘鱼苗
export function deleteZkPondFish(id) {
  return request({
    url: '/fish/zk-pond-fish/delete?id=' + id,
    method: 'delete'
  })
}

// 获得存塘鱼苗
export function getZkPondFish(id) {
  return request({
    url: '/fish/zk-pond-fish/get?id=' + id,
    method: 'get'
  })
}

// 获得存塘鱼苗分页
export function getZkPondFishPage(query) {
  return request({
    url: '/fish/zk-pond-fish/page',
    method: 'get',
    params: query
  })
}

// 导出存塘鱼苗 Excel
export function exportZkPondFishExcel(query) {
  return request({
    url: '/fish/zk-pond-fish/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
