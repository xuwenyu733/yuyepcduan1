package cn.iocoder.yudao.module.fish.dal.mysql.zkdatarevise;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdatarevise.ZkDataReviseDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo.*;

/**
 * 数据修正记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDataReviseMapper extends BaseMapperX<ZkDataReviseDO> {

    default PageResult<ZkDataReviseDO> selectPage(ZkDataRevisePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkDataReviseDO>()
                .eqIfPresent(ZkDataReviseDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkDataReviseDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkDataReviseDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkDataReviseDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkDataReviseDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkDataReviseDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkDataReviseDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkDataReviseDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(ZkDataReviseDO::getReviseType, reqVO.getReviseType())
                .eqIfPresent(ZkDataReviseDO::getOriginData, reqVO.getOriginData())
                .eqIfPresent(ZkDataReviseDO::getDifferenceData, reqVO.getDifferenceData())
                .eqIfPresent(ZkDataReviseDO::getChangeRatio, reqVO.getChangeRatio())
                .eqIfPresent(ZkDataReviseDO::getReason, reqVO.getReason())
                .eqIfPresent(ZkDataReviseDO::getTechnicalDirectorsOpinion, reqVO.getTechnicalDirectorsOpinion())
                .eqIfPresent(ZkDataReviseDO::getDirectorsOpinion, reqVO.getDirectorsOpinion())
                .eqIfPresent(ZkDataReviseDO::getBusId, reqVO.getBusId())
                .betweenIfPresent(ZkDataReviseDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDataReviseDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDataReviseDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDataReviseDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDataReviseDO::getId));
    }

    default List<ZkDataReviseDO> selectList(ZkDataReviseExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkDataReviseDO>()
                .eqIfPresent(ZkDataReviseDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkDataReviseDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkDataReviseDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkDataReviseDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkDataReviseDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkDataReviseDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkDataReviseDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkDataReviseDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(ZkDataReviseDO::getReviseType, reqVO.getReviseType())
                .eqIfPresent(ZkDataReviseDO::getOriginData, reqVO.getOriginData())
                .eqIfPresent(ZkDataReviseDO::getDifferenceData, reqVO.getDifferenceData())
                .eqIfPresent(ZkDataReviseDO::getChangeRatio, reqVO.getChangeRatio())
                .eqIfPresent(ZkDataReviseDO::getReason, reqVO.getReason())
                .eqIfPresent(ZkDataReviseDO::getTechnicalDirectorsOpinion, reqVO.getTechnicalDirectorsOpinion())
                .eqIfPresent(ZkDataReviseDO::getDirectorsOpinion, reqVO.getDirectorsOpinion())
                .eqIfPresent(ZkDataReviseDO::getBusId, reqVO.getBusId())
                .betweenIfPresent(ZkDataReviseDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDataReviseDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDataReviseDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDataReviseDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDataReviseDO::getId));
    }

}
