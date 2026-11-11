/**
 * 养殖实验数据管理系统 API（走 modules BFF /api/experiment）
 */
import { Message } from 'element-ui'
import { experimentApi } from '@/api/modules'

function wrap(promise) {
  return promise.catch(err => {
    Message.error((err && err.message) || '请求失败')
    return Promise.reject(err)
  })
}

const get = (url, config) => wrap(experimentApi.get(url, config))
const post = (url, data, config) => wrap(experimentApi.post(url, data, config))
const put = (url, data, config) => wrap(experimentApi.put(url, data, config))
const del = (url, config) => wrap(experimentApi.delete(url, config))

export const fetchExperimentMeta = () => get('/experiments/meta')
export const fetchExperiments = params => get('/experiments', { params })
export const fetchExperimentById = id => get(`/experiments/${id}`)
export const createExperiment = data => post('/experiments', data)
export const updateExperiment = (id, data) => put(`/experiments/${id}`, data)
export const deleteExperiment = id => del(`/experiments/${id}`)

export const fetchTemplateTypes = () => get('/templates/types')
export const fetchTemplatePresets = type => get(`/templates/presets/${type}`)
export const fetchTemplates = () => get('/templates')
export const createTemplate = data => post('/templates', data)
export const copyTemplate = id => post(`/templates/${id}/copy`)
export const deleteTemplate = id => del(`/templates/${id}`)

export const fetchCollectionRecords = params => get('/collection', { params })
export const submitCollection = data => post('/collection', data)

export const fetchMediaMeta = () => get('/media/meta')
export const fetchMediaList = params => get('/media', { params })
export const uploadMedia = data => post('/media', data)
export const deleteMedia = id => del(`/media/${id}`)

export const fetchAnalysisSummary = params => get('/analysis/summary', { params })

export const fetchRoles = () => get('/permissions/roles')
export const fetchUsers = () => get('/permissions/users')
export const createUser = data => post('/permissions/users', data)
export const updateUser = (id, data) => put(`/permissions/users/${id}`, data)
export const deleteUser = id => del(`/permissions/users/${id}`)
export const fetchOperationLogs = () => get('/permissions/logs')
