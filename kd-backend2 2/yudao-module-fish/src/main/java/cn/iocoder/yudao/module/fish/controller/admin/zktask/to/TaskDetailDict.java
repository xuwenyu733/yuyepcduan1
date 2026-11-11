package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建人 lr
 * @创建时间 2025-09-13
 * @描述
 */
public class TaskDetailDict {

    public static Map<String, String> PROPERTY_DICT = new HashMap<>();
    public static List<String> PROPERTY_DICT_WHITE = Arrays.asList("detailType", "detailTypeName");

    static {
        // -- 1.1 日巡检_设备区_鱼
        //  摄食状态
        PROPERTY_DICT.put("foodConsumption", "摄食量（具体数据）");
        PROPERTY_DICT.put("mortality", "死亡量（具体数据）");
        PROPERTY_DICT.put("feedingIntensity", "摄食强度");
        PROPERTY_DICT.put("fecalState", "粪便状态");

        //  鱼体状态
        PROPERTY_DICT.put("outlierSearchGame", "离群索游");
        PROPERTY_DICT.put("respiratoryRate", "呼吸频率");
        PROPERTY_DICT.put("surfaceCondition", "体表状态");
        PROPERTY_DICT.put("swimmingPosture", "游泳姿态");
        PROPERTY_DICT.put("relativeFatness", "肥满度");
        // -- 1.2 日巡检_设备区_鱼
        // 传感器指标
        PROPERTY_DICT.put("temperature", "温度");
        PROPERTY_DICT.put("saturationPercentageOfDissolvedOxygen", "溶氧/饱和度");
        PROPERTY_DICT.put("turbidity", "浊度");
        PROPERTY_DICT.put("salinity", "盐度");
        // 观察及检测指标
        PROPERTY_DICT.put("liquidLevel", "液位");
        PROPERTY_DICT.put("ammoniaNitrogen", "氨氮");
        PROPERTY_DICT.put("nitrite", "亚硝酸盐");
        PROPERTY_DICT.put("foam", "泡沫");
        PROPERTY_DICT.put("oilFilm", "油膜");
        PROPERTY_DICT.put("whiteTurbidity", "白浊");
        PROPERTY_DICT.put("cloudy", "浑浊");
        PROPERTY_DICT.put("transparency", "透明度");
        PROPERTY_DICT.put("refreshingDegree", "清爽度");
        PROPERTY_DICT.put("pollutionAccumulationAtTheBottomOfThePool", "池底积污情况");
        // -- 1.3 日巡检_设备区_鱼
        //  增氧控制柜
        PROPERTY_DICT.put("trachea", "气管");
        PROPERTY_DICT.put("solenoidValve", "电磁阀");
        PROPERTY_DICT.put("aerationDisc", "曝气盘");
        PROPERTY_DICT.put("dissolvedOxygenSensor", "溶氧传感器");
        PROPERTY_DICT.put("interceptNetworkCleaning", "拦截网清洗");
        PROPERTY_DICT.put("sealingOfEmptyingPipe", "排空管密封性");
        PROPERTY_DICT.put("pollutionAccumulationSituation", "粪便及积污情况，及时拔管");
        //  侧排水箱
        PROPERTY_DICT.put("interceptNetworkCleaning", "拦截网清洗");
        //  竖流沉淀器
        PROPERTY_DICT.put("sealingOfEmptyingPipe", "排空管密封性");
        PROPERTY_DICT.put("pollutionAccumulationSituation", "粪便及积污情况，及时拔管");
        // -- 2.1 日巡检_设备区_水泵微滤机
        // 水泵
        PROPERTY_DICT.put("runningState", "运行状态");
        PROPERTY_DICT.put("frequencyConverterFrequency", "变频器频率");
        PROPERTY_DICT.put("controlCabinetStatus", "控制柜状态");
        PROPERTY_DICT.put("checkTheDataOfTheLiquidLevelSensor", "检查液位传感器数据");
        // 微滤机
        PROPERTY_DICT.put("controlCabinetStatus", "控制柜状态");
        PROPERTY_DICT.put("checkTheResponseStatusOfTheLiquidLevelProbe", "检查液位探头应答状态");
        PROPERTY_DICT.put("tightness", "密封性");
        PROPERTY_DICT.put("isTheFilterClogged", "滤网是否堵塞、破损");
        PROPERTY_DICT.put("backwashNozzleBlockage", "反冲洗喷嘴堵塞、松动");
        PROPERTY_DICT.put("backwashFrequency", "反冲洗频率");
        PROPERTY_DICT.put("backwashPressure", "反冲洗压力");
        PROPERTY_DICT.put("sewageSump", "集污槽");
        PROPERTY_DICT.put("runningState", "运行状态");
        // -- 2.2 日巡检_设备区_移动床固定床脱气床臭氧发生器
        // 移动床
        PROPERTY_DICT.put("liquidLevelSituation", "液位情况");
        PROPERTY_DICT.put("packingState", "填料状态（翻滚、堆积）");
        PROPERTY_DICT.put("aerationSituation", "曝气情况");
        // 固定床
        PROPERTY_DICT.put("liquidLevelSituation", "液位情况");
        // 脱气床
        PROPERTY_DICT.put("waterFlowSituation", "过水情况");
        PROPERTY_DICT.put("fanOperatingStatus", "风机运行状态");
        // 紫外杀菌器
        PROPERTY_DICT.put("controlCabinetStatus", "控制柜状态");
        PROPERTY_DICT.put("lightIntensity", "光照强度");
        // 臭氧发生器
        PROPERTY_DICT.put("airTightness", "气密性");
        PROPERTY_DICT.put("inletAndOutletAirflowSetting", "进出气流量设定是否合理");
        PROPERTY_DICT.put("coolingWater", "冷却水（纯氧型）");
        // -- 2.3 日巡检_设备区_氧锥风机
        // 氧锥
        PROPERTY_DICT.put("statusOfEachValve", "各阀门状态");
        PROPERTY_DICT.put("oxygenConePumpStatus", "氧锥泵状态");
        PROPERTY_DICT.put("oxygenConeOxygenationControlCabinet", "氧锥增氧控制柜");
        PROPERTY_DICT.put("internalPressure", "内部压力");
        PROPERTY_DICT.put("liquidOxygenFlowRate", "液氧流量");
        PROPERTY_DICT.put("internalLiquidLevel", "内部液位");
        PROPERTY_DICT.put("airTightness", "气密性");
        // 风机
        PROPERTY_DICT.put("runningState", "运行状态（启停、声音、振动）");
        PROPERTY_DICT.put("fanControlCabinet", "风机控制柜");
        PROPERTY_DICT.put("airOutletPressure", "出风口压力");
        PROPERTY_DICT.put("lubricatingOilStatus", "润滑油状态");
        PROPERTY_DICT.put("airVolume", "风量（如有）");
        // -- 2.4 日巡检_设备区_中央控制柜液氧罐水源热泵
        // 中央控制柜
        PROPERTY_DICT.put("electricitySupplySituation", "电力供应情况");
        PROPERTY_DICT.put("operatingStatusOfEachDevice", "各设备运行状态");
        PROPERTY_DICT.put("handlingAlarmItems", "处理报警项");
        PROPERTY_DICT.put("electricalComponentStatus", "电器元件状态");
        PROPERTY_DICT.put("operatingMode", "运行模式（本地/远程）");
        PROPERTY_DICT.put("dataFromVariousSensors", "各传感器数据显示是否正常");
        // 液氧罐
        PROPERTY_DICT.put("stock", "存量");
        PROPERTY_DICT.put("pressure", "压力");
        PROPERTY_DICT.put("pressureReducingValve", "减压阀");
        PROPERTY_DICT.put("gasifier", "气化器");
        PROPERTY_DICT.put("airTightnessOfGasPath", "气路气密性");
        // 水源热泵
        PROPERTY_DICT.put("waterPump", "水泵");
        PROPERTY_DICT.put("solenoidValve", "电磁阀");
        PROPERTY_DICT.put("compressor", "压缩机");
        PROPERTY_DICT.put("inletAndOutletWaterTemperature", "进出水温度");
        // -- 2.5 日巡检_设备区_碱度添加补水源水系统投饵机
        // 碱度添加系统
        PROPERTY_DICT.put("phSensor", "pH传感器");
        PROPERTY_DICT.put("solenoidValve", "电磁阀");
        PROPERTY_DICT.put("liquidLevelSensor", "液位传感器");
        PROPERTY_DICT.put("waterPump", "水泵");
        PROPERTY_DICT.put("mixingMotor", "搅拌电机（或曝气搅拌）");
        // 补水系统
        PROPERTY_DICT.put("waterPump", "水泵");
        PROPERTY_DICT.put("solenoidValve", "电磁阀");
        PROPERTY_DICT.put("constantPressureWaterSupplyController", "恒压供水控制器");
        // 源水系统
        PROPERTY_DICT.put("waterPump", "水泵");
        PROPERTY_DICT.put("liquidLevelSensor", "液位传感器");
        PROPERTY_DICT.put("ozoneDisinfectionAndSterilizationSystem", "紫外/臭氧消杀系统");
        // 自动投饵机
        PROPERTY_DICT.put("automaticFeedingMachine", "自动投饵机");
        // -- 3.1 定期_养殖区_鱼
        // 打样（3-15天）
        PROPERTY_DICT.put("specifications", "规格");
        PROPERTY_DICT.put("fishInventory", "存鱼量");
        PROPERTY_DICT.put("growthRate", "生长率");
        PROPERTY_DICT.put("feedCoefficient", "饵料系数");
        // 观察与镜检（3-7天） 体表
        PROPERTY_DICT.put("woundCondition", "伤口状态（大小、溃烂检测）");
        PROPERTY_DICT.put("mucus", "粘液（多少、寄生虫）");
        PROPERTY_DICT.put("finRay", "鳍条（完整度、血液到达位置、寄生虫）");
        PROPERTY_DICT.put("scale", "鳞片（掉鳞、完整度）");
        PROPERTY_DICT.put("dirty", "脏污（挂脏、斑点、异物）");
        PROPERTY_DICT.put("anus", "肛门（红肿）");
        PROPERTY_DICT.put("gill", "鳃（完整度、颜色、血液充盈度、异物、寄生虫）");
        // 观察与镜检（3-7天） 体内
        PROPERTY_DICT.put("spleen", "脾脏（颜色、大小、斑点、孢子）");
        PROPERTY_DICT.put("intestine", "肠道（颜色、充血、肠绒毛、肠壁厚度、弹性）");
        PROPERTY_DICT.put("abdominalCavity", "腹腔（充血）");
        PROPERTY_DICT.put("kidney", "肾脏（颜色，充盈度、大小）");
        PROPERTY_DICT.put("muscle", "肌肉（颜色、紧实度）");
        // -- 3.2 定期_养殖区_水
        // 传感器指标（7天）
        PROPERTY_DICT.put("ph", "PH");
        PROPERTY_DICT.put("temperature", "温度");
        PROPERTY_DICT.put("saturationPercentageOfDissolvedOxygen", "溶氧/饱和度");
        PROPERTY_DICT.put("orp", "ORP");
        PROPERTY_DICT.put("turbidity", "浊度");
        PROPERTY_DICT.put("salinity", "盐度");
        // 检测指标（5-7天）
        PROPERTY_DICT.put("alkalinity", "总碱度");
        PROPERTY_DICT.put("ammoniaNitrogen", "氨氮");
        PROPERTY_DICT.put("nitrite", "亚硝酸盐");
        PROPERTY_DICT.put("totalHardness", "总硬度");
        // 观察指标（7天）
        PROPERTY_DICT.put("foam", "泡沫");
        PROPERTY_DICT.put("oilFilm", "油膜");
        PROPERTY_DICT.put("whiteTurbidity", "白浊");
        PROPERTY_DICT.put("cloudy", "浑浊");
        PROPERTY_DICT.put("transparency", "透明度");
        PROPERTY_DICT.put("refreshingDegree", "清爽度");
        PROPERTY_DICT.put("pollutionAccumulationAtTheBottomOfThePool", "池底积污情况");
        // ---------------------------- old_定期_仓库区 start
        // 饲料（先进先出、后进后出）
        PROPERTY_DICT.put("checkTheStockOfFeed", "检查饲料存量（保证剩余量不少于15天；跟踪采购进度，保证新饲料按时到场）");
        PROPERTY_DICT.put("checkTheStatusOfTheFeed", "检查饲料状态（保质期、异味、发霉、虫蛀、鼠咬），预警临期饲料，尽快投喂；变质饲料，及时销毁");
        PROPERTY_DICT.put("isThereAnyQualityIssue", "入库前检查饲料生产日期、包装完整性，抽检有无质量问题");
        PROPERTY_DICT.put("controlTheAmountOfFeedProcurement", "合理控制饲料采购量，保持库存量不超过一到三个月");
        // 动保（先进先出、后进后出）
        PROPERTY_DICT.put("dynamicStorageCapacity", "检查动保存量（保证剩余量不少于3次治疗量；跟踪采购进度，保证新动保按时到场）");
        PROPERTY_DICT.put("dynamicProtectionStatus", "检查动保状态（保质期、破损、涨包、结块、变质）");
        PROPERTY_DICT.put("dynamicStorageSequenceCodePlacement", "动保有序码放，规范分类（抗生素类药品单独置于仓库角落处）");
        PROPERTY_DICT.put("payAttentionToSealingTreatment", "易挥发、易受潮药品，如未整包（瓶）使用、有剩余，注意密封处理，下次使用时优先使用");
        PROPERTY_DICT.put("volatileDrugs", "易挥发药品，注意置于避光、通风处");
        PROPERTY_DICT.put("checkTheProductionDateOfAnimalProtection", "入库前检查动保生产日期、包装完整性、是否涨包等，及时处理");
        // 设备
        PROPERTY_DICT.put("ventilationDevice", "检查通风装置（运行状态、风量、风速，是否存在异响、振动）");
        PROPERTY_DICT.put("refrigerationDevice", "检查制冷装置（运行状态、制冷效果，是否存在异响、振动）");
        PROPERTY_DICT.put("humidityControlDevice", "检查湿度控制装置（运行状态、湿度）");
        PROPERTY_DICT.put("importantEquipment", "重要设备及专用零配件单独摆放，并做好标记");
        PROPERTY_DICT.put("timelyMaintenance", "及时维修损坏设备，及时补充备用零配件");
        PROPERTY_DICT.put("allDevicesAreArrangedInAnOrderlyManner", "所有设备有序码放、规范分类，不得混杂");
        // 工具
        PROPERTY_DICT.put("keepGoodUsageRecords", "做好使用记录、报损维修记录，并配有相关责任人");
        PROPERTY_DICT.put("arrangeVariousToolsInAnOrderlyManner", "各类工具有序码放，规范分类，不得混用");
        PROPERTY_DICT.put("timelyReturnToTheWarehouse", "借用工具后，及时还回入库");
        PROPERTY_DICT.put("timelyProcurement", "库存量不足时，及时采购");
        // ---------------------------- old_定期_仓库区 end
        // -- 4.1 定期_仓库区_饲料
        PROPERTY_DICT.put("remainingAmountOfFeedInTheWarehouseArea", "剩余量是否大于15天；保证新饲料是否能按时到场");
        PROPERTY_DICT.put("abnormalFeedInWarehouseArea", "是否有以下情况：过期、异味、发霉、虫蛀、鼠咬");
        // -- 4.2 定期_仓库区_动保
        PROPERTY_DICT.put("remainingInventoryOfDynamicProtectionInWarehouseArea", "剩余量是否不少于3次治疗量；新动保是否能按时到场");
        PROPERTY_DICT.put("abnormalMotionProtectionInTheWarehouseArea", "是否有以下情况：过期、破损、涨包、结块、变质");
        // -- 4.3 定期_仓库区_设备
        PROPERTY_DICT.put("equipmentStatusInWarehouseArea", "运行状态、风量、风速，是否存在异响、振动");
        PROPERTY_DICT.put("abnormalEquipmentInTheWarehouseArea", "运行状态、风量、风速，是否存在异响、振动");
        PROPERTY_DICT.put("humidityOfEquipmentInWarehouseArea", "运行状态、湿度");
        // -- 4.4 定期_仓库区_工具
        PROPERTY_DICT.put("adequateToolsInTheWarehouseArea", "库存量是否充足，采购是否及时");
        PROPERTY_DICT.put("returnOfToolsInTheWarehouseArea", "借用的工具，是否及时还回入库");
        // -- 5.1 定期_设备区 水泵、微滤机
        // 水泵 电路
        PROPERTY_DICT.put("virtualConnectionOfTheLine", "检查控制柜内各电子元件及线路（虚接）（15天）");
        PROPERTY_DICT.put("lineAging", "检查线路是否有破损、老化，接头防水处理（15天）");
        // 水泵 水路
        PROPERTY_DICT.put("abnormalNoiseInTheWaterway", "检查运行状态（异响、振动）（15天）");
        PROPERTY_DICT.put("waterwayCleaning", "排空并清洁水泵池（15天）");
        PROPERTY_DICT.put("waterwaySealing", "检查水路密封性（15天）");
        PROPERTY_DICT.put("waterwayLiquidLevelSensor", "检查液位传感器（15天）");
        // 微滤机 电路
        PROPERTY_DICT.put("microFilterCircuitVirtualConnection", "检查控制柜内各电子元件及线路（虚接）（15天）");
        PROPERTY_DICT.put("microFilterCircuitAging", "检查线路是否有破损、老化，接头防水处理（15天）");
        // 微滤机 水路
        PROPERTY_DICT.put("theWaterCircuitOfTheMicrofilterIsDirty", "检查（脏污、破损）并清洗滤网（7天）");
        PROPERTY_DICT.put("theWaterCircuitOfTheMicrofilterIsLoose", "检查（松动、堵塞）并清洗喷嘴（7天）");
        PROPERTY_DICT.put("microFilterWaterBackwashPump", "检查并清洗反冲洗泵前后端过滤器（7天）");
        PROPERTY_DICT.put("microFilterWaterCleaningCollectionTank", "检查并清洗集污槽（7天）");
        PROPERTY_DICT.put("microFilterWaterCircuitCleaningShell", "清洗外壳（7天）");
        PROPERTY_DICT.put("microFilterWaterCircuitSealing", "检查水路密封性（7天）");
        PROPERTY_DICT.put("microFilterWaterLevelProbe", "检查液位探头（7天）");
        PROPERTY_DICT.put("microFilterWaterCircuitDrum", "检查转鼓（齿轮、支撑轴、旋转同心情况、形变）（7天）");
        PROPERTY_DICT.put("bufferTanksBeforeAndAfterTheWaterCircuitOfTheMicrofilter", "清洗微滤机前后缓冲池（60天）");
        PROPERTY_DICT.put("microFilterWaterBackwash", "清洗反冲洗泵泵头（每年）");
        // 其他
        PROPERTY_DICT.put("otherAbnormalSituations", "其他异常情况");
        // -- 5.2 定期_设备区 移动床、固定床、脱气床
        // 移动床水路
        PROPERTY_DICT.put("liquidLevelSituationOfMovingBedWaterway", "检查液位情况（7天）");
        PROPERTY_DICT.put("packingStatusOfMovingBedWaterway", "检查填料状态（翻滚、堆积）（7天）");
        PROPERTY_DICT.put("mobileBedWaterwayCleaning", "清洗移动床（系统停运后）");

        // 移动床气路
        PROPERTY_DICT.put("mobileBedAirPathAerationPipe", "检查曝气管路（7天）");
        // 固定床水路
        PROPERTY_DICT.put("fixedBedWaterwayLiquidLevelSituation", "检查液位情况（7天）");
        PROPERTY_DICT.put("fixedBedWaterwayAerationCleaning", "曝气清洗（7-15天）");
        // 固定床气路
        PROPERTY_DICT.put("fixedBedGasPathAerationPipeline", "检查曝气管路（15天）");
        // 脱气床水路
        PROPERTY_DICT.put("degassingBedWaterCircuit", "检查脱气风机连接线路（7天）");
        PROPERTY_DICT.put("abnormalNoiseInTheDeaerationBedWaterCircuit", "检查脱气风机运行状态（异响、振动）（7天）");
        // 脱气床气路
        PROPERTY_DICT.put("degassingBedGasPassingThroughWater", "检查过水情况（7天）");
        PROPERTY_DICT.put("degassingBedGasPathFilterMaterial", "清洗柱状滤料（系统停运后）");
        // 其他
        PROPERTY_DICT.put("otherAbnormalSituations", "其他异常情况");
        // -- 5.3 定期_设备区 紫外杀菌器、氧锥
        // 紫外杀菌器水路
        PROPERTY_DICT.put("uvSterilizerWaterwayCleaning", "根据光强检查及实际情况，进行清洗（手动/自动清洗/酸洗）（15-30天）");
        // 紫外杀菌器电路
        PROPERTY_DICT.put("uvSterilizerCircuitAging", "检查线路是否有破损、老化，接头处做防水处理（15天）");
        PROPERTY_DICT.put("uvSterilizerCircuitIndicatorLight", "检查内各电子元件及线路（整流器指示灯）（15天）");
        // 氧锥水路
        PROPERTY_DICT.put("oxygenConeWaterwayWaterproofing", "检查线路是否有破损、老化，接头处做防水处理（15天）");
        // 氧锥电路
        PROPERTY_DICT.put("oxygenConeCircuitVibration", "检查氧锥泵运行状态（异响、振动）（15天）");
        PROPERTY_DICT.put("oxygenConeCircuitValve", "检查氧锥前后各阀门启闭状态（15天）");
        PROPERTY_DICT.put("oxygenConeCircuitWorkingWaterPump", "更换工作水泵，交替运行（15天）");
        PROPERTY_DICT.put("sealingPerformanceOfOxygenConeCircuit", "检查水路密封性（15天）");
        PROPERTY_DICT.put("afterTheOxygenConeCircuitIsShutDown", "打开底阀、排空（氧锥停运后）");
        // 氧锥气路
        PROPERTY_DICT.put("oxygenConeGasPathFlowRate", "检查气路密封性、流量计流量（15天）");
        // -- 5.4 定期_设备区 臭氧发生器、增氧控制柜、液氧罐
        // 臭氧发生器电路
        PROPERTY_DICT.put("ozoneGeneratorCircuitAging", "检查线路是否有破损、老化，接头处做防水处理（15天）");
        // 臭氧发生器水路
        PROPERTY_DICT.put("ozoneGeneratorWaterPump", "检查冷却水泵是否正常工作（15天）");
        // 臭氧发生器气路
        PROPERTY_DICT.put("ozoneGeneratorGasPathFlowRate", "检查进出气流量（15天）");
        PROPERTY_DICT.put("sealingOfOzoneGeneratorGasPath", "检查臭氧发生器进出气路密封性（15天）");
        // 增氧控制柜电路
        PROPERTY_DICT.put("oxygenControlCabinetCircuitLine", "检查控制柜内各电子元件及线路（虚接）（15天）");
        PROPERTY_DICT.put("thresholdOfOxygenationControlCabinetCircuit", "检查电磁阀启停状态及阈值（15天）");
        // 增氧控制柜气路
        PROPERTY_DICT.put("sealingOfTheGasPathInTheOxygenationControlCabinet", "检查供氧管路密封性（7天）");
        PROPERTY_DICT.put("oxygenSupplyInTheGasPathOfTheOxygenationControlCabinet", "检查曝气盘（开启供氧，观察出气情况）（7天）");
        // 液氧罐 
        PROPERTY_DICT.put("liquidOxygenTankInventory", "检查存量（5-7天）");
        PROPERTY_DICT.put("liquidOxygenTankPressureGauge", "检查压力表（5-7天）");
        PROPERTY_DICT.put("liquidOxygenTankPressureReducingValve", "检查减压阀（5-7天）");
        PROPERTY_DICT.put("liquidOxygenTankGasifier", "检查气化器（5-7天）");
        PROPERTY_DICT.put("sealingPerformanceOfLiquidOxygenTank", "检查气路密封性（5-7天）");
        // 其他
        PROPERTY_DICT.put("otherAbnormalSituations", "其他异常情况");
        // -- 5.5 定期_设备区 中央控制柜、风机
        // 中央控制柜"", "");
        PROPERTY_DICT.put("centralControlCabinetPowerSupply", "检查各设备电力供应情况（15天）");
        PROPERTY_DICT.put("operationStatusOfCentralControlCabinet", "检查各设备运行状态（15天）");
        PROPERTY_DICT.put("centralControlCabinetAlarmItem", "查看并处理报警项（15天）");
        PROPERTY_DICT.put("agingOfCentralControlCabinet", "检查线路是否有破损、老化，接头处做防水处理（15天）");
        PROPERTY_DICT.put("centralControlCabinetWiring", "检查控制柜内各电子元件及线路（虚接）（15天）");
        PROPERTY_DICT.put("centralControlCabinetOperationMode", "检查运行模式（本地/远程）（15天）");
        PROPERTY_DICT.put("sensorDataOfCentralControlCabinet", "检查各传感器数据显示是否正常（15天）");
        // 风机
        PROPERTY_DICT.put("fanFailure", "检查运行状态（供电、启停、故障、异响及振动）（15天）");
        PROPERTY_DICT.put("fanWaterproofing", "检查线路是否有破损、老化，接头处做防水处理（15天）");
        PROPERTY_DICT.put("fanVirtualConnection", "检查控制柜内各电子元件及线路（虚接）（15天）");
        PROPERTY_DICT.put("sealingPerformanceOfFan", "检查气路密封性（5-7天）");
        PROPERTY_DICT.put("fanPressureGauge", "检查压力表（5-7天）");
        PROPERTY_DICT.put("fanFrequency", "检查频率（15天，如有）");
        PROPERTY_DICT.put("fanAirVolume", "检查风量（15天，如有）");
        PROPERTY_DICT.put("fanFilterScreen", "清洗滤网（30天）");
        PROPERTY_DICT.put("fanGearOil", "更换齿轮油（30天，根据油脏污情况调整）（如有）");
        PROPERTY_DICT.put("fanBeltStatus", "检查传动皮带状态（30天，如有）");
        // 其他
        PROPERTY_DICT.put("otherAbnormalSituations", "其他异常情况");
        // -- 5.6 定期_设备区 水源热泵、发电机、补水系统
        // 水源热泵
        PROPERTY_DICT.put("agingOfWaterSourceHeatPump", "检查线路是否有破损、老化，接头处做防水处理（15天）");
        PROPERTY_DICT.put("waterSourceHeatPumpCompressor", "水源热泵状态（进出水、压缩机）（调整开启时间）");
        PROPERTY_DICT.put("waterSourceHeatPumpCircuit", "检查控制柜内各电子元件及线路（15天）");
        PROPERTY_DICT.put("operationStatusOfWaterSourceHeatPump", "检查机组运行状态（15天）");
        PROPERTY_DICT.put("temperatureDifferenceOfWaterSourceHeatPump", "检查进出水温度及温差是否在合理范围内（15天）");
        PROPERTY_DICT.put("waterSourceHeatPumpSolenoidValve", "检查进出水泵及电磁阀是否正常工作（15天）");
        PROPERTY_DICT.put("waterSourceHeatPumpFilter", "检查并清洗过滤器（如有）（15天）");
        // 发电机
        PROPERTY_DICT.put("generatorAging", "检查线路是否有破损、老化，接头处做防水处理（15天）");
        PROPERTY_DICT.put("generatorVirtualConnection", "检查控制柜内各电子元件及线路（虚接）（15天）");
        PROPERTY_DICT.put("generatorOilInventory", "检查汽柴油存量（发电机运行12小时以上）（30天）");
        PROPERTY_DICT.put("generatorPowerSupply", "检查电瓶是否正常供电，专用充电器充电（15天）");
        PROPERTY_DICT.put("generatorGearOil", "检查齿轮油状态（15天）");
        PROPERTY_DICT.put("waterVolumeOfGeneratorWaterTank", "检查冷却水箱水量是否充足（15天）");
        PROPERTY_DICT.put("generatorGenerator", "启动发电机运行，检查运行状态以及电瓶充电（15天）");
        // 补水系统
        PROPERTY_DICT.put("waterPumpForWaterReplenishmentSystem", "检查水泵运行状态（15天）");
        PROPERTY_DICT.put("waterReplenishmentSystemSolenoidValve", "在控制柜中，手动开启电磁阀检查是否正常运转（15天）");
        PROPERTY_DICT.put("waterReplenishmentSystemTransmitter", "检查压力变送器（15天）");
        PROPERTY_DICT.put("waterReplenishmentSystemLiquidLevelSensor", "检查液位传感器数值并清洗（15天）");
        // 其他
        PROPERTY_DICT.put("otherAbnormalSituations", "其他异常情况");
    }

}
