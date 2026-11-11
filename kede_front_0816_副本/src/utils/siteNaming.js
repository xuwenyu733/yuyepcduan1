/**
 * 全站车间 / 系统 / 鱼池命名统一
 * 展示约定：车间1-系统1-鱼池1（筛选项、列表、主页与二级页一致）
 * 兼容解析：c1-s1-1、A1-01、1号车间、A车间、车间1 等
 */

const LETTER_TO_NUM = { A: 1, B: 2, C: 3, D: 4, E: 5, F: 6, G: 7, H: 8 }

/** 筛选项 / 占位符，不可当作真实车间·系统·鱼池参与拼接 */
export function isSitePlaceholder(raw) {
  const s = String(raw == null ? '' : raw).trim()
  if (!s) return true
  if (s === '—' || s === '-' || s === '–' || s === '——' || s === '--') return true
  if (s === '全场') return true
  if (/^全部(车间|系统|鱼池|池号)?$/.test(s)) return true
  // 被错误反复 normalize 后的污染串
  if (/^(全部系统|全部鱼池|全部车间|[—–\-])+$/.test(s.replace(/\s/g, ''))) return true
  return false
}

function letterIndex(ch) {
  const u = String(ch || '').toUpperCase()
  return LETTER_TO_NUM[u] || null
}

function numToLetter(n) {
  const i = Number(n)
  if (!i || i < 1) return ''
  return 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'[i - 1] || String(i)
}

/** @returns {number|null} */
export function parseWorkshopNum(raw) {
  const s = String(raw == null ? '' : raw).trim()
  if (!s) return null
  let m = s.match(/^c(\d+)$/i)
  if (m) return Number(m[1])
  m = s.match(/^车间\s*(\d+)/)
  if (m) return Number(m[1])
  m = s.match(/^(\d+)\s*号?\s*车间/)
  if (m) return Number(m[1])
  m = s.match(/^([A-Za-z])\s*车间/)
  if (m) {
    const n = letterIndex(m[1])
    return n || null
  }
  m = s.match(/^车间\s*([A-Za-z])/)
  if (m) {
    const n = letterIndex(m[1])
    return n || null
  }
  if (/^\d+$/.test(s)) return Number(s)
  return null
}

/** @returns {number|null} */
export function parseSystemNum(raw) {
  const s = String(raw == null ? '' : raw).trim()
  if (!s) return null
  let m = s.match(/^s(\d+)$/i)
  if (m) return Number(m[1])
  m = s.match(/^系统\s*(\d+)/)
  if (m) return Number(m[1])
  m = s.match(/^(\d+)\s*号?\s*系统/)
  if (m) return Number(m[1])
  // 兼容「车间2-系统1」「车间2系统1」等复合写法
  m = s.match(/(?:^|[-_/])系统\s*(\d+)/)
  if (m) return Number(m[1])
  m = s.match(/系统\s*(\d+)\s*$/)
  if (m) return Number(m[1])
  m = s.match(/^([A-Za-z])(\d+)\s*系统?/)
  if (m) return Number(m[2])
  m = s.match(/^([A-Za-z])\s*系统/)
  if (m) {
    const n = letterIndex(m[1])
    return n || null
  }
  if (/^\d+$/.test(s)) return Number(s)
  return null
}

/** @returns {number|null} */
export function parsePondNum(raw) {
  const s = String(raw == null ? '' : raw).trim()
  if (!s) return null
  let m = s.match(/^鱼池\s*(\d+)/)
  if (m) return Number(m[1])
  m = s.match(/^(\d+)\s*号?\s*鱼池/)
  if (m) return Number(m[1])
  m = s.match(/池\s*(\d+)$/)
  if (m) return Number(m[1])
  if (/^\d+$/.test(s)) return Number(s)
  return null
}

/**
 * 解析组合字符串或单字段，返回统一结构
 * @returns {{ workshopNum, systemNum, pondNum, workshop, system, pond, label, code }}
 */
