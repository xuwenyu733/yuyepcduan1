package cn.iocoder.yudao.module.fish.service.zkdeviceevent;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeviceevent.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceevent.ZkDeviceEventDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkdeviceevent.ZkDeviceEventConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdeviceevent.ZkDeviceEventMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 设备事件记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkDeviceEventServiceImpl implements ZkDeviceEventService {

    @Resource
    private ZkDeviceEventMapper zkDeviceEventMapper;

    @Override
    public Long createZkDeviceEvent(ZkDeviceEventCreateReqVO createReqVO) {
        // 插入
        ZkDeviceEventDO zkDeviceEvent = BeanUtils.toBean(createReqVO, ZkDeviceEventDO.class);
        zkDeviceEventMapper.insert(zkDeviceEvent);
        // 返回
        return zkDeviceEvent.getId();
    }

    @Override
    public void updateZkDeviceEvent(ZkDeviceEventUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkDeviceEventExists(updateReqVO.getId());
        // 更新
        ZkDeviceEventDO updateObj = BeanUtils.toBean(updateReqVO, ZkDeviceEventDO.class);
        zkDeviceEventMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkDeviceEvent(Long id) {
        // 校验存在
        validateZkDeviceEventExists(id);
        // 删除
        zkDeviceEventMapper.deleteById(id);
    }

    private void validateZkDeviceEventExists(Long id) {
        if (zkDeviceEventMapper.selectById(id) == null) {
            throw exception(ZK_DEVICE_EVENT_NOT_EXISTS);
        }
    }

    @Override
    public ZkDeviceEventDO getZkDeviceEvent(Long id) {
        return zkDeviceEventMapper.selectById(id);
    }

    @Override
    public List<ZkDeviceEventDO> getZkDeviceEventList(Collection<Long> ids) {
        return zkDeviceEventMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkDeviceEventDO> getZkDeviceEventPage(ZkDeviceEventPageReqVO pageReqVO) {
        return zkDeviceEventMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkDeviceEventDO> getZkDeviceEventList(ZkDeviceEventExportReqVO exportReqVO) {
        return zkDeviceEventMapper.selectList(exportReqVO);
    }

}
