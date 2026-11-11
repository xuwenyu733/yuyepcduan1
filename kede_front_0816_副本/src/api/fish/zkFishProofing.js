import request from '@/utils/request_jidi'

// 创建打样记录
export function createZkFishProofing(data) {
  return request({
    url: '/fish/zk-fish-proofing/create',
    method: 'post',
    data: data
  })
}

// 更新打样记录
export function updateZkFishProofing(data) {
  return request({
    url: '/fish/zk-fish-proofing/update',
    method: 'put',
    data: data
  })
}

// 删除打样记录
export function deleteZkFishProofing(id) {
  return request({
    url: '/fish/zk-fish-proofing/delete?id=' + id,
    method: 'delete'
  })
}

// 获得打样记录
export function getZkFishProofing(id) {
  return request({
    url: '/fish/zk-fish-proofing/get?id=' + id,
    method: 'get'
  })
}
export function getLastProofing(pondId) {
  return request({
    url: '/fish/zk-fish-proofing/getLastProofing?pondId=' + pondId,
    method: 'get'
  })
}

// 获得打样记录分页
export function getZkFishProofingPage(query) {
  return request({
    url: '/fish/zk-fish-proofing/page',
    method: 'get',
    params: query
  })
}

// 导出打样记录 Excel
export function exportZkFishProofingExcel(query) {
  return request({
    url: '/fish/zk-fish-proofing/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
