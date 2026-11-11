package cn.iocoder.yudao.module.fish.dal.mysql.zkenergymeterreading;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading.vo.ZkEnergyMeterReadingPageReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkenergymeterreading.ZkEnergyMeterReadingDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ZkEnergyMeterReadingMapper extends BaseMapperX<ZkEnergyMeterReadingDO> {

    default PageResult<ZkEnergyMeterReadingDO> selectPage(ZkEnergyMeterReadingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkEnergyMeterReadingDO>()
                .eqIfPresent(ZkEnergyMeterReadingDO::getDeviceId, reqVO.getDeviceId())
                .eqIfPresent(ZkEnergyMeterReadingDO::getDeviceType, reqVO.getDeviceType())
                .likeIfPresent(ZkEnergyMeterReadingDO::getWorkshopName, reqVO.getWorkshopName())
                .betweenIfPresent(ZkEnergyMeterReadingDO::getReadingTime, reqVO.getReadingTime())
                .orderByDesc(ZkEnergyMeterReadingDO::getReadingTime)
                .orderByDesc(ZkEnergyMeterReadingDO::getId));
    }
}
