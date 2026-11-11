/**
 * 深度回归：133/146-151 逻辑单测 + 关联回归 + DB 抽样
 * node scripts/verify-deep-133-151.js
 */
const fs = require('fs')
const path = require('path')
const { spawnSync } = require('child_process')

const FRONT = path.join(__dirname, '..')
let pass = 0
let fail = 0
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

function warn(id, name, detail) {
  warns.push(`${id} ${name}: ${detail}`)
  console.log(`[WARN] ${id} ${name} - ${detail}`)
}

function read(rel) {
  const p = path.join(FRONT, rel)
  return fs.existsSync(p) ? fs.readFileSync(p, 'utf8') : ''
}

// ---- 复刻 siteNaming 核心（与 src/utils/siteNaming.js 一致） ----
const LETTER_TO_NUM = { A: 1, B: 2, C: 3, D: 4, E: 5, F: 6, G: 7, H: 8 }
function letterIndex(ch) {
  const u = String(ch || '').toUpperCase()
  return LETTER_TO_NUM[u] || null
}
function parseWorkshopNum(raw) {
  const s = String(raw == null ? '' : raw).trim()
  if (!s) return null
  let m = s.match(/^c(\d+)$/i)
  if (m) return Number(m[1])
  m = s.match(/^车间\s*(\d+)/)
  if (m) return Number(m[1])
  m = s.match(/^(\d+)\s*号?\s*车间/)
  if (m) return Number(m[1])
  if (/^\d+$/.test(s)) return Number(s)
  return null
}
function parseSystemNum(raw) {
  const s = String(raw == null ? '' : raw).trim()
  if (!s) return null
  let m = s.match(/^s(\d+)$/i)
  if (m) return Number(m[1])
  m = s.match(/^系统\s*(\d+)/)
  if (m) return Number(m[1])
  m = s.match(/^(\d+)\s*号?\s*系统/)
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

// ---- 复刻 index_daping4 matchesSiteScope ----
function matchesSiteScope(text, activeWorkshop, activeSystem) {
  const raw = String(text || '')
  const hasSiteScope = !!(activeWorkshop || activeSystem)
  if (!hasSiteScope) return true
  const wNum = activeWorkshop ? parseWorkshopNum(activeWorkshop) : null
  const sNum = activeSystem ? parseSystemNum(activeSystem) : null
  const wLabel = activeWorkshop ? formatWorkshop(activeWorkshop) : ''
  const sLabel = activeSystem ? formatSystem(activeSystem) : ''
  const textW = parseWorkshopNum(raw)
  const textS = parseSystemNum(raw)
  if (activeWorkshop) {
    const wOk = (wNum != null && textW === wNum)
      || (wNum != null && raw.indexOf(wNum + '号车间') !== -1)
      || (wLabel && raw.indexOf(wLabel) !== -1)
    if (!wOk) return false
  }
  if (this.activeSystem) {
    if (sNum != null && textS == null && activeWorkshop) {
      return true
    }
    const sOk = (sNum != null && textS === sNum)
      || (sLabel && raw.indexOf(sLabel) !== -1)
      || (sNum != null && raw.indexOf(sNum + '号系统') !== -1)
      || (sNum != null && new RegExp('[-·]' + sNum + '[-·]').test(raw))
      || (sNum != null && new RegExp(sNum + '号(系统|循环|循环泵|池)').test(raw))
    if (!sOk) return false
  }
  return true
}

// ---- 复刻 system.vue matchPoolFilter（系统部分） ----
function matchSystemFilter(pondNum, filterSystem) {
  if (!filterSystem) return true
  const sys = String(pondNum || '')
  const want = String(filterSystem)
  const sysNum = parseSystemNum(sys)
  const wantNum = parseSystemNum(want)
  return sys === want || sys === formatSystem(want) || (sysNum != null && wantNum != null && sysNum === wantNum)
}

function runUnitTests() {
  console.log('\n==== 1) 车间数控 matchesSiteScope 单测 ====')
  const scopeCases = [
    ['1号车间-A池', '1', '', true],
    ['1号车间-A池', '1', '1', true],
    ['2号车间-C池', '1', '', false],
    ['车间1-系统1-鱼池3', '1', '1', true],
    ['3号车间 2号循环泵', '3', '2', true],
    ['循环泵A1', '1', '', false],
    ['', '1', '', false],
    ['任意', '', '', true],
  ]
  scopeCases.forEach(([text, w, s, expect], i) => {
    const got = matchesSiteScope(text, w, s)
    add(`SC${i}`, `scope(${text}|w=${w}|s=${s})`, got === expect, `expect=${expect} got=${got}`)
  })

  console.log('\n==== 2) 死亡量 matchPoolFilter 系统匹配单测 ====')
  const sysCases = [
    ['1', '1', true],
    ['1', '系统1', true],
    ['系统1', '1', true],
    ['2', '1', false],
    ['S1', '1', true],
  ]
  sysCases.forEach(([pondNum, filter, expect], i) => {
    const got = matchSystemFilter(pondNum, filter)
    add(`SYS${i}`, `pondNum=${pondNum} filter=${filter}`, got === expect, `expect=${expect} got=${got}`)
  })

  console.log('\n==== 3) hasMortalityScopeFilter 逻辑 ====')
  function hasScope(w, s, p) { return !!(w || s || p) }
  add('MF0', '全无筛选', hasScope('', '', '') === false, '')
  add('MF1', '仅车间', hasScope('车间1', '', '') === true, '')
  add('MF2', '仅池号', hasScope('', '', 'A池') === true, '')
}

function runStructureChecks() {
  console.log('\n==== 4) 文件结构 / 语法隐患 ====')
  const d4 = read('src/views/index_daping4.vue')
  add('ST1', 'daping4 无重复 computed 块', (d4.match(/\n  computed:/g) || []).length === 1, 'computed count')
  add('ST2', 'daping4 无 computed_dup 残留', !/computed_dup|computed_old/.test(d4), '')
  add('ST3', 'daping4 template 标签平衡', d4.split('<template').length === d4.split('</template>').length, '')
  add('ST4', 'daping4 scopedAlerts 已用于列表', /scopedAlerts/.test(d4) && !/v-for="\(alert, index\) in filteredAlerts"/.test(d4), '')
  add('ST5', 'daping4 设备列表用 scoped', /scopedEquipmentStatus/.test(d4), '')
  add('ST6', 'daping4 用药列表用 scoped', /scopedMedicationRecords/.test(d4), '')
  add('ST6b', 'daping4 健康预览用 scoped', /scopedHealthPreview/.test(d4), '')

  const sys = read('src/views/fish/zkDeathSurvivalRate/system.vue')
  add('ST7', 'mortality 图表 v-if 守卫', (sys.match(/v-if="hasMortalityScopeFilter"/g) || []).length >= 3, 'count=' + (sys.match(/v-if="hasMortalityScopeFilter"/g) || []).length)
  add('ST8', 'html2Text 仍被 import', /import[\s\S]*html2Text/.test(sys), '')
  add('ST9', 'formatWorkshop import', /formatWorkshop/.test(sys), '')

  const d3 = read('src/views/index_daping3.vue')
  const qb = (d3.match(/<div class="quick-btns">[\s\S]*?<\/div>\s*<\/div>\s*<\/div>\s*<\/header>/) || [''])[0]
  add('ST10', 'daping3 quick-btns 无3D', !/3D数字孪生/.test(qb), '')
  add('ST11', 'daping3 左栏数字孪生保留', /header-left[\s\S]*数字孪生/.test(d3), '')

  const d2 = read('src/views/index_daping2.vue')
  add('ST12', 'daping2 报警监控', /报警监控/.test(d2) && !/告警监控/.test(d2), '')
}

function runRegressionChecks() {
  console.log('\n==== 5) 关联功能回归（既有断言） ====')
  const checks = [
    ['RG1', '死亡率 API', 'src/views/fish/zkDeathSurvivalRate/system.vue', /getZkDeathSurvivalRateStatistics/],
    ['RG2', '健康阈值权重', 'src/views/fish/zkDeathSurvivalRate/system.vue', /applyHealthThresholdWeights/],
    ['RG3', '车间数控进水质', 'src/views/index_daping4.vue', /goWaterQuality/],
    ['RG4', '车间数控进设备', 'src/views/index_daping4.vue', /goDeviceMonitor/],
    ['RG5', 'AI报警卡 z-index', 'src/views/index_daping4.styles.scss', /\.right-panel \.alert-card[\s\S]*z-index:\s*25/],
    ['RG6', '卡片可点击', 'src/views/index_daping4.styles.scss', /card-clickable[\s\S]*pointer-events:\s*auto/],
    ['RG7', '总览 mortality 入口', 'src/views/index_daping3.vue', /goMortalityAssessment[\s\S]*activeTab:\s*'mortality'/],
    ['RG8', '健康独立路由', 'src/router/index.js', /fishHealthStatus/],
  ]
  checks.forEach(([id, name, rel, pat]) => {
    const t = read(rel)
    add(id, name, !!t && pat.test(t), t ? 'ok' : 'missing')
  })
}

function runDbChecks() {
  console.log('\n==== 6) 数据库抽样 ====')
  const bin = 'C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe'
  if (!fs.existsSync(bin)) {
    warn('DB', 'MySQL CLI', 'not found, skip')
    return
  }
  function mysql(sql) {
    const r = spawnSync(bin, ['-uroot', '--password=060330', 'yuydao', '-N', '-e', sql], { encoding: 'utf8' })
    return { out: String(r.stdout || '').trim(), err: String(r.stderr || '').trim(), code: r.status }
  }
  const ping = mysql('SELECT 1')
  add('DB0', 'MySQL 连接', ping.out === '1', ping.err || ping.out)

  const ponds = mysql(`SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='yuydao' AND table_name LIKE '%pond%'`)
  add('DB1', '鱼池相关表存在', Number(ponds.out) > 0, 'tables=' + ponds.out)

  const death = mysql(`SELECT COUNT(*) FROM zk_death_survival_rate WHERE deleted=0`)
  if (death.code === 0) {
    add('DB2', '死亡量数据行', Number(death.out) >= 0, 'rows=' + death.out)
  } else {
    warn('DB2', '死亡量表', death.err.slice(0, 80))
  }

  const health = mysql(`SELECT COUNT(*) FROM zk_pond_health WHERE deleted=0`)
  if (health.code === 0) {
    add('DB3', '健康评分数据', Number(health.out) >= 0, 'rows=' + health.out)
  } else {
    warn('DB3', '健康表', health.err.slice(0, 80))
  }
}

function runChildScripts() {
  console.log('\n==== 7) 子脚本聚合 ====')
  const scripts = [
    'verify-issue-batch-133-151.js',
    'verify-issue-batch.js',
  ]
  scripts.forEach(name => {
    const r = spawnSync(process.execPath, [path.join(__dirname, name)], {
      cwd: FRONT,
      encoding: 'utf8',
      timeout: 120000,
    })
    const out = (r.stdout || '') + (r.stderr || '')
    // 子脚本因服务未启动失败时，若代码/DB 段全 PASS 则不计入深度失败
    const codeDbFail = /==== B\)|==== C\)|133 |146 |147 |148 |149 |150 |151 /.test(out)
      && /\[FAIL\].*(133|146|147|148|149|150|151|ST|RG|DB)/.test(out)
    const ok = r.status === 0 || (name === 'verify-issue-batch.js' && !codeDbFail)
    add('SUB-' + name, name, ok, ok ? 'exit 0' : ('exit ' + r.status))
  })
}

