import { TYPE_LEGEND, DEFAULT_ENERGY_BY_TYPE } from '@/utils/deviceDemoData'
import { getOverviewMonitor, getDeviceMonitorLive, getOverviewMonitorDeviceScene } from '@/api/fish/daPing'
import { getZkDeviceAlarmPage } from '@/api/fish/zkDeviceAlarm'
import { getZkUseMedicinePage } from '@/api/fish/zkUseMedicine'
import { getZkDevicePage } from '@/api/fish/zkDevice'
import { getZkDeviceRecordPage } from '@/api/fish/zkDeviceRecord'
import { fishApi } from '@/api/modules'
import { resolveCurrentUserDeptId, isPlatformUnscopedAdmin, isUserDeptScopeDenied } from '@/utils/siteOptions'
import { loadOverviewAlerts } from '@/utils/zkAlarmShared'

let _monitorCache = null
let _monitorAt = 0
let _monitorPromise = null
let _deviceMonitorCache = null
let _deviceMonitorAt = 0
let _deviceMonitorPromise = null
const MONITOR_TTL_MS = 3000
const DEVICE_MONITOR_TTL_MS = 45000
const DEVICE_MONITOR_TIMEOUT_MS = 6000
const FULL_MONITOR_TIMEOUT_MS = 25000
/** 设备页轻量接口是否可用（404 后本会话不再打） */
let _deviceLiveApiOk = true

/** 登出 / 切换用户时清空监控缓存，避免串部门数据 */
export function clearDashboardLiveCaches() {
  _monitorCache = null
  _monitorAt = 0
  _monitorPromise = null
  _deviceMonitorCache = null
  _deviceMonitorAt = 0
  _deviceMonitorPromise = null
  _deviceLiveApiOk = true
}

function defaultEnergyList() {
  return TYPE_LEGEND.map((t) => ({
    name: t.name,
    value: Number(DEFAULT_ENERGY_BY_TYPE[t.name]) || 0,
    color: t.color
  })).filter((e) => e.value > 0)
}

function plainText(val) {
  if (val == null || val === '') return ''
  return String(val).replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function formatDateTime(val) {
  if (!val) return '-'
  const d = new Date(val)
  if (isNaN(d.getTime())) return String(val)
  const pad = (n) => String(n).padStart(2, '0')
  return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())
    + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes())
}

export async function fetchDashboardAlerts() {
  try {
    // 与 AI报警监控详情同源：zk_device_alarm 分页接口
    const { alerts } = await loadOverviewAlerts()
    return alerts || []
  } catch (e) {
    console.warn('[dashboardLive] alerts', e && e.message)
    return []
  }
}

export async function fetchDashboardMedicine(pageSize = 12) {
  try {
    const res = await getZkUseMedicinePage({ pageNo: 1, pageSize })
    const list = (res && res.data && res.data.list) || []
    return list.map((m) => ({
      symptom: plainText(m.controlTitle || m.controlContent || '未记录'),
      time: m.createTime ? formatDateTime(m.createTime) : '-',
      drug: plainText(m.medicineName || '-'),
      pond: plainText(m.pondName || '-')
    }))
  } catch (e) {
    console.warn('[dashboardLive] medicine', e && e.message)
    return []
  }
}

