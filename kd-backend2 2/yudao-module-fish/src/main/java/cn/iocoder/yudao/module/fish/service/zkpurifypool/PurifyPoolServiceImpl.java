package cn.iocoder.yudao.module.fish.service.zkpurifypool;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypool.PurifyPoolDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifypool.PurifyPoolMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 鱼池管理 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class PurifyPoolServiceImpl implements PurifyPoolService {

    @Resource
    private PurifyPoolMapper purifyPoolMapper;

    @Override
    public Long createPurifyPool(PurifyPoolSaveReqVO createReqVO) {
        // 插入
        PurifyPoolDO purifyPool = BeanUtils.toBean(createReqVO, PurifyPoolDO.class);
        purifyPoolMapper.insert(purifyPool);

        // 返回
        return purifyPool.getId();
    }

    @Override
    public void updatePurifyPool(PurifyPoolSaveReqVO updateReqVO) {
        // 校验存在
        validatePurifyPoolExists(updateReqVO.getId());
        // 更新
        PurifyPoolDO updateObj = BeanUtils.toBean(updateReqVO, PurifyPoolDO.class);
        purifyPoolMapper.updateById(updateObj);
    }

    @Override
    public void deletePurifyPool(Long id) {
        // 校验存在
        validatePurifyPoolExists(id);
        // 删除
        purifyPoolMapper.deleteById(id);
    }

    @Override
        public void deletePurifyPoolListByIds(List<Long> ids) {
        // 删除
        purifyPoolMapper.deleteByIds(ids);
        }


    private void validatePurifyPoolExists(Long id) {
        if (purifyPoolMapper.selectById(id) == null) {
            throw exception(PURIFY_POOL_NOT_EXISTS);
        }
    }

    @Override
    public PurifyPoolDO getPurifyPool(Long id) {
        return purifyPoolMapper.selectById(id);
    }

    @Override
    public PageResult<PurifyPoolDO> getPurifyPoolPage(PurifyPoolPageReqVO pageReqVO) {
        return purifyPoolMapper.selectPage(pageReqVO);
    }

}