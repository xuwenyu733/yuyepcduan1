package cn.iocoder.yudao.module.fish.dal.mysql.zkgrowthmodel;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowthmodel.ZkGrowthModelDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowthmodel.vo.*;

/**
 * 生长模型 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkGrowthModelMapper extends BaseMapperX<ZkGrowthModelDO> {

    default PageResult<ZkGrowthModelDO> selectPage(ZkGrowthModelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkGrowthModelDO>()
                .eqIfPresent(ZkGrowthModelDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkGrowthModelDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkGrowthModelDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkGrowthModelDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkGrowthModelDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkGrowthModelDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkGrowthModelDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkGrowthModelDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkGrowthModelDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkGrowthModelDO::getAverageLength, reqVO.getAverageLength())
                .eqIfPresent(ZkGrowthModelDO::getAverageWeight, reqVO.getAverageWeight())
                .eqIfPresent(ZkGrowthModelDO::getGrowthRate, reqVO.getGrowthRate())
                .eqIfPresent(ZkGrowthModelDO::getSpecification, reqVO.getSpecification())
                .betweenIfPresent(ZkGrowthModelDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkGrowthModelDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkGrowthModelDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkGrowthModelDO::getFid, reqVO.getFid())
                .orderByDesc(ZkGrowthModelDO::getId));
    }

    default List<ZkGrowthModelDO> selectList(ZkGrowthModelExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkGrowthModelDO>()
                .eqIfPresent(ZkGrowthModelDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkGrowthModelDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkGrowthModelDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkGrowthModelDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkGrowthModelDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkGrowthModelDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkGrowthModelDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkGrowthModelDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkGrowthModelDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkGrowthModelDO::getAverageLength, reqVO.getAverageLength())
                .eqIfPresent(ZkGrowthModelDO::getAverageWeight, reqVO.getAverageWeight())
                .eqIfPresent(ZkGrowthModelDO::getGrowthRate, reqVO.getGrowthRate())
                .eqIfPresent(ZkGrowthModelDO::getSpecification, reqVO.getSpecification())
                .betweenIfPresent(ZkGrowthModelDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkGrowthModelDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkGrowthModelDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkGrowthModelDO::getFid, reqVO.getFid())
                .orderByDesc(ZkGrowthModelDO::getId));
    }

}
