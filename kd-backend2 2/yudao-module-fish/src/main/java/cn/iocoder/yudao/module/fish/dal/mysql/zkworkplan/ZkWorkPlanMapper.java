package cn.iocoder.yudao.module.fish.dal.mysql.zkworkplan;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkplan.ZkWorkPlanDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkworkplan.vo.*;

/**
 * 工作目标与计划 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkWorkPlanMapper extends BaseMapperX<ZkWorkPlanDO> {

    default PageResult<ZkWorkPlanDO> selectPage(ZkWorkPlanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkWorkPlanDO>()
                .eqIfPresent(ZkWorkPlanDO::getDaylyPlan, reqVO.getDaylyPlan())
                .eqIfPresent(ZkWorkPlanDO::getWeeklyPlan, reqVO.getWeeklyPlan())
                .eqIfPresent(ZkWorkPlanDO::getMonthlyGoal, reqVO.getMonthlyGoal())
                .eqIfPresent(ZkWorkPlanDO::getQuarterlyGoal, reqVO.getQuarterlyGoal())
                .eqIfPresent(ZkWorkPlanDO::getAnnualGoal, reqVO.getAnnualGoal())
                .betweenIfPresent(ZkWorkPlanDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkWorkPlanDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkWorkPlanDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkWorkPlanDO::getFid, reqVO.getFid())
                .orderByDesc(ZkWorkPlanDO::getId));
    }

    default List<ZkWorkPlanDO> selectList(ZkWorkPlanExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkWorkPlanDO>()
                .eqIfPresent(ZkWorkPlanDO::getDaylyPlan, reqVO.getDaylyPlan())
                .eqIfPresent(ZkWorkPlanDO::getWeeklyPlan, reqVO.getWeeklyPlan())
                .eqIfPresent(ZkWorkPlanDO::getMonthlyGoal, reqVO.getMonthlyGoal())
                .eqIfPresent(ZkWorkPlanDO::getQuarterlyGoal, reqVO.getQuarterlyGoal())
                .eqIfPresent(ZkWorkPlanDO::getAnnualGoal, reqVO.getAnnualGoal())
                .betweenIfPresent(ZkWorkPlanDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkWorkPlanDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkWorkPlanDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkWorkPlanDO::getFid, reqVO.getFid())
                .orderByDesc(ZkWorkPlanDO::getId));
    }

}
