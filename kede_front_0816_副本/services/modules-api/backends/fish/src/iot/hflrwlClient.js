/**
 * 门头沟基地 IoT（hflrwl / 淮来物联开放接口）
 * 链路与 wiidupmis_test.html 一致：login → me-iot → properties/latest
 *
 * 密码请用环境变量 HFLRWL_PASSWORD；代码内兜底仅便于本地联调。
 */
const BASE = String(process.env.HFLRWL_BASE_URL || 'https://hflrwl.com/lrzhwl').replace(/\/$/, '')
const USERNAME = process.env.HFLRWL_USERNAME || '13693335855'
const PASSWORD = process.env.HFLRWL_PASSWORD || '654321abc'
const TENANT_ID = String(process.env.HFLRWL_TENANT_ID || '1')
const DEVICE_ID = String(process.env.HFLRWL_DEVICE_ID || '868898083070563')
const TIMEOUT_MS = Number(process.env.HFLRWL_TIMEOUT_MS || 12000)
/** 上游短缓存，避免水质页+大屏同时开着重复打 */
const LATEST_CACHE_MS = Number(process.env.HFLRWL_LATEST_CACHE_MS || 10000)
const HISTORY_CACHE_MS = Number(process.env.HFLRWL_HISTORY_CACHE_MS || 60000)
const HISTORY_TIMEOUT_MS = Number(process.env.HFLRWL_HISTORY_TIMEOUT_MS || 20000)
const HISTORY_CONCURRENCY = Math.max(1, Number(process.env.HFLRWL_HISTORY_CONCURRENCY || 3))

/** 趋势图用到的属性（分池溶氧/水温 + 共享 pH/浊度/ORP） */
const HISTORY_PROPERTIES = [
  'do_oxygen_1',
  'do_oxygen_2',
  'do_oxygen_3',
  'do_temp_1',
  'do_temp_2',
  'do_temp_3',
  'ph_ph_1',
  'ntu_ntu_1',
  'orp_orp_1'
]

let cachedAccessToken = ''
let cachedAccessExpireAt = 0
let cachedIotToken = ''
let cachedIotExpireAt = 0
/** @type {Map<string, Promise<any>>} */
const inflightByDevice = new Map()
/** @type {Map<string, { at: number, data: any }>} */
const latestCacheByDevice = new Map()
/** @type {Map<string, { at: number, data: any }>} */
const historyCacheByKey = new Map()
/** @type {Map<string, Promise<any>>} */
const historyInflightByKey = new Map()

function now() {
  return Date.now()
}

async function httpJson(method, path, { headers = {}, body, timeoutMs } = {}) {
  const ctrl = typeof AbortController !== 'undefined' ? new AbortController() : null
  const wait = Number(timeoutMs) > 0 ? Number(timeoutMs) : TIMEOUT_MS
  const timer = setTimeout(() => {
    try {
      ctrl && ctrl.abort()
    } catch (_) {
      /* ignore */
    }
  }, wait)
  try {
    const res = await fetch(`${BASE}${path}`, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'tenant-id': TENANT_ID,
        ...headers
      },
      body: body != null ? JSON.stringify(body) : undefined,
      signal: ctrl ? ctrl.signal : undefined
    })
    const text = await res.text()
    let data = null
    try {
      data = text ? JSON.parse(text) : null
    } catch (_) {
      throw new Error(`hflrwl 非 JSON 响应 HTTP ${res.status}`)
    }
    if (!res.ok) {
      throw new Error((data && data.msg) || `hflrwl HTTP ${res.status}`)
    }
    return data
  } finally {
    clearTimeout(timer)
  }
}

async function authHeaders() {
  const accessToken = await login()
  const iotToken = await meIot()
  return {
    Authorization: `Bearer ${accessToken}`,
    'Iot-Token': iotToken
  }
}

function clearAuthOn401(data) {
  if (data && Number(data.code) === 401) {
    cachedAccessToken = ''
    cachedIotToken = ''
    cachedAccessExpireAt = 0
    cachedIotExpireAt = 0
  }
}

/**
 * 单属性历史：开放接口要求 POST + property + startTime/endTime（times 常空）
 * 上游可能忽略 pageSize，返回大批量点，调用方需自行按窗口过滤并降采样。
 */
