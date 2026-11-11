export type AlarmLevel = 'normal' | 'warn' | 'critical'

export type DeviceType =
  | 'tank'
  | 'pump'
  | 'sensor'
  | 'camera'
  | 'thermometer'
  | 'switch'
  | 'feeder'
  | 'aerator'
  | 'filter'

export type DeviceOption = {
  key: string
  label: string
  value: string
  unit?: string
  editable?: boolean
}

export type DeviceTelemetry = {
  id: string
  deviceCode: string
  name: string
  type: DeviceType
  hotspot: string
  workshop: string
  poolCode: string
  tempC: number
  dissolvedOxygen: number
  ph: number
  levelPct: number
  humidity: number
  powerOn: boolean
  switchState: 'on' | 'off' | 'auto'
  cameraOnline: boolean
  cameraScene: string
  rpm: number
  flowRate: number
  feeding: boolean
  alarm: AlarmLevel
  sceneNote: string
  options: DeviceOption[]
  updatedAt: string
}

type DeviceSeed = {
  id: string
  deviceCode: string
  name: string
  type: DeviceType
  hotspot: string
  workshop: string
  poolCode: string
  sceneNote: string
}

export const DEVICE_SEEDS: DeviceSeed[] = [
  // 开放厅
  {
    id: 'tank-01',
    deviceCode: 'TANK-OH-01',
    name: '开放厅 · 一号养成池',
    type: 'tank',
    hotspot: 'Hotspot_Tank_01',
    workshop: '开放厅',
    poolCode: 'OH-01',
    sceneNote: '圆形养成池，主养成鱼。可俯视水位与投喂区。',
  },
  {
    id: 'tank-02',
    deviceCode: 'TANK-OH-02',
    name: '开放厅 · 二号养成池',
    type: 'tank',
    hotspot: 'Hotspot_Tank_02',
    workshop: '开放厅',
    poolCode: 'OH-02',
    sceneNote: '圆形养成池，与一号并联循环。',
  },
  {
    id: 'tank-03',
    deviceCode: 'TANK-OH-03',
    name: '开放厅 · 三号养成池',
    type: 'tank',
    hotspot: 'Hotspot_Tank_03',
    workshop: '开放厅',
    poolCode: 'OH-03',
    sceneNote: '重点监测溶氧；偶发低氧预警演示。',
  },
  {
    id: 'tank-04',
    deviceCode: 'TANK-OH-04',
    name: '开放厅 · 四号苗种池',
    type: 'tank',
    hotspot: 'Hotspot_Tank_04',
    workshop: '开放厅',
    poolCode: 'OH-04',
    sceneNote: '苗种池，水位与水温要求更稳。',
  },
  {
    id: 'pump-01',
    deviceCode: 'PUMP-OH-A',
    name: '开放厅 · 循环水泵 A',
    type: 'pump',
    hotspot: 'Hotspot_Pump_01',
    workshop: '开放厅',
    poolCode: 'OH-01',
    sceneNote: '主循环泵，位于走道西侧机座。',
  },
  {
    id: 'sensor-do-01',
    deviceCode: 'SNS-OH-DO-01',
    name: '开放厅 · 溶氧探头 #1',
    type: 'sensor',
    hotspot: 'Hotspot_Sensor_DO_01',
    workshop: '开放厅',
    poolCode: 'OH-02',
    sceneNote: '走道立杆溶氧探头，覆盖二号池。',
  },
  {
    id: 'cam-oh-01',
    deviceCode: 'CAM-OH-01',
    name: '开放厅 · 球机摄像头',
    type: 'camera',
    hotspot: 'Hotspot_Cam_OH_01',
    workshop: '开放厅',
    poolCode: 'OH-01',
    sceneNote: '北侧梁下球机，覆盖一～三号池全景。',
  },
  {
    id: 'thermo-oh-01',
    deviceCode: 'TMP-OH-01',
    name: '开放厅 · 水温计',
    type: 'thermometer',
    hotspot: 'Hotspot_Thermo_OH_01',
    workshop: '开放厅',
    poolCode: 'OH-01',
    sceneNote: '一号池壁挂式温度变送器。',
  },
  {
    id: 'sw-oh-01',
    deviceCode: 'SW-OH-PUMP',
    name: '开放厅 · 水泵启停开关',
    type: 'switch',
    hotspot: 'Hotspot_Switch_OH_01',
    workshop: '开放厅',
    poolCode: 'OH-01',
    sceneNote: '机旁控制箱启停开关（演示可切换）。',
  },
  {
    id: 'feeder-oh-01',
    deviceCode: 'FEED-OH-01',
    name: '开放厅 · 自动投喂机',
    type: 'feeder',
    hotspot: 'Hotspot_Feeder_OH_01',
    workshop: '开放厅',
    poolCode: 'OH-02',
    sceneNote: '二号池上方投喂机，支持定时/手动。',
  },
  {
    id: 'aerator-oh-01',
    deviceCode: 'AER-OH-01',
    name: '开放厅 · 增氧机',
    type: 'aerator',
    hotspot: 'Hotspot_Aerator_OH_01',
    workshop: '开放厅',
    poolCode: 'OH-03',
    sceneNote: '三号池叶轮增氧，联动溶氧阈值。',
  },
  // 厂房一号
  {
    id: 'b1-tank-01',
    deviceCode: 'TANK-B1-01',
    name: '厂房一号 · 跑道池 1',
    type: 'tank',
    hotspot: 'Hotspot_B1_Tank_01',
    workshop: '厂房一号',
    poolCode: 'B1-01',
    sceneNote: '矩形跑道池，水流自西向东。',
  },
  {
    id: 'b1-tank-02',
    deviceCode: 'TANK-B1-02',
    name: '厂房一号 · 跑道池 2',
    type: 'tank',
    hotspot: 'Hotspot_B1_Tank_02',
    workshop: '厂房一号',
    poolCode: 'B1-02',
    sceneNote: '矩形跑道池 2。',
  },
  {
    id: 'b1-tank-03',
    deviceCode: 'TANK-B1-03',
    name: '厂房一号 · 跑道池 3',
    type: 'tank',
    hotspot: 'Hotspot_B1_Tank_03',
    workshop: '厂房一号',
    poolCode: 'B1-03',
    sceneNote: '矩形跑道池 3。',
  },
  {
    id: 'b1-tank-04',
    deviceCode: 'TANK-B1-04',
    name: '厂房一号 · 跑道池 4',
    type: 'tank',
    hotspot: 'Hotspot_B1_Tank_04',
    workshop: '厂房一号',
    poolCode: 'B1-04',
    sceneNote: '矩形跑道池 4。',
  },
  {
    id: 'b1-tank-05',
    deviceCode: 'TANK-B1-05',
    name: '厂房一号 · 跑道池 5',
    type: 'tank',
    hotspot: 'Hotspot_B1_Tank_05',
    workshop: '厂房一号',
    poolCode: 'B1-05',
    sceneNote: '矩形跑道池 5。',
  },
  {
    id: 'b1-tank-06',
    deviceCode: 'TANK-B1-06',
    name: '厂房一号 · 跑道池 6',
    type: 'tank',
    hotspot: 'Hotspot_B1_Tank_06',
    workshop: '厂房一号',
    poolCode: 'B1-06',
    sceneNote: '矩形跑道池 6。',
  },
  {
    id: 'b1-pump-01',
    deviceCode: 'PUMP-B1-01',
    name: '厂房一号 · 循环水泵',
    type: 'pump',
    hotspot: 'Hotspot_B1_Pump_01',
    workshop: '厂房一号',
    poolCode: 'B1-01',
    sceneNote: 'RAS 机房旁循环泵。',
  },
  {
    id: 'b1-filter-01',
    deviceCode: 'FLT-B1-01',
    name: '厂房一号 · 过滤单元',
    type: 'filter',
    hotspot: 'Hotspot_B1_Filter_01',
    workshop: '厂房一号',
    poolCode: 'B1-01',
    sceneNote: '西北角微滤/生物过滤组合。',
  },
  {
    id: 'b1-sensor-do-01',
    deviceCode: 'SNS-B1-DO-01',
    name: '厂房一号 · 溶氧探头',
    type: 'sensor',
    hotspot: 'Hotspot_B1_Sensor_DO_01',
    workshop: '厂房一号',
    poolCode: 'B1-03',
    sceneNote: '中央走道溶氧监测。',
  },
  {
    id: 'b1-cam-01',
    deviceCode: 'CAM-B1-01',
    name: '厂房一号 · 枪机摄像头',
    type: 'camera',
    hotspot: 'Hotspot_Cam_B1_01',
    workshop: '厂房一号',
    poolCode: 'B1-02',
    sceneNote: '南侧门楣枪机，覆盖跑道池阵列。',
  },
  {
    id: 'b1-thermo-01',
    deviceCode: 'TMP-B1-01',
    name: '厂房一号 · 水温计',
    type: 'thermometer',
    hotspot: 'Hotspot_Thermo_B1_01',
    workshop: '厂房一号',
    poolCode: 'B1-01',
    sceneNote: '一号跑道池温度探头。',
  },
  {
    id: 'b1-sw-01',
    deviceCode: 'SW-B1-LIGHT',
    name: '厂房一号 · 照明回路开关',
    type: 'switch',
    hotspot: 'Hotspot_Switch_B1_01',
    workshop: '厂房一号',
    poolCode: 'B1-00',
    sceneNote: '入口配电箱照明回路。',
  },
  {
    id: 'b1-feeder-01',
    deviceCode: 'FEED-B1-01',
    name: '厂房一号 · 投喂机',
    type: 'feeder',
    hotspot: 'Hotspot_Feeder_B1_01',
    workshop: '厂房一号',
    poolCode: 'B1-02',
    sceneNote: '二号跑道池定点投喂。',
  },
  {
    id: 'b1-aerator-01',
    deviceCode: 'AER-B1-01',
    name: '厂房一号 · 纳米增氧',
    type: 'aerator',
    hotspot: 'Hotspot_Aerator_B1_01',
    workshop: '厂房一号',
    poolCode: 'B1-04',
    sceneNote: '四号池底部纳米盘增氧。',
  },
]

