/**
 * 校验问题清单 133 / 146-151 修复
 * 用法：node scripts/verify-issue-batch-133-151.js
 */
const fs = require('fs')
const path = require('path')
const http = require('http')
const net = require('net')

const FRONT = path.join(__dirname, '..')
let pass = 0
let fail = 0
const fails = []

function add(id, name, ok, detail) {
  if (ok) pass++
  else {
    fail++
    fails.push(id + ' ' + name + (detail ? ' :: ' + detail : ''))
  }
  console.log(`[${ok ? 'PASS' : 'FAIL'}] ${id} ${name}` + (detail ? ` - ${detail}` : ''))
}

function read(rel) {
  const p = path.join(FRONT, rel)
  return fs.existsSync(p) ? fs.readFileSync(p, 'utf8') : ''
}

function assertFile(id, name, rel, pat, antiPat) {
  const text = read(rel)
  if (!text) {
    add(id, name, false, 'missing ' + rel)
    return
  }
  const ok = pat.test(text) && !(antiPat && antiPat.test(text))
  add(id, name, ok, ok ? 'match' : 'pattern mismatch')
}

function assertNotInFile(id, name, rel, antiPat) {
  const text = read(rel)
  if (!text) {
    add(id, name, false, 'missing ' + rel)
    return
  }
  add(id, name, !antiPat.test(text), antiPat.test(text) ? 'found forbidden pattern' : 'absent as expected')
}

function portOpen(port) {
  return new Promise(resolve => {
    const s = net.connect({ host: '127.0.0.1', port }, () => { s.end(); resolve(true) })
    s.on('error', () => resolve(false))
    s.setTimeout(1500, () => { s.destroy(); resolve(false) })
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
    req.on('timeout', () => { req.destroy(); resolve({ status: 0, body: '', error: 'timeout' }) })
  })
}

async function main() {
  console.log('==== A) 服务端口（可选，离线不判失败） ====')
  for (const [n, p] of [['前端', 4984], ['后端', 48181]]) {
    const ok = await portOpen(p)
    console.log(`[${ok ? 'PASS' : 'SKIP'}] SVC-${p} ${n} - port=${p}${ok ? '' : ' (offline)'}`)
    if (ok) pass++
  }

  console.log('\n==== B) 133 鱼类健康状态：概览文字白色 ====')
  assertFile('133', '健康页概览白字', 'src/views/fish/zkDeathSurvivalRate/system.vue',
    /\.health-page-light \.health-dist-label[\s\S]*color:\s*#ffffff/)
  assertFile('133b', 'AI健康分析白字', 'src/views/fish/zkDeathSurvivalRate/system.vue',
    /\.health-page-light \.ai-health-item-title[\s\S]*color:\s*#ffffff/)

  console.log('\n==== C) 146 死亡量死亡率：须筛选后才展示 ====')
  assertFile('146', 'hasMortalityScopeFilter', 'src/views/fish/zkDeathSurvivalRate/system.vue', /hasMortalityScopeFilter/)
  assertFile('146b', '未筛选提示', 'src/views/fish/zkDeathSurvivalRate/system.vue', /不展示全部鱼池\/网箱汇总/)
  assertFile('146c', '路由带入车间', 'src/views/fish/zkDeathSurvivalRate/system.vue', /applyRouteSiteFilters/)
  assertFile('146d', '总览跳转带workshop', 'src/views/index_daping3.vue', /goMortalityAssessment[\s\S]*query\.workshop/)

  console.log('\n==== D) 147-149 车间数控 ====')
  assertFile('147', 'AI报警卡最小高度', 'src/views/index_daping4.styles.scss', /\.right-panel \.alert-card[\s\S]*min-height:\s*220px/)
  assertFile('148', '闪电图标在标题内', 'src/views/index_daping4.vue', /<div class="title-wrap">[\s\S]*?<div class="logo">[\s\S]*?M22 12h-4l-3 9L9 3l-3 9H2/)
  assertFile('148b', 'logo绝对定位样式', 'src/views/index_daping4.styles.scss', /\.title-wrap[\s\S]*\.logo[\s\S]*position:\s*absolute[\s\S]*left:\s*-30px/)
  assertFile('149', '车间系统上下文标识', 'src/views/index_daping4.vue', /site-context-badge|siteContextLabel/)
  assertFile('149b', '按路由过滤数据', 'src/views/index_daping4.vue', /matchesSiteScope|scopedAlerts|syncSiteScopeFromRoute/)
  assertFile('149c', '嵌入模式标题车间数控', 'src/views/index_daping4.vue', /embedded\s*\?\s*['"]车间数控['"]/)
  assertFile('149d', '跳转死亡页带scope', 'src/views/index_daping4.vue', /goMortalityAssessment[\s\S]*siteRouteQuery/)

  console.log('\n==== E) 150 系统总览：移除右侧3D按钮 ====')
  const d3 = read('src/views/index_daping3.vue')
  const quickBtnsBlock = (d3.match(/<div class="quick-btns">[\s\S]*?<\/div>\s*<\/div>\s*<\/div>\s*<\/header>/) || [''])[0]
  add('150', 'quick-btns内无3D数字孪生', !/3D数字孪生/.test(quickBtnsBlock), quickBtnsBlock.includes('3D数字孪生') ? 'still in quick-btns' : 'removed from quick-btns')
  assertFile('150b', '左侧数字孪生入口保留', 'src/views/index_daping3.vue', /nav-btn btn-blue[\s\S]*数字孪生/)

  console.log('\n==== F) 151 视频监控：告警→报警 ====')
  assertFile('151', '报警监控文案', 'src/views/index_daping2.vue', /报警监控/)
  assertNotInFile('151b', '无告警监控文案', 'src/views/index_daping2.vue', /告警监控/)

  console.log('\n==== G) 运行时（前端在线时） ====')
  const frontUp = await portOpen(4984)
  if (frontUp) {
    const pages = [
      ['/#/index_daping3', '系统总览'],
      ['/#/index_daping2', '视频监控'],
      ['/#/keyshow/workshopCNC?workshop=1&system=1', '车间数控'],
      ['/#/keyshow/zkDeathSurvivalRate?activeTab=mortality', '死亡量死亡率'],
      ['/#/keyshow/fishHealthStatus', '鱼类健康状态']
    ]
    for (const [route, label] of pages) {
      const r = await get('http://127.0.0.1:4984' + route)
      add('RT-' + label, label + '可访问', r.status === 200, 'status=' + r.status + (r.error ? ' ' + r.error : ''))
    }
  } else {
    add('RT', '前端离线跳过运行时', true, 'port 4984 closed')
  }

  console.log('\n==== 汇总 ====')
  console.log(`PASS ${pass}  FAIL ${fail}`)
  if (fails.length) {
    console.log('失败项:')
    fails.forEach(f => console.log(' - ' + f))
    process.exit(1)
  }
}

main().catch(e => {
  console.error(e)
  process.exit(1)
})
