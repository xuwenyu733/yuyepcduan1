package cn.iocoder.yudao.module.fish.controller.admin.zkworkshop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 车间更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkWorkshopUpdateReqVO extends ZkWorkshopBaseVO {

    @Schema(description = "编号", required = true, example = "26501")
    @NotNull(message = "编号不能为空")
    private Long id;

}
