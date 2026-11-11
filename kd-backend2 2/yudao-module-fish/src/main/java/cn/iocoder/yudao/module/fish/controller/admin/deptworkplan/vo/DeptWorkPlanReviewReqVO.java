package cn.iocoder.yudao.module.fish.controller.admin.deptworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.NotNull;

/**
 * 部门工作计划审核请求 VO
 */
@Schema(description = "管理后台 - 部门工作计划审核请求 VO")
@Data
public class DeptWorkPlanReviewReqVO {

    @Schema(description = "编号", required = true, example = "1")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "审核意见", example = "审核通过")
    private String reviewComment;

    @Schema(description = "是否通过", required = true, example = "true")
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;
}



