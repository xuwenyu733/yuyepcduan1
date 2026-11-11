package cn.iocoder.yudao.module.fish.controller.admin.zkpondfish.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 存塘鱼苗 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPondFishRespVO extends ZkPondFishBaseVO {

    @Schema(description = "编号", required = true, example = "21716")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String flushInfo;

    private String creator;
    private String updater;
}
