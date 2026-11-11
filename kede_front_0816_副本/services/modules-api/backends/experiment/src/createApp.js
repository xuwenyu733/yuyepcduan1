const express = require('express')

const experimentsRouter = require('./routes/experiments')
const templatesRouter = require('./routes/templates')
const collectionRouter = require('./routes/collection')
const mediaRouter = require('./routes/media')
const analysisRouter = require('./routes/analysis')
const permissionsRouter = require('./routes/permissions')

/**
 * 可挂载路由（不含 listen）。
 * 独立运行：app.use('/api', createApp())
 * 统一 BFF：app.use('/api/experiment', createApp())
 */
function createApp() {
  const router = express.Router()

  router.get('/health', (req, res) => {
    res.json({
      code: 0,
      message: 'ok',
      data: {
        name: '养殖实验数据管理系统',
        module: 'experiment',
        store: 'memory'
      }
    })
  })

  router.use('/experiments', experimentsRouter)
  router.use('/templates', templatesRouter)
  router.use('/collection', collectionRouter)
  router.use('/media', mediaRouter)
  router.use('/analysis', analysisRouter)
  router.use('/permissions', permissionsRouter)

  return router
}

async function bootstrap() {
  console.log('[experiment] using in-memory mock store')
  return { skipped: true }
}

module.exports = { createApp, bootstrap }
