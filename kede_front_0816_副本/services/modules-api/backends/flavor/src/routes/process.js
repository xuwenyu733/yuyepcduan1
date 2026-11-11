const express = require('express')
const store = require('../data')
const router = express.Router()

router.get('/optimizing', async (req, res) => {
  res.json({ code: 0, data: await store.listOptimizing() })
})

router.get('/ready-exit', async (req, res) => {
  res.json({ code: 0, data: await store.listReadyToExit() })
})

router.get('/optimizations', async (req, res) => {
  res.json({ code: 0, data: await store.listOptimizations(req.query.entryId) })
})

router.post('/optimizations', async (req, res) => {
  const item = await store.createOptimization(req.body)
  if (!item) return res.status(404).json({ code: 404, message: '入池记录不存在' })
  res.status(201).json({ code: 0, data: item })
})

router.post('/exit', async (req, res) => {
  const item = await store.exitEntry(req.body)
  if (!item) return res.status(404).json({ code: 404, message: '记录不存在' })
  res.json({ code: 0, data: item })
})

module.exports = router
