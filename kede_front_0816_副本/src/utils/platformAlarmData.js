/**
 * AI 报警监控数据服务（供 alertMonitor.vue 使用）
 * 对接 yudao zk_device_alarm，并与任务计划 localStorage 桥接通
 */
import {
  updateZkDeviceAlarm,
  createZkDeviceAlarm
} from '@/api/fish/zkDeviceAlarm'
import { createInspectionPlanTask, pushToMissionPlanning, markAlarmUrgentLocal, applyAlarmUrgentOverride } from '@/utils/alarmTaskBridge'
import { toBackendUserId } from '@/utils/backendUserId'
import { getExtendedAlarms } from '@/api/fish/daPing'
import {
  ZK_ALARM_PAGE_SIZE,
  loadZkDeviceAlarms as loadZkDeviceAlarmsShared,
  toOverviewAlert
} from '@/utils/zkAlarmShared'
import { isUserDeptScopeDenied, isAlarmInUserSiteScope, peekSharedSiteOptions } from '@/utils/siteOptions'

export { ZK_ALARM_PAGE_SIZE, toOverviewAlert }

const CATEGORY_TO_TYPE = {
  device: 0,
  equip: 0,
  equipment: 0,
  water: 1,
  fish: 2,
  person: 3,
  human: 3
}

