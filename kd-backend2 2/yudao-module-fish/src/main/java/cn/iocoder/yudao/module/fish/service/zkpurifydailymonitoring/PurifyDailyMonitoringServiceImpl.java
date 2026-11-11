package cn.iocoder.yudao.module.fish.service.zkpurifydailymonitoring;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring.vo.PurifyDailyMonitoringPageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring.vo.PurifyDailyMonitoringSaveReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyarchive.PurifyArchiveDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydailymonitoring.PurifyDailyMonitoringDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyoutpool.PurifyOutPoolDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyarchive.PurifyArchiveMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifydailymonitoring.PurifyDailyMonitoringMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyoutpool.PurifyOutPoolMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyprocess.PurifyProcessMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 日常监测记录 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class PurifyDailyMonitoringServiceImpl implements PurifyDailyMonitoringService {

    @Resource
    private PurifyDailyMonitoringMapper purifyDailyMonitoringMapper;
    @Resource
    private PurifyProcessMapper purifyProcessMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurifyDailyMonitoring(PurifyDailyMonitoringSaveReqVO createReqVO) {
        // 插入
        PurifyDailyMonitoringDO purifyDailyMonitoring = BeanUtils.toBean(createReqVO, PurifyDailyMonitoringDO.class);
        purifyDailyMonitoringMapper.insert(purifyDailyMonitoring);

        // 更新流程统计数据
        syncProcessStats(purifyDailyMonitoring.getProcessId());

        // 返回
        return purifyDailyMonitoring.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurifyDailyMonitoring(PurifyDailyMonitoringSaveReqVO updateReqVO) {
        // 校验存在
        validatePurifyDailyMonitoringExists(updateReqVO.getId());
        // 更新
        PurifyDailyMonitoringDO updateObj = BeanUtils.toBean(updateReqVO, PurifyDailyMonitoringDO.class);
        purifyDailyMonitoringMapper.updateById(updateObj);

        // 更新流程统计数据
        syncProcessStats(updateObj.getProcessId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurifyDailyMonitoring(Long id) {
        // 校验存在
        PurifyDailyMonitoringDO monitoring = purifyDailyMonitoringMapper.selectById(id);
        if (monitoring == null) {
            throw exception(PURIFY_DAILY_MONITORING_NOT_EXISTS);
        }
        // 删除
        purifyDailyMonitoringMapper.deleteById(id);

        // 更新流程统计数据
        syncProcessStats(monitoring.getProcessId());
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
                .status(currentStockCount <= 0 ? "out_pool" : "farming")
                .build());
    }

    @Resource
    private PurifyArchiveMapper purifyArchiveMapper;
    @Resource
    private PurifyOutPoolMapper purifyOutPoolMapper;

    @Override
    public void deletePurifyDailyMonitoringListByIds(List<Long> ids) {
        // 删除
        purifyDailyMonitoringMapper.deleteByIds(ids);
        }


    private void validatePurifyDailyMonitoringExists(Long id) {
        if (purifyDailyMonitoringMapper.selectById(id) == null) {
            throw exception(PURIFY_DAILY_MONITORING_NOT_EXISTS);
        }
    }

    @Override
    public PurifyDailyMonitoringDO getPurifyDailyMonitoring(Long id) {
        return purifyDailyMonitoringMapper.selectById(id);
    }

    @Override
    public PageResult<PurifyDailyMonitoringDO> getPurifyDailyMonitoringPage(PurifyDailyMonitoringPageReqVO pageReqVO) {
        return purifyDailyMonitoringMapper.selectPage(pageReqVO);
    }

}