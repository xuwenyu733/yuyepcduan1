/**
 * 校验图片问题清单修复：113-127 / 88 / 116 / 117 / 121-124 等
 * 用法：node scripts/verify-issue-batch.js
 */
const fs = require('fs')
const path = require('path')
const http = require('http')
const net = require('net')
const { spawnSync } = require('child_process')

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

function assertFile(id, name, rel, pat) {
  const text = read(rel)
  add(id, name, !!text && pat.test(text), text ? 'match' : 'missing ' + rel)
}

function portOpen(port) {
  return new Promise(resolve => {
    const s = net.connect({ host: '127.0.0.1', port }, () => { s.end(); resolve(true) })
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
    req.on('timeout', () => { req.destroy(); resolve({ status: 0, body: '', error: 'timeout' }) })
  })
}

function mysql(sql) {
  const bin = 'C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe'
  // 密码使用引号形式传入 CLI（与 application-local.yaml 中 password: "060330" 一致）
  const r = spawnSync(bin, ['-uroot', '-p060330', 'yuydao', '-N', '-e', sql], { encoding: 'utf8' })
  return String(r.stdout || '').trim()
}

async function main() {
  console.log('==== A) 端口 ====')
  for (const [n, p] of [['后端', 48181], ['前端', 4984], ['modules-api', 3020], ['MySQL', 3306]]) {
    add('SVC', n, await portOpen(p), 'port=' + p)
  }

  console.log('\n==== B) 代码断言 ====')
  assertFile('113c', '死亡率页存在', 'src/views/fish/zkDeathSurvivalRate/system.vue', /getZkDeathSurvivalRateStatistics/)
  assertFile('115', '任务懒加载', 'src/views/MissionPlanning.vue', /lazySecondary/)
  assertFile('116', '逾期紧急隐藏转任务', 'src/views/alertMonitor.vue', /status === 'overdue' && item\.level === 'urgent'/)
  assertFile('117', '语段去HTML', 'src/views/fish/trainingKnowledge/index.vue', /plainContent/)
  assertFile('117b', '请假说明去HTML', 'src/views/fish/zkAskforLeave/index.vue', /plainLeaveContent|html2Text/)
  assertFile('118', '生物资产重试', 'src/views/fish/zkBiologicalAsset/system.vue', /activated\(|正在重试|暂时为空/)
  assertFile('119', '健康分布按阈值', 'src/views/fish/zkDeathSurvivalRate/system.vue', /totalSubHealthy[\s\S]*excellentMin/)
  assertFile('120', '健康布局分行', 'src/views/fish/zkDeathSurvivalRate/system.vue', /各鱼池数量对比/)
  assertFile('121', '实验模板权限隐藏', 'src/views/experiment/ExperimentShell.vue', /adminOnly:\s*true[\s\S]*templates|templates[\s\S]*adminOnly/)
  assertFile('122', '知识库placeholder', 'src/views/fishDisease/KnowledgeBase.vue', /placeholder="如：细菌性烂鳃"/)
  assertFile('123', '知识树侧栏', 'src/views/fishDisease/FishShell.vue', /knowledgeTree/)
  assertFile('123b', '知识树页', 'src/views/fishDisease/KnowledgeTree.vue', /zk-konwledge-tree/)
  assertFile('123c', '平台静态路由', 'src/router/index.js', /path:\s*'\/knowledge\/zk-konwledge-tree'/)
  assertFile('123d', '鱼病快捷跳转', 'src/router/index.js', /fish-disease\/knowledge-tree/)
  assertFile('123e', '普通角色不含知识树菜单权限', 'src/views/fishDisease/FishShell.vue', /adminOnly:\s*true/)
  assertFile('124', '菜单开放普通用户', 'src/views/system/menu/index.vue', /openToCommonUsers/)
  assertFile('125', '水质共用筛选项', 'src/views/waterQuality.vue', /loadSharedSiteOptions|fetchMergedPonds/)
  assertFile('126', '设备共用筛选项', 'src/views/deviceMonitor.vue', /loadSharedSiteOptions|fetchMergedPonds/)
  assertFile('127', '新增知识角色限制', 'src/views/fishDisease/KnowledgeBase.vue', /canEditKnowledge/)
  assertFile('127b', '知识库可修改', 'src/views/fishDisease/KnowledgeBase.vue', /updateKnowledge|openEdit/)
  assertFile('127c', '知识库可删除', 'src/views/fishDisease/KnowledgeBase.vue', /deleteKnowledge|handleDelete/)
  assertFile('127d', '知识库改删API', 'src/api/fishDisease.js', /updateKnowledge[\s\S]*deleteKnowledge/)
  assertFile('127e', '知识库管理页', 'src/views/fishDisease/KnowledgeManage.vue', /知识库管理[\s\S]*handleDelete|openEdit/)
  assertFile('127f', '管理页侧栏仅管理员', 'src/views/fishDisease/FishShell.vue', /knowledgeManage[\s\S]*adminOnly:\s*true/)
  assertFile('128', '协同诊断按角色', 'src/views/fishDisease/DiagnosisChat.vue', /diagnosisRole|isDiagnosisExpert|canAcceptDiagnosis/)
  assertFile('129', '设备正常运行数据', 'src/utils/deviceDemoData.js', /countRunningDemoDevices|投饵机A1|紫外线消毒器A1/)
  assertFile('130', '接收任务即时生效', 'src/views/MissionPlanning.vue', /applyLocalTaskStatus|scheduleSoftReload/)
  assertFile('88', '风味合并池', 'src/views/flavor/EntryManage.vue', /fetchFlavorMergedPonds/)
  assertFile('88p', '采购对齐旧系统', 'src/views/flavor/PurchaseManage.vue', /loadMergedPurchases|fromPurify/)
  assertFile('88b', '入池对齐旧档案', 'src/views/flavor/EntryManage.vue', /loadMergedEntries|syncEntryToPurify/)
  assertFile('88c', '字典标签桥接', 'src/utils/flavorPurifyBridge.js', /resolvePurifyCategory|mergePreferPurify|syncPurchaseToPurify/)
  assertFile('81', '水质阈值联动', 'src/views/waterQuality.vue', /getWaterThresholdConfig|evaluateAssociationAndPush/)
  assertFile('98', '水质消费阈值', 'src/views/waterQuality.vue', /loadWaterThresholds|waterThresholdConfig/)

  console.log('\n==== C) 数据库 ====')
  try {
    const death = parseInt(mysql('SELECT COUNT(*) FROM zk_death_survival_rate WHERE deleted=0'), 10)
    add('DB113', '死亡量演示数据', death >= 8, 'count=' + death)
    const e = parseInt(mysql('SELECT COUNT(*) FROM zk_pond_health WHERE deleted=0 AND overall_rating>=85'), 10)
    const g = parseInt(mysql('SELECT COUNT(*) FROM zk_pond_health WHERE deleted=0 AND overall_rating>=70 AND overall_rating<85'), 10)
    const w = parseInt(mysql('SELECT COUNT(*) FROM zk_pond_health WHERE deleted=0 AND overall_rating<70'), 10)
    add('DB119', '健康三档齐全', e > 0 && g > 0 && w > 0, `E=${e} G=${g} W=${w}`)
    const tree = parseInt(mysql('SELECT COUNT(*) FROM system_role_menu WHERE role_id=161 AND menu_id=5079'), 10)
    add('DB123', '普通角色161无知识树菜单', tree === 0, 'count=' + tree)
    const treeAdmin = parseInt(mysql('SELECT COUNT(*) FROM system_role_menu WHERE role_id IN (1,159,160) AND menu_id=5079'), 10)
    add('DB123b', '管理角色仍有知识树菜单', treeAdmin >= 1, 'count=' + treeAdmin)
    const menuPath = mysql("SELECT CONCAT(IFNULL((SELECT path FROM system_menu WHERE id=5060),''),'/',path) FROM system_menu WHERE id=5079")
    add('DB123c', '菜单全路径', /knowledge.*zk-konwledge-tree/.test(menuPath), menuPath)
    const htmlRows = parseInt(mysql("SELECT COUNT(*) FROM zk_training_knowledge WHERE deleted=0 AND knowledge_content LIKE '%<%'"), 10)
    add('DB117', '语段含HTML样例仍在库', htmlRows >= 1, 'htmlRows=' + htmlRows)
    const leaveHtml = parseInt(mysql("SELECT COUNT(*) FROM zk_askfor_leave WHERE deleted=0 AND leave_content LIKE '%<%'"), 10)
    add('DB117b', '请假含HTML样例仍在库', leaveHtml >= 1, 'leaveHtml=' + leaveHtml)
  } catch (e) {
    add('DB', 'mysql', false, e.message)
  }

  console.log('\n==== C2) 设备演示数据 ====')
  try {
    const text = read('src/utils/deviceDemoData.js')
    const running = (text.match(/status:\s*'running'/g) || []).length
    const total = (text.match(/status:\s*'(running|fault|stopped)'/g) || []).length
    add('129r', '正常运行设备数', running >= 30, 'running=' + running + ' total=' + total)
  } catch (e) {
    add('129r', '正常运行设备数', false, e.message)
  }

  console.log('\n==== D) API 冒烟 ====')
  try {
    const tenantName = encodeURIComponent('北京科德数传渔业科技有限公司')
    const t = await get('http://127.0.0.1:48181/admin-api/system/tenant/get-id-by-name?name=' + tenantName)
    let ok = false
    try { const j = JSON.parse(t.body || '{}'); ok = t.status === 200 && j.code === 0 } catch (e) {}
    add('API', '后端租户', ok, 'status=' + t.status)
  } catch (e) { add('API', '后端租户', false, e.message) }

  try {
    const fe = await get('http://127.0.0.1:4984/')
    add('API', '前端首页', fe.status === 200 && (fe.body || '').length > 500, 'len=' + ((fe.body || '').length))
  } catch (e) { add('API', '前端首页', false, e.message) }

  try {
    const ponds = await get('http://127.0.0.1:3020/api/fish/config/ponds')
    let n = 0
    try { const j = JSON.parse(ponds.body || '{}'); n = (j.data || []).length } catch (e) {}
    add('API', 'modules鱼池', ponds.status === 200 && n >= 0, 'n=' + n)
  } catch (e) { add('API', 'modules鱼池', false, e.message) }

  console.log('\n==== SUMMARY ====')
  console.log(`TOTAL=${pass + fail} PASS=${pass} FAIL=${fail}`)
  if (fails.length) {
    console.log('FAILED:')
    fails.forEach(f => console.log(' - ' + f))
  }
  process.exit(fail ? 1 : 0)
}

main().catch(e => { console.error(e); process.exit(1) })
