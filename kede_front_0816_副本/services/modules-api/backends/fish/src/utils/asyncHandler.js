/**
 * Express 4 不会自动捕获 async 路由里的 Promise reject，
 * 未处理时可能导致 Node 进程异常退出。
 */
function asyncHandler(fn) {
  return function wrapped(req, res, next) {
    Promise.resolve(fn(req, res, next)).catch(next)
  }
}

module.exports = { asyncHandler }
