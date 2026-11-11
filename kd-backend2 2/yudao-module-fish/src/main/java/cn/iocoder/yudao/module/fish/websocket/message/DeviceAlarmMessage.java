package cn.iocoder.yudao.module.fish.websocket.message;

import lombok.Data;

/**
 * 设备告警消息
 *
 * @author 芋道源码
 */
@Data
public class DeviceAlarmMessage {

    /**
     * 告警编号
     */
    private Long id;
    
    /**
     * 报警设备id
     */
    private Long warnDeviceId;
    
    /**
     * 告警设备名
     */
    private String warnDevice;
    
    /**
     * 告警对象
     */
    private String warnName;
    
    /**
     * 告警类型
     */
    private Integer warnType;
    
    /**
     * 告警描述
     */
    private String warnContent;
    
    /**
     * 负责人
     */
    private String userName;

    private Long userId;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 告警时间
     */
    private String createTime;

}