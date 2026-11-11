package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 实验模板类型 Response VO")
@Data
public class ZkExperimentalTemplateTypeRespVO {

    @Schema(description = "类型编码")
    private String code;

    @Schema(description = "类型名称")
    private String name;

    public ZkExperimentalTemplateTypeRespVO(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
