const express = require('express')
const { getLatest, getPropertiesHistory, getConfig, DEVICE_ID } = require('../iot/hflrwlClient')
const { mapToWaterLive } = require('../iot/mapWaterLive')
const { mapToWaterHistory } = require('../iot/mapWaterHistory')
const {
  loadMentougouLiveFromDb,
  loadMentougouHistoryFromDb,
  persistFromLatest
} = require('../iot/waterDb')
const { sampleOnce, getSamplerStatus } = require('../iot/waterSampler')

const router = express.Router()

/** 原始 latest 属性（调试用） */
router.get('/latest', async (req, res) => {
  try {
    const deviceId = req.query.deviceId || DEVICE_ID
    const latest = await getLatest(deviceId)
    res.json({ code: 0, data: latest })
  } catch (err) {
    console.error('[fish/iot/latest]', err && err.message ? err.message : err)
    res.status(502).json({
      code: 502,
      message: (err && err.message) || '门头沟 IoT latest 拉取失败'
    })
  }
})

/**
 * 水质 live：优先读库（采样器 5s 已写入），库空再直拉传感器并补写
 * 大屏 / 水质页统一走此接口渲染
 */
router.get('/water-live', async (req, res) => {
  const forceUpstream = String(req.query.force || '') === '1'
  try {
    const deviceId = req.query.deviceId || DEVICE_ID
    if (!forceUpstream) {
      try {
        const fromDb = await loadMentougouLiveFromDb()
        if (fromDb && (fromDb.pools || []).length) {
          // 库表无饱和度/水位列：用短缓存 latest 补齐附属量 + 设备开关
          try {
            const latest = await getLatest(deviceId)
            const live = mapToWaterLive(latest)
            const byIdx = new Map((live.pools || []).map((p) => [Number(p.idx), p]))
            fromDb.pools = (fromDb.pools || []).map((p) => {
              const hit = byIdx.get(Number(p.idx))
              if (!hit) return p
              return {
                ...p,
                do_saturation: hit.do_saturation != null ? hit.do_saturation : p.do_saturation,
                water_level: hit.water_level != null ? hit.water_level : p.water_level
              }
            })
            fromDb.extras = Object.assign({}, fromDb.extras || {}, live.extras || {})
            if (live.monitorMetrics) {
              fromDb.monitorMetrics = Object.assign({}, fromDb.monitorMetrics || {}, {
                do_saturation: live.monitorMetrics.do_saturation,
                water_level: live.monitorMetrics.water_level
              })
            }
          } catch (enrichErr) {
            console.warn('[fish/iot/water-live] enrich', enrichErr && enrichErr.message)
          }
          return res.json({ code: 0, data: fromDb })
        }
      } catch (dbErr) {
        console.warn('[fish/iot/water-live] db', dbErr && dbErr.message ? dbErr.message : dbErr)
      }
    }

    const latest = await getLatest(deviceId)
    const { live } = await persistFromLatest(latest, { writeHistory: true })
    res.json({ code: 0, data: { ...live, source: live.source || 'mentougou-hflrwl' } })
  } catch (err) {
    console.error('[fish/iot/water-live]', err && err.message ? err.message : err)
    res.status(502).json({
      code: 502,
      message: (err && err.message) || '门头沟水质实时拉取失败'
    })
  }
})

/**
 * 水质历史：优先读库采样点；不足再回退开放接口降采样
 */
router.get('/water-history', async (req, res) => {
  try {
    const hours = Math.min(168, Math.max(1, Number(req.query.hours) || 168))
    try {
      const fromDb = await loadMentougouHistoryFromDb(hours)
      // 有一定点数再认库（避免刚启动只有几条）
      if (fromDb && (fromDb.history || []).length >= 6) {
        return res.json({ code: 0, data: fromDb })
      }
    } catch (dbErr) {
      console.warn('[fish/iot/water-history] db', dbErr && dbErr.message ? dbErr.message : dbErr)
    }

    const deviceId = req.query.deviceId || DEVICE_ID
    const raw = await getPropertiesHistory(deviceId, { hours })
    const mapped = mapToWaterHistory(raw, {
      maxPoints: Math.max(96, Math.min(336, hours * 2))
    })
    res.json({ code: 0, data: mapped })
  } catch (err) {
    console.error('[fish/iot/water-history]', err && err.message ? err.message : err)
    res.status(502).json({
      code: 502,
      message: (err && err.message) || '门头沟水质历史拉取失败'
    })
  }
})

/** 采样器状态 / 手动采一针 */
router.get('/sampler/status', (req, res) => {
  res.json({ code: 0, data: getSamplerStatus() })
})

router.post('/sampler/tick', async (req, res) => {
  try {
    const out = await sampleOnce({ writeHistory: true })
    res.json({ code: 0, data: out })
  } catch (err) {
    res.status(502).json({ code: 502, message: (err && err.message) || '采样失败' })
  }
})

router.get('/config', (req, res) => {
  res.json({
    code: 0,
    data: {
      ...getConfig(),
      sampler: getSamplerStatus()
    }
  })
})

module.exports = router
