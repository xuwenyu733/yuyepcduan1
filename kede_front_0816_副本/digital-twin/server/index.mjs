import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import cors from 'cors'
import express from 'express'
import Database from 'better-sqlite3'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const root = path.resolve(__dirname, '..')
const dataDir = path.join(root, 'data')
const dbPath = path.join(dataDir, 'twin_telemetry.sqlite')

if (!fs.existsSync(dataDir)) fs.mkdirSync(dataDir, { recursive: true })

const db = new Database(dbPath)
db.pragma('journal_mode = WAL')

db.exec(`
CREATE TABLE IF NOT EXISTS device_telemetry (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  biz_id TEXT NOT NULL,
  device_code TEXT NOT NULL,
  device_name TEXT,
  device_type TEXT,
  pool_code TEXT,
  workshop TEXT,
  hotspot TEXT,
  temp_c REAL,
  do_mg_l REAL,
  ph REAL,
  level_pct REAL,
  humidity REAL,
  power_on INTEGER,
  switch_state TEXT,
  camera_online INTEGER,
  camera_scene TEXT,
  rpm REAL,
  flow_rate REAL,
  feeding INTEGER,
  alarm TEXT,
  payload_json TEXT,
  source_system TEXT DEFAULT 'fish-twin-generator',
  is_mock INTEGER DEFAULT 1,
  created_at TEXT NOT NULL,
  isDel INTEGER DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_telemetry_device_time
  ON device_telemetry(device_code, created_at DESC);
`)

