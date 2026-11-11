package cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 设备指标更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeviceMetricUpdateReqVO extends ZkDeviceMetricBaseVO {

    @Schema(description = "指标ID", required = true, example = "2902")
    @NotNull(message = "指标ID不能为空")
    private Long id;

}
