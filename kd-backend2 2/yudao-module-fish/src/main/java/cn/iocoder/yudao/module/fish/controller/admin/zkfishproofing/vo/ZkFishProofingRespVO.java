package cn.iocoder.yudao.module.fish.controller.admin.zkfishproofing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 打样记录 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkFishProofingRespVO extends ZkFishProofingBaseVO {

    @Schema(description = "编号", required = true, example = "3372")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
