/**
 * Created by yaominglei
 *
 * 数据字典工具类
 */
import store from '@/store'

export const DICT_TYPE_WHITE = {
	//  白名单
	"detailType": true,
	"detailTypeName": true,
}

export const DICT_TYPE = {
	// -- 1.1 日巡检_设备区_鱼
	//  摄食状态
	"foodConsumption": "摄食量（具体数据）",
	"mortality": "死亡量（具体数据）",
	"feedingIntensity": "摄食强度",
	"fecalState": "粪便状态",  
	
	//  鱼体状态
	"outlierSearchGame": "离群索游",
	"respiratoryRate": "呼吸频率",
	"surfaceCondition": "体表状态",
	"swimmingPosture": "游泳姿态",
	"relativeFatness": "肥满度",
	// -- 1.2 日巡检_设备区_鱼
	// 传感器指标
	"temperature": "温度",
	"saturationPercentageOfDissolvedOxygen": "溶氧/饱和度",
	"turbidity": "浊度",
	"salinity": "盐度",
	// 观察及检测指标
	"liquidLevel": "液位",
	"ammoniaNitrogen": "氨氮",
	"nitrite": "亚硝酸盐",
	"foam": "泡沫",
	"oilFilm": "油膜",
	"whiteTurbidity": "白浊",
	"cloudy": "浑浊",
	"transparency": "透明度",
	"refreshingDegree": "清爽度",
	"pollutionAccumulationAtTheBottomOfThePool": "池底积污情况",
	// -- 1.3 日巡检_设备区_鱼
	//  增氧控制柜
	"trachea": "气管",
	"solenoidValve": "电磁阀",
	"aerationDisc": "曝气盘",
	"dissolvedOxygenSensor": "溶氧传感器",
	"interceptNetworkCleaning": "拦截网清洗",
	"sealingOfEmptyingPipe": "排空管密封性",
	"pollutionAccumulationSituation": "粪便及积污情况，及时拔管",
	//  侧排水箱
	"interceptNetworkCleaning": "拦截网清洗",
	//  竖流沉淀器
	"sealingOfEmptyingPipe": "排空管密封性",
	"pollutionAccumulationSituation": "粪便及积污情况，及时拔管",
	// -- 2.1 日巡检_设备区_水泵微滤机
	// 水泵
	"runningState": "运行状态",
	"frequencyConverterFrequency": "变频器频率",
	"controlCabinetStatus": "控制柜状态",
	"checkTheDataOfTheLiquidLevelSensor": "检查液位传感器数据",
	// 微滤机
	"controlCabinetStatus": "控制柜状态",
	"checkTheResponseStatusOfTheLiquidLevelProbe": "检查液位探头应答状态",
	"tightness": "密封性",
	"isTheFilterClogged": "滤网是否堵塞、破损",
	"backwashNozzleBlockage": "反冲洗喷嘴堵塞、松动",
	"backwashFrequency": "反冲洗频率",
	"backwashPressure": "反冲洗压力",
	"sewageSump": "集污槽",
	"runningState": "运行状态",
	// -- 2.2 日巡检_设备区_移动床固定床脱气床臭氧发生器
	// 移动床
	"liquidLevelSituation": "液位情况",
	"packingState": "填料状态（翻滚、堆积）",
	"aerationSituation": "曝气情况",
	// 固定床
	"liquidLevelSituation": "液位情况",
	// 脱气床
	"waterFlowSituation":"过水情况",
	"fanOperatingStatus":"风机运行状态",
	// 紫外杀菌器
	"controlCabinetStatus":"控制柜状态",
	"lightIntensity":"光照强度",
	// 臭氧发生器
	"airTightness":"气密性",
	"inletAndOutletAirflowSetting":"进出气流量设定是否合理",
	"coolingWater":"冷却水（纯氧型）",
	// -- 2.3 日巡检_设备区_氧锥风机
	// 氧锥
	"statusOfEachValve":"各阀门状态",
	"oxygenConePumpStatus":"氧锥泵状态",
	"oxygenConeOxygenationControlCabinet":"氧锥增氧控制柜",
	"internalPressure":"内部压力",
	"liquidOxygenFlowRate":"液氧流量",
	"internalLiquidLevel":"内部液位",
	"airTightness":"气密性",
	// 风机
	"runningState":"运行状态（启停、声音、振动）",
	"fanControlCabinet":"风机控制柜",
	"airOutletPressure":"出风口压力",
	"lubricatingOilStatus":"润滑油状态",
	"airVolume":"风量（如有）",
	// -- 2.4 日巡检_设备区_中央控制柜液氧罐水源热泵
	// 中央控制柜
	"electricitySupplySituation":"电力供应情况",
	"operatingStatusOfEachDevice":"各设备运行状态",
	"handlingAlarmItems":"处理报警项",
	"electricalComponentStatus":"电器元件状态",
	"operatingMode":"运行模式（本地/远程）",
	"dataFromVariousSensors":"各传感器数据显示是否正常",
	// 液氧罐
	"stock":"存量",
	"pressure":"压力",
	"pressureReducingValve":"减压阀",
	"gasifier":"气化器",
	"airTightnessOfGasPath":"气路气密性",
	// 水源热泵
	"waterPump":"水泵",
	"solenoidValve":"电磁阀",
	"compressor":"压缩机",
	"inletAndOutletWaterTemperature":"进出水温度",
	// -- 2.5 日巡检_设备区_碱度添加补水源水系统投饵机
	// 碱度添加系统
	"phSensor":"pH传感器",
	"solenoidValve":"电磁阀",
	"liquidLevelSensor":"液位传感器",
	"waterPump":"水泵",
	"mixingMotor":"搅拌电机（或曝气搅拌）",
	// 补水系统
	"waterPump":"水泵",
	"solenoidValve":"电磁阀",
	"constantPressureWaterSupplyController":"恒压供水控制器",
	// 源水系统
	"waterPump":"水泵",
	"liquidLevelSensor":"液位传感器",
	"ozoneDisinfectionAndSterilizationSystem":"紫外/臭氧消杀系统",
	// 自动投饵机
	"automaticFeedingMachine":"自动投饵机",
	// -- 3.1 定期_养殖区_鱼
	// 打样（3-15天）
	"specifications":"规格",
	"fishInventory":"存鱼量",
	"growthRate":"生长率",
	"feedCoefficient":"饵料系数",
	// 观察与镜检（3-7天） 体表
	"woundCondition":"伤口状态（大小、溃烂检测）",
	"mucus":"粘液（多少、寄生虫）",
	"finRay":"鳍条（完整度、血液到达位置、寄生虫）",
	"scale":"鳞片（掉鳞、完整度）",
	"dirty":"脏污（挂脏、斑点、异物）",
	"anus":"肛门（红肿）",
	"gill":"鳃（完整度、颜色、血液充盈度、异物、寄生虫）",
	// 观察与镜检（3-7天） 体内
	"spleen":"脾脏（颜色、大小、斑点、孢子）",
	"intestine":"肠道（颜色、充血、肠绒毛、肠壁厚度、弹性）",
	"abdominalCavity":"腹腔（充血）",
	"kidney":"肾脏（颜色，充盈度、大小）",
	"muscle":"肌肉（颜色、紧实度）",
	// -- 3.2 定期_养殖区_水
	// 传感器指标（7天）
	"ph":"PH",
	"temperature":"温度",
	"saturationPercentageOfDissolvedOxygen":"溶氧/饱和度",
	"orp":"ORP",
	"turbidity":"浊度",
	"salinity":"盐度",
	// 检测指标（5-7天）
	"alkalinity":"总碱度",
	"ammoniaNitrogen":"氨氮",
	"nitrite":"亚硝酸盐",
	"totalHardness":"总硬度",
	// 观察指标（7天）
	"foam":"泡沫",
	"oilFilm":"油膜",
	"whiteTurbidity":"白浊",
	"cloudy":"浑浊",
	"transparency":"透明度",
	"refreshingDegree":"清爽度",
	"pollutionAccumulationAtTheBottomOfThePool":"池底积污情况",
	// ---------------------------- old_定期_仓库区 start
	// 饲料（先进先出、后进后出）
	"checkTheStockOfFeed":"检查饲料存量（保证剩余量不少于15天；跟踪采购进度，保证新饲料按时到场）",
	"checkTheStatusOfTheFeed":"检查饲料状态（保质期、异味、发霉、虫蛀、鼠咬），预警临期饲料，尽快投喂；变质饲料，及时销毁",
	"isThereAnyQualityIssue":"入库前检查饲料生产日期、包装完整性，抽检有无质量问题",
	"controlTheAmountOfFeedProcurement":"合理控制饲料采购量，保持库存量不超过一到三个月",
	// 动保（先进先出、后进后出）
	"dynamicStorageCapacity":"检查动保存量（保证剩余量不少于3次治疗量；跟踪采购进度，保证新动保按时到场）",
	"dynamicProtectionStatus":"检查动保状态（保质期、破损、涨包、结块、变质）",
	"dynamicStorageSequenceCodePlacement":"动保有序码放，规范分类（抗生素类药品单独置于仓库角落处）",
	"payAttentionToSealingTreatment":"易挥发、易受潮药品，如未整包（瓶）使用、有剩余，注意密封处理，下次使用时优先使用",
	"volatileDrugs":"易挥发药品，注意置于避光、通风处",
	"checkTheProductionDateOfAnimalProtection":"入库前检查动保生产日期、包装完整性、是否涨包等，及时处理",
	// 设备
	"ventilationDevice":"检查通风装置（运行状态、风量、风速，是否存在异响、振动）",
	"refrigerationDevice":"检查制冷装置（运行状态、制冷效果，是否存在异响、振动）",
	"humidityControlDevice":"检查湿度控制装置（运行状态、湿度）",
	"importantEquipment":"重要设备及专用零配件单独摆放，并做好标记",
	"timelyMaintenance":"及时维修损坏设备，及时补充备用零配件",
	"allDevicesAreArrangedInAnOrderlyManner":"所有设备有序码放、规范分类，不得混杂",
	// 工具
	"keepGoodUsageRecords":"做好使用记录、报损维修记录，并配有相关责任人",
	"arrangeVariousToolsInAnOrderlyManner":"各类工具有序码放，规范分类，不得混用",
	"timelyReturnToTheWarehouse":"借用工具后，及时还回入库",
	"timelyProcurement":"库存量不足时，及时采购",
	// ---------------------------- old_定期_仓库区 end
	// -- 4.1 定期_仓库区_饲料
	"remainingAmountOfFeedInTheWarehouseArea": "剩余量是否大于15天；保证新饲料是否能按时到场",
	"abnormalFeedInWarehouseArea": "是否有以下情况：过期、异味、发霉、虫蛀、鼠咬",
	// -- 4.2 定期_仓库区_动保
	"remainingInventoryOfDynamicProtectionInWarehouseArea": "剩余量是否不少于3次治疗量；新动保是否能按时到场",
	"abnormalMotionProtectionInTheWarehouseArea": "是否有以下情况：过期、破损、涨包、结块、变质",
	// -- 4.3 定期_仓库区_设备
	"equipmentStatusInWarehouseArea": "运行状态、风量、风速，是否存在异响、振动",
	"abnormalEquipmentInTheWarehouseArea": "运行状态、风量、风速，是否存在异响、振动",
	"humidityOfEquipmentInWarehouseArea": "运行状态、湿度",
	// -- 4.4 定期_仓库区_工具
	"adequateToolsInTheWarehouseArea": "库存量是否充足，采购是否及时",
	"returnOfToolsInTheWarehouseArea": "借用的工具，是否及时还回入库",
	// -- 5.1 定期_设备区 水泵、微滤机
	// 水泵 电路
	"virtualConnectionOfTheLine": "检查控制柜内各电子元件及线路（虚接）（15天）",
	"lineAging": "检查线路是否有破损、老化，接头防水处理（15天）",
	// 水泵 水路
	"abnormalNoiseInTheWaterway": "检查运行状态（异响、振动）（15天）",
	"waterwayCleaning": "排空并清洁水泵池（15天）",
	"waterwaySealing": "检查水路密封性（15天）",
	"waterwayLiquidLevelSensor": "检查液位传感器（15天）",
	// 微滤机 电路
	"microFilterCircuitVirtualConnection": "检查控制柜内各电子元件及线路（虚接）（15天）",
	"microFilterCircuitAging": "检查线路是否有破损、老化，接头防水处理（15天）",
	// 微滤机 水路
	"theWaterCircuitOfTheMicrofilterIsDirty": "检查（脏污、破损）并清洗滤网（7天）",
	"theWaterCircuitOfTheMicrofilterIsLoose": "检查（松动、堵塞）并清洗喷嘴（7天）",
	"microFilterWaterBackwashPump": "检查并清洗反冲洗泵前后端过滤器（7天）",
	"microFilterWaterCleaningCollectionTank": "检查并清洗集污槽（7天）",
	"microFilterWaterCircuitCleaningShell": "清洗外壳（7天）",
	"microFilterWaterCircuitSealing": "检查水路密封性（7天）",
	"microFilterWaterLevelProbe": "检查液位探头（7天）",
	"microFilterWaterCircuitDrum": "检查转鼓（齿轮、支撑轴、旋转同心情况、形变）（7天）",
	"bufferTanksBeforeAndAfterTheWaterCircuitOfTheMicrofilter": "清洗微滤机前后缓冲池（60天）",
	"microFilterWaterBackwash": "清洗反冲洗泵泵头（每年）",
	// 其他
	"otherAbnormalSituations": "其他异常情况",
	// -- 5.2 定期_设备区 移动床、固定床、脱气床
	// 移动床水路
	"liquidLevelSituationOfMovingBedWaterway": "检查液位情况（7天）",
	"packingStatusOfMovingBedWaterway": "检查填料状态（翻滚、堆积）（7天）",
	"mobileBedWaterwayCleaning": "清洗移动床（系统停运后）",
	
	// 移动床气路
	"mobileBedAirPathAerationPipe": "检查曝气管路（7天）",
	// 固定床水路
	"fixedBedWaterwayLiquidLevelSituation": "检查液位情况（7天）",
	"fixedBedWaterwayAerationCleaning": "曝气清洗（7-15天）",
	// 固定床气路
	"fixedBedGasPathAerationPipeline": "检查曝气管路（15天）",
	// 脱气床水路
	"degassingBedWaterCircuit": "检查脱气风机连接线路（7天）",
	"abnormalNoiseInTheDeaerationBedWaterCircuit": "检查脱气风机运行状态（异响、振动）（7天）",
	// 脱气床气路
	"degassingBedGasPassingThroughWater": "检查过水情况（7天）",
	"degassingBedGasPathFilterMaterial": "清洗柱状滤料（系统停运后）",
	// 其他
	"otherAbnormalSituations": "其他异常情况",
	// -- 5.3 定期_设备区 紫外杀菌器、氧锥
	// 紫外杀菌器水路
	"uvSterilizerWaterwayCleaning": "根据光强检查及实际情况，进行清洗（手动/自动清洗/酸洗）（15-30天）",
	// 紫外杀菌器电路
	"uvSterilizerCircuitAging": "检查线路是否有破损、老化，接头处做防水处理（15天）",
	"uvSterilizerCircuitIndicatorLight": "检查内各电子元件及线路（整流器指示灯）（15天）",
	// 氧锥水路
	"oxygenConeWaterwayWaterproofing": "检查线路是否有破损、老化，接头处做防水处理（15天）",
	// 氧锥电路
	"oxygenConeCircuitVibration": "检查氧锥泵运行状态（异响、振动）（15天）",
	"oxygenConeCircuitValve": "检查氧锥前后各阀门启闭状态（15天）",
	"oxygenConeCircuitWorkingWaterPump": "更换工作水泵，交替运行（15天）",
	"sealingPerformanceOfOxygenConeCircuit": "检查水路密封性（15天）",
	"afterTheOxygenConeCircuitIsShutDown": "打开底阀、排空（氧锥停运后）",
	// 氧锥气路
	"oxygenConeGasPathFlowRate": "检查气路密封性、流量计流量（15天）",
	// -- 5.4 定期_设备区 臭氧发生器、增氧控制柜、液氧罐
	// 臭氧发生器电路
	"ozoneGeneratorCircuitAging": "检查线路是否有破损、老化，接头处做防水处理（15天）",
	// 臭氧发生器水路
	"ozoneGeneratorWaterPump": "检查冷却水泵是否正常工作（15天）",
	// 臭氧发生器气路
	"ozoneGeneratorGasPathFlowRate": "检查进出气流量（15天）",
	"sealingOfOzoneGeneratorGasPath": "检查臭氧发生器进出气路密封性（15天）",
	// 增氧控制柜电路
	"oxygenControlCabinetCircuitLine": "检查控制柜内各电子元件及线路（虚接）（15天）",
	"thresholdOfOxygenationControlCabinetCircuit": "检查电磁阀启停状态及阈值（15天）",
	// 增氧控制柜气路
	"sealingOfTheGasPathInTheOxygenationControlCabinet": "检查供氧管路密封性（7天）",
	"oxygenSupplyInTheGasPathOfTheOxygenationControlCabinet": "检查曝气盘（开启供氧，观察出气情况）（7天）",
	// 液氧罐 
	"liquidOxygenTankInventory": "检查存量（5-7天）",
	"liquidOxygenTankPressureGauge": "检查压力表（5-7天）",
	"liquidOxygenTankPressureReducingValve": "检查减压阀（5-7天）",
	"liquidOxygenTankGasifier": "检查气化器（5-7天）",
	"sealingPerformanceOfLiquidOxygenTank": "检查气路密封性（5-7天）",
	// 其他
	"otherAbnormalSituations": "其他异常情况",
	// -- 5.5 定期_设备区 中央控制柜、风机
	// 中央控制柜"": "",
	"centralControlCabinetPowerSupply": "检查各设备电力供应情况（15天）",
	"operationStatusOfCentralControlCabinet": "检查各设备运行状态（15天）",
	"centralControlCabinetAlarmItem": "查看并处理报警项（15天）",
	"agingOfCentralControlCabinet": "检查线路是否有破损、老化，接头处做防水处理（15天）",
	"centralControlCabinetWiring": "检查控制柜内各电子元件及线路（虚接）（15天）",
	"centralControlCabinetOperationMode": "检查运行模式（本地/远程）（15天）",
	"sensorDataOfCentralControlCabinet": "检查各传感器数据显示是否正常（15天）",
	// 风机
	"fanFailure": "检查运行状态（供电、启停、故障、异响及振动）（15天）",
	"fanWaterproofing": "检查线路是否有破损、老化，接头处做防水处理（15天）",
	"fanVirtualConnection": "检查控制柜内各电子元件及线路（虚接）（15天）",
	"sealingPerformanceOfFan": "检查气路密封性（5-7天）",
	"fanPressureGauge": "检查压力表（5-7天）",
	"fanFrequency": "检查频率（15天，如有）",
	"fanAirVolume": "检查风量（15天，如有）",
	"fanFilterScreen": "清洗滤网（30天）",
	"fanGearOil": "更换齿轮油（30天，根据油脏污情况调整）（如有）",
	"fanBeltStatus": "检查传动皮带状态（30天，如有）",
	// 其他
	"otherAbnormalSituations": "其他异常情况",
	// -- 5.6 定期_设备区 水源热泵、发电机、补水系统
	// 水源热泵
	"agingOfWaterSourceHeatPump": "检查线路是否有破损、老化，接头处做防水处理（15天）",
	"waterSourceHeatPumpCompressor": "水源热泵状态（进出水、压缩机）（调整开启时间）",
	"waterSourceHeatPumpCircuit": "检查控制柜内各电子元件及线路（15天）",
	"operationStatusOfWaterSourceHeatPump": "检查机组运行状态（15天）",
	"temperatureDifferenceOfWaterSourceHeatPump": "检查进出水温度及温差是否在合理范围内（15天）",
	"waterSourceHeatPumpSolenoidValve": "检查进出水泵及电磁阀是否正常工作（15天）",
	"waterSourceHeatPumpFilter": "检查并清洗过滤器（如有）（15天）",
	// 发电机
	"generatorAging": "检查线路是否有破损、老化，接头处做防水处理（15天）",
	"generatorVirtualConnection": "检查控制柜内各电子元件及线路（虚接）（15天）",
	"generatorOilInventory": "检查汽柴油存量（发电机运行12小时以上）（30天）",
	"generatorPowerSupply": "检查电瓶是否正常供电，专用充电器充电（15天）",
	"generatorGearOil": "检查齿轮油状态（15天）",
	"waterVolumeOfGeneratorWaterTank": "检查冷却水箱水量是否充足（15天）",
	"generatorGenerator": "启动发电机运行，检查运行状态以及电瓶充电（15天）",
	// 补水系统
	"waterPumpForWaterReplenishmentSystem": "检查水泵运行状态（15天）",
	"waterReplenishmentSystemSolenoidValve": "在控制柜中，手动开启电磁阀检查是否正常运转（15天）",
	"waterReplenishmentSystemTransmitter": "检查压力变送器（15天）",
	"waterReplenishmentSystemLiquidLevelSensor": "检查液位传感器数值并清洗（15天）",
	// 其他
	"otherAbnormalSituations": "其他异常情况",

	// 日巡检_养殖区_投喂
	"twFeedCount": "投喂量(Kg)",
	"twFeedName": "饲料名称",
	// 日巡检_养殖区_死鱼
	"swFishNum": "死亡数量(尾)",
	"swFishName": "鱼苗名称",
	"swFishSpec": "鱼苗规格(g/尾)",
	
}

