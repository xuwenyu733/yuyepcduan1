/**
 * AI 报警 / 任务计划系统对接桥
 * - 写入 yudao zk_device_alarm（预警监控）
 * - 写入 wiidu test_table_alarm_record + test_table_task_plan（任务与计划可接收执行）
 * - 同步写入 localStorage（兼容旧版 taskPlan.vue）
 */
import { createZkDeviceAlarm, getZkDeviceAlarmPage } from '@/api/fish/zkDeviceAlarm'
import { isTaskInUserSiteScope, isUserDeptScopeDenied, peekSharedSiteOptions, resolveCurrentUserDeptId, isPlatformUnscopedAdmin } from '@/utils/siteOptions'

export const ROUTES = {
  ALERT: '/alert_monitor',
  TASK: '/mission_planning',
  INSPECT: '/keyshow/InspectionList',
}

export const TASK_STORAGE_KEY = 'kede_task_plan_v1'
export const ALARM_DEDUP_KEY = 'kede_biz_alarm_dedup_v1'
export const URGENT_OVERRIDE_KEY = 'kede_alarm_urgent_override_v1'

/** 本机 / 局域网打开前端时走同域代理或直连本地 Tomcat，避免打到远程生产 PMIS */
function isLocalOrLanHost(host) {
  if (!host) return false
  const h = String(host).toLowerCase()
  if (h === 'localhost' || h === '127.0.0.1' || h === '0.0.0.0' || h === '[::1]') return true
  if (/^10\.\d{1,3}\.\d{1,3}\.\d{1,3}$/.test(h)) return true
  if (/^192\.168\.\d{1,3}\.\d{1,3}$/.test(h)) return true
  if (/^172\.(1[6-9]|2\d|3[0-1])\.\d{1,3}\.\d{1,3}$/.test(h)) return true
  if (/^169\.254\.\d{1,3}\.\d{1,3}$/.test(h)) return true
  return false
}

function normalizeDatainfBase(url) {
  const s = String(url || '').trim()
  if (!s) return ''
  return s.endsWith('/') ? s : s + '/'
}

/**
 * PMIS datainf 根路径（末尾带 /）。
 * - 开发 / 本机 / 局域网：同源 /wiidu-datainf/（vue-devServer 或 nginx 反代到 Tomcat）
 * - 可用 VUE_APP_WIIDU_DATAINF 覆盖（仅本机调试直连时用，勿用于多人静态包）
 * - 公网生产包：仍指向远程 wiidu
 */
export function getWiiduApiBase() {
  if (typeof process !== 'undefined' && process.env && process.env.VUE_APP_WIIDU_DATAINF) {
    return normalizeDatainfBase(process.env.VUE_APP_WIIDU_DATAINF)
  }
  const isDev = typeof process !== 'undefined' && process.env && process.env.NODE_ENV === 'development'
  const host = typeof window !== 'undefined' && window.location ? window.location.hostname : ''
  if (isDev || (host && isLocalOrLanHost(host))) {
    // 同域代理：同事访问服务器 IP 时仍打到服务器上的 Tomcat，而不是对方本机 8088
    return '/wiidu-datainf/'
  }
  return 'https://www.wiidu.com.cn/zx/datainf/'
}
const ALARM_TABLE = 'test_table_alarm_record'
const TASK_TABLE = 'test_table_task_plan'

const PERIOD_MAP = { day: '日计划', week: '周计划', month: '月计划', year: '年计划' }

/** 去重窗口：同池同标题 30 分钟内不重复推送 */
const DEDUP_MS = 30 * 60 * 1000

function readDedupMap() {
  try {
    return JSON.parse(sessionStorage.getItem(ALARM_DEDUP_KEY) || '{}') || {}
  } catch (e) {
    return {}
  }
}

function writeDedupMap(map) {
  try {
    sessionStorage.setItem(ALARM_DEDUP_KEY, JSON.stringify(map))
  } catch (e) { /* ignore */ }
}

function dedupKey({ pondDetailNum, title, source }) {
  return [source || '', pondDetailNum || '', title || ''].join('||')
}

function isDup(payload) {
  const key = dedupKey(payload)
  const map = readDedupMap()
  const ts = map[key]
  if (ts && Date.now() - ts < DEDUP_MS) return true
  return false
}

function markDup(payload) {
  const key = dedupKey(payload)
  const map = readDedupMap()
  map[key] = Date.now()
  const now = Date.now()
  Object.keys(map).forEach(k => {
    if (now - map[k] > DEDUP_MS) delete map[k]
  })
  writeDedupMap(map)
}