export const TYPE_LABELS: Record<DeviceType, string> = {
  tank: '鱼池',
  pump: '水泵',
  sensor: '水质传感器',
  camera: '摄像头',
  thermometer: '温度计',
  switch: '开关',
  feeder: '投喂机',
  aerator: '增氧机',
  filter: '过滤设备',
}

export const TYPE_FILTERS: { key: DeviceType | 'all'; label: string }[] = [
  { key: 'all', label: '全部' },
  { key: 'tank', label: '鱼池' },
  { key: 'pump', label: '水泵' },
  { key: 'sensor', label: '传感器' },
  { key: 'camera', label: '摄像头' },
  { key: 'thermometer', label: '温度计' },
  { key: 'switch', label: '开关' },
  { key: 'feeder', label: '投喂' },
  { key: 'aerator', label: '增氧' },
  { key: 'filter', label: '过滤' },
]

export const ALARM_COLORS: Record<AlarmLevel, number> = {
  normal: 0x3d9b6e,
  warn: 0xd4a017,
  critical: 0xd64545,
}

export const ALARM_LABELS: Record<AlarmLevel, string> = {
  normal: '正常',
  warn: '预警',
  critical: '报警',
}

function clamp(n: number, min: number, max: number): number {
  return Math.min(max, Math.max(min, n))
}

