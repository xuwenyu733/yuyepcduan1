package cn.iocoder.yudao.module.fish.dal.mysql.zkdeviceproperties;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceproperties.ZkDevicePropertiesDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeviceproperties.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 设备属性记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDevicePropertiesMapper extends BaseMapperX<ZkDevicePropertiesDO> {

    default PageResult<ZkDevicePropertiesDO> selectPage(ZkDevicePropertiesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkDevicePropertiesDO>()
                .eqIfPresent(ZkDevicePropertiesDO::getDeviceId, reqVO.getDeviceId())
                .eqIfPresent(ZkDevicePropertiesDO::getDeviceTypeId, reqVO.getDeviceTypeId())
                .betweenIfPresent(ZkDevicePropertiesDO::getRecordTime, reqVO.getRecordTime())
                .eqIfPresent(ZkDevicePropertiesDO::getMetricId, reqVO.getMetricId())
                .eqIfPresent(ZkDevicePropertiesDO::getMetricValue, reqVO.getMetricValue())
                .eqIfPresent(ZkDevicePropertiesDO::getDataStatus, reqVO.getDataStatus())
                .eqIfPresent(ZkDevicePropertiesDO::getRawData, reqVO.getRawData())
                .eqIfPresent(ZkDevicePropertiesDO::getDeviceProperties, reqVO.getDeviceProperties())
                .eqIfPresent(ZkDevicePropertiesDO::getImei, reqVO.getImei())
                .eqIfPresent(ZkDevicePropertiesDO::getFirmwareVersion, reqVO.getFirmwareVersion())
                .eqIfPresent(ZkDevicePropertiesDO::getLatitude, reqVO.getLatitude())
                .eqIfPresent(ZkDevicePropertiesDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(ZkDevicePropertiesDO::getCollectionMethod, reqVO.getCollectionMethod())
                .betweenIfPresent(ZkDevicePropertiesDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDevicePropertiesDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDevicePropertiesDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDevicePropertiesDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDevicePropertiesDO::getId));
    }

    default List<ZkDevicePropertiesDO> selectList(ZkDevicePropertiesExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkDevicePropertiesDO>()
                .eqIfPresent(ZkDevicePropertiesDO::getDeviceId, reqVO.getDeviceId())
                .eqIfPresent(ZkDevicePropertiesDO::getDeviceTypeId, reqVO.getDeviceTypeId())
                .betweenIfPresent(ZkDevicePropertiesDO::getRecordTime, reqVO.getRecordTime())
                .eqIfPresent(ZkDevicePropertiesDO::getMetricId, reqVO.getMetricId())
                .eqIfPresent(ZkDevicePropertiesDO::getMetricValue, reqVO.getMetricValue())
                .eqIfPresent(ZkDevicePropertiesDO::getDataStatus, reqVO.getDataStatus())
                .eqIfPresent(ZkDevicePropertiesDO::getRawData, reqVO.getRawData())
                .eqIfPresent(ZkDevicePropertiesDO::getDeviceProperties, reqVO.getDeviceProperties())
                .eqIfPresent(ZkDevicePropertiesDO::getImei, reqVO.getImei())
                .eqIfPresent(ZkDevicePropertiesDO::getFirmwareVersion, reqVO.getFirmwareVersion())
                .eqIfPresent(ZkDevicePropertiesDO::getLatitude, reqVO.getLatitude())
                .eqIfPresent(ZkDevicePropertiesDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(ZkDevicePropertiesDO::getCollectionMethod, reqVO.getCollectionMethod())
                .betweenIfPresent(ZkDevicePropertiesDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDevicePropertiesDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDevicePropertiesDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDevicePropertiesDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDevicePropertiesDO::getId));
    }


    @Select({
            "SELECT * FROM zk_device_properties\n" +
                    "WHERE create_time < (\n" +
                    "\tSELECT MAX(create_time)-#{recentDays} FROM zk_device_properties\n" +
                    ") order by zk_device_properties.id asc limit 500"
    })
    List<ZkDevicePropertiesDO> selectNeedDel(@Param("recentDays") int recentDays);
}