package cn.iocoder.yudao.module.infra.websocket;

import cn.iocoder.yudao.framework.common.enums.DeviceTypeEnum;
import cn.iocoder.yudao.module.infra.websocket.service.MsgHandler;
import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

import static cn.iocoder.yudao.module.infra.websocket.WebSocketConfig.baseUserService;

/**
 * websocket 消息处理
 */
@Component
@ServerEndpoint("/websocket/message")
@Slf4j
@Order(99)
public class WebSocketServer {

    /**
     * 默认最多允许同时在线用户数
     */
    public static int socketMaxOnlineCount = 300;
    public static String APP_KEY = DeviceTypeEnum.APP.getDeviceType();
    public static String PC_KEY = DeviceTypeEnum.PC.getDeviceType();
    public static String MAC_KEY = DeviceTypeEnum.MAC.getDeviceType();

    private static final Semaphore SOCKET_SEMAPHORE = new Semaphore(socketMaxOnlineCount);
    private MsgHandler customMsgHandler;

    @PostConstruct
    public void init() {
        log.warn("===>1  WebSocketServer init ok! msgHandler:{},oauth2TokenService: {}", WebSocketConfig.customMsgHandler, WebSocketConfig.oauth2TokenService);
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public synchronized void onOpen(Session session) throws Exception {
        // 尝试获取信号量
        boolean semaphoreFlag = SemaphoreUtils.tryAcquire(SOCKET_SEMAPHORE);
        if (!semaphoreFlag) {
            // 未获取到信号量
            log.error("当前在线人数超过限制数：{}", socketMaxOnlineCount);
            WebSocketUsers.sendMessage(session, "当前在线人数超过限制数：" + socketMaxOnlineCount);
            session.close();
        } else {
            String userId = WebSocketUsers.getParam("userId", session);
            String deviceMac = WebSocketUsers.getParam("deviceMac", session);
            String deviceType = WebSocketUsers.getParam("deviceType", session);
            String accessToken = WebSocketUsers.getParam("accessToken", session);
            Boolean tokenIsEffective = WebSocketConfig.oauth2TokenService.easyCheckToken(accessToken);
            if (tokenIsEffective && userId != null) {
                // 添加用户
                WebSocketUsers.addSession(userId, session, deviceType, deviceMac);
                log.info("用户【userId={}_{} sessionId={} deviceType={}】建立连接，当前连接用户总数：{}", userId, baseUserService.byKey(userId, "nickname"),
                        session.getId(), deviceType, WebSocketUsers.getUsers().size()+WebSocketUsers.APP_SESSION_MAP.size());
                WebSocketUsers.sendMessage(session, String.format("websocket：用户%s_%s连接成功%s", userId, baseUserService.byKey(userId, "nickname"), deviceType));
            } else if(!tokenIsEffective){
                WebSocketUsers.sendMessage(session, String.format("websocket：连接失败 token 过期,用户id:%s_%s", userId, baseUserService.byKey(userId, "nickname")));
            } else {
                WebSocketUsers.sendMessage(session, "websocket：连接失败 用户id 为空");
            }
        }
    }

    /**
     * 连接关闭时处理
     */
    @OnClose
    public synchronized void onClose(Session session) {
        String sessionId = session.getId();
        String userId = WebSocketUsers.getParam("userId", session);
        String deviceType = WebSocketUsers.getParam("deviceType", session);
        log.info("用户【userId={}_{} sessionId={} deviceType={}】关闭连接！", userId, baseUserService.byKey(userId, "nickname"),
                sessionId, deviceType);
        // 移除用户
        WebSocketUsers.removeSession(session);
        // 获取到信号量则需释放
        SemaphoreUtils.release(SOCKET_SEMAPHORE);
    }

    /**
     * 抛出异常时处理
     */
    @OnError
    public synchronized void onError(Session session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            // 关闭连接
            session.close();
        }
        String sessionId = session.getId();
        String userId = WebSocketUsers.getParam("userId", session);
        String deviceType = WebSocketUsers.getParam("deviceType", session);
        log.warn("用户【userId={}_{} sessionId={} deviceType={}】连接异常！异常信息：{}", userId, baseUserService.byKey(userId, "nickname"),
                sessionId, deviceType, exception.getMessage());
        // 移出用户
        WebSocketUsers.removeSession(session);
        // 获取到信号量则需释放
        SemaphoreUtils.release(SOCKET_SEMAPHORE);
    }

    /**
     * 收到客户端消息时调用的方法
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        String userId = WebSocketUsers.getParam("userId", session);
        String deviceMac = WebSocketUsers.getParam("deviceMac", session);
        String nickname = (String) baseUserService.byKey(userId, "nickname");
        String deviceType = WebSocketUsers.getParam("deviceType", session);
        WebSocketUsers.addSession(userId, session, deviceType, deviceMac); // 偶尔会出现 session缺失情况，重复添加，后续可以高并发查看原因
        // 1. 判断 queue里面是否有消息需要发送
        boolean sendNextMsg = WebSocketConfig.customMsgHandler.sendNextMsg(new MsgItem(userId, deviceType));
        // 2. 心跳
        if ("heartCheck".equals(message)) {
            log.debug("Java后台收到用户【userId={}_{}】发送的心跳消息deviceType:{}", userId, nickname, deviceType);
            if(!sendNextMsg) WebSocketUsers.sendMessage(session, String.format("websocket：用户%s_%s一直和后台保持连接，heartCheck成功%s", userId, nickname, deviceType));
            WebSocketConfig.baseWebSocketHandleListHandler(userId, deviceType);
            return;
        }
        // 3. 其他消息
        log.debug("Java后台收到用户【userId={}_{} sessionId={} deviceType={}】发送的消息：{}", userId, nickname, session.getId(), deviceType, message);
        WebSocketConfig.customMsgHandler.onMessage(session, message);
    }
}
