const express = require('express')
const store = require('../data')
const router = express.Router()

router.get('/', async (req, res) => {
  try {
    res.json({ code: 0, data: await store.listAiAlarms(req.query) })
  } catch (err) {
    console.error('[alarms]', err)
    res.status(500).json({ code: 500, message: err.message || 'AI报警列表读取失败' })
  }
})

router.post('/', async (req, res) => {
  try {
    const item = await store.createAiAlarm(req.body || {})
    res.status(201).json({ code: 0, data: item })
  } catch (err) {
    console.error('[alarms:create]', err)
    res.status(500).json({ code: 500, message: err.message || '写入AI报警失败' })
  }
})

router.post('/:id/ack', async (req, res) => {
  try {
    const item = await store.ackAiAlarm(req.params.id)
    if (!item) return res.status(404).json({ code: 404, message: '报警不存在' })
    res.json({ code: 0, data: item })
  } catch (err) {
    console.error('[alarms:ack]', err)
    res.status(500).json({ code: 500, message: err.message || '确认报警失败' })
  }
})

module.exports = router