/** Keep catalog in sync with frontend seeds (duplicated lightly for server autonomy). */
const CATALOG = [
  { id: 'tank-01', deviceCode: 'TANK-OH-01', name: '开放厅 · 一号养成池', type: 'tank', hotspot: 'Hotspot_Tank_01', workshop: '开放厅', poolCode: 'OH-01' },
  { id: 'tank-02', deviceCode: 'TANK-OH-02', name: '开放厅 · 二号养成池', type: 'tank', hotspot: 'Hotspot_Tank_02', workshop: '开放厅', poolCode: 'OH-02' },
  { id: 'tank-03', deviceCode: 'TANK-OH-03', name: '开放厅 · 三号养成池', type: 'tank', hotspot: 'Hotspot_Tank_03', workshop: '开放厅', poolCode: 'OH-03' },
  { id: 'tank-04', deviceCode: 'TANK-OH-04', name: '开放厅 · 四号苗种池', type: 'tank', hotspot: 'Hotspot_Tank_04', workshop: '开放厅', poolCode: 'OH-04' },
  { id: 'pump-01', deviceCode: 'PUMP-OH-A', name: '开放厅 · 循环水泵 A', type: 'pump', hotspot: 'Hotspot_Pump_01', workshop: '开放厅', poolCode: 'OH-01' },
  { id: 'sensor-do-01', deviceCode: 'SNS-OH-DO-01', name: '开放厅 · 溶氧探头 #1', type: 'sensor', hotspot: 'Hotspot_Sensor_DO_01', workshop: '开放厅', poolCode: 'OH-02' },
  { id: 'cam-oh-01', deviceCode: 'CAM-OH-01', name: '开放厅 · 球机摄像头', type: 'camera', hotspot: 'Hotspot_Cam_OH_01', workshop: '开放厅', poolCode: 'OH-01' },
  { id: 'thermo-oh-01', deviceCode: 'TMP-OH-01', name: '开放厅 · 水温计', type: 'thermometer', hotspot: 'Hotspot_Thermo_OH_01', workshop: '开放厅', poolCode: 'OH-01' },
  { id: 'sw-oh-01', deviceCode: 'SW-OH-PUMP', name: '开放厅 · 水泵启停开关', type: 'switch', hotspot: 'Hotspot_Switch_OH_01', workshop: '开放厅', poolCode: 'OH-01' },
  { id: 'feeder-oh-01', deviceCode: 'FEED-OH-01', name: '开放厅 · 自动投喂机', type: 'feeder', hotspot: 'Hotspot_Feeder_OH_01', workshop: '开放厅', poolCode: 'OH-02' },
  { id: 'aerator-oh-01', deviceCode: 'AER-OH-01', name: '开放厅 · 增氧机', type: 'aerator', hotspot: 'Hotspot_Aerator_OH_01', workshop: '开放厅', poolCode: 'OH-03' },
  { id: 'b1-tank-01', deviceCode: 'TANK-B1-01', name: '厂房一号 · 跑道池 1', type: 'tank', hotspot: 'Hotspot_B1_Tank_01', workshop: '厂房一号', poolCode: 'B1-01' },
  { id: 'b1-tank-02', deviceCode: 'TANK-B1-02', name: '厂房一号 · 跑道池 2', type: 'tank', hotspot: 'Hotspot_B1_Tank_02', workshop: '厂房一号', poolCode: 'B1-02' },
  { id: 'b1-tank-03', deviceCode: 'TANK-B1-03', name: '厂房一号 · 跑道池 3', type: 'tank', hotspot: 'Hotspot_B1_Tank_03', workshop: '厂房一号', poolCode: 'B1-03' },
  { id: 'b1-tank-04', deviceCode: 'TANK-B1-04', name: '厂房一号 · 跑道池 4', type: 'tank', hotspot: 'Hotspot_B1_Tank_04', workshop: '厂房一号', poolCode: 'B1-04' },
  { id: 'b1-tank-05', deviceCode: 'TANK-B1-05', name: '厂房一号 · 跑道池 5', type: 'tank', hotspot: 'Hotspot_B1_Tank_05', workshop: '厂房一号', poolCode: 'B1-05' },
  { id: 'b1-tank-06', deviceCode: 'TANK-B1-06', name: '厂房一号 · 跑道池 6', type: 'tank', hotspot: 'Hotspot_B1_Tank_06', workshop: '厂房一号', poolCode: 'B1-06' },
  { id: 'b1-pump-01', deviceCode: 'PUMP-B1-01', name: '厂房一号 · 循环水泵', type: 'pump', hotspot: 'Hotspot_B1_Pump_01', workshop: '厂房一号', poolCode: 'B1-01' },
  { id: 'b1-filter-01', deviceCode: 'FLT-B1-01', name: '厂房一号 · 过滤单元', type: 'filter', hotspot: 'Hotspot_B1_Filter_01', workshop: '厂房一号', poolCode: 'B1-01' },
  { id: 'b1-sensor-do-01', deviceCode: 'SNS-B1-DO-01', name: '厂房一号 · 溶氧探头', type: 'sensor', hotspot: 'Hotspot_B1_Sensor_DO_01', workshop: '厂房一号', poolCode: 'B1-03' },
  { id: 'b1-cam-01', deviceCode: 'CAM-B1-01', name: '厂房一号 · 枪机摄像头', type: 'camera', hotspot: 'Hotspot_Cam_B1_01', workshop: '厂房一号', poolCode: 'B1-02' },
  { id: 'b1-thermo-01', deviceCode: 'TMP-B1-01', name: '厂房一号 · 水温计', type: 'thermometer', hotspot: 'Hotspot_Thermo_B1_01', workshop: '厂房一号', poolCode: 'B1-01' },
  { id: 'b1-sw-01', deviceCode: 'SW-B1-LIGHT', name: '厂房一号 · 照明回路开关', type: 'switch', hotspot: 'Hotspot_Switch_B1_01', workshop: '厂房一号', poolCode: 'B1-00' },
  { id: 'b1-feeder-01', deviceCode: 'FEED-B1-01', name: '厂房一号 · 投喂机', type: 'feeder', hotspot: 'Hotspot_Feeder_B1_01', workshop: '厂房一号', poolCode: 'B1-02' },
  { id: 'b1-aerator-01', deviceCode: 'AER-B1-01', name: '厂房一号 · 纳米增氧', type: 'aerator', hotspot: 'Hotspot_Aerator_B1_01', workshop: '厂房一号', poolCode: 'B1-04' },
]

