import request from '@/utils/request_jidi'

// 创建存塘鱼苗明细
export function createZkPondFishDetail(data) {
  return request({
    url: '/fish/zk-pond-fish-detail/create',
    method: 'post',
    data: data
  })
}

// 更新存塘鱼苗明细
export function updateZkPondFishDetail(data) {
  return request({
    url: '/fish/zk-pond-fish-detail/update',
    method: 'put',
    data: data
  })
}

// 删除存塘鱼苗明细
export function deleteZkPondFishDetail(id) {
  return request({
    url: '/fish/zk-pond-fish-detail/delete?id=' + id,
    method: 'delete'
  })
}

// 获得存塘鱼苗明细
export function getZkPondFishDetail(id) {
  return request({
    url: '/fish/zk-pond-fish-detail/get?id=' + id,
    method: 'get'
  })
}

// 获得存塘鱼苗明细分页
export function getZkPondFishDetailPage(query) {
  return request({
    url: '/fish/zk-pond-fish-detail/page',
    method: 'get',
    params: query
  })
}

// 导出存塘鱼苗明细 Excel
export function exportZkPondFishDetailExcel(query) {
  return request({
    url: '/fish/zk-pond-fish-detail/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