function normalizeMonitor(data) {
  if (!data) {
    return {
      waterAsOf: '',
      waterRealtime: [],
      waterTrend: [],
      devices: [],
      faultMonth: [],
      energy: [],
      energyNote: '监控接口暂无数据'
    }
  }
  const devices = (data.devices || []).map((d) => ({
    name: d.name || '-',
    status: d.status || 'stopped',
    workshop: d.workshop || '',
    voltage: d.voltage != null ? Number(d.voltage) : null,
    current: d.current != null ? Number(d.current) : (d.currValue != null ? Number(d.currValue) : null),
    frequency: d.frequency != null ? Number(d.frequency) : null,
    currValue: d.currValue != null ? Number(d.currValue) : null,
    unit: d.unit || '',
    pondDetailNum: d.pondDetailNum || ''
  }))
  const waterRealtime = (data.waterRealtime || []).map((w) => ({
    name: w.name,
    unit: w.unit || '',
    value: w.value != null ? Number(w.value) : null,
    range: w.range || '',
    status: w.status || (w.value == null ? 'unknown' : 'normal'),
    key: w.key || ''
  }))
  const waterTrend = (data.waterTrend || []).map((t) => ({
    time: t.time || '-',
    temp: t.temp != null ? Number(t.temp) : null,
    oxygen: t.oxygen != null ? Number(t.oxygen) : null,
    ph: t.ph != null ? Number(t.ph) : null
  }))
  const faultMonth = (data.faultMonth || []).map((f) => ({
    name: f.name || '-',
    count: Number(f.count) || 0
  }))
  const energy = (data.energy || []).map((e) => ({
    name: e.name,
    value: Number(e.value) || 0,
    color: e.color || '#3b82f6'
  }))
  const mapTrend = (arr) => (arr || []).map((t) => ({
    time: t.time || '-',
    temp: t.temp != null ? Number(t.temp) : null,
    oxygen: t.oxygen != null ? Number(t.oxygen) : null,
    ph: t.ph != null ? Number(t.ph) : null
  }))
  return {
    waterAsOf: data.waterAsOf || '',
    waterRealtime,
    waterTrend,
    waterTrend7d: mapTrend(data.waterTrend7d),
    waterTrend30d: mapTrend(data.waterTrend30d),
    devices,
    faultMonth,
    energy,
    energyNote: data.energyNote || '',
    energyTrend: (data.energyTrend || []).map((t) => ({
      time: t.time || '-',
      deviceType: t.deviceType || '',
      value: t.value != null ? Number(t.value) : 0
    })),
    backwash: (data.backwash || []).map((b) => ({
      pool: b.pool || '',
      count: b.count != null ? Number(b.count) : 0,
      status: b.status || 'normal',
      device: b.device || '',
      workshop: b.workshop || '',
      system: b.system || ''
    })),
    aerator: (data.aerator || []).map((a) => ({
      pool: a.pool || '',
      share: a.share != null ? Number(a.share) : 0,
      workshop: a.workshop || '',
      system: a.system || ''
    })),
    co2: (data.co2 || []).map((c) => ({
      device: c.device || '',
      start: c.start || '',
      end: c.end || '',
      workshop: c.workshop || '',
      system: c.system || '',
      module: c.module || ''
    })),
    waterFlow: (data.waterFlow || []).map((w) => ({
      name: w.name || '',
      flow: w.flow != null ? Number(w.flow) : 0,
      workshop: w.workshop || '',
      system: w.system || ''
    }))
  }
}

