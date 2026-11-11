import request from '@/utils/request_jidi'

// 获得健康状态分布
export function getHealthStatusDistribution() {
  return request({
    url: '/fish/zk-pond-health/health-status-distribution',
    method: 'get'
  })
}

// 获得各鱼池最新健康评分
export function getLatestPondHealthList() {
  return request({
    url: '/fish/zk-pond-health/latest-pond-health-list',
    method: 'get'
  })
}