function jitter(base: number, amp: number): number {
  return base + (Math.random() * 2 - 1) * amp
}

function buildOptions(seed: DeviceSeed, metrics: Partial<DeviceTelemetry>): DeviceOption[] {
  const common: DeviceOption[] = [
    { key: 'device_code', label: '设备编码', value: seed.deviceCode },
    { key: 'pool_code', label: '鱼池编码', value: seed.poolCode },
    { key: 'workshop', label: '车间', value: seed.workshop },
    { key: 'hotspot', label: '三维锚点', value: seed.hotspot },
  ]

  switch (seed.type) {
    case 'camera':
      return [
        ...common,
        { key: 'resolution', label: '分辨率', value: '2560×1440' },
        { key: 'codec', label: '编码', value: 'H.265' },
        { key: 'ptz', label: '云台', value: '支持', editable: true },
        { key: 'record', label: '录像', value: '连续', editable: true },
        {
          key: 'online',
          label: '在线',
          value: metrics.cameraOnline ? '在线' : '离线',
        },
      ]
    case 'thermometer':
      return [
        ...common,
        { key: 'range', label: '量程', value: '0～50', unit: '°C' },
        { key: 'accuracy', label: '精度', value: '±0.2', unit: '°C' },
        {
          key: 'temp',
          label: '当前温度',
          value: (metrics.tempC ?? 0).toFixed(1),
          unit: '°C',
        },
      ]
    case 'switch':
      return [
        ...common,
        {
          key: 'state',
          label: '开关状态',
          value: metrics.switchState ?? 'off',
          editable: true,
        },
        { key: 'mode', label: '控制模式', value: '就地/远程', editable: true },
        { key: 'rated', label: '额定电流', value: '16', unit: 'A' },
      ]
    case 'pump':
      return [
        ...common,
        {
          key: 'power',
          label: '运行',
          value: metrics.powerOn ? '运行' : '停止',
          editable: true,
        },
        {
          key: 'rpm',
          label: '转速',
          value: String(Math.round(metrics.rpm ?? 0)),
          unit: 'rpm',
        },
        {
          key: 'flow',
          label: '流量',
          value: (metrics.flowRate ?? 0).toFixed(1),
          unit: 'm³/h',
        },
      ]
    case 'feeder':
      return [
        ...common,
        {
          key: 'feeding',
          label: '投喂',
          value: metrics.feeding ? '进行中' : '待机',
          editable: true,
        },
        { key: 'dose', label: '单次剂量', value: '0.8', unit: 'kg', editable: true },
        { key: 'schedule', label: '计划', value: '08:00 / 16:00', editable: true },
      ]
    case 'aerator':
      return [
        ...common,
        {
          key: 'power',
          label: '运行',
          value: metrics.powerOn ? '运行' : '停止',
          editable: true,
        },
        {
          key: 'rpm',
          label: '转速',
          value: String(Math.round(metrics.rpm ?? 0)),
          unit: 'rpm',
        },
        { key: 'link_do', label: '溶氧联动', value: '开启', editable: true },
      ]
    case 'filter':
      return [
        ...common,
        {
          key: 'power',
          label: '运行',
          value: metrics.powerOn ? '运行' : '停止',
        },
        {
          key: 'level',
          label: '滤材负荷',
          value: (metrics.levelPct ?? 0).toFixed(0),
          unit: '%',
        },
        { key: 'backwash', label: '反洗', value: '自动', editable: true },
      ]
    case 'sensor':
      return [
        ...common,
        { key: 'params', label: '监测项', value: 'DO / pH / 水温' },
        {
          key: 'do',
          label: '溶氧',
          value: (metrics.dissolvedOxygen ?? 0).toFixed(2),
          unit: 'mg/L',
        },
        { key: 'calib', label: '最近校准', value: '2026-08-01' },
      ]
    default:
      return [
        ...common,
        {
          key: 'level',
          label: '液位',
          value: (metrics.levelPct ?? 0).toFixed(0),
          unit: '%',
        },
        {
          key: 'temp',
          label: '水温',
          value: (metrics.tempC ?? 0).toFixed(1),
          unit: '°C',
        },
        {
          key: 'do',
          label: '溶氧',
          value: (metrics.dissolvedOxygen ?? 0).toFixed(2),
          unit: 'mg/L',
        },
      ]
  }
}