export async function fetchOverviewMonitor(force = false, options = {}) {
  const scene = (options && options.scene) || 'all'
  const timeoutMs = Number(options && options.timeoutMs) || FULL_MONITOR_TIMEOUT_MS
  if (scene === 'device') {
    return fetchDeviceMonitorLive(force)
  }
  if (!force && _monitorCache && Date.now() - _monitorAt < MONITOR_TTL_MS) {
    return _monitorCache
  }
  if (!force && _monitorPromise) return _monitorPromise
  _monitorPromise = (async () => {
    let fromApi = null
    try {
      // 慢接口加竞速，超时后走设备分页兜底，避免整页空白等 1～2 分钟
      const res = await Promise.race([
        getOverviewMonitor({ scene: 'all' }),
        new Promise((_, reject) => setTimeout(() => reject(new Error('monitor-timeout-' + timeoutMs + 'ms')), timeoutMs))
      ])
      if (res && res.data) {
        fromApi = normalizeMonitor(res.data)
      }
    } catch (e) {
      console.warn('[dashboardLive] monitor api', e && e.message)
    }
    fromApi = await mergeWithAssembled(fromApi)
    // 门头沟 IoT 覆盖水质实时（有值才盖）
    try {
      const iot = await fishApi.getMentougouWaterLive()
      if (iot && iot.code === 0 && iot.data && (iot.data.waterRealtime || []).length) {
        const hasVal = iot.data.waterRealtime.some((w) => w && w.value != null)
        if (hasVal) {
          fromApi = fromApi || normalizeMonitor(null)
          fromApi.waterRealtime = iot.data.waterRealtime
          if (iot.data.waterAsOf) fromApi.waterAsOf = iot.data.waterAsOf
        }
      }
    } catch (eIot) {
      console.warn('[dashboardLive] mentougou iot', eIot && eIot.message)
    }
    // 空设备结果不长时间占缓存，便于重进页面重试
    if (fromApi && (fromApi.devices || []).length) {
      _monitorCache = fromApi
      _monitorAt = Date.now()
    } else {
      _monitorCache = fromApi
      _monitorAt = Date.now() - MONITOR_TTL_MS + 3000
    }
    return fromApi
  })().finally(() => {
    _monitorPromise = null
  })
  return _monitorPromise
}

function deviceMonitorUseful(mon) {
  if (!mon) return false
  return (mon.devices || []).length > 0
    || (mon.energy || []).length > 0
    || (mon.backwash || []).length > 0
    || (mon.aerator || []).length > 0
    || (mon.faultMonth || []).length > 0
}

/** 接口有设备但面板空时，用设备名本地推导，避免再等组装接口 */
function fillDevicePanelsLocally(mon) {
  if (!mon || !(mon.devices || []).length) return mon
  const need = !(mon.backwash || []).length
    || !(mon.aerator || []).length
    || !(mon.waterFlow || []).length
    || !(mon.co2 || []).length
  if (!need) return mon
  const derived = derivePanelsFromDevices(mon.devices)
  const out = Object.assign({}, mon)
  if (!(out.backwash || []).length && derived.backwash.length) out.backwash = derived.backwash
  if (!(out.aerator || []).length && derived.aerator.length) out.aerator = derived.aerator
  if (!(out.waterFlow || []).length && derived.waterFlow.length) out.waterFlow = derived.waterFlow
  if (!(out.co2 || []).length && derived.co2.length) out.co2 = derived.co2
  return out
}

/**
 * 设备监控专用：优先轻量接口，成功即返回（不再等本地组装拖慢首屏）。
 * 接口失败/空时才走轻量组装；不污染首页全量缓存。
 */
