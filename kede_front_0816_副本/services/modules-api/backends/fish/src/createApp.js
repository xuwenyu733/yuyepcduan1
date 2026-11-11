const express = require('express')
const store = require('./data')
const { isEnabled, config } = require('./pmis/config')
const { authRequired } = require('./auth/middleware')
const { asyncHandler } = require('./utils/asyncHandler')

const authRouter = require('./routes/auth')
const diagnosisRouter = require('./routes/diagnosis')
const knowledgeRouter = require('./routes/knowledge')
const configRouter = require('./routes/config')
const collectionRouter = require('./routes/collection')
const archiveRouter = require('./routes/archive')
const systemRouter = require('./routes/system')
const alarmsRouter = require('./routes/alarms')
const iotRouter = require('./routes/iot')

/**
 * 可挂载路由（不含 listen / WebSocket）。
 * 独立运行：app.use('/api', createApp())
 * 统一 BFF：app.use('/api/fish', createApp())
 */
function createApp() {
  const router = express.Router()

  router.get('/health', (req, res) => {
    res.json({
      code: 0,
      message: 'ok',
      data: {
        name: '鱼病诊断管理系统',
        module: 'fish',
        pmisEnabled: isEnabled(),
        pmisBaseUrl: config.baseUrl
      }
    })
  })

  router.use('/auth', authRouter)

  // 门头沟 IoT：水质页 / 大屏直拉，不经鱼病 JWT（上游自有 token）
  router.use('/iot', iotRouter)

  router.get(
    '/meta',
    authRequired,
    asyncHandler(async (req, res) => {
      res.json({ code: 0, data: await store.getMeta() })
    })
  )

  router.use('/diagnosis', authRequired, diagnosisRouter)
  router.use('/knowledge', authRequired, knowledgeRouter)
  router.use('/config', authRequired, configRouter)
  router.use('/collection', authRequired, collectionRouter)
  router.use('/archive', authRequired, archiveRouter)
  router.use('/alarms', authRequired, alarmsRouter)
  router.use('/system', systemRouter)

  return router
}

async function bootstrap() {
  if (!isEnabled()) {
    console.log('[fish/pmis] disabled, using local in-memory store')
    return { skipped: true }
  }
  const { bootstrapAll } = require('./pmis/bootstrap')
  console.log('[fish/pmis] enabled, bootstrapping fd_* tables (new only, never alter existing)...')
  const report = await bootstrapAll()
  console.log('[fish/pmis] bootstrap done:', JSON.stringify(report, null, 2))
  return report
}

module.exports = { createApp, bootstrap, store }
