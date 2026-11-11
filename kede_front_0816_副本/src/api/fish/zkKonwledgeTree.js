import request from '@/utils/request_jidi'

// 创建知识树
export function createZkKonwledgeTree(data) {
  return request({
    url: '/fish/zk-konwledge-tree/create',
    method: 'post',
    data: data
  })
}

// 更新知识树
export function updateZkKonwledgeTree(data) {
  return request({
    url: '/fish/zk-konwledge-tree/update',
    method: 'put',
    data: data
  })
}

// 删除知识树
export function deleteZkKonwledgeTree(id) {
  return request({
    url: '/fish/zk-konwledge-tree/delete?id=' + id,
    method: 'delete'
  })
}

// 获得知识树
export function getZkKonwledgeTree(id) {
  return request({
    url: '/fish/zk-konwledge-tree/get?id=' + id,
    method: 'get'
  })
}

// 获得知识树分页
export function getZkKonwledgeTreePage(query) {
  return request({
    url: '/fish/zk-konwledge-tree/page',
    method: 'get',
    params: query
  })
}
// 获得知识树列表
export function listKnowledge(query) {
  return request({
    url: '/fish/zk-konwledge-tree/listKnowledge',
    method: 'get',
    params: query
  })
}

// 导出知识树 Excel
export function exportZkKonwledgeTreeExcel(query) {
  return request({
    url: '/fish/zk-konwledge-tree/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
