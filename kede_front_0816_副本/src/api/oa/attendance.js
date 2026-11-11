import request from '@/utils/request'

/**
 * 打卡
 * @param {number} punchType 1上班 2下班
 * @param {string|object} locationOrOpts 兼容旧调用：字符串地点，或 { location, latitude, longitude }
 */
export function punch(punchType, locationOrOpts) {
  const opts = (locationOrOpts && typeof locationOrOpts === 'object')
    ? locationOrOpts
    : { location: locationOrOpts }
  return request({
    url: '/fish/zk-attendance/punch',
    method: 'post',
    // 由页面展示具体原因（如「不在打卡范围内」），避免拦截器只 reject('error')
    hideErrorMessage: true,
    data: {
      punchType,
      location: opts.location || '公司办公区',
      latitude: opts.latitude,
      longitude: opts.longitude
    }
  })
}

// 获得今日考勤信息
export function getTodayAttendance() {
  return request({
    url: '/fish/zk-attendance/today',
    method: 'get'
  })
}

// 获得考勤分页
export function getAttendancePage(query) {
  return request({
    url: '/fish/zk-attendance/page',
    method: 'get',
    params: query
  })
}

// 获得考勤统计
export function getAttendanceStatistics(month) {
  return request({
    url: '/fish/zk-attendance/statistics',
    method: 'get',
    params: { month }
  })
}
