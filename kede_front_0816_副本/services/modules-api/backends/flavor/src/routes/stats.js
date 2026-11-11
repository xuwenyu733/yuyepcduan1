const express = require('express')
const store = require('../data')
const router = express.Router()

router.get('/summary', async (req, res) => {
  res.json({ code: 0, data: await store.getStats(req.query) })
})

module.exports = router
