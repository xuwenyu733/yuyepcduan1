/**
 * 把门头沟 latest 属性映射为渔业水质 live 结构
 *（与 getWaterQualityLive / waterQualityLiveBridge 字段对齐）
 *
 * 关键：1#/2#/3# 探头分池输出，禁止 avgNum 抹平后假抖动。
 */

function num(v) {
  if (v == null || v === '') return null
  if (typeof v === 'boolean') return v ? 1 : 0
  const n = Number(v)
  return Number.isFinite(n) ? n : null
}

function indexByProperty(properties) {
  const map = Object.create(null)
  for (const row of properties || []) {
    if (!row || row.property == null) continue
    map[String(row.property)] = row
  }
  return map
}

function pickNum(map, keys) {
  for (const k of keys) {
    const row = map[k]
    if (!row) continue
    const v = num(row.value != null ? row.value : row.formatValue)
    if (v != null) return v
  }
  return null
}

/** 多探头取平均（仅作总览摘要；分池请用 pools） */
function avgNum(map, keys) {
  const vals = []
  for (const k of keys) {
    const row = map[k]
    if (!row) continue
    const v = num(row.value != null ? row.value : row.formatValue)
    if (v != null) vals.push(v)
  }
  if (!vals.length) return null
  return Number((vals.reduce((a, b) => a + b, 0) / vals.length).toFixed(2))
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

function formatAsOf(ts) {
  if (!ts) return ''
  const d = new Date(typeof ts === 'number' && ts < 1e12 ? ts * 1000 : ts)
  if (Number.isNaN(d.getTime())) return ''
  const p = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

/** 设备探头 → 分池（lel_level_2 量纲未确认，不参与报警/百分比） */
const POOL_PROBES = [
  { idx: 1, oxygen: 'do_oxygen_1', temp: 'do_temp_1', sat: 'do_oxyper_1', level: 'lel_level_1' },
  { idx: 2, oxygen: 'do_oxygen_2', temp: 'do_temp_2', sat: 'do_oxyper_2', level: null },
  { idx: 3, oxygen: 'do_oxygen_3', temp: 'do_temp_3', sat: 'do_oxyper_3', level: null }
]

function buildPools(map, shared) {
  const pools = []
  for (const probe of POOL_PROBES) {
    const oxygen = pickNum(map, [probe.oxygen])
    const temp = pickNum(map, [probe.temp])
    const sat = pickNum(map, [probe.sat])
    const level = probe.level ? pickNum(map, [probe.level]) : null
    // 至少有溶氧或水温才算该池在线
    if (oxygen == null && temp == null) continue
    pools.push({
      idx: probe.idx,
      probeNo: probe.idx,
      name: `门头沟${probe.idx}#`,
      lake_name: `门头沟${probe.idx}#`,
      lake_do: oxygen,
      lake_temp: temp,
      lake_ph: shared.ph,
      lake_tubid: shared.turbidity,
      lake_orp: shared.orp,
      lake_an: null,
      lake_co2: shared.co2,
      lake_alkalin: null,
      lake_hard: null,
      lake_nirate: null,
      lake_tss: null,
      lake_p: null,
      lake_n: null,
      do_saturation: sat,
      water_level: level,
      exact: true
    })
  }
  return pools
}

/**
 * @param {{ deviceId: string, asOf?: string, properties: any[] }} latest
 */
function mapToWaterLive(latest) {
  const props = (latest && latest.properties) || []
  const map = indexByProperty(props)

  const ph = pickNum(map, ['ph_ph_1'])
  const turbidity = pickNum(map, ['ntu_ntu_1'])
  const orp = pickNum(map, ['orp_orp_1'])
  // airC_co2_1 是空气 CO₂（ppm），不要当成水体溶解 CO₂(mg/L)
  const co2 = pickNum(map, ['wq_co2_1', 'do_co2_1'])
  const level1 = pickNum(map, ['lel_level_1'])
  const airCo2 = pickNum(map, ['airC_co2_1'])
  const airTemp = avgNum(map, ['airT_temp_1', 'airT_temp_2'])
  const lux = pickNum(map, ['airC_lux_1'])

  const pools = buildPools(map, { ph, turbidity, orp, co2 })

  // 总览摘要：分池均值（仅摘要；分池真值在 pools）
  const avgOf = (arr) => {
    const vals = arr.filter(v => v != null)
    if (!vals.length) return null
    return Number((vals.reduce((a, b) => a + b, 0) / vals.length).toFixed(2))
  }
  const oxygen = avgOf(pools.map(p => p.lake_do))
  const temp = avgOf(pools.map(p => p.lake_temp))
  const sat = avgOf(pools.map(p => p.do_saturation))

  let maxTs = 0
  for (const row of props) {
    const t = Number(row.timestamp || row.createTime || 0)
    // 忽略 timestamp=0 的从未上报点位
    if (t > 0 && t > maxTs) maxTs = t
  }

  const waterRealtime = [
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
  ]

  const monitorMetrics = {
    do_mg_l: oxygen,
    ph,
    ammonia: null,
    water_temp: temp,
    turbidity,
    orp,
    co2,
    water_level: level1,
    do_saturation: sat,
    air_co2: airCo2,
    air_temp: airTemp,
    lux
  }

  return {
    source: 'mentougou-hflrwl',
    deviceId: (latest && latest.deviceId) || '',
    waterAsOf: formatAsOf(maxTs) || (latest && latest.asOf) || '',
    waterAsOfTs: maxTs || 0,
    waterRealtime,
    waterTrend: [],
    pools,
    monitorMetrics,
    extras: {
      airCo2,
      airTemp,
      lux,
      waterLevel: level1,
      doSaturation: sat
    },
    rawCount: props.length
  }
}

module.exports = {
  mapToWaterLive,
  indexByProperty,
  pickNum,
  avgNum,
  POOL_PROBES,
  buildPools,
  formatAsOf
}
