package cn.iocoder.yudao.module.fish.service.zkprecisionfeeding;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.PrecisionFeedingPondRespVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo.ZkDeviceAlarmCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth.ZkPondHealthDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfeedinglog.ZkFeedingLogMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondfishdetail.ZkPondFishDetailMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondhealth.ZkPondHealthMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zktask.ZkTaskMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zktaskdetail.TaskDetailMapper;
import cn.iocoder.yudao.module.fish.service.zkdevicealarm.ZkDeviceAlarmService;
import cn.iocoder.yudao.module.fish.service.zkdevicerecord.ZkDeviceRecordService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PondWaterQualityHelper;
import cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to.PrecisionFeedCalculator;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Validated
public class ZkPrecisionFeedingBizServiceImpl implements ZkPrecisionFeedingBizService {

    private static final Set<String> AFFECTING_KEYWORDS = new HashSet<>(Arrays.asList(
            "分池", "消毒", "用药", "维护", "停食", "销售", "换水", "转塘", "出池"
    ));

    private final ConcurrentHashMap<String, PrecisionFeedPlanVO> planStore = new ConcurrentHashMap<>();
    private final AtomicLong planIdSeq = new AtomicLong(1);

    @Resource
    private ZkPondService zkPondService;
    @Resource
    private ZkDeviceRecordService zkDeviceRecordService;
    @Resource
    private ZkFeedingLogMapper zkFeedingLogMapper;
    @Resource
    private ZkPondHealthMapper zkPondHealthMapper;
    @Resource
    private ZkTaskMapper zkTaskMapper;
    @Resource
    private TaskDetailMapper taskDetailMapper;
    @Resource
    private ZkDeviceAlarmService zkDeviceAlarmService;
    @Resource
    private ZkPondFishDetailMapper zkPondFishDetailMapper;
    @Resource
    private DeptScopeSupport deptScopeSupport;

    @Override
    public PrecisionFeedRecommendRespVO recommend(Long pondId) {
        ZkPondDO pond = requirePond(pondId);
        PondWaterQualityHelper.WaterQuality wq = PondWaterQualityHelper.fromRecords(
                zkDeviceRecordService.selectRecentByQuery(pondId));

        int fishNum = pond.getFishNum() != null ? pond.getFishNum().intValue() : 0;
        double avgWeight = pond.getFishSpec() != null ? pond.getFishSpec().doubleValue() : 100.0;
        double behavior = latestBehaviorScore(pondId);

        PrecisionFeedCalculator.Input input = new PrecisionFeedCalculator.Input();
        input.effectiveCount = fishNum;
        input.averageWeightG = avgWeight;
        input.temperature = wq.temperature.doubleValue();
        input.dissolvedOxygen = wq.dissolvedOxygen.doubleValue();
        input.ammonia = wq.ammonia.doubleValue();
        input.nitrite = wq.nitrite.doubleValue();
        input.feedingBehavior = behavior;
        PrecisionFeedCalculator.Result calc = PrecisionFeedCalculator.calculate(input);

        ZkPondHealthDO health = latestHealth(pondId);
        BigDecimal healthScore = health != null ? health.getOverallRating() : null;
        String healthLevel = PrecisionFeedCalculator.healthLevel(healthScore);

        BigDecimal todayFeed = sumFeed(pondId, LocalDate.now(), LocalDate.now());
        BigDecimal yesterdayFeed = sumFeed(pondId, LocalDate.now().minusDays(1), LocalDate.now().minusDays(1));
        BigDecimal waterVolume = parseVolume(pond.getPondScale());
        BigDecimal density = null;
        if (waterVolume != null && waterVolume.doubleValue() > 0 && fishNum > 0) {
            density = BigDecimal.valueOf(fishNum).divide(waterVolume, 2, RoundingMode.HALF_UP);
        }

        PrecisionFeedRecommendRespVO vo = new PrecisionFeedRecommendRespVO();
        vo.setPondId(pondId);
        vo.setPondName(pond.getPondName());
        vo.setFishNum(fishNum);
        vo.setAverageWeightG(BigDecimal.valueOf(avgWeight).setScale(2, RoundingMode.HALF_UP));
        vo.setDensity(density);
        vo.setHealthLevel(healthLevel);
        vo.setHealthScore(healthScore);
        vo.setTodayDeathCount(todayDeathCount(pondId));
        vo.setWaterVolume(waterVolume);
        vo.setTemperature(wq.temperature);
        vo.setDissolvedOxygen(wq.dissolvedOxygen);
        vo.setAmmonia(wq.ammonia);
        vo.setNitrite(wq.nitrite);
        vo.setPh(wq.ph);
        vo.setWaterFromSensor(wq.fromSensor);
        vo.setTotalBiomassKg(calc.totalBiomassKg);
        vo.setBaseFeedRate(calc.baseFeedRate);
        vo.setTemperatureCoefficient(calc.temperatureCoefficient);
        vo.setDoCoefficient(calc.doCoefficient);
        vo.setWaterQualityCoefficient(calc.waterQualityCoefficient);
        vo.setFeedingBehaviorCoefficient(calc.feedingBehaviorCoefficient);
        vo.setDailyFeedAmountKg(calc.dailyFeedAmountKg);
        vo.setSingleFeedAmountKg(calc.singleFeedAmountKg);
        vo.setDailyFrequency(calc.dailyFrequency);
        vo.setFeedType(calc.feedType);
        vo.setRecommendation(calc.recommendation);
        vo.setWarnings(calc.warnings);
        vo.setTodayActualFeedKg(todayFeed);
        vo.setYesterdayFeedKg(yesterdayFeed);
        if (calc.dailyFeedAmountKg != null && calc.dailyFeedAmountKg.doubleValue() > 0 && todayFeed != null) {
            double diff = (todayFeed.doubleValue() - calc.dailyFeedAmountKg.doubleValue())
                    / calc.dailyFeedAmountKg.doubleValue() * 100;
            vo.setFeedDiffPercent(BigDecimal.valueOf(diff).setScale(1, RoundingMode.HALF_UP));
        }
        return vo;
    }

