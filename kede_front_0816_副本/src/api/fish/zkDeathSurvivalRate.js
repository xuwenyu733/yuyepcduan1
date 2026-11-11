import request from '@/utils/request_jidi'
import { withUserDeptParams } from '@/utils/deptScope'

// 创建死亡和存活率
export function createZkDeathSurvivalRate(data) {
  return request({
    url: '/fish/zk-death-survival-rate/create',
    method: 'post',
    data: data
  })
}

// 更新死亡和存活率
export function updateZkDeathSurvivalRate(data) {
  return request({
    url: '/fish/zk-death-survival-rate/update',
    method: 'put',
    data: data
  })
}

// 删除死亡和存活率
export function deleteZkDeathSurvivalRate(id) {
  return request({
    url: '/fish/zk-death-survival-rate/delete?id=' + id,
    method: 'delete'
  })
}

// 获得死亡和存活率
export function getZkDeathSurvivalRate(id) {
  return request({
    url: '/fish/zk-death-survival-rate/get?id=' + id,
    method: 'get'
  })
}

// 获得死亡和存活率分页
export function getZkDeathSurvivalRatePage(query) {
  return request({
    url: '/fish/zk-death-survival-rate/page',
    method: 'get',
    params: withUserDeptParams(query)
  })
}

// 导出死亡和存活率 Excel
export function exportZkDeathSurvivalRateExcel(query) {
  return request({
    url: '/fish/zk-death-survival-rate/export-excel',
    method: 'get',
    params: withUserDeptParams(query),
    responseType: 'blob'
  })
}

// 获得死亡和存活率统计数据
export function getZkDeathSurvivalRateStatistics(params = {}) {
  return request({
    url: '/fish/zk-death-survival-rate/statistics',
    method: 'get',
    params: withUserDeptParams(params)
  })
}
