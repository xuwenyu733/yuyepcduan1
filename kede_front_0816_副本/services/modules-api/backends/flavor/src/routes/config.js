const express = require('express')
const store = require('../data')
const router = express.Router()

router.get('/overview', async (req, res) => {
  res.json({
    code: 0,
    data: {
      overview: await store.getConfigOverview(),
      meta: await store.getMeta(),
      ponds: await store.listPonds()
    }
  })
})

router.put('/units', async (req, res) => {
  res.json({ code: 0, data: await store.updateUnits(req.body) })
})

router.put('/dictionaries', async (req, res) => {
  res.json({ code: 0, data: await store.updateDictionaries(req.body) })
})

router.get('/ponds', async (req, res) => {
  res.json({ code: 0, data: await store.listPonds() })
})

router.post('/ponds', async (req, res) => {
  if (!req.body.code) {
    return res.status(400).json({ code: 400, message: '请填写鱼池编号' })
  }
  res.status(201).json({ code: 0, data: await store.createPond(req.body) })
})

router.put('/ponds/:id', async (req, res) => {
  const item = await store.updatePond(req.params.id, req.body)
  if (!item) return res.status(404).json({ code: 404, message: '鱼池不存在' })
  res.json({ code: 0, data: item })
})

router.delete('/ponds/:id', async (req, res) => {
  const ok = await store.deletePond(req.params.id)
  if (!ok) return res.status(404).json({ code: 404, message: '鱼池不存在' })
  res.json({ code: 0, message: '删除成功' })
})

module.exports = router
