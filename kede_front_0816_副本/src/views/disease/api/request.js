import axios from 'axios'
import { Message } from 'element-ui'
import {
  clearAuth,
  getAccessToken,
  getRefreshToken,
  setAuth,
  LOGIN_REGISTER_DISABLED,
  ensureGuestAuth
} from '@/utils/disease_auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

let isRefreshing = false
let pendingQueue = []

function flushQueue(error, token) {
  pendingQueue.forEach(({ resolve, reject, config }) => {
    if (error) {
      reject(error)
      return
    }
    config.headers.Authorization = `Bearer ${token}`
    resolve(request(config))
  })
  pendingQueue = []
}

function redirectToLogin() {
  // 登录功能已关闭：不再跳转登录页
  if (LOGIN_REGISTER_DISABLED) {
    ensureGuestAuth()
    return
  }
  clearAuth()
  const hash = window.location.hash || ''
  if (!hash.includes('/login')) {
    const redirect = hash.replace(/^#/, '') || '/config'
    window.location.hash = `#/login?redirect=${encodeURIComponent(redirect)}`
  }
}

request.interceptors.request.use(config => {
  if (LOGIN_REGISTER_DISABLED) ensureGuestAuth()
  const token = getAccessToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 0) {
      Message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  async error => {
    const { response, config } = error
    const status = response?.status
    const message = response?.data?.message || error.message || '网络错误'
    const url = config?.url || ''

    // 登录 / 刷新接口失败不走自动刷新
    const isAuthEndpoint =
      url.includes('/auth/login') ||
      url.includes('/auth/register') ||
      url.includes('/auth/refresh') ||
      url.includes('/system/login')

    if (LOGIN_REGISTER_DISABLED && status === 401) {
      ensureGuestAuth()
      Message.error(message)
      return Promise.reject(error)
    }

    if (status !== 401 || isAuthEndpoint || config.__isRetryRequest) {
      Message.error(message)
      if (status === 401 && isAuthEndpoint && url.includes('/refresh')) {
        redirectToLogin()
      }
      return Promise.reject(error)
    }

    const refreshToken = getRefreshToken()
    if (!refreshToken) {
      Message.error(LOGIN_REGISTER_DISABLED ? message : '登录已失效，请重新登录')
      redirectToLogin()
      return Promise.reject(error)
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        pendingQueue.push({ resolve, reject, config })
      })
    }

    isRefreshing = true
    config.__isRetryRequest = true

    try {
      const refreshRes = await axios.post('/api/auth/refresh', { refreshToken })
      const data = refreshRes.data
      if (data.code !== 0) {
        throw new Error(data.message || '刷新失败')
      }
      setAuth(data.data)
      const newToken = data.data.accessToken || data.data.token
      flushQueue(null, newToken)
      config.headers.Authorization = `Bearer ${newToken}`
      return request(config)
    } catch (refreshError) {
      flushQueue(refreshError)
      Message.error(LOGIN_REGISTER_DISABLED ? '请求失败' : '登录已过期，请重新登录')
      redirectToLogin()
      return Promise.reject(refreshError)
    } finally {
      isRefreshing = false
    }
  }
)

export default request
