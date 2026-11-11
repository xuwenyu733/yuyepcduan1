/**
 * 健康度预览/总览统一数据桥接
 * 主页、车间数控、鱼类健康页共用评分与命名规则
 *
 * 注意：zk-pond/page(pageSize=500) 在本环境可达数秒，不可阻塞首屏健康卡片。
 */
import { getLatestPondHealthList } from '@/api/fish/zkPondHealth'
import { getZkPondPage } from '@/api/fish/zkPond'
import { getPrecisionFeedingPonds } from '@/api/fish/zkBiologyCapital'
import { getThresholdModule, resolveHealthGrades, resolveHealthWeights, recomputeHealthOverall } from '@/utils/thresholdConfig'
import { applySpeciesWaterTempToHealthItem } from '@/utils/speciesTempBridge'
import { buildSiteDisplayLabel, withTimeout } from '@/utils/siteDisplay'
import { formatWorkshop } from '@/utils/siteNaming'

const POND_MAP_TTL_MS = 5 * 60 * 1000
const HEALTH_LIST_TTL_MS = 30 * 1000
/** 首屏最多等鱼池档案多久；超时用缓存/空 Map，后台继续拉 */
const POND_MAP_FAST_WAIT_MS = 400

let _pondMapCache = { map: null, at: 0 }
let _pondMapInflight = null
let _healthListCache = { data: null, at: 0 }
const _pondMapReadyWaiters = []

/** 登出 / 切换用户或部门时清空健康缓存，避免串部门 */
export function clearHealthPreviewCaches() {
  _pondMapCache = { map: null, at: 0 }
  _pondMapInflight = null
  _healthListCache = { data: null, at: 0 }
  _pondMapReadyWaiters.length = 0
}

export function healthStatusMeta(score, healthGrade) {
  const s = Number(score) || 0
  const excellentMin = (healthGrade && healthGrade.excellentMin) || 85
  const watchMin = (healthGrade && healthGrade.watchMin) || 70
  if (s < watchMin) {
    return { status: '不健康', cls: 'bg-red-dim border-red', statusCls: 'text-red' }
  }
  if (s < excellentMin) {
    return { status: '亚健康', cls: 'bg-yellow-dim border-yellow', statusCls: 'text-yellow' }
  }
  return { status: '健康', cls: 'bg-emerald-dim border-emerald', statusCls: 'text-emerald' }
}

export function scoreHealthRow(item, weights) {
  const envScore = Number(item.environmentalLayerRating)
  const appScore = Number(item.behavioralLayerRating)
  const intScore = Number(item.bodyLayerRating)
  const recomputed = recomputeHealthOverall({
    envScore: Number.isFinite(envScore) ? envScore : 0,
    appScore: Number.isFinite(appScore) ? appScore : 0,
    intScore: Number.isFinite(intScore) ? intScore : 0,
    baseEnv: 20,
    baseApp: 60,
    baseInt: 20
  }, weights || { env: 20, appearance: 60, internal: 20 })
  return Number(recomputed.overall) || Number(item.overallRating) || 0
}

function indexPondsById(pondList) {
  const map = new Map()
  ;(pondList || []).forEach((p) => {
    if (p && p.id != null) map.set(String(p.id), p)
  })
  return map
}

export function mapHealthRowToPreview(item, healthGrade, weights, pondMap) {
  if (item && item._pendingHealth) {
    const pond = (pondMap && item.pondId != null) ? (pondMap.get(String(item.pondId)) || {}) : {}
    const merged = Object.assign({}, pond, item, {
      workshopName: item.workshopName || pond.workshopName || '',
      pondSystemName: item.pondSystemName || pond.pondSystemName || pond.pondNum || '',
      pondName: item.pondName || pond.pondName || '',
      pondDetailNum: item.pondDetailNum || pond.pondDetailNum || ''
    })
    const label = buildSiteDisplayLabel(merged) || merged.pondName || String(item.pondId || '')
    return {
      pondId: item.pondId,
      pondName: label,
      workshopName: merged.workshopName || '',
      pondSystemName: merged.pondSystemName || '',
      pondDetailNum: merged.pondDetailNum || '',
      label,
      status: '待评估',
      cls: 'bg-slate-dim border-slate',
      statusCls: 'text-slate',
      overallRating: null,
      pendingHealth: true
    }
  }
  const score = scoreHealthRow(item, weights)
  const pond = (pondMap && item.pondId != null) ? (pondMap.get(String(item.pondId)) || {}) : {}
  const merged = Object.assign({}, pond, item, {
    workshopName: item.workshopName || pond.workshopName || '',
    pondSystemName: item.pondSystemName || pond.pondSystemName || pond.pondNum || '',
    pondName: item.pondName || pond.pondName || '',
    pondDetailNum: item.pondDetailNum || pond.pondDetailNum || ''
  })
  const label = buildSiteDisplayLabel(merged)
  const meta = healthStatusMeta(score, healthGrade)
  return {
    pondId: item.pondId,
    pondName: label,
    workshopName: merged.workshopName || '',
    pondSystemName: merged.pondSystemName || '',
    pondDetailNum: merged.pondDetailNum || '',
    label,
    status: meta.status,
    cls: meta.cls,
    statusCls: meta.statusCls,
    overallRating: score
  }
}

