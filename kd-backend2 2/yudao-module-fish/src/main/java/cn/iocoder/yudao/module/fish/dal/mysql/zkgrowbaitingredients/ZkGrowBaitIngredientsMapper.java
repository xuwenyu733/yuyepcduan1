package cn.iocoder.yudao.module.fish.dal.mysql.zkgrowbaitingredients;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitingredients.ZkGrowBaitIngredientsDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitingredients.vo.*;

/**
 * 饵料成分对比 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkGrowBaitIngredientsMapper extends BaseMapperX<ZkGrowBaitIngredientsDO> {

    default PageResult<ZkGrowBaitIngredientsDO> selectPage(ZkGrowBaitIngredientsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkGrowBaitIngredientsDO>()
                .likeIfPresent(ZkGrowBaitIngredientsDO::getBaitName, reqVO.getBaitName())
                .eqIfPresent(ZkGrowBaitIngredientsDO::getFeedType, reqVO.getFeedType())
                .eqIfPresent(ZkGrowBaitIngredientsDO::getCrudeProtein, reqVO.getCrudeProtein())
                .eqIfPresent(ZkGrowBaitIngredientsDO::getCrudeFat, reqVO.getCrudeFat())
                .eqIfPresent(ZkGrowBaitIngredientsDO::getCarbohydrate, reqVO.getCarbohydrate())
                .eqIfPresent(ZkGrowBaitIngredientsDO::getCrudeFiber, reqVO.getCrudeFiber())
                .eqIfPresent(ZkGrowBaitIngredientsDO::getMoisture, reqVO.getMoisture())
                .eqIfPresent(ZkGrowBaitIngredientsDO::getAshContent, reqVO.getAshContent())
                .eqIfPresent(ZkGrowBaitIngredientsDO::getReferencePrice, reqVO.getReferencePrice())
                .betweenIfPresent(ZkGrowBaitIngredientsDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkGrowBaitIngredientsDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkGrowBaitIngredientsDO::getId));
    }

}