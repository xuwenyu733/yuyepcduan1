package cn.iocoder.yudao.module.fish.controller.admin.zkfcrmodel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 饵料系数FCR模型更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkFcrModelUpdateReqVO extends ZkFcrModelBaseVO {

    @Schema(description = "编号", required = true, example = "18576")
    @NotNull(message = "编号不能为空")
    private Long id;

}
