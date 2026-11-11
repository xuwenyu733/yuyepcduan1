/**
 * 数据关联阈值 → 业务报警 / 巡检任务
 * 根据阈值「数据关联」分区启用规则，将超标事件推送到 AI 报警与任务计划
 */
import { getThresholdModule, resolveAssociationRules, resolveFeedingFcr, resolveHealthGrades } from '@/utils/thresholdConfig'
import { pushAlarmAndCreateTask } from '@/utils/alarmTaskBridge'
import { reportSilentError } from '@/utils/reportSilentError'

function matchTrigger(trigger, ctx) {
  const t = String(trigger || '')
  if (t.includes('健康评分') && ctx.healthScore != null) {
    const limit = Number(ctx.healthWatchMin != null ? ctx.healthWatchMin : 85)
    return Number(ctx.healthScore) < limit
  }
  if (t.includes('FCR') && ctx.fcr != null) {
    const max = Number(ctx.fcrMax != null ? ctx.fcrMax : 1.5)
    return Number(ctx.fcr) > max
  }
  if (t.includes('投喂偏差') && ctx.feedDevPct != null) {
    const max = Number(ctx.feedDevMax != null ? ctx.feedDevMax : 100)
    return Math.abs(Number(ctx.feedDevPct)) > max
  }
  if (t.includes('死亡率') && ctx.mortalityPct != null) {
    return Number(ctx.mortalityPct) > 0.1
  }
  if ((t.includes('溶解氧') || t.includes('DO')) && ctx.doValue != null) {
    if (t.includes('临界')) return Number(ctx.doValue) < 3
    return Number(ctx.doValue) < 4
  }
  if (t.includes('氨氮') && ctx.nh3 != null) {
    return Number(ctx.nh3) > (t.includes('危险') ? 0.4 : 0.2)
  }
  if (t.includes('亚硝酸') && ctx.no2 != null) {
    return Number(ctx.no2) > 0.5
  }
  return false
}

/**
 * @param {object} ctx 业务上下文：pondDetailNum / title / healthScore / fcr / feedDevPct 等
 * @returns {Promise<{ matched: number, pushed: number }>}
 */
export async function evaluateAssociationAndPush(ctx = {}) {
  let rules = []
  try {
    const [association, feeding, health] = await Promise.all([
      getThresholdModule('association'),
      getThresholdModule('feeding'),
      getThresholdModule('health')
    ])
    rules = resolveAssociationRules(association)
    const fcr = resolveFeedingFcr(feeding)
    const grades = resolveHealthGrades(health)
    ctx = {
      ...ctx,
      fcrMax: ctx.fcrMax != null ? ctx.fcrMax : fcr.max,
      healthWatchMin: ctx.healthWatchMin != null ? ctx.healthWatchMin : grades.watchMin
    }
  } catch (e) {
    reportSilentError('关联规则-阈值加载', e)
    return { matched: 0, pushed: 0, error: (e && e.message) || String(e) }
  }

  let matched = 0
  let pushed = 0
  let pushFail = 0
  for (const rule of rules) {
    if (!matchTrigger(rule.trigger, ctx)) continue
    matched += 1
    const pondNo = ctx.pondDetailNum || ctx.pondCode || ctx.pondName || ''
    const title = ctx.title || `关联规则触发：${rule.trigger}`
    try {
      const res = await pushAlarmAndCreateTask({
        source: 'association',
        category: ctx.category || 'water',
        pondDetailNum: pondNo,
        pondName: ctx.pondName || pondNo,
        workshopName: ctx.workshopName || '',
        pondSystemName: ctx.pondSystemName || '',
        title,
        content: `${rule.trigger} → ${rule.action || '联动处置'}`,
        taskTitle: `巡检：${pondNo || '全场'} ${rule.action || rule.trigger}`,
        level: 'urgent',
        period: 'day'
      })
      if (res && res.alarm && res.alarm.ok && !res.alarm.skipped) pushed += 1
      else if (res && res.alarm && !res.alarm.ok && !res.alarm.skipped) pushFail += 1
    } catch (e) {
      pushFail += 1
      console.error('[associationBridge] push failed', e && e.message)
    }
  }
  if (pushFail > 0) {
    reportSilentError('关联规则推送', new Error(`失败 ${pushFail} 条（匹配 ${matched}，成功 ${pushed}）`))
  }
  return { matched, pushed, pushFail }
}
