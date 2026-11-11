const express = require('express')
const store = require('../data')
const router = express.Router()

function handleWriteError(res, err) {
  if (err && err.code === 403) {
    return res.status(403).json({ code: 403, message: err.message })
  }
  console.error(err)
  return res.status(500).json({ code: 500, message: err.message || '服务器错误' })
}

router.get('/ponds', async (req, res) => {
  res.json({ code: 0, data: await store.listPonds() })
})

router.post('/ponds', async (req, res) => {
  try {
    if (!req.body.code) {
      return res.status(400).json({ code: 400, message: '请填写鱼池编号' })
    }
    const item = await store.createPond(req.body)
    res.status(201).json({ code: 0, data: item })
  } catch (err) {
    handleWriteError(res, err)
  }
})

router.put('/ponds/:id', async (req, res) => {
  try {
    const item = await store.updatePond(req.params.id, req.body)
    if (!item) return res.status(404).json({ code: 404, message: '鱼池不存在' })
    res.json({ code: 0, data: item })
  } catch (err) {
    handleWriteError(res, err)
  }
})

router.delete('/ponds/:id', async (req, res) => {
  try {
    const ok = await store.deletePond(req.params.id)
    if (!ok) return res.status(404).json({ code: 404, message: '鱼池不存在' })
    res.json({ code: 0, message: '删除成功' })
  } catch (err) {
    handleWriteError(res, err)
  }
})

router.get('/species', async (req, res) => {
  res.json({ code: 0, data: await store.listFishSpecies() })
})

router.post('/species', async (req, res) => {
  if (!req.body.name) {
    return res.status(400).json({ code: 400, message: '请填写品种名称' })
  }
  res.status(201).json({ code: 0, data: await store.createFishSpecies(req.body) })
})

router.put('/species/:id', async (req, res) => {
  const item = await store.updateFishSpecies(req.params.id, req.body)
  if (!item) return res.status(404).json({ code: 404, message: '品种不存在' })
  res.json({ code: 0, data: item })
})

router.delete('/species/:id', async (req, res) => {
  const ok = await store.deleteFishSpecies(req.params.id)
  if (!ok) return res.status(404).json({ code: 404, message: '品种不存在' })
  res.json({ code: 0, message: '删除成功' })
})

router.get('/disease-types', async (req, res) => {
  res.json({ code: 0, data: await store.listDiseaseTypes() })
})

router.post('/disease-types', async (req, res) => {
  if (!req.body.name) {
    return res.status(400).json({ code: 400, message: '请填写病害类型名称' })
  }
  res.status(201).json({ code: 0, data: await store.createDiseaseType(req.body) })
})

router.put('/disease-types/:id', async (req, res) => {
  const item = await store.updateDiseaseType(req.params.id, req.body)
  if (!item) return res.status(404).json({ code: 404, message: '病害类型不存在' })
  res.json({ code: 0, data: item })
})

router.delete('/disease-types/:id', async (req, res) => {
  const ok = await store.deleteDiseaseType(req.params.id)
  if (!ok) return res.status(404).json({ code: 404, message: '病害类型不存在' })
  res.json({ code: 0, message: '删除成功' })
})

router.get('/equipments', async (req, res) => {
  res.json({ code: 0, data: await store.listEquipments() })
})

router.post('/equipments', async (req, res) => {
  if (!req.body.code) {
    return res.status(400).json({ code: 400, message: '请填写设备编号' })
  }
  res.status(201).json({ code: 0, data: await store.createEquipment(req.body) })
})

router.put('/equipments/:id', async (req, res) => {
  const item = await store.updateEquipment(req.params.id, req.body)
  if (!item) return res.status(404).json({ code: 404, message: '设备不存在' })
  res.json({ code: 0, data: item })
})

router.delete('/equipments/:id', async (req, res) => {
  const ok = await store.deleteEquipment(req.params.id)
  if (!ok) return res.status(404).json({ code: 404, message: '设备不存在' })
  res.json({ code: 0, message: '删除成功' })
})

module.exports = router
