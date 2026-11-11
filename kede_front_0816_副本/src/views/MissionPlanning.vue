<script>
import $ from 'jquery'
import { syncOpenSystemAlarmsToMission, TASK_STORAGE_KEY, getWiiduApiBase } from '@/utils/alarmTaskBridge'
import { getThresholdModule, resolveTimeoutHours } from '@/utils/thresholdConfig'
import { formatWorkshop, formatSystem, formatPond, normalizeLakeName, isSitePlaceholder, workshopsEqual, systemsEqual, pondsEqual } from '@/utils/siteNaming'
import {
  loadSharedSiteOptions as fetchSiteOptions,
  peekSharedSiteOptions,
  pickWorkshopOptionLabels,
  pickSystemOptionLabels,
  pickPondOptionLabels,
  isTaskInUserSiteScope,
  isUserDeptScopeDenied,
  isPlatformUnscopedAdmin,
  resolveCurrentUserDeptId,
  SITE_OPTIONS_CHANGED_EVENT
} from '@/utils/siteOptions'
import { USER_SCOPE_CHANGED_EVENT } from '@/utils/deptScope'
import MyChat from '@/components/MyChat/index'
import PageLoadingMask from '@/components/PageLoadingMask/index'
var API_BASE_URL = getWiiduApiBase();
var REQUEST_TIMEOUT_MS = 25000;
var LOAD_ALL_TIMEOUT_MS = 45000;
var TASK_CACHE_KEY_BASE = 'kede_mission_tasks_cache_v8';
var TASK_CACHE_MS = 90000;

function taskCacheKey() {
  // 无归属部门：一律独立空缓存，禁止超管角色落到 :d:all 串看全站周/月/年
  if (resolveCurrentUserDeptId() == null) {
    return TASK_CACHE_KEY_BASE + ':d:denied';
  }
  var deptId = resolveCurrentUserDeptId();
  return TASK_CACHE_KEY_BASE + ':d:' + String(deptId);
}

/** 任务与计划：归属部门为空一律空列表（含 super_admin 等超管角色；验收：空白部门不可见任何周/月/年/日计划） */
function isMissionEmptyDeptDenied() {
  return resolveCurrentUserDeptId() == null;
}

function missionDeptSqlClause() {
  if (isMissionEmptyDeptDenied()) return ' AND 1=0';
  // 平台超管不按部门裁剪 SQL，避免 admin 只能看到本部门且被前端再滤空
  if (isPlatformUnscopedAdmin()) return '';
  var deptId = resolveCurrentUserDeptId();
  if (deptId == null) return ' AND 1=0';
  // 本部门及子部门站点白名单已确认为空：直接不查任务，避免串到他单位周/月/年计划
  // 白名单加载失败（_loadFailed）不得当空部门处理，仍按 department_id 查本部门
  var peek = peekSharedSiteOptions();
  if (peek && peek._loadFailed) {
    /* keep dept clause below */
  } else if (peek && (peek._ready || peek._deniedUnscoped)) {
    var hasSite =
      ((peek.workshopOptions || []).length > 0) ||
      ((peek.systemOptions || []).length > 0) ||
      ((peek.pondOptions || []).length > 0);
    if (!hasSite) return ' AND 1=0';
  }
  var n = Number(deptId);
  if (!Number.isFinite(n) || n <= 0) return ' AND 1=0';
  return ' AND (department_id=' + n + " OR department_id='" + n + "')";
}

function withTimeout(promise, ms, label) {
  return Promise.race([
    promise,
    new Promise(function (_, reject) {
      setTimeout(function () {
        reject(new Error((label || '请求') + '超时（' + ms + 'ms）'));
      }, ms);
    })
  ]);
}

function baseRequest(url, method, data, options) {
  options = options || {};
  return new Promise(function (resolve, reject) {
    $.ajax({
      url: API_BASE_URL + String(url).replace(/^\/+/, ''),
      type: (method || 'GET').toUpperCase(),
      contentType: 'application/json;charset=UTF-8',
      dataType: 'json',
      timeout: options.timeout !== undefined ? options.timeout : REQUEST_TIMEOUT_MS,
      data: JSON.stringify(data || {}),
      processData: false,
      success: function (raw) {
        if (options.noVerify) {
          resolve(raw);
          return;
        }
        var body = raw && raw.data !== undefined ? raw.data : raw;
        if (!body) {
          reject(new Error('后端未返回数据'));
          return;
        }
        if (body.status === undefined || Number(body.status) === 200) resolve(body);
        else reject(new Error(body.message || '后端请求失败'));
      },
      error: function (xhr, textStatus) {
        var statusText = xhr.status ? 'HTTP ' + xhr.status : '未收到 HTTP 响应';
        var message = '请求失败：' + (textStatus || '网络错误') + '（' + statusText + '）';
        try {
          var body = JSON.parse(xhr.responseText);
          message = body.message || body.error || body.msg || message;
          if (body.detail) message += '：' + body.detail;
        } catch (ignore) {}
        reject(new Error(message));
      }
    });
  });
}

var request = {};
['get', 'post', 'put', 'delete'].forEach(function (method) {
  request[method] = function (api, data, options) {
    return baseRequest(api, method, data, options);
  };
});

global.API_BASE_URL = API_BASE_URL;
global.request = request;
var BASE_NAMES = {
  tasks: 'task_plan',
  locations: 'aquaculture_location',
  departmentHeads: 'task_department_head',
  logs: 'task_operation_log',
  delays: 'task_delay_request',
  alarms: 'alarm_record'
};
var TABLES = {};
Object.keys(BASE_NAMES).forEach(function (key) {
  // 业务表名不带前缀；运行时代码统一补 test_table_。
  TABLES[key] = 'test_table_' + BASE_NAMES[key];
});

