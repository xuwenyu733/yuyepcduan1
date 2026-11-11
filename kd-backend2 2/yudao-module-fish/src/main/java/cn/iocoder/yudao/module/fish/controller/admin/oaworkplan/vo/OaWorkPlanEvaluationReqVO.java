package cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * OA工作计划评价请求 VO
 */
@Schema(description = "OA工作计划评价请求 VO")
@Data
public class OaWorkPlanEvaluationReqVO {

    @Schema(description = "编号", required = true, example = "1")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "完成质量评价", example = "工作质量优秀，符合预期要求")
    private String qualityEvaluation;

    @Schema(description = "成本与效率评价", example = "成本控制良好，效率较高")
    private String costEfficiencyEvaluation;

    @Schema(description = "评价打分（0-25分）", required = true, example = "20")
    @NotNull(message = "评价打分不能为空")
    @Min(value = 0, message = "评价打分不能小于0")
    @Max(value = 25, message = "评价打分不能大于25")
    private Integer evaluationScore;

}