    @Override
    public PrecisionFeedPlanVO savePlan(PrecisionFeedPlanVO plan) {
        if (plan.getPlanDate() == null) {
            plan.setPlanDate(LocalDate.now());
        }
        if (plan.getStatus() == null) {
            plan.setStatus("draft");
        }
        if (plan.getPlanType() == null) {
            plan.setPlanType("smart");
        }
        if ("smart".equals(plan.getPlanType()) && plan.getTotalAmountKg() == null) {
            PrecisionFeedRecommendRespVO rec = recommend(plan.getPondId());
            plan.setTotalAmountKg(rec.getDailyFeedAmountKg());
            plan.setFrequency(rec.getDailyFrequency());
            plan.setSingleAmountKg(rec.getSingleFeedAmountKg());
            plan.setFeedType(rec.getFeedType());
            plan.setPondName(rec.getPondName());
        }
        if (plan.getFrequency() != null && plan.getFrequency() > 0 && plan.getTotalAmountKg() != null
                && plan.getSingleAmountKg() == null) {
            plan.setSingleAmountKg(plan.getTotalAmountKg()
                    .divide(BigDecimal.valueOf(plan.getFrequency()), 3, RoundingMode.HALF_UP));
        }
        String key = planKey(plan.getPondId(), plan.getPlanType(), plan.getPlanDate());
        PrecisionFeedPlanVO existing = planStore.get(key);
        if (existing != null) {
            plan.setId(existing.getId());
        } else {
            plan.setId(planIdSeq.getAndIncrement());
        }
        planStore.put(key, plan);
        return plan;
    }

