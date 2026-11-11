package cn.iocoder.yudao.module.fish.service.zkannualcapacityplan;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkannualcapacityplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkannualcapacityplan.ZkAnnualCapacityPlanDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkannualcapacityplan.ZkAnnualCapacityPlanMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 年度产能计划 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkAnnualCapacityPlanServiceImpl implements ZkAnnualCapacityPlanService {

    @Resource
    private ZkAnnualCapacityPlanMapper zkAnnualCapacityPlanMapper;

    @Override
    public Long createZkAnnualCapacityPlan(ZkAnnualCapacityPlanSaveReqVO createReqVO) {
        // 插入
        ZkAnnualCapacityPlanDO zkAnnualCapacityPlan = BeanUtils.toBean(createReqVO, ZkAnnualCapacityPlanDO.class);
        zkAnnualCapacityPlanMapper.insert(zkAnnualCapacityPlan);

        // 返回
        return zkAnnualCapacityPlan.getId();
    }

    @Override
    public void updateZkAnnualCapacityPlan(ZkAnnualCapacityPlanSaveReqVO updateReqVO) {
        // 校验存在
        validateZkAnnualCapacityPlanExists(updateReqVO.getId());
        // 更新
        ZkAnnualCapacityPlanDO updateObj = BeanUtils.toBean(updateReqVO, ZkAnnualCapacityPlanDO.class);
        zkAnnualCapacityPlanMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkAnnualCapacityPlan(Long id) {
        // 校验存在
        validateZkAnnualCapacityPlanExists(id);
        // 删除
        zkAnnualCapacityPlanMapper.deleteById(id);
    }

    @Override
        public void deleteZkAnnualCapacityPlanListByIds(List<Long> ids) {
        // 删除
        zkAnnualCapacityPlanMapper.deleteByIds(ids);
        }


    private void validateZkAnnualCapacityPlanExists(Long id) {
        if (zkAnnualCapacityPlanMapper.selectById(id) == null) {
            throw exception(ZK_ANNUAL_CAPACITY_PLAN_NOT_EXISTS);
        }
    }

    @Override
    public ZkAnnualCapacityPlanDO getZkAnnualCapacityPlan(Long id) {
        return zkAnnualCapacityPlanMapper.selectById(id);
    }

    @Override
    public PageResult<ZkAnnualCapacityPlanDO> getZkAnnualCapacityPlanPage(ZkAnnualCapacityPlanPageReqVO pageReqVO) {
        return zkAnnualCapacityPlanMapper.selectPage(pageReqVO);
    }

}