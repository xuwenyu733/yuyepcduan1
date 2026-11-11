/**
 * 全站统一车间 / 系统 / 鱼池筛选项
 * 唯一权威来源：生产记录
 *   车间 → zk_workshop（车间记录）
 *   系统 → zk_pond_system（系统记录）
 *   鱼池 → zk_pond（存塘记录）
 *
 * 可见范围：与基地管理一致 —— 当前用户归属部门 + 子部门（请求带 deptId，
 * 后端 enhancerDeptIds / getSubDeptIds 展开）。
 */
import { getZkWorkshopPage, getZkWorkshopDashboardList } from '@/api/fish/zkWorkshop'
import { getZkPondSystemPage } from '@/api/fish/zkPondSystem'
import { getZkPondPage } from '@/api/fish/zkPond'
import { UserInfoKey } from '@/utils/constants'
import {
  formatWorkshop,
  formatSystem,
  formatSiteLabel,
  parseSiteString,
  parseWorkshopNum,
  parseSystemNum,
  enrichPondRow
} from '@/utils/siteNaming'
import { buildSiteDisplayLabel, buildWorkshopOptionLabel, buildSystemOptionLabel } from '@/utils/siteDisplay'

const PAGE_SIZE = 200
/** 内存新鲜缓存：5 分钟内直接命中，避免总览反复打三表 */
const TTL_MS = 5 * 60 * 1000
/** session 内允许更久的 stale 回填，避免「一项有一项空」 */
const STALE_TTL_MS = 30 * 60 * 1000
/** localStorage 冷启动兜底（跨刷新仍可立刻填车间下拉） */
const LOCAL_TTL_MS = 24 * 60 * 60 * 1000
const STORAGE_KEY = 'kede:sharedSiteOptions:v4'
const LOCAL_STORAGE_KEY = 'kede:sharedSiteOptions:v4:local'
let _cache = { data: null, at: 0, includePonds: true, deptId: null }
/** @type {Map<string, Promise<object>>} */
const _inflightByKey = new Map()
/** 可选鱼池列表独立缓存（死亡/打样等下拉反复打开时避免全量翻页） */
let _pondListCache = { list: null, at: 0, deptId: null }
/** @type {Map<string, Promise<object[]>>} */
const _pondListInflight = new Map()

/** 仅真超管可按「无部门 = 平台级」放行全站；空部门普通用户不得放行 */
const PLATFORM_UNSCOPED_ROLE_RE = /^(admin|super_admin|super_administrator|tenant_admin|ke_admin)$/i

/**
 * 当前登录用户归属部门（用户管理「归属部门」），与登录态 USER_INFO_KEY.deptId 一致
 * @returns {number|string|null}
 */
export function resolveCurrentUserDeptId() {
  try {
    // 优先 Vuex（getInfo 后最新），再回落 localStorage
    try {
      // eslint-disable-next-line global-require
      const store = require('@/store').default
      const fromStore = store && store.getters && store.getters.deptId
      if (fromStore != null && fromStore !== '' && fromStore !== 0 && fromStore !== '0') {
        const sn = Number(fromStore)
        return Number.isFinite(sn) && sn > 0 ? sn : fromStore
      }
      // store 明确为 null/0：视为无部门（勿再被陈旧 localStorage 盖住）
      if (store && store.state && store.state.user && Object.prototype.hasOwnProperty.call(store.state.user, 'deptId')) {
        const d = store.state.user.deptId
        if (d == null || d === '' || d === 0 || d === '0') return null
      }
    } catch (e) { /* store 尚未就绪 */ }
    if (typeof localStorage === 'undefined') return null
    const raw = localStorage.getItem(UserInfoKey)
    if (!raw) return null
    const info = JSON.parse(raw)
    const deptId = info && (info.deptId != null ? info.deptId : (info.user && info.user.deptId))
    if (deptId == null || deptId === '' || deptId === 0 || deptId === '0') return null
    const n = Number(deptId)
    return Number.isFinite(n) && n > 0 ? n : deptId
  } catch (e) {
    return null
  }
}

/**
 * 是否允许无 deptId 时按平台级看全站数据。
 * 注意：deptId 为空 ≠ 平台级；仅角色命中超管白名单才放行。
 */
export function isPlatformUnscopedAdmin() {
  try {
    const roles = []
    if (typeof localStorage !== 'undefined') {
      const raw = localStorage.getItem(UserInfoKey)
      if (raw) {
        const info = JSON.parse(raw)
        if (Array.isArray(info.roles)) roles.push(...info.roles)
        if (info.roleCode) roles.push(info.roleCode)
        if (info.user && info.user.roleCode) roles.push(info.user.roleCode)
      }
    }
    try {
      // eslint-disable-next-line global-require
      const store = require('@/store').default
      const storeRoles = store && store.getters && store.getters.roles
      if (Array.isArray(storeRoles)) roles.push(...storeRoles)
    } catch (e) { /* store 尚未就绪 */ }
    return roles.some((r) => PLATFORM_UNSCOPED_ROLE_RE.test(String(r || '').trim()))
  } catch (e) {
    return false
  }
}

function uniqSorted(map) {
  return Array.from(map.values()).sort((a, b) => {
    const na = Number(String(a.value).replace(/\D/g, '')) || 0
    const nb = Number(String(b.value).replace(/\D/g, '')) || 0
    if (na !== nb) return na - nb
    return String(a.label).localeCompare(String(b.label), 'zh-CN')
  })
}

function cacheKey(includePonds, deptId) {
  const d = deptId != null ? String(deptId) : 'all'
  return (includePonds ? 'full' : 'filters') + ':d' + d
}

function cacheDeptMatches(cachedDeptId, deptId) {
  if (deptId == null && (cachedDeptId == null || cachedDeptId === 'all')) return true
  return String(cachedDeptId) === String(deptId)
}

function readSessionCache() {
  try {
    if (typeof sessionStorage === 'undefined') return null
    const raw = sessionStorage.getItem(STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    if (!parsed || !parsed.data || !parsed.at) return null
    if (Date.now() - parsed.at > STALE_TTL_MS) return null
    return parsed
  } catch (e) {
    return null
  }
}

function readLocalCache() {
  try {
    if (typeof localStorage === 'undefined') return null
    const raw = localStorage.getItem(LOCAL_STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    if (!parsed || !parsed.data || !parsed.at) return null
    if (Date.now() - parsed.at > LOCAL_TTL_MS) return null
    return parsed
  } catch (e) {
    return null
  }
}

function writeSessionCache(data, includePonds, deptId) {
  try {
    if (typeof sessionStorage === 'undefined' || !data) return
    // 只持久化下拉需要的轻量字段，避免存塘大表撑爆 session
    const slim = {
      workshops: data.workshops || [],
      systems: data.systems || [],
      ponds: includePonds ? (data.ponds || []) : [],
      workshopOptions: data.workshopOptions || [],
      systemOptions: data.systemOptions || [],
      pondOptions: includePonds ? (data.pondOptions || []) : [],
      rows: [],
      workshopsRaw: [],
      systemsRaw: [],
      pondsRaw: [],
      _deptId: deptId != null ? deptId : null,
      _deptScoped: deptId != null
    }
    const payload = {
      data: slim,
      at: Date.now(),
      includePonds: !!includePonds,
      deptId: deptId != null ? deptId : null
    }
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(payload))
    // 车间/系统选项额外落 local，刷新后首屏可立刻回填
    if ((slim.workshopOptions && slim.workshopOptions.length) || (slim.systemOptions && slim.systemOptions.length)) {
      try {
        if (typeof localStorage !== 'undefined') {
          localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(payload))
        }
      } catch (e2) { /* quota */ }
    }
  } catch (e) {
    /* quota / private mode */
  }
}

