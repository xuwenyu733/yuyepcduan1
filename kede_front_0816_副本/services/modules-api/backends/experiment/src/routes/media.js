const express = require('express')
const {
  getMediaMeta,
  listMedia,
  createMedia,
  deleteMedia
} = require('../data/media')

const router = express.Router()

router.get('/meta', (req, res) => {
  res.json({ code: 0, data: getMediaMeta() })
})

router.get('/', (req, res) => {
  res.json({ code: 0, data: listMedia(req.query) })
})

router.post('/', (req, res) => {
  const { experimentId, fileName, fileType, category } = req.body
  if (!experimentId || !fileName || !fileType || !category) {
    return res.status(400).json({ code: 400, message: '请填写必填字段' })
  }
  const item = createMedia(req.body)
  res.status(201).json({ code: 0, data: item })
})

router.delete('/:id', (req, res) => {
  const ok = deleteMedia(req.params.id)
  if (!ok) {
    return res.status(404).json({ code: 404, message: '文件不存在' })
  }
  res.json({ code: 0, message: '删除成功' })
})

module.exports = router
