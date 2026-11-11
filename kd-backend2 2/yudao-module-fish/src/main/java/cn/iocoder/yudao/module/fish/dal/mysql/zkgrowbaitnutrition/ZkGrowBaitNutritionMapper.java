package cn.iocoder.yudao.module.fish.dal.mysql.zkgrowbaitnutrition;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitnutrition.ZkGrowBaitNutritionDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitnutrition.vo.*;

/**
 * 饵料营养成分 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkGrowBaitNutritionMapper extends BaseMapperX<ZkGrowBaitNutritionDO> {

    default PageResult<ZkGrowBaitNutritionDO> selectPage(ZkGrowBaitNutritionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkGrowBaitNutritionDO>()
                .likeIfPresent(ZkGrowBaitNutritionDO::getBaitName, reqVO.getBaitName())
                .eqIfPresent(ZkGrowBaitNutritionDO::getPelletSize, reqVO.getPelletSize())
                .eqIfPresent(ZkGrowBaitNutritionDO::getCrudeProtein, reqVO.getCrudeProtein())
                .eqIfPresent(ZkGrowBaitNutritionDO::getCrudeFat, reqVO.getCrudeFat())
                .eqIfPresent(ZkGrowBaitNutritionDO::getNitrogenFreeExtract, reqVO.getNitrogenFreeExtract())
                .eqIfPresent(ZkGrowBaitNutritionDO::getAshContent, reqVO.getAshContent())
                .eqIfPresent(ZkGrowBaitNutritionDO::getCrudeFiber, reqVO.getCrudeFiber())
                .eqIfPresent(ZkGrowBaitNutritionDO::getPhosphorus, reqVO.getPhosphorus())
                .eqIfPresent(ZkGrowBaitNutritionDO::getGrossEnergy, reqVO.getGrossEnergy())
                .eqIfPresent(ZkGrowBaitNutritionDO::getDigestibleEnergy, reqVO.getDigestibleEnergy())
                .betweenIfPresent(ZkGrowBaitNutritionDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkGrowBaitNutritionDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkGrowBaitNutritionDO::getId));
    }

}