    @Override
    public PrecisionFeedPlanVO confirmPlan(Long pondId, String planType, LocalDate planDate) {
        LocalDate date = planDate != null ? planDate : LocalDate.now();
        String type = planType != null ? planType : "actual";
        String key = planKey(pondId, type, date);
        PrecisionFeedPlanVO plan = planStore.get(key);
        if (plan == null) {
            // 无草稿则用推荐生成实际计划
            PrecisionFeedRecommendRespVO rec = recommend(pondId);
            plan = new PrecisionFeedPlanVO();
            plan.setPondId(pondId);
            plan.setPondName(rec.getPondName());
            plan.setPlanDate(date);
            plan.setPlanType(type);
            plan.setTotalAmountKg(rec.getDailyFeedAmountKg());
            plan.setFrequency(rec.getDailyFrequency());
            plan.setSingleAmountKg(rec.getSingleFeedAmountKg());
            plan.setFeedType(rec.getFeedType());
            plan.setId(planIdSeq.getAndIncrement());
        }
        plan.setStatus("confirmed");
        planStore.put(key, plan);

        // 确认实际计划后写入一条投喂记录，便于饵料系数/生长模块联动
        if ("actual".equals(type) && plan.getTotalAmountKg() != null
                && plan.getTotalAmountKg().compareTo(BigDecimal.ZERO) > 0) {
            ZkPondDO pond = requirePond(pondId);
            ZkFeedingLogDO log = new ZkFeedingLogDO();
            log.setPondId(pondId);
            log.setPondName(pond.getPondName());
            log.setPondNum(pond.getPondNum());
            log.setPondDetailNum(pond.getPondDetailNum());
            log.setWorkshopId(pond.getWorkshopId());
            log.setWorkshopName(pond.getWorkshopName());
            log.setPondSystemName(pond.getPondSystemName());
            log.setFeedCount(plan.getTotalAmountKg());
            log.setFeedName(plan.getFeedType());
            log.setFeedSpec(plan.getFeedType());
            log.setFeedType("1");
            log.setFeedTime(LocalDateTime.now());
            log.setRemark("精准投喂计划确认实施");
            log.setDeptId(pond.getDeptId());
            zkFeedingLogMapper.insert(log);
        }
        return plan;
    }

    @Override
    public List<PrecisionFeedPlanVO> listPlans(Long pondId, LocalDate planDate) {
        LocalDate date = planDate != null ? planDate : LocalDate.now();
        List<PrecisionFeedPlanVO> list = new ArrayList<>();
        PrecisionFeedPlanVO smart = planStore.get(planKey(pondId, "smart", date));
        PrecisionFeedPlanVO actual = planStore.get(planKey(pondId, "actual", date));
        if (smart == null) {
            PrecisionFeedRecommendRespVO rec = recommend(pondId);
            smart = new PrecisionFeedPlanVO();
            smart.setPondId(pondId);
            smart.setPondName(rec.getPondName());
            smart.setPlanDate(date);
            smart.setPlanType("smart");
            smart.setTotalAmountKg(rec.getDailyFeedAmountKg());
            smart.setFrequency(rec.getDailyFrequency());
            smart.setSingleAmountKg(rec.getSingleFeedAmountKg());
            smart.setFeedType(rec.getFeedType());
            smart.setStatus("draft");
        }
        list.add(smart);
        if (actual != null) {
            list.add(actual);
        }
        return list;
    }

    @Override
    public PrecisionFeedConsumptionRespVO feedConsumption(Long workshopId, Long pondSystemId, Long pondId,
                                                          LocalDate beginDate, LocalDate endDate) {
        LocalDate begin = beginDate != null ? beginDate : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        List<ZkFeedingLogDO> logs = zkFeedingLogMapper.selectList(new LambdaQueryWrapperX<ZkFeedingLogDO>()
                .eqIfPresent(ZkFeedingLogDO::getWorkshopId, workshopId)
                .eqIfPresent(ZkFeedingLogDO::getPondId, pondId)
                .between(ZkFeedingLogDO::getFeedTime, begin.atStartOfDay(), end.atTime(LocalTime.MAX))
                .orderByDesc(ZkFeedingLogDO::getId));
        if (pondSystemId != null) {
            logs = logs.stream().filter(l -> {
                ZkPondDO p = zkPondService.getZkPond(l.getPondId());
                return p != null && Objects.equals(p.getPondSystemId(), pondSystemId);
            }).collect(Collectors.toList());
        }

        Map<String, PrecisionFeedConsumptionRespVO.Item> map = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        Set<Long> ponds = new HashSet<>();
        for (ZkFeedingLogDO log : logs) {
            if (log.getFeedCount() == null) continue;
            ponds.add(log.getPondId());
            total = total.add(log.getFeedCount());
            String key = (log.getPondId() == null ? "-" : log.getPondId()) + "|"
                    + Optional.ofNullable(log.getFeedName()).orElse("-") + "|"
                    + Optional.ofNullable(log.getFeedSpec()).orElse("-");
            PrecisionFeedConsumptionRespVO.Item item = map.computeIfAbsent(key, k -> {
                PrecisionFeedConsumptionRespVO.Item i = new PrecisionFeedConsumptionRespVO.Item();
                i.setWorkshopId(log.getWorkshopId());
                i.setWorkshopName(log.getWorkshopName());
                i.setPondId(log.getPondId());
                i.setPondName(log.getPondName());
                i.setFeedName(log.getFeedName());
                i.setFeedSpec(log.getFeedSpec());
                i.setFeedType(log.getFeedName());
                i.setFeedModel(log.getFeedSpec());
                i.setTotalKg(BigDecimal.ZERO);
                return i;
            });
            item.setTotalKg(item.getTotalKg().add(log.getFeedCount()));
            if (item.getPondSystemName() == null) {
                item.setPondSystemName(log.getPondSystemName());
            }
        }
        PrecisionFeedConsumptionRespVO resp = new PrecisionFeedConsumptionRespVO();
        resp.setTotalFeedKg(total.setScale(3, RoundingMode.HALF_UP));
        resp.setPondCount(ponds.size());
        resp.setItems(new ArrayList<>(map.values()));
        return resp;
    }