/**
 * 同步读取已有筛选项（内存 / session / local），供总览首屏立刻填下拉
 * 仅返回与当前用户部门匹配的缓存，避免串部门。
 * @returns {object|null}
 */
export function peekSharedSiteOptions() {
  const deptId = resolveCurrentUserDeptId()
  if (_cache.data && cacheDeptMatches(_cache.deptId, deptId)) return _cache.data
  const sess = readSessionCache()
  if (sess && sess.data && cacheDeptMatches(sess.deptId, deptId)) {
    _cache = { data: sess.data, at: sess.at || 0, includePonds: !!sess.includePonds, deptId: sess.deptId != null ? sess.deptId : null }
    return sess.data
  }
  const local = readLocalCache()
  if (local && local.data && cacheDeptMatches(local.deptId, deptId)) {
    _cache = { data: local.data, at: local.at || 0, includePonds: !!local.includePonds, deptId: local.deptId != null ? local.deptId : null }
    return local.data
  }
  return null
}

async function fetchAllListWithRetry(apiFn, extraParams, label) {
  let lastErr = null
  for (let attempt = 0; attempt < 3; attempt++) {
    try {
      const list = await fetchAllList(apiFn, extraParams)
      return list
    } catch (e) {
      lastErr = e
      console.warn('[siteOptions]', label, 'attempt', attempt + 1, e && e.message)
      if (attempt < 2) await new Promise((r) => setTimeout(r, 180 * (attempt + 1)))
    }
  }
  throw lastErr || new Error(label + ' failed')
}

/**
 * 管理模块可选鱼池：与账户可见站点同源（deptId + 全部分页），不再只取第一页/仅存塘>0。
 * 5 分钟内复用内存缓存；可复用 loadSharedSiteOptions 已拉到的 pondsRaw；同 key 并发合并为一次请求。
 * @param {{ deptId?: number|string|null, force?: boolean }} [opts]
 * @returns {Promise<object[]>}
 */
export async function loadDeptScopedPondList(opts) {
  const force = !!(opts && opts.force)
  const deptId = opts && Object.prototype.hasOwnProperty.call(opts, 'deptId')
    ? opts.deptId
    : resolveCurrentUserDeptId()
  if (deptId == null && !isPlatformUnscopedAdmin()) {
    return []
  }
  const cacheDeptKey = deptId != null ? deptId : null
  const inflightKey = deptId != null ? String(deptId) : 'all'

  if (
    !force &&
    Array.isArray(_pondListCache.list) &&
    _pondListCache.list.length &&
    cacheDeptMatches(_pondListCache.deptId, cacheDeptKey) &&
    Date.now() - _pondListCache.at < TTL_MS
  ) {
    return _pondListCache.list.slice()
  }

  // 全站筛选项刚拉过鱼池时直接复用，避免死亡记录等模块再翻一遍 zk_pond
  if (!force && _cache.data && _cache.includePonds && cacheDeptMatches(_cache.deptId, cacheDeptKey) && Date.now() - _cache.at < TTL_MS) {
    const raw = _cache.data.pondsRaw
    if (Array.isArray(raw) && raw.length) {
      _pondListCache = { list: raw, at: _cache.at, deptId: cacheDeptKey }
      return raw.slice()
    }
  }

  if (!force && _pondListInflight.has(inflightKey)) {
    return _pondListInflight.get(inflightKey)
  }

  const params = deptId != null ? { deptId } : {}
  const job = (async () => {
    try {
      const list = await fetchAllListWithRetry(getZkPondPage, params, 'zk_pond.selectable')
      const rows = Array.isArray(list) ? list : []
      _pondListCache = { list: rows, at: Date.now(), deptId: cacheDeptKey }
      return rows.slice()
    } catch (e) {
      console.warn('[siteOptions] loadDeptScopedPondList', e && e.message)
      if (Array.isArray(_pondListCache.list) && cacheDeptMatches(_pondListCache.deptId, cacheDeptKey)) {
        return _pondListCache.list.slice()
      }
      return []
    }
  })()

  _pondListInflight.set(inflightKey, job)
  try {
    return await job
  } finally {
    _pondListInflight.delete(inflightKey)
  }
}

/**
 * 车间列表：有部门时走分页+deptId（与基地管理同路径）；无部门才用大屏轻量接口
 */
async function fetchWorkshopListFast(extraParams) {
  const params = Object.assign({}, extraParams || {})
  if (params.deptId != null) {
    return await fetchAllListWithRetry(getZkWorkshopPage, params, 'zk_workshop.page')
  }
  let lastErr = null
  for (let attempt = 0; attempt < 3; attempt++) {
    try {
      const res = await getZkWorkshopDashboardList()
      const list = (res && res.data) || []
      if (Array.isArray(list) && list.length) {
        return list.map((w) => ({
          id: w.workshopId != null ? w.workshopId : w.id,
          workshopName: w.workshopName || w.name || '',
          name: w.workshopName || w.name || ''
        })).filter((w) => w.id != null && String(w.workshopName || '').trim())
      }
    } catch (e) {
      lastErr = e
      console.warn('[siteOptions] zk_workshop dashboard attempt', attempt + 1, e && e.message)
    }
    if (attempt < 2) await new Promise((r) => setTimeout(r, 160 * (attempt + 1)))
  }
  try {
    return await fetchAllListWithRetry(getZkWorkshopPage, params, 'zk_workshop.page')
  } catch (e) {
    throw lastErr || e
  }
}

/** 系统：先拉第一页快速出选项，必要时再补全 */
async function fetchSystemListFast(extraParams) {
  const params = Object.assign({}, extraParams || {})
  let lastErr = null
  for (let attempt = 0; attempt < 3; attempt++) {
    try {
      const res = await getZkPondSystemPage(Object.assign({ pageNo: 1, pageSize: PAGE_SIZE }, params))
      const list = (res && res.data && res.data.list) || []
      const total = Number((res && res.data && res.data.total) || 0)
      if (list.length && list.length < total && list.length >= PAGE_SIZE) {
        return { list, partial: true, total }
      }
      return { list, partial: false, total }
    } catch (e) {
      lastErr = e
      console.warn('[siteOptions] zk_pond_system fast attempt', attempt + 1, e && e.message)
      if (attempt < 2) await new Promise((r) => setTimeout(r, 160 * (attempt + 1)))
    }
  }
  throw lastErr || new Error('zk_pond_system failed')
}

