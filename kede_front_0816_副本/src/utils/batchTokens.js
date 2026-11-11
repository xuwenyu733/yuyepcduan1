/**
 * 鱼池批次统一处理：
 * zk_pond.batch_number 可存单批次，也可逗号拼接多批次。
 * 筛选项一律拆成单个批次号；详情展示仍可用完整原串。
 */

const BATCH_SPLIT_RE = /[,，;；|/]+/

/** 将原始批次字段拆成去重后的单个批次号列表 */
export function splitBatchTokens(raw) {
  if (raw == null || raw === '') return []
  const seen = {}
  const out = []
  String(raw)
    .split(BATCH_SPLIT_RE)
    .forEach((part) => {
      const s = String(part || '').trim()
      if (!s || s.toLowerCase() === 'null' || s === '-' || s === '未知') return
      if (seen[s]) return
      seen[s] = true
      out.push(s)
    })
  return out
}

/** 从多行记录收集批次筛选项（已拆开、去重、排序） */
export function collectBatchFilterOptions(rows, getRaw) {
  const getter = typeof getRaw === 'function'
    ? getRaw
    : (row) => (row && (row.batchNumber || row.batch || row.relationOrderNo || row.batchNo)) || ''
  const seen = {}
  const out = []
  ;(rows || []).forEach((row) => {
    splitBatchTokens(getter(row)).forEach((b) => {
      if (seen[b]) return
      seen[b] = true
      out.push(b)
    })
  })
  return out.sort((a, b) => String(a).localeCompare(String(b), 'zh-CN'))
}

/** 行是否包含所选批次（混养池命中其中任一即可） */
export function rowMatchesBatchFilter(row, selectedBatch, getRaw) {
  if (selectedBatch == null || selectedBatch === '') return true
  const target = String(selectedBatch).trim()
  if (!target) return true
  const getter = typeof getRaw === 'function'
    ? getRaw
    : (r) => (r && (r.batchNumber || r.batch || r.relationOrderNo || r.batchNo)) || ''
  return splitBatchTokens(getter(row)).includes(target)
}
