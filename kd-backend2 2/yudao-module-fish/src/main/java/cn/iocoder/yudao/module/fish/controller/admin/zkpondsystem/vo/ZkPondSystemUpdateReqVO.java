package cn.iocoder.yudao.module.fish.controller.admin.zkpondsystem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 存塘系统更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPondSystemUpdateReqVO extends ZkPondSystemBaseVO {

    @Schema(description = "id", required = true, example = "16660")
    @NotNull(message = "id不能为空")
    private Long id;

}
