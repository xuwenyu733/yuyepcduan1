const express = require('express')
const store = require('../data')
const router = express.Router()

router.get('/', async (req, res) => {
  res.json({ code: 0, data: await store.listPurchases() })
})

router.post('/', async (req, res) => {
  const {
    category,
    weight,
    unitPrice,
    unitWeight,
    manager,
    source,
    address,
    contact,
    phone,
    transportMethod
  } = req.body
  if (
    !category ||
    weight == null ||
    unitPrice == null ||
    !(Number(unitWeight) > 0) ||
    !manager ||
    !source ||
    !address ||
    !contact ||
    !phone ||
    !transportMethod
  ) {
    return res.status(400).json({ code: 400, message: '请填写必填字段（含单尾重>0）' })
  }
  res.status(201).json({ code: 0, data: await store.createPurchase(req.body) })
})

router.put('/:id', async (req, res) => {
  const item = await store.updatePurchase(req.params.id, req.body)
  if (!item) return res.status(404).json({ code: 404, message: '采购记录不存在' })
  res.json({ code: 0, data: item })
})

router.delete('/:id', async (req, res) => {
  const ok = await store.deletePurchase(req.params.id)
  if (!ok) return res.status(404).json({ code: 404, message: '采购记录不存在' })
  res.json({ code: 0, message: '删除成功' })
})

module.exports = router
