package cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - fish系统日志更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FishSystemLogUpdateReqVO extends FishSystemLogBaseVO {

    @Schema(description = "编号", required = true, example = "14117")
    @NotNull(message = "编号不能为空")
    private Long id;

}