async function fetchPropertyHistoryRaw(deviceId, property, startTime, endTime) {
  const id = String(deviceId || DEVICE_ID)
  const prop = String(property || '')
  if (!prop) return []

  const run = async () => {
    const headers = await authHeaders()
    const data = await httpJson(
      'POST',
      `/open/iot/device/${encodeURIComponent(id)}/properties/history`,
      {
        headers,
        body: {
          property: prop,
          startTime: Number(startTime),
          endTime: Number(endTime)
        },
        timeoutMs: HISTORY_TIMEOUT_MS
      }
    )
    if (!data || Number(data.code) !== 0) {
      clearAuthOn401(data)
      const msg = (data && data.msg) || `hflrwl history 失败 (${prop})`
      const err = new Error(msg)
      err.code = data && data.code
      throw err
    }
    return Array.isArray(data.data) ? data.data : []
  }

  try {
    return await run()
  } catch (err) {
    const msg = String((err && err.message) || err || '')
    if (/未登录|401|token|系统异常|500/i.test(msg)) {
      cachedAccessToken = ''
      cachedIotToken = ''
      cachedAccessExpireAt = 0
      cachedIotExpireAt = 0
      return run()
    }
    throw err
  }
}

async function mapPool(items, concurrency, fn) {
  const list = items || []
  const out = new Array(list.length)
  let cursor = 0
  const workers = Array.from({ length: Math.min(concurrency, list.length) || 1 }, async () => {
    while (cursor < list.length) {
      const idx = cursor++
      out[idx] = await fn(list[idx], idx)
    }
  })
  await Promise.all(workers)
  return out
}

/**
 * 并行拉取多属性历史（限并发，单属性失败不拖垮整包）
 * @returns {Promise<Record<string, any[]>>}
 */
async function fetchPropertiesHistory(deviceId, properties, startTime, endTime) {
  const props = (properties && properties.length ? properties : HISTORY_PROPERTIES).map(String)
  const results = await mapPool(props, HISTORY_CONCURRENCY, async (property) => {
    try {
      const rows = await fetchPropertyHistoryRaw(deviceId, property, startTime, endTime)
      return { property, rows, error: null }
    } catch (err) {
      console.warn(
        '[hflrwl] history',
        property,
        (err && err.message) || err
      )
      return { property, rows: [], error: String((err && err.message) || err) }
    }
  })
  /** @type {Record<string, any[]>} */
  const byProp = Object.create(null)
  for (const r of results) {
    if (!r) continue
    byProp[r.property] = r.rows || []
  }
  return byProp
}

/**
 * 带缓存的多属性历史。
 * 上游基本按「最近大批量点」返回且常忽略时间窗，因此按 device+属性集缓存原始 byProp，
 * 再按请求的 hours 裁切窗口，避免 24h/7d 各打一轮上游。
 */
function getPropertiesHistory(deviceId, options = {}) {
  const id = String(deviceId || DEVICE_ID)
  const hours = Math.min(168, Math.max(1, Number(options.hours) || 168))
  const endTime = Number(options.endTime) > 0 ? Number(options.endTime) : now()
  const startTime = Number(options.startTime) > 0
    ? Number(options.startTime)
    : endTime - hours * 3600 * 1000
  const props = options.properties || HISTORY_PROPERTIES
  const rawKey = `${id}|${props.join(',')}`

  const wrap = (byProp) => ({
    deviceId: id,
    startTime,
    endTime,
    hours,
    byProp
  })

  const hit = historyCacheByKey.get(rawKey)
  if (hit && now() - hit.at < HISTORY_CACHE_MS) {
    return Promise.resolve(wrap(hit.data.byProp))
  }
  const inflight = historyInflightByKey.get(rawKey)
  if (inflight) {
    return inflight.then((raw) => wrap(raw.byProp))
  }

  // 固定拉足 7 天窗，覆盖页面 24h/3d/7d；映射层再按 hours 过滤
  const fetchEnd = endTime
  const fetchStart = fetchEnd - 168 * 3600 * 1000
  const p = fetchPropertiesHistory(id, props, fetchStart, fetchEnd)
    .then((byProp) => {
      const raw = { deviceId: id, byProp }
      historyCacheByKey.set(rawKey, { at: now(), data: raw })
      return raw
    })
    .finally(() => {
      historyInflightByKey.delete(rawKey)
    })

  historyInflightByKey.set(rawKey, p)
  return p.then((raw) => wrap(raw.byProp))
}

