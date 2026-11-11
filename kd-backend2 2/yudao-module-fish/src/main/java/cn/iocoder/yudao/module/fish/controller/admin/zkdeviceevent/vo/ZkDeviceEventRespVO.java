package cn.iocoder.yudao.module.fish.controller.admin.zkdeviceevent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 设备事件记录 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeviceEventRespVO extends ZkDeviceEventBaseVO {

    @Schema(description = "记录ID", required = true, example = "29109")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
