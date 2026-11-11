package cn.iocoder.yudao.module.fish.service.zkexperimentalpool;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpool.ZkExperimentalPoolDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpool.ZkExperimentalPoolMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 实验鱼池 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkExperimentalPoolServiceImpl implements ZkExperimentalPoolService {

    @Resource
    private ZkExperimentalPoolMapper zkExperimentalPoolMapper;

    @Override
    public Long createZkExperimentalPool(ZkExperimentalPoolSaveReqVO createReqVO) {
        // 插入
        ZkExperimentalPoolDO zkExperimentalPool = BeanUtils.toBean(createReqVO, ZkExperimentalPoolDO.class);
        zkExperimentalPoolMapper.insert(zkExperimentalPool);

        // 返回
        return zkExperimentalPool.getId();
    }

    @Override
    public void updateZkExperimentalPool(ZkExperimentalPoolSaveReqVO updateReqVO) {
        // 校验存在
        validateZkExperimentalPoolExists(updateReqVO.getId());
        // 更新
        ZkExperimentalPoolDO updateObj = BeanUtils.toBean(updateReqVO, ZkExperimentalPoolDO.class);
        zkExperimentalPoolMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkExperimentalPool(Long id) {
        // 校验存在
        validateZkExperimentalPoolExists(id);
        // 删除
        zkExperimentalPoolMapper.deleteById(id);
    }

    @Override
        public void deleteZkExperimentalPoolListByIds(List<Long> ids) {
        // 删除
        zkExperimentalPoolMapper.deleteByIds(ids);
        }


    private void validateZkExperimentalPoolExists(Long id) {
        if (zkExperimentalPoolMapper.selectById(id) == null) {
            throw exception(ZK_EXPERIMENTAL_POOL_NOT_EXISTS);
        }
    }

    @Override
    public ZkExperimentalPoolDO getZkExperimentalPool(Long id) {
        return zkExperimentalPoolMapper.selectById(id);
    }

    @Override
    public PageResult<ZkExperimentalPoolDO> getZkExperimentalPoolPage(ZkExperimentalPoolPageReqVO pageReqVO) {
        return zkExperimentalPoolMapper.selectPage(pageReqVO);
    }

}