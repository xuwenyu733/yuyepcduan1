/**
 * 本地 Socket.IO 服务（port 9091）
 * 对齐后端 application-*.yaml 中的 socketio.port，以及前端 VUE_APP_SOCKET_BACE_URL。
 * 仅保证握手/在线，满足本地开发不再刷 ERR_CONNECTION_REFUSED；完整音视频信令需对接真实 IM 服务。
 */
const http = require('http')
const server = http.createServer((req, res) => {
  if (req.url === '/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' })
    res.end(JSON.stringify({ code: 0, message: 'ok', port: PORT }))
    return
  }
  res.writeHead(200, { 'Content-Type': 'text/plain' })
  res.end('kede socket-server')
})

const io = require('socket.io')(server, {
  pingTimeout: 60000,
  pingInterval: 25000,
  allowEIO3: true
})

const PORT = Number(process.env.SOCKET_PORT || 9091)

io.on('connection', (socket) => {
  const uid = (socket.handshake.query && socket.handshake.query.uid) || 'unknown'
  const deviceType = (socket.handshake.query && socket.handshake.query.deviceType) || ''
  console.log(`[socket] connected uid=${uid} deviceType=${deviceType} id=${socket.id}`)

  socket.on('sendMessage', (payload) => {
    // 回显，避免前端挂起
    socket.emit('receiveMessage', payload)
  })

  socket.on('disconnect', (reason) => {
    console.log(`[socket] disconnected uid=${uid} reason=${reason}`)
  })
})

server.listen(PORT, process.env.HOST || '0.0.0.0', () => {
  console.log(`[socket-server] listening on http://127.0.0.1:${PORT}`)
})
