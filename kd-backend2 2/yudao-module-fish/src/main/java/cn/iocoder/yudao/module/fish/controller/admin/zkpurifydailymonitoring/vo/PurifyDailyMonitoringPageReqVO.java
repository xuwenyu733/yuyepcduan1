package cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 日常监测记录分页 Request VO")
@Data
public class PurifyDailyMonitoringPageReqVO extends PageParam {

    @Schema(description = "关联流程ID", example = "26895")
    private Long processId;
    private String recordNumber;

    @Schema(description = "监测日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] monitorDate;

    @Schema(description = "溶氧(mg/L)")
    private BigDecimal oxygen;

    @Schema(description = "PH值")
    private BigDecimal ph;

    @Schema(description = "水温(℃)")
    private BigDecimal temperature;

    @Schema(description = "投喂量(kg)")
    private BigDecimal feedAmount;

    @Schema(description = "投喂时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] feedTime;

    @Schema(description = "健康状况", example = "1")
    private String healthStatus;

    @Schema(description = "当日死亡数", example = "30867")
    private Integer deathCount;

    @Schema(description = "当日死亡重量")
    private BigDecimal deathWeight;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "1302")
    private Long deptId;

}