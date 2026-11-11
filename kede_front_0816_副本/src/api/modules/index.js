/**
 * 模块统一 BFF 请求入口（方案 B）
 * - 默认使用相对路径 /modules-api（本地 vue 代理 / 远端 nginx 反代）
 * - 仅当 VUE_APP_MODULES_API 配成绝对地址时才直连（如 http://127.0.0.1:3020）
 * - 禁止在浏览器端写死 localhost，避免远程环境仍访问本机 3020
 */
import axios from 'axios'

export function resolveModulesBase() {
  let raw = process.env.VUE_APP_MODULES_API
  if (raw != null) {
    raw = String(raw).trim().replace(/^['"]|['"]$/g, '')
  }
  if (!raw) {
    raw = '/modules-api'
  }
  return raw.replace(/\/$/, '')
}

const modulesHttp = axios.create({
  baseURL: resolveModulesBase(),
  timeout: 30000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

modulesHttp.interceptors.response.use(
  res => {
    const payload = res.data
    // 三模块 BFF 约定：{ code: 0, data, message }
    if (payload && typeof payload.code !== 'undefined' && payload.code !== 0) {
      return Promise.reject(new Error(payload.message || '模块接口错误'))
    }
    return payload
  },
  err => {
    let msg =
      (err.response && err.response.data && err.response.data.message) ||
      err.message ||
      '网络错误'
    if (/timeout/i.test(msg)) msg = '模块接口请求超时，请稍后重试'
    else if (/network error/i.test(msg)) {
      msg = '无法连接模块服务（请确认 modules-api 已启动，或检查 VUE_APP_MODULES_API / 反代配置）'
    }
    return Promise.reject(new Error(msg))
  }
)

export const fishApi = {
  health: () => modulesHttp.get('/api/fish/health'),
  /** 门头沟 IoT 映射后的水质 live */
  getMentougouWaterLive: (params) =>
    modulesHttp.get('/api/fish/iot/water-live', { params, timeout: 15000 }),
  /** 门头沟 IoT 历史（BFF 已降采样；默认近 7 天） */
  getMentougouWaterHistory: (params) =>
    modulesHttp.get('/api/fish/iot/water-history', { params, timeout: 60000 }),
  getMentougouLatest: (params) =>
    modulesHttp.get('/api/fish/iot/latest', { params, timeout: 15000 }),
  get: (url, config) => modulesHttp.get(`/api/fish${url}`, config),
  post: (url, data, config) => modulesHttp.post(`/api/fish${url}`, data, config),
  put: (url, data, config) => modulesHttp.put(`/api/fish${url}`, data, config),
  delete: (url, config) => modulesHttp.delete(`/api/fish${url}`, config)
}

export const flavorApi = {
  health: () => modulesHttp.get('/api/flavor/health'),
  get: (url, config) => modulesHttp.get(`/api/flavor${url}`, config),
  post: (url, data, config) => modulesHttp.post(`/api/flavor${url}`, data, config),
  put: (url, data, config) => modulesHttp.put(`/api/flavor${url}`, data, config),
  delete: (url, config) => modulesHttp.delete(`/api/flavor${url}`, config)
}

export const thresholdApi = {
  health: () => modulesHttp.get('/api/threshold/health'),
  get: (url, config) => modulesHttp.get(`/api/threshold${url}`, config),
  post: (url, data, config) => modulesHttp.post(`/api/threshold${url}`, data, config),
  put: (url, data, config) => modulesHttp.put(`/api/threshold${url}`, data, config),
  delete: (url, config) => modulesHttp.delete(`/api/threshold${url}`, config)
}

export const experimentApi = {
  health: () => modulesHttp.get('/api/experiment/health'),
  get: (url, config) => modulesHttp.get(`/api/experiment${url}`, config),
  post: (url, data, config) => modulesHttp.post(`/api/experiment${url}`, data, config),
  put: (url, data, config) => modulesHttp.put(`/api/experiment${url}`, data, config),
  delete: (url, config) => modulesHttp.delete(`/api/experiment${url}`, config)
}

export default modulesHttp
