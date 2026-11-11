package cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 日常监测记录新增/修改 Request VO")
@Data
public class PurifyDailyMonitoringSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "21344")
    private Long id;

    @Schema(description = "关联流程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "26895")
    @NotNull(message = "关联流程ID不能为空")
    private Long processId;
    private String recordNumber;

    @Schema(description = "监测日期")
    private LocalDateTime monitorDate;

    @Schema(description = "溶氧(mg/L)")
    private BigDecimal oxygen;

    @Schema(description = "PH值")
    private BigDecimal ph;

    @Schema(description = "水温(℃)")
    private BigDecimal temperature;

    @Schema(description = "投喂量(kg)")
    private BigDecimal feedAmount;

    @Schema(description = "投喂时间")
    private LocalDateTime feedTime;

    @Schema(description = "健康状况", example = "1")
    private String healthStatus;

    @Schema(description = "当日死亡数", example = "30867")
    private Integer deathCount;

    @Schema(description = "当日死亡重量")
    private BigDecimal deathWeight;

    @Schema(description = "所属部门", example = "1302")
    private Long deptId;

}