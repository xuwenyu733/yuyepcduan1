import request from '@/utils/request'

// 获取个人考核数据
export function getPersonalEvaluation(period) {
  return request({
    url: '/fish/oa-evaluation/personal',
    method: 'get',
    params: { period },
    hideErrorMessage: true
  })
}

// 获取个人考核趋势数据（近6个月）
export function getPersonalEvaluationTrend() {
  return request({
    url: '/fish/oa-evaluation/personal-trend',
    method: 'get',
    hideErrorMessage: true
  })
}

// 获取个人考核统计摘要
export function getPersonalEvaluationSummary() {
  return request({
    url: '/fish/oa-evaluation/personal-summary',
    method: 'get',
    hideErrorMessage: true
  })
}

// 获取部门排名数据
export function getDepartmentRanking(period) {
  return request({
    url: '/fish/oa-evaluation/department-ranking',
    method: 'get',
    params: { period },
    hideErrorMessage: true
  })
}

// 获取历史考核记录
export function getEvaluationHistory(periods) {
  return request({
    url: '/fish/oa-evaluation/history',
    method: 'get',
    params: { periods },
    hideErrorMessage: true
  })
}

/** 维度满分配置（计划25/考勤10/考试15/他人20/绩效30） */
export function getDimensionWeights() {
  return request({
    url: '/fish/oa-evaluation/dimension-weights',
    method: 'get'
  })
}

/** 手动触发考核计算（会发站内信） */
export function calculateEvaluation(period) {
  return request({
    url: '/fish/oa-evaluation/calculate',
    method: 'post',
    params: { period }
  })
}

/** 录入实操考试分 0-5 */
export function setPracticeExamScore(data) {
  return request({
    url: '/fish/oa-evaluation/practice-exam',
    method: 'post',
    data
  })
}
