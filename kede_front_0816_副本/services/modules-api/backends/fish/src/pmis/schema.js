const { varcharField, textField } = require('./client')
const { NEW, NEW_META } = require('./tables')

function fields(defs) {
  const out = {}
  for (const [key, def] of Object.entries(defs)) {
    if (def.type === 'text') out[key] = textField(def.comment, def.value)
    else out[key] = varcharField(def.comment, def.value, def.len || 255)
  }
  return out
}

/** 与 PMIS 既有养殖表同名，用于 JOIN */
const SITE_LINK_FIELDS = {
  pool_code: { comment: '鱼池编号(关联键)', len: 64 },
  pool_name: { comment: '鱼池名称', len: 128 },
  workshop: { comment: '车间', len: 128 },
  system_name: { comment: '系统/产线', len: 128 },
  location_code: { comment: '区域编码', len: 64 },
  batch_code: { comment: '批次号', len: 64 }
}

/** 跨库/跨系统关联预留 */
const CROSS_DB_LINK_FIELDS = {
  source_system: { comment: '来源系统', len: 64 },
  external_id: { comment: '外部库业务ID', len: 128 },
  ref_table: { comment: '关联表名', len: 128 },
  ref_biz_id: { comment: '关联业务ID', len: 64 }
}

const SPECIES_LINK_FIELDS = {
  species_biz_id: { comment: '品种业务ID', len: 64 },
  fish_species: { comment: '品种名称(对齐存量表)', len: 100 }
}

function withLinks(base, extras = []) {
  const out = { ...base }
  for (const block of extras) Object.assign(out, block)
  return out
}

