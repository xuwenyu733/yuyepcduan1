/**
 * 门头沟属性历史 → 水质页 history_data 行（按池 + 时间桶降采样）
 */
const { POOL_PROBES, formatAsOf: formatAsOfFromLive } = (() => {
  try {
    return require('./mapWaterLive')
  } catch (_) {
    return { POOL_PROBES: [], formatAsOf: null }
  }
})()

function num(v) {
  if (v == null || v === '') return null
  const n = Number(v)
  return Number.isFinite(n) ? n : null
}

function formatAsOf(ts) {
  if (typeof formatAsOfFromLive === 'function') {
    const s = formatAsOfFromLive(ts)
    if (s) return s
  }
  if (!ts) return ''
  const d = new Date(typeof ts === 'number' && ts < 1e12 ? ts * 1000 : ts)
  if (Number.isNaN(d.getTime())) return ''
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

/**
 * 将原始历史点压到时间桶（桶内取最新）
 * @returns {Map<number, { ts: number, value: number }>}
 */
function toBuckets(rows, startTime, endTime, bucketMs) {
  const map = new Map()
  const start = Number(startTime)
  const end = Number(endTime)
  const step = Math.max(60_000, Number(bucketMs) || 60_000)
  for (const row of rows || []) {
    if (!row) continue
    const ts = Number(row.timestamp || row.createTime || 0)
    if (!(ts >= start && ts <= end)) continue
    const value = num(row.value != null ? row.value : row.formatValue)
    if (value == null) continue
    const b = Math.floor(ts / step) * step
    const prev = map.get(b)
    if (!prev || ts >= prev.ts) map.set(b, { ts, value })
  }
  return map
}

function bucketValue(map, bucket) {
  if (!map || !map.size) return null
  const hit = map.get(bucket)
  if (hit) return hit.value
  // 邻近桶兜底（±1），避免共享指标与分池时钟错位导致断线
  const prev = map.get(bucket - (map._step || 0))
  const next = map.get(bucket + (map._step || 0))
  if (prev) return prev.value
  if (next) return next.value
  return null
}

/**
 * @param {{ deviceId?: string, startTime: number, endTime: number, hours?: number, byProp: Record<string, any[]> }} hist
 * @param {{ maxPoints?: number }} [opts]
 */
function mapToWaterHistory(hist, opts = {}) {
  const byProp = (hist && hist.byProp) || {}
  const endTime = Number((hist && hist.endTime) || Date.now())
  const hours = Math.min(168, Math.max(1, Number((hist && hist.hours) || 168)))
  const startTime = Number((hist && hist.startTime) || endTime - hours * 3600 * 1000)
  const maxPoints = Math.max(48, Math.min(336, Number(opts.maxPoints) || Math.max(96, hours * 2)))
  const bucketMs = Math.max(60_000, Math.ceil((endTime - startTime) / maxPoints))

  const series = Object.create(null)
  for (const [prop, rows] of Object.entries(byProp)) {
    const buckets = toBuckets(rows, startTime, endTime, bucketMs)
    buckets._step = bucketMs
    series[prop] = buckets
  }

  const probes = (POOL_PROBES && POOL_PROBES.length)
    ? POOL_PROBES
    : [
      { idx: 1, oxygen: 'do_oxygen_1', temp: 'do_temp_1' },
      { idx: 2, oxygen: 'do_oxygen_2', temp: 'do_temp_2' },
      { idx: 3, oxygen: 'do_oxygen_3', temp: 'do_temp_3' }
    ]

  const sharedPh = series.ph_ph_1
  const sharedNtu = series.ntu_ntu_1
  const sharedOrp = series.orp_orp_1

  /** 时间轴：各分池溶氧/水温桶并集 */
  const bucketSet = new Set()
  for (const probe of probes) {
    const o = series[probe.oxygen]
    const t = series[probe.temp]
    if (o) for (const k of o.keys()) if (typeof k === 'number') bucketSet.add(k)
    if (t) for (const k of t.keys()) if (typeof k === 'number') bucketSet.add(k)
  }
  if (!bucketSet.size) {
    for (const m of [sharedPh, sharedNtu, sharedOrp]) {
      if (!m) continue
      for (const k of m.keys()) if (typeof k === 'number') bucketSet.add(k)
    }
  }
  const buckets = [...bucketSet].sort((a, b) => a - b)
  if (!buckets.length) {
    return {
      source: 'mentougou-hflrwl',
      deviceId: (hist && hist.deviceId) || '',
      startTime,
      endTime,
      hours,
      bucketMs,
      history: [],
      pointCount: 0
    }
  }

  const history = []
  let hid = 1
  for (const probe of probes) {
    const oxyMap = series[probe.oxygen]
    const tempMap = series[probe.temp]
    // 该池全程无溶氧且无水温 → 跳过
    if ((!oxyMap || !oxyMap.size) && (!tempMap || !tempMap.size)) continue
    const lakeName = `门头沟${probe.idx}#`
    for (const b of buckets) {
      const lake_do = bucketValue(oxyMap, b)
      const lake_temp = bucketValue(tempMap, b)
      const lake_ph = bucketValue(sharedPh, b)
      const lake_tubid = bucketValue(sharedNtu, b)
      const lake_orp = bucketValue(sharedOrp, b)
      if (
        lake_do == null &&
        lake_temp == null &&
        lake_ph == null &&
        lake_tubid == null &&
        lake_orp == null
      ) {
        continue
      }
      const sampleTs =
        (oxyMap && oxyMap.get(b) && oxyMap.get(b).ts) ||
        (tempMap && tempMap.get(b) && tempMap.get(b).ts) ||
        b
      history.push({
        Id: hid++,
        lake_name: lakeName,
        created_time: formatAsOf(sampleTs),
        created_ts: sampleTs,
        lake_temp,
        lake_do,
        lake_ph,
        lake_an: null,
        lake_tubid,
        lake_orp,
        lake_alkalin: null,
        lake_hard: null,
        lake_nirate: null,
        lake_co2: null,
        lake_tss: null,
        lake_p: null,
        lake_n: null,
        isDel: 0
      })
    }
  }

  return {
    source: 'mentougou-hflrwl',
    deviceId: (hist && hist.deviceId) || '',
    startTime,
    endTime,
    hours,
    bucketMs,
    history,
    pointCount: history.length
  }
}

module.exports = {
  mapToWaterHistory,
  toBuckets,
  formatAsOf
}
