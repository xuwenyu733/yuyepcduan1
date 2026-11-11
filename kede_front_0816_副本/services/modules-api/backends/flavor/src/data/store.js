const categories = ['鲈鱼', '大黄鱼', '石斑鱼', '黄花鱼', '鲷鱼']
const sources = ['自有养殖场', '合作供应商A', '合作供应商B', '近海捕捞']
const grades = ['0.3-0.5kg/尾', '0.5-0.8kg/尾', '0.8-1.2kg/尾', '1.2-1.5kg/尾', '1.5kg以上/尾']
const transportMethods = ['冷链运输车', '活水车', '空运', '海运', '其他']

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

let units = {
  weight: 'kg',
  length: 'cm'
}

let ponds = [
  {
    id: 'POND-001',
    code: '001-1',
    size: '120㎡',
    location: '鲈鱼车间',
    workshop: '鲈鱼车间',
    systemName: '001',
    manager: '张强',
    status: 'idle',
    remark: '主优化池（对齐鱼池档案 001-1）',
    createdAt: '2024-03-01 09:00:00'
  },
  {
    id: 'POND-002',
    code: '001-2',
    size: '100㎡',
    location: '鲈鱼车间',
    workshop: '鲈鱼车间',
    systemName: '001',
    manager: '李敏',
    status: 'idle',
    remark: '对齐鱼池档案 001-2',
    createdAt: '2024-03-01 09:10:00'
  },
  {
    id: 'POND-003',
    code: 'c1-s1-1',
    size: '150㎡',
    location: '车间1',
    workshop: '车间1',
    systemName: 'c1-s1',
    manager: '王磊',
    status: 'idle',
    remark: '对齐鱼池档案 c1-s1-1',
    createdAt: '2024-03-02 10:00:00'
  }
]

let purchases = [
  {
    id: 'PUR-2024-001',
    category: '鲈鱼',
    purchaseDate: '2024-05-08',
    weight: 520,
    unitPrice: 28.5,
    unitWeight: 0.85,
    manager: '张强',
    source: '宁波',
    address: '浙江省宁波市象山县东门渔港',
    contact: '陈老板',
    phone: '13800001111',
    transportMethod: '活水车运输',
    amount: 14820,
    remark: '首批试采购',
    createdAt: '2024-05-08 14:20:00'
  }
]

let entries = [
  {
    id: 'ENT-2024-001',
    recordNo: 'FW-20240508-001',
    purchaseId: 'PUR-2024-001',
    category: '鲈鱼',
    source: '自有养殖场',
    entryTime: '2024-05-08 16:30:00',
    totalWeight: 500,
    unitWeight: 0.85,
    quantity: 588,
    density: 4.17,
    unitPrice: 28.5,
    manager: '张强',
    pondCode: '001-1',
    status: 'optimizing',
    deathCount: 6,
    deathWeight: 4.2,
    exitWeight: 0,
    salesAmount: 0,
    laborCost: 800,
    electricityCost: 320,
    feedCost: 1260,
    protectCost: 180,
    remark: '入池观察正常',
    createdAt: '2024-05-08 16:35:00'
  }
]

// Mark pond 001-1 in use because of entry
ponds[0].status = 'in_use'

let optimizations = [
  {
    id: 'OPT-2024-001',
    entryId: 'ENT-2024-001',
    recordNo: 'FW-20240508-001',
    pondCode: '001-1',
    category: '鲈鱼',
    measureDate: '2024-05-10',
    deathCount: 2,
    deathWeight: 1.5,
    waterTemp: 22.5,
    dissolvedOxygen: 6.8,
    measure: '投喂风味强化饲料，开启微流水',
    operator: '李敏',
    createdAt: '2024-05-10 09:20:00'
  }
]

function now() {
  return new Date().toISOString().replace('T', ' ').slice(0, 19)
}

function getMeta() {
  return {
    categories: [...categories],
    sources: [...sources],
    grades: [...grades],
    transportMethods: [...transportMethods],
    statusMap: { ...statusMap },
    pondStatusMap: { ...pondStatusMap },
    units: { ...units }
  }
}

