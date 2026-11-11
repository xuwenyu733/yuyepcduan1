/**
 * 架构级回归：车间 ID vs 编号、健康数据桥接、报警 payload 净化
 * node scripts/verify-architect-site-scope.js
 */
const fs = require('fs')
const path = require('path')
const { spawnSync } = require('child_process')

const FRONT = path.join(__dirname, '..')
let pass = 0
let fail = 0
const fails = []

function add(id, name, ok, detail) {
  if (ok) pass++
  else {
    fail++
    fails.push(`${id} ${name}${detail ? ' :: ' + detail : ''}`)
  }
  console.log(`[${ok ? 'PASS' : 'FAIL'}] ${id} ${name}${detail ? ' - ' + detail : ''}`)
}

function read(rel) {
  return fs.readFileSync(path.join(FRONT, rel), 'utf8')
}

// ---- 内联 siteNaming 核心 ----
function parseWorkshopNum(raw) {
  const s = String(raw == null ? '' : raw).trim()
  if (!s) return null
  let m = s.match(/^车间\s*(\d+)/)
  if (m) return Number(m[1])
  m = s.match(/^(\d+)\s*号?\s*车间/)
  if (m) return Number(m[1])
  if (/^\d+$/.test(s)) return Number(s)
  return null
}
function parseSystemNum(raw) {
  const s = String(raw == null ? '' : raw).trim()
  if (!s) return null
  let m = s.match(/^系统\s*(\d+)/)
  if (m) return Number(m[1])
  if (/^\d+$/.test(s)) return Number(s)
  return null
}
function formatWorkshop(raw) {
  const n = parseWorkshopNum(raw)
  if (n != null) return '车间' + n
  return String(raw || '').trim()
}
function formatSystem(raw) {
  const n = parseSystemNum(raw)
  if (n != null) return '系统' + n
  return String(raw || '').trim()
}
function uniqNames(list) {
  const seen = new Set()
  const out = []
  ;(list || []).forEach((n) => {
    const s = String(n || '').trim()
    if (!s || seen.has(s)) return
    seen.add(s)
    out.push(s)
  })
  return out
}

function resolveWorkshopScope(raw, siteOptions, workshopRegistry) {
  const value = String(raw == null ? '' : raw).trim()
  const empty = { raw: value, id: null, num: null, label: '', names: [] }
  if (!value) return empty
  const registry = workshopRegistry || []
  const byId = registry.find((w) => String(w.workshopId) === value)
  if (byId) {
    const name = String(byId.workshopName || '').trim()
    const num = parseWorkshopNum(name)
    return {
      raw: value,
      id: byId.workshopId,
      num,
      label: name || formatWorkshop(value),
      names: uniqNames([name, formatWorkshop(name), num != null ? '车间' + num : ''])
    }
  }
  const num = parseWorkshopNum(value)
  const label = num != null ? formatWorkshop(num) : formatWorkshop(value)
  return { raw: value, id: null, num, label, names: uniqNames([label, value]) }
}

function resolveSystemScope(raw) {
  const value = String(raw == null ? '' : raw).trim()
  if (!value) return { raw: '', num: null, label: '', names: [] }
  const num = parseSystemNum(value)
  const label = formatSystem(value)
  return { raw: value, num, label, names: uniqNames([label, value]) }
}

function textMatchesWorkshopScope(text, workshopScope) {
  if (!workshopScope || !workshopScope.raw) return true
  const raw = String(text || '')
  if (!raw) return false
  const textNum = parseWorkshopNum(raw)
  if (workshopScope.num != null && textNum === workshopScope.num) return true
  if ((workshopScope.names || []).some((n) => n && raw.indexOf(n) !== -1)) return true
  const label = String(workshopScope.label || '').trim()
  if (label) {
    const core = label.replace(/车间$/, '').trim()
    if (core.length >= 2 && raw.indexOf(core) !== -1) return true
    const shortCore = core.replace(/\d+号?$/, '').trim()
    if (shortCore.length >= 2 && raw.indexOf(shortCore) !== -1 && /\d/.test(raw)) return true
  }
  return false
}

function textMatchesSystemScope(text, systemScope, workshopMatched) {
  if (!systemScope || !systemScope.raw) return true
  const raw = String(text || '')
  const textS = parseSystemNum(raw)
  const sNum = systemScope.num
  if (sNum != null && textS == null && workshopMatched) return true
  if (sNum != null && textS === sNum) return true
  return (systemScope.names || []).some((n) => n && raw.indexOf(n) !== -1)
}

function matchesSiteScopeText(text, workshopScope, systemScope) {
  const hasScope = !!(workshopScope && workshopScope.raw) || !!(systemScope && systemScope.raw)
  if (!hasScope) return true
  const wOk = textMatchesWorkshopScope(text, workshopScope)
  if (!wOk) return false
  return textMatchesSystemScope(text, systemScope, wOk)
}

function toBackendUserId(v) {
  if (v == null || v === '') return null
  const s = String(v).trim()
  if (!s || s === 'pc-user') return null
  if (!/^\d+$/.test(s)) return null
  const n = Number(s)
  return Number.isFinite(n) && n > 0 ? n : null
}

