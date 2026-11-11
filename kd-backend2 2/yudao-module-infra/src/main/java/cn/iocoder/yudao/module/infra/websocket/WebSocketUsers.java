package cn.iocoder.yudao.module.infra.websocket;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.iocoder.yudao.module.infra.websocket.WebSocketConfig.baseUserService;

/**
 * websocket 客户端用户
 */
@Slf4j
public class WebSocketUsers {

    /**
     * 用户集
     * 需要登录用户的session？
     */
    public static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>(); // pc端 的websocket链接
    public static final Map<String, Session> APP_SESSION_MAP = new ConcurrentHashMap<>(); // app端 的websocket链接
    public static final Map<String, Session> MAC_SESSION_MAP = new ConcurrentHashMap<>(); // mac端 的websocket链接

    public static final Map<String, String> USERID_MAC_MAP = new ConcurrentHashMap<>(); // mac端链接，传输用户id和mac绑定关系

    public static final Map<String, ArrayDeque<MsgItem>> SESSION_MSG_MAP = new ConcurrentHashMap<>(); // pc端 用户A给用户B发送消息，但是用户B不在线，做消息缓存
    public static final Map<String, ArrayDeque<MsgItem>> APP_SESSION_MSG_MAP = new ConcurrentHashMap<>(); // app端 用户A给用户B发送消息，但是用户B不在线，做消息缓存
    // 同步发送消息 map
    public static Map<String, WebSocketLock> LOCK_MAP = new ConcurrentHashMap<>();
    public static Map<String, WebSocketLock> APP_LOCK_MAP = new ConcurrentHashMap<>();
    public static Map<String, WebSocketLock> MAC_LOCK_MAP = new ConcurrentHashMap<>();

    /**
     * 存储用户
     *
     * @param userId  唯一键
     * @param session 用户信息
     */
    public static synchronized void addSession(String userId, Session session, String deviceType, String deviceMac) {
        if(StringUtils.equals(WebSocketServer.APP_KEY, deviceType)) {
            APP_SESSION_MAP.put(userId, session);
        } else if(StringUtils.equals(WebSocketServer.MAC_KEY, deviceType)) {
            MAC_SESSION_MAP.put(userId, session);
            if(StringUtils.isNotBlank(deviceMac)) USERID_MAC_MAP.put(userId, deviceMac);
        } else {
            SESSION_MAP.put(userId, session);
        }
    }

    /**
     * 移除用户
     * @param session 用户信息
     * @return 移除结果
     */
    public static synchronized boolean removeSession(Session session) {
        String key = null;
        boolean flag = SESSION_MAP.containsValue(session);
        boolean flagApp = APP_SESSION_MAP.containsValue(session);
        boolean flagMac = MAC_SESSION_MAP.containsValue(session);
        if (flag) {
            for (Map.Entry<String, Session> entry : SESSION_MAP.entrySet()) {
                Session value = entry.getValue();
                if (value.equals(session)) {
                    key = entry.getKey();
                    break;
                }
            }
            return removeSession(key);
        } else if(flagApp) {
            for (Map.Entry<String, Session> entry : APP_SESSION_MAP.entrySet()) {
                Session value = entry.getValue();
                if (value.equals(session)) {
                    key = entry.getKey();
                    break;
                }
            }
            return removeSessionApp(key);
        } else if(flagMac) {
            for (Map.Entry<String, Session> entry : MAC_SESSION_MAP.entrySet()) {
                Session value = entry.getValue();
                if (value.equals(session)) {
                    key = entry.getKey();
                    break;
                }
            }
            return removeSessionMac(key);
        } else {
            return true;
        }
    }

    /**
     * 移出用户
     *
     * @param userId 用户id
     */
    public static synchronized boolean removeSession(String userId) {
        log.info("pc端用户【userId={}_{}】退出", userId, baseUserService.byKey(userId, "nickname"));
        Session remove = SESSION_MAP.remove(userId);
        // Session remove2 = APP_SESSION_MAP.remove(userId);
        if (remove != null) {
            boolean containsValue = SESSION_MAP.containsValue(remove);
            log.info("pc端用户【userId={}_{}】退出{}，当前pc端连接用户总数：{}", userId, baseUserService.byKey(userId, "nickname"), containsValue ? "失败" : "成功", SESSION_MAP.size());
            return containsValue;
        } else {
            return true;
        }
    }
    public static synchronized boolean removeSessionApp(String userId) {
        log.info("app端用户【userId={}_{}】退出", userId, baseUserService.byKey(userId, "nickname"));
        Session remove = APP_SESSION_MAP.remove(userId);
        if (remove != null) {
            boolean containsValue = APP_SESSION_MAP.containsValue(remove);
            log.info("app端用户【userId={}_{}】退出{}，当前app端连接用户总数：{}", userId, baseUserService.byKey(userId, "nickname"), containsValue ? "失败" : "成功", APP_SESSION_MAP.size());
            return containsValue;
        } else {
            return true;
        }
    }
    public static synchronized boolean removeSessionMac(String userId) {
        log.info("mac端用户【userId={}_{}】退出", userId, baseUserService.byKey(userId, "nickname"));
        Session remove = MAC_SESSION_MAP.remove(userId);
        if (remove != null) {
            boolean containsValue = MAC_SESSION_MAP.containsValue(remove);
            log.info("mac端用户【userId={}_{}】退出{}，当前mac端连接用户总数：{}", userId, baseUserService.byKey(userId, "nickname"), containsValue ? "失败" : "成功", APP_SESSION_MAP.size());
            return containsValue;
        } else {
            return true;
        }
    }

