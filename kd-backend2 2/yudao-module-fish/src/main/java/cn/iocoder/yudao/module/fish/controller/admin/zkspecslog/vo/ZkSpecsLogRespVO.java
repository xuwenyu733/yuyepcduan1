package cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 规格记录 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkSpecsLogRespVO extends ZkSpecsLogBaseVO {

    @Schema(description = "编号", required = true, example = "21165")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
