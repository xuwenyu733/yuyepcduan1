package cn.iocoder.yudao.module.bpm.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Schema(description = "管理后台 - 流程模型的创建 Request VO")
@Data
public class ModelCreateReqVO {

    @Schema(description = "流程标识", required = true, example = "process_kbs")
    @NotEmpty(message = "流程标识不能为空")
    private String key;

    @Schema(description = "原流程标识", required = true, example = "process_kbs")
    private String srcKey;

    @Schema(description = "流程名称", required = true, example = "芋道")
    @NotEmpty(message = "流程名称不能为空")
    private String name;

    @Schema(description = "流程描述", example = "我是描述")
    private String description;

}