function esc(value) {
  return String(value == null ? '' : value)
    .replace(/\\/g, '\\\\')
    .replace(/'/g, "\\'")
    .replace(/\u0000/g, '')
}

function q(value) {
  return "'" + esc(value) + "'"
}

function mapPriority(level) {
  if (level === 'urgent' || level === 'danger') return 'urgent'
  if (level === 'normal' || level === 'daily') return 'normal'
  return 'priority'
}

function mapCategory(category) {
  const c = String(category || '').toLowerCase()
  if (c === 'water') return 'water'
  if (c === 'equipment' || c === 'device' || c === 'equip') return 'equip'
  if (c === 'person' || c === 'human') return 'person'
  return 'fish'
}

function buildAlarmCode(payload = {}) {
  if (payload.alarmCode) return String(payload.alarmCode).slice(0, 64)
  const stamp = Date.now().toString(36).toUpperCase()
  const src = String(payload.source || 'biz').replace(/[^a-zA-Z0-9]/g, '').slice(0, 8) || 'BIZ'
  return ('ZK-AI-' + src + '-' + stamp).slice(0, 64)
}

function nowSql() {
  const d = new Date()
  const p = n => String(n).padStart(2, '0')
  return (
    d.getFullYear() +
    '-' + p(d.getMonth() + 1) +
    '-' + p(d.getDate()) +
    ' ' + p(d.getHours()) +
    ':' + p(d.getMinutes()) +
    ':' + p(d.getSeconds())
  )
}

function alarmStorageKey(item) {
  if (!item) return ''
  return String(item.alarm_code || item.alarmCode || item.Id || item.id || '').trim()
}

export function markAlarmUrgentLocal(item, taskCode) {
  const key = alarmStorageKey(item)
  if (!key) return
  try {
    const map = JSON.parse(localStorage.getItem(URGENT_OVERRIDE_KEY) || '{}') || {}
    map[key] = {
      level: 'urgent',
      priority: 'urgent',
      taskCode: taskCode || '',
      at: Date.now()
    }
    localStorage.setItem(URGENT_OVERRIDE_KEY, JSON.stringify(map))
  } catch (e) { /* ignore */ }
}

export function applyAlarmUrgentOverride(alarm) {
  if (!alarm) return alarm
  const key = alarmStorageKey(alarm)
  if (!key) return alarm
  try {
    const map = JSON.parse(localStorage.getItem(URGENT_OVERRIDE_KEY) || '{}') || {}
    const hit = map[key]
    if (!hit) return alarm
    alarm.level = 'urgent'
    alarm.priority = 'urgent'
    if (hit.taskCode && !alarm.source_task_code && !alarm.sourceTaskCode) {
      alarm.source_task_code = hit.taskCode
      alarm.sourceTaskCode = hit.taskCode
    }
  } catch (e) { /* ignore */ }
  return alarm
}

async function wiiduQuery(sql) {
  const controller = typeof AbortController !== 'undefined' ? new AbortController() : null
  const timer = controller ? setTimeout(function () { controller.abort() }, 8000) : null
  try {
    const res = await fetch(getWiiduApiBase() + 'mysql/getDataListBySql.jsp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sqlstring: [sql] }),
      signal: controller ? controller.signal : undefined
    })
    if (!res.ok) throw new Error('wiidu query HTTP ' + res.status)
    const raw = await res.json()
    assertWiiduOk(raw, 'query')
    const groups = raw && raw.data
    if (!Array.isArray(groups)) return []
    return Array.isArray(groups[0]) ? groups[0] : []
  } finally {
    if (timer) clearTimeout(timer)
  }
}

async function wiiduQueryMulti(sqls) {
  const list = Array.isArray(sqls) ? sqls.filter(Boolean) : []
  if (!list.length) return []
  const controller = typeof AbortController !== 'undefined' ? new AbortController() : null
  const timer = controller ? setTimeout(function () { controller.abort() }, 10000) : null
  try {
    const res = await fetch(getWiiduApiBase() + 'mysql/getDataListBySql.jsp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sqlstring: list }),
      signal: controller ? controller.signal : undefined
    })
    if (!res.ok) throw new Error('wiidu query HTTP ' + res.status)
    const raw = await res.json()
    assertWiiduOk(raw, 'query')
    const groups = raw && raw.data
    if (!Array.isArray(groups)) return list.map(function () { return [] })
    return list.map(function (_, i) {
      return Array.isArray(groups[i]) ? groups[i] : []
    })
  } finally {
    if (timer) clearTimeout(timer)
  }
}

/** JSP 成功码 10000；失败曾误用 10000+「程序运行出错」，需同时看 msg */
function assertWiiduOk(raw, kind) {
  const msg = raw && raw.msg != null ? String(raw.msg) : ''
  if (msg.indexOf('出错') >= 0 || msg.indexOf('失败') >= 0) {
    throw new Error(msg || ('wiidu ' + kind + ' failed'))
  }
  const code = raw && raw.code
  if (code !== undefined && Number(code) !== 10000 && Number(code) !== 200) {
    throw new Error(msg || ('wiidu ' + kind + ' failed'))
  }
}

