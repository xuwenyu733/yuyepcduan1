package cn.iocoder.yudao.module.fish.controller.admin.zkworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 工作目标与计划 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkWorkPlanBaseVO {

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

    @Schema(description = "负责人id", example = "6144")
    private Long userId;

    @Schema(description = "所属部门", example = "13840")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "14531")
    private String fid;

}
