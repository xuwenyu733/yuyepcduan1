package cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 设备报警 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeviceAlarmRespVO extends ZkDeviceAlarmBaseVO {

    @Schema(description = "编号", required = true, example = "29019")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
