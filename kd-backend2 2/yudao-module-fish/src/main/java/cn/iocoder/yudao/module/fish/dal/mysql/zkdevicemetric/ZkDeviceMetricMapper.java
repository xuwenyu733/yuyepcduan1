package cn.iocoder.yudao.module.fish.dal.mysql.zkdevicemetric;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicemetric.ZkDeviceMetricDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric.vo.*;

/**
 * 设备指标 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDeviceMetricMapper extends BaseMapperX<ZkDeviceMetricDO> {

    default PageResult<ZkDeviceMetricDO> selectPage(ZkDeviceMetricPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkDeviceMetricDO>()
                .eqIfPresent(ZkDeviceMetricDO::getMetricCode, reqVO.getMetricCode())
                .likeIfPresent(ZkDeviceMetricDO::getMetricName, reqVO.getMetricName())
                .eqIfPresent(ZkDeviceMetricDO::getUnit, reqVO.getUnit())
                .eqIfPresent(ZkDeviceMetricDO::getDataType, reqVO.getDataType())
                .eqIfPresent(ZkDeviceMetricDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(ZkDeviceMetricDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceMetricDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceMetricDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDeviceMetricDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeviceMetricDO::getId));
    }

    default List<ZkDeviceMetricDO> selectList(ZkDeviceMetricExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkDeviceMetricDO>()
                .eqIfPresent(ZkDeviceMetricDO::getMetricCode, reqVO.getMetricCode())
                .likeIfPresent(ZkDeviceMetricDO::getMetricName, reqVO.getMetricName())
                .eqIfPresent(ZkDeviceMetricDO::getUnit, reqVO.getUnit())
                .eqIfPresent(ZkDeviceMetricDO::getDataType, reqVO.getDataType())
                .eqIfPresent(ZkDeviceMetricDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(ZkDeviceMetricDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkDeviceMetricDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkDeviceMetricDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkDeviceMetricDO::getFid, reqVO.getFid())
                .orderByDesc(ZkDeviceMetricDO::getId));
    }

}
