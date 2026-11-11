const { queryOne, execute, saveInfo, esc } = require('./client')
const { EXISTING, NEW } = require('./tables')
const { COLUMN_DEFS, buildRowPayload } = require('./schema')

const diseaseStatusMap = {
  pending: '待诊断',
  diagnosing: '进行中',
  treating: '进行中',
  resolved: '已完成'
}

const severityMap = {
  critical: '紧急',
  high: '严重',
  medium: '中等',
  low: '轻微'
}

const pondStatusMap = {
  idle: '空闲',
  in_use: '使用中',
  available: '可用'
}

const waterTypeMap = {
  marine: '海水鱼',
  freshwater: '淡水鱼',
  brackish: '咸淡水鱼'
}

const recoveryStatusMap = {
  good: '恢复良好',
  fair: '恢复一般',
  poor: '恢复较差'
}

const physicalSymptoms = [
  '体表充血', '白点/小颗粒', '烂鳃', '烂尾/烂鳍', '体表溃疡',
  '腹部膨大', '眼球突出', '体色发黑', '鳃丝苍白', '体表黏液增多'
]

const behavioralSymptoms = [
  '浮头/缺氧', '游动异常', '离群独游', '摄食下降', '沉底不动',
  '狂游/冲撞', '水面打转', '跳跃', '摩擦池壁', '呼吸急促'
]

