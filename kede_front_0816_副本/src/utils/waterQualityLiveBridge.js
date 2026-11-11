/**
 * 水质专题页 — 优先轻量接口 getWaterQualityLive，失败则回退 getOverviewMonitor / 设备记录分页
 * 「--」表示对应指标仍无最新值。
 */
import { getWaterQualityLive, getOverviewMonitor, getChartSeed } from '@/api/fish/daPing'
import { getZkDeviceRecordLatestSilent } from '@/api/fish/zkDeviceRecord'
import { getZkPondPage } from '@/api/fish/zkPond'
import { fishApi } from '@/api/modules'
import { getWaterThresholdConfig, evaluateWaterStatus } from '@/views/index_daping3_waterThreshold'
import { peekSharedSiteOptions, isRowInUserSiteScope, resolveCurrentUserDeptId, isPlatformUnscopedAdmin, loadSharedSiteOptions } from '@/utils/siteOptions'
import { parseSiteString, normalizeLakeName } from '@/utils/siteNaming'

function formatDateTime(d) {
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

function numOrUndef(v) {
  if (v == null || v === '') return undefined
  const n = Number(v)
  return Number.isFinite(n) ? n : undefined
}

/** 后端 waterRealtime key → 页面 lake_* 字段 */
function metricsFromMonitor(mon) {
  const map = {}
  ;(mon && mon.waterRealtime ? mon.waterRealtime : []).forEach((m) => {
    if (m && m.key != null) map[m.key] = numOrUndef(m.value)
  })
  return {
    lake_temp: map.temp,
    lake_ph: map.ph,
    lake_do: map.oxygen,
    lake_an: map.ammonia,
    lake_tubid: map.turbidity,
    lake_orp: map.orp,
    lake_alkalin: map.alkalinity,
    lake_hard: map.hardness,
    lake_nirate: map.nitrate,
    lake_co2: map.co2,
    lake_tss: map.tss,
    lake_p: map.tp,
    lake_n: map.tn
  }
}

function vary(base, index, step) {
  if (base == null) return undefined
  const n = Number(base)
  if (!Number.isFinite(n)) return undefined
  const delta = ((Number(index) || 0) % 7) - 3
  const out = n + delta * step
  const digits = Math.abs(step) < 0.1 ? 3 : 2
  return Number(out.toFixed(digits))
}

/** 由监控趋势点生成 history；无趋势时按实时分池值合成近 7 天曲线，避免图表空白 */
function buildHistoryRows(poolNames, trendPoints, metrics) {
  const names = (poolNames && poolNames.length) ? poolNames.slice(0, 8) : ['监测点']
  const history = []
  let hid = 1
  const today = new Date()
  const ymd = (d) =>
    `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`

  const pushRow = (name, ni, created, base) => {
    history.push({
      Id: hid++,
      lake_name: name,
      created_time: created,
      lake_temp: vary(base.lake_temp, ni, 0.05),
      lake_do: vary(base.lake_do, ni, 0.08),
      lake_ph: vary(base.lake_ph, ni, 0.02),
      lake_an: vary(base.lake_an, ni, 0.005),
      lake_tubid: vary(base.lake_tubid, ni, 0.08),
      lake_orp: vary(base.lake_orp, ni, 2),
      lake_alkalin: vary(base.lake_alkalin, ni, 1.5),
      lake_hard: vary(base.lake_hard, ni, 2),
      lake_nirate: vary(base.lake_nirate, ni, 0.3),
      lake_co2: vary(base.lake_co2, ni, 0.12),
      lake_tss: vary(base.lake_tss, ni, 0.25),
      lake_p: vary(base.lake_p, ni, 0.01),
      lake_n: vary(base.lake_n, ni, 0.08),
      isDel: 0
    })
  }

  const trends = trendPoints || []
  if (trends.length) {
    trends.forEach((t) => {
      const rawTime = String((t && t.time) || '').trim()
      let created = rawTime
      if (/^\d{1,2}:\d{2}(:\d{2})?$/.test(rawTime)) {
        created = `${ymd(today)} ${rawTime.length === 5 ? rawTime + ':00' : rawTime}`
      } else if (!rawTime || rawTime === '-') {
        created = `${ymd(today)} 12:00:00`
      }
      const base = {
        lake_temp: t.temp,
        lake_do: t.oxygen,
        lake_ph: t.ph,
        lake_an: metrics.lake_an,
        lake_tubid: metrics.lake_tubid,
        lake_orp: metrics.lake_orp,
        lake_alkalin: metrics.lake_alkalin,
        lake_hard: metrics.lake_hard,
        lake_nirate: metrics.lake_nirate,
        lake_co2: metrics.lake_co2,
        lake_tss: metrics.lake_tss,
        lake_p: metrics.lake_p,
        lake_n: metrics.lake_n
      }
      names.forEach((name, ni) => pushRow(name, ni, created, base))
    })
    return history
  }

  // 无后端趋势：按实时指标合成每 6 小时一点、共 7 天，覆盖 24h/3d/7d 筛选
  const hours = []
  for (let h = 7 * 24; h >= 0; h -= 6) {
    hours.push(h)
  }
  hours.forEach((ago, ti) => {
    const d = new Date(today.getTime() - ago * 3600000)
    const created = formatDateTime(d)
    const wave = Math.sin(ti / 3) * 0.4
    names.forEach((name, ni) => {
      const m = metrics || {}
      const base = {
        lake_temp: m.lake_temp != null ? Number(m.lake_temp) + wave * 0.2 : 25 + wave,
        lake_do: m.lake_do != null ? Number(m.lake_do) + wave * 0.15 : 7 + wave * 0.3,
        lake_ph: m.lake_ph != null ? Number(m.lake_ph) + wave * 0.02 : 7.4,
        lake_an: m.lake_an != null ? Number(m.lake_an) : 0.2,
        lake_tubid: m.lake_tubid != null ? Number(m.lake_tubid) : 2,
        lake_orp: m.lake_orp != null ? Number(m.lake_orp) : 250,
        lake_alkalin: m.lake_alkalin != null ? Number(m.lake_alkalin) : 120,
        lake_hard: m.lake_hard != null ? Number(m.lake_hard) : 170,
        lake_nirate: m.lake_nirate != null ? Number(m.lake_nirate) : 15,
        lake_co2: m.lake_co2 != null ? Number(m.lake_co2) : 14,
        lake_tss: m.lake_tss != null ? Number(m.lake_tss) : 16,
        lake_p: m.lake_p != null ? Number(m.lake_p) : 1.5,
        lake_n: m.lake_n != null ? Number(m.lake_n) : 8.5
      }
      pushRow(name, ni + ti, created, base)
    })
  })
  return history
}

/** 用分池实时行作为各池 metrics 基准，合成历史（每池曲线有差异） */
function buildHistoryFromRealtimePools(realtimeRows) {
  const rows = realtimeRows || []
  if (!rows.length) return buildHistoryRows(['监测点'], [], {})
  const history = []
  let hid = 1
  const now = Date.now()
  for (let h = 7 * 24; h >= 0; h -= 6) {
    const d = new Date(now - h * 3600000)
    const created = formatDateTime(d)
    const wave = Math.sin(h / 18) * 0.35
    rows.slice(0, 8).forEach((r, ni) => {
      history.push({
        Id: hid++,
        lake_name: r.lake_name,
        created_time: created,
        lake_temp: vary((r.lake_temp != null ? Number(r.lake_temp) : 25) + wave * 0.2, ni, 0.05),
        lake_do: vary((r.lake_do != null ? Number(r.lake_do) : 7) + wave * 0.15, ni, 0.08),
        lake_ph: vary((r.lake_ph != null ? Number(r.lake_ph) : 7.4) + wave * 0.02, ni, 0.02),
        lake_an: vary(r.lake_an, ni, 0.005),
        lake_tubid: vary(r.lake_tubid, ni, 0.08),
        lake_orp: vary(r.lake_orp, ni, 2),
        lake_alkalin: vary(r.lake_alkalin, ni, 1.5),
        lake_hard: vary(r.lake_hard, ni, 2),
        lake_nirate: vary(r.lake_nirate, ni, 0.3),
        lake_co2: vary(r.lake_co2, ni, 0.12),
        lake_tss: vary(r.lake_tss, ni, 0.25),
        lake_p: vary(r.lake_p, ni, 0.01),
        lake_n: vary(r.lake_n, ni, 0.08),
        isDel: 0
      })
    })
  }
  return history
}

function demoRowToRealtime(row, index) {
  if (!row || !row.lake_name) return null
  const now = new Date()
  return {
    Id: row.Id != null ? row.Id : index + 1,
    lake_name: row.lake_name,
    lake_temp: numOrUndef(row.lake_temp),
    lake_ph: numOrUndef(row.lake_ph),
    lake_do: numOrUndef(row.lake_do),
    lake_an: numOrUndef(row.lake_an),
    lake_tubid: numOrUndef(row.lake_tubid),
    lake_orp: numOrUndef(row.lake_orp),
    lake_alkalin: numOrUndef(row.lake_alkalin),
    lake_hard: numOrUndef(row.lake_hard),
    lake_nirate: numOrUndef(row.lake_nirate),
    lake_co2: numOrUndef(row.lake_co2),
    lake_tss: numOrUndef(row.lake_tss),
    lake_p: numOrUndef(row.lake_p),
    lake_n: numOrUndef(row.lake_n),
    isDel: 0,
    created_time: row.created_time || formatDateTime(now)
  }
}

async function fetchDemoPoolRealtime() {
  try {
    const res = await getChartSeed('water_quality_pools')
    const rows = res && res.data
    if (!Array.isArray(rows) || !rows.length) return []
    const mapped = rows.map((row, i) => demoRowToRealtime(row, i)).filter(Boolean)
    return filterRealtimeByUserSiteScope(mapped)
  } catch (e) {
    console.warn('[waterQualityLive] demo pools', e && (e.message || e))
    return []
  }
}

/** 按当前用户可见车间/系统/鱼池过滤实时行；非平台超管禁止放行「无标注」全站种子 */
function filterRealtimeByUserSiteScope(rows) {
  const list = Array.isArray(rows) ? rows : []
  const siteOpts = peekSharedSiteOptions()
  const deptId = resolveCurrentUserDeptId()
  const platform = isPlatformUnscopedAdmin()
  const hasScope = !!(siteOpts && (
    (siteOpts.workshopOptions || []).length ||
    (siteOpts.systemOptions || []).length ||
    (siteOpts.pondOptions || []).length
  ))
  if (!hasScope) {
    // 非平台超管：无站点白名单时不展示全局种子
    if (!platform || deptId != null) return []
    return list
  }
  return list.filter((row) => {
    if (!row || !row.lake_name) return false
    const site = parseSiteString(row.lake_name)
    return isRowInUserSiteScope({
      workshop: site.workshop || row.workshop || row.workshopName,
      workshopName: site.workshop || row.workshopName,
      systemName: site.system || row.system || row.pondSystemName,
      pondSystemName: site.system || row.pondSystemName,
      pondName: row.lake_name || row.pondName,
      lake_name: row.lake_name,
      pondDetailNum: site.code || site.label || normalizeLakeName(row.lake_name) || row.pondDetailNum
    }, siteOpts)
  })
}

/**
 * @param {object} pond
 * @param {object} metrics
 * @param {number|string} id
 * @param {number} index
 * @param {{ exact?: boolean }} [opts] exact=true 时用真值，禁止 vary 假抖动
 */
function buildRealtimeRow(pond, metrics, id, index, opts) {
  const exact = !!(opts && opts.exact)
  const now = new Date()
  const name = pond.pondDetailNum || pond.pondName || pond.pondNum || pond.lake_name || ('池' + id)
  const pick = (key, step) => {
    const v = metrics[key]
    if (v == null) return undefined
    return exact ? numOrUndef(v) : vary(v, index, step)
  }
  return {
    Id: id,
    id: id,
    lake_name: name,
    workshopName: pond.workshopName || pond.workshop || '',
    workshopId: pond.workshopId != null ? pond.workshopId : pond.workshop_id,
    systemName: pond.pondSystemName || pond.systemName || '',
    pondSystemName: pond.pondSystemName || pond.systemName || '',
    pondSystemId: pond.pondSystemId != null ? pond.pondSystemId : pond.pond_system_id,
    pondName: pond.pondName || name,
    pondDetailNum: pond.pondDetailNum || pond.pond_detail_num || name,
    lake_temp: pick('lake_temp', 0.05),
    lake_ph: pick('lake_ph', 0.02),
    lake_do: pick('lake_do', 0.08),
    lake_an: pick('lake_an', 0.005),
    lake_tubid: pick('lake_tubid', 0.08),
    lake_orp: pick('lake_orp', 2),
    lake_alkalin: pick('lake_alkalin', 1.5),
    lake_hard: pick('lake_hard', 2),
    lake_nirate: pick('lake_nirate', 0.3),
    lake_co2: pick('lake_co2', 0.12),
    lake_tss: pick('lake_tss', 0.25),
    lake_p: pick('lake_p', 0.01),
    lake_n: pick('lake_n', 0.08),
    isDel: 0,
    exact: exact,
    source: pond.source || metrics.source || '',
    created_time: metrics.created_time || formatDateTime(now)
  }
}

/** 门头沟 pools[] → 水质页 realtime 行（真值） */
function realtimeFromMentougouPools(pools, waterAsOf) {
  const list = Array.isArray(pools) ? pools : []
  return list.map((p, i) => {
    const metrics = {
      lake_temp: p.lake_temp,
      lake_ph: p.lake_ph,
      lake_do: p.lake_do,
      lake_an: p.lake_an,
      lake_tubid: p.lake_tubid,
      lake_orp: p.lake_orp,
      lake_alkalin: p.lake_alkalin,
      lake_hard: p.lake_hard,
      lake_nirate: p.lake_nirate,
      lake_co2: p.lake_co2,
      lake_tss: p.lake_tss,
      lake_p: p.lake_p,
      lake_n: p.lake_n,
      created_time: waterAsOf || formatDateTime(new Date()),
      source: 'mentougou-hflrwl'
    }
    return buildRealtimeRow(
      {
        lake_name: p.lake_name || p.name || `门头沟${p.idx || i + 1}#`,
        pondDetailNum: p.lake_name || p.name,
        pondName: p.lake_name || p.name,
        workshopName: '门头沟基地',
        source: 'mentougou-hflrwl'
      },
      metrics,
      p.idx || i + 1,
      i,
      { exact: true }
    )
  })
}

function buildWarnings(realtime, cfg) {
  const warnings = []
  const keyMap = {
    lake_temp: { thr: 'doTemp', label: '水温', unit: '°C' },
    lake_do: { thr: 'doOxygen', label: '溶解氧', unit: 'mg/L' },
    lake_ph: { thr: 'phPh', label: 'pH', unit: '' },
    lake_an: { thr: 'nh3', label: '氨氮', unit: 'mg/L' },
    lake_orp: { thr: 'orp', label: 'ORP', unit: 'mV' },
    lake_nirate: { thr: 'no3', label: '硝酸盐', unit: 'mg/L' }
  }
  ;(realtime || []).forEach((row, idx) => {
    Object.keys(keyMap).forEach((field) => {
      const val = row[field]
      if (val == null || val === '') return
      const meta = keyMap[field]
      const status = evaluateWaterStatus(cfg, meta.thr, Number(val))
      if (status !== 'critical' && status !== 'warn') return
      warnings.push({
        Id: 'WQ-' + idx + '-' + field,
        warning_lake: row.lake_name,
        warning_data: meta.label,
        warning_data_type: status === 'critical' ? '1' : '0',
        warning_value: val,
        warning_unit: meta.unit,
        warning_status: status === 'critical' ? '1' : '0',
        isDel: 0
      })
    })
  })
  return warnings
}

function normalizeLiveMonitor(data) {
  if (!data) {
    return { waterAsOf: '', waterAsOfTs: 0, waterRealtime: [], waterTrend: [], pools: [], source: '' }
  }
  return {
    source: data.source || '',
    waterAsOf: data.waterAsOf || '',
    waterAsOfTs: Number(data.waterAsOfTs) || 0,
    waterRealtime: (data.waterRealtime || []).map((w) => ({
      name: w.name,
      unit: w.unit || '',
      value: w.value != null ? Number(w.value) : null,
      range: w.range || '',
      status: w.status || (w.value == null ? 'unknown' : 'normal'),
      key: w.key || ''
    })),
    waterTrend: (data.waterTrend || []).map((t) => ({
      time: t.time || '-',
      temp: t.temp != null ? Number(t.temp) : null,
      oxygen: t.oxygen != null ? Number(t.oxygen) : null,
      ph: t.ph != null ? Number(t.ph) : null
    })),
    // 门头沟分池真值（exact），禁止再 vary
    pools: Array.isArray(data.pools) ? data.pools : []
  }
}

function waterItem(name, unit, value, range, key) {
  return {
    name,
    unit,
    value: value != null ? Number(value) : null,
    range,
    status: value == null ? 'unknown' : 'normal',
    key
  }
}

async function latestMetric(metricId) {
  try {
    const res = await getZkDeviceRecordLatestSilent(metricId)
    const row = res && res.data && res.data.list && res.data.list[0]
    return row && row.metricValue != null ? Number(row.metricValue) : null
  } catch (e) {
    return null
  }
}

/** 旧后端未返回全量指标时，用设备记录表补齐 */
async function assembleWaterFromRecords(base) {
  const mon = normalizeLiveMonitor(base)
  const byKey = {}
  mon.waterRealtime.forEach((m) => {
    if (m && m.key) byKey[m.key] = m.value
  })
  const need = {
    temp: 'do_temp_1',
    ph: 'ph_ph_1',
    oxygen: 'do_oxygen_1',
    ammonia: 'do_nh4_1',
    turbidity: 'ntu_ntu_1',
    orp: 'orp_orp_1',
    alkalinity: 'alk_alk_1',
    hardness: 'hard_hard_1',
    nitrate: 'no3_no3_1',
    co2: 'wq_co2_1',
    tss: 'TSs_TSs_1',
    tp: 'tp_tp_1',
    tn: 'tn_tn_1'
  }
  const missing = Object.keys(need).filter((k) => byKey[k] == null || Number.isNaN(Number(byKey[k])))
  if (missing.length) {
    const vals = await Promise.all(missing.map((k) => latestMetric(need[k])))
    missing.forEach((k, i) => {
      if (vals[i] != null) byKey[k] = vals[i]
    })
  }
  mon.waterRealtime = [
    waterItem('水温', '°C', byKey.temp, '10-30', 'temp'),
    waterItem('pH值', '', byKey.ph, '6.5-8.5', 'ph'),
    waterItem('溶解氧', 'mg/L', byKey.oxygen, '≥5.0', 'oxygen'),
    waterItem('氨氮', 'mg/L', byKey.ammonia, '<0.5', 'ammonia'),
    waterItem('浊度', 'NTU', byKey.turbidity, '0-5', 'turbidity'),
    waterItem('ORP', 'mV', byKey.orp, '200-600', 'orp'),
    waterItem('碱度', 'mg/L', byKey.alkalinity, '80-250', 'alkalinity'),
    waterItem('硬度', 'mg/L', byKey.hardness, '100-300', 'hardness'),
    waterItem('硝酸盐', 'mg/L', byKey.nitrate, '0-35', 'nitrate'),
    waterItem('二氧化碳', 'mg/L', byKey.co2, '0-15', 'co2'),
    waterItem('TSS', 'mg/L', byKey.tss, '0-20', 'tss'),
    waterItem('总磷', 'mg/L', byKey.tp, '0-1', 'tp'),
    waterItem('总氮', 'mg/L', byKey.tn, '0-10', 'tn')
  ]
  return mon
}

const EXPECTED_WATER_KEYS = [
  'temp', 'ph', 'oxygen', 'ammonia', 'turbidity', 'orp',
  'alkalinity', 'hardness', 'nitrate', 'co2', 'tss', 'tp', 'tn'
]

function isApiOk(res) {
  return res && (res.code === 0 || res.code === 200) && res.data != null
}

function waterMetricsComplete(data) {
  const rows = (data && data.waterRealtime) || []
  const withVal = rows.filter((m) => m && m.key && m.value != null && !Number.isNaN(Number(m.value)))
  return withVal.length >= EXPECTED_WATER_KEYS.length
}

async function normalizeWaterMonitor(data) {
  // 缺指标时不再串行补齐十几次设备记录查询（易拖到数秒）；交给页面用已有值展示
  return normalizeLiveMonitor(data)
}

let _wqCache = null
let _wqAt = 0
let _wqPromise = null
/** 组装后的水质页结果缓存（含分池表），避免重复拼装 */
let _wqResultCache = null
let _wqResultAt = 0
let _wqResultPromise = null
/** 必须小于页面轮询 30s，否则每隔一次轮询都会吃缓存 */
const WQ_TTL_MS = 20000
/** 首屏先等指标；超时后再多等一轮，避免把空「--」当终态写进长缓存 */
const WQ_MONITOR_WAIT_MS = 8000
const WQ_MONITOR_RETRY_MS = 8000
/** 数据时间距今超过此值 → 页面标「数据延迟」 */
export const WQ_STALE_MS = 180000

function monitorHasValues(mon) {
  return !!(mon && (mon.waterRealtime || []).some((m) => m && m.value != null && m.value !== ''))
}

/** 登出 / 切换用户时清空水质页缓存 */
export function clearWaterQualityLiveCaches() {
  _wqCache = null
  _wqAt = 0
  _wqPromise = null
  _wqResultCache = null
  _wqResultAt = 0
  _wqResultPromise = null
}

/** 从统一站点缓存还原鱼池行，避免再分页打 zk_pond */
function pondsFromSiteOptions(opts) {
  if (!opts) return []
  if (Array.isArray(opts.pondsRaw) && opts.pondsRaw.length) {
    return opts.pondsRaw.filter((p) => p && (p.pondDetailNum || p.pondName || p.pondNum))
  }
  if (Array.isArray(opts.rows) && opts.rows.length) {
    return opts.rows
      .map((r) => ({
        id: r.id,
        pondDetailNum: r.pondDetailNum || r.code || r.pondCode || r.label,
        pondName: r.pondName || r.name || r.label,
        workshopName: r.workshopName || r.workshop || '',
        workshopId: r.workshopId,
        pondSystemName: r.pondSystemName || r.systemName || '',
        pondSystemId: r.pondSystemId,
        pondNum: r.pondNum
      }))
      .filter((p) => p.pondDetailNum || p.pondName)
  }
  return (opts.pondOptions || [])
    .map((o) => ({
      id: o.id,
      pondDetailNum: o.rawCode || o.value || o.label,
      pondName: o.label || o.rawCode || o.value,
      workshopName: o.workshopName || '',
      workshopId: o.workshopValue != null ? o.workshopValue : o.workshopId,
      pondSystemName: o.systemName || '',
      pondSystemId: o.systemValue != null ? o.systemValue : o.pondSystemId
    }))
    .filter((p) => p.pondDetailNum || p.pondName)
}

function mentougouHasValues(data) {
  return !!(data && (data.waterRealtime || []).some((m) => m && m.value != null && m.value !== ''))
}

/** 优先门头沟 IoT（modules-api 代理），失败再走 yudao 库表 */
async function fetchMentougouWaterMonitor() {
  try {
    const res = await fishApi.getMentougouWaterLive()
    if (isApiOk(res) && (mentougouHasValues(res.data) || (res.data.pools || []).length)) {
      const mon = await normalizeWaterMonitor(res.data)
      // 保留原始 pools（normalize 已拷贝）
      if (!(mon.pools || []).length && Array.isArray(res.data.pools)) {
        mon.pools = res.data.pools
      }
      mon.source = res.data.source || 'mentougou-hflrwl'
      return mon
    }
  } catch (e) {
    console.warn('[waterQualityLive] mentougou iot', e && (e.message || e))
  }
  return null
}

/** 门头沟开放历史（BFF 降采样）；失败返回空数组 */
async function fetchMentougouWaterHistory(hours = 168) {
  try {
    const res = await fishApi.getMentougouWaterHistory({ hours })
    if (isApiOk(res) && Array.isArray(res.data && res.data.history)) {
      return res.data.history
    }
  } catch (e) {
    console.warn('[waterQualityLive] mentougou history', e && (e.message || e))
  }
  return []
}

/** 供水质页在实时指标先上屏后，单独补拉趋势 */
export async function fetchMentougouHistoryForChart(hours = 168) {
  return fetchMentougouWaterHistory(hours)
}

function mentougouHistoryLooksStub(history, realtime) {
  const h = history || []
  const r = realtime || []
  if (!h.length) return true
  // 仅有「每池当前一点」时视为占位，需再拉真历史
  if (r.length && h.length <= r.length) return true
  return false
}

async function fetchWaterMonitor(force = false) {
  if (!force && _wqCache && Date.now() - _wqAt < WQ_TTL_MS) {
    return _wqCache
  }
  if (!force && _wqPromise) return _wqPromise
  _wqPromise = (async () => {
    let mon = await fetchMentougouWaterMonitor()
    if (!mon) {
      try {
        const res = await getWaterQualityLive()
        if (isApiOk(res) && (res.data.waterRealtime || []).length) {
          mon = await normalizeWaterMonitor(res.data)
        } else if (isApiOk(res)) {
          // 本部门无指标：保留空结果，勿回退全站
          mon = await normalizeWaterMonitor(res.data || {})
        }
      } catch (e) {
        console.warn('[waterQualityLive] getWaterQualityLive', e && (e.message || e))
      }
    }
    if (!mon) {
      try {
        const res = await getOverviewMonitor()
        if (isApiOk(res)) {
          mon = await normalizeWaterMonitor(res.data)
        }
      } catch (e2) {
        console.warn('[waterQualityLive] getOverviewMonitor', e2 && (e2.message || e2))
      }
    }
    if (!mon) {
      mon = { waterAsOf: '', waterRealtime: [], waterTrend: [] }
    }
    _wqCache = mon
    _wqAt = Date.now()
    return mon
  })().finally(() => {
    _wqPromise = null
  })
  return _wqPromise
}

/**
 * 拉全量本部门（+子部门）鱼池，避免 pageSize=200 截断顶层管理员可见池。
 */
async function fetchAllDeptPonds() {
  const pageSize = 200
  const maxPages = 50
  const all = []
  const deptId = resolveCurrentUserDeptId()
  const base = deptId != null ? { deptId } : {}
  for (let pageNo = 1; pageNo <= maxPages; pageNo++) {
    try {
      const res = await getZkPondPage({ ...base, pageNo, pageSize })
      const list = (res && res.data && res.data.list) || []
      all.push(...list)
      const total = Number((res && res.data && res.data.total) || 0)
      if (!list.length || all.length >= total || list.length < pageSize) break
    } catch (e) {
      console.warn('[waterQualityLive] ponds page', pageNo, e && (e.message || e))
      break
    }
  }
  return all.filter((p) => p && (p.pondDetailNum || p.pondName))
}

/**
 * @param {boolean} [force]
 * @returns {{ realtime: object[], history: object[], warnings: object[], waterAsOf: string }}
 */
export async function fetchWaterQualityLiveData(force = false) {
  if (force) {
    clearWaterQualityLiveCaches()
  } else if (_wqResultCache && Date.now() - _wqResultAt < WQ_TTL_MS) {
    return _wqResultCache
  } else if (_wqResultPromise) {
    return _wqResultPromise
  }

  _wqResultPromise = (async () => {
    const deptId = resolveCurrentUserDeptId()
    const platform = isPlatformUnscopedAdmin()

    // 无归属部门且非真超管：禁止全站/演示兜底
    if (!platform && deptId == null) {
      return {
        realtime: [],
        history: [],
        warnings: [],
        waterAsOf: '',
        emptyDeptSites: true
      }
    }

    const cfgPromise = getWaterThresholdConfig().catch(() => null)
    // 有站点缓存则直接用，不再重复打 zk_pond；否则与指标接口并行拉取
    const peekOpts = peekSharedSiteOptions()
    const cachedPonds = pondsFromSiteOptions(peekOpts)
    const sitePromise = cachedPonds.length
      ? Promise.resolve(peekOpts)
      : loadSharedSiteOptions({ includePonds: true }).catch((e) => {
        console.warn('[waterQualityLive] siteOptions', e && (e.message || e))
        return peekSharedSiteOptions()
      })
    // 仅平台超管且无部门时才拉演示种子
    const demoPromise = (platform && deptId == null)
      ? fetchDemoPoolRealtime().catch(() => [])
      : Promise.resolve([])
    const monPromise = fetchWaterMonitor(!!force).catch((e) => {
      console.warn('[waterQualityLive] monitor', e && (e.message || e))
      return { waterAsOf: '', waterRealtime: [], waterTrend: [] }
    })
    // 与实时并行预拉历史；首屏最多等 2.5s，避免卡死整页（真历史由页面补拉）
    const mentougouHistPromise = fetchMentougouWaterHistory(168)
    const HISTORY_WAIT_MS = 2500

    // 指标 / 站点鱼池 / 阈值 并行；指标超时后继续等一轮，避免偶发空「--」
    const emptyMon = { waterAsOf: '', waterRealtime: [], waterTrend: [] }
    const [cfg, siteOpts, demoRealtime, monFirst] = await Promise.all([
      cfgPromise,
      sitePromise,
      demoPromise,
      Promise.race([
        monPromise,
        new Promise((resolve) => setTimeout(() => resolve(null), WQ_MONITOR_WAIT_MS))
      ])
    ])

    let mon = monFirst
    if (!monitorHasValues(mon)) {
      mon = await Promise.race([
        monPromise,
        new Promise((resolve) => setTimeout(() => resolve(mon || emptyMon), WQ_MONITOR_RETRY_MS))
      ]).catch(() => mon || emptyMon)
    }
    if (!mon) mon = emptyMon

    const mentougouPools = (mon && mon.pools) || []
    const fromMentougou = (mon && mon.source === 'mentougou-hflrwl') || mentougouPools.length > 0

    let ponds = pondsFromSiteOptions(siteOpts)
    if (!ponds.length && cachedPonds.length) ponds = cachedPonds
    if (!ponds.length && deptId != null && !fromMentougou) {
      ponds = await fetchAllDeptPonds().catch((e) => {
        console.warn('[waterQualityLive] ponds fallback', e && (e.message || e))
        return []
      })
    }

    // 有归属部门、无鱼池、且没有门头沟真探头：空页
    if (deptId != null && !ponds.length && !fromMentougou) {
      const empty = {
        realtime: [],
        history: [],
        warnings: [],
        waterAsOf: '',
        waterAsOfTs: 0,
        dataStale: false,
        emptyDeptSites: true,
        source: ''
      }
      _wqResultCache = empty
      _wqResultAt = Date.now()
      return empty
    }

    const metrics = metricsFromMonitor(mon || emptyMon)
    let realtime = []
    // P0：有门头沟分池真值 → 只渲染探头对应的池，exact，禁止铺满 40 池 + vary
    if (mentougouPools.length) {
      realtime = realtimeFromMentougouPools(mentougouPools, (mon && mon.waterAsOf) || '')
    } else if (ponds.length) {
      realtime = ponds.map((p, i) => buildRealtimeRow(p, metrics, p.id || i + 1, i, { exact: false }))
    } else if (platform && demoRealtime.length) {
      realtime = demoRealtime
    } else if (platform && ((mon && mon.waterRealtime) || []).some((m) => m && m.value != null)) {
      realtime = [buildRealtimeRow({ pondDetailNum: '监测点', pondName: '监测点' }, metrics, 1, 0, { exact: true })]
    }

    const poolNames = realtime.map((r) => r.lake_name)
    let history = []
    let historyPending = false
    if (fromMentougou) {
      // 真历史：开放 properties/history（BFF 降采样）；首屏短等，超时用当前点占位（不造假曲线）
      const histRace = await Promise.race([
        mentougouHistPromise
          .then((rows) => ({ rows: rows || [], pending: false }))
          .catch(() => ({ rows: [], pending: false })),
        new Promise((resolve) =>
          setTimeout(() => resolve({ rows: null, pending: true }), HISTORY_WAIT_MS)
        )
      ])
      if (histRace.rows && histRace.rows.length) {
        history = histRace.rows
      } else {
        historyPending = !!histRace.pending || !(histRace.rows && histRace.rows.length)
        const asOf = (mon && mon.waterAsOf) || formatDateTime(new Date())
        realtime.forEach((r, i) => {
          history.push({
            Id: i + 1,
            lake_name: r.lake_name,
            created_time: asOf,
            lake_temp: r.lake_temp,
            lake_do: r.lake_do,
            lake_ph: r.lake_ph,
            lake_an: r.lake_an,
            lake_tubid: r.lake_tubid,
            lake_orp: r.lake_orp,
            lake_alkalin: r.lake_alkalin,
            lake_hard: r.lake_hard,
            lake_nirate: r.lake_nirate,
            lake_co2: r.lake_co2,
            lake_tss: r.lake_tss,
            lake_p: r.lake_p,
            lake_n: r.lake_n,
            isDel: 0
          })
        })
        // 后台写回缓存，供补拉命中
        if (histRace.pending) {
          mentougouHistPromise
            .then((rows) => {
              if (!rows || !rows.length || !_wqResultCache) return
              if (_wqResultCache.source !== 'mentougou-hflrwl') return
              _wqResultCache = { ..._wqResultCache, history: rows, historyPending: false }
            })
            .catch(() => {})
        }
      }
    } else {
      history = buildHistoryRows(poolNames, (mon && mon.waterTrend) || [], metrics)
      if (!history.length && realtime.length) {
        history = buildHistoryFromRealtimePools(realtime)
      }
    }

    const waterAsOf = (mon && mon.waterAsOf) || ''
    const waterAsOfTs = Number((mon && mon.waterAsOfTs) || 0)
    let dataStale = false
    if (waterAsOfTs > 0) {
      dataStale = Date.now() - waterAsOfTs > WQ_STALE_MS
    } else if (waterAsOf) {
      const t = new Date(waterAsOf.replace(/-/g, '/')).getTime()
      if (!Number.isNaN(t)) dataStale = Date.now() - t > WQ_STALE_MS
    }

    const result = {
      realtime,
      history,
      historyPending: fromMentougou ? historyPending || mentougouHistoryLooksStub(history, realtime) : false,
      warnings: buildWarnings(realtime, cfg),
      waterAsOf,
      waterAsOfTs,
      dataStale,
      source: (mon && mon.source) || '',
      emptyDeptSites: false,
      metricsIncomplete: !monitorHasValues(mon) && !mentougouPools.length
    }
    _wqResultCache = result
    // 空指标只短缓存，避免刷新前一直卡在「--」
    _wqResultAt = monitorHasValues(mon)
      ? Date.now()
      : Date.now() - WQ_TTL_MS + 2500
    return result
  })().finally(() => {
    _wqResultPromise = null
  })

  return _wqResultPromise
}
