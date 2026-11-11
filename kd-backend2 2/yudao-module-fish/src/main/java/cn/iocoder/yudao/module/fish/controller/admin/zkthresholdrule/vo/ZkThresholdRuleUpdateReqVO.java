package cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 阈值规则更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkThresholdRuleUpdateReqVO extends ZkThresholdRuleBaseVO {

    @Schema(description = "规则ID", required = true, example = "29187")
    @NotNull(message = "规则ID不能为空")
    private Long id;

}
