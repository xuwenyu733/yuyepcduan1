import request, { getBaseHeader } from '@/utils/request_jidi'

export function saveSmartEntry(form) {
  return request({ url: '/fish/smart-entry/records', method: 'post', data: form,
    headers: { 'Content-Type': 'multipart/form-data' }, timeout: 60000 })
}
export function listSmartEntries(pageNo = 1) {
  return request({ url: '/fish/smart-entry/records', method: 'get', params: { pageNo, pageSize: 10 } })
}

/** 一键 AI 规范录入标题/文字说明（走本机 ai-xiaoke :5190） */
export async function polishSmartEntry(title, content, signal) {
  let resp
  try {
    resp = await fetch('/llm/entry/polish', {
      method: 'POST',
      signal,
      headers: {
        'Content-Type': 'application/json;charset=utf-8',
        Accept: 'application/json',
      },
      body: JSON.stringify({
        title: String(title || ''),
        content: String(content || ''),
      }),
    })
  } catch (e) {
    if (e && e.name === 'AbortError') throw e
    throw new Error('AI小科服务未启动或连不上（:5190），无法优化录入内容')
  }
  const data = await resp.json().catch(() => ({}))
  if (!resp.ok || data.ok === false) {
    throw new Error(data.error || `优化失败（${resp.status || '网络错误'}）`)
  }
  return data
}
export async function loadSmartImage(id, signal) {
  const base = (process.env.VUE_APP_BASE_API || '').replace(/\/+$/, '')
  const response = await fetch(`${base}/admin-api/fish/smart-entry/images/${encodeURIComponent(id)}`, {
    headers: getBaseHeader(), signal
  })
  if (!response.ok || !/^image\//i.test(response.headers.get('content-type') || '')) {
    throw new Error('图片读取失败，请确认登录状态后重试')
  }
  return response.blob()
}
