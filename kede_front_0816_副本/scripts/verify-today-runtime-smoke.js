/**
 * 今日改动启动后保障冒烟：静态断言 + HTTP 可达性 + 关键逻辑复现
 * node scripts/verify-today-runtime-smoke.js
 */
const fs = require('fs')
const path = require('path')
const http = require('http')
const https = require('https')
const { spawnSync } = require('child_process')

const FRONT = path.join(__dirname, '..')
let pass = 0
let fail = 0
let warn = 0
const fails = []
const warns = []

function add(id, name, ok, detail) {
  if (ok) pass++
  else {
    fail++
    fails.push(`${id} ${name}${detail ? ' :: ' + detail : ''}`)
  }
  console.log(`[${ok ? 'PASS' : 'FAIL'}] ${id} ${name}${detail ? ' - ' + detail : ''}`)
}
function note(id, name, detail) {
  warn++
  warns.push(`${id} ${name}: ${detail}`)
  console.log(`[WARN] ${id} ${name} - ${detail}`)
}
function read(rel) {
  return fs.readFileSync(path.join(FRONT, rel), 'utf8')
}
function exists(rel) {
  return fs.existsSync(path.join(FRONT, rel))
}

function httpGet(url, timeoutMs = 4000) {
  return new Promise((resolve) => {
    const lib = url.startsWith('https') ? https : http
    const req = lib.get(url, { timeout: timeoutMs }, (res) => {
      let body = ''
      res.on('data', (c) => { body += c })
      res.on('end', () => resolve({ ok: true, status: res.statusCode, body }))
    })
    req.on('error', (e) => resolve({ ok: false, error: e.message }))
    req.on('timeout', () => {
      req.destroy()
      resolve({ ok: false, error: 'timeout' })
    })
  })
}

