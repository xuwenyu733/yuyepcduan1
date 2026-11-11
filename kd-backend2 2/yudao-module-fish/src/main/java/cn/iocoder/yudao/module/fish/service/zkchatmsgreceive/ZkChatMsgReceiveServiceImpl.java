package cn.iocoder.yudao.module.fish.service.zkchatmsgreceive;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsg.to.ChatMsgHandleTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive.to.MsgTransformer;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import cn.iocoder.yudao.module.fish.dal.redis.RedisKeyConstants;
import cn.iocoder.yudao.module.fish.service.zkchatgroupuser.ZkChatGroupUserService;
import cn.iocoder.yudao.module.fish.service.zkchatmsg.ZkChatMsgService;
import cn.iocoder.yudao.module.infra.websocket.service.MsgHandler;
import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsgreceive.ZkChatMsgReceiveDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkchatmsgreceive.ZkChatMsgReceiveMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 聊天消息接受 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
@Slf4j
public class ZkChatMsgReceiveServiceImpl implements ZkChatMsgReceiveService {

    @Resource
    private ZkChatMsgReceiveMapper zkChatMsgReceiveMapper;

    @Autowired
    private AdminUserApi adminUserApi;

    @Autowired
    @Lazy
    private ZkChatGroupUserService zkChatGroupUserService;

    @Autowired
    @Lazy
    private ZkChatMsgService zkChatMsgService;

    @Autowired
    @Lazy
    private MsgHandler msgHandler;

    @Resource
    private PlusRedisService plusRedisService;

    @Override
    public Long createZkChatMsgReceive(ZkChatMsgReceiveSaveReqVO createReqVO) {
        // 插入
        ZkChatMsgReceiveDO zkChatMsgReceive = BeanUtils.toBean(createReqVO, ZkChatMsgReceiveDO.class);
        zkChatMsgReceiveMapper.insert(zkChatMsgReceive);

        // 返回
        return zkChatMsgReceive.getId();
    }

