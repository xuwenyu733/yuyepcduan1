/**
 * 系统总览 - 水质阈值适配层
 * 统一加载 BFF 阈值配置，兜底使用内置默认值
 * 提供设定值格式化、状态判定等功能
 */
import { fetchAll } from '@/api/threshold'
import { resolveSpeciesTempRange } from '@/utils/speciesTempBridge'

/* ===== 内置默认阈值（匹配 BFF seed.js，兜底使用） ===== */
const DEFAULT_WATER = {
  speciesTemps: [
    { species: '大西洋鲑', warnLow: 10, warnHigh: 16, criticalLow: 8, criticalHigh: 18, enabled: true },
    { species: '虹鳟', warnLow: 8, warnHigh: 14, criticalLow: 6, criticalHigh: 16, enabled: true },
    { species: '加州鲈', warnLow: 22, warnHigh: 28, criticalLow: 20, criticalHigh: 32, enabled: true },
    { species: '鲤鱼', warnLow: 18, warnHigh: 28, criticalLow: 15, criticalHigh: 32, enabled: true },
    { species: '海参', warnLow: 10, warnHigh: 18, criticalLow: 8, criticalHigh: 22, enabled: true }
  ],
  doLevels: { normal: 5, warn: 4, highRisk: 3, emergency: 2, unit: 'mg/L' },
  nh3: {
    enabled: true,
    levels: [{ label: '安全', value: 0.2 }, { label: '警戒', value: 0.4 }, { label: '危险', value: 0.4 }],
    unit: 'mg/L'
  },
  no2: {
    enabled: true,
    levels: [{ label: '安全', value: 0.2 }, { label: '警戒', value: 0.5 }, { label: '危险', value: 0.5 }],
    unit: 'mg/L'
  },
  metrics: [
    { id: 'ph', name: 'pH 值', unit: '', warnLow: 7.0, warnHigh: 8.0, criticalLow: 6.5, criticalHigh: 8.5, enabled: true },
    { id: 'orp', name: 'ORP', unit: 'mV', warnLow: 200, warnHigh: 600, criticalLow: 100, criticalHigh: 700, enabled: true },
    { id: 'alk', name: '碱度', unit: 'mg/L CaCO₃', warnLow: 80, warnHigh: 250, criticalLow: 50, criticalHigh: 350, enabled: true },
    { id: 'hard', name: '硬度', unit: 'mg/L CaCO₃', warnLow: 100, warnHigh: 300, criticalLow: 50, criticalHigh: 400, enabled: true },
    { id: 'co2', name: 'CO₂', unit: 'mg/L', warnLow: 0, warnHigh: 15, criticalLow: 0, criticalHigh: 25, enabled: true },
    { id: 'tp', name: '总磷', unit: 'mg/L', warnLow: 0, warnHigh: 1, criticalLow: 0, criticalHigh: 2, enabled: true },
    { id: 'tn', name: '总氮', unit: 'mg/L', warnLow: 0, warnHigh: 10, criticalLow: 0, criticalHigh: 20, enabled: true },
    { id: 'gsm', name: 'GSM', unit: 'ng/L', warnLow: 0, warnHigh: 10, criticalLow: 0, criticalHigh: 20, enabled: true },
    { id: 'mib', name: '2-MIB', unit: 'ng/L', warnLow: 0, warnHigh: 10, criticalLow: 0, criticalHigh: 20, enabled: true },
    { id: 'no3', name: '硝酸盐', unit: 'mg/L', warnLow: 0, warnHigh: 35, criticalLow: 0, criticalHigh: 50, enabled: true },
    { id: 'tss', name: '浊度 TSS', unit: 'mg/L', warnLow: 0, warnHigh: 20, criticalLow: 0, criticalHigh: 40, enabled: true }
  ]
}

/* ===== 单例状态 ===== */
let _waterConfig = null
let _loading = false
let _loadPromise = null

/**
 * 获取水质阈值配置（优先 BFF，失败兜底默认值）
 */
export async function getWaterThresholdConfig() {
  if (_waterConfig) return _waterConfig
  if (_loadPromise) return _loadPromise

  _loadPromise = (async () => {
    try {
      const res = await fetchAll()
      const data = (res && res.data) || res || {}
      if (data && data.water && isValidWaterConfig(data.water)) {
        _waterConfig = data.water
      }
    } catch (e) {
      console.warn('[水阈值] BFF 不可用，使用内置默认阈值', e.message || e)
    }
    if (!_waterConfig) {
      _waterConfig = DEFAULT_WATER
    }
    return _waterConfig
  })()

  return _loadPromise
}

/**
 * 清空水质阈值单例缓存（阈值页保存后调用）
 */
export function clearWaterThresholdCache() {
  _waterConfig = null
  _loading = false
  _loadPromise = null
}

/**
 * 获取指标对象（从 metrics 数组中按 id 查找）
 */
function findMetric(waterConfig, metricId) {
  const metrics = waterConfig && waterConfig.metrics ? waterConfig.metrics : []
  return metrics.find(m => m.id === metricId) || null
}

/**
 * 获取温度区间：优先按品种匹配，否则取第一个启用的鱼种
 */
export function getTempRange(waterConfig, speciesName) {
  const species = (waterConfig && waterConfig.speciesTemps) || []
  if (speciesName) {
    const hit = resolveSpeciesTempRange(species, speciesName)
    if (hit) return hit
  }
  const enabled = species.find(s => s.enabled !== false) || species[0]
  if (!enabled) return { warnLow: 10, warnHigh: 16, criticalLow: 8, criticalHigh: 18 }
  return enabled
}

/**
 * 格式化阈值范围为展示字符串
 */
