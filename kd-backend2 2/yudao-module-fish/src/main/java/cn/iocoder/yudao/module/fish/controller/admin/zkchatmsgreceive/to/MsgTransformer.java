package cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsgreceive.ZkChatMsgReceiveDO;
import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;
import cn.iocoder.yudao.module.infra.websocket.vo.ReceiveOperateEnum;

import java.time.ZoneId;
import java.util.Objects;

public class MsgTransformer {

    /**
     * 将 群聊或者聊天消息 转化为 websocket 消息
     * @param zkChatMsgDO 消息对象
     * @param zkChatMsgReceiveDO 消息接受对象
     * @return websocket对象
     */
    public static MsgItem transformer(ZkChatMsgDO zkChatMsgDO, ZkChatMsgReceiveDO zkChatMsgReceiveDO) {
        if(zkChatMsgDO == null || zkChatMsgReceiveDO == null) return null;
        // 1. 默认群组消息
        long version = zkChatMsgDO.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        MsgItem msgItem = new MsgItem(zkChatMsgDO.getUserId(), zkChatMsgReceiveDO.getUserId(), zkChatMsgDO.getMsg(),
                ReceiveOperateEnum.CHAT_MSG_FOR_GROUP.getOperateKey(), version + "",
                EasyDateUtil.format(zkChatMsgDO.getCreateTime(), "yyyy-MM-dd HH:mm:ss")).setChatMsgId(zkChatMsgDO.getId())
                .setChatMsgReceiveId(zkChatMsgReceiveDO.getId())
                .setGroupId(zkChatMsgDO.getGroupId())
                .setFid(zkChatMsgDO.getFid())
                .setDeviceType(zkChatMsgDO.getDeviceType())
                .setSelf(Objects.equals(zkChatMsgDO.getUserId(), zkChatMsgReceiveDO.getUserId()));

        // 2. 如果groupId为空、targetUserid不为空，则是一对一聊天消息，需要将 operateKey修改，同时针对自己给自己发送的消息进行修改
        if(zkChatMsgDO.getGroupId() == null && zkChatMsgDO.getTargetUserId() != null) {
            msgItem.setOperateKey(ReceiveOperateEnum.CHAT_MSG_FOR_USER.getOperateKey());
             if(!Objects.equals(zkChatMsgReceiveDO.getUserId(), zkChatMsgDO.getUserId())) return msgItem;
            // 给自己发送消息,需要模拟是 B对方给A自己发的, 原来 currUserId和oppositeUserId都是自己，现在需要将currUserId改成对方id
             msgItem.setCurrUserId(zkChatMsgDO.getTargetUserId().toString());
        }
        return msgItem;
    }

}