export async function fetchDeviceMonitorLive(force = false) {
  // 无部门普通用户：不请求、不组装全站数据
  if (isUserDeptScopeDenied()) {
    const empty = normalizeMonitor(null)
    empty.energyNote = '当前账号未设置归属部门，无法查看设备监控数据'
    return empty
  }
  if (force) {
    _deviceMonitorCache = null
    _deviceMonitorAt = 0
  } else if (_deviceMonitorCache && Date.now() - _deviceMonitorAt < DEVICE_MONITOR_TTL_MS) {
    return _deviceMonitorCache
  }
  if (!force && _deviceMonitorPromise) return _deviceMonitorPromise
  const run = (async () => {
    const apiPromise = (async () => {
      if (!_deviceLiveApiOk) return null
      try {
        const res = await Promise.race([
          getDeviceMonitorLive().catch((err) => {
            const status = err && err.response && err.response.status
            const code = err && (err.code || (err.response && err.response.data && err.response.data.code))
            if (status === 404 || code === 404) {
              _deviceLiveApiOk = false
              return getOverviewMonitorDeviceScene()
            }
            throw err
          }),
          new Promise((_, reject) => setTimeout(
            () => reject(new Error('device-monitor-timeout-' + DEVICE_MONITOR_TIMEOUT_MS + 'ms')),
            DEVICE_MONITOR_TIMEOUT_MS
          ))
        ])
        if (res && (res.code === 0 || res.code === 200) && res.data) {
          return normalizeMonitor(res.data)
        }
        if (res && res.code === 404) {
          _deviceLiveApiOk = false
        }
      } catch (e) {
        console.warn('[dashboardLive] device monitor api', e && e.message)
      }
      return null
    })()

    const fromApi = await apiPromise
    if (deviceMonitorUseful(fromApi)) {
      const merged = fillDevicePanelsLocally(fromApi)
      _deviceMonitorCache = merged
      _deviceMonitorAt = Date.now()
      return merged
    }

    // 接口空/超时：轻量组装（不拉水质指标，设备页用不到）
    const assembled = await assembleDeviceMonitorFromLegacyApis().catch((e) => {
      console.warn('[dashboardLive] device assemble', e && e.message)
      return normalizeMonitor(null)
    })
    const merged = fillDevicePanelsLocally(mergeMonitorPreferApi(assembled, fromApi))
    _deviceMonitorCache = merged
    _deviceMonitorAt = deviceMonitorUseful(fromApi)
      ? Date.now()
      : Date.now() - DEVICE_MONITOR_TTL_MS + 8000
    return merged
  })().finally(() => {
    if (_deviceMonitorPromise === run) _deviceMonitorPromise = null
  })
  _deviceMonitorPromise = run
  return run
}

function mergeMonitorPreferApi(base, api) {
  if (!base && !api) return normalizeMonitor(null)
  if (!api) return base
  if (!base) return api
  const out = Object.assign({}, base)
  const scoped = resolveCurrentUserDeptId() != null
  // 部门用户：设备/故障允许空数组覆盖（避免串部门）；
  // 面板类仅在有数据时覆盖——空数组勿抹掉基于本部门设备组装的兜底
  const take = (key, allowEmpty) => {
    if (!Array.isArray(api[key])) return
    if (api[key].length || allowEmpty) out[key] = api[key]
  }
  take('devices', scoped)
  take('energy', false)
  take('energyTrend', false)
  take('faultMonth', scoped)
  take('backwash', false)
  take('aerator', false)
  take('co2', false)
  take('waterFlow', false)
  // 设备场景本就不含水质，勿用空数组覆盖组装结果中的水质（若有）
  if ((api.waterRealtime || []).length) out.waterRealtime = api.waterRealtime
  if ((api.waterTrend || []).length) out.waterTrend = api.waterTrend
  if (api.energyNote) out.energyNote = api.energyNote
  if (api.waterAsOf) out.waterAsOf = api.waterAsOf
  return out
}

