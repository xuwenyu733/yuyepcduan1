package cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "电度抄表 Response")
@Data
public class ZkEnergyMeterReadingRespVO {
    private Long id;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String workshopName;
    private String pondDetailNum;
    private String meterSn;
    private LocalDateTime readingTime;
    private BigDecimal kwhValue;
    private BigDecimal deltaKwh;
    private String remark;
    private LocalDateTime createTime;
}
