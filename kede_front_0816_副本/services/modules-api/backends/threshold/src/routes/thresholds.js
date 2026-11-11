const express = require('express')
const store = require('../data')

const router = express.Router()

router.get('/nav', async (req, res) => {
  try {
    res.json({ code: 0, data: await store.getNav() })
  } catch (err) {
    res.status(500).json({ code: 500, message: err.message || '读取导航失败' })
  }
})

router.get('/meta', async (req, res) => {
  try {
    res.json({ code: 0, data: await store.getMeta() })
  } catch (err) {
    res.status(500).json({ code: 500, message: err.message || '读取元信息失败' })
  }
})

router.get('/all', async (req, res) => {
  try {
    res.json({ code: 0, data: await store.getAll() })
  } catch (err) {
    console.error('[thresholds/all]', err)
    res.status(500).json({ code: 500, message: err.message || '读取配置失败' })
  }
})

router.get('/existing', async (req, res) => {
  try {
    res.json({ code: 0, data: await store.listExistingThresholds() })
  } catch (err) {
    console.error('[thresholds/existing]', err)
    res.status(500).json({ code: 500, message: err.message || '读取既有阈值失败' })
  }
})

router.put('/', async (req, res) => {
  try {
    const data = await store.saveAll(req.body || {})
    res.json({ code: 0, message: '已保存所有配置', data })
  } catch (err) {
    console.error('[thresholds/saveAll]', err)
    res.status(500).json({ code: 500, message: err.message || '保存失败' })
  }
})

router.post('/reset', async (req, res) => {
  try {
    const data = await store.resetAll()
    res.json({ code: 0, message: '已恢复默认配置', data })
  } catch (err) {
    console.error('[thresholds/reset]', err)
    res.status(500).json({ code: 500, message: err.message || '重置失败' })
  }
})

router.get('/:moduleId', async (req, res) => {
  try {
    const data = await store.getModule(req.params.moduleId)
    if (!data) {
      return res.status(404).json({ code: 404, message: '模块不存在' })
    }
    res.json({ code: 0, data })
  } catch (err) {
    res.status(500).json({ code: 500, message: err.message || '读取模块失败' })
  }
})

router.put('/:moduleId', async (req, res) => {
  try {
    const data = await store.saveModule(req.params.moduleId, req.body)
    if (!data) {
      return res.status(404).json({ code: 404, message: '模块不存在' })
    }
    res.json({ code: 0, message: '已保存', data })
  } catch (err) {
    console.error('[thresholds/saveModule]', err)
    res.status(500).json({ code: 500, message: err.message || '保存失败' })
  }
})

module.exports = router