async function wiiduWrite(sqls) {
  const controller = typeof AbortController !== 'undefined' ? new AbortController() : null
  const timer = controller ? setTimeout(function () { controller.abort() }, 8000) : null
  try {
    const res = await fetch(getWiiduApiBase() + 'mysql/updateInfo.jsp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sqlstring: sqls }),
      signal: controller ? controller.signal : undefined
    })
    if (!res.ok) throw new Error('wiidu write HTTP ' + res.status)
    const raw = await res.json()
    assertWiiduOk(raw, 'write')
  } finally {
    if (timer) clearTimeout(timer)
  }
}

/**
 * 推送业务报警到系统表 zk_device_alarm
 * @returns {{ ok: boolean, skipped?: boolean, id?: number, error?: string }}
 */
export async function pushBusinessAlarm(payload = {}) {
  const {
    source = 'biz',
    category = 'fish',
    pondDetailNum = '',
    pondName = '',
    workshopName = '',
    pondSystemName = '',
    title = '',
    content = '',
    level = 'priority', // urgent | priority | normal
  } = payload

  if (!title) {
    return { ok: false, error: '缺少报警标题' }
  }

  if (isDup({ pondDetailNum, title, source })) {
    return { ok: true, skipped: true }
  }

  const warnType = 0
  const categoryTypeMap = {
    equipment: 0,
    device: 0,
    equip: 0,
    water: 1,
    fish: 2,
    person: 3,
    human: 3
  }
  const warnDeviceType = categoryTypeMap[String(category || '').toLowerCase()]
  const resolvedDeviceType = warnDeviceType != null ? warnDeviceType : 2
  const warnContent = [
    content,
    source ? `来源:${source}` : '',
    category ? `分类:${category}` : '',
    level ? `级别:${level}` : '',
  ].filter(Boolean).join(' | ')

  try {
    const res = await createZkDeviceAlarm({
      workshopName: workshopName || undefined,
      pondSystemName: pondSystemName || undefined,
      pondDetailNum: pondDetailNum || undefined,
      pondName: pondName || pondDetailNum || undefined,
      warnName: title,
      warnContent,
      warnType,
      warnDevice: source === 'fcr' ? '饵料系数监测' : (source === 'health' ? '健康度评估' : '业务监测'),
      warnDeviceType: resolvedDeviceType,
      status: 0,
    }, { silent: true, hideErrorMessage: true })
    if (res && (res.code === 0 || res.code === 200)) {
      markDup({ pondDetailNum, title, source })
      return { ok: true, id: res.data }
    }
    return { ok: false, error: (res && res.msg) || '创建报警失败' }
  } catch (e) {
    return { ok: false, error: (e && e.message) || '创建报警失败' }
  }
}

/**
 * 写入任务与计划（localStorage，与 taskPlan 共用）
 * @returns {{ ok: boolean, task?: object, path: string }}
 */
export function createInspectionPlanTask(payload = {}) {
  const {
    title = '',
    level = 'priority',
    period = 'day',
    from = '',
    pondDetailNum = '',
  } = payload

  if (!title || !String(title).trim()) {
    return { ok: false, path: ROUTES.TASK, error: '缺少任务内容' }
  }

  const task = {
    id: 't' + Date.now() + '_' + Math.random().toString(36).slice(2, 7),
    period: period || 'day',
    level: level || 'priority',
    title: String(title).trim(),
    done: false,
    periodLabel: PERIOD_MAP[period] || period,
    from: from || '',
    pondDetailNum: pondDetailNum || '',
    workshop: payload.workshopName || payload.workshop || '',
    systemName: payload.pondSystemName || payload.systemName || '',
    createdAt: Date.now(),
  }

  let list = []
  try {
    const raw = localStorage.getItem(TASK_STORAGE_KEY)
    if (raw) {
      const parsed = JSON.parse(raw)
      if (Array.isArray(parsed)) list = parsed
    }
  } catch (e) { /* ignore */ }

  const existsIdx = list.findIndex(t =>
    !t.done &&
    t.title === task.title &&
    (!pondDetailNum || t.pondDetailNum === pondDetailNum)
  )
  let savedTask = task
  if (existsIdx >= 0) {
    if (level === 'urgent') list[existsIdx].level = 'urgent'
    savedTask = list[existsIdx]
  } else {
    list.unshift(task)
  }
  localStorage.setItem(TASK_STORAGE_KEY, JSON.stringify(list))
  try {
    if (typeof window !== 'undefined' && window.dispatchEvent) {
      window.dispatchEvent(new CustomEvent('kede-inspection-task-created', { detail: savedTask }))
    }
  } catch (e) { /* ignore */ }

  const qs = new URLSearchParams()
  qs.set('focus', savedTask.id)
  if (from) qs.set('from', from)
  if (pondDetailNum) qs.set('pond', pondDetailNum)
  return {
    ok: true,
    task: savedTask,
    path: `${ROUTES.TASK}?${qs.toString()}`,
  }
}

