/**
 * 水质页演示用数据发生器（甲方演示）
 * - 本地模拟实时波动，不连库
 * - 后续接库时关闭页面开关即可恢复原 SQL 拉取
 * - 系统总览与水质页共用同一份 snapshot，保证指标名/数值/设定区间一致
 */
import { WATER_QUALITY_METRICS } from '@/utils/waterQualityMetrics'
import { evalMetricStatus } from '@/utils/waterMetricStatus'

/** 与水质页开关同步：true = 演示发生器；false = 读库（Java API + zk_device_record） */
export const USE_WATER_QUALITY_DEMO = false

const POOL_NAMES = [
  'c1-s1-1', 'c1-s1-2', 'c1-s1-3', 'c1-s1-4',
  'c1-s2-1', 'c1-s2-2', 'c1-s2-3',
  'c2-s1-1', 'c2-s1-2', 'c2-s1-3', 'c2-s1-4',
  'c2-s2-1', 'c2-s2-2',
]

/** 各指标基准值 + 振幅（多数在正常 range 内，个别池偶尔偏高） */
const METRIC_BASE = {
  lake_ph: { base: 7.4, amp: 0.35, digits: 2 },
  lake_do: { base: 7.2, amp: 0.9, digits: 2 },
  lake_temp: { base: 24.5, amp: 1.2, digits: 1 },
  lake_tubid: { base: 2.1, amp: 0.8, digits: 2 },
  lake_orp: { base: 280, amp: 40, digits: 0 },
  lake_alkalin: { base: 125, amp: 12, digits: 1 },
  lake_hard: { base: 170, amp: 15, digits: 1 },
  lake_nirate: { base: 18, amp: 8, digits: 1 },
  lake_an: { base: 0.18, amp: 0.12, digits: 3 },
  lake_co2: { base: 22, amp: 8, digits: 1 },
  lake_tss: { base: 12, amp: 6, digits: 1 },
  lake_p: { base: 1.2, amp: 0.5, digits: 2 },
  lake_n: { base: 8, amp: 3, digits: 1 },
}

function round(n, digits) {
  const f = 10 ** digits
  return Math.round(n * f) / f
}

function wave(t, seed, base, amp) {
  // 多频叠加，看起来像真实传感器抖动
  const a = Math.sin(t * 0.7 + seed)
  const b = Math.sin(t * 1.9 + seed * 1.7) * 0.45
  const c = Math.sin(t * 4.1 + seed * 0.3) * 0.2
  return base + amp * (a * 0.55 + b + c)
}

