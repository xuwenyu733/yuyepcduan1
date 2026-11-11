import request from '@/utils/request_jidi'
import { getBaseHeader } from '@/utils/request_jidi'

// 创建客户
export function chat(data) {
  return request({
    url: '/fish/zk-ai-agent/chat',
    method: 'post',
    data: data
  })
}

// 对齐 Python /api/chat 的新接口（Java 转发）
export function chatPy(data) {
  return request({
    url: '/fish/zk-ai-agent/chatPy',
    method: 'post',
    data: data
  })
}

// 流式：对齐 Python /api/chat/stream（前端 -> Java SSE 中转 -> Python SSE）
// usage:
// await chatPyStream(req, {
//   onEvent: (evt) => { ... },  // evt = {type:'progress'|'meta'|'delta'|'final', ...}
//   signal,                    // AbortController.signal
// })
export async function chatPyStream(data, opts = {}) {
  const { onEvent, signal } = opts || {}
  const base = (process.env.VUE_APP_BASE_API || '').replace(/\/+$/, '')
  const url = `${base}/admin-api/fish/zk-ai-agent/chatPyStream`

  const resp = await fetch(url, {
    method: 'POST',
    headers: {
      ...getBaseHeader(),
      'Content-Type': 'application/json;charset=utf-8',
      'Accept': 'text/event-stream',
    },
    body: JSON.stringify(data || {}),
    signal,
  })
  if (!resp.ok) {
    const text = await resp.text().catch(() => '')
    throw new Error(`SSE请求失败(${resp.status}): ${text || resp.statusText}`)
  }
  if (!resp.body) throw new Error('浏览器不支持ReadableStream')

  const reader = resp.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  const emit = (evt) => {
    if (typeof onEvent === 'function') onEvent(evt)
  }

  const parseSseChunk = () => {
    // 兼容 \n\n 与 \r\n\r\n 两种 SSE 分隔
    const sepRegex = /\r?\n\r?\n/
    let match
    while ((match = sepRegex.exec(buffer)) !== null) {
      const idx = match.index
      const sepLen = match[0].length
      const rawEvent = buffer.slice(0, idx)
      buffer = buffer.slice(idx + sepLen)

      const lines = rawEvent.split(/\r?\n/)
      for (const line of lines) {
        if (!line.startsWith('data:')) continue
        const payload = line.slice('data:'.length).trim()
        if (!payload) continue
        try {
          emit(JSON.parse(payload))
        } catch (e) {
          // 非 JSON：也透传出去，便于定位问题
          emit({ type: 'delta', content: payload })
        }
      }
    }
  }

  while (true) {
    const { value, done } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })
    parseSseChunk()
  }

  // 流关闭前再尝试解析一次尾包（防止最后一个事件分隔不完整）
  if (buffer.trim()) {
    const lines = buffer.split(/\r?\n/)
    for (const line of lines) {
      if (!line.startsWith('data:')) continue
      const payload = line.slice('data:'.length).trim()
      if (!payload) continue
      try {
        emit(JSON.parse(payload))
      } catch (e) {
        emit({ type: 'delta', content: payload })
      }
    }
  }
}

// 数据录入提交：前端 -> Java -> Python -> Java(业务接口)
export function dataEntrySubmit(data) {
  return request({
    url: '/fish/zk-ai-agent/dataEntrySubmit',
    method: 'post',
    data: data
  })
}

// 更新客户
export function updateAgent(data) {
  return request({
    url: '/fish/Agent/update',
    method: 'put',
    data: data
  })
}

// 删除客户
export function deleteAgent(id) {
  return request({
    url: '/fish/Agent/delete?id=' + id,
    method: 'delete'
  })
}

// 获得客户
export function getAgent(id) {
  return request({
    url: '/fish/Agent/get?id=' + id,
    method: 'get'
  })
}

// 获得客户
export function getAgentByAgentNo(AgentNo) {
  return request({
    url: '/fish/Agent/getByAgentNo?AgentNo=' + AgentNo,
    method: 'get'
  })
}

// 获得客户分页
export function getAgentPage(query) {
  return request({
    url: '/fish/Agent/page',
    method: 'get',
    params: query
  })
}
export function getAgentPageForMy(query) {
  return request({
    url: '/fish/Agent/pageForMy',
    method: 'get',
    params: query
  })
}
// 获得客户分页
export function listSimpleAgents(query) {
  return request({
    url: '/fish/Agent/list-all-simple',
    method: 'get',
    params: query
  })
}
// 获取客户列表给添加客户人员做提示
export function listForNotice(query) {
  return request({
    url: '/fish/Agent/listForNotice',
    method: 'get',
    params: query
  })
}

export function listSimpleMyAgents(query) {
  return request({
    url: '/fish/Agent/list-all-simple-my',
    method: 'get',
    params: query
  })
}

// 导出客户 Excel
export function exportAgentExcel(query) {
  return request({
    url: '/fish/Agent/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

export function transferAgent(data) {
  return request({
    url: '/fish/Agent/transfer',
    method: 'PUT',
    data: data
  })
}

// 用户状态修改
export function changeAgentStatus(id, enableStatus) {
  const data = {
    id,
    enableStatus
  }
  return request({
    url: '/fish/Agent/update-status',
    method: 'put',
    data: data
  })
}
