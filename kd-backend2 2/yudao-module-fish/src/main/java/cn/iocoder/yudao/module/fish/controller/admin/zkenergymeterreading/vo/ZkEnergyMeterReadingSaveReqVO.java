package cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "电度抄表新增/修改")
@Data
public class ZkEnergyMeterReadingSaveReqVO {
    private Long id;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String workshopName;
    private String pondDetailNum;
    private String meterSn;
    @NotNull(message = "抄表时间不能为空")
    private LocalDateTime readingTime;
    private BigDecimal kwhValue;
    private BigDecimal deltaKwh;
    private String remark;
    private Long deptId;
}
