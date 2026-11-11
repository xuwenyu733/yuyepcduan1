package cn.iocoder.yudao.module.fish.controller.admin.zkdevicerecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 设备数据记录更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeviceRecordUpdateReqVO extends ZkDeviceRecordBaseVO {

    @Schema(description = "记录ID", required = true, example = "18156")
    @NotNull(message = "记录ID不能为空")
    private Long id;

}
