import request from '@/utils/request_jidi'

// 创建精准投喂
export function createZkPrecisionFeeding(data) {
  return request({
    url: '/fish/zk-precision-feeding/create',
    method: 'post',
    data: data
  })
}

// 更新精准投喂
export function updateZkPrecisionFeeding(data) {
  return request({
    url: '/fish/zk-precision-feeding/update',
    method: 'put',
    data: data
  })
}

// 删除精准投喂
export function deleteZkPrecisionFeeding(id) {
  return request({
    url: '/fish/zk-precision-feeding/delete?id=' + id,
    method: 'delete'
  })
}

// 获得精准投喂
export function getZkPrecisionFeeding(id) {
  return request({
    url: '/fish/zk-precision-feeding/get?id=' + id,
    method: 'get'
  })
}

// 获得精准投喂分页
export function getZkPrecisionFeedingPage(query) {
  return request({
    url: '/fish/zk-precision-feeding/page',
    method: 'get',
    params: query
  })
}

// 导出精准投喂 Excel
export function exportZkPrecisionFeedingExcel(query) {
  return request({
    url: '/fish/zk-precision-feeding/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

/** 后端计算智能投喂推荐量
 * @param {string|number} pondId
 * @param {object} [config] axios 额外配置；协同诊断传 { silent: true } 避免「鱼池不存在」打扰
 */
export function getPrecisionFeedRecommend(pondId, config = {}) {
  return request({
    url: '/fish/zk-precision-feeding/recommend',
    method: 'get',
    params: { pondId },
    ...config
  })
}

export function getPrecisionFeedPlans(pondId, planDate, config = {}) {
  return request({
    url: '/fish/zk-precision-feeding/plans',
    method: 'get',
    params: { pondId, planDate },
    ...config
  })
}

export function savePrecisionFeedPlan(data) {
  return request({
    url: '/fish/zk-precision-feeding/plan/save',
    method: 'post',
    data
  })
}

export function confirmPrecisionFeedPlan(pondId, planType = 'actual', planDate) {
  return request({
    url: '/fish/zk-precision-feeding/plan/confirm',
    method: 'post',
    params: { pondId, planType, planDate }
  })
}

export function getPrecisionFeedConsumption(params, config = {}) {
  return request({
    url: '/fish/zk-precision-feeding/feed-consumption',
    method: 'get',
    params,
    ...config
  })
}

export function getPrecisionFeedAiAnalysis(pondId, config = {}) {
  return request({
    url: '/fish/zk-precision-feeding/ai-analysis',
    method: 'get',
    params: { pondId },
    ...config
  })
}

export function getPrecisionFeedAffectingTasks(pondId, config = {}) {
  return request({
    url: '/fish/zk-precision-feeding/affecting-tasks',
    method: 'get',
    params: { pondId },
    ...config
  })
}
