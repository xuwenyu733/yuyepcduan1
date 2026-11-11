const express = require('express')
const store = require('../data')
const router = express.Router()

router.get('/summary', async (req, res) => {
  res.json({ code: 0, data: await store.getArchiveSummary() })
})

router.get('/categories', async (req, res) => {
  res.json({ code: 0, data: await store.getArchiveCategories() })
})

router.get('/cases', async (req, res) => {
  res.json({ code: 0, data: await store.listArchivedCases(req.query) })
})

router.get('/cases/:id', async (req, res) => {
  const item = await store.getArchivedCase(req.params.id)
  if (!item) return res.status(404).json({ code: 404, message: '案例不存在' })
  res.json({ code: 0, data: item })
})

module.exports = router
