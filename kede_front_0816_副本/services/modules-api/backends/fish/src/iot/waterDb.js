/**
 * 门头沟水质 → PMIS 库表读写
 * realtime: test_table_water_quality_realtime
 * history:  test_table_water_quality_history
 */
const { query, queryOne, execute, esc } = require('../pmis/client')
const { EXISTING } = require('../pmis/tables')
const { mapToWaterLive } = require('./mapWaterLive')

const REALTIME = EXISTING.waterQualityRealtime
const HISTORY = EXISTING.waterQualityHistory

function s(v) {
  if (v == null || v === '') return ''
  return String(v)
}

function formatTs(ts) {
  if (!ts) {
    const d = new Date()
    const p = (n) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
  }
  const d = new Date(typeof ts === 'number' && ts < 1e12 ? ts * 1000 : ts)
  if (Number.isNaN(d.getTime())) return s(ts)
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

function poolCodeOf(idx) {
  return `MTG-${idx}`
}

/** 从 mapToWaterLive 结果拆成可入库行（分池） */
function liveToDbRows(live) {
  const asOf = (live && live.waterAsOf) || formatTs()
  const shared = {}
  for (const m of (live && live.waterRealtime) || []) {
    if (!m || !m.key) continue
    shared[m.key] = m.value
  }
  const rows = []
  for (const p of (live && live.pools) || []) {
    if (!p) continue
    const idx = p.idx || p.probeNo || 1
    rows.push({
      pool_name: p.lake_name || p.name || `门头沟${idx}#`,
      pool_code: poolCodeOf(idx),
      workshop: '门头沟',
      system_name: '门头沟系统',
      ph: s(p.lake_ph != null ? p.lake_ph : shared.ph),
      do_mg_l: s(p.lake_do),
      water_temp: s(p.lake_temp),
      turbidity: s(p.lake_tubid != null ? p.lake_tubid : shared.turbidity),
      orp: s(p.lake_orp != null ? p.lake_orp : shared.orp),
      alkalinity: '',
      hardness: '',
      nitrate: '',
      ammonia: '',
      nitrite: '',
      co2: s(p.lake_co2 != null ? p.lake_co2 : shared.co2),
      tss: '',
      total_phosphorus: '',
      total_nitrogen: '',
      gsm: '',
      mib: '',
      sensor_status: 'normal',
      sample_time: asOf,
      report_time: asOf
    })
  }
  return rows
}

async function findRealtimeByPoolName(poolName) {
  const rows = await queryOne(
    `select Id, sample_time from ${REALTIME} where isDel=0 and pool_name='${esc(poolName)}' order by Id desc limit 1`
  )
  const hit = rows[0]
  if (!hit || hit.Id == null) return null
  return { id: Number(hit.Id), sample_time: String(hit.sample_time || '') }
}

async function upsertRealtimeRow(row) {
  const existing = await findRealtimeByPoolName(row.pool_name)
  // 设备同一帧（sample_time 未变）：不重复 update / 不写 history
  if (existing && existing.sample_time && existing.sample_time === String(row.sample_time || '')) {
    return { action: 'skip', id: existing.id, skipped: true }
  }
  if (existing && existing.id) {
    await execute([
      `update ${REALTIME} set ` +
        `ph='${esc(row.ph)}',do_mg_l='${esc(row.do_mg_l)}',water_temp='${esc(row.water_temp)}',` +
        `turbidity='${esc(row.turbidity)}',orp='${esc(row.orp)}',co2='${esc(row.co2)}',` +
        `sensor_status='${esc(row.sensor_status)}',sample_time='${esc(row.sample_time)}',` +
        `report_time='${esc(row.report_time)}',pool_code='${esc(row.pool_code)}',` +
        `workshop='${esc(row.workshop)}',system_name='${esc(row.system_name)}' ` +
        `where Id=${existing.id}`
    ])
    return { action: 'update', id: existing.id, skipped: false }
  }
  await execute([
    `insert into ${REALTIME} (isDel,recId,pool_name,ph,do_mg_l,water_temp,turbidity,orp,alkalinity,hardness,nitrate,ammonia,nitrite,co2,tss,total_phosphorus,total_nitrogen,gsm,mib,sensor_status,sample_time,report_time,pool_code,workshop,system_name) values (` +
      `0,0,'${esc(row.pool_name)}','${esc(row.ph)}','${esc(row.do_mg_l)}','${esc(row.water_temp)}',` +
      `'${esc(row.turbidity)}','${esc(row.orp)}','${esc(row.alkalinity)}','${esc(row.hardness)}',` +
      `'${esc(row.nitrate)}','${esc(row.ammonia)}','${esc(row.nitrite)}','${esc(row.co2)}',` +
      `'${esc(row.tss)}','${esc(row.total_phosphorus)}','${esc(row.total_nitrogen)}','${esc(row.gsm)}',` +
      `'${esc(row.mib)}','${esc(row.sensor_status)}','${esc(row.sample_time)}','${esc(row.report_time)}',` +
      `'${esc(row.pool_code)}','${esc(row.workshop)}','${esc(row.system_name)}')`
  ])
  return { action: 'insert', id: null, skipped: false }
}

async function insertHistoryRow(row) {
  await execute([
    `insert into ${HISTORY} (isDel,recId,pool_name,ph,do_mg_l,water_temp,turbidity,orp,alkalinity,hardness,nitrate,ammonia,nitrite,co2,tss,total_phosphorus,total_nitrogen,gsm,mib,sensor_code,sensor_status,sample_time,report_time,raw_payload_id) values (` +
      `0,0,'${esc(row.pool_name)}','${esc(row.ph)}','${esc(row.do_mg_l)}','${esc(row.water_temp)}',` +
      `'${esc(row.turbidity)}','${esc(row.orp)}','${esc(row.alkalinity)}','${esc(row.hardness)}',` +
      `'${esc(row.nitrate)}','${esc(row.ammonia)}','${esc(row.nitrite)}','${esc(row.co2)}',` +
      `'${esc(row.tss)}','${esc(row.total_phosphorus)}','${esc(row.total_nitrogen)}','${esc(row.gsm)}',` +
      `'${esc(row.mib)}','${esc(row.pool_code)}','${esc(row.sensor_status)}','${esc(row.sample_time)}',` +
      `'${esc(row.report_time)}','mentougou')`
  ])
}

/**
 * 把一次 live 快照写入 realtime（upsert）+ history（仅 sample_time 变化时 insert）
 */
async function persistLiveSnapshot(live, { writeHistory = true } = {}) {
  const rows = liveToDbRows(live)
  if (!rows.length) return { saved: 0, skipped: 0, rows: [] }
  const results = []
  let skipped = 0
  for (const row of rows) {
    const r = await upsertRealtimeRow(row)
    if (r.skipped) {
      skipped += 1
      results.push({ pool: row.pool_name, ...r, sample_time: row.sample_time })
      continue
    }
    if (writeHistory) await insertHistoryRow(row)
    results.push({ pool: row.pool_name, ...r, sample_time: row.sample_time })
  }
  return {
    saved: results.length - skipped,
    skipped,
    rows: results
  }
}

function numOrNull(v) {
  if (v == null || v === '') return null
  const n = Number(v)
  return Number.isFinite(n) ? n : null
}

/**
 * 从 realtime 表读门头沟分池，组装与 mapToWaterLive 同结构
 */
async function loadMentougouLiveFromDb() {
  const rows = await queryOne(
    `select * from ${REALTIME} where isDel=0 and (pool_name like '门头沟%' or pool_code like 'MTG-%') order by pool_code asc, Id asc`
  )
  if (!rows || !rows.length) return null

  // 同池多名时取最新 sample_time
  const byPool = new Map()
  for (const r of rows) {
    const key = r.pool_name || r.pool_code
    if (!key) continue
    const prev = byPool.get(key)
    if (!prev || String(r.sample_time || '') >= String(prev.sample_time || '')) {
      byPool.set(key, r)
    }
  }
  const list = [...byPool.values()]
  if (!list.length) return null

  const pools = list.map((r, i) => {
    const m = String(r.pool_name || '').match(/(\d+)#/)
    const idx = m ? Number(m[1]) : i + 1
    return {
      idx,
      probeNo: idx,
      name: r.pool_name,
      lake_name: r.pool_name,
      lake_do: numOrNull(r.do_mg_l),
      lake_temp: numOrNull(r.water_temp),
      lake_ph: numOrNull(r.ph),
      lake_tubid: numOrNull(r.turbidity),
      lake_orp: numOrNull(r.orp),
      lake_an: numOrNull(r.ammonia),
      lake_co2: numOrNull(r.co2),
      lake_alkalin: numOrNull(r.alkalinity),
      lake_hard: numOrNull(r.hardness),
      lake_nirate: numOrNull(r.nitrate),
      lake_tss: numOrNull(r.tss),
      lake_p: numOrNull(r.total_phosphorus),
      lake_n: numOrNull(r.total_nitrogen),
      exact: true
    }
  })

  const avg = (arr) => {
    const vals = arr.filter((v) => v != null)
    if (!vals.length) return null
    return Number((vals.reduce((a, b) => a + b, 0) / vals.length).toFixed(2))
  }

  const sampleTime = list.map((r) => r.sample_time || r.report_time).filter(Boolean).sort().slice(-1)[0] || ''
  const waterAsOfTs = sampleTime ? new Date(String(sampleTime).replace(/-/g, '/')).getTime() : 0

  const ph = avg(pools.map((p) => p.lake_ph))
  const oxygen = avg(pools.map((p) => p.lake_do))
  const temp = avg(pools.map((p) => p.lake_temp))
  const turbidity = avg(pools.map((p) => p.lake_tubid))
  const orp = avg(pools.map((p) => p.lake_orp))
  const co2 = avg(pools.map((p) => p.lake_co2))

  const waterItem = (name, unit, value, range, key) => ({
    name,
    unit,
    value: value != null ? Number(value) : null,
    range,
    status: value == null ? 'unknown' : 'normal',
    key
  })

  return {
    source: 'mentougou-db',
    deviceId: '',
    waterAsOf: sampleTime,
    waterAsOfTs: Number.isFinite(waterAsOfTs) ? waterAsOfTs : 0,
    waterRealtime: [
      waterItem('水温', '°C', temp, '10-30', 'temp'),
      waterItem('pH值', '', ph, '6.5-8.5', 'ph'),
      waterItem('溶解氧', 'mg/L', oxygen, '≥5.0', 'oxygen'),
      waterItem('氨氮', 'mg/L', null, '<0.5', 'ammonia'),
      waterItem('浊度', 'NTU', turbidity, '0-5', 'turbidity'),
      waterItem('ORP', 'mV', orp, '200-600', 'orp'),
      waterItem('碱度', 'mg/L', null, '80-250', 'alkalinity'),
      waterItem('硬度', 'mg/L', null, '100-300', 'hardness'),
      waterItem('硝酸盐', 'mg/L', null, '0-35', 'nitrate'),
      waterItem('二氧化碳', 'mg/L', co2, '0-15', 'co2'),
      waterItem('TSS', 'mg/L', null, '0-20', 'tss'),
      waterItem('总磷', 'mg/L', null, '0-1', 'tp'),
      waterItem('总氮', 'mg/L', null, '0-10', 'tn')
    ],
    waterTrend: [],
    pools,
    monitorMetrics: {
      do_mg_l: oxygen,
      ph,
      ammonia: null,
      water_temp: temp,
      turbidity,
      orp,
      co2
    },
    extras: {},
    rawCount: list.length
  }
}

/**
 * 从 history 表读出门头沟趋势行（对齐前端 history_data）
 */
async function loadMentougouHistoryFromDb(hours = 168) {
  const h = Math.min(168, Math.max(1, Number(hours) || 168))
  const end = Date.now()
  const start = end - h * 3600 * 1000
  const startStr = formatTs(start)
  // 取窗口内最新 5000 行，再反转为升序，避免行数涨破后永远卡在最早一段
  const rowsNested = await query([
    `select * from ${HISTORY} where isDel=0 and (pool_name like '门头沟%' or sensor_code like 'MTG-%') and sample_time>='${esc(startStr)}' order by sample_time desc limit 5000`
  ])
  const rows = ((rowsNested && rowsNested[0]) || []).slice().reverse()
  if (!rows.length) return { history: [], pointCount: 0, hours: h }

  // 降采样：按池 + 时间桶
  const maxPoints = Math.max(96, Math.min(336, h * 2))
  const bucketMs = Math.max(60_000, Math.ceil((h * 3600 * 1000) / maxPoints))
  const buckets = new Map() // key = pool|bucket -> row

  for (const r of rows) {
    const ts = new Date(String(r.sample_time || r.createdTime || '').replace(/-/g, '/')).getTime()
    if (!Number.isFinite(ts)) continue
    const b = Math.floor(ts / bucketMs) * bucketMs
    const pool = r.pool_name || r.sensor_code || '门头沟'
    const key = `${pool}|${b}`
    const prev = buckets.get(key)
    if (!prev || ts >= prev._ts) {
      buckets.set(key, { ...r, _ts: ts })
    }
  }

  let hid = 1
  const history = [...buckets.values()]
    .sort((a, b) => a._ts - b._ts || String(a.pool_name).localeCompare(String(b.pool_name)))
    .map((r) => ({
      Id: hid++,
      lake_name: r.pool_name,
      created_time: r.sample_time || formatTs(r._ts),
      created_ts: r._ts,
      lake_temp: numOrNull(r.water_temp),
      lake_do: numOrNull(r.do_mg_l),
      lake_ph: numOrNull(r.ph),
      lake_an: numOrNull(r.ammonia),
      lake_tubid: numOrNull(r.turbidity),
      lake_orp: numOrNull(r.orp),
      lake_alkalin: numOrNull(r.alkalinity),
      lake_hard: numOrNull(r.hardness),
      lake_nirate: numOrNull(r.nitrate),
      lake_co2: numOrNull(r.co2),
      lake_tss: numOrNull(r.tss),
      lake_p: numOrNull(r.total_phosphorus),
      lake_n: numOrNull(r.total_nitrogen),
      isDel: 0
    }))

  return {
    source: 'mentougou-db',
    hours: h,
    history,
    pointCount: history.length
  }
}

/** 供外部：latest → live → 入库 */
async function persistFromLatest(latest, opts) {
  const live = mapToWaterLive(latest)
  const saved = await persistLiveSnapshot(live, opts)
  return { live, saved }
}

module.exports = {
  liveToDbRows,
  persistLiveSnapshot,
  persistFromLatest,
  loadMentougouLiveFromDb,
  loadMentougouHistoryFromDb,
  formatTs
}
