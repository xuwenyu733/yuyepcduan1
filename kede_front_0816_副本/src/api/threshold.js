/**
 * 阈值模块 API → 统一 BFF /api/threshold/*
 */
import modulesHttp from '@/api/modules'

function wrap(promise, { quiet = false } = {}) {
  return promise.catch(err => {
    const message = (err && err.message) || '请求失败'
    if (!quiet) {
      try {
        const { Message } = require('element-ui')
        Message.error(message)
      } catch (e) { /* ignore */ }
    }
    return Promise.reject(err)
  })
}

export function fetchNav() {
  return wrap(modulesHttp.get('/api/threshold/thresholds/nav'))
}

export function fetchMeta() {
  return wrap(modulesHttp.get('/api/threshold/thresholds/meta'))
}

export function fetchAll() {
  // 业务页后台拉取阈值时不弹窗，避免打断精准投喂等模块
  return wrap(modulesHttp.get('/api/threshold/thresholds/all'), { quiet: true })
}

export function fetchModule(moduleId) {
  return wrap(modulesHttp.get(`/api/threshold/thresholds/${moduleId}`), { quiet: true })
}

export function saveModule(moduleId, data) {
  return wrap(modulesHttp.put(`/api/threshold/thresholds/${moduleId}`, data))
}

export function saveAll(data) {
  return wrap(modulesHttp.put('/api/threshold/thresholds', data))
}

export function resetAll() {
  return wrap(modulesHttp.post('/api/threshold/thresholds/reset'))
}

/** 阈值 AI 报警 mock 列表 */
export function fetchAiAlarms(params) {
  return wrap(modulesHttp.get('/api/threshold/alarms', { params }))
}
