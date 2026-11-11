const path = require('path')
const fs = require('fs')
const http = require('http')
const express = require('express')
const cors = require('cors')

// 必须在 createApp / 路由注册之前：捕获 async 路由 reject，避免进程崩溃
require('./express-async')

// 轻量加载 .env（无强制 dotenv 依赖）
;(function loadEnv() {
  const envPath = path.join(__dirname, '..', '.env')
  if (!fs.existsSync(envPath)) return
  fs.readFileSync(envPath, 'utf8')
    .split('\n')
    .forEach(line => {
      const s = line.trim()
      if (!s || s.startsWith('#')) return
      const i = s.indexOf('=')
      if (i <= 0) return
      const key = s.slice(0, i).trim()
      const val = s.slice(i + 1).trim()
      if (!(key in process.env)) process.env[key] = val
    })
})()

// 模块后端已内置在本仓库 services/modules-api/backends/
const BACKENDS = path.join(__dirname, '..', 'backends')
const fishDir = path.join(BACKENDS, 'fish', 'src')
const flavorDir = path.join(BACKENDS, 'flavor', 'src')
const thresholdDir = path.join(BACKENDS, 'threshold', 'src')
const experimentDir = path.join(BACKENDS, 'experiment', 'src')

const { createApp: createFishApp, bootstrap: bootstrapFish } = require(path.join(fishDir, 'createApp.js'))
const { createApp: createFlavorApp, bootstrap: bootstrapFlavor } = require(path.join(flavorDir, 'createApp.js'))
const {
  createApp: createThresholdApp,
  bootstrap: bootstrapThreshold
} = require(path.join(thresholdDir, 'createApp.js'))
const {
  createApp: createExperimentApp,
  bootstrap: bootstrapExperiment
} = require(path.join(experimentDir, 'createApp.js'))
const { attachChatWebSocket } = require(path.join(fishDir, 'ws', 'chat.js'))

const app = express()
const PORT = Number(process.env.PORT || 3020)

app.use(cors())
app.use(express.json({ limit: '2mb' }))

app.get('/api/health', (req, res) => {
  res.json({
    code: 0,
    message: 'ok',
    data: {
      name: 'aquaculture-modules-api',
      location: 'kede_fromt/services/modules-api',
      port: PORT,
      modules: ['fish', 'flavor', 'threshold', 'experiment'],
      paths: {
        fish: '/api/fish',
        flavor: '/api/flavor',
        threshold: '/api/threshold',
        experiment: '/api/experiment',
        fishWs: '/ws/chat'
      }
    }
  })
})

// 命名空间挂载，避免 /alarms、/config 冲突
app.use('/api/fish', createFishApp())
app.use('/api/flavor', createFlavorApp())
app.use('/api/threshold', createThresholdApp())
app.use('/api/experiment', createExperimentApp())

app.use((err, req, res, next) => {
  console.error('[modules-api]', err && err.stack ? err.stack : err)
  if (res.headersSent) return next(err)
  res.status(500).json({ code: 500, message: (err && err.message) || '服务器错误' })
})

app.use((req, res) => {
  res.status(404).json({ code: 404, message: '接口不存在', path: req.path })
})

// 防止鱼病/PMIS 异步异常把整个 BFF 打挂
process.on('unhandledRejection', err => {
  console.error('[modules-api] unhandledRejection:', err && err.stack ? err.stack : err)
})
process.on('uncaughtException', err => {
  console.error('[modules-api] uncaughtException:', err && err.stack ? err.stack : err)
})

async function runBootstrap(name, fn) {
  try {
    await fn()
  } catch (err) {
    console.error(`[${name}/pmis] bootstrap failed:`, err.message || err)
  }
}

async function start() {
  // 先 listen，再异步 bootstrap：避免 PMIS 慢/挂导致 3020 起不来、整条 IoT 实时链路失效
  const server = http.createServer(app)
  attachChatWebSocket(server)

  await new Promise((resolve, reject) => {
    server.listen(PORT, () => {
      console.log(`[modules-api] http://localhost:${PORT}`)
      console.log(`[modules-api] fish       /api/fish/*`)
      console.log(`[modules-api] flavor     /api/flavor/*`)
      console.log(`[modules-api] threshold  /api/threshold/*`)
      console.log(`[modules-api] experiment /api/experiment/*`)
      console.log(`[modules-api] fish ws    ws://localhost:${PORT}/ws/chat`)
      resolve()
    })
    server.on('error', reject)
  })

  // 后台 bootstrap，不阻塞 IoT / health
  Promise.resolve()
    .then(() => runBootstrap('fish', bootstrapFish))
    .then(() => runBootstrap('flavor', bootstrapFlavor))
    .then(() => runBootstrap('threshold', bootstrapThreshold))
    .then(() => runBootstrap('experiment', bootstrapExperiment))
    .catch((err) => console.error('[modules-api] bootstrap chain', err))
}

if (require.main === module) {
  start().catch(err => {
    console.error('Failed to start modules-api:', err)
    process.exit(1)
  })
}

module.exports = { app, start }
