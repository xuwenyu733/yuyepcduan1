const { WebSocketServer } = require('ws')
const store = require('../data')

function send(ws, payload) {
  if (ws.readyState === ws.OPEN) {
    ws.send(JSON.stringify(payload))
  }
}

function broadcast(wss, caseId, payload, except) {
  const data = JSON.stringify(payload)
  wss.clients.forEach(client => {
    if (client !== except && client.readyState === client.OPEN && client.caseId === caseId) {
      client.send(data)
    }
  })
}

function attachChatWebSocket(server) {
  const wss = new WebSocketServer({ server, path: '/ws/chat' })

  wss.on('connection', ws => {
    ws.isAlive = true
    ws.caseId = null
    ws.role = 'expert'
    ws.senderName = '陈专家'

    ws.on('pong', () => {
      ws.isAlive = true
    })

    send(ws, { type: 'ready', message: 'WebSocket connected' })

    ws.on('message', raw => {
      ;(async () => {
        let data
        try {
          data = JSON.parse(String(raw))
        } catch (e) {
          send(ws, { type: 'error', message: '消息格式错误' })
          return
        }

        const type = data.type

        if (type === 'ping') {
          send(ws, { type: 'pong' })
          return
        }

        if (type === 'join') {
          const caseId = data.caseId
          if (!caseId) {
            send(ws, { type: 'error', message: '缺少 caseId' })
            return
          }
          const record = await store.getDiagnosis(caseId)
          if (!record) {
            send(ws, { type: 'error', message: '诊断记录不存在' })
            return
          }
          ws.caseId = caseId
          ws.role = data.role || 'expert'
          ws.senderName = data.senderName || (ws.role === 'expert' ? '陈专家' : '工人')
          send(ws, {
            type: 'joined',
            caseId,
            record
          })
          return
        }

        if (type === 'leave') {
          ws.caseId = null
          send(ws, { type: 'left' })
          return
        }

        if (type === 'chat') {
          const caseId = data.caseId || ws.caseId
          const content = (data.content || data.message || '').trim()
          if (!caseId) {
            send(ws, { type: 'error', message: '请先加入诊断任务' })
            return
          }
          if (!content) {
            send(ws, { type: 'error', message: '消息不能为空' })
            return
          }

          const result = await store.appendDiagnosisMessage(caseId, {
            content,
            role: data.role || ws.role || 'expert',
            senderId: data.senderId || '',
            senderName: data.senderName || ws.senderName || '陈专家',
            toUserId: data.toUserId || '',
            toName: data.toName || '',
            msgType: data.msgType || 'text',
            fileName: data.fileName || '',
            fileUrl: data.fileUrl || '',
            fileSize: data.fileSize || 0,
            duration: data.duration || 0
          })

          if (!result) {
            send(ws, { type: 'error', message: '诊断记录不存在' })
            return
          }

          const payload = {
            type: 'message',
            caseId,
            message: result.message,
            record: result.record
          }

          send(ws, payload)
          broadcast(wss, caseId, payload, ws)
          return
        }

        send(ws, { type: 'error', message: `未知消息类型: ${type}` })
      })().catch(err => {
        console.error('[ws]', err)
        send(ws, { type: 'error', message: err.message || '服务器错误' })
      })
    })

    ws.on('close', () => {
      ws.caseId = null
    })
  })

  const heartbeat = setInterval(() => {
    wss.clients.forEach(ws => {
      if (!ws.isAlive) {
        ws.terminate()
        return
      }
      ws.isAlive = false
      ws.ping()
    })
  }, 30000)

  wss.on('close', () => clearInterval(heartbeat))

  console.log('WebSocket chat attached at /ws/chat')
  return wss
}

module.exports = { attachChatWebSocket }
