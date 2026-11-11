package cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - OA工作计划审核 Request VO")
@Data
public class OaWorkPlanReviewReqVO {

    @Schema(description = "工作计划编号", required = true, example = "1")
    @NotNull(message = "工作计划编号不能为空")
    private Long id;

    @Schema(description = "审核结果：approved-通过, rejected-拒绝", required = true, example = "approved")
    @NotBlank(message = "审核结果不能为空")
    private String reviewResult;

    @Schema(description = "审核意见")
    private String reviewComment;

}



