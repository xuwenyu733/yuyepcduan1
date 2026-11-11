package cn.iocoder.yudao.module.fish.service.zkchatgroup;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroup.ZkChatGroupDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkchatgroup.ZkChatGroupMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 群聊组 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkChatGroupServiceImpl implements ZkChatGroupService {

    @Resource
    private ZkChatGroupMapper zkChatGroupMapper;

    @Override
    public ZkChatGroupDO createZkChatGroup(ZkChatGroupSaveReqVO createReqVO) {
        // 插入
        ZkChatGroupDO zkChatGroup = BeanUtils.toBean(createReqVO, ZkChatGroupDO.class);
        zkChatGroupMapper.insert(zkChatGroup);

        // 返回
        return zkChatGroup;
    }

    @Override
    public void updateZkChatGroup(ZkChatGroupSaveReqVO updateReqVO) {
        // 校验存在
        validateZkChatGroupExists(updateReqVO.getId());
        // 更新
        ZkChatGroupDO updateObj = BeanUtils.toBean(updateReqVO, ZkChatGroupDO.class);
        zkChatGroupMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkChatGroup(Long id) {
        // 校验存在
        validateZkChatGroupExists(id);
        // 删除
        zkChatGroupMapper.deleteById(id);
    }

    @Override
        public void deleteZkChatGroupListByIds(List<Long> ids) {
        // 删除
        zkChatGroupMapper.deleteByIds(ids);
        }


    private void validateZkChatGroupExists(Long id) {
        if (zkChatGroupMapper.selectById(id) == null) {
            throw exception(ZK_CHAT_GROUP_NOT_EXISTS);
        }
    }

    @Override
    public ZkChatGroupDO getZkChatGroup(Long id) {
        return zkChatGroupMapper.selectById(id);
    }

    @Override
    public PageResult<ZkChatGroupDO> getZkChatGroupPage(ZkChatGroupPageReqVO reqVO) {
        QueryWrapper<ZkChatGroupDO> queryWrapper = new QueryWrapper<>();
        if(reqVO.getGroupUserId() != null) {
            queryWrapper.eq("zcgu.user_id", reqVO.getGroupUserId());
        }

        queryWrapper.eq("zcg.deleted", 0);
        Page<ZkChatGroupDO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        IPage<ZkChatGroupDO> pageRes = zkChatGroupMapper.customPage(page, queryWrapper);
        return new PageResult<>(pageRes.getRecords(), pageRes.getTotal());
    }

    @Override
    public List<ZkChatGroupDO> getByIds(Set<Long> ids) {
        if(CollectionUtils.isAnyEmpty(ids)) return new ArrayList<>();
        LambdaQueryWrapper<ZkChatGroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ZkChatGroupDO::getId, ids);
        return zkChatGroupMapper.selectList(queryWrapper);
    }

}