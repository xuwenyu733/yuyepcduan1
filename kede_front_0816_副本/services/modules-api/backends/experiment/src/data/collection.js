let records = [
  {
    id: 'COL-1001',
    experimentId: 'EXP-2024-001',
    experimentName: '南美白对虾密度对比实验',
    data: {
      goal: '提升高密度组成活率',
      measure: '增加微孔增氧时长',
      tempBefore: '27.2',
      tempAfter: '26.8',
      phBefore: '7.9',
      phAfter: '7.6',
      doBefore: '4.2',
      doAfter: '6.1'
    },
    operator: '刘涛',
    createdAt: '2024-04-12 09:30:15'
  },
  {
    id: 'COL-1002',
    experimentId: 'EXP-2024-001',
    experimentName: '南美白对虾密度对比实验',
    data: {
      goal: '抑制氨氮上升',
      measure: '换水 20% 并投放益生菌',
      tempBefore: '28.1',
      tempAfter: '27.5',
      phBefore: '8.1',
      phAfter: '7.8',
      doBefore: '5.0',
      doAfter: '6.4'
    },
    operator: '刘涛',
    createdAt: '2024-04-19 10:05:42'
  },
  {
    id: 'COL-1003',
    experimentId: 'EXP-2024-002',
    experimentName: '罗非鱼饲料配方试验',
    data: {
      disease: '无明显疾病',
      symptom: '摄食正常',
      medicine: '-',
      dose: '-',
      frequency: '-',
      period: '90天',
      mortalityBefore: '1.2',
      mortalityAfter: '0.8'
    },
    operator: '陈晓雯',
    createdAt: '2024-03-20 14:18:03'
  },
  {
    id: 'COL-1004',
    experimentId: 'EXP-2024-003',
    experimentName: '草鱼夏花培育成活率实验',
    data: {
      batchA: '轮虫开口组',
      batchB: '人工微粒组',
      survivalA: '85.6',
      survivalB: '78.2',
      growthA: '0.12',
      growthB: '0.09',
      conclusion: '轮虫开口组成活率更高'
    },
    operator: '刘涛',
    createdAt: '2024-06-10 11:22:50'
  },
  {
    id: 'COL-1005',
    experimentId: 'EXP-2024-004',
    experimentName: '大黄鱼白点病治疗方案试验',
    data: {
      disease: '白点病',
      symptom: '体表白点、摩擦池壁',
      medicine: '中草药复方',
      dose: '5ppm',
      frequency: '每日 1 次',
      period: '7天',
      mortalityBefore: '12.5',
      mortalityAfter: '3.1'
    },
    operator: '王敏',
    createdAt: '2024-02-28 16:40:11'
  },
  {
    id: 'COL-1006',
    experimentId: 'EXP-2024-006',
    experimentName: '凡纳滨对虾温控育苗实验',
    data: {
      device: '恒温控制系统 A',
      status: '运行异常后停机',
      runtime: '312',
      fault: '3',
      remark: '传感器漂移导致温控偏差'
    },
    operator: '刘涛',
    createdAt: '2024-04-18 08:55:27'
  }
]

function listRecords({ experimentId } = {}) {
  let result = [...records]
  if (experimentId) {
    result = result.filter(item => item.experimentId === experimentId)
  }
  return result
}

function createRecord(payload) {
  const item = {
    id: `COL-${Date.now()}`,
    experimentId: payload.experimentId,
    experimentName: payload.experimentName || '',
    data: payload.data || {},
    operator: payload.operator || '实验员',
    createdAt: new Date().toISOString().replace('T', ' ').slice(0, 19)
  }
  records.unshift(item)
  return item
}

module.exports = {
  listRecords,
  createRecord
}
