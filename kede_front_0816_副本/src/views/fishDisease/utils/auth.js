const ACCESS_KEY = 'fish_disease_access_token'
const REFRESH_KEY = 'fish_disease_refresh_token'
const USER_KEY = 'fish_disease_user'
// 兼容旧版单 token
const LEGACY_TOKEN_KEY = 'fish_disease_token'

/** 注册与登录功能已暂时关闭（恢复时改为 false，并取消相关注释） */
export const LOGIN_REGISTER_DISABLED = true

/** 关闭登录后的访客身份（仅前端展示；后端中间件同步放行） */
const GUEST_USER = {
  id: 'U-004',
  username: '系统管理员',
  role: 'admin',
  roleLabel: '管理员',
  status: 'active',
  email: 'admin@system.com',
  department: '管理部'
}

export function getAccessToken() {
  return localStorage.getItem(ACCESS_KEY) || localStorage.getItem(LEGACY_TOKEN_KEY) || ''
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_KEY) || ''
}

/** @deprecated 使用 getAccessToken */
export function getToken() {
  return getAccessToken()
}

export function getUser() {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch (e) {
    return null
  }
}

export function setAuth(payload = {}) {
  const accessToken = payload.accessToken || payload.token || ''
  const refreshToken = payload.refreshToken || ''
  const user = payload.user

  if (accessToken) {
    localStorage.setItem(ACCESS_KEY, accessToken)
    localStorage.setItem(LEGACY_TOKEN_KEY, accessToken)
  }
  if (refreshToken) {
    localStorage.setItem(REFRESH_KEY, refreshToken)
  }
  if (user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  }
}

export function clearAuth() {
  localStorage.removeItem(ACCESS_KEY)
  localStorage.removeItem(REFRESH_KEY)
  localStorage.removeItem(USER_KEY)
  localStorage.removeItem(LEGACY_TOKEN_KEY)
}

/** 登录关闭时注入访客，避免页面被拦 */
export function ensureGuestAuth() {
  if (!LOGIN_REGISTER_DISABLED) return
  if (getUser() && getAccessToken()) return
  setAuth({
    accessToken: 'guest-login-disabled',
    refreshToken: '',
    user: GUEST_USER
  })
}

export function isLoggedIn() {
  if (LOGIN_REGISTER_DISABLED) {
    ensureGuestAuth()
    return true
  }
  return !!(getAccessToken() && getUser())
}

export function hasRole(...roles) {
  if (LOGIN_REGISTER_DISABLED) {
    ensureGuestAuth()
  }
  const user = getUser()
  if (!user) return false
  return roles.includes(user.role)
}

export function getDisplayName(user = getUser()) {
  if (!user) return '未登录'
  return user.roleLabel || user.username || '用户'
}

export function getAvatarText(user = getUser()) {
  const name = getDisplayName(user)
  return name.slice(0, 1)
}
