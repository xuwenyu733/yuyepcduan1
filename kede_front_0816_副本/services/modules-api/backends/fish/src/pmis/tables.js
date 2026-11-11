/** 既有表（只读）与新建 fd_* 表名常量。物理表 = test_table_ + 短名 */

const EXISTING = {
  aquacultureLocation: 'test_table_aquaculture_location',
  waterQualityRealtime: 'test_table_water_quality_realtime',
  waterQualityHistory: 'test_table_water_quality_history',
  deviceRealtime: 'test_table_device_realtime',
  thresholdConfig: 'test_table_threshold_config',
  fishPoolStock: 'test_table_fish_pool_stock',
  feedingRecord: 'test_table_feeding_record',
  mortalityRecord: 'test_table_mortality_record',
  alarmRecord: 'test_table_alarm_record'
}

const NEW = {
  species: 'test_table_fd_species',
  diseaseTypes: 'test_table_fd_disease_types',
  diseaseKnowledge: 'test_table_fd_disease_knowledge',
  diagnosisRecords: 'test_table_fd_diagnosis_records',
  diagnosisMessages: 'test_table_fd_diagnosis_messages',
  collectionRecords: 'test_table_fd_collection_records',
  archivedCases: 'test_table_fd_archived_cases',
  equipments: 'test_table_fd_equipments',
  operationLogs: 'test_table_fd_operation_logs',
  systemSettings: 'test_table_fd_system_settings',
  aiAlarm: 'test_table_fd_ai_alarm'
}

const NEW_META = [
  { short: 'fd_species', table: NEW.species, cn: '鱼病_鱼品种' },
  { short: 'fd_disease_types', table: NEW.diseaseTypes, cn: '鱼病_病害类型' },
  { short: 'fd_disease_knowledge', table: NEW.diseaseKnowledge, cn: '鱼病_病害知识库' },
  { short: 'fd_diagnosis_records', table: NEW.diagnosisRecords, cn: '鱼病_诊断记录' },
  { short: 'fd_diagnosis_messages', table: NEW.diagnosisMessages, cn: '鱼病_诊断消息' },
  { short: 'fd_collection_records', table: NEW.collectionRecords, cn: '鱼病_数据采集' },
  { short: 'fd_archived_cases', table: NEW.archivedCases, cn: '鱼病_案例归档' },
  { short: 'fd_equipments', table: NEW.equipments, cn: '鱼病_监测设备台账' },
  { short: 'fd_operation_logs', table: NEW.operationLogs, cn: '鱼病_操作日志' },
  { short: 'fd_system_settings', table: NEW.systemSettings, cn: '鱼病_系统设置' },
  { short: 'fd_ai_alarm', table: NEW.aiAlarm, cn: '鱼病_AI报警(Mock)' }
]

module.exports = {
  EXISTING,
  NEW,
  NEW_META
}
