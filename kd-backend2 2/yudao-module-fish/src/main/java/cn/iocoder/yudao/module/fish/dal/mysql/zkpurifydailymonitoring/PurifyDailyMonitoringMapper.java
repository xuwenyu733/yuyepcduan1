package cn.iocoder.yudao.module.fish.dal.mysql.zkpurifydailymonitoring;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydailymonitoring.PurifyDailyMonitoringDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring.vo.*;

/**
 * 日常监测记录 Mapper
 *
 * @author admin2
 */
@Mapper
public interface PurifyDailyMonitoringMapper extends BaseMapperX<PurifyDailyMonitoringDO> {

    default PageResult<PurifyDailyMonitoringDO> selectPage(PurifyDailyMonitoringPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurifyDailyMonitoringDO>()
                .eqIfPresent(PurifyDailyMonitoringDO::getProcessId, reqVO.getProcessId())
                .betweenIfPresent(PurifyDailyMonitoringDO::getMonitorDate, reqVO.getMonitorDate())
                .eqIfPresent(PurifyDailyMonitoringDO::getOxygen, reqVO.getOxygen())
                .eqIfPresent(PurifyDailyMonitoringDO::getPh, reqVO.getPh())
                .eqIfPresent(PurifyDailyMonitoringDO::getTemperature, reqVO.getTemperature())
                .eqIfPresent(PurifyDailyMonitoringDO::getFeedAmount, reqVO.getFeedAmount())
                .betweenIfPresent(PurifyDailyMonitoringDO::getFeedTime, reqVO.getFeedTime())
                .eqIfPresent(PurifyDailyMonitoringDO::getHealthStatus, reqVO.getHealthStatus())
                .eqIfPresent(PurifyDailyMonitoringDO::getDeathCount, reqVO.getDeathCount())
                .eqIfPresent(PurifyDailyMonitoringDO::getDeathWeight, reqVO.getDeathWeight())
                .betweenIfPresent(PurifyDailyMonitoringDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurifyDailyMonitoringDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(PurifyDailyMonitoringDO::getId));
    }

}