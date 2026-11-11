#!/usr/bin/env node
/**
 * 同时启动：三模块 BFF (3020) + Socket.IO (9091) + 前端 local
 * 若对应端口已有可用服务，则复用。
 */
const { spawn } = require('child_process')
const http = require('http')
const path = require('path')

const root = path.join(__dirname, '..')
const children = []
let shuttingDown = false

function run(name, command, args, opts = {}) {
  const child = spawn(command, args, {
    cwd: opts.cwd || root,
    stdio: 'inherit',
    shell: true,
    env: { ...process.env, ...opts.env }
  })
  child.on('exit', code => {
    if (shuttingDown) return
    // 仅当前端退出时结束；API 异常退出不拖死前端
    if (name === 'web') {
      console.log(`[web] exited with code ${code}`)
      shutdown(code || 0)
    } else {
      console.log(`[${name}] exited with code ${code}（前端继续运行）`)
    }
  })
  children.push(child)
  return child
}

function shutdown(code = 0) {
  if (shuttingDown) return
  shuttingDown = true
  children.forEach(c => {
    try {
      c.kill('SIGTERM')
    } catch (e) {}
  })
  setTimeout(() => process.exit(code), 400)
}

process.on('SIGINT', () => shutdown(0))
process.on('SIGTERM', () => shutdown(0))

function checkHttpOk(url, validate) {
  return new Promise(resolve => {
    const req = http.get(url, res => {
      let body = ''
      res.on('data', c => { body += c })
      res.on('end', () => {
        try {
          resolve(validate ? validate(res.statusCode, body) : res.statusCode === 200)
        } catch (e) {
          resolve(false)
        }
      })
    })
    req.on('error', () => resolve(false))
    req.setTimeout(1500, () => {
      req.destroy()
      resolve(false)
    })
  })
}

function checkModulesApi() {
  return checkHttpOk('http://127.0.0.1:3020/api/health', (_code, body) => {
    try {
      const json = JSON.parse(body)
      return json && json.code === 0
    } catch (e) {
      return false
    }
  })
}

function checkSocketServer() {
  return checkHttpOk('http://127.0.0.1:9091/health', (code, body) => {
    if (code !== 200) return false
    try {
      const json = JSON.parse(body)
      return json && json.code === 0
    } catch (e) {
      return true
    }
  })
}

;(async () => {
  const apiOk = await checkModulesApi()
  if (apiOk) {
    console.log('[local] modules-api 已在 :3020 运行，复用现有服务')
  } else {
    console.log('[local] starting modules-api :3020 ...')
    run('api', 'npm', ['--prefix', 'services/modules-api', 'start'])
  }

  const socketOk = await checkSocketServer()
  if (socketOk) {
    console.log('[local] socket-server 已在 :9091 运行，复用现有服务')
  } else {
    console.log('[local] starting socket-server :9091 ...')
    run('socket', 'npm', ['start'], { cwd: path.join(root, 'services/socket-server') })
  }

  // 等附属服务起来再起前端，减少抢端口噪音
  await new Promise(r => setTimeout(r, 800))

  console.log('[local] starting frontend ...')
  run('web', 'vue-cli-service', ['serve', '--mode', 'local'], {
    env: { NODE_OPTIONS: '--openssl-legacy-provider' }
  })
})()
