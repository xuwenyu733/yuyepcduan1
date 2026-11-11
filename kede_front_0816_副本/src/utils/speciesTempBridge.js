/**
 * 阈值「品种水温」↔ 鱼类健康状态「品种筛选」同步，
 * 并按对应品种水温区间重算健康环境层水温得分。
 */
import { getPrecisionFeedingPonds } from '@/api/fish/zkBiologyCapital'
import { recomputeHealthOverall } from '@/utils/thresholdConfig'
import { parseLayerInfo } from '@/utils/healthLayerDisplay'
import { withTimeout } from '@/utils/siteDisplay'

/** 展示名归一：健康页鱼种名 → 阈值常用名 */
const SPECIES_CANONICAL = {
  鲈鱼苗: '加州鲈',
  鲈鱼: '加州鲈',
  加州鲈: '加州鲈',
  三文鱼: '大西洋鲑',
  鲑鱼: '大西洋鲑',
  大西洋鲑: '大西洋鲑',
  虹鳟: '虹鳟',
  鲤鱼: '鲤鱼',
  海参: '海参'
}

/** 字典 zk_pond_fish_types value → label（兼容 fish_types='4' / 'null,4'） */
const FISH_TYPE_DICT_LABEL = {
  1: '三文鱼',
  2: '鲑鱼',
  3: '大西洋鲑',
  4: '鲈鱼苗',
  5: '海参'
}

/** 已知品种默认水温（warn / critical），单位 °C */
const SPECIES_TEMP_PRESETS = {
  大西洋鲑: { warnLow: 10, warnHigh: 16, criticalLow: 8, criticalHigh: 18 },
  虹鳟: { warnLow: 8, warnHigh: 14, criticalLow: 6, criticalHigh: 16 },
  加州鲈: { warnLow: 22, warnHigh: 28, criticalLow: 20, criticalHigh: 32 },
  鲤鱼: { warnLow: 18, warnHigh: 28, criticalLow: 15, criticalHigh: 32 },
  海参: { warnLow: 10, warnHigh: 18, criticalLow: 8, criticalHigh: 22 }
}

const FALLBACK_TEMP = { warnLow: 18, warnHigh: 28, criticalLow: 15, criticalHigh: 32 }

const TEMP_FULL = 3
const TEMP_PARTIAL = 1

function canonicalizeLabel(label) {
  const s = String(label == null ? '' : label).trim()
  // 「未知」是接口空值占位，不当作品种筛选项
  if (!s || s.toLowerCase() === 'null' || s === '-' || s === '未知') return ''
  return SPECIES_CANONICAL[s] || s
}

/**
 * 归一鱼种名：丢弃字面量 null，支持字典编码与「null,4」脏串。
 * 返回阈值常用名（如 鲈鱼苗→加州鲈）；无法识别则返回 ''。
 */
export function normalizeSpeciesName(name) {
  const raw = String(name == null ? '' : name).trim()
  if (!raw || raw === '-' || raw === '未知' || raw.toLowerCase() === 'null') return ''

  const parts = raw
    .split(/[,，;；|/]+/)
    .map((s) => s.trim())
    .filter((s) => s && s.toLowerCase() !== 'null' && s !== '-' && s !== '未知')
  if (!parts.length) return ''

  // 优先中文品名
  for (let i = 0; i < parts.length; i++) {
    if (/[\u4e00-\u9fff]/.test(parts[i])) {
      return canonicalizeLabel(parts[i])
    }
  }
  // 字典 value：与主档 SQL 一致，取末个有效编码（null,4 → 4）
  for (let i = parts.length - 1; i >= 0; i--) {
    const label = FISH_TYPE_DICT_LABEL[parts[i]]
    if (label) return canonicalizeLabel(label)
  }

  const token = parts[0]
  if (!token || token.toLowerCase() === 'null') return ''
  // 纯数字且未进字典：不当作品种名，避免下拉出现 "4"
  if (/^\d+$/.test(token)) return ''
  return canonicalizeLabel(token)
}

export function speciesNamesEqual(a, b) {
  const na = normalizeSpeciesName(a)
  const nb = normalizeSpeciesName(b)
  if (!na || !nb) return false
  return na === nb || String(a).trim() === String(b).trim()
}

export function defaultSpeciesTempRow(speciesName) {
  const canon = normalizeSpeciesName(speciesName) || String(speciesName || '').trim()
  const preset = SPECIES_TEMP_PRESETS[canon] || FALLBACK_TEMP
  return {
    species: canon || String(speciesName || '').trim(),
    warnLow: preset.warnLow,
    warnHigh: preset.warnHigh,
    criticalLow: preset.criticalLow,
    criticalHigh: preset.criticalHigh,
    enabled: true
  }
}

