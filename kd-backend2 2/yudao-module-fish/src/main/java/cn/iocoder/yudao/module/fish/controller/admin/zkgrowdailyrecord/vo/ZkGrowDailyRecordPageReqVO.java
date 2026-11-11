package cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 每日生长与环境记录分页 Request VO")
@Data
public class ZkGrowDailyRecordPageReqVO extends PageParam {

    @Schema(description = "养殖批次ID", example = "25506")
    private Long batchId;

    @Schema(description = "养殖天数 (第X天)")
    private Integer dayIndex;

    @Schema(description = "记录日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] recordDate;

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
    private Long deptId;
    private Long tenantId;
    private String batchNo;
    private String recommendedFeedRate; // 推荐投饵率 (%)

}