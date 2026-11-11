package cn.iocoder.yudao.module.fish.dal.mysql.zkusemedicine;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicine.ZkUseMedicineDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.*;

/**
 * 用药记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkUseMedicineMapper extends BaseMapperX<ZkUseMedicineDO> {

    default PageResult<ZkUseMedicineDO> selectPage(ZkUseMedicinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkUseMedicineDO>()
                .likeIfPresent(ZkUseMedicineDO::getMedicineName, reqVO.getMedicineName())
                .eqIfPresent(ZkUseMedicineDO::getMidicineDosage, reqVO.getMidicineDosage())
                .likeIfPresent(ZkUseMedicineDO::getControlTitle, reqVO.getControlTitle())
                .likeIfPresent(ZkUseMedicineDO::getControlContent, reqVO.getControlContent())
                .eqIfPresent(ZkUseMedicineDO::getMedicineAfter, reqVO.getMedicineAfter())
                .eqIfPresent(ZkUseMedicineDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkUseMedicineDO::getFids, reqVO.getFids())
                .eqIfPresent(ZkUseMedicineDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .betweenIfPresent(ZkUseMedicineDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkUseMedicineDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkUseMedicineDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkUseMedicineDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkUseMedicineDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(ZkUseMedicineDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkUseMedicineDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(ZkUseMedicineDO::getPondName, reqVO.getPondName())
                .orderByDesc(ZkUseMedicineDO::getId));
    }

    default List<ZkUseMedicineDO> selectList(ZkUseMedicineExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkUseMedicineDO>()
                .likeIfPresent(ZkUseMedicineDO::getMedicineName, reqVO.getMedicineName())
                .eqIfPresent(ZkUseMedicineDO::getMidicineDosage, reqVO.getMidicineDosage())
                .eqIfPresent(ZkUseMedicineDO::getControlTitle, reqVO.getControlTitle())
                .eqIfPresent(ZkUseMedicineDO::getControlContent, reqVO.getControlContent())
                .eqIfPresent(ZkUseMedicineDO::getMedicineAfter, reqVO.getMedicineAfter())
                .eqIfPresent(ZkUseMedicineDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ZkUseMedicineDO::getFids, reqVO.getFids())
                .eqIfPresent(ZkUseMedicineDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .betweenIfPresent(ZkUseMedicineDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkUseMedicineDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkUseMedicineDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkUseMedicineDO::getFid, reqVO.getFid())
                .orderByDesc(ZkUseMedicineDO::getId));
    }

}
