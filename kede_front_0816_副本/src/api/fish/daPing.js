import request from '@/utils/request_jidi'
import { withUserDeptParams } from '@/utils/deptScope'

/** 系统总览大屏 - 渔况智览6模块数据 */
export function getOverview3(params = {}) {
  return request({
    url: '/fish/zk-pond-warn/getOverview3',
    method: 'get',
    params: withUserDeptParams(params)
  })
}

/** 大屏监控面板：水质/设备/故障（库表） */
export function getOverviewMonitor(params = {}) {
  return request({
    url: '/fish/zk-pond-warn/getOverviewMonitor',
    method: 'get',
    params: withUserDeptParams(params),
    silent: true,
    hideErrorMessage: true
  })
}

/** 设备监控轻量接口；失败时不回退慢全量（避免 404 后仍等 10s+） */
export function getDeviceMonitorLive(params = {}) {
  return request({
    url: '/fish/zk-pond-warn/getDeviceMonitorLive',
    method: 'get',
    params: withUserDeptParams(params),
    silent: true,
    hideErrorMessage: true
  })
}

/** 设备场景监控（跳过水质长查询）；仅新后端生效 */
export function getOverviewMonitorDeviceScene(params = {}) {
  return request({
    url: '/fish/zk-pond-warn/getOverviewMonitor',
    method: 'get',
    params: withUserDeptParams({ scene: 'device', ...params }),
    silent: true,
    hideErrorMessage: true
  })
}

/** 水质专题轻量接口：仅最新指标 + 24h 趋势 */
export function getWaterQualityLive(params = {}) {
  return request({
    url: '/fish/zk-pond-warn/getWaterQualityLive',
    method: 'get',
    params: withUserDeptParams(params),
    silent: true,
    hideErrorMessage: true
  })
}

/** 鱼类视觉 / 人员门禁扩展告警（预留表） */
export function getExtendedAlarms(params = {}) {
  return request({
    url: '/fish/zk-pond-warn/getExtendedAlarms',
    method: 'get',
    params: withUserDeptParams(params),
    silent: true,
    hideErrorMessage: true
  })
}

/** 更新人员门禁 / 鱼类视觉扩展告警状态 */
export function updateExtendedAlarm(data = {}) {
  return request({
    url: '/fish/zk-pond-warn/updateExtendedAlarm',
    method: 'put',
    data
  })
}

/** 鱼健康树形快照（库表） */
export function getFishHealthSnapshot(key = 'default') {
  return request({
    url: '/fish/zk-pond-warn/getFishHealthSnapshot',
    method: 'get',
    params: withUserDeptParams({ key }),
    silent: true,
    hideErrorMessage: true
  })
}

/** 图表/考评种子（库表 zk_chart_seed） */
export function getChartSeed(key) {
  return request({
    url: '/fish/zk-pond-warn/getChartSeed',
    method: 'get',
    params: withUserDeptParams({ key }),
    silent: true,
    hideErrorMessage: true
  })
}

/** 总览日程事件 */
export function getOverviewSchedule(params = {}) {
  return request({
    url: '/fish/zk-pond-warn/getOverviewSchedule',
    method: 'get',
    params: withUserDeptParams(params),
    silent: true,
    hideErrorMessage: true
  })
}

/** 获取基地设备/水质实时信息 */
export function getDeviceInfo(workshopId) {
  return request({
    url: '/fish/zk-pond-warn/getDeviceInfo',
    method: 'get',
    params: withUserDeptParams({ workshopId })
  })
}

/** 获取系统警告 */
export function getWarn(workshopId) {
  return request({
    url: '/fish/zk-pond-warn/getWarn',
    method: 'get',
    params: withUserDeptParams({ workshopId })
  })
}

/** 存量分布：批次鱼苗树（对齐 jidi_daping/model4） */
export function getFishBatch(params = {}) {
  return request({
    url: '/fish/zk-pond-warn/getFishBatch',
    method: 'get',
    params: withUserDeptParams(params),
    silent: true,
    hideErrorMessage: true
  })
}
