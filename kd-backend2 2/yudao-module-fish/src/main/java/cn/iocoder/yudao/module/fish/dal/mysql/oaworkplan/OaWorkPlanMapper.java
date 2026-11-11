package cn.iocoder.yudao.module.fish.dal.mysql.oaworkplan;

import java.util.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaworkplan.OaWorkPlanDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo.*;

/**
 * OA工作计划 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface OaWorkPlanMapper extends BaseMapperX<OaWorkPlanDO> {

    default PageResult<OaWorkPlanDO> selectPage(OaWorkPlanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OaWorkPlanDO>()
                .likeIfPresent(OaWorkPlanDO::getTitle, reqVO.getTitle())
                .eqIfPresent(OaWorkPlanDO::getType, reqVO.getType())
                .eqIfPresent(OaWorkPlanDO::getStatus, reqVO.getStatus())
                .eqIfPresent(OaWorkPlanDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OaWorkPlanDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(OaWorkPlanDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(OaWorkPlanDO::getSubmitDate, reqVO.getSubmitDate())
                .orderByDesc(OaWorkPlanDO::getSubmitDate)
                .orderByDesc(OaWorkPlanDO::getId));
    }

    default List<OaWorkPlanDO> selectList(OaWorkPlanExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<OaWorkPlanDO>()
                .likeIfPresent(OaWorkPlanDO::getTitle, reqVO.getTitle())
                .eqIfPresent(OaWorkPlanDO::getType, reqVO.getType())
                .eqIfPresent(OaWorkPlanDO::getStatus, reqVO.getStatus())
                .eqIfPresent(OaWorkPlanDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OaWorkPlanDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(OaWorkPlanDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(OaWorkPlanDO::getSubmitDate, reqVO.getSubmitDate())
                .orderByDesc(OaWorkPlanDO::getSubmitDate)
                .orderByDesc(OaWorkPlanDO::getId));
    }

}



