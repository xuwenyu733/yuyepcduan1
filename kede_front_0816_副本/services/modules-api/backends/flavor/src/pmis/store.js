const { queryOne, execute, saveInfo, esc } = require('./client')
const { NEW } = require('./tables')
const { COLUMN_DEFS, buildRowPayload } = require('./schema')

const statusMap = {
  entered: '已入池',
  optimizing: '优化中',
  exited: '已出池'
}

const pondStatusMap = {
  idle: '空闲',
  in_use: '使用中',
  available: '可用'
}

const transportMethodsFallback = ['冷链运输车', '活水车', '空运', '海运', '其他']

function now() {
  return new Date().toISOString().replace('T', ' ').slice(0, 19)
}

function num(v, fallback = 0) {
  const n = Number(v)
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

async function insertRow(tableName, row) {
  await saveInfo(buildRowPayload(tableName, row, COLUMN_DEFS[tableName]))
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

async function loadDict(type) {
  const rows = await queryOne(
    `select * from ${NEW.dict} where isDel=0 and dict_type='${esc(type)}' and enabled<>'0' and biz_id not in ('__bootstrap__','__link_migrate__') order by sort_no+0 asc, Id asc`
  )
  return rows.map(r => pick(r, 'dict_value')).filter(Boolean)
}

async function loadUnits() {
  const rows = await queryOne(
    `select * from ${NEW.sysConfig} where isDel=0 and biz_id not in ('__bootstrap__','__link_migrate__') order by Id desc limit 1`
  )
  if (!rows[0]) return { weight: 'kg', length: 'cm' }
  try {
    return JSON.parse(pick(rows[0], 'settings_json') || '{}')
  } catch (e) {
    return { weight: 'kg', length: 'cm' }
  }
}

async function getMeta() {
  const [categories, sources, grades, transportMethods, units] = await Promise.all([
    loadDict('category'),
    loadDict('source'),
    loadDict('grade'),
    loadDict('transport'),
    loadUnits()
  ])
  return {
    categories: categories.length ? categories : ['鲈鱼', '大黄鱼', '石斑鱼', '黄花鱼', '鲷鱼'],
    sources: sources.length ? sources : ['自有养殖场', '合作供应商A', '合作供应商B', '近海捕捞'],
    grades: grades.length ? grades : ['0.3-0.5kg/尾', '0.5-0.8kg/尾', '0.8-1.2kg/尾', '1.2-1.5kg/尾', '1.5kg以上/尾'],
    transportMethods: transportMethods.length ? transportMethods : transportMethodsFallback,
    statusMap: { ...statusMap },
    pondStatusMap: { ...pondStatusMap },
    units: { weight: units.weight || 'kg', length: units.length || 'cm' }
  }
}

async function getConfigOverview() {
  const meta = await getMeta()
  return {
    categoryCount: meta.categories.length,
    sourceCount: meta.sources.length,
    gradeCount: meta.grades.length,
    weightUnit: meta.units.weight,
    lengthUnit: meta.units.length
  }
}

function mapPond(row) {
  const status = pick(row, 'status') || 'idle'
  const code = pick(row, 'code') || pick(row, 'pool_code') || ''
  return {
    id: pick(row, 'biz_id'),
    code,
    poolCode: pick(row, 'pool_code') || code,
    size: pick(row, 'size') || '',
    location: pick(row, 'location') || pick(row, 'workshop') || '',
    workshop: pick(row, 'workshop') || '',
    manager: pick(row, 'manager') || '',
    status,
    statusLabel: pondStatusMap[status] || status,
    remark: pick(row, 'remark') || '',
    createdAt: pick(row, 'created_at') || '',
    sourceSystem: pick(row, 'source_system') || '',
    externalId: pick(row, 'external_id') || ''
  }
}

async function listPonds() {
  const rows = await queryOne(
    `select * from ${NEW.pond} where isDel=0 and biz_id not in ('__bootstrap__','__link_migrate__') order by Id desc`
  )
  return rows.map(mapPond)
}

async function createPond(payload) {
  const code = payload.code
  const item = {
    biz_id: `POND-${Date.now()}`,
    code,
    pool_code: payload.poolCode || code,
    pool_name: payload.poolName || '',
    workshop: payload.workshop || payload.location || '',
    system_name: payload.systemName || '',
    location_code: payload.locationCode || '',
    batch_code: payload.batchCode || '',
    size: payload.size || '',
    location: payload.location || '',
    manager: payload.manager || '',
    status: payload.status === 'available' ? 'idle' : (payload.status || 'idle'),
    remark: payload.remark || '',
    source_system: payload.sourceSystem || 'flavor-opt',
    external_id: payload.externalId || '',
    ref_table: payload.refTable || '',
    ref_biz_id: payload.refBizId || '',
    created_at: now()
  }
  await insertRow(NEW.pond, item)
  return mapPond(item)
}

async function updatePond(id, payload) {
  const fields = {}
  const map = {
    code: 'code',
    size: 'size',
    location: 'location',
    manager: 'manager',
    status: 'status',
    remark: 'remark',
    poolCode: 'pool_code',
    workshop: 'workshop',
    systemName: 'system_name',
    locationCode: 'location_code',
    batchCode: 'batch_code',
    externalId: 'external_id'
  }
  Object.entries(map).forEach(([from, to]) => {
    if (payload[from] !== undefined) fields[to] = payload[from]
  })
  if (payload.code && !payload.poolCode) fields.pool_code = payload.code
  await updateFieldsByBizId(NEW.pond, id, fields)
  const rows = await queryOne(
    `select * from ${NEW.pond} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  return rows[0] ? mapPond(rows[0]) : null
}

async function deletePond(id) {
  const rows = await queryOne(
    `select Id from ${NEW.pond} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  if (!rows[0]) return false
  await softDeleteByBizId(NEW.pond, id)
  return true
}

async function markPondInUse(pondCode) {
  const rows = await queryOne(
    `select * from ${NEW.pond} where isDel=0 and (code='${esc(pondCode)}' or pool_code='${esc(pondCode)}') limit 1`
  )
  if (!rows[0]) return
  const status = pick(rows[0], 'status')
  if (status === 'idle' || status === 'available') {
    await updateFieldsByBizId(NEW.pond, pick(rows[0], 'biz_id'), { status: 'in_use' })
  }
}

/** 出池/删除后：若该池无在池记录，则释放为空闲 */
async function markPondIdleIfFree(pondCode) {
  if (!pondCode) return
  const list = await listEntries()
  const active = list.some(
    e =>
      (e.pondCode === pondCode || e.poolCode === pondCode) &&
      (e.status === 'entered' || e.status === 'optimizing')
  )
  if (active) return
  const rows = await queryOne(
    `select * from ${NEW.pond} where isDel=0 and (code='${esc(pondCode)}' or pool_code='${esc(pondCode)}') limit 1`
  )
  if (!rows[0]) return
  await updateFieldsByBizId(NEW.pond, pick(rows[0], 'biz_id'), { status: 'idle' })
}

async function updateUnits(payload) {
  const current = await loadUnits()
  const next = { ...current, ...payload }
  const rows = await queryOne(
    `select biz_id from ${NEW.sysConfig} where isDel=0 and biz_id not in ('__bootstrap__','__link_migrate__') order by Id desc limit 1`
  )
  if (rows[0]) {
    await updateFieldsByBizId(NEW.sysConfig, pick(rows[0], 'biz_id'), {
      settings_json: JSON.stringify(next)
    })
  } else {
    await insertRow(NEW.sysConfig, {
      biz_id: 'CFG-001',
      settings_json: JSON.stringify(next),
      source_system: 'flavor-opt'
    })
  }
  return next
}

async function replaceDictType(type, values) {
  const existing = await queryOne(
    `select biz_id from ${NEW.dict} where isDel=0 and dict_type='${esc(type)}'`
  )
  for (const row of existing) {
    await softDeleteByBizId(NEW.dict, pick(row, 'biz_id'))
  }
  let i = 0
  for (const v of values) {
    i += 1
    await insertRow(NEW.dict, {
      biz_id: `DICT-${type}-${Date.now()}-${i}`,
      dict_type: type,
      dict_value: v,
      sort_no: String(i),
      enabled: '1',
      source_system: 'flavor-opt'
    })
  }
}

async function updateDictionaries(payload) {
  if (Array.isArray(payload.categories)) await replaceDictType('category', payload.categories)
  if (Array.isArray(payload.sources)) await replaceDictType('source', payload.sources)
  if (Array.isArray(payload.grades)) await replaceDictType('grade', payload.grades)
  return getMeta()
}

function mapPurchase(row) {
  return {
    id: pick(row, 'biz_id'),
    category: pick(row, 'category') || '',
    purchaseDate: pick(row, 'purchase_date') || '',
    weight: num(pick(row, 'weight')),
    unitPrice: num(pick(row, 'unit_price')),
    unitWeight: num(pick(row, 'unit_weight')),
    manager: pick(row, 'manager') || '',
    source: pick(row, 'source') || '',
    address: pick(row, 'address') || '',
    contact: pick(row, 'contact') || '',
    phone: pick(row, 'phone') || '',
    transportMethod: pick(row, 'transport_method') || '',
    amount: num(pick(row, 'amount')),
    remark: pick(row, 'remark') || '',
    createdAt: pick(row, 'created_at') || '',
    sourceSystem: pick(row, 'source_system') || '',
    externalId: pick(row, 'external_id') || ''
  }
}

async function listPurchases() {
  const rows = await queryOne(
    `select * from ${NEW.purchase} where isDel=0 and biz_id not in ('__bootstrap__','__link_migrate__') order by Id desc`
  )
  return rows.map(mapPurchase)
}

async function createPurchase(payload) {
  const weight = num(payload.weight)
  const unitPrice = num(payload.unitPrice)
  const item = {
    biz_id: `PUR-${Date.now()}`,
    category: payload.category,
    purchase_date: payload.purchaseDate || now().slice(0, 10),
    weight: String(weight),
    unit_price: String(unitPrice),
    unit_weight: String(num(payload.unitWeight)),
    amount: String(Number((weight * unitPrice).toFixed(2))),
    manager: payload.manager || '',
    source: payload.source || '',
    address: payload.address || '',
    contact: payload.contact || '',
    phone: payload.phone || '',
    transport_method: payload.transportMethod || '',
    remark: payload.remark || '',
    source_system: payload.sourceSystem || 'flavor-opt',
    external_id: payload.externalId || '',
    ref_table: payload.refTable || '',
    ref_biz_id: payload.refBizId || '',
    created_at: now()
  }
  await insertRow(NEW.purchase, item)
  return mapPurchase(item)
}

async function deletePurchase(id) {
  const rows = await queryOne(
    `select Id from ${NEW.purchase} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  if (!rows[0]) return false
  await softDeleteByBizId(NEW.purchase, id)
  return true
}

function enrichEntry(item) {
  const purchaseCost = Number((item.totalWeight * item.unitPrice).toFixed(2))
  const otherCost =
    (item.laborCost || 0) +
    (item.electricityCost || 0) +
    (item.feedCost || 0) +
    (item.protectCost || 0)
  const totalCost = purchaseCost + otherCost
  const mortalityRate = item.quantity
    ? Number((((item.deathCount || 0) / item.quantity) * 100).toFixed(2))
    : 0
  const weightLoss = Math.max(0, item.totalWeight - (item.exitWeight || 0) - (item.deathWeight || 0))
  const weightLossRate = item.totalWeight
    ? Number(((weightLoss / item.totalWeight) * 100).toFixed(2))
    : 0
  const profit = (item.salesAmount || 0) - totalCost
  const profitRate = item.salesAmount
    ? Number(((profit / item.salesAmount) * 100).toFixed(2))
    : 0
  return {
    ...item,
    statusLabel: statusMap[item.status] || item.status,
    purchaseCost,
    totalCost,
    mortalityRate,
    weightLoss: Number(weightLoss.toFixed(2)),
    weightLossRate,
    profit: Number(profit.toFixed(2)),
    profitRate
  }
}

function mapEntry(row) {
  const poolCode = pick(row, 'pool_code') || pick(row, 'pond_code') || ''
  return enrichEntry({
    id: pick(row, 'biz_id'),
    recordNo: pick(row, 'record_no') || '',
    purchaseId: pick(row, 'purchase_biz_id') || '',
    category: pick(row, 'category') || '',
    source: pick(row, 'source') || '',
    entryTime: pick(row, 'entry_time') || '',
    totalWeight: num(pick(row, 'total_weight')),
    unitWeight: num(pick(row, 'unit_weight')),
    quantity: num(pick(row, 'quantity')),
    density: num(pick(row, 'density')),
    unitPrice: num(pick(row, 'unit_price')),
    manager: pick(row, 'manager') || '',
    pondCode: poolCode,
    poolCode,
    poolName: pick(row, 'pool_name') || '',
    workshop: pick(row, 'workshop') || '',
    systemName: pick(row, 'system_name') || '',
    locationCode: pick(row, 'location_code') || '',
    batchCode: pick(row, 'batch_code') || pick(row, 'record_no') || '',
    receiver: pick(row, 'receiver') || '',
    optLevel: pick(row, 'opt_level') || '',
    reportFile: pick(row, 'report_file') || '',
    pondCapacity: num(pick(row, 'pond_capacity'), 300),
    estimatedLossRate: num(pick(row, 'estimated_loss_rate'), 5),
    appearance: pick(row, 'appearance') || '',
    vitality: pick(row, 'vitality') || '',
    health: pick(row, 'health') || '',
    uniformity: pick(row, 'uniformity') || '',
    stress: pick(row, 'stress') || '',
    survivalRate: num(pick(row, 'survival_rate')),
    riskLevel: pick(row, 'risk_level') || '',
    assessor: pick(row, 'assessor') || '',
    evalRemark: pick(row, 'eval_remark') || '',
    purchaseAmount: num(pick(row, 'purchase_amount')),
    estimatedLoss: num(pick(row, 'estimated_loss')),
    estimatedNet: num(pick(row, 'estimated_net')),
    status: pick(row, 'status') || 'entered',
    deathCount: num(pick(row, 'death_count')),
    deathWeight: num(pick(row, 'death_weight')),
    exitWeight: num(pick(row, 'exit_weight')),
    salesAmount: num(pick(row, 'sales_amount')),
    laborCost: num(pick(row, 'labor_cost')),
    electricityCost: num(pick(row, 'electricity_cost')),
    feedCost: num(pick(row, 'feed_cost')),
    protectCost: num(pick(row, 'protect_cost')),
    exitAppearance: pick(row, 'exit_appearance') || '',
    exitVitality: pick(row, 'exit_vitality') || '',
    exitHealth: pick(row, 'exit_health') || '',
    exitUniformity: pick(row, 'exit_uniformity') || '',
    exitTime: pick(row, 'exit_time') || '',
    remark: pick(row, 'remark') || '',
    createdAt: pick(row, 'created_at') || '',
    sourceSystem: pick(row, 'source_system') || '',
    externalId: pick(row, 'external_id') || ''
  })
}

async function listEntries() {
  const rows = await queryOne(
    `select * from ${NEW.entry} where isDel=0 and biz_id not in ('__bootstrap__','__link_migrate__') order by Id desc`
  )
  return rows.map(mapEntry)
}

async function getEntryById(id) {
  const rows = await queryOne(
    `select * from ${NEW.entry} where isDel=0 and biz_id='${esc(id)}' limit 1`
  )
  return rows[0] ? mapEntry(rows[0]) : null
}

async function createEntry(payload) {
  const poolCode = payload.poolCode || payload.pondCode || ''
  const bizId = `ENT-${Date.now()}`
  const item = {
    biz_id: bizId,
    record_no: payload.recordNo || `FW-${Date.now()}`,
    purchase_biz_id: payload.purchaseId || '',
    category: payload.category,
    source: payload.source || '',
    entry_time: payload.entryTime || now(),
    total_weight: String(num(payload.totalWeight)),
    unit_weight: String(num(payload.unitWeight)),
    quantity: String(num(payload.quantity)),
    density: String(num(payload.density)),
    unit_price: String(num(payload.unitPrice)),
    manager: payload.manager || '',
    pond_code: poolCode,
    pool_code: poolCode,
    pool_name: payload.poolName || '',
    workshop: payload.workshop || '',
    system_name: payload.systemName || '',
    location_code: payload.locationCode || '',
    batch_code: payload.batchCode || payload.recordNo || '',
    receiver: payload.receiver || '',
    opt_level: payload.optLevel || '',
    report_file: payload.reportFile || '',
    pond_capacity: String(num(payload.pondCapacity, 300)),
    estimated_loss_rate: String(num(payload.estimatedLossRate, 5)),
    appearance: payload.appearance || '',
    vitality: payload.vitality || '',
    health: payload.health || '',
    uniformity: payload.uniformity || '',
    stress: payload.stress || '',
    survival_rate: String(num(payload.survivalRate)),
    risk_level: payload.riskLevel || '',
    assessor: payload.assessor || '',
    eval_remark: payload.evalRemark || '',
    purchase_amount: String(num(payload.purchaseAmount)),
    estimated_loss: String(num(payload.estimatedLoss)),
    estimated_net: String(num(payload.estimatedNet)),
    status: 'entered',
    death_count: '0',
    death_weight: '0',
    exit_weight: '0',
    sales_amount: '0',
    labor_cost: String(num(payload.laborCost)),
    electricity_cost: String(num(payload.electricityCost)),
    feed_cost: String(num(payload.feedCost)),
    protect_cost: String(num(payload.protectCost)),
    exit_appearance: '',
    exit_vitality: '',
    exit_health: '',
    exit_uniformity: '',
    exit_time: '',
    remark: payload.remark || '',
    source_system: payload.sourceSystem || 'flavor-opt',
    external_id: payload.externalId || '',
    ref_table: payload.refTable || '',
    ref_biz_id: payload.refBizId || '',
    created_at: now()
  }
  await insertRow(NEW.entry, item)
  await markPondInUse(poolCode)
  return getEntryById(bizId)
}

async function deleteEntry(id) {
  const existing = await getEntryById(id)
  if (!existing) return false
  await softDeleteByBizId(NEW.entry, id)
  await markPondIdleIfFree(existing.pondCode || existing.poolCode)
  return true
}

async function listOptimizing() {
  const list = await listEntries()
  return list.filter(e => e.status === 'entered' || e.status === 'optimizing')
}

async function listReadyToExit() {
  return listOptimizing()
}

function mapOpt(row) {
  const poolCode = pick(row, 'pool_code') || pick(row, 'pond_code') || ''
  return {
    id: pick(row, 'biz_id'),
    entryId: pick(row, 'entry_biz_id'),
    recordNo: pick(row, 'record_no') || '',
    pondCode: poolCode,
    poolCode,
    category: pick(row, 'category') || '',
    measureDate: pick(row, 'measure_date') || '',
    deathCount: num(pick(row, 'death_count')),
    deathWeight: num(pick(row, 'death_weight')),
    waterTemp: num(pick(row, 'water_temp')),
    dissolvedOxygen: num(pick(row, 'dissolved_oxygen')),
    appearance: pick(row, 'appearance') || '',
    vitality: pick(row, 'vitality') || '',
    health: pick(row, 'health') || '',
    uniformity: pick(row, 'uniformity') || '',
    measure: pick(row, 'measure') || '',
    operator: pick(row, 'operator') || '',
    createdAt: pick(row, 'created_at') || '',
    sourceSystem: pick(row, 'source_system') || '',
    externalId: pick(row, 'external_id') || ''
  }
}

async function listOptimizations(entryId) {
  let sql = `select * from ${NEW.optLog} where isDel=0 and biz_id not in ('__bootstrap__','__link_migrate__')`
  if (entryId) sql += ` and entry_biz_id='${esc(entryId)}'`
  sql += ' order by Id desc'
  const rows = await queryOne(sql)
  return rows.map(mapOpt)
}

async function createOptimization(payload) {
  let entry = await getEntryById(payload.entryId)
  // 管理端流程记录无风味入池行时，用快照写入优化日志
  if (!entry) {
    if (!payload || !payload.entryId) return null
    entry = {
      id: payload.entryId,
      recordNo: payload.recordNo || '',
      pondCode: payload.pondCode || '',
      poolName: payload.poolName || '',
      workshop: payload.workshop || '',
      systemName: payload.systemName || '',
      locationCode: payload.locationCode || '',
      batchCode: payload.batchCode || payload.recordNo || '',
      category: payload.category || '',
      deathCount: 0,
      deathWeight: 0
    }
  }
  const deathCount = num(payload.deathCount)
  const deathWeight = num(payload.deathWeight)
  const item = {
    biz_id: `OPT-${Date.now()}`,
    entry_biz_id: entry.id,
    record_no: entry.recordNo || payload.recordNo || '',
    pond_code: entry.pondCode || payload.pondCode || '',
    pool_code: entry.pondCode || payload.pondCode || '',
    pool_name: entry.poolName || '',
    workshop: entry.workshop || '',
    system_name: entry.systemName || '',
    location_code: entry.locationCode || '',
    batch_code: entry.batchCode || entry.recordNo || payload.recordNo || '',
    category: entry.category || payload.category || '',
    measure_date: payload.measureDate || now().slice(0, 10),
    death_count: String(deathCount),
    death_weight: String(deathWeight),
    water_temp: String(num(payload.waterTemp)),
    dissolved_oxygen: String(num(payload.dissolvedOxygen)),
    appearance: payload.appearance || '',
    vitality: payload.vitality || '',
    health: payload.health || '',
    uniformity: payload.uniformity || '',
    measure: payload.measure || '',
    operator: payload.operator || '',
    source_system: 'flavor-opt',
    external_id: payload.externalId || '',
    ref_table: '',
    ref_biz_id: '',
    created_at: now()
  }
  await insertRow(NEW.optLog, item)
  const localEntry = await getEntryById(entry.id)
  if (localEntry) {
    await updateFieldsByBizId(NEW.entry, entry.id, {
      status: 'optimizing',
      death_count: String((localEntry.deathCount || 0) + deathCount),
      death_weight: String(Number(((localEntry.deathWeight || 0) + deathWeight).toFixed(2)))
    })
  }

  // AI 报警 mock：监测异常写入 fo_ai_alarm（不改既有报警表）
  try {
    const doVal = num(payload.dissolvedOxygen)
    const tempVal = num(payload.waterTemp)
    if (doVal > 0 && doVal < 5) {
      await ensureAiAlarmMock({
        category: 'water',
        priority: 'high',
        poolCode: entry.pondCode,
        poolName: entry.poolName || '',
        workshop: entry.workshop || '',
        metricCode: 'do',
        metricName: '溶氧',
        currentValue: String(doVal),
        thresholdValue: '5.0',
        unit: 'mg/L',
        alarmReason: `【Mock AI】优化监测溶氧 ${doVal} mg/L 低于建议下限`,
        aiSuggestion: '加大增氧，暂缓加料，1小时后复测。',
        confidence: '82',
        entryBizId: entry.id,
        optBizId: item.biz_id,
        batchCode: entry.recordNo
      })
    }
    if (tempVal > 0 && (tempVal < 15 || tempVal > 30)) {
      await ensureAiAlarmMock({
        category: 'water',
        priority: 'medium',
        poolCode: entry.pondCode,
        poolName: entry.poolName || '',
        workshop: entry.workshop || '',
        metricCode: 'temp',
        metricName: '水温',
        currentValue: String(tempVal),
        thresholdValue: '15-30',
        unit: '℃',
        alarmReason: `【Mock AI】水温 ${tempVal}℃ 偏离优化建议区间`,
        aiSuggestion: '检查进水与遮阳/加热措施，避免剧烈温差。',
        confidence: '78',
        entryBizId: entry.id,
        optBizId: item.biz_id,
        batchCode: entry.recordNo
      })
    }
    if (deathCount >= 5) {
      await ensureAiAlarmMock({
        category: 'process',
        priority: deathCount >= 10 ? 'urgent' : 'high',
        poolCode: entry.pondCode,
        poolName: entry.poolName || '',
        workshop: entry.workshop || '',
        metricCode: 'death',
        metricName: '日死亡尾数',
        currentValue: String(deathCount),
        thresholdValue: '5',
        unit: '尾',
        alarmReason: `【Mock AI】本日死亡 ${deathCount} 尾，超过阈值`,
        aiSuggestion: '排查应激与水质，隔离弱鱼并记录处置。',
        confidence: '86',
        entryBizId: entry.id,
        optBizId: item.biz_id,
        batchCode: entry.recordNo
      })
    }
  } catch (e) {
    console.error('[ai-alarm]', e.message || e)
  }

  return mapOpt(item)
}

function mapAiAlarm(row) {
  return {
    id: pick(row, 'biz_id'),
    alarmCode: pick(row, 'alarm_code') || '',
    alarmTitle: pick(row, 'alarm_title') || '',
    category: pick(row, 'category') || '',
    priority: pick(row, 'priority') || 'medium',
    alarmStatus: pick(row, 'alarm_status') || 'open',
    poolCode: pick(row, 'pool_code') || pick(row, 'pond_code') || '',
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
    entryBizId: pick(row, 'entry_biz_id') || '',
    optBizId: pick(row, 'opt_biz_id') || '',
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

  const bizId = `FO-AIA-${Date.now()}`
  const row = {
    biz_id: bizId,
    alarm_code: payload.alarmCode || `FO-AI-${(metricCode || 'GEN').toUpperCase()}-${Date.now()}`,
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
    entry_biz_id: payload.entryBizId || '',
    opt_biz_id: payload.optBizId || '',
    alarm_time: payload.alarmTime || now(),
    is_mock: '1',
    source_system: 'flavor-opt-ai-mock',
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

async function exitEntry(payload) {
  const entry = await getEntryById(payload.entryId)
  if (!entry) return null
  await updateFieldsByBizId(NEW.entry, entry.id, {
    exit_weight: String(num(payload.exitWeight)),
    sales_amount: String(num(payload.salesAmount)),
    death_count: String(
      num(payload.deathCount != null ? payload.deathCount : entry.deathCount)
    ),
    death_weight: String(
      num(payload.deathWeight != null ? payload.deathWeight : entry.deathWeight)
    ),
    labor_cost: String(num(payload.laborCost != null ? payload.laborCost : entry.laborCost)),
    electricity_cost: String(
      num(payload.electricityCost != null ? payload.electricityCost : entry.electricityCost)
    ),
    feed_cost: String(num(payload.feedCost != null ? payload.feedCost : entry.feedCost)),
    protect_cost: String(
      num(payload.protectCost != null ? payload.protectCost : entry.protectCost)
    ),
    exit_appearance: payload.appearance || '',
    exit_vitality: payload.vitality || '',
    exit_health: payload.health || '',
    exit_uniformity: payload.uniformity || '',
    exit_time: payload.exitTime || now(),
    status: 'exited',
    remark: payload.remark || entry.remark || ''
  })
  await markPondIdleIfFree(entry.pondCode || entry.poolCode)
  return getEntryById(entry.id)
}

async function queryRecords(query = {}) {
  let list = await listEntries()
  if (query.recordNo) list = list.filter(i => i.recordNo.includes(query.recordNo))
  if (query.category) list = list.filter(i => i.category === query.category)
  if (query.pondCode) list = list.filter(i => i.pondCode.includes(query.pondCode))
  if (query.status) list = list.filter(i => i.status === query.status)
  if (query.startDate) list = list.filter(i => i.entryTime >= `${query.startDate} 00:00:00`)
  if (query.endDate) list = list.filter(i => i.entryTime <= `${query.endDate} 23:59:59`)
  if (query.minMortality != null && query.minMortality !== '') {
    list = list.filter(i => i.mortalityRate >= Number(query.minMortality))
  }
  if (query.maxMortality != null && query.maxMortality !== '') {
    list = list.filter(i => i.mortalityRate <= Number(query.maxMortality))
  }
  if (query.minWeightLoss != null && query.minWeightLoss !== '') {
    list = list.filter(i => i.weightLossRate >= Number(query.minWeightLoss))
  }
  if (query.maxWeightLoss != null && query.maxWeightLoss !== '') {
    list = list.filter(i => i.weightLossRate <= Number(query.maxWeightLoss))
  }
  return list
}

async function getStats(query = {}) {
  let list = await listEntries()
  if (query.category) list = list.filter(i => i.category === query.category)
  if (query.recordNo) list = list.filter(i => i.recordNo === query.recordNo)
  if (query.range) {
    const days = { '7d': 7, '30d': 30, '90d': 90 }[query.range]
    if (days) {
      const from = new Date()
      from.setDate(from.getDate() - days)
      const fromStr = from.toISOString().slice(0, 10)
      list = list.filter(i => String(i.entryTime || '').slice(0, 10) >= fromStr)
    }
  }

  const meta = await getMeta()
  const categories = meta.categories
  const entryWeight = list.reduce((s, i) => s + i.totalWeight, 0)
  const exitWeight = list.reduce((s, i) => s + (i.exitWeight || 0), 0)
  const deathCount = list.reduce((s, i) => s + (i.deathCount || 0), 0)
  const deathWeight = list.reduce((s, i) => s + (i.deathWeight || 0), 0)
  const quantity = list.reduce((s, i) => s + (i.quantity || 0), 0)
  const weightLoss = list.reduce((s, i) => s + (i.weightLoss || 0), 0)
  const salesAmount = list.reduce((s, i) => s + (i.salesAmount || 0), 0)
  const purchaseCost = list.reduce((s, i) => s + (i.purchaseCost || 0), 0)
  const laborCost = list.reduce((s, i) => s + (i.laborCost || 0), 0)
  const electricityCost = list.reduce((s, i) => s + (i.electricityCost || 0), 0)
  const feedCost = list.reduce((s, i) => s + (i.feedCost || 0), 0)
  const protectCost = list.reduce((s, i) => s + (i.protectCost || 0), 0)
  const totalCost = purchaseCost + laborCost + electricityCost + feedCost + protectCost
  const profit = salesAmount - totalCost
  const totalBatches = list.length || 1

  const categoryDist = categories
    .map(name => {
      const count = list.filter(i => i.category === name).length
      return {
        name,
        count,
        percent: Number(((count / totalBatches) * 100).toFixed(1))
      }
    })
    .filter(i => i.count > 0)

  const batchDetails = list.map(i => ({
    recordNo: i.recordNo,
    category: i.category,
    pondCode: i.pondCode,
    totalWeight: i.totalWeight,
    deathCount: i.deathCount,
    mortalityRate: i.mortalityRate,
    weightLoss: i.weightLoss,
    weightLossRate: i.weightLossRate,
    day3Rate: Number((i.mortalityRate * 0.4).toFixed(2)),
    day5Rate: Number((i.mortalityRate * 0.7).toFixed(2)),
    day7Rate: i.mortalityRate
  }))

  const scatterData = list.map(i => ({
    recordNo: i.recordNo,
    category: i.category,
    x: i.weightLossRate || 0,
    y: i.mortalityRate || 0,
    size: i.totalWeight || 0
  }))

  const entrySpec = list.length
    ? Number((list.reduce((s, i) => s + (i.unitWeight || 0), 0) / list.length).toFixed(3))
    : 0
  const exitSpec = list.length
    ? Number(
        (
          list.reduce((s, i) => {
            const q = i.quantity || 1
            return s + (i.exitWeight || 0) / q
          }, 0) / list.length
        ).toFixed(3)
      )
    : 0

  const feedAmount = Number(
    list.reduce((s, i) => s + (Number(i.feedAmount) || Number(i.totalWeight) * 0.12 || 0), 0).toFixed(2)
  )

  const stageTrend = {
    day3: batchDetails.length
      ? Number((batchDetails.reduce((s, i) => s + i.day3Rate, 0) / batchDetails.length).toFixed(2))
      : 0,
    day5: batchDetails.length
      ? Number((batchDetails.reduce((s, i) => s + i.day5Rate, 0) / batchDetails.length).toFixed(2))
      : 0,
    day7: batchDetails.length
      ? Number((batchDetails.reduce((s, i) => s + i.day7Rate, 0) / batchDetails.length).toFixed(2))
      : 0
  }

  return {
    weightChange: Number((exitWeight - entryWeight).toFixed(2)),
    entryWeight: Number(entryWeight.toFixed(2)),
    exitWeight: Number(exitWeight.toFixed(2)),
    mortalityRate: quantity ? Number(((deathCount / quantity) * 100).toFixed(2)) : 0,
    deathCount,
    deathWeight: Number(deathWeight.toFixed(2)),
    weightLossRate: entryWeight ? Number(((weightLoss / entryWeight) * 100).toFixed(2)) : 0,
    weightLoss: Number(weightLoss.toFixed(2)),
    salesAmount: Number(salesAmount.toFixed(2)),
    avgPrice: exitWeight ? Number((salesAmount / exitWeight).toFixed(2)) : 0,
    feedAmount,
    costs: {
      purchaseCost: Number(purchaseCost.toFixed(2)),
      laborCost: Number(laborCost.toFixed(2)),
      electricityCost: Number(electricityCost.toFixed(2)),
      feedCost: Number(feedCost.toFixed(2)),
      protectCost: Number(protectCost.toFixed(2)),
      totalCost: Number(totalCost.toFixed(2)),
      profit: Number(profit.toFixed(2)),
      profitRate: salesAmount ? Number(((profit / salesAmount) * 100).toFixed(2)) : 0
    },
    categoryDist,
    specChange: { entrySpec, exitSpec },
    stageTrend,
    batchDetails,
    scatterData,
    recordOptions: list.map(i => i.recordNo)
  }
}

module.exports = {
  getMeta,
  getConfigOverview,
  listPonds,
  createPond,
  updatePond,
  deletePond,
  updateUnits,
  updateDictionaries,
  listPurchases,
  createPurchase,
  deletePurchase,
  listEntries,
  createEntry,
  deleteEntry,
  listOptimizing,
  listReadyToExit,
  listOptimizations,
  createOptimization,
  exitEntry,
  queryRecords,
  getStats,
  listAiAlarms,
  createAiAlarm,
  ackAiAlarm,
  ensureAiAlarmMock
}
