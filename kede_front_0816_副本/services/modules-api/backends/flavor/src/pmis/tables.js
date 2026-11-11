/** 既有表只读；风味专用新表 fo_* */

const EXISTING = {
  aquacultureLocation: 'test_table_aquaculture_location',
  waterQualityRealtime: 'test_table_water_quality_realtime',
  thresholdConfig: 'test_table_threshold_config',
  fishPoolStock: 'test_table_fish_pool_stock'
}

const NEW = {
  sysConfig: 'test_table_fo_sys_config',
  dict: 'test_table_fo_dict',
  pond: 'test_table_fo_pond',
  purchase: 'test_table_fo_purchase',
  entry: 'test_table_fo_entry',
  optLog: 'test_table_fo_opt_log',
  aiAlarm: 'test_table_fo_ai_alarm'
}

const NEW_META = [
  { short: 'fo_sys_config', table: NEW.sysConfig, cn: '风味_系统配置' },
  { short: 'fo_dict', table: NEW.dict, cn: '风味_业务字典' },
  { short: 'fo_pond', table: NEW.pond, cn: '风味_鱼池档案' },
  { short: 'fo_purchase', table: NEW.purchase, cn: '风味_采购单' },
  { short: 'fo_entry', table: NEW.entry, cn: '风味_入池批次' },
  { short: 'fo_opt_log', table: NEW.optLog, cn: '风味_优化监测日志' },
  { short: 'fo_ai_alarm', table: NEW.aiAlarm, cn: '风味_AI报警(Mock)' }
]

module.exports = { EXISTING, NEW, NEW_META }
