package cn.iocoder.yudao.module.bpm.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Schema(description = "管理后台 - 流程模型更新状态 Request VO")
@Data
public class ModelUpdateStateReqVO {

    @Schema(description = "标识", required = true, example = "1024")
    @NotNull(message = "标识不能为空")
    private String id;

    @Schema(description = "状态-见 SuspensionState 枚举", required = true, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer state;

}
