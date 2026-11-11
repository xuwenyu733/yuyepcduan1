package cn.iocoder.yudao.module.fish.service.zkprecisionfeeding;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkprecisionfeeding.ZkPrecisionFeedingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkprecisionfeeding.ZkPrecisionFeedingConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkprecisionfeeding.ZkPrecisionFeedingMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 精准投喂 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkPrecisionFeedingServiceImpl implements ZkPrecisionFeedingService {

    @Resource
    private ZkPrecisionFeedingMapper zkPrecisionFeedingMapper;

    @Override
    public Long createZkPrecisionFeeding(ZkPrecisionFeedingCreateReqVO createReqVO) {
        // 插入
        ZkPrecisionFeedingDO zkPrecisionFeeding = BeanUtils.toBean(createReqVO, ZkPrecisionFeedingDO.class);
        zkPrecisionFeedingMapper.insert(zkPrecisionFeeding);
        // 返回
        return zkPrecisionFeeding.getId();
    }

    @Override
    public void updateZkPrecisionFeeding(ZkPrecisionFeedingUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkPrecisionFeedingExists(updateReqVO.getId());
        // 更新
        ZkPrecisionFeedingDO updateObj = BeanUtils.toBean(updateReqVO, ZkPrecisionFeedingDO.class);
        zkPrecisionFeedingMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPrecisionFeeding(Long id) {
        // 校验存在
        validateZkPrecisionFeedingExists(id);
        // 删除
        zkPrecisionFeedingMapper.deleteById(id);
    }

    private void validateZkPrecisionFeedingExists(Long id) {
        if (zkPrecisionFeedingMapper.selectById(id) == null) {
            throw exception(ZK_PRECISION_FEEDING_NOT_EXISTS);
        }
    }

    @Override
    public ZkPrecisionFeedingDO getZkPrecisionFeeding(Long id) {
        return zkPrecisionFeedingMapper.selectById(id);
    }

    @Override
    public List<ZkPrecisionFeedingDO> getZkPrecisionFeedingList(Collection<Long> ids) {
        return zkPrecisionFeedingMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkPrecisionFeedingDO> getZkPrecisionFeedingPage(ZkPrecisionFeedingPageReqVO pageReqVO) {
        return zkPrecisionFeedingMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkPrecisionFeedingDO> getZkPrecisionFeedingList(ZkPrecisionFeedingExportReqVO exportReqVO) {
        return zkPrecisionFeedingMapper.selectList(exportReqVO);
    }

}
