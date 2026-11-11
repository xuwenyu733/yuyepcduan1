package cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.to.InitData;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfarmingbatch.ZkGrowFarmingBatchDO;
import cn.iocoder.yudao.module.fish.service.zkgrowfarmingbatch.ZkGrowFarmingBatchService;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowmodelconfig.ZkGrowModelConfigDO;
import cn.iocoder.yudao.module.fish.service.zkgrowmodelconfig.ZkGrowModelConfigService;
import io.swagger.v3.oas.annotations.Parameters;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowDailyRecordDO;
import cn.iocoder.yudao.module.fish.service.zkgrowdailyrecord.ZkGrowDailyRecordService;

@Tag(name = "管理后台 - 每日生长与环境记录")
@RestController
@RequestMapping("/fish/zk-grow-daily-record")
@Validated
public class ZkGrowDailyRecordController {

    @Resource
    private ZkGrowDailyRecordService zkGrowDailyRecordService;

    @Resource
    private ZkGrowFarmingBatchService zkGrowFarmingBatchService;

    @Resource
    private ZkGrowModelConfigService zkGrowModelConfigService;

    @Resource
    private InitData initData;

    @Resource
    private cn.iocoder.yudao.module.fish.service.zkdevicerecord.ZkDeviceRecordService zkDeviceRecordService;

    @Resource
    private cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService zkPondService;

    @Resource
    private cn.iocoder.yudao.module.fish.dal.mysql.zkgrowbaitnutrition.ZkGrowBaitNutritionMapper zkGrowBaitNutritionMapper;

    @Resource
    private cn.iocoder.yudao.module.fish.service.zkdevicealarm.ZkDeviceAlarmService zkDeviceAlarmService;

    @Resource
    private cn.iocoder.yudao.module.fish.dal.mysql.zkpondfish.ZkPondFishMapper zkPondFishMapper;

    @GetMapping("/predict")
    @Operation(summary = "预测鱼类生长数据")
    @Parameters({
            @Parameter(name = "batchId", description = "养殖批次ID或编号", required = false),
            @Parameter(name = "speciesId", description = "鱼种ID", required = false),
            @Parameter(name = "waterTemp", description = "当日平均水温", required = true),
            @Parameter(name = "feedRate", description = "当日投饵率 (%)", required = true),
            @Parameter(name = "maxDays", description = "预测天数", required = false)
    })
    public CommonResult<List<ZkGrowDailyRecordRespVO>> predict(@RequestParam(value = "batchId", required = false) String batchId,
                                                               @RequestParam(value = "speciesId", required = false) String speciesId,
                                                               @RequestParam("waterTemp") BigDecimal waterTemp,
                                                               @RequestParam("feedRate") BigDecimal feedRate,
                                                               @RequestParam(value = "maxDays", defaultValue = "180") Integer maxDays) {
        Long sId = null;
        if (speciesId != null) {
            try {
                sId = Long.parseLong(speciesId);
            } catch (Exception ignored) {}
        }
        
        if (sId == null && batchId != null) {
            try {
                // 如果 batchId 是数字，尝试获取批次信息
                Long bId = Long.parseLong(batchId);
                ZkGrowFarmingBatchDO batch = zkGrowFarmingBatchService.getZkGrowFarmingBatch(bId);
                if (batch != null) {
                    sId = batch.getSpeciesId();
                }
            } catch (Exception ignored) {
                // batchId 是字符串 (如 CGRK...)，目前逻辑中主要靠 speciesId 驱动模型
            }
        }

        ZkGrowModelConfigDO config = null;
        if (sId != null) {
            config = zkGrowModelConfigService.getZkGrowModelConfigBySpeciesId(sId);
        }

        if (config == null) {
            // 如果没有配置，使用默认参数
            config = new ZkGrowModelConfigDO();
            config.setLInf(new BigDecimal("45"));
            config.setWInf(new BigDecimal("1200"));
            config.setKLength(new BigDecimal("0.012"));
            config.setKWeight(new BigDecimal("0.015"));
            config.setInitialLength(new BigDecimal("4"));
            config.setInitialWeight(new BigDecimal("5"));
        }

        List<ZkGrowDailyRecordRespVO> result = new ArrayList<>();
        double tempFactor = tempCorrection(waterTemp.doubleValue());
        double feedFactor = feedRateCorrection(feedRate.doubleValue());
        double growthFactor = tempFactor * feedFactor;

        double kLengthAdj = config.getKLength().doubleValue() * growthFactor;
        double kWeightAdj = config.getKWeight().doubleValue() * growthFactor;
        double lInf = config.getLInf().doubleValue();
        double wInf = config.getWInf().doubleValue();
        double initialLength = config.getInitialLength().doubleValue();
        double initialWeight = config.getInitialWeight().doubleValue();

        Random random = new Random();

        for (int day = 0; day <= maxDays; day++) {
            double length = lInf * (1 - Math.exp(-kLengthAdj * day)) + initialLength * Math.exp(-kLengthAdj * day);
            double weight = wInf * (1 - Math.exp(-kWeightAdj * day)) + initialWeight * Math.exp(-kWeightAdj * day);

            ZkGrowDailyRecordRespVO vo = new ZkGrowDailyRecordRespVO();
            vo.setDayIndex(day);
            vo.setPredictedLength(BigDecimal.valueOf(length).setScale(2, RoundingMode.HALF_UP));
            vo.setPredictedWeight(BigDecimal.valueOf(weight).setScale(2, RoundingMode.HALF_UP));
            
            // 模拟视觉识别数据 (每5天)
            if (day > 0 && day % 5 == 0) {
                double vError = (random.nextDouble() - 0.5) * 2 * 5 / 100.0;
                double lError = (random.nextDouble() - 0.5) * 2 * 3 / 100.0;
                vo.setVisionWeight(BigDecimal.valueOf(weight * (1 + vError)).setScale(2, RoundingMode.HALF_UP));
                vo.setVisionLength(BigDecimal.valueOf(length * (1 + lError)).setScale(2, RoundingMode.HALF_UP));
            }

            // 模拟手动测量数据 (每15天)
            if (day > 0 && day % 15 == 0) {
                double mError = (random.nextDouble() - 0.5) * 2 * 2 / 100.0;
                double mlError = (random.nextDouble() - 0.5) * 2 * 1.5 / 100.0;
                vo.setManualWeight(BigDecimal.valueOf(weight * (1 + mError)).setScale(2, RoundingMode.HALF_UP));
                vo.setManualLength(BigDecimal.valueOf(length * (1 + mlError)).setScale(2, RoundingMode.HALF_UP));
            }
            
            // 计算 SGR (与前一天相比，如果是第0天则为0)
            if (day > 0) {
                double prevWeight = wInf * (1 - Math.exp(-kWeightAdj * (day - 1))) + initialWeight * Math.exp(-kWeightAdj * (day - 1));
                double sgr = (Math.log(weight) - Math.log(prevWeight)) * 100;
                vo.setSgr(BigDecimal.valueOf(sgr).setScale(4, RoundingMode.HALF_UP));
            } else {
                vo.setSgr(BigDecimal.ZERO);
            }

            result.add(vo);
        }

        return success(result);
    }

