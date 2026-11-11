package cn.iocoder.yudao.module.fish.service.zkdevicealarm;

import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicealarm.ZkDeviceAlarmDO;
import cn.iocoder.yudao.module.fish.websocket.DeviceAlarmWebSocketMessageListener;
import cn.iocoder.yudao.module.fish.websocket.message.DeviceAlarmMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 设备告警 WebSocket 推送服务
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class ZkDeviceAlarmWebSocketService {

    @Resource
    private DeviceAlarmWebSocketMessageListener deviceAlarmWebSocketMessageListener;

    /**
     * 推送设备告警到前端
     *
     * @param alarmDO 设备告警信息
     */
    public void sendDeviceAlarm(ZkDeviceAlarmDO alarmDO) {
        // 参数校验
        if (alarmDO == null) {
            log.warn("[sendDeviceAlarm][设备告警信息为空，无法推送]");
            return;
        }
        
        try {
            // 构造推送消息
            DeviceAlarmMessage alarmMessage = new DeviceAlarmMessage();
            alarmMessage.setId(alarmDO.getId());
            alarmMessage.setWarnDeviceId(alarmDO.getWarnDeviceId());
            alarmMessage.setWarnDevice(alarmDO.getWarnDevice());
            alarmMessage.setWarnName(alarmDO.getWarnName());
            alarmMessage.setWarnType(alarmDO.getWarnType());
            alarmMessage.setWarnContent(alarmDO.getWarnContent());
            alarmMessage.setUserName(alarmDO.getUserName());
            alarmMessage.setStatus(alarmDO.getStatus());
            alarmMessage.setUserId(alarmDO.getUserId());
            
            // 安全处理创建时间，避免空指针
            LocalDateTime createTime = alarmDO.getCreateTime();
            alarmMessage.setCreateTime(createTime != null ? DateUtils.format(createTime) : "");

            // 推送消息
            deviceAlarmWebSocketMessageListener.sendDeviceAlarmMessageByUsr(alarmMessage);
            
            log.info("[sendDeviceAlarm][设备告警推送成功，告警ID:{}]", alarmDO.getId());
        } catch (Exception e) {
            log.error("[sendDeviceAlarm][推送设备告警消息失败，告警ID:{}]", alarmDO.getId(), e);
        }
    }
}