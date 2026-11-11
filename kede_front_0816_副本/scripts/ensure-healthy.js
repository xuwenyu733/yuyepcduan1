/**
 * 运行时健康巡检：保障开发服关键链路无错
 * 用法：node scripts/ensure-healthy.js
 */
const http = require('http')
const net = require('net')
const fs = require('fs')
const path = require('path')

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
  console.log(`[${ok ? 'PASS' : 'FAIL'}] ${id} ${name}` + (detail ? ` - ${detail}` : ''))
}

function portOpen(port) {
  return new Promise(resolve => {
    const s = net.connect({ host: '127.0.0.1', port }, () => {
      s.end()
      resolve(true)
    })
    s.on('error', () => resolve(false))
  })
}

function get(url, timeoutMs) {
  return new Promise(resolve => {
    const req = http.get(url, { timeout: timeoutMs || 8000 }, res => {
      let body = ''
      res.on('data', c => { body += c })
      res.on('end', () => resolve({ status: res.statusCode, body }))
    })
    req.on('error', e => resolve({ status: 0, body: '', error: e.message }))
    req.on('timeout', () => {
      req.destroy()
      resolve({ status: 0, body: '', error: 'timeout' })
    })
  })
}

function request(method, url, data, headers) {
  return new Promise(resolve => {
    const body = data == null ? null : JSON.stringify(data)
    const u = new URL(url)
    const opts = {
      hostname: u.hostname,
      port: u.port,
      path: u.pathname + u.search,
      method,
      headers: Object.assign({}, headers || {}),
      timeout: 10000
    }
    if (body != null) {
      opts.headers['Content-Type'] = 'application/json'
      opts.headers['Content-Length'] = Buffer.byteLength(body)
    }
    const req = http.request(opts, res => {
      let text = ''
      res.on('data', c => { text += c })
      res.on('end', () => resolve({ status: res.statusCode, body: text }))
    })
    req.on('error', e => resolve({ status: 0, body: '', error: e.message }))
    req.on('timeout', () => {
      req.destroy()
      resolve({ status: 0, body: '', error: 'timeout' })
    })
    if (body != null) req.write(body)
    req.end()
  })
}

async function main() {
  console.log('==== 健康巡检 ensure-healthy ====')

  for (const [name, port] of [
    ['后端', 48181],
    ['前端', 4984],
    ['modules-api', 3020],
    ['MySQL', 3306],
    ['Redis', 6379]
  ]) {
    add('PORT', name, await portOpen(port), 'port=' + port)
  }

  const fe = await get('http://127.0.0.1:4984/')
  add('FE', '首页', fe.status === 200 && (fe.body || '').length > 500, 'len=' + ((fe.body || '').length))

  const health = await get('http://127.0.0.1:3020/api/health')
  add('API', 'modules health', health.status === 200 && /"code"\s*:\s*0/.test(health.body || ''), 'status=' + health.status)

  const ponds = await get('http://127.0.0.1:3020/api/fish/config/ponds')
  let pondOk = false
  try {
    const j = JSON.parse(ponds.body || '{}')
    pondOk = ponds.status === 200 && Array.isArray(j.data) && j.data.length > 0
  } catch (e) {}
  add('API', 'fish ponds', pondOk, 'status=' + ponds.status)

  const proxy = await get('http://127.0.0.1:4984/modules-api/api/health')
  add('PROXY', '前端→modules', proxy.status === 200, 'status=' + proxy.status)

  const tenant = await get(
    'http://127.0.0.1:48181/admin-api/system/tenant/get-id-by-name?name=' +
      encodeURIComponent('北京科德数传渔业科技有限公司')
  )
  let tenantOk = false
  try {
    const j = JSON.parse(tenant.body || '{}')
    tenantOk = tenant.status === 200 && j.code === 0
  } catch (e) {}
  add('BE', '租户接口', tenantOk, 'status=' + tenant.status)

  const routerSrc = fs.readFileSync(path.join(FRONT, 'src/router/index.js'), 'utf8')
  add('ROUTE', '知识树静态路由', routerSrc.includes("/knowledge/zk-konwledge-tree"), 'router')
  add('ROUTE', '鱼病知识树跳转', routerSrc.includes('/fish-disease/knowledge-tree'), 'router')

  const shellSrc = fs.readFileSync(path.join(FRONT, 'src/views/fishDisease/FishShell.vue'), 'utf8')
  add('UI', '鱼病侧栏知识树', /knowledgeTree/.test(shellSrc) && /adminOnly:\s*true/.test(shellSrc), 'FishShell')

  const login = await request(
    'POST',
    'http://127.0.0.1:48181/admin-api/system/auth/login',
    { username: 'admin', password: 'admin123' },
    { 'tenant-id': '1' }
  )
  let token = ''
  try {
    const j = JSON.parse(login.body || '{}')
    token = (j.data && j.data.accessToken) || ''
    add('AUTH', 'admin登录', j.code === 0 && !!token, 'code=' + j.code)
  } catch (e) {
    add('AUTH', 'admin登录', false, e.message)
  }

  if (token) {
    const page = await request(
      'GET',
      'http://127.0.0.1:48181/admin-api/fish/zk-konwledge-tree/page?pageNo=1&pageSize=5',
      null,
      { Authorization: 'Bearer ' + token, 'tenant-id': '1' }
    )
    let pageOk = false
    let total = 0
    try {
      const j = JSON.parse(page.body || '{}')
      total = (j.data && j.data.total) || 0
      pageOk = page.status === 200 && j.code === 0 && total > 0
    } catch (e) {}
    add('KT', '知识树分页API', pageOk, 'total=' + total)
  }

  console.log('\n==== SUMMARY ====')
  console.log(`TOTAL=${pass + fail} PASS=${pass} FAIL=${fail}`)
  if (fails.length) {
    console.log('FAILED:')
    fails.forEach(f => console.log(' - ' + f))
  }
  process.exit(fail ? 1 : 0)
}

main().catch(e => {
  console.error(e)
  process.exit(1)
})
