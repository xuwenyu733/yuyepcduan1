const statusMap = {
  planned: '计划中',
  in_progress: '进行中',
  completed: '已完成',
  stopped: '已停止'
}

let experiments = [
  {
    id: 'EXP-2024-001',
    name: '南美白对虾密度对比实验',
    workshop: '1',
    system: '1',
    species: '南美白对虾',
    poolNo: 'A1, A2',
    seedSource: '本地育苗场',
    category: 'breeding',
    status: 'in_progress',
    manager: '刘涛',
    startDate: '2024-03-01',
    endDate: '2024-06-01',
    duration: 90,
    templateId: 'TPL-WATER-001',
    goal: '对比不同养殖密度下的生长表现与成活率差异',
    content: '设置高密度与常规密度两组池塘进行对照养殖，持续监测生长与水质指标',
    method: '每周采样测量体重、体长、成活率，同步记录溶氧、pH、氨氮',
    pools: [
      { name: 'A1', groupType: 'experiment', initialCount: '1200', initialWeight: '0.8', description: '高密度实验组' },
      { name: 'A2', groupType: 'control', initialCount: '800', initialWeight: '0.8', description: '常规密度对照组' }
    ],
    metric: { label: '存活率', value: '96%' },
    conclusion: '高密度组生长更快但成活率略低，常规密度组综合表现更稳'
  },
  {
    id: 'EXP-2024-002',
    name: '罗非鱼饲料配方试验',
    workshop: '2',
    system: '1',
    species: '罗非鱼',
    poolNo: 'B1, B2',
    seedSource: '外购苗种',
    category: 'feed',
    status: 'completed',
    manager: '陈晓雯',
    startDate: '2024-01-10',
    endDate: '2024-04-10',
    duration: 90,
    templateId: 'TPL-TREAT-001',
    goal: '优化饲料蛋白水平，降低料肉比',
    content: '对比高蛋白与常规蛋白两种饲料配方',
    method: '分组定量投喂，统计摄食量、日增重与料肉比',
    pools: [
      { name: 'B1', groupType: 'experiment', initialCount: '1000', initialWeight: '50.5', description: '高蛋白饲料组' },
      { name: 'B2', groupType: 'control', initialCount: '1000', initialWeight: '50.5', description: '常规蛋白对照组' }
    ],
    metric: { label: '料肉比', value: '1.35' },
    conclusion: '高蛋白组料肉比显著下降，综合效益优于对照组'
  },
  {
    id: 'EXP-2024-003',
    name: '草鱼夏花培育成活率实验',
    workshop: '1',
    system: '2',
    species: '草鱼',
    poolNo: 'C1, C2, C3',
    seedSource: '长江水系原种场',
    category: 'growth',
    status: 'in_progress',
    manager: '刘涛',
    startDate: '2024-05-15',
    endDate: '2024-08-15',
    duration: 92,
    templateId: 'TPL-SEED-001',
    goal: '评估不同开口饵料对夏花成活率的影响',
    content: '设置轮虫、人工微粒、混合饵料三组对照',
    method: '每日观察摄食与活力，每 10 天统计成活数量',
    pools: [
      { name: 'C1', groupType: 'experiment', initialCount: '5000', initialWeight: '0.05', description: '轮虫开口组' },
      { name: 'C2', groupType: 'experiment', initialCount: '5000', initialWeight: '0.05', description: '人工微粒组' },
      { name: 'C3', groupType: 'control', initialCount: '5000', initialWeight: '0.05', description: '混合饵料对照组' }
    ],
    metric: { label: '成活率', value: '82%' }
  },
  {
    id: 'EXP-2024-004',
    name: '大黄鱼白点病治疗方案试验',
    species: '大黄鱼',
    poolNo: 'D1, D2',
    seedSource: '宁德海域育苗基地',
    category: 'disease',
    status: 'completed',
    manager: '王敏',
    startDate: '2024-02-01',
    endDate: '2024-03-15',
    duration: 43,
    templateId: 'TPL-TREAT-001',
    goal: '筛选白点病高效低毒治疗药物组合',
    content: '对比福尔马林与中草药复方两种治疗方案',
    method: '发病后分组用药，记录症状消退时间与死亡率变化',
    pools: [
      { name: 'D1', groupType: 'experiment', initialCount: '600', initialWeight: '120', description: '中草药复方组' },
      { name: 'D2', groupType: 'control', initialCount: '600', initialWeight: '118', description: '常规福尔马林组' }
    ],
    metric: { label: '治愈率', value: '88%' }
  },
  {
    id: 'EXP-2024-005',
    name: '池塘溶氧昼夜波动调控实验',
    species: '鲤鱼',
    poolNo: 'E1',
    seedSource: '省淡水研究所',
    category: 'water',
    status: 'planned',
    manager: '赵磊',
    startDate: '2024-09-01',
    endDate: '2024-11-30',
    duration: 90,
    templateId: 'TPL-WATER-001',
    goal: '通过增氧策略降低夜间溶氧低谷风险',
    content: '对比定时增氧与智能联动增氧两种方案',
    method: '连续监测溶氧曲线，统计低氧时长与浮头次数',
    pools: [
      { name: 'E1', groupType: 'experiment', initialCount: '1500', initialWeight: '85', description: '智能增氧实验池' }
    ],
    metric: { label: '关键指标', value: '-' }
  },
  {
    id: 'EXP-2024-006',
    name: '凡纳滨对虾温控育苗实验',
    species: '凡纳滨对虾',
    poolNo: 'F1, F2',
    seedSource: '海南种苗基地',
    category: 'environment',
    status: 'stopped',
    manager: '刘涛',
    startDate: '2024-04-01',
    endDate: '2024-04-20',
    duration: 19,
    templateId: 'TPL-SYSTEM-001',
    goal: '验证恒温系统对幼体变态率的影响',
    content: '恒温 30℃ 与自然变温两组育苗对照',
    method: '记录变态节点、畸形率与设备运行稳定性',
    pools: [
      { name: 'F1', groupType: 'experiment', initialCount: '20000', initialWeight: '0.01', description: '恒温育苗组' },
      { name: 'F2', groupType: 'control', initialCount: '20000', initialWeight: '0.01', description: '自然变温组' }
    ],
    metric: { label: '变态率', value: '71%' }
  }
]

