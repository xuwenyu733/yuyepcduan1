package cn.iocoder.yudao.module.fish.dal.mysql.zkdevice;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevice.ZkDeviceDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.vo.*;
import cn.iocoder.yudao.module.fish.utils.QueryUtil;

/**
 * 设备 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDeviceMapper extends BaseMapperX<ZkDeviceDO> {

    default PageResult<ZkDeviceDO> selectPage(ZkDevicePageReqVO reqVO) {
        LambdaQueryWrapperX<ZkDeviceDO> wrapper = new LambdaQueryWrapperX<ZkDeviceDO>()
                .likeIfPresent(ZkDeviceDO::getDeviceName, reqVO.getDeviceName())
                .likeIfPresent(ZkDeviceDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkDeviceDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(ZkDeviceDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkDeviceDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkDeviceDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkDeviceDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkDeviceDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkDeviceDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkDeviceDO::getDeviceType, reqVO.getDeviceType())
                .eqIfPresent(ZkDeviceDO::getDeviceMetricId, reqVO.getDeviceMetricId())
                .eqIfPresent(ZkDeviceDO::getDeviceSn, reqVO.getDeviceSn())
                .eqIfPresent(ZkDeviceDO::getPondSystemId, reqVO.getPondSystemId())
                .orderByDesc(ZkDeviceDO::getId);
        QueryUtil.applyDeptFilter(wrapper, ZkDeviceDO::getDeptId, reqVO);
        return selectPage(reqVO, wrapper);
    }

    default List<ZkDeviceDO> selectList(ZkDeviceExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkDeviceDO>()
                .likeIfPresent(ZkDeviceDO::getDeviceName, reqVO.getDeviceName())
                .likeIfPresent(ZkDeviceDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkDeviceDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkDeviceDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkDeviceDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkDeviceDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkDeviceDO::getPondNum, reqVO.getPondNum())
                .eqIfPresent(ZkDeviceDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkDeviceDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDeviceDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkDeviceDO::getDeviceType, reqVO.getDeviceType())
                .orderByDesc(ZkDeviceDO::getId));
    }

}
