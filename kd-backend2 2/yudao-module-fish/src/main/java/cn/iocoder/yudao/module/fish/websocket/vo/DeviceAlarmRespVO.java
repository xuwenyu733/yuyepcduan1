package cn.iocoder.yudao.module.fish.websocket.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "设备告警响应 VO")
@Data
public class DeviceAlarmRespVO {

    @Schema(description = "告警编号")
    private Long id;
    
    @Schema(description = "报警设备id")
    private Long warnDeviceId;
    
    @Schema(description = "告警设备名")
    private String warnDevice;
    
    @Schema(description = "告警对象")
    private String warnName;
    
    @Schema(description = "告警类型")
    private Integer warnType;
    
    @Schema(description = "告警描述")
    private String warnContent;
    
    @Schema(description = "负责人")
    private String userName;

    @Schema(description = "负责人id")
    private Long userId;
//    private Long userId;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "告警时间")
    private String createTime;
}