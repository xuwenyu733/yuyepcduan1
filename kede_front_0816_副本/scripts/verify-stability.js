const http = require('http')
const https = require('https')
const net = require('net')
const fs = require('fs')
const path = require('path')
const { spawnSync } = require('child_process')

const FRONT = path.join(__dirname, '..')
const ROOT = path.join(FRONT, '..')
const results = []

function add(id, name, ok, detail) {
  results.push({ id, name, ok: !!ok, detail: String(detail || '') })
  console.log(`[${ok ? 'PASS' : 'FAIL'}] ${id} ${name} - ${detail}`)
}

function tcp(port, host = '127.0.0.1', ms = 2500) {
  return new Promise((resolve) => {
    const s = net.connect({ port, host }, () => { s.destroy(); resolve(true) })
    s.on('error', () => resolve(false))
    s.setTimeout(ms, () => { s.destroy(); resolve(false) })
  })
}

function req(url, opts = {}) {
  return new Promise((resolve, reject) => {
    const u = new URL(url)
    const lib = u.protocol === 'https:' ? https : http
    const body = opts.body ? Buffer.from(opts.body) : null
    const r = lib.request({
      hostname: u.hostname,
      port: u.port,
      path: u.pathname + u.search,
      method: opts.method || 'GET',
      headers: opts.headers || {},
      timeout: opts.timeout || 12000
    }, (res) => {
      const chunks = []
      res.on('data', (c) => chunks.push(c))
      res.on('end', () => {
        const text = Buffer.concat(chunks).toString('utf8')
        resolve({ status: res.statusCode, text })
      })
    })
    r.on('error', reject)
    r.on('timeout', () => { r.destroy(); reject(new Error('timeout')) })
    if (body) r.write(body)
    r.end()
  })
}

