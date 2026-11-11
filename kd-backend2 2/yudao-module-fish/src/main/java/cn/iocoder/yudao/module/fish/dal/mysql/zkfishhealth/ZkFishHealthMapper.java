package cn.iocoder.yudao.module.fish.dal.mysql.zkfishhealth;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishhealth.ZkFishHealthDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishhealth.vo.*;

/**
 * 鱼类健康评估 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkFishHealthMapper extends BaseMapperX<ZkFishHealthDO> {

    default PageResult<ZkFishHealthDO> selectPage(ZkFishHealthPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkFishHealthDO>()
                .eqIfPresent(ZkFishHealthDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkFishHealthDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkFishHealthDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkFishHealthDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkFishHealthDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkFishHealthDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkFishHealthDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkFishHealthDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFishHealthDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkFishHealthDO::getBreathingRate, reqVO.getBreathingRate())
                .eqIfPresent(ZkFishHealthDO::getFeedingStatus, reqVO.getFeedingStatus())
                .eqIfPresent(ZkFishHealthDO::getHealthyCount, reqVO.getHealthyCount())
                .eqIfPresent(ZkFishHealthDO::getSubhealthyCount, reqVO.getSubhealthyCount())
                .eqIfPresent(ZkFishHealthDO::getDiseasedCount, reqVO.getDiseasedCount())
                .eqIfPresent(ZkFishHealthDO::getHealthScore, reqVO.getHealthScore())
                .betweenIfPresent(ZkFishHealthDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkFishHealthDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkFishHealthDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkFishHealthDO::getFid, reqVO.getFid())
                .orderByDesc(ZkFishHealthDO::getId));
    }

    default List<ZkFishHealthDO> selectList(ZkFishHealthExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkFishHealthDO>()
                .eqIfPresent(ZkFishHealthDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkFishHealthDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkFishHealthDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkFishHealthDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkFishHealthDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkFishHealthDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkFishHealthDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkFishHealthDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFishHealthDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkFishHealthDO::getBreathingRate, reqVO.getBreathingRate())
                .eqIfPresent(ZkFishHealthDO::getFeedingStatus, reqVO.getFeedingStatus())
                .eqIfPresent(ZkFishHealthDO::getHealthyCount, reqVO.getHealthyCount())
                .eqIfPresent(ZkFishHealthDO::getSubhealthyCount, reqVO.getSubhealthyCount())
                .eqIfPresent(ZkFishHealthDO::getDiseasedCount, reqVO.getDiseasedCount())
                .eqIfPresent(ZkFishHealthDO::getHealthScore, reqVO.getHealthScore())
                .betweenIfPresent(ZkFishHealthDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkFishHealthDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkFishHealthDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkFishHealthDO::getFid, reqVO.getFid())
                .orderByDesc(ZkFishHealthDO::getId));
    }

}
