import request from './request'

export const fetchMeta = () => request.get('/meta')
export const fetchSummary = () => request.get('/diagnosis/summary')
export const fetchDiagnosisRecords = params => request.get('/diagnosis/records', { params })
export const fetchDiagnosisRecord = id => request.get(`/diagnosis/records/${id}`)
export const createDiagnosisRecord = data => request.post('/diagnosis/records', data)
export const updateDiagnosisRecord = (id, data) => request.put(`/diagnosis/records/${id}`, data)
export const deleteDiagnosisRecord = id => request.delete(`/diagnosis/records/${id}`)
export const runDiagnosis = data => request.post('/diagnosis/diagnose', data)
export const chatDiagnosis = (id, data) => request.post(`/diagnosis/records/${id}/chat`, data)

export const fetchKnowledge = () => request.get('/knowledge')
export const fetchKnowledgeDetail = id => request.get(`/knowledge/${id}`)

export const fetchPonds = () => request.get('/config/ponds')
export const createPond = data => request.post('/config/ponds', data)
export const updatePond = (id, data) => request.put(`/config/ponds/${id}`, data)
export const deletePond = id => request.delete(`/config/ponds/${id}`)

export const fetchFishSpecies = () => request.get('/config/species')
export const createFishSpecies = data => request.post('/config/species', data)
export const updateFishSpecies = (id, data) => request.put(`/config/species/${id}`, data)
export const deleteFishSpecies = id => request.delete(`/config/species/${id}`)

export const fetchDiseaseTypes = () => request.get('/config/disease-types')
export const createDiseaseType = data => request.post('/config/disease-types', data)
export const updateDiseaseType = (id, data) => request.put(`/config/disease-types/${id}`, data)
export const deleteDiseaseType = id => request.delete(`/config/disease-types/${id}`)

export const fetchEquipments = () => request.get('/config/equipments')
export const createEquipment = data => request.post('/config/equipments', data)
export const updateEquipment = (id, data) => request.put(`/config/equipments/${id}`, data)
export const deleteEquipment = id => request.delete(`/config/equipments/${id}`)

export const fetchMonitorData = () => request.get('/collection/monitor')
export const submitCollection = data => request.post('/collection/records', data)
export const fetchCollectionRecords = () => request.get('/collection/records')

export const fetchAiAlarms = params => request.get('/alarms', { params })
export const createAiAlarm = data => request.post('/alarms', data)
export const ackAiAlarm = id => request.post(`/alarms/${id}/ack`)

export const fetchArchiveSummary = () => request.get('/archive/summary')
export const fetchArchiveCategories = () => request.get('/archive/categories')
export const fetchArchivedCases = params => request.get('/archive/cases', { params })
export const fetchArchivedCase = id => request.get(`/archive/cases/${id}`)

export const login = data => request.post('/auth/login', data) // 登录已关闭（后端返回 403）
export const register = data => request.post('/auth/register', data) // 注册已关闭（后端返回 403）
export const refreshToken = data => request.post('/auth/refresh', data)
export const fetchProfile = () => request.get('/auth/me')
export const fetchChatContacts = () => request.get('/auth/contacts')
export const logout = () => request.post('/auth/logout')
export const fetchSystemUsers = () => request.get('/system/users')
export const createSystemUser = data => request.post('/system/users', data)
export const updateSystemUser = (id, data) => request.put(`/system/users/${id}`, data)
export const deleteSystemUser = id => request.delete(`/system/users/${id}`)
export const fetchOperationLogs = params => request.get('/system/logs', { params })
export const fetchSystemSettings = () => request.get('/system/settings')
export const updateSystemSettings = data => request.put('/system/settings', data)