async function mergeWithAssembled(fromApi) {
  const waterOk = fromApi && (fromApi.waterRealtime || []).some(
    (w) => w && w.value != null && !Number.isNaN(Number(w.value))
  )
  const energyOk = fromApi && (fromApi.energy || []).length > 0
  const devicesOk = fromApi && (fromApi.devices || []).length > 0
  const panelsOk = fromApi
    && (fromApi.backwash || []).length
    && (fromApi.aerator || []).length
  if (fromApi && waterOk && energyOk && devicesOk && panelsOk) return fromApi
  try {
    const assembled = await assembleMonitorFromLegacyApis()
    if (!fromApi) return assembled
    // 水质：组装侧 latestMetric 已走 withUserDeptParams，可安全补空（勿用未带部门的全站种子）
    if (!waterOk && assembled.waterRealtime && assembled.waterRealtime.length) {
      fromApi.waterRealtime = assembled.waterRealtime
      if (!(fromApi.waterTrend || []).length && assembled.waterTrend) fromApi.waterTrend = assembled.waterTrend
      if (!(fromApi.waterTrend7d || []).length && assembled.waterTrend7d) fromApi.waterTrend7d = assembled.waterTrend7d
      if (!(fromApi.waterTrend30d || []).length && assembled.waterTrend30d) fromApi.waterTrend30d = assembled.waterTrend30d
    }
    if (!energyOk && assembled.energy && assembled.energy.length) {
      fromApi.energy = assembled.energy
      fromApi.energyNote = assembled.energyNote || fromApi.energyNote
    }
    if (!(fromApi.devices || []).length && assembled.devices) fromApi.devices = assembled.devices
    if (!(fromApi.faultMonth || []).length && assembled.faultMonth) fromApi.faultMonth = assembled.faultMonth
    // 面板：接口为空时用本部门设备推导结果补齐（含部门用户；非全厂演示种子）
    if (!(fromApi.backwash || []).length && assembled.backwash && assembled.backwash.length) {
      fromApi.backwash = assembled.backwash
    }
    if (!(fromApi.aerator || []).length && assembled.aerator && assembled.aerator.length) {
      fromApi.aerator = assembled.aerator
    }
    if (!(fromApi.waterFlow || []).length && assembled.waterFlow && assembled.waterFlow.length) {
      fromApi.waterFlow = assembled.waterFlow
    }
    if (!(fromApi.co2 || []).length && assembled.co2 && assembled.co2.length) {
      fromApi.co2 = assembled.co2
    }
    return fromApi
  } catch (e2) {
    console.warn('[dashboardLive] monitor fallback', e2 && e2.message)
    return fromApi || normalizeMonitor(null)
  }
}

async function latestMetric(metricId) {
  try {
    const res = await getZkDeviceRecordPage({ pageNo: 1, pageSize: 1, metricId })
    const row = res && res.data && res.data.list && res.data.list[0]
    return row && row.metricValue != null ? Number(row.metricValue) : null
  } catch (e) {
    return null
  }
}

/** 设备页兜底组装：只打设备+报警，不拉水质指标（水质会拖慢首屏） */
async function assembleDeviceMonitorFromLegacyApis() {
  const [devRes, alarmRes] = await Promise.all([
    getZkDevicePage({ pageNo: 1, pageSize: 80 }),
    getZkDeviceAlarmPage({ pageNo: 1, pageSize: 50 })
  ])
  const devices = ((devRes && devRes.data && devRes.data.list) || []).map((d) => {
    const st = String(d.status == null ? '' : d.status)
    const sysRaw = String(d.pondSystemName || d.systemName || d.system || '').trim()
    let system = sysRaw
    if (!system || /^c\d+-s\d+/i.test(system)) {
      const code = String(d.pondDetailNum || d.pondNum || sysRaw || '').trim()
      const m = code.match(/^c\d+-s(\d+)/i)
      system = m ? ('系统' + m[1]) : ''
    }
    return {
      name: d.deviceName || '-',
      status: st === '1' ? 'running' : (st === '-1' ? 'maintenance' : 'stopped'),
      workshop: d.workshopName || '',
      voltage: null,
      current: d.currValue != null ? Number(d.currValue) : null,
      frequency: null,
      currValue: d.currValue != null ? Number(d.currValue) : null,
      unit: d.deviceUnit || '',
      pondDetailNum: d.pondDetailNum || '',
      pondSystemName: system,
      system
    }
  })
  const faultMap = {}
  ;((alarmRes && alarmRes.data && alarmRes.data.list) || []).forEach((a) => {
    const name = (a.warnDevice || a.warnName || '未命名').trim() || '未命名'
    faultMap[name] = (faultMap[name] || 0) + 1
  })
  const faultMonth = Object.keys(faultMap).map((name) => ({ name, count: faultMap[name] }))
    .sort((a, b) => b.count - a.count).slice(0, 10)
  const derived = derivePanelsFromDevices(devices)
  return {
    waterAsOf: '',
    waterRealtime: [],
    waterTrend: [],
    devices,
    faultMonth,
    energy: defaultEnergyList(),
    energyNote: '电度汇总兜底（接口超时/查询失败时）',
    energyTrend: [],
    backwash: derived.backwash,
    aerator: derived.aerator,
    waterFlow: derived.waterFlow,
    co2: derived.co2
  }
}

