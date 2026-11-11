package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 用药明细更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkUseMedicineLogUpdateReqVO extends ZkUseMedicineLogBaseVO {

    @Schema(description = "编号", required = true, example = "2832")
    @NotNull(message = "编号不能为空")
    private Long id;

}
