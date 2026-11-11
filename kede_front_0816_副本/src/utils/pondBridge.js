/**
 * 鱼池唯一权威来源：存塘主表 zk_pond（GET /fish/zk-pond/page）
 * 鱼病 / 风味等模块统一经本桥接读取，不再合并 modules-api 本地池表。
 */
import { getZkPondPage } from '@/api/fish/zkPond'
import { enrichPondRow } from '@/utils/siteNaming'

const PAGE_SIZE = 200
const MAX_PAGES = 20
/** 短缓存：入池弹窗反复打开时避免每次全量翻页 */
const POND_CACHE_TTL_MS = 90 * 1000
let _pondCache = { at: 0, list: null }

function withSourceErrors(list, errors) {
  Object.defineProperty(list, '_sourceErrors', {
    value: errors.slice(),
    enumerable: false,
    configurable: true
  })
  if (errors.length) {
    console.warn('[pondBridge] zk_pond 加载失败:', errors.map(e => e.source + ': ' + e.message).join('; '))
  }
  return list
}

/** @param {any[]} list */
export function getPondSourceErrors(list) {
  return (list && list._sourceErrors) || []
}

/**
 * 将 zk_pond 行映射为鱼病/风味下游已用的池对象形态
 * @param {object} item
 */
function mapZkPond(item) {
  if (!item) return null
  const code = item.pondDetailNum || item.pondName || item.pondNum || ''
  if (!code) return null
  const statusRaw = item.status
  const inUse = statusRaw === 1 || statusRaw === '1' || statusRaw === 'in_use' || statusRaw == null
  return enrichPondRow({
    id: item.id,
    code,
    pondCode: code,
    poolCode: code,
    pondDetailNum: item.pondDetailNum || code,
    name: item.pondName || code,
    workshop: item.workshopName || item.workshop || '',
    workshopId: item.workshopId,
    systemName: item.pondSystemName || item.pondNum || '',
    pondSystemId: item.pondSystemId,
    species: item.label || item.fishType || '',
    batchCode: item.batchNumber || item.relationOrderNo || item.batchNo || '',
    fishSpec: item.fishSpec || item.avgSpecs || '',
    fishNum: item.fishNum,
    totalWeight: item.totalWeight,
    source: 'zk_pond',
    status: inUse ? 'in_use' : 'idle',
    statusLabel: inUse ? '使用中' : '空闲',
    location: item.workshopName || item.workshop || '',
    size: item.pondSize || item.size || ''
  })
}

async function fetchAllZkPonds() {
  const all = []
  let pageNo = 1
  while (pageNo <= MAX_PAGES) {
    const res = await getZkPondPage({ pageNo, pageSize: PAGE_SIZE })
    const list = (res && res.data && res.data.list) || []
    const total = Number((res && res.data && res.data.total) || 0)
    all.push.apply(all, list)
    if (!list.length || all.length >= total || list.length < PAGE_SIZE) break
    pageNo += 1
  }
  return all
}

/**
 * @param {{ includeFlavor?: boolean, force?: boolean }} [opts] includeFlavor 已忽略；force 跳过缓存
 * @returns {Promise<object[]>}
 */
export async function fetchMergedPonds(opts) {
  void (opts && opts.includeFlavor)
  const force = !!(opts && opts.force)
  if (!force && _pondCache.list && Date.now() - _pondCache.at < POND_CACHE_TTL_MS) {
    return _pondCache.list
  }
  const errors = []
  let raw = []
  try {
    raw = await fetchAllZkPonds()
  } catch (e) {
    errors.push({ source: 'zk_pond', message: (e && e.message) || String(e) })
  }
  const map = new Map()
  raw.forEach(item => {
    const mapped = mapZkPond(item)
    if (!mapped) return
    const key = String(mapped.code).trim().toUpperCase()
    if (!map.has(key)) map.set(key, mapped)
  })
  const list = withSourceErrors(Array.from(map.values()), errors)
  if (!errors.length || list.length) {
    _pondCache = { at: Date.now(), list }
  }
  return list
}

/** 风味优化：与鱼病同一数据源 zk_pond */
export async function fetchFlavorMergedPonds() {
  const list = await fetchMergedPonds()
  const mapped = list.map(p => enrichPondRow({
    ...p,
    status: p.status === 'available' ? 'idle' : (p.status || 'idle'),
    statusLabel: p.statusLabel || (p.status === 'in_use' ? '使用中' : '空闲')
  }))
  return withSourceErrors(mapped, getPondSourceErrors(list))
}

/** 主数据已统一到 zk_pond，不再区分「本地池 / 外部池」 */
export function isExternalPond() {
  return false
}