function workshopsEqualLoose(a, b) {
  if (a == null || b == null || a === '' || b === '') return false
  if (String(a) === String(b)) return true
  const na = parseWorkshopNum(a)
  const nb = parseWorkshopNum(b)
  return na != null && nb != null && na === nb
}

/** 去掉「系统1(车间3)」类展示后缀，便于与行内裸系统名互认 */
function stripSystemWorkshopSuffix(s) {
  return String(s == null ? '' : s).trim().replace(/\s*[（(][^）)]*[）)]\s*$/, '').trim()
}

function systemsEqualLoose(a, b) {
  if (a == null || b == null || a === '' || b === '') return false
  if (String(a) === String(b)) return true
  const rawA = String(a).trim()
  const rawB = String(b).trim()
  const sa = stripSystemWorkshopSuffix(rawA)
  const sb = stripSystemWorkshopSuffix(rawB)
  const aHasSuffix = sa !== rawA
  const bHasSuffix = sb !== rawB
  // 两个都带车间后缀且不全等：视为不同系统，避免 系统1(车间2)/系统1(车间3) 被数字解析等同
  if (aHasSuffix && bHasSuffix) return false
  if (sa && sb && sa === sb) return true
  const na = parseSystemNum(sa || rawA)
  const nb = parseSystemNum(sb || rawB)
  return na != null && nb != null && na === nb
}

/**
 * 同名系统跨车间时，label 追加所属车间：系统1(车间3)
 * name 始终保留裸系统名，供匹配使用
 */
function disambiguateSystemOptionLabels(systemOptions) {
  const list = systemOptions || []
  const counts = new Map()
  list.forEach((o) => {
    const base = String((o && (o.name || o.label)) || '').trim()
    if (!base) return
    counts.set(base, (counts.get(base) || 0) + 1)
  })
  list.forEach((o) => {
    if (!o) return
    const base = String(o.name || o.label || '').trim()
    if (!base) return
    o.name = base
    if ((counts.get(base) || 0) > 1 && o.workshopName) {
      o.label = base + '(' + String(o.workshopName).trim() + ')'
    } else {
      o.label = base
    }
  })
  return list
}

/** 拉取分页接口全部页（上限防死循环） */
async function fetchAllList(apiFn, extraParams) {
  const all = []
  let pageNo = 1
  const maxPages = 20
  while (pageNo <= maxPages) {
    const res = await apiFn(Object.assign({ pageNo, pageSize: PAGE_SIZE }, extraParams || {}))
    const list = (res && res.data && res.data.list) || []
    const total = Number((res && res.data && res.data.total) || 0)
    all.push.apply(all, list)
    if (!list.length || all.length >= total || list.length < PAGE_SIZE) break
    pageNo += 1
  }
  return all
}

/**
 * 仍保留：少数模块用「池行」本地推导选项（兼容旧调用）
 * @param {object[]} ponds
 */
export function buildSiteOptionsFromPonds(ponds) {
  const workshopMap = new Map()
  const systemMap = new Map()
  const pondMap = new Map()
  const rows = []

  ;(ponds || []).forEach((raw) => {
    const p = enrichPondRow(raw)
    rows.push(p)
    const site = parseSiteString(
      p.code || p.pondCode || p.pondDetailNum || '',
      p.systemName,
      p.code
    )
    const wLabel = buildWorkshopOptionLabel(p) || formatWorkshop(site.workshopNum != null ? site.workshopNum : p.workshop)
    const sLabel = buildSystemOptionLabel(p) || formatSystem(site.systemNum != null ? site.systemNum : p.systemName)
    const pondLabel = buildSiteDisplayLabel(p) || p.displayLabel || formatSiteLabel(wLabel, sLabel, p.code)

    if (wLabel) {
      const wVal = p.workshopId != null ? String(p.workshopId) : (site.workshopNum != null ? String(site.workshopNum) : wLabel)
      workshopMap.set(wVal, { value: wVal, label: wLabel, name: wLabel, id: p.workshopId })
      if (sLabel) {
        const sVal = p.pondSystemId != null ? String(p.pondSystemId) : (site.systemNum != null ? String(site.systemNum) : sLabel)
        systemMap.set(wVal + '::' + sVal, {
          value: sVal,
          label: sLabel,
          name: sLabel,
          workshopValue: wVal,
          workshopId: p.workshopId,
          workshopName: wLabel,
          id: p.pondSystemId
        })
      }
    } else if (sLabel) {
      const sVal = p.pondSystemId != null ? String(p.pondSystemId) : (site.systemNum != null ? String(site.systemNum) : sLabel)
      systemMap.set('::' + sVal, { value: sVal, label: sLabel, name: sLabel, workshopValue: '', id: p.pondSystemId })
    }
    if (pondLabel) {
      const pondVal = p.pondDetailNum || p.code || p.id || pondLabel
      pondMap.set(String(pondVal), {
        value: String(pondVal),
        label: pondLabel,
        rawCode: p.pondDetailNum || p.code || p.pondCode,
        workshopValue: p.workshopId != null ? String(p.workshopId) : (site.workshopNum != null ? String(site.workshopNum) : ''),
        systemValue: p.pondSystemId != null ? String(p.pondSystemId) : (site.systemNum != null ? String(site.systemNum) : ''),
        workshopName: wLabel || '',
        systemName: sLabel || '',
        id: p.id
      })
    }
  })

  const workshopOptions = uniqSorted(workshopMap)
  const systemOptions = disambiguateSystemOptionLabels(uniqSorted(systemMap))
  const pondOptions = Array.from(pondMap.values()).sort((a, b) =>
    String(a.label).localeCompare(String(b.label), 'zh-CN')
  )

  return {
    workshops: [...new Set(workshopOptions.map((o) => o.label).filter(Boolean))],
    systems: [...new Set(systemOptions.map((o) => o.label).filter(Boolean))],
    ponds: [...new Set(pondOptions.map((o) => o.label).filter(Boolean))],
    workshopOptions,
    systemOptions,
    pondOptions,
    rows,
    workshopsRaw: [],
    systemsRaw: [],
    pondsRaw: []
  }
}

/**
 * 从生产记录三表构建筛选项（权威）
 */
