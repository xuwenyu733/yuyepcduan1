const express = require('express')
const cors = require('cors')
const { createApp, bootstrap } = require('./createApp')

const app = express()
const PORT = process.env.PORT || 3000

app.use(cors())
app.use(express.json())
app.use('/api', createApp())

app.use((req, res) => {
  res.status(404).json({ code: 404, message: '接口不存在' })
})

async function start() {
  await bootstrap()
  app.listen(PORT, () => {
    console.log(`Backend server running at http://localhost:${PORT}`)
  })
}

if (require.main === module) {
  start().catch(err => {
    console.error('Failed to start experiment backend:', err)
    process.exit(1)
  })
}

module.exports = { app, start, createApp, bootstrap }