/**
 * 写入「任务与计划」真实库：alarm_record + task_plan（他人可接收执行）
 */
export async function pushToMissionPlanning(payload = {}) {
  const title = String(payload.taskTitle || payload.title || '').trim()
  if (!title) {
    return { ok: false, error: '缺少任务标题', path: ROUTES.TASK }
  }

  const alarmCode = buildAlarmCode(payload)
  const priority = mapPriority(payload.level)
  const category = mapCategory(payload.category)
  const reason = String(payload.content || payload.title || title).slice(0, 2000)
  const workshop = payload.workshopName || payload.workshop || ''
  const systemName = payload.pondSystemName || payload.systemName || ''
  const poolCode = payload.pondDetailNum || payload.poolCode || ''
  const poolName = payload.pondName || poolCode
  const sourceCode = String(payload.source || 'ai').slice(0, 64)
  const assigneeName = payload.assigneeName || ''
  const assigneeId = payload.assigneeId || ''
  const alarmTime = payload.alarmTime || nowSql()
  const taskCode = ('TK-AL-' + alarmCode).slice(0, 64)
  const estimated = priority === 'urgent' ? 60 : 120
  const skipDedupe = !!payload.skipDedupe || (!!payload.forceUrgent && !!payload.alarmCode)

  const upgradeUrgentSqls = (priority === 'urgent' || payload.forceUrgent) ? [
    'UPDATE ' + TASK_TABLE + " SET priority='urgent',estimated_minutes=60,end_time=DATE_ADD(COALESCE(start_time,NOW()), INTERVAL 60 MINUTE) WHERE (source_alarm_code=" + q(alarmCode) + ' OR task_code=' + q(taskCode) + ') AND (isDel=0 OR isDel IS NULL)',
    'UPDATE ' + ALARM_TABLE + " SET priority='urgent' WHERE alarm_code=" + q(alarmCode) + ' AND (isDel=0 OR isDel IS NULL)'
  ] : []

  if (!skipDedupe) {
  try {
    const existing = await wiiduQuery(
      'SELECT task_code,source_alarm_code,priority FROM ' + TASK_TABLE +
      " WHERE (isDel=0 OR isDel IS NULL) AND (source_alarm_code=" + q(alarmCode) +
      ' OR task_code=' + q(taskCode) + ') LIMIT 1'
    )
    if (existing && existing.length) {
      const existingTaskCode = existing[0].task_code || taskCode
      if (priority === 'urgent' || payload.forceUrgent) {
        try {
          await wiiduWrite([
            'UPDATE ' + TASK_TABLE + " SET priority='urgent',estimated_minutes=60,end_time=DATE_ADD(COALESCE(start_time,NOW()), INTERVAL 60 MINUTE) WHERE task_code=" + q(existingTaskCode) + ' AND (isDel=0 OR isDel IS NULL)',
            'UPDATE ' + ALARM_TABLE + " SET priority='urgent' WHERE alarm_code=" + q(alarmCode) + ' AND (isDel=0 OR isDel IS NULL)'
          ])
        } catch (e) {
          console.warn('pushToMissionPlanning upgrade urgent', e)
        }
      }
      return {
        ok: true,
        skipped: true,
        upgraded: priority === 'urgent' || !!payload.forceUrgent,
        alarmCode,
        taskCode: existingTaskCode,
        path: `${ROUTES.TASK}?focus=${encodeURIComponent(existingTaskCode)}`
      }
    }
  } catch (e) {
    console.warn('pushToMissionPlanning dedupe query', e)
  }
  }

  const alarmSql =
    'INSERT INTO ' + ALARM_TABLE +
    ' (alarm_code,alarm_title,category,priority,alarm_status,workshop,system_name,pool_code,pool_name,' +
    'source_code,current_value,unit,threshold_value,alarm_reason,assignee_id,assignee_name,alarm_time,isDel) VALUES (' +
    [
      q(alarmCode),
      q(title),
      q(category),
      q(priority),
      q('unreceived'),
      q(workshop),
      q(systemName),
      q(poolCode),
      q(poolName),
      q(sourceCode),
      q(payload.currentValue || ''),
      q(payload.unit || ''),
      q(payload.thresholdValue || ''),
      q(reason),
      q(assigneeId),
      q(assigneeName),
      q(alarmTime),
      '0'
    ].join(',') + ')'

  const taskSql =
    'INSERT INTO ' + TASK_TABLE +
    ' (task_code,task_name,task_content,period_type,category,priority,task_status,workshop,system_name,pool_code,' +
    'assignee_id,assignee_name,publisher_id,publisher_name,start_time,end_time,estimated_minutes,instructions,' +
    'source_type,source_alarm_code,delay_status,isDel) VALUES (' +
    [
      q(taskCode),
      q(title),
      q(reason),
      q(payload.period || 'day'),
      q(category),
      q(priority),
      q('unreceived'),
      q(workshop),
      q(systemName),
      q(poolCode),
      q(assigneeId),
      q(assigneeName),
      q('system'),
      q('AI预警'),
      q(alarmTime),
      'DATE_ADD(' + q(alarmTime) + ', INTERVAL ' + estimated + ' MINUTE)',
      String(estimated),
      q(reason),
      q('alarm'),
      q(alarmCode),
      q('none'),
      '0'
    ].join(',') + ')'

  try {
    await wiiduWrite(upgradeUrgentSqls.concat([alarmSql, taskSql]))
    return {
      ok: true,
      alarmCode,
      taskCode,
      path: `${ROUTES.TASK}?focus=${encodeURIComponent(taskCode)}`
    }
  } catch (e) {
    return {
      ok: false,
      error: (e && e.message) || '写入任务与计划失败',
      path: ROUTES.TASK
    }
  }
}