const state = new Map()
for (const [i, d] of CATALOG.entries()) {
  state.set(d.id, {
    ...d,
    tempC: 24 + (i % 5) * 0.6,
    dissolvedOxygen: 6.4 - (i % 4) * 0.25,
    ph: 7.1 + (i % 3) * 0.12,
    levelPct: 72 - (i % 6) * 3,
    humidity: 55 + (i % 7),
    powerOn: i % 5 !== 0,
    switchState: i % 4 === 0 ? 'auto' : i % 3 === 0 ? 'off' : 'on',
    cameraOnline: true,
    cameraScene: d.type === 'camera' ? (d.workshop === '厂房一号' ? '跑道池阵列俯视' : '养成池全景') : '—',
    rpm: d.type === 'pump' || d.type === 'aerator' ? 1200 + (i % 5) * 80 : 0,
    flowRate: d.type === 'pump' ? 35 + (i % 4) * 2.5 : 0,
    feeding: d.type === 'feeder' ? i % 2 === 0 : false,
    alarm: 'normal',
  })
}

function clamp(n, min, max) {
  return Math.min(max, Math.max(min, n))
}
function jitter(base, amp) {
  return base + (Math.random() * 2 - 1) * amp
}

function deriveAlarm(d) {
  if (d.type === 'camera' && !d.cameraOnline) return 'critical'
  if (d.type === 'switch' && d.switchState === 'off' && d.deviceCode.includes('PUMP')) return 'warn'
  if (d.type === 'pump' || d.type === 'aerator' || d.type === 'filter') {
    if (!d.powerOn) return 'warn'
    if (d.levelPct > 90 && d.type === 'filter') return 'critical'
  }
  if (d.type === 'thermometer' && (d.tempC > 30 || d.tempC < 18)) return 'critical'
  if (d.type === 'thermometer' && (d.tempC > 28 || d.tempC < 20)) return 'warn'
  if (d.type === 'tank' || d.type === 'sensor') {
    if (d.dissolvedOxygen < 4.5 || d.tempC > 30 || d.ph < 6.5 || d.ph > 8.8) return 'critical'
    if (d.dissolvedOxygen < 5.5 || d.tempC > 28 || d.levelPct < 40) return 'warn'
  }
  return 'normal'
}

function tickOne(d, i) {
  const next = { ...d }
  next.tempC = clamp(jitter(d.tempC, d.type === 'thermometer' ? 0.12 : 0.06), 16, 34)
  next.dissolvedOxygen = clamp(jitter(d.dissolvedOxygen, 0.1), 3.0, 9.5)
  next.ph = clamp(jitter(d.ph, 0.025), 6.0, 9.0)
  next.levelPct = clamp(jitter(d.levelPct, 0.5), 18, 98)
  next.humidity = clamp(jitter(d.humidity, 0.4), 35, 85)
  next.rpm =
    d.powerOn && (d.type === 'pump' || d.type === 'aerator')
      ? clamp(jitter(d.rpm, 25), 600, 1800)
      : d.type === 'pump' || d.type === 'aerator'
        ? 0
        : d.rpm
  next.flowRate =
    d.powerOn && d.type === 'pump' ? clamp(jitter(d.flowRate, 0.8), 10, 60) : d.type === 'pump' ? 0 : d.flowRate
  next.feeding = d.type === 'feeder' ? (i % 3 === 0 ? Math.random() > 0.55 : d.feeding) : d.feeding
  next.cameraOnline =
    d.type === 'camera' ? Math.random() > 0.008 : d.cameraOnline
  if (d.id === 'tank-03' && Math.random() < 0.1) {
    next.dissolvedOxygen = clamp(next.dissolvedOxygen - 1.2, 3.0, 9.5)
  }
  next.alarm = deriveAlarm(next)
  return next
}

const insertStmt = db.prepare(`
INSERT INTO device_telemetry (
  biz_id, device_code, device_name, device_type, pool_code, workshop, hotspot,
  temp_c, do_mg_l, ph, level_pct, humidity, power_on, switch_state,
  camera_online, camera_scene, rpm, flow_rate, feeding, alarm,
  payload_json, source_system, is_mock, created_at, isDel
) VALUES (
  @biz_id, @device_code, @device_name, @device_type, @pool_code, @workshop, @hotspot,
  @temp_c, @do_mg_l, @ph, @level_pct, @humidity, @power_on, @switch_state,
  @camera_online, @camera_scene, @rpm, @flow_rate, @feeding, @alarm,
  @payload_json, 'fish-twin-generator', 1, @created_at, 0
)`)

const insertMany = db.transaction((rows) => {
  for (const row of rows) insertStmt.run(row)
})