export function parseSiteString(raw, systemHint, pondHint) {
  const empty = {
    workshopNum: null,
    systemNum: null,
    pondNum: null,
    workshop: '',
    system: '',
    pond: '',
    label: '',
    code: ''
  }
  const rawText = isSitePlaceholder(raw) ? '' : String(raw == null ? '' : raw).trim()
  const sysText = isSitePlaceholder(systemHint) ? '' : String(systemHint == null ? '' : systemHint).trim()
  const pondText = isSitePlaceholder(pondHint) ? '' : String(pondHint == null ? '' : pondHint).trim()
  const s = rawText
  if (!s && !sysText && !pondText) return empty

  let wn = null
  let sn = null
  let pn = null

  let m = s.match(/^c(\d+)-s(\d+)-(\d+)$/i)
  if (m) {
    wn = Number(m[1])
    sn = Number(m[2])
    pn = Number(m[3])
  }
  if (!wn) {
    m = s.match(/^车间\s*(\d+)\s*[-·]\s*系统\s*(\d+)\s*[-·]\s*鱼池\s*(\d+)/)
    if (m) {
      wn = Number(m[1])
      sn = Number(m[2])
      pn = Number(m[3])
    }
  }
  if (!wn) {
    m = s.match(/^([A-Za-z])(\d+)-(\d+)$/)
    if (m) {
      wn = letterIndex(m[1])
      sn = Number(m[2])
      pn = Number(m[3])
    }
  }
  if (!wn) {
    m = s.match(/^([A-Za-z])(\d+)-(\d{2})$/)
    if (m) {
      wn = letterIndex(m[1])
      sn = Number(m[2])
      pn = Number(m[3])
    }
  }

  if (!wn) {
    m = s.match(/^(\d+)\s*号?\s*车间\s*[-·]?\s*([A-Za-z])\s*池/)
    if (m) {
      wn = Number(m[1])
      pn = letterIndex(m[2])
      sn = sn || 1
    }
  }

  if (wn == null && s) wn = parseWorkshopNum(s)
  if (sn == null) sn = parseSystemNum(sysText || (s && !wn && !pn ? s : ''))
  if (pn == null) pn = parsePondNum(pondText || s)

  if (wn == null && s && /^[A-Za-z]/.test(s) && sn == null) {
    const letter = s.charAt(0)
    wn = letterIndex(letter)
  }

  const workshop = wn != null ? '车间' + wn : (s ? formatWorkshopLegacy(s) : '')
  const system = sn != null ? '系统' + sn : (sysText ? formatSystemLegacy(sysText) : '')
  const pond = pn != null ? '鱼池' + pn : formatPondLegacy(pondText || (pn == null && s && !wn && !sn ? s : ''))
  let label = formatSiteLabel(workshop, system, pond)
  if (wn != null && sn == null && pn == null && (/^[A-Za-z]车间$/.test(s) || /^\d+\s*号?\s*车间$/.test(s))) {
    label = workshop
  }
  const code = formatSiteCode(wn, sn, pn) || s

  return {
    workshopNum: wn,
    systemNum: sn,
    pondNum: pn,
    workshop,
    system,
    pond,
    label: label || s,
    code
  }
}

function formatWorkshopLegacy(raw) {
  const n = parseWorkshopNum(raw)
  return n != null ? '车间' + n : String(raw || '').trim()
}

function formatSystemLegacy(raw) {
  const n = parseSystemNum(raw)
  return n != null ? '系统' + n : String(raw || '').trim()
}

function formatPondLegacy(raw) {
  const n = parsePondNum(raw)
  if (n != null) return '鱼池' + n
  const s = String(raw || '').trim()
  if (!s) return ''
  if (/池$/.test(s)) return s
  return s
}

/** 车间展示：车间1 */
export function formatWorkshop(raw) {
  const n = parseWorkshopNum(raw)
  if (n != null) return '车间' + n
  const s = String(raw || '').trim()
  if (!s) return ''
  const legacy = s.match(/^(\d+)号车间$/)
  if (legacy) return '车间' + legacy[1]
  return s
}

/** 系统展示：系统1 */
export function formatSystem(raw) {
  const n = parseSystemNum(raw)
  if (n != null) return '系统' + n
  const s = String(raw || '').trim()
  if (!s) return ''
  const legacy = s.match(/^(\d+)号系统$/)
  if (legacy) return '系统' + legacy[1]
  return s
}

/** 鱼池展示：鱼池1（兼容旧 formatPondCode 引用） */
export function formatPond(raw) {
  const n = parsePondNum(raw)
  if (n != null) return '鱼池' + n
  return formatPondLegacy(raw)
}

export function formatPondCode(raw) {
  return formatPond(raw)
}

/** 车间-系统-鱼池 组合标签 */
export function formatSiteLabel(workshop, system, pond) {
  const wn = parseWorkshopNum(workshop)
  const sn = parseSystemNum(system)
  const pn = parsePondNum(pond)
  const parts = []
  if (wn != null) parts.push('车间' + wn)
  else if (workshop) parts.push(formatWorkshop(workshop))
  if (sn != null) parts.push('系统' + sn)
  else if (system) parts.push(formatSystem(system))
  if (pn != null) parts.push('鱼池' + pn)
  else if (pond) parts.push(formatPond(pond))
  return parts.filter(Boolean).join('-')
}

/** 内部编码：c1-s1-1 */
export function formatSiteCode(workshop, system, pond) {
  const wn = typeof workshop === 'number' ? workshop : parseWorkshopNum(workshop)
  const sn = typeof system === 'number' ? system : parseSystemNum(system)
  const pn = typeof pond === 'number' ? pond : parsePondNum(pond)
  if (wn != null && sn != null && pn != null) {
    return 'c' + wn + '-s' + sn + '-' + pn
  }
  return ''
}

