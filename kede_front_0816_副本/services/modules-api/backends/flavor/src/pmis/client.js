const { config } = require('./config')

function esc(value) {
  if (value == null) return ''
  return String(value).replace(/\\/g, '\\\\').replace(/'/g, "''")
}

async function postJson(pathname, body) {
  const url = `${config.baseUrl}/${pathname.replace(/^\//, '')}`
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), config.timeoutMs)
  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'User-Agent': 'flavor-backend/1.0'
      },
      body: JSON.stringify(body),
      signal: controller.signal
    })
    const text = await res.text()
    let data
    try {
      data = JSON.parse(text.replace(/^\uFEFF/, '').trim() || '{}')
    } catch (e) {
      throw new Error(`PMIS 响应非 JSON: ${text.slice(0, 200)}`)
    }
    return data
  } finally {
    clearTimeout(timer)
  }
}

/**
 * 只读查询。sqlstring 内只能是 SELECT。
 * @param {string[]} sqls
 * @returns {Promise<any[][]>} data 数组
 */
async function query(sqls) {
  const list = Array.isArray(sqls) ? sqls : [sqls]
  const data = await postJson('getDataListBySql.jsp', { sqlstring: list })
  if (String(data.code) !== '10000') {
    throw new Error(data.msg || 'PMIS 查询失败')
  }
  return data.data || []
}

async function queryOne(sql) {
  const rows = await query([sql])
  return rows[0] || []
}

/**
 * 写入（INSERT/UPDATE）。仅用于本次新建的 fo_* 表。
 * @param {string[]} sqls
 */
async function execute(sqls) {
  const list = Array.isArray(sqls) ? sqls : [sqls]
  const data = await postJson('updateInfo.jsp', { sqlstring: list })
  const ok = String(data.code) === '10000' || String(data.msg || '').includes('成功')
  if (!ok) {
    throw new Error(data.msg || 'PMIS 写入失败')
  }
  return data
}

/**
 * PMIS 官方建表/插行接口：字段值为 "类型定义;实际值"
 * 与表单「增加一条/保存表单设计」相同机制。
 */
async function saveInfo(payload) {
  const data = await postJson('saveInfo1.jsp', payload)
  if (String(data.code) !== '10000') {
    throw new Error(data.msg || 'PMIS saveInfo 失败')
  }
  return data
}

function varcharField(comment, value, len = 255) {
  const v = value == null ? '' : String(value)
  return `varchar(${len}) NOT NULL DEFAULT '' COMMENT '${esc(comment)}';${v}`
}

function textField(comment, value) {
  const v = value == null ? '' : String(value)
  return `text COMMENT '${esc(comment)}';${v}`
}

module.exports = {
  esc,
  query,
  queryOne,
  execute,
  saveInfo,
  varcharField,
  textField
}
