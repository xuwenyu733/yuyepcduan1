const fs = require('fs')
const path = require('path')
let s = fs.readFileSync(path.join(__dirname, '../src/utils/backendUserId.js'), 'utf8')
s = s.replace(/export function /g, 'function ')
s += '\nmodule.exports = { toBackendUserId, resolveOperatorUser };'
const mod = { exports: {} }
new Function('module', 'exports', s)(mod, mod.exports)
const { toBackendUserId } = mod.exports
const cases = [
  ['pc-user', null],
  ['abc', null],
  ['', null],
  [0, null],
  [1, 1],
  ['142', 142],
  [null, null],
  [undefined, null]
]
let fail = 0
for (const [a, b] of cases) {
  const g = toBackendUserId(a)
  if (g !== b) {
    console.log('FAIL', a, 'got', g, 'want', b)
    fail++
  }
}
if (fail) process.exit(1)
console.log('UID_OK', cases.length)
