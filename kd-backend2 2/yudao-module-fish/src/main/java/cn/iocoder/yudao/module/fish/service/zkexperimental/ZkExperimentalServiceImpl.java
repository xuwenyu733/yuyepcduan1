package cn.iocoder.yudao.module.fish.service.zkexperimental;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpool.ZkExperimentalPoolDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpoolmapping.ZkExperimentalPoolMappingDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpool.ZkExperimentalPoolMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpoolmapping.ZkExperimentalPoolMappingMapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkexperimental.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimental.ZkExperimentalDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimental.ZkExperimentalMapper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentaldata.ZkExperimentalDataMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 实验 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkExperimentalServiceImpl implements ZkExperimentalService {

    @Resource
    private ZkExperimentalMapper zkExperimentalMapper;

    @Resource
    private ZkExperimentalPoolMappingMapper zkExperimentalPoolMappingMapper;

    @Resource
    private ZkExperimentalPoolMapper zkExperimentalPoolMapper;

    @Resource
    private ZkExperimentalDataMapper zkExperimentalDataMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createZkExperimental(ZkExperimentalSaveReqVO createReqVO) {
        // 插入
        ZkExperimentalDO zkExperimental = BeanUtils.toBean(createReqVO, ZkExperimentalDO.class);
        zkExperimentalMapper.insert(zkExperimental);

        // 保存关联池子
        savePoolMappings(zkExperimental.getId(), createReqVO.getPoolIds(), createReqVO.getPoolDetails());

        // 返回
        return zkExperimental.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateZkExperimental(ZkExperimentalSaveReqVO updateReqVO) {
        // 校验存在
        validateZkExperimentalExists(updateReqVO.getId());
        // 更新
        ZkExperimentalDO updateObj = BeanUtils.toBean(updateReqVO, ZkExperimentalDO.class);

        // 填写了效果评估/主要结果时，未显式指定状态则自动标为已完成；
        // 用户主动回退到进行中或更早状态时，尊重选择，不再强制改回已完成
        Integer requestedStatus = updateReqVO.getExperimentalStatus();
        boolean explicitRollback = requestedStatus != null && requestedStatus < 3;
        if (!explicitRollback
                && (StringUtil.isNotEmpty(updateReqVO.getEffectEvaluation())
                    || StringUtil.isNotEmpty(updateReqVO.getMainResults()))
                && (updateObj.getExperimentalStatus() == null || updateObj.getExperimentalStatus() < 3)) {
            updateObj.setExperimentalStatus(3); // 3:已完成
            if (updateObj.getCompletionTime() == null) {
                updateObj.setCompletionTime(LocalDateTime.now());
            }
        }

        zkExperimentalMapper.updateById(updateObj);

        // 更新关联池子
        zkExperimentalPoolMappingMapper.deleteByExperimentalId(updateReqVO.getId());
        savePoolMappings(updateReqVO.getId(), updateReqVO.getPoolIds(), updateReqVO.getPoolDetails());
    }

    private void validateZkExperimentalExists(Long id) {
        if (zkExperimentalMapper.selectById(id) == null) {
            throw exception(ZK_EXPERIMENTAL_NOT_EXISTS);
        }
    }

    private void savePoolMappings(Long experimentalId, List<Long> poolIds, List<ZkExperimentalPoolMappingDO> poolDetails) {
        if (CollUtil.isEmpty(poolIds) && CollUtil.isEmpty(poolDetails)) {
            return;
        }
        
        // 如果有详情，以详情为准
        if (CollUtil.isNotEmpty(poolDetails)) {
            poolDetails.forEach(mapping -> {
                mapping.setExperimentalId(experimentalId);
                // 补充池号
                if (StringUtil.isEmpty(mapping.getPoolNumber()) && mapping.getPoolId() != null) {
                    ZkExperimentalPoolDO pool = zkExperimentalPoolMapper.selectById(mapping.getPoolId());
                    if (pool != null) {
                        mapping.setPoolNumber(pool.getPoolNumber());
                    }
                }
            });
            zkExperimentalPoolMappingMapper.insertBatch(poolDetails);
            return;
        }

        // 只有 ID 的回退逻辑
        List<ZkExperimentalPoolDO> pools = zkExperimentalPoolMapper.selectBatchIds(poolIds);
        Map<Long, String> poolNumberMap = CollectionUtils.convertMap(pools, ZkExperimentalPoolDO::getId, ZkExperimentalPoolDO::getPoolNumber);
        
        List<ZkExperimentalPoolMappingDO> mappings = poolIds.stream().map(poolId -> {
            ZkExperimentalPoolMappingDO mapping = new ZkExperimentalPoolMappingDO();
            mapping.setExperimentalId(experimentalId);
            mapping.setPoolId(poolId);
            mapping.setPoolNumber(poolNumberMap.get(poolId));
            return mapping;
        }).collect(Collectors.toList());
        zkExperimentalPoolMappingMapper.insertBatch(mappings);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteZkExperimental(Long id) {
        // 校验存在
        validateZkExperimentalExists(id);
        // 删除
        zkExperimentalMapper.deleteById(id);
        // 删除关联
        zkExperimentalPoolMappingMapper.deleteByExperimentalId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteZkExperimentalListByIds(List<Long> ids) {
        // 删除
        zkExperimentalMapper.deleteByIds(ids);
        // 删除关联
        ids.forEach(zkExperimentalPoolMappingMapper::deleteByExperimentalId);
    }

    @Override
    public ZkExperimentalDO getZkExperimental(Long id) {
        ZkExperimentalDO experimental = zkExperimentalMapper.selectById(id);
        if (experimental != null) {
            List<ZkExperimentalPoolMappingDO> mappings = zkExperimentalPoolMappingMapper.selectListByExperimentalId(id);
            experimental.setPoolIds(CollectionUtils.convertList(mappings, ZkExperimentalPoolMappingDO::getPoolId));
            experimental.setPoolNumbers(CollectionUtils.convertList(mappings, ZkExperimentalPoolMappingDO::getPoolNumber));
            experimental.setPoolDetails(mappings);
        }
        return experimental;
    }

    @Override
    public PageResult<ZkExperimentalDO> getZkExperimentalPage(ZkExperimentalPageReqVO pageReqVO) {
        return zkExperimentalMapper.selectPage(pageReqVO);
    }

    @Override
    public ZkExperimentalAnalysisSummaryRespVO getAnalysisSummary(Long experimentId, String startDate, String endDate) {
        LambdaQueryWrapper<ZkExperimentalDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ZkExperimentalDO::getDeleted, false);
        if (experimentId != null) {
            wrapper.eq(ZkExperimentalDO::getId, experimentId);
        }
        LocalDateTime rangeStart = parseDateStart(startDate);
        LocalDateTime rangeEnd = parseDateEnd(endDate);
        if (rangeStart != null) {
            wrapper.ge(ZkExperimentalDO::getStartTime, rangeStart);
        }
        if (rangeEnd != null) {
            wrapper.le(ZkExperimentalDO::getStartTime, rangeEnd);
        }
        List<ZkExperimentalDO> experiments = zkExperimentalMapper.selectList(wrapper);

        ZkExperimentalAnalysisSummaryRespVO resp = new ZkExperimentalAnalysisSummaryRespVO();
        if (CollUtil.isEmpty(experiments)) {
            resp.setOngoingExperiments(0);
            resp.setAvgWeightGainRate(BigDecimal.ZERO);
            resp.setAvgSurvivalRate(BigDecimal.ZERO);
            resp.setWarningCount(0);
            resp.setGrowthTrend(new ArrayList<>());
            resp.setSurvivalCompare(new ArrayList<>());
            return resp;
        }

        Set<Long> expIds = experiments.stream().map(ZkExperimentalDO::getId).collect(Collectors.toSet());
        List<ZkExperimentalDataDO> dataList = zkExperimentalDataMapper.selectList(
                new LambdaQueryWrapper<ZkExperimentalDataDO>()
                        .in(ZkExperimentalDataDO::getExperimentId, expIds)
                        .eq(ZkExperimentalDataDO::getDeleted, false));

        int ongoing = (int) experiments.stream()
                .filter(e -> e.getExperimentalStatus() != null && e.getExperimentalStatus() == 2)
                .count();

        LocalDateTime now = LocalDateTime.now();
        int warningCount = 0;
        List<ZkExperimentalAnalysisSummaryRespVO.SurvivalCompareItem> survivalCompare = new ArrayList<>();
        Map<String, List<BigDecimal>> trendByDate = new TreeMap<>();

        double weightGainSum = 0;
        int weightGainCount = 0;
        double survivalSum = 0;
        int survivalCount = 0;

        Map<Long, List<ZkExperimentalDataDO>> dataByExp = dataList.stream()
                .collect(Collectors.groupingBy(ZkExperimentalDataDO::getExperimentId));

        for (ZkExperimentalDO exp : experiments) {
            if (exp.getEndTime() != null && exp.getEndTime().isBefore(now)
                    && (exp.getExperimentalStatus() == null || exp.getExperimentalStatus() < 3)) {
                warningCount++;
            }

            List<ZkExperimentalDataDO> expData = dataByExp.getOrDefault(exp.getId(), Collections.emptyList());
            BigDecimal survivalRate = extractSurvivalRate(expData);
            if (survivalRate != null) {
                survivalSum += survivalRate.doubleValue();
                survivalCount++;
                if (survivalRate.compareTo(new BigDecimal("90")) < 0) {
                    warningCount++;
                }
                ZkExperimentalAnalysisSummaryRespVO.SurvivalCompareItem item =
                        new ZkExperimentalAnalysisSummaryRespVO.SurvivalCompareItem();
                item.setExperimentNo(exp.getExperimentNumber());
                item.setRate(survivalRate.setScale(1, RoundingMode.HALF_UP));
                survivalCompare.add(item);
            }

            // 平均增重率：所有生长记录增重率的算术平均（非先按实验再平均）
            for (ZkExperimentalDataDO data : expData) {
                BigDecimal recordRate = extractWeightGainRateOfRecord(data);
                if (recordRate != null) {
                    weightGainSum += recordRate.doubleValue();
                    weightGainCount++;
                }
                appendGrowthTrendPoint(data, trendByDate);
            }
        }

        resp.setOngoingExperiments(ongoing);
        double avgGain = weightGainCount > 0 ? (weightGainSum / weightGainCount) : 0;
        // 表单常存小数比率（0.67），对外统一为百分数
        if (Math.abs(avgGain) <= 1d) {
            avgGain = avgGain * 100d;
        }
        resp.setAvgWeightGainRate(weightGainCount > 0
                ? BigDecimal.valueOf(avgGain).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        resp.setAvgSurvivalRate(survivalCount > 0
                ? BigDecimal.valueOf(survivalSum / survivalCount).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        resp.setWarningCount(warningCount);
        resp.setSurvivalCompare(survivalCompare);

        List<ZkExperimentalAnalysisSummaryRespVO.GrowthTrendItem> growthTrend = new ArrayList<>();
        for (Map.Entry<String, List<BigDecimal>> entry : trendByDate.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0);
            ZkExperimentalAnalysisSummaryRespVO.GrowthTrendItem item =
                    new ZkExperimentalAnalysisSummaryRespVO.GrowthTrendItem();
            item.setDate(entry.getKey());
            item.setWeight(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
            growthTrend.add(item);
        }
        resp.setGrowthTrend(growthTrend);
        return resp;
    }

    private LocalDateTime parseDateStart(String date) {
        if (StrUtil.isBlank(date)) {
            return null;
        }
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseDateEnd(String date) {
        if (StrUtil.isBlank(date)) {
            return null;
        }
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal extractSurvivalRate(List<ZkExperimentalDataDO> dataList) {
        List<BigDecimal> rates = new ArrayList<>();
        for (ZkExperimentalDataDO data : dataList) {
            String type = data.getCollectionType();
            boolean survivalType = type != null && (type.contains("成活") || type.contains("存活")
                    || type.toLowerCase().contains("survival"));
            if (!survivalType) {
                continue;
            }
            BigDecimal val = parseMetricFromCollectData(data.getCollectData(),
                    "survivalRate", "rate", "survival", "成活率", "存活率", "value");
            if (val != null) {
                // 跳过未计算占位：成活率=0 且死亡量=0
                BigDecimal mortality = parseMetricFromCollectData(data.getCollectData(),
                        "mortality", "死亡量");
                if (val.compareTo(BigDecimal.ZERO) == 0
                        && (mortality == null || mortality.compareTo(BigDecimal.ZERO) == 0)) {
                    continue;
                }
                rates.add(val);
            }
        }
        if (rates.isEmpty()) {
            for (ZkExperimentalDataDO data : dataList) {
                BigDecimal val = parseMetricFromCollectData(data.getCollectData(),
                        "survivalRate", "rate", "survival", "成活率", "存活率");
                if (val != null) {
                    BigDecimal mortality = parseMetricFromCollectData(data.getCollectData(),
                            "mortality", "死亡量");
                    if (val.compareTo(BigDecimal.ZERO) == 0
                            && (mortality == null || mortality.compareTo(BigDecimal.ZERO) == 0)) {
                        continue;
                    }
                    rates.add(val);
                }
            }
        }
        if (rates.isEmpty()) {
            return null;
        }
        double avg = rates.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0);
        return BigDecimal.valueOf(avg);
    }

    private BigDecimal extractWeightGainRate(List<ZkExperimentalDataDO> dataList) {
        List<BigDecimal> rates = new ArrayList<>();
        for (ZkExperimentalDataDO data : dataList) {
            BigDecimal val = extractWeightGainRateOfRecord(data);
            if (val != null) {
                rates.add(val);
            }
        }
        if (rates.isEmpty()) {
            return null;
        }
        double avg = rates.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0);
        return BigDecimal.valueOf(avg);
    }

    private BigDecimal extractWeightGainRateOfRecord(ZkExperimentalDataDO data) {
        if (data == null) {
            return null;
        }
        String type = data.getCollectionType();
        if (type == null || !(type.contains("增重") || type.contains("生长")
                || type.toLowerCase().contains("weight"))) {
            return null;
        }
        BigDecimal val = parseMetricFromCollectData(data.getCollectData(),
                "weightGainRate", "gainRate", "增重率", "weightGain", "value");
        if (val == null) {
            BigDecimal end = parseMetricFromCollectData(data.getCollectData(),
                    "endWeight", "finalWeight", "结束重量", "末重");
            BigDecimal start = parseMetricFromCollectData(data.getCollectData(),
                    "startingWeight", "startWeight", "initialWeight", "起始重量", "初始重量", "开始重量");
            if (end != null && start != null && start.compareTo(BigDecimal.ZERO) != 0) {
                val = end.subtract(start).divide(start, 4, RoundingMode.HALF_UP);
            }
        }
        return val;
    }

    private void appendGrowthTrendPoint(ZkExperimentalDataDO data, Map<String, List<BigDecimal>> trendByDate) {
        if (data.getCreateTime() == null) {
            return;
        }
        String type = data.getCollectionType();
        if (type == null || !(type.contains("生长") || type.contains("增重"))) {
            return;
        }
        // 生长趋势：结束重量 - 起始重量
        BigDecimal end = parseMetricFromCollectData(data.getCollectData(),
                "endWeight", "finalWeight", "结束重量", "末重");
        BigDecimal start = parseMetricFromCollectData(data.getCollectData(),
                "startingWeight", "startWeight", "initialWeight", "起始重量", "初始重量", "开始重量");
        BigDecimal gain = null;
        if (end != null && start != null) {
            gain = end.subtract(start);
        }
        if (gain == null) {
            gain = parseMetricFromCollectData(data.getCollectData(),
                    "weight", "avgWeight", "fishWeight", "平均体重", "体重", "value");
        }
        if (gain == null) {
            return;
        }
        String dateKey = data.getCreateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        trendByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(gain);
    }

    /**
     * 解析采集 JSON：兼容扁平对象与字段数组 [{keyEn,keyCh,val}]。
     */
    private BigDecimal parseMetricFromCollectData(String collectData, String... keys) {
        if (StrUtil.isBlank(collectData)) {
            return null;
        }
        String trimmed = collectData.trim();
        if (!JSONUtil.isTypeJSON(trimmed)) {
            try {
                return new BigDecimal(trimmed.replaceAll("[^0-9.-]", ""));
            } catch (Exception ignored) {
                return null;
            }
        }
        // 字段数组格式（前端 DynamicDataForm 入库）
        if (JSONUtil.isTypeJSONArray(trimmed)) {
            cn.hutool.json.JSONArray arr = JSONUtil.parseArray(trimmed);
            for (String key : keys) {
                for (Object o : arr) {
                    if (!(o instanceof JSONObject)) {
                        continue;
                    }
                    JSONObject field = (JSONObject) o;
                    String keyEn = field.getStr("keyEn");
                    String keyCh = field.getStr("keyCh");
                    if (key.equals(keyEn) || (keyCh != null && keyCh.contains(key))) {
                        BigDecimal val = toBigDecimal(field.get("val"));
                        if (val != null) {
                            return val;
                        }
                    }
                }
            }
            return null;
        }
        JSONObject json = JSONUtil.parseObj(trimmed);
        for (String key : keys) {
            if (json.containsKey(key)) {
                return toBigDecimal(json.get(key));
            }
        }
        return null;
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) {
            return null;
        }
        try {
            return new BigDecimal(val.toString());
        } catch (Exception e) {
            return null;
        }
    }

}