    @Override
    public PrecisionFeedAiAnalysisRespVO aiAnalysis(Long pondId) {
        PrecisionFeedRecommendRespVO rec = recommend(pondId);
        LocalDate today = LocalDate.now();
        PrecisionFeedPlanVO smart = planStore.get(planKey(pondId, "smart", today));
        PrecisionFeedPlanVO actual = planStore.get(planKey(pondId, "actual", today));
        BigDecimal smartKg = smart != null && smart.getTotalAmountKg() != null
                ? smart.getTotalAmountKg() : rec.getDailyFeedAmountKg();
        BigDecimal actualKg = actual != null && actual.getTotalAmountKg() != null
                ? actual.getTotalAmountKg()
                : (rec.getTodayActualFeedKg() != null ? rec.getTodayActualFeedKg() : BigDecimal.ZERO);

        List<String> reasons = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        double deviation = 0;
        if (smartKg != null && smartKg.doubleValue() > 0) {
            deviation = (actualKg.doubleValue() - smartKg.doubleValue()) / smartKg.doubleValue() * 100;
        }
        if (Math.abs(deviation) > 100) {
            reasons.add("实际投喂与智能计划偏差超过100%，疑似过量或漏喂");
            suggestions.add("立即核对投喂设备/人工录入，并按智能计划重新确认实施");
        } else if (Math.abs(deviation) > 15) {
            reasons.add("实际投喂与智能计划偏差超过15%");
            suggestions.add("结合水质与摄食行为，微调单次投喂量");
        }
        if (rec.getWarnings() != null) {
            reasons.addAll(rec.getWarnings());
        }
        if (rec.getTemperatureCoefficient() != null && rec.getTemperatureCoefficient().doubleValue() < 1) {
            reasons.add("水温修正系数下调，生长模型预测长势可能放缓");
            suggestions.add("联动鱼类生长大模型查看推荐投饵率，避免盲目加料");
        }
        if (rec.getWaterQualityCoefficient() != null && rec.getWaterQualityCoefficient().doubleValue() < 1) {
            reasons.add("氨氮/亚硝酸盐处于警戒，残饵会进一步污染水质");
            suggestions.add("减料并联动水质模块排查滤池负荷");
        }
        if ("介入".equals(rec.getHealthLevel())) {
            reasons.add("鱼池健康状态为介入，投喂应优先保障鱼群稳定");
            suggestions.add("同步生成巡检核查任务，必要时停食观察");
        }
        if (reasons.isEmpty()) {
            reasons.add("智能计划与实际投喂基本一致，环境与摄食指标正常");
            suggestions.add("维持当前投喂节奏，持续关注饵料系数 1.2~1.5 目标区间");
        }

        boolean needAlarm = Math.abs(deviation) > 100
                || (rec.getDoCoefficient() != null && rec.getDoCoefficient().doubleValue() == 0)
                || "介入".equals(rec.getHealthLevel());

        PrecisionFeedAiAnalysisRespVO vo = new PrecisionFeedAiAnalysisRespVO();
        vo.setPondId(pondId);
        vo.setPondName(rec.getPondName());
        vo.setHealthLevel(rec.getHealthLevel());
        vo.setSmartTotalKg(smartKg);
        vo.setActualTotalKg(actualKg);
        vo.setDeviationPercent(BigDecimal.valueOf(deviation).setScale(1, RoundingMode.HALF_UP));
        vo.setDeviationReasons(reasons);
        vo.setSuggestions(suggestions);
        vo.setNeedAlarm(needAlarm);
        vo.setSummary(String.format("鱼池[%s]健康%s，智能计划%.2fkg / 实际%.2fkg，偏差%.1f%%。%s",
                Optional.ofNullable(rec.getPondName()).orElse(String.valueOf(pondId)),
                rec.getHealthLevel(),
                smartKg != null ? smartKg.doubleValue() : 0,
                actualKg.doubleValue(),
                deviation,
                suggestions.get(0)));

        if (needAlarm) {
            tryPushFeedAlarm(rec, vo);
        }
        return vo;
    }

