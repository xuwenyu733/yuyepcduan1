const express = require('express')
const store = require('../data')
const { issueTokenPair } = require('../auth/jwt')
const { authRequired, refreshRequired } = require('../auth/middleware')

const router = express.Router()

/* —— 注册与登录功能已注释关闭（恢复时取消本段注释，并删除下方 stub） ——
router.post('/login', async (req, res) => {
  const result = await store.loginUser({
    ...req.body,
    ip: req.ip || req.headers['x-forwarded-for'] || '127.0.0.1'
  })
  if (!result.ok) {
    return res.status(401).json({ code: 401, message: result.message })
  }

  const tokens = issueTokenPair(result.data.user)
  res.json({
    code: 0,
    message: '登录成功',
    data: {
      ...tokens,
      token: tokens.accessToken,
      user: result.data.user
    }
  })
})

router.post('/register', async (req, res) => {
  const result = await store.registerUser({
    ...req.body,
    ip: req.ip || req.headers['x-forwarded-for'] || '127.0.0.1'
  })
  if (!result.ok) {
    return res.status(400).json({ code: 400, message: result.message })
  }

  const tokens = issueTokenPair(result.data.user)
  res.status(201).json({
    code: 0,
    message: '注册成功',
    data: {
      ...tokens,
      token: tokens.accessToken,
      user: result.data.user
    }
  })
})
—— */

// stub：登录/注册关闭后接口直接拒绝，避免误用
router.post('/login', (req, res) => {
  res.status(403).json({ code: 403, message: '登录功能已关闭' })
})
router.post('/register', (req, res) => {
  res.status(403).json({ code: 403, message: '注册功能已关闭' })
})

router.post('/refresh', refreshRequired, (req, res) => {
  const tokens = issueTokenPair(req.user)
  res.json({
    code: 0,
    message: 'token 已刷新',
    data: {
      ...tokens,
      token: tokens.accessToken,
      user: req.user
    }
  })
})

router.get('/me', authRequired, (req, res) => {
  res.json({ code: 0, data: req.user })
})

router.get('/contacts', authRequired, async (req, res) => {
  res.json({ code: 0, data: await store.listChatContacts(req.user) })
})

router.post('/logout', authRequired, async (req, res) => {
  await store.addOperationLog({
    operator: req.user.username,
    action: '退出登录',
    ip: req.ip || '127.0.0.1',
    result: 'success'
  })
  res.json({ code: 0, message: '已退出登录' })
})

module.exports = router
