/**
 * 水质指标阈值：卡片标红与表格着色共用同一套 range 规则。
 * range 形如 "6-10"、"0-0.5"。
 */

/**
 * @param {string} range
 * @returns {[number, number]|null}
 */
export function parseMetricRange(range) {
  if (range == null || range === '') return null
  const text = String(range).trim()
  // 支持 0-0.5 / 6-10；不支持带负号的复杂区间
  const m = text.match(/^(-?\d+(?:\.\d+)?)\s*-\s*(-?\d+(?:\.\d+)?)$/)
  if (!m) return null
  const min = parseFloat(m[1])
  const max = parseFloat(m[2])
  if (Number.isNaN(min) || Number.isNaN(max)) return null
  return [min, max]
}

/**
 * @param {*} raw
 * @param {string} range
 * @returns {'正常'|'超标'}
 */
export function evalMetricStatus(raw, range) {
  if (raw === undefined || raw === null || raw === '' || raw === '--') return '正常'
  const v = parseFloat(raw)
  if (Number.isNaN(v)) return '正常'
  const bounds = parseMetricRange(range)
  if (!bounds) return '正常'
  const [min, max] = bounds
  if (v < min || v > max) return '超标'
  return '正常'
}

/**
 * @param {Array<{key:string,range?:string}>} metrics
 * @param {object|null|undefined} row
 * @param {string} key
 * @returns {'正常'|'超标'}
 */
export function metricStatusByKey(metrics, row, key) {
  if (!row) return '正常'
  const metric = (metrics || []).find((m) => m.key === key)
  if (!metric) return '正常'
  return evalMetricStatus(row[key], metric.range)
}

/**
 * 表格单元格着色（与卡片 card-alert 同源判定）
 * @returns {object}
 */
export function metricCellStyle(metrics, row, key) {
  if (metricStatusByKey(metrics, row, key) === '超标') {
    return {
      color: '#ffecec',
      fontWeight: 700,
      background: 'rgba(255, 59, 59, 0.22)',
      boxShadow: 'inset 0 0 0 1px rgba(255, 80, 80, 0.65)'
    }
  }
  return {}
}
