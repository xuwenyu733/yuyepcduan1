import { Message } from 'element-ui'
import { fishApi } from '@/api/modules'
import {
  ensureGuestAuth,
  getAccessToken,
  LOGIN_REGISTER_DISABLED
} from '@/views/fishDisease/utils/auth'

if (LOGIN_REGISTER_DISABLED) ensureGuestAuth()

function withAuth(config = {}) {
  if (LOGIN_REGISTER_DISABLED) ensureGuestAuth()
  const token = getAccessToken()
  const headers = { ...(config.headers || {}) }
  if (token) headers.Authorization = `Bearer ${token}`
  return { ...config, headers }
}

function friendlyError(err) {
  const raw =
    (err && err.response && err.response.data && err.response.data.message) ||
    (err && err.message) ||
    '请求失败'
  if (/timeout/i.test(raw)) return '模块接口请求超时，请稍后重试'
  if (/network error/i.test(raw)) return '无法连接模块服务（请确认 :3020 已启动）'
  return raw
}

/**
 * @param {Promise} promise
 * @param {{ silent?: boolean }} options silent=true 时不弹 Toast（页面加载类请求默认静默）
 */
function wrap(promise, options = {}) {
  const silent = options.silent === true
  return promise.catch(err => {
    const msg = friendlyError(err)
    if (!silent) Message.error(msg)
    const e = err instanceof Error ? err : new Error(msg)
    e.message = msg
    return Promise.reject(e)
  })
}

// GET 默认静默，避免与布局层/其它轮询叠弹；写操作仍提示
const get = (url, config = {}) => {
  const { silent = true, ...rest } = config
  return wrap(fishApi.get(url, withAuth(rest)), { silent })
}
const post = (url, data, config = {}) => {
  const { silent = false, ...rest } = config
  return wrap(fishApi.post(url, data, withAuth(rest)), { silent })
}
const put = (url, data, config = {}) => {
  const { silent = false, ...rest } = config
  return wrap(fishApi.put(url, data, withAuth(rest)), { silent })
}
const del = (url, config = {}) => {
  const { silent = false, ...rest } = config
  return wrap(fishApi.delete(url, withAuth(rest)), { silent })
}

export const fetchMeta = () => get('/meta')
export const fetchSummary = () => get('/diagnosis/summary')
export const fetchDiagnosisRecords = params => get('/diagnosis/records', { params })
export const fetchDiagnosisRecord = id => get(`/diagnosis/records/${id}`)
export const createDiagnosisRecord = data => post('/diagnosis/records', data)
export const updateDiagnosisRecord = (id, data) => put(`/diagnosis/records/${id}`, data)
export const deleteDiagnosisRecord = id => del(`/diagnosis/records/${id}`)
export const runDiagnosis = data => post('/diagnosis/diagnose', data)
export const chatDiagnosis = (id, data) => post(`/diagnosis/records/${id}/chat`, data)

export const fetchKnowledge = () => get('/knowledge')
export const fetchKnowledgeDetail = id => get(`/knowledge/${id}`)
export const createKnowledge = data => post('/knowledge', data)
export const updateKnowledge = (id, data) => put(`/knowledge/${id}`, data)
export const deleteKnowledge = id => del(`/knowledge/${id}`)

export const fetchPonds = () => get('/config/ponds')
export const createPond = data => post('/config/ponds', data)
export const updatePond = (id, data) => put(`/config/ponds/${id}`, data)
export const deletePond = id => del(`/config/ponds/${id}`)

export const fetchFishSpecies = () => get('/config/species')
export const createFishSpecies = data => post('/config/species', data)
export const updateFishSpecies = (id, data) => put(`/config/species/${id}`, data)
export const deleteFishSpecies = id => del(`/config/species/${id}`)

export const fetchDiseaseTypes = () => get('/config/disease-types')
export const createDiseaseType = data => post('/config/disease-types', data)
export const updateDiseaseType = (id, data) => put(`/config/disease-types/${id}`, data)
export const deleteDiseaseType = id => del(`/config/disease-types/${id}`)

export const fetchEquipments = () => get('/config/equipments')
export const createEquipment = data => post('/config/equipments', data)
export const updateEquipment = (id, data) => put(`/config/equipments/${id}`, data)
export const deleteEquipment = id => del(`/config/equipments/${id}`)

export const fetchMonitorData = (params = {}) => get('/collection/monitor', { params })
export const submitCollection = data => post('/collection/records', data)
export const fetchCollectionRecords = () => get('/collection/records')

export const fetchAiAlarms = params => get('/alarms', { params })
export const createAiAlarm = data => post('/alarms', data)
export const ackAiAlarm = id => post(`/alarms/${id}/ack`)

export const fetchArchiveSummary = () => get('/archive/summary')
export const fetchArchiveCategories = () => get('/archive/categories')
export const fetchArchivedCases = params => get('/archive/cases', { params })
export const fetchArchivedCase = id => get(`/archive/cases/${id}`)

export const login = data => post('/auth/login', data)
export const register = data => post('/auth/register', data)
export const refreshToken = data => post('/auth/refresh', data)
export const fetchProfile = () => get('/auth/me')
export const fetchChatContacts = () => get('/auth/contacts')
export const logout = () => post('/auth/logout')
export const fetchSystemUsers = () => get('/system/users')
export const createSystemUser = data => post('/system/users', data)
export const updateSystemUser = (id, data) => put(`/system/users/${id}`, data)
export const deleteSystemUser = id => del(`/system/users/${id}`)
export const fetchOperationLogs = params => get('/system/logs', { params })
export const fetchSystemSettings = () => get('/system/settings')
export const updateSystemSettings = data => put('/system/settings', data)
