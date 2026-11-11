package cn.iocoder.yudao.module.fish.service.zkgrowdailyrecord;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowDailyRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowdailyrecord.ZkGrowDailyRecordMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 每日生长与环境记录 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkGrowDailyRecordServiceImpl implements ZkGrowDailyRecordService {

    @Resource
    private ZkGrowDailyRecordMapper zkGrowDailyRecordMapper;

    @Override
    public Long createZkGrowDailyRecord(ZkGrowDailyRecordSaveReqVO createReqVO) {
        // 插入
        ZkGrowDailyRecordDO zkGrowDailyRecord = BeanUtils.toBean(createReqVO, ZkGrowDailyRecordDO.class);
        zkGrowDailyRecordMapper.insert(zkGrowDailyRecord);

        // 返回
        return zkGrowDailyRecord.getId();
    }

    @Override
    public void updateZkGrowDailyRecord(ZkGrowDailyRecordSaveReqVO updateReqVO) {
        // 校验存在
        validateZkGrowDailyRecordExists(updateReqVO.getId());
        // 更新
        ZkGrowDailyRecordDO updateObj = BeanUtils.toBean(updateReqVO, ZkGrowDailyRecordDO.class);
        zkGrowDailyRecordMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkGrowDailyRecord(Long id) {
        // 校验存在
        validateZkGrowDailyRecordExists(id);
        // 删除
        zkGrowDailyRecordMapper.deleteById(id);
    }

    @Override
        public void deleteZkGrowDailyRecordListByIds(List<Long> ids) {
        // 删除
        zkGrowDailyRecordMapper.deleteByIds(ids);
        }


    private void validateZkGrowDailyRecordExists(Long id) {
        if (zkGrowDailyRecordMapper.selectById(id) == null) {
            throw exception(ZK_GROW_DAILY_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public ZkGrowDailyRecordDO getZkGrowDailyRecord(Long id) {
        return zkGrowDailyRecordMapper.selectById(id);
    }

    @Override
    public PageResult<ZkGrowDailyRecordDO> getZkGrowDailyRecordPage(ZkGrowDailyRecordPageReqVO pageReqVO) {
        return zkGrowDailyRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkGrowBatchSummaryRespVO> getBatchSummaryList() {
        // 依赖 ERP 表；未导入时返回空列表，避免整页 501 打断其它筛选/看板
        try {
            return zkGrowDailyRecordMapper.selectBatchSummaryList();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<ZkGrowPondSummaryRespVO> getPondSummaryList() {
        return zkGrowDailyRecordMapper.selectPondSummaryList();
    }

    @Override
    public List<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowRealStatsDO> getRealStats(String batchNo, String pondNum, String pondDetailNum) {
        return zkGrowDailyRecordMapper.selectRealStats(batchNo, pondNum, pondDetailNum);
    }

    @Override
    public List<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowManualMeasureDO> getManualMeasurements(List<Long> pondIds) {
        if (cn.hutool.core.collection.CollUtil.isEmpty(pondIds)) {
            return new java.util.ArrayList<>();
        }
        return zkGrowDailyRecordMapper.selectManualMeasurements(pondIds);
    }

}