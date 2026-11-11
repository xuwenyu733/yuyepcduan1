/**
 * 今日改动 API 级运行时保障（登录后打关键接口）
 * node scripts/verify-today-api-smoke.js
 */
const http = require('http')
const crypto = require('crypto')

const FRONT = 'http://127.0.0.1:4984'
const API = 'http://127.0.0.1:48181'
const TENANT = '北京科德数传渔业科技有限公司'
const USER = 'admin'
const PASS = 'admin123'

// 与 .env.local 一致
const REQ_KEY = '52549111389893486934626385991395'
const RESP_KEY = '96103715984234343991809655248883'
const ENC_HEADER = 'X-Api-Encrypt'

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

function aesEncrypt(plain, keyHex) {
  const key = Buffer.from(keyHex, 'utf8')
  const iv = crypto.randomBytes(16)
  const cipher = crypto.createCipheriv('aes-256-cbc', key, iv)
  const enc = Buffer.concat([cipher.update(String(plain), 'utf8'), cipher.final()])
  return Buffer.concat([iv, enc]).toString('base64')
}

function aesDecrypt(b64, keyHex) {
  const buf = Buffer.from(b64, 'base64')
  const iv = buf.subarray(0, 16)
  const data = buf.subarray(16)
  const key = Buffer.from(keyHex, 'utf8')
  const decipher = crypto.createDecipheriv('aes-256-cbc', key, iv)
  return Buffer.concat([decipher.update(data), decipher.final()]).toString('utf8')
}

function request(method, url, { headers = {}, body, encrypt } = {}) {
  return new Promise((resolve) => {
    const u = new URL(url)
    const opts = {
      hostname: u.hostname,
      port: u.port,
      path: u.pathname + u.search,
      method,
      headers: { ...headers },
      timeout: 15000
    }
    let payload = body
    if (encrypt && body != null) {
      const raw = typeof body === 'string' ? body : JSON.stringify(body)
      payload = aesEncrypt(raw, REQ_KEY)
      opts.headers[ENC_HEADER] = 'true'
      opts.headers['Content-Type'] = 'text/plain'
      opts.headers['Content-Length'] = Buffer.byteLength(payload)
    } else if (body != null) {
      payload = typeof body === 'string' ? body : JSON.stringify(body)
      opts.headers['Content-Type'] = opts.headers['Content-Type'] || 'application/json'
      opts.headers['Content-Length'] = Buffer.byteLength(payload)
    }
    const req = http.request(opts, (res) => {
      let chunks = []
      res.on('data', (c) => chunks.push(c))
      res.on('end', () => {
        let text = Buffer.concat(chunks).toString('utf8')
        const isEnc = String(res.headers[ENC_HEADER.toLowerCase()] || '') === 'true'
          || (text && !text.trim().startsWith('{') && !text.trim().startsWith('['))
        try {
          if (encrypt || isEnc) {
            try { text = aesDecrypt(text.trim().replace(/^"|"$/g, ''), RESP_KEY) } catch (e) { /* keep */ }
          }
          const json = text ? JSON.parse(text) : null
          resolve({ ok: true, status: res.statusCode, json, text, headers: res.headers })
        } catch (e) {
          resolve({ ok: true, status: res.statusCode, json: null, text, headers: res.headers })
        }
      })
    })
    req.on('error', (e) => resolve({ ok: false, error: e.message }))
    req.on('timeout', () => { req.destroy(); resolve({ ok: false, error: 'timeout' }) })
    if (payload != null) req.write(payload)
    req.end()
  })
}

