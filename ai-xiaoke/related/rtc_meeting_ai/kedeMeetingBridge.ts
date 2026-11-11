/**
 * 科德渔业专家会议：父页桥接（iframe / window.open opener）+ 本地回退
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
    const raw = JSON.stringify(info || {})
    sessionStorage.setItem(USER_KEY, raw)
    // 新窗口与父页 sessionStorage 不共享，用 localStorage 兜底
    localStorage.setItem(USER_KEY, raw)
  } catch (e) { /* ignore */ }
}

export function loadKedeUser(): KedeUserInfo {
  try {
    const raw = sessionStorage.getItem(USER_KEY) || localStorage.getItem(USER_KEY)
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
    const meta = loadMeetingMeta()
    const meetingNum = String(meta.meetingNum || '').trim()
    const prev = loadSelectedAsrText(meetingNum)
    const next = prev ? `${prev}\n${text}` : text
    const packed = JSON.stringify({ meetingNum, text: next })
    sessionStorage.setItem(SELECTED_ASR_KEY, packed)
    // 会议在独立窗，父页总结弹窗读不到 sessionStorage，必须同步 localStorage
    localStorage.setItem(SELECTED_ASR_KEY, packed)
    window.dispatchEvent(new CustomEvent('kede:asr-selected', { detail: { text, all: next, meetingNum } }))
  } catch (e) { /* ignore */ }
}

export function loadSelectedAsrText(meetingNum?: string) {
  try {
    const want = String(meetingNum || loadMeetingMeta().meetingNum || '').trim()
    const raw = sessionStorage.getItem(SELECTED_ASR_KEY)
      || localStorage.getItem(SELECTED_ASR_KEY)
      || ''
    if (!raw) return ''
    try {
      const parsed = JSON.parse(raw)
      if (parsed && typeof parsed === 'object') {
        const savedNum = String(parsed.meetingNum || '').trim()
        if (want && savedNum && savedNum !== want) return ''
        return String(parsed.text || '')
      }
    } catch {
      // 旧版纯文本：无会议号约束时才返回，避免串会
      if (!want) return raw
      return ''
    }
    return ''
  } catch (e) {
    return ''
  }
}

export function clearSelectedAsrText() {
  try {
    sessionStorage.removeItem(SELECTED_ASR_KEY)
    localStorage.removeItem(SELECTED_ASR_KEY)
  } catch (e) { /* ignore */ }
}

export function saveMeetingMeta(meta: { meetingNum?: string; meetingId?: string | number; meetingPoint?: string; meetToDo?: string }) {
  try {
    const prev = loadMeetingMeta()
    const nextNum = meta.meetingNum != null ? String(meta.meetingNum).trim() : ''
    const prevNum = String(prev.meetingNum || '').trim()
    // 换会议号时丢掉上一次纪要，避免总结页串会
    if (nextNum && prevNum && nextNum !== prevNum) {
      const next = {
        meetingNum: nextNum,
        meetingId: meta.meetingId,
        meetingPoint: meta.meetingPoint || '',
        meetToDo: meta.meetToDo || '',
      }
      sessionStorage.setItem(MEETING_META_KEY, JSON.stringify(next))
      try { localStorage.setItem(MEETING_META_KEY, JSON.stringify(next)) } catch (e2) { /* ignore */ }
      clearSelectedAsrText()
      return
    }
    const merged = { ...prev, ...meta }
    sessionStorage.setItem(MEETING_META_KEY, JSON.stringify(merged))
    try { localStorage.setItem(MEETING_META_KEY, JSON.stringify(merged)) } catch (e2) { /* ignore */ }
  } catch (e) { /* ignore */ }
}

export function loadMeetingMeta() {
  try {
    const raw = sessionStorage.getItem(MEETING_META_KEY) || localStorage.getItem(MEETING_META_KEY) || '{}'
    return JSON.parse(raw) || {}
  } catch (e) {
    return {}
  }
}

/** 进入新会议 / 离开后开新场：清空本场会议记录与字幕缓存 */
export function resetMeetingSession(meetingNum?: string) {
  const num = String(meetingNum || '').trim()
  clearSelectedAsrText()
  try {
    const next = num ? { meetingNum: num } : {}
    sessionStorage.setItem(MEETING_META_KEY, JSON.stringify(next))
    localStorage.setItem(MEETING_META_KEY, JSON.stringify(next))
  } catch (e) { /* ignore */ }
  try {
    window.dispatchEvent(new CustomEvent('kede:meeting-reset', { detail: { meetingNum: num } }))
  } catch (e) { /* ignore */ }
}

/** iframe 嵌入，或由渔业父页 window.open 打开（opener 可通信） */
function canTalkToParent() {
  try {
    if (window.opener && !window.opener.closed) return true
    return window.self !== window.top
  } catch (e) {
    return true
  }
}

function getParentWindow(): Window | null {
  try {
    if (window.opener && !window.opener.closed) return window.opener
    if (window.parent && window.parent !== window) return window.parent
  } catch (e) { /* ignore */ }
  return null
}

function postToParent(data: Record<string, any>) {
  const w = getParentWindow()
  if (!w) return
  w.postMessage(data, '*')
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

/** 向父页请求渔业后端能力；无父窗或超时则抛错，由调用方回退 */
export function callParentApi<T = any>(action: string, payload: Record<string, any> = {}, timeoutMs = 20000): Promise<T> {
  ensureListener()
  if (!canTalkToParent()) {
    return Promise.reject(new Error('standalone_no_parent'))
  }
  const requestId = `kede_${Date.now()}_${++reqSeq}`
  return new Promise((resolve, reject) => {
    const timer = setTimeout(() => {
      pending.delete(requestId)
      reject(new Error('小科/父页接口超时'))
    }, timeoutMs)
    pending.set(requestId, { resolve, reject, timer })
    postToParent({
      type: 'rtc_kede_api',
      requestId,
      action,
      payload,
    })
  })
}

export function notifyParentJoin(meetingNum: string) {
  if (!canTalkToParent()) return
  const num = String(meetingNum || '').trim()
  if (!num) return
  postToParent({
    type: 'son_send_to_parent_meeting',
    value_type: 'son_send_to_parent_meeting_jion_meet',
    meetingNum: num,
  })
}

export function notifyParentLeave(meetingNum?: string) {
  if (!canTalkToParent()) return
  postToParent({
    type: 'son_send_to_parent_meeting',
    value_type: 'son_send_to_parent_meeting_leave_meet',
    meetingNum: String(meetingNum || '').trim(),
  })
  postToParent({ type: 'rtc_kede_left_room' })
}

export function notifyParentChat(meetingNum: string, value: string) {
  if (!canTalkToParent()) return
  const num = String(meetingNum || '').trim()
  if (!num) return
  postToParent({
    type: 'son_send_to_parent_meeting',
    value_type: 'son_send_to_parent_meeting_chat_msg',
    meetingNum: num,
    value,
  })
}
