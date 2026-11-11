#!/usr/bin/env node
/**
 * 仅创建/校验鱼病专用 fd_* 新表并写入种子数据。
 * 绝不修改既有表结构与既有数据。
 */
const { bootstrapAll } = require('../src/pmis/bootstrap')
const { isEnabled } = require('../src/pmis/config')

async function main() {
  if (!isEnabled()) {
    console.error('PMIS_ENABLED=false，退出')
    process.exit(1)
  }
  const report = await bootstrapAll()
  console.log(JSON.stringify(report, null, 2))
}

main().catch(err => {
  console.error(err)
  process.exit(1)
})