;(async () => {
  console.log('==== 今日改动 API 运行时冒烟 ====\n')

  // 1) 前端首页
  const home = await request('GET', FRONT + '/')
  add('API1', '前端首页 200', home.ok && home.status === 200, String(home.status || home.error))

  // 2) 租户 ID
  const tenant = await request(
    'GET',
    API + '/admin-api/system/tenant/get-id-by-name?name=' + encodeURIComponent(TENANT)
  )
  const tenantId = tenant.json && (tenant.json.data != null ? tenant.json.data : tenant.json)
  add('API2', '获取租户 ID', !!tenantId, String(tenantId))

  // 3) 登录（加密）
  const login = await request('POST', API + '/admin-api/system/auth/login', {
    headers: {
      'tenant-id': String(tenantId || '1')
    },
    body: { username: USER, password: PASS },
    encrypt: true
  })
  let token = null
  if (login.json && login.json.data && login.json.data.accessToken) {
    token = login.json.data.accessToken
  } else if (login.json && login.json.data && login.json.data.access_token) {
    token = login.json.data.access_token
  }
  // 若加密失败，尝试明文
  if (!token) {
    const login2 = await request('POST', API + '/admin-api/system/auth/login', {
      headers: { 'tenant-id': String(tenantId || '1') },
      body: { username: USER, password: PASS }
    })
    if (login2.json && login2.json.data) {
      token = login2.json.data.accessToken || login2.json.data.access_token
    }
    add('API3', '登录获取 token', !!token, token ? 'ok' : JSON.stringify(login2.json || login2.text || login2.error).slice(0, 180))
  } else {
    add('API3', '登录获取 token', !!token, 'encrypted ok')
  }

  if (!token) {
    console.log('\n无法登录，跳过鉴权接口。汇总 PASS=' + pass + ' FAIL=' + fail)
    process.exit(1)
  }

  const auth = {
    Authorization: 'Bearer ' + token,
    'tenant-id': String(tenantId || '1')
  }

  // 4) 车间列表（验证 id=9 名称）
  const workshops = await request('GET', API + '/admin-api/fish/zk-workshop/list-for-dashboard', { headers: auth })
  const list = (workshops.json && workshops.json.data) || []
  const w9 = list.find((w) => String(w.workshopId) === '9' || String(w.id) === '9')
  add('API4', '车间 dashboard 可读', Array.isArray(list) && list.length > 0, 'count=' + list.length)
  add('API5', 'workshopId=9 名称非「车间9」', !!(w9 && w9.workshopName && w9.workshopName !== '车间9'), w9 ? w9.workshopName : 'not found')
  add('API6', 'workshopId=9 含大西洋/鲑', !!(w9 && /大西洋|鲑|三文鱼/.test(w9.workshopName || '')), w9 ? w9.workshopName : '')

  // 5) 健康最新列表
  const health = await request('GET', API + '/admin-api/fish/zk-pond-health/latest-pond-health-list', { headers: auth })
  const hlist = (health.json && health.json.data) || []
  add('API7', '健康评分列表可读', Array.isArray(hlist) && hlist.length > 0, 'count=' + hlist.length)
  if (hlist.length) {
    const sample = hlist[0]
    add('API8', '健康项含评分字段', sample.overallRating != null || sample.environmentalLayerRating != null, JSON.stringify(sample).slice(0, 120))
  }

  // 6) 总览 overview3（与前端 daPing.js 一致）
  const ov = await request('GET', API + '/admin-api/fish/zk-pond-warn/getOverview3', { headers: auth })
  const overview = ov.json
  add('API9', '总览 overview3 可读', !!(overview && (overview.code === 0 || overview.code === 200) && overview.data), overview ? ('code=' + overview.code) : 'null')
  if (overview && overview.data && Array.isArray(overview.data.healthList)) {
    add('API10', '总览含 healthList', true, 'len=' + overview.data.healthList.length)
    const names = overview.data.healthList.map((h) => h.pondName || '').filter(Boolean)
    add('API10b', 'healthList 有池名', names.length > 0, names.slice(0, 3).join(' | '))
  } else {
    add('API10', '总览含 healthList', false, 'missing')
  }

  // 7) 报警分页（检查 handleInfo HTML）
  const alarms = await request('GET', API + '/admin-api/fish/zk-device-alarm/page?pageNo=1&pageSize=20', { headers: auth })
  const al = ((alarms.json && alarms.json.data && alarms.json.data.list) || [])
  add('API11', '报警分页可读', alarms.json && (alarms.json.code === 0 || alarms.json.code === 200), 'count=' + al.length)
  const htmlOnes = al.filter((a) => /<[^>]+>/.test(String(a.handleInfo || '')))
  // 后端可能仍存 HTML，前端 strip 是正确保障；这里仅统计
  console.log(`[INFO] 报警含 HTML 的 handleInfo 条数=${htmlOnes.length}（前端 stripHtmlPlain 负责净化）`)

  // 8) 代理路径（前端代理）
  const proxy = await request('GET', FRONT + '/proxy-api/admin-api/system/tenant/get-id-by-name?name=' + encodeURIComponent(TENANT))
  add('API12', '前端 proxy-api 代理后端', proxy.ok && proxy.status < 500, 'status=' + (proxy.status || proxy.error))

  console.log('\n==== API 冒烟汇总 ====')
  console.log(`PASS=${pass} FAIL=${fail}`)
  if (fails.length) {
    console.log('\n失败:')
    fails.forEach((f) => console.log(' - ' + f))
  }
  process.exit(fail ? 1 : 0)
})()
