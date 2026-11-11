package cn.iocoder.yudao.module.fish.controller.admin.zkbiologicalasset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 生物资产评估 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkBiologicalAssetRespVO extends ZkBiologicalAssetBaseVO {

    @Schema(description = "编号", required = true, example = "20950")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
