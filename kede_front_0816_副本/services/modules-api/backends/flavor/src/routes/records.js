const express = require('express')
const store = require('../data')
const router = express.Router()

router.get('/', async (req, res) => {
  res.json({ code: 0, data: await store.queryRecords(req.query) })
})

module.exports = router