function getConfigOverview() {
  return {
    categoryCount: categories.length,
    sourceCount: sources.length,
    gradeCount: grades.length,
    weightUnit: units.weight,
    lengthUnit: units.length
  }
}

function listPonds() {
  return ponds.map(p => ({ ...p, statusLabel: pondStatusMap[p.status] || p.status }))
}

function createPond(payload) {
  const item = {
    id: `POND-${Date.now()}`,
    code: payload.code,
    size: payload.size || '',
    location: payload.location || '',
    manager: payload.manager || '',
    status: payload.status === 'available' ? 'idle' : (payload.status || 'idle'),
    remark: payload.remark || '',
    createdAt: now()
  }
  ponds.unshift(item)
  return item
}

function updatePond(id, payload) {
  const index = ponds.findIndex(p => p.id === id)
  if (index === -1) return null
  ponds[index] = { ...ponds[index], ...payload }
  return ponds[index]
}

function deletePond(id) {
  const index = ponds.findIndex(p => p.id === id)
  if (index === -1) return false
  ponds.splice(index, 1)
  return true
}

function markPondInUse(pondCode) {
  const pond = ponds.find(p => p.code === pondCode)
  if (!pond) return
  if (pond.status === 'idle' || pond.status === 'available') {
    pond.status = 'in_use'
  }
}

/** 出池/删除后：若该池无在池记录，则释放为空闲 */
function markPondIdleIfFree(pondCode) {
  if (!pondCode) return
  const active = entries.some(
    e => e.pondCode === pondCode && (e.status === 'entered' || e.status === 'optimizing')
  )
  if (active) return
  const pond = ponds.find(p => p.code === pondCode)
  if (pond) pond.status = 'idle'
}

function updateUnits(payload) {
  units = { ...units, ...payload }
  return units
}

function updateDictionaries(payload) {
  if (Array.isArray(payload.categories)) {
    categories.splice(0, categories.length, ...payload.categories)
  }
  if (Array.isArray(payload.sources)) {
    sources.splice(0, sources.length, ...payload.sources)
  }
  if (Array.isArray(payload.grades)) {
    grades.splice(0, grades.length, ...payload.grades)
  }
  return getMeta()
}

function listPurchases() {
  return [...purchases]
}

function createPurchase(payload) {
  const weight = Number(payload.weight) || 0
  const unitPrice = Number(payload.unitPrice) || 0
  const item = {
    id: `PUR-${Date.now()}`,
    category: payload.category,
    purchaseDate: payload.purchaseDate || now().slice(0, 10),
    weight,
    unitPrice,
    unitWeight: Number(payload.unitWeight) || 0,
    manager: payload.manager || '',
    source: payload.source || '',
    address: payload.address || '',
    contact: payload.contact || '',
    phone: payload.phone || '',
    transportMethod: payload.transportMethod || '',
    amount: Number((weight * unitPrice).toFixed(2)),
    remark: payload.remark || '',
    createdAt: now()
  }
  purchases.unshift(item)
  return item
}

function deletePurchase(id) {
  const index = purchases.findIndex(p => p.id === id)
  if (index === -1) return false
  purchases.splice(index, 1)
  return true
}

