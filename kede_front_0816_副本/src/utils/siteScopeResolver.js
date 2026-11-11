/**
 * 全站车间/系统作用域解析
 * 统一处理 route.query.workshop 为 zk_workshop.id 或 车间编号/名称 的情况
 */
import { getZkWorkshopDashboardList } from '@/api/fish/zkWorkshop'
import { formatWorkshop, formatSystem, parseWorkshopNum, parseSystemNum } from '@/utils/siteNaming'

let workshopRegistryCache = null
let workshopRegistryPromise = null

export async function loadWorkshopRegistry(force = false) {
  if (!force && workshopRegistryCache) return workshopRegistryCache
  if (!force && workshopRegistryPromise) return workshopRegistryPromise
  workshopRegistryPromise = getZkWorkshopDashboardList()
    .then((res) => {
      workshopRegistryCache = (res && res.data) || []
      return workshopRegistryCache
    })
    .catch(() => {
      workshopRegistryCache = []
      return workshopRegistryCache
    })
    .finally(() => {
      workshopRegistryPromise = null
    })
  return workshopRegistryPromise
}

export function clearWorkshopRegistryCache() {
  workshopRegistryCache = null
  workshopRegistryPromise = null
}

function uniqNames(list) {
  const seen = new Set()
  const out = []
  ;(list || []).forEach((n) => {
    const s = String(n || '').trim()
    if (!s || seen.has(s)) return
    seen.add(s)
    out.push(s)
  })
  return out
}

/** @returns {{ raw: string, id: number|null, num: number|null, label: string, names: string[] }} */
export function resolveWorkshopScope(raw, siteOptions, workshopRegistry) {
  const value = String(raw == null ? '' : raw).trim()
  const empty = { raw: value, id: null, num: null, label: '', names: [] }
  if (!value) return empty

  const registry = workshopRegistry || []
  const byId = registry.find((w) => String(w.workshopId) === value)
  if (byId) {
    const name = String(byId.workshopName || '').trim()
    const num = parseWorkshopNum(name)
    return {
      raw: value,
      id: byId.workshopId,
      num,
      label: name || formatWorkshop(value),
      names: uniqNames([name, formatWorkshop(name), num != null ? '车间' + num : '', num != null ? num + '号车间' : ''])
    }
  }

  const opts = siteOptions && siteOptions.workshopOptions
  if (opts && opts.length) {
    const wn = parseWorkshopNum(value)
    const found = opts.find((o) => o.value === value
      || (wn != null && parseWorkshopNum(o.value) === wn)
      || o.label === value)
    if (found) {
      const num = parseWorkshopNum(found.value) != null
        ? parseWorkshopNum(found.value)
        : parseWorkshopNum(found.label)
      return {
        raw: value,
        id: null,
        num,
        label: found.label,
        names: uniqNames([found.label, found.value, formatWorkshop(found.value), num != null ? '车间' + num : ''])
      }
    }
  }

  const num = parseWorkshopNum(value)
  const label = num != null ? formatWorkshop(num) : formatWorkshop(value)
  return {
    raw: value,
    id: null,
    num,
    label,
    names: uniqNames([label, value, num != null ? '车间' + num : '', num != null ? num + '号车间' : ''])
  }
}

/** @returns {{ raw: string, num: number|null, label: string, names: string[] }} */
export function resolveSystemScope(raw) {
  const value = String(raw == null ? '' : raw).trim()
  if (!value) return { raw: '', num: null, label: '', names: [] }
  const num = parseSystemNum(value)
  const label = formatSystem(value)
  return {
    raw: value,
    num,
    label,
    names: uniqNames([label, value, num != null ? '系统' + num : '', num != null ? num + '号系统' : ''])
  }
}

export function textMatchesWorkshopScope(text, workshopScope) {
  if (!workshopScope || !workshopScope.raw) return true
  const raw = String(text || '')
  if (!raw) return false
  const textNum = parseWorkshopNum(raw)
  if (workshopScope.num != null && textNum === workshopScope.num) return true
  if ((workshopScope.names || []).some((n) => n && raw.indexOf(n) !== -1)) return true
  const label = String(workshopScope.label || '').trim()
  if (label) {
    const core = label.replace(/车间$/u, '').trim()
    if (core.length >= 2 && raw.indexOf(core) !== -1) return true
    const shortCore = core.replace(/\d+号?$/u, '').trim()
    if (shortCore.length >= 2 && raw.indexOf(shortCore) !== -1 && /\d/.test(raw)) return true
  }
  return false
}

export function textMatchesSystemScope(text, systemScope, workshopMatched) {
  if (!systemScope || !systemScope.raw) return true
  const raw = String(text || '')
  const textS = parseSystemNum(raw)
  const sNum = systemScope.num
  if (sNum != null && textS == null && workshopMatched) return true
  if (sNum != null && textS === sNum) return true
  if ((systemScope.names || []).some((n) => n && raw.indexOf(n) !== -1)) return true
  if (sNum != null && new RegExp('[-·]' + sNum + '[-·]').test(raw)) return true
  if (sNum != null && new RegExp(sNum + '号(系统|循环|循环泵|池)').test(raw)) return true
  return false
}

export function matchesSiteScopeText(text, workshopScope, systemScope) {
  const hasScope = !!(workshopScope && workshopScope.raw) || !!(systemScope && systemScope.raw)
  if (!hasScope) return true
  const wOk = textMatchesWorkshopScope(text, workshopScope)
  if (!wOk) return false
  return textMatchesSystemScope(text, systemScope, wOk)
}

export function pondRowMatchesScope(row, workshopScope, systemScope) {
  const label = row.label || row.displayLabel || row.pondDetailNum || row.pondName || ''
  const workshopName = row.workshopName || row.workshop || ''
  const wOk = textMatchesWorkshopScope(label, workshopScope)
    || textMatchesWorkshopScope(workshopName, workshopScope)
  if (!wOk) return false
  if (!systemScope || !systemScope.raw) return true
  const sys = row.systemName || row.pondSystemName || row.systemValue || ''
  return textMatchesSystemScope(label, systemScope, true)
    || textMatchesSystemScope(sys, systemScope, true)
}