function esc(value) {
  return String(value == null ? '' : value)
    .replace(/\\/g, '\\\\')
    .replace(/'/g, "\\'")
    .replace(/\u0000/g, '');
}

function q(value) { return "'" + esc(value) + "'"; }
function n(value) {
  var number = Number(value);
  return isFinite(number) ? String(number) : '0';
}
function resultGroups(raw) {
  var payload = raw;
  if (typeof payload === 'string') {
    var text = payload.replace(/^\uFEFF/, '').trim();
    if (!text) return null;
    try { payload = JSON.parse(text); } catch (e) { return null; }
  }
  if (payload == null) return null;
  // 已是分组：[[rows],[rows]] 或单组行数组 [{...}]
  if (Array.isArray(payload)) return payload;
  if (typeof payload !== 'object') return null;

  // wiidu 业务失败：曾误用 code:10000 +「程序运行出错」；data 常为 ErrorId 字符串
  var failMsg = payload.msg != null ? String(payload.msg) : '';
  if (failMsg.indexOf('出错') >= 0 || failMsg.indexOf('失败') >= 0) return null;
  var code = payload.code;
  if (code !== undefined && Number(code) !== 10000 && Number(code) !== 200 && Number(code) !== 0) {
    if (typeof payload.data === 'string' || payload.data == null) return null;
  }

  // 常见包装：{ code, msg, data }；勿因 list:null 跳过解包
  if (Object.prototype.hasOwnProperty.call(payload, 'data')) {
    var inner = payload.data;
    if (typeof inner === 'string') {
      var innerText = inner.replace(/^\uFEFF/, '').trim();
      if (!innerText || /^ErrorId/i.test(innerText)) return null;
      try { inner = JSON.parse(innerText); } catch (e2) { return null; }
    }
    if (inner && typeof inner === 'object' && !Array.isArray(inner) && Object.prototype.hasOwnProperty.call(inner, 'data')) {
      inner = inner.data;
    }
    payload = inner;
  } else if (Array.isArray(payload.list)) {
    payload = payload.list;
  }

  if (typeof payload === 'string') return null;
  if (!Array.isArray(payload)) return null;
  return payload;
}
function errorText(error) {
  return error && error.message ? error.message : String(error || '未知错误');
}

async function read(sqls) {
  var list = Array.isArray(sqls) ? sqls : [sqls];
  var empty = list.map(function () { return []; });
  try {
    var raw = await global.request.post(
      'mysql/getDataListBySql.jsp',
      { sqlstring: list },
      { noVerify: true, noAuth: true }
    );
    var groups = resultGroups(raw);
    if (!groups) {
      console.warn('[MissionPlanning] 数据库返回格式无法识别', raw);
      return empty;
    }
    // 保证每一组都是数组；单 SQL 若直接返回行数组也包一层
    if (groups.length && groups[0] && !Array.isArray(groups[0]) && typeof groups[0] === 'object') {
      return [groups];
    }
    return groups.map(function (g) { return Array.isArray(g) ? g : []; });
  } catch (error) {
    console.warn('[MissionPlanning] 读库失败', error);
    return empty;
  }
}

async function readOne(sql) {
  var groups = await read([sql]);
  return Array.isArray(groups[0]) ? groups[0] : [];
}

async function safeRead(sql, label, errors) {
  try {
    return await readOne(sql);
  } catch (error) {
    errors.push(label + '：' + errorText(error));
    return [];
  }
}

// 本地开发经 devServer 代理写入；生产同域 /wiidu-datainf 由 nginx 反代
async function write(sqls) {
  var useProxy = API_BASE_URL.indexOf('/wiidu-datainf') === 0;
  var response = await fetch(global.API_BASE_URL + 'mysql/updateInfo.jsp', {
    method: 'POST',
    mode: useProxy ? 'cors' : 'no-cors',
    credentials: useProxy ? 'same-origin' : 'include',
    headers: { 'Content-Type': 'application/json;charset=UTF-8' },
    body: JSON.stringify({ sqlstring: sqls })
  });
  if (useProxy) {
    if (!response.ok) {
      throw new Error('wiidu write HTTP ' + response.status);
    }
    try {
      var raw = await response.json();
      var msg = raw && raw.msg != null ? String(raw.msg) : '';
      // JSP 失败曾误用 code:10000 +「程序运行出错」，必须同时看 msg
      if (msg.indexOf('出错') >= 0 || msg.indexOf('失败') >= 0) {
        throw new Error(msg || 'wiidu write failed');
      }
      var code = raw && raw.code;
      if (code !== undefined && Number(code) !== 10000 && Number(code) !== 200) {
        throw new Error(msg || 'wiidu write failed');
      }
    } catch (parseErr) {
      if (parseErr && /wiidu|出错|失败/.test(String(parseErr.message || ''))) throw parseErr;
      // 非 JSON 但 HTTP 200 时仍视为成功（部分网关只回文本）
    }
  }
  return response;
}

function logSql(task, actionType, beforeStatus, afterStatus, detail, operator) {
  return 'INSERT INTO ' + TABLES.logs +
    ' (task_code,operation_type,before_status,after_status,operator_id,operator_name,operation_note,operation_time,isDel) VALUES (' +
    q(task.task_code) + ',' + q(actionType) + ',' + q(beforeStatus) + ',' + q(afterStatus) + ',' +
    q(operator.id) + ',' + q(operator.name) + ',' + q(detail) + ",DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s'),0)";
}

function taskDeptIdOf(task) {
  if (!task) return null;
  var raw = task.department_id != null ? task.department_id : task.departmentId;
  if (raw == null || raw === '' || raw === 0 || raw === '0') return null;
  // wiidu 演示数据常用 DEP-OPS 等非数字编码，与系统数字 deptId 不可比
  if (!/^\d+$/.test(String(raw).trim())) return null;
  return raw;
}

/**
 * @returns {true|false|null} true=本部门；false=明确他部门；null=无数字部门标注
 */
function taskPassesDeptId(task) {
  var userDept = resolveCurrentUserDeptId();
  // 空部门不得因超管角色放行；有部门的平台超管仍不按数字部门硬比
  if (userDept == null) return false;
  var taskDept = taskDeptIdOf(task);
  if (taskDept == null) return null;
  return String(taskDept) === String(userDept);
}

function isPlantWideWorkshop(ws) {
  var s = String(ws == null ? '' : ws).trim();
  return s === '全部车间' || s === '全场' || s === '所有车间' || s === '全部';
}

/**
 * 部门用户：任务必须落在本部门可见车间/系统（鱼池责任单位）内。
 * - 平台超管且有归属部门：不过滤，可见全部（含全场周/月/年）
 * - 归属部门为空（含 super_admin）：一律不可见
 * - 本部门及子部门无任何车间/系统/鱼池：一律不可见（含周/月/年）
 * - 明确他部门 department_id：拒绝
 * - 禁止「长周期演示例外」放行白名单外车间
 */
function taskPassesSiteScope(task, siteOpts) {
  if (isMissionEmptyDeptDenied() || isUserDeptScopeDenied()) return false;
  // 超管看全量，避免被「无站点 / 全场车间」规则误杀成空屏
  if (isPlatformUnscopedAdmin()) return true;

  var deptScoped = resolveCurrentUserDeptId() != null;
  var siteScope = siteOpts || {};
  var hasAnySite =
    ((siteScope.workshopOptions || []).length > 0) ||
    ((siteScope.systemOptions || []).length > 0) ||
    ((siteScope.pondOptions || []).length > 0);

  var deptHit = taskPassesDeptId(task);
  if (deptHit === false) return false;

  // 白名单未就绪 / 加载失败：仅保留部门过滤，勿按空白名单误杀本部门长周期计划
  if (deptScoped && !hasAnySite) {
    if (!siteScope._ready || siteScope._loadFailed) {
      return deptHit !== false;
    }
    // 已就绪且确为空站点部门：不可见
    return false;
  }

  var sitePayload = {
    workshop: task && task.workshop,
    workshopName: task && (task.workshopName || task.workshop),
    system: task && (task.system_name || task.systemName),
    pondSystemName: task && (task.pondSystemName || task.system_name || task.systemName),
    pool_code: task && (task.pool_code || task.poolCode),
    pondDetailNum: task && (task.pondDetailNum || task.pool_code || task.poolCode)
  };
  if (isPlantWideWorkshop(task && (task.workshopName || task.workshop))) return false;
  return isTaskInUserSiteScope(sitePayload, siteOpts);
}

function readTaskCache() {
  try {
    var raw = sessionStorage.getItem(taskCacheKey());
    if (!raw) return null;
    var hit = JSON.parse(raw);
    if (!hit || !hit.at || Date.now() - hit.at > TASK_CACHE_MS) return null;
    // 缓存内再滤一次，防止旧会话脏数据
    var siteOpts = peekSharedSiteOptions();
    var tasks = (hit.tasks || []).filter(function (task) {
      return taskPassesSiteScope(task, siteOpts);
    });
    return { at: hit.at, tasks: tasks, serverTime: hit.serverTime || '' };
  } catch (e) {
    return null;
  }
}

function writeTaskCache(tasks, serverTime) {
  try {
    sessionStorage.setItem(taskCacheKey(), JSON.stringify({
      at: Date.now(),
      tasks: tasks || [],
      serverTime: serverTime || ''
    }));
  } catch (e) { /* ignore */ }
}

function clearTaskCache() {
  try {
    sessionStorage.removeItem(taskCacheKey());
    // 兼容旧 key
    sessionStorage.removeItem('kede_mission_tasks_cache_v5');
    sessionStorage.removeItem('kede_mission_tasks_cache_v6');
    sessionStorage.removeItem('kede_mission_tasks_cache_v7');
  } catch (e) { /* ignore */ }
}

async function loadSecondaryBatch(errors) {
  var locationSql = 'SELECT workshop,system_name,pool_code FROM ' + TABLES.locations +
    ' WHERE (isDel=0 OR isDel IS NULL) ORDER BY workshop,system_name,pool_code LIMIT 500';
  var deptSql = 'SELECT * FROM ' + TABLES.departmentHeads +
    " WHERE (isDel=0 OR isDel IS NULL) AND is_head=1 AND employment_status='on_duty'" +
    ' ORDER BY department_name,active_task_count,user_name LIMIT 200';
  var logSql = 'SELECT * FROM ' + TABLES.logs +
    ' WHERE (isDel=0 OR isDel IS NULL) ORDER BY operation_time DESC LIMIT 300';
  var delaySql = 'SELECT * FROM ' + TABLES.delays +
    " WHERE (isDel=0 OR isDel IS NULL) AND review_status='pending' ORDER BY apply_time DESC LIMIT 200";
  var alarmSql = 'SELECT * FROM ' + TABLES.alarms +
    " WHERE (isDel=0 OR isDel IS NULL) AND priority IN ('urgent','priority') AND alarm_status<>'completed' ORDER BY alarm_time DESC LIMIT 200";
  try {
    var groups = await read([
      locationSql,
      deptSql,
      logSql,
      delaySql,
      alarmSql,
      'SELECT NOW() AS server_time'
    ]);
    return groups.map(function (group) { return Array.isArray(group) ? group : []; });
  } catch (error) {
    errors.push('次级数据读取失败：' + errorText(error));
    return [[], [], [], [], [], []];
  }
}

async function loadAll(opts) {
  opts = opts || {};
  if (isMissionEmptyDeptDenied()) {
    return {
      tasks: [],
      locations: [],
      departmentHeads: [],
      logs: [],
      delays: [],
      alarms: [],
      serverTime: '',
      _errors: []
    };
  }
  if (!global.request || !global.request.post) throw new Error('请求模块未加载');
  var errors = [];
  // 只取列表所需字段；去掉易缺列，避免 wiidu 返回 ErrorId 字符串导致整批解析失败
  var taskCols = 'Id,task_code,task_name,task_content,period_type,category,priority,task_status,' +
    'workshop,system_name,pool_code,department_id,department_name,assignee_id,assignee_name,' +
    'start_time,end_time,estimated_minutes,actual_start_time,actual_minutes,complete_time,' +
    'instructions,notes,source_type,source_alarm_code,delay_status';
  var taskWhere = " WHERE (isDel=0 OR isDel IS NULL) AND task_name IS NOT NULL AND task_name<>''" +
    missionDeptSqlClause();
  var weekSql = 'SELECT ' + taskCols + ' FROM ' + TABLES.tasks + taskWhere +
    " AND LOWER(period_type)='week' ORDER BY start_time DESC LIMIT 120";
  var monthSql = 'SELECT ' + taskCols + ' FROM ' + TABLES.tasks + taskWhere +
    " AND LOWER(period_type)='month' ORDER BY start_time DESC LIMIT 120";
  var yearSql = 'SELECT ' + taskCols + ' FROM ' + TABLES.tasks + taskWhere +
    " AND LOWER(period_type)='year' ORDER BY start_time DESC LIMIT 80";
  var daySql = 'SELECT ' + taskCols + ' FROM ' + TABLES.tasks + taskWhere +
    " AND (period_type IS NULL OR period_type='' OR LOWER(period_type)='day')" +
    ' ORDER BY start_time DESC LIMIT 220';

  var seen = {};
  var tasks = [];
  function pushTaskRows(rows) {
    (Array.isArray(rows) ? rows : []).forEach(function (row) {
      var key = row && row.Id != null ? 'id:' + row.Id : ('code:' + (row && row.task_code || ''));
      if (!row || seen[key]) return;
      seen[key] = true;
      tasks.push(row);
    });
  }

  // 周/月/年分请求：避免一批 SQL 失败/超时导致三列全空；日计划仍单独请求
  var weekPromise = read([weekSql]);
  var monthPromise = read([monthSql]);
  var yearPromise = read([yearSql]);
  var timePromise = read(['SELECT NOW() AS server_time']);
  var dayPromise = read([daySql]);

  var weekGroups = await weekPromise;
  var monthGroups = await monthPromise;
  var yearGroups = await yearPromise;
  var timeGroups = await timePromise;
  pushTaskRows(weekGroups[0]);
  pushTaskRows(monthGroups[0]);
  pushTaskRows(yearGroups[0]);
  var serverTime = timeGroups[0] && timeGroups[0][0] ? timeGroups[0][0].server_time : '';
  if (typeof opts.onEarlyPeriods === 'function') {
    try {
      opts.onEarlyPeriods({
        tasks: tasks.slice(),
        serverTime: serverTime
      });
    } catch (ignore) {}
  }

  var dayGroups = await dayPromise;
  pushTaskRows(dayGroups[0]);

  var lazy = opts.lazySecondary !== false;
  var secondaryPromise = loadSecondaryBatch(errors);

  var results;
  if (lazy) {
    results = [[], [], [], [], [], serverTime ? [{ server_time: serverTime }] : []];
    secondaryPromise.then(function (sec) {
      if (typeof opts.onSecondary === 'function') opts.onSecondary(sec, errors);
    }).catch(function () {});
  } else {
    results = await secondaryPromise;
    if (!results[5] || !results[5][0] || !results[5][0].server_time) {
      results[5] = [{ server_time: serverTime }];
    }
  }

  return {
    tasks: tasks,
    locations: results[0],
    departmentHeads: results[1],
    logs: results[2],
    delays: results[3],
    alarms: results[4],
    serverTime: results[5] && results[5][0] ? results[5][0].server_time : serverTime,
    _errors: errors,
    _secondaryPromise: lazy ? secondaryPromise : null
  };
}

async function createTask(form, operator) {
  var code = 'TK-' + Date.now();
  var categories = Array.isArray(form.categories) ? form.categories.join(',') : form.category;
  var sql = 'INSERT INTO ' + TABLES.tasks +
    ' (task_code,task_name,task_content,period_type,category,priority,task_status,workshop,system_name,pool_code,' +
    'department_id,department_name,assignee_id,assignee_name,publisher_id,publisher_name,start_time,end_time,' +
    'estimated_minutes,sop_url,knowledge_url,instructions,notes,source_type,source_alarm_code,delay_status,isDel) VALUES (' +
    [q(code), q(form.name), q(form.content), q(form.periodType), q(categories), q(form.level), q('unreceived'),
      q(form.workshop), q(form.systemName), q(form.poolCode), q(form.departmentCode), q(form.departmentName),
      q(form.assigneeId), q(form.assigneeName), q(operator.id), q(operator.name), q(form.startTime), q(form.endTime),
      n(form.estimatedMinutes), q(form.sopUrl), q(form.knowledgeUrl), q(form.instructions), q(form.notes),
      q('manual'), q(''), q('none'), '0'].join(',') + ')';
  var pseudoTask = { Id: 0, task_code: code };
  await write([sql, logSql(pseudoTask, 'create', '', 'unreceived', '创建并发布任务：' + form.name, operator)]);
  return code;
}

async function acceptTask(task, operator) {
  var idPart = Number(task.Id) > 0 ? ('Id=' + n(task.Id)) : '0=1';
  var codePart = task.task_code ? ('task_code=' + q(task.task_code)) : '0=1';
  var sql = 'UPDATE ' + TABLES.tasks +
    " SET task_status='executing',actual_start_time=COALESCE(actual_start_time,NOW()) WHERE (" +
    idPart + ' OR ' + codePart + ')' +
    " AND task_status IN ('unreceived','overdue') AND (isDel=0 OR isDel IS NULL)";
  await write([sql, logSql(task, 'accept', task.task_status, 'executing', '接收任务', operator)]);
}

async function completeTask(task, form, operator) {
  var sqls = [
    'UPDATE ' + TABLES.tasks + " SET task_status='completed',complete_time=NOW(),actual_minutes=TIMESTAMPDIFF(MINUTE,COALESCE(actual_start_time,start_time),NOW()),result_record=" + q(form.resultRecord) +
    ',detection_data=' + q(form.detectionData) + ',attachment_urls=' + q(form.attachmentUrls) +
    ' WHERE Id=' + n(task.Id) + ' AND (isDel=0 OR isDel IS NULL)',
    logSql(task, 'complete', task.task_status, 'completed', form.resultRecord, operator)
  ];
  if (task.source_alarm_code) {
    sqls.push('UPDATE ' + TABLES.alarms + " SET alarm_status='completed',resolution=" + q(form.resultRecord) +
      ' WHERE alarm_code=' + q(task.source_alarm_code) + ' AND (isDel=0 OR isDel IS NULL)');
  }
  await write(sqls);
}

async function requestDelay(task, form, operator) {
  var sqls = [
    'INSERT INTO ' + TABLES.delays +
    ' (task_id,task_code,original_end_time,requested_end_time,delay_reason,applicant_id,applicant_name,review_status,apply_time,isDel) VALUES (' +
    n(task.Id) + ',' + q(task.task_code) + ',' + q(task.end_time) + ',' + q(form.requestedEnd) + ',' +
    q(form.delayReason) + ',' + q(operator.id) + ',' + q(operator.name) + ",'pending',NOW(),0)",
    'UPDATE ' + TABLES.tasks + " SET delay_status='pending' WHERE Id=" + n(task.Id),
    logSql(task, 'delay_apply', task.task_status, task.task_status, '申请延期至 ' + form.requestedEnd + '：' + form.delayReason, operator)
  ];
  await write(sqls);
}

async function reassignTask(task, assignee, operator) {
  var detail = '由 ' + (task.assignee_name || '未分配') + ' 重新分配给 ' + assignee.user_name;
  await write([
    'UPDATE ' + TABLES.tasks + ' SET department_id=' + q(assignee.department_id) +
    ',department_name=' + q(assignee.department_name) + ',assignee_id=' + q(assignee.user_id) +
    ',assignee_name=' + q(assignee.user_name) + ' WHERE Id=' + n(task.Id),
    logSql(task, 'reassign', task.task_status, task.task_status, detail, operator)
  ]);
}

async function reviewDelay(delay, approved, remark, operator) {
  var status = approved ? 'approved' : 'rejected';
  var sqls = [
    'UPDATE ' + TABLES.delays + ' SET review_status=' + q(status) + ',reviewer_id=' + q(operator.id) +
    ',reviewer_name=' + q(operator.name) + ',review_remark=' + q(remark) + ',review_time=NOW() WHERE Id=' + n(delay.Id)
  ];
  if (approved) {
    sqls.push('UPDATE ' + TABLES.tasks + ' SET end_time=' + q(delay.requested_end_time) +
      ",delay_status='approved' WHERE Id=" + n(delay.task_id));
  } else {
    sqls.push('UPDATE ' + TABLES.tasks + " SET delay_status='rejected' WHERE Id=" + n(delay.task_id));
  }
  await write(sqls);
}

async function syncAlarmTasks(alarms, tasks, operator) {
  var existing = {};
  tasks.forEach(function (task) {
    if (task.source_alarm_code) existing[task.source_alarm_code] = true;
  });
  var sqls = [];
  alarms.forEach(function (alarm) {
    if (!alarm.alarm_code || existing[alarm.alarm_code]) return;
    var code = 'TK-AL-' + alarm.alarm_code;
    var priority = alarm.priority === 'urgent' ? 'urgent' : 'priority';
    var minutes = priority === 'urgent' ? 60 : 120;
    var endExpr = 'DATE_ADD(NOW(), INTERVAL ' + minutes + ' MINUTE)';
    sqls.push('INSERT INTO ' + TABLES.tasks +
      ' (task_code,task_name,task_content,period_type,category,priority,task_status,workshop,system_name,pool_code,' +
      'assignee_name,publisher_id,publisher_name,start_time,end_time,estimated_minutes,instructions,source_type,source_alarm_code,delay_status,isDel) VALUES (' +
      [q(code), q(alarm.alarm_title), q(alarm.alarm_reason), q('day'), q(alarm.category), q(priority), q('unreceived'),
        q(alarm.workshop), q(alarm.system_name), q(alarm.pool_code), q(alarm.assignee_name), q('system'), q('系统报警'),
        'NOW()', endExpr, String(minutes), q(alarm.alarm_reason), q('alarm'), q(alarm.alarm_code), q('none'), '0'].join(',') + ')');
  });
  if (sqls.length) await write(sqls);
  return sqls.length;
}

global.PlatformTaskData = {
  TABLES: TABLES,
  loadAll: loadAll,
  createTask: createTask,
  acceptTask: acceptTask,
  completeTask: completeTask,
  requestDelay: requestDelay,
  reassignTask: reassignTask,
  reviewDelay: reviewDelay,
  syncAlarmTasks: syncAlarmTasks
};
var ICONS = {
  fish: '<path d="M12 4c-2.5 2-7 3.5-9 6 2 2.5 6.5 4 9 6 2.5-2 7-3.5 9-6-2-2.5-6.5-4-9-6zm0 4.5a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3z"/>',
  water: '<path d="M12 2.7S5 10.1 5 14.5A7 7 0 0 0 12 21a7 7 0 0 0 7-6.5C19 10.1 12 2.7 12 2.7z"/>',
  equip: '<path d="M19.14 12.94c.04-.31.06-.63.06-.94s-.02-.63-.06-.94l2.03-1.58-1.92-3.32-2.39.96a7.3 7.3 0 0 0-1.62-.94l-.36-2.54h-3.84l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96-1.92 3.32 2.03 1.58c-.04.31-.06.63-.06.94s.02.63.06.94l-2.03 1.58 1.92 3.32 2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54h3.84l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96 1.92-3.32-2.03-1.58zM12 15.6A3.6 3.6 0 1 1 12 8.4a3.6 3.6 0 0 1 0 7.2z"/>',
  person: '<path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8V22h19.2v-2.8c0-3.2-6.4-4.8-9.6-4.8z"/>'
}
var STATUS_TEXT = { done: '已完成', exec: '执行中', pending: '未接收', overdue: '已逾期', delayed: '已延期' }
var DB_STATUS = { completed: 'done', executing: 'exec', unreceived: 'pending', overdue: 'overdue' }
var DB_PRIORITY = { urgent: 'urgent', priority: 'priority', routine: 'daily', daily: 'daily' }
var PERIODS = [
  { key: 'day', title: '日计划', en: 'DAILY', tone: 'green' },
  { key: 'week', title: '周计划', en: 'WEEKLY', tone: 'blue' },
  { key: 'month', title: '月计划', en: 'MONTHLY', tone: 'purple' },
  { key: 'year', title: '年计划', en: 'ANNUAL', tone: 'orange' }
]

function pad(value) {
  return String(value).padStart(2, '0')
}

function dateOnly(date) {
  return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate())
}

function inputDateTime(date) {
  return dateOnly(date) + 'T' + pad(date.getHours()) + ':' + pad(date.getMinutes())
}

function sqlDate(value) {
  return value ? String(value).replace('T', ' ') + (String(value).length === 16 ? ':00' : '') : ''
}

function parseDate(value) {
  if (!value) return null
  var parsed = new Date(String(value).replace(' ', 'T'))
  return isNaN(parsed.getTime()) ? null : parsed
}

function minutesBetween(start, end) {
  var a = parseDate(start), b = parseDate(end)
  return a && b ? Math.max(0, Math.round((b - a) / 60000)) : 0
}

function durationText(minutes) {
  var number = Number(minutes)
  if (!isFinite(number) || number <= 0) return '—'
  if (number < 60) return number + 'm'
  if (number % 60 === 0) return (number / 60) + 'h'
  return Math.floor(number / 60) + 'h ' + (number % 60) + 'm'
}

function unique(values) {
  var seen = {}
  return values.filter(function(value) {
    value = String(value || '').trim()
    if (!value || seen[value]) return false
    seen[value] = true
    return true
  })
}