export function mapHealthRowToOverview(item, healthGrade, weights, pondMap) {
  const preview = mapHealthRowToPreview(item, healthGrade, weights, pondMap)
  return {
    pondId: preview.pondId,
    pondName: preview.pondName,
    healthStatus: preview.status,
    overallRating: preview.overallRating,
    workshopName: preview.workshopName,
    pondDetailNum: preview.pondDetailNum,
    pendingHealth: !!preview.pendingHealth
  }
}

/** 按 pondId 去重，保留首次出现 */
export function dedupePondsById(list) {
  const map = new Map()
  ;(list || []).forEach((p) => {
    if (!p) return
    const id = p.id != null ? p.id : p.pondId
    if (id == null) return
    const key = String(id)
    if (!map.has(key)) map.set(key, p)
  })
  return Array.from(map.values())
}

/** 车间主键：优先 workshopId，与精准投喂 / 饵料 / 健康下拉一致 */
export function resolvePondWorkshopKey(p) {
  if (!p) return ''
  if (p.workshopId != null && p.workshopId !== '') return String(p.workshopId)
  const raw = p.workshopName || p.workshop || ''
  return formatWorkshop(raw) || String(raw || '').trim()
}

/**
 * 从主档鱼池提取统一车间列表（同 id 多名取票数最高）
 * @returns {{id:string,name:string}[]}
 */
export function collectWorkshopsFromPonds(ponds) {
  const map = new Map()
  dedupePondsById(ponds || []).forEach((p) => {
    const key = resolvePondWorkshopKey(p)
    if (!key) return
    const label = formatWorkshop(p.workshopName || p.workshop) || p.workshopName || p.workshop || key
    if (!map.has(key)) {
      map.set(key, { id: key, name: label, votes: new Map() })
    }
    const w = map.get(key)
    if (label) w.votes.set(label, (w.votes.get(label) || 0) + 1)
  })
  return Array.from(map.values())
    .map((w) => {
      let best = w.name
      let bestN = -1
      w.votes.forEach((n, name) => {
        if (n > bestN) {
          bestN = n
          best = name
        }
      })
      return { id: w.id, name: best }
    })
    .sort((a, b) => String(a.name).localeCompare(String(b.name), 'zh-CN'))
}

/** 某池在全量主档下的统一车间展示名 */
export function workshopLabelForPond(pond, allPonds) {
  const key = resolvePondWorkshopKey(pond)
  if (!key) return ''
  const list = collectWorkshopsFromPonds(allPonds && allPonds.length ? allPonds : [pond])
  const hit = list.find((w) => String(w.id) === String(key))
  if (hit) return hit.name
  return formatWorkshop(pond.workshopName || pond.workshop) || pond.workshopName || pond.workshop || ''
}

/**
 * 以业务鱼池主档为准，合并最新健康评分。
 * 无评分的池保留并标记 _pendingHealth，保证与投喂等模块池集合一致。
 */