    /**
     * 获取在线用户列表
     *
     * @return 返回用户集合
     */
    public static Map<String, Session> getUsers() {
        return SESSION_MAP;
    }

    public static Map<String, ArrayDeque<MsgItem>> getMsgQueue(String deviceType) {
        if(WebSocketServer.APP_KEY.equals(deviceType)) return APP_SESSION_MSG_MAP;
        if(WebSocketServer.PC_KEY.equals(deviceType)) return SESSION_MSG_MAP;
        return new HashMap<>();
    }

    /**
     * 向所有在线人发送消息
     *
     * @param message 消息内容
     */
    public static void sendMessageToAll(String message) {
        SESSION_MAP.forEach((userId, session) -> {
            if (session.isOpen()) {
                sendMessage(session, message);
            }
        });
    }

    /**
     * 异步发送文本消息
     *
     * @param session 用户session
     * @param message 消息内容
     */
    public static synchronized void sendMessageAsync(Session session, String message) {
        if (session.isOpen()) {
            //  需要加synchronized锁（synchronized(session)）？单个session创建线程？
            session.getAsyncRemote().sendText(message);
        } else {
            log.warn("用户【session={}】不在线", session.getId());
        }
    }

    /**
     * 同步发送文本消息
     * @param session 用户session
     * @param message 消息内容
     */
    public static synchronized void sendMessage(Session session, String message) {
        try {
            if (session.isOpen()) {
                //  需要加synchronized锁（synchronized(session)）？单个session创建线程？
                session.getBasicRemote().sendText(message);
            } else {
                log.warn("用户【session={}】不在线", session.getId());
            }
        } catch (Exception e) {
            log.error("发送消息异常", e);
        }
    }

    /**
     * 根据用户id发送消息
     * @param userId  用户id
     * @param message 消息内容
     * @return 1:成功;-1:当前没有连接;
     */
    public static synchronized Integer sendMessage(String userId, String message) {
        Session session = SESSION_MAP.get(userId);
        if(session == null || !session.isOpen()) session = APP_SESSION_MAP.get(userId);
        if(session == null || !session.isOpen()) session = MAC_SESSION_MAP.get(userId);
        //判断是否存在该用户的session，并且是否在线
        if (session == null || !session.isOpen()) {
            return -1;
        }
        sendMessage(session, message);
        return 1;
    }
    public static CommonResult<String> syncSendMsg(String userId, String message, String version) {
        return commonSendMsg(userId, message, version, WebSocketServer.PC_KEY);
    }
    public static CommonResult<String> commonSendMsg(String userId, String message, String version , String deviceType) {
        return baseSendMsg(userId, message, version, deviceType, true);
    }
    public static CommonResult<String> baseSendMsg(String userId, String message, String version , String deviceType, boolean wait) {
        try {
            Session session = SESSION_MAP.get(userId);
            if(StringUtils.equals(deviceType, WebSocketServer.APP_KEY))
                session = APP_SESSION_MAP.get(userId);
            if(StringUtils.equals(deviceType, WebSocketServer.MAC_KEY))
                session = MAC_SESSION_MAP.get(userId);
            // 1. 判断是否存在该用户的session，并且是否在线
            if (session == null || !session.isOpen()) {
                return CommonResult.error(-1, String.format("当前用户【userId:%s_%s】的websocket不在线。", userId, baseUserService.byKey(userId, "nickname")));
            }
            // 2. 发送消息，然后阻塞，等待唤醒
            sendMessage(session, message);
            if(!wait) return CommonResult.success("消息发送成功，无需等待消息。");
            WebSocketLock socketLock = new WebSocketLock(userId, message, version);
            if(StringUtils.equals(deviceType, WebSocketServer.APP_KEY))
                APP_LOCK_MAP.put(socketLock.selectKey(), socketLock);
            else if(StringUtils.equals(deviceType, WebSocketServer.MAC_KEY))
                MAC_LOCK_MAP.put(socketLock.selectKey(), socketLock);
            else LOCK_MAP.put(socketLock.selectKey(), socketLock);
            synchronized (socketLock) {
                log.warn("【发送同步websocket消息-等待开始】middle,server->client->server，receiveMsg:{}，deviceType:{}", socketLock.getReceiveMsg(), deviceType);
                socketLock.wait(500); // 2000
                log.warn("【发送同步websocket消息-等待结束】middle,server->client->server，receiveMsg:{}，deviceType:{}", socketLock.getReceiveMsg(), deviceType);
            }
            // 3. 等待前端发送消息过来
            return CommonResult.success(socketLock.getReceiveMsg());
        } catch (Exception e) {
            log.error("【发送同步websocket消息-异常】middle,server->client->server, 异常信息：{}", ExceptionUtil.getStackTrace(e));
            return CommonResult.error(500, String.format("发送同步websocket消息-异常:%s", e.getMessage()));
        }
    }


    /**
     * 获取session中的指定参数值
     *
     * @param key     参数key
     * @param session 用户session
     */
    public static String getParam(@NotNull String key, Session session) {
        // 目前只针对获取一个key的值，后期根据情况拓展多个 或者直接在onClose onOpen上获取参数？
        String value = null;
        Map<String, List<String>> parameters = session.getRequestParameterMap();
        if (MapUtil.isNotEmpty(parameters) && parameters.get(key) != null) {
            value = parameters.get(key).get(0);
        } else {
            String queryString = session.getQueryString();
            if (!StrUtil.isEmpty(queryString)) {
                String[] params = StringUtils.split(queryString, '&');
                for (String paramPair : params) {
                    String[] nameValues = StringUtils.split(paramPair, '=');
                    if (key.equals(nameValues[0])) {
                        value = nameValues[1];
                    }
                }
            }
        }
        return value;
    }

}