/**
 * 从精准投喂主档收集健康页可筛品种（与鱼类健康状态同源）
 */
export async function fetchHealthFilterSpecies(options) {
  const timeoutMs = (options && options.timeoutMs) || 10000
  const res = await withTimeout(getPrecisionFeedingPonds(), timeoutMs, { data: [] }).catch(() => ({ data: [] }))
  const names = []
  ;(res && res.data ? res.data : []).forEach((p) => {
    const n = normalizeSpeciesName(p && (p.fishTypeName || p.fishTypes || p.fishName || p.species))
    if (n) names.push(n)
  })
  return [...new Set(names)].sort((a, b) => a.localeCompare(b, 'zh-CN'))
}

/**
 * 合并：健康可筛品种 + 已有阈值行 → 可供管理的 speciesTemps
 * - 健康有、阈值无：按预设补行
 * - 双方都有：保留已配置区间（按归一名对齐）
 * - 阈值有、健康暂无：保留（避免丢配置）
 */
export function syncSpeciesTempsWithHealthCatalog(speciesTemps, catalogNames) {
  const existing = Array.isArray(speciesTemps) ? speciesTemps : []
  const catalog = [...new Set((catalogNames || []).map(normalizeSpeciesName).filter(Boolean))]
  const byKey = new Map()

  existing.forEach((row) => {
    if (!row || !row.species) return
    const key = normalizeSpeciesName(row.species) || String(row.species).trim()
    if (!key) return
    if (!byKey.has(key)) {
      byKey.set(key, {
        species: key,
        warnLow: Number(row.warnLow),
        warnHigh: Number(row.warnHigh),
        criticalLow: Number(row.criticalLow),
        criticalHigh: Number(row.criticalHigh),
        enabled: row.enabled !== false
      })
    }
  })

  catalog.forEach((name) => {
    if (byKey.has(name)) return
    byKey.set(name, defaultSpeciesTempRow(name))
  })

  const ordered = []
  const seen = new Set()
  catalog.forEach((name) => {
    if (seen.has(name)) return
    seen.add(name)
    ordered.push(byKey.get(name))
  })
  byKey.forEach((row, key) => {
    if (seen.has(key)) return
    seen.add(key)
    ordered.push(row)
  })
  return ordered
}

export function listSyncedSpeciesOptions(catalogNames, speciesTemps, liveNames) {
  const set = new Set()
  ;(catalogNames || []).forEach((n) => {
    const k = normalizeSpeciesName(n)
    if (k) set.add(k)
  })
  ;(liveNames || []).forEach((n) => {
    const k = normalizeSpeciesName(n)
    if (k) set.add(k)
  })
  ;(speciesTemps || []).forEach((row) => {
    if (!row || row.enabled === false) return
    const k = normalizeSpeciesName(row.species)
    if (k) set.add(k)
  })
  return [...set].sort((a, b) => a.localeCompare(b, 'zh-CN'))
}

/**
 * 全站统一「品种」筛选项：只取鱼池/业务行真实品种字段，归一别名后去重。
 * 不注入阈值预设里未实际出现的品种，保证健康/投喂/生长/死亡/生物资产页一致。
 */
export function collectSpeciesFilterOptions(rows) {
  const live = []
  ;(rows || []).forEach((p) => {
    if (!p) return
    const raw =
      p.fishName ||
      p.fishTypeName ||
      p.fishSpecies ||
      p.speciesName ||
      p.species ||
      p.fishTypes ||
      p.label ||
      ''
    const n = normalizeSpeciesName(raw)
    if (n) live.push(n)
  })
  return listSyncedSpeciesOptions(live, [], live)
}

/** 按鱼种匹配水温区间（别名归一） */
export function resolveSpeciesTempRange(speciesTemps, speciesName) {
  const list = Array.isArray(speciesTemps) ? speciesTemps : []
  if (!list.length) return null
  const key = normalizeSpeciesName(speciesName)
  if (key) {
    const hit = list.find((row) => row && speciesNamesEqual(row.species, key))
    if (hit) return hit
  }
  // 无品种标注时不误用第一条，避免冷水/温水串配
  return null
}

/**
 * 警告区间满分；临界外圈部分分；临界外 0
 */
export function scoreTempBySpeciesRange(tempValue, range, fullScore = TEMP_FULL, partialScore = TEMP_PARTIAL) {
  if (!range || range.enabled === false) return null
  const t = Number(tempValue)
  if (!Number.isFinite(t)) return null
  const warnLow = Number(range.warnLow)
  const warnHigh = Number(range.warnHigh)
  const critLow = Number(range.criticalLow)
  const critHigh = Number(range.criticalHigh)
  if (![warnLow, warnHigh, critLow, critHigh].every(Number.isFinite)) return null
  if (t >= warnLow && t <= warnHigh) return fullScore
  if (t >= critLow && t <= critHigh) return partialScore
  return 0
}

