package cn.iocoder.yudao.module.infra.websocket.service;

import cn.iocoder.yudao.framework.common.base_api.fish.BaseChatMsgReceive;
import cn.iocoder.yudao.framework.common.util.easy.EasyArrayDeque;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.module.infra.websocket.WebSocketConfig;
import cn.iocoder.yudao.module.infra.websocket.WebSocketLock;
import cn.iocoder.yudao.module.infra.websocket.WebSocketServer;
import cn.iocoder.yudao.module.infra.websocket.WebSocketUsers;
import cn.iocoder.yudao.module.infra.websocket.vo.ChatUserList;
import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;
import cn.iocoder.yudao.module.infra.websocket.vo.ReceiveOperateEnum;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * WebSocket消息处理器
 * @创建人 lr
 * @创建时间 2025-06-13
 * @描述
 */
@Component("customMsgHandler")
@Slf4j
public class MsgHandlerImpl implements MsgHandler, BeanNameAware {

    @Resource
    private ChatUserService chatUserService;

    @Resource
    private BaseChatMsgReceive baseChatMsgReceive;

    @PostConstruct
    public void init() {
        log.warn("===>1  MsgHandler init ok!");
    }

    /**
     * Java后台接受WebSocket消息统一处理方法
     * @param session
     * @param message
     */
    public void onMessage(Session session, String message) {
        if(StringUtils.contains(message, "isSyncMsg")){
            // 同步消息，需要释放 之前的锁
            asyncMsgResponse(message, false);
        }  else if(StringUtils.contains(message, "isSyncAppMsg")){
            asyncMsgResponse(message, true);
        } else {
            // 通用消息 处理
            MsgItem msgItem = messageTransform(message);
            if(msgItem == null) {
                WebSocketUsers.sendMessage(session, "接收内容：" + message);
                return;
            }
            normalMsgHandle(session, message, msgItem);
        }
    }

    /**
     * 通用 WebSocket 消息 处理入口
     * @param session 链接对象
     * @param message 描述
     * @param msgItem 消息内容
     */
    public void normalMsgHandle(Session session, String message, MsgItem msgItem) {
        if(StringUtils.equals(msgItem.getOperateKey(), ReceiveOperateEnum.ASK_CHAT_LOG_LIST.getOperateKey())) {
            ChatUserList chatUserList = chatUserService.selectOnLineUsers(msgItem);
            // 1. 通知 client，获取当前用户列表信息
            if(chatUserList != null) {
                // String currUserId, String oppositeUserId, String chatTime, String msg, String operateKey, String version, String sendType
                MsgItem targetMsg = new MsgItem(msgItem.getCurrUserId(), msgItem.getCurrUserId(), EasyDateUtil.format2(new Date()), JSON.toJSONString(chatUserList),
                        ReceiveOperateEnum.ASK_CHAT_LOG_LIST.getOperateKey(), msgItem.getVersion(), "1");
                WebSocketUsers.sendMessage(session, JSON.toJSONString(targetMsg));
            }
        } else if (StringUtils.equals(msgItem.getOperateKey(), ReceiveOperateEnum.CHAT_MSG_FOR_USER.getOperateKey())
            || ReceiveOperateEnum.CHAT_MSG_FOR_GROUP.getOperateKey().equals(msgItem.getOperateKey())) {
            a_sendMsg_b(msgItem);
        } else if (StringUtils.equals(msgItem.getOperateKey(), ReceiveOperateEnum.CHAT_MSG_FOR_DELETE.getOperateKey())) {
            handledMsg(msgItem);
        }
    }

    /**
     * 后台接受前台信息，需要发送queue中的下一条消息
     * @param msgItem 当前消息
     */
    public boolean sendNextMsg(MsgItem msgItem) {
        String userId = msgItem.getOppositeUserId();
        // 1. 获取queue
        ArrayDeque<MsgItem> queue = WebSocketUsers.SESSION_MSG_MAP.getOrDefault(userId, new ArrayDeque<>());

        if(msgItem.getDeviceType().contains(WebSocketServer.APP_KEY)) {
            queue = WebSocketUsers.APP_SESSION_MSG_MAP.getOrDefault(userId, new ArrayDeque<>());
        } else if(msgItem.getDeviceType().contains(WebSocketServer.PC_KEY)) {
            queue = WebSocketUsers.SESSION_MSG_MAP.getOrDefault(userId, new ArrayDeque<>());
        }
        if(!queue.isEmpty()) return a_sendMsg_b(queue.remove());
        // 2. 查询 redis 和 zk_chat_msg_receive 进行消息通知
        return baseChatMsgReceive.sendNextMsg(userId);
    }

