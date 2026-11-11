/**
 * 88：风味优化 ↔ 旧净化档案系统（yudao zk_purify_*）数据桥接
 * 采购管理 ↔ 采购记录；入池管理 ↔ 入池档案
 * 管理端为权威数据源：合并时优先旧系统记录，并解析字典码为可读标签
 */
import {
  fetchMeta,
  fetchEntries,
  fetchRecords,
  fetchStats,
  fetchOptimizing,
  fetchReadyExit,
  fetchOptimizations
} from '@/api/flavor'
import { getPurifyPurchasePage, createPurifyPurchase, updatePurifyPurchase, deletePurifyPurchase, getPurifyPurchase } from '@/api/fish/zkpurifypurchase'
import {
  getPurifyArchivePage,
  createPurifyArchive,
  updatePurifyArchive,
  deletePurifyArchive,
  getPurifyArchive,
  getRecordQueryPage,
  getPurifyStats
} from '@/api/fish/zkpurifyarchive'
import { getPurifyProcessPage, updatePurifyProcess, getPurifyProcess } from '@/api/fish/zkpurifyprocess'
import {
  getPurifyDailyMonitoringPage,
  createPurifyDailyMonitoring
} from '@/api/fish/zkpurifydailymonitoring'
import { createPurifyOutPool } from '@/api/fish/zkpurifyoutpool'
import { DICT_TYPE, getDictDataLabel, getDictDatas } from '@/utils/dict'

function pad(n) {
  return String(n).padStart(2, '0')
}

const OPT_LEVEL_MAP = {
  premium: '精品级 - 净化10天',
  wagyu: '和牛级 - 净化20天',
  tuna: '金枪鱼级 - 净化30天',
  gold: '金枪鱼级 - 净化30天',
  '精品级': '精品级 - 净化10天',
  '和牛级': '和牛级 - 净化20天',
  '金枪鱼级': '金枪鱼级 - 净化30天'
}

/** 统一日期/时间展示：支持时间戳、ISO、Date */
export function formatPurifyDateTime(val) {
  if (val == null || val === '') return ''
  if (typeof val === 'number' || /^\d{10,13}$/.test(String(val))) {
    const n = Number(val)
    const d = new Date(n < 1e12 ? n * 1000 : n)
    if (isNaN(d.getTime())) return String(val)
    return (
      d.getFullYear() +
      '-' +
      pad(d.getMonth() + 1) +
      '-' +
      pad(d.getDate()) +
      ' ' +
      pad(d.getHours()) +
      ':' +
      pad(d.getMinutes()) +
      ':' +
      pad(d.getSeconds())
    )
  }
  const s = String(val).replace('T', ' ')
  return s.length >= 19 ? s.slice(0, 19) : s.slice(0, 10)
}

export function formatPurifyDate(val) {
  const full = formatPurifyDateTime(val)
  return full ? full.slice(0, 10) : ''
}

function looksLikeDictCode(val) {
  const s = String(val == null ? '' : val).trim()
  if (!s) return false
  return /^\d+$/.test(s)
}

/** 品类：字典码 → 标签（与管理端 dict-tag 一致） */
export function resolvePurifyCategory(val) {
  if (val == null || val === '') return ''
  const s = String(val).trim()
  if (!looksLikeDictCode(s) && /[\u4e00-\u9fa5a-zA-Z]/.test(s)) return s
  try {
    const label = getDictDataLabel(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY, s)
    if (label && label !== s) return label
  } catch (e) { /* dict 未就绪时回退 */ }
  return s
}

export function resolvePurifyTransport(val) {
  if (val == null || val === '') return ''
  const s = String(val).trim()
  if (!looksLikeDictCode(s) && /[\u4e00-\u9fa5a-zA-Z]/.test(s)) return s
  try {
    const label = getDictDataLabel(DICT_TYPE.ZK_PURIFY_DICT_DATA_TRANSPORT, s)
    if (label && label !== s) return label
  } catch (e) { /* ignore */ }
  return s
}

export function resolvePurifyOptLevel(val) {
  if (val == null || val === '') return ''
  const s = String(val).trim()
  if (OPT_LEVEL_MAP[s]) return OPT_LEVEL_MAP[s]
  const lower = s.toLowerCase()
  if (OPT_LEVEL_MAP[lower]) return OPT_LEVEL_MAP[lower]
  return s
}

/** 标签 → 字典值（写入管理端时用） */
export function categoryLabelToCode(label) {
  const s = String(label || '').trim()
  if (!s) return s
  if (looksLikeDictCode(s)) return s
  try {
    const dicts = getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY) || []
    const hit = dicts.find(d => d && d.label === s)
    if (hit && hit.value != null) return String(hit.value)
  } catch (e) { /* ignore */ }
  const known = {
    鲈鱼: '1',
    大黄鱼: '2',
    石斑鱼: '3',
    黄花鱼: '4',
    鲷鱼: '5',
    鮰鱼: '3'
  }
  return known[s] || s
}

/** 旧风味/净化系统字典标签列表（与管理端下拉一致） */
export function getPurifyDictLabels(dictType) {
  try {
    return (getDictDatas(dictType) || [])
      .map(d => (d && d.label != null ? String(d.label).trim() : ''))
      .filter(Boolean)
  } catch (e) {
    return []
  }
}

function uniqLabels(list) {
  const out = []
  const seen = new Set()
  ;(list || []).forEach((raw) => {
    const s = String(raw == null ? '' : raw).trim()
    if (!s || seen.has(s)) return
    seen.add(s)
    out.push(s)
  })
  return out
}

/**
 * 合并风味 meta 与旧系统字典的品类/来源/规格/运输方式。
 * 品类取并集：甲方在基础档案新增、以及系统字典已有项都可出现在下拉中。
 */
