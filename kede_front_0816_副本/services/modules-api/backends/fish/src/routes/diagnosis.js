const express = require('express')
const store = require('../data')
const { asyncHandler } = require('../utils/asyncHandler')
const router = express.Router()

router.get(
  '/meta',
  asyncHandler(async (req, res) => {
    res.json({ code: 0, data: await store.getMeta() })
  })
)

router.get(
  '/summary',
  asyncHandler(async (req, res) => {
    res.json({ code: 0, data: await store.getSummary() })
  })
)

router.get(
  '/records',
  asyncHandler(async (req, res) => {
    res.json({ code: 0, data: await store.listDiagnosis(req.query) })
  })
)

router.get(
  '/records/:id',
  asyncHandler(async (req, res) => {
    const item = await store.getDiagnosis(req.params.id)
    if (!item) return res.status(404).json({ code: 404, message: '诊断记录不存在' })
    res.json({ code: 0, data: item })
  })
)

router.post(
  '/records',
  asyncHandler(async (req, res) => {
    const item = await store.createDiagnosis(req.body)
    res.status(201).json({ code: 0, data: item })
  })
)

router.put(
  '/records/:id',
  asyncHandler(async (req, res) => {
    const item = await store.updateDiagnosis(req.params.id, req.body)
    if (!item) return res.status(404).json({ code: 404, message: '诊断记录不存在' })
    res.json({ code: 0, data: item })
  })
)

router.delete(
  '/records/:id',
  asyncHandler(async (req, res) => {
    const ok = await store.deleteDiagnosis(req.params.id)
    if (!ok) return res.status(404).json({ code: 404, message: '诊断记录不存在' })
    res.json({ code: 0, message: '已删除' })
  })
)

router.post(
  '/diagnose',
  asyncHandler(async (req, res) => {
    res.json({ code: 0, data: await store.runDiagnosis(req.body) })
  })
)

router.post(
  '/records/:id/chat',
  asyncHandler(async (req, res) => {
    const item = await store.chatDiagnosis(req.params.id, req.body)
    if (!item) return res.status(404).json({ code: 404, message: '诊断记录不存在' })
    res.json({ code: 0, data: item })
  })
)

module.exports = router
