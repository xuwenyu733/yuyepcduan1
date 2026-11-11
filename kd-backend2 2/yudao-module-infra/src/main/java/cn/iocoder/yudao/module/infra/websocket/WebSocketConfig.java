package cn.iocoder.yudao.module.infra.websocket;

import cn.iocoder.yudao.framework.common.base_api.BaseNotifyService;
import cn.iocoder.yudao.framework.common.base_api.BaseOAuth2TokenService;
import cn.iocoder.yudao.framework.common.base_api.BaseUserService;
import cn.iocoder.yudao.framework.common.base_api.BaseWebSocketHandle;
import cn.iocoder.yudao.framework.common.enums.DeviceTypeEnum;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EnvInfo;
import cn.iocoder.yudao.module.infra.websocket.service.MsgHandler;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.List;
import java.util.Map;

/**
 * websocket 配置
 */
@Configuration
@Slf4j
public class WebSocketConfig {

    public static MsgHandler customMsgHandler;
    public static BaseOAuth2TokenService oauth2TokenService;
    public static BaseNotifyService baseNotifyService;
    public static BaseUserService baseUserService;
    public static List<BaseWebSocketHandle> baseWebSocketHandleList;

    /**
     * 每次心跳，可以触发后台任务，例如 消息通知
     * @param userId socket用户id
     * @param deviceType socket用户设备类型
     */
    public static void baseWebSocketHandleListHandler(String userId, String deviceType) {
        if(EnvInfo.isDev()) {
            log.warn("===>WebSocketConfig 5  每次心跳，可以触发后台任务，例如 消息通知，当前环境为开发环境，不需要执行心跳通知任务");
            return;
        }
        if(EasyListUtil.isEmpty(baseWebSocketHandleList)) {
            log.warn("===>WebSocketConfig 5  每次心跳需执行任务 ， 当前配置的BaseWebSocketHandle列表为空，不需要执行心跳通知任务");
            return;
        }
        if(DeviceTypeEnum.PC.getDeviceType().equals(deviceType)) {
            log.info("===>WebSocketConfig 5  每次心跳需执行任务 ， 当前deviceType为pc，pc端有默认通知，暂时不需要执行心跳通知任务");
            return;
        }
        for(BaseWebSocketHandle item : baseWebSocketHandleList) {
            Map<String, Object> stringObjectMap = item.heartCheckHandle(userId, deviceType);
            log.warn("===>WebSocketConfig 6  每次心跳需执行任务 ， 当前配置:{},执行结果:{}", item.getClass(), JSON.toJSONString(stringObjectMap));
        }
    }


    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Autowired
    public void setCustomMsgHandler(MsgHandler customMsgHandler) {
        WebSocketConfig.customMsgHandler = customMsgHandler;
        log.warn("===>WebSocketConfig 1  WebSocketConfig init ok! msgHandler:{}", WebSocketConfig.customMsgHandler);
    }

    @Autowired
    public void setBaseUserService(BaseUserService baseUserService) {
        WebSocketConfig.baseUserService = baseUserService;
    }

    @Autowired
    public void setOauth2TokenService(BaseOAuth2TokenService oauth2TokenService) {
        WebSocketConfig.oauth2TokenService = oauth2TokenService;
        log.warn("===>WebSocketConfig 2  WebSocketConfig init ok! oauth2TokenService:{}", WebSocketConfig.customMsgHandler);
    }

    @Autowired
    public void setBaseNotifyService(BaseNotifyService baseNotifyService) {
        WebSocketConfig.baseNotifyService = baseNotifyService;
        log.warn("===>WebSocketConfig 3  WebSocketConfig init ok! baseNotifyService:{}", WebSocketConfig.baseNotifyService);
    }

    @Autowired
    public void setBaseWebSocketHandleList(List<BaseWebSocketHandle> baseWebSocketHandleList) {
        WebSocketConfig.baseWebSocketHandleList = baseWebSocketHandleList;
        log.warn("===>WebSocketConfig 4  WebSocketConfig init ok! baseWebSocketHandleList:{}", WebSocketConfig.baseWebSocketHandleList);
    }
}