/**
 * 业务超标：推报警 + 建任务（localStorage + 任务与计划库）
 */
export async function pushAlarmAndCreateTask(payload = {}) {
  const alarmRes = await pushBusinessAlarm(payload)
  const title = payload.taskTitle || payload.title || '业务异常巡检核查'
  const level = payload.level === 'urgent' || payload.level === 'danger'
    ? 'urgent'
    : (payload.level === 'normal' ? 'normal' : 'priority')
  const taskRes = createInspectionPlanTask({
    title,
    level,
    period: payload.period || 'day',
    from: payload.source || '',
    pondDetailNum: payload.pondDetailNum || '',
  })

  let missionRes = { ok: false, skipped: true }
  // 报警成功或去重跳过时都尝试建任务（任务侧按稳定 alarmCode 去重）
  if (alarmRes.ok) {
    missionRes = await pushToMissionPlanning({
      ...payload,
      title,
      taskTitle: title,
      level,
      alarmCode: payload.alarmCode || buildStableAlarmCode(payload),
    })
  }

  return {
    alarm: alarmRes,
    task: taskRes,
    mission: missionRes,
    alertPath: `${ROUTES.ALERT}?source=${encodeURIComponent(payload.source || 'biz')}&category=${encodeURIComponent(payload.category || 'fish')}${payload.pondDetailNum ? '&pond=' + encodeURIComponent(payload.pondDetailNum) : ''}`,
    taskPath: (missionRes && missionRes.path) || taskRes.path,
  }
}

function buildStableAlarmCode(payload = {}) {
  const hour = new Date().toISOString().slice(0, 13).replace(/[-T:]/g, '')
  const raw = dedupKey(payload) + '|' + hour
  let hash = 0
  for (let i = 0; i < raw.length; i++) {
    hash = ((hash << 5) - hash) + raw.charCodeAt(i)
    hash |= 0
  }
  const src = String(payload.source || 'biz').replace(/[^a-zA-Z0-9]/g, '').slice(0, 8) || 'BIZ'
  return ('ZK-AI-' + src + '-' + hour + '-' + (hash >>> 0).toString(36).toUpperCase()).slice(0, 64)
}

/**
 * 将未处理的 zk_device_alarm 同步进任务与计划（页面打开/刷新时补链）
 */
export async function syncOpenSystemAlarmsToMission() {
  // 控制批量，避免打开任务页时串行写库超过前端超时
  const list = await fetchSystemAlarms({ pageSize: 25, status: 0 })
  let created = 0
  for (const item of list) {
    const content = String(item.warnContent || '')
    let level = 'priority'
    if (/级别:urgent|级别:danger/.test(content)) level = 'urgent'
    else if (/级别:normal/.test(content)) level = 'normal'

    let category = 'fish'
    const type = Number(item.warnDeviceType)
    if (type === 0) category = 'equip'
    else if (type === 1) category = 'water'
    else if (type === 3) category = 'person'

    let source = 'zk'
    const m = content.match(/来源:([^|\s]+)/)
    if (m) source = m[1]

    const title = String(item.warnName || '系统报警').trim()
    try {
      const res = await pushToMissionPlanning({
        alarmCode: 'ZK-DEV-' + item.id,
        title,
        taskTitle: '【系统报警】' + title,
        content,
        level,
        category,
        source,
        pondDetailNum: item.pondDetailNum || '',
        pondName: item.pondName || '',
        workshopName: item.workshopName || '',
        pondSystemName: item.pondSystemName || '',
        alarmTime: item.createTime || undefined,
      })
      if (res.ok && !res.skipped) created += 1
    } catch (e) {
      console.warn('syncOpenSystemAlarmsToMission item', item && item.id, e)
    }
  }
  return created
}

/**
 * 读取「任务与计划」未完成任务（大屏 / 其他模块展示用）
 * 按日/周/月/年分别拉取，避免日计划告警占满导致周月年空白；
 * 各部门周期同一套站点/部门范围，空部门不得看到他部门周月年。
 */
