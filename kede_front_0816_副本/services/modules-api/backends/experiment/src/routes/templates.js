const express = require('express')
const {
  getTemplateTypes,
  getPresetFields,
  listTemplates,
  createTemplate,
  copyTemplate,
  deleteTemplate
} = require('../data/templates')

const router = express.Router()

router.get('/types', (req, res) => {
  res.json({ code: 0, data: getTemplateTypes() })
})

router.get('/presets/:type', (req, res) => {
  const fields = getPresetFields(req.params.type)
  if (!fields.length) {
    return res.status(404).json({ code: 404, message: '模板类型不存在' })
  }
  res.json({ code: 0, data: fields })
})

router.get('/', (req, res) => {
  res.json({ code: 0, data: listTemplates() })
})

router.post('/', (req, res) => {
  const { type } = req.body
  if (!type) {
    return res.status(400).json({ code: 400, message: '请选择模板类型' })
  }
  const item = createTemplate(req.body)
  if (!item) {
    return res.status(400).json({ code: 400, message: '模板类型无效' })
  }
  res.status(201).json({ code: 0, data: item })
})

router.post('/:id/copy', (req, res) => {
  const item = copyTemplate(req.params.id)
  if (!item) {
    return res.status(404).json({ code: 404, message: '模板不存在' })
  }
  res.status(201).json({ code: 0, data: item })
})

router.delete('/:id', (req, res) => {
  const ok = deleteTemplate(req.params.id)
  if (!ok) {
    return res.status(404).json({ code: 404, message: '模板不存在' })
  }
  res.json({ code: 0, message: '删除成功' })
})

module.exports = router
