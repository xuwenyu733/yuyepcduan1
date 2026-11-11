package cn.iocoder.yudao.module.fish.service.zkchatgroupuser;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroupuser.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkchatgroupuser.ZkChatGroupUserMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 群聊成员 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkChatGroupUserServiceImpl implements ZkChatGroupUserService {

    @Resource
    private ZkChatGroupUserMapper zkChatGroupUserMapper;

    @Override
    public Long createZkChatGroupUser(ZkChatGroupUserSaveReqVO createReqVO) {
        // 插入
        ZkChatGroupUserDO zkChatGroupUser = BeanUtils.toBean(createReqVO, ZkChatGroupUserDO.class);
        zkChatGroupUserMapper.insert(zkChatGroupUser);

        // 返回
        return zkChatGroupUser.getId();
    }

    @Override
    public void updateZkChatGroupUser(ZkChatGroupUserSaveReqVO updateReqVO) {
        // 校验存在
        validateZkChatGroupUserExists(updateReqVO.getId());
        // 更新
        ZkChatGroupUserDO updateObj = BeanUtils.toBean(updateReqVO, ZkChatGroupUserDO.class);
        zkChatGroupUserMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkChatGroupUser(Long id) {
        // 校验存在
        validateZkChatGroupUserExists(id);
        // 删除
        zkChatGroupUserMapper.deleteById(id);
    }

    @Override
    public void deleteZkChatGroupUserListByIds(List<Long> ids) {
        // 删除
        zkChatGroupUserMapper.deleteByIds(ids);
    }


    private void validateZkChatGroupUserExists(Long id) {
        if (zkChatGroupUserMapper.selectById(id) == null) {
            throw exception(ZK_CHAT_GROUP_USER_NOT_EXISTS);
        }
    }

    @Override
    public ZkChatGroupUserDO getZkChatGroupUser(Long id) {
        return zkChatGroupUserMapper.selectById(id);
    }

    @Override
    public PageResult<ZkChatGroupUserDO> getZkChatGroupUserPage(ZkChatGroupUserPageReqVO pageReqVO) {
        return zkChatGroupUserMapper.selectPage(pageReqVO);
    }

    @Override
    public void customSaveOrUpdate(Long groupId, List<ZkChatGroupUserDO> groupUserDOS) {
        // 1. 获取当前 已有用户
        if(EasyListUtil.isEmpty(groupUserDOS)) return;
        List<ZkChatGroupUserDO> byIds = getByIds(Set.of(groupId));
        if(EasyListUtil.isEmpty(byIds)) { // 直接全部添加
            zkChatGroupUserMapper.insertBatch(groupUserDOS);
            return;
        }
        // 2. 获取需要新增和删除
        Set<Long> needHasUserIds = EasyListUtil.listToIds(groupUserDOS, ZkChatGroupUserDO::getUserId);
        Set<Long> oldHadUserIds = EasyListUtil.listToIds(byIds, ZkChatGroupUserDO::getUserId);
        Set<Long> oldNeedDeleteIds = byIds.stream().filter(obj -> !needHasUserIds.contains(obj.getUserId())).map(ZkChatGroupUserDO::getId).collect(Collectors.toSet());
        List<ZkChatGroupUserDO> newNeedAddList = groupUserDOS.stream().filter(obj -> !oldHadUserIds.contains(obj.getUserId())).toList();
        // 3. 执行新增
        if(EasyListUtil.isNotEmpty(newNeedAddList)) zkChatGroupUserMapper.insertBatch(newNeedAddList);
        // 4. 执行删除逻辑
        EasyDataMapper.deleteIn(zkChatGroupUserMapper, ZkChatGroupUserDO::getId, oldNeedDeleteIds);
    }

    @Override
    public void customUpdate(List<ZkChatGroupUserDO> groupUserDOS) {
        if (EasyListUtil.isEmpty(groupUserDOS)) return;
        zkChatGroupUserMapper.updateBatch(groupUserDOS);
    }

    @Override
    public List<ZkChatGroupUserDO> getByIds(Set<Long> oIds) {
        return EasyDataMapper.listByValueList(zkChatGroupUserMapper, ZkChatGroupUserDO::getId, oIds);
    }

    @Override
    public List<ZkChatGroupUserDO> getByGroupIds(Set<Long> oIds) {
        return EasyDataMapper.listByValueList(zkChatGroupUserMapper, ZkChatGroupUserDO::getGroupId, oIds);
    }

}