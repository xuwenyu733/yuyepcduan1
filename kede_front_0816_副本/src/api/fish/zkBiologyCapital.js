import request from '@/utils/request_jidi'

// 创建生物性资产
export function createZkBiologyCapital(data) {
  return request({
    url: '/fish/zk-biology-capital/create',
    method: 'post',
    data: data
  })
}

// 更新生物性资产
export function updateZkBiologyCapital(data) {
  return request({
    url: '/fish/zk-biology-capital/update',
    method: 'put',
    data: data
  })
}

// 删除生物性资产
export function deleteZkBiologyCapital(id) {
  return request({
    url: '/fish/zk-biology-capital/delete?id=' + id,
    method: 'delete'
  })
}

// 获得生物性资产
export function getZkBiologyCapital(id) {
  return request({
    url: '/fish/zk-biology-capital/get?id=' + id,
    method: 'get'
  })
}

// 获得生物性资产分页
export function getZkBiologyCapitalPage(query) {
  return request({
    url: '/fish/zk-biology-capital/page',
    method: 'get',
    params: query
  })
}

// 导出生物性资产 Excel
export function exportZkBiologyCapitalExcel(query) {
  return request({
    url: '/fish/zk-biology-capital/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 获得首页统计数据
export function getZkBiologyCapitalMainHome() {
  return request({
    url: '/fish/zk-biology-capital/main-home',
    method: 'get'
  })
}

// 获得精准投喂顶部统计
export function getPrecisionFeedingStats() {
  return request({
    url: '/fish/zk-biology-capital/precision-feeding-stats',
    method: 'get',
    silent: true,
    hideErrorMessage: true
  })
}

let _pfPondsCache = null
let _pfPondsAt = 0
let _pfPondsInflight = null
const PF_PONDS_TTL_MS = 45000

export function clearPrecisionFeedingPondsCache() {
  _pfPondsCache = null
  _pfPondsAt = 0
  _pfPondsInflight = null
}

// 获得精准投喂鱼池列表（45s 内复用，二次进页更快）
export function getPrecisionFeedingPonds(force = false) {
  if (!force && _pfPondsCache && Date.now() - _pfPondsAt < PF_PONDS_TTL_MS) {
    return Promise.resolve(_pfPondsCache)
  }
  if (!force && _pfPondsInflight) return _pfPondsInflight
  _pfPondsInflight = request({
    url: '/fish/zk-biology-capital/precision-feeding-ponds',
    method: 'get'
  }).then((res) => {
    if (res && (res.code === 0 || res.code === 200) && Array.isArray(res.data)) {
      _pfPondsCache = res
      _pfPondsAt = Date.now()
    }
    return res
  }).finally(() => {
    if (_pfPondsInflight) _pfPondsInflight = null
  })
  return _pfPondsInflight
}

// 获得精准投喂今日投喂记录
export function getPrecisionFeedingTodayLogs() {
  return request({
    url: '/fish/zk-biology-capital/precision-feeding-today-logs',
    method: 'get',
    silent: true,
    hideErrorMessage: true
  })
}
