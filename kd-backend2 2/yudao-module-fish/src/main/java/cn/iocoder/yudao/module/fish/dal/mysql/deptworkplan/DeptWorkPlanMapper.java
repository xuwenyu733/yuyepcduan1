package cn.iocoder.yudao.module.fish.dal.mysql.deptworkplan;

import java.util.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.deptworkplan.DeptWorkPlanDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.deptworkplan.vo.*;

/**
 * 部门工作计划 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface DeptWorkPlanMapper extends BaseMapperX<DeptWorkPlanDO> {

    default PageResult<DeptWorkPlanDO> selectPage(DeptWorkPlanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DeptWorkPlanDO>()
                .likeIfPresent(DeptWorkPlanDO::getTitle, reqVO.getTitle())
                .eqIfPresent(DeptWorkPlanDO::getType, reqVO.getType())
                .eqIfPresent(DeptWorkPlanDO::getUserId, reqVO.getUserId())
                .eqIfPresent(DeptWorkPlanDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(DeptWorkPlanDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(DeptWorkPlanDO::getSubmitDate, reqVO.getSubmitDate())
                .orderByDesc(DeptWorkPlanDO::getSubmitDate)
                .orderByDesc(DeptWorkPlanDO::getId));
    }

    default List<DeptWorkPlanDO> selectList(DeptWorkPlanExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<DeptWorkPlanDO>()
                .likeIfPresent(DeptWorkPlanDO::getTitle, reqVO.getTitle())
                .eqIfPresent(DeptWorkPlanDO::getType, reqVO.getType())
                .eqIfPresent(DeptWorkPlanDO::getUserId, reqVO.getUserId())
                .eqIfPresent(DeptWorkPlanDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(DeptWorkPlanDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(DeptWorkPlanDO::getSubmitDate, reqVO.getSubmitDate())
                .orderByDesc(DeptWorkPlanDO::getSubmitDate)
                .orderByDesc(DeptWorkPlanDO::getId));
    }
}

