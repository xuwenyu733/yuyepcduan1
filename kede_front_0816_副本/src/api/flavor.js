import { Message } from 'element-ui'
import { flavorApi } from '@/api/modules'

function wrap(promise) {
  return promise.catch(err => {
    Message.error((err && err.message) || '请求失败')
    return Promise.reject(err)
  })
}

const get = (url, config) => wrap(flavorApi.get(url, config))
const post = (url, data, config) => wrap(flavorApi.post(url, data, config))
const put = (url, data, config) => wrap(flavorApi.put(url, data, config))
const del = (url, config) => wrap(flavorApi.delete(url, config))

export const fetchMeta = () => get('/meta')
export const fetchPurchases = () => get('/purchases')
export const createPurchase = data => post('/purchases', data)
export const updatePurchase = (id, data) => put(`/purchases/${id}`, data)
export const deletePurchase = id => del(`/purchases/${id}`)

export const fetchEntries = () => get('/entries')
export const createEntry = data => post('/entries', data)
export const updateEntry = (id, data) => put(`/entries/${id}`, data)
export const deleteEntry = id => del(`/entries/${id}`)

export const fetchOptimizing = () => get('/process/optimizing')
export const fetchReadyExit = () => get('/process/ready-exit')
export const fetchOptimizations = params => get('/process/optimizations', { params })
export const createOptimization = data => post('/process/optimizations', data)
export const exitEntry = data => post('/process/exit', data)

export const fetchStats = params => get('/stats/summary', { params })
export const fetchRecords = params => get('/records', { params })

export const fetchConfig = () => get('/config/overview')
export const updateUnits = data => put('/config/units', data)
export const updateDictionaries = data => put('/config/dictionaries', data)
export const fetchPonds = () => get('/config/ponds')
export const createPond = data => post('/config/ponds', data)
export const updatePond = (id, data) => put(`/config/ponds/${id}`, data)
export const deletePond = id => del(`/config/ponds/${id}`)

export const fetchAiAlarms = params => get('/alarms', { params })
export const createAiAlarm = data => post('/alarms', data)
export const ackAiAlarm = id => post(`/alarms/${id}/ack`)