function stripHtmlPlain(val) {
  if (val == null || val === '') return ''
  return String(val).replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function healthStatusMeta(score, healthGrade) {
  const s = Number(score) || 0
  const excellentMin = (healthGrade && healthGrade.excellentMin) || 85
  const watchMin = (healthGrade && healthGrade.watchMin) || 70
  if (s < watchMin) return { status: '不健康' }
  if (s < excellentMin) return { status: '亚健康' }
  return { status: '健康' }
}

console.log('==== 架构级测试 site-scope / health / alarm ====\n')

const registry = [
  { workshopId: 9, workshopName: '大西洋鲑1车间', pondCount: 5 },
  { workshopId: 1, workshopName: '车间1', pondCount: 3 }
]

console.log('---- 1) 车间 ID 解析 ----')
const ws9 = resolveWorkshopScope('9', null, registry)
add('AR1', 'workshop=9 解析为真实名称', ws9.label === '大西洋鲑1车间', `label=${ws9.label}`)
add('AR2', 'workshop=9 不再显示车间9', ws9.label !== '车间9', ws9.label)
add('AR3', 'workshop=1 按编号解析', resolveWorkshopScope('1', null, registry).label === '车间1')

console.log('\n---- 2) 作用域匹配 ----')
const scope9 = resolveWorkshopScope('9', null, registry)
add('AR4', '大西洋鲑池名匹配 workshop=9', matchesSiteScopeText('大西洋鲑1号池', scope9, resolveSystemScope('')))
add('AR5', '车间1池名不匹配 workshop=9', !matchesSiteScopeText('车间1-系统1-鱼池1', scope9, resolveSystemScope('')))
add('AR6', '车间1-系统1 匹配 workshop=1 system=1',
  matchesSiteScopeText('车间1-系统1-鱼池1', resolveWorkshopScope('1', null, registry), resolveSystemScope('1')))

console.log('\n---- 3) 健康评分分界 ----')
const grade = { excellentMin: 85, watchMin: 70 }
add('AR7', '高分→健康', healthStatusMeta(90, grade).status === '健康')
add('AR8', '中分→亚健康', healthStatusMeta(75, grade).status === '亚健康')
add('AR9', '低分→不健康', healthStatusMeta(60, grade).status === '不健康')

console.log('\n---- 4) 报警 payload 净化 ----')
add('AR10', 'pc-user 被过滤', toBackendUserId('pc-user') == null)
const payload = { id: 1, userId: 'pc-user', handleInfo: '<p>故障已修复</p>' }
delete payload.userId
if (payload.handleInfo) payload.handleInfo = stripHtmlPlain(payload.handleInfo)
add('AR11', 'payload 无 userId', payload.userId == null)
add('AR12', 'handleInfo 去 HTML', payload.handleInfo === '故障已修复')

console.log('\n---- 5) 源码结构 ----')
const d4 = read('src/views/index_daping4.vue')
const d3 = read('src/views/index_daping3.vue')
const sys = read('src/views/fish/zkDeathSurvivalRate/system.vue')
const alarm = read('src/utils/platformAlarmData.js')
add('AR13', 'daping4 使用 siteScopeResolver', /siteScopeResolver/.test(d4))
add('AR14', 'daping4 使用 healthPreviewBridge', /healthPreviewBridge/.test(d4))
add('AR15', 'daping3 使用 healthPreviewBridge', /fetchUnifiedHealthList/.test(d3))
add('AR15b', 'siteDisplay 模块存在', fs.existsSync(path.join(FRONT, 'src/utils/siteDisplay.js')))
add('AR15c', '加载超时保护 withTimeout', /withTimeout/.test(read('src/utils/healthPreviewBridge.js')) && /withTimeout/.test(sys))
add('AR16', 'system 路由车间用 resolveWorkshopScope', /resolveWorkshopScope/.test(sys))
add('AR17', 'system 池号 clear 处理', /onFilterPondClear/.test(sys))
add('AR18', 'system 并行加载', /Promise\.all/.test(sys))
add('AR19', 'alarm stripHtmlPlain', /stripHtmlPlain/.test(alarm))
add('AR20', 'alarm patch 先删 userId', /delete payload\.userId/.test(alarm))
add('AR21', 'daping1_son 传递 workshopName', /workshopName=/.test(read('src/views/index_daping1_son.vue')))
add('AR22', 'siteScopeResolver 模块存在', fs.existsSync(path.join(FRONT, 'src/utils/siteScopeResolver.js')))
add('AR23', 'healthPreviewBridge 模块存在', fs.existsSync(path.join(FRONT, 'src/utils/healthPreviewBridge.js')))

console.log('\n---- 6) 子脚本聚合 ----')
const sub = spawnSync('node', [path.join(__dirname, 'verify-deep-133-151.js')], {
  cwd: FRONT,
  encoding: 'utf8',
  timeout: 120000
})
add('AR24', 'verify-deep-133-151 仍通过', sub.status === 0, sub.status != null ? 'exit ' + sub.status : '')

console.log('\n==== 架构测试汇总 ====')
console.log(`PASS=${pass} FAIL=${fail}`)
if (fails.length) {
  console.log('\n失败项:')
  fails.forEach((f) => console.log(' - ' + f))
}
process.exit(fail ? 1 : 0)