export function mergeFlavorMetaWithPurify(meta) {
  const base = meta && typeof meta === 'object' ? { ...meta } : {}
  const categories = getPurifyDictLabels(DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY)
  const sources = getPurifyDictLabels(DICT_TYPE.ZK_PURIFY_DICT_DATA_SOURCE)
  const grades = getPurifyDictLabels(DICT_TYPE.ZK_PURIFY_DICT_DATA_SPEC)
  const transportMethods = getPurifyDictLabels(DICT_TYPE.ZK_PURIFY_DICT_DATA_TRANSPORT)
  return {
    ...base,
    categories: uniqLabels([...(base.categories || []), ...categories]),
    sources: sources.length ? sources : (base.sources || []),
    grades: grades.length ? grades : (base.grades || []),
    transportMethods: transportMethods.length ? transportMethods : (base.transportMethods || [])
  }
}

/** 从入池记录（风味入池 + 管理端入池档案）收集已出现的鱼种名称 */
export function collectCategoriesFromEntries(entries) {
  return uniqLabels((entries || []).map((r) => (r && r.category) || ''))
}

/**
 * 拉取入池记录中出现、但尚未纳入「可用鱼类」列表的鱼种。
 * 用于基础档案一键纳入，以及采购等下拉允许使用历史入池鱼种。
 */
export async function loadEntryCategoryHints(configuredLabels) {
  const configured = new Set(uniqLabels(configuredLabels))
  let entries = []
  try {
    entries = await loadMergedEntries()
  } catch (e) {
    console.warn('[flavorPurifyBridge] 入池鱼种扫描失败', e && (e.message || e))
    entries = []
  }
  const fromEntries = collectCategoriesFromEntries(entries)
  return {
    fromEntries,
    missing: fromEntries.filter((c) => !configured.has(c))
  }
}

/**
 * 将基础档案中的可用鱼类同步到系统字典 zk_purify_dict_data_category，
 * 保证采购/入池下拉与码值映射一致；以 desiredLabels 为权威列表。
 */
export async function syncFlavorCategoriesToPurifyDict(desiredLabels) {
  const { listData, addData, delData } = await import('@/api/system/dict/data')
  const dictType = DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY
  const desired = uniqLabels(desiredLabels)
  const pageRes = await listData({
    dictType,
    pageNo: 1,
    pageSize: 200
  })
  const existing = (pageRes && pageRes.data && pageRes.data.list) || []
  const byLabel = new Map()
  existing.forEach((row) => {
    const label = String((row && row.label) || '').trim()
    if (label) byLabel.set(label, row)
  })

  let maxVal = 0
  existing.forEach((row) => {
    const n = parseInt(row && row.value, 10)
    if (Number.isFinite(n) && n > maxVal) maxVal = n
  })

  for (let i = 0; i < desired.length; i++) {
    const label = desired[i]
    if (byLabel.has(label)) continue
    maxVal += 1
    await addData({
      dictType,
      label,
      value: String(maxVal),
      sort: maxVal,
      status: 0,
      remark: '风味优化可用鱼类'
    })
  }

  const desiredSet = new Set(desired)
  // 空列表不批量清空系统字典，避免误保存把历史品类删光
  if (desired.length) {
    for (let i = 0; i < existing.length; i++) {
      const row = existing[i]
      const label = String((row && row.label) || '').trim()
      if (!label || desiredSet.has(label)) continue
      if (row && row.id != null) {
        await delData(row.id)
      }
    }
  }

  return desired
}

/** 拉取风味 meta，并与旧净化字典、入池已有鱼种同步可选品类等 */
export async function fetchFlavorMetaSynced(options) {
  const opts = options || {}
  const res = await fetchMeta()
  let data = mergeFlavorMetaWithPurify((res && res.data) || {})
  if (opts.includeEntryCategories !== false) {
    try {
      const hints = await loadEntryCategoryHints(data.categories || [])
      data = {
        ...data,
        categories: uniqLabels([...(data.categories || []), ...(hints.fromEntries || [])])
      }
    } catch (e) {
      /* 入池扫描失败时仍返回字典品类 */
    }
  }
  return { ...(res || {}), data }
}

export function transportLabelToCode(label) {
  const s = String(label || '').trim()
  if (!s) return s
  if (looksLikeDictCode(s)) return s
  try {
    const dicts = getDictDatas(DICT_TYPE.ZK_PURIFY_DICT_DATA_TRANSPORT) || []
    const hit = dicts.find(d => d.label === s)
    if (hit) return hit.value
  } catch (e) { /* ignore */ }
  return s
}

export function optLevelLabelToCode(label) {
  const s = String(label || '').trim()
  if (!s) return s
  const reverse = {
    '精品级 - 净化10天': 'premium',
    '和牛级 - 净化20天': 'wagyu',
    '金枪鱼级 - 净化30天': 'tuna'
  }
  if (reverse[s]) return reverse[s]
  const lower = s.toLowerCase()
  if (lower === 'gold') return 'tuna'
  if (['premium', 'wagyu', 'tuna'].includes(lower)) return lower
  return s
}

function purifyPurchaseToForm(data) {
  if (!data) return null
  return {
    purchaseOrderNo: data.purchaseOrderNo || '',
    category: resolvePurifyCategory(data.category),
    purchaseDate: formatPurifyDate(data.purchaseDate),
    weight: Number(data.quantity) || 0,
    unitPrice: Number(data.unitPrice) || 0,
    unitWeight: Number(data.avgWeight) || 0,
    amount: Number(data.totalAmount) || (Number(data.quantity) || 0) * (Number(data.unitPrice) || 0),
    manager: data.purchaseManager || '',
    source: data.source || '',
    address: data.detailedAddress || '',
    contact: data.contactPerson || '',
    phone: data.contactPhone || '',
    transportMethod: resolvePurifyTransport(data.transportMethod),
    remark: data.notes || ''
  }
}