function currentUser(provided) {
  if (provided) {
    return {
      id: provided.id || provided.Id || '',
      name: provided.name || provided.user_name || '当前登录用户',
      role: provided.role || 'operator'
    }
  }
  var query = new URLSearchParams(location.search)
  var stored = {}
  try {
    stored = JSON.parse(localStorage.getItem('userInfo') || '{}')
  } catch (ignore) {
  }
  return {
    id: query.get('user_id') || stored.Id || stored.id || localStorage.getItem('user_id') || '',
    name: query.get('user_name') || stored.user_name || stored.name || localStorage.getItem('user_name') || '当前登录用户',
    role: query.get('user_role') || stored.role || localStorage.getItem('user_role') || 'operator'
  }
}

function displayStatus(row) {
  if (row.task_status === 'completed' || row.status === 'done') return 'done'
  // 延期审批通过/进行中：计入「已延期」，避免被截止日期改写成逾期后落到「未接收」
  var delay = String(row.delay_status || '').toLowerCase()
  if (delay === 'approved' || delay === 'pending' || row.status === 'delayed') return 'delayed'
  var mapped = DB_STATUS[row.task_status] || row.status || ''
  // 执行中保持执行中，不因过期截止时间被改成逾期（否则顶部「执行中」永远是 0）
  if (mapped === 'exec' || row.task_status === 'executing') return 'exec'
  var deadline = parseDate(row.end_time)
  if (deadline) {
    var graceHours = 0
    var priority = String(row.priority || row.groupLevel || '')
    var th = global.__missionTimeoutHours || { urgent: 0, priority: 0, routine: 0 }
    if (priority === 'urgent' || priority.indexOf('紧急') >= 0) graceHours = th.urgent || 0
    else if (priority === 'priority' || priority.indexOf('优先') >= 0) graceHours = th.priority || 0
    else graceHours = th.routine || 0
    if (deadline.getTime() + graceHours * 3600000 < Date.now()) return 'delayed'
  }
  return mapped || 'pending'
}

/** 合并各模块写入 localStorage 的巡检任务（饵料系数等），保证任务与计划页可见 */
function mergeLocalInspectionTasks(taskRows) {
  // 空白部门账号：禁止本地巡检任务绕过部门过滤出现在周/月/年
  if (isMissionEmptyDeptDenied()) return [];
  var list = []
  try {
    var raw = localStorage.getItem(TASK_STORAGE_KEY)
    if (raw) {
      var parsed = JSON.parse(raw)
      if (Array.isArray(parsed)) list = parsed
    }
  } catch (e) { /* ignore */ }
  if (!list.length) return taskRows
  var existing = {}
  ;(taskRows || []).forEach(function (t) {
    var key = String(t.source_alarm_code || t.task_code || t.name || t.task_name || '')
    if (key) existing[key] = true
    if (t.name) existing[String(t.name)] = true
    if (t.task_name) existing[String(t.task_name)] = true
  })
  var extras = []
  list.forEach(function (item) {
    if (!item || item.done) return
    var title = String(item.title || '').trim()
    if (!title || existing[title]) return
    var localId = 'LOCAL-' + (item.id || title)
    if (existing[localId]) return
    existing[title] = true
    existing[localId] = true
    var level = item.level === 'urgent' ? 'urgent' : (item.level === 'normal' || item.level === 'daily' ? 'routine' : 'priority')
    var period = item.period || 'day'
    if (period === 'daily') period = 'day'
    var ws = item.workshop || item.workshopName || ''
    var sys = item.systemName || item.pondSystemName || ''
    // 部门用户：不把无站点的本地巡检标成「全部车间」以免绕过范围过滤
    if (resolveCurrentUserDeptId() != null) {
      if (!String(ws).trim() && !String(sys).trim() && !(item.pondDetailNum || item.pool_code)) return
    } else {
      ws = ws || '全部车间'
      sys = sys || '全部系统'
    }
    extras.push({
      Id: localId,
      task_code: localId,
      task_name: title,
      task_content: title,
      period_type: period,
      category: item.from === 'fcr' ? 'fish' : (item.from || 'fish'),
      priority: level,
      task_status: 'unreceived',
      workshop: ws,
      system_name: sys,
      pool_code: item.pondDetailNum || '',
      end_time: '',
      start_time: item.createdAt ? new Date(item.createdAt).toISOString().slice(0, 19).replace('T', ' ') : new Date().toISOString().slice(0, 19).replace('T', ' '),
      source_type: 'local',
      source_alarm_code: localId,
      estimated_minutes: 60,
      isDel: 0,
      from_module: item.from || ''
    })
  })
  return (taskRows || []).concat(extras)
}

function cleanPoolCode(raw) {
  if (isSitePlaceholder(raw)) return ''
  var s = String(raw == null ? '' : raw).trim()
  // 历史污染：仅由「全部系统 / 占位符」反复拼接而成
  if (/全部系统/.test(s) && !/\d/.test(s) && !/[A-Za-z]/.test(s.replace(/全部[车间系统鱼池]/g, ''))) return ''
  return s
}

function displayPondCode(raw) {
  var pool = cleanPoolCode(raw)
  if (!pool) return '—'
  return formatPond(pool) || normalizeLakeName(pool) || pool
}

function normalizeLocationRow(row) {
  if (!row) return row
  var pool = cleanPoolCode(row.pool_code)
  return {
    workshop: formatWorkshop(row.workshop) || row.workshop,
    system_name: formatSystem(row.system_name) || row.system_name,
    pool_code: pool ? (formatPond(pool) || pool) : ''
  }
}

function mapLocations(rows) {
  return (rows || []).map(normalizeLocationRow)
}

function normalizeTask(row, logs, delays) {
  var categories = String(row.category || 'water').split(',').filter(Boolean)
  var status = displayStatus(row)
  var taskLogs = logs.filter(function(item) {
    return String(item.task_id) === String(row.Id) || (item.task_code && item.task_code === row.task_code)
  }).map(function(item) {
    return Object.assign({}, item, {
      action_type: item.action_type || item.operation_type,
      action_detail: item.action_detail || item.operation_note
    })
  })
  var pendingDelay = delays.find(function(item) {
    return String(item.task_id) === String(row.Id) || (item.task_code && item.task_code === row.task_code)
  }) || null
  var actualMinutes = Number(row.actual_minutes) || minutesBetween(row.actual_start_time || row.start_time, row.complete_time)
  var poolRaw = cleanPoolCode(row.pool_code)
  // 不写回展示文案到 pool_code，避免静默刷新时 normalize 指数膨胀
  return Object.assign({}, row, {
    name: row.task_name || '未命名任务',
    period_type: String(row.period_type || 'day').toLowerCase(),
    status: status,
    cat: categories[0] === 'device' ? 'equip' : categories[0],
    categories: categories,
    groupLevel: DB_PRIORITY[row.priority] || 'daily',
    timeLabel: row.end_time ? String(row.end_time).slice(5, 16) : '待定',
    start: row.start_time || '—',
    end: row.end_time || '—',
    planDur: durationText(row.estimated_minutes),
    actualDur: actualMinutes ? durationText(actualMinutes) : '—',
    actualOk: !actualMinutes || actualMinutes <= Number(row.estimated_minutes || 0),
    workshop: formatWorkshop(row.workshop) || row.workshop || '全场',
    system_name: formatSystem(row.system_name) || row.system_name || '全部系统',
    pool_code: poolRaw,
    sys: formatSystem(row.system_name) || row.system_name || '全部系统',
    pond: displayPondCode(poolRaw),
    owner: row.assignee_name || '待指派',
    helper: row.collaborator_names || '',
    resp: row.actual_start_time ? durationText(minutesBetween(row.start_time, row.actual_start_time)) : '—',
    progress: status === 'done' ? '100%' : status === 'exec' ? '执行中' : '—',
    quality: row.quality_score || '—',
    fault: row.source_type === 'alarm' || !!row.source_alarm_code,
    sop: row.sop_url || '',
    knowledgeUrl: row.knowledge_url || '',
    instructionsText: row.instructions || row.task_content || '',
    notesText: row.notes || '',
    resultText: row.result_record || '',
    logs: taskLogs,
    pendingDelay: pendingDelay,
    _open: false
  })
}

