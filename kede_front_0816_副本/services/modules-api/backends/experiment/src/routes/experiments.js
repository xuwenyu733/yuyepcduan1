const express = require('express')
const {
  getStatusMap,
  listExperiments,
  getExperimentById,
  createExperiment,
  updateExperiment,
  deleteExperiment
} = require('../data/experiments')

const router = express.Router()

router.get('/meta', (req, res) => {
  res.json({
    code: 0,
    data: {
      statusMap: getStatusMap()
    }
  })
})

router.get('/', (req, res) => {
  const { keyword, status, category, sort } = req.query
  res.json({
    code: 0,
    data: listExperiments({ keyword, status, category, sort })
  })
})

router.get('/:id', (req, res) => {
  const item = getExperimentById(req.params.id)
  if (!item) {
    return res.status(404).json({ code: 404, message: '实验不存在' })
  }
  res.json({ code: 0, data: item })
})

router.post('/', (req, res) => {
  const { name, manager, startDate, endDate, species, poolNo, seedSource, goal, content, method } = req.body
  if (!name || !manager || !startDate || !endDate || !species || !poolNo || !seedSource || !goal || !content || !method) {
    return res.status(400).json({ code: 400, message: '请填写必填字段' })
  }
  try {
    const item = createExperiment(req.body)
    res.status(201).json({ code: 0, data: item })
  } catch (err) {
    return res.status(400).json({ code: 400, message: err.message || '创建失败' })
  }
})

router.put('/:id', (req, res) => {
  const item = updateExperiment(req.params.id, req.body)
  if (!item) {
    return res.status(404).json({ code: 404, message: '实验不存在' })
  }
  res.json({ code: 0, data: item })
})

router.delete('/:id', (req, res) => {
  const ok = deleteExperiment(req.params.id)
  if (!ok) {
    return res.status(404).json({ code: 404, message: '实验不存在' })
  }
  res.json({ code: 0, message: '删除成功' })
})

module.exports = router
