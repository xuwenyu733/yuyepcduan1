/**
 * 阈值配置统一读取：优先 BFF，失败用内存缓存 / 空对象
 * 供精准投喂、健康评分、任务超时、设备运行等模块消费
 */
import { fetchAll, fetchModule } from '@/api/threshold'

const TTL_MS = 60 * 1000
let _cache = null
let _cachedAt = 0
let _loading = null

export async function getAllThresholds(force = false) {
  const now = Date.now()
  if (!force && _cache && now - _cachedAt < TTL_MS) return _cache
  if (!force && _loading) return _loading

  _loading = (async () => {
    try {
      const res = await fetchAll()
      const data = (res && res.data) || res || {}
      _cache = data
      _cachedAt = Date.now()
      return data
    } catch (e) {
      console.warn('[thresholdConfig] fetchAll failed', e && e.message)
      return _cache || {}
    } finally {
      _loading = null
    }
  })()

  return _loading
}

export async function getThresholdModule(moduleId, force = false) {
  if (!moduleId) return null
  try {
    if (!force && _cache && _cache[moduleId]) return _cache[moduleId]
    if (force) {
      const res = await fetchModule(moduleId)
      const data = (res && res.data) || null
      if (data && _cache) {
        _cache[moduleId] = data
        _cachedAt = Date.now()
      }
      return data
    }
    const all = await getAllThresholds(false)
    return (all && all[moduleId]) || null
  } catch (e) {
    console.warn('[thresholdConfig] getModule failed', moduleId, e && e.message)
    return (_cache && _cache[moduleId]) || null
  }
}

/** 精准投喂：FCR 目标区间 */
export function resolveFeedingFcr(feeding) {
  const fcr = feeding && feeding.fcr
  if (!fcr) return { min: 1.2, max: 1.5, target: 1.35 }
  return {
    min: Number(fcr.min) || 1.2,
    max: Number(fcr.max) || 1.5,
    target: Number(fcr.target) || 1.35
  }
}

/**
 * 健康评分分界（对齐阈值「健康评分」分区）
 * excellentMin: 优秀下限；watchMin: 关注/高风险分界；低于 watchMin 为关注
 */
export function resolveHealthGrades(health) {
  const grades = (health && Array.isArray(health.grades)) ? health.grades : []
  let excellentMin = 85
  let watchMin = 70
  let hasHighRisk = false
  grades.forEach(g => {
    if (!g) return
    const label = String(g.label || '')
    if (label.includes('优秀') && g.min != null) excellentMin = Number(g.min)
    if (label.includes('高风险')) {
      hasHighRisk = true
      if (g.max != null) watchMin = Number(g.max) + 1
      else if (g.min != null) watchMin = Number(g.min)
    }
  })
  if (!hasHighRisk) {
    grades.forEach(g => {
      if (!g) return
      const label = String(g.label || '')
      if ((label.includes('关注') || label.includes('良好')) && g.min != null) {
        watchMin = Number(g.min)
      }
    })
  }
  if (!Number.isFinite(excellentMin)) excellentMin = 85
  if (!Number.isFinite(watchMin)) watchMin = 70
  if (watchMin > excellentMin) watchMin = Math.max(0, excellentMin - 1)
  return { excellentMin, watchMin, attentionMax: watchMin - 1 }
}

/**
 * 健康三层权重（对齐阈值「分层评分权重」）
 * 默认 外部环境20 + 外观行为60 + 内部肌体20
 */
export function resolveHealthWeights(health) {
  const layers = (health && Array.isArray(health.layers)) ? health.layers : []
  const pick = (id, fallback) => {
    const row = layers.find(x => x && x.id === id)
    const n = row && row.enabled !== false ? Number(row.full) : fallback
    return Number.isFinite(n) && n > 0 ? n : fallback
  }
  const env = pick('env', 20)
  const appearance = pick('appearance', 60)
  const internal = pick('internal', 20)
  const sum = env + appearance + internal
  return {
    env,
    appearance,
    internal,
    sum: sum || 100,
    envWarn: (() => {
      const row = layers.find(x => x && x.id === 'env')
      return row && row.warn != null ? Number(row.warn) : Math.round(env * 0.8)
    })(),
    appearanceWarn: (() => {
      const row = layers.find(x => x && x.id === 'appearance')
      return row && row.warn != null ? Number(row.warn) : Math.round(appearance * 0.8)
    })(),
    internalWarn: (() => {
      const row = layers.find(x => x && x.id === 'internal')
      return row && row.warn != null ? Number(row.warn) : Math.round(internal * 0.8)
    })()
  }
}

/**
 * 按阈值权重重算综合分：层得分先归一到原满分比例，再映射到新权重
 * @param {{ envScore?: number, appScore?: number, intScore?: number, baseEnv?: number, baseApp?: number, baseInt?: number }} scores
 * @param {{ env: number, appearance: number, internal: number }} weights
 */
