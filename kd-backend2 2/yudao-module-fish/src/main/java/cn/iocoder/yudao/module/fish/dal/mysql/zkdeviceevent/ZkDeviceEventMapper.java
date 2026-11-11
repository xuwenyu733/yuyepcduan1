package cn.iocoder.yudao.module.fish.dal.mysql.zkdeviceevent;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceevent.ZkDeviceEventDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeviceevent.vo.*;

/**
 * 设备事件记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDeviceEventMapper extends BaseMapperX<ZkDeviceEventDO> {

    default PageResult<ZkDeviceEventDO> selectPage(ZkDeviceEventPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkDeviceEventDO>()
                .eqIfPresent(ZkDeviceEventDO::getDeviceId, reqVO.getDeviceId())
                .eqIfPresent(ZkDeviceEventDO::getDeviceTypeId, reqVO.getDeviceTypeId())
                .betweenIfPresent(ZkDeviceEventDO::getRecordTime, reqVO.getRecordTime())
                .eqIfPresent(ZkDeviceEventDO::getMetricId, reqVO.getMetricId())
                .eqIfPresent(ZkDeviceEventDO::getMetricValue, reqVO.getMetricValue())
                .eqIfPresent(ZkDeviceEventDO::getDataStatus, reqVO.getDataStatus())
                .eqIfPresent(ZkDeviceEventDO::getRawData, reqVO.getRawData())
                .eqIfPresent(ZkDeviceEventDO::getDeviceEvent, reqVO.getDeviceEvent())
                .betweenIfPresent(ZkDeviceEventDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceEventDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceEventDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDeviceEventDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeviceEventDO::getId));
    }

    default List<ZkDeviceEventDO> selectList(ZkDeviceEventExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkDeviceEventDO>()
                .eqIfPresent(ZkDeviceEventDO::getDeviceId, reqVO.getDeviceId())
                .eqIfPresent(ZkDeviceEventDO::getDeviceTypeId, reqVO.getDeviceTypeId())
                .betweenIfPresent(ZkDeviceEventDO::getRecordTime, reqVO.getRecordTime())
                .eqIfPresent(ZkDeviceEventDO::getMetricId, reqVO.getMetricId())
                .eqIfPresent(ZkDeviceEventDO::getMetricValue, reqVO.getMetricValue())
                .eqIfPresent(ZkDeviceEventDO::getDataStatus, reqVO.getDataStatus())
                .eqIfPresent(ZkDeviceEventDO::getRawData, reqVO.getRawData())
                .eqIfPresent(ZkDeviceEventDO::getDeviceEvent, reqVO.getDeviceEvent())
                .betweenIfPresent(ZkDeviceEventDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceEventDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceEventDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDeviceEventDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeviceEventDO::getId));
    }

}
