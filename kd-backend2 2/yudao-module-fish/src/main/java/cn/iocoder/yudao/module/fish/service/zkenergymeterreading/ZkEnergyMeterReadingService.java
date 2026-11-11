package cn.iocoder.yudao.module.fish.service.zkenergymeterreading;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading.vo.ZkEnergyMeterReadingPageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading.vo.ZkEnergyMeterReadingSaveReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkenergymeterreading.ZkEnergyMeterReadingDO;

public interface ZkEnergyMeterReadingService {
    Long create(ZkEnergyMeterReadingSaveReqVO createReqVO);
    void update(ZkEnergyMeterReadingSaveReqVO updateReqVO);
    void delete(Long id);
    ZkEnergyMeterReadingDO get(Long id);
    PageResult<ZkEnergyMeterReadingDO> getPage(ZkEnergyMeterReadingPageReqVO pageReqVO);
}
