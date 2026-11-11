/**
 * 智能体对话：用户显式路由意图（与 Python route_hint 对齐）
 */

export const ROUTE_HINT_QUERY = 'query_analysis'
export const ROUTE_HINT_ENTRY = 'data_entry'

const QUERY_KEYS = [
  '多少', '有几个', '有多少', '统计', '汇总', '查询', '总共', '一共',
  '对比', '趋势', '排名', '环比', '同比', '占比',
  '为什么', '原因', '怎么回事', '怎么办',
]

const ENTRY_ACTION_KEYS = ['录入', '新增', '添加', '创建', '我要', '我想', '帮我', '请帮我', '准备']
const ENTRY_BUSINESS_KEYS = [
  '投放', '投喂', '喂料', '用药', '分池', '死亡', '死鱼', '死苗', '车间', '存塘', '修正', '鱼苗',
]

export function looksLikeQuerySemantics(text) {
  const t = String(text || '').trim()
  if (!t) return false
  if (QUERY_KEYS.some((k) => t.includes(k))) return true
  if (/一共.{0,6}(多少|几)/.test(t)) return true
  return false
}

export function looksLikeDataEntrySemantics(text) {
  const t = String(text || '').trim()
  if (!t) return false
  const hasBusiness = ENTRY_BUSINESS_KEYS.some((k) => t.includes(k))
  if (!hasBusiness) return false
  const hasAction = ENTRY_ACTION_KEYS.some((k) => t.includes(k))
    || ['现在投放', '去投放', '投放一下', '投喂一下'].some((k) => t.includes(k))
  if (hasAction && !looksLikeQuerySemantics(t)) return true
  if (/投放/.test(t) && /(?:数量|重量|规格|批次)\s*\d+/.test(t)) return true
  if (/(?:投喂|喂料)/.test(t) && (
    /\d+(\.\d+)?\s*(?:kg|KG|公斤|千克|斤|g|G|克)/.test(t)
    || /(?:投喂|喂料)\s*\d+/.test(t)
  )) return true
  return false
}

/**
 * 解析 route_hint，非法值回退为默认「查询分析」。
 */
export function normalizeRouteHint(hint) {
  if (hint === ROUTE_HINT_ENTRY) return ROUTE_HINT_ENTRY
  return ROUTE_HINT_QUERY
}

export function detectRouteHintConflict(routeHint, text) {
  const hint = normalizeRouteHint(routeHint)
  const t = String(text || '').trim()
  if (!t) return null
  const isQuery = looksLikeQuerySemantics(t)
  const isEntry = looksLikeDataEntrySemantics(t)

  if (hint === ROUTE_HINT_ENTRY && isQuery && !isEntry) {
    return {
      message: '您选择了「录入数据」，但内容像是在查询或分析。请确认按哪种方式处理？',
      confirmText: '按查询分析处理',
      cancelText: '仍按录入数据处理',
      confirmHint: ROUTE_HINT_QUERY,
      cancelHint: ROUTE_HINT_ENTRY,
    }
  }
  if (hint === ROUTE_HINT_QUERY && isEntry && !isQuery) {
    return {
      message: '您选择了「查询分析」，但内容像是在录入数据。请确认按哪种方式处理？',
      confirmText: '按录入数据处理',
      cancelText: '仍按查询分析处理',
      confirmHint: ROUTE_HINT_ENTRY,
      cancelHint: ROUTE_HINT_QUERY,
    }
  }
  if (hint === ROUTE_HINT_ENTRY && isQuery && isEntry) {
    return {
      message: '当前内容同时像「查询分析」也像「录入数据」，请确认本次如何处理？',
      confirmText: '按查询分析处理',
      cancelText: '仍按录入数据处理',
      confirmHint: ROUTE_HINT_QUERY,
      cancelHint: ROUTE_HINT_ENTRY,
    }
  }
  if (hint === ROUTE_HINT_QUERY && isQuery && isEntry) {
    return {
      message: '当前内容同时像「查询分析」也像「录入数据」，请确认本次如何处理？',
      confirmText: '按录入数据处理',
      cancelText: '仍按查询分析处理',
      confirmHint: ROUTE_HINT_ENTRY,
      cancelHint: ROUTE_HINT_QUERY,
    }
  }
  return null
}
