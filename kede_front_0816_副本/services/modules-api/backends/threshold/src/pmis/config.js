const path = require('path')
const fs = require('fs')

function loadEnvFile() {
  const envPath = path.join(__dirname, '../../.env')
  if (!fs.existsSync(envPath)) return
  const text = fs.readFileSync(envPath, 'utf8')
  text.split(/\r?\n/).forEach(line => {
    const trimmed = line.trim()
    if (!trimmed || trimmed.startsWith('#')) return
    const eq = trimmed.indexOf('=')
    if (eq <= 0) return
    const key = trimmed.slice(0, eq).trim()
    const val = trimmed.slice(eq + 1).trim()
    if (process.env[key] === undefined) process.env[key] = val
  })
}

loadEnvFile()

const config = {
  enabled: String(process.env.PMIS_ENABLED || 'true').toLowerCase() !== 'false',
  baseUrl: (process.env.PMIS_BASE_URL || 'http://127.0.0.1:8088/zx/datainf/mysql').replace(
    /\/$/,
    ''
  ),
  timeoutMs: Number(process.env.PMIS_TIMEOUT_MS || 30000)
}

function isEnabled() {
  return config.enabled
}

module.exports = {
  config,
  isEnabled
}
