/** 既有表只读；阈值专用新表 th_* */

const EXISTING = {
  thresholdConfig: 'test_table_threshold_config',
  aquacultureLocation: 'test_table_aquaculture_location',
  waterQualityRealtime: 'test_table_water_quality_realtime'
}

const NEW = {
  moduleConfig: 'test_table_th_module_config',
  aiAlarm: 'test_table_th_ai_alarm'
}

const NEW_META = [
  { short: 'th_module_config', table: NEW.moduleConfig, cn: '阈值_模块配置' },
  { short: 'th_ai_alarm', table: NEW.aiAlarm, cn: '阈值_AI报警(Mock)' }
]

const MODULE_NAMES = {
  meta: '元信息',
  water: '水质阈值',
  device: '设备运行',
  mortality: '鱼类死亡',
  health: '健康评分',
  feeding: '投喂与生长',
  alarm: 'AI 报警',
  energy: '能耗管理',
  experiment: '实验系统',
  security: '人员安防',
  holding: '暂养损耗',
  timeout: '任务超时',
  association: '数据关联'
}

module.exports = { EXISTING, NEW, NEW_META, MODULE_NAMES }