function purifyArchiveToForm(data) {
  if (!data) return null
  return {
    purchaseId: data.purchaseOrderNo || '',
    category: resolvePurifyCategory(data.category),
    source: data.source || '',
    entryTime: formatPurifyDateTime(data.inPoolTime),
    totalWeight: Number(data.inPoolWeight) || 0,
    unitWeight: Number(data.avgWeight) || 0,
    pondCode: data.poolNumber || '',
    optLevel: resolvePurifyOptLevel(data.optimizationLevel),
    appearance: data.appearanceScore || '',
    vitality: data.vitalityScore || '',
    health: data.healthStatus || '',
    uniformity: data.specUniformity || '',
    stress: data.stressLevel || '',
    survivalRate: data.expectedSurvivalRate != null ? Number(data.expectedSurvivalRate) : '',
    riskLevel: data.riskLevel || '',
    assessor: data.assessor || '',
    manager: data.assessor || '',
    reportFile: data.inspectionReport || '',
    receiver: data.receiver || data.assessor || ''
  }
}

function buildPurifyPurchasePayload(form, id) {
  const payload = {
    purchaseOrderNo: form.purchaseOrderNo || form.id || 'FW-PUR-' + Date.now(),
    category: categoryLabelToCode(form.category),
    source: form.source || '',
    detailedAddress: form.address || '',
    contactPerson: form.contact || '',
    contactPhone: form.phone || '',
    quantity: Number(form.weight) || 0,
    unitPrice: Number(form.unitPrice) || 0,
    totalAmount:
      Number(form.amount) ||
      (Number(form.weight) || 0) * (Number(form.unitPrice) || 0),
    transportMethod: transportLabelToCode(form.transportMethod),
    // 管理端 LocalDateTime 仅接受 epoch 毫秒
    purchaseDate: form.purchaseDate
      ? toEpochMs(String(form.purchaseDate).slice(0, 10) + ' 00:00:00')
      : Date.now(),
    purchaseManager: form.manager || '',
    avgWeight: Number(form.unitWeight) || 0,
    notes: form.remark || '由风味优化采购管理写入'
  }
  if (id != null) payload.id = id
  return payload
}

/** 管理端 LocalDateTime 仅接受 epoch 毫秒 */
function toEpochMs(val) {
  if (val == null || val === '') return Date.now()
  if (typeof val === 'number' && !Number.isNaN(val)) {
    return val < 1e12 ? val * 1000 : val
  }
  const s = String(val).trim()
  if (/^\d{10,13}$/.test(s)) {
    const n = Number(s)
    return n < 1e12 ? n * 1000 : n
  }
  const d = new Date(s.replace(/-/g, '/').replace('T', ' '))
  return Number.isNaN(d.getTime()) ? Date.now() : d.getTime()
}

function buildPurifyArchivePayload(form, extras, id) {
  const ext = extras || {}
  const recordNo = form.recordNo || ext.recordNo || ''
  const payload = {
    // 优先用风味单号，保证列表可对上新建记录
    purchaseOrderNo: recordNo || form.purchaseId || '',
    category: categoryLabelToCode(form.category),
    source: form.source || '',
    inPoolTime: toEpochMs(form.entryTime),
    inPoolWeight: Number(form.totalWeight) || 0,
    avgWeight: Number(form.unitWeight) || 0,
    inPoolCount: Number(ext.quantity != null ? ext.quantity : form.quantity) || 0,
    poolNumber: form.pondCode || '',
    density: Number(ext.density != null ? ext.density : form.density) || 0,
    optimizationLevel: optLevelLabelToCode(form.optLevel),
    inspectionReport: form.reportFile || '',
    appearanceScore: form.appearance || '',
    vitalityScore: form.vitality || '',
    healthStatus: form.health || '',
    specUniformity: form.uniformity || '',
    stressLevel: form.stress || '',
    expectedSurvivalRate: form.survivalRate != null ? Number(form.survivalRate) : undefined,
    riskLevel: form.riskLevel || '',
    assessor: form.assessor || form.manager || ''
  }
  if (form.purchaseRecordId != null && form.purchaseRecordId !== '') {
    payload.purchaseRecordId = Number(form.purchaseRecordId)
  } else if (ext.purchaseRecordId != null && ext.purchaseRecordId !== '') {
    payload.purchaseRecordId = Number(ext.purchaseRecordId)
  }
  if (id != null) payload.id = id
  return payload
}

/** 加载管理端采购记录供编辑 */
export async function loadPurifyPurchaseForm(row) {
  if (!row || !row._purifyId) throw new Error('无效的管理端采购记录')
  const res = await getPurifyPurchase(row._purifyId)
  const data = res && res.data
  if (!data) throw new Error('采购记录不存在')
  return purifyPurchaseToForm(data)
}

/** 更新管理端采购记录 */
export async function updatePurifyPurchaseRecord(form, row) {
  if (!row || !row._purifyId) return false
  await updatePurifyPurchase(buildPurifyPurchasePayload(form, row._purifyId))
  return true
}

/** 删除管理端采购记录 */
export async function deletePurifyPurchaseRecord(row) {
  if (!row || !row._purifyId) return false
  await deletePurifyPurchase(row._purifyId)
  return true
}

/** 加载管理端入池档案供编辑 */
export async function loadPurifyArchiveForm(row) {
  if (!row || !row._purifyArchiveId) throw new Error('无效的管理端入池档案')
  const res = await getPurifyArchive(row._purifyArchiveId)
  const data = res && res.data
  if (!data) throw new Error('入池档案不存在')
  return purifyArchiveToForm(data)
}

/** 更新管理端入池档案 */
export async function updatePurifyArchiveRecord(form, extras, row) {
  if (!row || !row._purifyArchiveId) return false
  await updatePurifyArchive(buildPurifyArchivePayload(form, extras, row._purifyArchiveId))
  return true
}

/** 删除管理端入池档案 */
export async function deletePurifyArchiveRecord(row) {
  if (!row || !row._purifyArchiveId) return false
  await deletePurifyArchive(row._purifyArchiveId)
  return true
}

