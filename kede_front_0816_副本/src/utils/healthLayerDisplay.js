/** 解析健康分层 JSON（字符串或对象） */
export function parseLayerInfo(raw) {
  if (raw == null || raw === '') return null
  if (typeof raw === 'object') return raw
  try {
    return JSON.parse(String(raw))
  } catch (e) {
    return null
  }
}

function trimNum(val) {
  if (val == null || val === '') return ''
  const n = Number(val)
  if (Number.isFinite(n)) return Number.isInteger(n) ? String(n) : n.toFixed(2).replace(/\.?0+$/, '')
  return String(val)
}

/** 环境层摘要：溶氧 · pH · 水温 … */
export function formatEnvironmentalSummary(raw) {
  const obj = parseLayerInfo(raw)
  if (!obj) return '—'
  const parts = []
  if (obj.doOxygen != null) parts.push(`溶氧 ${trimNum(obj.doOxygen)} mg/L`)
  if (obj.phPh != null) parts.push(`pH ${trimNum(obj.phPh)}`)
  if (obj.doTemp != null) parts.push(`水温 ${trimNum(obj.doTemp)}°C`)
  if (obj.ammoniaNitrogen != null) parts.push(`氨氮 ${trimNum(obj.ammoniaNitrogen)}`)
  if (obj.nitrite != null) parts.push(`亚硝酸盐 ${trimNum(obj.nitrite)}`)
  if (obj.nitrate != null) parts.push(`硝酸盐 ${trimNum(obj.nitrate)}`)
  return parts.length ? parts.join(' · ') : '—'
}

/** 行为层摘要 */
export function formatBehavioralSummary(raw) {
  const obj = parseLayerInfo(raw)
  if (!obj) return '—'
  const parts = []
  const food = obj.foodIntakeAmount || obj.foodSituation
  const swim = obj.swimming || obj.swimmingState
  const death = obj.death || obj.deadFishSituation
  if (food) parts.push(`摄食 ${food}`)
  if (swim) parts.push(`游泳 ${swim}`)
  if (obj.todayDeathCount != null) parts.push(`今日死亡 ${obj.todayDeathCount} 尾`)
  else if (death) parts.push(`死亡 ${death}`)
  return parts.length ? parts.join(' · ') : '—'
}

/** 机体层摘要 */
export function formatBodySummary(raw) {
  const obj = parseLayerInfo(raw)
  if (!obj) return '—'
  if (obj.itemScores && obj.itemScores.placeholder) return '暂无打样数据'
  const parts = []
  if (obj.liverAndGallbladder) parts.push(`肝胆 ${obj.liverAndGallbladder}`)
  if (obj.gillFilaments) parts.push(`鳃丝 ${obj.gillFilaments}`)
  if (obj.intestine) parts.push(`肠道 ${obj.intestine}`)
  if (obj.visceralNodule) parts.push(`内脏结节 ${obj.visceralNodule}`)
  if (obj.specificationAndBaitCoefficient) parts.push(`规格饵料 ${obj.specificationAndBaitCoefficient}`)
  return parts.length ? parts.join(' · ') : '—'
}
