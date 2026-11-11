const express = require('express')
const { listRecords, createRecord } = require('../data/collection')
const { getExperimentById } = require('../data/experiments')

const router = express.Router()

router.get('/', (req, res) => {
  res.json({ code: 0, data: listRecords(req.query) })
})

router.post('/', (req, res) => {
  const { experimentId, data } = req.body
  if (!experimentId) {
    return res.status(400).json({ code: 400, message: '请选择实验' })
  }
  const exp = getExperimentById(experimentId)
  if (!exp) {
    return res.status(404).json({ code: 404, message: '实验不存在' })
  }
  const item = createRecord({
    experimentId,
    experimentName: exp.name,
    data,
    operator: req.body.operator
  })
  res.status(201).json({ code: 0, data: item })
})

module.exports = router
