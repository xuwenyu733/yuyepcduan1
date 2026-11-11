const TEMPLATE_TYPES = [
  { key: 'drug', label: '药敏实验', icon: '💊' },
  { key: 'seed', label: '苗种比对实验', icon: '🐟' },
  { key: 'flavor', label: '鱼肉风味实验', icon: '🍽️' },
  { key: 'treatment', label: '治疗方案实验', icon: '⚕️' },
  { key: 'water', label: '水质调控实验', icon: '💧' },
  { key: 'system', label: '系统运营实验', icon: '⚙️' }
]

const PRESET_FIELDS = {
  water: [
    { key: 'goal', label: '调控目标', type: 'text', required: true },
    { key: 'measure', label: '调控措施', type: 'textarea', required: true },
    { key: 'tempBefore', label: '调控前水温', type: 'number', unit: '°C', required: true },
    { key: 'tempAfter', label: '调控后水温', type: 'number', unit: '°C', required: true },
    { key: 'phBefore', label: '调控前pH', type: 'number', required: true },
    { key: 'phAfter', label: '调控后pH', type: 'number', required: true },
    { key: 'doBefore', label: '调控前溶氧', type: 'number', unit: 'mg/L', required: true },
    { key: 'doAfter', label: '调控后溶氧', type: 'number', unit: 'mg/L', required: true },
    { key: 'ammonia', label: '氨氮', type: 'number', unit: 'mg/L', required: true },
    { key: 'nitrite', label: '亚硝酸盐', type: 'number', unit: 'mg/L', required: true },
    { key: 'operator', label: '操作人', type: 'text', required: true },
    { key: 'remark', label: '备注', type: 'textarea', required: false }
  ],
  treatment: [
    { key: 'disease', label: '疾病名称', type: 'text', required: true },
    { key: 'symptom', label: '发病症状', type: 'textarea', required: true },
    { key: 'medicine', label: '治疗药物', type: 'text', required: true },
    { key: 'dose', label: '用药剂量', type: 'text', required: true },
    { key: 'frequency', label: '用药频次', type: 'text', required: true },
    { key: 'period', label: '治疗周期', type: 'number', unit: '天', required: true },
    { key: 'mortalityBefore', label: '治疗前死亡率', type: 'number', unit: '%', required: true },
    { key: 'mortalityAfter', label: '治疗后死亡率', type: 'number', unit: '%', required: true },
    { key: 'cureRate', label: '治愈率', type: 'number', unit: '%', required: true },
    { key: 'sideEffect', label: '副作用观察', type: 'textarea', required: false },
    { key: 'evaluation', label: '方案评价', type: 'textarea', required: true }
  ],
  drug: [
    { key: 'pathogenType', label: '病原体类型', type: 'text', required: true },
    { key: 'testDrug', label: '测试药物', type: 'text', required: true },
    { key: 'drugConcentration', label: '药物浓度', type: 'number', unit: 'mg/L', required: true },
    { key: 'actionTime', label: '作用时间', type: 'number', unit: '小时', required: true },
    { key: 'inhibition', label: '抑菌圈直径', type: 'number', unit: 'mm', required: true },
    {
      key: 'sensitivityLevel',
      label: '敏感性评级',
      type: 'select',
      required: true,
      options: ['敏感', '中度敏感', '耐药']
    },
    { key: 'remark', label: '备注', type: 'textarea', required: false }
  ],
  seed: [
    { key: 'sourceA', label: '苗种来源A', type: 'text', required: true },
    { key: 'sourceB', label: '苗种来源B', type: 'text', required: true },
    { key: 'specA', label: '初始规格A', type: 'number', unit: 'g', required: true },
    { key: 'specB', label: '初始规格B', type: 'number', unit: 'g', required: true },
    { key: 'testPeriod', label: '测试周期', type: 'number', unit: '天', required: true },
    { key: 'finalWeightA', label: '终末重量A', type: 'number', unit: 'g', required: true },
    { key: 'finalWeightB', label: '终末重量B', type: 'number', unit: 'g', required: true },
    { key: 'survivalA', label: '成活率A', type: 'number', unit: '%', required: true },
    { key: 'survivalB', label: '成活率B', type: 'number', unit: '%', required: true },
    { key: 'fcrA', label: '料肉比A', type: 'number', required: true },
    { key: 'fcrB', label: '料肉比B', type: 'number', required: true },
    { key: 'evaluation', label: '综合评价', type: 'textarea', required: true }
  ],
  flavor: [
    { key: 'feedFormula', label: '饲料配方', type: 'text', required: true },
    { key: 'breedPeriod', label: '养殖周期', type: 'number', unit: '天', required: true },
    {
      key: 'muscleColor',
      label: '肌肉色泽',
      type: 'select',
      required: true,
      options: ['优良', '良好', '一般', '较差']
    },
    {
      key: 'firmness',
      label: '肉质紧实度',
      type: 'select',
      required: true,
      options: ['优良', '良好', '一般', '较差']
    },
    {
      key: 'fishiness',
      label: '腥味程度',
      type: 'select',
      required: true,
      options: ['无', '轻微', '中等', '明显']
    },
    { key: 'umamiScore', label: '鲜味评分', type: 'number', unit: '分(1-10)', required: true },
    { key: 'textureScore', label: '口感评分', type: 'number', unit: '分(1-10)', required: true },
    { key: 'overallScore', label: '综合评分', type: 'number', unit: '分(1-10)', required: true },
    { key: 'sensoryDesc', label: '感官描述', type: 'textarea', required: true }
  ],
  system: [
    { key: 'systemType', label: '系统类型', type: 'text', required: true },
    { key: 'runParams', label: '运行参数', type: 'textarea', required: true },
    { key: 'energy', label: '能耗', type: 'number', unit: 'kWh/天', required: true },
    { key: 'waterVolume', label: '水处理量', type: 'number', unit: 'm³/天', required: true },
    { key: 'efficiency', label: '处理效率', type: 'number', unit: '%', required: true },
    {
      key: 'stability',
      label: '设备稳定性',
      type: 'select',
      required: true,
      options: ['优秀', '良好', '一般', '较差']
    },
    { key: 'maintainFreq', label: '维护频次', type: 'text', required: true },
    { key: 'faultCount', label: '故障次数', type: 'number', unit: '次/月', required: true },
    { key: 'operateCost', label: '运营成本', type: 'number', unit: '元/天', required: true },
    { key: 'evaluation', label: '综合评价', type: 'textarea', required: true }
  ]
}