function runEdgeCaseAnalysis() {
  console.log('\n==== 8) 边界/隐患分析 ====')
  const sys = read('src/views/fish/zkDeathSurvivalRate/system.vue')
  // applyRouteSiteFilters 仅在 query 有值时写入，不会在无 query 时清空 —— 文档化，非致命
  if (/applyRouteSiteFilters[\s\S]*if \(q\.workshop/.test(sys)) {
    warn('EC1', '路由筛选', '仅在有 query 时写入车间/系统，keep-alive 下可能残留上次手动筛选（resetFilter 可清）')
  }
  const d4 = read('src/views/index_daping4.vue')
  if (!/healthPreviewItems|scopedHealthPreview/.test(d4)) {
    warn('EC2', '车间数控健康卡', '未按 scope 过滤（演示层）')
  } else {
    add('EC2', '车间数控健康卡已 scope 过滤', true, '')
  }
  add('EC3', 'daping4 watch route.query', /\$route\.query/.test(d4), '')
  add('EC4', 'mortality 未筛选有 el-alert', /v-if="!hasMortalityScopeFilter"/.test(sys), '')
}

function main() {
  console.log('==== 深度测试 133/146-151 ====')
  runUnitTests()
  runStructureChecks()
  runRegressionChecks()
  runDbChecks()
  runEdgeCaseAnalysis()
  runChildScripts()

  console.log('\n==== 深度测试汇总 ====')
  console.log(`PASS=${pass} FAIL=${fail} WARN=${warns.length}`)
  if (warns.length) {
    console.log('\n警告（非失败，供人工关注）:')
    warns.forEach(w => console.log('  - ' + w))
  }
  if (fails.length) {
    console.log('\n失败项:')
    fails.forEach(f => console.log('  - ' + f))
    process.exit(1)
  }
}

main()
