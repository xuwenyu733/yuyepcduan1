/**
 * 生产记录 ↔ 后加看板模块贯通：投放/死亡/投喂等写入后通知死亡率、精准投喂等刷新。
 */
export const PRODUCTION_RECORD_CHANGED_EVENT = 'kede:production-record-changed'

/**
 * @param {{ type?: string, pondId?: number|string, source?: string }} [detail]
 */
export function notifyProductionRecordChanged(detail) {
  try {
    if (typeof window === 'undefined' || typeof window.dispatchEvent !== 'function') return
    window.dispatchEvent(new CustomEvent(PRODUCTION_RECORD_CHANGED_EVENT, { detail: detail || {} }))
  } catch (e) { /* ignore */ }
}

/**
 * @param {(detail: object) => void} handler
 * @returns {() => void}
 */
export function onProductionRecordChanged(handler) {
  if (typeof window === 'undefined' || typeof handler !== 'function') {
    return () => {}
  }
  const wrapped = (ev) => {
    try { handler((ev && ev.detail) || {}) } catch (e) { /* ignore */ }
  }
  window.addEventListener(PRODUCTION_RECORD_CHANGED_EVENT, wrapped)
  return () => window.removeEventListener(PRODUCTION_RECORD_CHANGED_EVENT, wrapped)
}