export function mergeMasterPondsWithHealth(masterPonds, healthRows) {
  const healthById = new Map()
  ;(healthRows || []).forEach((row) => {
    if (row && row.pondId != null) healthById.set(String(row.pondId), row)
  })
  const masters = dedupePondsById(masterPonds)
  const merged = masters.map((pond) => {
    const id = pond.id != null ? pond.id : pond.pondId
    const hit = healthById.get(String(id))
    if (hit) {
      healthById.delete(String(id))
      return Object.assign({}, hit, {
        pondId: id,
        pondName: hit.pondName || pond.pondName || pond.name || '',
        pondDetailNum: hit.pondDetailNum || pond.pondDetailNum || '',
        workshopId: hit.workshopId != null ? hit.workshopId : pond.workshopId,
        workshopName: hit.workshopName || pond.workshopName || '',
        pondSystemName: hit.pondSystemName || pond.pondSystemName || pond.pondNum || '',
        fishName: hit.fishName || pond.fishTypeName || pond.fishTypes || '',
        batchNumber: hit.batchNumber || pond.batchNumber || '',
        fishSpec: hit.fishSpec || pond.fishSpec || pond.avgSpecs || ''
      })
    }
    return {
      pondId: id,
      pondName: pond.pondName || pond.name || '',
      pondDetailNum: pond.pondDetailNum || '',
      workshopId: pond.workshopId,
      workshopName: pond.workshopName || '',
      pondSystemName: pond.pondSystemName || pond.pondNum || '',
      fishName: pond.fishTypeName || pond.fishTypes || '',
      batchNumber: pond.batchNumber || '',
      fishSpec: pond.fishSpec || pond.avgSpecs || '',
      overallRating: null,
      environmentalLayerRating: null,
      behavioralLayerRating: null,
      bodyLayerRating: null,
      numberOfExceptions: 0,
      _pendingHealth: true
    }
  })
  healthById.forEach((row) => {
    merged.push(Object.assign({}, row, { pondId: row.pondId }))
  })
  return merged
}

export async function loadHealthThresholdBundle() {
  let healthGrade = { excellentMin: 85, watchMin: 70 }
  let weights = { env: 20, appearance: 60, internal: 20 }
  let water = null
  try {
    // 不用 force，避免远端 modules-api 不可达时反复挂死；并加超时
    const [health, waterMod] = await Promise.all([
      withTimeout(getThresholdModule('health', false), 6000, null),
      withTimeout(getThresholdModule('water', false), 6000, null)
    ])
    if (health) {
      healthGrade = resolveHealthGrades(health)
      weights = resolveHealthWeights(health)
    }
    if (waterMod && Array.isArray(waterMod.speciesTemps)) {
      water = waterMod
    }
  } catch (e) { /* 默认分界 */ }
  return { healthGrade, weights, water }
}

function notifyPondMapReady(map) {
  const waiters = _pondMapReadyWaiters.splice(0, _pondMapReadyWaiters.length)
  waiters.forEach((fn) => {
    try { fn(map) } catch (e) { /* ignore */ }
  })
}

/** 后台刷新鱼池档案；多次调用共用同一 in-flight */
export function warmPondArchiveMap() {
  if (_pondMapCache.map && Date.now() - _pondMapCache.at < POND_MAP_TTL_MS) {
    return Promise.resolve(_pondMapCache.map)
  }
  if (_pondMapInflight) return _pondMapInflight
  _pondMapInflight = (async () => {
    try {
      const res = await withTimeout(
        getZkPondPage({ pageNo: 1, pageSize: 500 }),
        10000,
        null
      )
      const list = (res && res.data && res.data.list) || []
      const map = indexPondsById(list)
      if (map.size) {
        _pondMapCache = { map, at: Date.now() }
        // 作废列表结果，保留 _rows 供档案补全时重算
        if (_healthListCache) {
          _healthListCache = Object.assign({}, _healthListCache, { data: null, at: 0 })
        }
      }
      notifyPondMapReady(map)
      return map
    } catch (e) {
      const fallback = _pondMapCache.map || new Map()
      notifyPondMapReady(fallback)
      return fallback
    } finally {
      _pondMapInflight = null
    }
  })()
  return _pondMapInflight
}

/** 同步取已缓存的鱼池列表（无缓存返回 []） */
export function getCachedPondList() {
  if (_pondMapCache.map && _pondMapCache.map.size) {
    return Array.from(_pondMapCache.map.values())
  }
  return []
}

/** 确保档案就绪并返回列表（可后台调用，勿阻塞健康首屏） */
export async function ensurePondArchiveList() {
  const map = await warmPondArchiveMap()
  return Array.from((map || new Map()).values())
}

/**
 * 首屏：有缓存立即返回；否则最多等 POND_MAP_FAST_WAIT_MS，超时返回空 Map 并后台继续拉。
 */
