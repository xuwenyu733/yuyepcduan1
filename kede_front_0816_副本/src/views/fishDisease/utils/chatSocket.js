function resolveWsUrl() {
  if (process.env.VUE_APP_FISH_WS_URL) {
    return String(process.env.VUE_APP_FISH_WS_URL).trim().replace(/^['"]|['"]$/g, '')
  }
  let base = process.env.VUE_APP_MODULES_API
  if (base == null || String(base).trim() === '') {
    // 与 HTTP 入口一致：默认相对路径，由当前站点反代，禁止写死 localhost
    base = '/modules-api'
  }
  base = String(base).trim().replace(/^['"]|['"]$/g, '').replace(/\/$/, '')

  // http(s)://host:port → ws(s)://host:port/ws/chat
  if (/^https?:\/\//i.test(base)) {
    return base.replace(/^http/i, 'ws') + '/ws/chat'
  }
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  return `${protocol}//${window.location.host}${base}/ws/chat`
}

export default class ChatSocket {
  constructor() {
    this.ws = null
    this.url = resolveWsUrl()
    this.handlers = new Set()
    this.reconnectTimer = null
    this.manualClose = false
    this.joinedCaseId = null
    this.senderName = '陈专家'
    this.role = 'expert'
    this.reconnectAttempts = 0
  }

  connect() {
    if (this.ws && (this.ws.readyState === WebSocket.OPEN || this.ws.readyState === WebSocket.CONNECTING)) {
      return
    }

    this.manualClose = false
    this.ws = new WebSocket(this.url)

    this.ws.onopen = () => {
      this.reconnectAttempts = 0
      this.emit({ type: 'socket_open' })
      if (this.joinedCaseId) {
        this.join(this.joinedCaseId, {
          role: this.role,
          senderName: this.senderName
        })
      }
    }

    this.ws.onmessage = event => {
      let data
      try {
        data = JSON.parse(event.data)
      } catch (e) {
        return
      }
      this.emit(data)
    }

    this.ws.onclose = () => {
      this.emit({ type: 'socket_close' })
      this.ws = null
      if (!this.manualClose) this.scheduleReconnect()
    }

    this.ws.onerror = () => {
      this.emit({ type: 'socket_error', message: 'WebSocket 连接异常' })
    }
  }

  scheduleReconnect() {
    if (this.reconnectTimer) return
    const delay = Math.min(1000 * 2 ** this.reconnectAttempts, 10000)
    this.reconnectAttempts += 1
    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = null
      this.connect()
    }, delay)
  }

  close() {
    this.manualClose = true
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  on(handler) {
    this.handlers.add(handler)
    return () => this.handlers.delete(handler)
  }

  emit(data) {
    this.handlers.forEach(fn => {
      try {
        fn(data)
      } catch (e) {
        // ignore handler errors
      }
    })
  }

  send(payload) {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      this.connect()
      return false
    }
    this.ws.send(JSON.stringify(payload))
    return true
  }

  join(caseId, options = {}) {
    this.joinedCaseId = caseId
    this.role = options.role || this.role
    this.senderName = options.senderName || this.senderName
    return this.send({
      type: 'join',
      caseId,
      role: this.role,
      senderName: this.senderName
    })
  }

  leave() {
    const caseId = this.joinedCaseId
    this.joinedCaseId = null
    if (caseId) this.send({ type: 'leave', caseId })
  }

  chat(content, options = {}) {
    return this.send({
      type: 'chat',
      caseId: options.caseId || this.joinedCaseId,
      content,
      role: options.role || this.role,
      senderId: options.senderId || '',
      senderName: options.senderName || this.senderName,
      toUserId: options.toUserId || '',
      toName: options.toName || '',
      msgType: options.msgType || 'text',
      fileName: options.fileName || '',
      fileUrl: options.fileUrl || '',
      fileSize: options.fileSize || 0,
      duration: options.duration || 0
    })
  }

  get connected() {
    return !!(this.ws && this.ws.readyState === WebSocket.OPEN)
  }
}
