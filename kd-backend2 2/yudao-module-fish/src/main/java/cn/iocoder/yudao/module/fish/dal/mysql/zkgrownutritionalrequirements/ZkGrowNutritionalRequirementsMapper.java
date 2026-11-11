package cn.iocoder.yudao.module.fish.dal.mysql.zkgrownutritionalrequirements;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrownutritionalrequirements.ZkGrowNutritionalRequirementsDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrownutritionalrequirements.vo.*;

/**
 * 各阶段营养需求 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkGrowNutritionalRequirementsMapper extends BaseMapperX<ZkGrowNutritionalRequirementsDO> {

    default PageResult<ZkGrowNutritionalRequirementsDO> selectPage(ZkGrowNutritionalRequirementsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkGrowNutritionalRequirementsDO>()
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getGrowthStage, reqVO.getGrowthStage())
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getDayold, reqVO.getDayold())
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getWeightRange, reqVO.getWeightRange())
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getCrudeProtein, reqVO.getCrudeProtein())
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getCrudeFat, reqVO.getCrudeFat())
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getCarbohydrate, reqVO.getCarbohydrate())
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getCrudeFiber, reqVO.getCrudeFiber())
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getCalcium, reqVO.getCalcium())
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getPhosphorus, reqVO.getPhosphorus())
                .betweenIfPresent(ZkGrowNutritionalRequirementsDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkGrowNutritionalRequirementsDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkGrowNutritionalRequirementsDO::getId));
    }

}