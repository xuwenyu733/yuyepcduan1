package cn.iocoder.yudao.module.infra.websocket.service;

import cn.iocoder.yudao.module.infra.websocket.WebSocketUsers;
import cn.iocoder.yudao.module.infra.websocket.vo.ChatUserItem;
import cn.iocoder.yudao.module.infra.websocket.vo.ChatUserList;
import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 聊天用户处理类
 * @创建人 lr
 * @创建时间 2025-06-13
 * @描述
 */
@Component("chatUserService")
@Slf4j
public class ChatUserServiceImpl implements ChatUserService{

    @PostConstruct
    public void init() {
        log.warn("===>1  ChatUserService init ok!");
    }

    /**
     * 获取在线的用户列表
     * @return
     */
    public ChatUserList selectOnLineUsers(MsgItem msgItem) {
        if(StringUtils.isEmpty(msgItem.getMsg())) return null;
        // 1. 获取对方userId的列表
        String[] userIdArr = msgItem.getMsg().split(",");
        List<ChatUserItem> userItemList = new ArrayList<>();
        // 2. 获取好友是否在线，同时将好友的发送给currUserId的消息存储到当前列表中 站在对方角度，currUserId是他的id
        for(String currUserId : userIdArr) {
            int onLineStatus = 0; // 0:不在线;1:pc在线;2:app在线;3:pc、app都在线;
            for(String pcOnLineUserId : WebSocketUsers.SESSION_MAP.keySet()) {
                if(StringUtils.equals(pcOnLineUserId, currUserId)) {
                    onLineStatus+=1;
                    break;
                }
            }
            for(String appOnLineUserId : WebSocketUsers.APP_SESSION_MAP.keySet()) {
                if(StringUtils.equals(appOnLineUserId, currUserId)) {
                    onLineStatus+=2;
                    break;
                }
            }
            ChatUserItem item = new ChatUserItem().setCurrUserId(currUserId).setOnLineStatus(onLineStatus+"").buildOnLineInfo();
            // 把没接受的消息给接受一下
            enhancerOppositeMsgList(msgItem.getCurrUserId(), item);
            userItemList.add(item);
        }
        ChatUserList chatUserList = new ChatUserList(msgItem.getCurrUserId(), userItemList, msgItem.build_version());

        return chatUserList.build_userItemList();
    }

    /**
     * 把没有接受的消息给发送给最新用户
     * @param currUserId
     * @param item
     */
    public List<MsgItem> enhancerOppositeMsgList(String currUserId, ChatUserItem item) {
        // 1. 获取消息列表
        ArrayDeque<MsgItem> msgQueue = WebSocketUsers.SESSION_MSG_MAP.getOrDefault(currUserId, new ArrayDeque<>());
        // 2. 消息组装
        Iterator<MsgItem> iterator = msgQueue.iterator();
        List<MsgItem> msgItemList = item.getMsgItemList();
        boolean flag = false;
        while (iterator.hasNext()) {
            MsgItem curr = iterator.next();
            if(StringUtils.equals(curr.getOppositeUserId(), item.getOppositeUserId())) {
                curr.setSendType("1"); // 表示用户B已经接受用户A发送的消息
                msgItemList.add(curr);
                flag = true;
            }
        }
        if(flag) item.build_lastMsg(msgItemList.get(0));
        return msgItemList;
    }

}
