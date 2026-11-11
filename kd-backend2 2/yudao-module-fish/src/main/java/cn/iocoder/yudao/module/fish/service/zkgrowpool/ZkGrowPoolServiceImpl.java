package cn.iocoder.yudao.module.fish.service.zkgrowpool;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowpool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowpool.ZkGrowPoolDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowpool.ZkGrowPoolMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 生长模型鱼池 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkGrowPoolServiceImpl implements ZkGrowPoolService {

    @Resource
    private ZkGrowPoolMapper zkGrowPoolMapper;

    @Override
    public Long createZkGrowPool(ZkGrowPoolSaveReqVO createReqVO) {
        // 插入
        ZkGrowPoolDO zkGrowPool = BeanUtils.toBean(createReqVO, ZkGrowPoolDO.class);
        zkGrowPoolMapper.insert(zkGrowPool);

        // 返回
        return zkGrowPool.getId();
    }

    @Override
    public void updateZkGrowPool(ZkGrowPoolSaveReqVO updateReqVO) {
        // 校验存在
        validateZkGrowPoolExists(updateReqVO.getId());
        // 更新
        ZkGrowPoolDO updateObj = BeanUtils.toBean(updateReqVO, ZkGrowPoolDO.class);
        zkGrowPoolMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkGrowPool(Long id) {
        // 校验存在
        validateZkGrowPoolExists(id);
        // 删除
        zkGrowPoolMapper.deleteById(id);
    }

    @Override
        public void deleteZkGrowPoolListByIds(List<Long> ids) {
        // 删除
        zkGrowPoolMapper.deleteByIds(ids);
        }


    private void validateZkGrowPoolExists(Long id) {
        if (zkGrowPoolMapper.selectById(id) == null) {
            throw exception(ZK_GROW_POOL_NOT_EXISTS);
        }
    }

    @Override
    public ZkGrowPoolDO getZkGrowPool(Long id) {
        return zkGrowPoolMapper.selectById(id);
    }

    @Override
    public PageResult<ZkGrowPoolDO> getZkGrowPoolPage(ZkGrowPoolPageReqVO pageReqVO) {
        return zkGrowPoolMapper.selectPage(pageReqVO);
    }

}