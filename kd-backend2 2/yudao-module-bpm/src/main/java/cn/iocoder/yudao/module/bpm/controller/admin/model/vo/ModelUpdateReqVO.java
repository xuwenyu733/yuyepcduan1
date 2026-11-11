package cn.iocoder.yudao.module.bpm.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Schema(description = "管理后台 - 流程模型的更新 Request VO")
@Data
public class ModelUpdateReqVO {

    @Schema(description = "标识", required = true, example = "1024")
    @NotEmpty(message = "标识不能为空")
    private String id;

    @Schema(description = "流程名称", example = "芋道")
    private String name;

    @Schema(description = "流程描述", example = "我是描述")
    private String description;

    @Schema(description = "流程分类-参见 bpm_model_category 数据字典", example = "1")
    private String category;

    @Schema(description = "BPMN XML", required = true)
    private String bpmnXml;


}
