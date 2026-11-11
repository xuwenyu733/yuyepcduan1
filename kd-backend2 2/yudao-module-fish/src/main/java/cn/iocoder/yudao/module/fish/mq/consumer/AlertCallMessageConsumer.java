package cn.iocoder.yudao.module.fish.mq.consumer;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.websocket.core.sender.WebSocketMessageSender;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicealarm.ZkDeviceAlarmDO;
import cn.iocoder.yudao.module.fish.mq.entity.ZkDeviceAlarm;
import cn.iocoder.yudao.module.fish.mq.mqConfig.RabbitMQConfig;
import cn.iocoder.yudao.module.fish.websocket.message.DeviceAlarmMessage;
import cn.iocoder.yudao.module.fish.service.zkdevicealarm.ZkDeviceAlarmWebSocketService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class AlertCallMessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(AlertCallMessageConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Resource
    private ZkDeviceAlarmWebSocketService zkDeviceAlarmWebSocketService;

    @Resource
    private WebSocketMessageSender webSocketMessageSender;

    @RabbitListener(queues = RabbitMQConfig.ALERT_CALL_QUEUE2)
    public void handleAlertCallMessage(Message message, Channel channel) {
        long deliveryTag = 0;
        try {
            deliveryTag = message.getMessageProperties().getDeliveryTag();

            // 获取消息内容
            byte[] body = message.getBody();
            String messageBody = new String(body, StandardCharsets.UTF_8);

            // 打印消息日志
            logger.info("接收到 alertCallMessage 消息: {}", messageBody);

            // 解析消息内容
            ZkDeviceAlarm deviceAlarm = null;
            try {
                deviceAlarm = objectMapper.readValue(messageBody, ZkDeviceAlarm.class);
                logger.info("解析后的告警内容: deviceId={}, warnDevice={}, warnName={}, warnContent={}",
                        deviceAlarm.getWarnDeviceId(),
                        deviceAlarm.getWarnDevice(),
                        deviceAlarm.getWarnName(),
                        deviceAlarm.getWarnContent());

            } catch (Exception e) {
                logger.warn("无法解析告警消息内容为ZkDeviceAlarm对象: {}", messageBody, e);
                // 尝试解析为Map格式
                try {
                    Map<String, Object> messageMap = objectMapper.readValue(messageBody, Map.class);
                    logger.info("以Map格式解析消息: {}", messageMap);
                    // 记录Map格式的日志，便于调试
                    logger.info("Map格式消息字段: {}", String.join(", ", messageMap.keySet()));
                } catch (Exception mapException) {
                    logger.error("无法解析告警消息内容为Map格式: {}", messageBody, mapException);
                }
                // 如果无法解析为对象，我们仍然尝试继续处理，但deviceAlarm为null
            }

            // 处理告警消息
            processAlertMessage(deviceAlarm);

            // 手动确认消息前检查通道状态
            if (channel.isOpen()) {
                channel.basicAck(deliveryTag, false);
                logger.debug("已确认 alertCallMessage 消息，delivery tag: {}", deliveryTag);
            } else {
                logger.warn("通道已关闭，无法确认消息，delivery tag: {}", deliveryTag);
            }
        } catch (Exception e) {
            logger.error("处理 alertCallMessage 消息时发生异常: {}", e.getMessage(), e);
            try {
                // 拒绝消息前检查通道状态
                if (channel.isOpen()) {
                    // 拒绝消息并重新入队
                    channel.basicNack(deliveryTag, false, true);
                    logger.warn("消息处理失败，已拒绝消息并重新入队，delivery tag: {}", deliveryTag);
                } else {
                    logger.warn("通道已关闭，无法拒绝消息，delivery tag: {}", deliveryTag);
                }
            } catch (Exception channelException) {
                logger.error("处理消息失败后，拒绝消息时发生异常: {}", channelException.getMessage(), channelException);
            }
        }
    }

    /**
     * 处理告警消息 - ZkDeviceAlarm对象格式
     * @param deviceAlarm 告警对象
     */
    private void processAlertMessage(ZkDeviceAlarm deviceAlarm) {
        // 参数校验
        if (deviceAlarm == null) {
            logger.warn("收到空的告警消息");
            return;
        }
        
        // 使用标准的WebSocket服务发送消息
        try {
            // 转换为DO对象
            ZkDeviceAlarmDO zkDeviceAlarmDO = BeanUtils.toBean(deviceAlarm, ZkDeviceAlarmDO.class);
            // 通过标准WebSocket服务推送告警信息到前端
            zkDeviceAlarmWebSocketService.sendDeviceAlarm(zkDeviceAlarmDO);
            logger.info("成功通过WebSocket推送告警信息到前端: 设备ID={}, 告警名称={}, 告警内容={}",
                    deviceAlarm.getWarnDeviceId(),
                    deviceAlarm.getWarnName(),
                    deviceAlarm.getWarnContent());
        } catch (Exception e) {
            logger.error("通过WebSocket推送告警信息到前端时发生异常: 设备ID={}, 告警名称={}, 告警内容={}",
                deviceAlarm.getWarnDeviceId(),
                deviceAlarm.getWarnName(),
                deviceAlarm.getWarnContent(),
                e);
        }
        
        // 在这里实现具体的告警处理逻辑
        logger.info("处理告警消息: 设备ID={}, 告警名称={}, 告警内容={}",
                deviceAlarm.getWarnDeviceId(),
                deviceAlarm.getWarnName(),
                deviceAlarm.getWarnContent());

        // 示例处理逻辑：
        // 1. 发送邮件通知
        // 2. 发送短信通知
        // 3. 推送微信消息
        // 4. 记录到专门的告警日志表
        // 5. 触发其他业务逻辑
    }
}