var PAGE_CONFIG = {
  icons: ICONS,
  statusText: STATUS_TEXT,
  dbStatus: DB_STATUS,
  dbPriority: DB_PRIORITY,
  periods: PERIODS
}
export default {
  components: { MyChat, PageLoadingMask },
  data: function() {
    var today = new Date()
    var start = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 9, 0)
    var end = new Date(start.getTime() + 60 * 60000)
    return {
      config: PAGE_CONFIG,
      loading: false,
      dataSource: 'loading',
      backendMessage: '正在连接数据库',
      workshop: '全部车间',
      system: '全部系统',
      poolCode: '全部鱼池',
      dateStart: dateOnly(new Date(today.getFullYear(), today.getMonth(), 1)),
      dateEnd: dateOnly(new Date(today.getFullYear(), today.getMonth() + 1, 0)),
      openDd: null,
      pressKey: null,
      nowText: '',
      serverTimeBase: null,
      serverTimeLoadedAt: 0,
      operator: currentUser(),
      taskRows: [],
      visibleTaskLimit: 8,
      /** 已展开「查看全部」的分组：periodKey-level */
      expandedGroupKeys: {},
      refreshInterval: 20000,
      locations: [],
      sharedSiteOptions: peekSharedSiteOptions() || { workshops: [], systems: [], ponds: [], workshopOptions: [], systemOptions: [], pondOptions: [] },
      departmentHeads: [],
      alarms: [],
      rawLogs: [],
      rawDelays: [],
      newPlanOpen: false,
      newPlanTarget: null,
      newForm: {
        name: '', content: '', level: 'priority', categories: ['water'], departmentCode: '', departmentName: '',
        assigneeId: '', assigneeName: '', startTime: inputDateTime(start), endTime: inputDateTime(end), estimatedMinutes: 60,
        workshop: '', systemName: '', poolCode: '', sopUrl: '', knowledgeUrl: '', instructions: '', notes: ''
      },
      actionOpen: false,
      actionType: '',
      actionTask: null,
      actionForm: { resultRecord: '', detectionData: '', attachmentUrls: '', delayReason: '', requestedEnd: '', assigneeId: '', reviewRemark: '', reviewApproved: true },
      toastText: '',
      _toastTimer: null,
      _clockTimer: null,
      _refreshTimer: null,
      _alarmSyncing: false,
    }
  },
  computed: {
    workshopOpts: function () {
      // 与健康/成活率/投喂等页同源：只读生产记录三表，禁止回退 locations/任务行
      var labels = unique(pickWorkshopOptionLabels(this.sharedSiteOptions)).filter(function (v) {
        return v && !isSitePlaceholder(v) && v !== '所有车间';
      });
      return ['全部车间'].concat(labels);
    },
    systemOpts: function () {
      var ws = this.workshop === '全部车间' ? '' : this.workshop;
      var labels = unique(pickSystemOptionLabels(this.sharedSiteOptions, ws)).filter(function (v) {
        return v && !isSitePlaceholder(v) && v !== '所有系统';
      });
      return ['全部系统'].concat(labels);
    },
    poolOpts: function () {
      var ws = this.workshop === '全部车间' ? '' : this.workshop;
      var sys = this.system === '全部系统' ? '' : this.system;
      var labels = unique(pickPondOptionLabels(this.sharedSiteOptions, ws, sys)).filter(function (v) {
        return v && !isSitePlaceholder(v) && v !== '所有鱼池';
      });
      return ['全部鱼池'].concat(labels);
    },
    departmentOpts: function () {
      var map = {};
      this.departmentHeads.forEach(function (head) {
        if (!head.department_id) return;
        map[head.department_id] = { code: head.department_id, name: head.department_name || head.department_id };
      });
      return Object.keys(map).map(function (key) { return map[key]; });
    },
    availableHeads: function () {
      var code = this.newForm.departmentCode;
      return this.departmentHeads.filter(function (head) { return String(head.department_id) === String(code); });
    },
    reassignHeads: function () {
      var task = this.actionTask;
      if (!task) return [];
      return this.departmentHeads.filter(function (head) {
        return !task.department_id || String(head.department_id) === String(task.department_id);
      });
    },
    filteredTasks: function () {
      var self = this;
      var from = this.dateStart ? new Date(this.dateStart + 'T00:00:00').getTime() : -Infinity;
      var to = this.dateEnd ? new Date(this.dateEnd + 'T23:59:59').getTime() : Infinity;
      if (isMissionEmptyDeptDenied() || isUserDeptScopeDenied()) return [];
      var deptScoped = resolveCurrentUserDeptId() != null;
      var allWs = this.workshop === '全部车间' || this.workshop === '全场';
      var allSys = this.system === '全部系统';
      return this.taskRows.filter(function (task) {
        var period = task.period_type || 'day';
        // 部门可见范围：周/月/年与日计划同一套规则，禁止「全部车间」放行他部门长周期
        if (!taskPassesSiteScope(task, self.sharedSiteOptions)) return false;
        // 车间/系统/鱼池：再按用户当前筛选项过滤。
        var taskWsAll = !task.workshop || task.workshop === '全部车间' || task.workshop === '全场'
        if (deptScoped) {
          if (!allWs && !workshopsEqual(task.workshop, self.workshop) && !taskWsAll) return false;
        } else if (!(allWs || taskWsAll || workshopsEqual(task.workshop, self.workshop))) {
          return false;
        }
        var taskSysAll = !task.system_name || task.system_name === '全部系统'
        if (deptScoped) {
          if (!allSys && !systemsEqual(task.system_name, self.system) && !taskSysAll) return false;
        } else if (!(allSys || taskSysAll || systemsEqual(task.system_name, self.system))) {
          return false;
        }
        var allPool = self.poolCode === '全部鱼池'
        if (!(allPool || !task.pool_code || task.pool_code === '—' || pondsEqual(task.pool_code, self.poolCode))) return false;
        var start = parseDate(task.start_time);
        var end = parseDate(task.end_time || task.start_time);
        var inRange = (!start || start.getTime() <= to) && (!end || end.getTime() >= from);
        var open = task.status === 'pending' || task.status === 'exec' || task.status === 'overdue' || task.status === 'delayed';
        // 日计划：未完成始终可见
        if (period === 'day' && open) return true;
        // 年计划：同年即可见
        if (period === 'year' && start && self.dateStart) {
          var yearHit = start.getFullYear() === new Date(self.dateStart + 'T00:00:00').getFullYear();
          if (yearHit) return true;
        }
        // 周/月计划：未完成且起止落在筛选年（或与日期窗相交）均保留，避免被日期窗切空
        if ((period === 'week' || period === 'month') && open && start && self.dateStart) {
          var y0 = new Date(self.dateStart + 'T00:00:00').getFullYear();
          if (start.getFullYear() === y0) return true;
          if (end && end.getFullYear() === y0) return true;
        }
        return inRange;
      });
    },
    plans: function () {
      var self = this;
      return PERIODS.map(function (period) {
        var list = self.filteredTasks.filter(function (task) { return (task.period_type || 'day') === period.key; });
        var stats = { done: 0, exec: 0, pending: 0, delayed: 0 };
        list.forEach(function (task) {
          var status = task.status === 'overdue' ? 'delayed' : task.status;
          status = Object.prototype.hasOwnProperty.call(stats, status) ? status : 'pending';
          stats[status] += 1;
        });
        function makeGroup(level, label) {
          var allTasks = list.filter(function (task) { return task.groupLevel === level; });
          var groupKey = period.key + '-' + level;
          var expanded = !!self.expandedGroupKeys[groupKey];
          var limit = expanded ? allTasks.length : self.visibleTaskLimit;
          var shown = [];
          var localExtra = 0;
          allTasks.forEach(function (t, i) {
            // 默认只渲染前 N 条，避免告警风暴拖垮 DOM；可点击「展开」查看全部
            if (i < limit) {
              shown.push(t);
              return;
            }
            if (!expanded && t.source_type === 'local' && localExtra < 2) {
              shown.push(t);
              localExtra += 1;
            }
          });
          return {
            level: level,
            label: label,
            total: allTasks.length,
            tasks: shown,
            groupKey: groupKey,
            expanded: expanded,
            hiddenCount: Math.max(0, allTasks.length - shown.length)
          };
        }
        var groups = [
          makeGroup('urgent', '紧急任务'),
          makeGroup('priority', '优先任务'),
          makeGroup('daily', '日常任务')
        ];
        return Object.assign({}, period, { stats: stats, groups: groups, rate: list.length ? Math.round(stats.done / list.length * 100) : 0 });
      });
    },
    actionTitle: function () {
      return { complete: '提交完成', delay: '延期申请', reassign: '重新分配', reviewDelay: '审批延期申请' }[this.actionType] || '任务处置';
    },
    isAdmin: function () { return ['admin', 'administrator'].indexOf(String(this.operator.role).toLowerCase()) >= 0; }
  },
  mounted: function() {
    if (isMissionEmptyDeptDenied()) {
      this.taskRows = []
      this._unfilteredTasks = []
      try { sessionStorage.removeItem(taskCacheKey()); } catch (e) { /* ignore */ }
    }
    this.bootstrapPreviewTasks()
    this.tickClock()
    this.loadSharedSiteOptions(true)
    this.reload(false)
    this._clockTimer = setInterval(this.tickClock.bind(this), 30000)
    var self = this
    this._onLocalTask = function () { self.reload(true, true) }
    this._onSiteOptionsChanged = function () { self.loadSharedSiteOptions(true) }
    if (typeof window !== 'undefined') {
      window.addEventListener('kede-inspection-task-created', this._onLocalTask)
      window.addEventListener('storage', this._onLocalTask)
      window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged)
      this._onUserScopeChanged = function () {
        clearTaskCache()
        self.taskRows = []
        self.loadSharedSiteOptions(true).finally(function () {
          if (typeof self.reload === 'function') self.reload(true)
        })
      }
      window.addEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged)
    }
  },
  activated: function () {
    this.loadSharedSiteOptions(true)
  },
  beforeDestroy: function() {
    clearInterval(this._clockTimer)
    clearTimeout(this._refreshTimer)
    clearTimeout(this._toastTimer)
    if (typeof window !== 'undefined' && this._onLocalTask) {
      window.removeEventListener('kede-inspection-task-created', this._onLocalTask)
      window.removeEventListener('storage', this._onLocalTask)
    }
    if (typeof window !== 'undefined' && this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged)
    }
    if (typeof window !== 'undefined' && this._onUserScopeChanged) {
      window.removeEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged)
    }
  },
  methods: {
    loadSharedSiteOptions: function (force) {
      var self = this
      return fetchSiteOptions({ includePonds: true, force: !!force }).then(function (opts) {
        self.sharedSiteOptions = opts || self.sharedSiteOptions
        if (self.sharedSiteOptions) {
          self.sharedSiteOptions._ready = true
        }
        self.sanitizeSiteFilters()
        // 白名单到位后，用暂存的全量行重算，修复首屏竞态滤空
        if (self._unfilteredTasks && self._unfilteredTasks.length) {
          if (self.commitFilteredTasks(null)) {
            self.dataSource = 'database'
            self.backendMessage = '数据库实时数据'
          }
        }
      }).catch(function (e) {
        console.warn('[MissionPlanning] shared site options', e)
      })
    },
    /** 有归属部门时，站点白名单未就绪不得按空白名单滤任务 */
    canApplySiteFilter: function () {
      var o = this.sharedSiteOptions || {}
      if (o._loadFailed) return false
      if (o._ready || o._deniedUnscoped) return true
      if ((o.workshopOptions || []).length || (o.systemOptions || []).length) return true
      var deptId = resolveCurrentUserDeptId()
      // 无部门：过滤不依赖白名单（超管放行 / 拒权账号走 _deniedUnscoped）
      return deptId == null
    },
    commitFilteredTasks: function (serverTime) {
      var self = this
      if (!this.canApplySiteFilter()) return false
      var list = this._unfilteredTasks || []
      this.taskRows = list.filter(function (task) {
        return taskPassesSiteScope(task, self.sharedSiteOptions)
      })
      if (serverTime) {
        this.serverTimeBase = parseDate(serverTime)
        this.serverTimeLoadedAt = Date.now()
        this.tickClock()
      }
      return true
    },
    sanitizeSiteFilters: function () {
      var wsOpts = this.workshopOpts
      if (this.workshop !== '全部车间' && wsOpts.indexOf(this.workshop) < 0) {
        this.workshop = '全部车间'
        this.system = '全部系统'
        this.poolCode = '全部鱼池'
        return
      }
      var sysOpts = this.systemOpts
      if (this.system !== '全部系统' && sysOpts.indexOf(this.system) < 0) {
        this.system = '全部系统'
        this.poolCode = '全部鱼池'
        return
      }
      var poolOpts = this.poolOpts
      if (this.poolCode !== '全部鱼池' && poolOpts.indexOf(this.poolCode) < 0) {
        this.poolCode = '全部鱼池'
      }
    },
    pad: pad,
    dateOnly: dateOnly,
    inputDateTime: inputDateTime,
    sqlDate: sqlDate,
    parseDate: parseDate,
    minutesBetween: minutesBetween,
    durationText: durationText,
    unique: unique,
    currentUser: currentUser,
    displayStatus: displayStatus,
    normalizeTask: normalizeTask,
    toPage(path) {
      this.$router.push(path).catch(() => {});
    },
    goFishDisease() {
      this.$router.push({ path: '/fish-disease', query: { from: '/mission_planning' } }).catch(() => {});
    },
    syncViewportLayout: function () {
      var height = window.innerHeight || document.documentElement.clientHeight || 900;
      var width = window.innerWidth || document.documentElement.clientWidth || 1440;
      // 首屏展示条数：兼顾可读性与性能（展开后仍可看全部）
      var nextLimit = width < 1100
        ? (height < 850 ? 4 : 6)
        : (height < 760 ? 5 : (height < 940 ? 8 : 12));
      this.visibleTaskLimit = nextLimit;
    },
    toggleGroupExpand: function (groupKey) {
      if (!groupKey) return;
      var next = !this.expandedGroupKeys[groupKey];
      this.$set(this.expandedGroupKeys, groupKey, next);
    },
    collapseGroup: function (groupKey) {
      if (!groupKey) return;
      this.$set(this.expandedGroupKeys, groupKey, false);
    },
    refreshTaskStatuses: function () {
      var self = this;
      this.taskRows.forEach(function (task) {
        var nextStatus = self.displayStatus(task, task.pendingDelay);
        if (task.status !== nextStatus) task.status = nextStatus;
      });
    },
    bootstrapPreviewTasks: function () {
      var cached = readTaskCache();
      if (cached && cached.tasks && cached.tasks.length) {
        this.applyTasksFromRows(cached.tasks, [], [], cached.serverTime);
        this.dataSource = 'database';
        this.backendMessage = '缓存数据，正在刷新…';
        return;
      }
      // 不再用仅含日计划的本地巡检抢先铺屏，避免「先只有日计划、过一会才出周月年」的闪烁
    },
    applyTasksFromRows: function (taskRows, logs, delays, serverTime) {
      var self = this;
      if (isMissionEmptyDeptDenied()) {
        this._unfilteredTasks = [];
        this.taskRows = [];
        try { sessionStorage.removeItem(taskCacheKey()); } catch (e) { /* ignore */ }
        if (serverTime) {
          this.serverTimeBase = parseDate(serverTime);
          this.serverTimeLoadedAt = Date.now();
          this.tickClock();
        }
        return;
      }
      var openTaskKeys = {};
      var recent = this._recentAccepts || {};
      var now = Date.now();
      this.taskRows.forEach(function (task) {
        if (task._open) openTaskKeys[String(task.Id || task.task_code)] = true;
      });
      var mergedTasks = mergeLocalInspectionTasks(taskRows || []);
      var normalized = mergedTasks.map(function (row) {
        var task = normalizeTask(row, logs || [], delays || []);
        task._raw = row;
        task._open = !!openTaskKeys[String(task.Id || task.task_code)];
        var keyId = String(task.Id || '');
        var keyCode = String(task.task_code || '');
        var acceptedAt = recent[keyId] || recent[keyCode] || 0;
        if (acceptedAt && now - acceptedAt < 20000 && task.task_status === 'unreceived') {
          task.task_status = 'executing';
          task.status = 'exec';
          if (task._raw) task._raw.task_status = 'executing';
        }
        return task;
      });
      // 暂存未按站点过滤的行：白名单后到时再 commit，避免首屏滤成 0 后永不恢复
      this._unfilteredTasks = normalized;
      // 清理过期的乐观接收标记
      Object.keys(recent).forEach(function (k) {
        if (now - recent[k] > 20000) delete recent[k];
      });
      this._recentAccepts = recent;
      if (!this.commitFilteredTasks(serverTime)) {
        // 白名单未就绪：保持原 taskRows（或空），不把滤空结果写死
        if (serverTime) {
          this.serverTimeBase = parseDate(serverTime);
          this.serverTimeLoadedAt = Date.now();
          this.tickClock();
        }
      }
    },
    reload: async function (silent, skipAlarmSync) {
      if (this.loading) {
        // 130：静默刷新排队，避免被 loading 挡住导致接收后迟迟不刷新
        if (silent) {
          this._pendingSilentReload = { skipAlarmSync: !!skipAlarmSync };
        }
        return;
      }
      this.loading = true;
      if (!silent && !this.taskRows.length) {
        this.dataSource = 'loading';
        this.backendMessage = '正在连接数据库';
      } else if (!silent) {
        this.backendMessage = '正在刷新…';
      }
      var runAlarmSync = !skipAlarmSync;
      var loadedData = null;
      try {
        var self = this;
        var timeoutPromise = withTimeout(getThresholdModule('timeout'), 5000, '阈值配置').catch(function () { return null; });
        var dataPromise = withTimeout(PlatformTaskData.loadAll({
          lazySecondary: true,
          onEarlyPeriods: function (early) {
            if (!early || !early.tasks || !early.tasks.length) return;
            // 周/月/年先到：保留当前日计划行，立刻补齐其他三列
            var dayKeep = [];
            self.taskRows.forEach(function (task) {
              if ((task.period_type || 'day') === 'day') dayKeep.push(task._raw || task);
            });
            self.applyTasksFromRows(dayKeep.concat(early.tasks), self.rawLogs || [], self.rawDelays || [], early.serverTime);
            self.dataSource = 'database';
            self.backendMessage = '周/月/年已加载，日计划同步中…';
          },
          onSecondary: function (sec) {
            if (!sec) return;
            self.locations = mapLocations(sec[0] || []);
            self.departmentHeads = sec[1] || [];
            self.rawLogs = sec[2] || [];
            self.rawDelays = sec[3] || [];
            self.alarms = sec[4] || [];
            if (sec[5] && sec[5][0] && sec[5][0].server_time) {
              self.serverTimeBase = parseDate(sec[5][0].server_time);
              self.serverTimeLoadedAt = Date.now();
              self.tickClock();
            }
            var openTaskKeys2 = {};
            self.taskRows.forEach(function (task) {
              if (task._open) openTaskKeys2[String(task.Id || task.task_code)] = true;
            });
            self.taskRows = self.taskRows.map(function (row) {
              var base = row._raw || row;
              var task = normalizeTask(base, self.rawLogs || [], self.rawDelays || []);
              task._open = !!openTaskKeys2[String(task.Id || task.task_code)];
              return task;
            });
          }
        }), LOAD_ALL_TIMEOUT_MS, '任务数据加载');
        var results = await Promise.all([timeoutPromise, dataPromise]);
        global.__missionTimeoutHours = resolveTimeoutHours(results[0] || {});
        var data = results[1];
        loadedData = data;
        this.locations = mapLocations(data.locations || []);
        this.departmentHeads = data.departmentHeads || [];
        this.rawLogs = data.logs || [];
        this.rawDelays = data.delays || [];
        this.alarms = data.alarms || [];
        this.applyTasksFromRows(data.tasks || [], data.logs || [], data.delays || [], data.serverTime);
        writeTaskCache(data.tasks || [], data.serverTime);

        if (!this.canApplySiteFilter() && !(this.taskRows && this.taskRows.length)) {
          // 任务已到、白名单未到：继续显示加载，避免「数据库实时数据 + 0 条」假空屏
          this.dataSource = 'loading';
          this.backendMessage = '正在加载站点范围…';
        } else {
          this.dataSource = 'database';
          this.backendMessage = data._errors && data._errors.length ? '主任务表已连接；' + data._errors.join('；') : '数据库实时数据';
        }
      } catch (error) {
        runAlarmSync = false;
        // 超时/网络抖动时优先保留已有库数据或缓存，避免周/月/年被本地日计划冲成全 0
        try {
          var errMsg = (error && error.message) || '数据库暂不可用';
          if (this.taskRows && this.taskRows.length) {
            this.dataSource = this.dataSource === 'database' ? 'database' : 'cache';
            this.backendMessage = '沿用上次数据（' + errMsg + '）';
          } else {
            var cachedFallback = readTaskCache();
            if (cachedFallback && cachedFallback.tasks && cachedFallback.tasks.length) {
              this.applyTasksFromRows(cachedFallback.tasks, [], [], cachedFallback.serverTime);
              this.dataSource = 'cache';
              this.backendMessage = '缓存数据（' + errMsg + '）';
            } else {
              var localOnly = mergeLocalInspectionTasks([]);
              var openTaskKeysErr = {};
              this.taskRows.forEach(function (task) {
                if (task._open) openTaskKeysErr[String(task.Id || task.task_code)] = true;
              });
              this.taskRows = localOnly.map(function (row) {
                var task = normalizeTask(row, [], []);
                task._open = !!openTaskKeysErr[String(task.Id || task.task_code)];
                return task;
              });
              if (this.taskRows.length) {
                this.dataSource = 'local';
                this.backendMessage = '本地巡检任务（' + errMsg + '）';
              } else {
                this.taskRows = [];
                this.dataSource = 'error';
                this.backendMessage = errMsg;
              }
            }
          }
        } catch (ignore) {
          this.dataSource = 'error';
          this.backendMessage = error && error.message ? error.message : String(error);
        }

      } finally {
        this.loading = false;
        if (this._pendingSilentReload) {
          var pending = this._pendingSilentReload;
          this._pendingSilentReload = null;
          var selfFinally = this;
          setTimeout(function () { selfFinally.reload(true, pending.skipAlarmSync); }, 0);
          return;
        }
        clearTimeout(this._refreshTimer);
        this._refreshTimer = setTimeout(this.reload.bind(this, true, false), this.dataSource === 'database' ? this.refreshInterval : Math.max(this.refreshInterval, 30000));
      }
      // 报警同步放后台，错开首屏日计划请求，减少抢带宽
      if (runAlarmSync && loadedData) {
        var syncSelf = this;
        var syncData = loadedData;
        setTimeout(function () {
          syncSelf.syncAlarmsInBackground(syncData);
        }, 1200);
      }
    },
    syncAlarmsInBackground: async function (data) {
      if (this._alarmSyncing) return;
      this._alarmSyncing = true;
      try {
        var zkCreated = 0;
        try {
          zkCreated = await withTimeout(syncOpenSystemAlarmsToMission(), 45000, '系统报警同步');
        } catch (syncErr) {
          console.warn('syncOpenSystemAlarmsToMission', syncErr);
        }
        var created = 0;
        if (this.alarms.length) {
          try {
            created = await withTimeout(
              PlatformTaskData.syncAlarmTasks(this.alarms, data.tasks || [], this.operator),
              12000,
              '报警任务写入'
            );
          } catch (alarmErr) {
            console.warn('syncAlarmTasks', alarmErr);
          }
        }
        var totalCreated = (created || 0) + (zkCreated || 0);
        if (totalCreated) {
          this.showToast('已同步 ' + totalCreated + ' 条报警任务');
          setTimeout(this.reload.bind(this, true, true), 500);
        }
      } finally {
        this._alarmSyncing = false;
      }
    },
    slashDate: function (iso) { return iso ? String(iso).replace(/-/g, '/') : '--/--/--'; },
    statusText: function (status) { return STATUS_TEXT[status] || status; },
    statusLabel: function (status) { return STATUS_TEXT[status] || status; },
    levelLabel: function (level) { return ({ urgent: '紧急', priority: '优先', daily: '日常' })[level] || level; },
    catIcon: function (key) { return ICONS[key] || ICONS.equip; },
    categoryText: function (key) { return ({ fish: '鱼', water: '水', device: '设备', equip: '设备', person: '人' })[key] || key; },
    categoryListText: function (task) { var self = this; return (task.categories || []).map(function (key) { return self.categoryText(key); }).join('、'); },
    groupIcon: function (level) {
      if (level === 'urgent') return '<path d="M12 2L2 22h20L12 2zm0 6l6.5 12h-13L12 8zm-1 4h2v4h-2v-4zm0 6h2v2h-2v-2z"/>';
      if (level === 'priority') return '<path d="M12 2l8 10-8 10L4 12 12 2zm0 4.5L7.2 12 12 17.5 16.8 12 12 6.5z"/>';
      return '<circle cx="12" cy="12" r="7"/>';
    },
    segBarBg: function (plan) {
      var total = plan.stats.done + plan.stats.exec + plan.stats.pending + plan.stats.delayed;
      if (!total) return 'none';
      var done = plan.stats.done / total * 100;
      var exec = done + plan.stats.exec / total * 100;
      var pending = exec + plan.stats.pending / total * 100;
      return 'linear-gradient(90deg,var(--green) 0%,var(--green) ' + done + '%,var(--blue) ' + done + '%,var(--blue) ' + exec + '%,var(--orange) ' + exec + '%,var(--orange) ' + pending + '%,var(--red) ' + pending + '%,var(--red) 100%)';
    },
    toggleDd: function (id) { this.openDd = this.openDd === id ? null : id; },
    closeDd: function () { this.openDd = null; },
    pick: function (field, value) {
      this[field] = value;
      if (field === 'workshop') { this.system = '全部系统'; this.poolCode = '全部鱼池'; }
      if (field === 'system') this.poolCode = '全部鱼池';
      this.openDd = null;
    },
    toggleTask: function (task) { task._open = !task._open; },
    canReassign: function (task) {
      var self = this;
      if (this.isAdmin) return true;
      return this.departmentHeads.some(function (head) {
        return String(head.user_id) === String(self.operator.id) &&
          (!task.department_id || String(head.department_id) === String(task.department_id));
      });
    },
    onNewPlan: function (plan) {
      if (!this.departmentHeads.length) {
        this.showToast('缺少部门负责人数据，请先配置 task_department_head 表');
        return;
      }
      var start = new Date(), end = new Date(Date.now() + 60 * 60000);
      this.newPlanTarget = plan;
      this.newForm = {
        name: '', content: '', level: 'priority', categories: ['water'], departmentCode: '', departmentName: '',
        assigneeId: '', assigneeName: '', startTime: inputDateTime(start), endTime: inputDateTime(end), estimatedMinutes: 60,
        workshop: this.workshop === '全部车间' ? '' : this.workshop,
        systemName: this.system === '全部系统' ? '' : this.system,
        poolCode: this.poolCode === '全部鱼池' ? '' : this.poolCode,
        sopUrl: '', knowledgeUrl: '', instructions: '', notes: ''
      };
      this.newPlanOpen = true;
    },
    closeNewPlan: function () { this.newPlanOpen = false; this.newPlanTarget = null; },
    onDepartmentChange: function () {
      var item = this.departmentOpts.find(function (department) { return department.code === this.newForm.departmentCode; }.bind(this));
      this.newForm.departmentName = item ? item.name : '';
      this.newForm.assigneeId = '';
      this.newForm.assigneeName = '';
    },
    onAssigneeChange: function () {
      var item = this.availableHeads.find(function (head) { return String(head.user_id) === String(this.newForm.assigneeId); }.bind(this));
      this.newForm.assigneeName = item ? item.user_name : '';
    },
    confirmNewPlan: async function () {
      var form = this.newForm;
      if (!form.name.trim() || !form.content.trim()) return this.showToast('请填写任务名称和任务内容');
      if (!form.categories.length) return this.showToast('至少选择一个任务分类');
      if (!form.departmentCode || !form.assigneeId) return this.showToast('请选择部门和部门负责人');
      if (!form.startTime || !form.endTime || Number(form.estimatedMinutes) <= 0) return this.showToast('请完整填写起止时间和预估时长');
      if (new Date(form.endTime) <= new Date(form.startTime)) return this.showToast('结束时间必须晚于开始时间');
      var payload = Object.assign({}, form, {
        periodType: this.newPlanTarget.key,
        startTime: sqlDate(form.startTime),
        endTime: sqlDate(form.endTime)
      });
      try {
        this.loading = true;
        var code = await PlatformTaskData.createTask(payload, this.operator);
        this.closeNewPlan();

        this.showToast('任务已发布，正在同步数据库');
        var self = this;
        setTimeout(function () { self.loading = false; self.reload(true, true); }, 500);
      } catch (error) {
        this.loading = false;
        this.showToast(error.message || '任务创建失败');
      }
    },
    acceptTask: async function (task) {
      try {
        await PlatformTaskData.acceptTask(task, this.operator);
        // 130：接收后本地立即切到执行中，不再等整表重载才生效
        this.applyLocalTaskStatus(task, 'executing');
        // 短时内软刷新若仍读到旧 unreceived，保留执行中，避免「接收后又变回未接收」
        if (!this._recentAccepts) this._recentAccepts = {};
        this._recentAccepts[String(task.Id || '')] = Date.now();
        if (task.task_code) this._recentAccepts[String(task.task_code)] = Date.now();
        this.showToast('任务已接收');
        this.scheduleSoftReload(true);
      } catch (error) { this.showToast(error.message || '接收失败'); }
    },
    /** 本地即时更新任务状态（接收/完成等），保证 UI 立刻响应 */
    applyLocalTaskStatus: function (task, dbStatus) {
      if (!task || !dbStatus) return;
      var id = String(task.Id || task.task_code || '');
      if (!id) return;
      var now = new Date();
      var nowIso = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) +
        ' ' + pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds());
      var uiStatus = DB_STATUS[dbStatus] || dbStatus;
      this.taskRows.forEach(function (row) {
        if (String(row.Id || row.task_code) !== id && String(row.task_code || '') !== String(task.task_code || '')) return;
        row.task_status = dbStatus;
        row.status = uiStatus;
        if (row._raw) {
          row._raw.task_status = dbStatus;
          if (dbStatus === 'executing' && !row._raw.actual_start_time) {
            row._raw.actual_start_time = nowIso;
          }
        }
        if (dbStatus === 'executing' && !row.actual_start_time) {
          row.actual_start_time = nowIso;
        }
      });
      this.refreshTaskStatuses();
    },
    /** 后台静默对齐库表，不阻塞当前操作 */
    scheduleSoftReload: function (skipAlarmSync) {
      var self = this;
      clearTimeout(this._softReloadTimer);
      // 略加长，给 wiidu 写库一点落盘时间，减少读到旧状态
      this._softReloadTimer = setTimeout(function () {
        self.reload(true, !!skipAlarmSync);
      }, 600);
    },
    openAction: function (type, task) {
      this.actionType = type;
      this.actionTask = task;
      this.actionForm = {
        resultRecord: '', detectionData: '', attachmentUrls: '', delayReason: '',
        requestedEnd: inputDateTime(parseDate(task.end_time) || new Date(Date.now() + 3600000)), assigneeId: '',
        reviewRemark: '', reviewApproved: true
      };
      this.actionOpen = true;
    },
    closeAction: function () { this.actionOpen = false; this.actionType = ''; this.actionTask = null; },
    confirmAction: async function () {
      var task = this.actionTask, form = this.actionForm;
      if (!task) return;
      var actionType = this.actionType;
      try {
        if (actionType === 'complete') {
          if (!form.resultRecord.trim()) return this.showToast('请填写处置记录');
          await PlatformTaskData.completeTask(task, form, this.operator);
        } else if (actionType === 'delay') {
          if (!form.delayReason.trim() || !form.requestedEnd) return this.showToast('请填写延期原因和申请截止时间');
          form.requestedEnd = sqlDate(form.requestedEnd);
          await PlatformTaskData.requestDelay(task, form, this.operator);
        } else if (actionType === 'reassign') {
          var head = this.reassignHeads.find(function (item) { return String(item.user_id) === String(form.assigneeId); });
          if (!head) return this.showToast('请选择新的负责人');
          await PlatformTaskData.reassignTask(task, head, this.operator);
        } else if (actionType === 'reviewDelay') {
          if (!task.pendingDelay) return this.showToast('未找到待审批的延期申请');
          if (!form.reviewApproved && !form.reviewRemark.trim()) return this.showToast('请填写驳回原因');
          await PlatformTaskData.reviewDelay(task.pendingDelay, form.reviewApproved, form.reviewRemark, this.operator);
        }

        this.closeAction();
        this.showToast('任务处置已提交');
        // 完成等操作同样本地先生效
        if (actionType === 'complete' && task) {
          this.applyLocalTaskStatus(task, 'completed');
        }
        this.scheduleSoftReload(true);
      } catch (error) { this.showToast(error.message || '任务处置失败'); }
    },
    openDelayReview: function (task, approved) {
      this.actionType = 'reviewDelay';
      this.actionTask = task;
      this.actionForm = {
        resultRecord: '', detectionData: '', attachmentUrls: '', delayReason: '',
        requestedEnd: '', assigneeId: '', reviewRemark: '', reviewApproved: approved
      };
      this.actionOpen = true;
    },
    showToast: function (message) {
      var self = this;
      this.toastText = message;
      clearTimeout(this._toastTimer);
      this._toastTimer = setTimeout(function () { self.toastText = ''; }, 3000);
    },
    tickClock: function () {
      var date = this.serverTimeBase
        ? new Date(this.serverTimeBase.getTime() + (Date.now() - this.serverTimeLoadedAt))
        : new Date();
      this.nowText = dateOnly(date) + ' ' + pad(date.getHours()) + ':' + pad(date.getMinutes()) + ':' + pad(date.getSeconds());
      this.refreshTaskStatuses();
    },
    logTime: function (value) { return value ? String(value).slice(0, 16) : ''; },
    openLink: function (url) { if (url) window.open(url, '_blank', 'noopener'); }

  }
}
</script>

