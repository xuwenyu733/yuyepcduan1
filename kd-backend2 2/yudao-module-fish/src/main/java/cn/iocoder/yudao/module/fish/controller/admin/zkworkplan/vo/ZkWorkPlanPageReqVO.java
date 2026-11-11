package cn.iocoder.yudao.module.fish.controller.admin.zkworkplan.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 工作目标与计划分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkWorkPlanPageReqVO extends PageParam {

    @Schema(description = "日计划")
    private String daylyPlan;

    @Schema(description = "周计划")
    private String weeklyPlan;

    @Schema(description = "月度目标")
    private String monthlyGoal;

    @Schema(description = "季度目标")
    private String quarterlyGoal;

    @Schema(description = "年度目标")
    private String annualGoal;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "6144")
    private Long userId;

    @Schema(description = "所属部门", example = "13840")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "14531")
    private String fid;

}