    private double tempCorrection(double temp) {
        double optimalTemp = 25.0;
        double tempDiff = Math.abs(temp - optimalTemp);
        if (temp < 18 || temp > 32) return 0.5;
        if (temp >= 23 && temp <= 28) return 1.0;
        return Math.max(0.6, 1 - tempDiff * 0.05);
    }

    private double feedRateCorrection(double feedRate) {
        double optimalFeedRate = 3.0;
        if (feedRate < 1) return 0.5;
        if (feedRate > 5) return 0.85;
        double diff = Math.abs(feedRate - optimalFeedRate);
        return Math.max(0.7, 1 - diff * 0.08);
    }

    @GetMapping("/get-summary")
    @Operation(summary = "获得生长模型概览数据 (对应前端红框模块)")
    @Parameters({
            @Parameter(name = "batchId", description = "养殖批次ID或编号", required = false),
            @Parameter(name = "speciesId", description = "鱼种ID", required = false),
            @Parameter(name = "waterTemp", description = "当日平均水温", required = true),
            @Parameter(name = "feedRate", description = "当日投饵率 (%)", required = true),
            @Parameter(name = "dayIndex", description = "养殖天数", required = true)
    })
    public CommonResult<ZkGrowDailyRecordRespVO> getSummary(@RequestParam(value = "batchId", required = false) String batchId,
                                                            @RequestParam(value = "speciesId", required = false) String speciesId,
                                                            @RequestParam("waterTemp") BigDecimal waterTemp,
                                                            @RequestParam("feedRate") BigDecimal feedRate,
                                                            @RequestParam("dayIndex") Integer dayIndex) {
        Long sId = null;
        if (speciesId != null) {
            try {
                sId = Long.parseLong(speciesId);
            } catch (Exception ignored) {}
        }
        
        if (sId == null && batchId != null) {
            try {
                Long bId = Long.parseLong(batchId);
                ZkGrowFarmingBatchDO batch = zkGrowFarmingBatchService.getZkGrowFarmingBatch(bId);
                if (batch != null) {
                    sId = batch.getSpeciesId();
                }
            } catch (Exception ignored) {}
        }

        ZkGrowModelConfigDO config = null;
        if (sId != null) {
            config = zkGrowModelConfigService.getZkGrowModelConfigBySpeciesId(sId);
        }

        if (config == null) {
            config = new ZkGrowModelConfigDO();
            config.setLInf(new BigDecimal("45"));
            config.setWInf(new BigDecimal("1200"));
            config.setKLength(new BigDecimal("0.012"));
            config.setKWeight(new BigDecimal("0.015"));
            config.setInitialLength(new BigDecimal("4"));
            config.setInitialWeight(new BigDecimal("5"));
        }

        double tempFactor = tempCorrection(waterTemp.doubleValue());
        double feedFactor = feedRateCorrection(feedRate.doubleValue());
        double growthFactor = tempFactor * feedFactor;

        double kLengthAdj = config.getKLength().doubleValue() * growthFactor;
        double kWeightAdj = config.getKWeight().doubleValue() * growthFactor;
        double lInf = config.getLInf().doubleValue();
        double wInf = config.getWInf().doubleValue();
        double initialLength = config.getInitialLength().doubleValue();
        double initialWeight = config.getInitialWeight().doubleValue();

        double length = lInf * (1 - Math.exp(-kLengthAdj * dayIndex)) + initialLength * Math.exp(-kLengthAdj * dayIndex);
        double weight = wInf * (1 - Math.exp(-kWeightAdj * dayIndex)) + initialWeight * Math.exp(-kWeightAdj * dayIndex);

        ZkGrowDailyRecordRespVO vo = new ZkGrowDailyRecordRespVO();
        vo.setDayIndex(dayIndex);
        vo.setWaterTemp(waterTemp);
        vo.setFeedRate(feedRate);
        vo.setPredictedLength(BigDecimal.valueOf(length).setScale(2, RoundingMode.HALF_UP));
        vo.setPredictedWeight(BigDecimal.valueOf(weight).setScale(2, RoundingMode.HALF_UP));

        return success(vo);
    }

