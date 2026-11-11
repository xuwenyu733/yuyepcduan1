const express = require('express')
const cors = require('cors')
const { createApp, bootstrap } = require('./createApp')

const app = express()
const PORT = process.env.PORT || 3010

app.use(cors())
app.use(express.json())
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
    console.error('[flavor/pmis] bootstrap failed:', err.message || err)
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
