package cn.iocoder.yudao.module.fish.controller.admin.zkworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 工作目标与计划更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkWorkPlanUpdateReqVO extends ZkWorkPlanBaseVO {

    @Schema(description = "编号", required = true, example = "14178")
    @NotNull(message = "编号不能为空")
    private Long id;

}
