package cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - OA工作计划创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaWorkPlanCreateReqVO extends OaWorkPlanBaseVO {

    @Schema(description = "计划类型", required = true, example = "weekly")
    @NotBlank(message = "计划类型不能为空")
    private String type;

    @Schema(description = "计划标题", required = true, example = "第51周工作计划")
    @NotBlank(message = "计划标题不能为空")
    private String title;

    @Schema(description = "计划周期", required = true, example = "2024-12-16 至 2024-12-22")
    @NotBlank(message = "计划周期不能为空")
    private String period;

}