async function loadPondArchiveMapFast() {
  if (_pondMapCache.map && Date.now() - _pondMapCache.at < POND_MAP_TTL_MS) {
    return _pondMapCache.map
  }
  const warming = warmPondArchiveMap()
  return Promise.race([
    warming,
    new Promise((resolve) => {
      setTimeout(() => resolve(_pondMapCache.map || new Map()), POND_MAP_FAST_WAIT_MS)
    })
  ])
}

function buildHealthBundle(rows, healthGrade, weights, pondMap, waterConfig) {
  const adjusted = (rows || []).map((row) => applySpeciesWaterTempToHealthItem(row, waterConfig))
  return {
    healthGrade,
    weights,
    overviewList: adjusted.map((row) => mapHealthRowToOverview(row, healthGrade, weights, pondMap)),
    previewList: adjusted.map((row) => mapHealthRowToPreview(row, healthGrade, weights, pondMap))
  }
}

/**
 * 档案后台就绪后，用完整池名重建并回调（可选）。
 * 用于车间数控等首屏先出分、后补名称。
 */
export function whenPondArchiveReady(cb) {
  if (typeof cb !== 'function') return
  if (_pondMapCache.map && Date.now() - _pondMapCache.at < POND_MAP_TTL_MS && _pondMapCache.map.size) {
    cb(_pondMapCache.map)
    return
  }
  _pondMapReadyWaiters.push(cb)
  warmPondArchiveMap()
}

export async function fetchUnifiedHealthList(options) {
  const force = options && options.force
  if (!force && _healthListCache.data && Date.now() - _healthListCache.at < HEALTH_LIST_TTL_MS) {
    return _healthListCache.data
  }

  // 健康分 + 阈值先出；投喂主档/档案用短超时，避免拖死首屏
  const feedingPromise = withTimeout(getPrecisionFeedingPonds(), 4500, { data: [] }).catch(() => ({ data: [] }))
  const pondMapPromise = loadPondArchiveMapFast()

  const [{ healthGrade, weights, water }, res] = await Promise.all([
    loadHealthThresholdBundle(),
    withTimeout(getLatestPondHealthList(), 8000, { data: [] })
  ])
  const healthRows = (res && res.data) || []

  const [pondMap, feedingRes] = await Promise.all([
    pondMapPromise,
    Promise.race([
      feedingPromise,
      new Promise((resolve) => setTimeout(() => resolve({ data: [] }), 1200))
    ])
  ])

  const feedingPonds = dedupePondsById((feedingRes && feedingRes.data) || [])
  let master = feedingPonds
  if (!master.length && pondMap && pondMap.size) {
    master = Array.from(pondMap.values())
  }
  const rows = master.length
    ? mergeMasterPondsWithHealth(master, healthRows)
    : healthRows
  const bundle = buildHealthBundle(rows, healthGrade, weights, pondMap || new Map(), water)
  _healthListCache = { data: bundle, at: Date.now(), _rows: rows, healthGrade, weights, water }

  // 投喂主档若被短超时丢掉，后台补一次完整合并（不挡 UI）
  feedingPromise.then((fullFeed) => {
    const full = dedupePondsById((fullFeed && fullFeed.data) || [])
    if (!full.length || full.length <= feedingPonds.length) return
    const merged = mergeMasterPondsWithHealth(full, healthRows)
    const next = buildHealthBundle(merged, healthGrade, weights, _pondMapCache.map || pondMap || new Map(), water)
    _healthListCache = { data: next, at: Date.now(), _rows: merged, healthGrade, weights, water }
  }).catch(() => {})

  return bundle
}

/** 档案就绪后强制用完整池名重算（供 UI 二次刷新） */
export async function refetchUnifiedHealthListWithArchive() {
  const map = await warmPondArchiveMap()
  if (_healthListCache._rows && _healthListCache.healthGrade) {
    const bundle = buildHealthBundle(
      _healthListCache._rows,
      _healthListCache.healthGrade,
      _healthListCache.weights,
      map || new Map(),
      _healthListCache.water
    )
    _healthListCache = {
      data: bundle,
      at: Date.now(),
      _rows: _healthListCache._rows,
      healthGrade: _healthListCache.healthGrade,
      weights: _healthListCache.weights
    }
    return bundle
  }
  return fetchUnifiedHealthList({ force: true })
}