export function mapPurifyPurchase(row) {
  if (!row) return null
  const id = row.purchaseOrderNo || 'PURIFY-' + row.id
  return {
    id,
    category: resolvePurifyCategory(row.category),
    categoryCode: row.category != null ? String(row.category) : '',
    purchaseDate: formatPurifyDate(row.purchaseDate),
    weight: Number(row.quantity) || 0,
    unitPrice: Number(row.unitPrice) || 0,
    unitWeight: Number(row.avgWeight) || 0,
    amount:
      Number(row.totalAmount) ||
      (Number(row.quantity) || 0) * (Number(row.unitPrice) || 0),
    source: row.source || '',
    address: row.detailedAddress || '',
    contact: row.contactPerson || '',
    phone: row.contactPhone || '',
    transportMethod: resolvePurifyTransport(row.transportMethod),
    manager: row.purchaseManager || '',
    remark: row.notes || '',
    fromPurify: true,
    sourceLabel: '管理端采购记录',
    _purifyId: row.id
  }
}

export function mapPurifyArchive(row) {
  if (!row) return null
  const id = row.purchaseOrderNo
    ? 'ARCH-' + row.id + '-' + row.purchaseOrderNo
    : 'ARCH-' + row.id
  const qty = Number(row.inPoolCount) || 0
  const totalWeight = Number(row.inPoolWeight) || 0
  const unitWeight = Number(row.avgWeight) || (qty > 0 ? totalWeight / qty : 0)
  return {
    id,
    recordNo: row.purchaseOrderNo || id,
    purchaseId: row.purchaseOrderNo || '',
    category: resolvePurifyCategory(row.category),
    categoryCode: row.category != null ? String(row.category) : '',
    source: row.source || '',
    entryTime: formatPurifyDateTime(row.inPoolTime),
    totalWeight,
    unitWeight,
    quantity: qty,
    density: Number(row.density) || 0,
    unitPrice: '',
    manager: row.assessor || '',
    pondCode: row.poolNumber || '',
    optLevel: resolvePurifyOptLevel(row.optimizationLevel),
    status: 'entered',
    statusLabel: '已入池',
    appearance: row.appearanceScore || '',
    vitality: row.vitalityScore || '',
    health: row.healthStatus || '',
    uniformity: row.specUniformity || '',
    stress: row.stressLevel || '',
    survivalRate: row.expectedSurvivalRate != null ? Number(row.expectedSurvivalRate) : '',
    riskLevel: row.riskLevel || '',
    assessor: row.assessor || '',
    reportFile: row.inspectionReport || '',
    fromPurify: true,
    sourceLabel: '管理端入池档案',
    _purifyArchiveId: row.id
  }
}

async function fetchAllPurifyPages(fetchPage, pageSize) {
  const size = pageSize || 200
  let pageNo = 1
  let list = []
  let total = Infinity
  while (list.length < total && pageNo <= 20) {
    const res = await fetchPage({ pageNo, pageSize: size })
    const data = (res && res.data) || {}
    const chunk = Array.isArray(data.list) ? data.list : []
    total = Number(data.total != null ? data.total : chunk.length) || chunk.length
    list = list.concat(chunk)
    if (!chunk.length || chunk.length < size) break
    pageNo += 1
  }
  return list
}

const FLAVOR_STATUS_TO_PURIFY = {
  entered: 'farming',
  optimizing: 'farming',
  exited: 'out_pool'
}

const PURIFY_STATUS_TO_FLAVOR = {
  farming: 'optimizing',
  out_pool: 'exited'
}

export function resolvePurifyProcessStatus(val) {
  if (val == null || val === '') return ''
  const s = String(val).trim()
  try {
    const label = getDictDataLabel(DICT_TYPE.ZK_PURIFY_PROCESS_STATUS, s)
    if (label && label !== s) return label
  } catch (e) { /* ignore */ }
  if (s === 'farming') return '优化中'
  if (s === 'out_pool') return '已出池'
  return s
}

function buildPurifyRecordQuery(query = {}) {
  const params = { pageNo: 1, pageSize: 500 }
  if (query.recordNo) params.recordNumber = query.recordNo
  if (query.category) params.category = categoryLabelToCode(query.category) || query.category
  if (query.pondCode) params.poolNumber = query.pondCode
  if (query.status) params.status = FLAVOR_STATUS_TO_PURIFY[query.status] || query.status
  if (query.minMortality != null && query.minMortality !== '') params.minMortalityRate = query.minMortality
  if (query.maxMortality != null && query.maxMortality !== '') params.maxMortalityRate = query.maxMortality
  if (query.minWeightLoss != null && query.minWeightLoss !== '') params.minScaleDropRate = query.minWeightLoss
  if (query.maxWeightLoss != null && query.maxWeightLoss !== '') params.maxScaleDropRate = query.maxWeightLoss
  return params
}

function buildPurifyStatsQuery(params = {}) {
  const q = {}
  if (params.category) q.category = categoryLabelToCode(params.category) || params.category
  if (params.recordNo) q.recordNumber = params.recordNo
  if (params.range) {
    const m = { '7d': '7', '30d': '30', '90d': '90' }
    q.timeRange = m[params.range] || params.range
  }
  return q
}

function filterRecordsByDate(list, query = {}) {
  let rows = list || []
  if (query.startDate) {
    rows = rows.filter(i => (i.entryTime || '').slice(0, 10) >= query.startDate)
  }
  if (query.endDate) {
    rows = rows.filter(i => (i.entryTime || '').slice(0, 10) <= query.endDate)
  }
  return rows
}

export function mapPurifyRecord(row) {
  if (!row) return null
  const recordNo = row.recordNumber || row.purchaseOrderNo || ('REC-' + row.id)
  const flavorStatus = PURIFY_STATUS_TO_FLAVOR[row.status] || row.status || 'optimizing'
  return {
    id: 'PURIFY-REC-' + row.id,
    recordNo,
    category: resolvePurifyCategory(row.category),
    status: flavorStatus,
    statusLabel: resolvePurifyProcessStatus(row.status),
    source: row.source || '管理端',
    sourceLabel: '管理端',
    pondCode: row.poolNumber || '',
    entryTime: formatPurifyDateTime(row.inPoolTime),
    exitTime: row.totalOutWeight > 0 ? formatPurifyDateTime(row.updateTime || row.createTime) : '',
    totalWeight: Number(row.inPoolWeight) || 0,
    exitWeight: Number(row.totalOutWeight) || 0,
    mortalityRate: Number(row.deathRate) || 0,
    weightLossRate: Number(row.weightLossRate) || 0,
    fromPurify: true,
    _purifyArchiveId: row.id
  }
}

