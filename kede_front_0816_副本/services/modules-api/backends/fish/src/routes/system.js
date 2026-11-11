const express = require('express')
const store = require('../data')
const { authRequired, requireRoles } = require('../auth/middleware')

const router = express.Router()

router.use(authRequired)

router.get('/users', requireRoles('admin'), async (req, res) => {
  res.json({ code: 0, data: await store.listSystemUsers() })
})

router.post('/users', requireRoles('admin'), async (req, res) => {
  if (!req.body.username) {
    return res.status(400).json({ code: 400, message: '请填写用户名' })
  }
  res.status(201).json({ code: 0, data: await store.createSystemUser(req.body) })
})

router.put('/users/:id', requireRoles('admin'), async (req, res) => {
  const item = await store.updateSystemUser(req.params.id, req.body)
  if (!item) return res.status(404).json({ code: 404, message: '用户不存在' })
  res.json({ code: 0, data: item })
})

router.delete('/users/:id', requireRoles('admin'), async (req, res) => {
  const ok = await store.deleteSystemUser(req.params.id)
  if (!ok) return res.status(404).json({ code: 404, message: '用户不存在' })
  res.json({ code: 0, message: '已删除' })
})

router.get('/logs', requireRoles('admin'), async (req, res) => {
  res.json({ code: 0, data: await store.listOperationLogs(req.query) })
})

router.get('/settings', requireRoles('admin'), async (req, res) => {
  res.json({ code: 0, data: await store.getSystemSettings() })
})

router.put('/settings', requireRoles('admin'), async (req, res) => {
  res.json({ code: 0, data: await store.updateSystemSettings(req.body) })
})

module.exports = router
