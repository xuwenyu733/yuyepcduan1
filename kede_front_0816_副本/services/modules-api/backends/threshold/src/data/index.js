const local = require('./thresholds')
const pmisStore = require('../pmis/store')
const { isEnabled } = require('../pmis/config')

const PMIS_METHODS = new Set(Object.keys(pmisStore))

function wrap(name) {
  return async (...args) => {
    if (isEnabled() && PMIS_METHODS.has(name)) {
      return pmisStore[name](...args)
    }
    const fn = local[name]
    if (typeof fn !== 'function') {
      throw new Error(`本地 store 无方法: ${name}`)
    }
    return fn(...args)
  }
}

const api = {}
for (const name of Object.keys(local)) {
  if (typeof local[name] === 'function') api[name] = wrap(name)
}

// PMIS 专有方法（本地无实现时补齐 stub）
;['listExistingThresholds', 'listAiAlarms', 'createAiAlarm', 'ackAiAlarm', 'ensureAiAlarmMock'].forEach(
  name => {
    if (!api[name]) {
      api[name] = async (...args) => {
        if (isEnabled() && PMIS_METHODS.has(name)) return pmisStore[name](...args)
        if (name === 'listExistingThresholds' || name === 'listAiAlarms') return []
        if (name === 'ackAiAlarm') return null
        return {
          id: `LOCAL-${Date.now()}`,
          isMock: true,
          alarmStatus: 'open',
          ...(args[0] || {})
        }
      }
    }
  }
)

api.isPmisEnabled = isEnabled

module.exports = api
