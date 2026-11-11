const express = require('express')
const {
  getRoles,
  listUsers,
  createUser,
  updateUser,
  deleteUser,
  listLogs
} = require('../data/permissions')

const router = express.Router()

router.get('/roles', (req, res) => {
  res.json({ code: 0, data: getRoles() })
})

router.get('/users', (req, res) => {
  res.json({ code: 0, data: listUsers() })
})

router.post('/users', (req, res) => {
  const { name, email, role, department } = req.body
  if (!name || !email || !role || !department) {
    return res.status(400).json({ code: 400, message: '请填写必填字段' })
  }
  const item = createUser(req.body)
  res.status(201).json({ code: 0, data: item })
})

router.put('/users/:id', (req, res) => {
  const item = updateUser(req.params.id, req.body)
  if (!item) {
    return res.status(404).json({ code: 404, message: '用户不存在' })
  }
  res.json({ code: 0, data: item })
})

router.delete('/users/:id', (req, res) => {
  const ok = deleteUser(req.params.id)
  if (!ok) {
    return res.status(404).json({ code: 404, message: '用户不存在' })
  }
  res.json({ code: 0, message: '删除成功' })
})

router.get('/logs', (req, res) => {
  res.json({ code: 0, data: listLogs() })
})

module.exports = router
