/**
 * 部门数据范围：请求统一附带当前用户 deptId；切换用户/部门后通知页面强制刷新。
 */
import { resolveCurrentUserDeptId } from '@/utils/siteOptions'

export const USER_SCOPE_CHANGED_EVENT = 'kede:user-scope-changed'

/**
 * 合并查询参数，自动带上当前登录用户归属部门（已有 deptId 时不覆盖）。
 * @param {Record<string, any>} [params]
 * @returns {Record<string, any>}
 */
export function withUserDeptParams(params = {}) {
  const next = params && typeof params === 'object' ? { ...params } : {}
  if (next.deptId == null || next.deptId === '' || next.deptId === 0 || next.deptId === '0') {
    const deptId = resolveCurrentUserDeptId()
    if (deptId != null) next.deptId = deptId
  }
  return next
}

export function notifyUserScopeChanged(detail = {}) {
  try {
    if (typeof window !== 'undefined' && typeof window.dispatchEvent === 'function') {
      window.dispatchEvent(new CustomEvent(USER_SCOPE_CHANGED_EVENT, { detail }))
    }
  } catch (e) { /* ignore */ }
}

/**
 * 页面挂载时监听部门/用户切换，回调里应清空本地状态并重新拉数。
 * @param {() => void} handler
 * @returns {() => void} dispose
 */
export function onUserScopeChanged(handler) {
  if (typeof window === 'undefined' || typeof handler !== 'function') {
    return () => {}
  }
  const wrapped = () => {
    try { handler() } catch (e) { /* ignore */ }
  }
  window.addEventListener(USER_SCOPE_CHANGED_EVENT, wrapped)
  return () => window.removeEventListener(USER_SCOPE_CHANGED_EVENT, wrapped)
}