export function buildSiteOptionsFromProduction(payload) {
  const workshopsRaw = (payload && payload.workshops) || []
  const systemsRaw = (payload && payload.systems) || []
  const pondsRaw = (payload && payload.ponds) || []

  const workshopById = new Map()
  const workshopOptions = []
  workshopsRaw.forEach((w) => {
    if (!w || w.id == null) return
    const label = String(w.workshopName || w.name || '').trim()
    if (!label) return
    const value = String(w.id)
    const opt = { value, label, name: label, id: w.id }
    workshopById.set(value, opt)
    workshopById.set(label, opt)
    workshopOptions.push(opt)
  })
  workshopOptions.sort((a, b) => String(a.label).localeCompare(String(b.label), 'zh-CN'))

  const systemOptions = []
  systemsRaw.forEach((s) => {
    if (!s || s.id == null) return
    const label = String(s.systemName || s.pondSystemName || s.name || '').trim()
    if (!label) return
    const value = String(s.id)
    const workshopId = s.workshopId != null ? String(s.workshopId) : ''
    const workshopName = String(s.workshopName || (workshopById.get(workshopId) && workshopById.get(workshopId).label) || '').trim()
    systemOptions.push({
      value,
      label,
      name: label,
      id: s.id,
      workshopValue: workshopId,
      workshopId: s.workshopId,
      workshopName
    })
  })
  disambiguateSystemOptionLabels(systemOptions)
  systemOptions.sort((a, b) => String(a.label).localeCompare(String(b.label), 'zh-CN'))

  const pondOptions = []
  const rows = []
  pondsRaw.forEach((p) => {
    if (!p) return
    const workshopId = p.workshopId != null ? String(p.workshopId) : ''
    const systemId = p.pondSystemId != null ? String(p.pondSystemId) : ''
    // 展示名：优先登记表名称，避免存塘冗余名与车间记录不一致
    const workshopName =
      (workshopById.get(workshopId) && workshopById.get(workshopId).label) ||
      String(p.workshopName || '').trim()
    const systemHit = systemOptions.find((o) => o.value === systemId)
    const systemName =
      (systemHit && (systemHit.name || systemHit.label)) ||
      String(p.pondSystemName || p.pondNum || '').trim()
    const pondName = String(p.pondName || p.pondDetailNum || '').trim()
    const code = String(p.pondDetailNum || '').trim()
    const label =
      buildSiteDisplayLabel({
        workshopName,
        pondSystemName: systemName,
        pondName: pondName || code,
        pondDetailNum: code
      }) || pondName || code
    if (!label) return
    const value = code || (p.id != null ? String(p.id) : label)
    const row = enrichPondRow({
      id: p.id,
      code,
      pondCode: code,
      pondDetailNum: code,
      name: pondName || code,
      pondName: pondName || code,
      workshop: workshopName,
      workshopName,
      workshopId: p.workshopId,
      systemName,
      pondSystemName: systemName,
      pondSystemId: p.pondSystemId,
      pondNum: p.pondNum,
      source: 'production'
    })
    rows.push(row)
    pondOptions.push({
      value,
      label,
      rawCode: code,
      workshopValue: workshopId,
      systemValue: systemId,
      workshopName,
      systemName,
      id: p.id
    })
  })
  pondOptions.sort((a, b) => String(a.label).localeCompare(String(b.label), 'zh-CN'))

  return {
    workshops: workshopOptions.map((o) => o.label),
    systems: [...new Set(systemOptions.map((o) => o.label).filter(Boolean))],
    ponds: [...new Set(pondOptions.map((o) => o.label).filter(Boolean))],
    workshopOptions,
    systemOptions,
    pondOptions,
    rows,
    workshopsRaw,
    systemsRaw,
    pondsRaw
  }
}

/**
 * 全站统一筛选项：只读生产记录三表（按当前用户部门+子部门收窄，与基地管理一致）
 * @param {{ force?: boolean, includePonds?: boolean, onPartial?: function, deptId?: number|string|null }} [opts]
 *   includePonds 默认 true；总览右上角车间/系统筛选用 false，跳过沉重的 zk_pond 分页
 *   onPartial 可选：车间或系统任一先返回时回调，便于总览先填下拉
 *   deptId 可选覆盖；默认取登录用户归属部门
 */