function snapshotRows() {
  const now = new Date().toISOString()
  const rows = []
  let i = 0
  for (const [id, prev] of state.entries()) {
    const next = tickOne(prev, i++)
    state.set(id, next)
    rows.push({
      biz_id: `${next.deviceCode}-${Date.now()}-${i}`,
      device_code: next.deviceCode,
      device_name: next.name,
      device_type: next.type,
      pool_code: next.poolCode,
      workshop: next.workshop,
      hotspot: next.hotspot,
      temp_c: next.tempC,
      do_mg_l: next.dissolvedOxygen,
      ph: next.ph,
      level_pct: next.levelPct,
      humidity: next.humidity,
      power_on: next.powerOn ? 1 : 0,
      switch_state: next.switchState,
      camera_online: next.cameraOnline ? 1 : 0,
      camera_scene: next.cameraScene,
      rpm: next.rpm,
      flow_rate: next.flowRate,
      feeding: next.feeding ? 1 : 0,
      alarm: next.alarm,
      payload_json: JSON.stringify(next),
      created_at: now,
    })
  }
  return rows
}

function toClientDevice(rowOrState) {
  if (rowOrState.payload_json) {
    try {
      return JSON.parse(rowOrState.payload_json)
    } catch {
      /* fallthrough */
    }
  }
  const d = rowOrState
  return {
    id: CATALOG.find((c) => c.deviceCode === d.device_code)?.id ?? d.device_code,
    deviceCode: d.device_code ?? d.deviceCode,
    name: d.device_name ?? d.name,
    type: d.device_type ?? d.type,
    hotspot: d.hotspot,
    workshop: d.workshop,
    poolCode: d.pool_code ?? d.poolCode,
    tempC: d.temp_c ?? d.tempC,
    dissolvedOxygen: d.do_mg_l ?? d.dissolvedOxygen,
    ph: d.ph,
    levelPct: d.level_pct ?? d.levelPct,
    humidity: d.humidity,
    powerOn: Boolean(d.power_on ?? d.powerOn),
    switchState: d.switch_state ?? d.switchState,
    cameraOnline: Boolean(d.camera_online ?? d.cameraOnline),
    cameraScene: d.camera_scene ?? d.cameraScene,
    rpm: d.rpm,
    flowRate: d.flow_rate ?? d.flowRate,
    feeding: Boolean(d.feeding),
    alarm: d.alarm,
    sceneNote: CATALOG.find((c) => c.deviceCode === (d.device_code ?? d.deviceCode))?.name ?? '',
    options: [],
    updatedAt: d.created_at ?? new Date().toISOString(),
  }
}

let insertCount = 0
let genCycles = 0
let cachedTotalRows = db.prepare('SELECT COUNT(*) AS c FROM device_telemetry').get().c

function pruneOldRows() {
  // Cheap prune: delete by max id window instead of ORDER BY subquery every tick
  const row = db.prepare('SELECT MAX(id) AS maxId FROM device_telemetry').get()
  const maxId = row?.maxId
  if (!maxId || maxId <= 50000) return
  const cut = maxId - 50000
  const info = db.prepare('DELETE FROM device_telemetry WHERE id < ?').run(cut)
  if (info.changes) {
    cachedTotalRows = Math.max(0, cachedTotalRows - info.changes)
  }
}

function generateOnce() {
  const rows = snapshotRows()
  insertMany(rows)
  insertCount += rows.length
  cachedTotalRows += rows.length
  genCycles += 1
  // Retention only every ~60 cycles (~5 min at 5s interval)
  if (genCycles % 60 === 0) pruneOldRows()
}

generateOnce()
const INTERVAL_MS = Number(process.env.GENERATOR_INTERVAL_MS || 5000)
setInterval(generateOnce, INTERVAL_MS)

const app = express()
app.use(cors())
app.use(express.json())

app.get('/api/health', (_req, res) => {
  res.json({
    ok: true,
    dbPath,
    totalRows: cachedTotalRows,
    insertCount,
    intervalMs: INTERVAL_MS,
  })
})

