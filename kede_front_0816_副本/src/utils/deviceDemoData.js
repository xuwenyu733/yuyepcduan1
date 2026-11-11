/**
 * 设备监控页图例与（已废弃）演示数据。
 * DEMO_* 假数据已写入 zk_energy_meter_reading / zk_device_monitor_panel，
 * 下列 DEMO 常量整段注释保留，勿再作为页面数据源。
 */
import { formatWorkshop, formatSystem } from '@/utils/siteNaming'

export const TYPE_LEGEND = [
  { name: '水泵', color: '#36b8ff' },
  { name: '增氧机', color: '#2e8b57' },
  { name: '温控器', color: '#e8a020' },
  { name: '投饵机', color: '#e86aa8' },
  { name: '微滤机', color: '#9b6dff' },
  { name: '风机', color: '#d4783a' },
  { name: '臭氧发生器', color: '#6fd6a0' },
  { name: '紫外线消毒器', color: '#d946a8' },
  { name: '碱度调节系统', color: '#a8d930' },
  { name: '反硝化系统', color: '#5eb8ff' }
]

/**
 * zk_device 没有生产模块字段；按设备名/类型归入工艺模块，供筛选下拉使用。
 */
export function inferProductionModule(name, type) {
  const t = String(name || '') + ' ' + String(type || '')
  if (!t.trim()) return ''
  if (/反硝化/.test(t)) return '反硝化'
  if (/CO₂|CO2|吹脱|风机|二氧化碳/.test(t)) return 'CO₂吹脱'
  if (/紫外|臭氧|消毒/.test(t)) return '消毒'
  if (/投饵|投喂/.test(t)) return '投喂'
  if (/温控器|温控系统|加热|冷却/.test(t)) return '温控'
  if (/增氧机|增氧电磁|曝气/.test(t) && !/溶氧|溶解氧/.test(t)) return '增氧曝气'
  if (/微滤|反冲|循环泵|底排|进排|过滤/.test(t) || /水泵/.test(t)) return '循环过滤'
  if (/碱度/.test(t)) return '水质调节'
  if (/溶氧|溶解氧|温度|PH|pH|氨氮|浊度|ORP|氧化还原|水质/.test(t)) return '水质监测'
  if (/摄像|监控|门禁/.test(t)) return '安防监控'
  return ''
}

/** 能耗环形图兜底（与 seed-demo 一致，单位 MWh；接口无数据时使用） */
export const DEFAULT_ENERGY_BY_TYPE = {
  水泵: 8.2,
  增氧机: 3.1,
  温控器: 2.4,
  投饵机: 1.4,
  微滤机: 1.8,
  风机: 1.2,
  臭氧发生器: 0.9,
  紫外线消毒器: 0.7,
  碱度调节系统: 0.6,
  反硝化系统: 1.9
}

/* ========== 以下 DEMO 假数据已落库，注释保留备查（完整列表见 seed-demo-as-real-20260831.sql / git 历史） ==========
export const DEMO_DEVICES = [ ... ]
export const DEMO_ENERGY = { 水泵: 8.2, 增氧机: 3.1, ... }
export const DEMO_FAULT_MONTH = [ { name: '微滤机A2-1', count: 14 }, ... ]
========== DEMO 注释结束 ========== */

/** @deprecated 请使用 getOverviewMonitor 库表接口 */
export function getOverviewDeviceStatus() {
  return []
}

/** @deprecated 请使用 getOverviewMonitor 库表接口 */
export function getOverviewEnergyData() {
  return []
}

/** @deprecated 请使用 getOverviewMonitor 库表接口 */
export function getOverviewFaultMonth() {
  return []
}

export function deviceStatusLabel(status) {
  return { running: '运行', stopped: '停止', fault: '故障' }[status] || status
}

/** @deprecated */
export function countRunningDemoDevices() {
  return 0
}

// silence unused import lint for commented historical helpers
void formatWorkshop
void formatSystem