<template>

    <div id="app" class="task-planning-page" v-cloak @click="closeDd">
    <PageLoadingMask
      :visible="loading && dataSource === 'loading' && !taskRows.length"
      text="正在加载任务与计划…"
    />
    <header class="page-hd">
      <div class="brand">
        <h1>任务与计划中心</h1>
        <div class="sub">TASK &amp; PLANNING CENTER · 智慧工厂化循环水养殖平台</div>
        <button type="button" class="manual-entry-link" @click="$router.push('/keyshow/taskPlan')">人工录入</button>
      </div>

      <div class="filter-box">
        <span class="flabel">筛选</span>
        <div class="filter-field">
          <span class="fkey">车间</span>
          <div class="dd" @click.stop>
            <button type="button" class="dd-btn" @click="toggleDd('ws')">
              {{ workshop }} <span class="arrow">▾</span>
            </button>
            <div class="dd-menu scroll-y" v-if="openDd === 'ws'" @click.stop>
              <div
                class="dd-item"
                v-for="o in workshopOpts"
                :key="o"
                :class="{ on: workshop === o }"
                @click="pick('workshop', o)"
              >{{ o }}</div>
            </div>
          </div>
        </div>
        <div class="filter-field">
          <span class="fkey">系统</span>
          <div class="dd" @click.stop>
            <button type="button" class="dd-btn" @click="toggleDd('sys')">
              {{ system }} <span class="arrow">▾</span>
            </button>
            <div class="dd-menu scroll-y" v-if="openDd === 'sys'" @click.stop>
              <div
                class="dd-item"
                v-for="o in systemOpts"
                :key="o"
                :class="{ on: system === o }"
                @click="pick('system', o)"
              >{{ o }}</div>
            </div>
          </div>
        </div>
        <div class="filter-field">
          <span class="fkey">鱼池</span>
          <div class="dd dd-end" @click.stop>
            <button type="button" class="dd-btn" @click="toggleDd('pool')">
              {{ poolCode }} <span class="arrow">▾</span>
            </button>
            <div class="dd-menu scroll-y" v-if="openDd === 'pool'" @click.stop>
              <div class="dd-item" v-for="o in poolOpts" :key="o" :class="{ on: poolCode === o }" @click="pick('poolCode', o)">{{ o }}</div>
            </div>
          </div>
        </div>
        <div class="filter-field">
          <span class="fkey">时间</span>
          <div class="date-pair">
            <label class="date-field">
              <span class="dt">{{ slashDate(dateStart) }}</span>
              <svg class="cal" viewBox="0 0 24 24"><path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z"/></svg>
              <input type="date" v-model="dateStart" />
            </label>
            <span class="sep">—</span>
            <label class="date-field">
              <span class="dt">{{ slashDate(dateEnd) }}</span>
              <svg class="cal" viewBox="0 0 24 24"><path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z"/></svg>
              <input type="date" v-model="dateEnd" />
            </label>
          </div>
        </div>
      </div>

      <div class="hd-meta">
        <div class="backend-state" :class="dataSource" :title="backendMessage">{{ loading ? '同步中' : backendMessage }}</div>
        <div class="legend">
          <span><i class="dot done"></i>已完成</span>
          <span><i class="dot exec"></i>执行中</span>
          <span><i class="dot pending"></i>未接收</span>
          <span><i class="dot delayed"></i>已延期</span>
        </div>
        <div class="clock">{{ nowText }}</div>
      </div>
    </header>
    <div class="header">
      <div class="header-bar">
        <div class="header-left">
          <button class="nav-btn btn-blue" @click="toPage('/index_daping1_son')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="7" height="7" x="3" y="3" rx="1"></rect><rect width="7" height="7" x="14" y="3" rx="1"></rect><rect width="7" height="7" x="14" y="14" rx="1"></rect><rect width="7" height="7" x="3" y="14" rx="1"></rect></svg>
            <span>数字孪生</span>
          </button>
          <button class="nav-btn btn-purple" @click="toPage('/index_daping2')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
            <span>视频监控</span>
          </button>
          <button class="nav-btn btn-cyan" @click="toPage('/index')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline></svg>
            <span>数字化监控</span>
          </button>
        </div>

        <div class="header-center">

        </div>

        <div class="header-right">
          <div class="quick-btns">
            <button class="q-btn btn-emerald" @click="toPage('/stock_distribution')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <span>存量分布</span>
            </button>
            <button class="q-btn btn-orange" @click="toPage('/keyshow/InspectionList')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
              <span>智能巡检</span>
            </button>
            <button class="q-btn btn-cyan-light" @click="toPage('/keyshow/inspectionreport')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
              <span>分析报告</span>
            </button>
            <button class="q-btn btn-pink" @click="goFishDisease()">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
              <span>鱼病诊断</span>
            </button>
            <button class="q-btn btn-indigo" @click="toPage('/knowledge/zk-meeting-expert')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              <span>专家会议</span>
            </button>
            <!-- Excel：综合目录去掉试验系统入口 -->
          </div>
        </div>
      </div>
    </div>
    <section class="plan-grid">
      <div
        class="plan-card"
        v-for="plan in plans"
        :key="plan.key"
        :class="'tone-' + plan.tone"
      >
        <div class="plan-hd">
          <div class="plan-title">{{ plan.title }}<span class="en">{{ plan.en }}</span></div>
          <div class="plan-rate">
            <div class="pct">{{ plan.rate }}%</div>
            <div class="lbl">完成率</div>
          </div>
        </div>

        <div class="stat-row">
          <div class="stat-box done"><div class="n">{{ plan.stats.done }}</div><div class="t">已完成</div></div>
          <div class="stat-box exec"><div class="n">{{ plan.stats.exec }}</div><div class="t">执行中</div></div>
          <div class="stat-box pending"><div class="n">{{ plan.stats.pending }}</div><div class="t">未接收</div></div>
          <div class="stat-box delayed"><div class="n">{{ plan.stats.delayed }}</div><div class="t">已延期</div></div>
        </div>

        <div class="seg-bar" :style="{ backgroundImage: segBarBg(plan) }"></div>

        <div class="task-scroll scroll-y">
          <div v-for="g in plan.groups" :key="plan.key + '-' + g.level">
            <div class="group-hd" :class="g.level">
              <span class="cnt">{{ g.total }}</span>
              <svg class="gico" viewBox="0 0 24 24" v-html="groupIcon(g.level)"></svg>
              {{ g.label }}
            </div>
            <div
              class="task-item"
              v-for="(task, ti) in g.tasks"
              :key="task.Id || task.task_code || (plan.key + '-' + g.level + '-' + ti)"
              :class="['lv-' + g.level, { open: task._open }]"
            >
              <div class="task-sum" @click="toggleTask(task)">
                <i class="dot" :class="task.status"></i>
                <span class="name">{{ task.name }}</span>
                <span class="tag-fault" v-if="task.fault">故障</span>
                <span class="st delayed" v-else-if="task.status === 'delayed'">已延期</span>
                <span class="meta">
                <svg viewBox="0 0 24 24" v-html="catIcon(task.cat)"></svg>
                {{ task.timeLabel }}
              </span>
                <span class="st" :class="task.status" v-if="task.status === 'exec'">{{ statusText(task.status) }}</span>
                <span class="chev">▾</span>
              </div>
              <div class="task-detail" v-if="task._open">
                <button type="button" class="detail-close" @click.stop="toggleTask(task)">收起详情</button>
                <div class="detail-grid">
                  <div class="cell"><div class="k">开始时间</div><div class="v">{{ task.start }}</div></div>
                  <div class="cell"><div class="k">完成时间</div><div class="v">{{ task.end }}</div></div>
                  <div class="cell"><div class="k">预计时长</div><div class="v">{{ task.planDur }}</div></div>
                  <div class="cell"><div class="k">实际时长</div><div class="v" :class="task.actualOk ? 'ok' : 'warn'">{{ task.actualDur }}</div></div>
                  <div class="cell"><div class="k">车间</div><div class="v">{{ task.workshop }}</div></div>
                  <div class="cell"><div class="k">系统</div><div class="v">{{ task.sys }}</div></div>
                  <div class="cell"><div class="k">池号</div><div class="v">{{ task.pond }}</div></div>
                  <div class="cell"><div class="k">负责人</div><div class="v">{{ task.owner }}</div></div>
                  <div class="cell"><div class="k">协作人</div><div class="v"><span class="pill" v-if="task.helper">{{ task.helper }}</span><span v-else>—</span></div></div>
                  <div class="cell"><div class="k">响应时长</div><div class="v ok">{{ task.resp }}</div></div>
                  <div class="cell"><div class="k">进度</div><div class="v ok">{{ task.progress }}</div></div>
                  <div class="cell"><div class="k">质量分</div><div class="v warn">{{ task.quality }}</div></div>
                </div>
                <div class="task-context">
                  <div class="context-box"><strong>任务分类：</strong>{{ categoryListText(task) || '—' }}<br><strong>发布人：</strong>{{ task.publisher_name || '系统' }}<br><strong>所属部门：</strong>{{ task.department_name || '—' }}</div>
                  <div class="context-box"><strong>任务内容：</strong>{{ task.task_content || '—' }}<br><strong>操作细则：</strong>{{ task.instructionsText || '—' }}<br><strong>注意事项：</strong>{{ task.notesText || '—' }}</div>
                </div>
                <div class="sop-link" v-if="task.sop" @click.stop="openLink(task.sop)">📄 SOP：{{ task.sop }}</div>
                <div class="sop-link" v-if="task.knowledgeUrl" @click.stop="openLink(task.knowledgeUrl)">📚 知识库：{{ task.knowledgeUrl }}</div>
                <div class="delay-notice" v-if="task.pendingDelay">延期申请待审批：申请截止 {{ task.pendingDelay.requested_end_time }} · {{ task.pendingDelay.delay_reason }}</div>
                <div class="operation-log" v-if="task.logs && task.logs.length">
                  <strong>操作日志（不可删除）</strong>
                  <div class="log-row" v-for="log in task.logs.slice(0, 8)" :key="log.Id"><span>{{ logTime(log.operation_time) }}</span><span>{{ log.operator_name }}</span><span>{{ log.action_detail || log.action_type }}</span></div>
                </div>
                <div class="task-actions">
                  <button class="primary" v-if="task.task_status === 'unreceived'" @click.stop="acceptTask(task)">接收任务</button>
                  <button class="primary" v-if="task.status === 'exec' || task.status === 'delayed'" @click.stop="openAction('complete', task)">提交完成</button>
                  <button class="warn" v-if="task.status !== 'done' && task.status !== 'delayed' && !task.pendingDelay" @click.stop="openAction('delay', task)">延期申请</button>
                  <button v-if="task.status !== 'done' && canReassign(task)" @click.stop="openAction('reassign', task)">重新分配</button>
                  <button v-if="task.pendingDelay && isAdmin" @click.stop="openDelayReview(task, true)">同意延期</button>
                  <button v-if="task.pendingDelay && isAdmin" @click.stop="openDelayReview(task, false)">驳回延期</button>
                </div>
              </div>
            </div>
            <button
              type="button"
              class="more-count more-count-btn"
              v-if="g.hiddenCount"
              @click="toggleGroupExpand(g.groupKey)"
            >
              另有 {{ g.hiddenCount }} 项任务，已计入统计 · 点击展开全部
            </button>
            <button
              type="button"
              class="more-count more-count-btn is-collapse"
              v-else-if="g.expanded && g.total > visibleTaskLimit"
              @click="collapseGroup(g.groupKey)"
            >
              收起列表（保留前 {{ visibleTaskLimit }} 项）
            </button>
          </div>
        </div>

        <button
          type="button"
          class="btn-new"
          :class="{ 'is-press': pressKey === plan.key }"
          @mousedown="pressKey = plan.key"
          @mouseup="pressKey = null"
          @mouseleave="pressKey = null"
          @click.stop="onNewPlan(plan)"
        >+ 新建{{ plan.title }}</button>
      </div>
    </section>



    <div class="modal-mask" v-if="newPlanOpen" @click.self="closeNewPlan">
      <div class="modal" :class="'tone-' + (newPlanTarget && newPlanTarget.tone)" @click.stop>
        <div class="modal-hd">
          <h3>新建{{ newPlanTarget ? newPlanTarget.title : '计划' }}</h3>
          <button type="button" class="x" @click="closeNewPlan">×</button>
        </div>
        <div class="modal-form">
          <div class="form-2">
            <div class="form-row"><label>任务名称 *</label><input v-model.trim="newForm.name" type="text" placeholder="请输入任务名称" maxlength="80" /></div>
            <div class="form-row"><label>任务优先级 *</label><select v-model="newForm.level"><option value="urgent">紧急任务</option><option value="priority">优先任务</option><option value="daily">日常任务</option></select></div>
          </div>
          <div class="form-row"><label>任务内容 *</label><textarea v-model.trim="newForm.content" placeholder="请输入具体作业内容"></textarea></div>
          <div class="form-row">
            <label>任务分类（可多选）*</label>
            <div class="check-list">
              <label class="check-item"><input type="checkbox" value="fish" v-model="newForm.categories" />鱼</label>
              <label class="check-item"><input type="checkbox" value="water" v-model="newForm.categories" />水</label>
              <label class="check-item"><input type="checkbox" value="device" v-model="newForm.categories" />设备</label>
              <label class="check-item"><input type="checkbox" value="person" v-model="newForm.categories" />人</label>
            </div>
          </div>
          <div class="form-2">
            <div class="form-row"><label>部门 *</label><select v-model="newForm.departmentCode" @change="onDepartmentChange"><option value="">请选择部门</option><option v-for="d in departmentOpts" :key="d.code" :value="d.code">{{ d.name }}</option></select></div>
            <div class="form-row"><label>部门负责人 *</label><select v-model="newForm.assigneeId" @change="onAssigneeChange" :disabled="!newForm.departmentCode"><option value="">请选择负责人</option><option v-for="h in availableHeads" :key="h.user_id" :value="h.user_id">{{ h.user_name }} · 在岗 · {{ h.active_task_count || 0 }}项任务</option></select></div>
          </div>
          <div class="form-3">
            <div class="form-row"><label>车间</label><select v-model="newForm.workshop"><option value="">全场</option><option v-for="o in workshopOpts.slice(1)" :key="o" :value="o">{{ o }}</option></select></div>
            <div class="form-row"><label>系统</label><select v-model="newForm.systemName"><option value="">全部系统</option><option v-for="o in systemOpts.slice(1)" :key="o" :value="o">{{ o }}</option></select></div>
            <div class="form-row"><label>鱼池</label><select v-model="newForm.poolCode"><option value="">全部鱼池</option><option v-for="o in poolOpts.slice(1)" :key="o" :value="o">{{ o }}</option></select></div>
          </div>
          <div class="form-3">
            <div class="form-row"><label>开始时间 *</label><input v-model="newForm.startTime" type="datetime-local" /></div>
            <div class="form-row"><label>结束时间 *</label><input v-model="newForm.endTime" type="datetime-local" /></div>
            <div class="form-row"><label>预估时长（分钟）*</label><input v-model.number="newForm.estimatedMinutes" type="number" min="1" max="525600" /></div>
          </div>
          <div class="form-2">
            <div class="form-row"><label>SOP 链接</label><input v-model.trim="newForm.sopUrl" type="url" placeholder="https://..." /></div>
            <div class="form-row"><label>知识库链接</label><input v-model.trim="newForm.knowledgeUrl" type="url" placeholder="https://..." /></div>
          </div>
          <div class="form-2">
            <div class="form-row"><label>操作细则</label><textarea v-model.trim="newForm.instructions" placeholder="操作步骤、验收标准"></textarea></div>
            <div class="form-row"><label>注意事项</label><textarea v-model.trim="newForm.notes" placeholder="安全事项、风险提示"></textarea></div>
          </div>
          <div class="form-row"><label>发布人</label><div class="publisher-readonly">{{ operator.name }}（自动读取当前登录账号）</div></div>
        </div>
        <div class="modal-ft">
          <button type="button" class="btn-ghost" @click="closeNewPlan">取消</button>
          <button type="button" class="btn-primary" @click="confirmNewPlan">确认创建</button>
        </div>
      </div>
    </div>

    <div class="modal-mask" v-if="actionOpen" @click.self="closeAction">
      <div class="modal compact" @click.stop>
        <div class="modal-hd"><h3>{{ actionTitle }} · {{ actionTask && actionTask.name }}</h3><button type="button" class="x" @click="closeAction">×</button></div>
        <div class="modal-form" v-if="actionType === 'complete'">
          <div class="form-row"><label>处置记录 <span class="req-star">*</span></label><textarea v-model.trim="actionForm.resultRecord" placeholder="填写实际处置过程与结果"></textarea></div>
          <div class="form-row"><label>检测数据</label><textarea v-model.trim="actionForm.detectionData" placeholder="填写检测项目、数值、单位"></textarea></div>
          <div class="form-row"><label>附件地址</label><input v-model.trim="actionForm.attachmentUrls" type="text" placeholder="多个地址用逗号分隔" /></div>
        </div>
        <div class="modal-form" v-else-if="actionType === 'delay'">
          <div class="form-row"><label>申请延期至 *</label><input v-model="actionForm.requestedEnd" type="datetime-local" /></div>
          <div class="form-row"><label>延期原因 *</label><textarea v-model.trim="actionForm.delayReason" placeholder="说明无法按时完成的原因"></textarea></div>
        </div>
        <div class="modal-form" v-else-if="actionType === 'reassign'">
          <div class="form-row"><label>新的部门负责人 *</label><select v-model="actionForm.assigneeId"><option value="">请选择负责人</option><option v-for="h in reassignHeads" :key="h.user_id" :value="h.user_id">{{ h.user_name }} · {{ h.active_task_count || 0 }}项任务</option></select></div>
        </div>
        <div class="modal-form" v-else-if="actionType === 'reviewDelay'">
          <div class="review-decision" :class="actionForm.reviewApproved ? 'approved' : 'rejected'">
            {{ actionForm.reviewApproved ? '同意延期' : '驳回延期' }}
          </div>
          <div class="form-row">
            <label>{{ actionForm.reviewApproved ? '审批意见' : '驳回原因' }}</label>
            <textarea v-model.trim="actionForm.reviewRemark" :placeholder="actionForm.reviewApproved ? '可填写审批说明' : '请填写驳回原因'"></textarea>
          </div>
        </div>
        <div class="modal-ft"><button type="button" class="btn-ghost" @click="closeAction">取消</button><button type="button" class="btn-primary" @click="confirmAction">确认提交</button></div>
      </div>
    </div>

    <div class="toast" v-if="toastText">{{ toastText }}</div>
    <MyChat />
  </div>
