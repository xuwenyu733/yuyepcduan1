const { varcharField, textField } = require('./client')
const { NEW, NEW_META } = require('./tables')

const SITE_LINK_FIELDS = {
  pool_code: { comment: '鱼池编号(关联键)', len: 64 },
  pool_name: { comment: '鱼池名称', len: 128 },
  workshop: { comment: '车间', len: 128 },
  system_name: { comment: '系统/产线', len: 128 },
  location_code: { comment: '区域编码', len: 64 },
  batch_code: { comment: '批次号', len: 64 }
}

const CROSS_DB_LINK_FIELDS = {
  source_system: { comment: '来源系统', len: 64 },
  external_id: { comment: '外部库业务ID', len: 128 },
  ref_table: { comment: '关联表名', len: 128 },
  ref_biz_id: { comment: '关联业务ID', len: 64 }
}

function withLinks(base, extras = []) {
  const out = { ...base }
  for (const block of extras) Object.assign(out, block)
  return out
}

function buildRowPayload(tableName, row, columnDefs) {
  const payload = { tableName, recId: '' }
  for (const [key, meta] of Object.entries(columnDefs)) {
    const value = row[key] != null ? row[key] : ''
    if (meta.type === 'text') payload[key] = textField(meta.comment, value)
    else payload[key] = varcharField(meta.comment, value, meta.len || 255)
  }
  return payload
}

const COLUMN_DEFS = {
  [NEW.moduleConfig]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      module_id: { comment: '模块ID', len: 64 },
      module_name: { comment: '模块名称', len: 128 },
      config_json: { comment: '配置JSON', type: 'text' },
      version: { comment: '版本', len: 32 },
      last_saved: { comment: '最近保存', len: 32 }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.aiAlarm]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      alarm_code: { comment: '报警编号', len: 64 },
      alarm_title: { comment: '报警标题', len: 255 },
      category: { comment: '分类', len: 64 },
      priority: { comment: '优先级', len: 32 },
      alarm_status: { comment: '状态', len: 32 },
      metric_code: { comment: '指标编码', len: 64 },
      metric_name: { comment: '指标名称', len: 64 },
      current_value: { comment: '当前值', len: 64 },
      threshold_value: { comment: '阈值', len: 64 },
      unit: { comment: '单位', len: 32 },
      alarm_reason: { comment: '报警原因', type: 'text' },
      ai_suggestion: { comment: 'AI处置建议', type: 'text' },
      confidence: { comment: 'AI置信度', len: 16 },
      alarm_time: { comment: '报警时间', len: 32 },
      is_mock: { comment: '是否Mock(1是)', len: 8 }
    },
    [SITE_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  )
}

const SEED_AI_ALARMS = [
  {
    biz_id: 'TH-AIA-001',
    alarm_code: 'TH-AI-PH-20260723-001',
    alarm_title: 'AI预警：pH偏离阈值',
    category: 'water',
    priority: 'high',
    alarm_status: 'open',
    pool_code: 'A1',
    pool_name: 'A1优化池',
    workshop: '一号车间',
    system_name: '',
    location_code: '',
    batch_code: '',
    metric_code: 'ph',
    metric_name: 'pH',
    current_value: '6.4',
    threshold_value: '7.0-8.5',
    unit: '',
    alarm_reason: '【Mock AI】pH 低于阈值配置下限，疑似水质酸化。',
    ai_suggestion: '核查酸碱缓冲与进水，必要时调节并复测。',
    confidence: '85',
    alarm_time: '2026-07-23 11:00:00',
    is_mock: '1',
    source_system: 'threshold-ai-mock',
    external_id: '',
    ref_table: 'test_table_threshold_config',
    ref_biz_id: ''
  },
  {
    biz_id: 'TH-AIA-002',
    alarm_code: 'TH-AI-DENSE-20260723-002',
    alarm_title: 'AI预警：密集告警触发',
    category: 'alarm',
    priority: 'urgent',
    alarm_status: 'open',
    pool_code: 'B1',
    pool_name: 'B1备用池',
    workshop: '二号车间',
    system_name: '',
    location_code: '',
    batch_code: '',
    metric_code: 'dense',
    metric_name: '密集报警',
    current_value: '6',
    threshold_value: '5/30min',
    unit: '次',
    alarm_reason: '【Mock AI】同区域同类报警短时间触发超过密集阈值。',
    ai_suggestion: '排查系统性风险，合并处理同类告警。',
    confidence: '90',
    alarm_time: '2026-07-23 11:20:00',
    is_mock: '1',
    source_system: 'threshold-ai-mock',
    external_id: '',
    ref_table: '',
    ref_biz_id: ''
  }
]

function emptyBootstrapPayload(tableName) {
  const defs = COLUMN_DEFS[tableName]
  if (!defs) throw new Error(`未知表 ${tableName}`)
  const empty = {}
  for (const key of Object.keys(defs)) empty[key] = ''
  if (defs.biz_id) empty.biz_id = '__bootstrap__'
  if (defs.source_system) empty.source_system = 'threshold-settings'
  return buildRowPayload(tableName, empty, defs)
}

function forminfoPayload(meta) {
  return {
    tableName: 'forminfo',
    recId: '',
    tbl_name: varcharField('表名', meta.table, 100),
    tbl_name_cn: varcharField('中文表名', meta.cn, 100),
    tbl_memo: varcharField('备注', meta.cn, 100),
    tbl_design_html: textField('设计HTML', ''),
    tbl_deploy_html: textField('部署HTML', ''),
    bizVarJs: textField('业务脚本', '')
  }
}

module.exports = {
  NEW_META,
  COLUMN_DEFS,
  SEED_AI_ALARMS,
  SITE_LINK_FIELDS,
  CROSS_DB_LINK_FIELDS,
  buildRowPayload,
  emptyBootstrapPayload,
  forminfoPayload
}