export function deriveAlarm(d: DeviceTelemetry): AlarmLevel {
  if (d.type === 'camera' && !d.cameraOnline) return 'critical'
  if (d.type === 'switch' && d.switchState === 'off' && d.deviceCode.includes('PUMP')) return 'warn'
  if (d.type === 'pump' || d.type === 'aerator' || d.type === 'filter') {
    if (!d.powerOn) return 'warn'
    if (d.levelPct > 90 && d.type === 'filter') return 'critical'
  }
  if (d.type === 'thermometer' && (d.tempC > 30 || d.tempC < 18)) return 'critical'
  if (d.type === 'thermometer' && (d.tempC > 28 || d.tempC < 20)) return 'warn'
  if (['tank', 'sensor'].includes(d.type)) {
    if (d.dissolvedOxygen < 4.5 || d.tempC > 30 || d.ph < 6.5 || d.ph > 8.8) return 'critical'
    if (d.dissolvedOxygen < 5.5 || d.tempC > 28 || d.levelPct < 40) return 'warn'
  }
  return 'normal'
}

function seedMetrics(seed: DeviceSeed, i: number): Omit<DeviceTelemetry, 'id' | 'deviceCode' | 'name' | 'type' | 'hotspot' | 'workshop' | 'poolCode' | 'sceneNote' | 'options' | 'alarm' | 'updatedAt'> {
  return {
    tempC: 24 + (i % 5) * 0.6,
    dissolvedOxygen: 6.4 - (i % 4) * 0.25,
    ph: 7.1 + (i % 3) * 0.12,
    levelPct: 72 - (i % 6) * 3,
    humidity: 55 + (i % 7),
    powerOn: i % 5 !== 0,
    switchState: i % 4 === 0 ? 'auto' : i % 3 === 0 ? 'off' : 'on',
    cameraOnline: true,
    cameraScene:
      seed.type === 'camera'
        ? seed.workshop === '厂房一号'
          ? '跑道池阵列俯视'
          : '养成池全景'
        : '—',
    rpm: seed.type === 'pump' || seed.type === 'aerator' ? 1200 + (i % 5) * 80 : 0,
    flowRate: seed.type === 'pump' ? 35 + (i % 4) * 2.5 : 0,
    feeding: seed.type === 'feeder' ? i % 2 === 0 : false,
  }
}