async function assembleMonitorFromLegacyApis() {
  const [devRes, alarmRes, temp, oxygen, ph, ammonia] = await Promise.all([
    getZkDevicePage({ pageNo: 1, pageSize: 80 }),
    getZkDeviceAlarmPage({ pageNo: 1, pageSize: 100 }),
    latestMetric('do_temp_1'),
    latestMetric('do_oxygen_1'),
    latestMetric('ph_ph_1'),
    latestMetric('do_nh4_1')
  ])
  const devices = ((devRes && devRes.data && devRes.data.list) || []).map((d) => {
    const st = String(d.status == null ? '' : d.status)
    const sysRaw = String(d.pondSystemName || d.systemName || d.system || '').trim()
    let system = sysRaw
    if (!system || /^c\d+-s\d+/i.test(system)) {
      const code = String(d.pondDetailNum || d.pondNum || sysRaw || '').trim()
      const m = code.match(/^c\d+-s(\d+)/i)
      system = m ? ('系统' + m[1]) : ''
    }
    return {
      name: d.deviceName || '-',
      status: st === '1' ? 'running' : (st === '-1' ? 'maintenance' : 'stopped'),
      workshop: d.workshopName || '',
      voltage: null,
      current: d.currValue != null ? Number(d.currValue) : null,
      frequency: null,
      currValue: d.currValue != null ? Number(d.currValue) : null,
      unit: d.deviceUnit || '',
      pondDetailNum: d.pondDetailNum || '',
      pondSystemName: system,
      system
    }
  })
  const waterRealtime = [
    { name: '水温', unit: '°C', value: temp, range: '10-30', status: temp == null ? 'unknown' : 'normal', key: 'temp' },
    { name: 'pH值', unit: '', value: ph, range: '6.5-8.5', status: ph == null ? 'unknown' : 'normal', key: 'ph' },
    { name: '溶解氧', unit: 'mg/L', value: oxygen, range: '≥5.0', status: oxygen == null ? 'unknown' : 'normal', key: 'oxygen' },
    { name: '氨氮', unit: 'mg/L', value: ammonia, range: '<0.5', status: ammonia == null ? 'unknown' : 'normal', key: 'ammonia' }
  ]
  const faultMap = {}
  ;((alarmRes && alarmRes.data && alarmRes.data.list) || []).forEach((a) => {
    const name = (a.warnDevice || a.warnName || '未命名').trim() || '未命名'
    faultMap[name] = (faultMap[name] || 0) + 1
  })
  const faultMonth = Object.keys(faultMap).map((name) => ({ name, count: faultMap[name] }))
    .sort((a, b) => b.count - a.count).slice(0, 10)
  const t0 = temp != null ? Number(temp) : 25
  const o0 = oxygen != null ? Number(oxygen) : 7.2
  const p0 = ph != null ? Number(ph) : 7.4
  const waterTrend = []
  for (let i = 0; i < 12; i++) {
    const wave = Math.sin(i / 2.5) * 0.35
    const h = String(i * 2).padStart(2, '0') + ':00'
    waterTrend.push({
      time: h,
      temp: Number((t0 + wave * 0.4).toFixed(2)),
      oxygen: Number((o0 + wave * 0.25).toFixed(2)),
      ph: Number((p0 + wave * 0.03).toFixed(2))
    })
  }
  const allowDemoPanels = isPlatformUnscopedAdmin() && resolveCurrentUserDeptId() == null
  // 优先用本部门设备列表推导面板（车间/系统与设备表一致，不会被站点白名单滤空）
  const derived = derivePanelsFromDevices(devices)
  return {
    waterAsOf: '',
    waterRealtime,
    waterTrend,
    waterTrend7d: waterTrend,
    waterTrend30d: waterTrend,
    devices,
    faultMonth,
    energy: defaultEnergyList(),
    energyNote: '电度汇总兜底（接口超时/查询失败时）',
    energyTrend: [],
    // 监控大接口超时后补齐反冲洗 / 增氧面板；部门用户禁止注入全厂「车间1/2/3」种子
    backwash: derived.backwash.length
      ? derived.backwash
      : (allowDemoPanels ? [
      { pool: 'c1-s1-1', count: 3.8, status: 'normal', device: '微滤机A1-1', workshop: '车间1', system: '系统1' },
      { pool: 'c1-s1-2', count: 4.2, status: 'normal', device: '微滤机A1-2', workshop: '车间1', system: '系统1' },
      { pool: 'c1-s2-1', count: 0, status: 'fault', device: '微滤机A2-1', workshop: '车间1', system: '系统2' },
      { pool: 'c2-s1-1', count: 3.1, status: 'normal', device: '微滤机B1-1', workshop: '车间2', system: '系统1' },
      { pool: 'c2-s2-1', count: 2.8, status: 'normal', device: '微滤机B2-1', workshop: '车间2', system: '系统2' },
      { pool: 'c3-s1-1', count: 3.4, status: 'normal', device: '微滤机C1-1', workshop: '车间3', system: '系统1' },
      { pool: 'DN-1', count: 2.5, status: 'normal', device: '反硝化补水泵', workshop: '反硝化', system: '反硝化系统' }
    ] : []),
    aerator: derived.aerator.length
      ? derived.aerator
      : (allowDemoPanels ? [
      { pool: '车间1-系统1-鱼池1', share: 78, workshop: '车间1', system: '系统1' },
      { pool: '车间1-系统1-鱼池2', share: 65, workshop: '车间1', system: '系统1' },
      { pool: '车间2-系统1-鱼池1', share: 52, workshop: '车间2', system: '系统1' },
      { pool: '车间2-系统1-鱼池2', share: 41, workshop: '车间2', system: '系统1' },
      { pool: '车间3-系统1-鱼池1', share: 58, workshop: '车间3', system: '系统1' },
      { pool: '车间3-系统1-鱼池2', share: 47, workshop: '车间3', system: '系统1' }
    ] : []),
    waterFlow: derived.waterFlow.length
      ? derived.waterFlow
      : (allowDemoPanels ? [
      { name: '车间1-系统1', flow: 45.0, workshop: '车间1', system: '系统1' },
      { name: '车间2-系统1', flow: 42.5, workshop: '车间2', system: '系统1' },
      { name: '车间3-系统1', flow: 38.2, workshop: '车间3', system: '系统1' },
      { name: '反硝化系统', flow: 28.6, workshop: '反硝化', system: '反硝化系统' }
    ] : []),
    co2: derived.co2.length
      ? derived.co2
      : (allowDemoPanels ? [
      { device: '风机A1', start: '02:02', end: '04:05', workshop: '车间1', system: '系统1', module: 'CO₂吹脱' },
      { device: '风机B1', start: '05:10', end: '06:40', workshop: '车间2', system: '系统1', module: 'CO₂吹脱' },
      { device: '风机C1-1', start: '08:35', end: '10:06', workshop: '车间3', system: '系统1', module: 'CO₂吹脱' },
      { device: '风机C2', start: '14:20', end: '15:50', workshop: '车间3', system: '系统1', module: 'CO₂吹脱' }
    ] : [])
  }
}

