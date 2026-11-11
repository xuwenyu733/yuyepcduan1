package cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 请假 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkAskforLeaveRespVO extends ZkAskforLeaveBaseVO {

    @Schema(description = "编号", required = true, example = "259")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
