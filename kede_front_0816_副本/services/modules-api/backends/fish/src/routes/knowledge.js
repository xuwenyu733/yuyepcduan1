const express = require('express')
const store = require('../data')
const router = express.Router()

function sendStoreError(res, err) {
  const status = err && err.status ? err.status : 500
  const message = (err && err.message) || '知识库操作失败'
  return res.status(status).json({ code: status, message })
}

router.get('/', async (req, res) => {
  try {
    res.json({ code: 0, data: await store.listDiseaseKnowledge() })
  } catch (err) {
    sendStoreError(res, err)
  }
})

router.post('/', async (req, res) => {
  try {
    const item = await store.createDiseaseKnowledge(req.body || {})
    res.status(201).json({ code: 0, data: item })
  } catch (err) {
    sendStoreError(res, err)
  }
})

router.put('/:id', async (req, res) => {
  try {
    const id = decodeURIComponent(req.params.id || '')
    if (!id) return res.status(400).json({ code: 400, message: '缺少知识ID' })
    const item = await store.updateDiseaseKnowledge(id, req.body || {})
    if (!item) return res.status(404).json({ code: 404, message: '病害不存在或已被删除' })
    res.json({ code: 0, data: item })
  } catch (err) {
    sendStoreError(res, err)
  }
})

router.delete('/:id', async (req, res) => {
  try {
    const id = decodeURIComponent(req.params.id || '')
    if (!id) return res.status(400).json({ code: 400, message: '缺少知识ID' })
    const ok = await store.deleteDiseaseKnowledge(id)
    if (!ok) return res.status(404).json({ code: 404, message: '病害不存在或已被删除' })
    res.json({ code: 0, data: true })
  } catch (err) {
    sendStoreError(res, err)
  }
})

router.get('/:id', async (req, res) => {
  try {
    const id = decodeURIComponent(req.params.id || '')
    if (!id) return res.status(400).json({ code: 400, message: '缺少知识ID' })
    const item = await store.getKnowledge(id)
    if (!item) return res.status(404).json({ code: 404, message: '病害不存在或已被删除' })
    res.json({ code: 0, data: item })
  } catch (err) {
    sendStoreError(res, err)
  }
})

module.exports = router