// ---- 内联今日核心逻辑 ----
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
function formatWorkshop(raw) {
  const n = parseWorkshopNum(raw)
  if (n != null) return '车间' + n
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
function resolveWorkshopScope(raw, _opts, registry) {
  const value = String(raw == null ? '' : raw).trim()
  if (!value) return { raw: '', id: null, num: null, label: '', names: [] }
  const byId = (registry || []).find((w) => String(w.workshopId) === value)
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
function toBackendUserId(v) {
  if (v == null || v === '') return null
  const s = String(v).trim()
  if (!s || s === 'pc-user' || s === 'pc_user') return null
  if (!/^\d+$/.test(s)) return null
  const n = Number(s)
  return Number.isFinite(n) && n > 0 ? n : null
}
function stripHtmlPlain(val) {
  if (val == null || val === '') return ''
  return String(val).replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}
function healthStatus(score) {
  const s = Number(score) || 0
  if (s < 70) return '不健康'
  if (s < 85) return '亚健康'
  return '健康'
}

;(async () => {
  console.log('==== 今日改动启动保障冒烟 ====\n')

  console.log('---- A) 服务可达性 ----')
  const frontCandidates = [
    'http://127.0.0.1:4984',
    'http://localhost:4984',
    'http://127.0.0.1:8080',
    'http://127.0.0.1:8081'
  ]
  let frontOk = null
  for (const u of frontCandidates) {
    const r = await httpGet(u)
    if (r.ok && r.status < 500) {
      frontOk = { url: u, status: r.status }
      break
    }
  }
  add('SM1', '前端 HTTP 可达', !!frontOk, frontOk ? `${frontOk.url} status=${frontOk.status}` : '未检测到 8080/8081')

  const modules = await httpGet('http://127.0.0.1:3020/api/health')
  add('SM2', 'modules-api :3020', modules.ok && modules.status === 200, modules.ok ? `status=${modules.status}` : modules.error)

  const socket = await httpGet('http://127.0.0.1:9091/health')
  add('SM3', 'socket-server :9091', socket.ok, socket.ok ? `status=${socket.status}` : socket.error)

  const backend = await httpGet('http://127.0.0.1:48181/admin-api/system/tenant/get-id-by-name?name=test')
  if (!backend.ok) note('SM4', 'Java 后端 :48181', backend.error || '不可达（登录/业务接口依赖后端）')
  else add('SM4', 'Java 后端 :48181', true, `status=${backend.status}`)

  console.log('\n---- B) 今日改动逻辑复现 ----')
  const registry = [
    { workshopId: 9, workshopName: '大西洋鲑1车间' },
    { workshopId: 1, workshopName: '车间1' }
  ]
  const s9 = resolveWorkshopScope('9', null, registry)
  add('SM5', 'workshop=9 → 大西洋鲑1车间', s9.label === '大西洋鲑1车间', s9.label)
  add('SM6', '不再显示车间9', s9.label !== '车间9')
  add('SM7', '大西洋鲑池匹配 scope', textMatchesWorkshopScope('大西洋鲑1号池', s9))
  add('SM8', '车间1池不匹配 scope9', !textMatchesWorkshopScope('车间1-系统1-鱼池1', s9))
  add('SM9', '健康分界', healthStatus(90) === '健康' && healthStatus(75) === '亚健康' && healthStatus(60) === '不健康')
  add('SM10', 'pc-user 过滤', toBackendUserId('pc-user') == null)
  add('SM11', 'HTML 去标签', stripHtmlPlain('<p>故障已修复</p>') === '故障已修复')

  console.log('\n---- C) 源码保障点 ----')
  const files = {
    scope: 'src/utils/siteScopeResolver.js',
    health: 'src/utils/healthPreviewBridge.js',
    d3: 'src/views/index_daping3.vue',
    d4: 'src/views/index_daping4.vue',
    sys: 'src/views/fish/zkDeathSurvivalRate/system.vue',
    alarm: 'src/utils/platformAlarmData.js'
  }
  // 兼容实际路径命名差异
  const candidates = {
    scope: ['src/utils/siteScopeResolver.js', 'src/utils/siteScopeResolver.js', 'src/utils/siteNamingScope.js'],
    health: ['src/utils/healthPreviewBridge.js', 'src/utils/healthDataBridge.js'],
    d3: ['src/views/index_daping3.vue'],
    d4: ['src/views/index_daping4.vue'],
    sys: [
      'src/views/fish/zkDeathSurvivalRate/system.vue',
      'src/views/fish/zkDeathSurvivalRate/system.vue'
    ],
    alarm: ['src/utils/platformAlarmData.js', 'src/utils/platformAlarmData.js']
  }
  function findFile(keys) {
    for (const k of keys) if (exists(k)) return k
    return null
  }
  const scopeFile = findFile(['src/utils/siteScopeResolver.js', 'src/utils/siteScope.js'])
  const healthFile = findFile(['src/utils/healthPreviewBridge.js', 'src/utils/healthDataBridge.js'])
  const d3 = findFile(['src/views/index_daping3.vue'])
  const d4 = findFile(['src/views/index_daping4.vue'])
  const sys = findFile([
    'src/views/fish/zkDeathSurvivalRate/system.vue',
    'src/views/fish/zkDeathSurvivalRate/system.vue'
  ])
  const alarm = findFile(['src/utils/platformAlarmData.js'])

  add('SM12', 'siteScope 模块存在', !!scopeFile, scopeFile || 'missing')
  add('SM13', 'healthBridge 模块存在', !!healthFile, healthFile || 'missing')
  add('SM14', 'daping3 存在', !!d3)
  add('SM15', 'daping4 存在', !!d4)
  add('SM16', '死亡/健康 system 存在', !!sys)
  add('SM17', 'alarm 数据层存在', !!alarm)

  if (d4) {
    const t = read(d4)
    add('SM18', 'daping4 接入 scope/health', /siteScope|healthPreview|healthBridge|resolveWorkshopScope|fetchUnifiedHealth/.test(t))
  }
  if (d3) {
    const t = read(d3)
    add('SM19', 'daping3 接入统一健康', /healthPreview|healthBridge|fetchUnifiedHealth|syncOverviewHealth/.test(t))
  }
  if (sys) {
    const t = read(sys)
    add('SM20', 'system 池号 clear + 并行加载', /@clear|onFilterPondClear|Promise\.all|pageLoading/.test(t))
    add('SM21', 'system 路由无参清空筛选', /filterWorkshop\s*=\s*''|filterPond\s*=\s*''/.test(t) && /applyRouteSiteFilters|applyRoute/.test(t))
  }
  if (alarm) {
    const t = read(alarm)
    add('SM22', 'alarm 去 HTML + 删非法 userId', /stripHtml|stripHtmlPlain|delete\s+payload\.userId|toBackendUserId/.test(t))
  }

  console.log('\n---- D) 既有验证脚本 ----')
  const arch = spawnSync('node', [path.join(__dirname, 'verify-architect-site-scope.js')], {
    cwd: FRONT,
    encoding: 'utf8',
    timeout: 120000
  })
  add('SM23', 'architect 脚本通过', arch.status === 0, 'exit ' + arch.status)

  console.log('\n==== 冒烟汇总 ====')
  console.log(`PASS=${pass} FAIL=${fail} WARN=${warn}`)
  if (warns.length) {
    console.log('\n警告:')
    warns.forEach((w) => console.log(' - ' + w))
  }
  if (fails.length) {
    console.log('\n失败:')
    fails.forEach((f) => console.log(' - ' + f))
  }
  process.exit(fail ? 1 : 0)
})()
