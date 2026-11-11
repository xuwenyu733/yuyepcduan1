package cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 设备报警更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeviceAlarmUpdateReqVO extends ZkDeviceAlarmBaseVO {

    @Schema(description = "编号", required = true, example = "29019")
    @NotNull(message = "编号不能为空")
    private Long id;
   }