export function recomputeHealthOverall(scores, weights) {
  const w = weights || { env: 20, appearance: 60, internal: 20 }
  const baseEnv = Number(scores && scores.baseEnv) || 20
  const baseApp = Number(scores && scores.baseApp) || 60
  const baseInt = Number(scores && scores.baseInt) || 20
  const envScore = Number(scores && scores.envScore) || 0
  const appScore = Number(scores && scores.appScore) || 0
  const intScore = Number(scores && scores.intScore) || 0
  const env = (envScore / baseEnv) * w.env
  const app = (appScore / baseApp) * w.appearance
  const inn = (intScore / baseInt) * w.internal
  const overall = Math.round((env + app + inn) * 10) / 10
  return {
    overall: Math.max(0, Math.min(100, overall)),
    envScaled: Math.round(env * 10) / 10,
    appScaled: Math.round(app * 10) / 10,
    intScaled: Math.round(inn * 10) / 10
  }
}

/**
 * 任务超时：按优先级返回宽限小时数
 * @returns {{ urgent: number, priority: number, routine: number }}
 */
export function resolveTimeoutHours(timeout) {
  const defaults = { urgent: 0, priority: 0, routine: 0 }
  const list = timeout && Array.isArray(timeout.overdue) ? timeout.overdue : []
  list.forEach(item => {
    if (!item) return
    const level = String(item.level || '')
    const hours = Number(item.hours)
    if (!Number.isFinite(hours)) return
    if (level.includes('紧急')) defaults.urgent = hours
    else if (level.includes('优先')) defaults.priority = hours
    else if (level.includes('日常')) defaults.routine = hours
  })
  return defaults
}

/** AI 报警：各级别处理时限（分钟）与密集报警规则 */
export function resolveAlarmRules(alarm) {
  const overdue = { urgent: 15, priority: 60, routine: 240 }
  const list = alarm && Array.isArray(alarm.overdue) ? alarm.overdue : []
  list.forEach(item => {
    if (!item) return
    const level = String(item.level || '')
    const minutes = Number(item.minutes)
    if (!Number.isFinite(minutes)) return
    if (level.includes('紧急')) overdue.urgent = minutes
    else if (level.includes('优先')) overdue.priority = minutes
    else if (level.includes('日常')) overdue.routine = minutes
  })
  const dense = alarm && alarm.dense ? alarm.dense : {}
  const multi = alarm && alarm.multi ? alarm.multi : {}
  return {
    overdue,
    dense: {
      count: Number(dense.count) || 5,
      window: Number(dense.window) || 30,
      enabled: dense.enabled !== false
    },
    multi: {
      value: Number(multi.value) || 3,
      enabled: multi.enabled !== false
    }
  }
}

/** 生长模型：实测相对预测偏差、日增重/SGR 下限 */
export function resolveGrowthThreshold(feeding) {
  const g = feeding && feeding.growth
  return {
    modelDevMax: Number(g && g.modelDevMax) || 15,
    dailyGainMin: Number(g && g.dailyGainMin) || 0.5,
    sgrMin: Number(g && g.sgrMin) || 0.8
  }
}

/** 精准投喂：投喂偏差告警阈值（%） */
export function resolveFeedingDev(feeding) {
  const fd = feeding && feeding.feedDev
  return {
    value: Number(fd && fd.value) || 100,
    enabled: !fd || fd.enabled !== false,
    unit: (fd && fd.unit) || '%'
  }
}

/** 实验系统：增重/成活/对照偏离阈值 */
export function resolveExperimentThresholds(experiment) {
  const items = experiment && Array.isArray(experiment.items) ? experiment.items : []
  const find = id => items.find(i => i && i.id === id) || {}
  const growth = find('growth')
  const survive = find('survive')
  const control = find('control')
  return {
    growthMax: Number(growth.value) || 20,
    surviveMin: Number(survive.value) || 80,
    controlMax: Number(control.value) || 15,
    enabled: !experiment || experiment.enabled !== false
  }
}

/** 数据关联：启用的联动规则 */
export function resolveAssociationRules(association) {
  const rules = association && Array.isArray(association.rules) ? association.rules : []
  return rules.filter(r => r && r.enabled !== false)
}

/** 设备运行：扁平化全部启用指标 */
export function resolveDeviceMetrics(device) {
  const sections = ['power', 'pipeline', 'electric', 'levels', 'maintain']
  const list = []
  sections.forEach(key => {
    const arr = device && Array.isArray(device[key]) ? device[key] : []
    arr.forEach(m => {
      if (m && m.enabled !== false) list.push({ ...m, section: key })
    })
  })
  return list
}

export function invalidateThresholdCache() {
  _cache = null
  _cachedAt = 0
}
