#!/usr/bin/env node
/**
 * 跨平台设置 NODE_OPTIONS=--openssl-legacy-provider 后执行命令。
 * 用法: node scripts/run-with-openssl.js vue-cli-service serve --mode local
 */
const { spawn } = require('child_process')

const args = process.argv.slice(2)
if (!args.length) {
  console.error('Usage: node scripts/run-with-openssl.js <command> [args...]')
  process.exit(1)
}

const existing = process.env.NODE_OPTIONS || ''
const flag = '--openssl-legacy-provider'
const nodeOptions = existing.includes(flag)
  ? existing
  : [existing, flag].filter(Boolean).join(' ').trim()

const child = spawn(args[0], args.slice(1), {
  stdio: 'inherit',
  shell: true,
  env: { ...process.env, NODE_OPTIONS: nodeOptions }
})

child.on('exit', code => process.exit(code == null ? 1 : code))
child.on('error', err => {
  console.error(err)
  process.exit(1)
})
