/**
 * 系统总览短时缓存：快速进出模块后回首页可立即展示快照，再后台刷新
 * 按 userId+deptId 隔离，避免换账号串数据
 */
import { resolveCurrentUserDeptId } from '@/utils/siteOptions'
import { UserInfoKey } from '@/utils/constants'

const CACHE_KEY_PREFIX = 'kede_daping3_snapshot_v8_'
/** 兼容清理旧版无用户维度的 key */
const LEGACY_CACHE_KEY = 'kede_daping3_snapshot_v2'
const LEGACY_CACHE_KEY_V3_PREFIX = 'kede_daping3_snapshot_v3_'
const LEGACY_CACHE_KEY_V4_PREFIX = 'kede_daping3_snapshot_v4_'
const LEGACY_CACHE_KEY_V5_PREFIX = 'kede_daping3_snapshot_v5_'
const LEGACY_CACHE_KEY_V6_PREFIX = 'kede_daping3_snapshot_v6_'
const LEGACY_CACHE_KEY_V7_PREFIX = 'kede_daping3_snapshot_v7_'
/** 新鲜窗口：优先用新鲜快照；过期仍可 stale 展示避免空白 */
const TTL_MS = 5 * 60 * 1000
const STALE_MS = 30 * 60 * 1000

function resolveScopeKey() {
  let userId = ''
  try {
    const raw = typeof localStorage !== 'undefined' ? localStorage.getItem(UserInfoKey) : null
    const info = raw ? JSON.parse(raw) : null
    userId = String((info && (info.userId || info.id)) || '')
  } catch (e) { /* ignore */ }
  const deptId = resolveCurrentUserDeptId()
  return String(userId || '0') + '_' + String(deptId != null ? deptId : 'all')
}

function cacheKey() {
  return CACHE_KEY_PREFIX + resolveScopeKey()
}

/**
 * @param {{ allowStale?: boolean }} [opts]
 */
export function readDashboardSnapshot(opts) {
  try {
    const allowStale = !!(opts && opts.allowStale)
    const raw = sessionStorage.getItem(cacheKey())
    if (!raw) return null
    const parsed = JSON.parse(raw)
    if (!parsed || !parsed.ts || !parsed.data) return null
    const scope = resolveScopeKey()
    if (parsed.scope && parsed.scope !== scope) return null
    const age = Date.now() - parsed.ts
    if (age <= TTL_MS) return parsed.data
    if (allowStale && age <= STALE_MS) return parsed.data
    return null
  } catch (e) {
    return null
  }
}

export function writeDashboardSnapshot(data) {
  try {
    const scope = resolveScopeKey()
    sessionStorage.setItem(cacheKey(), JSON.stringify({ ts: Date.now(), scope, data }))
  } catch (e) { /* ignore quota */ }
}

/** 登出 / 切换用户或部门时清除总览快照，避免串数据 */
export function clearDashboardSnapshot() {
  try {
    sessionStorage.removeItem(LEGACY_CACHE_KEY)
    sessionStorage.removeItem(cacheKey())
    const keys = []
    for (let i = 0; i < sessionStorage.length; i++) {
      const k = sessionStorage.key(i)
      if (
        k &&
        (k.indexOf(CACHE_KEY_PREFIX) === 0 ||
          k.indexOf(LEGACY_CACHE_KEY_V7_PREFIX) === 0 ||
          k.indexOf(LEGACY_CACHE_KEY_V6_PREFIX) === 0 ||
          k.indexOf(LEGACY_CACHE_KEY_V5_PREFIX) === 0 ||
          k.indexOf(LEGACY_CACHE_KEY_V4_PREFIX) === 0 ||
          k.indexOf(LEGACY_CACHE_KEY_V3_PREFIX) === 0 ||
          k === LEGACY_CACHE_KEY)
      ) {
        keys.push(k)
      }
    }
    keys.forEach((k) => sessionStorage.removeItem(k))
  } catch (e) { /* ignore */ }
}