/** 将任意池名转为统一展示标签（占位符不参与拼接，避免「全部系统」污染池号） */
export function normalizeLakeName(raw, systemHint, pondHint) {
  if (isSitePlaceholder(raw) && isSitePlaceholder(systemHint) && isSitePlaceholder(pondHint)) return ''
  if (isSitePlaceholder(raw) && !isSitePlaceholder(systemHint) && isSitePlaceholder(pondHint)) {
    // 仅有系统提示、无真实池号时，不把系统名当成池号标签
    return ''
  }
  const site = parseSiteString(raw, systemHint, pondHint)
  if (!site.label) return isSitePlaceholder(raw) ? '' : String(raw || '').trim()
  return site.label
}

export function workshopsEqual(a, b) {
  const na = parseWorkshopNum(a)
  const nb = parseWorkshopNum(b)
  if (na != null && nb != null) return na === nb
  return formatWorkshop(a) === formatWorkshop(b)
}

export function systemsEqual(a, b) {
  const na = parseSystemNum(a)
  const nb = parseSystemNum(b)
  if (na != null && nb != null) return na === nb
  return formatSystem(a) === formatSystem(b)
}

export function pondsEqual(a, b) {
  const na = parsePondNum(a)
  const nb = parsePondNum(b)
  if (na != null && nb != null) return na === nb
  return normalizeLakeName(a) === normalizeLakeName(b)
}

export function siteLabelsEqual(a, b) {
  return normalizeLakeName(a) === normalizeLakeName(b)
}

/**
 * 设备展示名（保留设备类型+编号，车间统一）
 */
export function formatDeviceName(raw, workshop) {
  const s = String(raw || '').trim()
  if (!s) return ''
  if (/^(微滤机|循环泵|增氧机|温控器|风机|水泵|臭氧)/.test(s) && /[A-Za-z0-9]/.test(s)) {
    return s
  }
  const m = s.match(/^(\d+)\s*号\s*(.+)$/)
  if (m) {
    const wn = parseWorkshopNum(workshop) || Number(m[1])
    const letter = numToLetter(wn) || 'A'
    return m[2] + letter + m[1]
  }
  return s
}

/** 统一报警/任务/设备行中的车间-系统-鱼池字段 */
export function normalizeSiteRecord(row) {
  if (!row) return row
  const workshop = formatWorkshop(row.workshop || row.workshopName)
  const systemName = formatSystem(row.systemName || row.system_name || row.system || row.pondSystemName)
  const poolRaw = row.pool_code || row.poolCode || row.pondDetailNum || row.pool_name || row.poolName || row.location || row.pond || row.lake_name || ''
  const site = parseSiteString(poolRaw, systemName, poolRaw)
  const pondLabel = site.label || normalizeLakeName(poolRaw, systemName)
  const siteCode = site.code || formatSiteCode(site.workshopNum, site.systemNum, site.pondNum) || poolRaw
  return Object.assign({}, row, {
    workshop: workshop || site.workshop || row.workshop || '',
    workshopName: workshop || site.workshop || row.workshopName || '',
    systemName: systemName || site.system || '',
    system_name: systemName || site.system || row.system_name || '',
    system: systemName || site.system || row.system || '',
    poolCode: pondLabel,
    pool_code: pondLabel,
    poolName: pondLabel,
    pond: pondLabel,
    location: pondLabel || workshop || row.location || '未指定位置',
    displayLabel: pondLabel,
    siteCode: siteCode
  })
}

/** 从鱼池档案行提取并规范化字段 */
export function enrichPondRow(row) {
  if (!row) return row
  const code = row.pondDetailNum || row.pondCode || row.code || row.poolCode || row.name || ''
  const site = parseSiteString(code, row.systemName || row.pondSystemName || row.pondNum, code)
  const workshop = site.workshopNum != null ? '车间' + site.workshopNum : formatWorkshop(row.workshop || row.workshopName)
  const systemName = site.systemNum != null ? '系统' + site.systemNum : formatSystem(row.systemName || row.pondSystemName || row.pondNum)
  const pondLabel = site.label || formatSiteLabel(workshop, systemName, code)
  const siteCode = site.code || formatSiteCode(site.workshopNum, site.systemNum, site.pondNum)
  return Object.assign({}, row, {
    workshop,
    workshopName: workshop,
    systemName,
    pondSystemName: systemName,
    displayLabel: pondLabel,
    siteCode,
    code: code || siteCode,
    pondCode: row.pondCode || code || siteCode,
    poolCode: row.poolCode || code || siteCode
  })
}