    @Override
    public boolean handledMsg(MsgItem msgItem) {
        if(msgItem.getChatMsgReceiveId() == null) {
            log.warn("【后台接受用户A{}_{}发来的websocket消息-目的：表示当前用户已收到该消息，后台可以删除该消息】middle,消息的chatMsgReceiveId为空，无需处理,消息：{}", msgItem.getCurrUserId(),
                    WebSocketConfig.baseUserService.byKey(msgItem.getOppositeUserId(), "nickname"), JSON.toJSONString(msgItem));
            return false;
        }
        // 3. 用户收到该消息，后台可以删除该消息
        boolean flag = handledMsgForSession(msgItem, WebSocketUsers.getMsgQueue(msgItem.getDeviceType()));
        // 3.2 更新数据库，避免重复通知
        Object o = baseChatMsgReceive.msgIsHandled(Set.of(msgItem.getChatMsgReceiveId()));
        log.warn("【后台接受用户A{}_{}发来的websocket消息-目的：表示当前用户已收到该消息，后台可以删除该消息】middle,后台接受到的前台消息是：{}，更新数据库结果：{}", msgItem.getCurrUserId(),
                WebSocketConfig.baseUserService.byKey(msgItem.getOppositeUserId(), "nickname"),JSON.toJSONString(msgItem), o);
        // 4. 通知下一条消息
        sendNextMsg(msgItem);
        return flag;
    }

    private static boolean handledMsgForSession(MsgItem msgItem, Map<String, ArrayDeque<MsgItem>> sessionMap) {
        ArrayDeque<MsgItem> oppositeQueue = sessionMap.getOrDefault(msgItem.getOppositeUserId(), new ArrayDeque<>());
        Iterator<MsgItem> iterator = oppositeQueue.iterator();
        boolean flag = false;
        while (!oppositeQueue.isEmpty() && iterator.hasNext()) {
            MsgItem curr = iterator.next();
            if(!(Objects.equals(curr.getChatMsgReceiveId(), msgItem.getChatMsgReceiveId()))) continue;
            // 3.1 删除该消息
            iterator.remove();
            log.warn("【后台接受用户A{}_{}发来的websocket消息-目的：表示当前用户已收到该消息，后台可以删除该消息】middle,后台接受到的前台消息是：{},queue删除的消息是:{}", msgItem.getCurrUserId(),
                    WebSocketConfig.baseUserService.byKey(msgItem.getOppositeUserId(), "nickname"), JSON.toJSONString(msgItem), JSON.toJSONString(curr));
            flag = true;
            break;
        }
        sessionMap.put(msgItem.getOppositeUserId(), oppositeQueue);
        return flag;
    }