export async function fetchMissionPlanOpenTasks(limit = 40) {
  try {
    if (isUserDeptScopeDenied()) return []
    const perPeriod = Math.max(12, Math.min(48, Number(limit) || 40))
    const cols =
      'task_code,task_name,priority,period_type,task_status,end_time,source_type,source_alarm_code,start_time,workshop,system_name,pool_code,department_id'
    const userDept = resolveCurrentUserDeptId()
    const platformAdminForSql = isPlatformUnscopedAdmin()
    const deptSql = platformAdminForSql
      ? ''
      : (userDept != null && Number(userDept) > 0)
        ? (' AND (department_id=' + Number(userDept) + " OR department_id='" + Number(userDept) + "')")
        : ' AND 1=0'
    const base =
      'SELECT ' + cols + ' FROM ' + TASK_TABLE +
      " WHERE (isDel=0 OR isDel IS NULL) AND task_status IN ('unreceived','executing','overdue')" +
      " AND task_name IS NOT NULL AND task_name<>''" + deptSql
    const order =
      " ORDER BY CASE LOWER(IFNULL(priority,'')) WHEN 'urgent' THEN 0 WHEN 'priority' THEN 1 ELSE 2 END, start_time DESC LIMIT " +
      perPeriod
    const sqls = [
      base + " AND (period_type IS NULL OR period_type='' OR LOWER(period_type)='day')" + order,
      base + " AND LOWER(period_type)='week'" + order,
      base + " AND LOWER(period_type)='month'" + order,
      base + " AND LOWER(period_type)='year'" + order
    ]
    const groups = await wiiduQueryMulti(sqls)
    const seen = {}
    const rows = []
    const siteOpts = peekSharedSiteOptions()
    const platformAdmin = isPlatformUnscopedAdmin()
    const hasAnySite = !!(siteOpts && (
      ((siteOpts.workshopOptions || []).length > 0) ||
      ((siteOpts.systemOptions || []).length > 0) ||
      ((siteOpts.pondOptions || []).length > 0)
    ))
    // 普通部门用户且站点为空：大屏不展示；超管不受此限
    // 白名单加载失败不得当空部门 → 仍按部门过滤展示本部门任务
    if (!platformAdmin && userDept != null && siteOpts && siteOpts._loadFailed) {
      /* fall through with dept filter below */
    } else if (!platformAdmin && userDept != null && siteOpts && (siteOpts._ready || siteOpts._deniedUnscoped) && !hasAnySite) {
      return []
    }
    ;(groups || []).forEach((group, gi) => {
      ;(Array.isArray(group) ? group : []).forEach((row) => {
        if (!row) return
        const key = String(row.task_code || row.Id || row.task_name || '')
        if (!key || seen[key]) return
        if (!platformAdmin) {
          const taskDept = row.department_id != null && row.department_id !== '' && row.department_id !== 0 && row.department_id !== '0'
            ? row.department_id
            : null
          const numericTaskDept = taskDept != null && /^\d+$/.test(String(taskDept).trim()) ? taskDept : null
          if (userDept != null && numericTaskDept != null && String(numericTaskDept) !== String(userDept)) {
            return
          }
          if (!isTaskInUserSiteScope({
            workshop: row.workshop,
            workshopName: row.workshop,
            system: row.system_name,
            pondSystemName: row.system_name,
            pool_code: row.pool_code,
            pondDetailNum: row.pool_code
          }, siteOpts)) {
            return
          }
          if (userDept == null) return
        }
        seen[key] = true
        if (!row.period_type || String(row.period_type).trim() === '') {
          row.period_type = gi === 0 ? 'day' : ['day', 'week', 'month', 'year'][gi]
        }
        rows.push(row)
      })
    })
    return rows
  } catch (e) {
    console.warn('fetchMissionPlanOpenTasks', e)
    return []
  }
}

function mapTimelineCategory(raw) {
  const first = String(raw || '')
    .split(',')[0]
    .trim()
    .toLowerCase()
  const c = mapCategory(first)
  if (c === 'water') return 'water'
  if (c === 'equip') return 'equipment'
  if (c === 'person') return 'human'
  return 'fish'
}

function parseSqlDate(val) {
  if (!val) return null
  if (val instanceof Date && !Number.isNaN(val.getTime())) return val
  const s = String(val).trim().replace('T', ' ').replace(/-/g, '/')
  const d = new Date(s)
  return Number.isNaN(d.getTime()) ? null : d
}

function formatHourLabel(d) {
  const h = d.getHours()
  const m = d.getMinutes()
  return String(h).padStart(2, '0') + ':' + String(m).padStart(2, '0')
}

/**
 * 当天任务时间轴节点：已完成按 complete_time；未完成日计划按 start/end 落点
 * @returns {Promise<{fish:object[],water:object[],equipment:object[],human:object[]}>}
 */
