const store = require('../data')
const { verifyAccessToken, verifyRefreshToken } = require('./jwt')

/** 与前端 LOGIN_REGISTER_DISABLED 对应：登录关闭时放行接口 */
const LOGIN_REGISTER_DISABLED = true

function extractBearer(req) {
  const header = req.headers.authorization || ''
  if (header.startsWith('Bearer ')) return header.slice(7).trim()
  return ''
}

function authError(res, status, message) {
  return res.status(status).json({ code: status, message })
}

async function resolveGuestUser() {
  const user = await store.getSystemUserById('U-004')
  if (user) return user
  return {
    id: 'U-004',
    username: '系统管理员',
    role: 'admin',
    roleLabel: '管理员',
    status: 'active',
    email: 'admin@system.com',
    department: '管理部'
  }
}

/**
 * 双认证：
 * 1. 校验 JWT access token 签名与类型
 * 2. 校验用户仍存在且状态为 active
 *
 * 登录/注册关闭时：无 token 或 token 无效 → 注入访客管理员，保证业务接口可用
 */
async function authRequired(req, res, next) {
  try {
    const token = extractBearer(req)

    if (LOGIN_REGISTER_DISABLED && (!token || token === 'guest-login-disabled')) {
      req.user = await resolveGuestUser()
      req.tokenPayload = { sub: req.user.id, type: 'guest' }
      return next()
    }

    if (!token) {
      return authError(res, 401, '未登录或 access token 缺失')
    }

    try {
      const payload = verifyAccessToken(token)
      const user = await store.getSystemUserById(payload.sub)
      if (!user) {
        return authError(res, 401, '用户不存在或已被删除')
      }
      if (user.status !== 'active') {
        return authError(res, 401, '账号已停用')
      }
      req.user = user
      req.tokenPayload = payload
      return next()
    } catch (e) {
      if (LOGIN_REGISTER_DISABLED) {
        req.user = await resolveGuestUser()
        req.tokenPayload = { sub: req.user.id, type: 'guest' }
        return next()
      }
      if (e.name === 'TokenExpiredError') {
        return authError(res, 401, 'access token 已过期')
      }
      return authError(res, 401, 'access token 无效')
    }
  } catch (err) {
    // 防止 async 中间件 reject 打崩进程
    console.error('[fish/auth]', err)
    if (LOGIN_REGISTER_DISABLED) {
      req.user = {
        id: 'U-004',
        username: '系统管理员',
        role: 'admin',
        roleLabel: '管理员',
        status: 'active'
      }
      req.tokenPayload = { sub: 'U-004', type: 'guest' }
      return next()
    }
    return next(err)
  }
}

async function refreshRequired(req, res, next) {
  if (LOGIN_REGISTER_DISABLED) {
    req.user = await resolveGuestUser()
    req.refreshToken = ''
    req.tokenPayload = { sub: req.user.id, type: 'guest' }
    return next()
  }

  const token = req.body.refreshToken || extractBearer(req)
  if (!token) {
    return authError(res, 401, 'refresh token 缺失')
  }

  try {
    const payload = verifyRefreshToken(token)
    const user = await store.getSystemUserById(payload.sub)
    if (!user) {
      return authError(res, 401, '用户不存在或已被删除')
    }
    if (user.status !== 'active') {
      return authError(res, 401, '账号已停用')
    }
    req.user = user
    req.refreshToken = token
    req.tokenPayload = payload
    next()
  } catch (e) {
    if (e.name === 'TokenExpiredError') {
      return authError(res, 401, 'refresh token 已过期，请重新登录')
    }
    return authError(res, 401, 'refresh token 无效')
  }
}

function requireRoles(...roles) {
  return (req, res, next) => {
    if (!req.user) {
      return authError(res, 401, '未登录')
    }
    if (!roles.includes(req.user.role)) {
      return authError(res, 403, '无权限访问')
    }
    next()
  }
}

module.exports = {
  authRequired,
  refreshRequired,
  requireRoles,
  extractBearer
}
