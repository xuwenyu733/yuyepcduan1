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
  [NEW.sysConfig]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      settings_json: { comment: '配置JSON', type: 'text' }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.dict]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      dict_type: { comment: '字典类型', len: 64 },
      dict_value: { comment: '字典值', len: 128 },
      sort_no: { comment: '排序', len: 16 },
      enabled: { comment: '启用', len: 8 }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.pond]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      code: { comment: '鱼池编号', len: 64 },
      size: { comment: '面积', len: 64 },
      location: { comment: '位置', len: 128 },
      manager: { comment: '负责人', len: 64 },
      status: { comment: '状态', len: 32 },
      remark: { comment: '备注', type: 'text' },
      created_at: { comment: '创建时间', len: 32 }
    },
    [SITE_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  ),
  [NEW.purchase]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      category: { comment: '品类', len: 64 },
      purchase_date: { comment: '采购日期', len: 32 },
      weight: { comment: '重量kg', len: 32 },
      unit_price: { comment: '单价', len: 32 },
      unit_weight: { comment: '单尾重', len: 32 },
      amount: { comment: '金额', len: 32 },
      manager: { comment: '负责人', len: 64 },
      source: { comment: '来源', len: 128 },
      address: { comment: '地址', len: 255 },
      contact: { comment: '联系人', len: 64 },
      phone: { comment: '电话', len: 32 },
      transport_method: { comment: '运输方式', len: 64 },
      remark: { comment: '备注', type: 'text' },
      created_at: { comment: '创建时间', len: 32 }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.entry]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      record_no: { comment: '批次号', len: 64 },
      purchase_biz_id: { comment: '采购业务ID', len: 64 },
      category: { comment: '品类', len: 64 },
      source: { comment: '来源', len: 128 },
      entry_time: { comment: '入池时间', len: 32 },
      total_weight: { comment: '总重', len: 32 },
      unit_weight: { comment: '单尾重', len: 32 },
      quantity: { comment: '尾数', len: 32 },
      density: { comment: '密度', len: 32 },
      unit_price: { comment: '单价', len: 32 },
      manager: { comment: '负责人', len: 64 },
      pond_code: { comment: '鱼池编号(兼容)', len: 64 },
      receiver: { comment: '收货人', len: 64 },
      opt_level: { comment: '优化等级', len: 64 },
      report_file: { comment: '报告文件', len: 255 },
      pond_capacity: { comment: '池容量', len: 32 },
      estimated_loss_rate: { comment: '预估掉秤率', len: 32 },
      appearance: { comment: '外观', len: 32 },
      vitality: { comment: '活力', len: 32 },
      health: { comment: '健康', len: 32 },
      uniformity: { comment: '整齐度', len: 32 },
      stress: { comment: '应激', len: 32 },
      survival_rate: { comment: '存活率', len: 32 },
      risk_level: { comment: '风险', len: 32 },
      assessor: { comment: '评估人', len: 64 },
      eval_remark: { comment: '评估备注', type: 'text' },
      purchase_amount: { comment: '采购金额', len: 32 },
      estimated_loss: { comment: '预估掉秤', len: 32 },
      estimated_net: { comment: '预估净重', len: 32 },
      status: { comment: '状态', len: 32 },
      death_count: { comment: '死亡尾数', len: 32 },
      death_weight: { comment: '死亡重量', len: 32 },
      exit_weight: { comment: '出池重量', len: 32 },
      sales_amount: { comment: '销售额', len: 32 },
      labor_cost: { comment: '人工成本', len: 32 },
      electricity_cost: { comment: '电费', len: 32 },
      feed_cost: { comment: '饲料成本', len: 32 },
      protect_cost: { comment: '防护成本', len: 32 },
      exit_appearance: { comment: '出池外观', len: 32 },
      exit_vitality: { comment: '出池活力', len: 32 },
      exit_health: { comment: '出池健康', len: 32 },
      exit_uniformity: { comment: '出池整齐度', len: 32 },
      exit_time: { comment: '出池时间', len: 32 },
      remark: { comment: '备注', type: 'text' },
      created_at: { comment: '创建时间', len: 32 }
    },
    [SITE_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  ),
  [NEW.optLog]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      entry_biz_id: { comment: '入池业务ID', len: 64 },
      record_no: { comment: '批次号', len: 64 },
      pond_code: { comment: '鱼池编号(兼容)', len: 64 },
      category: { comment: '品类', len: 64 },
      measure_date: { comment: '监测日期', len: 32 },
      death_count: { comment: '死亡尾数', len: 32 },
      death_weight: { comment: '死亡重量', len: 32 },
      water_temp: { comment: '水温', len: 32 },
      dissolved_oxygen: { comment: '溶氧', len: 32 },
      appearance: { comment: '外观', len: 32 },
      vitality: { comment: '活力', len: 32 },
      health: { comment: '健康', len: 32 },
      uniformity: { comment: '整齐度', len: 32 },
      measure: { comment: '措施', type: 'text' },
      operator: { comment: '操作人', len: 64 },
      created_at: { comment: '创建时间', len: 32 }
    },
    [SITE_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  ),
  [NEW.aiAlarm]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      alarm_code: { comment: '报警编号', len: 64 },
      alarm_title: { comment: '报警标题', len: 255 },
      category: { comment: '分类(water/process/quality)', len: 64 },
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
      entry_biz_id: { comment: '关联入池业务ID', len: 64 },
      opt_biz_id: { comment: '关联优化日志ID', len: 64 },
      alarm_time: { comment: '报警时间', len: 32 },
      is_mock: { comment: '是否Mock(1是)', len: 8 }
    },
    [SITE_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  )
}

