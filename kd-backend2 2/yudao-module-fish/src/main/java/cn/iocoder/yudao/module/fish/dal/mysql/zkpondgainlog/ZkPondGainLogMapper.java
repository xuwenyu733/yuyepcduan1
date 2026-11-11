package cn.iocoder.yudao.module.fish.dal.mysql.zkpondgainlog;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondgainlog.ZkPondGainLogDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondgainlog.vo.*;

/**
 * 鱼生长 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkPondGainLogMapper extends BaseMapperX<ZkPondGainLogDO> {

    default PageResult<ZkPondGainLogDO> selectPage(ZkPondGainLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkPondGainLogDO>()
                .eqIfPresent(ZkPondGainLogDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(ZkPondGainLogDO::getSystemId, reqVO.getSystemId())
                .eqIfPresent(ZkPondGainLogDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkPondGainLogDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkPondGainLogDO::getStaticDay, reqVO.getStaticDay())
                .eqIfPresent(ZkPondGainLogDO::getFishNum, reqVO.getFishNum())
                .eqIfPresent(ZkPondGainLogDO::getFishSpec, reqVO.getFishSpec())
                .eqIfPresent(ZkPondGainLogDO::getFishGainCount, reqVO.getFishGainCount())
                .betweenIfPresent(ZkPondGainLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkPondGainLogDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkPondGainLogDO::getId));
    }

}