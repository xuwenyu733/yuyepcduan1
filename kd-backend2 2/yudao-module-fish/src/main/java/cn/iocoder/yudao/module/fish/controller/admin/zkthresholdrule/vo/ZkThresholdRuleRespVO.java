package cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 阈值规则 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkThresholdRuleRespVO extends ZkThresholdRuleBaseVO {

    @Schema(description = "规则ID", required = true, example = "29187")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
