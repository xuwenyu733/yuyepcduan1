package cn.iocoder.yudao.module.fish.dal.mysql.zkusemedicinelog;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo.*;

/**
 * 用药明细 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkUseMedicineLogMapper extends BaseMapperX<ZkUseMedicineLogDO> {

    default PageResult<ZkUseMedicineLogDO> selectPage(ZkUseMedicineLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkUseMedicineLogDO>()
                .eqIfPresent(ZkUseMedicineLogDO::getZkUseMedicineId, reqVO.getZkUseMedicineId())
                .eqIfPresent(ZkUseMedicineLogDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkUseMedicineLogDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkUseMedicineLogDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkUseMedicineLogDO::getPondScale, reqVO.getPondScale())
                .eqIfPresent(ZkUseMedicineLogDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkUseMedicineLogDO::getProductNo, reqVO.getProductNo())
                .likeIfPresent(ZkUseMedicineLogDO::getMedicineName, reqVO.getMedicineName())
                .eqIfPresent(ZkUseMedicineLogDO::getMidicineDosage, reqVO.getMidicineDosage())
                .eqIfPresent(ZkUseMedicineLogDO::getControlTitle, reqVO.getControlTitle())
                .eqIfPresent(ZkUseMedicineLogDO::getMedicineAfter, reqVO.getMedicineAfter())
                .eqIfPresent(ZkUseMedicineLogDO::getMedicineBrand, reqVO.getMedicineBrand())
                .eqIfPresent(ZkUseMedicineLogDO::getMedicineSpec, reqVO.getMedicineSpec())
                .betweenIfPresent(ZkUseMedicineLogDO::getFeedTime, reqVO.getFeedTime())
                .eqIfPresent(ZkUseMedicineLogDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ZkUseMedicineLogDO::getFeedType, reqVO.getFeedType())
                .eqIfPresent(ZkUseMedicineLogDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkUseMedicineLogDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkUseMedicineLogDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ZkUseMedicineLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkUseMedicineLogDO::getId));
    }

    default List<ZkUseMedicineLogDO> selectList(ZkUseMedicineLogExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkUseMedicineLogDO>()
                .eqIfPresent(ZkUseMedicineLogDO::getZkUseMedicineId, reqVO.getZkUseMedicineId())
                .eqIfPresent(ZkUseMedicineLogDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkUseMedicineLogDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkUseMedicineLogDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkUseMedicineLogDO::getPondScale, reqVO.getPondScale())
                .eqIfPresent(ZkUseMedicineLogDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkUseMedicineLogDO::getProductNo, reqVO.getProductNo())
                .likeIfPresent(ZkUseMedicineLogDO::getMedicineName, reqVO.getMedicineName())
                .eqIfPresent(ZkUseMedicineLogDO::getMidicineDosage, reqVO.getMidicineDosage())
                .eqIfPresent(ZkUseMedicineLogDO::getControlTitle, reqVO.getControlTitle())
                .eqIfPresent(ZkUseMedicineLogDO::getMedicineAfter, reqVO.getMedicineAfter())
                .eqIfPresent(ZkUseMedicineLogDO::getMedicineBrand, reqVO.getMedicineBrand())
                .eqIfPresent(ZkUseMedicineLogDO::getMedicineSpec, reqVO.getMedicineSpec())
                .betweenIfPresent(ZkUseMedicineLogDO::getFeedTime, reqVO.getFeedTime())
                .eqIfPresent(ZkUseMedicineLogDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ZkUseMedicineLogDO::getFeedType, reqVO.getFeedType())
                .eqIfPresent(ZkUseMedicineLogDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkUseMedicineLogDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkUseMedicineLogDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ZkUseMedicineLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkUseMedicineLogDO::getId));
    }

}