export async function loadSharedSiteOptions(opts) {
  const force = !!(opts && opts.force)
  const includePonds = !(opts && opts.includePonds === false)
  const onPartial = opts && typeof opts.onPartial === 'function' ? opts.onPartial : null
  const deptId = opts && Object.prototype.hasOwnProperty.call(opts, 'deptId')
    ? opts.deptId
    : resolveCurrentUserDeptId()
  // 无部门且非真超管：直接空站点，禁止拉全站车间/系统/鱼池
  if (deptId == null && !isPlatformUnscopedAdmin()) {
    const empty = buildSiteOptionsFromProduction({ workshops: [], systems: [], ponds: [] })
    empty._deptId = null
    empty._deptScoped = true
    empty._deniedUnscoped = true
    empty._ready = true
    _cache = { data: empty, at: Date.now(), includePonds: true, deptId: null }
    return empty
  }
  const deptParams = deptId != null ? { deptId } : {}
  const key = cacheKey(includePonds, deptId)

  if (!force && _cache.data && cacheDeptMatches(_cache.deptId, deptId) && Date.now() - _cache.at < TTL_MS) {
    const hasWs = (_cache.data.workshopOptions || []).length > 0
    const hasSys = (_cache.data.systemOptions || []).length > 0
    if (!includePonds && (hasWs || hasSys)) {
      _cache.data._ready = true
      return _cache.data
    }
    if (includePonds && _cache.includePonds) {
      _cache.data._ready = true
      return _cache.data
    }
  }

  if (!force) {
    const sess = readSessionCache() || readLocalCache()
    if (sess && sess.data && cacheDeptMatches(sess.deptId, deptId) && Date.now() - sess.at < TTL_MS) {
      const hasWs = (sess.data.workshopOptions || []).length > 0
      const hasSys = (sess.data.systemOptions || []).length > 0
      if ((!includePonds && (hasWs || hasSys)) || (includePonds && sess.includePonds)) {
        sess.data._ready = true
        _cache = { data: sess.data, at: sess.at, includePonds: !!sess.includePonds, deptId: sess.deptId != null ? sess.deptId : null }
        return sess.data
      }
    }
  }

  if (!force && _inflightByKey.has(key)) {
    const inflight = _inflightByKey.get(key)
    if (onPartial && _cache.data && cacheDeptMatches(_cache.deptId, deptId)) {
      try { onPartial(_cache.data) } catch (e) { /* ignore */ }
    }
    return inflight
  }

  const sessPrev = readSessionCache() || readLocalCache()
  const prevOk = sessPrev && cacheDeptMatches(sessPrev.deptId, deptId) ? sessPrev.data : null
  const prev = (_cache.data && cacheDeptMatches(_cache.deptId, deptId) ? _cache.data : null) || prevOk || null

  const job = (async () => {
    let workshopsAcc = []
    let systemsAcc = []
    let pondsAcc = []
    let wsOk = false
    let sysOk = false
    let pondOk = !includePonds

    const emitPartial = () => {
      if (!onPartial) return
      try {
        const partial = buildSiteOptionsFromProduction({
          workshops: workshopsAcc,
          systems: systemsAcc,
          ponds: includePonds ? pondsAcc : []
        })
        partial._deptId = deptId
        partial._deptScoped = deptId != null
        // 仅接口失败时用同部门旧缓存补洞；成功为空（空部门）不得回填，否则串数据
        if (prev && !wsOk && !(partial.workshopOptions || []).length && (prev.workshopOptions || []).length) {
          partial.workshopOptions = prev.workshopOptions.slice()
          partial.workshops = (prev.workshops || []).slice()
        }
        if (prev && !sysOk && !(partial.systemOptions || []).length && (prev.systemOptions || []).length) {
          partial.systemOptions = prev.systemOptions.slice()
          partial.systems = (prev.systems || []).slice()
        }
        onPartial(partial)
      } catch (e) {
        console.warn('[siteOptions] onPartial', e && e.message)
      }
    }

    const workshopsFromPrev =
      (prev && prev.workshopsRaw && prev.workshopsRaw.length && prev.workshopsRaw) ||
      ((prev && prev.workshopOptions) || []).map((o) => ({
        id: o.id != null ? o.id : o.value,
        workshopName: o.label || o.name
      }))
    const systemsFromPrev =
      (prev && prev.systemsRaw && prev.systemsRaw.length && prev.systemsRaw) ||
      ((prev && prev.systemOptions) || []).map((o) => ({
        id: o.id != null ? o.id : o.value,
        systemName: o.label || o.name,
        workshopId: o.workshopId != null ? o.workshopId : o.workshopValue,
        workshopName: o.workshopName
      }))
    const pondsFromPrev = (prev && prev.pondsRaw) || []

    // 筛选项模式：车间/系统带 deptId（与基地管理点选部门一致）
    // 有归属部门时：成功为空即空，禁止无 deptId / 旧缓存兜底（否则空部门仍能看到站点）
    if (!includePonds) {
      const wsTask = fetchWorkshopListFast(deptParams)
        .then((list) => {
          wsOk = true
          workshopsAcc = Array.isArray(list) ? list : []
          emitPartial()
          return list
        })
        .catch(() => {
          wsOk = false
          workshopsAcc = workshopsFromPrev
          emitPartial()
          return []
        })

      const sysTask = fetchSystemListFast(deptParams)
        .then(async (fast) => {
          sysOk = true
          systemsAcc = (fast && fast.list) || []
          emitPartial()
          if (fast && fast.partial) {
            try {
              const full = await fetchAllListWithRetry(getZkPondSystemPage, deptParams, 'zk_pond_system')
              if (full && full.length) systemsAcc = full
              emitPartial()
            } catch (e) { /* keep first page */ }
          }
          return systemsAcc
        })
        .catch(() => {
          sysOk = false
          systemsAcc = systemsFromPrev
          emitPartial()
          return []
        })

      await Promise.all([wsTask, sysTask])
    } else {
      const [wsRes, sysRes, pondRes] = await Promise.all([
        fetchWorkshopListFast(deptParams).then(
          (list) => ({ ok: true, list: Array.isArray(list) ? list : [] }),
          () => ({ ok: false, list: [] })
        ),
        fetchAllListWithRetry(getZkPondSystemPage, deptParams, 'zk_pond_system').then(
          (list) => ({ ok: true, list: Array.isArray(list) ? list : [] }),
          () => ({ ok: false, list: [] })
        ),
        fetchAllListWithRetry(getZkPondPage, deptParams, 'zk_pond').then(
          (list) => ({ ok: true, list: Array.isArray(list) ? list : [] }),
          () => ({ ok: false, list: [] })
        )
      ])
      wsOk = wsRes.ok
      sysOk = sysRes.ok
      pondOk = pondRes.ok
      workshopsAcc = wsRes.ok ? wsRes.list : workshopsFromPrev
      systemsAcc = sysRes.ok ? sysRes.list : systemsFromPrev
      pondsAcc = pondRes.ok ? pondRes.list : pondsFromPrev
    }

    const workshops = wsOk ? (workshopsAcc || []) : (workshopsFromPrev || [])
    const systems = sysOk ? (systemsAcc || []) : (systemsFromPrev || [])
    const ponds = includePonds
      ? (pondOk ? (pondsAcc || []) : (pondsFromPrev || []))
      : []

    if (!wsOk) console.warn('[siteOptions] zk_workshop 失败，已回退缓存/空列表')
    if (!sysOk) console.warn('[siteOptions] zk_pond_system 失败，已回退缓存/空列表')
    if (includePonds && !pondOk) console.warn('[siteOptions] zk_pond 失败，已回退缓存/空列表')

    // 仅当相关接口都失败时整包回退同部门旧缓存；成功为空不得回退
    if (!wsOk && !sysOk && (!includePonds || !pondOk) && prev &&
        ((prev.workshopOptions || []).length || (prev.systemOptions || []).length)) {
      prev._ready = true
      prev._loadFailed = false
      _cache = { data: prev, at: Date.now(), includePonds: includePonds || !!_cache.includePonds, deptId }
      writeSessionCache(prev, _cache.includePonds, deptId)
      return prev
    }

    // 全失败且无旧缓存：标记加载失败，勿当成「空白部门」就绪空白名单（否则误杀本部门计划）
    if (!wsOk && !sysOk && (!includePonds || !pondOk)) {
      const failed = buildSiteOptionsFromProduction({
        workshops: [],
        systems: [],
        ponds: []
      })
      failed._deptId = deptId
      failed._deptScoped = deptId != null
      failed._ready = false
      failed._loadFailed = true
      _cache = {
        data: failed,
        at: Date.now(),
        includePonds: includePonds || !!(prev && _cache.includePonds),
        deptId
      }
      return failed
    }

    const data = buildSiteOptionsFromProduction({
      workshops: workshops || [],
      systems: systems || [],
      ponds: includePonds ? (ponds || []) : []
    })
    data._deptId = deptId
    data._deptScoped = deptId != null
    data._ready = true
    data._loadFailed = false

    // 单源失败时才用同部门上一份补洞；成功为空保持空
    if (!wsOk && !(data.workshopOptions || []).length && prev && (prev.workshopOptions || []).length) {
      data.workshopOptions = prev.workshopOptions.slice()
      data.workshops = (prev.workshops || []).slice()
    }
    if (!sysOk && !(data.systemOptions || []).length && prev && (prev.systemOptions || []).length) {
      data.systemOptions = prev.systemOptions.slice()
      data.systems = (prev.systems || []).slice()
    }
    if (!includePonds && prev && (prev.pondOptions || []).length) {
      data.pondOptions = prev.pondOptions
      data.ponds = prev.ponds
      data.rows = prev.rows
    } else if (includePonds && !pondOk && !(data.pondOptions || []).length && prev && (prev.pondOptions || []).length) {
      data.pondOptions = prev.pondOptions.slice()
      data.ponds = (prev.ponds || []).slice()
    }

    _cache = {
      data,
      at: Date.now(),
      includePonds: includePonds || !!(prev && _cache.includePonds),
      deptId
    }
    writeSessionCache(data, _cache.includePonds, deptId)
    return data
  })()

  _inflightByKey.set(key, job)
  try {
    return await job
  } finally {
    _inflightByKey.delete(key)
  }
}

