package cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 设备指标 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeviceMetricRespVO extends ZkDeviceMetricBaseVO {

    @Schema(description = "指标ID", required = true, example = "2902")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
