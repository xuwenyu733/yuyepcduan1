package cn.iocoder.yudao.module.fish.websocket;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.websocket.core.listener.WebSocketMessageListener;
import cn.iocoder.yudao.framework.websocket.core.sender.WebSocketMessageSender;
import cn.iocoder.yudao.framework.websocket.core.util.WebSocketFrameworkUtils;
import cn.iocoder.yudao.module.fish.websocket.message.DeviceAlarmMessage;
import cn.iocoder.yudao.module.infra.websocket.WebSocketUsers;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

/**
 * 设备告警 WebSocket 消息监听器
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class DeviceAlarmWebSocketMessageListener implements WebSocketMessageListener<DeviceAlarmMessage> {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false) // 由于 yudao.websocket.enable 配置项，可以关闭 WebSocket 的功能，所以这里只能不强制注入
    private WebSocketMessageSender webSocketMessageSender;

    @Override
    public void onMessage(WebSocketSession session, DeviceAlarmMessage message) {
        // 设备告警消息不需要客户端发送，只做服务端推送，所以这个方法可以留空
        // 或者可以根据需要处理客户端发来的相关消息
        log.info("[onMessage][会话({}) 收到设备告警消息，内容：{}]", session.getId(), message);
    }

    @Override
    public String getType() {
        return "device-alarm";
    }

    /**
     * 向所有用户发送设备告警消息
     *
     * @param alarmMessage 告警消息
     */
    public void sendDeviceAlarmMessage(DeviceAlarmMessage alarmMessage) {
        try {
            Integer userType = 146;  //代替UserTypeEnum.ADMIN.getValue()
            // 使用基础send方法，发送给所有连接的用户
            webSocketMessageSender.send(userType, "device-alarm", JSONUtil.toJsonStr(alarmMessage));
            log.info("[sendDeviceAlarmMessage][向所有用户发送设备告警消息成功，告警ID:{}]", alarmMessage.getId());
        } catch (Exception e) {
            log.error("[sendDeviceAlarmMessage][向所有用户发送设备告警消息失败，告警ID:{}]", alarmMessage.getId(), e);
        }

    }


    public void sendDeviceAlarmMessageByUsr(DeviceAlarmMessage alarmMessage) {
        try {
            String userId = String.valueOf(alarmMessage.getUserId());
            String version = new Date().getTime()+"";
            // 使用基础send方法，发送给所有连接的用户
            CommonResult<String> sendRes = WebSocketUsers.baseSendMsg(userId, JSONUtil.toJsonStr(alarmMessage), version, "pc", false);
            log.info("[sendDeviceAlarmMessage][向所有用户发送设备告警消息成功，告警:{}]", JSON.toJSONString(sendRes));
        } catch (Exception e) {
            log.error("[sendDeviceAlarmMessage][向所有用户发送设备告警消息失败，告警ID:{}]", alarmMessage.getId(), e);
        }

    }
    /**
     * 向指定用户发送设备告警消息
     *
     * @param userId 用户ID
     * @param alarmMessage 告警消息
     */
    public void sendDeviceAlarmMessage(Long userId, DeviceAlarmMessage alarmMessage) {
        try {
            webSocketMessageSender.sendObject(UserTypeEnum.ADMIN.getValue(), userId,
                    "device-alarm", alarmMessage);
            log.info("[sendDeviceAlarmMessage][向指定用户发送设备告警消息成功，用户ID:{}，告警ID:{}]", userId, alarmMessage.getId());
        } catch (Exception e) {
            log.error("[sendDeviceAlarmMessage][向指定用户发送设备告警消息失败，用户ID:{}，告警ID:{}]", userId, alarmMessage.getId(), e);
        }
    }
    
    /**
     * 测试方法：发送测试告警消息
     * 用于验证WebSocket连接和消息推送功能
     */
    public void sendTestDeviceAlarmMessage() {
        try {
            DeviceAlarmMessage testMessage = new DeviceAlarmMessage();
            testMessage.setId(9999L);
            testMessage.setWarnDeviceId(1001L);
            testMessage.setWarnDevice("测试设备");
            testMessage.setWarnName("测试告警");
            testMessage.setWarnType(1);
            testMessage.setWarnContent("这是一条测试告警消息");
            testMessage.setUserName("系统管理员");
            testMessage.setStatus(0);
            testMessage.setCreateTime("2025-10-28 18:00:00");
            
            sendDeviceAlarmMessage(testMessage);
            log.info("[sendTestDeviceAlarmMessage][测试告警消息发送成功]");
        } catch (Exception e) {
            log.error("[sendTestDeviceAlarmMessage][测试告警消息发送失败]", e);
        }
    }
}