    /**
     * a 给 b 发送消息
     * @param msgItem 消息item
     */
    public boolean a_sendMsg_b(MsgItem msgItem) {
        try {
            if(baseChatMsgReceive.judgeIsHandled(msgItem.getChatMsgReceiveId())) {
                log.warn("【websocket a({}_{})_sendMsg_b】middle,消息已经发送过：{},app queue:{},pc queue:{}", msgItem.getCurrUserId(),
                        WebSocketConfig.baseUserService.byKey(msgItem.getOppositeUserId(), "nickname"), JSON.toJSONString(msgItem)
                        , JSON.toJSONString(WebSocketUsers.APP_SESSION_MSG_MAP), JSON.toJSONString(WebSocketUsers.SESSION_MSG_MAP));
                return true;
            }
            // 1. 用户A发送给用户B的消息
            Session pcSession = WebSocketUsers.SESSION_MAP.get(msgItem.getOppositeUserId());
            Session appSession = WebSocketUsers.APP_SESSION_MAP.get(msgItem.getOppositeUserId());
            MsgItem needSendMsg = msgItem.cacheMsg();
            if(msgItem.getDeviceType().contains(WebSocketServer.APP_KEY)) {
                ArrayDeque<MsgItem> oppositeQueue = a_sendMsg_b_exe(WebSocketUsers.APP_SESSION_MSG_MAP, needSendMsg.build_deviceType(WebSocketServer.APP_KEY), appSession,
                        "【后台接受用户A{}发来的websocket消息-目的：表示当前用户需要向B发送消息】middle,当前用户{}app在线，已经向app发送消息needSendMsg：{}，");
                WebSocketUsers.APP_SESSION_MSG_MAP.put(msgItem.getOppositeUserId(), EasyArrayDeque.toSet(oppositeQueue, MsgItem::getChatMsgReceiveId));
            }
            if(msgItem.getDeviceType().contains(WebSocketServer.PC_KEY)) {
                ArrayDeque<MsgItem> oppositeQueue = a_sendMsg_b_exe(WebSocketUsers.SESSION_MSG_MAP, needSendMsg.build_deviceType(WebSocketServer.PC_KEY), pcSession,
                        "【后台接受用户A{}发来的websocket消息-目的：表示当前用户需要向B发送消息】middle,当前用户{}pc在线，已经向app发送消息needSendMsg：{}，");
                WebSocketUsers.SESSION_MSG_MAP.put(msgItem.getOppositeUserId(), EasyArrayDeque.toSet(oppositeQueue, MsgItem::getChatMsgReceiveId));
            }
        } catch (Exception e) {
            String sub = String.format("【websocket通过a_sendMsg_b发送消息异常】send:%s,e:%s", msgItem.build_sendInfo(), e.getMessage());
            String msg = String.format("msgItem:%s, error: %s", JSON.toJSONString(msgItem), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, msg);
            log.error(sub, msg);
            return false;
        }
        return true;
    }

    private static ArrayDeque<MsgItem> a_sendMsg_b_exe(Map<String, ArrayDeque<MsgItem>> appSessionMsgMap, MsgItem needSendMsg, Session session, String s) {
        ArrayDeque<MsgItem> oppositeQueue = appSessionMsgMap.getOrDefault(needSendMsg.getOppositeUserId(), new ArrayDeque<>());
        oppositeQueue.add(needSendMsg);
        if (session == null) return oppositeQueue;
        WebSocketUsers.sendMessage(session, JSON.toJSONString(needSendMsg));
        log.warn(s, needSendMsg.getCurrUserId(), needSendMsg.getOppositeUserId(), JSON.toJSONString(needSendMsg));
        return oppositeQueue;
    }

    /**
     * 将消息转变为 消息类型
     * @param message
     * @return
     */
    public MsgItem messageTransform(String message) {
        try{
            MsgItem msgItem = JSON.parseObject(message, MsgItem.class);
            if(StringUtils.isNotBlank(msgItem.getOperateKey()) && StringUtils.isNotBlank(msgItem.getMsg()))
                return msgItem;
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 后台发送WebSocket消息，等待前台WebSocket响应
     * @param message
     * @param isSyncAppMsg
     */
    private static void asyncMsgResponse(String message, Boolean isSyncAppMsg) {
        String deviceType = isSyncAppMsg ?"app":"pc";
        Map<String, WebSocketLock> appLockMap = isSyncAppMsg ?WebSocketUsers.APP_LOCK_MAP:WebSocketUsers.LOCK_MAP;
        // 同步消息，需要释放 之前的锁
        log.warn("【后台发送同步websocket消息-后台接受到{}前台响应，准备解锁】middle,后台接受到的前台消息是：{}", deviceType, message);
        WebSocketLock clientSendMsg = JSON.parseObject(message, WebSocketLock.class);
        WebSocketLock serverCacheMsg = appLockMap.get(clientSendMsg.selectKey());
        if(serverCacheMsg == null) {
            log.warn("【后台发送同步websocket消息-后台接受到{}前台响应，准备解锁】middle,当前消息锁对象为空，跳过解锁：{}", deviceType, message);
        } else {
            log.warn("【后台发送同步websocket消息-后台接受到{}前台响应，准备解锁】middle,当前消息锁对象不为空，开始 同步前台返回消息：{}", deviceType, message);
            synchronized (serverCacheMsg) {
                serverCacheMsg.build_receiveMsg(clientSendMsg.getReceiveMsg()).notifyAll();
            }
            log.warn("【后台发送同步websocket消息-后台接受到{}前台响应，准备解锁】middle,当前消息锁对象不为空，结束 同步前台返回消息：{}", deviceType, message);
        }
    }

    @Override
    public void setBeanName(String s) {
        log.warn("===>1  MsgHandler init ok! MsgHandlerImpl {}", s);
    }
}