export function mapPurifyProcess(row, archiveMap) {
  if (!row) return null
  const archive = (archiveMap && row.archiveId && archiveMap[row.archiveId]) || null
  const flavorStatus = PURIFY_STATUS_TO_FLAVOR[row.status] || 'optimizing'
  const quantity =
    Number(row.currentStockCount) ||
    Number((archive && archive.inPoolCount) || 0) ||
    0
  return {
    id: 'PURIFY-PROC-' + row.id,
    recordNo: row.recordNumber || '',
    category: resolvePurifyCategory(row.category || (archive && archive.category)),
    pondCode: row.poolNumber || (archive && archive.poolNumber) || '',
    entryTime: formatPurifyDateTime(row.startTime || (archive && archive.inPoolTime)),
    totalWeight: Number((archive && archive.inPoolWeight) || row.totalOutWeight) || 0,
    quantity,
    currentStockCount: Number(row.currentStockCount) || quantity,
    deathCount: Number(row.totalDeathCount) || 0,
    mortalityRate: Number(row.mortalityRate) || 0,
    status: flavorStatus,
    statusLabel: resolvePurifyProcessStatus(row.status),
    fromPurify: true,
    sourceLabel: '管理端',
    _purifyProcessId: row.id,
    _purifyArchiveId: row.archiveId,
    laborCost: 0,
    electricityCost: 0,
    feedCost: 0,
    protectCost: 0
  }
}

function mapPurifyMonitoring(row, processMap) {
  if (!row) return null
  const proc =
    (processMap && row.processId != null && processMap[row.processId]) || null
  return {
    id: 'PURIFY-MON-' + row.id,
    entryId: row.processId ? 'PURIFY-PROC-' + row.processId : '',
    recordNo:
      row.recordNumber ||
      (proc && (proc.recordNumber || proc.recordNo)) ||
      '',
    measureDate: formatPurifyDate(row.monitorDate),
    deathCount: row.deathCount || 0,
    deathWeight: Number(row.deathWeight) || 0,
    waterTemp: Number(row.temperature) || 0,
    dissolvedOxygen: Number(row.oxygen) || 0,
    measure: row.healthStatus || '日常监测',
    operator: '',
    fromPurify: true
  }
}

function mapPurifyStatsToFlavor(data) {
  if (!data) return null
  const categoryDist = Object.entries(data.categoryDistribution || {}).map(([name, count]) => ({
    name: resolvePurifyCategory(name),
    count: Number(count) || 0,
    percent: 0
  }))
  const totalBatches = categoryDist.reduce((s, i) => s + i.count, 0) || 1
  categoryDist.forEach(i => {
    i.percent = Number(((i.count / totalBatches) * 100).toFixed(1))
  })

  const specComparison = data.specComparison || {}
  const specLabels = Object.keys(specComparison)
  let entrySpec = 0
  let exitSpec = 0
  if (specLabels.length) {
    entrySpec =
      specLabels.reduce((s, k) => s + ((specComparison[k] && specComparison[k][0]) || 0), 0) /
      specLabels.length
    exitSpec =
      specLabels.reduce((s, k) => s + ((specComparison[k] && specComparison[k][1]) || 0), 0) /
      specLabels.length
  }

  const stageAnalysis = data.stageMortalityAnalysis || []
  const stageTrend = {
    day3: stageAnalysis.length
      ? Number(
        (stageAnalysis.reduce((s, i) => s + Number(i.day3Rate || 0), 0) / stageAnalysis.length).toFixed(2)
      )
      : 0,
    day5: stageAnalysis.length
      ? Number(
        (stageAnalysis.reduce((s, i) => s + Number(i.day5Rate || 0), 0) / stageAnalysis.length).toFixed(2)
      )
      : 0,
    day7: stageAnalysis.length
      ? Number(
        (stageAnalysis.reduce((s, i) => s + Number(i.day7Rate || 0), 0) / stageAnalysis.length).toFixed(2)
      )
      : 0
  }

  const scatterMap = {}
  ;(data.batchScatterData || []).forEach(d => {
    if (d && d.recordNumber) scatterMap[d.recordNumber] = d
  })

  const batchDetails = stageAnalysis.map(i => {
    const scatter = scatterMap[i.recordNumber] || {}
    return {
      recordNo: i.recordNumber,
      category: resolvePurifyCategory(i.category),
      pondCode: i.poolNumber || '',
      deathCount: i.totalDeaths || 0,
      mortalityRate: Number(i.totalRate) || 0,
      day3Rate: Number(i.day3Rate) || 0,
      day5Rate: Number(i.day5Rate) || 0,
      day7Rate: Number(i.day7Rate) || 0,
      weightLossRate: Number(scatter.weightLossRate) || 0
    }
  })

  const scatterData = (data.batchScatterData || []).map(d => ({
    recordNo: d.recordNumber,
    category: resolvePurifyCategory(d.category),
    x: Number(d.weightLossRate) || 0,
    y: Number(d.mortalityRate) || 0,
    size: Number(d.inPoolWeight) || 0
  }))

  const entryWeight = Number(data.totalInWeight) || 0
  const exitWeight = Number(data.totalOutWeight) || 0

  return {
    weightChange: Number((exitWeight - entryWeight).toFixed(2)),
    entryWeight,
    exitWeight,
    mortalityRate: Number(data.deathRate) || 0,
    deathCount: data.totalDeathCount || 0,
    deathWeight: Number(data.totalDeathWeight) || 0,
    weightLossRate: Number(data.weightLossRate) || 0,
    weightLoss: Number(data.totalWeightLoss) || 0,
    salesAmount: Number(data.totalSalesAmount) || 0,
    avgPrice: Number(data.averagePrice) || 0,
    feedAmount: Number(data.totalFeedAmount) || 0,
    costs: {
      purchaseCost: Number(data.totalPurchaseCost) || 0,
      laborCost: Number(data.totalLaborCost) || 0,
      electricityCost: Number(data.totalElectricityCost) || 0,
      feedCost: Number(data.totalFeedCost) || 0,
      protectCost: Number(data.totalHealthProductCost) || 0,
      totalCost: Number(data.totalCost) || 0,
      profit: Number(data.totalProfit) || 0,
      profitRate: Number(data.profitMargin) || 0
    },
    categoryDist,
    specChange: { entrySpec: Number(entrySpec.toFixed(3)), exitSpec: Number(exitSpec.toFixed(3)) },
    stageTrend,
    batchDetails,
    scatterData,
    recordOptions: stageAnalysis.map(i => i.recordNumber).filter(Boolean)
  }
}

