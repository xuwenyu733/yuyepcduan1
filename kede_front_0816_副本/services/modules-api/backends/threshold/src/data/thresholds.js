const fs = require('fs')
const path = require('path')
const seed = require('./seed')

function clone(data) {
  return JSON.parse(JSON.stringify(data))
}

function nowDate() {
  const d = new Date()
  const p = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`
}

const STORE_FILE = path.join(__dirname, 'thresholds.store.json')

function loadPersisted() {
  try {
    if (!fs.existsSync(STORE_FILE)) return null
    const raw = fs.readFileSync(STORE_FILE, 'utf8')
    const parsed = JSON.parse(raw)
    if (!parsed || typeof parsed !== 'object') return null
    return parsed
  } catch (e) {
    console.warn('[threshold] load persisted store failed:', e && e.message)
    return null
  }
}

function persist() {
  try {
    fs.writeFileSync(STORE_FILE, JSON.stringify(store, null, 2), 'utf8')
  } catch (e) {
    console.warn('[threshold] persist store failed:', e && e.message)
  }
}

const DEVICE_SECTIONS = ['power', 'pipeline', 'electric', 'levels', 'maintain']

/** 设备运行分区若被错误写成 items[]，从 seed 恢复分区结构 */
function normalizeDeviceModule(device) {
  const base = clone(seed.device)
  if (!device || typeof device !== 'object') return base
  const hasSections = DEVICE_SECTIONS.some(k => Array.isArray(device[k]) && device[k].length)
  if (!hasSections) {
    return { ...base, enabled: device.enabled !== false }
  }
  const next = { enabled: device.enabled !== false }
  DEVICE_SECTIONS.forEach(k => {
    next[k] = Array.isArray(device[k]) && device[k].length ? clone(device[k]) : clone(base[k] || [])
  })
  return next
}

function normalizeStore(raw) {
  const next = raw && typeof raw === 'object' ? clone(raw) : clone(seed)
  if (!next.meta) next.meta = clone(seed.meta)
  next.device = normalizeDeviceModule(next.device)
  // 缺模块时用 seed 补齐，避免前端空白
  Object.keys(seed).forEach(key => {
    if (key === 'meta' || key === 'device') return
    if (next[key] == null) next[key] = clone(seed[key])
  })
  return next
}

let store = normalizeStore(loadPersisted() || clone(seed))
if (!store.meta) store.meta = clone(seed.meta)
// 启动时写回规范化后的设备分区，修复历史损坏数据
persist()

const MODULES = [
  { id: 'water', group: '监测配置', name: '水质指标', icon: 'el-icon-s-data' },
  { id: 'device', group: '监测配置', name: '设备运行', icon: 'el-icon-cpu' },
  { id: 'mortality', group: '鱼类健康', name: '鱼类死亡/死亡率', icon: 'el-icon-warning-outline' },
  { id: 'health', group: '鱼类健康', name: '健康评分', icon: 'el-icon-s-opportunity' },
  { id: 'feeding', group: '鱼类健康', name: '精准投喂', icon: 'el-icon-s-goods' },
  { id: 'alarm', group: '运营管理', name: 'AI 报警', icon: 'el-icon-bell' },
  { id: 'energy', group: '运营管理', name: '能耗管理', icon: 'el-icon-s-finance' },
  { id: 'experiment', group: '实验', name: '实验系统', icon: 'el-icon-s-order' },
  { id: 'security', group: '运营管理', name: '人员安防', icon: 'el-icon-user' },
  { id: 'holding', group: '运营管理', name: '暂养损耗', icon: 'el-icon-box' },
  { id: 'timeout', group: '运营管理', name: '任务超时', icon: 'el-icon-time' },
  { id: 'association', group: '关联配置', name: '数据关联', icon: 'el-icon-share' }
]

function getNav() {
  return MODULES
}

function getMeta() {
  return clone(store.meta)
}

function getAll() {
  return clone(store)
}

function getModule(id) {
  if (!store[id]) return null
  return clone(store[id])
}

function saveModule(id, data) {
  if (!store[id] && id !== 'meta') return null
  store[id] = id === 'device' ? normalizeDeviceModule(data) : clone(data)
  store.meta.lastSaved = nowDate()
  persist()
  return clone(store[id])
}

function saveAll(payload) {
  const keys = MODULES.map(m => m.id)
  keys.forEach(key => {
    if (!payload[key]) return
    store[key] = key === 'device' ? normalizeDeviceModule(payload[key]) : clone(payload[key])
  })
  store.meta.lastSaved = nowDate()
  persist()
  return getAll()
}

function resetAll() {
  store = normalizeStore(clone(seed))
  store.meta.lastSaved = nowDate()
  persist()
  return getAll()
}

module.exports = {
  getNav,
  getMeta,
  getAll,
  getModule,
  saveModule,
  saveAll,
  resetAll
}
