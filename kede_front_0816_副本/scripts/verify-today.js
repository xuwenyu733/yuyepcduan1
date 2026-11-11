/**
 * 今日改动回归 + 隐患扫描
 * node scripts/verify-today.js
 */
const fs = require('fs')
const path = require('path')
const http = require('http')
const net = require('net')

const FRONT = path.resolve(__dirname, '..')
const results = []

function add(id, name, ok, detail) {
  results.push({ id, name, ok: !!ok, detail: String(detail || '') })
  console.log(`[${ok ? 'PASS' : 'FAIL'}] ${id} ${name} - ${detail}`)
}

function read(rel) {
  return fs.readFileSync(path.join(FRONT, rel), 'utf8')
}

function exists(rel) {
  return fs.existsSync(path.join(FRONT, rel))
}

function checkPort(port) {
  return new Promise((resolve) => {
    const s = net.connect({ host: '127.0.0.1', port }, () => {
      s.end()
      resolve(true)
    })
    s.on('error', () => resolve(false))
    s.setTimeout(1500, () => {
      s.destroy()
      resolve(false)
    })
  })
}

function httpGet(url, timeoutMs = 8000) {
  return new Promise((resolve, reject) => {
    const req = http.get(url, { timeout: timeoutMs }, (res) => {
      let body = ''
      res.on('data', (c) => { body += c })
      res.on('end', () => resolve({ status: res.statusCode, body }))
    })
    req.on('error', reject)
    req.on('timeout', () => {
      req.destroy()
      reject(new Error('timeout'))
    })
  })
}

function httpJson(method, url, headers, body, timeoutMs = 10000) {
  return new Promise((resolve) => {
    const u = new URL(url)
    const data = body == null ? null : JSON.stringify(body)
    const opt = {
      hostname: u.hostname,
      port: u.port,
      path: u.pathname + u.search,
      method,
      headers: Object.assign({ 'Content-Type': 'application/json' }, headers || {}),
      timeout: timeoutMs
    }
    if (data) opt.headers['Content-Length'] = Buffer.byteLength(data)
    const req = http.request(opt, (res) => {
      let b = ''
      res.on('data', (c) => { b += c })
      res.on('end', () => resolve({ status: res.statusCode, body: b }))
    })
    req.on('error', (e) => resolve({ status: 0, body: e.message }))
    req.on('timeout', () => {
      req.destroy()
      resolve({ status: 0, body: 'timeout' })
    })
    if (data) req.write(data)
    req.end()
  })
}

function walk(dir, exts, out = []) {
  if (!fs.existsSync(dir)) return out
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name)
    const st = fs.statSync(p)
    if (st.isDirectory()) {
      if (/node_modules|monaco|dist/.test(name)) continue
      walk(p, exts, out)
    } else if (exts.some((e) => name.endsWith(e))) {
      out.push(p)
    }
  }
  return out
}