export function clearSharedSiteOptionsCache() {
  _cache = { data: null, at: 0, includePonds: true, deptId: null }
  _inflightByKey.clear()
  _pondListCache = { list: null, at: 0, deptId: null }
  _pondListInflight.clear()
  try {
    if (typeof sessionStorage !== 'undefined') sessionStorage.removeItem(STORAGE_KEY)
  } catch (e) { /* ignore */ }
  try {
    if (typeof localStorage !== 'undefined') localStorage.removeItem(LOCAL_STORAGE_KEY)
  } catch (e) { /* ignore */ }
}

/** 车间记录 / 系统记录变更后调用：清缓存并通知各页面重新拉取筛选项 */
export function notifySharedSiteOptionsChanged() {
  clearSharedSiteOptionsCache()
  try {
    if (typeof window !== 'undefined' && typeof window.dispatchEvent === 'function') {
      window.dispatchEvent(new CustomEvent('kede:site-options-changed'))
    }
  } catch (e) { /* ignore */ }
}

export const SITE_OPTIONS_CHANGED_EVENT = 'kede:site-options-changed'

/** 按车间筛选系统选项（返回 label 列表，兼容旧调用） */
export function filterSystemOptions(allOptions, workshopValue) {
  const opts = allOptions && allOptions.systemOptions ? allOptions : buildSiteOptionsFromPonds([])
  const items = !workshopValue
    ? (opts.systemOptions || []).slice()
    : (opts.systemOptions || []).filter((o) => {
        if (!o.workshopValue && !o.workshopName) return true
        return (
          workshopsEqualLoose(o.workshopValue, workshopValue) ||
          workshopsEqualLoose(o.workshopName, workshopValue) ||
          workshopsEqualLoose(o.workshopId, workshopValue)
        )
      })
  // 按系统 id/value 去重，允许同名不同车间并存
  const seen = new Set()
  const labels = []
  items.forEach((o) => {
    const k = String((o && (o.value != null ? o.value : o.id)) || o.label || '')
    if (!k || seen.has(k)) return
    seen.add(k)
    if (o && o.label) labels.push(o.label)
  })
  return labels
}

/** 按车间+系统筛选鱼池选项（返回 label 列表） */
export function filterPondOptions(allOptions, workshopValue, systemValue) {
  const opts = allOptions && allOptions.pondOptions ? allOptions : buildSiteOptionsFromPonds([])
  const raw = (opts.pondOptions || [])
    .filter((o) => {
      if (workshopValue) {
        const ok =
          workshopsEqualLoose(o.workshopValue, workshopValue) ||
          workshopsEqualLoose(o.workshopName, workshopValue)
        if (!ok) return false
      }
      if (systemValue) {
        const ok =
          systemsEqualLoose(o.systemValue, systemValue) ||
          systemsEqualLoose(o.systemName, systemValue)
        if (!ok) return false
      }
      return true
    })
    .map((o) => o.label)
  return [...new Set(raw.filter(Boolean))]
}

/**
 * 统一下拉文案（两页及以上必须共用，避免各写各的）
 */
export function pickWorkshopOptionLabels(siteOptions) {
  if (!siteOptions) return []
  const fromOpts = (siteOptions.workshopOptions || []).map((o) => o.label)
  const fromNames = siteOptions.workshops || []
  return [...new Set([].concat(fromOpts, fromNames).filter((v) => {
    if (!v) return false
    const s = String(v).trim()
    if (!s) return false
    if (s === '全场' || s === '所有车间') return false
    if (/^全部(车间|系统|鱼池)?$/.test(s)) return false
    return true
  }))]
}

export function pickSystemOptionLabels(siteOptions, workshopValue) {
  if (!siteOptions) return []
  return filterSystemOptions(siteOptions, workshopValue)
}

export function pickPondOptionLabels(siteOptions, workshopValue, systemValue) {
  if (!siteOptions) return []
  return filterPondOptions(siteOptions, workshopValue, systemValue)
}

/** 带 id/value 的完整选项（大屏、投喂等按 id 绑定） */
export function pickWorkshopOptionItems(siteOptions) {
  return ((siteOptions && siteOptions.workshopOptions) || []).slice()
}

export function pickSystemOptionItems(siteOptions, workshopValue) {
  const opts = siteOptions || {}
  let list = opts.systemOptions || []
  if (workshopValue) {
    list = list.filter((o) => {
      if (!o.workshopValue && !o.workshopName) return true
      return (
        workshopsEqualLoose(o.workshopValue, workshopValue) ||
        workshopsEqualLoose(o.workshopName, workshopValue) ||
        workshopsEqualLoose(o.workshopId, workshopValue)
      )
    })
  }
  const seen = new Set()
  return list.filter((o) => {
    const k = String((o && (o.value != null ? o.value : o.id)) || o.label || '')
    if (!k || seen.has(k)) return false
    seen.add(k)
    return true
  })
}

export function pickPondOptionItems(siteOptions, workshopValue, systemValue) {
  const opts = siteOptions || {}
  let list = opts.pondOptions || []
  if (workshopValue) {
    list = list.filter(
      (o) =>
        workshopsEqualLoose(o.workshopValue, workshopValue) ||
        workshopsEqualLoose(o.workshopName, workshopValue)
    )
  }
  if (systemValue) {
    list = list.filter(
      (o) =>
        systemsEqualLoose(o.systemValue, systemValue) ||
        systemsEqualLoose(o.systemName, systemValue) ||
        systemsEqualLoose(o.label, systemValue)
    )
  }
  const seen = new Set()
  return list.filter((o) => {
    const k = String((o && (o.value != null ? o.value : o.id)) || o.label || '')
    if (!k || seen.has(k)) return false
    seen.add(k)
    return true
  })
}

/**
 * 业务行是否命中当前车间/系统/池筛选（与下拉同源）
 * @param {object} row
 * @param {{ workshop?: string, system?: string, pond?: string }} filters
 * @param {object|null} siteOptions
 */
