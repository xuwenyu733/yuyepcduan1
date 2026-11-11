package cn.iocoder.yudao.module.fish.dal.mysql.zkprecisionfeeding;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkprecisionfeeding.ZkPrecisionFeedingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo.*;

/**
 * 精准投喂 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkPrecisionFeedingMapper extends BaseMapperX<ZkPrecisionFeedingDO> {

    default PageResult<ZkPrecisionFeedingDO> selectPage(ZkPrecisionFeedingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPrecisionFeedingDO>()
                .eqIfPresent(ZkPrecisionFeedingDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkPrecisionFeedingDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPrecisionFeedingDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkPrecisionFeedingDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkPrecisionFeedingDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkPrecisionFeedingDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkPrecisionFeedingDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkPrecisionFeedingDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkPrecisionFeedingDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkPrecisionFeedingDO::getFeedingWeight, reqVO.getFeedingWeight())
                .eqIfPresent(ZkPrecisionFeedingDO::getFeedingFrequency, reqVO.getFeedingFrequency())
                .eqIfPresent(ZkPrecisionFeedingDO::getFoodIntake, reqVO.getFoodIntake())
                .eqIfPresent(ZkPrecisionFeedingDO::getFeedingAmount, reqVO.getFeedingAmount())
                .eqIfPresent(ZkPrecisionFeedingDO::getFeedConversionRatio, reqVO.getFeedConversionRatio())
                .eqIfPresent(ZkPrecisionFeedingDO::getConversionRate, reqVO.getConversionRate())
                .betweenIfPresent(ZkPrecisionFeedingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPrecisionFeedingDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPrecisionFeedingDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPrecisionFeedingDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPrecisionFeedingDO::getId));
    }

    default List<ZkPrecisionFeedingDO> selectList(ZkPrecisionFeedingExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkPrecisionFeedingDO>()
                .eqIfPresent(ZkPrecisionFeedingDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkPrecisionFeedingDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkPrecisionFeedingDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkPrecisionFeedingDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkPrecisionFeedingDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkPrecisionFeedingDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkPrecisionFeedingDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkPrecisionFeedingDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkPrecisionFeedingDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkPrecisionFeedingDO::getFeedingWeight, reqVO.getFeedingWeight())
                .eqIfPresent(ZkPrecisionFeedingDO::getFeedingFrequency, reqVO.getFeedingFrequency())
                .eqIfPresent(ZkPrecisionFeedingDO::getFoodIntake, reqVO.getFoodIntake())
                .eqIfPresent(ZkPrecisionFeedingDO::getFeedingAmount, reqVO.getFeedingAmount())
                .eqIfPresent(ZkPrecisionFeedingDO::getFeedConversionRatio, reqVO.getFeedConversionRatio())
                .eqIfPresent(ZkPrecisionFeedingDO::getConversionRate, reqVO.getConversionRate())
                .betweenIfPresent(ZkPrecisionFeedingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPrecisionFeedingDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkPrecisionFeedingDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkPrecisionFeedingDO::getFid, reqVO.getFid())
                .orderByDesc(ZkPrecisionFeedingDO::getId));
    }

}
