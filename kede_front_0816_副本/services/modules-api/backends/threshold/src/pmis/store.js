const { queryOne, saveInfo, execute, esc } = require('./client')
const { EXISTING, NEW, MODULE_NAMES } = require('./tables')
const { COLUMN_DEFS, buildRowPayload } = require('./schema')
const seed = require('../data/seed')

function clone(data) {
  return JSON.parse(JSON.stringify(data))
}

function nowDate() {
  const d = new Date()
  const p = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`
}

function now() {
  const d = new Date()
  const p = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
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

function num(v, fallback = 0) {
  const n = Number(v)
  return Number.isFinite(n) ? n : fallback
}

async function insertRow(tableName, row) {
  await saveInfo(buildRowPayload(tableName, row, COLUMN_DEFS[tableName]))
}

async function updateFieldsByBizId(tableName, bizId, fields) {
  const sets = Object.entries(fields)
    .map(([k, v]) => `${k}='${esc(v)}'`)
    .join(',')
  if (!sets) return
  await execute([`update ${tableName} set ${sets} where isDel=0 and biz_id='${esc(bizId)}'`])
}

const MODULES = [
  { id: 'water', group: '监测配置', name: '水质指标', icon: 'el-icon-s-data' },
  { id: 'device', group: '监测配置', name: '设备运行', icon: 'el-icon-cpu' },
  { id: 'mortality', group: '鱼类健康', name: '鱼类死亡/死亡率', icon: 'el-icon-warning-outline' },
  { id: 'health', group: '鱼类健康', name: '健康评分', icon: 'el-icon-s-opportunity' },
  { id: 'feeding', group: '鱼类健康', name: '精准投喂', icon: 'el-icon-s-goods' },
  { id: 'alarm', group: '运营管理', name: 'AI 报警', icon: 'el-icon-bell' },
  { id: 'energy', group: '运营管理', name: '能耗管理', icon: 'el-icon-s-finance' },
  { id: 'experiment', group: '实验', name: '实验系统', icon: 'el-icon-s-order' },
  { id: 'security', group: '运营管理', name: '人员安防', icon: 'el-icon-user' },
  { id: 'holding', group: '运营管理', name: '暂养损耗', icon: 'el-icon-box' },
  { id: 'timeout', group: '运营管理', name: '任务超时', icon: 'el-icon-time' },
  { id: 'association', group: '关联配置', name: '数据关联', icon: 'el-icon-share' }
]

function getNav() {
  return MODULES
}

const DEVICE_SECTIONS = ['power', 'pipeline', 'electric', 'levels', 'maintain']

/** 设备运行分区若被错误写成 items[]，从 seed 恢复分区结构 */
function normalizeDeviceModule(device) {
  const base = clone(seed.device)
  if (!device || typeof device !== 'object') return base
  const hasSections = DEVICE_SECTIONS.some(k => Array.isArray(device[k]) && device[k].length)
  if (!hasSections) {
    return { ...base, enabled: device.enabled !== false }
  }
  const next = { enabled: device.enabled !== false }
  DEVICE_SECTIONS.forEach(k => {
    next[k] = Array.isArray(device[k]) && device[k].length ? clone(device[k]) : clone(base[k] || [])
  })
  return next
}

async function loadStoreFromDb() {
  const store = clone(seed)
  const rows = await queryOne(
    `select * from ${NEW.moduleConfig} where isDel=0 and biz_id not in ('__bootstrap__','__link_migrate__')`
  )
  for (const row of rows) {
    const moduleId = pick(row, 'module_id')
    if (!moduleId) continue
    try {
      const parsed = JSON.parse(pick(row, 'config_json') || '{}')
      store[moduleId] = parsed
    } catch (e) {
      /* keep seed */
    }
  }
  if (!store.meta) store.meta = clone(seed.meta)
  store.meta.moduleCount = MODULES.length
  store.device = normalizeDeviceModule(store.device)
  return store
}


async function upsertModule(moduleId, data, meta = {}) {
  const bizId = `TH-MOD-${moduleId}`
  const lastSaved = meta.lastSaved || nowDate()
  const version = meta.version != null ? String(meta.version) : String((seed.meta && seed.meta.version) || '1.0')
  const existed = await queryOne(
    `select biz_id from ${NEW.moduleConfig} where isDel=0 and biz_id='${esc(bizId)}' limit 1`
  )
  const row = {
    biz_id: bizId,
    module_id: moduleId,
    module_name: MODULE_NAMES[moduleId] || moduleId,
    config_json: JSON.stringify(data),
    version,
    last_saved: lastSaved,
    source_system: 'threshold-settings',
    external_id: '',
    ref_table: '',
    ref_biz_id: ''
  }
  if (existed[0]) {
    await updateFieldsByBizId(NEW.moduleConfig, bizId, {
      module_name: row.module_name,
      config_json: row.config_json,
      version: row.version,
      last_saved: row.last_saved
    })
  } else {
    await insertRow(NEW.moduleConfig, row)
  }
}

async function getMeta() {
  const store = await loadStoreFromDb()
  return clone(store.meta)
}

async function getAll() {
  return loadStoreFromDb()
}

async function getModule(id) {
  const store = await loadStoreFromDb()
  if (!store[id]) return null
  return clone(store[id])
}

async function saveModule(id, data) {
  const store = await loadStoreFromDb()
  if (!store[id] && id !== 'meta') return null
  const lastSaved = nowDate()
  await upsertModule(id, id === 'device' ? normalizeDeviceModule(data) : clone(data), {
    lastSaved,
    version: store.meta && store.meta.version
  })
  const meta = { ...(store.meta || seed.meta), lastSaved, moduleCount: MODULES.length }
  await upsertModule('meta', meta, { lastSaved, version: meta.version })
  return clone(data)
}

async function saveAll(payload) {
  const lastSaved = nowDate()
  const keys = MODULES.map(m => m.id)
  for (const key of keys) {
    if (payload[key]) {
      await upsertModule(key, key === 'device' ? normalizeDeviceModule(payload[key]) : clone(payload[key]), {
        lastSaved,
        version: payload.meta && payload.meta.version
      })
    }
  }
  const store = await loadStoreFromDb()
  const meta = { ...(store.meta || seed.meta), lastSaved, moduleCount: MODULES.length }
  await upsertModule('meta', meta, { lastSaved, version: meta.version })
  return getAll()
}

async function resetAll() {
  const lastSaved = nowDate()
  const fresh = clone(seed)
  fresh.meta.lastSaved = lastSaved
  for (const [moduleId, data] of Object.entries(fresh)) {
    await upsertModule(moduleId, data, {
      lastSaved,
      version: fresh.meta.version
    })
  }
  return getAll()
}

/** 只读：既有 threshold_config */
async function listExistingThresholds() {
  const rows = await queryOne(
    `select * from ${EXISTING.thresholdConfig} where isDel=0 order by Id desc limit 200`
  )
  return rows.map(row => ({
    id: pick(row, 'Id'),
    thresholdCode: pick(row, 'threshold_code') || '',
    thresholdGroup: pick(row, 'threshold_group') || '',
    metricCode: pick(row, 'metric_code') || '',
    metricName: pick(row, 'metric_name') || '',
    scopeType: pick(row, 'scope_type') || '',
    scopeCode: pick(row, 'scope_code') || '',
    scopeName: pick(row, 'scope_name') || '',
    normalMin: pick(row, 'normal_min'),
    normalMax: pick(row, 'normal_max'),
    warningMin: pick(row, 'warning_min'),
    warningMax: pick(row, 'warning_max'),
    dangerMin: pick(row, 'danger_min'),
    dangerMax: pick(row, 'danger_max'),
    unit: pick(row, 'unit') || '',
    enabled: pick(row, 'enabled'),
    remark: pick(row, 'remark') || ''
  }))
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

  const bizId = `TH-AIA-${Date.now()}`
  const row = {
    biz_id: bizId,
    alarm_code: payload.alarmCode || `TH-AI-${(metricCode || 'GEN').toUpperCase()}-${Date.now()}`,
    alarm_title: payload.alarmTitle || `AI预警：${payload.metricName || metricCode || '异常'}`,
    category: payload.category || 'alarm',
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
    alarm_time: payload.alarmTime || now(),
    is_mock: '1',
    source_system: 'threshold-ai-mock',
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

module.exports = {
  getNav,
  getMeta,
  getAll,
  getModule,
  saveModule,
  saveAll,
  resetAll,
  listExistingThresholds,
  listAiAlarms,
  createAiAlarm,
  ackAiAlarm,
  ensureAiAlarmMock
}