const SEED_DICTS = [
  ...['鲈鱼', '大黄鱼', '石斑鱼', '黄花鱼', '鲷鱼'].map((v, i) => ({
    biz_id: `DICT-CAT-${i + 1}`,
    dict_type: 'category',
    dict_value: v,
    sort_no: String(i + 1),
    enabled: '1',
    source_system: 'flavor-opt'
  })),
  ...['自有养殖场', '合作供应商A', '合作供应商B', '近海捕捞'].map((v, i) => ({
    biz_id: `DICT-SRC-${i + 1}`,
    dict_type: 'source',
    dict_value: v,
    sort_no: String(i + 1),
    enabled: '1',
    source_system: 'flavor-opt'
  })),
  ...['0.3-0.5kg/尾', '0.5-0.8kg/尾', '0.8-1.2kg/尾', '1.2-1.5kg/尾', '1.5kg以上/尾'].map((v, i) => ({
    biz_id: `DICT-GRD-${i + 1}`,
    dict_type: 'grade',
    dict_value: v,
    sort_no: String(i + 1),
    enabled: '1',
    source_system: 'flavor-opt'
  })),
  ...['冷链运输车', '活水车', '空运', '海运', '其他'].map((v, i) => ({
    biz_id: `DICT-TRN-${i + 1}`,
    dict_type: 'transport',
    dict_value: v,
    sort_no: String(i + 1),
    enabled: '1',
    source_system: 'flavor-opt'
  }))
]

const SEED_PONDS = [
  {
    biz_id: 'POND-001',
    code: 'A1',
    pool_code: 'A1',
    size: '120㎡',
    location: '一号车间',
    workshop: '一号车间',
    manager: '张强',
    status: 'in_use',
    remark: '主优化池',
    source_system: 'flavor-opt',
    created_at: '2024-03-01 09:00:00'
  },
  {
    biz_id: 'POND-002',
    code: 'A2',
    pool_code: 'A2',
    size: '100㎡',
    location: '一号车间',
    workshop: '一号车间',
    manager: '李敏',
    status: 'idle',
    remark: '',
    source_system: 'flavor-opt',
    created_at: '2024-03-01 09:10:00'
  },
  {
    biz_id: 'POND-003',
    code: 'B1',
    pool_code: 'B1',
    size: '150㎡',
    location: '二号车间',
    workshop: '二号车间',
    manager: '王磊',
    status: 'idle',
    remark: '备用池',
    source_system: 'flavor-opt',
    created_at: '2024-03-02 10:00:00'
  }
]

const SEED_AI_ALARMS = [
  {
    biz_id: 'FO-AIA-001',
    alarm_code: 'FO-AI-DO-20260723-001',
    alarm_title: 'AI预警：优化池溶氧偏低',
    category: 'water',
    priority: 'high',
    alarm_status: 'open',
    pool_code: 'A1',
    pool_name: 'A1优化池',
    workshop: '一号车间',
    system_name: '',
    location_code: '',
    batch_code: '',
    metric_code: 'do',
    metric_name: '溶氧',
    current_value: '4.5',
    threshold_value: '5.0',
    unit: 'mg/L',
    alarm_reason: '【Mock AI】优化监测溶氧低于建议下限，可能影响风味蓄养效果。',
    ai_suggestion: '加大增氧，暂缓加料，1小时后复测。',
    confidence: '84',
    entry_biz_id: '',
    opt_biz_id: '',
    alarm_time: '2026-07-23 09:20:00',
    is_mock: '1',
    source_system: 'flavor-opt-ai-mock',
    external_id: '',
    ref_table: '',
    ref_biz_id: ''
  },
  {
    biz_id: 'FO-AIA-002',
    alarm_code: 'FO-AI-DEATH-20260723-002',
    alarm_title: 'AI预警：日死亡偏高',
    category: 'process',
    priority: 'urgent',
    alarm_status: 'open',
    pool_code: 'A1',
    pool_name: 'A1优化池',
    workshop: '一号车间',
    system_name: '',
    location_code: '',
    batch_code: '',
    metric_code: 'death',
    metric_name: '日死亡尾数',
    current_value: '12',
    threshold_value: '5',
    unit: '尾',
    alarm_reason: '【Mock AI】单日死亡超过阈值，疑似应激或水质波动。',
    ai_suggestion: '排查运输应激与水质指标，隔离弱鱼并记录措施。',
    confidence: '88',
    entry_biz_id: '',
    opt_biz_id: '',
    alarm_time: '2026-07-23 10:05:00',
    is_mock: '1',
    source_system: 'flavor-opt-ai-mock',
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
  if (defs.source_system) empty.source_system = 'flavor-opt'
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
  SEED_DICTS,
  SEED_PONDS,
  SEED_AI_ALARMS,
  SITE_LINK_FIELDS,
  CROSS_DB_LINK_FIELDS,
  buildRowPayload,
  emptyBootstrapPayload,
  forminfoPayload
}