function hasPurifyStats(data) {
  if (!data) return false
  return (
    (data.entryWeight || 0) > 0 ||
    (data.batchDetails && data.batchDetails.length > 0) ||
    (data.categoryDist && data.categoryDist.length > 0)
  )
}

function mergeByRecordNo(purifyRows, flavorRows) {
  const map = new Map()
  ;(purifyRows || []).forEach(r => {
    if (!r || !r.recordNo) return
    map.set(String(r.recordNo), r)
  })
  ;(flavorRows || []).forEach(r => {
    if (!r || !r.recordNo) return
    if (!map.has(String(r.recordNo))) {
      map.set(String(r.recordNo), {
        ...r,
        category: resolvePurifyCategory(r.category) || r.category,
        fromPurify: false,
        sourceLabel: r.sourceLabel || '风味模块',
        source: r.source || '风味模块'
      })
    }
  })
  return Array.from(map.values())
}

function mergeProcessLists(purifyRows, flavorRows) {
  const map = new Map()
  ;(purifyRows || []).forEach(r => {
    if (!r || !r.recordNo) return
    map.set(String(r.recordNo), r)
  })
  ;(flavorRows || []).forEach(r => {
    if (!r || !r.recordNo) return
    if (!map.has(String(r.recordNo))) {
      map.set(String(r.recordNo), {
        ...r,
        category: resolvePurifyCategory(r.category) || r.category,
        fromPurify: false,
        sourceLabel: '风味模块'
      })
    }
  })
  return Array.from(map.values())
}

async function loadPurifyProcesses(status) {
  const params = { pageSize: 500 }
  if (status) params.status = status
  const [processList, archiveList] = await Promise.all([
    fetchAllPurifyPages(p => getPurifyProcessPage({ ...params, ...p })).catch(() => []),
    fetchAllPurifyPages(getPurifyArchivePage).catch(() => [])
  ])
  const archiveMap = {}
  archiveList.forEach(a => {
    archiveMap[a.id] = a
  })
  return processList.map(p => mapPurifyProcess(p, archiveMap)).filter(Boolean)
}

function findEntryRow(list, entryId) {
  if (entryId == null || entryId === '') return null
  const key = String(entryId)
  return (list || []).find(r => r && String(r.id) === key) || null
}

/**
 * 合并：管理端优先（同采购单号以 zk_purify_* 为准），再补风味自有记录
 */
function mergePreferPurify(purifyRows, flavorRows) {
  const map = new Map()
  ;(purifyRows || []).forEach(r => {
    if (!r || r.id == null) return
    map.set(String(r.id), r)
  })
  ;(flavorRows || []).forEach(r => {
    if (!r || r.id == null) return
    const key = String(r.id)
    if (!map.has(key)) {
      map.set(key, {
        ...r,
        category: resolvePurifyCategory(r.category) || r.category,
        fromPurify: false,
        sourceLabel: r.sourceLabel || '风味模块'
      })
    }
  })
  return Array.from(map.values())
}

/**
 * 采购记录唯一权威来源：管理端 zk_purify_purchase
 * （不再合并风味本地采购，避免双源漂移）
 * @param {{ force?: boolean }} [opts]
 */
let _purchaseCache = { at: 0, list: null }
const PURCHASE_CACHE_TTL_MS = 60 * 1000

export async function loadMergedPurchases(opts) {
  const force = !!(opts && opts.force)
  if (!force && _purchaseCache.list && Date.now() - _purchaseCache.at < PURCHASE_CACHE_TTL_MS) {
    return _purchaseCache.list.slice()
  }
  const purifyList = await fetchAllPurifyPages(getPurifyPurchasePage).catch(err => {
    console.warn('[flavorPurifyBridge] 采购记录拉取失败', err && err.message)
    return []
  })
  const list = purifyList
    .map(mapPurifyPurchase)
    .filter(Boolean)
    .sort((a, b) => String(b.purchaseDate || '').localeCompare(String(a.purchaseDate || '')))
  _purchaseCache = { at: Date.now(), list }
  return list.slice()
}

/** 风味侧写入采购：只落管理端采购表 */
export async function syncPurchaseToPurify(form) {
  if (!form) return null
  try {
    const payload = buildPurifyPurchasePayload(form)
    const res = await createPurifyPurchase(payload)
    return res
  } catch (e) {
    console.warn('[flavorPurifyBridge] 写入管理端采购失败', e && (e.message || e.msg))
    return null
  }
}

/** 合并风味入池 + 旧系统入池档案（管理端优先；按单号去重，新记录在前） */
export async function loadMergedEntries() {
  const [flavorRes, archiveList] = await Promise.all([
    fetchEntries().catch(() => ({ data: [] })),
    fetchAllPurifyPages(getPurifyArchivePage).catch(err => {
      console.warn('[flavorPurifyBridge] 入池档案拉取失败', err && err.message)
      return []
    })
  ])
  const flavor = (Array.isArray(flavorRes && flavorRes.data) ? flavorRes.data : []).map(r => ({
    ...r,
    entryTime: formatPurifyDateTime(r.entryTime) || r.entryTime,
    fromPurify: false,
    sourceLabel: '风味模块'
  }))
  const archive = archiveList.map(mapPurifyArchive).filter(Boolean)
  const byRecord = new Map()
  archive.forEach(r => {
    const key = String(r.recordNo || r.id)
    byRecord.set(key, r)
  })
  flavor.forEach(r => {
    const key = String(r.recordNo || r.id)
    if (!byRecord.has(key)) byRecord.set(key, r)
  })
  return Array.from(byRecord.values()).sort((a, b) =>
    String(b.entryTime || '').localeCompare(String(a.entryTime || ''))
  )
}

