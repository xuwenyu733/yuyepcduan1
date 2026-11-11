import request from '@/utils/request_jidi'

// 创建阈值规则
export function createZkThresholdRule(data) {
  return request({
    url: '/fish/zk-threshold-rule/create',
    method: 'post',
    data: data
  })
}

// 更新阈值规则
export function updateZkThresholdRule(data) {
  return request({
    url: '/fish/zk-threshold-rule/update',
    method: 'put',
    data: data
  })
}

// 删除阈值规则
export function deleteZkThresholdRule(id) {
  return request({
    url: '/fish/zk-threshold-rule/delete?id=' + id,
    method: 'delete'
  })
}

// 获得阈值规则
export function getZkThresholdRule(id) {
  return request({
    url: '/fish/zk-threshold-rule/get?id=' + id,
    method: 'get'
  })
}

// 获得阈值规则分页
export function getZkThresholdRulePage(query) {
  return request({
    url: '/fish/zk-threshold-rule/page',
    method: 'get',
    params: query
  })
}

// 导出阈值规则 Excel
export function exportZkThresholdRuleExcel(query) {
  return request({
    url: '/fish/zk-threshold-rule/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
