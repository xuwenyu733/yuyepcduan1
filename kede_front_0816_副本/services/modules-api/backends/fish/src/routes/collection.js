const express = require('express')
const store = require('../data')
const router = express.Router()

router.get('/monitor', async (req, res) => {
  try {
    const pondCode = req.query.pondCode || req.query.pond_code || ''
    res.json({ code: 0, data: await store.getMonitorData(pondCode) })
  } catch (err) {
    console.error('[monitor]', err)
    res.status(500).json({ code: 500, message: err.message || '监测数据读取失败' })
  }
})

router.get('/records', async (req, res) => {
  res.json({ code: 0, data: await store.listCollectionRecords() })
})

router.post('/records', async (req, res) => {
  if (!req.body.species) {
    return res.status(400).json({ code: 400, message: '请选择鱼品种' })
  }
  if (!req.body.pondCode) {
    return res.status(400).json({ code: 400, message: '请填写养殖池编号' })
  }
  res.status(201).json({ code: 0, data: await store.createCollectionRecord(req.body) })
})

module.exports = router
