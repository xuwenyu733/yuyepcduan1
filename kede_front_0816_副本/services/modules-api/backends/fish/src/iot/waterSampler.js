/**
 * 门头沟水质定时采样：拉传感器 → 写库 → 页面读库渲染
 */
const { getLatest, DEVICE_ID } = require('./hflrwlClient')
const { persistFromLatest } = require('./waterDb')

// 设备约 62s 一帧；30s 采一次足够，页面仍可 5s 读库。同帧由 waterDb 按 sample_time 去重
const INTERVAL_MS = Math.max(
  5000,
  Number(process.env.HFLRWL_SAMPLE_INTERVAL_MS || 30000)
)

let timer = null
let running = false
let lastOkAt = 0
let lastError = ''
let lastSaved = null
let tickCount = 0

async function sampleOnce({ writeHistory = true } = {}) {
  if (running) return { skipped: true, reason: 'inflight' }
  running = true
  try {
    const latest = await getLatest(DEVICE_ID)
    const { live, saved } = await persistFromLatest(latest, { writeHistory })
    lastOkAt = Date.now()
    lastError = ''
    lastSaved = saved
    tickCount += 1
    if (tickCount <= 3 || tickCount % 20 === 0) {
      console.log(
        `[fish/iot/sampler] ok saved=${saved.saved} skipped=${saved.skipped || 0} asOf=${live && live.waterAsOf} tick=${tickCount}`
      )
    }
    return { ok: true, live, saved }
  } catch (err) {
    lastError = String((err && err.message) || err)
    console.warn('[fish/iot/sampler]', lastError)
    return { ok: false, error: lastError }
  } finally {
    running = false
  }
}

function startWaterSampler() {
  if (timer) return getSamplerStatus()
  console.log(`[fish/iot/sampler] start interval=${INTERVAL_MS}ms`)
  // 启动先采一针，再进入周期
  sampleOnce({ writeHistory: true }).catch(() => {})
  timer = setInterval(() => {
    sampleOnce({ writeHistory: true }).catch(() => {})
  }, INTERVAL_MS)
  if (typeof timer.unref === 'function') timer.unref()
  return getSamplerStatus()
}

function stopWaterSampler() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

function getSamplerStatus() {
  return {
    running: !!timer,
    inflight: running,
    intervalMs: INTERVAL_MS,
    lastOkAt: lastOkAt || null,
    lastError: lastError || '',
    lastSaved,
    tickCount
  }
}

module.exports = {
  sampleOnce,
  startWaterSampler,
  stopWaterSampler,
  getSamplerStatus,
  INTERVAL_MS
}
