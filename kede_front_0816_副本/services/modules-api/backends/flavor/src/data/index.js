const local = require('./store')
const pmisStore = require('../pmis/store')
const { isEnabled } = require('../pmis/config')

const PMIS_METHODS = new Set(Object.keys(pmisStore))

function wrap(name) {
  return async (...args) => {
    if (isEnabled() && PMIS_METHODS.has(name)) {
      return pmisStore[name](...args)
    }
    return local[name](...args)
  }
}

const api = {}
for (const name of Object.keys(local)) {
  if (typeof local[name] === 'function') api[name] = wrap(name)
}
api.isPmisEnabled = isEnabled

module.exports = api
