package cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - OA工作计划更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaWorkPlanUpdateReqVO extends OaWorkPlanBaseVO {

    @Schema(description = "编号", required = true, example = "14178")
    @NotNull(message = "编号不能为空")
    private Long id;

}



