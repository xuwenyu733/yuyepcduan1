package cn.iocoder.yudao.module.fish.service.zkscheduling;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscheduling.ZkSchedulingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkscheduling.ZkSchedulingConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkscheduling.ZkSchedulingMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 排班 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkSchedulingServiceImpl implements ZkSchedulingService {

    @Resource
    private ZkSchedulingMapper zkSchedulingMapper;

    @Override
    public ZkSchedulingDO createZkScheduling(ZkSchedulingCreateReqVO createReqVO) {
        // 插入
        ZkSchedulingDO zkScheduling = BeanUtils.toBean(createReqVO, ZkSchedulingDO.class);
        zkSchedulingMapper.insert(zkScheduling);
        zkScheduling.build_userIds(createReqVO.getUserIds());
        // 返回
        return zkScheduling;
    }

    @Override
    public List<ZkSchedulingDO> updateZkScheduling(ZkSchedulingUpdateReqVO updateReqVO) {
        List<ZkSchedulingDO> res = new ArrayList<>();
        // 校验存在
        ZkSchedulingDO zkSchedulingDO = validateZkSchedulingExists(updateReqVO.getId());
        res.add(zkSchedulingDO);
        // 更新
        ZkSchedulingDO updateObj = BeanUtils.toBean(updateReqVO, ZkSchedulingDO.class);
        zkSchedulingMapper.updateById(updateObj);
        res.add(updateObj);
        return res; // 返回的是 历史任务
    }

    @Override
    public void deleteZkScheduling(Long id) {
        // 校验存在
        validateZkSchedulingExists(id);
        // 删除
        zkSchedulingMapper.deleteById(id);
    }

    private ZkSchedulingDO validateZkSchedulingExists(Long id) {
        ZkSchedulingDO zkSchedulingDO = zkSchedulingMapper.selectById(id);
        if (zkSchedulingDO == null) {
            throw exception(ZK_SCHEDULING_NOT_EXISTS);
        }
        return zkSchedulingDO;
    }

    @Override
    public ZkSchedulingDO getZkScheduling(Long id) {
        return zkSchedulingMapper.selectById(id);
    }

    @Override
    public List<ZkSchedulingDO> getZkSchedulingList(Collection<Long> ids) {
        return zkSchedulingMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkSchedulingDO> getZkSchedulingPage(ZkSchedulingPageReqVO pageReqVO) {
        return zkSchedulingMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkSchedulingDO> getZkSchedulingList(ZkSchedulingExportReqVO exportReqVO) {
        return zkSchedulingMapper.selectList(exportReqVO);
    }

}
