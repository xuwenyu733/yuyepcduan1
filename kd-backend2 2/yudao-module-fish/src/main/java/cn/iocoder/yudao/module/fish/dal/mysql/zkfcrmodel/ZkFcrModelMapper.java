package cn.iocoder.yudao.module.fish.dal.mysql.zkfcrmodel;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfcrmodel.ZkFcrModelDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkfcrmodel.vo.*;

/**
 * 饵料系数FCR模型 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkFcrModelMapper extends BaseMapperX<ZkFcrModelDO> {

    default PageResult<ZkFcrModelDO> selectPage(ZkFcrModelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkFcrModelDO>()
                .eqIfPresent(ZkFcrModelDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkFcrModelDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkFcrModelDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkFcrModelDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkFcrModelDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkFcrModelDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkFcrModelDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkFcrModelDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFcrModelDO::getAverageSamplingLength, reqVO.getAverageSamplingLength())
                .eqIfPresent(ZkFcrModelDO::getAverageSamplingSize, reqVO.getAverageSamplingSize())
                .eqIfPresent(ZkFcrModelDO::getAverageFeedingAmount, reqVO.getAverageFeedingAmount())
                .eqIfPresent(ZkFcrModelDO::getAverageWeightGain, reqVO.getAverageWeightGain())
                .eqIfPresent(ZkFcrModelDO::getActualFcr, reqVO.getActualFcr())
                .eqIfPresent(ZkFcrModelDO::getFcrGrowthRate, reqVO.getFcrGrowthRate())
                .eqIfPresent(ZkFcrModelDO::getTotalFeedingAmount, reqVO.getTotalFeedingAmount())
                .eqIfPresent(ZkFcrModelDO::getStockingQuantity, reqVO.getStockingQuantity())
                .eqIfPresent(ZkFcrModelDO::getTotalWeight, reqVO.getTotalWeight())
                .betweenIfPresent(ZkFcrModelDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkFcrModelDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkFcrModelDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkFcrModelDO::getFid, reqVO.getFid())
                .orderByDesc(ZkFcrModelDO::getId));
    }

    default List<ZkFcrModelDO> selectList(ZkFcrModelExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkFcrModelDO>()
                .eqIfPresent(ZkFcrModelDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkFcrModelDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkFcrModelDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkFcrModelDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkFcrModelDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkFcrModelDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkFcrModelDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkFcrModelDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFcrModelDO::getAverageSamplingLength, reqVO.getAverageSamplingLength())
                .eqIfPresent(ZkFcrModelDO::getAverageSamplingSize, reqVO.getAverageSamplingSize())
                .eqIfPresent(ZkFcrModelDO::getAverageFeedingAmount, reqVO.getAverageFeedingAmount())
                .eqIfPresent(ZkFcrModelDO::getAverageWeightGain, reqVO.getAverageWeightGain())
                .eqIfPresent(ZkFcrModelDO::getActualFcr, reqVO.getActualFcr())
                .eqIfPresent(ZkFcrModelDO::getFcrGrowthRate, reqVO.getFcrGrowthRate())
                .eqIfPresent(ZkFcrModelDO::getTotalFeedingAmount, reqVO.getTotalFeedingAmount())
                .eqIfPresent(ZkFcrModelDO::getStockingQuantity, reqVO.getStockingQuantity())
                .eqIfPresent(ZkFcrModelDO::getTotalWeight, reqVO.getTotalWeight())
                .betweenIfPresent(ZkFcrModelDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkFcrModelDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkFcrModelDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkFcrModelDO::getFid, reqVO.getFid())
                .orderByDesc(ZkFcrModelDO::getId));
    }

}
