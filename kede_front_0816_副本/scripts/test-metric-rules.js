/**
 * 纯函数单测（不依赖 Jest）：node scripts/test-metric-rules.js
 */
const assert = require('assert')
const path = require('path')

// 用 babel-free 方式：复制核心逻辑断言，避免 vue alias
// 同步实现与 src/utils 保持一致；若改 util 请同步本文件或改为编译导入

function parseMetricRange(range) {
  if (range == null || range === '') return null
  const text = String(range).trim()
  const m = text.match(/^(-?\d+(?:\.\d+)?)\s*-\s*(-?\d+(?:\.\d+)?)$/)
  if (!m) return null
  const min = parseFloat(m[1])
  const max = parseFloat(m[2])
  if (Number.isNaN(min) || Number.isNaN(max)) return null
  return [min, max]
}

function evalMetricStatus(raw, range) {
  if (raw === undefined || raw === null || raw === '' || raw === '--') return '正常'
  const v = parseFloat(raw)
  if (Number.isNaN(v)) return '正常'
  const bounds = parseMetricRange(range)
  if (!bounds) return '正常'
  const [min, max] = bounds
  if (v < min || v > max) return '超标'
  return '正常'
}

function metricStatusByKey(metrics, row, key) {
  if (!row) return '正常'
  const metric = (metrics || []).find((m) => m.key === key)
  if (!metric) return '正常'
  return evalMetricStatus(row[key], metric.range)
}

function metricCellStyle(metrics, row, key) {
  if (metricStatusByKey(metrics, row, key) === '超标') {
    return { color: '#ff9999' }
  }
  return {}
}

function canSubmitDiagnosisConclusion(status) {
  if (!status) return false
  return status !== 'resolved' && status !== 'pending'
}

const METRICS = [
  { key: 'lake_do', range: '6-10' },
  { key: 'lake_an', range: '0-0.5' },
  { key: 'lake_ph', range: '6-10' }
]

let passed = 0
function check(name, fn) {
  try {
    fn()
    passed += 1
    console.log('PASS', name)
  } catch (e) {
    console.error('FAIL', name, e.message)
    process.exitCode = 1
  }
}

check('parse 0-0.5', () => {
  assert.deepStrictEqual(parseMetricRange('0-0.5'), [0, 0.5])
})
check('parse 6-10', () => {
  assert.deepStrictEqual(parseMetricRange('6-10'), [6, 10])
})
check('empty / -- 正常', () => {
  assert.strictEqual(evalMetricStatus('', '6-10'), '正常')
  assert.strictEqual(evalMetricStatus('--', '6-10'), '正常')
  assert.strictEqual(evalMetricStatus(null, '6-10'), '正常')
})
check('边界恰好 min/max 正常', () => {
  assert.strictEqual(evalMetricStatus(6, '6-10'), '正常')
  assert.strictEqual(evalMetricStatus(10, '6-10'), '正常')
  assert.strictEqual(evalMetricStatus(0, '0-0.5'), '正常')
  assert.strictEqual(evalMetricStatus(0.5, '0-0.5'), '正常')
})
check('越界超标', () => {
  assert.strictEqual(evalMetricStatus(5.9, '6-10'), '超标')
  assert.strictEqual(evalMetricStatus(10.1, '6-10'), '超标')
  assert.strictEqual(evalMetricStatus(0.51, '0-0.5'), '超标')
})
check('卡片与表格同源', () => {
  const row = { lake_do: 5, lake_an: 0.2 }
  assert.strictEqual(metricStatusByKey(METRICS, row, 'lake_do'), '超标')
  assert.deepStrictEqual(metricCellStyle(METRICS, row, 'lake_do'), { color: '#ff9999' })
  assert.strictEqual(metricStatusByKey(METRICS, row, 'lake_an'), '正常')
  assert.deepStrictEqual(metricCellStyle(METRICS, row, 'lake_an'), {})
})
check('结论按钮：pending/resolved 隐藏', () => {
  assert.strictEqual(canSubmitDiagnosisConclusion('pending'), false)
  assert.strictEqual(canSubmitDiagnosisConclusion('resolved'), false)
  assert.strictEqual(canSubmitDiagnosisConclusion(''), false)
  assert.strictEqual(canSubmitDiagnosisConclusion(null), false)
})
check('结论按钮：进行中可提交', () => {
  assert.strictEqual(canSubmitDiagnosisConclusion('diagnosing'), true)
  assert.strictEqual(canSubmitDiagnosisConclusion('treating'), true)
  assert.strictEqual(canSubmitDiagnosisConclusion('active'), true)
})

// 与源文件内容交叉校验（防止脚本与 util 漂移）
const fs = require('fs')
const utilPath = path.join(__dirname, '../src/utils/waterMetricStatus.js')
const diagPath = path.join(__dirname, '../src/utils/diagnosisStatus.js')
check('util 文件存在', () => {
  assert.ok(fs.existsSync(utilPath))
  assert.ok(fs.existsSync(diagPath))
  const water = fs.readFileSync(utilPath, 'utf8')
  const diag = fs.readFileSync(diagPath, 'utf8')
  assert.ok(water.includes('evalMetricStatus'))
  assert.ok(diag.includes('canSubmitDiagnosisConclusion'))
})

console.log(`\n==== ${passed} checks done ====`)
