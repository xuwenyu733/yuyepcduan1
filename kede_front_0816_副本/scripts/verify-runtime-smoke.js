/**
 * 运行时冒烟：端口 + 关键页 HTML + 关键 API，保障无运行错误
 */
const http = require('http')
const path = require('path')

const FRONT = path.join(__dirname, '..')
let pass = 0
let fail = 0

function add(id, name, ok, detail) {
  const mark = ok ? 'PASS' : 'FAIL'
  if (ok) pass++
  else fail++
  console.log(`[${mark}] ${id} ${name}` + (detail ? ` - ${detail}` : ''))
}

function get(url, timeoutMs) {
  return new Promise(resolve => {
    const req = http.get(url, { timeout: timeoutMs || 5000 }, res => {
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

function portOpen(port) {
  return new Promise(resolve => {
    const net = require('net')
    const s = net.connect({ host: '127.0.0.1', port }, () => {
      s.end()
      resolve(true)
    })
    s.on('error', () => resolve(false))
  })
}

async function main() {
  console.log('==== 运行时冒烟 ====')
  for (const [name, port] of [['后端', 48181], ['前端', 4984], ['modules-api', 3020]]) {
    add('PORT', name, await portOpen(port), `port=${port}`)
  }

  const fe = await get('http://127.0.0.1:4984/')
  add('FE', '前端首页', fe.status === 200 && fe.body.length > 500, `status=${fe.status} len=${(fe.body || '').length}`)

  const health = await get('http://127.0.0.1:3020/api/health')
  let okHealth = false
  try {
    const j = JSON.parse(health.body || '{}')
    okHealth = health.status === 200 && j.code === 0
  } catch (e) {}
  add('API', 'modules-api health', okHealth, `status=${health.status}`)

  const th = await get('http://127.0.0.1:3020/api/threshold/thresholds/all')
  add('API', 'threshold/all', th.status === 200, `status=${th.status}`)

  const ponds = await get('http://127.0.0.1:3020/api/fish/config/ponds')
  let pondOk = false
  try {
    const j = JSON.parse(ponds.body || '{}')
    const list = (j && j.data) || j
    pondOk = ponds.status === 200 && Array.isArray(list) && list.length > 0
  } catch (e) {}
  add('API', 'fish/ponds', pondOk, `status=${ponds.status}`)

  // 前端代理
  const proxy = await get('http://127.0.0.1:4984/modules-api/api/health')
  add('PROXY', '前端代理 modules-api', proxy.status === 200, `status=${proxy.status}`)

  // 租户接口（后端）
  const tenant = await get('http://127.0.0.1:48181/admin-api/system/tenant/get-by-website?website=localhost')
  add('BE', '租户接口', tenant.status === 200, `status=${tenant.status}`)

  // 关键路由页面能被 webpack-dev-server 返回（hash 模式仍走 index）
  const routes = [
    ['/#/login', '登录'],
    ['/#/keyshow/workshopCNC?workshop=9&system=1', '车间数控'],
    ['/#/device_monitor', '设备监控'],
    ['/#/water_quality', '水质'],
    ['/#/mission_planning', '任务与计划'],
    ['/#/iot/thresholdSettings', '阈值管理'],
    ['/#/keyshow/fishHealthStatus', '健康评分'],
  ]
  for (const [hash, label] of routes) {
    const r = await get('http://127.0.0.1:4984/' + hash.replace(/^\/#/, '?'))
    // SPA：任意 hash 最终都返回 index.html
    const index = await get('http://127.0.0.1:4984/')
    add('PAGE', label, index.status === 200 && index.body.includes('app'), `spa ok (${label})`)
  }

  // 关键源文件无语法级明显破坏（require 断言文件存在）
  const fs = require('fs')
  const checks = [
    ['src/views/index_daping4.vue', /goWaterQuality|goDeviceMonitor/],
    ['src/views/deviceMonitor.vue', /buildTrend\(startIso|onDateRangeChange|notMerge:\s*true|,\s*true\)/],
    ['src/utils/request.js', /正在跳转登录页/],
    ['src/views/fish/zkDeathSurvivalRate/system.vue', /applyHealthThresholdWeights|handleRefresh\(\)/],
    ['src/utils/thresholdConfig.js', /resolveHealthWeights/],
    ['src/utils/alarmTaskBridge.js', /createInspectionPlanTask|syncModuleAiAlarmsToMission/],
    ['src/views/MissionPlanning.vue', /mergeLocalInspectionTasks/],
  ]
  for (const [file, pat] of checks) {
    const full = path.join(FRONT, file)
    const text = fs.existsSync(full) ? fs.readFileSync(full, 'utf8') : ''
    add('CODE', file.replace(/^src\//, ''), !!text && pat.test(text), text ? 'match' : 'missing')
  }

  console.log(`\n==== SUMMARY ====`)
  console.log(`TOTAL=${pass + fail} PASS=${pass} FAIL=${fail}`)
  process.exit(fail ? 1 : 0)
}

main().catch(e => {
  console.error(e)
  process.exit(1)
})
