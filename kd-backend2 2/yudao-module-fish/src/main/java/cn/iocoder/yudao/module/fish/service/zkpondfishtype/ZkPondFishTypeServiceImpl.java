package cn.iocoder.yudao.module.fish.service.zkpondfishtype;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishtype.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishtype.ZkPondFishTypeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkpondfishtype.ZkPondFishTypeMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 鱼苗种类 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkPondFishTypeServiceImpl implements ZkPondFishTypeService {

    @Resource
    private ZkPondFishTypeMapper zkPondFishTypeMapper;

    @Override
    public Long createZkPondFishType(ZkPondFishTypeSaveReqVO createReqVO) {
        // 插入
        ZkPondFishTypeDO zkPondFishType = BeanUtils.toBean(createReqVO, ZkPondFishTypeDO.class);
        zkPondFishTypeMapper.insert(zkPondFishType);

        // 返回
        return zkPondFishType.getId();
    }

    @Override
    public void updateZkPondFishType(ZkPondFishTypeSaveReqVO updateReqVO) {
        // 校验存在
        validateZkPondFishTypeExists(updateReqVO.getId());
        // 更新
        ZkPondFishTypeDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondFishTypeDO.class);
        zkPondFishTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPondFishType(Long id) {
        // 校验存在
        validateZkPondFishTypeExists(id);
        // 删除
        zkPondFishTypeMapper.deleteById(id);
    }

    @Override
        public void deleteZkPondFishTypeListByIds(List<Long> ids) {
        // 删除
        zkPondFishTypeMapper.deleteByIds(ids);
        }


    private void validateZkPondFishTypeExists(Long id) {
        if (zkPondFishTypeMapper.selectById(id) == null) {
            throw exception(ZK_POND_FISH_TYPE_NOT_EXISTS);
        }
    }

    @Override
    public ZkPondFishTypeDO getZkPondFishType(Long id) {
        return zkPondFishTypeMapper.selectById(id);
    }

    @Override
    public PageResult<ZkPondFishTypeDO> getZkPondFishTypePage(ZkPondFishTypePageReqVO pageReqVO) {
        return zkPondFishTypeMapper.selectPage(pageReqVO);
    }

    @Override
    public void saveBatch(List<ZkPondFishTypeDO> list) {
        if (cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.isAnyEmpty(list)) {
            return;
        }
        zkPondFishTypeMapper.insertBatch(list);
    }

    @Override
    public void remove(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ZkPondFishTypeDO> queryWrapper) {
        zkPondFishTypeMapper.delete(queryWrapper);
    }

}