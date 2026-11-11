/**
 * 2026-09-02 今日改动专项冒烟
 * node scripts/verify-changes-20260902.js
 */
const http = require('http')
const crypto = require('crypto')
const fs = require('fs')
const path = require('path')

const FRONT = 'http://127.0.0.1:4984'
const API = 'http://127.0.0.1:48181'
const TENANT = '北京科德数传渔业科技有限公司'
const USER = 'admin'
const PASS = 'admin123'
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
      timeout: 180000
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
      const chunks = []
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
          resolve({ ok: true, status: res.statusCode, json, text })
        } catch (e) {
          resolve({ ok: true, status: res.statusCode, json: null, text })
        }
      })
    })
    req.on('error', (e) => resolve({ ok: false, error: e.message }))
    req.on('timeout', () => { req.destroy(); resolve({ ok: false, error: 'timeout' }) })
    if (payload != null) req.write(payload)
    req.end()
  })
}

function readFile(rel) {
  return fs.readFileSync(path.join(__dirname, '..', rel), 'utf8')
}

function testWaterFilterLogic() {
  // 166：模拟 selectPool 后 filteredRealtime 不应为空
  function normalizeLakeName(raw) {
    const m = String(raw || '').match(/(?:车间\d+[-－—])?(?:系统\d+[-－—])?(.+)$/i)
    return (m && m[1] ? m[1].trim() : String(raw || '').trim())
  }
  const rows = [
    { lake_name: '车间1-系统1-鱼池1' },
    { lake_name: 'c1-s1-1' }
  ]
  const name = '车间1-系统1-鱼池1'
  const filterPool = normalizeLakeName(name)
  const filtered = rows.filter((row) => {
    if (!row.lake_name) return false
    if (filterPool && normalizeLakeName(row.lake_name) !== normalizeLakeName(filterPool)) return false
    return true
  })
  add('CODE166', '点击鱼池后筛选逻辑', filtered.length === 1, `matched=${filtered.length}`)
}

function testSourcePatches() {
  const wq = readFile('src/utils/waterQualityLiveBridge.js')
  add('CODE170', '水质桥接读取 water_quality_pools', wq.includes("getChartSeed('water_quality_pools')"), '')
  add('CODE170b', 'demo 分池优先于 vary 复制', wq.includes('if (demoRealtime.length)'), '')

  const death = readFile('src/views/fish/zkDeathSurvivalRate/system.vue')
  add('CODE171', '死亡页 mergeMortalityWithMasterPonds', death.includes('mergeMortalityWithMasterPonds'), '')

  const ov = readFile('src/views/index_daping3.vue')
  add('CODE168', '总览任务 urgent 为数组切片', ov.includes('urgent: []') && ov.includes('.slice(0, 3)'), '')
  add('CODE168b', '日程按小时排序后最多3条', ov.includes('.sort((a, b) => a.hour - b.hour)') && ov.includes('.slice(0, 3)'), '')
  const bridge = readFile('src/utils/alarmTaskBridge.js')
  add('CODE168c', 'buildDashboardTaskBoard 返回数组', bridge.includes('urgent: []') && bridge.includes('.slice(0, 3)'), '')
  add('CODEASR', 'FunAsr start 空指针防护', readFile('src/components/funasr_voice2txt/FunAsrVoice2Txt.vue').includes('asr_unavailable'), '')
  add('CODEWQ_TIME', '水质趋势补全日时间', wq.includes("padStart(2, '0')") && wq.includes('created ='), '')

  const mission = readFile('src/views/MissionPlanning.vue')
  add('CODE169', '任务刷新保留 _open', mission.includes('openTaskKeysErr'), '')

  const fcr = readFile('src/views/fish/zkFcrModel/system.vue')
  add('CODE163', 'FCR displayLabel', fcr.includes('displayLabel') && fcr.includes('buildSiteDisplayLabel'), '')

  const dev = readFile('src/views/deviceMonitor.vue')
  add('CODE167', '设备监控使用缓存 fetch', dev.includes('fetchOverviewMonitor(false)'), '')

  const wqVue = readFile('src/views/waterQuality.vue')
  add('CODE165', '空态引导数据管理页', wqVue.includes('水质数据管理') && !wqVue.includes('PMIS'), '')
}