function updatePurchase(id, payload) {
  const index = purchases.findIndex(p => p.id === id)
  if (index === -1) return null
  const weight = Number(payload.weight) || 0
  const unitPrice = Number(payload.unitPrice) || 0
  const item = {
    ...purchases[index],
    category: payload.category,
    purchaseDate: payload.purchaseDate || purchases[index].purchaseDate,
    weight,
    unitPrice,
    unitWeight: Number(payload.unitWeight) || 0,
    manager: payload.manager || '',
    source: payload.source || '',
    address: payload.address || '',
    contact: payload.contact || '',
    phone: payload.phone || '',
    transportMethod: payload.transportMethod || '',
    amount: Number((weight * unitPrice).toFixed(2)),
    remark: payload.remark != null ? payload.remark : purchases[index].remark
  }
  purchases[index] = item
  return item
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

function listEntries() {
  return entries.map(enrichEntry)
}

function createEntry(payload) {
  const item = {
    id: `ENT-${Date.now()}`,
    recordNo: payload.recordNo || `FW-${Date.now()}`,
    purchaseId: payload.purchaseId || '',
    category: payload.category,
    source: payload.source || '',
    entryTime: payload.entryTime || now(),
    totalWeight: Number(payload.totalWeight) || 0,
    unitWeight: Number(payload.unitWeight) || 0,
    quantity: Number(payload.quantity) || 0,
    density: Number(payload.density) || 0,
    unitPrice: Number(payload.unitPrice) || 0,
    manager: payload.manager || '',
    pondCode: payload.pondCode,
    receiver: payload.receiver || '',
    optLevel: payload.optLevel || '',
    reportFile: payload.reportFile || '',
    pondCapacity: Number(payload.pondCapacity) || 300,
    estimatedLossRate: Number(payload.estimatedLossRate) || 5,
    appearance: payload.appearance || '',
    vitality: payload.vitality || '',
    health: payload.health || '',
    uniformity: payload.uniformity || '',
    stress: payload.stress || '',
    survivalRate: Number(payload.survivalRate) || 0,
    riskLevel: payload.riskLevel || '',
    assessor: payload.assessor || '',
    evalRemark: payload.evalRemark || '',
    purchaseAmount: Number(payload.purchaseAmount) || 0,
    estimatedLoss: Number(payload.estimatedLoss) || 0,
    estimatedNet: Number(payload.estimatedNet) || 0,
    status: 'entered',
    deathCount: 0,
    deathWeight: 0,
    exitWeight: 0,
    salesAmount: 0,
    laborCost: Number(payload.laborCost) || 0,
    electricityCost: Number(payload.electricityCost) || 0,
    feedCost: Number(payload.feedCost) || 0,
    protectCost: Number(payload.protectCost) || 0,
    remark: payload.remark || '',
    createdAt: now()
  }
  entries.unshift(item)
  markPondInUse(item.pondCode)
  return enrichEntry(item)
}

function deleteEntry(id) {
  const index = entries.findIndex(e => e.id === id)
  if (index === -1) return false
  const pondCode = entries[index].pondCode
  entries.splice(index, 1)
  markPondIdleIfFree(pondCode)
  return true
}

function updateEntry(id, payload) {
  const index = entries.findIndex(e => e.id === id)
  if (index === -1) return null
  const prev = entries[index]
  const item = {
    ...prev,
    purchaseId: payload.purchaseId != null ? payload.purchaseId : prev.purchaseId,
    category: payload.category != null ? payload.category : prev.category,
    source: payload.source != null ? payload.source : prev.source,
    entryTime: payload.entryTime || prev.entryTime,
    totalWeight: Number(payload.totalWeight) || prev.totalWeight,
    unitWeight: Number(payload.unitWeight) || prev.unitWeight,
    quantity: Number(payload.quantity) || prev.quantity,
    density: Number(payload.density) || prev.density,
    unitPrice: Number(payload.unitPrice) || prev.unitPrice,
    manager: payload.manager != null ? payload.manager : prev.manager,
    pondCode: payload.pondCode || prev.pondCode,
    receiver: payload.receiver != null ? payload.receiver : prev.receiver,
    optLevel: payload.optLevel != null ? payload.optLevel : prev.optLevel,
    reportFile: payload.reportFile != null ? payload.reportFile : prev.reportFile,
    appearance: payload.appearance != null ? payload.appearance : prev.appearance,
    vitality: payload.vitality != null ? payload.vitality : prev.vitality,
    health: payload.health != null ? payload.health : prev.health,
    uniformity: payload.uniformity != null ? payload.uniformity : prev.uniformity,
    stress: payload.stress != null ? payload.stress : prev.stress,
    survivalRate: payload.survivalRate != null ? Number(payload.survivalRate) : prev.survivalRate,
    riskLevel: payload.riskLevel != null ? payload.riskLevel : prev.riskLevel,
    assessor: payload.assessor != null ? payload.assessor : prev.assessor,
    evalRemark: payload.evalRemark != null ? payload.evalRemark : prev.evalRemark,
    purchaseAmount: payload.purchaseAmount != null ? Number(payload.purchaseAmount) : prev.purchaseAmount,
    estimatedLoss: payload.estimatedLoss != null ? Number(payload.estimatedLoss) : prev.estimatedLoss,
    estimatedNet: payload.estimatedNet != null ? Number(payload.estimatedNet) : prev.estimatedNet
  }
  entries[index] = item
  if (prev.pondCode !== item.pondCode) {
    markPondIdleIfFree(prev.pondCode)
    markPondInUse(item.pondCode)
  }
  return enrichEntry(item)
}

function listOptimizing() {
  return entries
    .filter(e => e.status === 'entered' || e.status === 'optimizing')
    .map(enrichEntry)
}

function listReadyToExit() {
  return entries
    .filter(e => e.status === 'entered' || e.status === 'optimizing')
    .map(enrichEntry)
}

function listOptimizations(entryId) {
  let list = [...optimizations]
  if (entryId) list = list.filter(o => o.entryId === entryId)
  return list
}

function createOptimization(payload) {
  let entry = entries.find(e => e.id === payload.entryId)
  // 管理端流程(PURIFY-PROC-*)不在风味入池表：用表单快照落库，保证「最近优化记录」可展示
  if (!entry) {
    if (!payload || !payload.entryId) return null
    entry = {
      id: payload.entryId,
      recordNo: payload.recordNo || '',
      pondCode: payload.pondCode || '',
      category: payload.category || '',
      deathCount: 0,
      deathWeight: 0
    }
  }
  const item = {
    id: `OPT-${Date.now()}`,
    entryId: entry.id,
    recordNo: entry.recordNo || payload.recordNo || '',
    pondCode: entry.pondCode || payload.pondCode || '',
    category: entry.category || payload.category || '',
    measureDate: payload.measureDate || now().slice(0, 10),
    deathCount: Number(payload.deathCount) || 0,
    deathWeight: Number(payload.deathWeight) || 0,
    waterTemp: Number(payload.waterTemp) || 0,
    dissolvedOxygen: Number(payload.dissolvedOxygen) || 0,
    appearance: payload.appearance || '',
    vitality: payload.vitality || '',
    health: payload.health || '',
    uniformity: payload.uniformity || '',
    measure: payload.measure || '',
    operator: payload.operator || '',
    createdAt: now()
  }
  optimizations.unshift(item)
  const local = entries.find(e => e.id === entry.id)
  if (local) {
    local.status = 'optimizing'
    local.deathCount = (local.deathCount || 0) + item.deathCount
    local.deathWeight = Number(((local.deathWeight || 0) + item.deathWeight).toFixed(2))
  }
  return item
}

function exitEntry(payload) {
  const entry = entries.find(e => e.id === payload.entryId)
  if (!entry) return null
  entry.exitWeight = Number(payload.exitWeight) || 0
  entry.salesAmount = Number(payload.salesAmount) || 0
  entry.deathCount = Number(payload.deathCount != null ? payload.deathCount : entry.deathCount) || 0
  entry.deathWeight = Number(payload.deathWeight != null ? payload.deathWeight : entry.deathWeight) || 0
  entry.laborCost = Number(payload.laborCost != null ? payload.laborCost : entry.laborCost) || 0
  entry.electricityCost =
    Number(payload.electricityCost != null ? payload.electricityCost : entry.electricityCost) || 0
  entry.feedCost = Number(payload.feedCost != null ? payload.feedCost : entry.feedCost) || 0
  entry.protectCost = Number(payload.protectCost != null ? payload.protectCost : entry.protectCost) || 0
  entry.exitAppearance = payload.appearance || ''
  entry.exitVitality = payload.vitality || ''
  entry.exitHealth = payload.health || ''
  entry.exitUniformity = payload.uniformity || ''
  entry.exitTime = payload.exitTime || now()
  entry.status = 'exited'
  entry.remark = payload.remark || entry.remark
  markPondIdleIfFree(entry.pondCode)
  return enrichEntry(entry)
}

function queryRecords(query = {}) {
  let list = entries.map(enrichEntry)
  if (query.recordNo) {
    list = list.filter(i => i.recordNo.includes(query.recordNo))
  }
  if (query.category) {
    list = list.filter(i => i.category === query.category)
  }
  if (query.pondCode) {
    list = list.filter(i => i.pondCode.includes(query.pondCode))
  }
  if (query.status) {
    list = list.filter(i => i.status === query.status)
  }
  if (query.startDate) {
    list = list.filter(i => i.entryTime >= `${query.startDate} 00:00:00`)
  }
  if (query.endDate) {
    list = list.filter(i => i.entryTime <= `${query.endDate} 23:59:59`)
  }
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

function getStats(query = {}) {
  let list = entries.map(enrichEntry)
  if (query.category) list = list.filter(i => i.category === query.category)
  if (query.recordNo) list = list.filter(i => i.recordNo === query.recordNo)
  if (query.range) {
    const days = { '7d': 7, '30d': 30, '90d': 90 }[query.range]
    if (days) {
      const from = new Date()
      from.setDate(from.getDate() - days)
      const fromStr = from.toISOString().slice(0, 10)
      list = list.filter(i => i.entryTime.slice(0, 10) >= fromStr)
    }
  }

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
    ? Number((list.reduce((s, i) => {
      const q = i.quantity || 1
      return s + ((i.exitWeight || 0) / q)
    }, 0) / list.length).toFixed(3))
    : 0

  const feedAmount = Number(list.reduce((s, i) => s + (Number(i.feedAmount) || Number(i.totalWeight) * 0.12 || 0), 0).toFixed(2))

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

/** 本地模式 AI 报警 mock */
let localAiAlarms = [
  {
    id: 'FO-AIA-001',
    alarmCode: 'FO-AI-DO-LOCAL-001',
    alarmTitle: 'AI预警：优化池溶氧偏低',
    category: 'water',
    priority: 'high',
    alarmStatus: 'open',
    poolCode: 'A1',
    poolName: 'A1优化池',
    workshop: '一号车间',
    metricCode: 'do',
    metricName: '溶氧',
    currentValue: '4.5',
    thresholdValue: '5.0',
    unit: 'mg/L',
    alarmReason: '【Mock AI】优化监测溶氧低于建议下限。',
    aiSuggestion: '加大增氧，暂缓加料，1小时后复测。',
    confidence: 84,
    entryBizId: '',
    optBizId: '',
    alarmTime: '2026-07-23 09:20:00',
    isMock: true,
    sourceSystem: 'flavor-opt-ai-mock',
    externalId: ''
  }
]

function listAiAlarms(query = {}) {
  return localAiAlarms.filter(a => {
    if (query.status && a.alarmStatus !== query.status) return false
    if (query.category && a.category !== query.category) return false
    if (query.poolCode && !String(a.poolCode).includes(query.poolCode)) return false
    return true
  })
}

function createAiAlarm(payload = {}) {
  const item = {
    id: `FO-AIA-${Date.now()}`,
    alarmCode: payload.alarmCode || `FO-AI-LOCAL-${Date.now()}`,
    alarmTitle: payload.alarmTitle || `AI预警：${payload.metricName || '异常'}`,
    category: payload.category || 'water',
    priority: payload.priority || 'medium',
    alarmStatus: 'open',
    poolCode: payload.poolCode || '',
    poolName: payload.poolName || '',
    workshop: payload.workshop || '',
    metricCode: payload.metricCode || '',
    metricName: payload.metricName || '',
    currentValue: String(payload.currentValue != null ? payload.currentValue : ''),
    thresholdValue: String(payload.thresholdValue != null ? payload.thresholdValue : ''),
    unit: payload.unit || '',
    alarmReason: payload.alarmReason || '【Mock AI】本地模拟报警',
    aiSuggestion: payload.aiSuggestion || '',
    confidence: Number(payload.confidence) || 75,
    entryBizId: payload.entryBizId || '',
    optBizId: payload.optBizId || '',
    alarmTime: payload.alarmTime || new Date().toISOString().replace('T', ' ').slice(0, 19),
    isMock: true,
    sourceSystem: 'flavor-opt-ai-mock',
    externalId: payload.externalId || ''
  }
  localAiAlarms.unshift(item)
  return item
}

function ackAiAlarm(id) {
  const item = localAiAlarms.find(a => a.id === id)
  if (!item) return null
  item.alarmStatus = 'acked'
  return item
}

function ensureAiAlarmMock(payload) {
  return createAiAlarm(payload)
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
  updatePurchase,
  deletePurchase,
  listEntries,
  createEntry,
  updateEntry,
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
