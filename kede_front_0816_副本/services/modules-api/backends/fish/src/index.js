const http = require('http')
const express = require('express')
const cors = require('cors')
const { createApp, bootstrap } = require('./createApp')
const { attachChatWebSocket } = require('./ws/chat')

const app = express()
const PORT = process.env.PORT || 3011

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
    console.error('[fish/pmis] bootstrap failed:', err.message || err)
    console.error('[fish/pmis] server will still start; set PMIS_ENABLED=false to use local mock')
  }

  const server = http.createServer(app)
  attachChatWebSocket(server)

  server.listen(PORT, () => {
    console.log(`Backend server running at http://localhost:${PORT}`)
    console.log(`WebSocket chat at ws://localhost:${PORT}/ws/chat`)
  })
}

if (require.main === module) {
  start().catch(err => {
    console.error('Failed to start server:', err)
    process.exit(1)
  })
}

module.exports = { app, start }