export async function fetchMissionPlanTodayTimelineEvents(limit = 120) {
  const empty = { fish: [], water: [], equipment: [], human: [] }
  try {
    const lim = Math.max(20, Math.min(200, Number(limit) || 120))
    const dayEnd = "DATE_ADD(CURDATE(), INTERVAL 1 DAY)"
    const completedSql =
      'SELECT task_code,task_name,category,task_status,complete_time,start_time,end_time' +
      ' FROM ' + TASK_TABLE +
      " WHERE (isDel=0 OR isDel IS NULL) AND task_status='completed'" +
      " AND task_name IS NOT NULL AND task_name<>''" +
      ' AND complete_time>=CURDATE() AND complete_time<' + dayEnd +
      ' ORDER BY complete_time ASC LIMIT ' + lim
    const openSql =
      'SELECT task_code,task_name,category,task_status,complete_time,start_time,end_time,period_type' +
      ' FROM ' + TASK_TABLE +
      " WHERE (isDel=0 OR isDel IS NULL) AND task_status IN ('unreceived','executing','overdue')" +
      " AND task_name IS NOT NULL AND task_name<>''" +
      " AND (period_type IS NULL OR period_type='' OR LOWER(period_type)='day')" +
      ' AND (' +
      '  (start_time>=CURDATE() AND start_time<' + dayEnd + ')' +
      '  OR (end_time>=CURDATE() AND end_time<' + dayEnd + ')' +
      '  OR start_time IS NULL' +
      ' ) ORDER BY COALESCE(start_time,end_time) ASC LIMIT ' + Math.ceil(lim / 2)

    const groups = await wiiduQueryMulti([completedSql, openSql])
    const completedRows = Array.isArray(groups[0]) ? groups[0] : []
    const openRows = Array.isArray(groups[1]) ? groups[1] : []
    return buildDashboardEventNodes(completedRows, openRows)
  } catch (e) {
    console.warn('fetchMissionPlanTodayTimelineEvents', e)
    return empty
  }
}

/**
 * @param {object[]} completedRows
 * @param {object[]} openRows
 */
export function buildDashboardEventNodes(completedRows, openRows) {
  const map = { fish: [], water: [], equipment: [], human: [] }
  const seen = {}

  function pushNode(row, completed) {
    if (!row) return
    const name = String(row.task_name || row.taskName || '').trim()
    if (!name) return
    const key = String(row.task_code || row.Id || name)
    if (seen[key]) return
    seen[key] = true

    const timeSrc = completed
      ? parseSqlDate(row.complete_time || row.completeTime)
      : parseSqlDate(row.start_time || row.startTime) || parseSqlDate(row.end_time || row.endTime) || new Date()
    if (!timeSrc) return

    let hour = timeSrc.getHours() + timeSrc.getMinutes() / 60
    // 保留真实时刻，由前端按任务动态扩展时间轴；越界仅作兜底夹紧到 0–24
    if (hour < 0) hour = 0
    if (hour > 24) hour = 24

    const cat = mapTimelineCategory(row.category)
    map[cat].push({
      hour,
      timeLabel: formatHourLabel(timeSrc),
      event: name,
      completed: !!completed,
      taskCode: row.task_code || row.taskCode || ''
    })
  }

  ;(completedRows || []).forEach((r) => pushNode(r, true))
  ;(openRows || []).forEach((r) => pushNode(r, false))

  Object.keys(map).forEach((k) => {
    map[k] = map[k]
      .slice()
      .sort((a, b) => a.hour - b.hour)
      .slice(0, 8)
  })
  return map
}

/**
 * 将任务列表压成大屏日/周/月/年看板结构（各周期互不串填）
 */
export function buildDashboardTaskBoard(taskRows) {
  // 模板 v-for 按数组渲染，每类最多 3 条（与 index_daping3 回退路径一致）
  const board = {
    day: { urgent: [], priority: [], normal: [] },
    week: { urgent: [], priority: [], normal: [] },
    month: { urgent: [], priority: [], normal: [] },
    year: { urgent: [], priority: [], normal: [] }
  }
  const buckets = {
    day: { urgent: [], priority: [], normal: [] },
    week: { urgent: [], priority: [], normal: [] },
    month: { urgent: [], priority: [], normal: [] },
    year: { urgent: [], priority: [], normal: [] }
  }
  ;(taskRows || []).forEach(t => {
    const period = String(t.period_type || 'day').toLowerCase()
    const key = buckets[period] ? period : 'day'
    const name = String(t.task_name || t.taskName || '').trim()
    if (!name) return
    const p = String(t.priority || 'priority').toLowerCase()
    if (p === 'urgent') buckets[key].urgent.push(name)
    else if (p === 'normal' || p === 'daily' || p === 'routine') buckets[key].normal.push(name)
    else buckets[key].priority.push(name)
  })
  Object.keys(board).forEach(period => {
    board[period].urgent = buckets[period].urgent.slice(0, 3)
    board[period].priority = buckets[period].priority.slice(0, 3)
    board[period].normal = buckets[period].normal.slice(0, 3)
  })
  return board
}