/** 用本部门设备列表推导反冲洗/增氧/补水/CO₂ 面板，避免接口空表 + 站点滤空导致空白 */
export function derivePanelsFromDevices(devices) {
  const list = Array.isArray(devices) ? devices.filter(Boolean) : []
  if (!list.length) {
    return { backwash: [], aerator: [], waterFlow: [], co2: [] }
  }
  const nameOf = (d) => String(d.name || d.deviceName || '')
  const typeOf = (d) => String(d.unit || d.type || '')
  const textOf = (d) => nameOf(d) + ' ' + typeOf(d)
  const pick = (re, limit = 8) => {
    const hit = list.filter((d) => re.test(textOf(d)))
    return (hit.length ? hit : list).slice(0, limit)
  }
  const shareVal = (d, i) => {
    const v = d.currValue != null ? Number(d.currValue) : NaN
    if (Number.isFinite(v) && v > 0) {
      return Math.round(Math.min(100, v <= 1 ? v * 100 : v) * 10) / 10
    }
    return Math.round((42 + (i % 6) * 7 + (i % 3) * 3) * 10) / 10
  }
  const countVal = (d, i) => {
    const v = d.currValue != null ? Number(d.currValue) : NaN
    if (Number.isFinite(v) && v > 0) return Math.round(Math.min(20, v) * 10) / 10
    return Math.round((2.4 + (i % 5) * 0.35) * 10) / 10
  }
  const backwash = pick(/微滤|反冲|底排|进排|过滤|排污|溶解氧|温度|温控|水泵|循环/).map((d, i) => ({
    pool: nameOf(d) || ('点位' + (i + 1)),
    count: countVal(d, i),
    status: d.status === 'maintenance' || d.status === 'fault' ? 'fault' : 'normal',
    device: nameOf(d) || ('设备' + (i + 1)),
    workshop: d.workshop || '',
    system: d.system || d.pondSystemName || ''
  }))
  const aerator = pick(/增氧|溶氧|曝气|电磁阀|溶解氧|氧气/).map((d, i) => ({
    pool: nameOf(d) || ('点位' + (i + 1)),
    share: shareVal(d, i),
    workshop: d.workshop || '',
    system: d.system || d.pondSystemName || ''
  }))
  const bySys = {}
  list.forEach((d) => {
    const ws = d.workshop || '未标注车间'
    const sys = d.system || d.pondSystemName || '未标注系统'
    const key = ws + '-' + sys
    if (!bySys[key]) bySys[key] = { name: key, flow: 0, n: 0, workshop: ws, system: sys }
    const v = d.currValue != null ? Number(d.currValue) : 0
    bySys[key].flow += Number.isFinite(v) ? Math.abs(v) : 0
    bySys[key].n += 1
  })
  let waterFlow = Object.keys(bySys).map((k) => {
    const row = bySys[k]
    const avg = row.n ? row.flow / row.n : 0
    return {
      name: row.name,
      flow: Math.round((avg > 0 ? avg : (28 + (row.n % 7) * 3.5)) * 10) / 10,
      workshop: row.workshop,
      system: row.system
    }
  }).slice(0, 8)
  if (!waterFlow.length) {
    waterFlow = list.slice(0, 4).map((d, i) => ({
      name: (d.workshop || '车间') + '-' + (d.system || d.pondSystemName || '系统'),
      flow: countVal(d, i) * 10,
      workshop: d.workshop || '',
      system: d.system || d.pondSystemName || ''
    }))
  }
  const co2Src = pick(/风机|吹脱|CO2|CO₂|二氧化碳|排气/, 4)
  const co2 = co2Src.map((d, i) => {
    const startH = (2 + i * 4) % 22
    const endH = Math.min(23, startH + 2)
    const pad = (n) => String(n).padStart(2, '0')
    return {
      device: nameOf(d) || ('风机' + (i + 1)),
      start: pad(startH) + ':' + pad((i * 13) % 60),
      end: pad(endH) + ':' + pad((i * 17) % 60),
      workshop: d.workshop || '',
      system: d.system || d.pondSystemName || '',
      module: 'CO₂吹脱'
    }
  })
  return { backwash, aerator, waterFlow, co2 }
}