/** 风味新建入池时同步写入管理端入池档案，并对齐流程单号便于合并去重 */
export async function syncEntryToPurify(form, extras) {
  if (!form) return null
  const ext = extras || {}
  try {
    const payload = buildPurifyArchivePayload(form, ext)
    const res = await createPurifyArchive(payload, {
      silent: true,
      hideErrorMessage: true
    })
    const archiveId = res && (res.data != null ? res.data : null)
    const recordNo = form.recordNo || ext.recordNo || payload.purchaseOrderNo || ''
    if (archiveId != null && recordNo) {
      try {
        const pageRes = await getPurifyProcessPage({
          archiveId,
          pageNo: 1,
          pageSize: 5
        })
        const list = (pageRes && pageRes.data && pageRes.data.list) || []
        const proc = list[0]
        if (proc && proc.id) {
          await updatePurifyProcess(
            {
              id: proc.id,
              archiveId: Number(proc.archiveId != null ? proc.archiveId : archiveId),
              recordNumber: recordNo,
              status: proc.status || 'farming',
              currentStockCount:
                proc.currentStockCount != null
                  ? proc.currentStockCount
                  : Number(ext.quantity) || Number(form.quantity) || 0
            },
            { silent: true, hideErrorMessage: true }
          )
        }
      } catch (e) {
        console.warn('[flavorPurifyBridge] 对齐流程单号失败', e && (e.message || e.msg))
      }
    }
    return res
  } catch (e) {
    console.warn('[flavorPurifyBridge] 同步入池到管理端失败', e && (e.message || e.msg))
    return null
  }
}

/** 合并记录查询：管理端 record-query-page + 风味 records */
export async function loadMergedRecords(query = {}) {
  const purifyParams = buildPurifyRecordQuery(query)
  const [flavorRes, purifyList] = await Promise.all([
    fetchRecords(query).catch(() => ({ data: [] })),
    fetchAllPurifyPages(p => getRecordQueryPage({ ...purifyParams, ...p })).catch(err => {
      console.warn('[flavorPurifyBridge] 记录查询拉取失败', err && err.message)
      return []
    })
  ])
  const flavor = (Array.isArray(flavorRes && flavorRes.data) ? flavorRes.data : []).map(r => ({
    ...r,
    entryTime: formatPurifyDateTime(r.entryTime) || r.entryTime,
    fromPurify: false,
    sourceLabel: '风味模块',
    source: r.source || '风味模块'
  }))
  const purify = purifyList.map(mapPurifyRecord).filter(Boolean)
  return filterRecordsByDate(mergeByRecordNo(purify, flavor), query)
}

/** 合并数据统计：优先管理端 get-stats */
export async function loadMergedStats(params = {}) {
  const purifyParams = buildPurifyStatsQuery(params)
  const [flavorRes, purifyRes] = await Promise.all([
    fetchStats(params).catch(() => ({ data: {} })),
    getPurifyStats(purifyParams).catch(err => {
      console.warn('[flavorPurifyBridge] 统计数据拉取失败', err && err.message)
      return { data: null }
    })
  ])
  const purifyMapped = mapPurifyStatsToFlavor(purifyRes && purifyRes.data)
  const flavorData = (flavorRes && flavorRes.data) || {}
  if (hasPurifyStats(purifyMapped)) {
    const opts = new Set([
      ...(purifyMapped.recordOptions || []),
      ...(flavorData.recordOptions || [])
    ])
    purifyMapped.recordOptions = Array.from(opts)
    // 补齐风味侧已出池但管理端统计尚未覆盖的重量（避免统计无变化）
    if ((flavorData.exitWeight || 0) > (purifyMapped.exitWeight || 0)) {
      purifyMapped.exitWeight = flavorData.exitWeight
    }
    if ((flavorData.entryWeight || 0) > (purifyMapped.entryWeight || 0)) {
      purifyMapped.entryWeight = flavorData.entryWeight
    }
    return purifyMapped
  }
  return flavorData
}

/** 合并优化中记录：管理端 farming 流程 + 风味 optimizing */
export async function loadMergedOptimizing() {
  const [flavorRes, purifyList] = await Promise.all([
    fetchOptimizing().catch(() => ({ data: [] })),
    loadPurifyProcesses('farming').catch(() => [])
  ])
  const flavor = (Array.isArray(flavorRes && flavorRes.data) ? flavorRes.data : []).map(r => ({
    ...r,
    fromPurify: false,
    sourceLabel: '风味模块'
  }))
  return mergeProcessLists(purifyList, flavor)
}

/** 合并可出池记录：与管理端 farming 流程同源 */
export async function loadMergedReadyExit() {
  const [flavorRes, purifyList] = await Promise.all([
    fetchReadyExit().catch(() => ({ data: [] })),
    loadPurifyProcesses('farming').catch(() => [])
  ])
  const flavor = (Array.isArray(flavorRes && flavorRes.data) ? flavorRes.data : []).map(r => ({
    ...r,
    fromPurify: false,
    sourceLabel: '风味模块'
  }))
  return mergeProcessLists(purifyList, flavor)
}