export function rowMatchesSiteFilters(row, filters, siteOptions) {
  if (!row) return false
  const f = filters || {}
  const workshop = f.workshop
  const system = f.system
  const pond = f.pond

  if (workshop) {
    const wOpt = ((siteOptions && siteOptions.workshopOptions) || []).find(
      (o) => workshopsEqualLoose(o.label, workshop) || workshopsEqualLoose(o.value, workshop)
    )
    const ok =
      workshopsEqualLoose(row.workshopName, workshop) ||
      workshopsEqualLoose(row.workshop, workshop) ||
      workshopsEqualLoose(row.workshop_name, workshop) ||
      (wOpt &&
        (workshopsEqualLoose(row.workshopId, wOpt.id) ||
          workshopsEqualLoose(row.workshopId, wOpt.value) ||
          workshopsEqualLoose(row.workshopName, wOpt.label)))
    if (!ok) return false
  }

  if (system) {
    const sOpt = ((siteOptions && siteOptions.systemOptions) || []).find(
      (o) =>
        systemsEqualLoose(o.label, system) ||
        systemsEqualLoose(o.name, system) ||
        systemsEqualLoose(o.value, system)
    )
    const ok =
      systemsEqualLoose(row.pondSystemName, system) ||
      systemsEqualLoose(row.systemName, system) ||
      systemsEqualLoose(row.pond_system_name, system) ||
      systemsEqualLoose(row.pondNum, system) ||
      systemsEqualLoose(row.system, system) ||
      (sOpt &&
        (systemsEqualLoose(row.pondSystemId, sOpt.id) ||
          systemsEqualLoose(row.pondSystemId, sOpt.value) ||
          systemsEqualLoose(row.pondSystemName, sOpt.name || sOpt.label) ||
          systemsEqualLoose(row.systemName, sOpt.name || sOpt.label)))
    if (!ok) return false
  }

  if (pond) {
    const label = buildSiteDisplayLabel(row)
    const code = String(row.pondDetailNum || row.pond_detail_num || row.code || row.pondCode || '').trim()
    const name = String(row.pondName || row.name || '').trim()
    const pOpt = ((siteOptions && siteOptions.pondOptions) || []).find(
      (o) =>
        String(o.label) === String(pond) ||
        String(o.rawCode || '') === String(pond) ||
        String(o.value) === String(pond)
    )
    const ok =
      String(label) === String(pond) ||
      code === String(pond) ||
      name === String(pond) ||
      (pOpt &&
        (String(row.id) === String(pOpt.id) ||
          code === String(pOpt.rawCode || '') ||
          code === String(pOpt.value) ||
          String(label) === String(pOpt.label)))
    if (!ok) return false
  }

  return true
}

/**
 * 是否「无归属部门且非平台超管」——应看到空数据，禁止串全站。
 */
export function isUserDeptScopeDenied() {
  return resolveCurrentUserDeptId() == null && !isPlatformUnscopedAdmin()
}

/**
 * 行数据是否在当前用户可见的车间/系统/鱼池范围内（与筛选项同源）
 * 有归属部门时：拒绝「全场/空标注」与白名单外站点，避免串部门。
 * 平台超管（无 deptId）：无标注行仍可见。
 * 普通用户无 deptId：一律不可见。
 */
export function isRowInUserSiteScope(row, siteOptions) {
  if (!row) return false
  if (isUserDeptScopeDenied()) return false
  const deptScoped = resolveCurrentUserDeptId() != null
  if (!siteOptions) {
    // 部门用户尚未拿到选项对象：不放行，避免闪现全站数据
    return !deptScoped
  }
  const wsOpts = siteOptions.workshopOptions || []
  const sysOpts = siteOptions.systemOptions || []
  const pondOpts = siteOptions.pondOptions || []
  const hasScope = wsOpts.length > 0 || sysOpts.length > 0 || pondOpts.length > 0
  // 部门用户白名单未就绪或加载失败：不按空白名单误杀；超管保持不过滤
  if (!hasScope) {
    if (deptScoped && siteOptions && (siteOptions._loadFailed || !siteOptions._ready)) return true
    return !deptScoped
  }

  const ws = row.workshopName || row.workshop || row.workshop_name || row.workshopLabel || ''
  const wsId = row.workshopId != null ? row.workshopId : row.workshop_id
  const sysRaw =
    row.pondSystemName ||
    row.systemName ||
    row.system_name ||
    row.pond_system_name ||
    row.system ||
    ''
  // 鱼池编码（c1-s1-1 / c2-s1-p1）常被误写入系统字段，不当作系统名做白名单
  const looksLikePondCode = (v) => {
    const s = String(v == null ? '' : v).trim()
    if (!s) return false
    return /^c\d+-s\d+/i.test(s) || /\d+号?池$/i.test(s) || /存塘\d+$/i.test(s)
  }
  const sys = looksLikePondCode(sysRaw) ? '' : sysRaw
  const sysId = row.pondSystemId != null ? row.pondSystemId : (row.systemId != null ? row.systemId : row.pond_system_id)
  const pondCode = String(
    row.pondDetailNum ||
      row.pond_detail_num ||
      row.pool_code ||
      row.poolCode ||
      row.code ||
      row.pondCode ||
      (looksLikePondCode(sysRaw) ? sysRaw : '') ||
      ''
  ).trim()
  // 勿用通用 row.name（设备名/报警标题会误判成鱼池，导致整页被滤空）
  const pondName = String(row.pondName || row.lake_name || row.pool_name || '').trim()

  const wsText = String(ws == null ? '' : ws).trim()
  const isPlantWide =
    wsText === '全部车间' || wsText === '全场' || wsText === '所有车间' || wsText === '全部'
  const isAll =
    !wsText &&
    wsId == null &&
    !sys &&
    sysId == null &&
    !pondCode &&
    !pondName
  // 部门用户：全场/无站点标注不视为本部门数据
  if (deptScoped && (isAll || isPlantWide)) return false
  if (isAll) return true
  if (isPlantWide) return true

  if (wsText || wsId != null) {
    const ok = wsOpts.some(
      (o) =>
        workshopsEqualLoose(o.label, ws) ||
        workshopsEqualLoose(o.value, ws) ||
        workshopsEqualLoose(o.value, wsId) ||
        workshopsEqualLoose(o.id, wsId) ||
        workshopsEqualLoose(o.label, wsId)
    )
    if (!ok) return false
  }

  if (sys || sysId != null) {
    // 仅有系统而无车间时，用系统白名单；有车间时再收窄
    const list = sysOpts
    const ok = list.some(
      (o) =>
        systemsEqualLoose(o.label, sys) ||
        systemsEqualLoose(o.name, sys) ||
        systemsEqualLoose(o.value, sys) ||
        systemsEqualLoose(o.value, sysId) ||
        systemsEqualLoose(o.id, sysId) ||
        systemsEqualLoose(o.label, sysId) ||
        systemsEqualLoose(o.name, sysId)
    )
    if (list.length && !ok) return false
  }

  if (pondCode || pondName) {
    // 有鱼池标注但白名单为空：部门用户不放行（避免仅 location 文本的扩展告警串部门）
    if (!pondOpts.length) return !deptScoped
    const rowPondId = row.pondId != null ? row.pondId : row.id
    const ok = pondOpts.some(
      (o) =>
        String(o.label) === pondName ||
        String(o.label) === pondCode ||
        String(o.rawCode || '') === pondCode ||
        String(o.value) === pondCode ||
        String(o.value) === String(rowPondId) ||
        String(o.id) === String(rowPondId) ||
        String(o.id) === String(row.id)
    )
    // 车间已过白名单时，池号编码格式不一致不硬拒（避免今日死亡/投喂被滤空）
    if (!ok && !(wsText || wsId != null)) return false
  }

  return true
}

