const local = require('./store')
const pmisStore = require('../pmis/store')
const { isEnabled } = require('../pmis/config')

const PMIS_METHODS = new Set(Object.keys(pmisStore))

/** 用户体系一期仍用本地内存 */
const LOCAL_ONLY = new Set([
  'listSystemUsers',
  'listChatContacts',
  'getSystemUserById',
  'createSystemUser',
  'updateSystemUser',
  'deleteSystemUser',
  'loginUser',
  'registerUser'
])

function wrap(name) {
  return async (...args) => {
    if (isEnabled() && PMIS_METHODS.has(name) && !LOCAL_ONLY.has(name)) {
      try {
        return await pmisStore[name](...args)
      } catch (err) {
        // PMIS 超时/网络异常时回退本地，避免拖垮整页与 Node 进程
        console.error(`[fish/pmis] ${name} failed, fallback local:`, (err && err.message) || err)
        if (typeof local[name] === 'function') {
          return local[name](...args)
        }
        throw err
      }
    }
    const result = local[name](...args)
    return result
  }
}

const api = {}
for (const name of Object.keys(local)) {
  if (typeof local[name] === 'function') {
    api[name] = wrap(name)
  }
}

api.isPmisEnabled = isEnabled

module.exports = api
