const mediaMeta = {
  fileTypes: ['图片', '视频'],
  categories: {
    体表: ['体侧', '头嘴', '腹部', '各鳍'],
    鳃: ['左鳃', '右鳃', '整体'],
    内脏: ['肝脏', '肠道', '其他']
  }
}

let mediaList = [
  {
    id: 'MED-2001',
    experimentId: 'EXP-2024-001',
    experimentName: '南美白对虾密度对比实验',
    fileName: 'A1-体表-体侧-0412.jpg',
    fileType: '图片',
    category: '体表',
    subCategory: '体侧',
    poolNo: 'A1',
    remark: '第 6 周采样，体色正常',
    size: 2457600,
    createdAt: '2024-04-12 09:45:10'
  },
  {
    id: 'MED-2002',
    experimentId: 'EXP-2024-001',
    experimentName: '南美白对虾密度对比实验',
    fileName: 'A1-鳃部-左鳃-0412.jpg',
    fileType: '图片',
    category: '鳃',
    subCategory: '左鳃',
    poolNo: 'A1',
    remark: '鳃丝完整，未见明显病变',
    size: 1984500,
    createdAt: '2024-04-12 09:48:33'
  },
  {
    id: 'MED-2003',
    experimentId: 'EXP-2024-004',
    experimentName: '大黄鱼白点病治疗方案试验',
    fileName: 'D1-治疗前体表白点.mp4',
    fileType: '视频',
    category: '体表',
    subCategory: '腹部',
    poolNo: 'D1',
    remark: '治疗前症状记录',
    size: 18654200,
    createdAt: '2024-02-05 15:20:08'
  },
  {
    id: 'MED-2004',
    experimentId: 'EXP-2024-004',
    experimentName: '大黄鱼白点病治疗方案试验',
    fileName: 'D1-治疗后复查.jpg',
    fileType: '图片',
    category: '体表',
    subCategory: '体侧',
    poolNo: 'D1',
    remark: '治疗 7 天后白点明显减少',
    size: 2210040,
    createdAt: '2024-02-12 10:11:56'
  },
  {
    id: 'MED-2005',
    experimentId: 'EXP-2024-003',
    experimentName: '草鱼夏花培育成活率实验',
    fileName: 'C1-夏花整体-0612.jpg',
    fileType: '图片',
    category: '体表',
    subCategory: '各鳍',
    poolNo: 'C1',
    remark: '第 4 周采样整体照片',
    size: 1845200,
    createdAt: '2024-06-12 17:03:41'
  },
  {
    id: 'MED-2006',
    experimentId: 'EXP-2024-006',
    experimentName: '凡纳滨对虾温控育苗实验',
    fileName: 'F1-育苗设备运行.mp4',
    fileType: '视频',
    category: '体表',
    subCategory: '头嘴',
    poolNo: 'F1',
    remark: '恒温设备运行状态录像',
    size: 12512000,
    createdAt: '2024-04-19 08:30:22'
  }
]

function getMediaMeta() {
  return mediaMeta
}

function listMedia({ category } = {}) {
  let result = [...mediaList]
  if (category) {
    result = result.filter(item => item.category === category)
  }
  return result
}

function createMedia(payload) {
  const item = {
    id: `MED-${Date.now()}`,
    experimentId: payload.experimentId,
    experimentName: payload.experimentName || '',
    fileName: payload.fileName,
    fileType: payload.fileType,
    category: payload.category,
    subCategory: payload.subCategory || '',
    poolNo: payload.poolNo || '',
    remark: payload.remark || '',
    size: payload.size || 0,
    createdAt: new Date().toISOString().replace('T', ' ').slice(0, 19)
  }
  mediaList.unshift(item)
  return item
}

function deleteMedia(id) {
  const index = mediaList.findIndex(item => item.id === id)
  if (index === -1) return false
  mediaList.splice(index, 1)
  return true
}

module.exports = {
  getMediaMeta,
  listMedia,
  createMedia,
  deleteMedia
}