    @GetMapping("/batch-summary-list")
    @Operation(summary = "获得养殖批次摘要列表")
    public CommonResult<List<ZkGrowBatchSummaryRespVO>> getBatchSummaryList() {
        return success(zkGrowDailyRecordService.getBatchSummaryList());
    }

    @GetMapping("/pond-summary-list")
    @Operation(summary = "获得养殖鱼池摘要列表")
    public CommonResult<List<ZkGrowPondSummaryRespVO>> getPondSummaryList() {
        return success(zkGrowDailyRecordService.getPondSummaryList());
    }

    @PostMapping("/create")
    @Operation(summary = "创建每日生长与环境记录")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-daily-record:create')")
    public CommonResult<Long> createZkGrowDailyRecord(@Valid @RequestBody ZkGrowDailyRecordSaveReqVO createReqVO) {
        return success(zkGrowDailyRecordService.createZkGrowDailyRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新每日生长与环境记录")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-daily-record:update')")
    public CommonResult<Boolean> updateZkGrowDailyRecord(@Valid @RequestBody ZkGrowDailyRecordSaveReqVO updateReqVO) {
        zkGrowDailyRecordService.updateZkGrowDailyRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除每日生长与环境记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-daily-record:delete')")
    public CommonResult<Boolean> deleteZkGrowDailyRecord(@RequestParam("id") Long id) {
        zkGrowDailyRecordService.deleteZkGrowDailyRecord(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除每日生长与环境记录")
                @PreAuthorize("@ss.hasPermission('fish:zk-grow-daily-record:delete')")
    public CommonResult<Boolean> deleteZkGrowDailyRecordList(@RequestParam("ids") List<Long> ids) {
        zkGrowDailyRecordService.deleteZkGrowDailyRecordListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得每日生长与环境记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-daily-record:query')")
    public CommonResult<ZkGrowDailyRecordRespVO> getZkGrowDailyRecord(@RequestParam("id") Long id) {
        ZkGrowDailyRecordDO zkGrowDailyRecord = zkGrowDailyRecordService.getZkGrowDailyRecord(id);
        enhancerList(List.of(zkGrowDailyRecord));
        return success(BeanUtils.toBean(zkGrowDailyRecord, ZkGrowDailyRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得每日生长与环境记录分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-daily-record:query')")
    public CommonResult<PageResult<ZkGrowDailyRecordRespVO>> getZkGrowDailyRecordPage(@Valid ZkGrowDailyRecordPageReqVO pageReqVO) {
        // initData.initData();
        PageResult<ZkGrowDailyRecordDO> pageResult = zkGrowDailyRecordService.getZkGrowDailyRecordPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkGrowDailyRecordRespVO.class));
    }

    public void enhancerList(List<ZkGrowDailyRecordDO> tList) {
        if(EasyListUtil.isEmpty(tList)) return;
        // 1. 获取数据
        Set<Long> batchIds = tList.stream().map(ZkGrowDailyRecordDO::getBatchId).collect(Collectors.toSet());
        List<ZkGrowFarmingBatchDO> oList = zkGrowFarmingBatchService.getByIds(batchIds);
        // 2. 执行赋值
        CommonUtil.origins2Targets4(oList, ZkGrowFarmingBatchDO::getId, tList, ZkGrowDailyRecordDO::getBatchId, (ZkGrowFarmingBatchDO o, ZkGrowDailyRecordDO t) -> {
            t.setBatchNo(o.getBatchNo());
        });
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出每日生长与环境记录 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-daily-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkGrowDailyRecordExcel(@Valid ZkGrowDailyRecordPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkGrowDailyRecordDO> list = zkGrowDailyRecordService.getZkGrowDailyRecordPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "每日生长与环境记录.xls", "数据", ZkGrowDailyRecordRespVO.class,
                        BeanUtils.toBean(list, ZkGrowDailyRecordRespVO.class));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "获得生长大模型看板数据")
    @Parameters({
            @Parameter(name = "searchType", description = "搜索维度: batch/system/pool", required = true),
            @Parameter(name = "searchId", description = "对应的ID", required = true),
            @Parameter(name = "speciesId", description = "鱼种ID", required = false),
            @Parameter(name = "maxDays", description = "预测天数", required = false)
    })
    public CommonResult<ZkGrowDashboardRespVO> getDashboardData(@RequestParam("searchType") String searchType,
                                                                @RequestParam("searchId") String searchId,
                                                                @RequestParam(value = "speciesId", required = false) String speciesId,
                                                                @RequestParam(value = "maxDays", defaultValue = "180") Integer maxDays) {
        String batchNo = "batch".equals(searchType) ? searchId : null;
        String pondNum = "system".equals(searchType) ? searchId : null;
        String pondDetailNum = "pool".equals(searchType) ? searchId : null;

        List<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowRealStatsDO> statsList = zkGrowDailyRecordService.getRealStats(batchNo, pondNum, pondDetailNum);

        ZkGrowDashboardRespVO resp = new ZkGrowDashboardRespVO();
        resp.setMaxDays(maxDays);

        int count = 0;
        double sumBreedingDays = 0;
        double sumCurrentWeight = 0;
        double sumCurrentLength = 0;
        double sumWaterTemp = 0;
        double sumFeedRate = 0;
        double sumInitialWeight = 0;
        List<Long> pondIds = new ArrayList<>();

        for (cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowRealStatsDO stat : statsList) {
            pondIds.add(stat.getPondId());
            count++;
            sumBreedingDays += stat.getBreedingDays() != null ? stat.getBreedingDays() : 0;
            
            BigDecimal cWeight = parseSpec(stat.getFishSpec());
            sumCurrentWeight += cWeight != null ? cWeight.doubleValue() : 0;
            
            sumCurrentLength += stat.getAvgBodyLength() != null ? stat.getAvgBodyLength().doubleValue() : 0;
            sumWaterTemp += stat.getTemperature() != null ? stat.getTemperature().doubleValue() : 0;
            sumFeedRate += stat.getBaitingRate() != null ? stat.getBaitingRate().doubleValue() : 0;
            
            BigDecimal iWeight = parseSpec(stat.getInitialWeightSpec());
            sumInitialWeight += iWeight != null ? iWeight.doubleValue() : 0;
        }

        double avgBreedingDays = count > 0 ? sumBreedingDays / count : 0;
        double avgCurrentWeight = count > 0 ? sumCurrentWeight / count : 0;
        double avgCurrentLength = count > 0 ? sumCurrentLength / count : 0;
        double avgWaterTemp = count > 0 ? sumWaterTemp / count : 25.0; // 默认25
        double avgFeedRate = count > 0 ? sumFeedRate / count : 3.0; // 默认3.0
        double avgInitialWeight = count > 0 ? sumInitialWeight / count : 5.0; // 默认5.0
        double initialLength = 4.0; // 默认初始体长

        resp.setBreedingDays((int) Math.round(avgBreedingDays));
        resp.setCurrentWeight(BigDecimal.valueOf(avgCurrentWeight).setScale(2, RoundingMode.HALF_UP));
        resp.setCurrentLength(BigDecimal.valueOf(avgCurrentLength).setScale(2, RoundingMode.HALF_UP));
        resp.setWaterTemp(BigDecimal.valueOf(avgWaterTemp).setScale(2, RoundingMode.HALF_UP));
        resp.setSensorTemp(BigDecimal.valueOf(avgWaterTemp).setScale(2, RoundingMode.HALF_UP));
        resp.setFeedRate(BigDecimal.valueOf(avgFeedRate).setScale(2, RoundingMode.HALF_UP));
        resp.setActualFeedRate(BigDecimal.valueOf(avgFeedRate).setScale(2, RoundingMode.HALF_UP));

        // 水质扩展：溶氧/氨氮/亚硝酸盐/pH（取筛选范围内鱼池传感器均值）
        double sumDo = 0, sumNh3 = 0, sumNo2 = 0, sumPh = 0;
        int wqN = 0;
        for (Long pid : pondIds) {
            try {
                cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PondWaterQualityHelper.WaterQuality wq =
                        cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PondWaterQualityHelper.fromRecords(
                                zkDeviceRecordService.selectRecentByQuery(pid));
                if (wq.fromSensor) {
                    sumDo += wq.dissolvedOxygen.doubleValue();
                    sumNh3 += wq.ammonia.doubleValue();
                    sumNo2 += wq.nitrite.doubleValue();
                    sumPh += wq.ph.doubleValue();
                    wqN++;
                }
            } catch (Exception ignored) {}
        }
        if (wqN > 0) {
            resp.setDissolvedOxygen(BigDecimal.valueOf(sumDo / wqN).setScale(2, RoundingMode.HALF_UP));
            resp.setAmmonia(BigDecimal.valueOf(sumNh3 / wqN).setScale(3, RoundingMode.HALF_UP));
            resp.setNitrite(BigDecimal.valueOf(sumNo2 / wqN).setScale(3, RoundingMode.HALF_UP));
            resp.setPh(BigDecimal.valueOf(sumPh / wqN).setScale(2, RoundingMode.HALF_UP));
            // 重新用传感器水温均值（若有）
            double sumT = 0; int tn = 0;
            for (Long pid : pondIds) {
                try {
                    cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PondWaterQualityHelper.WaterQuality wq =
                            cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PondWaterQualityHelper.fromRecords(
                                    zkDeviceRecordService.selectRecentByQuery(pid));
                    if (wq.fromSensor && wq.temperature != null) {
                        sumT += wq.temperature.doubleValue();
                        tn++;
                    }
                } catch (Exception ignored) {}
            }
            if (tn > 0) {
                avgWaterTemp = sumT / tn;
                resp.setWaterTemp(BigDecimal.valueOf(avgWaterTemp).setScale(2, RoundingMode.HALF_UP));
                resp.setSensorTemp(resp.getWaterTemp());
            }
        } else {
            resp.setDissolvedOxygen(new BigDecimal("6.00"));
            resp.setAmmonia(new BigDecimal("0.050"));
            resp.setNitrite(new BigDecimal("0.050"));
            resp.setPh(new BigDecimal("7.50"));
        }

        // 推荐投饵率：复用精准投喂公式 R基础 × 修正系数
        double recDo = resp.getDissolvedOxygen() != null ? resp.getDissolvedOxygen().doubleValue() : 6;
        double recNh3 = resp.getAmmonia() != null ? resp.getAmmonia().doubleValue() : 0.05;
        double recNo2 = resp.getNitrite() != null ? resp.getNitrite().doubleValue() : 0.05;
        cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PrecisionFeedCalculator.Input feedIn =
                new cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PrecisionFeedCalculator.Input();
        feedIn.effectiveCount = 1000;
        feedIn.averageWeightG = avgCurrentWeight > 0 ? avgCurrentWeight : 100;
        feedIn.temperature = avgWaterTemp;
        feedIn.dissolvedOxygen = recDo;
        feedIn.ammonia = recNh3;
        feedIn.nitrite = recNo2;
        cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PrecisionFeedCalculator.Result feedRec =
                cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PrecisionFeedCalculator.calculate(feedIn);
        double recommendedRate = feedRec.baseFeedRate.doubleValue()
                * feedRec.temperatureCoefficient.doubleValue()
                * feedRec.doCoefficient.doubleValue()
                * feedRec.waterQualityCoefficient.doubleValue()
                * feedRec.feedingBehaviorCoefficient.doubleValue();
        resp.setRecommendedFeedRate(BigDecimal.valueOf(recommendedRate).setScale(2, RoundingMode.HALF_UP));
        if (recommendedRate > 0) {
            double diffPct = (avgFeedRate - recommendedRate) / recommendedRate * 100;
            resp.setRecommendedFeedDiffPercent(BigDecimal.valueOf(diffPct).setScale(1, RoundingMode.HALF_UP));
        }

        // 饲料营养修正：粗蛋白越高生长略加速
        double nutritionFactor = 1.0;
        String nutritionRemark = "未配置饵料营养，使用默认修正系数 1.0";
        try {
            List<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitnutrition.ZkGrowBaitNutritionDO> baits =
                    zkGrowBaitNutritionMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitnutrition.ZkGrowBaitNutritionDO>()
                            .eq("deleted", 0).last("LIMIT 20"));
            if (baits != null && !baits.isEmpty()) {
                double proteinSum = 0; int pn = 0;
                for (cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitnutrition.ZkGrowBaitNutritionDO b : baits) {
                    if (b.getCrudeProtein() == null) continue;
                    String num = b.getCrudeProtein().replaceAll("[^0-9.]", "");
                    if (num.isEmpty()) continue;
                    proteinSum += Double.parseDouble(num);
                    pn++;
                }
                if (pn > 0) {
                    double avgProtein = proteinSum / pn;
                    // 42% 为优质配合饲料基准
                    nutritionFactor = Math.max(0.85, Math.min(1.15, avgProtein / 42.0));
                    nutritionRemark = String.format("按饵料平均粗蛋白 %.1f%% 修正生长系数=%.3f", avgProtein, nutritionFactor);
                }
            }
        } catch (Exception ignored) {}
        resp.setNutritionFactor(BigDecimal.valueOf(nutritionFactor).setScale(3, RoundingMode.HALF_UP));
        resp.setNutritionRemark(nutritionRemark);

        // 生物性资产：汇总筛选范围内鱼池存塘生物资产
        BigDecimal biologyAsset = BigDecimal.ZERO;
        if (!pondIds.isEmpty()) {
            try {
                List<cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO> pondFishList =
                        zkPondFishMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<
                                cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO>()
                                .in(cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO::getPondId, pondIds)
                                .eq(cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO::getDeleted, false));
                for (cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO pf : pondFishList) {
                    if (pf.getBiologicalAssets() != null) {
                        biologyAsset = biologyAsset.add(pf.getBiologicalAssets());
                    } else if (pf.getCurrFishSpec() != null && pf.getFishNum() != null) {
                        biologyAsset = biologyAsset.add(
                                pf.getCurrFishSpec().multiply(BigDecimal.valueOf(pf.getFishNum()))
                                        .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP));
                    }
                }
            } catch (Exception ignored) {}
        }
        resp.setBiologyAsset(biologyAsset.setScale(2, RoundingMode.HALF_UP));

        // 日增重
        double weightGainPerDay = avgBreedingDays > 0 ? (avgCurrentWeight - avgInitialWeight) / avgBreedingDays : 0;
        resp.setWeightGainPerDay(BigDecimal.valueOf(weightGainPerDay).setScale(2, RoundingMode.HALF_UP));
        // 日增长
        double lengthGainPerDay = avgBreedingDays > 0 ? (avgCurrentLength - initialLength) / avgBreedingDays : 0;
        resp.setLengthGainPerDay(BigDecimal.valueOf(lengthGainPerDay).setScale(2, RoundingMode.HALF_UP));
        // SGR
        double dsgr = avgBreedingDays > 0 && avgCurrentWeight > 0 && avgInitialWeight > 0 
                    ? (Math.log(avgCurrentWeight) - Math.log(avgInitialWeight)) / avgBreedingDays * 100 : 0;
        resp.setSgr(BigDecimal.valueOf(dsgr).setScale(4, RoundingMode.HALF_UP));

        // 获取模型配置
        Long sId = null;
        if (speciesId != null) {
            try { sId = Long.parseLong(speciesId); } catch (Exception ignored) {}
        }
        if (sId == null && batchNo != null) {
            try {
                Long bId = Long.parseLong(batchNo);
                ZkGrowFarmingBatchDO batch = zkGrowFarmingBatchService.getZkGrowFarmingBatch(bId);
                if (batch != null) sId = batch.getSpeciesId();
            } catch (Exception ignored) {}
        }
        ZkGrowModelConfigDO config = null;
        if (sId != null) config = zkGrowModelConfigService.getZkGrowModelConfigBySpeciesId(sId);
        if (config == null) {
            config = new ZkGrowModelConfigDO();
            config.setLInf(new BigDecimal("45"));
            config.setWInf(new BigDecimal("1200"));
            config.setKLength(new BigDecimal("0.012"));
            config.setKWeight(new BigDecimal("0.015"));
            config.setInitialLength(new BigDecimal("4"));
            config.setInitialWeight(new BigDecimal("5"));
        }

        // 分组手工测量数据
        Map<Long, java.time.LocalDateTime> firstDayMap = statsList.stream()
                .filter(s -> s.getPondId() != null && s.getFirstDay() != null)
                .collect(Collectors.toMap(cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowRealStatsDO::getPondId, cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowRealStatsDO::getFirstDay, (a,b)->a));

        Map<Integer, List<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowManualMeasureDO>> dayMeasurements = new HashMap<>();
        List<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowManualMeasureDO> manualMeasures = zkGrowDailyRecordService.getManualMeasurements(pondIds);
        for (cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowManualMeasureDO mm : manualMeasures) {
            java.time.LocalDateTime firstDay = firstDayMap.get(mm.getPondId());
            if (firstDay != null && mm.getCreateTime() != null) {
                int diffDays = (int) java.time.Duration.between(firstDay, mm.getCreateTime()).toDays();
                if (diffDays >= 0) {
                    dayMeasurements.computeIfAbsent(diffDays, k -> new ArrayList<>()).add(mm);
                }
            }
        }

        // 生成预测数据（叠加水质/投饵/营养修正）
        List<ZkGrowDailyRecordRespVO> predictions = new ArrayList<>();
        double tempFactor = tempCorrection(avgWaterTemp);
        double feedFactor = feedRateCorrection(avgFeedRate);
        double doFactor = Math.max(0.5, Math.min(1.0, recDo / 5.0));
        double waterFactor = cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PrecisionFeedCalculator
                .waterQualityCoefficient(recNh3, recNo2);
        double growthFactor = tempFactor * feedFactor * doFactor * waterFactor * nutritionFactor;

        double kLengthAdj = config.getKLength().doubleValue() * growthFactor;
        double kWeightAdj = config.getKWeight().doubleValue() * growthFactor;
        double lInf = config.getLInf().doubleValue();
        double wInf = config.getWInf().doubleValue();
        double cInitialLength = config.getInitialLength().doubleValue();
        double cInitialWeight = avgInitialWeight > 0 ? avgInitialWeight : config.getInitialWeight().doubleValue();

        Random random = new Random();

        for (int day = 0; day <= maxDays; day++) {
            double length = lInf * (1 - Math.exp(-kLengthAdj * day)) + cInitialLength * Math.exp(-kLengthAdj * day);
            double weight = wInf * (1 - Math.exp(-kWeightAdj * day)) + cInitialWeight * Math.exp(-kWeightAdj * day);

            ZkGrowDailyRecordRespVO vo = new ZkGrowDailyRecordRespVO();
            vo.setDayIndex(day);
            vo.setPredictedLength(BigDecimal.valueOf(length).setScale(2, RoundingMode.HALF_UP));
            vo.setPredictedWeight(BigDecimal.valueOf(weight).setScale(2, RoundingMode.HALF_UP));
            
            // 模拟视觉识别数据 (每5天)，仅模拟当前天数之前的数据
            if (day > 0 && day % 5 == 0 && day <= avgBreedingDays) {
                double vError = (random.nextDouble() - 0.5) * 2 * 5 / 100.0;
                double lError = (random.nextDouble() - 0.5) * 2 * 3 / 100.0;
                vo.setVisionWeight(BigDecimal.valueOf(weight * (1 + vError)).setScale(2, RoundingMode.HALF_UP));
                vo.setVisionLength(BigDecimal.valueOf(length * (1 + lError)).setScale(2, RoundingMode.HALF_UP));
            }

            // 手动测量数据 (取SQL C)
            List<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowManualMeasureDO> mms = dayMeasurements.get(day);
            if (mms != null && !mms.isEmpty()) {
                double avgMmWeight = mms.stream().filter(m -> m.getAvgSpecs() != null)
                                                 .mapToDouble(m -> m.getAvgSpecs().doubleValue()).average().orElse(0);
                double avgMmLength = mms.stream().filter(m -> m.getAvgBodyLength() != null)
                                                 .mapToDouble(m -> m.getAvgBodyLength().doubleValue()).average().orElse(0);
                if (avgMmWeight > 0) vo.setManualWeight(BigDecimal.valueOf(avgMmWeight).setScale(2, RoundingMode.HALF_UP));
                if (avgMmLength > 0) vo.setManualLength(BigDecimal.valueOf(avgMmLength).setScale(2, RoundingMode.HALF_UP));
            }
            
            // 计算 SGR
            if (day > 0) {
                double prevWeight = wInf * (1 - Math.exp(-kWeightAdj * (day - 1))) + cInitialWeight * Math.exp(-kWeightAdj * (day - 1));
                double dsgr_p = (Math.log(weight) - Math.log(prevWeight)) * 100;
                vo.setSgr(BigDecimal.valueOf(dsgr_p).setScale(4, RoundingMode.HALF_UP));
            } else {
                vo.setSgr(BigDecimal.ZERO);
            }
            predictions.add(vo);
        }
        resp.setPredictions(predictions);

        // AI 分析：按鱼池对比实测 vs 模型预测 → 发育良好/正常/缓慢
        int dayIndex = Math.max(0, Math.min((int) Math.round(avgBreedingDays), maxDays));
        double predictedNow = 0;
        if (!predictions.isEmpty()) {
            predictedNow = predictions.get(dayIndex).getPredictedWeight().doubleValue();
        }
        List<ZkGrowPondAiItemVO> pondAiItems = new ArrayList<>();
        int good = 0, normal = 0, slow = 0;
        for (cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowRealStatsDO stat : statsList) {
            BigDecimal cWeight = parseSpec(stat.getFishSpec());
            double actualW = cWeight != null ? cWeight.doubleValue() : 0;
            int days = stat.getBreedingDays() != null ? Math.min(stat.getBreedingDays(), maxDays) : dayIndex;
            double predW = predictions.get(Math.max(0, days)).getPredictedWeight().doubleValue();
            double deviation = predW > 0 ? (actualW - predW) / predW * 100 : 0;
            String status;
            if (deviation >= -5) {
                status = "发育良好";
                good++;
            } else if (deviation >= -15) {
                status = "正常";
                normal++;
            } else {
                status = "缓慢";
                slow++;
            }
            ZkGrowPondAiItemVO item = new ZkGrowPondAiItemVO();
            item.setPondId(stat.getPondId());
            item.setPondDetailNum(stat.getPondDetailNum());
            try {
                cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO pond = zkPondService.getZkPond(stat.getPondId());
                if (pond != null) item.setPondName(pond.getPondName());
            } catch (Exception ignored) {}
            item.setCurrentWeight(BigDecimal.valueOf(actualW).setScale(2, RoundingMode.HALF_UP));
            item.setPredictedWeight(BigDecimal.valueOf(predW).setScale(2, RoundingMode.HALF_UP));
            item.setDeviationPercent(BigDecimal.valueOf(deviation).setScale(1, RoundingMode.HALF_UP));
            item.setGrowthStatus(status);
            item.setRemark(String.format("实测%.1fg / 预测%.1fg，偏差%.1f%%", actualW, predW, deviation));
            pondAiItems.add(item);
        }
        resp.setPondAiItems(pondAiItems);
        String overallStatus = slow > 0 ? "缓慢" : (good >= normal ? "发育良好" : "正常");
        if (pondAiItems.isEmpty()) {
            double overallDev = predictedNow > 0 ? (avgCurrentWeight - predictedNow) / predictedNow * 100 : 0;
            if (overallDev >= -5) overallStatus = "发育良好";
            else if (overallDev >= -15) overallStatus = "正常";
            else overallStatus = "缓慢";
        }
        resp.setGrowthStatus(overallStatus);
        resp.setAiAnalysisSummary(String.format(
                "生长模拟第%d天：整体%s。发育良好%d池、正常%d池、缓慢%d池。推荐投饵率%.2f%%，实际%.2f%%。%s",
                dayIndex, overallStatus, good, normal, slow,
                recommendedRate, avgFeedRate, nutritionRemark));
        boolean needAlarm = "缓慢".equals(overallStatus)
                || (resp.getRecommendedFeedDiffPercent() != null
                    && Math.abs(resp.getRecommendedFeedDiffPercent().doubleValue()) > 100);
        resp.setNeedAlarm(needAlarm);
        if (needAlarm && !pondIds.isEmpty()) {
            try {
                cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo.ZkDeviceAlarmCreateReqVO alarm =
                        new cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo.ZkDeviceAlarmCreateReqVO();
                alarm.setPondId(pondIds.get(0));
                alarm.setWarnName("鱼类生长异常");
                alarm.setWarnType(0);
                alarm.setWarnDeviceType(1);
                alarm.setWarnContent(resp.getAiAnalysisSummary());
                alarm.setStatus(0);
                zkDeviceAlarmService.createZkDeviceAlarm(alarm);
            } catch (Exception ignored) {}
        }

        return success(resp);
    }

    private BigDecimal parseSpec(String spec) {
        if (spec == null) return null;
        String numStr = spec.replaceAll("[^0-9.]", "");
        if (numStr.isEmpty()) return null;
        try {
            return new BigDecimal(numStr);
        } catch (Exception e) {
            return null;
        }
    }

}