function formatTime(d) {
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

function buildPoolRow(lakeName, id, tSec, now) {
  const seed = lakeName.split('').reduce((s, c) => s + c.charCodeAt(0), 0) * 0.17
  // 少数池故意抬高氨氮/压低溶氧，方便演示预警态
  const stress =
    lakeName === 'c1-s1-3' || lakeName === 'c2-s1-2'
      ? { lake_an: 0.22, lake_do: -1.4, lake_nirate: 8 }
      : lakeName === 'c2-s2-1'
        ? { lake_an: 0.35, lake_do: -2.2, lake_nirate: 14 }
        : { lake_an: 0, lake_do: 0, lake_nirate: 0 }

  const row = {
    Id: id,
    lake_name: lakeName,
    isDel: 0,
    created_time: formatTime(now),
  }
  Object.keys(METRIC_BASE).forEach((key) => {
    const cfg = METRIC_BASE[key]
    let v = wave(tSec / 8, seed + cfg.base, cfg.base, cfg.amp)
    if (stress[key]) v += stress[key]
    row[key] = round(Math.max(0, v), cfg.digits)
  })
  return row
}

/**
 * @returns {{ realtime: object[], history: object[], warnings: object[] }}
 */
export function createWaterQualityDemoState() {
  const now = Date.now()
  const history = []
  let histId = 1
  // 近 26 小时，每 10 分钟一点，供 24h/3d 曲线
  for (let i = 26 * 6; i >= 0; i--) {
    const ts = new Date(now - i * 10 * 60 * 1000)
    const tSec = ts.getTime() / 1000
    POOL_NAMES.forEach((name, pi) => {
      history.push(buildPoolRow(name, histId++, tSec + pi, ts))
    })
  }
  return {
    tick: 0,
    history,
    nextId: histId,
  }
}

/**
 * 推进一帧：刷新实时表，并向历史追加当前点（图表会跟着动）
 * @param {ReturnType<typeof createWaterQualityDemoState>} state
 */
export function tickWaterQualityDemo(state) {
  state.tick += 1
  const now = new Date()
  const tSec = now.getTime() / 1000 + state.tick * 0.35
  const realtime = POOL_NAMES.map((name, i) =>
    buildPoolRow(name, state.nextId + i, tSec + i * 0.2, now)
  )
  state.nextId += POOL_NAMES.length

  // 每 tick 追加当前快照到历史（控制体积：只保留约 30 小时）
  realtime.forEach((row) => {
    state.history.push({ ...row, Id: state.nextId++ })
  })
  const cutoff = now.getTime() - 30 * 3600 * 1000
  state.history = state.history.filter((r) => {
    const t = new Date(r.created_time).getTime()
    return !isNaN(t) && t >= cutoff
  })

  const warnings = []
  realtime.forEach((row, idx) => {
    const an = row.lake_an
    const dO = row.lake_do
    if (an > 0.3) {
      warnings.push({
        Id: 9000 + idx,
        warning_lake: row.lake_name,
        warning_data: '氨氮',
        warning_value: String(an),
        warning_unit: 'mg/L',
        warning_status: an > 0.5 ? '1' : '0',
        isDel: 0,
        createdTime: { hours: now.getHours(), minutes: now.getMinutes() },
      })
    } else if (dO < 5.5) {
      warnings.push({
        Id: 9100 + idx,
        warning_lake: row.lake_name,
        warning_data: '溶解氧',
        warning_value: String(dO),
        warning_unit: 'mg/L',
        warning_status: dO < 4 ? '1' : '0',
        isDel: 0,
        createdTime: { hours: now.getHours(), minutes: now.getMinutes() },
      })
    }
  })

  return { realtime, history: state.history, warnings }
}

let sharedState = null
let lastSnap = null
let lastTickAt = 0

export function getSharedWaterQualityDemoState() {
  if (!sharedState) sharedState = createWaterQualityDemoState()
  return sharedState
}

/**
 * 总览 / 水质页共用快照。短时间内多次读取不重复推进，避免两边各 tick 一次对不上。
 */
export function getSharedWaterQualitySnapshot(opts) {
  const staleMs = (opts && opts.tickIfStaleMs) != null ? opts.tickIfStaleMs : 1800
  const now = Date.now()
  if (!lastSnap || now - lastTickAt >= staleMs) {
    lastSnap = tickWaterQualityDemo(getSharedWaterQualityDemoState())
    lastTickAt = now
  }
  return lastSnap
}

export function poolRowToOverviewMetrics(row, metrics) {
  const list = metrics || WATER_QUALITY_METRICS
  return list.map((m) => {
    const raw = row ? row[m.key] : ''
    const pageStatus = evalMetricStatus(raw, m.range)
    return {
      name: String(m.label || '').trim(),
      unit: String(m.unit || '').trim(),
      value: raw,
      range: m.range,
      status: pageStatus === '超标' ? 'critical' : 'normal',
      statusLabel: pageStatus,
      _key: m.key,
      _fromWaterPage: true
    }
  })
}

function pad2(n) {
  return String(n).padStart(2, '0')
}

export function historyToOverviewTrend(history, lakeName, rangeKey) {
  const hours = rangeKey === '30d' ? 720 : rangeKey === '7d' ? 168 : 24
  const cutoff = Date.now() - hours * 3600 * 1000
  const rows = (history || []).filter((r) => {
    if (lakeName && r.lake_name !== lakeName) return false
    const t = new Date(r.created_time).getTime()
    return !isNaN(t) && t >= cutoff
  })
  if (!rows.length) return []
  const maxPts = rangeKey === '24h' ? 12 : 10
  const step = Math.max(1, Math.floor(rows.length / maxPts))
  const out = []
  for (let i = 0; i < rows.length; i += step) {
    const r = rows[i]
    const d = new Date(r.created_time)
    out.push({
      time: rangeKey === '24h' ? pad2(d.getHours()) + ':' + pad2(d.getMinutes()) : (d.getMonth() + 1) + '/' + d.getDate(),
      temp: r.lake_temp,
      oxygen: r.lake_do,
      ph: r.lake_ph
    })
  }
  if (rows.length && (rows.length - 1) % step !== 0) {
    const r = rows[rows.length - 1]
    const d = new Date(r.created_time)
    out.push({
      time: rangeKey === '24h' ? pad2(d.getHours()) + ':' + pad2(d.getMinutes()) : (d.getMonth() + 1) + '/' + d.getDate(),
      temp: r.lake_temp,
      oxygen: r.lake_do,
      ph: r.lake_ph
    })
  }
  return out
}
