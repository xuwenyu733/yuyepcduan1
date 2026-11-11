package cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 日常监测记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurifyDailyMonitoringRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "21344")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "关联流程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "26895")
    @ExcelProperty("关联流程ID")
    private Long processId;
    private String recordNumber;

    @Schema(description = "监测日期")
    @ExcelProperty("监测日期")
    private LocalDateTime monitorDate;

    @Schema(description = "溶氧(mg/L)")
    @ExcelProperty("溶氧(mg/L)")
    private BigDecimal oxygen;

    @Schema(description = "PH值")
    @ExcelProperty("PH值")
    private BigDecimal ph;

    @Schema(description = "水温(℃)")
    @ExcelProperty("水温(℃)")
    private BigDecimal temperature;

    @Schema(description = "投喂量(kg)")
    @ExcelProperty("投喂量(kg)")
    private BigDecimal feedAmount;

    @Schema(description = "投喂时间")
    @ExcelProperty("投喂时间")
    private LocalDateTime feedTime;

    @Schema(description = "健康状况", example = "1")
    @ExcelProperty("健康状况")
    private String healthStatus;

    @Schema(description = "当日死亡数", example = "30867")
    @ExcelProperty("当日死亡数")
    private Integer deathCount;

    @Schema(description = "当日死亡重量")
    @ExcelProperty("当日死亡重量")
    private BigDecimal deathWeight;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "1302")
    @ExcelProperty("所属部门")
    private Long deptId;

}