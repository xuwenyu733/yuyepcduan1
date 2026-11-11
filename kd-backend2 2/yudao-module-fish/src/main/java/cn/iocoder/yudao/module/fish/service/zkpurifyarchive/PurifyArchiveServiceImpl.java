package cn.iocoder.yudao.module.fish.service.zkpurifyarchive;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo.PurifyArchivePageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo.PurifyArchiveRespVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo.PurifyArchiveSaveReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo.PurifyStatsRespVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyarchive.PurifyArchiveDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypool.PurifyPoolDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyoutpool.PurifyOutPoolDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifycostdetail.PurifyCostDetailDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyarchive.PurifyArchiveMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifypool.PurifyPoolMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyprocess.PurifyProcessMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyoutpool.PurifyOutPoolMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifycostdetail.PurifyCostDetailMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifypurchase.PurifyPurchaseMapper;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypurchase.PurifyPurchaseDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifydailymonitoring.PurifyDailyMonitoringMapper;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydailymonitoring.PurifyDailyMonitoringDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 入池档案 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class PurifyArchiveServiceImpl implements PurifyArchiveService {

    @Resource
    private PurifyArchiveMapper purifyArchiveMapper;
    @Resource
    private PurifyProcessMapper purifyProcessMapper;
    @Resource
    private PurifyPoolMapper purifyPoolMapper;
    @Resource
    private PurifyOutPoolMapper purifyOutPoolMapper;
    @Resource
    private PurifyCostDetailMapper purifyCostDetailMapper;
    @Resource
    private PurifyDailyMonitoringMapper purifyDailyMonitoringMapper;
    @Resource
    private PurifyPurchaseMapper purifyPurchaseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurifyArchive(PurifyArchiveSaveReqVO createReqVO) {
        // 插入
        PurifyArchiveDO purifyArchive = BeanUtils.toBean(createReqVO, PurifyArchiveDO.class);
        purifyArchiveMapper.insert(purifyArchive);

        // 2. 更新鱼池状态为使用中
        if (StrUtil.isNotEmpty(purifyArchive.getPoolNumber())) {
            PurifyPoolDO pool = purifyPoolMapper.selectOne(PurifyPoolDO::getPoolNumber, purifyArchive.getPoolNumber());
            if (pool != null) {
                purifyPoolMapper.updateById(PurifyPoolDO.builder().id(pool.getId()).status("in_use").build());
            }
        }

        // 3. 自动创建优化流程记录
        PurifyProcessDO process = new PurifyProcessDO();
        process.setArchiveId(purifyArchive.getId());
        process.setRecordNumber("REC" + System.currentTimeMillis());
        process.setStatus("farming");
        process.setStartTime(purifyArchive.getInPoolTime());
        process.setOperator(purifyArchive.getAssessor()); // 默认使用评估人为负责人
        process.setTotalDeathCount(0);
        process.setTotalDeathWeight(BigDecimal.ZERO);
        process.setTotalOutCount(0);
        process.setTotalOutWeight(BigDecimal.ZERO);
        process.setCurrentStockCount(purifyArchive.getInPoolCount());
        process.setDeptId(purifyArchive.getDeptId());
        purifyProcessMapper.insert(process);

        // 返回
        return purifyArchive.getId();
    }

    @Override
    public void updatePurifyArchive(PurifyArchiveSaveReqVO updateReqVO) {
        // 校验存在
        validatePurifyArchiveExists(updateReqVO.getId());
        // 更新
        PurifyArchiveDO updateObj = BeanUtils.toBean(updateReqVO, PurifyArchiveDO.class);
        purifyArchiveMapper.updateById(updateObj);
    }

    @Override
    public void deletePurifyArchive(Long id) {
        // 校验存在
        validatePurifyArchiveExists(id);
        // 删除
        purifyArchiveMapper.deleteById(id);
    }

    @Override
    public void deletePurifyArchiveListByIds(List<Long> ids) {
        // 删除
        purifyArchiveMapper.deleteByIds(ids);
    }

    private void validatePurifyArchiveExists(Long id) {
        if (purifyArchiveMapper.selectById(id) == null) {
            throw exception(PURIFY_ARCHIVE_NOT_EXISTS);
        }
    }

    @Override
    public PurifyArchiveDO getPurifyArchive(Long id) {
        return purifyArchiveMapper.selectById(id);
    }

    @Override
    public PageResult<PurifyArchiveDO> getPurifyArchivePage(PurifyArchivePageReqVO pageReqVO) {
        return purifyArchiveMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PurifyArchiveDO> getByIds(Set<Long> achrivedIdSet) {
        if(CollectionUtils.isAnyEmpty(achrivedIdSet)) return new ArrayList<>();
        LambdaQueryWrapper<PurifyArchiveDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PurifyArchiveDO::getId, achrivedIdSet);
        return purifyArchiveMapper.selectList(queryWrapper);
    }

    @Override
    public PurifyStatsRespVO getPurifyStats(PurifyArchivePageReqVO pageReqVO) {
        PurifyStatsRespVO stats = new PurifyStatsRespVO();
        // 初始化默认值
        stats.setTotalInWeight(BigDecimal.ZERO);
        stats.setTotalOutWeight(BigDecimal.ZERO);
        stats.setTotalDeathCount(0);
        stats.setTotalDeathWeight(BigDecimal.ZERO);
        stats.setTotalWeightLoss(BigDecimal.ZERO);
        stats.setDeathRate(BigDecimal.ZERO);
        stats.setWeightLossRate(BigDecimal.ZERO);
        stats.setTotalSalesAmount(BigDecimal.ZERO);
        stats.setTotalProfit(BigDecimal.ZERO);
        stats.setProfitMargin(BigDecimal.ZERO);
        stats.setTotalFeedAmount(BigDecimal.ZERO);
        stats.setAveragePrice(BigDecimal.ZERO);
        stats.setTotalPurchaseCost(BigDecimal.ZERO);
        stats.setTotalLaborCost(BigDecimal.ZERO);
        stats.setTotalElectricityCost(BigDecimal.ZERO);
        stats.setTotalFeedCost(BigDecimal.ZERO);
        stats.setTotalHealthProductCost(BigDecimal.ZERO);
        stats.setTotalCost(BigDecimal.ZERO);
        stats.setCategoryDistribution(new HashMap<>());
        stats.setSpecComparison(new HashMap<>());
        stats.setMortalityTrend(new ArrayList<>());
        stats.setStageMortalityAnalysis(new ArrayList<>());
        stats.setBatchScatterData(new ArrayList<>());
        
        // 1. 获取过滤后的档案和流程
        LambdaQueryWrapperX<PurifyArchiveDO> archiveWrapper = new LambdaQueryWrapperX<PurifyArchiveDO>()
                .eqIfPresent(PurifyArchiveDO::getCategory, pageReqVO.getCategory())
                .eqIfPresent(PurifyArchiveDO::getPoolNumber, pageReqVO.getPoolNumber());
        
        // 处理时间范围
        if (StringUtil.isNotEmptyWith0(pageReqVO.getTimeRange())) {
            int days = Integer.parseInt(pageReqVO.getTimeRange());
            LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
            archiveWrapper.ge(PurifyArchiveDO::getInPoolTime, cutoff);
        }

        List<PurifyArchiveDO> archives = purifyArchiveMapper.selectList(archiveWrapper);
        
        Set<Long> archiveIds = archives.stream().map(PurifyArchiveDO::getId).collect(Collectors.toSet());
        if (archiveIds.isEmpty()) return stats;

        // 2. 获取过滤后的流程
        LambdaQueryWrapperX<PurifyProcessDO> processWrapper = new LambdaQueryWrapperX<PurifyProcessDO>()
                .in(PurifyProcessDO::getArchiveId, archiveIds);
        
        if (StrUtil.isNotEmpty(pageReqVO.getRecordNumber())) {
            processWrapper.like(PurifyProcessDO::getRecordNumber, pageReqVO.getRecordNumber());
        }
        if (StrUtil.isNotEmpty(pageReqVO.getStatus())) {
            processWrapper.eq(PurifyProcessDO::getStatus, pageReqVO.getStatus());
        }
        
        List<PurifyProcessDO> processes = purifyProcessMapper.selectList(processWrapper);
        
        Set<Long> processIds = processes.stream().map(PurifyProcessDO::getId).collect(Collectors.toSet());
        if (processIds.isEmpty()) return stats;

        // 重新过滤 archives，只保留有对应 process 的
        Set<Long> validArchiveIds = processes.stream().map(PurifyProcessDO::getArchiveId).collect(Collectors.toSet());
        archives = archives.stream().filter(a -> validArchiveIds.contains(a.getId())).collect(Collectors.toList());

        List<PurifyOutPoolDO> outPools = purifyOutPoolMapper.selectList(new LambdaQueryWrapper<PurifyOutPoolDO>()
                .in(PurifyOutPoolDO::getProcessId, processIds));
        List<PurifyDailyMonitoringDO> monitorings = purifyDailyMonitoringMapper.selectList(new LambdaQueryWrapper<PurifyDailyMonitoringDO>()
                .in(PurifyDailyMonitoringDO::getProcessId, processIds));
        List<PurifyCostDetailDO> costs = purifyCostDetailMapper.selectList(new LambdaQueryWrapper<PurifyCostDetailDO>()
                .in(PurifyCostDetailDO::getProcessId, processIds));

        // 2. 基本指标计算
        BigDecimal totalInWeight = archives.stream()
                .map(PurifyArchiveDO::getInPoolWeight)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalOutWeight = outPools.stream()
                .map(PurifyOutPoolDO::getOutPoolWeight)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalInCount = archives.stream()
                .mapToInt(a -> a.getInPoolCount() != null ? a.getInPoolCount() : 0)
                .sum();

        int totalDeathCount = processes.stream()
                .mapToInt(p -> p.getTotalDeathCount() != null ? p.getTotalDeathCount() : 0)
                .sum();
        
        BigDecimal totalDeathWeight = processes.stream()
                .map(p -> p.getTotalDeathWeight() != null ? p.getTotalDeathWeight() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSalesAmount = outPools.stream()
                .map(PurifyOutPoolDO::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 成本计算 - 关联采购记录获取真实单价
        Set<Long> purchaseIds = archives.stream()
                .map(PurifyArchiveDO::getPurchaseRecordId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, BigDecimal> purchasePriceMap = new HashMap<>();
        if (!purchaseIds.isEmpty()) {
            List<PurifyPurchaseDO> purchases = purifyPurchaseMapper.selectBatchIds(purchaseIds);
            purchasePriceMap = purchases.stream()
                    .collect(Collectors.toMap(PurifyPurchaseDO::getId, p -> p.getUnitPrice() != null ? p.getUnitPrice() : BigDecimal.ZERO));
        }

        Map<Long, BigDecimal> finalPurchasePriceMap = purchasePriceMap;
        BigDecimal totalPurchaseCost = archives.stream()
                .map(a -> {
                    BigDecimal weight = a.getInPoolWeight() != null ? a.getInPoolWeight() : BigDecimal.ZERO;
                    BigDecimal price = BigDecimal.ZERO;
                    if (a.getPurchaseRecordId() != null && finalPurchasePriceMap.containsKey(a.getPurchaseRecordId())) {
                        price = finalPurchasePriceMap.get(a.getPurchaseRecordId());
                    } else {
                        // 兜底逻辑：如果没有关联采购单，尝试给一个默认参考价或者不计算
                        price = new BigDecimal("20"); 
                    }
                    return weight.multiply(price);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLaborCost = costs.stream().filter(c -> "labor".equals(c.getCostType())).map(PurifyCostDetailDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalElectricityCost = costs.stream().filter(c -> "electricity".equals(c.getCostType())).map(PurifyCostDetailDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFeedCost = costs.stream().filter(c -> "feed".equals(c.getCostType())).map(PurifyCostDetailDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHealthProductCost = costs.stream().filter(c -> "health".equals(c.getCostType())).map(PurifyCostDetailDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCost = totalPurchaseCost.add(totalLaborCost).add(totalElectricityCost).add(totalFeedCost).add(totalHealthProductCost);

        stats.setTotalInWeight(totalInWeight);
        stats.setTotalOutWeight(totalOutWeight);
        stats.setTotalDeathCount(totalDeathCount);
        stats.setTotalDeathWeight(totalDeathWeight);
        stats.setTotalSalesAmount(totalSalesAmount);
        stats.setTotalPurchaseCost(totalPurchaseCost);
        stats.setTotalLaborCost(totalLaborCost);
        stats.setTotalElectricityCost(totalElectricityCost);
        stats.setTotalFeedCost(totalFeedCost);
        stats.setTotalHealthProductCost(totalHealthProductCost);
        stats.setTotalCost(totalCost);
        
        // 掉秤 (只计算已出池的或者已完成的)
        // 修正逻辑：针对每个流程单独计算掉秤，然后累加
        BigDecimal totalWeightLoss = BigDecimal.ZERO;
        for (PurifyProcessDO p : processes) {
            PurifyArchiveDO a = archives.stream().filter(ar -> ar.getId().equals(p.getArchiveId())).findFirst().orElse(null);
            if (a == null || a.getInPoolWeight() == null) continue;
            
            // 如果流程未结束且没有完全出池，很难精确计算掉秤。
            // 这里遵循：掉秤 = 入池 - (出池 + 死亡 + 存塘估算)。
            // 如果存塘估算未知，则对于 'farming' 状态暂不计算掉秤，或者认为掉秤为 0（除非有中间称重）。
            // 简单起见，且为了数据展示，我们只计算 'out_pool' 状态或者 scaleDropRate > 0 的。
            
            BigDecimal pIn = a.getInPoolWeight();
            BigDecimal pOut = p.getTotalOutWeight() != null ? p.getTotalOutWeight() : BigDecimal.ZERO;
            BigDecimal pDeath = p.getTotalDeathWeight() != null ? p.getTotalDeathWeight() : BigDecimal.ZERO;
            
            // 如果已完成，则差值即为掉秤
            if ("out_pool".equals(p.getStatus()) || (p.getCurrentStockCount() != null && p.getCurrentStockCount() <= 0)) {
                totalWeightLoss = totalWeightLoss.add(pIn.subtract(pOut).subtract(pDeath).max(BigDecimal.ZERO));
            }
        }
        stats.setTotalWeightLoss(totalWeightLoss);

        // 死亡率
        if (totalInCount > 0) {
            stats.setDeathRate(BigDecimal.valueOf(totalDeathCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalInCount), 2, RoundingMode.HALF_UP));
        } else {
            stats.setDeathRate(BigDecimal.ZERO);
        }

        // 掉秤率
        if (totalInWeight.compareTo(BigDecimal.ZERO) > 0) {
            stats.setWeightLossRate(stats.getTotalWeightLoss().multiply(BigDecimal.valueOf(100))
                    .divide(totalInWeight, 2, RoundingMode.HALF_UP));
        } else {
            stats.setWeightLossRate(BigDecimal.ZERO);
        }

        // 利润 & 利润率
        BigDecimal totalProfit = totalSalesAmount.subtract(totalCost);
        stats.setTotalProfit(totalProfit);
        if (totalSalesAmount.compareTo(BigDecimal.ZERO) > 0) {
            stats.setProfitMargin(totalProfit.multiply(BigDecimal.valueOf(100))
                    .divide(totalSalesAmount, 2, RoundingMode.HALF_UP));
        } else {
            stats.setProfitMargin(BigDecimal.ZERO);
        }

        // 投喂量
        stats.setTotalFeedAmount(monitorings.stream().map(m -> m.getFeedAmount() != null ? m.getFeedAmount() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add));
        
        // 平均售价
        if (totalOutWeight.compareTo(BigDecimal.ZERO) > 0) {
            stats.setAveragePrice(totalSalesAmount.divide(totalOutWeight, 2, RoundingMode.HALF_UP));
        } else {
            stats.setAveragePrice(BigDecimal.ZERO);
        }

        // 3. 图表数据
        // 品类分布
        Map<String, Long> categoryDist = archives.stream()
                .collect(Collectors.groupingBy(PurifyArchiveDO::getCategory, Collectors.counting()));
        stats.setCategoryDistribution(categoryDist);

        // 规格对比
        Map<String, List<Integer>> specComp = new HashMap<>();
        archives.forEach(a -> {
            String spec = getSpecByWeight(a.getAvgWeight());
            List<Integer> counts = specComp.computeIfAbsent(spec, k -> new ArrayList<>(Arrays.asList(0, 0)));
            counts.set(0, counts.get(0) + (a.getInPoolCount() != null ? a.getInPoolCount() : 0));
        });
        outPools.forEach(o -> {
            String spec = o.getSpecification();
            if (spec == null) spec = "未知";
            List<Integer> counts = specComp.computeIfAbsent(spec, k -> new ArrayList<>(Arrays.asList(0, 0)));
            counts.set(1, counts.get(1) + (o.getOutPoolCount() != null ? o.getOutPoolCount() : 0));
        });
        stats.setSpecComparison(specComp);

        // 4. 阶段性死亡率分析
        List<PurifyStatsRespVO.StageMortalityVO> stageAnalysis = new ArrayList<>();
        List<PurifyArchiveDO> finalArchives1 = archives;
        processes.forEach(p -> {
            PurifyArchiveDO a = finalArchives1.stream().filter(ar -> ar.getId().equals(p.getArchiveId())).findFirst().orElse(null);
            if (a == null) return;

            PurifyStatsRespVO.StageMortalityVO vo = new PurifyStatsRespVO.StageMortalityVO();
            vo.setRecordNumber(p.getRecordNumber());
            vo.setCategory(a.getCategory());
            vo.setPoolNumber(a.getPoolNumber());
            vo.setInPoolCount(a.getInPoolCount());
            vo.setInPoolTime(a.getInPoolTime());

            List<PurifyDailyMonitoringDO> pMonitorings = monitorings.stream()
                    .filter(m -> m.getProcessId().equals(p.getId()))
                    .sorted(Comparator.comparing(PurifyDailyMonitoringDO::getMonitorDate))
                    .collect(Collectors.toList());

            int d3 = 0, d5 = 0, d7 = 0, totalD = 0;
            for (PurifyDailyMonitoringDO m : pMonitorings) {
                if (m.getMonitorDate() == null) continue;
                long days = java.time.Duration.between(a.getInPoolTime(), m.getMonitorDate()).toDays();
                int dc = m.getDeathCount() != null ? m.getDeathCount() : 0;
                totalD += dc;
                if (days <= 3) d3 += dc;
                if (days <= 5) d5 += dc;
                if (days <= 7) d7 += dc;
            }
            vo.setDay3Deaths(d3);
            vo.setDay5Deaths(d5);
            vo.setDay7Deaths(d7);
            vo.setTotalDeaths(totalD);
            if (a.getInPoolCount() != null && a.getInPoolCount() > 0) {
                vo.setDay3Rate(BigDecimal.valueOf(d3).multiply(new BigDecimal("100")).divide(BigDecimal.valueOf(a.getInPoolCount()), 2, RoundingMode.HALF_UP));
                vo.setDay5Rate(BigDecimal.valueOf(d5).multiply(new BigDecimal("100")).divide(BigDecimal.valueOf(a.getInPoolCount()), 2, RoundingMode.HALF_UP));
                vo.setDay7Rate(BigDecimal.valueOf(d7).multiply(new BigDecimal("100")).divide(BigDecimal.valueOf(a.getInPoolCount()), 2, RoundingMode.HALF_UP));
                vo.setTotalRate(BigDecimal.valueOf(totalD).multiply(new BigDecimal("100")).divide(BigDecimal.valueOf(a.getInPoolCount()), 2, RoundingMode.HALF_UP));
            }
            stageAnalysis.add(vo);
        });
        stats.setStageMortalityAnalysis(stageAnalysis);

        // 5. 散点图数据
        List<PurifyStatsRespVO.BatchScatterVO> scatterData = new ArrayList<>();
        List<PurifyArchiveDO> finalArchives = archives;
        processes.forEach(p -> {
            PurifyArchiveDO a = finalArchives.stream().filter(ar -> ar.getId().equals(p.getArchiveId())).findFirst().orElse(null);
            if (a == null) return;
            PurifyStatsRespVO.BatchScatterVO vo = new PurifyStatsRespVO.BatchScatterVO();
            vo.setRecordNumber(p.getRecordNumber());
            vo.setCategory(a.getCategory());
            vo.setMortalityRate(p.getMortalityRate());
            vo.setWeightLossRate(p.getScaleDropRate());
            vo.setInPoolWeight(a.getInPoolWeight());
            scatterData.add(vo);
        });
        stats.setBatchScatterData(scatterData);

        return stats;
    }

    private String getSpecByWeight(BigDecimal weight) {
        if (weight == null) return "未知";
        double w = weight.doubleValue();
        if (w < 0.3) return "<0.3kg/尾";
        if (w < 0.5) return "0.3-0.5kg/尾";
        if (w < 0.8) return "0.5-0.8kg/尾";
        if (w < 1.2) return "0.8-1.2kg/尾";
        if (w < 1.5) return "1.2-1.5kg/尾";
        return "1.5kg以上/尾";
    }

    @Override
    public PageResult<PurifyArchiveRespVO> getRecordQueryPage(PurifyArchivePageReqVO pageReqVO) {
        // 1. 查询入池档案分页
        // PageResult<PurifyArchiveDO> archivePage = purifyArchiveMapper.selectPage(pageReqVO);
        QueryWrapper<PurifyArchiveDO> queryWrapper = new QueryWrapper<>();
        Page<PurifyArchiveDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        IPage<PurifyArchiveDO> purifyArchiveDOIPage = purifyArchiveMapper.customPage(page, enhancerQuery(queryWrapper, pageReqVO));

        if (CollectionUtils.isAnyEmpty(purifyArchiveDOIPage.getRecords())) {
            return new PageResult<>(new ArrayList<>(), purifyArchiveDOIPage.getTotal());
        }

        // 2. 转换 VO
        List<PurifyArchiveRespVO> respList = BeanUtils.toBean(purifyArchiveDOIPage.getRecords(), PurifyArchiveRespVO.class);

        // 3. 关联查询流程信息
        Set<Long> archiveIds = purifyArchiveDOIPage.getRecords().stream().map(PurifyArchiveDO::getId).collect(Collectors.toSet());
        List<PurifyProcessDO> processList = purifyProcessMapper.selectList(new LambdaQueryWrapper<PurifyProcessDO>()
                .in(PurifyProcessDO::getArchiveId, archiveIds));
        Map<Long, PurifyProcessDO> processMap = processList.stream()
                .collect(Collectors.toMap(PurifyProcessDO::getArchiveId, p -> p, (p1, p2) -> p1));

        // 4. 填充扩展字段
        respList.forEach(resp -> {
            PurifyProcessDO process = processMap.get(resp.getId());
            if (process != null) {
                resp.setRecordNumber(process.getRecordNumber());
                resp.setStatus(process.getStatus());
                resp.setTotalDeathCount(process.getTotalDeathCount());
                resp.setTotalDeathWeight(process.getTotalDeathWeight());
                resp.setTotalOutCount(process.getTotalOutCount());
                resp.setTotalOutWeight(process.getTotalOutWeight());
                resp.setCurrentStockCount(process.getCurrentStockCount());

                // 直接从流程记录中获取预计算的死亡率和掉秤率
                resp.setDeathRate(process.getMortalityRate() != null ? process.getMortalityRate() : BigDecimal.ZERO);
                resp.setWeightLossRate(process.getScaleDropRate() != null ? process.getScaleDropRate() : BigDecimal.ZERO);
            }
        });

        // 5. 根据流程单号或状态过滤（如果 Mapper 没过滤的话，这里做内存过滤，但最好是 Mapper 过滤）
        // 如果 pageReqVO 中的 recordNumber 或 status 有值，且 Mapper 没处理，这里需要处理
        // 注意：MyBatis Plus 的 selectPage 默认只处理了 PurifyArchiveDO 的字段

        return new PageResult<>(respList, purifyArchiveDOIPage.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAllProcessStats() {
        List<PurifyProcessDO> processList = purifyProcessMapper.selectList();
        for (PurifyProcessDO process : processList) {
            syncProcessStats(process.getId());
        }
    }

    private void syncProcessStats(Long processId) {
        PurifyProcessDO process = purifyProcessMapper.selectById(processId);
        if (process == null) return;

        PurifyArchiveDO archive = purifyArchiveMapper.selectById(process.getArchiveId());
        if (archive == null) return;

        // 1. 计算累计死亡
        List<PurifyDailyMonitoringDO> monitoringList = purifyDailyMonitoringMapper.selectList(
                new LambdaQueryWrapper<PurifyDailyMonitoringDO>().eq(PurifyDailyMonitoringDO::getProcessId, processId));
        int totalDeathCount = monitoringList.stream().mapToInt(m -> m.getDeathCount() == null ? 0 : m.getDeathCount()).sum();
        BigDecimal totalDeathWeight = monitoringList.stream()
                .map(m -> m.getDeathWeight() == null ? BigDecimal.ZERO : m.getDeathWeight())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. 计算累计出池
        List<PurifyOutPoolDO> outPoolList = purifyOutPoolMapper.selectList(
                new LambdaQueryWrapper<PurifyOutPoolDO>().eq(PurifyOutPoolDO::getProcessId, processId));
        int totalOutCount = outPoolList.stream().mapToInt(o -> o.getOutPoolCount() == null ? 0 : o.getOutPoolCount()).sum();
        BigDecimal totalOutWeight = outPoolList.stream()
                .map(o -> o.getOutPoolWeight() == null ? BigDecimal.ZERO : o.getOutPoolWeight())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 计算当前存塘
        int initialCount = archive.getInPoolCount() == null ? 0 : archive.getInPoolCount();
        int currentStockCount = initialCount - totalDeathCount - totalOutCount;

        // 4. 计算死亡率 (累计死亡数量 / 入池数量)
        BigDecimal mortalityRate = BigDecimal.ZERO;
        if (initialCount > 0) {
            mortalityRate = BigDecimal.valueOf(totalDeathCount).multiply(new BigDecimal("100"))
                    .divide(BigDecimal.valueOf(initialCount), 2, RoundingMode.HALF_UP);
        }

        // 5. 计算掉秤率 ((入池重量 - 累计出池重量 - 累计死亡重量) / 入池重量)
        BigDecimal initialWeight = archive.getInPoolWeight() == null ? BigDecimal.ZERO : archive.getInPoolWeight();
        BigDecimal scaleDropRate = BigDecimal.ZERO;
        if (initialWeight.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal weightLoss = initialWeight.subtract(totalOutWeight).subtract(totalDeathWeight);
            scaleDropRate = weightLoss.multiply(new BigDecimal("100"))
                    .divide(initialWeight, 2, RoundingMode.HALF_UP);
        }

        String newStatus = currentStockCount <= 0 ? "out_pool" : "farming";

        // 6. 更新流程表
        purifyProcessMapper.updateById(PurifyProcessDO.builder()
                .id(processId)
                .totalDeathCount(totalDeathCount)
                .totalDeathWeight(totalDeathWeight)
                .totalOutCount(totalOutCount)
                .totalOutWeight(totalOutWeight)
                .currentStockCount(Math.max(0, currentStockCount))
                .mortalityRate(mortalityRate)
                .scaleDropRate(scaleDropRate)
                .status(newStatus)
                .build());

        // 7. 同步更新鱼池状态
        if ("out_pool".equals(newStatus)) {
            if (StrUtil.isNotEmpty(archive.getPoolNumber())) {
                PurifyPoolDO pool = purifyPoolMapper.selectOne(PurifyPoolDO::getPoolNumber, archive.getPoolNumber());
                if (pool != null) {
                    purifyPoolMapper.updateById(PurifyPoolDO.builder().id(pool.getId()).status("available").build());
                }
            }
        } else {
            if (StrUtil.isNotEmpty(archive.getPoolNumber())) {
                PurifyPoolDO pool = purifyPoolMapper.selectOne(PurifyPoolDO::getPoolNumber, archive.getPoolNumber());
                if (pool != null && "available".equals(pool.getStatus())) {
                    purifyPoolMapper.updateById(PurifyPoolDO.builder().id(pool.getId()).status("in_use").build());
                }
            }
        }
    }

    public QueryWrapper<PurifyArchiveDO> enhancerQuery(QueryWrapper<PurifyArchiveDO> queryWrapper, PurifyArchivePageReqVO pageReqVO) {
        if(StringUtil.isNotEmptyWith0(pageReqVO.getRecordNumber())) {
            queryWrapper.like("zpp.record_number", pageReqVO.getRecordNumber());
        }
        if(StringUtil.isNotEmptyWith0(pageReqVO.getCategory())) {
            queryWrapper.eq("zpa.category", pageReqVO.getCategory());
        }
        if(StringUtil.isNotEmptyWith0(pageReqVO.getPoolNumber())) {
            queryWrapper.like("zpa.pool_number", pageReqVO.getPoolNumber());
        }
        if(StringUtil.isNotEmptyWith0(pageReqVO.getStatus())) {
            queryWrapper.eq("zpp.status", pageReqVO.getStatus());
        }
        if(pageReqVO.getMinMortalityRate() != null) {
            queryWrapper.ge("zpp.mortality_rate", pageReqVO.getMinMortalityRate());
        }
        if(pageReqVO.getMaxMortalityRate() != null) {
            queryWrapper.le("zpp.mortality_rate", pageReqVO.getMaxMortalityRate());
        }
        if(pageReqVO.getMinScaleDropRate() != null) {
            queryWrapper.ge("zpp.scale_drop_rate", pageReqVO.getMinScaleDropRate());
        }
        if(pageReqVO.getMaxScaleDropRate() != null) {
            queryWrapper.le("zpp.scale_drop_rate", pageReqVO.getMaxScaleDropRate());
        }
        return queryWrapper;
    }
}
