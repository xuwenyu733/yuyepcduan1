#!/usr/bin/env node
const { bootstrapAll } = require('../src/pmis/bootstrap')
const { isEnabled } = require('../src/pmis/config')

async function main() {
  if (!isEnabled()) {
    console.error('PMIS_ENABLED=false')
    process.exit(1)
  }
  console.log(JSON.stringify(await bootstrapAll(), null, 2))
}

main().catch(err => {
  console.error(err)
  process.exit(1)
})
