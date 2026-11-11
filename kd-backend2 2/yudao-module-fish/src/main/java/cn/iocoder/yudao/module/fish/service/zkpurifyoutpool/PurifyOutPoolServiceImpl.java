package cn.iocoder.yudao.module.fish.service.zkpurifyoutpool;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool.vo.PurifyOutPoolPageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool.vo.PurifyOutPoolSaveReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyarchive.PurifyArchiveDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydailymonitoring.PurifyDailyMonitoringDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypool.PurifyPoolDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyoutpool.PurifyOutPoolDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyarchive.PurifyArchiveMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifypool.PurifyPoolMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyoutpool.PurifyOutPoolMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifydailymonitoring.PurifyDailyMonitoringMapper;
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
 * 出池登记 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class PurifyOutPoolServiceImpl implements PurifyOutPoolService {

    @Resource
    private PurifyOutPoolMapper purifyOutPoolMapper;
    @Resource
    private PurifyProcessMapper purifyProcessMapper;
    @Resource
    private PurifyPoolMapper purifyPoolMapper;
    @Resource
    private PurifyArchiveMapper purifyArchiveMapper;
    @Resource
    private PurifyDailyMonitoringMapper purifyDailyMonitoringMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurifyOutPool(PurifyOutPoolSaveReqVO createReqVO) {
        // 插入
        PurifyOutPoolDO purifyOutPool = BeanUtils.toBean(createReqVO, PurifyOutPoolDO.class);
        purifyOutPoolMapper.insert(purifyOutPool);

        // 更新流程统计数据
        syncProcessStats(purifyOutPool.getProcessId());

        // 返回
        return purifyOutPool.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurifyOutPool(PurifyOutPoolSaveReqVO updateReqVO) {
        // 校验存在
        validatePurifyOutPoolExists(updateReqVO.getId());
        // 更新
        PurifyOutPoolDO updateObj = BeanUtils.toBean(updateReqVO, PurifyOutPoolDO.class);
        purifyOutPoolMapper.updateById(updateObj);

        // 更新流程统计数据
        syncProcessStats(updateObj.getProcessId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurifyOutPool(Long id) {
        // 校验存在
        PurifyOutPoolDO outPool = purifyOutPoolMapper.selectById(id);
        if (outPool == null) {
            throw exception(PURIFY_OUT_POOL_NOT_EXISTS);
        }
        // 删除
        purifyOutPoolMapper.deleteById(id);

        // 更新流程统计数据
        syncProcessStats(outPool.getProcessId());
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

        // 如果全部出池，更新鱼池状态为空闲
        if ("out_pool".equals(newStatus)) {
            if (StrUtil.isNotEmpty(archive.getPoolNumber())) {
                PurifyPoolDO pool = purifyPoolMapper.selectOne(PurifyPoolDO::getPoolNumber, archive.getPoolNumber());
                if (pool != null) {
                    purifyPoolMapper.updateById(PurifyPoolDO.builder().id(pool.getId()).status("available").build());
                }
            }
        } else {
            // 如果还有存塘，且鱼池状态为 available，则改回 in_use
            if (StrUtil.isNotEmpty(archive.getPoolNumber())) {
                PurifyPoolDO pool = purifyPoolMapper.selectOne(PurifyPoolDO::getPoolNumber, archive.getPoolNumber());
                if (pool != null && "available".equals(pool.getStatus())) {
                    purifyPoolMapper.updateById(PurifyPoolDO.builder().id(pool.getId()).status("in_use").build());
                }
            }
        }
    }

    @Override
        public void deletePurifyOutPoolListByIds(List<Long> ids) {
        // 删除
        purifyOutPoolMapper.deleteByIds(ids);
        }


    private void validatePurifyOutPoolExists(Long id) {
        if (purifyOutPoolMapper.selectById(id) == null) {
            throw exception(PURIFY_OUT_POOL_NOT_EXISTS);
        }
    }

    @Override
    public PurifyOutPoolDO getPurifyOutPool(Long id) {
        return purifyOutPoolMapper.selectById(id);
    }

    @Override
    public PageResult<PurifyOutPoolDO> getPurifyOutPoolPage(PurifyOutPoolPageReqVO pageReqVO) {
        return purifyOutPoolMapper.selectPage(pageReqVO);
    }

}