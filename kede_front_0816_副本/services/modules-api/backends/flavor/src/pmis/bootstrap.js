const { queryOne, saveInfo, execute, esc } = require('./client')
const { NEW, NEW_META } = require('./tables')
const {
  COLUMN_DEFS,
  SEED_DICTS,
  SEED_PONDS,
  SEED_AI_ALARMS,
  buildRowPayload,
  emptyBootstrapPayload,
  forminfoPayload
} = require('./schema')

async function tableExists(tableName) {
  const rows = await queryOne(
    `select table_name from information_schema.tables where table_schema='datong' and table_name='${esc(tableName)}' limit 1`
  )
  return rows.length > 0
}

async function forminfoExists(tableName) {
  const rows = await queryOne(
    `select Id from forminfo where isDel=0 and tbl_name='${esc(tableName)}' limit 1`
  )
  return rows.length > 0
}

async function countActive(tableName) {
  try {
    const rows = await queryOne(
      `select count(*) as cnt from ${tableName} where isDel=0 and (biz_id is null or biz_id <> '__bootstrap__') and (biz_id is null or biz_id <> '__link_migrate__')`
    )
    const cnt = rows[0] && (rows[0].cnt != null ? rows[0].cnt : rows[0].CNT)
    return Number(cnt) || 0
  } catch (e) {
    return 0
  }
}

async function listColumns(tableName) {
  const rows = await queryOne(
    `select column_name from information_schema.columns where table_schema='datong' and table_name='${esc(tableName)}'`
  )
  const set = new Set()
  for (const r of rows) {
    const name = r.COLUMN_NAME || r.column_name
    if (name) set.add(String(name).toLowerCase())
  }
  return set
}

async function ensureForminfo(meta) {
  if (await forminfoExists(meta.table)) return { created: false }
  await saveInfo(forminfoPayload(meta))
  return { created: true }
}

async function ensurePhysicalTable(tableName) {
  if (await tableExists(tableName)) return { created: false }
  await saveInfo(emptyBootstrapPayload(tableName))
  try {
    await execute([`update ${tableName} set isDel=1 where biz_id='__bootstrap__'`])
  } catch (e) {
    /* ignore */
  }
  return { created: true }
}

async function ensureLinkColumns(tableName) {
  const defs = COLUMN_DEFS[tableName]
  if (!defs) return { added: [] }
  const existing = await listColumns(tableName)
  const missing = Object.keys(defs).filter(k => !existing.has(k.toLowerCase()))
  if (!missing.length) return { added: [] }
  const row = { biz_id: '__link_migrate__' }
  if (defs.source_system) row.source_system = 'flavor-opt'
  for (const key of missing) {
    if (key !== 'biz_id') row[key] = ''
  }
  const partialDefs = { biz_id: defs.biz_id }
  for (const key of missing) partialDefs[key] = defs[key]
  await saveInfo(buildRowPayload(tableName, row, partialDefs))
  try {
    await execute([`update ${tableName} set isDel=1 where biz_id='__link_migrate__'`])
  } catch (e) {
    /* ignore */
  }
  return { added: missing }
}

async function seedIfEmpty(tableName, rows) {
  if (!rows || !rows.length) return { seeded: 0 }
  const cnt = await countActive(tableName)
  if (cnt > 0) return { seeded: 0 }
  const defs = COLUMN_DEFS[tableName]
  let seeded = 0
  for (const row of rows) {
    await saveInfo(buildRowPayload(tableName, row, defs))
    seeded += 1
  }
  return { seeded }
}

async function bootstrapAll() {
  const report = []
  for (const meta of NEW_META) {
    const item = { table: meta.table, cn: meta.cn }
    item.forminfo = await ensureForminfo(meta)
    item.physical = await ensurePhysicalTable(meta.table)
    item.linkColumns = await ensureLinkColumns(meta.table)
    report.push(item)
  }

  report.push({ seed: 'dict', ...(await seedIfEmpty(NEW.dict, SEED_DICTS)) })
  report.push({ seed: 'pond', ...(await seedIfEmpty(NEW.pond, SEED_PONDS)) })
  report.push({ seed: 'aiAlarms', ...(await seedIfEmpty(NEW.aiAlarm, SEED_AI_ALARMS || [])) })

  const cfgCnt = await countActive(NEW.sysConfig)
  if (cfgCnt === 0) {
    await saveInfo(
      buildRowPayload(
        NEW.sysConfig,
        {
          biz_id: 'CFG-001',
          settings_json: JSON.stringify({ weight: 'kg', length: 'cm' }),
          source_system: 'flavor-opt'
        },
        COLUMN_DEFS[NEW.sysConfig]
      )
    )
    report.push({ seed: 'sysConfig', seeded: 1 })
  }

  return report
}

module.exports = {
  bootstrapAll,
  tableExists,
  ensurePhysicalTable,
  ensureForminfo,
  ensureLinkColumns
}
