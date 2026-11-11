/**
 * 全站鱼池展示名：与档案字段、筛选选项对齐
 * 格式优先：车间名-系统名-鱼池名（保留「网箱4」等真实名称）
 */
import { formatWorkshop, formatSystem, formatPond, parseWorkshopNum, parseSystemNum, parsePondNum, normalizeLakeName } from '@/utils/siteNaming'

function pickFirst() {
  for (let i = 0; i < arguments.length; i++) {
    const v = arguments[i]
    if (v != null && String(v).trim() !== '') return String(v).trim()
  }
  return ''
}

/** 从鱼池档案 / 健康行提取车间、系统、池名三元组 */
export function resolveSiteParts(row) {
  if (!row) return { workshop: '', system: '', pond: '', code: '' }
  const workshop = pickFirst(row.workshopName, row.workshop, row.workshop_name)
  const system = pickFirst(
    row.pondSystemName,
    row.pond_system_name,
    row.systemName,
    row.system_name,
    row.pondNum && !/^c\d+-s\d+/i.test(String(row.pondNum)) ? row.pondNum : '',
    formatSystem(row.pondNum)
  )
  const pond = pickFirst(row.pondName, row.pond_name, row.name)
  const code = pickFirst(row.pondDetailNum, row.pond_detail_num, row.pondCode, row.code, row.poolCode)
  return { workshop, system, pond, code }
}

/**
 * 统一展示标签：档案有名用档案；否则回退 normalizeLakeName(code)
 * 例：车间1-系统1-鱼池1 / 车间1-系统1-网箱4 / 大西洋鲑1车间-系统1-大西洋鲑1号池
 */
export function buildSiteDisplayLabel(row) {
  const parts = resolveSiteParts(row)
  if (parts.workshop && parts.system && parts.pond) {
    return parts.workshop + '-' + parts.system + '-' + parts.pond
  }
  if (parts.workshop && parts.pond) {
    return parts.system
      ? parts.workshop + '-' + parts.system + '-' + parts.pond
      : parts.workshop + '-' + parts.pond
  }
  if (parts.code) {
    const normalized = normalizeLakeName(parts.code, parts.system, parts.pond)
    if (normalized) return normalized
    return parts.code
  }
  const wn = parseWorkshopNum(parts.workshop)
  const sn = parseSystemNum(parts.system)
  const pn = parsePondNum(parts.pond)
  const fallback = [
    wn != null ? '车间' + wn : formatWorkshop(parts.workshop),
    sn != null ? '系统' + sn : formatSystem(parts.system),
    pn != null ? '鱼池' + pn : formatPond(parts.pond)
  ].filter(Boolean)
  return fallback.join('-')
}

export function buildWorkshopOptionLabel(row) {
  const parts = resolveSiteParts(row)
  return parts.workshop || formatWorkshop(parts.code) || ''
}

export function buildSystemOptionLabel(row) {
  const parts = resolveSiteParts(row)
  if (parts.system) return parts.system
  const sn = parseSystemNum(parts.code)
  return sn != null ? '系统' + sn : ''
}

/** Promise 超时，避免远端 modules-api / 阈值挂死导致整页 loading */
export function withTimeout(promise, ms, fallback) {
  const timeoutMs = Number(ms) || 8000
  let timer = null
  const timeoutPromise = new Promise((resolve) => {
    timer = setTimeout(() => resolve(fallback), timeoutMs)
  })
  return Promise.race([
    Promise.resolve(promise).then((v) => {
      if (timer) clearTimeout(timer)
      return v
    }).catch((e) => {
      if (timer) clearTimeout(timer)
      throw e
    }),
    timeoutPromise
  ])
}
