/**
 * 首页 / AI报警详情 共用：只读 zk_device_alarm
 * 独立模块，避免与 dashboardLiveBridge / alarmTaskBridge 循环依赖。
 */
import {
  getZkDeviceAlarmPage,
  getZkDeviceAlarmDashboardList
} from '@/api/fish/zkDeviceAlarm'
import { isUserDeptScopeDenied, resolveCurrentUserDeptId } from '@/utils/siteOptions'

export const ZK_ALARM_PAGE_SIZE = 100

/** warnDeviceType: 0设备 1水 2鱼 3人 */
const TYPE_TO_CATEGORY = {
  0: 'device',
  1: 'water',
  2: 'fish',
  3: 'person'
}

const DETAIL_TO_OVERVIEW_CATEGORY = {
  device: 'equipment',
  equip: 'equipment',
  equipment: 'equipment',
  water: 'water',
  fish: 'fish',
  person: 'human',
  human: 'human'
}

const CATEGORY_CN = {
  device: '设备',
  equip: '设备',
  equipment: '设备',
  water: '水',
  fish: '鱼',
  person: '人',
  human: '人'
}

const OVERVIEW_TO_DETAIL_CATEGORY = {
  equipment: 'device',
  water: 'water',
  fish: 'fish',
  human: 'person',
  person: 'person',
  device: 'device'
}

