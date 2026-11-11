package cn.iocoder.yudao.module.fish.service.zkgrowfarmingbatch;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowfarmingbatch.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfarmingbatch.ZkGrowFarmingBatchDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowfarmingbatch.ZkGrowFarmingBatchMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 养殖批次 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkGrowFarmingBatchServiceImpl implements ZkGrowFarmingBatchService {

    @Resource
    private ZkGrowFarmingBatchMapper zkGrowFarmingBatchMapper;

    @Override
    public Long createZkGrowFarmingBatch(ZkGrowFarmingBatchSaveReqVO createReqVO) {
        // 插入
        ZkGrowFarmingBatchDO zkGrowFarmingBatch = BeanUtils.toBean(createReqVO, ZkGrowFarmingBatchDO.class);
        zkGrowFarmingBatchMapper.insert(zkGrowFarmingBatch);

        // 返回
        return zkGrowFarmingBatch.getId();
    }

    @Override
    public void updateZkGrowFarmingBatch(ZkGrowFarmingBatchSaveReqVO updateReqVO) {
        // 校验存在
        validateZkGrowFarmingBatchExists(updateReqVO.getId());
        // 更新
        ZkGrowFarmingBatchDO updateObj = BeanUtils.toBean(updateReqVO, ZkGrowFarmingBatchDO.class);
        zkGrowFarmingBatchMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkGrowFarmingBatch(Long id) {
        // 校验存在
        validateZkGrowFarmingBatchExists(id);
        // 删除
        zkGrowFarmingBatchMapper.deleteById(id);
    }

    @Override
        public void deleteZkGrowFarmingBatchListByIds(List<Long> ids) {
        // 删除
        zkGrowFarmingBatchMapper.deleteByIds(ids);
        }


    private void validateZkGrowFarmingBatchExists(Long id) {
        if (zkGrowFarmingBatchMapper.selectById(id) == null) {
            throw exception(ZK_GROW_FARMING_BATCH_NOT_EXISTS);
        }
    }

    @Override
    public ZkGrowFarmingBatchDO getZkGrowFarmingBatch(Long id) {
        return zkGrowFarmingBatchMapper.selectById(id);
    }

    @Override
    public PageResult<ZkGrowFarmingBatchDO> getZkGrowFarmingBatchPage(ZkGrowFarmingBatchPageReqVO pageReqVO) {
        return zkGrowFarmingBatchMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkGrowFarmingBatchDO> getByIds(Set<Long> batchIds) {
        if(CollectionUtils.isAnyEmpty(batchIds)) return new ArrayList<>();
        LambdaQueryWrapper<ZkGrowFarmingBatchDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ZkGrowFarmingBatchDO::getId, batchIds);
        return zkGrowFarmingBatchMapper.selectList(queryWrapper);
    }

}