</template>

<style scoped lang="scss">
.header {
  border-bottom: 1px solid rgba(59, 130, 246, 0.3);
  padding: 16px 0;
  flex-shrink: 0;

  .header-bar {
    display: grid;
    grid-template-columns: 1fr auto 1fr;
    align-items: center;
    gap: 1rem;
    min-width: 0;
  }

  .header-left {
    justify-self: start;
    display: flex;
    align-items: center;
    gap: 0.6rem;
    min-width: 0;

    .nav-btn {
      display: flex;
      align-items: center;
      gap: 0.45rem;
      padding: 0 0.9rem;
      height: 2.5rem;
      border-radius: 0.5rem;
      color: white;
      font-weight: 600;
      font-size: 0.94rem;
      cursor: pointer;
      border: none;
      transition: all 0.3s;
      white-space: nowrap;
      flex-shrink: 0;

      .icon { width: 0.9rem; height: 0.9rem; }

      &.btn-blue {
        background: linear-gradient(to right, #2563eb, #3b82f6);
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
        &:hover { filter: brightness(1.08); transform: translateY(-1px); }
      }
      &.btn-purple {
        background: linear-gradient(to right, #9333ea, #a855f7);
        box-shadow: 0 4px 12px rgba(168, 85, 247, 0.35);
        &:hover { filter: brightness(1.08); transform: translateY(-1px); }
      }
      &.btn-cyan {
        background: linear-gradient(to right, #0891b2, #06b6d4);
        box-shadow: 0 4px 12px rgba(6, 182, 212, 0.35);
        &:hover { filter: brightness(1.08); transform: translateY(-1px); }
      }
    }
  }

  .header-center {
    justify-self: center;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    min-width: 0;

    .logo {
      width: 2.75rem;
      height: 2.75rem;
      background: linear-gradient(to bottom right, #3b82f6, #22d3ee);
      border-radius: 0.5rem;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 15px rgba(59, 130, 246, 0.45);
      flex-shrink: 0;

      .icon-lg { width: 1.6rem; height: 1.6rem; color: white; }
    }

    .title-wrap {
      h1 {
        font-size: 1.75rem;
        font-weight: 800;
        letter-spacing: 0.04em;
        line-height: 1.2;
        white-space: nowrap;
      }
    }
  }

  .header-right {
    justify-self: end;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    min-width: 0;

    .quick-btns {
      display: flex;
      flex-wrap: wrap;
      justify-content: flex-end;
      gap: 0.6rem;

      .q-btn {
        height: 2.5rem;
        padding: 0 0.9rem;
        border-radius: 0.5rem;
        display: flex;
        align-items: center;
        gap: 0.45rem;
        color: white;
        font-size: 0.94rem;
        font-weight: 600;
        border: none;
        cursor: pointer;
        transition: all 0.3s;
        white-space: nowrap;
        flex-shrink: 0;

        .icon { width: 0.9rem; height: 0.9rem; }

        &.btn-emerald { background: linear-gradient(to right, #059669, #10b981); box-shadow: 0 4px 10px rgba(16, 185, 129, 0.25); }
        &.btn-orange { background: linear-gradient(to right, #ea580c, #f97316); box-shadow: 0 4px 10px rgba(249, 115, 22, 0.25); }
        &.btn-cyan-light { background: linear-gradient(to right, #0891b2, #06b6d4); box-shadow: 0 4px 10px rgba(6, 182, 212, 0.25); }
        &.btn-pink { background: linear-gradient(to right, #db2777, #ec4899); box-shadow: 0 4px 10px rgba(236, 72, 153, 0.25); }
        &.btn-indigo { background: linear-gradient(to right, #4f46e5, #6366f1); box-shadow: 0 4px 10px rgba(99, 102, 241, 0.25); }
        &.btn-teal { background: linear-gradient(to right, #0d9488, #14b8a6); box-shadow: 0 4px 10px rgba(20, 184, 166, 0.25); }

        &:hover {
          filter: brightness(1.08);
          transform: translateY(-1px);
        }
      }
    }
  }
}

//html,
//body {
//  width: 100%;
//  min-width: 320px;
//  height: 100%;
//  margin: 0;
//  overflow: hidden;
//  background: #060b14;
//}

[v-cloak] {
  display: none !important;
}

*, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

.task-planning-page {
  --bg: #060b14;
  --panel: #0a1422;
  --panel-2: #0d1828;
  --border: rgba(70, 140, 200, 0.16);
  --text: #e8eef8;
  --text-2: #8b9bb0;
  --text-3: #5a6a80;
  --cyan: #00e5ff;
  --green: #00ffc6;
  --blue: #3aa0ff;
  --purple: #b14dff;
  --orange: #ff9a3c;
  --yellow: #faad14;
  --red: #ff4d6a;
  --gap: 12px;
  --r: 10px;
}

.task-planning-page {
  width: 100%;
  height: 100%;
  position: relative;
  padding: 16px !important;
  background: var(--bg);
  color: var(--text);
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", sans-serif;
  -webkit-font-smoothing: antialiased;
  overflow: hidden;
}
.task-planning-page {
  height: 100vh;
  height: 100dvh;
  padding: 10px 12px;
  background:
    radial-gradient(ellipse at 20% -10%, rgba(0, 80, 120, 0.22) 0%, transparent 45%),
    radial-gradient(ellipse at 80% 0%, rgba(80, 40, 140, 0.12) 0%, transparent 40%),
    var(--bg);
}

.task-planning-page button, .task-planning-page input, .task-planning-page select {
  font-family: inherit;
  color: inherit;
  -webkit-appearance: none;
  appearance: none;
  border: none;
  background: transparent;
  cursor: pointer;
}

.task-planning-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  height: 100%;
  max-width: none;
  margin: 0;
  overflow: hidden;
  min-height: 0;
}

.scroll-y {
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
  scrollbar-color: #2a4058 transparent;
}
.scroll-y::-webkit-scrollbar { width: 4px; }
.scroll-y::-webkit-scrollbar-thumb { background: #2a4058; border-radius: 2px; }

/* ===== Header ===== */
.page-hd {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: nowrap;
  flex-shrink: 0;
  min-height: 48px;
}

.brand {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding-left: 12px;
  border-left: 3px solid #3aa0ff;
  box-shadow: -3px 0 14px rgba(58, 160, 255, 0.45);
  flex-shrink: 0;
}

.brand h1 {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.5px;
  color: #fff;
  line-height: 1.15;
}

.brand .sub {
  font-size: 11px;
  color: #7a8ba0;
  letter-spacing: 0.3px;
  white-space: nowrap;
}

.manual-entry-link {
  margin-top: 6px;
  align-self: flex-start;
  border: 1px solid rgba(58, 160, 255, 0.45);
  background: rgba(58, 160, 255, 0.12);
  color: #9fd0ff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 6px;
  cursor: pointer;
}

.manual-entry-link:hover {
  background: rgba(58, 160, 255, 0.22);
  color: #fff;
}

.filter-box {
  display: flex;
  align-items: center;
  gap: 0;
  padding: 6px 12px 6px 14px;
  border: 1px solid rgba(70, 130, 190, 0.35);
  border-radius: 8px;
  background: rgba(8, 18, 34, 0.92);
  flex-shrink: 0;
}

.filter-box .flabel {
  font-size: 12px;
  color: #7a8ba0;
  white-space: nowrap;
  padding-right: 10px;
}

.filter-field {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  position: relative;
}

.filter-field + .filter-field {
  border-left: 1px solid rgba(90, 130, 180, 0.28);
}

.filter-field .fkey {
  font-size: 12px;
  color: #8b9bb0;
  white-space: nowrap;
}

.dd {
  position: relative;
}

.dd-end .dd-menu {
  left: auto;
  right: 0;
}

.dd-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px;
  border: 1px solid rgba(94, 200, 255, 0.4);
  border-radius: 4px;
  font-size: 13px;
  color: #5ec8ff;
  background: rgba(20, 40, 70, 0.35);
  white-space: nowrap;
}

.dd-btn:hover {
  color: #8adbff;
  border-color: rgba(94, 200, 255, 0.65);
}

.dd-btn .arrow {
  font-size: 10px;
  opacity: 0.85;
  color: #5ec8ff;
}

.dd-menu {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  min-width: 120px;
  max-width: min(360px, 70vw);
  max-height: min(280px, 45vh);
  overflow-x: hidden;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  overscroll-behavior: contain;
  z-index: 80;
  padding: 4px 0;
  border: 1px solid rgba(94, 200, 255, 0.35);
  border-radius: 8px;
  background: rgba(10, 20, 36, 0.98);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);
}

.dd-item {
  padding: 7px 14px;
  font-size: 12px;
  color: var(--cyan);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dd-item:hover,
.dd-item.on {
  background: rgba(160, 207, 255, 0.85);
  color: #0a1220;
}

.date-pair {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.date-pair .sep {
  color: #8b9bb0;
  font-size: 12px;
  line-height: 1;
}

.date-field {
  position: relative;
  display: inline-flex;
  align-items: center;
  min-width: 112px;
  height: 26px;
  padding: 0 26px 0 8px;
  border: 1px solid rgba(250, 173, 20, 0.65);
  border-radius: 4px;
  background: rgba(20, 16, 8, 0.65);
  cursor: pointer;
}

.date-field:hover { border-color: rgba(250, 173, 20, 0.9); }

.date-field .dt {
  font-size: 12px;
  color: #faad14;
  letter-spacing: 0.2px;
  pointer-events: none;
  font-variant-numeric: tabular-nums;
}

.date-field .cal {
  position: absolute;
  right: 7px;
  top: 50%;
  transform: translateY(-50%);
  width: 12px;
  height: 12px;
  fill: #e8e8e8;
  pointer-events: none;
}

.date-field input[type="date"] {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
  width: 100%;
  height: 100%;
}

.hd-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.legend {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 12px;
  color: #a8b4c4;
}

.legend span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  white-space: nowrap;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  box-shadow: 0 0 6px currentColor;
}

.dot.done { background: #00e8a0; color: #00e8a0; }
.dot.exec { background: #3aa0ff; color: #3aa0ff; }
.dot.pending { background: #ff9a3c; color: #ff9a3c; }
.dot.overdue { background: #ff4d6a; color: #ff4d6a; }
.dot.urgent { background: var(--red); color: var(--red); }
.dot.priority { background: var(--orange); color: var(--orange); }
.dot.daily { background: var(--green); color: var(--green); }

.clock {
  font-size: 12px;
  color: #8b9bb0;
  white-space: nowrap;
  font-variant-numeric: tabular-nums;
}

/* ===== Plan Grid ===== */
.plan-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--gap);
  flex: 1 1 0;
  min-height: 0;
  overflow: hidden;
}

.plan-card {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  max-height: none;
  padding: 10px 10px 10px;
  background: linear-gradient(180deg, rgba(12, 22, 38, 0.98) 0%, rgba(8, 14, 26, 0.98) 100%);
  border: 1px solid rgba(80, 140, 200, 0.18);
  border-radius: var(--r);
  overflow: hidden;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.02), 0 0 24px rgba(0, 0, 0, 0.35);
}

.plan-card.tone-green {
  --accent: var(--green);
  border-color: rgba(0, 255, 198, 0.28);
  box-shadow: 0 0 20px rgba(0, 255, 198, 0.08), inset 0 0 0 1px rgba(0, 255, 198, 0.06);
}
.plan-card.tone-blue {
  --accent: var(--blue);
  border-color: rgba(58, 160, 255, 0.3);
  box-shadow: 0 0 20px rgba(58, 160, 255, 0.08), inset 0 0 0 1px rgba(58, 160, 255, 0.06);
}
.plan-card.tone-purple {
  --accent: var(--purple);
  border-color: rgba(177, 77, 255, 0.3);
  box-shadow: 0 0 20px rgba(177, 77, 255, 0.08), inset 0 0 0 1px rgba(177, 77, 255, 0.06);
}
.plan-card.tone-orange {
  --accent: var(--orange);
  border-color: rgba(255, 154, 60, 0.3);
  box-shadow: 0 0 20px rgba(255, 154, 60, 0.08), inset 0 0 0 1px rgba(255, 154, 60, 0.06);
}

.plan-card::before,
.plan-card::after {
  content: "";
  position: absolute;
  width: 18px;
  height: 18px;
  pointer-events: none;
}

.plan-card::before {
  top: -1px;
  left: -1px;
  border-top: 2px solid var(--accent);
  border-left: 2px solid var(--accent);
  border-top-left-radius: 10px;
  box-shadow: -2px -2px 10px rgba(0, 232, 160, 0.35);
}

.plan-card::after {
  bottom: -1px;
  right: -1px;
  border-bottom: 2px solid var(--accent);
  border-right: 2px solid var(--accent);
  border-bottom-right-radius: 10px;
  box-shadow: 2px 2px 10px rgba(0, 232, 160, 0.25);
}

.plan-card.tone-green { --accent: var(--green); }
.plan-card.tone-green::before,
.plan-card.tone-green::after { box-shadow: 0 0 10px rgba(0, 232, 160, 0.4); }
.plan-card.tone-blue { --accent: var(--blue); }
.plan-card.tone-blue::before,
.plan-card.tone-blue::after { box-shadow: 0 0 10px rgba(59, 158, 255, 0.4); }
.plan-card.tone-purple { --accent: var(--purple); }
.plan-card.tone-purple::before,
.plan-card.tone-purple::after { box-shadow: 0 0 10px rgba(168, 85, 247, 0.4); }
.plan-card.tone-orange { --accent: var(--orange); }
.plan-card.tone-orange::before,
.plan-card.tone-orange::after { box-shadow: 0 0 10px rgba(255, 159, 67, 0.4); }

.plan-hd {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.plan-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--accent);
  text-shadow: 0 0 14px rgba(0, 229, 255, 0.25);
  line-height: 1.15;
}

.plan-card.tone-green .plan-title { text-shadow: 0 0 16px rgba(0, 255, 198, 0.65); }
.plan-card.tone-blue .plan-title { text-shadow: 0 0 16px rgba(58, 160, 255, 0.65); }
.plan-card.tone-purple .plan-title { text-shadow: 0 0 16px rgba(177, 77, 255, 0.65); }
.plan-card.tone-orange .plan-title { text-shadow: 0 0 16px rgba(255, 154, 60, 0.65); }

.plan-title .en {
  margin-left: 8px;
  font-size: 11px;
  font-weight: 500;
  color: var(--text-3);
  letter-spacing: 1px;
  text-shadow: none;
}

.plan-rate {
  text-align: right;
}

.plan-rate .pct {
  font-size: 20px;
  font-weight: 700;
  color: var(--accent);
  line-height: 1;
}

.plan-card.tone-green .plan-rate .pct { text-shadow: 0 0 14px rgba(0, 255, 198, 0.55); }
.plan-card.tone-blue .plan-rate .pct { text-shadow: 0 0 14px rgba(58, 160, 255, 0.55); }
.plan-card.tone-purple .plan-rate .pct { text-shadow: 0 0 14px rgba(177, 77, 255, 0.55); }
.plan-card.tone-orange .plan-rate .pct { text-shadow: 0 0 14px rgba(255, 154, 60, 0.55); }

.plan-rate .lbl {
  margin-top: 2px;
  font-size: 11px;
  color: var(--text-3);
}

.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 6px;
  margin-bottom: 8px;
  flex-shrink: 0;
}

.stat-box .n {
  font-size: 16px;
  font-weight: 700;
  line-height: 1.1;
}

.stat-box {
  text-align: center;
  padding: 6px 2px 4px;
  border-radius: 6px;
  border: 1px solid;
  background: rgba(0, 0, 0, 0.25);
}

.stat-box .t {
  margin-top: 2px;
  font-size: 10px;
  opacity: 0.9;
}

.stat-box.done { border-color: rgba(0, 232, 160, 0.55); color: var(--green); background: rgba(0, 232, 160, 0.06); }
.stat-box.exec { border-color: rgba(59, 158, 255, 0.55); color: var(--blue); background: rgba(59, 158, 255, 0.06); }
.stat-box.pending { border-color: rgba(255, 159, 67, 0.55); color: var(--orange); background: rgba(255, 159, 67, 0.06); }
.stat-box.overdue { border-color: rgba(255, 77, 106, 0.55); color: var(--red); background: rgba(255, 77, 106, 0.06); }

.seg-bar {
  height: 4px;
  border-radius: 2px;
  margin-bottom: 12px;
  background-color: rgba(255, 255, 255, 0.06);
  background-repeat: no-repeat;
  background-size: 100% 100%;
  flex-shrink: 0;
  overflow: hidden;
}

.task-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 2px;
}

.group-hd {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 10px 0 8px;
  font-size: 13px;
  font-weight: 600;
}

.group-hd .cnt {
  min-width: 20px;
  height: 18px;
  padding: 0 5px;
  border-radius: 3px;
  font-size: 11px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid;
  font-weight: 700;
}

.group-hd .gico {
  width: 12px;
  height: 12px;
  flex-shrink: 0;
}

.group-hd.urgent { color: var(--red); }
.group-hd.urgent .cnt { border-color: var(--red); color: var(--red); background: rgba(255, 77, 106, 0.15); }
.group-hd.urgent .gico { fill: var(--red); }
.group-hd.priority { color: var(--orange); }
.group-hd.priority .cnt { border-color: var(--orange); color: var(--orange); background: rgba(255, 154, 60, 0.12); }
.group-hd.priority .gico { fill: var(--orange); }
.group-hd.daily { color: var(--green); }
.group-hd.daily .cnt { border-color: var(--green); color: var(--green); background: rgba(0, 255, 198, 0.1); }
.group-hd.daily .gico { fill: var(--green); }

.more-count {
  margin: 6px 2px 4px;
  padding: 6px 10px;
  font-size: 12px;
  color: var(--text-2, #8b9bb0);
  text-align: center;
  border-radius: 6px;
  border: 1px dashed rgba(139, 155, 176, 0.35);
  background: rgba(8, 18, 34, 0.45);
}
.more-count-btn {
  display: block;
  width: 100%;
  cursor: pointer;
  color: #7dd3fc;
  border-color: rgba(56, 189, 248, 0.35);
  background: rgba(14, 116, 144, 0.12);
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}
.more-count-btn:hover {
  color: #e0f2fe;
  border-color: rgba(56, 189, 248, 0.55);
  background: rgba(14, 116, 144, 0.22);
}
.more-count-btn.is-collapse {
  color: #94a3b8;
  border-style: solid;
  border-color: rgba(148, 163, 184, 0.3);
  background: rgba(15, 23, 42, 0.35);
}

.task-item {
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 8px;
  background: rgba(8, 16, 28, 0.7);
  margin-bottom: 8px;
  overflow: hidden;
}

.task-item.flash {
  animation: taskFlash 1.1s ease;
}

@keyframes taskFlash {
  0%, 100% { box-shadow: none; }
  30%, 60% { box-shadow: 0 0 0 1px rgba(94, 200, 255, 0.7), 0 0 18px rgba(58, 160, 255, 0.35); }
}

.task-item.lv-urgent { border-color: rgba(255, 77, 106, 0.28); }
.task-item.lv-priority { border-color: rgba(255, 159, 67, 0.22); }
.task-item.lv-daily { border-color: rgba(0, 232, 160, 0.18); }

.task-sum {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 10px;
  cursor: pointer;
  user-select: none;
}

.task-sum:hover { background: rgba(255, 255, 255, 0.03); }

.task-sum .name {
  flex: 1;
  min-width: 0;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-sum .tag-fault {
  flex-shrink: 0;
  padding: 1px 5px;
  font-size: 10px;
  border-radius: 2px;
  background: rgba(255, 77, 106, 0.2);
  border: 1px solid rgba(255, 77, 106, 0.6);
  color: var(--red);
}

.task-sum .meta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  font-size: 11px;
  color: var(--text-3);
}

.task-sum .meta svg {
  width: 12px;
  height: 12px;
  fill: currentColor;
}

.task-sum .st {
  flex-shrink: 0;
  padding: 2px 7px;
  border-radius: 3px;
  font-size: 11px;
  border: 1px solid;
}

.task-sum .st.exec {
  color: #0a1220;
  background: var(--blue);
  border-color: var(--blue);
  font-weight: 600;
}

.task-sum .st.pending {
  color: var(--orange);
  border-color: rgba(255, 159, 67, 0.55);
  background: rgba(255, 159, 67, 0.1);
}

.task-sum .st.done {
  color: var(--green);
  border-color: rgba(0, 232, 160, 0.5);
  background: rgba(0, 232, 160, 0.1);
}

.task-sum .st.overdue {
  color: var(--red);
  border-color: rgba(255, 77, 106, 0.55);
  background: rgba(255, 77, 106, 0.12);
}

.task-sum .chev {
  flex-shrink: 0;
  color: var(--text-3);
  font-size: 10px;
  transition: transform 0.15s;
}

.task-item.open .task-sum .chev { transform: rotate(180deg); }

.task-detail {
  padding: 0 10px 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px 10px;
  padding: 10px 0 8px;
  font-size: 11px;
}

.detail-grid .cell .k {
  color: var(--text-3);
  margin-bottom: 2px;
}

.detail-grid .cell .v {
  color: var(--text);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-grid .cell .v.ok { color: var(--green); }
.detail-grid .cell .v.warn { color: var(--orange); }

.pill {
  display: inline-block;
  padding: 1px 7px;
  border-radius: 10px;
  font-size: 11px;
  background: rgba(168, 85, 247, 0.2);
  color: #d8b4fe;
  border: 1px solid rgba(168, 85, 247, 0.4);
}

.sop-link {
  margin-top: 8px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--blue);
  cursor: pointer;
}

.btn-new {
  position: relative;
  z-index: 5;
  flex-shrink: 0;
  margin-top: 8px;
  height: 34px;
  width: 100%;
  border-radius: 6px;
  border: 1px solid var(--accent);
  color: var(--accent);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
  background: rgba(0, 0, 0, 0.28);
  cursor: pointer;
  transition: background 0.15s, box-shadow 0.15s, transform 0.12s, filter 0.15s;
  user-select: none;
}

.plan-card.tone-green .btn-new { box-shadow: 0 0 12px rgba(0, 232, 160, 0.28); }
.plan-card.tone-blue .btn-new { box-shadow: 0 0 12px rgba(59, 158, 255, 0.28); }
.plan-card.tone-purple .btn-new { box-shadow: 0 0 12px rgba(168, 85, 247, 0.28); }
.plan-card.tone-orange .btn-new { box-shadow: 0 0 12px rgba(255, 159, 67, 0.28); }

.btn-new:hover {
  filter: brightness(1.12);
}

.plan-card.tone-green .btn-new:hover {
  background: rgba(0, 232, 160, 0.12);
  box-shadow: 0 0 22px rgba(0, 232, 160, 0.45);
}
.plan-card.tone-blue .btn-new:hover {
  background: rgba(59, 158, 255, 0.12);
  box-shadow: 0 0 22px rgba(59, 158, 255, 0.45);
}
.plan-card.tone-purple .btn-new:hover {
  background: rgba(168, 85, 247, 0.12);
  box-shadow: 0 0 22px rgba(168, 85, 247, 0.45);
}
.plan-card.tone-orange .btn-new:hover {
  background: rgba(255, 159, 67, 0.12);
  box-shadow: 0 0 22px rgba(255, 159, 67, 0.45);
}

.btn-new:active,
.btn-new.is-press {
  transform: scale(0.96);
  filter: brightness(1.2);
}

.plan-card.tone-green .btn-new:active,
.plan-card.tone-green .btn-new.is-press {
  background: rgba(0, 232, 160, 0.22);
  box-shadow: inset 0 0 12px rgba(0, 232, 160, 0.35), 0 0 16px rgba(0, 232, 160, 0.4);
}
.plan-card.tone-blue .btn-new:active,
.plan-card.tone-blue .btn-new.is-press {
  background: rgba(59, 158, 255, 0.22);
  box-shadow: inset 0 0 12px rgba(59, 158, 255, 0.35), 0 0 16px rgba(59, 158, 255, 0.4);
}
.plan-card.tone-purple .btn-new:active,
.plan-card.tone-purple .btn-new.is-press {
  background: rgba(168, 85, 247, 0.22);
  box-shadow: inset 0 0 12px rgba(168, 85, 247, 0.35), 0 0 16px rgba(168, 85, 247, 0.4);
}
.plan-card.tone-orange .btn-new:active,
.plan-card.tone-orange .btn-new.is-press {
  background: rgba(255, 159, 67, 0.22);
  box-shadow: inset 0 0 12px rgba(255, 159, 67, 0.35), 0 0 16px rgba(255, 159, 67, 0.4);
}

/* ===== Modal / Toast ===== */
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(2, 8, 18, 0.72);
  backdrop-filter: blur(4px);
  animation: fadeIn 0.18s ease;
}

.modal {
  width: min(420px, calc(100vw - 32px));
  padding: 20px 22px 18px;
  border-radius: 12px;
  border: 1px solid rgba(94, 200, 255, 0.28);
  background: linear-gradient(180deg, #0e1a2c 0%, #0a1422 100%);
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.03), 0 20px 50px rgba(0, 0, 0, 0.55),
  0 0 40px rgba(58, 160, 255, 0.12);
  animation: popIn 0.2s ease;
}

.modal.tone-green { border-color: rgba(0, 232, 160, 0.4); box-shadow: 0 20px 50px rgba(0,0,0,0.55), 0 0 36px rgba(0,232,160,0.12); }
.modal.tone-blue { border-color: rgba(59, 158, 255, 0.4); box-shadow: 0 20px 50px rgba(0,0,0,0.55), 0 0 36px rgba(59,158,255,0.12); }
.modal.tone-purple { border-color: rgba(168, 85, 247, 0.4); box-shadow: 0 20px 50px rgba(0,0,0,0.55), 0 0 36px rgba(168,85,247,0.12); }
.modal.tone-orange { border-color: rgba(255, 159, 67, 0.4); box-shadow: 0 20px 50px rgba(0,0,0,0.55), 0 0 36px rgba(255,159,67,0.12); }

.modal-hd {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.modal-hd h3 {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}

.modal-hd .x {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  color: #8b9bb0;
  font-size: 18px;
  line-height: 1;
}

.modal-hd .x:hover { color: #fff; background: rgba(255,255,255,0.06); }

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-row label {
  font-size: 12px;
  color: #8b9bb0;
}

.form-row label .req-star {
  color: #ff4d6a;
  font-weight: 700;
}

.form-row input,
.form-row select {
  height: 34px;
  padding: 0 10px;
  border-radius: 6px;
  border: 1px solid rgba(94, 160, 220, 0.28);
  background: rgba(4, 12, 22, 0.9);
  color: #e8eef8;
  font-size: 13px;
  outline: none;
}

.form-row input:focus,
.form-row select:focus {
  border-color: rgba(94, 200, 255, 0.65);
  box-shadow: 0 0 0 2px rgba(58, 160, 255, 0.15);
}

.form-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.modal-ft {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 18px;
}

.btn-ghost,
.btn-primary {
  height: 34px;
  padding: 0 16px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.15s, transform 0.1s, box-shadow 0.15s;
}

.btn-ghost {
  border: 1px solid rgba(140, 160, 190, 0.3);
  color: #a8b4c4;
  background: transparent;
}

.btn-ghost:hover { background: rgba(255,255,255,0.04); color: #fff; }
.btn-ghost:active { transform: scale(0.97); }

.btn-primary {
  border: 1px solid var(--m-accent, var(--cyan));
  color: #061018;
  background: var(--m-accent, var(--cyan));
  box-shadow: 0 0 16px color-mix(in srgb, var(--m-accent, var(--cyan)) 35%, transparent);
}

.btn-primary:hover { filter: brightness(1.08); }
.btn-primary:active { transform: scale(0.97); }

.modal.tone-green { --m-accent: var(--green); }
.modal.tone-blue { --m-accent: var(--blue); }
.modal.tone-purple { --m-accent: var(--purple); }
.modal.tone-orange { --m-accent: var(--orange); }

.toast {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1100;
  padding: 10px 18px;
  border-radius: 8px;
  border: 1px solid rgba(0, 232, 160, 0.45);
  background: rgba(8, 24, 20, 0.96);
  color: #00e8a0;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.45), 0 0 20px rgba(0, 232, 160, 0.2);
  animation: fadeIn 0.18s ease;
  pointer-events: none;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes popIn {
  from { opacity: 0; transform: translateY(8px) scale(0.97); }
  to { opacity: 1; transform: none; }
}

@media (max-width: 1280px) {
  .plan-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); }
  .brand h1 { font-size: 18px; }
  .brand .sub { font-size: 10px; }
  .filter-field { padding: 0 10px; }
  .hd-meta .legend { gap: 10px; }
}

@media (max-width: 1100px) {
  .task-planning-page { overflow: auto; height: auto; min-height: 100vh; }
  .plan-grid { grid-template-columns: repeat(2, 1fr); flex: none; min-height: 420px; }
  .plan-card { min-height: 420px; }

}

@media (max-width: 720px) {
  .plan-grid { grid-template-columns: 1fr; }
  .detail-grid { grid-template-columns: repeat(2, 1fr); }
}

/* 任务与计划组件补充样式 */

.plan-grid { flex: 1 1 auto; min-height: 0; }
.filter-box { max-width: none; }
.backend-state {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  max-width: 260px;
  color: var(--text-2);
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.backend-state::before {
  content: '';
  width: 7px;
  height: 7px;
  flex: 0 0 7px;
  border-radius: 50%;
  background: var(--orange);
  box-shadow: 0 0 8px currentColor;
}
.backend-state.database::before { background: var(--green); }
.backend-state.error::before { background: var(--red); }
.backend-state.loading::before { animation: glowPulse 1.2s infinite; }
.task-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid var(--border);
}
.task-actions button,
.task-actions a {
  min-height: 30px;
  padding: 0 13px;
  border: 1px solid rgba(58,160,255,.35);
  border-radius: 6px;
  background: rgba(58,160,255,.12);
  color: #bfe4ff;
  font-size: 12px;
  line-height: 28px;
  text-decoration: none;
}
.task-actions button.primary { border-color: rgba(0,255,198,.45); background: rgba(0,255,198,.12); color: var(--green); }
.task-actions button.warn { border-color: rgba(250,173,20,.45); background: rgba(250,173,20,.1); color: #ffd666; }
.task-actions button:disabled { opacity: .45; cursor: not-allowed; }
.task-context {
  display: grid;
  grid-template-columns: repeat(2, minmax(0,1fr));
  gap: 8px;
  margin-top: 10px;
}
.task-context .context-box,
.operation-log {
  padding: 10px;
  border: 1px solid var(--border);
  border-radius: 7px;
  background: rgba(7,17,30,.55);
  font-size: 12px;
  color: var(--text-2);
  line-height: 1.55;
}
.task-context strong,
.operation-log strong { color: #b9c9dc; }
.operation-log { margin-top: 8px; }
.log-row { display: grid; grid-template-columns: 112px 72px minmax(0,1fr); gap: 8px; padding: 4px 0; border-bottom: 1px dashed rgba(90,120,150,.18); }
.log-row:last-child { border-bottom: 0; }
.delay-notice { margin-top: 8px; padding: 8px 10px; border: 1px solid rgba(250,173,20,.35); border-radius: 6px; color: #ffd666; background: rgba(250,173,20,.08); font-size: 12px; }
.modal { width: min(760px, calc(100vw - 28px)); max-height: calc(100vh - 30px); overflow-y: auto; }
.modal.compact { width: min(560px, calc(100vw - 28px)); }
.form-3 { display: grid; grid-template-columns: repeat(3, minmax(0,1fr)); gap: 12px; }
.form-row textarea {
  width: 100%;
  min-height: 72px;
  resize: vertical;
  border: 1px solid rgba(80,130,180,.3);
  border-radius: 6px;
  background: rgba(4,12,22,.72);
  color: var(--text);
  padding: 9px 10px;
  outline: none;
}
.form-row textarea:focus { border-color: var(--blue); }
.check-list { display: flex; flex-wrap: wrap; gap: 8px; }
.check-item { display: inline-flex; align-items: center; gap: 6px; min-height: 34px; padding: 0 10px; border: 1px solid rgba(80,130,180,.3); border-radius: 6px; color: var(--text-2); cursor: pointer; }
.check-item:has(input:checked) { border-color: var(--green); color: var(--green); background: rgba(0,255,198,.08); }
.check-item input { width: 14px; height: 14px; appearance: auto; cursor: pointer; }
.publisher-readonly { min-height: 36px; display: flex; align-items: center; padding: 0 10px; border: 1px dashed rgba(80,130,180,.3); border-radius: 6px; color: var(--text-2); }
.empty-group { padding: 10px; color: var(--text-3); font-size: 12px; text-align: center; }
@media (max-width: 1180px) {
  .page-hd { flex-wrap: wrap; }
  .filter-box { order: 3; width: 100%; overflow-x: auto; }
  .backend-state { max-width: 180px; }
}
@media (max-width: 720px) {
  .form-2, .form-3, .task-context { grid-template-columns: 1fr; }
  .log-row { grid-template-columns: 1fr; gap: 2px; }
}


.review-decision {
  padding: 10px 12px;
  border: 1px solid rgba(0, 255, 198, 0.35);
  border-radius: 7px;
  color: var(--green);
  background: rgba(0, 255, 198, 0.08);
  font-size: 13px;
  font-weight: 600;
}

.review-decision.rejected {
  border-color: rgba(255, 77, 106, 0.4);
  color: var(--red);
  background: rgba(255, 77, 106, 0.08);
}


</style>
