package cn.iocoder.yudao.module.fish.controller.admin.zkfishhealth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 鱼类健康评估 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkFishHealthRespVO extends ZkFishHealthBaseVO {

    @Schema(description = "编号", required = true, example = "23582")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
