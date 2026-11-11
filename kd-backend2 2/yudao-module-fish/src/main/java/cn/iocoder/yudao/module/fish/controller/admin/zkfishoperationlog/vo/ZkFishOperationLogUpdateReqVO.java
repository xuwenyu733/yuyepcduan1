package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 鱼苗统计更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkFishOperationLogUpdateReqVO extends ZkFishOperationLogBaseVO {

    @Schema(description = "编号", required = true, example = "7610")
    @NotNull(message = "编号不能为空")
    private Long id;

}