app.get('/api/devices/latest', (_req, res) => {
  const devices = [...state.values()].map((d) => {
    const client = {
      id: d.id,
      deviceCode: d.deviceCode,
      name: d.name,
      type: d.type,
      hotspot: d.hotspot,
      workshop: d.workshop,
      poolCode: d.poolCode,
      tempC: d.tempC,
      dissolvedOxygen: d.dissolvedOxygen,
      ph: d.ph,
      levelPct: d.levelPct,
      humidity: d.humidity,
      powerOn: d.powerOn,
      switchState: d.switchState,
      cameraOnline: d.cameraOnline,
      cameraScene: d.cameraScene,
      rpm: d.rpm,
      flowRate: d.flowRate,
      feeding: d.feeding,
      alarm: d.alarm,
      updatedAt: new Date().toISOString(),
    }
    return client
  })
  res.json({ devices, source: 'sqlite-generator' })
})

app.get('/api/devices/:code/history', (req, res) => {
  const limit = Math.min(Number(req.query.limit) || 20, 100)
  const rows = db
    .prepare(
      `SELECT created_at, alarm, temp_c, do_mg_l, ph, level_pct, power_on, switch_state, camera_online, rpm, flow_rate, feeding, device_type
       FROM device_telemetry
       WHERE device_code = ? AND isDel = 0
       ORDER BY id DESC LIMIT ?`,
    )
    .all(req.params.code, limit)

  const history = rows.map((r) => {
    let summary = `报警 ${r.alarm}`
    if (r.device_type === 'camera') summary = r.camera_online ? '摄像头在线' : '摄像头离线'
    else if (r.device_type === 'thermometer') summary = `温度 ${Number(r.temp_c).toFixed(1)}°C`
    else if (r.device_type === 'switch') summary = `开关 ${r.switch_state}`
    else if (r.device_type === 'pump') summary = r.power_on ? `流量 ${Number(r.flow_rate).toFixed(1)}` : '停机'
    else if (r.device_type === 'feeder') summary = r.feeding ? '投喂中' : '待机'
    else summary = `DO ${Number(r.do_mg_l).toFixed(2)} · ${Number(r.temp_c).toFixed(1)}°C`
    return {
      t: new Date(r.created_at).toLocaleTimeString(),
      summary,
      raw: r,
    }
  })
  res.json({ history })
})

app.post('/api/devices/:id/action', (req, res) => {
  const d = state.get(req.params.id)
  if (!d) return res.status(404).json({ error: 'device not found' })
  const action = req.body?.action
  if (action === 'toggle_power') d.powerOn = !d.powerOn
  if (action === 'toggle_switch') {
    d.switchState = d.switchState === 'off' ? 'on' : 'off'
    d.powerOn = d.switchState !== 'off'
  }
  if (action === 'toggle_feeding') d.feeding = !d.feeding
  if (action === 'cycle_switch_mode') {
    d.switchState = d.switchState === 'on' ? 'auto' : d.switchState === 'auto' ? 'off' : 'on'
  }
  if (!d.powerOn && (d.type === 'pump' || d.type === 'aerator')) {
    d.rpm = 0
    d.flowRate = 0
  }
  d.alarm = deriveAlarm(d)
  state.set(d.id, d)
  // Persist action as a row
  insertStmt.run({
    biz_id: `${d.deviceCode}-action-${Date.now()}`,
    device_code: d.deviceCode,
    device_name: d.name,
    device_type: d.type,
    pool_code: d.poolCode,
    workshop: d.workshop,
    hotspot: d.hotspot,
    temp_c: d.tempC,
    do_mg_l: d.dissolvedOxygen,
    ph: d.ph,
    level_pct: d.levelPct,
    humidity: d.humidity,
    power_on: d.powerOn ? 1 : 0,
    switch_state: d.switchState,
    camera_online: d.cameraOnline ? 1 : 0,
    camera_scene: d.cameraScene,
    rpm: d.rpm,
    flow_rate: d.flowRate,
    feeding: d.feeding ? 1 : 0,
    alarm: d.alarm,
    payload_json: JSON.stringify({ ...d, action }),
    created_at: new Date().toISOString(),
  })
  res.json({ ok: true, device: toClientDevice(d) })
})

const PORT = Number(process.env.PORT || 8787)
app.listen(PORT, () => {
  console.log(`[twin-api] http://127.0.0.1:${PORT}`)
  console.log(`[twin-api] sqlite: ${dbPath}`)
  console.log(`[twin-api] generator every ${INTERVAL_MS}ms`)
})