const SEED = {
  species: [
    {
      biz_id: 'SP-001',
      name: '鲈鱼',
      fish_species: '鲈鱼',
      water_type: 'marine',
      source_system: 'fish-disease',
      created_at: '2024-03-01 09:00:00'
    },
    {
      biz_id: 'SP-002',
      name: '石斑鱼',
      fish_species: '石斑鱼',
      water_type: 'marine',
      source_system: 'fish-disease',
      created_at: '2024-03-01 09:00:00'
    },
    {
      biz_id: 'SP-003',
      name: '草鱼',
      fish_species: '草鱼',
      water_type: 'freshwater',
      source_system: 'fish-disease',
      created_at: '2024-03-01 09:00:00'
    },
    {
      biz_id: 'SP-004',
      name: '大黄鱼',
      fish_species: '大黄鱼',
      water_type: 'marine',
      source_system: 'fish-disease',
      created_at: '2024-03-05 09:00:00'
    },
    {
      biz_id: 'SP-005',
      name: '对虾',
      fish_species: '对虾',
      water_type: 'marine',
      source_system: 'fish-disease',
      created_at: '2024-03-05 09:00:00'
    },
    {
      biz_id: 'SP-006',
      name: '罗非鱼',
      fish_species: '罗非鱼',
      water_type: 'freshwater',
      source_system: 'fish-disease',
      created_at: '2024-03-08 09:00:00'
    }
  ],
  diseaseTypes: [
    {
      biz_id: 'DT-001',
      name: '白点病',
      category: '寄生虫',
      description: '',
      source_system: 'fish-disease',
      created_at: '2024-03-01 09:00:00'
    },
    {
      biz_id: 'DT-002',
      name: '烂鳃病',
      category: '细菌',
      description: '',
      source_system: 'fish-disease',
      created_at: '2024-03-01 09:00:00'
    },
    {
      biz_id: 'DT-003',
      name: '肠炎',
      category: '细菌',
      description: '',
      source_system: 'fish-disease',
      created_at: '2024-03-01 09:00:00'
    }
  ],
  equipments: [
    {
      biz_id: 'EQ-001',
      code: 'DO-001',
      device_code: 'DO-001',
      name: '溶氧监测',
      type: '溶氧监测',
      frequency: '5分钟',
      location: '',
      source_system: 'fish-disease',
      created_at: '2024-03-01 09:00:00'
    },
    {
      biz_id: 'EQ-002',
      code: 'PH-001',
      device_code: 'PH-001',
      name: 'pH监测',
      type: 'pH监测',
      frequency: '10分钟',
      location: '',
      source_system: 'fish-disease',
      created_at: '2024-03-01 09:00:00'
    }
  ],
  diseaseKnowledge: [
    {
      biz_id: 'DIS-001',
      name: '白点病（小瓜虫）',
      category: '寄生虫病',
      symptoms: JSON.stringify(['白点/小颗粒', '摩擦池壁', '游动异常', '摄食下降']),
      description: '由小瓜虫寄生引起，体表出现白色小点，鱼体摩擦池壁，严重时大量死亡。',
      treatment: '升温至 28-30℃ 并配合 0.3-0.5mg/L 硫酸铜或专用驱虫药，连续药浴 3-5 天。',
      prevention: '控制密度，新鱼入池前检疫，保持水温稳定，定期换水。',
      medicine: '硫酸铜、专用白点净',
      risk_level: 'high',
      source_system: 'fish-disease'
    },
    {
      biz_id: 'DIS-002',
      name: '细菌性烂鳃',
      category: '细菌病',
      symptoms: JSON.stringify(['烂鳃', '鳃丝苍白', '浮头/缺氧', '呼吸急促', '离群独游']),
      description: '鳃组织被细菌感染，鳃丝腐烂、颜色异常，溶氧交换受阻。',
      treatment: '停食 1-2 天，使用 0.3-0.5ppm 二氧化氯或专用抗菌药，加大换水量。',
      prevention: '避免氨氮/亚硝酸盐超标，定期消毒，减少机械损伤。',
      medicine: '二氧化氯、恩诺沙星',
      risk_level: 'critical',
      source_system: 'fish-disease'
    },
    {
      biz_id: 'DIS-003',
      name: '弧菌病（体表溃疡）',
      category: '细菌病',
      symptoms: JSON.stringify(['体表溃疡', '体表充血', '体色发黑', '沉底不动', '摄食下降']),
      description: '弧菌感染导致体表组织坏死，形成溃疡，常伴随内脏病变。',
      treatment: '隔离病鱼，全池泼洒抗菌药物，加强增氧，每日监测水质。',
      prevention: '控制投喂量，避免水质恶化，减少应激操作。',
      medicine: '氟苯尼考、复合碘',
      risk_level: 'high',
      source_system: 'fish-disease'
    },
    {
      biz_id: 'DIS-004',
      name: '缺氧应激',
      category: '环境应激',
      symptoms: JSON.stringify(['浮头/缺氧', '呼吸急促', '游动异常', '狂游/冲撞']),
      description: '溶氧不足或水质突变导致鱼群缺氧应激，非传染性但可诱发继发感染。',
      treatment: '立即开启增氧机，部分换水，检查 DO 与氨氮，暂停投喂。',
      prevention: '保持 DO > 5mg/L，合理密度，夜间加强增氧。',
      medicine: '无需用药，以环境调控为主',
      risk_level: 'critical',
      source_system: 'fish-disease'
    },
    {
      biz_id: 'DIS-005',
      name: '水霉病',
      category: '真菌病',
      symptoms: JSON.stringify(['体表黏液增多', '白点/小颗粒', '沉底不动', '体表溃疡']),
      description: '真菌感染，受伤鱼体表面出现棉絮状白色菌丝。',
      treatment: '用 3-5% 食盐水药浴 10-15 分钟，全池泼洒抗真菌药物。',
      prevention: '避免鱼体损伤，低温季节加强管理，保持水质清洁。',
      medicine: '食盐、亚甲基蓝',
      risk_level: 'medium',
      source_system: 'fish-disease'
    }
  ],
  aiAlarms: [
    {
      biz_id: 'AIA-001',
      alarm_code: 'AI-DO-20260723-001',
      alarm_title: 'AI预警：溶氧偏低',
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
      current_value: '4.2',
      threshold_value: '5.0',
      unit: 'mg/L',
      alarm_reason: '【Mock AI】近15分钟溶氧持续低于阈值，疑似增氧不足或密度过高。',
      ai_suggestion: '立即开启增氧设备，减少投喂，30分钟后复测溶氧。',
      confidence: '86',
      diagnosis_biz_id: '',
      knowledge_biz_id: 'DIS-004',
      alarm_time: '2026-07-23 09:15:00',
      is_mock: '1',
      source_system: 'fish-disease-ai-mock',
      external_id: '',
      ref_table: 'test_table_water_quality_realtime',
      ref_biz_id: ''
    },
    {
      biz_id: 'AIA-002',
      alarm_code: 'AI-NH3-20260723-002',
      alarm_title: 'AI预警：氨氮偏高',
      category: 'water',
      priority: 'urgent',
      alarm_status: 'open',
      pool_code: 'B1',
      pool_name: 'B1备用池',
      workshop: '二号车间',
      system_name: '',
      location_code: '',
      batch_code: '',
      metric_code: 'ammonia',
      metric_name: '氨氮',
      current_value: '0.72',
      threshold_value: '0.5',
      unit: 'mg/L',
      alarm_reason: '【Mock AI】氨氮超标，结合近期投喂量增加，疑似残饵与粪便累积。',
      ai_suggestion: '部分换水并排污，暂停投喂1餐，关注鳃部与浮头症状。',
      confidence: '91',
      diagnosis_biz_id: '',
      knowledge_biz_id: 'DIS-002',
      alarm_time: '2026-07-23 09:40:00',
      is_mock: '1',
      source_system: 'fish-disease-ai-mock',
      external_id: '',
      ref_table: 'test_table_threshold_config',
      ref_biz_id: ''
    },
    {
      biz_id: 'AIA-003',
      alarm_code: 'AI-DIS-20260722-003',
      alarm_title: 'AI预警：疑似细菌性烂鳃',
      category: 'disease',
      priority: 'critical',
      alarm_status: 'acked',
      pool_code: 'C-05',
      pool_name: 'C-05',
      workshop: '',
      system_name: '',
      location_code: '',
      batch_code: '',
      metric_code: 'disease',
      metric_name: '病害匹配',
      current_value: '88',
      threshold_value: '70',
      unit: '%',
      alarm_reason: '【Mock AI】症状匹配细菌性烂鳃置信度88%，建议采样镜检。',
      ai_suggestion: '停食1-2天，二氧化氯消毒并联系专家协同诊断。',
      confidence: '88',
      diagnosis_biz_id: 'DX-20241221-015',
      knowledge_biz_id: 'DIS-002',
      alarm_time: '2026-07-22 16:20:00',
      is_mock: '1',
      source_system: 'fish-disease-ai-mock',
      external_id: '',
      ref_table: 'test_table_fd_diagnosis_records',
      ref_biz_id: 'DX-20241221-015'
    }
  ]
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
  [NEW.species]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      name: { comment: '品种名称', len: 100 },
      fish_species: { comment: '品种名称(对齐存量表)', len: 100 },
      water_type: { comment: '水体类型', len: 32 },
      created_at: { comment: '创建时间', len: 32 }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.diseaseTypes]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      name: { comment: '病害名称', len: 100 },
      category: { comment: '分类', len: 64 },
      description: { comment: '描述', type: 'text' },
      created_at: { comment: '创建时间', len: 32 }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.equipments]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      code: { comment: '设备编号', len: 64 },
      device_code: { comment: '设备编码(对齐实时设备表)', len: 64 },
      name: { comment: '设备名称', len: 100 },
      type: { comment: '设备类型', len: 64 },
      frequency: { comment: '采集频率', len: 64 },
      location: { comment: '安装位置', len: 128 },
      created_at: { comment: '创建时间', len: 32 }
    },
    [SITE_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  ),
  [NEW.diseaseKnowledge]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      name: { comment: '病害名称', len: 128 },
      category: { comment: '分类', len: 64 },
      symptoms: { comment: '症状JSON', type: 'text' },
      description: { comment: '描述', type: 'text' },
      treatment: { comment: '治疗方案', type: 'text' },
      prevention: { comment: '预防措施', type: 'text' },
      medicine: { comment: '推荐药物', len: 255 },
      risk_level: { comment: '风险等级', len: 32 },
      disease_type_biz_id: { comment: '病害类型业务ID', len: 64 }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.diagnosisRecords]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      case_no: { comment: '病例编号', len: 64 },
      pond_code: { comment: '鱼池编号(兼容旧字段)', len: 64 },
      category: { comment: '品种', len: 64 },
      reporter: { comment: '上报人', len: 64 },
      physical_symptoms: { comment: '体表症状JSON', type: 'text' },
      behavioral_symptoms: { comment: '行为症状JSON', type: 'text' },
      water_temp: { comment: '水温', len: 32 },
      dissolved_oxygen: { comment: '溶氧', len: 32 },
      ammonia: { comment: '氨氮', len: 32 },
      suspected_disease: { comment: '疑似病害', len: 128 },
      disease_id: { comment: '知识库业务ID', len: 64 },
      knowledge_biz_id: { comment: '知识库业务ID(同disease_id)', len: 64 },
      confidence: { comment: '置信度', len: 16 },
      severity: { comment: '严重程度', len: 32 },
      status: { comment: '状态', len: 32 },
      ai_suggestion: { comment: 'AI建议', type: 'text' },
      treatment: { comment: '治疗方案', type: 'text' },
      veterinarian: { comment: '专家', len: 64 },
      assignee_id: { comment: '负责技工ID', len: 64 },
      assignee_name: { comment: '负责技工', len: 64 },
      accepted_at: { comment: '受理时间', len: 32 },
      conclusion: { comment: '结论', type: 'text' },
      diagnosis_date: { comment: '诊断日期', len: 32 },
      resolved_date: { comment: '完成日期', len: 32 },
      created_at: { comment: '创建时间', len: 32 },
      updated_at: { comment: '更新时间', len: 32 }
    },
    [SITE_LINK_FIELDS, SPECIES_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  ),
  [NEW.diagnosisMessages]: withLinks(
    {
      biz_id: { comment: '消息业务ID', len: 64 },
      diagnosis_id: { comment: '诊断业务ID', len: 64 },
      role: { comment: '角色', len: 32 },
      sender_id: { comment: '发送人ID', len: 64 },
      sender_name: { comment: '发送人', len: 64 },
      to_user_id: { comment: '接收人ID', len: 64 },
      to_name: { comment: '接收人', len: 64 },
      content: { comment: '内容', type: 'text' },
      msg_type: { comment: '消息类型', len: 32 },
      file_name: { comment: '文件名', len: 255 },
      file_url: { comment: '文件URL', type: 'text' },
      file_size: { comment: '文件大小', len: 32 },
      duration: { comment: '时长', len: 32 },
      msg_time: { comment: '消息时间', len: 32 }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.collectionRecords]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      species: { comment: '品种', len: 64 },
      pond_code: { comment: '鱼池编号(兼容旧字段)', len: 64 },
      fry_source: { comment: '苗种来源', len: 128 },
      disease_count: { comment: '病鱼数量', len: 32 },
      feeding_method: { comment: '投喂方式', len: 64 },
      abnormal_parts: { comment: '异常部位JSON', type: 'text' },
      abnormal_desc: { comment: '异常描述', type: 'text' },
      media_count: { comment: '媒体数量', len: 16 },
      media_names: { comment: '媒体名JSON', type: 'text' },
      metrics: { comment: '指标JSON', type: 'text' },
      created_at: { comment: '创建时间', len: 32 }
    },
    [SITE_LINK_FIELDS, SPECIES_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  ),
  [NEW.archivedCases]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      archive_no: { comment: '归档编号', len: 64 },
      pond_code: { comment: '鱼池编号(兼容旧字段)', len: 64 },
      species: { comment: '品种', len: 64 },
      disease_name: { comment: '病害名称', len: 128 },
      category: { comment: '分类', len: 64 },
      expert: { comment: '专家', len: 64 },
      archive_date: { comment: '归档日期', len: 32 },
      recovery_status: { comment: '恢复状态', len: 32 },
      match_rate: { comment: '匹配度', len: 16 },
      tags: { comment: '标签JSON', type: 'text' },
      summary: { comment: '摘要', type: 'text' },
      diagnosis_basis: { comment: '诊断依据', type: 'text' },
      cause_analysis: { comment: '原因分析', type: 'text' },
      treatment: { comment: '治疗', type: 'text' },
      treatment_steps: { comment: '治疗步骤JSON', type: 'text' },
      diagnosis_biz_id: { comment: '来源诊断业务ID', len: 64 },
      knowledge_biz_id: { comment: '知识库业务ID', len: 64 },
      created_at: { comment: '创建时间', len: 32 }
    },
    [SITE_LINK_FIELDS, SPECIES_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  ),
  [NEW.operationLogs]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      operator_name: { comment: '操作人', len: 64 },
      action: { comment: '动作', len: 128 },
      log_time: { comment: '时间', len: 32 },
      ip: { comment: 'IP', len: 64 },
      result: { comment: '结果', len: 32 },
      diagnosis_biz_id: { comment: '关联诊断业务ID', len: 64 }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.systemSettings]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      settings_json: { comment: '设置JSON', type: 'text' }
    },
    [CROSS_DB_LINK_FIELDS]
  ),
  [NEW.aiAlarm]: withLinks(
    {
      biz_id: { comment: '业务ID', len: 64 },
      alarm_code: { comment: '报警编号', len: 64 },
      alarm_title: { comment: '报警标题', len: 255 },
      category: { comment: '分类(water/disease/device)', len: 64 },
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
      diagnosis_biz_id: { comment: '关联诊断业务ID', len: 64 },
      knowledge_biz_id: { comment: '关联知识库ID', len: 64 },
      alarm_time: { comment: '报警时间', len: 32 },
      is_mock: { comment: '是否Mock(1是)', len: 8 }
    },
    [SITE_LINK_FIELDS, CROSS_DB_LINK_FIELDS]
  )
}

/** 空行模板：用于首次建表（列齐全） */
function emptyBootstrapPayload(tableName) {
  const defs = COLUMN_DEFS[tableName]
  if (!defs) throw new Error(`未知表 ${tableName}`)
  const empty = {}
  for (const key of Object.keys(defs)) empty[key] = ''
  if (defs.biz_id) empty.biz_id = `__bootstrap__`
  if (defs.source_system) empty.source_system = 'fish-disease'
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
  SEED,
  SITE_LINK_FIELDS,
  CROSS_DB_LINK_FIELDS,
  SPECIES_LINK_FIELDS,
  buildRowPayload,
  emptyBootstrapPayload,
  forminfoPayload,
  fields
}
