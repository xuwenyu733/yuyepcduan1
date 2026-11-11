package cn.iocoder.yudao.module.fish.service.zkpondfishparent;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishparent.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishparent.ZkPondFishParentDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkpondfishparent.ZkPondFishParentMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 鱼池操作父 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkPondFishParentServiceImpl implements ZkPondFishParentService {

    @Resource
    private ZkPondFishParentMapper zkPondFishParentMapper;

    @Override
    public Long createZkPondFishParent(ZkPondFishParentSaveReqVO createReqVO) {
        // 插入
        ZkPondFishParentDO zkPondFishParent = BeanUtils.toBean(createReqVO, ZkPondFishParentDO.class);
        zkPondFishParentMapper.insert(zkPondFishParent);

        // 返回
        return zkPondFishParent.getId();
    }

    @Override
    public void updateZkPondFishParent(ZkPondFishParentSaveReqVO updateReqVO) {
        // 校验存在
        validateZkPondFishParentExists(updateReqVO.getId());
        // 更新
        ZkPondFishParentDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondFishParentDO.class);
        zkPondFishParentMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPondFishParent(Long id) {
        // 校验存在
        validateZkPondFishParentExists(id);
        // 删除
        zkPondFishParentMapper.deleteById(id);
    }

    @Override
        public void deleteZkPondFishParentListByIds(List<Long> ids) {
        // 删除
        zkPondFishParentMapper.deleteByIds(ids);
        }


    private void validateZkPondFishParentExists(Long id) {
        if (zkPondFishParentMapper.selectById(id) == null) {
            throw exception(ZK_POND_FISH_PARENT_NOT_EXISTS);
        }
    }

    @Override
    public ZkPondFishParentDO getZkPondFishParent(Long id) {
        return zkPondFishParentMapper.selectById(id);
    }

    @Override
    public PageResult<ZkPondFishParentDO> getZkPondFishParentPage(ZkPondFishParentPageReqVO pageReqVO) {
        return zkPondFishParentMapper.selectPage(pageReqVO);
    }

}