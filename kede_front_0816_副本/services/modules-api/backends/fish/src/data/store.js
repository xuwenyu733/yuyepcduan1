const categories = ['鲈鱼', '大黄鱼', '石斑鱼', '黄花鱼', '对虾', '罗非鱼']

const diseaseStatusMap = {
  pending: '待诊断',
  diagnosing: '进行中',
  treating: '进行中',
  resolved: '已完成'
}

const severityMap = {
  critical: '紧急',
  high: '严重',
  medium: '中等',
  low: '轻微'
}

const physicalSymptoms = [
  '体表充血', '白点/小颗粒', '烂鳃', '烂尾/烂鳍', '体表溃疡',
  '腹部膨大', '眼球突出', '体色发黑', '鳃丝苍白', '体表黏液增多'
]

const behavioralSymptoms = [
  '浮头/缺氧', '游动异常', '离群独游', '摄食下降', '沉底不动',
  '狂游/冲撞', '水面打转', '跳跃', '摩擦池壁', '呼吸急促'
]

const pondStatusMap = {
  idle: '空闲',
  in_use: '使用中',
  available: '可用'
}

const waterTypeMap = {
  marine: '海水鱼',
  freshwater: '淡水鱼',
  brackish: '咸淡水鱼'
}

let fishSpecies = [
  { id: 'SP-001', name: '鲈鱼', waterType: 'marine', createdAt: '2024-03-01 09:00:00' },
  { id: 'SP-002', name: '石斑鱼', waterType: 'marine', createdAt: '2024-03-01 09:00:00' },
  { id: 'SP-003', name: '草鱼', waterType: 'freshwater', createdAt: '2024-03-01 09:00:00' },
  { id: 'SP-004', name: '大黄鱼', waterType: 'marine', createdAt: '2024-03-05 09:00:00' },
  { id: 'SP-005', name: '对虾', waterType: 'marine', createdAt: '2024-03-05 09:00:00' },
  { id: 'SP-006', name: '罗非鱼', waterType: 'freshwater', createdAt: '2024-03-08 09:00:00' }
]

let diseaseTypes = [
  { id: 'DT-001', name: '白点病', category: '寄生虫', description: '', createdAt: '2024-03-01 09:00:00' },
  { id: 'DT-002', name: '烂鳃病', category: '细菌', description: '', createdAt: '2024-03-01 09:00:00' },
  { id: 'DT-003', name: '肠炎', category: '细菌', description: '', createdAt: '2024-03-01 09:00:00' }
]

let equipments = [
  { id: 'EQ-001', code: 'DO-001', name: '溶氧监测', type: '溶氧监测', frequency: '5分钟', location: '', pondCodes: ['A1', 'A2'], createdAt: '2024-03-01 09:00:00' },
  { id: 'EQ-002', code: 'PH-001', name: 'pH监测', type: 'pH监测', frequency: '10分钟', location: '', pondCodes: ['A1'], createdAt: '2024-03-01 09:00:00' },
  { id: 'EQ-003', code: 'NH3-001', name: '氨氮监测', type: '氨氮监测', frequency: '10分钟', location: '', pondCodes: ['A2', 'B1'], createdAt: '2024-03-01 09:00:00' },
  { id: 'EQ-004', code: 'TEMP-001', name: '温度监测', type: '温度监测', frequency: '5分钟', location: '', pondCodes: ['B1'], createdAt: '2024-03-01 09:00:00' },
  { id: 'EQ-005', code: 'BEH-001', name: '行为监测', type: '行为监测', frequency: '30分钟', location: '', pondCodes: ['A1', 'B1'], createdAt: '2024-03-05 09:00:00' }
]

const WELCOME = '您好，我是智能鱼病诊断助手。请描述鱼群症状，或选择下方标签，我将为您匹配疑似病害并给出处置建议。'

let ponds = [
  { id: 'POND-001', code: 'A1', size: '120㎡', location: '一号车间', manager: '张强', status: 'in_use', remark: '主养殖池', createdAt: '2024-03-01 09:00:00' },
  { id: 'POND-002', code: 'A2', size: '100㎡', location: '一号车间', manager: '李敏', status: 'in_use', remark: '', createdAt: '2024-03-01 09:00:00' },
  { id: 'POND-003', code: 'B1', size: '150㎡', location: '二号车间', manager: '王磊', status: 'in_use', remark: '大黄鱼专用', createdAt: '2024-03-05 09:00:00' }
]

const diseaseKnowledge = [
  {
    id: 'DIS-001',
    name: '白点病（小瓜虫）',
    category: '寄生虫病',
    symptoms: ['白点/小颗粒', '摩擦池壁', '游动异常', '摄食下降'],
    description: '由小瓜虫寄生引起，体表出现白色小点，鱼体摩擦池壁，严重时大量死亡。',
    treatment: '升温至 28-30℃ 并配合 0.3-0.5mg/L 硫酸铜或专用驱虫药，连续药浴 3-5 天。',
    prevention: '控制密度，新鱼入池前检疫，保持水温稳定，定期换水。',
    medicine: '硫酸铜、专用白点净',
    riskLevel: 'high'
  },
  {
    id: 'DIS-002',
    name: '细菌性烂鳃',
    category: '细菌病',
    symptoms: ['烂鳃', '鳃丝苍白', '浮头/缺氧', '呼吸急促', '离群独游'],
    description: '鳃组织被细菌感染，鳃丝腐烂、颜色异常，溶氧交换受阻。',
    treatment: '停食 1-2 天，使用 0.3-0.5ppm 二氧化氯或专用抗菌药，加大换水量。',
    prevention: '避免氨氮/亚硝酸盐超标，定期消毒，减少机械损伤。',
    medicine: '二氧化氯、恩诺沙星',
    riskLevel: 'critical'
  },
  {
    id: 'DIS-003',
    name: '弧菌病（体表溃疡）',
    category: '细菌病',
    symptoms: ['体表溃疡', '体表充血', '体色发黑', '沉底不动', '摄食下降'],
    description: '弧菌感染导致体表组织坏死，形成溃疡，常伴随内脏病变。',
    treatment: '隔离病鱼，全池泼洒抗菌药物，加强增氧，每日监测水质。',
    prevention: '控制投喂量，避免水质恶化，减少应激操作。',
    medicine: '氟苯尼考、复合碘',
    riskLevel: 'high'
  },
  {
    id: 'DIS-004',
    name: '缺氧应激',
    category: '环境应激',
    symptoms: ['浮头/缺氧', '呼吸急促', '游动异常', '狂游/冲撞'],
    description: '溶氧不足或水质突变导致鱼群缺氧应激，非传染性但可诱发继发感染。',
    treatment: '立即开启增氧机，部分换水，检查 DO 与氨氮，暂停投喂。',
    prevention: '保持 DO > 5mg/L，合理密度，夜间加强增氧。',
    medicine: '无需用药，以环境调控为主',
    riskLevel: 'critical'
  },
  {
    id: 'DIS-005',
    name: '水霉病',
    category: '真菌病',
    symptoms: ['体表黏液增多', '白点/小颗粒', '沉底不动', '体表溃疡'],
    description: '真菌感染，受伤鱼体表面出现棉絮状白色菌丝。',
    treatment: '用 3-5% 食盐水药浴 10-15 分钟，全池泼洒抗真菌药物。',
    prevention: '避免鱼体损伤，低温季节加强管理，保持水质清洁。',
    medicine: '食盐、亚甲基蓝',
    riskLevel: 'medium'
  }
]

