const BASE = '/proxy-api/admin-api'
const TENANT_ID = '1'
const STORAGE_KEY = 'xiaoke-auth'

export function loadAuth() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null')
  } catch {
    return null
  }
}

export function saveAuth(data) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(data || null))
}

export function clearAuth() {
  localStorage.removeItem(STORAGE_KEY)
}

function headers(extra = {}) {
  const auth = loadAuth()
  const h = {
    'Content-Type': 'application/json;charset=utf-8',
    'tenant-id': TENANT_ID,
    ...extra,
  }
  if (auth?.accessToken) {
    h.Authorization = `Bearer ${auth.accessToken}`
  }
  return h
}

export async function login(username = 'admin', password = 'admin123') {
  const resp = await fetch(`${BASE}/system/auth/login`, {
    method: 'POST',
    headers: headers(),
    body: JSON.stringify({ username, password }),
  })
  const json = await resp.json()
  if (json.code !== 0 || !json.data?.accessToken) {
    throw new Error(json.msg || `登录失败(${resp.status})`)
  }
  saveAuth(json.data)
  return json.data
}

export async function ensureLogin() {
  const auth = loadAuth()
  if (auth?.accessToken && auth.expiresTime && Date.now() < auth.expiresTime - 60_000) {
    return auth
  }
  return login()
}

export async function getLlmStatus() {
  const resp = await fetch('/llm/status')
  if (!resp.ok) throw new Error('无法读取 LLM 配置状态')
  return resp.json()
}

export async function listSkills() {
  const resp = await fetch('/llm/skills')
  const json = await resp.json()
  if (!resp.ok) throw new Error(json.error || '获取技能列表失败')
  return json
}

export async function previewSkillMarkdown(title, content) {
  const resp = await fetch('/llm/skills/preview', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, content }),
  })
  const json = await resp.json()
  if (!resp.ok) throw new Error(json.error || '预览失败')
  return json.markdown
}

export async function saveSkillApi(payload) {
  const resp = await fetch('/llm/skills', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
  const json = await resp.json()
  if (!resp.ok) throw new Error(json.error || '保存失败')
  return json.skill
}

export async function deleteSkillApi(file) {
  const resp = await fetch(`/llm/skills?file=${encodeURIComponent(file)}`, {
    method: 'DELETE',
  })
  const json = await resp.json()
  if (!resp.ok) throw new Error(json.error || '删除失败')
  return json
}

export async function getSkillDetail(file, folder = 'skills') {
  const resp = await fetch(
    `/llm/skills/detail?file=${encodeURIComponent(file)}&folder=${encodeURIComponent(folder)}`,
  )
  const json = await resp.json()
  if (!resp.ok) throw new Error(json.error || '读取失败')
  return json
}

async function consumeSse(resp, onEvent) {
  if (!resp.body) throw new Error('浏览器不支持 ReadableStream')
  const reader = resp.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  const emit = (evt) => {
    if (typeof onEvent === 'function') onEvent(evt)
  }

  const parse = () => {
    const sep = /\r?\n\r?\n/
    let match
    while ((match = sep.exec(buffer)) !== null) {
      const raw = buffer.slice(0, match.index)
      buffer = buffer.slice(match.index + match[0].length)
      for (const line of raw.split(/\r?\n/)) {
        if (!line.startsWith('data:')) continue
        const payload = line.slice(5).trim()
        if (!payload) continue
        try {
          emit(JSON.parse(payload))
        } catch {
          emit({ type: 'delta', content: payload })
        }
      }
    }
  }

  while (true) {
    const { value, done } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })
    parse()
  }
}

export async function chatDirectStream(data, { onEvent, signal } = {}) {
  const resp = await fetch('/llm/chat', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json;charset=utf-8',
      Accept: 'text/event-stream',
    },
    body: JSON.stringify(data || {}),
    signal,
  })
  if (!resp.ok) {
    const text = await resp.text().catch(() => '')
    let msg = text
    try {
      msg = JSON.parse(text).error || text
    } catch {
      /* keep */
    }
    throw new Error(msg || `LLM请求失败(${resp.status})`)
  }
  await consumeSse(resp, onEvent)
}