function stripHtmlPlain(val) {
  if (val == null || val === '') return ''
  return String(val).replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function parseSourceTaskCode(row) {
  const info = String(row.handleInfo || '')
  const m = info.match(/已转(?:紧急)?任务[:：](\S+)/)
  return m ? m[1].replace(/\|.*/, '').trim() : ''
}

function parsePriority(row) {
  const content = String(row.warnContent || row.message || '')
  const m = content.match(/级别:(urgent|priority|normal|daily|danger)/i)
  if (m) {
    const v = m[1].toLowerCase()
    if (v === 'urgent' || v === 'danger') return 'urgent'
    if (v === 'priority') return 'priority'
    return 'daily'
  }
  if (Number(row.warnType) === 1) return 'urgent'
  if (/已转(?:紧急)?任务/.test(String(row.handleInfo || ''))) return 'urgent'
  return 'priority'
}

function parseAlarmStatus(row) {
  if (Number(row.status) === 1) return 'completed'
  const info = String(row.handleInfo || '')
  if (/执行中|已接收|executing/i.test(info)) return 'executing'
  if (/逾期|overdue/i.test(info)) return 'overdue'
  return 'unreceived'
}

function formatRelativeTime(val) {
  if (!val) return ''
  const d = new Date(val)
  if (isNaN(d.getTime())) return String(val)
  const mins = Math.max(0, Math.floor((Date.now() - d.getTime()) / 60000))
  if (mins <= 30) return mins + '分钟前'
  if (mins < 1440) return (Math.round(mins / 60 * 10) / 10) + '小时前'
  if (mins >= 1440) return (Math.round(mins / (60 * 24) * 10) / 10) + '天前'
  return '刚刚'
}

/** 标题/内容兜底分类：warnDeviceType 缺失或被默认成设备时，人员/水质/鱼类仍能进对口栏目 */
function inferCategory(row) {
  const typed = TYPE_TO_CATEGORY[Number(row && row.warnDeviceType)]
  const text = String((row && (row.warnName || row.title || '')) + ' ' + (row && (row.warnContent || row.message || ''))).toLowerCase()
  if (/人员|门禁|闯入|未授权|陌生人|人脸|入侵|值班/.test(text) || /person|human|access/.test(text)) return 'person'
  if (/水质|溶氧|氨氮|浊度|水温|ph\b|orp|硝酸/.test(text) || /water|ph_|do_/.test(text)) return 'water'
  if (/鱼类|生长|饵料|摄食|死鱼|健康评分|鱼病/.test(text)) return 'fish'
  if (typed) return typed
  return 'device'
}

/** 分页接口 DO → 详情结构 */
export function mapPageRow(row) {
  const category = inferCategory(row)
  return {
    Id: row.id,
    id: row.id,
    alarm_code: 'ZK-' + row.id,
    alarm_title: row.warnName || '未命名报警',
    category,
    priority: parsePriority(row),
    alarm_status: parseAlarmStatus(row),
    workshop: row.workshopName || '',
    system_name: row.pondSystemName || '',
    pool_code: row.pondDetailNum || '',
    pool_name: row.pondName || row.pondDetailNum || '',
    assignee_id: row.userId || '',
    assignee_name: row.userName || '',
    alarm_time: row.createTime || row.updateTime || '',
    alarm_reason: row.warnContent || '',
    ai_advice: '',
    resolution: stripHtmlPlain(row.handleInfo || ''),
    source_task_code: parseSourceTaskCode(row),
    deptId: row.deptId != null ? row.deptId : row.dept_id,
    dept_id: row.deptId != null ? row.deptId : row.dept_id,
    _raw: row
  }
}

/**
 * list-for-dashboard 行 → 详情结构（同源表 zk_device_alarm）
 * 兼容旧 VO（仅 category/title/message）与扩展 VO（含 id/车间等）
 */
export function mapDashboardRow(row, index) {
  if (!row) return null
  // 扩展字段齐备时按分页行处理
  if (row.id != null && (row.warnName != null || row.warnDeviceType != null)) {
    return mapPageRow({
      id: row.id,
      warnDeviceType: row.warnDeviceType,
      warnType: row.warnType,
      status: row.status,
      warnName: row.warnName || String(row.title || '').replace(/^【[^】]*】/, ''),
      warnContent: row.warnContent != null ? row.warnContent : row.message,
      handleInfo: row.handleInfo || '',
      workshopName: row.workshopName || '',
      pondSystemName: row.pondSystemName || '',
      pondDetailNum: row.pondDetailNum || '',
      pondName: row.pondName || '',
      userId: row.userId,
      userName: row.userName || '',
      createTime: row.createTime,
      updateTime: row.updateTime,
      deptId: row.deptId != null ? row.deptId : row.dept_id
    })
  }
  const overviewCat = row.category || 'equipment'
  const inferred = inferCategory(row)
  const category = inferred !== 'device'
    ? inferred
    : (OVERVIEW_TO_DETAIL_CATEGORY[overviewCat] || 'device')
  const rawTitle = String(row.title || '报警')
  const alarmTitle = rawTitle.replace(/^【[^】]*】/, '') || rawTitle
  const id = row.id != null ? row.id : null
  return {
    Id: id != null ? id : ('DASH-' + index),
    id: id,
    alarm_code: id != null ? ('ZK-' + id) : ('DASH-' + index),
    alarm_title: alarmTitle,
    category,
    priority: parsePriority(row),
    alarm_status: parseAlarmStatus(row),
    workshop: row.workshopName || '',
    system_name: row.pondSystemName || '',
    pool_code: row.pondDetailNum || '',
    pool_name: row.pondName || '',
    assignee_id: row.userId || '',
    assignee_name: row.userName || '',
    alarm_time: row.createTime || '',
    alarm_reason: row.message || row.warnContent || '',
    ai_advice: row.suggestion || '',
    resolution: stripHtmlPlain(row.handleInfo || ''),
    source_task_code: parseSourceTaskCode(row),
    deptId: row.deptId != null ? row.deptId : row.dept_id,
    dept_id: row.deptId != null ? row.deptId : row.dept_id,
    _raw: id != null ? row : null,
    _dashboardOnly: id == null
  }
}

export function toOverviewAlert(mapped) {
  const cat = DETAIL_TO_OVERVIEW_CATEGORY[mapped.category] || 'equipment'
  const cn = CATEGORY_CN[mapped.category] || '设备'
  const name = mapped.alarm_title || mapped.title || '报警'
  return {
    category: cat,
    title: '【' + cn + '】' + name,
    message: mapped.alarm_reason || mapped.message || '',
    suggestion: mapped.ai_advice || mapped.suggestion || '立即检修',
    time: mapped.time || formatRelativeTime(mapped.alarm_time) || '',
    workshop: mapped.workshop || '',
    workshopName: mapped.workshop || '',
    system: mapped.system_name || '',
    pondSystemName: mapped.system_name || '',
    pondDetailNum: mapped.pool_code || '',
    pondName: mapped.pool_name || '',
    id: mapped.id != null ? mapped.id : mapped.Id,
    alarm_status: mapped.alarm_status,
    priority: mapped.priority
  }
}

function withTimeout(promise, ms, label) {
  return new Promise(function (resolve, reject) {
    const timer = setTimeout(function () {
      reject(new Error((label || '请求') + '超时'))
    }, ms)
    Promise.resolve(promise).then(
      function (v) { clearTimeout(timer); resolve(v) },
      function (e) { clearTimeout(timer); reject(e) }
    )
  })
}

let _alarmCache = null
let _alarmAt = 0
let _alarmInflight = null
let _alarmCacheDeptKey = null
const ALARM_TTL_MS = 20000

function alarmDeptCacheKey() {
  const deptId = resolveCurrentUserDeptId()
  return deptId != null ? String(deptId) : (isUserDeptScopeDenied() ? 'denied' : 'all')
}

export function clearZkAlarmCache() {
  _alarmCache = null
  _alarmAt = 0
  _alarmInflight = null
  _alarmCacheDeptKey = null
}

/**
 * 统一拉取 zk_device_alarm：
 * 分页优先（字段更全），大屏列表补漏；20s 内复用缓存（按部门隔离，避免串部门）。
 */
export async function loadZkDeviceAlarms(force) {
  if (isUserDeptScopeDenied()) {
    clearZkAlarmCache()
    return { alarms: [], errors: [] }
  }
  const deptKey = alarmDeptCacheKey()
  if (
    !force &&
    _alarmCache &&
    _alarmCacheDeptKey === deptKey &&
    Date.now() - _alarmAt < ALARM_TTL_MS
  ) {
    return _alarmCache
  }
  if (!force && _alarmInflight && _alarmCacheDeptKey === deptKey) return _alarmInflight

  _alarmCacheDeptKey = deptKey
  _alarmInflight = (async function () {
    const errors = []
    let pageAlarms = []
    let dashAlarms = []

    const pageTask = withTimeout(
      getZkDeviceAlarmPage({ pageNo: 1, pageSize: ZK_ALARM_PAGE_SIZE }),
      6000,
      '报警分页'
    ).then(function (res) {
      if (res && (res.code === 0 || res.code === 200)) {
        pageAlarms = ((res.data && res.data.list) || []).map(mapPageRow)
      } else {
        errors.push((res && res.msg) || '报警分页读取失败')
      }
    }).catch(function (e) {
      errors.push((e && e.message) || '报警分页请求失败')
    })

    const dashTask = withTimeout(
      getZkDeviceAlarmDashboardList(),
      6000,
      '报警大屏列表'
    ).then(function (res) {
      const list =
        (res && (res.code === 0 || res.code === 200) && Array.isArray(res.data) && res.data) ||
        (Array.isArray(res) && res) ||
        (res && Array.isArray(res.data) && res.data) ||
        []
      dashAlarms = list.map(mapDashboardRow).filter(Boolean)
      if (!dashAlarms.length && res && res.code != null && res.code !== 0 && res.code !== 200) {
        errors.push(res.msg || '报警大屏列表读取失败')
      }
    }).catch(function (e) {
      errors.push((e && e.message) || '报警大屏列表请求失败')
    })

    await Promise.all([pageTask, dashTask])

    if (alarmDeptCacheKey() !== deptKey) {
      return { alarms: [], errors }
    }

    const byKey = {}
    pageAlarms.concat(dashAlarms).forEach(function (a) {
      if (!a) return
      const k = String(a.id != null ? a.id : (a.alarm_code || a.Id || ''))
      if (!k) return
      if (!byKey[k] || (a._raw && !byKey[k]._raw)) byKey[k] = a
    })
    const alarms = Object.keys(byKey).map(function (k) { return byKey[k] })
    const result = { alarms, errors }
    _alarmCache = result
    _alarmAt = Date.now()
    _alarmCacheDeptKey = deptKey
    return result
  })().finally(function () {
    _alarmInflight = null
  })

  return _alarmInflight
}

export async function loadOverviewAlerts() {
  const { alarms, errors } = await loadZkDeviceAlarms()
  return {
    alerts: alarms.map(toOverviewAlert),
    errors
  }
}
