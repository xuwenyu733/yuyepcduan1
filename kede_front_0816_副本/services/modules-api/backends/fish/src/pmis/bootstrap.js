const { queryOne, saveInfo, execute, esc } = require('./client')
const { NEW, NEW_META } = require('./tables')
const {
  COLUMN_DEFS,
  SEED,
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
      `select count(*) as cnt from ${tableName} where isDel=0 and (biz_id is null or biz_id <> '__bootstrap__')`
    )
    const cnt = rows[0] && (rows[0].cnt != null ? rows[0].cnt : rows[0].CNT)
    return Number(cnt) || 0
  } catch (e) {
    return 0
  }
}

async function ensureForminfo(meta) {
  if (await forminfoExists(meta.table)) return { created: false }
  await saveInfo(forminfoPayload(meta))
  return { created: true }
}

async function ensurePhysicalTable(tableName) {
  if (await tableExists(tableName)) return { created: false }
  await saveInfo(emptyBootstrapPayload(tableName))
  // soft-delete bootstrap row so it won't pollute lists
  try {
    await execute([
      `update ${tableName} set isDel=1 where biz_id='__bootstrap__'`
    ])
  } catch (e) {
    // ignore if column not yet queryable
  }
  return { created: true }
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

/**
 * 仅为 fd_* 新表补齐关联字段（不改既有业务表）。
 * 通过 saveInfo1 声明缺失列，与 PMIS 加列机制一致。
 */
async function ensureLinkColumns(tableName) {
  const defs = COLUMN_DEFS[tableName]
  if (!defs) return { added: [] }
  const existing = await listColumns(tableName)
  const missing = Object.keys(defs).filter(k => !existing.has(k.toLowerCase()))
  if (!missing.length) return { added: [] }

  const row = { biz_id: '__link_migrate__' }
  if (defs.source_system) row.source_system = 'fish-disease'
  for (const key of missing) {
    if (key !== 'biz_id') row[key] = ''
  }
  // 带上 biz_id + 缺失列的类型定义，触发加列
  const partialDefs = { biz_id: defs.biz_id }
  for (const key of missing) partialDefs[key] = defs[key]
  await saveInfo(buildRowPayload(tableName, row, partialDefs))
  try {
    await execute([
      `update ${tableName} set isDel=1 where biz_id='__link_migrate__'`
    ])
  } catch (e) {
    // ignore
  }
  return { added: missing }
}

async function seedIfEmpty(tableName, rows, mapRow) {
  if (!rows || !rows.length) return { seeded: 0 }
  const cnt = await countActive(tableName)
  if (cnt > 0) return { seeded: 0 }
  const defs = COLUMN_DEFS[tableName]
  let seeded = 0
  for (const row of rows) {
    const payload = buildRowPayload(tableName, mapRow ? mapRow(row) : row, defs)
    await saveInfo(payload)
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

  // seed core dictionaries / knowledge (only into NEW tables)
  report.push({
    seed: 'species',
    ...(await seedIfEmpty(NEW.species, SEED.species))
  })
  report.push({
    seed: 'diseaseTypes',
    ...(await seedIfEmpty(NEW.diseaseTypes, SEED.diseaseTypes))
  })
  report.push({
    seed: 'equipments',
    ...(await seedIfEmpty(NEW.equipments, SEED.equipments))
  })
  report.push({
    seed: 'diseaseKnowledge',
    ...(await seedIfEmpty(NEW.diseaseKnowledge, SEED.diseaseKnowledge))
  })
  report.push({
    seed: 'aiAlarms',
    ...(await seedIfEmpty(NEW.aiAlarm, SEED.aiAlarms || []))
  })

  // optional settings row
  const settingsCnt = await countActive(NEW.systemSettings)
  if (settingsCnt === 0) {
    await saveInfo(
      buildRowPayload(
        NEW.systemSettings,
        {
          biz_id: 'SETTINGS-001',
          settings_json: JSON.stringify({
            dataRetentionDays: 365,
            uploadLimitMb: 50,
            autoBackup: true,
            notifyMethod: 'email',
            notifyNewTask: true,
            notifyDiagnosisDone: true,
            notifyAbnormalData: true,
            deviceApiUrl: '',
            deviceApiKey: '',
            syncIntervalSeconds: 60,
            version: 'v1.0.0'
          }),
          source_system: 'fish-disease'
        },
        COLUMN_DEFS[NEW.systemSettings]
      )
    )
    report.push({ seed: 'systemSettings', seeded: 1 })
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
