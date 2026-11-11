package cn.iocoder.yudao.module.fish.dal.mysql.zkmanualinspection;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmanualinspection.ZkManualInspectionDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkmanualinspection.vo.*;

/**
 * 人工巡检 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkManualInspectionMapper extends BaseMapperX<ZkManualInspectionDO> {

    default PageResult<ZkManualInspectionDO> selectPage(ZkManualInspectionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkManualInspectionDO>()
                .eqIfPresent(ZkManualInspectionDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkManualInspectionDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkManualInspectionDO::getInspectionName, reqVO.getInspectionName())
                .eqIfPresent(ZkManualInspectionDO::getInspectionContent, reqVO.getInspectionContent())
                .eqIfPresent(ZkManualInspectionDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkManualInspectionDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkManualInspectionDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkManualInspectionDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkManualInspectionDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkManualInspectionDO::getPondId, reqVO.getPondId())
                .likeIfPresent(ZkManualInspectionDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkManualInspectionDO::getPondNum, reqVO.getPondNum())
                .orderByDesc(ZkManualInspectionDO::getId));
    }

    default List<ZkManualInspectionDO> selectList(ZkManualInspectionExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkManualInspectionDO>()
                .eqIfPresent(ZkManualInspectionDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkManualInspectionDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkManualInspectionDO::getInspectionName, reqVO.getInspectionName())
                .eqIfPresent(ZkManualInspectionDO::getInspectionContent, reqVO.getInspectionContent())
                .eqIfPresent(ZkManualInspectionDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkManualInspectionDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkManualInspectionDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkManualInspectionDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkManualInspectionDO::getFid, reqVO.getFid())
                .orderByDesc(ZkManualInspectionDO::getId));
    }

}