/**
 * 各模块 AI 报警 → 任务与计划（按业务 id 去重）
 * 兼容：鱼病/阈值 mock、设备监控 DEMO、大屏 dashboard 列表
 */
export async function syncModuleAiAlarmsToMission(alarms, options = {}) {
  const prefix = options.prefix || 'MOD-AI-'
  const source = options.source || 'module_ai'
  let created = 0
  for (const item of alarms || []) {
    const tone = String(item.tone || '').toLowerCase()
    if (tone === 'ok' || tone === 'normal') continue

    const status = String(item.alarmStatus || item.alarm_status || item.status || 'open').toLowerCase()
    if (status === 'completed' || status === 'acked' || status === 'closed' || status === '-1') continue

    const id = item.id || item.biz_id || item.alarmCode || item.alarm_code ||
      item.device_id || [item.title, item.message, item.type].filter(Boolean).join('_')
    if (!id) continue

    let level = 'priority'
    const rawPriority = String(item.priority || item.level || tone || '').toLowerCase()
    if (rawPriority === 'critical' || rawPriority === 'urgent' || rawPriority === 'danger') level = 'urgent'
    else if (rawPriority === 'warn' || rawPriority === 'warning' || rawPriority === 'high') level = 'priority'
    else if (rawPriority === 'info' || rawPriority === 'normal' || rawPriority === 'low') level = 'normal'
    else if (options.defaultLevel) level = options.defaultLevel

    const devicePart = item.device_name || item.deviceName || ''
    const typePart = item.type || item.message || ''
    const title = String(
      item.alarmTitle || item.alarm_title || item.title ||
      (devicePart && typePart ? (devicePart + ' ' + typePart) : (devicePart || typePart)) ||
      'AI预警'
    ).trim()
    if (!title) continue

    let category = item.category || options.category || 'fish'
    const catLower = String(category).toLowerCase()
    if (catLower.includes('微滤') || catLower.includes('温控') || catLower.includes('泵') ||
        catLower.includes('风机') || catLower === 'equipment' || catLower === 'equip' || catLower === 'device') {
      category = 'equip'
    } else if (catLower === 'water' || catLower.includes('水')) {
      category = 'water'
    }

    const res = await pushToMissionPlanning({
      alarmCode: String(prefix + id).slice(0, 64),
      title,
      taskTitle: title.startsWith('【') ? title : ('【AI预警】' + title),
      content: item.alarmReason || item.alarm_reason || item.aiSuggestion || item.ai_suggestion ||
        item.suggestion || item.message || typePart || title,
      level,
      category,
      source,
      pondDetailNum: item.poolCode || item.pool_code || item.pondCode || item.pondDetailNum || '',
      pondName: item.poolName || item.pool_name || '',
      workshopName: item.workshop || item.workshopName || '',
      pondSystemName: item.system || item.system_name || item.pondSystemName || '',
      currentValue: item.currentValue || item.current_value || item.warning_value || '',
      thresholdValue: item.thresholdValue || item.threshold_value || '',
      unit: item.unit || item.warning_unit || '',
      alarmTime: item.alarmTime || item.alarm_time || undefined
    })
    // 同步写 localStorage，任务与计划页在库不可用时仍可见
    createInspectionPlanTask({
      title: title.startsWith('【') ? title : ('【AI预警】' + title),
      level,
      period: 'day',
      from: source,
      pondDetailNum: item.poolCode || item.pool_code || item.pondCode || item.pondDetailNum || ''
    })
    if (res.ok && !res.skipped) created += 1
  }
  return created
}

/** 拉取系统业务报警列表（供报警页展示） */
export async function fetchSystemAlarms(query = {}) {
  try {
    const res = await getZkDeviceAlarmPage({
      pageNo: 1,
      pageSize: query.pageSize || 50,
      pondDetailNum: query.pond || undefined,
      status: query.status,
    })
    if (res && (res.code === 0 || res.code === 200)) {
      const list = (res.data && res.data.list) || []
      return list
    }
  } catch (e) {
    console.error('fetchSystemAlarms', e)
  }
  return []
}

export function filterSystemAlarms(list, query = {}) {
  const source = (query.source || '').toLowerCase()
  const category = (query.category || '').toLowerCase()
  const pond = (query.pond || '').trim()
  const keyword = (query.keyword || '').trim()
  return (list || []).filter(item => {
    const content = String(item.warnContent || '')
    const name = String(item.warnName || '')
    if (pond && String(item.pondDetailNum || '') !== pond && String(item.pondName || '') !== pond) return false
    if (source && !content.includes(`来源:${source}`) && !(item.warnDevice || '').toLowerCase().includes(source)) {
      if (content.includes('来源:')) return false
    }
    if (category && !content.includes(`分类:${category}`)) {
      if (content.includes('分类:')) return false
    }
    if (keyword && !name.includes(keyword) && !content.includes(keyword)) return false
    return true
  })
}
