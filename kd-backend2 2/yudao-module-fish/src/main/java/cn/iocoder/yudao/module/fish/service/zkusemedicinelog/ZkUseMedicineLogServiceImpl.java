package cn.iocoder.yudao.module.fish.service.zkusemedicinelog;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicine.ZkUseMedicineDO;
import cn.iocoder.yudao.module.fish.service.zkbiologycapital.ZkBiologyCapitalService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkusemedicinelog.ZkUseMedicineLogConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkusemedicinelog.ZkUseMedicineLogMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 用药明细 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkUseMedicineLogServiceImpl implements ZkUseMedicineLogService {

    @Resource
    private ZkUseMedicineLogMapper zkUseMedicineLogMapper;
    @Resource
    private ZkBiologyCapitalService zkBiologyCapitalService;

    @Override
    public ZkUseMedicineLogDO createZkUseMedicineLog(ZkUseMedicineLogCreateReqVO createReqVO) {
        // 插入
        ZkUseMedicineLogDO zkUseMedicineLog = BeanUtils.toBean(createReqVO, ZkUseMedicineLogDO.class);
        zkUseMedicineLogMapper.insert(zkUseMedicineLog);
        // 返回
        return zkUseMedicineLog;
    }

    @Override
    public List<ZkUseMedicineLogDO> updateZkUseMedicineLog(ZkUseMedicineLogUpdateReqVO updateReqVO) {
        ZkUseMedicineLogDO updateObj = BeanUtils.toBean(updateReqVO, ZkUseMedicineLogDO.class);
        return customUpdate(updateObj);
    }

    @Override
    public List<ZkUseMedicineLogDO> customUpdate(ZkUseMedicineLogDO updateObj) {
        // 校验存在
        ZkUseMedicineLogDO before = validateZkUseMedicineLogExists(updateObj.getId());
        sync_biologyCapital(updateObj, before);
        // 更新
        zkUseMedicineLogMapper.updateById(updateObj);
        return List.of(before, updateObj);
    }

    public void sync_biologyCapital(ZkUseMedicineLogDO curr, ZkUseMedicineLogDO before) {
        if(EasyBigDecimalUtil.valueEquals(curr.getMedicineCost(), before.getMedicineCost())) {
            // 不 需要同步数据
            return;
        }
        // 需要同步数据 1:增加count，difference正，最终加成本，正确; 2:减少count，difference负，最终减成本，正确;
        BigDecimal difference = EasyBigDecimalUtil.subtract(curr.getMedicineCost(), before.getMedicineCost());
        zkBiologyCapitalService.medicineCostUpdate(curr.getPondId(), difference);
    }

    @Override
    public ZkUseMedicineLogDO deleteZkUseMedicineLog(Long id) {
        // 校验存在
        ZkUseMedicineLogDO zkUseMedicineLogDO = validateZkUseMedicineLogExists(id);
        // 删除
        zkUseMedicineLogMapper.deleteById(id);
        return zkUseMedicineLogDO;
    }

    private ZkUseMedicineLogDO validateZkUseMedicineLogExists(Long id) {
        ZkUseMedicineLogDO zkUseMedicineLogDO = zkUseMedicineLogMapper.selectById(id);
        if (zkUseMedicineLogDO == null) {
            throw exception(ZK_USE_MEDICINE_LOG_NOT_EXISTS);
        }
        return zkUseMedicineLogDO;
    }

    @Override
    public ZkUseMedicineLogDO getZkUseMedicineLog(Long id) {
        return zkUseMedicineLogMapper.selectById(id);
    }

    @Override
    public List<ZkUseMedicineLogDO> getZkUseMedicineLogList(Collection<Long> ids) {
        return zkUseMedicineLogMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkUseMedicineLogDO> getZkUseMedicineLogPage(ZkUseMedicineLogPageReqVO pageReqVO) {
        return zkUseMedicineLogMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkUseMedicineLogDO> getZkUseMedicineLogList(ZkUseMedicineLogExportReqVO exportReqVO) {
        return zkUseMedicineLogMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkUseMedicineLogDO> getByUseMedicineIds(Set<Long> useMedicineIds) {
        if(CollectionUtils.isAnyEmpty(useMedicineIds)) return new ArrayList<>();
        QueryWrapper<ZkUseMedicineLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("zk_use_medicine_id", useMedicineIds);

        return zkUseMedicineLogMapper.selectList(queryWrapper);
    }

}