    @Override
    public List<PrecisionFeedAffectingTaskVO> affectingTasks(Long pondId) {
        List<PrecisionFeedAffectingTaskVO> result = new ArrayList<>();
        try {
            // 本地库 zk_task 无 pond_ids 列，通过 task_detail.pond_id 关联
            List<TaskDetailDO> details = taskDetailMapper.selectList(new LambdaQueryWrapperX<TaskDetailDO>()
                    .eq(TaskDetailDO::getPondId, pondId)
                    .orderByDesc(TaskDetailDO::getId)
                    .last("LIMIT 50"));
            Set<Long> taskIds = details.stream()
                    .map(TaskDetailDO::getTaskId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (taskIds.isEmpty()) {
                return result;
            }
            List<ZkTaskDO> tasks = zkTaskMapper.selectBatchIds(taskIds);
            Map<Long, ZkTaskDO> taskMap = tasks.stream()
                    .filter(t -> t.getId() != null)
                    .collect(Collectors.toMap(ZkTaskDO::getId, t -> t, (a, b) -> a));
            for (Long taskId : taskIds) {
                ZkTaskDO t = taskMap.get(taskId);
                if (t == null) continue;
                String text = (Optional.ofNullable(t.getTaskName()).orElse("") + " "
                        + Optional.ofNullable(t.getTaskType()).orElse("") + " "
                        + Optional.ofNullable(t.getTaskContent()).orElse(""));
                boolean affecting = AFFECTING_KEYWORDS.stream().anyMatch(text::contains);
                if (!affecting) {
                    // 明细检查项名称也可能含停食/用药等
                    affecting = details.stream()
                            .filter(d -> Objects.equals(d.getTaskId(), taskId))
                            .anyMatch(d -> {
                                String c = Optional.ofNullable(d.getCheckName()).orElse("")
                                        + Optional.ofNullable(d.getCheckContent()).orElse("")
                                        + Optional.ofNullable(d.getCheckType()).orElse("");
                                return AFFECTING_KEYWORDS.stream().anyMatch(c::contains);
                            });
                }
                if (!affecting) continue;
                PrecisionFeedAffectingTaskVO vo = new PrecisionFeedAffectingTaskVO();
                vo.setTaskId(t.getId());
                vo.setTaskName(t.getTaskName());
                vo.setTaskType(t.getTaskType());
                vo.setStatus(t.getStatus() != null ? String.valueOf(t.getStatus()) : null);
                vo.setPlanTime(t.getStartTime());
                vo.setRemark(t.getTaskContent());
                result.add(vo);
                if (result.size() >= 20) break;
            }
        } catch (Exception e) {
            // 任务表结构差异时返回空列表，避免拖垮投喂页
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public void enrichPondList(List<PrecisionFeedingPondRespVO> ponds) {
        if (ponds == null || ponds.isEmpty()) return;
        // 列表页只做轻量 enrich：健康分 + 密度。水质/推荐投喂在选中鱼池详情接口再算，避免 N 次设备查询卡首屏
        Map<Long, ZkPondHealthDO> healthMap = zkPondHealthMapper
                .selectLatestPondHealthList(deptScopeSupport.resolveDeptIds(null)).stream()
                .filter(h -> h.getPondId() != null)
                .collect(Collectors.toMap(ZkPondHealthDO::getPondId, h -> h, (a, b) -> a));
        for (PrecisionFeedingPondRespVO pond : ponds) {
            ZkPondHealthDO health = healthMap.get(pond.getId());
            BigDecimal score = health != null ? health.getOverallRating() : null;
            pond.setHealthScore(score);
            pond.setHealthLevel(PrecisionFeedCalculator.healthLevel(score));
            pond.setPondStatus(pond.getHealthLevel());
            BigDecimal volume = parseVolume(pond.getPondScale());
            pond.setWaterVolume(volume);
            if (volume != null && volume.doubleValue() > 0 && pond.getFishNum() != null) {
                pond.setAvgDensity(BigDecimal.valueOf(pond.getFishNum())
                        .divide(volume, 2, RoundingMode.HALF_UP));
            }
        }
    }

    private void tryPushFeedAlarm(PrecisionFeedRecommendRespVO rec, PrecisionFeedAiAnalysisRespVO analysis) {
        try {
            ZkDeviceAlarmCreateReqVO alarm = new ZkDeviceAlarmCreateReqVO();
            alarm.setPondId(rec.getPondId());
            alarm.setPondName(rec.getPondName());
            alarm.setWarnName("精准投喂异常");
            alarm.setWarnType(0);
            alarm.setWarnDeviceType(1);
            alarm.setWarnContent(analysis.getSummary());
            alarm.setStatus(0);
            zkDeviceAlarmService.createZkDeviceAlarm(alarm);
        } catch (Exception ignored) {
        }
    }

    private ZkPondDO requirePond(Long pondId) {
        ZkPondDO pond = zkPondService.getZkPond(pondId);
        if (pond == null) {
            throw new IllegalArgumentException("鱼池不存在: " + pondId);
        }
        return pond;
    }

    private ZkPondHealthDO latestHealth(Long pondId) {
        return zkPondHealthMapper.selectLatestPondHealthList(deptScopeSupport.resolveDeptIds(null)).stream()
                .filter(h -> Objects.equals(h.getPondId(), pondId))
                .findFirst().orElse(null);
    }

    private Integer todayDeathCount(Long pondId) {
        try {
            Integer n = zkPondFishDetailMapper.selectTodayDeathCount(pondId);
            return n != null ? n : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private BigDecimal sumFeed(Long pondId, LocalDate begin, LocalDate end) {
        List<ZkFeedingLogDO> logs = zkFeedingLogMapper.selectList(new LambdaQueryWrapperX<ZkFeedingLogDO>()
                .eq(ZkFeedingLogDO::getPondId, pondId)
                .between(ZkFeedingLogDO::getFeedTime, begin.atStartOfDay(), end.atTime(LocalTime.MAX)));
        BigDecimal sum = BigDecimal.ZERO;
        for (ZkFeedingLogDO log : logs) {
            if (log.getFeedCount() != null) sum = sum.add(log.getFeedCount());
        }
        return sum.setScale(3, RoundingMode.HALF_UP);
    }

    private double latestBehaviorScore(Long pondId) {
        List<ZkFeedingLogDO> logs = zkFeedingLogMapper.selectList(new LambdaQueryWrapperX<ZkFeedingLogDO>()
                .eq(ZkFeedingLogDO::getPondId, pondId)
                .orderByDesc(ZkFeedingLogDO::getId)
                .last("LIMIT 5"));
        return logs.stream()
                .map(ZkFeedingLogDO::getPersonScore)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(90.0);
    }

    private BigDecimal parseVolume(String pondScale) {
        if (pondScale == null || pondScale.isEmpty()) return null;
        String num = pondScale.replaceAll("[^0-9.]", "");
        if (num.isEmpty()) return null;
        try {
            return new BigDecimal(num);
        } catch (Exception e) {
            return null;
        }
    }

    private String planKey(Long pondId, String planType, LocalDate date) {
        return pondId + "|" + planType + "|" + date;
    }
}
