/**
 * 科德渔业专家会议：iframe 桥接 + 本地回退
 */
export type KedeUserInfo = {
  userId?: string | number
  nickname?: string
  userName?: string
  deptId?: string | number
  tenantId?: string | number
  accessToken?: string
  roles?: string[]
  /** 专业人员：可提交知识库 */
  isProfessional?: boolean
}

const USER_KEY = 'kede-expert-user'
const SELECTED_ASR_KEY = 'kede-selected-asr-text'
const MEETING_META_KEY = 'kede-meeting-meta'

export function saveKedeUser(info: KedeUserInfo) {
  try {
    sessionStorage.setItem(USER_KEY, JSON.stringify(info || {}))
  } catch (e) { /* ignore */ }
}

export function loadKedeUser(): KedeUserInfo {
  try {
    const raw = sessionStorage.getItem(USER_KEY)
    if (raw) return JSON.parse(raw) || {}
  } catch (e) { /* ignore */ }
  return {}
}

export function isProfessionalUser(info?: KedeUserInfo) {
  const u = info || loadKedeUser()
  if (u.isProfessional === true) return true
  const roles = (u.roles || []).map((r) => String(r).toLowerCase())
  // 科德专家/管理员等角色视为专业人员；可按菜单配置扩展
  return roles.some((r) =>
    /expert|专业|专家|ke_admin|kede_group|professor|专员/.test(r)
  )
}

export function saveSelectedAsrText(text: string) {
  try {
    const prev = sessionStorage.getItem(SELECTED_ASR_KEY) || ''
    const next = prev ? `${prev}\n${text}` : text
    sessionStorage.setItem(SELECTED_ASR_KEY, next)
    window.dispatchEvent(new CustomEvent('kede:asr-selected', { detail: { text, all: next } }))
  } catch (e) { /* ignore */ }
}

export function loadSelectedAsrText() {
  try {
    return sessionStorage.getItem(SELECTED_ASR_KEY) || ''
  } catch (e) {
    return ''
  }
}

export function clearSelectedAsrText() {
  try {
    sessionStorage.removeItem(SELECTED_ASR_KEY)
  } catch (e) { /* ignore */ }
}

export function saveMeetingMeta(meta: { meetingNum?: string; meetingId?: string | number; meetingPoint?: string; meetToDo?: string }) {
  try {
    const prev = JSON.parse(sessionStorage.getItem(MEETING_META_KEY) || '{}')
    sessionStorage.setItem(MEETING_META_KEY, JSON.stringify({ ...prev, ...meta }))
  } catch (e) { /* ignore */ }
}

export function loadMeetingMeta() {
  try {
    return JSON.parse(sessionStorage.getItem(MEETING_META_KEY) || '{}')
  } catch (e) {
    return {}
  }
}

function inIframe() {
  try {
    return window.self !== window.top
  } catch (e) {
    return true
  }
}

let reqSeq = 0
const pending = new Map<string, { resolve: (v: any) => void; reject: (e: any) => void; timer: any }>()

function ensureListener() {
  if ((window as any).__kedeApiListener) return
  ;(window as any).__kedeApiListener = true
  window.addEventListener('message', (event) => {
    const data = event.data || {}
    if (data.type === 'parent_init_kede' && data.userInfo) {
      saveKedeUser(data.userInfo)
      window.dispatchEvent(new CustomEvent('kede:user-ready', { detail: data.userInfo }))
      return
    }
    if (data.type === 'parent_mirror_chat') {
      window.dispatchEvent(new CustomEvent('kede:parent-mirror', { detail: data }))
      if (data.meetingId) saveMeetingMeta({ meetingId: data.meetingId })
      return
    }
    if (data.type === 'rtc_kede_api_result' && data.requestId && pending.has(data.requestId)) {
      const p = pending.get(data.requestId)!
      clearTimeout(p.timer)
      pending.delete(data.requestId)
      if (data.ok) p.resolve(data.data)
      else p.reject(new Error(data.msg || '系统异常'))
    }
  })
}

ensureListener()

/** 向父页请求渔业后端能力；不在 iframe 或超时则抛错，由调用方回退 */
export function callParentApi<T = any>(action: string, payload: Record<string, any> = {}, timeoutMs = 20000): Promise<T> {
  ensureListener()
  if (!inIframe()) {
    return Promise.reject(new Error('standalone_no_parent'))
  }
  const requestId = `kede_${Date.now()}_${++reqSeq}`
  return new Promise((resolve, reject) => {
    const timer = setTimeout(() => {
      pending.delete(requestId)
      reject(new Error('小科/父页接口超时'))
    }, timeoutMs)
    pending.set(requestId, { resolve, reject, timer })
    window.parent.postMessage(
      {
        type: 'rtc_kede_api',
        requestId,
        action,
        payload,
      },
      '*'
    )
  })
}

export function notifyParentJoin(meetingNum: string) {
  if (!inIframe()) return
  const num = String(meetingNum || '').trim()
  if (!num) return
  window.parent.postMessage(
    {
      type: 'son_send_to_parent_meeting',
      value_type: 'son_send_to_parent_meeting_jion_meet',
      meetingNum: num,
    },
    '*'
  )
}

export function notifyParentChat(meetingNum: string, value: string) {
  if (!inIframe()) return
  const num = String(meetingNum || '').trim()
  if (!num) return
  window.parent.postMessage(
    {
      type: 'son_send_to_parent_meeting',
      value_type: 'son_send_to_parent_meeting_chat_msg',
      meetingNum: num,
      value,
    },
    '*'
  )
}