function getStatusMap() {
  return statusMap
}

function listExperiments({ keyword, status, category, sort = 'startDate_desc' } = {}) {
  let result = [...experiments]

  if (category) {
    result = result.filter(item => item.category === category)
  }

  if (status) {
    result = result.filter(item => item.status === status)
  }

  if (keyword) {
    const q = String(keyword).trim().toLowerCase()
    result = result.filter(item =>
      item.name.toLowerCase().includes(q) ||
      item.id.toLowerCase().includes(q) ||
      (item.species || '').toLowerCase().includes(q) ||
      (item.manager || '').toLowerCase().includes(q) ||
      (item.poolNo || '').toLowerCase().includes(q)
    )
  }

  result.sort((a, b) => {
    if (sort === 'startDate_asc') return a.startDate.localeCompare(b.startDate)
    if (sort === 'name_asc') return a.name.localeCompare(b.name, 'zh-CN')
    return b.startDate.localeCompare(a.startDate)
  })

  return result
}

function getExperimentById(id) {
  return experiments.find(item => item.id === id)
}

function createExperiment(payload) {
  const id = payload.id || `EXP-${Date.now()}`
  if (experiments.some(item => item.id === id)) {
    const error = new Error('实验编号已存在')
    error.code = 400
    throw error
  }

  const item = {
    id,
    name: payload.name,
    species: payload.species || '',
    poolNo: payload.poolNo || '',
    seedSource: payload.seedSource || '',
    category: payload.category || 'breeding',
    status: payload.status || 'planned',
    manager: payload.manager,
    startDate: payload.startDate,
    endDate: payload.endDate || null,
    duration: payload.duration || 0,
    templateId: payload.templateId || '',
    goal: payload.goal || '',
    content: payload.content || '',
    method: payload.method || '',
    workshop: payload.workshop || '',
    system: payload.system || '',
    conclusion: payload.conclusion || payload.effectEvaluation || '',
    effectEvaluation: payload.effectEvaluation || payload.conclusion || '',
    pools: Array.isArray(payload.pools) ? payload.pools : [],
    metric: payload.metric || { label: '', value: '' }
  }
  experiments.unshift(item)
  return item
}

function updateExperiment(id, payload) {
  const index = experiments.findIndex(item => item.id === id)
  if (index === -1) return null
  experiments[index] = { ...experiments[index], ...payload, id }
  return experiments[index]
}

function deleteExperiment(id) {
  const index = experiments.findIndex(item => item.id === id)
  if (index === -1) return false
  experiments.splice(index, 1)
  return true
}

module.exports = {
  getStatusMap,
  listExperiments,
  getExperimentById,
  createExperiment,
  updateExperiment,
  deleteExperiment
}