async function login() {
  if (cachedAccessToken && now() < cachedAccessExpireAt) return cachedAccessToken
  const data = await httpJson('POST', '/open/auth/login', {
    headers: { Authorization: '' },
    body: { username: USERNAME, password: PASSWORD }
  })
  if (!data || Number(data.code) !== 0 || !data.data || !data.data.accessToken) {
    throw new Error((data && data.msg) || 'hflrwl 登录失败')
  }
  cachedAccessToken = String(data.data.accessToken)
  const ttlMs = Number(data.data.expiresTime) > now()
    ? Math.max(60_000, Number(data.data.expiresTime) - now() - 60_000)
    : 25 * 60 * 1000
  cachedAccessExpireAt = now() + Math.min(ttlMs, 30 * 60 * 1000)
  cachedIotToken = ''
  cachedIotExpireAt = 0
  return cachedAccessToken
}

async function meIot() {
  if (cachedIotToken && now() < cachedIotExpireAt) return cachedIotToken
  const accessToken = await login()
  const data = await httpJson('GET', '/open/auth/me-iot', {
    headers: { Authorization: `Bearer ${accessToken}` }
  })
  if (!data || Number(data.code) !== 0 || !data.data || !data.data.token) {
    throw new Error((data && data.msg) || 'hflrwl me-iot 失败')
  }
  cachedIotToken = String(data.data.token)
  cachedIotExpireAt = now() + 10 * 60 * 1000
  return cachedIotToken
}

async function fetchLatestRaw(deviceId = DEVICE_ID) {
  const id = String(deviceId || DEVICE_ID)
  const accessToken = await login()
  const iotToken = await meIot()
  const data = await httpJson('GET', `/open/iot/device/${encodeURIComponent(id)}/properties/latest`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
      'Iot-Token': iotToken
    }
  })
  if (!data || Number(data.code) !== 0) {
    if (data && Number(data.code) === 401) {
      cachedAccessToken = ''
      cachedIotToken = ''
      cachedAccessExpireAt = 0
      cachedIotExpireAt = 0
    }
    throw new Error((data && data.msg) || 'hflrwl latest 失败')
  }
  return {
    deviceId: id,
    asOf: new Date().toISOString(),
    properties: Array.isArray(data.data) ? data.data : []
  }
}

/**
 * 按 deviceId 合并并发；结果短缓存 ≤10s，避免页+大屏双打上游。
 */
function getLatest(deviceId) {
  const id = String(deviceId || DEVICE_ID)
  const hit = latestCacheByDevice.get(id)
  if (hit && now() - hit.at < LATEST_CACHE_MS) {
    return Promise.resolve(hit.data)
  }
  const inflight = inflightByDevice.get(id)
  if (inflight) return inflight

  const p = fetchLatestRaw(id)
    .catch(async (err) => {
      const msg = String((err && err.message) || err || '')
      if (/未登录|401|token/i.test(msg)) {
        cachedAccessToken = ''
        cachedIotToken = ''
        cachedAccessExpireAt = 0
        cachedIotExpireAt = 0
        return fetchLatestRaw(id)
      }
      throw err
    })
    .then((data) => {
      latestCacheByDevice.set(id, { at: now(), data })
      return data
    })
    .finally(() => {
      inflightByDevice.delete(id)
    })

  inflightByDevice.set(id, p)
  return p
}

function getConfig() {
  return {
    baseUrl: BASE,
    deviceId: DEVICE_ID,
    tenantId: TENANT_ID,
    username: USERNAME ? `${USERNAME.slice(0, 3)}****` : '',
    latestCacheMs: LATEST_CACHE_MS,
    historyCacheMs: HISTORY_CACHE_MS,
    historyProperties: HISTORY_PROPERTIES.slice()
  }
}

module.exports = {
  getLatest,
  getPropertiesHistory,
  fetchPropertyHistoryRaw,
  getConfig,
  DEVICE_ID,
  HISTORY_PROPERTIES
}
