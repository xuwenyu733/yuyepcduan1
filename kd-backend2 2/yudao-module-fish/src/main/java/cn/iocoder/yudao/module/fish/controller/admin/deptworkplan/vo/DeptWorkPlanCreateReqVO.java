package cn.iocoder.yudao.module.fish.controller.admin.deptworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.NotNull;

/**
 * 部门工作计划创建请求 VO
 */
@Schema(description = "管理后台 - 部门工作计划创建请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeptWorkPlanCreateReqVO extends DeptWorkPlanBaseVO {

    @Schema(description = "计划类型", required = true, example = "weekly")
    @NotNull(message = "计划类型不能为空")
    private String type;

    @Schema(description = "计划标题", required = true, example = "第51周工作计划")
    @NotNull(message = "计划标题不能为空")
    private String title;
}



