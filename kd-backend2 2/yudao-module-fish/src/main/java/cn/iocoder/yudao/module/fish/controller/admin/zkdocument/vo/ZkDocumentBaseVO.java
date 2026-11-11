package cn.iocoder.yudao.module.fish.controller.admin.zkdocument.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 文档 Base VO，提供给添加、修改、详细的子 VO 使用
 */
@Data
public class ZkDocumentBaseVO {

    @Schema(description = "文档标题", required = true, example = "2024年农业项目申报材料")
    private String title;

    @Schema(description = "文档类型：申报文档、设计文档、实验文档、工作计划、报告文档、其他", example = "申报文档")
    private String type;

    @Schema(description = "文档描述", example = "简要描述文档内容")
    private String description;

    @Schema(description = "作者ID", example = "1")
    private Long authorId;

    @Schema(description = "作者姓名", example = "张三")
    private String author;

    @Schema(description = "部门", example = "研发部")
    private String department;

    @Schema(description = "文件URL", required = true, example = "https://...")
    private String fileUrl;

    @Schema(description = "文件ID", example = "123")
    private Long fileId;

    @Schema(description = "文件大小（字节）", example = "2560000")
    private Long fileSize;

    @Schema(description = "状态：shared-共享, private-私有", example = "shared")
    private String status;

    @Schema(description = "协作者（逗号分隔）", example = "李四,王五")
    private String collaborators;
}



