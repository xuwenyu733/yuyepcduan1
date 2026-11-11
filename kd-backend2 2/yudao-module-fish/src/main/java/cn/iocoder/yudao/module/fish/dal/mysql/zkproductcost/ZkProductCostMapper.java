package cn.iocoder.yudao.module.fish.dal.mysql.zkproductcost;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkproductcost.ZkProductCostDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkproductcost.vo.*;

/**
 * 成本 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkProductCostMapper extends BaseMapperX<ZkProductCostDO> {

    default PageResult<ZkProductCostDO> selectPage(ZkProductCostPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkProductCostDO>()
                .likeIfPresent(ZkProductCostDO::getProductName, reqVO.getProductName())
                .likeIfPresent(ZkProductCostDO::getProductBrand, reqVO.getProductBrand())
                .eqIfPresent(ZkProductCostDO::getProductSpec, reqVO.getProductSpec())
                .eqIfPresent(ZkProductCostDO::getProductPrice, reqVO.getProductPrice())
                .eqIfPresent(ZkProductCostDO::getProductCost, reqVO.getProductCost())
                .eqIfPresent(ZkProductCostDO::getFinalCost, reqVO.getFinalCost())
                .eqIfPresent(ZkProductCostDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ZkProductCostDO::getProductType, reqVO.getProductType())
                .eqIfPresent(ZkProductCostDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkProductCostDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkProductCostDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkProductCostDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(ZkProductCostDO::getSystemId, reqVO.getSystemId())
                .eqIfPresent(ZkProductCostDO::getPondId, reqVO.getPondId())
                .betweenIfPresent(ZkProductCostDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkProductCostDO::getId));
    }

    default List<ZkProductCostDO> selectList(ZkProductCostExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkProductCostDO>()
                .eqIfPresent(ZkProductCostDO::getProductBrand, reqVO.getProductBrand())
                .eqIfPresent(ZkProductCostDO::getProductSpec, reqVO.getProductSpec())
                .eqIfPresent(ZkProductCostDO::getProductPrice, reqVO.getProductPrice())
                .eqIfPresent(ZkProductCostDO::getProductCost, reqVO.getProductCost())
                .eqIfPresent(ZkProductCostDO::getFinalCost, reqVO.getFinalCost())
                .eqIfPresent(ZkProductCostDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ZkProductCostDO::getProductType, reqVO.getProductType())
                .eqIfPresent(ZkProductCostDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkProductCostDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkProductCostDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ZkProductCostDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkProductCostDO::getId));
    }

}