/** 批量按用户可见范围过滤 */
export function filterRowsByUserSiteScope(rows, siteOptions) {
  if (!Array.isArray(rows)) return []
  if (!siteOptions) return rows.slice()
  return rows.filter((row) => isRowInUserSiteScope(row, siteOptions))
}

/**
 * 报警专用范围判断：
 * - 有归属部门但本部门及子部门站点白名单为空：一律不可见（含无车间标注）
 * - 有车间/系统时按白名单校验；鱼池编码常与下拉 label 不一致，不做硬匹配
 * - 「全场/全部车间」对部门用户仍拒绝
 * - 无站点标注：仅在白名单非空时信任后端部门过滤；并校验行上 deptId（若有）
 */
export function isAlarmInUserSiteScope(row, siteOptions) {
  if (!row) return false
  if (isUserDeptScopeDenied()) return false
  const deptScoped = resolveCurrentUserDeptId() != null
  const userDept = resolveCurrentUserDeptId()
  const rowDeptRaw = row.deptId != null ? row.deptId : (row.dept_id != null ? row.dept_id : (row.department_id != null ? row.department_id : null))
  const rowDept =
    rowDeptRaw == null || rowDeptRaw === '' || rowDeptRaw === 0 || rowDeptRaw === '0'
      ? null
      : rowDeptRaw

  const wsOpts = (siteOptions && siteOptions.workshopOptions) || []
  const sysOpts = (siteOptions && siteOptions.systemOptions) || []
  const hasScope = wsOpts.length > 0 || sysOpts.length > 0
  // 空部门 / 子部门无站点：禁止任何报警（含「少量」无车间标注串入）
  if (deptScoped && !hasScope) return false

  const ws = String(
    row.workshopName || row.workshop || row.workshop_name || row.workshopLabel || ''
  ).trim()
  const wsId = row.workshopId != null ? row.workshopId : row.workshop_id
  const sysRaw = String(
    row.pondSystemName ||
      row.systemName ||
      row.system_name ||
      row.pond_system_name ||
      row.system ||
      ''
  ).trim()
  const looksLikePondCode = (v) => {
    const s = String(v == null ? '' : v).trim()
    if (!s) return false
    return /^c\d+-s\d+/i.test(s) || /\d+号?池$/i.test(s) || /存塘\d+$/i.test(s)
  }
  const sys = looksLikePondCode(sysRaw) ? '' : sysRaw
  const isPlantWide =
    ws === '全部车间' || ws === '全场' || ws === '所有车间' || ws === '全部'
  if (deptScoped && isPlantWide) return false

  // 行上明确标了他部门：拒绝（子部门站点仍可通过下方白名单放行无 dept 或不匹配但车间在白名单的数据）
  if (deptScoped && userDept != null && rowDept != null && String(rowDept) !== String(userDept)) {
    // 若车间落在本部门白名单（含子部门站点），仍可见
    if (ws || wsId != null) {
      const ok = wsOpts.some(
        (o) =>
          workshopsEqualLoose(o.label, ws) ||
          workshopsEqualLoose(o.value, ws) ||
          workshopsEqualLoose(o.value, wsId) ||
          workshopsEqualLoose(o.id, wsId) ||
          workshopsEqualLoose(o.label, wsId)
      )
      if (!ok) return false
    } else if (!sys) {
      return false
    }
  }

  // 无站点标注：白名单非空时信任后端；超管可见
  if (!ws && wsId == null && !sys) return true

  if (ws || wsId != null) {
    const ok = wsOpts.some(
      (o) =>
        workshopsEqualLoose(o.label, ws) ||
        workshopsEqualLoose(o.value, ws) ||
        workshopsEqualLoose(o.value, wsId) ||
        workshopsEqualLoose(o.id, wsId) ||
        workshopsEqualLoose(o.label, wsId)
    )
    if (!ok) return false
  }

  if (sys) {
    if (!sysOpts.length) return true
    const ok = sysOpts.some(
      (o) =>
        systemsEqualLoose(o.label, sys) ||
        systemsEqualLoose(o.name, sys) ||
        systemsEqualLoose(o.value, sys) ||
        systemsEqualLoose(o.label, sys)
    )
    if (!ok) return false
  }

  return true
}

/**
 * 任务专用范围判断（wiidu 任务无后端 deptId）：
 * - 部门用户：拒绝「全场/空站点」（避免串部门）
 * - 有车间/系统时按白名单校验；鱼池不做硬匹配（池号常与下拉不一致）
 */
export function isTaskInUserSiteScope(row, siteOptions) {
  if (!row) return false
  if (isUserDeptScopeDenied()) return false
  const deptScoped = resolveCurrentUserDeptId() != null
  const ws = String(
    row.workshopName || row.workshop || row.workshop_name || row.workshopLabel || ''
  ).trim()
  const wsId = row.workshopId != null ? row.workshopId : row.workshop_id
  const sysRaw = String(
    row.pondSystemName ||
      row.systemName ||
      row.system_name ||
      row.pond_system_name ||
      row.system ||
      ''
  ).trim()
  const looksLikePondCode = (v) => {
    const s = String(v == null ? '' : v).trim()
    if (!s) return false
    return /^c\d+-s\d+/i.test(s) || /\d+号?池$/i.test(s) || /存塘\d+$/i.test(s)
  }
  const sys = looksLikePondCode(sysRaw) ? '' : sysRaw
  const isPlantWide =
    ws === '全部车间' || ws === '全场' || ws === '所有车间' || ws === '全部' ||
    ws === '全部系统' || sys === '全部系统' || sys === '全场'
  if (deptScoped && isPlantWide) return false
  // 无站点：超管可见；部门用户不可见（任务表无部门字段兜底）
  if (!ws && wsId == null && !sys) return !deptScoped

  const wsOpts = (siteOptions && siteOptions.workshopOptions) || []
  const sysOpts = (siteOptions && siteOptions.systemOptions) || []
  const hasScope = wsOpts.length > 0 || sysOpts.length > 0
  // 有归属部门但本部门及子部门站点为空：不可见任何任务（含周/月/年）
  // 白名单加载失败 / 未就绪：勿误杀，交给部门字段过滤
  if (!hasScope) {
    if (deptScoped && siteOptions && (siteOptions._loadFailed || siteOptions._ready === false)) return true
    return !deptScoped
  }

  if (ws || wsId != null) {
    const ok = wsOpts.some(
      (o) =>
        workshopsEqualLoose(o.label, ws) ||
        workshopsEqualLoose(o.value, ws) ||
        workshopsEqualLoose(o.value, wsId) ||
        workshopsEqualLoose(o.id, wsId) ||
        workshopsEqualLoose(o.label, wsId)
    )
    if (!ok) return false
  }

  if (sys) {
    if (!sysOpts.length) return true
    const ok = sysOpts.some(
      (o) =>
        systemsEqualLoose(o.label, sys) ||
        systemsEqualLoose(o.name, sys) ||
        systemsEqualLoose(o.value, sys)
    )
    if (!ok) return false
  }

  return true
}

export { workshopsEqualLoose, systemsEqualLoose }
