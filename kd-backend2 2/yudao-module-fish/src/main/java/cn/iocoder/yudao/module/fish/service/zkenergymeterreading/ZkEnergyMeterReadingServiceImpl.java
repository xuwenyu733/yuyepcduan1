package cn.iocoder.yudao.module.fish.service.zkenergymeterreading;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading.vo.ZkEnergyMeterReadingPageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading.vo.ZkEnergyMeterReadingSaveReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkenergymeterreading.ZkEnergyMeterReadingDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkenergymeterreading.ZkEnergyMeterReadingMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ZkEnergyMeterReadingServiceImpl implements ZkEnergyMeterReadingService {

    @Resource
    private ZkEnergyMeterReadingMapper mapper;

    @Override
    public Long create(ZkEnergyMeterReadingSaveReqVO createReqVO) {
        ZkEnergyMeterReadingDO row = BeanUtils.toBean(createReqVO, ZkEnergyMeterReadingDO.class);
        mapper.insert(row);
        return row.getId();
    }

    @Override
    public void update(ZkEnergyMeterReadingSaveReqVO updateReqVO) {
        if (updateReqVO.getId() == null || mapper.selectById(updateReqVO.getId()) == null) {
            throw new IllegalArgumentException("电度抄表记录不存在");
        }
        mapper.updateById(BeanUtils.toBean(updateReqVO, ZkEnergyMeterReadingDO.class));
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public ZkEnergyMeterReadingDO get(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public PageResult<ZkEnergyMeterReadingDO> getPage(ZkEnergyMeterReadingPageReqVO pageReqVO) {
        return mapper.selectPage(pageReqVO);
    }
}