let diagnosisRecords = [
  {
    id: 'DX-20241222-001',
    caseNo: 'DX-20241222-001',
    pondCode: 'A1',
    category: '鲈鱼',
    reporter: '张工',
    physicalSymptoms: ['浮头'],
    behavioralSymptoms: ['游动异常'],
    waterTemp: 24.5,
    dissolvedOxygen: 4.2,
    ammonia: 0.8,
    suspectedDisease: '',
    diseaseId: '',
    confidence: 0,
    severity: 'high',
    status: 'diagnosing',
    aiSuggestion: '',
    treatment: '',
    veterinarian: '陈专家',
    diagnosisDate: '2024-12-22',
    resolvedDate: '',
    createdAt: '2024-12-22 09:30:00',
    updatedAt: '2024-12-22 09:42:00',
    messages: [
      { id: 'm1', role: 'worker', senderName: '张工', content: 'A-01池鲈鱼出现浮头现象，已上传水质数据', time: '09:35' },
      { id: 'm2', role: 'expert', senderName: '陈专家', content: '收到，溶氧偏低可能是主要原因。最近投喂量有没有变化？', time: '09:42' },
      { id: 'm3', role: 'worker', senderName: '张工', content: '投喂量最近增加了约20%，主要是担心鱼生长偏慢', time: '09:45' },
      { id: 'm4', role: 'expert', senderName: '陈专家', content: '明白了，建议先减少投喂量，同时开启增氧设备，观察24小时', time: '09:48' }
    ]
  },
  {
    id: 'DX-20241222-002',
    caseNo: 'DX-20241222-002',
    pondCode: 'B1',
    category: '石斑鱼',
    reporter: '李工',
    physicalSymptoms: [],
    behavioralSymptoms: [],
    waterTemp: 0,
    dissolvedOxygen: 0,
    ammonia: 0,
    suspectedDisease: '',
    diseaseId: '',
    confidence: 0,
    severity: 'medium',
    status: 'pending',
    aiSuggestion: '',
    treatment: '',
    veterinarian: '',
    diagnosisDate: '2024-12-22',
    resolvedDate: '',
    createdAt: '2024-12-22 10:15:00',
    updatedAt: '2024-12-22 10:15:00',
    messages: []
  },
  {
    id: 'DX-20241221-015',
    caseNo: 'DX-20241221-015',
    pondCode: 'A2',
    category: '草鱼',
    reporter: '王工',
    physicalSymptoms: ['体表充血'],
    behavioralSymptoms: ['摄食下降'],
    waterTemp: 22.0,
    dissolvedOxygen: 6.1,
    ammonia: 0.3,
    suspectedDisease: '细菌性烂鳃',
    diseaseId: 'DIS-002',
    confidence: 88,
    severity: 'medium',
    status: 'resolved',
    aiSuggestion: '已完成诊断并给出治疗方案',
    treatment: '泼洒二氧化氯消毒，连续药浴3天',
    veterinarian: '陈专家',
    diagnosisDate: '2024-12-21',
    resolvedDate: '2024-12-21',
    createdAt: '2024-12-21 14:20:00',
    updatedAt: '2024-12-21 16:00:00',
    messages: [
      { id: 'm1', role: 'worker', senderName: '王工', content: 'C-05池草鱼摄食下降，体表有轻微充血', time: '14:25' },
      { id: 'm2', role: 'expert', senderName: '陈专家', content: '建议采样镜检鳃部，初步判断为细菌性感染', time: '14:40' }
    ],
    conclusion: '细菌性烂鳃，已给出消毒与药浴方案'
  }
]