function stripHtmlPlain(val) {
  if (val == null || val === '') return ''
  if (typeof document !== 'undefined') {
    const div = document.createElement('div')
    div.innerHTML = String(val)
    return (div.textContent || div.innerText || '').replace(/\s+/g, ' ').trim()
  }
  return String(val).replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function parseSourceTaskCode(row) {
  const info = String(row.handleInfo || '')
  const m = info.match(/已转(?:紧急)?任务[:：](\S+)/)
  return m ? m[1].replace(/\|.*/, '').trim() : ''
}

/** 人员/鱼类扩展告警：Id 形如 person-1 / fish-3 */
function isExtendedAlarm(item) {
  if (!item) return false
  if (item._readonlyExt) return true
  const key = String(item.Id != null ? item.Id : (item.alarm_code || item.alarmCode || ''))
  return /^(person|fish)-\d+$/i.test(key) || /^(AC|FV)-\d+$/i.test(key)
}

function extendedAlarmNumericId(item) {
  if (!item) return null
  const direct = Number(item.id)
  if (Number.isFinite(direct) && direct > 0) return direct
  const key = String(item.Id != null ? item.Id : (item.alarm_code || item.alarmCode || ''))
  const m = key.match(/^(?:person|fish|AC|FV)-(\d+)$/i)
  if (m) return Number(m[1])
  return null
}

export async function loadZkDeviceAlarms(force) {
  const result = await loadZkDeviceAlarmsShared(force)
  return {
    alarms: (result.alarms || []).map(function (a) { return applyAlarmUrgentOverride(a) }),
    errors: result.errors || []
  }
}

export async function loadOverviewAlerts() {
  const { alarms, errors } = await loadZkDeviceAlarms()
  return {
    alerts: alarms.map(toOverviewAlert),
    errors
  }
}

function mapExtendedRow(row, category, prefix) {
  if (!row) return null
  const id = row.id
  const statusRaw = String(row.status || row.alarmStatus || 'pending').toLowerCase()
  let alarm_status = 'unreceived'
  if (statusRaw === 'completed' || statusRaw === 'handled' || statusRaw === 'done') alarm_status = 'completed'
  else if (statusRaw === 'executing' || statusRaw === 'exec') alarm_status = 'executing'
  else if (statusRaw === 'overdue') alarm_status = 'overdue'
  const level = String(row.level || row.alarmLevel || 'priority').toLowerCase()
  const priority = (level === 'urgent' || level === 'danger') ? 'urgent' : (level === 'daily' || level === 'normal') ? 'daily' : 'priority'
  return {
    Id: prefix + id,
    id: id,
    alarm_code: prefix + id,
    alarm_title: row.title || '未命名报警',
    category,
    priority,
    alarm_status,
    workshop: row.workshop || row.workshopName || '',
    system_name: row.system || row.pondSystemName || '',
    pool_code: row.poolCode || row.pondDetailNum || '',
    pool_name: row.location || row.pondName || '',
    assignee_id: '',
    assignee_name: row.owner || row.ownerName || '',
    alarm_time: row.alarmTime || row.alarm_time || '',
    alarm_reason: row.message || '',
    ai_advice: row.suggestion || '',
    resolution: '',
    source_task_code: '',
    _raw: row,
    _readonlyExt: true
  }
}

async function loadExtendedAlarmsSafe() {
  if (isUserDeptScopeDenied()) return []
  try {
    const res = await getExtendedAlarms()
    const payload = (res && (res.code === 0 || res.code === 200) && res.data) || res || {}
    const data = (payload && (payload.fish || payload.person)) ? payload : (payload && payload.data) || {}
    const fish = (data.fish || []).map(function (r) { return mapExtendedRow(r, 'fish', 'fish-') }).filter(Boolean)
    const person = (data.person || []).map(function (r) { return mapExtendedRow(r, 'person', 'person-') }).filter(Boolean)
    return fish.concat(person)
  } catch (e) {
    console.warn('[platformAlarm] extended', e && e.message)
    return []
  }
}

async function loadAll(force) {
  if (isUserDeptScopeDenied()) {
    return {
      alarms: [],
      tasks: [],
      locations: [],
      thresholds: [],
      serverTime: new Date().toISOString(),
      _errors: []
    }
  }
  const [{ alarms, errors }, extra] = await Promise.all([
    loadZkDeviceAlarms(force),
    loadExtendedAlarmsSafe()
  ])
  const list = Array.isArray(alarms) ? alarms.slice() : []
  const seen = {}
  list.forEach(function (a) {
    seen[String(a.alarm_code || a.Id || a.id || '')] = true
  })
  extra.forEach(function (a) {
    const k = String(a.alarm_code || a.Id || '')
    if (k && !seen[k]) {
      list.push(a)
      seen[k] = true
    }
  })
  const siteOpts = peekSharedSiteOptions()
  const scoped = list.filter(function (a) {
    return isAlarmInUserSiteScope({
      workshop: a.workshop,
      workshopName: a.workshop,
      system: a.system_name,
      system_name: a.system_name,
      pondSystemName: a.system_name,
      deptId: a.deptId != null ? a.deptId : a.dept_id,
      dept_id: a.deptId != null ? a.deptId : a.dept_id
    }, siteOpts)
  })
  return {
    alarms: scoped,
    tasks: [],
    locations: [],
    thresholds: [],
    serverTime: new Date().toISOString(),
    _errors: errors
  }
}

function patchPayload(item, extra = {}) {
  const raw = (item && item._raw) || {}
  const operator = extra.operator || {}
  const fields = Object.assign({}, extra.fields || {})
  if (fields.handleInfo != null) fields.handleInfo = stripHtmlPlain(fields.handleInfo)
  // 扩展告警 Id 形如 person-1 / fish-3，不能作为 zk_device_alarm 主键
  const numericId = Number(item && item.id != null ? item.id : raw.id)
  const legacyId = item && item.Id != null ? item.Id : null
  const ext = isExtendedAlarm(item)
  const idForUpdate = ext
    ? (Number.isFinite(numericId) && numericId > 0 ? numericId : extendedAlarmNumericId(item))
    : (typeof legacyId === 'number' || (typeof legacyId === 'string' && /^\d+$/.test(legacyId))
      ? Number(legacyId)
      : (Number.isFinite(numericId) ? numericId : legacyId))
  if (!ext && (typeof idForUpdate === 'string' && !/^\d+$/.test(idForUpdate))) {
    throw new Error('报警编号格式错误，无法更新设备告警表')
  }
  const payload = Object.assign({}, raw, {
    id: idForUpdate,
    workshopName: item.workshop || raw.workshopName,
    pondSystemName: item.system_name || item.systemName || raw.pondSystemName,
    pondDetailNum: item.pool_code || item.poolCode || raw.pondDetailNum,
    pondName: item.pool_name || item.poolName || raw.pondName,
    warnName: item.alarm_title || item.title || raw.warnName,
    warnContent: item.alarm_reason || item.reason || raw.warnContent,
    warnDeviceType:
      raw.warnDeviceType != null
        ? raw.warnDeviceType
        : CATEGORY_TO_TYPE[item.backendCategory || item.category] || 0,
    warnType: raw.warnType != null ? raw.warnType : 0,
    userName: operator.name || item.assignee_name || raw.userName,
    status: raw.status,
    handleInfo: stripHtmlPlain(raw.handleInfo)
  }, fields)
  delete payload.userId
  delete payload.user_id
  const userId = toBackendUserId(operator.id) ||
    toBackendUserId(item.assignee_id) ||
    toBackendUserId(raw.userId)
  if (userId != null) payload.userId = userId
  return payload
}

async function acceptAlarm(item, operator) {
  if (isExtendedAlarm(item)) {
    return updateExtendedAlarmStatus(item, 'executing', operator)
  }
  const payload = patchPayload(item, {
    operator,
    fields: {
      status: 0,
      handleInfo: '执行中|已接收 ' + new Date().toLocaleString()
    }
  })
  const res = await updateZkDeviceAlarm(payload)
  if (!(res && (res.code === 0 || res.code === 200))) {
    throw new Error((res && res.msg) || '接收报警失败')
  }
  return true
}

async function updateStatus(item, dbStatus, operator) {
  if (isExtendedAlarm(item)) {
    return updateExtendedAlarmStatus(item, dbStatus, operator)
  }
  const completed = dbStatus === 'completed' || dbStatus === 'handled'
  const handleInfo =
    dbStatus === 'executing'
      ? '执行中|已接收 ' + new Date().toLocaleString()
      : dbStatus === 'overdue'
        ? '逾期 ' + new Date().toLocaleString()
        : completed
          ? (item.solution || item.resolution || item.handleInfo || '已处理')
          : (item.handleInfo || '')
  const payload = patchPayload(item, {
    operator,
    fields: {
      status: completed ? 1 : 0,
      handleInfo
    }
  })
  const res = await updateZkDeviceAlarm(payload)
  if (!(res && (res.code === 0 || res.code === 200))) {
    throw new Error((res && res.msg) || '更新报警状态失败')
  }
  return true
}

async function completeAlarm(item, resolution, operator) {
  if (isExtendedAlarm(item)) {
    item.solution = resolution || item.solution || '已处理'
    return updateExtendedAlarmStatus(item, 'completed', operator)
  }
  const payload = patchPayload(item, {
    operator,
    fields: {
      status: 1,
      handleInfo: resolution || '已处理'
    }
  })
  const res = await updateZkDeviceAlarm(payload)
  if (!(res && (res.code === 0 || res.code === 200))) {
    throw new Error((res && res.msg) || '完成报警失败')
  }
  return true
}

/** 人员门禁 / 鱼类视觉扩展告警：更新各自业务表，不再走 zk_device_alarm */
async function updateExtendedAlarmStatus(item, dbStatus, operator) {
  const id = extendedAlarmNumericId(item)
  if (!Number.isFinite(id) || id <= 0) {
    throw new Error('扩展告警缺少有效编号')
  }
  const key = String(item.Id != null ? item.Id : (item.alarm_code || item.category || ''))
  const category = (/^fish|^FV/i.test(key) || item.category === 'fish') ? 'fish' : 'person'
  const status =
    dbStatus === 'completed' || dbStatus === 'handled' || dbStatus === 'done'
      ? 'completed'
      : dbStatus === 'executing' || dbStatus === 'exec'
        ? 'executing'
        : dbStatus === 'overdue'
          ? 'overdue'
          : 'pending'
  const { updateExtendedAlarm } = await import('@/api/fish/daPing')
  const res = await updateExtendedAlarm({
    category,
    id,
    status,
    ownerName: (operator && operator.name) || item.assignee_name || item.owner || ''
  })
  if (!(res && (res.code === 0 || res.code === 200))) {
    throw new Error((res && res.msg) || '扩展告警状态更新失败')
  }
  return true
}

async function transferToTask(item, operator, urgentTimeoutMinutes, options = {}) {
  const title = (item.alarm_title || item.title || '报警处置任务').trim()
  const level = 'urgent'
  const pondDetailNum = item.pool_code || item.poolCode || item.pondDetailNum || ''
  const taskTitle = '【报警转任务】' + title
  const alarmCode = item.alarm_code || item.alarmCode || ('ZK-TR-' + (item.id || item.Id || Date.now()))
  let taskCode = options.taskCode || ''

  if (!options.skipLocal) {
    const taskRes = createInspectionPlanTask({
      title: taskTitle,
      level,
      period: 'day',
      from: 'alert_monitor',
      pondDetailNum
    })
    if (!taskRes.ok) {
      throw new Error(taskRes.error || '转任务失败')
    }
    taskCode = (taskRes.task && taskRes.task.id) || taskCode
  }

  markAlarmUrgentLocal(
    { alarm_code: alarmCode, id: item.Id || item.id },
    taskCode
  )

  const raw = (item && item._raw) || {}
  let warnContent = String(item.alarm_reason || item.warnContent || raw.warnContent || '')
  if (!/级别:\s*urgent/i.test(warnContent)) {
    warnContent = (warnContent ? warnContent + ' | ' : '') + '级别:urgent'
  }
  const handleInfo = '已转紧急任务:' + taskCode + (urgentTimeoutMinutes ? ' |时限' + urgentTimeoutMinutes + '分钟' : '')

  const missionPromise = pushToMissionPlanning({
    alarmCode,
    title: taskTitle,
    taskTitle,
    content: item.alarm_reason || item.warnContent || title,
    level,
    forceUrgent: true,
    skipDedupe: true,
    category: item.category || 'fish',
    source: 'alert_monitor',
    pondDetailNum,
    pondName: item.pool_name || item.pondName || '',
    workshopName: item.workshop || item.workshopName || '',
    pondSystemName: item.system_name || item.pondSystemName || '',
    assigneeName: (operator && operator.name) || '',
    assigneeId: toBackendUserId(operator && operator.id) || '',
    currentValue: item.current_value || '',
    thresholdValue: item.threshold_value || '',
    unit: item.unit || ''
  }).then(function (missionRes) {
    if (missionRes && missionRes.taskCode) return missionRes.taskCode
    return taskCode
  }).catch(function (e) {
    console.warn('transferToTask mission planning', e)
    return taskCode
  })

  const zkPromise = isExtendedAlarm(item)
    ? updateExtendedAlarmStatus(item, 'executing', operator).catch(function (e) {
      console.warn('transferToTask extended status sync', e)
      return null
    })
    : updateZkDeviceAlarm(
      patchPayload(item, {
        operator,
        fields: {
          status: 0,
          warnType: 1,
          warnContent,
          handleInfo
        }
      })
    ).catch(function (e) {
      console.warn('transferToTask status sync', e)
      return null
    })

  const resolvedCode = await Promise.all([missionPromise, zkPromise]).then(function (results) {
    return results[0]
  })
  if (resolvedCode) taskCode = resolvedCode

  item.priority = 'urgent'
  if (item._raw) {
    item._raw.warnType = 1
    item._raw.handleInfo = '已转紧急任务:' + taskCode
  }
  return taskCode || options.taskCode || ''
}

const PlatformAlarmData = {
  loadAll,
  loadZkDeviceAlarms,
  loadOverviewAlerts,
  toOverviewAlert,
  acceptAlarm,
  updateStatus,
  completeAlarm,
  transferToTask,
  createZkDeviceAlarm
}

if (typeof window !== 'undefined') {
  window.PlatformAlarmData = PlatformAlarmData
}

export default PlatformAlarmData
