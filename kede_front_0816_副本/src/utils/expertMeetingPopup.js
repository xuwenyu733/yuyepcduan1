/**
 * 专家会议新窗口：在用户点击手势内打开，避免浏览器拦截弹窗。
 * 与 remote_meeting.vue 共用 window name = kede_expert_meeting。
 */
export function isExpertMeetingPath(path) {
  const p = String(path || '')
  return /zk-meeting-expert|remote_meeting|zkMeeting\/remote|zk-meeting\/remote/i.test(p)
    || (p.indexOf('meeting') !== -1 && p.indexOf('expert') !== -1)
}

function pickNick(...cands) {
  for (let i = 0; i < cands.length; i++) {
    const s = String(cands[i] == null ? '' : cands[i]).trim()
    if (s && !/^\d+$/.test(s)) return s
  }
  return ''
}

/** 从 store / localStorage 解析当前登录账号展示名与 id */
export function resolveExpertMeetingUser(storeUser = {}) {
  let fromLs = {}
  try {
    fromLs = JSON.parse(localStorage.getItem('USER_INFO_KEY') || '{}') || {}
  } catch (e) {
    fromLs = {}
  }
  const nick = pickNick(
    fromLs.nickname,
    fromLs.userName,
    storeUser.nickname,
    storeUser.name,
  )
  const uid = fromLs.userId || fromLs.id || storeUser.id || storeUser.userId || ''
  return {
    userId: uid,
    nickname: nick,
    userInfo: {
      userId: uid,
      nickname: nick,
      userName: nick,
      deptId: fromLs.deptId != null ? fromLs.deptId : storeUser.deptId,
      roles: fromLs.roles || fromLs.roleNames || storeUser.roles || [],
    },
  }
}

export function buildExpertMeetingHref(opts = {}) {
  const uid = opts.userId != null ? String(opts.userId) : ''
  const nick = opts.nickname != null ? String(opts.nickname) : ''
  const bust = `v=${Date.now()}&rtc=20260916lobby`
  const entry = `${window.location.origin}/rtc_kede/index.html`
  let hash = `#/login?redirect=${encodeURIComponent('/home')}`
  if (uid) {
    hash = `#/login?userid=${encodeURIComponent(uid)}&username=${encodeURIComponent(nick)}&redirect=${encodeURIComponent('/home')}`
  }
  return `${entry}?${bust}${hash}`
}

export function seedExpertMeetingUser(userInfo) {
  try {
    const raw = JSON.stringify(userInfo || {})
    sessionStorage.setItem('kede-expert-user', raw)
    localStorage.setItem('kede-expert-user', raw)
  } catch (e) { /* ignore */ }
}

/** @returns {Window|null} */
export function openExpertMeetingPopup(opts = {}) {
  try {
    let finalOpts = opts
    if (!opts.userId && !opts.nickname) {
      finalOpts = { ...opts, ...resolveExpertMeetingUser(opts.storeUser || {}) }
    } else if (!opts.userInfo) {
      finalOpts = {
        ...opts,
        userInfo: {
          userId: opts.userId,
          nickname: opts.nickname,
          userName: opts.nickname,
        },
      }
    }
    if (finalOpts.userInfo) seedExpertMeetingUser(finalOpts.userInfo)
    const href = buildExpertMeetingHref(finalOpts)
    return window.open(href, 'kede_expert_meeting')
  } catch (e) {
    return null
  }
}
