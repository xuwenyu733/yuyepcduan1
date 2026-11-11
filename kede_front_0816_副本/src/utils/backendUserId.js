/**
 * 后端 Long 型用户 ID：禁止把 'pc-user' 等占位字符串传给 yudao。
 */
export function toBackendUserId(v) {
  if (v == null || v === '') return null
  if (typeof v === 'number') {
    if (!Number.isFinite(v) || v <= 0) return null
    return Math.trunc(v)
  }
  const s = String(v).trim()
  if (!s || s === 'pc-user' || s === 'undefined' || s === 'null') return null
  if (!/^\d+$/.test(s)) return null
  const n = Number(s)
  if (!Number.isFinite(n) || n <= 0) return null
  return n
}

export function resolveOperatorUser(vm, fallbackName) {
  const nameFallback = fallbackName || '当前操作员'
  let id = null
  let name = nameFallback
  try {
    if (vm && vm.$store && vm.$store.getters) {
      id = toBackendUserId(vm.$store.getters.userId)
      name = vm.$store.getters.nickname || vm.$store.getters.name || name
    }
    if (typeof localStorage !== 'undefined') {
      const raw = localStorage.getItem('platform_operator') || localStorage.getItem('current_user')
      if (raw) {
        const user = JSON.parse(raw)
        if (!id) id = toBackendUserId(user.id || user.user_id || user.userId)
        name = user.nickname || user.name || user.user_name || user.userName || name
      }
    }
  } catch (e) { /* ignore */ }
  return { id: id, name: name }
}
