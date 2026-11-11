const express = require('express')
const store = require('../data')
const router = express.Router()

router.get('/', async (req, res) => {
  res.json({ code: 0, data: await store.listEntries() })
})

router.post('/', async (req, res) => {
  const { category, pondCode, totalWeight, purchaseId, unitWeight } = req.body
  if (!category || !pondCode || totalWeight == null || !purchaseId) {
    return res.status(400).json({ code: 400, message: '请填写必填字段（含关联采购记录）' })
  }
  if (!(Number(unitWeight) > 0)) {
    return res.status(400).json({ code: 400, message: '单尾重须大于 0' })
  }
  res.status(201).json({ code: 0, data: await store.createEntry(req.body) })
})

router.put('/:id', async (req, res) => {
  const item = await store.updateEntry(req.params.id, req.body)
  if (!item) return res.status(404).json({ code: 404, message: '入池记录不存在' })
  res.json({ code: 0, data: item })
})

router.delete('/:id', async (req, res) => {
  const ok = await store.deleteEntry(req.params.id)
  if (!ok) return res.status(404).json({ code: 404, message: '入池记录不存在' })
  res.json({ code: 0, message: '删除成功' })
})

module.exports = router
