package cn.iocoder.yudao.module.fish.controller.admin.deptworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 部门工作计划导出 Request VO")
@Data
public class DeptWorkPlanExportReqVO {

    @Schema(description = "计划标题", example = "第51周工作计划")
    private String title;

    @Schema(description = "计划类型", example = "weekly")
    private String type;

    @Schema(description = "创建者ID", example = "6144")
    private Long userId;

    @Schema(description = "所属部门ID", example = "13840")
    private Long deptId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "提交日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] submitDate;
}

