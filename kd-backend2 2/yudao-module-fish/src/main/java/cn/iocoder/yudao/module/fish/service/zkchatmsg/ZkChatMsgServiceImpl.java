package cn.iocoder.yudao.module.fish.service.zkchatmsg;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsg.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkchatmsg.ZkChatMsgMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 聊天消息 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkChatMsgServiceImpl implements ZkChatMsgService {

    @Resource
    private ZkChatMsgMapper zkChatMsgMapper;

    @Override
    public ZkChatMsgDO createZkChatMsg(ZkChatMsgSaveReqVO createReqVO) {
        // 插入
        ZkChatMsgDO zkChatMsg = BeanUtils.toBean(createReqVO, ZkChatMsgDO.class);
        zkChatMsgMapper.insert(zkChatMsg);

        // 返回
        return zkChatMsg;
    }

    @Override
    public void updateZkChatMsg(ZkChatMsgSaveReqVO updateReqVO) {
        // 校验存在
        validateZkChatMsgExists(updateReqVO.getId());
        // 更新
        ZkChatMsgDO updateObj = BeanUtils.toBean(updateReqVO, ZkChatMsgDO.class);
        zkChatMsgMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkChatMsg(Long id) {
        // 校验存在
        validateZkChatMsgExists(id);
        // 删除
        zkChatMsgMapper.deleteById(id);
    }

    @Override
        public void deleteZkChatMsgListByIds(List<Long> ids) {
        // 删除
        zkChatMsgMapper.deleteByIds(ids);
        }


    private void validateZkChatMsgExists(Long id) {
        if (zkChatMsgMapper.selectById(id) == null) {
            throw exception(ZK_CHAT_MSG_NOT_EXISTS);
        }
    }

    @Override
    public ZkChatMsgDO getZkChatMsg(Long id) {
        return zkChatMsgMapper.selectById(id);
    }

    @Override
    public PageResult<ZkChatMsgDO> getZkChatMsgPage(ZkChatMsgPageReqVO pageReqVO) {
        return zkChatMsgMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkChatMsgDO> getByIds(Set<Long> chatMsgIdSet) {
        QueryWrapper<ZkChatMsgDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("zcm.id", chatMsgIdSet);
        return zkChatMsgMapper.customList(queryWrapper);
    }

}