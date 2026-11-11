/**
 * 水质页与系统总览共用的指标定义（名称 / 单位 / 设定区间）
 */
export const WATER_QUALITY_METRICS = [
  { key: 'lake_ph', label: 'pH值', unit: '', range: '6-10', cardClass: 'card-ph', color: '#39e692', icon: 'vital_signs' },
  { key: 'lake_do', label: '溶解氧', unit: ' mg/L', range: '6-10', cardClass: 'card-do', color: '#36b8ff', icon: 'air' },
  { key: 'lake_temp', label: '水温', unit: ' ℃', range: '20-28', cardClass: 'card-temp', color: '#ffd240', icon: 'device_thermostat' },
  { key: 'lake_tubid', label: '浊度', unit: ' NTU', range: '0-5', cardClass: 'card-turb', color: '#8cf0b8', icon: 'water' },
  { key: 'lake_orp', label: 'ORP', unit: ' mv', range: '200-400', cardClass: 'card-orp', color: '#63d2ff', icon: 'bolt' },
  { key: 'lake_alkalin', label: '碱度', unit: ' mg/L', range: '100-150', cardClass: 'card-alk', color: '#ff9f40', icon: 'trending_up' },
  { key: 'lake_hard', label: '硬度', unit: ' mg/L', range: '150-200', cardClass: 'card-hard', color: '#ff6b6b', icon: 'task_alt' },
  { key: 'lake_nirate', label: '硝酸盐', unit: ' mg/L', range: '0-35', cardClass: 'card-nitrate', color: '#ff88cc', icon: 'experiment' },
  { key: 'lake_an', label: '氨氮', unit: ' mg/L', range: '0-0.5', cardClass: 'card-ammonia', color: '#ff7a7a', icon: 'water_drops' },
  { key: 'lake_co2', label: '二氧化碳', unit: ' mg/L', range: '0-50', cardClass: 'card-co2', color: '#c39eff', icon: 'bubble_chart' },
  { key: 'lake_tss', label: 'TSS', unit: ' mg/L', range: '0-30', cardClass: 'card-tss', color: '#b0c4de', icon: 'filter_alt' },
  { key: 'lake_p', label: '总磷', unit: ' mg/L', range: '0-5', cardClass: 'card-tp', color: '#40e0d0', icon: 'labs' },
  { key: 'lake_n', label: '总氮', unit: ' mg/L', range: '0-20', cardClass: 'card-tn', color: '#e6e6e6', icon: 'bar_chart' }
]
