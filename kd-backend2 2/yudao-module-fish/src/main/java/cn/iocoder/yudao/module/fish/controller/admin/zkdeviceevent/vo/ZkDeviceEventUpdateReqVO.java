package cn.iocoder.yudao.module.fish.controller.admin.zkdeviceevent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 设备事件记录更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeviceEventUpdateReqVO extends ZkDeviceEventBaseVO {

    @Schema(description = "记录ID", required = true, example = "29109")
    @NotNull(message = "记录ID不能为空")
    private Long id;

}