function now() {
  const d = new Date()
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

function nowTime() {
  const d = new Date()
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

function parseJson(value, fallback) {
  if (value == null || value === '') return fallback
  if (typeof value === 'object') return value
  try {
    return JSON.parse(value)
  } catch (e) {
    return fallback
  }
}

function num(value, fallback = 0) {
  const n = Number(value)
  return Number.isFinite(n) ? n : fallback
}

function pick(row, ...keys) {
  for (const k of keys) {
    if (row[k] != null) return row[k]
    const lower = k.toLowerCase()
    for (const rk of Object.keys(row)) {
      if (rk.toLowerCase() === lower) return row[rk]
    }
  }
  return undefined
}

function formatMaybeDate(value) {
  if (value == null || value === '') return ''
  if (typeof value === 'string') return value
  if (typeof value === 'object' && value.time != null) {
    const d = new Date(Number(value.time))
    if (!Number.isNaN(d.getTime())) {
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    }
  }
  return String(value)
}

async function insertRow(tableName, row) {
  const defs = COLUMN_DEFS[tableName]
  await saveInfo(buildRowPayload(tableName, row, defs))
}

async function softDeleteByBizId(tableName, bizId) {
  await execute([
    `update ${tableName} set isDel=1 where isDel=0 and biz_id='${esc(bizId)}'`
  ])
}

async function updateFieldsByBizId(tableName, bizId, fields) {
  const sets = Object.entries(fields)
    .filter(([, v]) => v !== undefined)
    .map(([k, v]) => `${k}='${esc(v == null ? '' : v)}'`)
  if (!sets.length) return
  await execute([
    `update ${tableName} set ${sets.join(', ')} where isDel=0 and biz_id='${esc(bizId)}'`
  ])
}

/* ---------------- readonly existing tables ---------------- */

function mapPond(row) {
  const code = pick(row, 'pool_code') || ''
  const name = pick(row, 'pool_name') || ''
  const workshop = pick(row, 'workshop') || ''
  const systemName = pick(row, 'system_name') || ''
  const enabled = String(pick(row, 'enabled') || '1')
  return {
    id: `POND-${pick(row, 'Id') || code}`,
    code,
    size: '',
    location: [workshop, systemName].filter(Boolean).join(' / '),
    manager: '',
    status: enabled === '0' ? 'idle' : 'in_use',
    statusLabel: enabled === '0' ? pondStatusMap.idle : pondStatusMap.in_use,
    remark: name,
    createdAt: formatMaybeDate(pick(row, 'createdTime'))
  }
}

async function listPonds() {
  const rows = await queryOne(
    `select * from ${EXISTING.aquacultureLocation} where isDel=0 order by Id desc`
  )
  const seen = new Set()
  const list = []
  for (const row of rows) {
    const mapped = mapPond(row)
    if (!mapped.code || seen.has(mapped.code)) continue
    seen.add(mapped.code)
    list.push(mapped)
  }
  return list
}

async function createPond() {
  const err = new Error('鱼池数据来自 PMIS 既有表 aquaculture_location，禁止写入')
  err.code = 403
  throw err
}

async function updatePond() {
  return createPond()
}

async function deletePond() {
  return createPond()
}

function buildMetric(key, label, value, unit, rangeText, min, max, maxExclusive) {
  const item = { key, label, value: num(value, 0), unit, rangeText, min, max, maxExclusive }
  let abnormal = false
  if (item.maxExclusive != null) abnormal = item.value >= item.maxExclusive
  else {
    if (item.min != null && item.value < item.min) abnormal = true
    if (item.max != null && item.value > item.max) abnormal = true
  }
  return {
    key: item.key,
    label: item.label,
    value: item.value,
    unit: item.unit,
    rangeText: item.rangeText,
    abnormal
  }
}

async function fetchMentougouMetrics() {
  try {
    const { getLatest } = require('../iot/hflrwlClient')
    const { mapToWaterLive } = require('../iot/mapWaterLive')
    const live = mapToWaterLive(await getLatest())
    return (live && live.monitorMetrics) || null
  } catch (err) {
    console.warn('[fish/pmis] mentougou iot skip:', (err && err.message) || err)
    return null
  }
}

async function getMonitorData(pondCode) {
  const [waterRows, thresholdRows, iotMetrics] = await Promise.all([
    queryOne(
      `select * from ${EXISTING.waterQualityRealtime} where isDel=0 order by Id desc limit 1`
    ),
    queryOne(
      `select * from ${EXISTING.thresholdConfig} where isDel=0 and enabled<>'0' order by Id desc`
    ),
    fetchMentougouMetrics()
  ])

  const water = Object.assign({}, waterRows[0] || {})
  // 门头沟实时覆盖库表（有值才盖）
  if (iotMetrics) {
    if (iotMetrics.do_mg_l != null) water.do_mg_l = iotMetrics.do_mg_l
    if (iotMetrics.ph != null) water.ph = iotMetrics.ph
    if (iotMetrics.water_temp != null) water.water_temp = iotMetrics.water_temp
    if (iotMetrics.ammonia != null) water.ammonia = iotMetrics.ammonia
  }
  const thresholds = {}
  for (const t of thresholdRows) {
    const code = String(pick(t, 'metric_code') || '').toLowerCase()
    const name = String(pick(t, 'metric_name') || '')
    thresholds[code] = t
    if (name) thresholds[name] = t
  }

  function rangeFrom(metricKeys, fallbackMin, fallbackMax, fallbackExclusive) {
    for (const k of metricKeys) {
      const t = thresholds[k]
      if (!t) continue
      const nmin = pick(t, 'normal_min')
      const nmax = pick(t, 'normal_max')
      return {
        min: nmin !== '' && nmin != null ? num(nmin) : fallbackMin,
        max: nmax !== '' && nmax != null ? num(nmax) : fallbackMax,
        maxExclusive: fallbackExclusive,
        rangeText:
          fallbackExclusive != null
            ? `<${fallbackExclusive}`
            : `${fallbackMin ?? ''}-${fallbackMax ?? ''}`
      }
    }
    return {
      min: fallbackMin,
      max: fallbackMax,
      maxExclusive: fallbackExclusive,
      rangeText:
        fallbackExclusive != null
          ? `<${fallbackExclusive}`
          : `${fallbackMin ?? ''}-${fallbackMax ?? ''}`
    }
  }

  const doR = rangeFrom(['do', 'do_mg_l', '溶氧'], 5, 8)
  const phR = rangeFrom(['ph', 'ph值', 'pH'], 7.0, 8.5)
  const amR = rangeFrom(['ammonia', '氨氮'], null, null, 0.5)
  const tempR = rangeFrom(['temp', 'water_temp', '水温'], 22, 28)

  let metrics = [
    buildMetric('do', '溶氧', pick(water, 'do_mg_l'), 'mg/L', doR.rangeText, doR.min, doR.max),
    buildMetric('ph', 'pH值', pick(water, 'ph'), '', phR.rangeText, phR.min, phR.max),
    buildMetric(
      'ammonia',
      '氨氮',
      pick(water, 'ammonia'),
      'mg/L',
      amR.rangeText,
      amR.min,
      amR.max,
      amR.maxExclusive
    ),
    buildMetric(
      'temp',
      '水温',
      pick(water, 'water_temp'),
      '℃',
      tempR.rangeText,
      tempR.min,
      tempR.max
    )
  ]

  // 按养殖池已绑定设备类型过滤可展示指标
  const code = pondCode ? String(pondCode) : ''
  if (code) {
    try {
      const eqs = await listEquipments()
      const typeMap = {
        do: ['溶氧监测'],
        ph: ['pH监测'],
        ammonia: ['氨氮监测'],
        temp: ['温度监测']
      }
      const availableTypes = new Set()
      ;(eqs || []).forEach(eq => {
        const ponds = Array.isArray(eq.pondCodes) ? eq.pondCodes : (eq.pondCode ? [eq.pondCode] : [])
        if (!ponds.length || ponds.map(String).includes(code)) {
          if (eq.type) availableTypes.add(eq.type)
          if (eq.name) availableTypes.add(eq.name)
        }
      })
      if (availableTypes.size) {
        metrics = metrics.filter(m => (typeMap[m.key] || []).some(t => availableTypes.has(t)))
      } else {
        metrics = []
      }
    } catch (e) {
      // 设备台账失败时保留全量指标
    }
  }

  // AI 报警：异常指标 → 向新建表写入 mock（不改既有报警表）
  const poolCode = code || pick(water, 'pool_code') || ''
  const poolName = pick(water, 'pool_name') || ''
  const workshop = pick(water, 'workshop') || ''
  for (const m of metrics) {
    if (!m.abnormal) continue
    await ensureAiAlarmMock({
      category: 'water',
      priority: m.key === 'ammonia' || m.key === 'do' ? 'high' : 'medium',
      poolCode,
      poolName,
      workshop,
      metricCode: m.key,
      metricName: m.label,
      currentValue: String(m.value),
      thresholdValue: m.rangeText,
      unit: m.unit,
      alarmReason: `【Mock AI】监测到${m.label}异常（当前 ${m.value}${m.unit || ''}，参考范围 ${m.rangeText}）`,
      aiSuggestion: `请复核${m.label}传感器与现场工况，必要时换水/增氧并联系值班人员。`,
      confidence: '80',
      refTable: EXISTING.waterQualityRealtime
    })
  }

  return metrics
}

function mapAiAlarm(row) {
  return {
    id: pick(row, 'biz_id'),
    alarmCode: pick(row, 'alarm_code') || '',
    alarmTitle: pick(row, 'alarm_title') || '',
    category: pick(row, 'category') || '',
    priority: pick(row, 'priority') || 'medium',
    alarmStatus: pick(row, 'alarm_status') || 'open',
    poolCode: pick(row, 'pool_code') || '',
    poolName: pick(row, 'pool_name') || '',
    workshop: pick(row, 'workshop') || '',
    metricCode: pick(row, 'metric_code') || '',
    metricName: pick(row, 'metric_name') || '',
    currentValue: pick(row, 'current_value') || '',
    thresholdValue: pick(row, 'threshold_value') || '',
    unit: pick(row, 'unit') || '',
    alarmReason: pick(row, 'alarm_reason') || '',
    aiSuggestion: pick(row, 'ai_suggestion') || '',
    confidence: num(pick(row, 'confidence')),
    diagnosisBizId: pick(row, 'diagnosis_biz_id') || '',
    knowledgeBizId: pick(row, 'knowledge_biz_id') || '',
    alarmTime: pick(row, 'alarm_time') || '',
    isMock: String(pick(row, 'is_mock') || '1') === '1',
    sourceSystem: pick(row, 'source_system') || '',
    externalId: pick(row, 'external_id') || ''
  }
}

async function listAiAlarms(query = {}) {
  let sql = `select * from ${NEW.aiAlarm} where isDel=0 and biz_id not in ('__bootstrap__','__link_migrate__')`
  if (query.status) sql += ` and alarm_status='${esc(query.status)}'`
  if (query.category) sql += ` and category='${esc(query.category)}'`
  if (query.poolCode) sql += ` and pool_code like '%${esc(query.poolCode)}%'`
  sql += ' order by Id desc'
  const rows = await queryOne(sql)
  return rows.map(mapAiAlarm)
}

/**
 * AI 报警一律写 mock 到 fd_ai_alarm（新建表）。
 * 同池同指标同日已有 open 记录则不重复写入。
 */
async function ensureAiAlarmMock(payload = {}) {
  const poolCode = payload.poolCode || ''
  const metricCode = payload.metricCode || ''
  const today = now().slice(0, 10)
  if (poolCode && metricCode) {
    const existed = await queryOne(
      `select biz_id from ${NEW.aiAlarm} where isDel=0 and pool_code='${esc(poolCode)}' and metric_code='${esc(metricCode)}' and alarm_status='open' and alarm_time like '${esc(today)}%' limit 1`
    )
    if (existed[0]) return mapAiAlarm({ ...payload, biz_id: pick(existed[0], 'biz_id') })
  }

  const bizId = `AIA-${Date.now()}`
  const row = {
    biz_id: bizId,
    alarm_code: payload.alarmCode || `AI-${(metricCode || 'GEN').toUpperCase()}-${Date.now()}`,
    alarm_title: payload.alarmTitle || `AI预警：${payload.metricName || metricCode || '异常'}`,
    category: payload.category || 'water',
    priority: payload.priority || 'medium',
    alarm_status: payload.alarmStatus || 'open',
    pool_code: poolCode,
    pool_name: payload.poolName || '',
    workshop: payload.workshop || '',
    system_name: payload.systemName || '',
    location_code: payload.locationCode || '',
    batch_code: payload.batchCode || '',
    metric_code: metricCode,
    metric_name: payload.metricName || '',
    current_value: String(payload.currentValue != null ? payload.currentValue : ''),
    threshold_value: String(payload.thresholdValue != null ? payload.thresholdValue : ''),
    unit: payload.unit || '',
    alarm_reason: payload.alarmReason || '【Mock AI】系统自动生成的模拟报警',
    ai_suggestion: payload.aiSuggestion || '',
    confidence: String(payload.confidence != null ? payload.confidence : '75'),
    diagnosis_biz_id: payload.diagnosisBizId || '',
    knowledge_biz_id: payload.knowledgeBizId || '',
    alarm_time: payload.alarmTime || now(),
    is_mock: '1',
    source_system: 'fish-disease-ai-mock',
    external_id: payload.externalId || '',
    ref_table: payload.refTable || '',
    ref_biz_id: payload.refBizId || ''
  }
  await insertRow(NEW.aiAlarm, row)
  return mapAiAlarm(row)
}

async function createAiAlarm(payload) {
  return ensureAiAlarmMock(payload)
}

async function ackAiAlarm(id) {
  const rows = await queryOne(
    `select * from ${NEW.aiAlarm} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  if (!rows[0]) return null
  await updateFieldsByBizId(NEW.aiAlarm, id, { alarm_status: 'acked' })
  return mapAiAlarm({ ...rows[0], alarm_status: 'acked' })
}

/* ---------------- fd_* CRUD helpers ---------------- */

function mapSpecies(row) {
  const waterType = pick(row, 'water_type') || 'freshwater'
  return {
    id: pick(row, 'biz_id'),
    name: pick(row, 'name') || '',
    waterType,
    waterTypeLabel: waterTypeMap[waterType] || waterType,
    createdAt: pick(row, 'created_at') || ''
  }
}

async function listFishSpecies() {
  const rows = await queryOne(
    `select * from ${NEW.species} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc`
  )
  return rows.map(mapSpecies)
}

async function createFishSpecies(payload) {
  const item = {
    biz_id: `SP-${Date.now()}`,
    name: payload.name,
    fish_species: payload.name,
    water_type: payload.waterType || 'freshwater',
    source_system: payload.sourceSystem || 'fish-disease',
    external_id: payload.externalId || '',
    ref_table: payload.refTable || '',
    ref_biz_id: payload.refBizId || '',
    created_at: now()
  }
  await insertRow(NEW.species, item)
  return mapSpecies(item)
}

async function updateFishSpecies(id, payload) {
  const fields = {}
  if (payload.name != null) fields.name = payload.name
  if (payload.waterType != null) fields.water_type = payload.waterType
  await updateFieldsByBizId(NEW.species, id, fields)
  const rows = await queryOne(
    `select * from ${NEW.species} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  return rows[0] ? mapSpecies(rows[0]) : null
}

async function deleteFishSpecies(id) {
  const rows = await queryOne(
    `select Id from ${NEW.species} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  if (!rows[0]) return false
  await softDeleteByBizId(NEW.species, id)
  return true
}

function mapDiseaseType(row) {
  return {
    id: pick(row, 'biz_id'),
    name: pick(row, 'name') || '',
    category: pick(row, 'category') || '',
    description: pick(row, 'description') || '',
    createdAt: pick(row, 'created_at') || ''
  }
}

async function listDiseaseTypes() {
  const rows = await queryOne(
    `select * from ${NEW.diseaseTypes} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc`
  )
  return rows.map(mapDiseaseType)
}

async function createDiseaseType(payload) {
  const item = {
    biz_id: `DT-${Date.now()}`,
    name: payload.name,
    category: payload.category || '',
    description: payload.description || '',
    created_at: now()
  }
  await insertRow(NEW.diseaseTypes, item)
  return mapDiseaseType(item)
}

async function updateDiseaseType(id, payload) {
  const fields = {}
  if (payload.name != null) fields.name = payload.name
  if (payload.category != null) fields.category = payload.category
  if (payload.description != null) fields.description = payload.description
  await updateFieldsByBizId(NEW.diseaseTypes, id, fields)
  const rows = await queryOne(
    `select * from ${NEW.diseaseTypes} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  return rows[0] ? mapDiseaseType(rows[0]) : null
}

async function deleteDiseaseType(id) {
  const rows = await queryOne(
    `select Id from ${NEW.diseaseTypes} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  if (!rows[0]) return false
  await softDeleteByBizId(NEW.diseaseTypes, id)
  return true
}

function mapEquipment(row) {
  return {
    id: pick(row, 'biz_id'),
    code: pick(row, 'code') || '',
    name: pick(row, 'name') || '',
    type: pick(row, 'type') || '',
    frequency: pick(row, 'frequency') || '',
    location: pick(row, 'location') || '',
    createdAt: pick(row, 'created_at') || ''
  }
}

async function listEquipments() {
  const rows = await queryOne(
    `select * from ${NEW.equipments} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc`
  )
  return rows.map(mapEquipment)
}

async function createEquipment(payload) {
  const item = {
    biz_id: `EQ-${Date.now()}`,
    code: payload.code,
    device_code: payload.deviceCode || payload.code,
    name: payload.name || '',
    type: payload.type || '',
    frequency: payload.frequency || '',
    location: payload.location || '',
    pool_code: payload.poolCode || payload.pondCode || '',
    pool_name: payload.poolName || '',
    workshop: payload.workshop || '',
    system_name: payload.systemName || '',
    location_code: payload.locationCode || '',
    batch_code: payload.batchCode || '',
    source_system: payload.sourceSystem || 'fish-disease',
    external_id: payload.externalId || '',
    ref_table: payload.refTable || '',
    ref_biz_id: payload.refBizId || '',
    created_at: now()
  }
  await insertRow(NEW.equipments, item)
  return mapEquipment(item)
}

async function updateEquipment(id, payload) {
  const fields = {}
  ;['code', 'name', 'type', 'frequency', 'location'].forEach(k => {
    if (payload[k] != null) fields[k] = payload[k]
  })
  await updateFieldsByBizId(NEW.equipments, id, fields)
  const rows = await queryOne(
    `select * from ${NEW.equipments} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  return rows[0] ? mapEquipment(rows[0]) : null
}

async function deleteEquipment(id) {
  const rows = await queryOne(
    `select Id from ${NEW.equipments} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  if (!rows[0]) return false
  await softDeleteByBizId(NEW.equipments, id)
  return true
}

function mapKnowledge(row) {
  return {
    id: pick(row, 'biz_id'),
    name: pick(row, 'name') || '',
    category: pick(row, 'category') || '',
    symptoms: parseJson(pick(row, 'symptoms'), []),
    description: pick(row, 'description') || '',
    treatment: pick(row, 'treatment') || '',
    prevention: pick(row, 'prevention') || '',
    medicine: pick(row, 'medicine') || '',
    riskLevel: pick(row, 'risk_level') || 'medium'
  }
}

async function listDiseaseKnowledge() {
  const rows = await queryOne(
    `select * from ${NEW.diseaseKnowledge} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc`
  )
  return rows.map(mapKnowledge)
}

async function getKnowledge(id) {
  const rows = await queryOne(
    `select * from ${NEW.diseaseKnowledge} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  return rows[0] ? mapKnowledge(rows[0]) : null
}

async function createDiseaseKnowledge(payload = {}) {
  const item = {
    biz_id: `DIS-${Date.now()}`,
    name: payload.name || '未命名病害',
    category: payload.category || '其他',
    symptoms: JSON.stringify(
      Array.isArray(payload.symptoms)
        ? payload.symptoms
        : String(payload.symptoms || '')
            .split(/[,，、]/)
            .map(s => s.trim())
            .filter(Boolean)
    ),
    description: payload.description || '',
    treatment: payload.treatment || '',
    prevention: payload.prevention || '',
    medicine: payload.medicine || '',
    risk_level: payload.riskLevel || 'medium'
  }
  await insertRow(NEW.diseaseKnowledge, item)
  return mapKnowledge(item)
}

async function updateDiseaseKnowledge(id, payload = {}) {
  const fields = {}
  if (payload.name != null) fields.name = payload.name
  if (payload.category != null) fields.category = payload.category
  if (payload.symptoms != null) {
    fields.symptoms = JSON.stringify(
      Array.isArray(payload.symptoms)
        ? payload.symptoms
        : String(payload.symptoms || '')
            .split(/[,，、]/)
            .map(s => s.trim())
            .filter(Boolean)
    )
  }
  if (payload.description != null) fields.description = payload.description
  if (payload.treatment != null) fields.treatment = payload.treatment
  if (payload.prevention != null) fields.prevention = payload.prevention
  if (payload.medicine != null) fields.medicine = payload.medicine
  if (payload.riskLevel != null) fields.risk_level = payload.riskLevel
  await updateFieldsByBizId(NEW.diseaseKnowledge, id, fields)
  return getKnowledge(id)
}

async function deleteDiseaseKnowledge(id) {
  const rows = await queryOne(
    `select Id from ${NEW.diseaseKnowledge} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  if (!rows[0]) return false
  await softDeleteByBizId(NEW.diseaseKnowledge, id)
  return true
}

function mapMessage(row) {
  return {
    id: pick(row, 'biz_id'),
    role: pick(row, 'role') || 'expert',
    senderId: pick(row, 'sender_id') || '',
    senderName: pick(row, 'sender_name') || '',
    toUserId: pick(row, 'to_user_id') || '',
    toName: pick(row, 'to_name') || '',
    content: pick(row, 'content') || '',
    msgType: pick(row, 'msg_type') || 'text',
    fileName: pick(row, 'file_name') || '',
    fileUrl: pick(row, 'file_url') || '',
    fileSize: num(pick(row, 'file_size'), 0),
    duration: num(pick(row, 'duration'), 0),
    time: pick(row, 'msg_time') || ''
  }
}

async function listMessages(diagnosisId) {
  const rows = await queryOne(
    `select * from ${NEW.diagnosisMessages} where isDel=0 and diagnosis_id='${esc(diagnosisId)}' and biz_id<>'__bootstrap__' order by Id asc`
  )
  return rows.map(mapMessage)
}

function mapDiagnosis(row, messages = []) {
  const status = pick(row, 'status') || 'pending'
  const severity = pick(row, 'severity') || 'medium'
  const poolCode = pick(row, 'pool_code') || pick(row, 'pond_code') || ''
  return {
    id: pick(row, 'biz_id'),
    caseNo: pick(row, 'case_no') || '',
    pondCode: poolCode,
    poolCode,
    poolName: pick(row, 'pool_name') || '',
    workshop: pick(row, 'workshop') || '',
    systemName: pick(row, 'system_name') || '',
    locationCode: pick(row, 'location_code') || '',
    batchCode: pick(row, 'batch_code') || '',
    speciesBizId: pick(row, 'species_biz_id') || '',
    fishSpecies: pick(row, 'fish_species') || pick(row, 'category') || '',
    category: pick(row, 'category') || '',
    reporter: pick(row, 'reporter') || '',
    physicalSymptoms: parseJson(pick(row, 'physical_symptoms'), []),
    behavioralSymptoms: parseJson(pick(row, 'behavioral_symptoms'), []),
    waterTemp: num(pick(row, 'water_temp')),
    dissolvedOxygen: num(pick(row, 'dissolved_oxygen')),
    ammonia: num(pick(row, 'ammonia')),
    suspectedDisease: pick(row, 'suspected_disease') || '',
    diseaseId: pick(row, 'disease_id') || pick(row, 'knowledge_biz_id') || '',
    knowledgeBizId: pick(row, 'knowledge_biz_id') || pick(row, 'disease_id') || '',
    confidence: num(pick(row, 'confidence')),
    severity,
    severityLabel: severityMap[severity] || severity,
    status,
    statusLabel: diseaseStatusMap[status] || status,
    aiSuggestion: pick(row, 'ai_suggestion') || '',
    treatment: pick(row, 'treatment') || '',
    veterinarian: pick(row, 'veterinarian') || '',
    assigneeId: pick(row, 'assignee_id') || '',
    assigneeName: pick(row, 'assignee_name') || '',
    acceptedAt: pick(row, 'accepted_at') || '',
    conclusion: pick(row, 'conclusion') || '',
    diagnosisDate: pick(row, 'diagnosis_date') || '',
    resolvedDate: pick(row, 'resolved_date') || '',
    sourceSystem: pick(row, 'source_system') || '',
    externalId: pick(row, 'external_id') || '',
    createdAt: pick(row, 'created_at') || '',
    updatedAt: pick(row, 'updated_at') || '',
    messages
  }
}

async function getDiagnosis(id) {
  const rows = await queryOne(
    `select * from ${NEW.diagnosisRecords} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  if (!rows[0]) return null
  const messages = await listMessages(id)
  return mapDiagnosis(rows[0], messages)
}

async function listDiagnosis(query = {}) {
  const rows = await queryOne(
    `select * from ${NEW.diagnosisRecords} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc`
  )
  let list = []
  for (const row of rows) {
    const id = pick(row, 'biz_id')
    const messages = await listMessages(id)
    list.push(mapDiagnosis(row, messages))
  }

  if (query.tab === 'pending') list = list.filter(i => i.status === 'pending')
  else if (query.tab === 'active') {
    list = list.filter(i => i.status === 'diagnosing' || i.status === 'treating')
  } else if (query.tab === 'done') list = list.filter(i => i.status === 'resolved')

  if (query.pondCode) list = list.filter(i => i.pondCode.includes(query.pondCode))
  if (query.category) list = list.filter(i => i.category === query.category)
  if (query.status) list = list.filter(i => i.status === query.status)
  if (query.severity) list = list.filter(i => i.severity === query.severity)
  if (query.keyword) {
    const kw = query.keyword.toLowerCase()
    list = list.filter(
      i =>
        i.caseNo.toLowerCase().includes(kw) ||
        (i.suspectedDisease || '').toLowerCase().includes(kw) ||
        (i.pondCode || '').toLowerCase().includes(kw)
    )
  }

  return list.sort((a, b) => {
    const ta = a.updatedAt || a.createdAt || ''
    const tb = b.updatedAt || b.createdAt || ''
    return tb > ta ? 1 : tb < ta ? -1 : 0
  })
}

async function getSummary() {
  const list = await listDiagnosis()
  return {
    active: list.filter(i => i.status === 'diagnosing' || i.status === 'treating').length,
    critical: list.filter(i => i.severity === 'critical' && i.status !== 'resolved').length,
    treating: list.filter(i => i.status === 'treating').length,
    resolved: list.filter(i => i.status === 'resolved').length,
    pending: list.filter(i => i.status === 'pending' || i.status === 'diagnosing').length,
    total: list.length
  }
}

async function getMeta() {
  const species = await listFishSpecies()
  return {
    categories: species.map(i => i.name),
    physicalSymptoms: [...physicalSymptoms],
    behavioralSymptoms: [...behavioralSymptoms],
    diseaseStatusMap: { ...diseaseStatusMap },
    severityMap: { ...severityMap },
    pondStatusMap: { ...pondStatusMap },
    waterTypeMap: { ...waterTypeMap }
  }
}

function matchDiseaseSync(knowledge, physical = [], behavioral = [], water = {}) {
  const selected = [...physical, ...behavioral]
  if (!selected.length) return null

  let best = null
  let bestScore = 0

  knowledge.forEach(disease => {
    const matched = (disease.symptoms || []).filter(s => selected.includes(s))
    let score = matched.length / Math.max((disease.symptoms || []).length, 1)

    if (water.dissolvedOxygen != null && water.dissolvedOxygen < 5) {
      if (disease.id === 'DIS-004') score += 0.3
    }
    if (water.ammonia != null && water.ammonia > 0.5) {
      if (disease.id === 'DIS-002' || disease.id === 'DIS-003') score += 0.2
    }

    if (score > bestScore) {
      bestScore = score
      best = disease
    }
  })

  if (!best || bestScore < 0.2) return null
  const confidence = Math.min(95, Math.round(bestScore * 100 + selected.length * 3))
  return {
    diseaseId: best.id,
    suspectedDisease: best.name,
    confidence,
    severity: best.riskLevel,
    aiSuggestion: `根据症状匹配，疑似 ${best.name}（${best.category}），建议：${(best.treatment || '').slice(0, 40)}…`,
    treatment: best.treatment
  }
}

async function createDiagnosis(payload) {
  const datePart = now().slice(0, 10).replace(/-/g, '')
  const bizId = `DX-${Date.now()}`
  const caseNo = payload.caseNo || `DX-${datePart}-${String(Date.now()).slice(-3)}`
  const poolCode = payload.poolCode || payload.pondCode || ''
  const speciesName = payload.category || payload.fishSpecies || ''
  const diseaseId = payload.diseaseId || payload.knowledgeBizId || ''
  const row = {
    biz_id: bizId,
    case_no: caseNo,
    pond_code: poolCode,
    pool_code: poolCode,
    pool_name: payload.poolName || '',
    workshop: payload.workshop || '',
    system_name: payload.systemName || '',
    location_code: payload.locationCode || '',
    batch_code: payload.batchCode || '',
    species_biz_id: payload.speciesBizId || '',
    fish_species: speciesName,
    category: speciesName,
    reporter: payload.reporter || '张工',
    physical_symptoms: JSON.stringify(payload.physicalSymptoms || []),
    behavioral_symptoms: JSON.stringify(payload.behavioralSymptoms || []),
    water_temp: String(num(payload.waterTemp)),
    dissolved_oxygen: String(num(payload.dissolvedOxygen)),
    ammonia: String(num(payload.ammonia)),
    suspected_disease: payload.suspectedDisease || '',
    disease_id: diseaseId,
    knowledge_biz_id: diseaseId,
    confidence: String(payload.confidence || 0),
    severity: payload.severity || 'medium',
    status: payload.status || 'pending',
    ai_suggestion: payload.aiSuggestion || '',
    treatment: payload.treatment || '',
    veterinarian: payload.veterinarian || '',
    assignee_id: payload.assigneeId || '',
    assignee_name: payload.assigneeName || '',
    accepted_at: payload.acceptedAt || '',
    conclusion: payload.conclusion || '',
    diagnosis_date: payload.diagnosisDate || now().slice(0, 10),
    resolved_date: '',
    source_system: payload.sourceSystem || 'fish-disease',
    external_id: payload.externalId || '',
    ref_table: payload.refTable || '',
    ref_biz_id: payload.refBizId || '',
    created_at: now(),
    updated_at: now()
  }
  await insertRow(NEW.diagnosisRecords, row)

  const welcome = {
    biz_id: `m-${Date.now()}`,
    diagnosis_id: bizId,
    role: 'worker',
    sender_id: '',
    sender_name: row.reporter,
    to_user_id: '',
    to_name: '',
    content: '已提交诊断申请，等待专家诊断',
    msg_type: 'text',
    file_name: '',
    file_url: '',
    file_size: '0',
    duration: '0',
    msg_time: nowTime(),
    source_system: 'fish-disease',
    external_id: '',
    ref_table: '',
    ref_biz_id: ''
  }
  await insertRow(NEW.diagnosisMessages, welcome)
  return getDiagnosis(bizId)
}

async function updateDiagnosis(id, payload) {
  const existing = await getDiagnosis(id)
  if (!existing) return null
  const fields = {
    updated_at: now()
  }
  const map = {
    pondCode: 'pond_code',
    category: 'category',
    reporter: 'reporter',
    suspectedDisease: 'suspected_disease',
    diseaseId: 'disease_id',
    confidence: 'confidence',
    severity: 'severity',
    status: 'status',
    aiSuggestion: 'ai_suggestion',
    treatment: 'treatment',
    veterinarian: 'veterinarian',
    assigneeId: 'assignee_id',
    assigneeName: 'assignee_name',
    acceptedAt: 'accepted_at',
    conclusion: 'conclusion',
    diagnosisDate: 'diagnosis_date',
    resolvedDate: 'resolved_date',
    waterTemp: 'water_temp',
    dissolvedOxygen: 'dissolved_oxygen',
    ammonia: 'ammonia'
  }
  Object.entries(map).forEach(([from, to]) => {
    if (payload[from] !== undefined) fields[to] = String(payload[from] == null ? '' : payload[from])
  })
  if (payload.physicalSymptoms) fields.physical_symptoms = JSON.stringify(payload.physicalSymptoms)
  if (payload.behavioralSymptoms) {
    fields.behavioral_symptoms = JSON.stringify(payload.behavioralSymptoms)
  }
  if (payload.status === 'resolved' && !payload.resolvedDate) {
    fields.resolved_date = now().slice(0, 10)
  }
  if ((payload.status === 'diagnosing' || payload.status === 'treating') && !existing.acceptedAt && !payload.acceptedAt) {
    fields.accepted_at = now()
  }
  if ((payload.status === 'diagnosing' || payload.status === 'treating') && !payload.assigneeName && !existing.assigneeName) {
    fields.assignee_name = existing.reporter || ''
  }
  await updateFieldsByBizId(NEW.diagnosisRecords, id, fields)
  const updated = await getDiagnosis(id)
  if (updated && (updated.status === 'diagnosing' || updated.status === 'treating' || updated.status === 'resolved')) {
    try {
      await archiveFromDiagnosis(updated)
    } catch (e) {
      console.error('[pmis] archiveFromDiagnosis failed', e && e.message)
    }
  }
  return updated
}

async function archiveFromDiagnosis(diagnosis) {
  if (!diagnosis || !diagnosis.id || diagnosis.status === 'pending') return null
  const bizId = `AL-${diagnosis.id}`
  const rows = await queryOne(
    `select * from ${NEW.archivedCases} where isDel=0 and (biz_id='${esc(bizId)}' or diagnosis_biz_id='${esc(diagnosis.id)}') limit 1`
  )
  const tags = [
    ...(diagnosis.physicalSymptoms || []),
    ...(diagnosis.behavioralSymptoms || []).slice(0, 2),
    diagnosis.category,
    diagnosis.pondCode
  ].filter(Boolean)
  const item = {
    biz_id: rows[0] ? pick(rows[0], 'biz_id') : bizId,
    archive_no: rows[0] ? pick(rows[0], 'archive_no') : `AL-${diagnosis.caseNo || diagnosis.id}`,
    pond_code: diagnosis.pondCode || '',
    species: diagnosis.category || '',
    disease_name: diagnosis.suspectedDisease || diagnosis.conclusion || '待完善病害名称',
    category: diagnosis.category || '未分类',
    expert: diagnosis.veterinarian || '',
    archive_date: String(diagnosis.acceptedAt || diagnosis.resolvedDate || now()).slice(0, 10),
    recovery_status: diagnosis.status === 'resolved' ? 'good' : 'fair',
    match_rate: String(Number(diagnosis.confidence) || (diagnosis.status === 'resolved' ? 80 : 60)),
    tags: JSON.stringify(tags),
    summary: [
      `来源诊断 ${diagnosis.caseNo || diagnosis.id}`,
      diagnosis.status === 'resolved' ? '已完成诊断' : '已受理进行中',
      diagnosis.assigneeName ? `负责技工:${diagnosis.assigneeName}` : '',
      diagnosis.treatment || diagnosis.aiSuggestion || ''
    ].filter(Boolean).join('；'),
    diagnosis_basis: diagnosis.conclusion || diagnosis.suspectedDisease || '受理归档，待补充诊断依据',
    cause_analysis: (diagnosis.behavioralSymptoms || []).join('、') || '待分析',
    treatment: diagnosis.treatment || '',
    treatment_steps: JSON.stringify(
      diagnosis.treatment
        ? [{ title: '治疗方案', content: diagnosis.treatment }]
        : [{ title: '后续分析', content: '已受理案例，预留给 AI 初判与经验沉淀' }]
    ),
    diagnosis_biz_id: diagnosis.id,
    knowledge_biz_id: diagnosis.diseaseId || '',
    created_at: rows[0] ? pick(rows[0], 'created_at') || now() : now()
  }
  if (rows[0]) {
    await updateFieldsByBizId(NEW.archivedCases, item.biz_id, item)
  } else {
    await insertRow(NEW.archivedCases, item)
  }
  return getArchivedCase(item.biz_id)
}

async function deleteDiagnosis(id) {
  const existing = await getDiagnosis(id)
  if (!existing) return false
  await softDeleteByBizId(NEW.diagnosisRecords, id)
  await execute([
    `update ${NEW.diagnosisMessages} set isDel=1 where isDel=0 and diagnosis_id='${esc(id)}'`
  ])
  return true
}

async function runDiagnosis(payload) {
  const knowledge = await listDiseaseKnowledge()
  const match = matchDiseaseSync(
    knowledge,
    payload.physicalSymptoms || [],
    payload.behavioralSymptoms || [],
    { dissolvedOxygen: payload.dissolvedOxygen, ammonia: payload.ammonia }
  )
  if (!match) {
    return {
      suspectedDisease: '未能匹配',
      confidence: 0,
      severity: 'medium',
      aiSuggestion: '症状信息不足，建议补充观察并联系水产兽医进一步确认。',
      treatment: '',
      alternatives: []
    }
  }
  const selected = [...(payload.physicalSymptoms || []), ...(payload.behavioralSymptoms || [])]
  const alternatives = knowledge
    .filter(d => d.id !== match.diseaseId)
    .map(d => {
      const matched = (d.symptoms || []).filter(s => selected.includes(s))
      return {
        name: d.name,
        matchRate: Math.round((matched.length / Math.max((d.symptoms || []).length, 1)) * 100)
      }
    })
    .filter(a => a.matchRate > 20)
    .sort((a, b) => b.matchRate - a.matchRate)
    .slice(0, 3)

  // 诊断命中高风险 → 写 AI 病害报警 mock
  if (match.severity === 'critical' || match.severity === 'high' || match.confidence >= 70) {
    try {
      await ensureAiAlarmMock({
        category: 'disease',
        priority: match.severity === 'critical' ? 'critical' : 'high',
        poolCode: payload.pondCode || payload.poolCode || '',
        metricCode: 'disease',
        metricName: '病害匹配',
        currentValue: String(match.confidence),
        thresholdValue: '70',
        unit: '%',
        alarmTitle: `AI预警：疑似${match.suspectedDisease}`,
        alarmReason: `【Mock AI】症状匹配 ${match.suspectedDisease}，置信度 ${match.confidence}%`,
        aiSuggestion: match.aiSuggestion || match.treatment || '',
        confidence: String(match.confidence),
        knowledgeBizId: match.diseaseId,
        diagnosisBizId: payload.diagnosisId || '',
        refTable: NEW.diseaseKnowledge,
        refBizId: match.diseaseId
      })
    } catch (e) {
      console.error('[ai-alarm]', e.message || e)
    }
  }

  return { ...match, alternatives }
}

async function appendDiagnosisMessage(id, payload = {}) {
  const record = await getDiagnosis(id)
  if (!record) return null
  const content = String(payload.content || payload.message || '').trim()
  if (!content) return null

  const role = payload.role || 'expert'
  const senderName =
    payload.senderName ||
    (role === 'expert' ? record.veterinarian || '陈专家' : record.reporter || '工人')

  const messageRow = {
    biz_id: `m-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
    diagnosis_id: id,
    role,
    sender_id: payload.senderId || '',
    sender_name: senderName,
    to_user_id: payload.toUserId || '',
    to_name: payload.toName || '',
    content,
    msg_type: payload.msgType || 'text',
    file_name: payload.fileName || '',
    file_url: payload.fileUrl || '',
    file_size: String(payload.fileSize || 0),
    duration: String(payload.duration || 0),
    msg_time: nowTime()
  }
  await insertRow(NEW.diagnosisMessages, messageRow)

  const fields = { updated_at: now() }
  if (record.status === 'pending') {
    fields.status = 'diagnosing'
    fields.veterinarian = record.veterinarian || '陈专家'
  }
  await updateFieldsByBizId(NEW.diagnosisRecords, id, fields)

  const next = await getDiagnosis(id)
  return {
    record: next,
    message: mapMessage(messageRow)
  }
}

async function chatDiagnosis(id, payload) {
  const record = await getDiagnosis(id)
  if (!record) return null

  const physical = payload.physicalSymptoms || record.physicalSymptoms || []
  const behavioral = payload.behavioralSymptoms || record.behavioralSymptoms || []
  const waterTemp = payload.waterTemp ?? record.waterTemp
  const dissolvedOxygen = payload.dissolvedOxygen ?? record.dissolvedOxygen
  const ammonia = payload.ammonia ?? record.ammonia

  if (payload.message) {
    await appendDiagnosisMessage(id, {
      role: payload.role || 'expert',
      senderId: payload.senderId || '',
      senderName: payload.senderName || record.veterinarian || '陈专家',
      toUserId: payload.toUserId || '',
      toName: payload.toName || '',
      content: payload.message,
      msgType: payload.msgType || 'text',
      fileName: payload.fileName || '',
      fileUrl: payload.fileUrl || '',
      fileSize: payload.fileSize || 0,
      duration: payload.duration || 0
    })
  }

  const symptomParts = [...physical, ...behavioral]
  if (symptomParts.length && !payload.message) {
    await appendDiagnosisMessage(id, {
      role: 'expert',
      senderName: payload.senderName || record.veterinarian || '陈专家',
      content: `已记录症状：${symptomParts.join('、')}`
    })
  }

  const knowledge = await listDiseaseKnowledge()
  const match = matchDiseaseSync(knowledge, physical, behavioral, { dissolvedOxygen, ammonia })
  const patch = {
    physicalSymptoms: physical,
    behavioralSymptoms: behavioral,
    waterTemp,
    dissolvedOxygen,
    ammonia
  }

  if (match && !payload.message) {
    patch.suspectedDisease = match.suspectedDisease
    patch.diseaseId = match.diseaseId
    patch.confidence = match.confidence
    patch.severity = match.severity
    patch.aiSuggestion = match.aiSuggestion
    patch.treatment = match.treatment
    if (record.status === 'pending') patch.status = 'diagnosing'
    await updateDiagnosis(id, patch)
    await appendDiagnosisMessage(id, {
      role: 'expert',
      senderName: record.veterinarian || '陈专家',
      content: match.aiSuggestion
    })
  } else if (!payload.message) {
    await updateDiagnosis(id, patch)
    await appendDiagnosisMessage(id, {
      role: 'expert',
      senderName: record.veterinarian || '陈专家',
      content: '已收到信息，请补充更多症状描述以便进一步诊断。'
    })
  } else {
    if (record.status === 'pending') patch.status = 'diagnosing'
    patch.veterinarian = record.veterinarian || '陈专家'
    await updateDiagnosis(id, patch)
  }

  return getDiagnosis(id)
}

function mapCollection(row) {
  return {
    id: pick(row, 'biz_id'),
    species: pick(row, 'species') || '',
    pondCode: pick(row, 'pond_code') || '',
    frySource: pick(row, 'fry_source') || '',
    diseaseCount: pick(row, 'disease_count') || '',
    feedingMethod: pick(row, 'feeding_method') || '',
    abnormalParts: parseJson(pick(row, 'abnormal_parts'), []),
    abnormalDesc: pick(row, 'abnormal_desc') || '',
    mediaCount: num(pick(row, 'media_count'), 0),
    mediaNames: parseJson(pick(row, 'media_names'), []),
    metrics: parseJson(pick(row, 'metrics'), []),
    createdAt: pick(row, 'created_at') || ''
  }
}

async function listCollectionRecords() {
  const rows = await queryOne(
    `select * from ${NEW.collectionRecords} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc`
  )
  return rows.map(mapCollection)
}

async function createCollectionRecord(payload) {
  const poolCode = payload.poolCode || payload.pondCode || ''
  const speciesName = payload.species || payload.fishSpecies || ''
  const item = {
    biz_id: `COL-${Date.now()}`,
    species: speciesName,
    fish_species: speciesName,
    species_biz_id: payload.speciesBizId || '',
    pond_code: poolCode,
    pool_code: poolCode,
    pool_name: payload.poolName || '',
    workshop: payload.workshop || '',
    system_name: payload.systemName || '',
    location_code: payload.locationCode || '',
    batch_code: payload.batchCode || '',
    fry_source: payload.frySource || '',
    disease_count: payload.diseaseCount || '',
    feeding_method: payload.feedingMethod || '',
    abnormal_parts: JSON.stringify(payload.abnormalParts || []),
    abnormal_desc: payload.abnormalDesc || '',
    media_count: String(payload.mediaCount || 0),
    media_names: JSON.stringify(payload.mediaNames || []),
    metrics: JSON.stringify(payload.metrics || []),
    source_system: payload.sourceSystem || 'fish-disease',
    external_id: payload.externalId || '',
    ref_table: payload.refTable || '',
    ref_biz_id: payload.refBizId || '',
    created_at: now()
  }
  await insertRow(NEW.collectionRecords, item)
  return mapCollection(item)
}

function mapArchive(row) {
  const matchRate = num(pick(row, 'match_rate'))
  const recoveryStatus = pick(row, 'recovery_status') || 'fair'
  return {
    id: pick(row, 'biz_id'),
    archiveNo: pick(row, 'archive_no') || '',
    pondCode: pick(row, 'pond_code') || '',
    species: pick(row, 'species') || '',
    diseaseName: pick(row, 'disease_name') || '',
    category: pick(row, 'category') || '',
    expert: pick(row, 'expert') || '',
    archiveDate: pick(row, 'archive_date') || '',
    recoveryStatus,
    recoveryLabel: recoveryStatusMap[recoveryStatus] || recoveryStatus,
    matchRate,
    isHighMatch: matchRate >= 80,
    tags: parseJson(pick(row, 'tags'), []),
    summary: pick(row, 'summary') || '',
    diagnosisBasis: pick(row, 'diagnosis_basis') || '',
    causeAnalysis: pick(row, 'cause_analysis') || '',
    treatment: pick(row, 'treatment') || '',
    treatmentSteps: parseJson(pick(row, 'treatment_steps'), []),
    createdAt: pick(row, 'created_at') || ''
  }
}

async function listArchivedCases(query = {}) {
  const rows = await queryOne(
    `select * from ${NEW.archivedCases} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc`
  )
  let list = rows.map(mapArchive)
  if (query.tab === 'highMatch') list = list.filter(i => i.isHighMatch)
  if (query.category && query.category !== 'all') {
    list = list.filter(i => i.category === query.category)
  }
  if (query.keyword) {
    const kw = query.keyword.toLowerCase()
    list = list.filter(
      i =>
        i.archiveNo.toLowerCase().includes(kw) ||
        i.diseaseName.toLowerCase().includes(kw) ||
        i.species.toLowerCase().includes(kw) ||
        (i.tags || []).some(t => String(t).toLowerCase().includes(kw))
    )
  }
  return list.sort((a, b) => {
    const ta = a.archiveDate || a.createdAt || ''
    const tb = b.archiveDate || b.createdAt || ''
    return tb > ta ? 1 : tb < ta ? -1 : 0
  })
}

async function getArchivedCase(id) {
  const rows = await queryOne(
    `select * from ${NEW.archivedCases} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  return rows[0] ? mapArchive(rows[0]) : null
}

async function getArchiveSummary() {
  const list = await listArchivedCases()
  return {
    total: list.length,
    highMatch: list.filter(i => i.isHighMatch).length,
    estimatedOpen: '2025年Q1'
  }
}

async function getArchiveCategories() {
  const list = await listArchivedCases()
  return [...new Set(list.map(i => i.category).filter(Boolean))]
}

/* operation logs / settings optional on PMIS; users stay local */

async function addOperationLog(payload = {}) {
  const item = {
    biz_id: `L-${Date.now()}`,
    operator_name: payload.operator || '系统',
    action: payload.action || '',
    log_time: now(),
    ip: payload.ip || '127.0.0.1',
    result: payload.result || 'success'
  }
  try {
    await insertRow(NEW.operationLogs, item)
  } catch (e) {
    // non-fatal
  }
  return {
    id: item.biz_id,
    operator: item.operator_name,
    action: item.action,
    time: item.log_time,
    ip: item.ip,
    result: item.result
  }
}

async function listOperationLogs(query = {}) {
  const rows = await queryOne(
    `select * from ${NEW.operationLogs} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc`
  )
  let list = rows.map(row => ({
    id: pick(row, 'biz_id'),
    operator: pick(row, 'operator_name') || '',
    action: pick(row, 'action') || '',
    time: pick(row, 'log_time') || '',
    ip: pick(row, 'ip') || '',
    result: pick(row, 'result') || 'success',
    resultLabel: pick(row, 'result') === 'success' ? '成功' : '失败'
  }))
  // keep filter parity loosely
  if (query.range === 'today') {
    const todayStr = now().slice(0, 10)
    list = list.filter(i => String(i.time || '').startsWith(todayStr))
  }
  return list
}

async function getSystemSettings() {
  const rows = await queryOne(
    `select * from ${NEW.systemSettings} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc limit 1`
  )
  if (!rows[0]) return {}
  return parseJson(pick(rows[0], 'settings_json'), {})
}

async function updateSystemSettings(payload) {
  const current = await getSystemSettings()
  const next = { ...current, ...payload }
  const rows = await queryOne(
    `select biz_id from ${NEW.systemSettings} where isDel=0 and biz_id<>'__bootstrap__' order by Id desc limit 1`
  )
  if (rows[0]) {
    await updateFieldsByBizId(NEW.systemSettings, pick(rows[0], 'biz_id'), {
      settings_json: JSON.stringify(next)
    })
  } else {
    await insertRow(NEW.systemSettings, {
      biz_id: 'SETTINGS-001',
      settings_json: JSON.stringify(next)
    })
  }
  return next
}

module.exports = {
  getMeta,
  getSummary,
  listDiagnosis,
  getDiagnosis,
  createDiagnosis,
  updateDiagnosis,
  deleteDiagnosis,
  runDiagnosis,
  chatDiagnosis,
  appendDiagnosisMessage,
  listPonds,
  createPond,
  updatePond,
  deletePond,
  listFishSpecies,
  createFishSpecies,
  updateFishSpecies,
  deleteFishSpecies,
  listDiseaseTypes,
  createDiseaseType,
  updateDiseaseType,
  deleteDiseaseType,
  listEquipments,
  createEquipment,
  updateEquipment,
  deleteEquipment,
  getMonitorData,
  listCollectionRecords,
  createCollectionRecord,
  getArchiveSummary,
  listArchivedCases,
  getArchivedCase,
  getArchiveCategories,
  archiveFromDiagnosis,
  listDiseaseKnowledge,
  getKnowledge,
  createDiseaseKnowledge,
  updateDiseaseKnowledge,
  deleteDiseaseKnowledge,
  addOperationLog,
  listOperationLogs,
  getSystemSettings,
  updateSystemSettings,
  listAiAlarms,
  createAiAlarm,
  ackAiAlarm,
  ensureAiAlarmMock
}
