const express = require('express')
const { getSummary } = require('../data/analysis')

const router = express.Router()

router.get('/summary', (req, res) => {
  res.json({ code: 0, data: getSummary(req.query) })
})

module.exports = router