export function createInitialDevices(): DeviceTelemetry[] {
  return DEVICE_SEEDS.map((seed, i) => {
    const metrics = seedMetrics(seed, i)
    const partial = { ...seed, ...metrics } as DeviceTelemetry
    partial.options = buildOptions(seed, metrics)
    partial.alarm = deriveAlarm(partial)
    partial.updatedAt = new Date().toISOString()
    return partial
  })
}

export function tickDevices(devices: DeviceTelemetry[]): DeviceTelemetry[] {
  return devices.map((d, i) => {
    const next: DeviceTelemetry = {
      ...d,
      tempC: clamp(jitter(d.tempC, d.type === 'thermometer' ? 0.12 : 0.06), 16, 34),
      dissolvedOxygen: clamp(jitter(d.dissolvedOxygen, 0.1), 3.0, 9.5),
      ph: clamp(jitter(d.ph, 0.025), 6.0, 9.0),
      levelPct: clamp(jitter(d.levelPct, 0.5), 18, 98),
      humidity: clamp(jitter(d.humidity, 0.4), 35, 85),
      rpm: d.powerOn && (d.type === 'pump' || d.type === 'aerator')
        ? clamp(jitter(d.rpm, 25), 600, 1800)
        : d.type === 'pump' || d.type === 'aerator'
          ? 0
          : d.rpm,
      flowRate: d.powerOn && d.type === 'pump' ? clamp(jitter(d.flowRate, 0.8), 10, 60) : d.type === 'pump' ? 0 : d.flowRate,
      feeding: d.type === 'feeder' ? (i % 3 === 0 ? Math.random() > 0.55 : d.feeding) : d.feeding,
      cameraOnline: d.type === 'camera' ? Math.random() > 0.01 : d.cameraOnline,
      updatedAt: new Date().toISOString(),
    }

    if (d.id === 'tank-03' && Math.random() < 0.1) {
      next.dissolvedOxygen = clamp(next.dissolvedOxygen - 1.2, 3.0, 9.5)
    }

    const seed = DEVICE_SEEDS.find((s) => s.id === d.id)!
    next.options = buildOptions(seed, next)
    next.alarm = deriveAlarm(next)
    return next
  })
}

/** 列表副标题：按类型展示最关键指标 */
export function deviceSummaryLine(d: DeviceTelemetry): string {
  switch (d.type) {
    case 'camera':
      return d.cameraOnline ? `在线 · ${d.cameraScene}` : '离线'
    case 'thermometer':
      return `${d.tempC.toFixed(1)} °C`
    case 'switch':
      return `状态 ${d.switchState}`
    case 'pump':
      return d.powerOn ? `${d.flowRate.toFixed(0)} m³/h · ${Math.round(d.rpm)} rpm` : '已停机'
    case 'feeder':
      return d.feeding ? '投喂中' : '待机'
    case 'aerator':
      return d.powerOn ? `${Math.round(d.rpm)} rpm` : '已停机'
    case 'filter':
      return `负荷 ${d.levelPct.toFixed(0)}%`
    case 'sensor':
      return `DO ${d.dissolvedOxygen.toFixed(1)} · pH ${d.ph.toFixed(1)}`
    default:
      return `DO ${d.dissolvedOxygen.toFixed(1)} · ${d.tempC.toFixed(1)}°C`
  }
}

export function refreshDeviceView(d: DeviceTelemetry): DeviceTelemetry {
  const seed = DEVICE_SEEDS.find((s) => s.id === d.id)
  if (!seed) return d
  const next = { ...d }
  next.options = buildOptions(seed, next)
  next.alarm = deriveAlarm(next)
  next.updatedAt = d.updatedAt || new Date().toISOString()
  return next
}

export function applyDeviceAction(
  devices: DeviceTelemetry[],
  deviceId: string,
  action: string,
): DeviceTelemetry[] {
  return devices.map((d) => {
    if (d.id !== deviceId) return d
    const next = { ...d }
    if (action === 'toggle_power') next.powerOn = !next.powerOn
    if (action === 'toggle_switch') {
      next.switchState = next.switchState === 'off' ? 'on' : 'off'
      next.powerOn = next.switchState !== 'off'
    }
    if (action === 'toggle_feeding') next.feeding = !next.feeding
    if (action === 'cycle_switch_mode') {
      next.switchState = next.switchState === 'on' ? 'auto' : next.switchState === 'auto' ? 'off' : 'on'
    }
    if (!next.powerOn && (next.type === 'pump' || next.type === 'aerator')) {
      next.rpm = 0
      next.flowRate = 0
    }
    return refreshDeviceView(next)
  })
}
