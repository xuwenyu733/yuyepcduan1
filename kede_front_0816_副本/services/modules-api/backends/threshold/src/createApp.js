const express = require('express')
const store = require('./data')
const { isEnabled, config } = require('./pmis/config')

const thresholdsRouter = require('./routes/thresholds')
const alarmsRouter = require('./routes/alarms')

/**
 * 可挂载路由（不含 listen）。
 * 独立运行：app.use('/api', createApp())
 * 统一 BFF：app.use('/api/threshold', createApp())
 */
function createApp() {
  const router = express.Router()

  router.get('/health', (req, res) => {
    res.json({
      code: 0,
      message: 'ok',
      data: {
        name: '阈值参数全局配置',
        module: 'threshold',
        pmisEnabled: isEnabled(),
        pmisBaseUrl: config.baseUrl
      }
    })
  })

  router.use('/thresholds', thresholdsRouter)
  router.use('/alarms', alarmsRouter)

  return router
}

async function bootstrap() {
  if (!isEnabled()) {
    console.log('[threshold/pmis] disabled, using local in-memory store')
    return { skipped: true }
  }
  const { bootstrapAll } = require('./pmis/bootstrap')
  console.log('[threshold/pmis] enabled, bootstrapping th_* tables (new only)...')
  const report = await bootstrapAll()
  console.log('[threshold/pmis] bootstrap done:', JSON.stringify(report, null, 2))
  return report
}

module.exports = { createApp, bootstrap, store }
