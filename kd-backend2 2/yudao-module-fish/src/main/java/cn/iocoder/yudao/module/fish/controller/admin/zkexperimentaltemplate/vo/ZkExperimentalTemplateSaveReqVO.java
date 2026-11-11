package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 实验模板新增/修改 Request VO")
@Data
public class ZkExperimentalTemplateSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @Schema(description = "模板类型")
    private String templateType;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "品种")
    private String variety;

    @Schema(description = "实验目标")
    private String experimentalObjective;

    @Schema(description = "实验内容")
    private String experimentalContent;

    @Schema(description = "实验方法")
    private String experimentalMethod;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态 0启用 1禁用")
    private Integer status;

    @Schema(description = "所属部门")
    private Long deptId;

}
