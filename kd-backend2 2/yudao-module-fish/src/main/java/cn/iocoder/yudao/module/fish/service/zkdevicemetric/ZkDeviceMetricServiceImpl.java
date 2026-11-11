package cn.iocoder.yudao.module.fish.service.zkdevicemetric;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicemetric.ZkDeviceMetricDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkdevicemetric.ZkDeviceMetricConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdevicemetric.ZkDeviceMetricMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 设备指标 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkDeviceMetricServiceImpl implements ZkDeviceMetricService {

    @Resource
    private ZkDeviceMetricMapper zkDeviceMetricMapper;

    @Override
    public Long createZkDeviceMetric(ZkDeviceMetricCreateReqVO createReqVO) {
        // 插入
        ZkDeviceMetricDO zkDeviceMetric = BeanUtils.toBean(createReqVO, ZkDeviceMetricDO.class);
        zkDeviceMetricMapper.insert(zkDeviceMetric);
        // 返回
        return zkDeviceMetric.getId();
    }

    @Override
    public void updateZkDeviceMetric(ZkDeviceMetricUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkDeviceMetricExists(updateReqVO.getId());
        // 更新
        ZkDeviceMetricDO updateObj = BeanUtils.toBean(updateReqVO, ZkDeviceMetricDO.class);
        zkDeviceMetricMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkDeviceMetric(Long id) {
        // 校验存在
        validateZkDeviceMetricExists(id);
        // 删除
        zkDeviceMetricMapper.deleteById(id);
    }

    private void validateZkDeviceMetricExists(Long id) {
        if (zkDeviceMetricMapper.selectById(id) == null) {
            throw exception(ZK_DEVICE_METRIC_NOT_EXISTS);
        }
    }

    @Override
    public ZkDeviceMetricDO getZkDeviceMetric(Long id) {
        return zkDeviceMetricMapper.selectById(id);
    }

    @Override
    public List<ZkDeviceMetricDO> getZkDeviceMetricList(Collection<Long> ids) {
        return zkDeviceMetricMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkDeviceMetricDO> getZkDeviceMetricPage(ZkDeviceMetricPageReqVO pageReqVO) {
        return zkDeviceMetricMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkDeviceMetricDO> getZkDeviceMetricList(ZkDeviceMetricExportReqVO exportReqVO) {
        return zkDeviceMetricMapper.selectList(exportReqVO);
    }

}
