package cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 每日生长与环境记录新增/修改 Request VO")
@Data
public class ZkGrowDailyRecordSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11058")
    private Long id;

    @Schema(description = "养殖批次ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "25506")
    @NotNull(message = "养殖批次ID不能为空")
    private Long batchId;

    @Schema(description = "养殖天数 (第X天)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "养殖天数 (第X天)不能为空")
    private Integer dayIndex;

    @Schema(description = "记录日期")
    private LocalDateTime recordDate;

    @Schema(description = "当日平均水温")
    private BigDecimal waterTemp;

    @Schema(description = "当日投饵率 (%)")
    private BigDecimal feedRate;

    @Schema(description = "模型预测体重 (g)")
    private BigDecimal predictedWeight;

    @Schema(description = "模型预测体长 (cm)")
    private BigDecimal predictedLength;

    @Schema(description = "视觉识别体重 (g)")
    private BigDecimal visionWeight;

    @Schema(description = "视觉识别体长 (cm)")
    private BigDecimal visionLength;

    @Schema(description = "手动测量体重 (g)")
    private BigDecimal manualWeight;

    @Schema(description = "手动测量体长 (cm)")
    private BigDecimal manualLength;

    @Schema(description = "特定生长率 (SGR)")
    private BigDecimal sgr;
    private Long deptId;
    private Long tenantId;
    private String batchNo;
    private String recommendedFeedRate; // 推荐投饵率 (%)

}