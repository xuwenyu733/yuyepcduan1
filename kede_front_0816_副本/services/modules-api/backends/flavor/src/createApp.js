const express = require('express')
const store = require('./data')
const { isEnabled, config } = require('./pmis/config')

const purchasesRouter = require('./routes/purchases')
const entriesRouter = require('./routes/entries')
const processRouter = require('./routes/process')
const statsRouter = require('./routes/stats')
const recordsRouter = require('./routes/records')
const configRouter = require('./routes/config')
const alarmsRouter = require('./routes/alarms')

/**
 * 可挂载路由（不含 listen）。
 * 独立运行：app.use('/api', createApp())
 * 统一 BFF：app.use('/api/flavor', createApp())
 */
function createApp() {
  const router = express.Router()

  router.get('/health', (req, res) => {
    res.json({
      code: 0,
      message: 'ok',
      data: {
        name: '风味优化管理系统',
        module: 'flavor',
        pmisEnabled: isEnabled(),
        pmisBaseUrl: config.baseUrl
      }
    })
  })

  router.get('/meta', async (req, res) => {
    res.json({ code: 0, data: await store.getMeta() })
  })

  router.use('/purchases', purchasesRouter)
  router.use('/entries', entriesRouter)
  router.use('/process', processRouter)
  router.use('/stats', statsRouter)
  router.use('/records', recordsRouter)
  router.use('/config', configRouter)
  router.use('/alarms', alarmsRouter)

  return router
}

async function bootstrap() {
  if (!isEnabled()) {
    console.log('[flavor/pmis] disabled, using local in-memory store')
    return { skipped: true }
  }
  const { bootstrapAll } = require('./pmis/bootstrap')
  console.log('[flavor/pmis] enabled, bootstrapping fo_* tables (new only)...')
  const report = await bootstrapAll()
  console.log('[flavor/pmis] bootstrap done:', JSON.stringify(report, null, 2))
  return report
}

module.exports = { createApp, bootstrap, store }