function nowText() {
  const d = new Date()
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}/${d.getMonth() + 1}/${d.getDate()} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

function today() {
  return new Date().toISOString().slice(0, 10)
}

let templates = [
  {
    id: 'TPL-DRUG-001',
    name: '药敏实验模板',
    type: 'drug',
    typeLabel: '药敏实验',
    description: '用于抗菌药物筛选、抑菌效果与敏感性评级记录',
    fields: PRESET_FIELDS.drug,
    updatedAt: '2026-07-17',
    createdAt: '2026/7/17 09:10:22'
  },
  {
    id: 'TPL-WATER-001',
    name: '水质调控实验模板',
    type: 'water',
    typeLabel: '水质调控实验',
    description: '用于记录水质调控前后关键指标变化',
    fields: PRESET_FIELDS.water,
    updatedAt: '2026-07-17',
    createdAt: '2026/7/16 13:49:14'
  },
  {
    id: 'TPL-TREAT-001',
    name: '治疗方案实验模板',
    type: 'treatment',
    typeLabel: '治疗方案实验',
    description: '用于病害治疗方案对比与效果评估',
    fields: PRESET_FIELDS.treatment,
    updatedAt: '2026-07-17',
    createdAt: '2026/7/16 13:50:37'
  },
  {
    id: 'TPL-SEED-001',
    name: '苗种比对实验模板',
    type: 'seed',
    typeLabel: '苗种比对实验',
    description: '用于不同苗种来源规格、成活率与料肉比对比',
    fields: PRESET_FIELDS.seed,
    updatedAt: '2026-07-17',
    createdAt: '2026/7/16 15:05:33'
  },
  {
    id: 'TPL-FLAVOR-001',
    name: '鱼肉风味实验模板',
    type: 'flavor',
    typeLabel: '鱼肉风味实验',
    description: '用于鱼肉感官评价与风味评分记录',
    fields: PRESET_FIELDS.flavor,
    updatedAt: '2026-07-17',
    createdAt: '2026/7/17 09:46:19'
  },
  {
    id: 'TPL-SYSTEM-001',
    name: '系统运营实验模板',
    type: 'system',
    typeLabel: '系统运营实验',
    description: '用于系统运行参数、能耗效率与稳定性评价',
    fields: PRESET_FIELDS.system,
    updatedAt: '2026-07-17',
    createdAt: '2026/7/16 17:01:44'
  }
]

function getTemplateTypes() {
  return TEMPLATE_TYPES
}

function listTemplates() {
  return templates
}

function getTemplateById(id) {
  return templates.find(item => item.id === id)
}

function getPresetFields(type) {
  const fields = PRESET_FIELDS[type]
  return fields ? JSON.parse(JSON.stringify(fields)) : []
}

function createTemplate({ type, name, description, fields }) {
  const typeItem = TEMPLATE_TYPES.find(item => item.key === type)
  if (!typeItem) return null
  const finalFields = Array.isArray(fields) && fields.length
    ? fields
    : getPresetFields(type)
  const item = {
    id: `TPL-${Date.now()}`,
    name: name || `${typeItem.label}模板`,
    type,
    typeLabel: typeItem.label,
    description: description || '',
    fields: finalFields,
    updatedAt: today(),
    createdAt: nowText()
  }
  templates.push(item)
  return item
}

function copyTemplate(id) {
  const index = templates.findIndex(item => item.id === id)
  if (index === -1) return null
  const source = templates[index]
  const item = {
    ...JSON.parse(JSON.stringify(source)),
    id: `TPL-${Date.now()}`,
    name: `${source.name}（副本）`,
    updatedAt: today(),
    createdAt: nowText()
  }
  templates.splice(index + 1, 0, item)
  return item
}

function deleteTemplate(id) {
  const index = templates.findIndex(item => item.id === id)
  if (index === -1) return false
  templates.splice(index, 1)
  return true
}

module.exports = {
  getTemplateTypes,
  getPresetFields,
  listTemplates,
  getTemplateById,
  createTemplate,
  copyTemplate,
  deleteTemplate
}