function now() {
  const d = new Date()
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

function nowTime() {
  const d = new Date()
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

function enrichPond(item) {
  return { ...item, statusLabel: pondStatusMap[item.status] || item.status }
}

function enrichDiagnosis(item) {
  return {
    ...item,
    statusLabel: diseaseStatusMap[item.status] || item.status,
    severityLabel: severityMap[item.severity] || item.severity,
    messages: item.messages || []
  }
}

function matchDisease(physical = [], behavioral = [], water = {}) {
  const selected = [...physical, ...behavioral]
  if (!selected.length) return null

  let best = null
  let bestScore = 0

  diseaseKnowledge.forEach(disease => {
    const matched = disease.symptoms.filter(s => selected.includes(s))
    let score = matched.length / disease.symptoms.length

    if (water.dissolvedOxygen != null && water.dissolvedOxygen < 5) {
      if (disease.id === 'DIS-004') score += 0.3
    }
    if (water.ammonia != null && water.ammonia > 0.5) {
      if (disease.id === 'DIS-002' || disease.id === 'DIS-003') score += 0.2
    }

    if (score > bestScore) {
      bestScore = score
      best = disease
    }
  })

  if (!best || bestScore < 0.2) return null

  const confidence = Math.min(95, Math.round(bestScore * 100 + selected.length * 3))
  return {
    diseaseId: best.id,
    suspectedDisease: best.name,
    confidence,
    severity: best.riskLevel,
    aiSuggestion: `根据症状匹配，疑似 ${best.name}（${best.category}），建议：${best.treatment.slice(0, 40)}…`,
    treatment: best.treatment
  }
}

function getMeta() {
  return {
    categories: fishSpecies.map(i => i.name),
    physicalSymptoms: [...physicalSymptoms],
    behavioralSymptoms: [...behavioralSymptoms],
    diseaseStatusMap: { ...diseaseStatusMap },
    severityMap: { ...severityMap },
    pondStatusMap: { ...pondStatusMap },
    waterTypeMap: { ...waterTypeMap }
  }
}

function getSummary() {
  const list = diagnosisRecords.map(enrichDiagnosis)
  return {
    active: list.filter(i => i.status === 'diagnosing' || i.status === 'treating').length,
    critical: list.filter(i => i.severity === 'critical' && i.status !== 'resolved').length,
    treating: list.filter(i => i.status === 'treating').length,
    resolved: list.filter(i => i.status === 'resolved').length,
    pending: list.filter(i => i.status === 'pending' || i.status === 'diagnosing').length,
    total: list.length
  }
}

function listDiagnosis(query = {}) {
  let list = diagnosisRecords.map(enrichDiagnosis)

  if (query.tab === 'pending') {
    list = list.filter(i => i.status === 'pending')
  } else if (query.tab === 'active') {
    list = list.filter(i => i.status === 'diagnosing' || i.status === 'treating')
  } else if (query.tab === 'done') {
    list = list.filter(i => i.status === 'resolved')
  }

  if (query.pondCode) list = list.filter(i => i.pondCode.includes(query.pondCode))
  if (query.category) list = list.filter(i => i.category === query.category)
  if (query.status) list = list.filter(i => i.status === query.status)
  if (query.severity) list = list.filter(i => i.severity === query.severity)

  if (query.keyword) {
    const kw = query.keyword.toLowerCase()
    list = list.filter(i =>
      i.caseNo.toLowerCase().includes(kw) ||
      (i.suspectedDisease || '').toLowerCase().includes(kw) ||
      (i.pondCode || '').toLowerCase().includes(kw)
    )
  }

  return list.sort((a, b) => {
    const ta = a.updatedAt || a.createdAt || ''
    const tb = b.updatedAt || b.createdAt || ''
    return tb > ta ? 1 : tb < ta ? -1 : 0
  })
}

function getDiagnosis(id) {
  const item = diagnosisRecords.find(i => i.id === id)
  return item ? enrichDiagnosis(item) : null
}

function metricValue(metrics, key) {
  if (!Array.isArray(metrics)) return 0
  const hit = metrics.find(m => m && m.key === key)
  return hit ? Number(hit.value) || 0 : 0
}

function createDiagnosis(payload) {
  const datePart = now().slice(0, 10).replace(/-/g, '')
  const seq = String(diagnosisRecords.length + 1).padStart(3, '0')
  const metrics = payload.metrics || []
  const item = {
    id: `DX-${Date.now()}`,
    caseNo: payload.caseNo || `DX-${datePart}-${seq}`,
    pondCode: payload.pondCode || '',
    category: payload.category || '',
    physicalSymptoms: payload.physicalSymptoms || [],
    behavioralSymptoms: payload.behavioralSymptoms || [],
    waterTemp: Number(payload.waterTemp) || metricValue(metrics, 'temp') || 0,
    dissolvedOxygen: Number(payload.dissolvedOxygen) || metricValue(metrics, 'do') || 0,
    ammonia: Number(payload.ammonia) || metricValue(metrics, 'ammonia') || 0,
    suspectedDisease: payload.suspectedDisease || '',
    diseaseId: payload.diseaseId || '',
    confidence: payload.confidence || 0,
    severity: payload.severity || 'medium',
    status: payload.status || 'pending',
    aiSuggestion: payload.aiSuggestion || '',
    treatment: payload.treatment || '',
    veterinarian: payload.veterinarian || '',
    reporter: payload.reporter || '张工',
    assigneeId: payload.assigneeId || '',
    assigneeName: payload.assigneeName || '',
    acceptedAt: payload.acceptedAt || '',
    diagnosisDate: payload.diagnosisDate || now().slice(0, 10),
    resolvedDate: '',
    createdAt: now(),
    updatedAt: now(),
    messages: payload.messages || []
  }
  if (!item.messages.length) {
    item.messages = [
      { id: `m-${Date.now()}`, role: 'worker', senderName: item.reporter, content: '已提交诊断申请，等待专家诊断', time: nowTime() }
    ]
  }
  diagnosisRecords.unshift(item)
  return enrichDiagnosis(item)
}

function updateDiagnosis(id, payload) {
  const index = diagnosisRecords.findIndex(i => i.id === id)
  if (index === -1) return null
  const prev = diagnosisRecords[index]
  if (payload.status === 'resolved' && !payload.resolvedDate) {
    payload.resolvedDate = now().slice(0, 10)
  }
  // 受理时若未指定技工，默认回派给上报人
  if (payload.status === 'diagnosing' || payload.status === 'treating') {
    if (!payload.assigneeName && !prev.assigneeName) {
      payload.assigneeName = payload.assigneeName || prev.reporter || ''
    }
    if (!payload.acceptedAt && !prev.acceptedAt) {
      payload.acceptedAt = now()
    }
  }
  diagnosisRecords[index] = { ...prev, ...payload, updatedAt: now() }
  const enriched = enrichDiagnosis(diagnosisRecords[index])
  // 已受理/已完成的任务自动进入案例归档，供后续 AI 分析
  if (enriched.status === 'diagnosing' || enriched.status === 'treating' || enriched.status === 'resolved') {
    archiveFromDiagnosis(enriched)
  }
  return enriched
}

/** 将已受理诊断同步到案例归档（幂等：按 diagnosisId / archiveNo） */
function archiveFromDiagnosis(diagnosis) {
  if (!diagnosis || !diagnosis.id) return null
  if (diagnosis.status === 'pending') return null
  const exist = archivedCases.find(
    i => i.diagnosisId === diagnosis.id || i.id === `AL-${diagnosis.id}` || i.archiveNo === `AL-${diagnosis.caseNo || diagnosis.id}`
  )
  const tags = [
    ...(diagnosis.physicalSymptoms || []),
    ...(diagnosis.behavioralSymptoms || []).slice(0, 2),
    diagnosis.category,
    diagnosis.pondCode
  ].filter(Boolean)
  const item = {
    id: exist ? exist.id : `AL-${diagnosis.id}`,
    archiveNo: exist ? exist.archiveNo : `AL-${diagnosis.caseNo || diagnosis.id}`,
    pondCode: diagnosis.pondCode || '',
    species: diagnosis.category || '',
    diseaseName: diagnosis.suspectedDisease || diagnosis.conclusion || '待完善病害名称',
    category: diagnosis.category || '未分类',
    expert: diagnosis.veterinarian || '',
    assigneeName: diagnosis.assigneeName || diagnosis.reporter || '',
    archiveDate: (diagnosis.acceptedAt || diagnosis.resolvedDate || now()).slice(0, 10),
    recoveryStatus: diagnosis.status === 'resolved' ? 'good' : 'fair',
    matchRate: Number(diagnosis.confidence) || (diagnosis.status === 'resolved' ? 80 : 60),
    tags,
    summary: [
      `来源诊断 ${diagnosis.caseNo || diagnosis.id}`,
      diagnosis.status === 'resolved' ? '已完成诊断' : '已受理进行中',
      diagnosis.assigneeName ? `负责技工:${diagnosis.assigneeName}` : '',
      diagnosis.treatment || diagnosis.aiSuggestion || ''
    ].filter(Boolean).join('；'),
    diagnosisBasis: diagnosis.conclusion || diagnosis.suspectedDisease || '受理归档，待补充诊断依据',
    causeAnalysis: (diagnosis.behavioralSymptoms || []).join('、') || '待分析',
    treatment: diagnosis.treatment || '',
    treatmentSteps: diagnosis.treatment
      ? [{ title: '治疗方案', content: diagnosis.treatment }]
      : [{ title: '后续分析', content: '已受理案例，预留给 AI 初判与经验沉淀' }],
    diagnosisId: diagnosis.id,
    createdAt: exist ? exist.createdAt : now(),
    updatedAt: now(),
    aiReady: true
  }
  if (exist) {
    const idx = archivedCases.findIndex(i => i.id === exist.id)
    archivedCases[idx] = { ...exist, ...item }
    return enrichArchiveCase(archivedCases[idx])
  }
  archivedCases.unshift(item)
  return enrichArchiveCase(item)
}

function deleteDiagnosis(id) {
  const index = diagnosisRecords.findIndex(i => i.id === id)
  if (index === -1) return false
  diagnosisRecords.splice(index, 1)
  return true
}

function runDiagnosis(payload) {
  const match = matchDisease(
    payload.physicalSymptoms || [],
    payload.behavioralSymptoms || [],
    { dissolvedOxygen: payload.dissolvedOxygen, ammonia: payload.ammonia }
  )
  if (!match) {
    return {
      suspectedDisease: '未能匹配',
      confidence: 0,
      severity: 'medium',
      aiSuggestion: '症状信息不足，建议补充观察并联系水产兽医进一步确认。',
      treatment: '',
      alternatives: []
    }
  }

  const alternatives = diseaseKnowledge
    .filter(d => d.id !== match.diseaseId)
    .map(d => {
      const selected = [...(payload.physicalSymptoms || []), ...(payload.behavioralSymptoms || [])]
      const matched = d.symptoms.filter(s => selected.includes(s))
      return { name: d.name, matchRate: Math.round((matched.length / d.symptoms.length) * 100) }
    })
    .filter(a => a.matchRate > 20)
    .sort((a, b) => b.matchRate - a.matchRate)
    .slice(0, 3)

  return { ...match, alternatives }
}

function appendDiagnosisMessage(id, payload = {}) {
  const index = diagnosisRecords.findIndex(i => i.id === id)
  if (index === -1) return null

  const record = diagnosisRecords[index]
  const content = String(payload.content || payload.message || '').trim()
  if (!content) return null

  const role = payload.role || 'expert'
  const senderName =
    payload.senderName ||
    (role === 'expert' ? record.veterinarian || '陈专家' : record.reporter || '工人')

  const message = {
    id: `m-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
    role,
    senderId: payload.senderId || '',
    senderName,
    toUserId: payload.toUserId || '',
    toName: payload.toName || '',
    content,
    msgType: payload.msgType || 'text',
    fileName: payload.fileName || '',
    fileUrl: payload.fileUrl || '',
    fileSize: payload.fileSize || 0,
    duration: payload.duration || 0,
    time: nowTime()
  }

  const messages = [...(record.messages || []), message]
  if (record.status === 'pending') {
    record.status = 'diagnosing'
    record.veterinarian = record.veterinarian || '陈专家'
  }

  diagnosisRecords[index] = enrichDiagnosis({
    ...record,
    messages,
    updatedAt: now()
  })

  return {
    record: diagnosisRecords[index],
    message
  }
}

function chatDiagnosis(id, payload) {
  const index = diagnosisRecords.findIndex(i => i.id === id)
  if (index === -1) return null

  const record = diagnosisRecords[index]
  const messages = [...(record.messages || [])]
  const time = nowTime()

  const physical = payload.physicalSymptoms || record.physicalSymptoms || []
  const behavioral = payload.behavioralSymptoms || record.behavioralSymptoms || []
  const waterTemp = payload.waterTemp ?? record.waterTemp
  const dissolvedOxygen = payload.dissolvedOxygen ?? record.dissolvedOxygen
  const ammonia = payload.ammonia ?? record.ammonia

  if (payload.message) {
    messages.push({
      id: `m-${Date.now()}-e`,
      role: payload.role || 'expert',
      senderId: payload.senderId || '',
      senderName: payload.senderName || record.veterinarian || '陈专家',
      toUserId: payload.toUserId || '',
      toName: payload.toName || '',
      content: payload.message,
      msgType: payload.msgType || 'text',
      fileName: payload.fileName || '',
      fileUrl: payload.fileUrl || '',
      fileSize: payload.fileSize || 0,
      duration: payload.duration || 0,
      time
    })
  }

  const symptomParts = [...physical, ...behavioral]
  if (symptomParts.length && !payload.message) {
    messages.push({
      id: `m-${Date.now()}-e2`,
      role: 'expert',
      senderName: payload.senderName || record.veterinarian || '陈专家',
      content: `已记录症状：${symptomParts.join('、')}`,
      time
    })
  }

  const match = matchDisease(physical, behavioral, { dissolvedOxygen, ammonia })
  let reply
  if (match && !payload.message) {
    reply = match.aiSuggestion
    record.suspectedDisease = match.suspectedDisease
    record.diseaseId = match.diseaseId
    record.confidence = match.confidence
    record.severity = match.severity
    record.aiSuggestion = match.aiSuggestion
    record.treatment = match.treatment
    record.status = record.status === 'pending' ? 'diagnosing' : record.status
    messages.push({
      id: `m-${Date.now()}-e3`,
      role: 'expert',
      senderName: record.veterinarian || '陈专家',
      content: reply,
      time
    })
  } else if (!payload.message) {
    reply = '已收到信息，请补充更多症状描述以便进一步诊断。'
    messages.push({
      id: `m-${Date.now()}-e4`,
      role: 'expert',
      senderName: record.veterinarian || '陈专家',
      content: reply,
      time
    })
  } else if (record.status === 'pending') {
    record.status = 'diagnosing'
    record.veterinarian = record.veterinarian || '陈专家'
  }

  diagnosisRecords[index] = enrichDiagnosis({
    ...record,
    physicalSymptoms: physical,
    behavioralSymptoms: behavioral,
    waterTemp,
    dissolvedOxygen,
    ammonia,
    messages,
    updatedAt: now()
  })

  return diagnosisRecords[index]
}

function listPonds() {
  return ponds.map(enrichPond)
}

function createPond(payload) {
  const item = {
    id: `POND-${Date.now()}`,
    code: payload.code,
    size: payload.size || '',
    location: payload.location || '',
    manager: payload.manager || '',
    status: payload.status || 'idle',
    remark: payload.remark || '',
    createdAt: now()
  }
  ponds.unshift(item)
  return enrichPond(item)
}

function updatePond(id, payload) {
  const index = ponds.findIndex(i => i.id === id)
  if (index === -1) return null
  ponds[index] = { ...ponds[index], ...payload }
  return enrichPond(ponds[index])
}

function deletePond(id) {
  const index = ponds.findIndex(i => i.id === id)
  if (index === -1) return false
  ponds.splice(index, 1)
  return true
}

function listDiseaseKnowledge() {
  return diseaseKnowledge
}

function getKnowledge(id) {
  return diseaseKnowledge.find(i => i.id === id) || null
}

function normalizeSymptoms(symptoms) {
  const raw = Array.isArray(symptoms)
    ? symptoms
    : String(symptoms || '').split(/[,，、;；\n]/)
  const seen = new Set()
  const out = []
  for (const part of raw) {
    const s = String(part || '').trim().slice(0, 40)
    if (!s) continue
    const key = s.toLowerCase()
    if (seen.has(key)) continue
    seen.add(key)
    out.push(s)
    if (out.length >= 20) break
  }
  return out
}

const RISK_LEVELS = new Set(['low', 'medium', 'high', 'critical'])

function clip(str, max) {
  return String(str == null ? '' : str).trim().slice(0, max)
}

function normalizeKnowledgeFields(payload = {}, fallback = {}) {
  const name = clip(payload.name != null ? payload.name : fallback.name, 64)
  const category = clip(payload.category != null ? payload.category : fallback.category, 32)
  const riskRaw = payload.riskLevel != null ? payload.riskLevel : fallback.riskLevel
  const riskLevel = RISK_LEVELS.has(String(riskRaw || '').toLowerCase())
    ? String(riskRaw).toLowerCase()
    : 'medium'
  return {
    name: name || fallback.name || '',
    category: category || fallback.category || '其他',
    symptoms:
      payload.symptoms != null
        ? normalizeSymptoms(payload.symptoms)
        : fallback.symptoms || [],
    description: clip(
      payload.description != null ? payload.description : fallback.description,
      500
    ),
    treatment: clip(payload.treatment != null ? payload.treatment : fallback.treatment, 800),
    prevention: clip(payload.prevention != null ? payload.prevention : fallback.prevention, 800),
    medicine: clip(payload.medicine != null ? payload.medicine : fallback.medicine, 120),
    riskLevel
  }
}

function createDiseaseKnowledge(payload = {}) {
  const name = clip(payload.name, 64)
  if (!name) {
    const err = new Error('病害名称不能为空')
    err.status = 400
    throw err
  }
  const fields = normalizeKnowledgeFields({ ...payload, name }, {
    name,
    category: '其他',
    symptoms: [],
    description: '',
    treatment: '',
    prevention: '',
    medicine: '',
    riskLevel: 'medium'
  })
  // 同名拒绝（忽略大小写）
  const dup = diseaseKnowledge.find(
    i => String(i.name || '').trim().toLowerCase() === fields.name.toLowerCase()
  )
  if (dup) {
    const err = new Error(`已存在同名知识「${dup.name}」`)
    err.status = 409
    throw err
  }
  const item = {
    id: `DIS-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
    ...fields
  }
  diseaseKnowledge.unshift(item)
  return item
}

function updateDiseaseKnowledge(id, payload = {}) {
  const idx = diseaseKnowledge.findIndex(i => i.id === id)
  if (idx < 0) return null
  const prev = diseaseKnowledge[idx]
  const fields = normalizeKnowledgeFields(payload, prev)
  if (!fields.name) {
    const err = new Error('病害名称不能为空')
    err.status = 400
    throw err
  }
  const dup = diseaseKnowledge.find(
    i =>
      i.id !== id &&
      String(i.name || '').trim().toLowerCase() === fields.name.toLowerCase()
  )
  if (dup) {
    const err = new Error(`已存在同名知识「${dup.name}」`)
    err.status = 409
    throw err
  }
  const next = { ...prev, ...fields }
  diseaseKnowledge.splice(idx, 1, next)
  return next
}

function deleteDiseaseKnowledge(id) {
  const idx = diseaseKnowledge.findIndex(i => i.id === String(id || ''))
  if (idx < 0) return false
  diseaseKnowledge.splice(idx, 1)
  return true
}

function enrichSpecies(item) {
  return {
    ...item,
    waterTypeLabel: waterTypeMap[item.waterType] || item.waterType
  }
}

function listFishSpecies() {
  return fishSpecies.map(enrichSpecies)
}

function createFishSpecies(payload) {
  const item = {
    id: `SP-${Date.now()}`,
    name: payload.name,
    waterType: payload.waterType || 'freshwater',
    createdAt: now()
  }
  fishSpecies.unshift(item)
  return enrichSpecies(item)
}

function updateFishSpecies(id, payload) {
  const index = fishSpecies.findIndex(i => i.id === id)
  if (index === -1) return null
  fishSpecies[index] = { ...fishSpecies[index], ...payload }
  return enrichSpecies(fishSpecies[index])
}

function deleteFishSpecies(id) {
  const index = fishSpecies.findIndex(i => i.id === id)
  if (index === -1) return false
  fishSpecies.splice(index, 1)
  return true
}

function listDiseaseTypes() {
  return [...diseaseTypes]
}

function createDiseaseType(payload) {
  const item = {
    id: `DT-${Date.now()}`,
    name: payload.name,
    category: payload.category || '',
    description: payload.description || '',
    createdAt: now()
  }
  diseaseTypes.unshift(item)
  return item
}

function updateDiseaseType(id, payload) {
  const index = diseaseTypes.findIndex(i => i.id === id)
  if (index === -1) return null
  diseaseTypes[index] = { ...diseaseTypes[index], ...payload }
  return diseaseTypes[index]
}

function deleteDiseaseType(id) {
  const index = diseaseTypes.findIndex(i => i.id === id)
  if (index === -1) return false
  diseaseTypes.splice(index, 1)
  return true
}

function listEquipments() {
  return [...equipments]
}

function createEquipment(payload) {
  const pondCodes = Array.isArray(payload.pondCodes)
    ? payload.pondCodes.map(String).filter(Boolean)
    : (payload.pondCode ? [String(payload.pondCode)] : [])
  const item = {
    id: `EQ-${Date.now()}`,
    code: payload.code,
    name: payload.name || '',
    type: payload.type || '',
    frequency: payload.frequency || '',
    location: payload.location || '',
    pondCodes,
    createdAt: now()
  }
  equipments.unshift(item)
  return item
}

function updateEquipment(id, payload) {
  const index = equipments.findIndex(i => i.id === id)
  if (index === -1) return null
  equipments[index] = { ...equipments[index], ...payload }
  return equipments[index]
}

function deleteEquipment(id) {
  const index = equipments.findIndex(i => i.id === id)
  if (index === -1) return false
  equipments.splice(index, 1)
  return true
}

let collectionRecords = []

const defaultMonitorMetrics = [
  { key: 'do', label: '溶氧', value: 5.2, unit: 'mg/L', rangeText: '5-8', min: 5, max: 8, types: ['溶氧监测'] },
  { key: 'ph', label: 'pH值', value: 7.8, unit: '', rangeText: '7.0-8.5', min: 7.0, max: 8.5, types: ['pH监测'] },
  { key: 'ammonia', label: '氨氮', value: 0.8, unit: 'mg/L', rangeText: '<0.5', maxExclusive: 0.5, types: ['氨氮监测'] },
  { key: 'temp', label: '水温', value: 24.5, unit: '℃', rangeText: '22-28', min: 22, max: 28, types: ['温度监测'] },
  { key: 'behavior', label: '行为活跃度', value: 72, unit: '%', rangeText: '60-90', min: 60, max: 90, types: ['行为监测'] }
]

function isMetricAbnormal(item) {
  if (item.maxExclusive != null) return item.value >= item.maxExclusive
  if (item.min != null && item.value < item.min) return true
  if (item.max != null && item.value > item.max) return true
  return false
}

function equipmentCoversPond(eq, pondCode) {
  if (!pondCode) return true
  const codes = Array.isArray(eq.pondCodes) ? eq.pondCodes : (eq.pondCode ? [eq.pondCode] : [])
  if (!codes.length) return true
  return codes.map(String).includes(String(pondCode))
}

function getMonitorData(pondCode) {
  const code = pondCode ? String(pondCode) : ''
  const availableTypes = new Set(
    equipments
      .filter(eq => equipmentCoversPond(eq, code))
      .map(eq => eq.type || eq.name)
      .filter(Boolean)
  )
  // 未绑设备时：无池号返回默认全量；有池号但无绑定则返回空
  const metrics = defaultMonitorMetrics.filter(item => {
    if (!code) return true
    if (!availableTypes.size) return false
    return (item.types || []).some(t => availableTypes.has(t))
  })
  return metrics.map(item => ({
    key: item.key,
    label: item.label,
    value: item.value,
    unit: item.unit,
    rangeText: item.rangeText,
    abnormal: isMetricAbnormal(item),
    deviceType: (item.types && item.types[0]) || '',
    pondCode: code || ''
  }))
}

function listCollectionRecords() {
  return [...collectionRecords]
}

function createCollectionRecord(payload) {
  const item = {
    id: `COL-${Date.now()}`,
    species: payload.species || '',
    pondCode: payload.pondCode || '',
    frySource: payload.frySource || '',
    diseaseCount: payload.diseaseCount || '',
    feedingMethod: payload.feedingMethod || '',
    abnormalParts: payload.abnormalParts || [],
    abnormalDesc: payload.abnormalDesc || '',
    mediaCount: payload.mediaCount || 0,
    mediaNames: payload.mediaNames || [],
    metrics: payload.metrics || [],
    createdAt: now()
  }
  collectionRecords.unshift(item)
  return item
}

const recoveryStatusMap = {
  good: '恢复良好',
  fair: '恢复一般',
  poor: '恢复较差'
}

let archivedCases = [
  {
    id: 'AL-20241220-001',
    archiveNo: 'AL-20241220-001',
    pondCode: 'A1',
    species: '鲈鱼',
    diseaseName: '细菌性烂鳃病',
    category: '细菌',
    expert: '陈专家',
    archiveDate: '2024-12-20',
    recoveryStatus: 'good',
    matchRate: 85,
    tags: ['鳃部异常', '溶氧偏低', '冬季', '鲈鱼'],
    summary: '冬季低温期溶氧偏低导致细菌性烂鳃，经消毒和药浴治疗后恢复良好。',
    diagnosisBasis: '鳃部出现明显充血发白，镜检发现大量革兰氏阴性杆菌，结合水质溶氧偏低数据及冬季低温期症状，确诊为细菌性烂鳃病。',
    causeAnalysis: '冬季低温期溶氧持续偏低，鱼体免疫力下降，水中致病菌大量繁殖导致感染。',
    treatment: '泼洒二氧化氯消毒，连续药浴3天，加强增氧',
    treatmentSteps: [
      { title: '用药建议', content: '使用二氧化氯全池泼洒消毒（0.3ppm），配合恩诺沙星药浴（10ppm）连续3天' },
      { title: '水质调节', content: '加强增氧，保持溶氧≥6mg/L，适当换水并稳定水温' },
      { title: '投喂调整', content: '减少投喂量至正常的60%，待鳃部症状好转后逐步恢复' },
      { title: '隔离建议', content: '将重症个体隔离至治疗池单独药浴，避免交叉感染' }
    ],
    createdAt: '2024-12-20 16:00:00'
  },
  {
    id: 'AL-20241218-002',
    archiveNo: 'AL-20241218-002',
    pondCode: 'B1',
    species: '石斑鱼',
    diseaseName: '白点病',
    category: '寄生虫',
    expert: '陈专家',
    archiveDate: '2024-12-18',
    recoveryStatus: 'fair',
    matchRate: 72,
    tags: ['体表白点', '摄食下降', '石斑鱼'],
    summary: '新进鱼苗携带小瓜虫，升温并药浴处理后基本恢复。',
    diagnosisBasis: '体表出现明显白点，镜检发现大量小瓜虫虫体，结合新进鱼苗来源及摄食下降症状，确诊为白点病。',
    causeAnalysis: '新进鱼苗携带寄生虫，适应期应激导致免疫力下降，小瓜虫趁机大量繁殖。',
    treatment: '升温至29℃并硫酸铜药浴，隔离观察',
    treatmentSteps: [
      { title: '用药建议', content: '使用硫酸铜和硫酸亚铁合剂（5:2），浓度0.7ppm，全池泼洒，连续3天' },
      { title: '水质调节', content: '保持水温稳定在25-26℃，适当增加换水频率' },
      { title: '投喂调整', content: '减少投喂量至正常的50%，待病情好转后逐步恢复' },
      { title: '隔离建议', content: '将重症个体隔离至治疗池，避免交叉感染' }
    ],
    createdAt: '2024-12-18 11:30:00'
  },
  {
    id: 'AL-20241215-003',
    archiveNo: 'AL-20241215-003',
    pondCode: 'A2',
    species: '草鱼',
    diseaseName: '肠炎',
    category: '细菌',
    expert: '李专家',
    archiveDate: '2024-12-15',
    recoveryStatus: 'good',
    matchRate: 91,
    tags: ['肠道异常', '投喂过量', '草鱼'],
    summary: '投喂过量引发肠炎，调整投喂方案并拌药后恢复良好。',
    diagnosisBasis: '解剖发现肠道充血肿胀、有白色黏液，结合近期投喂过量记录及摄食异常，确诊为肠炎。',
    causeAnalysis: '投喂过量导致饲料在肠道内滞留，引发细菌感染和肠道炎症。',
    treatment: '停食2天，恩诺沙星拌料投喂5天',
    treatmentSteps: [
      { title: '用药建议', content: '停食2天，使用恩诺沙星拌料投喂（50mg/kg），连续5天' },
      { title: '水质调节', content: '保持水质稳定，适当换水并排污，降低池底有机物' },
      { title: '投喂调整', content: '调整投喂方案，减量至70%并逐步恢复，改为少食多餐' },
      { title: '隔离建议', content: '症状明显个体单独隔离观察，防止病情扩散' }
    ],
    createdAt: '2024-12-15 09:00:00'
  }
]

function enrichArchiveCase(item) {
  return {
    ...item,
    recoveryLabel: recoveryStatusMap[item.recoveryStatus] || item.recoveryStatus,
    isHighMatch: item.matchRate >= 80
  }
}

function getArchiveSummary() {
  const list = archivedCases.map(enrichArchiveCase)
  return {
    total: list.length,
    highMatch: list.filter(i => i.isHighMatch).length,
    estimatedOpen: '2025年Q1'
  }
}

function listArchivedCases(query = {}) {
  // 回填：所有已受理/已完成诊断确保在归档中
  diagnosisRecords.forEach(d => {
    if (d.status === 'diagnosing' || d.status === 'treating' || d.status === 'resolved') {
      archiveFromDiagnosis(enrichDiagnosis(d))
    }
  })
  let list = archivedCases.map(enrichArchiveCase)

  if (query.tab === 'highMatch') {
    list = list.filter(i => i.isHighMatch)
  }

  if (query.category && query.category !== 'all') {
    list = list.filter(i => i.category === query.category)
  }

  if (query.keyword) {
    const kw = query.keyword.toLowerCase()
    list = list.filter(i =>
      i.archiveNo.toLowerCase().includes(kw) ||
      i.diseaseName.toLowerCase().includes(kw) ||
      i.species.toLowerCase().includes(kw) ||
      (i.tags || []).some(t => t.toLowerCase().includes(kw))
    )
  }

  return list.sort((a, b) => {
    const ta = a.archiveDate || a.createdAt || ''
    const tb = b.archiveDate || b.createdAt || ''
    return tb > ta ? 1 : tb < ta ? -1 : 0
  })
}

function getArchivedCase(id) {
  const item = archivedCases.find(i => i.id === id)
  return item ? enrichArchiveCase(item) : null
}

function getArchiveCategories() {
  return [...new Set(archivedCases.map(i => i.category))]
}

const roleLabelMap = {
  expert: '专家',
  worker: '工人',
  admin: '管理员'
}

let systemUsers = [
  { id: 'U-001', username: '陈专家', role: 'expert', department: '病害诊断部', status: 'active', email: 'chen@system.com', password: '123456' },
  { id: 'U-002', username: '张工', role: 'worker', department: '技术部', status: 'active', email: 'zhang@system.com', password: '123456' },
  { id: 'U-003', username: '李工', role: 'worker', department: '技术部', status: 'active', email: 'li@system.com', password: '123456' },
  { id: 'U-004', username: '系统管理员', role: 'admin', department: '管理部', status: 'active', email: 'admin@system.com', password: '123456' }
]

let operationLogs = [
  { id: 'L-001', operator: '陈专家', action: '填写诊断结论', time: '2024-12-22 10:15:00', ip: '192.168.1.100', result: 'success' },
  { id: 'L-002', operator: '张工', action: '提交诊断申请', time: '2024-12-22 09:30:00', ip: '192.168.1.101', result: 'success' },
  { id: 'L-003', operator: '系统管理员', action: '修改系统配置', time: '2024-12-22 08:45:00', ip: '192.168.1.1', result: 'success' },
  { id: 'L-004', operator: '李工', action: '登录系统', time: '2024-12-22 08:20:00', ip: '192.168.1.102', result: 'failed' }
]

let systemSettings = {
  dataRetentionDays: 365,
  uploadLimitMb: 50,
  autoBackup: true,
  notifyMethod: 'email',
  notifyNewTask: true,
  notifyDiagnosisDone: true,
  notifyAbnormalData: true,
  deviceApiUrl: 'https://api.aquaculture.com/v1',
  deviceApiKey: '••••••••••••••••',
  syncIntervalSeconds: 60,
  version: 'v1.0.0',
  databaseSize: '2.3 GB',
  activeUsers: 12,
  totalCases: 156,
  lastBackupTime: '2024-12-22 02:00'
}

function sanitizeUser(item) {
  if (!item) return null
  const { password, ...rest } = item
  return {
    ...rest,
    roleLabel: roleLabelMap[rest.role] || rest.role,
    statusLabel: rest.status === 'active' ? '正常' : '停用'
  }
}

function enrichUser(item) {
  return sanitizeUser(item)
}

function listSystemUsers() {
  return systemUsers.map(enrichUser)
}

function listChatContacts(currentUser = null) {
  return systemUsers
    .filter(u => u.status === 'active')
    .filter(u => !currentUser || u.id !== currentUser.id)
    .map(enrichUser)
}

function createSystemUser(payload) {
  const item = {
    id: `U-${Date.now()}`,
    username: payload.username,
    role: payload.role || 'worker',
    department: payload.department || '',
    status: payload.status || 'active',
    email: payload.email || '',
    password: payload.password || '123456'
  }
  systemUsers.unshift(item)
  addOperationLog({
    operator: payload.operator || '系统管理员',
    action: `新增用户 ${item.username}`,
    ip: payload.ip || '127.0.0.1'
  })
  return enrichUser(item)
}

function updateSystemUser(id, payload) {
  const index = systemUsers.findIndex(i => i.id === id)
  if (index === -1) return null
  const next = { ...systemUsers[index], ...payload }
  if (!payload.password) next.password = systemUsers[index].password
  systemUsers[index] = next
  addOperationLog({
    operator: payload.operator || '系统管理员',
    action: `更新用户 ${next.username}`,
    ip: payload.ip || '127.0.0.1'
  })
  return enrichUser(systemUsers[index])
}

function getSystemUserById(id) {
  const user = systemUsers.find(u => u.id === id)
  return user ? sanitizeUser(user) : null
}

function addOperationLog(payload = {}) {
  const item = {
    id: `L-${Date.now()}`,
    operator: payload.operator || '系统',
    action: payload.action || '',
    time: now(),
    ip: payload.ip || '127.0.0.1',
    result: payload.result || 'success'
  }
  operationLogs.unshift(item)
  return item
}

function loginUser(payload = {}) {
  const account = String(payload.account || payload.username || payload.email || '').trim()
  const password = String(payload.password || '')
  if (!account || !password) {
    return { ok: false, message: '请输入账号和密码' }
  }

  const user = systemUsers.find(
    u => u.username === account || u.email === account
  )
  if (!user) {
    return { ok: false, message: '账号或密码错误' }
  }
  if (user.status !== 'active') {
    return { ok: false, message: '该账号已停用' }
  }
  if (user.password !== password) {
    return { ok: false, message: '账号或密码错误' }
  }

  addOperationLog({
    operator: user.username,
    action: '登录系统',
    ip: payload.ip || '127.0.0.1',
    result: 'success'
  })

  return {
    ok: true,
    data: {
      user: sanitizeUser(user)
    }
  }
}

function registerUser(payload = {}) {
  const username = String(payload.username || '').trim()
  const email = String(payload.email || '').trim()
  const password = String(payload.password || '')
  const role = ['worker', 'expert'].includes(payload.role) ? payload.role : 'worker'
  const department = String(payload.department || '').trim()

  if (!username) return { ok: false, message: '请填写用户名' }
  if (!email) return { ok: false, message: '请填写邮箱' }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return { ok: false, message: '邮箱格式不正确' }
  }
  if (password.length < 6) return { ok: false, message: '密码至少 6 位' }

  if (systemUsers.some(u => u.username === username)) {
    return { ok: false, message: '用户名已存在' }
  }
  if (systemUsers.some(u => u.email === email)) {
    return { ok: false, message: '邮箱已被注册' }
  }

  const item = {
    id: `U-${Date.now()}`,
    username,
    email,
    password,
    role,
    department: department || (role === 'expert' ? '病害诊断部' : '技术部'),
    status: 'active'
  }
  systemUsers.unshift(item)

  addOperationLog({
    operator: username,
    action: '注册账号',
    ip: payload.ip || '127.0.0.1',
    result: 'success'
  })

  return {
    ok: true,
    data: {
      user: sanitizeUser(item)
    }
  }
}

function deleteSystemUser(id) {
  const index = systemUsers.findIndex(i => i.id === id)
  if (index === -1) return false
  systemUsers.splice(index, 1)
  return true
}

function listOperationLogs(query = {}) {
  let list = [...operationLogs]
  const pad = n => String(n).padStart(2, '0')
  const formatDate = d => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
  const today = new Date()
  const todayStr = formatDate(today)

  if (query.range === 'today') {
    list = list.filter(i => String(i.time || '').startsWith(todayStr) || String(i.time || '').startsWith('2024-12-22'))
  } else if (query.range === 'week') {
    const start = new Date(today)
    start.setDate(start.getDate() - 6)
    const startStr = formatDate(start)
    list = list.filter(i => {
      const t = String(i.time || '').slice(0, 10)
      return t >= startStr || t >= '2024-12-16'
    })
  } else if (query.range === 'month') {
    const start = new Date(today)
    start.setDate(start.getDate() - 29)
    const startStr = formatDate(start)
    list = list.filter(i => {
      const t = String(i.time || '').slice(0, 10)
      return t >= startStr || t >= '2024-12-01'
    })
  }
  // all: no filter

  return list.map(item => ({
    ...item,
    resultLabel: item.result === 'success' ? '成功' : '失败'
  }))
}

function getSystemSettings() {
  return { ...systemSettings }
}

function updateSystemSettings(payload) {
  systemSettings = { ...systemSettings, ...payload }
  return { ...systemSettings }
}

/** 本地模式：AI 报警 mock（PMIS 开启时走 fd_ai_alarm） */
let localAiAlarms = [
  {
    id: 'AIA-001',
    alarmCode: 'AI-DO-LOCAL-001',
    alarmTitle: 'AI预警：溶氧偏低',
    category: 'water',
    priority: 'high',
    alarmStatus: 'open',
    poolCode: 'A1',
    poolName: 'A1优化池',
    workshop: '一号车间',
    metricCode: 'do',
    metricName: '溶氧',
    currentValue: '4.2',
    thresholdValue: '5.0',
    unit: 'mg/L',
    alarmReason: '【Mock AI】近15分钟溶氧持续低于阈值。',
    aiSuggestion: '立即开启增氧设备，减少投喂，30分钟后复测溶氧。',
    confidence: 86,
    diagnosisBizId: '',
    knowledgeBizId: '',
    alarmTime: '2026-07-23 09:15:00',
    isMock: true,
    sourceSystem: 'fish-disease-ai-mock',
    externalId: ''
  }
]

function listAiAlarms(query = {}) {
  return localAiAlarms.filter(a => {
    if (query.status && a.alarmStatus !== query.status) return false
    if (query.category && a.category !== query.category) return false
    if (query.poolCode && !String(a.poolCode).includes(query.poolCode)) return false
    return true
  })
}

function createAiAlarm(payload = {}) {
  const item = {
    id: `AIA-${Date.now()}`,
    alarmCode: payload.alarmCode || `AI-LOCAL-${Date.now()}`,
    alarmTitle: payload.alarmTitle || `AI预警：${payload.metricName || '异常'}`,
    category: payload.category || 'water',
    priority: payload.priority || 'medium',
    alarmStatus: 'open',
    poolCode: payload.poolCode || '',
    poolName: payload.poolName || '',
    workshop: payload.workshop || '',
    metricCode: payload.metricCode || '',
    metricName: payload.metricName || '',
    currentValue: String(payload.currentValue != null ? payload.currentValue : ''),
    thresholdValue: String(payload.thresholdValue != null ? payload.thresholdValue : ''),
    unit: payload.unit || '',
    alarmReason: payload.alarmReason || '【Mock AI】本地模拟报警',
    aiSuggestion: payload.aiSuggestion || '',
    confidence: Number(payload.confidence) || 75,
    diagnosisBizId: payload.diagnosisBizId || '',
    knowledgeBizId: payload.knowledgeBizId || '',
    alarmTime: payload.alarmTime || new Date().toISOString().replace('T', ' ').slice(0, 19),
    isMock: true,
    sourceSystem: 'fish-disease-ai-mock',
    externalId: payload.externalId || ''
  }
  localAiAlarms.unshift(item)
  return item
}

function ackAiAlarm(id) {
  const item = localAiAlarms.find(a => a.id === id)
  if (!item) return null
  item.alarmStatus = 'acked'
  return item
}

function ensureAiAlarmMock(payload) {
  return createAiAlarm(payload)
}

module.exports = {
  getMeta,
  getSummary,
  listDiagnosis,
  getDiagnosis,
  createDiagnosis,
  updateDiagnosis,
  deleteDiagnosis,
  runDiagnosis,
  chatDiagnosis,
  appendDiagnosisMessage,
  listPonds,
  createPond,
  updatePond,
  deletePond,
  listFishSpecies,
  createFishSpecies,
  updateFishSpecies,
  deleteFishSpecies,
  listDiseaseTypes,
  createDiseaseType,
  updateDiseaseType,
  deleteDiseaseType,
  listEquipments,
  createEquipment,
  updateEquipment,
  deleteEquipment,
  getMonitorData,
  listCollectionRecords,
  createCollectionRecord,
  getArchiveSummary,
  listArchivedCases,
  getArchivedCase,
  getArchiveCategories,
  archiveFromDiagnosis,
  listSystemUsers,
  listChatContacts,
  getSystemUserById,
  createSystemUser,
  updateSystemUser,
  deleteSystemUser,
  loginUser,
  registerUser,
  addOperationLog,
  listOperationLogs,
  getSystemSettings,
  updateSystemSettings,
  listDiseaseKnowledge,
  getKnowledge,
  createDiseaseKnowledge,
  updateDiseaseKnowledge,
  deleteDiseaseKnowledge,
  listAiAlarms,
  createAiAlarm,
  ackAiAlarm,
  ensureAiAlarmMock
}