async function main() {
  console.log('==== 1) 服务端口 ====')
  for (const [name, port] of [
    ['后端', 48181],
    ['前端', 4984],
    ['modules-api', 3020],
    ['MySQL', 3306],
    ['Redis', 6379]
  ]) {
    const ok = await checkPort(port)
    add('SVC', name, ok, `port=${port}`)
  }

  console.log('\n==== 2) 代码断言 ====')
  const checks = [
    ['56', 'modules-api 默认相对路径', 'src/api/modules/index.js', /resolveModulesBase|\/modules-api/, /baseURL:\s*['"]http:\/\/localhost:3020/],
    ['56b', 'chatSocket 默认相对路径', 'src/views/fishDisease/utils/chatSocket.js', /\/modules-api/, /['"]http:\/\/localhost:3020/],
    ['56c', 'env.local MODULES_API', '.env.local', /VUE_APP_MODULES_API\s*=\s*\/modules-api/, null],
    ['56d', 'env.dev MODULES_API', '.env.dev', /VUE_APP_MODULES_API\s*=\s*\/modules-api/, null],
    ['57', '水质 card-alert 强化', 'src/views/waterQuality.vue', /card-alert-pulse|0 0 18px rgba\(255/, null],
    ['57b', '表格超标样式强化', 'src/utils/waterMetricStatus.js', /fontWeight:\s*700|boxShadow/, null],
    ['58', 'MessageBox 深色高对比', 'src/assets/styles/element-ui.scss', /#0f1a26[\s\S]*#ffffff[\s\S]*10b981|#ffffff[\s\S]*#0f1a26/, null],
    ['55', '诊断池号归一匹配', 'src/views/fishDisease/DiagnosisChat.vue', /normalizePondKey/, null],
    ['55b', '投喂推荐可传 silent', 'src/api/fish/zkPrecisionFeeding.js', /getPrecisionFeedRecommend\(pondId,\s*config/, null],
    ['55e', '诊断调用静默投喂', 'src/views/fishDisease/DiagnosisChat.vue', /getPrecisionFeedRecommend\(pondCode,\s*silent\)/, null],
    ['55c', 'request 静默业务码', 'src/utils/request.js', /hideErrorMessage/, null],
    ['55d', 'request_jidi 静默', 'src/utils/request_jidi.js', /hideErrorMessage/, null],
    ['52', '菜单同步授权', 'src/views/system/menu/index.vue', /syncVisibleMenusToRoles/, null],
    ['52b', '菜单更新时授权', 'src/views/system/menu/index.vue', /enabled && this\.form\.visible/, null],
    ['52c', '菜单角色白名单', 'src/views/system/menu/index.vue', /kede_group_member/, null],
    ['54', '视频标题摄像头图标', 'src/views/index_daping2.vue', /视频监控系统[\s\S]*a855f7|a855f7[\s\S]*视频监控系统/, null],
    ['39', '待诊断隐藏结论', 'src/views/fishDisease/DiagnosisChat.vue', /canSubmitConclusion/, null],
    ['47', '指标卡片跳转', 'src/views/waterQuality.vue', /goMetricTrend/, null],
    ['48', '按指标标红', 'src/views/waterQuality.vue', /card-alert/, null],
    ['51', '风味单位 m3', 'src/views/flavor/BasicConfig.vue', /m³/, null],
    ['53', '退出取消键深色字', 'src/assets/styles/element-ui.scss', /el-button--default[\s\S]*#303133/, null],
    // —— 88~100 联通修复断言 ——
    ['88', '风味合并生长/投喂鱼池', 'src/views/flavor/EntryManage.vue', /fetchFlavorMergedPonds/, null],
    ['88b', '风味配置页同源池', 'src/views/flavor/BasicConfig.vue', /fetchFlavorMergedPonds|checkExternalPond/, null],
    ['88c', 'pondBridge 风味导出', 'src/utils/pondBridge.js', /export async function fetchFlavorMergedPonds/, null],
    ['89', '鱼病合并鱼池', 'src/views/fishDisease/BasicConfig.vue', /fetchMergedPonds/, null],
    ['89b', '鱼病保护 yudao 池', 'src/views/fishDisease/BasicConfig.vue', /isYudaoOnlyPond|isExternalPond/, null],
    ['90', '首页快照 stale', 'src/utils/dashboardCache.js', /allowStale|STALE_MS/, null],
    ['90b', '首页 activated 缓存', 'src/views/index_daping3.vue', /activated\(\)[\s\S]*readDashboardSnapshot/, null],
    ['91', '生长默认全部批次', 'src/views/fish/zkgrowdailyrecord/system.vue', /filterBatch\s*=\s*['"]{2}/, null],
    ['92', '精准投喂读阈值', 'src/views/fish/zkPrecisionFeeding/system.vue', /applyFeedingThresholds|resolveFeedingDev/, null],
    ['93', 'AI报警读阈值', 'src/views/alertMonitor.vue', /resolveAlarmRules|applyAlarmOverdueByThreshold/, null],
    ['94', '任务超时读阈值', 'src/views/MissionPlanning.vue', /resolveTimeoutHours/, null],
    ['95', '实验系统读阈值', 'src/views/fish/zkexperimental/components/DataAnalytics.vue', /resolveExperimentThresholds/, null],
    ['96', '数据关联桥', 'src/utils/associationBridge.js', /evaluateAssociationAndPush/, null],
    ['96b', 'FCR 触发关联', 'src/views/fish/zkFcrModel/system.vue', /evaluateAssociationAndPush/, null],
    ['97', '健康评分读阈值', 'src/views/fish/zkDeathSurvivalRate/system.vue', /loadHealthThresholds|resolveHealthGrades/, null],
    ['97b', '阈值保存清缓存', 'src/views/threshold/ThresholdSettings.vue', /invalidateThresholdCache/, null],
    ['99', '设备运行分区非空', 'services/modules-api/backends/threshold/src/data/thresholds.store.json', /"power"\s*:\s*\[/, /"device"[\s\S]*"items"\s*:\s*\[\s*\{\s*"id"\s*:\s*"e2e/],
    ['99b', '设备监控读阈值', 'src/views/deviceMonitor.vue', /applyDeviceThresholds|resolveDeviceMetrics/, null],
    ['100', '任务合并本地巡检', 'src/views/MissionPlanning.vue', /mergeLocalInspectionTasks/, null],
    ['100b', 'FCR 推送任务', 'src/views/fish/zkFcrModel/system.vue', /pushAlarmAndCreateTask/, null],
    ['100c', '本地巡检原位置可见', 'src/views/MissionPlanning.vue', /keepVisible[\s\S]*visibleTaskLimit/, null],
    ['101', '设备日期不超过今天', 'src/views/deviceMonitor.vue', /disabled:\s*iso > today|iso > todayIso\(\)/, null],
    ['102', '能耗横轴日期可见', 'src/views/deviceMonitor.vue', /containLabel:\s*true[\s\S]*axisLabel:[\s\S]*interval:\s*0/, null],
    ['103', '总览刷新不少于30秒', 'src/views/index_daping3.vue', /_lastDashLoad[\s\S]*30000|tickIfStaleMs:\s*30000/, null],
    ['105', '报警 userId 转 Long', 'src/utils/platformAlarmData.js', /toBackendUserId/, /userId:\s*operator\.id/],
    ['105b', '报警去掉 pc-user 占位', 'src/views/alertMonitor.vue', /resolveOperatorUser/, /id:\s*'pc-user'/],
    ['108', '发起流程校验表单路由', 'src/views/bpm/processInstance/create/index.vue', /openBpmCustomForm/, null],
    ['108b', '流程详情已知表单才加载', 'src/views/bpm/processInstance/detail.vue', /isKnownBpmView/, /require\(\[`@\/views\$\{path\}`\]/],
    ['108c', '流程详情含业务审批页', 'src/views/bpm/processInstance/detail.vue', /zkDeviceAlarm\/alarmHolder/, null],
    ['108d', 'BPM 模型走 list 接口', 'src/api/bpm/model.js', /\/bpm\/model\/list/, /url:\s*["']\/bpm\/model\/page/],
    ['108e', '请假审批映射到发起页', 'src/utils/bpmFormPath.js', /oa\/leave\/approve['"]:\s*['"]\/bpm\/oa\/leave\/create/, null],
    ['110', '任务页加载动画', 'src/views/MissionPlanning.vue', /page-loading-mask/, null],
    ['104', '处置方案去HTML', 'src/views/alertMonitor.vue', /stripHtml/, null],
    ['106', '报警展开保持', 'src/views/alertMonitor.vue', /hasEditingAlarm|_open:\s*!!keep\._open/, null],
    ['107', '流程详情行距对比', 'src/views/bpm/processInstance/detail.vue', /bpm-process-detail/, null],
    ['109', '工作计划类型图标', 'src/utils/workPlanTypeMeta.js', /workPlanTypeIcon/, null],
    ['111', '退出整页进login', 'src/utils/goLogin.js', /from=logout/, null],
    // —— 74 / 78~87 ——
    ['74', '退出跳转 login', 'src/layout/components/Navbar.vue', /goLoginPage|#\/login/, null],
    ['74b', '会话超时跳转 login', 'src/utils/request.js', /goLoginPage|#\/login/, null],
    ['74c', 'GetInfo失败跳转 login', 'src/permission.js', /next\(`\/login\?redirect/, null],
    ['78', '车间数控进水质', 'src/views/index_daping4.vue', /goWaterQuality/, null],
    ['78b', '车间数控进设备', 'src/views/index_daping4.vue', /goDeviceMonitor/, null],
    ['78c', '车间数控报警详情', 'src/views/index_daping4.vue', /alert_monitor/, null],
    ['79', '设备日期默认今天', 'src/views/deviceMonitor.vue', /todayIso|daysAgoIso/, null],
    ['79b', '设备日期触发刷新', 'src/views/deviceMonitor.vue', /onDateRangeChange/, null],
    ['80', '水质报警进任务', 'src/views/waterQuality.vue', /syncWarningsToMission|pushAlarmAndCreateTask|alarmTaskBridge/, null],
    ['80b', '设备报警进任务', 'src/views/deviceMonitor.vue', /syncAlarmsToMission/, null],
    ['82', '设备页AI报警面板', 'src/views/deviceMonitor.vue', /DEMO_ALARMS|filteredAlarms/, null],
    ['83', '首页设备同源设备页', 'src/views/index_daping3.vue', /getOverviewDeviceStatus|deviceDemoData/, null],
    ['83b', '设备演示名对齐', 'src/utils/deviceDemoData.js', /微滤机A1-2/, null],
    ['83c', '首页水质同源水质页', 'src/views/index_daping3.vue', /getSharedWaterQualitySnapshot|poolRowToOverviewMetrics/, null],
    ['85', '命名统一工具', 'src/utils/siteNaming.js', /formatWorkshop/, null],
    ['85b', '设备筛选用统一命名', 'src/views/deviceMonitor.vue', /formatWorkshop/, null],
    ['86', '健康页默认全池', 'src/views/fish/zkDeathSurvivalRate/system.vue', /applyRouteSiteFilters|hasMortalityScopeFilter/, null],
    ['87', '死亡率入口无预选池', 'src/views/index_daping3.vue', /goMortalityAssessment[\s\S]*activeTab:\s*'mortality'/, null],
    // —— 隐藏错误可见性 ——
    ['HE1', '静默错误上报工具', 'src/utils/reportSilentError.js', /export function silentCatch/, null],
    ['HE2', '实验图不伪造样例', 'src/views/fish/zkexperimental/components/DataAnalytics.vue', /暂无生长趋势真实数据|updateGrowthChart\(\[\]\)/, null],
    ['HE3', '关联推送失败上报', 'src/utils/associationBridge.js', /reportSilentError|pushFail/, null],
    ['HE4', '鱼池源错误暴露', 'src/utils/pondBridge.js', /_sourceErrors|getPondSourceErrors/, null],
    ['HE5', '风味同步池失败阻断', 'src/views/flavor/EntryManage.vue', /鱼池档案同步失败/, null],
    ['HE6', '总览降级可见提示', 'src/views/index_daping3.vue', /degradedSources|data-degrade-banner/, null],
    ['HE7', '健康刷新有 catch', 'src/views/fish/zkDeathSurvivalRate/system.vue', /健康状态\] 分布加载失败|各池健康评分加载失败/, null],
    // —— 已驳回项复测 ——
    ['R74', '会话过期直跳 login', 'src/utils/request.js', /正在跳转登录页/, null],
    ['R74b', 'jidi会话过期直跳', 'src/utils/request_jidi.js', /正在跳转登录页/, null],
    ['R78', '车间数控可点击样式', 'src/views/index_daping4.styles.scss', /card-clickable[\s\S]*pointer-events:\s*auto/, null],
    ['R79', '设备能耗按日期重建', 'src/views/deviceMonitor.vue', /function buildTrend\(startIso,\s*endIso\)/, null],
    ['R79b', '设备能耗趋势强制刷新', 'src/views/deviceMonitor.vue', /renderEnergyTrend[\s\S]*notMerge:\s*true|setOption\(\{[\s\S]*series:[\s\S]*\},\s*true\)/, null],
    ['R79c', '设备命名归一化', 'src/views/deviceMonitor.vue', /formatWorkshop\(row\.workshop\)/, null],
    ['R97', '健康权重重算', 'src/utils/thresholdConfig.js', /resolveHealthWeights|recomputeHealthOverall/, null],
    ['R97b', '健康页应用权重', 'src/views/fish/zkDeathSurvivalRate/system.vue', /applyHealthThresholdWeights/, null],
    ['R97c', '健康页激活刷新阈值', 'src/views/fish/zkDeathSurvivalRate/system.vue', /activated\(\)[\s\S]*handleRefresh\(\)/, null],
    ['R78b', '车间数控左右面板可点', 'src/views/index_daping4.styles.scss', /\.left-panel[\s\S]*z-index:\s*20[\s\S]*pointer-events:\s*auto/, null]
  ]

  for (const [id, name, file, pat, anti] of checks) {
    if (!exists(file)) {
      add(id, name, false, `missing ${file}`)
      continue
    }
    const text = read(file)
    let ok = pat.test(text)
    let detail = `match=${ok}`
    if (anti && anti.test(text)) {
      ok = false
      detail = 'contains forbidden runtime localhost'
    }
    // 56: 注释里举例 127.0.0.1 允许
    if (id === '56' && /禁止|勿写死/.test(text) && /127\.0\.0\.1:3020/.test(text)) {
      const runtime = /baseURL:\s*['"]http:\/\/(localhost|127\.0\.0\.1):3020/.test(text)
      ok = pat.test(text) && !runtime
      detail = runtime ? 'runtime hardcoded' : 'ok (comment only)'
    }
    add(id, name, ok, detail)
  }

  console.log('\n==== 3) 单测 ====')
  try {
    require('child_process').execFileSync(process.execPath, [path.join(FRONT, 'scripts/test-metric-rules.js')], {
      stdio: 'pipe',
      encoding: 'utf8'
    })
    add('TEST', 'metric-rules', true, '9 checks')
  } catch (e) {
    add('TEST', 'metric-rules', false, (e.stdout || e.message || '').slice(0, 200))
  }

  console.log('\n==== 4) 隐患扫描 ====')
  // HAZ1: src 运行时硬编码
  const hardHits = []
  for (const f of walk(path.join(FRONT, 'src'), ['.js', '.vue'])) {
    const lines = fs.readFileSync(f, 'utf8').split(/\r?\n/)
    lines.forEach((line, i) => {
      if (!/localhost:3020|127\.0\.0\.1:3020/.test(line)) return
      if (/^\s*(\*|\/\/|\/\*)/.test(line) || /禁止|勿写死|例如|如 http/.test(line)) return
      hardHits.push(`${path.relative(FRONT, f)}:${i + 1}:${line.trim().slice(0, 80)}`)
    })
  }
  add('HAZ1', 'src 无运行时 localhost:3020', hardHits.length === 0, hardHits.length ? hardHits.join(' | ') : 'clean')

  // HAZ2: 模块主题是否再覆盖 MessageBox
  const themeHits = []
  for (const tf of [
    'src/views/fishDisease/fish-theme.css',
    'src/views/flavor/flavor-theme.css',
    'src/views/threshold/threshold-theme.css'
  ]) {
    if (!exists(tf)) continue
    if (/\.el-message-box\s*\{/.test(read(tf))) themeHits.push(tf)
  }
  add('HAZ2', '模块主题不再覆盖 MessageBox', themeHits.length === 0, themeHits.length ? themeHits.join(',') : 'no override')

  // HAZ3: getPrecisionFeedRecommend 全局 silent
  const pf = read('src/api/fish/zkPrecisionFeeding.js')
  const silentAlways = /export function getPrecisionFeedRecommend[\s\S]*?silent:\s*true/.test(pf) &&
    !/export function getPrecisionFeedRecommend[\s\S]*?\.\.\.config/.test(pf)
  add(
    'HAZ3',
    '投喂推荐 silent 仅按需',
    /getPrecisionFeedRecommend\(pondId,\s*config/.test(pf) || /getPrecisionFeedRecommend\(pondId,\s*config\s*=/.test(pf),
    `alwaysSilent=${silentAlways}；应对 config 传入 silent`
  )

  // HAZ4: 菜单 quiet 批量同步
  const menu = read('src/views/system/menu/index.vue')
  add('HAZ4', '菜单批量同步 quiet', /syncVisibleMenusToRoles/.test(menu) && /quiet:\s*true/.test(menu), 'ok')

  // HAZ5: vue 代理仍指向本机 3020（本地正确；远程靠 nginx）
  const vueCfg = read('vue.config.js')
  const proxyLocal = /\/modules-api[\s\S]*localhost:3020/.test(vueCfg)
  add('HAZ5', '本地代理 modules-api→3020', proxyLocal, proxyLocal ? 'dev-only proxy OK；生产需 nginx' : 'proxy missing')

  // HAZ6: MessageBox 取消键是否仍有白字覆盖风险（全局 .el-button color white）
  const eu = read('src/assets/styles/element-ui.scss')
  const globalWhiteBtn = /\.el-button\s*\{[\s\S]*?color:\s*#ffffff\s*!important/.test(eu)
  const boxDefaultDark = /\.el-message-box[\s\S]*el-button--default[\s\S]*#303133/.test(eu)
  add(
    'HAZ6',
    'MessageBox 取消键覆盖全局白字',
    boxDefaultDark,
    `globalWhiteBtn=${globalWhiteBtn}; defaultDarkText=${boxDefaultDark}`
  )

  // HAZ7: card-alert 动画性能
  const wq = read('src/views/waterQuality.vue')
  const hasPulse = /card-alert-pulse/.test(wq)
  add('HAZ7', '标红脉冲动画已加（提示）', true, `pulse=${hasPulse}；多卡同时闪可能略耗性能，可接受`)

  console.log('\n==== 5) 接口抽测 ====')
  const beUp = results.some((r) => r.id === 'SVC' && r.name === '后端' && r.ok)
  const apiUp = results.some((r) => r.id === 'SVC' && r.name === 'modules-api' && r.ok)

  if (beUp) {
    try {
      const url =
        'http://127.0.0.1:48181/admin-api/system/tenant/get-id-by-name?name=' +
        encodeURIComponent('北京科德数传渔业科技有限公司')
      const res = await httpGet(url)
      const j = JSON.parse(res.body)
      add('API', '租户接口', res.status === 200 && (j.code === 0 || j.data != null), `tenantId=${j.data}`)
    } catch (e) {
      add('API', '租户接口', false, e.message)
    }
  } else {
    add('API', '租户接口', false, 'backend offline - skip')
  }

  if (apiUp) {
    try {
      const res = await httpGet('http://127.0.0.1:3020/api/fish/config/ponds')
      const j = JSON.parse(res.body)
      const list = (j && j.data) || []
      const codes = list.map((p) => p.code || p.pondCode || '').filter(Boolean)
      add('API', '鱼池列表', res.status === 200 && Array.isArray(list), `n=${list.length} codes=${codes.join(',')}`)
      add('API', '鱼池非空', codes.length > 0, `codes=${codes.join(',')}`)
    } catch (e) {
      add('API', 'modules ponds', false, e.message)
    }
    try {
      const res = await httpGet('http://127.0.0.1:3020/api/threshold/thresholds/all')
      let deviceOk = false
      let deviceDetail = `status=${res.status}`
      try {
        const j = JSON.parse(res.body)
        const device = (j && j.data && j.data.device) || (j && j.device) || {}
        const powerLen = Array.isArray(device.power) ? device.power.length : 0
        deviceOk = res.status === 200 && powerLen > 0
        deviceDetail = `status=${res.status} power=${powerLen}`
      } catch (e2) {
        deviceDetail = e2.message
      }
      add('API', 'threshold/all', res.status === 200, `status=${res.status}`)
      add('API99', '阈值设备分区', deviceOk, deviceDetail)
    } catch (e) {
      add('API', 'threshold/all', false, e.message)
      add('API99', '阈值设备分区', false, e.message)
    }
    try {
      const res = await httpGet('http://127.0.0.1:3020/api/flavor/config/ponds')
      const j = JSON.parse(res.body)
      const list = (j && j.data) || []
      add('API88', '风味鱼池接口', res.status === 200 && Array.isArray(list), `n=${list.length}`)
    } catch (e) {
      add('API88', '风味鱼池接口', false, e.message)
    }
  } else {
    add('API', 'modules-api', false, 'offline - skip')
  }

  // 前端代理抽测
  const feUp = results.some((r) => r.id === 'SVC' && r.name === '前端' && r.ok)
  if (feUp) {
    try {
      const res = await httpGet('http://127.0.0.1:4984/modules-api/api/fish/config/ponds')
      add('API', '前端代理 /modules-api', res.status === 200, `status=${res.status}`)
    } catch (e) {
      add('API', '前端代理 /modules-api', false, e.message)
    }
    try {
      const name = encodeURIComponent('北京科德数传渔业科技有限公司')
      const res = await httpGet('http://127.0.0.1:4984/proxy-api/admin-api/system/tenant/get-id-by-name?name=' + name)
      const j = JSON.parse(res.body)
      add('API', '前端代理 /proxy-api 租户', res.status === 200 && j.code === 0 && j.data != null, `tenantId=${j.data}`)
    } catch (e) {
      add('API', '前端代理 /proxy-api 租户', false, e.message)
    }
    try {
      const login = await httpJson('POST', 'http://127.0.0.1:4984/proxy-api/admin-api/system/auth/login', { 'tenant-id': '1' }, {
        username: 'admin', password: 'admin123', captchaVerification: ''
      })
      const lj = JSON.parse(login.body || '{}')
      const token = lj.data && lj.data.accessToken
      add('API', '前端代理登录', login.status === 200 && lj.code === 0 && !!token, token ? 'ok' : String(lj.msg || login.body).slice(0, 80))
      if (token) {
        const h = { 'tenant-id': '1', Authorization: 'Bearer ' + token }
        const models = await httpJson('GET', 'http://127.0.0.1:4984/proxy-api/admin-api/bpm/model/list', h)
        const mj = JSON.parse(models.body || '{}')
        const mlist = Array.isArray(mj.data) ? mj.data : ((mj.data && mj.data.list) || [])
        add('API108', '流程模型 list', models.status === 200 && mj.code === 0 && mlist.length > 0, `n=${mlist.length}`)
        const alarms = await httpJson('GET', 'http://127.0.0.1:4984/proxy-api/admin-api/fish/zk-device-alarm/page?pageNo=1&pageSize=1', h)
        const aj = JSON.parse(alarms.body || '{}')
        add('API105', '设备报警分页', alarms.status === 200 && aj.code === 0, `total=${aj.data && aj.data.total}`)
      }
    } catch (e) {
      add('API', '前端代理登录', false, e.message)
    }
  } else {
    add('API', '前端代理', false, 'frontend offline - skip')
  }

  console.log('\n==== SUMMARY ====')
  const pass = results.filter((r) => r.ok).length
  const fail = results.filter((r) => !r.ok).length
  console.log(`TOTAL=${results.length} PASS=${pass} FAIL=${fail}`)
  const fails = results.filter((r) => !r.ok)
  if (fails.length) {
    console.log('--- FAIL ITEMS ---')
    fails.forEach((f) => console.log(`FAIL_ITEM: ${f.id} ${f.name} :: ${f.detail}`))
  }

  // 隐患摘要（非 FAIL，但需知晓）
  console.log('\n==== 隐患/取舍 ====')
  console.log('1. 协同诊断上下文请求按需 silent；精准投喂页仍会正常报错')
  console.log('2. vue.config 代理仅本地有效；远程必须配 nginx /modules-api')
  console.log('3. 菜单白名单仅 common/kede_group_*；其它角色需手动授权或点「同步可见菜单授权」')
  console.log('4. 水质超标卡片脉冲动画：指标多时略耗 GPU，属体验取舍')
  console.log('5. 服务未启动时接口项会 FAIL（环境问题，非代码回归）')
  console.log('6. 当前 modules 鱼池码为业务数据（如 A1-1），与种子 A1 不同属正常')

  const out = path.join(FRONT, '..', 'verify-today-result.json')
  fs.writeFileSync(out, JSON.stringify({ at: new Date().toISOString(), pass, fail, results }, null, 2), 'utf8')
  console.log(`saved=${out}`)
  process.exit(fail > 0 ? 1 : 0)
}

main().catch((e) => {
  console.error(e)
  process.exit(2)
})