async function main() {
  console.log('==== A. Ports ====')
  for (const [name, port] of [['后端', 48181], ['前端', 4984], ['modules-api', 3020], ['MySQL', 3306], ['Redis', 6379]]) {
    add('SVC', name, await tcp(port), `port=${port}`)
  }

  console.log('==== B. Unit tests ====')
  const t = spawnSync(process.execPath, [path.join(FRONT, 'scripts/test-metric-rules.js')], { encoding: 'utf8' })
  add('TEST', 'metric-rules', t.status === 0 && /9 checks done/.test(t.stdout || ''), (t.stdout || t.stderr || '').trim().split(/\r?\n/).pop())

  console.log('==== C. Code symbols ====')
  const checks = [
    ['U1', 'src/utils/waterMetricStatus.js', 'evalMetricStatus'],
    ['U2', 'src/utils/diagnosisStatus.js', 'canSubmitDiagnosisConclusion'],
    ['M1', 'src/views/system/menu/index.vue', 'AUTO_ASSIGN_ROLE_CODES'],
    ['M2', 'src/views/system/menu/index.vue', 'Promise.allSettled'],
    ['M3', 'src/views/system/menu/index.vue', 'isSensitiveSystemMenu'],
    ['W1', 'src/views/waterQuality.vue', 'metricStatusByKey'],
    ['W2', 'src/views/waterQuality.vue', 'metricCellStyle'],
    ['D1', 'src/views/fishDisease/DiagnosisChat.vue', 'canSubmitConclusion'],
    ['D2', 'src/views/fishDisease/DiagnosisChat.vue', 'canSubmitDiagnosisConclusion'],
    ['F1', 'src/views/flavor/BasicConfig.vue', 'm³'],
    ['S1', 'src/assets/styles/element-ui.scss', 'el-message-box__btns']
  ]
  for (const [id, rel, sym] of checks) {
    const p = path.join(FRONT, rel)
    if (!fs.existsSync(p)) { add(id, sym, false, 'FILE_MISSING'); continue }
    const txt = fs.readFileSync(p, 'utf8')
    add(id, sym, txt.includes(sym), rel)
  }
  const menu = fs.readFileSync(path.join(FRONT, 'src/views/system/menu/index.vue'), 'utf8')
  add('Mneg', '无SUPER_ADMIN全量策略', !menu.includes('SUPER_ADMIN_ROLE_CODES'), 'ok')
  const wq = fs.readFileSync(path.join(FRONT, 'src/views/waterQuality.vue'), 'utf8')
  add('Wneg', '无旧硬编码DO阈值', !/lake_do[^\n]{0,40}5\.5/.test(wq), 'ok')
  const flavor = fs.readFileSync(path.join(FRONT, 'src/views/flavor/BasicConfig.vue'), 'utf8')
  add('Fneg', '无立方米', !flavor.includes('立方米'), 'ok')

  console.log('==== D. APIs ====')
  try {
    const tenantName = encodeURIComponent('北京科德数传渔业科技有限公司')
    const t1 = await req(`http://127.0.0.1:48181/admin-api/system/tenant/get-id-by-name?name=${tenantName}`)
    const j1 = JSON.parse(t1.text)
    add('API', '租户', t1.status === 200 && j1.code === 0, `id=${j1.data}`)
  } catch (e) { add('API', '租户', false, e.message) }

  try {
    const p = await req('http://127.0.0.1:4984/proxy-api/admin-api/system/tenant/get-id-by-name?name=x')
    add('API', '前端代理', p.status === 200, `status=${p.status}`)
  } catch (e) { add('API', '前端代理', false, e.message) }

  try {
    const h = await req('http://127.0.0.1:4984/')
    add('API', '前端首页', h.status === 200 && h.text.length > 500, `len=${h.text.length}`)
  } catch (e) { add('API', '前端首页', false, e.message) }

  try {
    const ponds = JSON.parse((await req('http://127.0.0.1:3020/api/fish/config/ponds')).text)
    add('API', '鱼池', ponds.code === 0, `count=${(ponds.data || []).length}`)
  } catch (e) { add('API', '鱼池', false, e.message) }

  try {
    const pending = JSON.parse((await req('http://127.0.0.1:3020/api/fish/diagnosis/records?tab=pending')).text)
    add('API', '待诊断', pending.code === 0 || pending.data != null, `code=${pending.code}`)
  } catch (e) { add('API', '待诊断', false, e.message) }

  try {
    const loginRes = await req('http://127.0.0.1:48181/admin-api/system/auth/login', {
      method: 'POST',
      headers: { 'tenant-id': '1', 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: 'zhangbo', password: '123456k' })
    })
    const login = JSON.parse(loginRes.text)
    const token = login.data && login.data.accessToken
    add('API', 'zhangbo登录', login.code === 0 && !!token, `code=${login.code}`)
    const infoRes = await req('http://127.0.0.1:48181/admin-api/system/auth/get-permission-info', {
      headers: { Authorization: `Bearer ${token}`, 'tenant-id': '1' }
    })
    const info = JSON.parse(infoRes.text)
    const names = []
    const walk = (nodes) => { (nodes || []).forEach(n => { if (n.name) names.push(n.name); walk(n.children) }) }
    walk(info.data && info.data.menus)
    const need = ['鱼病管理系统', '风味优化管理', '阈值设置', '水质情况']
    const hit = need.filter(n => names.includes(n))
    add('API', 'zhangbo菜单', hit.length === 4, hit.join(','))
  } catch (e) { add('API', '登录/菜单', false, e.message) }

  console.log('==== E. DB ====')
  try {
    const mysql = 'C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe'
    const db = spawnSync(mysql, ['-uroot', '-p060330', 'yuydao', '-N', '-e',
      'SELECT COUNT(*) FROM system_role_menu WHERE role_id=161 AND menu_id IN (5772,5774,5775,5776);'], { encoding: 'utf8' })
    const count = parseInt(String(db.stdout || '').trim(), 10)
    add('DB', '角色161关键菜单', count === 4, `count=${count}`)
  } catch (e) { add('DB', '角色161关键菜单', false, e.message) }

  const pass = results.filter(r => r.ok).length
  const fail = results.filter(r => !r.ok).length
  console.log('')
  console.log(`==== FINAL: ${pass} PASS / ${fail} FAIL / ${results.length} TOTAL ====`)
  process.exit(fail ? 1 : 0)
}

main().catch((e) => { console.error(e); process.exit(1) })