/** 合并优化过程日志：管理端日常监测 + 风味 optimizations（同单同日优先风味） */
export async function loadMergedOptimizations(entryId) {
  const [flavorRes, purifyList, processList] = await Promise.all([
    fetchOptimizations(entryId).catch(() => ({ data: [] })),
    fetchAllPurifyPages(getPurifyDailyMonitoringPage).catch(() => []),
    fetchAllPurifyPages(getPurifyProcessPage).catch(() => [])
  ])
  const processMap = {}
  ;(processList || []).forEach(p => {
    if (p && p.id != null) processMap[p.id] = p
  })
  let purify = purifyList.map(row => mapPurifyMonitoring(row, processMap)).filter(Boolean)
  if (entryId) {
    const procKey = String(entryId)
    purify = purify.filter(m => m.entryId === procKey)
  }
  let flavor = Array.isArray(flavorRes && flavorRes.data) ? flavorRes.data : []
  if (entryId) {
    flavor = flavor.filter(f => String(f.entryId) === String(entryId))
  }
  // 双写去重：风味侧已有同 entryId + 日期，则不再展示管理端同日监测
  const flavorKeys = new Set(
    flavor.map(f => `${String(f.entryId || '')}|${String(f.measureDate || '').slice(0, 10)}`)
  )
  purify = purify.filter(
    p => !flavorKeys.has(`${String(p.entryId || '')}|${String(p.measureDate || '').slice(0, 10)}`)
  )
  return [...purify, ...flavor].sort((a, b) =>
    String(b.measureDate || '').localeCompare(String(a.measureDate || ''))
  )
}

/** 风味添加优化记录时同步到管理端日常监测 */
export async function syncOptimizationToPurify(form, entryRow) {
  const processId = Number(entryRow && entryRow._purifyProcessId)
  if (!processId || Number.isNaN(processId) || !form) {
    throw new Error('缺少管理端流程信息，无法添加优化记录')
  }
  const monitorDate = form.measureDate
    ? new Date(String(form.measureDate).replace(/-/g, '/')).getTime()
    : Date.now()
  const healthParts = [
    form.measure,
    form.health ? `健康:${form.health}` : '',
    form.appearance ? `外观:${form.appearance}` : '',
    form.vitality ? `活力:${form.vitality}` : ''
  ].filter(Boolean)
  const payload = {
    processId,
    recordNumber: (entryRow && entryRow.recordNo) || undefined,
    monitorDate: Number.isNaN(monitorDate) ? Date.now() : monitorDate,
    oxygen: form.dissolvedOxygen != null && form.dissolvedOxygen !== '' ? Number(form.dissolvedOxygen) : undefined,
    temperature: form.waterTemp != null && form.waterTemp !== '' ? Number(form.waterTemp) : undefined,
    deathCount: Number(form.deathCount) || 0,
    deathWeight: Number(form.deathWeight) || 0,
    healthStatus: healthParts.join('；') || '日常监测'
  }
  return await createPurifyDailyMonitoring(payload)
}

/** 风味出池登记时同步到管理端出池记录，并完结流程（离开 farming 列表） */
export async function syncExitToPurify(form, entryRow) {
  const processId = entryRow && entryRow._purifyProcessId
  if (!processId || !form) {
    throw new Error('缺少管理端流程信息，无法出池')
  }
  const exitWeight = Number(form.exitWeight) || 0
  const salesAmount = Number(form.salesAmount) || 0

  let outPoolCount =
    Number(entryRow.currentStockCount) ||
    Number(entryRow.quantity) ||
    0
  let archiveId =
    entryRow._purifyArchiveId != null && entryRow._purifyArchiveId !== ''
      ? Number(entryRow._purifyArchiveId)
      : null
  let procSnapshot = null

  try {
    const procRes = await getPurifyProcess(processId)
    procSnapshot = procRes && procRes.data
    if (procSnapshot) {
      if ((archiveId == null || Number.isNaN(archiveId)) && procSnapshot.archiveId != null) {
        archiveId = Number(procSnapshot.archiveId)
      }
      if (!(outPoolCount > 0)) {
        outPoolCount =
          Number(procSnapshot.currentStockCount) ||
          Number(procSnapshot.totalOutCount) ||
          0
      }
    }
  } catch (e) {
    /* ignore fetch errors; still try out-pool with known fields */
  }

  // 出池登记视为整批出池：存塘数未知时至少传 1，避免 outPoolCount=0 导致流程仍停在 farming
  if (!(outPoolCount > 0)) outPoolCount = 1

  if (archiveId == null || Number.isNaN(archiveId)) {
    throw new Error('关联档案ID缺失，无法同步出池到管理端，请确认该入池已同步管理端档案')
  }

  const payload = {
    processId,
    outPoolTime: Date.now(),
    outPoolWeight: exitWeight,
    outPoolCount,
    totalAmount: salesAmount,
    salesPrice: exitWeight > 0 ? Number((salesAmount / exitWeight).toFixed(2)) : 0,
    fishQuality: form.appearance || form.health || ''
  }
  const res = await createPurifyOutPool(payload)

  // 出池服务端会按存塘重算状态；再强制一次 out_pool，确保列表刷新
  // SaveReqVO 要求 archiveId；静默失败避免全局再弹「关联档案ID不能为空」
  try {
    let needForce = true
    try {
      const afterRes = await getPurifyProcess(processId)
      const after = afterRes && afterRes.data
      if (after && String(after.status) === 'out_pool' && Number(after.currentStockCount) === 0) {
        needForce = false
      }
    } catch (e) {
      /* still force */
    }
    if (needForce) {
      await updatePurifyProcess(
        {
          id: processId,
          archiveId,
          status: 'out_pool',
          currentStockCount: 0,
          totalOutWeight: exitWeight,
          recordNumber:
            (procSnapshot && procSnapshot.recordNumber) ||
            entryRow.recordNo ||
            undefined,
          poolNumber:
            (procSnapshot && procSnapshot.poolNumber) ||
            entryRow.pondCode ||
            undefined,
          category:
            (procSnapshot && procSnapshot.category) ||
            entryRow.category ||
            undefined
        },
        { silent: true, hideErrorMessage: true }
      )
    }
  } catch (e) {
    console.warn('[flavorPurifyBridge] 更新流程状态失败', e && (e.message || e.msg || e))
  }

  return res
}

export { findEntryRow }
