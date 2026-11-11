package cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 死亡和存活率更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeathSurvivalRateUpdateReqVO extends ZkDeathSurvivalRateBaseVO {

    @Schema(description = "编号", required = true, example = "21329")
    @NotNull(message = "编号不能为空")
    private Long id;

}