/**
 * 用阈值品种水温重算单池健康环境层水温分与综合分（在权重重算前调用，层分按满分 20 基线）
 */
export function applySpeciesWaterTempToHealthItem(item, waterConfig) {
  if (!item || item._pendingHealth) return item
  const species = item.fishName || item.species || item.fishTypeName || item.fishTypes || ''
  const range = resolveSpeciesTempRange(waterConfig && waterConfig.speciesTemps, species)
  if (!range || range.enabled === false) {
    return Object.assign({}, item, { _speciesTempRange: null })
  }

  let envObj = item.environmentalLayerInformationObj
  if (!envObj) {
    envObj = parseLayerInfo(item.environmentalLayerInformation) || null
  }
  if (!envObj) {
    return Object.assign({}, item, { _speciesTempRange: range })
  }

  const tempVal = envObj.doTemp
  const newTempScore = scoreTempBySpeciesRange(tempVal, range)
  if (newTempScore == null) {
    return Object.assign({}, item, {
      environmentalLayerInformationObj: envObj,
      _speciesTempRange: range
    })
  }

  const oldScores = (envObj.itemScores && typeof envObj.itemScores === 'object')
    ? envObj.itemScores
    : {}
  const oldTempScore = Number(oldScores.doTemp)
  const nextScores = Object.assign({}, oldScores, { doTemp: newTempScore })

  let nextEnv = Number(item.environmentalLayerRating)
  if (Number.isFinite(oldTempScore) && Number.isFinite(nextEnv)) {
    nextEnv = Math.max(0, Math.min(20, nextEnv - oldTempScore + newTempScore))
  } else {
    const keys = ['doOxygen', 'nitrite', 'ammoniaNitrogen', 'phPh', 'doTemp', 'nitrate']
    let sum = 0
    let n = 0
    keys.forEach((k) => {
      const v = Number(nextScores[k])
      if (Number.isFinite(v)) {
        sum += v
        n += 1
      }
    })
    if (n) nextEnv = Math.max(0, Math.min(20, sum))
  }

  const exceptions = Array.isArray(envObj.exceptions) ? envObj.exceptions.slice() : []
  const filtered = exceptions.filter((e) => e && !String(e).includes('水温'))
  if (newTempScore < TEMP_FULL) {
    filtered.push(
      newTempScore <= 0
        ? `水温异常(${tempVal}℃，适宜 ${range.warnLow}-${range.warnHigh}℃)`
        : `水温偏离适宜区间(${tempVal}℃，适宜 ${range.warnLow}-${range.warnHigh}℃)`
    )
  }

  const nextEnvObj = Object.assign({}, envObj, {
    itemScores: nextScores,
    exceptions: filtered,
    layerScore: nextEnv
  })

  return Object.assign({}, item, {
    environmentalLayerRating: nextEnv,
    environmentalLayerInformationObj: nextEnvObj,
    _speciesTempRange: range,
    _tempScoreFromThreshold: newTempScore
  })
}

/** 批量应用后再按健康权重重算综合分 */
export function applySpeciesWaterTempAndWeights(list, waterConfig, weights) {
  const w = weights || { env: 20, appearance: 60, internal: 20 }
  return (list || []).map((item) => {
    if (!item || item._pendingHealth) return item
    const withTemp = applySpeciesWaterTempToHealthItem(item, waterConfig)
    const envScore = Number(withTemp.environmentalLayerRating)
    const appScore = Number(withTemp.behavioralLayerRating)
    const intScore = Number(withTemp.bodyLayerRating)
    const recomputed = recomputeHealthOverall({
      envScore: Number.isFinite(envScore) ? envScore : 0,
      appScore: Number.isFinite(appScore) ? appScore : 0,
      intScore: Number.isFinite(intScore) ? intScore : 0,
      baseEnv: 20,
      baseApp: 60,
      baseInt: 20
    }, w)
    return Object.assign({}, withTemp, {
      _rawOverallRating: item.overallRating,
      environmentalLayerRating: recomputed.envScaled,
      behavioralLayerRating: recomputed.appScaled,
      bodyLayerRating: recomputed.intScaled,
      overallRating: recomputed.overall
    })
  })
}

export function formatSpeciesTempHint(range) {
  if (!range) return ''
  return `${range.warnLow}-${range.warnHigh}°C`
}
