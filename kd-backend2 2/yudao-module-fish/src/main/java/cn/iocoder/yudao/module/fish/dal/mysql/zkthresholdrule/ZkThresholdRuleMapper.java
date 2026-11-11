package cn.iocoder.yudao.module.fish.dal.mysql.zkthresholdrule;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkthresholdrule.ZkThresholdRuleDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule.vo.*;

/**
 * 阈值规则 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkThresholdRuleMapper extends BaseMapperX<ZkThresholdRuleDO> {

    default PageResult<ZkThresholdRuleDO> selectPage(ZkThresholdRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkThresholdRuleDO>()
                .likeIfPresent(ZkThresholdRuleDO::getRuleName, reqVO.getRuleName())
                .eqIfPresent(ZkThresholdRuleDO::getDeviceTypeId, reqVO.getDeviceTypeId())
                .eqIfPresent(ZkThresholdRuleDO::getMetricId, reqVO.getMetricId())
                .eqIfPresent(ZkThresholdRuleDO::getComparisonOperator, reqVO.getComparisonOperator())
                .eqIfPresent(ZkThresholdRuleDO::getThresholdValue1, reqVO.getThresholdValue1())
                .eqIfPresent(ZkThresholdRuleDO::getThresholdValue2, reqVO.getThresholdValue2())
                .eqIfPresent(ZkThresholdRuleDO::getIsEnabled, reqVO.getIsEnabled())
                .eqIfPresent(ZkThresholdRuleDO::getTriggerLevel, reqVO.getTriggerLevel())
                .eqIfPresent(ZkThresholdRuleDO::getTriggerAction, reqVO.getTriggerAction())
                .eqIfPresent(ZkThresholdRuleDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(ZkThresholdRuleDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkThresholdRuleDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkThresholdRuleDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkThresholdRuleDO::getFid, reqVO.getFid())
                .orderByDesc(ZkThresholdRuleDO::getId));
    }

    default List<ZkThresholdRuleDO> selectList(ZkThresholdRuleExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkThresholdRuleDO>()
                .likeIfPresent(ZkThresholdRuleDO::getRuleName, reqVO.getRuleName())
                .eqIfPresent(ZkThresholdRuleDO::getDeviceTypeId, reqVO.getDeviceTypeId())
                .eqIfPresent(ZkThresholdRuleDO::getMetricId, reqVO.getMetricId())
                .eqIfPresent(ZkThresholdRuleDO::getComparisonOperator, reqVO.getComparisonOperator())
                .eqIfPresent(ZkThresholdRuleDO::getThresholdValue1, reqVO.getThresholdValue1())
                .eqIfPresent(ZkThresholdRuleDO::getThresholdValue2, reqVO.getThresholdValue2())
                .eqIfPresent(ZkThresholdRuleDO::getIsEnabled, reqVO.getIsEnabled())
                .eqIfPresent(ZkThresholdRuleDO::getTriggerLevel, reqVO.getTriggerLevel())
                .eqIfPresent(ZkThresholdRuleDO::getTriggerAction, reqVO.getTriggerAction())
                .eqIfPresent(ZkThresholdRuleDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(ZkThresholdRuleDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkThresholdRuleDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkThresholdRuleDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkThresholdRuleDO::getFid, reqVO.getFid())
                .orderByDesc(ZkThresholdRuleDO::getId));
    }

}