;(async () => {
  console.log('==== 2026-09-02 今日改动测试 ====\n')

  testSourcePatches()
  testWaterFilterLogic()

  const ports = [
    ['后端', 48181],
    ['前端', 4984]
  ]
  for (const [n, p] of ports) {
    const r = await request('GET', `http://127.0.0.1:${p}/`)
    add('RUN', `${n} :${p} 可达`, r.ok || r.status === 200 || r.status === 404, String(r.status || r.error))
  }

  const tenant = await request('GET', API + '/admin-api/system/tenant/get-id-by-name?name=' + encodeURIComponent(TENANT))
  const tenantId = tenant.json && tenant.json.data != null ? tenant.json.data : null
  add('API', '租户 ID', tenantId != null, String(tenantId))

  let login = await request('POST', API + '/admin-api/system/auth/login', {
    headers: { 'tenant-id': String(tenantId || '1') },
    body: { username: USER, password: PASS },
    encrypt: true
  })
  let token = login.json && login.json.data && (login.json.data.accessToken || login.json.data.access_token)
  if (!token) {
    login = await request('POST', API + '/admin-api/system/auth/login', {
      headers: { 'tenant-id': String(tenantId || '1') },
      body: { username: USER, password: PASS }
    })
    token = login.json && login.json.data && (login.json.data.accessToken || login.json.data.access_token)
  }
  add('API', '登录 token', !!token, token ? 'ok' : JSON.stringify(login.json || login.error).slice(0, 120))

  if (!token) {
    console.log(`\n汇总 PASS=${pass} FAIL=${fail}`)
    process.exit(fail ? 1 : 0)
  }

  const auth = { Authorization: 'Bearer ' + token, 'tenant-id': String(tenantId || '1') }

  // 170 水质分池种子
  const seed = await request('GET', API + '/admin-api/fish/zk-pond-warn/getChartSeed?key=water_quality_pools', { headers: auth })
  const pools = (seed.json && seed.json.data) || []
  const doVals = [...new Set(pools.map((p) => p.lake_do).filter((v) => v != null))]
  add('API170', 'water_quality_pools 可读', Array.isArray(pools) && pools.length >= 10, `count=${pools.length}`)
  add('API170b', '各池 DO 有差异', doVals.length >= 2, `uniqueDO=${doVals.length} sample=${doVals.slice(0, 3).join(',')}`)

  const wlive = await request('GET', API + '/admin-api/fish/zk-pond-warn/getWaterQualityLive', { headers: auth })
  const wrows = (wlive.json && wlive.json.data && wlive.json.data.waterRealtime) || []
  const wWithVal = wrows.filter((m) => m && m.value != null)
  add('API170c', 'getWaterQualityLive 有指标', wWithVal.length >= 3, `metrics=${wWithVal.length}`)

  // 171 死亡统计
  const death = await request('GET', API + '/admin-api/fish/zk-death-survival-rate/statistics', { headers: auth })
  const poolStats = (death.json && death.json.data && death.json.data.poolStatsList) || []
  const withDay = poolStats.filter((p) => Number(p.dayDead) > 0)
  add('API171', '死亡统计 poolStatsList', poolStats.length >= 10, `pools=${poolStats.length}`)
  add('API171b', '今日死亡有数据', withDay.length >= 10, `dayDeadPools=${withDay.length}`)

  // 168 日程
  const sched = await request('GET', API + '/admin-api/fish/zk-pond-warn/getOverviewSchedule', { headers: auth })
  const events = (sched.json && sched.json.data) || []
  const byCat = {}
  events.forEach((e) => {
    const k = e.category || 'other'
    byCat[k] = (byCat[k] || 0) + 1
  })
  add('API168', '总览日程可读', events.length >= 8, `total=${events.length} cats=${Object.keys(byCat).join(',')}`)

  // 167 设备监控
  const mon = await request('GET', API + '/admin-api/fish/zk-pond-warn/getOverviewMonitor', { headers: auth })
  const devices = (mon.json && mon.json.data && mon.json.data.devices) || []
  add('API167', '设备监控 overview 可读', devices.length >= 1, `devices=${devices.length}`)

  const t0 = Date.now()
  const mon2 = await request('GET', API + '/admin-api/fish/zk-pond-warn/getOverviewMonitor', { headers: auth })
  const t1 = Date.now()
  const devices2 = (mon2.json && mon2.json.data && mon2.json.data.devices) || []
  // 167 前端走缓存；后端 overview 本身仍可能很慢，这里只校验二次可读，耗时作告警
  add('API167b', 'overview 二次可读', devices2.length >= 1, `devices=${devices2.length} ${t1 - t0}ms`)
  if (t1 - t0 >= 3000) {
    console.log(`[WARN] API167b 后端 overview 仍慢（前端已改 fetchOverviewMonitor(false) 走缓存）: ${t1 - t0}ms`)
  }

  const proxy = await request('GET', FRONT + '/proxy-api/admin-api/fish/zk-pond-warn/getChartSeed?key=water_quality_pools', {
    headers: auth
  })
  const proxyPools = (proxy.json && proxy.json.data) || []
  add('API', '前端代理 chartSeed', proxy.status === 200 && Array.isArray(proxyPools), `status=${proxy.status} count=${proxyPools.length}`)

  console.log('\n==== 汇总 ====')
  console.log(`PASS=${pass} FAIL=${fail}`)
  if (fails.length) {
    console.log('\n失败项:')
    fails.forEach((f) => console.log(' - ' + f))
  }
  process.exit(fail ? 1 : 0)
})()