export function formatThresholdRange(waterConfig, metricKey, speciesName) {
  const wc = waterConfig || DEFAULT_WATER
  switch (metricKey) {
    case 'doTemp': {
      const t = getTempRange(wc, speciesName)
      return `${t.warnLow}-${t.warnHigh}°C`
    }
    case 'phPh': {
      const m = findMetric(wc, 'ph')
      return m ? `${m.warnLow}-${m.warnHigh}` : '7.0-8.0'
    }
    case 'doOxygen': {
      const d = (wc && wc.doLevels) || DEFAULT_WATER.doLevels
      return `≥${d.normal} mg/L`
    }
    case 'nh3': {
      const safe = (wc.nh3 && wc.nh3.levels && wc.nh3.levels[0]) ? wc.nh3.levels[0].value : 0.2
      return `<${safe} mg/L`
    }
    case 'no2': {
      const safe = (wc.no2 && wc.no2.levels && wc.no2.levels[0]) ? wc.no2.levels[0].value : 0.2
      return `<${safe} mg/L`
    }
    case 'orp': {
      const m = findMetric(wc, 'orp')
      return m ? `${m.warnLow}-${m.warnHigh} mV` : '200-600 mV'
    }
    case 'alk': {
      const m = findMetric(wc, 'alk')
      return m ? `${m.warnLow}-${m.warnHigh}` : '80-250'
    }
    case 'hard': {
      const m = findMetric(wc, 'hard')
      return m ? `${m.warnLow}-${m.warnHigh}` : '100-300'
    }
    case 'co2': {
      const m = findMetric(wc, 'co2')
      return m ? `<${m.warnHigh} mg/L` : '<15 mg/L'
    }
    case 'no3': {
      const m = findMetric(wc, 'no3')
      return m ? `<${m.warnHigh} mg/L` : '<35 mg/L'
    }
    case 'tss': {
      const m = findMetric(wc, 'tss')
      return m ? `<${m.warnHigh} mg/L` : '<20 mg/L'
    }
    case 'tp': {
      const m = findMetric(wc, 'tp')
      return m ? `<${m.warnHigh} mg/L` : '<1 mg/L'
    }
    case 'tn': {
      const m = findMetric(wc, 'tn')
      return m ? `<${m.warnHigh} mg/L` : '<10 mg/L'
    }
    // 盐度、电导率无阈值配置，使用默认
    case 'salinity':
      return '30-35‰'
    case 'conductivity':
      return '300-800 μS/cm'
    default:
      return '-'
  }
}

/**
 * 评估水质值状态：normal / warn / critical
 */
export function evaluateWaterStatus(waterConfig, metricKey, currentValue, speciesName) {
  if (currentValue == null || isNaN(currentValue)) return 'normal'
  const val = Number(currentValue)
  const wc = waterConfig || DEFAULT_WATER

  switch (metricKey) {
    case 'doTemp': {
      const t = getTempRange(wc, speciesName)
      if (t.enabled === false) return 'normal'
      if (val < t.criticalLow || val > t.criticalHigh) return 'critical'
      if (val < t.warnLow || val > t.warnHigh) return 'warn'
      return 'normal'
    }
    case 'phPh': {
      const m = findMetric(wc, 'ph')
      if (!m || m.enabled === false) return 'normal'
      if (val < m.criticalLow || val > m.criticalHigh) return 'critical'
      if (val < m.warnLow || val > m.warnHigh) return 'warn'
      return 'normal'
    }
    case 'doOxygen': {
      const d = (wc && wc.doLevels) || DEFAULT_WATER.doLevels
      // ≥normal 正常；[warn, normal) 预警；<warn 含高危/紧急 → 异常
      if (val < d.highRisk) return 'critical'
      if (val < d.warn) return 'critical'
      if (val < d.normal) return 'warn'
      return 'normal'
    }
    case 'nh3': {
      if (!wc.nh3 || wc.nh3.enabled === false) return 'normal'
      const levels = wc.nh3.levels || []
      const safe = levels[0] ? levels[0].value : 0.2
      const warn = levels[1] ? levels[1].value : 0.4
      if (val > warn) return 'critical'
      if (val > safe) return 'warn'
      return 'normal'
    }
    case 'no2': {
      if (!wc.no2 || wc.no2.enabled === false) return 'normal'
      const levels = wc.no2.levels || []
      const safe = levels[0] ? levels[0].value : 0.2
      const warn = levels[1] ? levels[1].value : 0.5
      if (val > warn) return 'critical'
      if (val > safe) return 'warn'
      return 'normal'
    }
    case 'orp': {
      const m = findMetric(wc, 'orp')
      if (!m || m.enabled === false) return 'normal'
      if (val < m.criticalLow || val > m.criticalHigh) return 'critical'
      if (val < m.warnLow || val > m.warnHigh) return 'warn'
      return 'normal'
    }
    default:
      // 其他指标通过 metrics 通用方法评估
      return evaluateMetricStatus(wc, metricKey, val)
  }
}

/**
 * 通用指标状态评估
 */
function evaluateMetricStatus(waterConfig, metricId, value) {
  const m = findMetric(waterConfig, metricId)
  if (!m || m.enabled === false) return 'normal'
  if (value < m.criticalLow || value > m.criticalHigh) return 'critical'
  if (value < m.warnLow || value > m.warnHigh) return 'warn'
  return 'normal'
}

/**
 * 根据状态码返回显示标签和样式
 */
export function getStatusInfo(status) {
  const map = {
    normal: { label: '正常', cls: 'tag-emerald' },
    warn: { label: '预警', cls: 'tag-yellow' },
    critical: { label: '异常', cls: 'tag-red' }
  }
  return map[status] || map.normal
}