    @Override
    public void updateZkChatMsgReceive(ZkChatMsgReceiveSaveReqVO updateReqVO) {
        // 校验存在
        validateZkChatMsgReceiveExists(updateReqVO.getId());
        // 更新
        ZkChatMsgReceiveDO updateObj = BeanUtils.toBean(updateReqVO, ZkChatMsgReceiveDO.class);
        zkChatMsgReceiveMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkChatMsgReceive(Long id) {
        // 校验存在
        validateZkChatMsgReceiveExists(id);
        // 删除
        zkChatMsgReceiveMapper.deleteById(id);
    }

    @Override
        public void deleteZkChatMsgReceiveListByIds(List<Long> ids) {
        // 删除
        zkChatMsgReceiveMapper.deleteByIds(ids);
        }


    private void validateZkChatMsgReceiveExists(Long id) {
        if (zkChatMsgReceiveMapper.selectById(id) == null) {
            throw exception(ZK_CHAT_MSG_RECEIVE_NOT_EXISTS);
        }
    }

    @Override
    public ZkChatMsgReceiveDO getZkChatMsgReceive(Long id) {
        return zkChatMsgReceiveMapper.selectById(id);
    }

    @Override
    public PageResult<ZkChatMsgReceiveDO> getZkChatMsgReceivePage(ZkChatMsgReceivePageReqVO pageReqVO) {
        return zkChatMsgReceiveMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkChatMsgReceiveDO> customInsert(ZkChatMsgDO zkChatMsg) {
        if(zkChatMsg.getGroupId() != null) {
            // 1. 群聊消息
            List<ZkChatGroupUserDO> groupUserList = zkChatGroupUserService.getByGroupIds(Set.of(zkChatMsg.getGroupId()));
            if(EasyListUtil.isEmpty(groupUserList)) return null;
            // 2. 添加用户消息
            List<ZkChatMsgReceiveDO> needAddList = groupUserList.stream() // .filter(obj -> !Objects.equals(obj.getUserId(), zkChatMsg.getUserId()))
                    .map(obj -> new ZkChatMsgReceiveDO(zkChatMsg.getId(), obj.getUserId(),
                            ChatMsgHandleTypeEnum.NEED_NOTICE.getType(), obj.getDeptId())).toList();
            zkChatMsgReceiveMapper.insertBatch(needAddList);
            // 3. 方便后续使用，设置
            return needAddList.stream().map(obj -> obj.setZkChatMsgDO(zkChatMsg)).toList();
        } else if(zkChatMsg.getTargetUserId() != null) {
            // 1. 用户消息
            ZkChatMsgReceiveDO currReceive = new ZkChatMsgReceiveDO(zkChatMsg.getId(), zkChatMsg.getUserId(),
                    ChatMsgHandleTypeEnum.NEED_NOTICE.getType(), zkChatMsg.getDeptId()); // 给自己发送消息,需要模拟是 B对方给A自己发的
            ZkChatMsgReceiveDO oppositeReceive = new ZkChatMsgReceiveDO(zkChatMsg.getId(), zkChatMsg.getTargetUserId(),
                    ChatMsgHandleTypeEnum.NEED_NOTICE.getType(), adminUserApi.getUser(zkChatMsg.getTargetUserId()).getDeptId()); // 给对方用户发送消息
            List<ZkChatMsgReceiveDO> tList = List.of(currReceive, oppositeReceive);
            zkChatMsgReceiveMapper.insertBatch(tList);
            // 2. 方便后续使用，设置
            return tList.stream().map(obj -> obj.setZkChatMsgDO(zkChatMsg)).toList();
        }
        return null;
    }

    @Override
    public boolean notifyMsgList(List<ZkChatMsgReceiveDO> msgList) {
        try {
            if(EasyListUtil.isEmpty(msgList)) return false;
            // 1. 找 需要 填充 消息DO 的列表
            List<ZkChatMsgReceiveDO> needFillMsgDO = msgList.stream().filter(obj -> obj.getZkChatMsgDO() == null).toList();
            if(EasyListUtil.isNotEmpty(needFillMsgDO)) {
                Set<Long> chatMsgIdSet = needFillMsgDO.stream().map(ZkChatMsgReceiveDO::getChatMsgId).collect(Collectors.toSet());
                List<ZkChatMsgDO> oList = zkChatMsgService.getByIds(chatMsgIdSet);
                CommonUtil.origins2Targets4(oList, ZkChatMsgDO::getId, msgList, ZkChatMsgReceiveDO::getChatMsgId, (o, t) -> {
                    t.setZkChatMsgDO(o);
                });
            }
            // 2. 转换成 对应的 socket消息
            List<MsgItem> list = msgList.stream().map(obj -> MsgTransformer.transformer(obj.getZkChatMsgDO(), obj)).filter(Objects::nonNull).toList();
            // 3. websocket 消息通知
            for (MsgItem item : list) {
                msgHandler.normalMsgHandle(null, "", item);
            }
            return true;
        } catch (Exception e) {
            String sub = String.format("【ZkChatMsgReceiveServiceImpl notifyMsgList 异常】e:%s", e.getMessage());
            String msg = String.format("消息内容:%s, error: %s", JSON.toJSONString(msgList), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, msg);
            log.error(sub, msg);
            return false;
        }
    }

    @Override
    public List<ZkChatMsgReceiveDO> getNeedNoticeMsgList(String userId) {
        LambdaQueryWrapper<ZkChatMsgReceiveDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ZkChatMsgReceiveDO::getUserId, userId)
                .eq(ZkChatMsgReceiveDO::getHandleType, ChatMsgHandleTypeEnum.NEED_NOTICE.getType());
        return zkChatMsgReceiveMapper.selectList(queryWrapper);
    }

    @Override
    public Object msgIsHandled(Set<Long> chatMsgReceiveIdSet) {
        if(CollectionUtils.isAnyEmpty(chatMsgReceiveIdSet)) return "chatMsgReceiveIdSet参数不能为空";
        // 1. redis缓存已经 处理的消息
        List<Long> hadHandledIds = plusRedisService.selectArr(RedisKeyConstants.HAD_HANDLED_CHAT_MSG_RECEIVE_ID, Long.class);
        if(hadHandledIds == null) return "redis异常";
        hadHandledIds.addAll(chatMsgReceiveIdSet.stream().filter(obj -> !hadHandledIds.contains(obj)).toList());
        plusRedisService.insertStr(RedisKeyConstants.HAD_HANDLED_CHAT_MSG_RECEIVE_ID, JSON.toJSONString(hadHandledIds), 60*24*30*12L);
        // 2. 查询消息
        List<ZkChatMsgReceiveDO> oList = EasyDataMapper.listByValueList(zkChatMsgReceiveMapper, ZkChatMsgReceiveDO::getId, chatMsgReceiveIdSet);
        if(EasyListUtil.isEmpty(oList)) return "暂无需要处理的消息";
        List<ZkChatMsgReceiveDO> needUpdate = oList.stream().filter(obj -> Objects.equals(ChatMsgHandleTypeEnum.NEED_NOTICE.getType(), obj.getHandleType())).toList();
        if(EasyListUtil.isEmpty(needUpdate)) return "暂无需要处理的消息";
        // 3. 更新数据库
        needUpdate.forEach(obj -> obj.setHandleType(ChatMsgHandleTypeEnum.HAD_NOTICE.getType()));
        zkChatMsgReceiveMapper.updateBatch(needUpdate);
        return String.format("更新的条数为：%s", needUpdate.size());
    }

    @Override
    public boolean sendNextMsg(String userId) {
        // 1. 从 redis 获取 需要通知的 消息
        String key = String.format("%s_%s", RedisKeyConstants.NEED_NOTICE_CHAT_MSG, userId);
        List<ZkChatMsgReceiveDO> oList = plusRedisService.selectArr(key, ZkChatMsgReceiveDO.class);
        if(EasyListUtil.isNotEmpty(oList)) {
            // 2. 判断消息是否已经处理过
            List<Long> hadHandledIds = plusRedisService.selectArr(RedisKeyConstants.HAD_HANDLED_CHAT_MSG_RECEIVE_ID, Long.class);
            List<ZkChatMsgReceiveDO> needNotice = oList.stream().filter(obj -> !hadHandledIds.contains(obj.getId())).toList();
            if(notifyMsgList(needNotice)) return true;
        }
        // 2. 如果 redis 没有， 则需要查询数据库
        List<ZkChatMsgReceiveDO> needNoticeMsgList = getNeedNoticeMsgList(userId);
        if(EasyListUtil.isEmpty(needNoticeMsgList)) return false;
        plusRedisService.insertStr(key, JSON.toJSONString(needNoticeMsgList), 60*24*30*12L);

        return notifyMsgList(needNoticeMsgList);
    }

    @Override
    public boolean judgeIsHandled(Long chatMsgReceiveId) {
        List<Long> hadHandledIds = plusRedisService.selectArr(RedisKeyConstants.HAD_HANDLED_CHAT_MSG_RECEIVE_ID, Long.class);
        if(hadHandledIds == null) return false;
        return hadHandledIds.contains(chatMsgReceiveId);
    }
}