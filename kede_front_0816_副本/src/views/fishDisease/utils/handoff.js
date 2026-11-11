/** 平台 ↔ 鱼病模块深链 / 媒体交接约定 */

export const PENDING_MEDIA_KEY = 'fish_disease_pending_media'

export function writePendingMedia(payload) {
  try {
    sessionStorage.setItem(PENDING_MEDIA_KEY, JSON.stringify({
      ...payload,
      ts: payload.ts || Date.now()
    }))
  } catch (e) {
    console.warn('writePendingMedia failed', e)
  }
}

export function readPendingMedia() {
  try {
    const raw = sessionStorage.getItem(PENDING_MEDIA_KEY)
    if (!raw) return null
    return JSON.parse(raw)
  } catch (e) {
    return null
  }
}

export function clearPendingMedia() {
  try {
    sessionStorage.removeItem(PENDING_MEDIA_KEY)
  } catch (e) { /* ignore */ }
}

/** 取出并清除 pending 媒体（诊断页消费一次） */
export function takePendingMedia() {
  const data = readPendingMedia()
  if (data) clearPendingMedia()
  return data
}

export function buildFishDiseasePath(query = {}) {
  const q = Object.keys(query)
    .filter(k => query[k] != null && query[k] !== '')
    .map(k => `${encodeURIComponent(k)}=${encodeURIComponent(query[k])}`)
    .join('&')
  return q ? `/fish-disease?${q}` : '/fish-disease'
}
