package cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * OA工作计划完成情况请求 VO
 */
@Schema(description = "OA工作计划完成情况请求 VO")
@Data
public class OaWorkPlanCompleteReqVO {

    @Schema(description = "编号", required = true, example = "1")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "完成情况：completed-已完成, uncompleted-未完成", required = true, example = "completed")
    @NotNull(message = "完成情况不能为空")
    private String completionStatus;

    @Schema(description = "完成说明", example = "计划已全部完成")
    private String completionComment;

}

