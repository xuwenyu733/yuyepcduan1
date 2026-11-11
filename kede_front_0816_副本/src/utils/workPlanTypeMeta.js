const TYPE_META = {
  weekly: { label: '周计划', icon: 'el-icon-date' },
  week: { label: '周计划', icon: 'el-icon-date' },
  周计划: { label: '周计划', icon: 'el-icon-date' },
  monthly: { label: '月计划', icon: 'el-icon-s-grid' },
  month: { label: '月计划', icon: 'el-icon-s-grid' },
  月计划: { label: '月计划', icon: 'el-icon-s-grid' },
  quarterly: { label: '季度计划', icon: 'el-icon-s-data' },
  quarter: { label: '季度计划', icon: 'el-icon-s-data' },
  季度计划: { label: '季度计划', icon: 'el-icon-s-data' },
  annual: { label: '年度计划', icon: 'el-icon-trophy' },
  year: { label: '年度计划', icon: 'el-icon-trophy' },
  yearly: { label: '年度计划', icon: 'el-icon-trophy' },
  年度计划: { label: '年度计划', icon: 'el-icon-trophy' },
  daily: { label: '日计划', icon: 'el-icon-alarm-clock' },
  day: { label: '日计划', icon: 'el-icon-alarm-clock' },
  日计划: { label: '日计划', icon: 'el-icon-alarm-clock' }
}

export function workPlanTypeIcon(type) {
  const key = String(type || '').trim()
  return (TYPE_META[key] && TYPE_META[key].icon) || 'el-icon-notebook-2'
}

export function workPlanTypeLabel(type) {
  const key = String(type || '').trim()
  return (TYPE_META[key] && TYPE_META[key].label) || type || '未知'
}
