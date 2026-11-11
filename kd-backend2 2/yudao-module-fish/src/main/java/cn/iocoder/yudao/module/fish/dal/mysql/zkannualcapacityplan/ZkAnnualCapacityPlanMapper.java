package cn.iocoder.yudao.module.fish.dal.mysql.zkannualcapacityplan;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkannualcapacityplan.ZkAnnualCapacityPlanDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkannualcapacityplan.vo.*;

/**
 * 年度产能计划 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkAnnualCapacityPlanMapper extends BaseMapperX<ZkAnnualCapacityPlanDO> {

    default PageResult<ZkAnnualCapacityPlanDO> selectPage(ZkAnnualCapacityPlanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkAnnualCapacityPlanDO>()
                .eqIfPresent(ZkAnnualCapacityPlanDO::getPlanYear, reqVO.getPlanYear())
                .eqIfPresent(ZkAnnualCapacityPlanDO::getSpeciesId, reqVO.getSpeciesId())
                .likeIfPresent(ZkAnnualCapacityPlanDO::getSpeciesName, reqVO.getSpeciesName())
                .eqIfPresent(ZkAnnualCapacityPlanDO::getCurrentCapacity, reqVO.getCurrentCapacity())
                .eqIfPresent(ZkAnnualCapacityPlanDO::getTargetCapacity, reqVO.getTargetCapacity())
                .betweenIfPresent(ZkAnnualCapacityPlanDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkAnnualCapacityPlanDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkAnnualCapacityPlanDO::getId));
    }

}