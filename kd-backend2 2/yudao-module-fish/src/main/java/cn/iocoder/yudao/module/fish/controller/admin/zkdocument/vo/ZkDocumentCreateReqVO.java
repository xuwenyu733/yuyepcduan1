package cn.iocoder.yudao.module.fish.controller.admin.zkdocument.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 文档创建请求 VO
 */
@Schema(description = "管理后台 - 文档创建请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDocumentCreateReqVO extends ZkDocumentBaseVO {

    @Schema(description = "文档标题", required = true, example = "2024年农业项目申报材料")
    @NotBlank(message = "文档标题不能为空")
    private String title;

    @Schema(description = "文档类型", required = true, example = "申报文档")
    @NotBlank(message = "文档类型不能为空")
    private String type;

    @Schema(description = "文件URL", required = true, example = "https://...")
    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    @Schema(description = "文件大小（字节）", example = "2560000")
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;
}



