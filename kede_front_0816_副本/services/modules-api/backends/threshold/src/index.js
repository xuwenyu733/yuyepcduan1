const express = require('express')
const cors = require('cors')
const { createApp, bootstrap } = require('./createApp')

const app = express()
const PORT = process.env.PORT || 3000

app.use(cors())
app.use(express.json({ limit: '2mb' }))
app.use('/api', createApp())

app.use((err, req, res, next) => {
  console.error(err)
  res.status(500).json({ code: 500, message: err.message || '服务器错误' })
})

app.use((req, res) => {
  res.status(404).json({ code: 404, message: '接口不存在' })
})

async function start() {
  try {
    await bootstrap()
  } catch (err) {
    console.error('[threshold/pmis] bootstrap failed:', err.message || err)
    console.error('[threshold/pmis] server will still start; set PMIS_ENABLED=false to use local mock')
  }

  app.listen(PORT, () => {
    console.log(`Backend server running at http://localhost:${PORT}`)
  })
}

if (require.main === module) {
  start().catch(err => {
    console.error('Failed to start:', err)
    process.exit(1)
  })
}

module.exports = { app, start }
