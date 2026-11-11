const express = require('express')
const { getLatest, getPropertiesHistory, getConfig, DEVICE_ID } = require('../iot/hflrwlClient')
const { mapToWaterLive } = require('../iot/mapWaterLive')
const { mapToWaterHistory } = require('../iot/mapWaterHistory')

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

/** 映射后的水质 live（给水质页 / 大屏） */
router.get('/water-live', async (req, res) => {
  try {
    const deviceId = req.query.deviceId || DEVICE_ID
    const latest = await getLatest(deviceId)
    const live = mapToWaterLive(latest)
    res.json({ code: 0, data: live })
    // 后台预热 7 天历史缓存，避免趋势图首屏干等上游 18k 点
    setImmediate(() => {
      getPropertiesHistory(deviceId, { hours: 168 }).catch((err) => {
        console.warn('[fish/iot/water-live] history warm', err && err.message ? err.message : err)
      })
    })
  } catch (err) {
    console.error('[fish/iot/water-live]', err && err.message ? err.message : err)
    res.status(502).json({
      code: 502,
      message: (err && err.message) || '门头沟水质实时拉取失败'
    })
  }
})

/**
 * 映射后的水质历史（降采样，供「历史数据及趋势」折线）
 * query: hours=24|72|168（默认 168，前端再按 24h/3d/7d 裁切）
 */
router.get('/water-history', async (req, res) => {
  try {
    const deviceId = req.query.deviceId || DEVICE_ID
    const hours = Math.min(168, Math.max(1, Number(req.query.hours) || 168))
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

router.get('/config', (req, res) => {
  res.json({ code: 0, data: getConfig() })
})

module.exports = router