/**
 * 获取 dictType 对应的数据字典数组
 *
 * @param dictType 数据类型
 * @returns {*|Array} 数据字典数组
 */
export function getDictDatas(dictType) {
  return store.getters.dict_datas[dictType] || []
}

/**
 * 获取 dictType 对应的数据字典数组
 *
 * @param dictType 数据类型
 * @param values 数组、单个元素
 * @returns {*|Array} 数据字典数组
 */
export function getDictDatas2(dictType, values) {
  if (values === undefined) {
    return [];
  }
  // 如果是单个元素，则转换成数组
  if (!Array.isArray(values)) {
    values = [values];
  }
  // 获得字典数据
  const results = [];
  for (const value of values) {
    const dict = getDictData(dictType, value);
    if (dict) {
      results.push(dict);
    }
  }
  return results;
}

export function getDictData(dictType, value) {
  // 获取 dictType 对应的数据字典数组
  const dictDatas = getDictDatas(dictType)
  if (!dictDatas || dictDatas.length === 0) {
    return ''
  }
  // 获取 value 对应的展示名
  value = value + '' // 强制转换成字符串，因为 DictData 小类数值，是字符串
  for (const dictData of dictDatas) {
    if (dictData.value === value) {
      return dictData;
    }
  }
  return undefined
}

export function getDictDataLabel(dictType, value) {
  const dict = getDictData(dictType, value);
  return dict ? dict.label : '';
}
