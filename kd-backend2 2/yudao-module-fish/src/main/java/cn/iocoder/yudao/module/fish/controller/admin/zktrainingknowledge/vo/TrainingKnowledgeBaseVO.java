package cn.iocoder.yudao.module.fish.controller.admin.zktrainingknowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 培训知识 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class TrainingKnowledgeBaseVO {

    @Schema(description = "知识名称", example = "李四")
    private String knowledgeName;

    @Schema(description = "知识详情")
    private String knowledgeContent;

    @Schema(description = "知识备注", example = "你说的对")
    private String knowledgeRemark;

    @Schema(description = "状态(0:待入库;1:已入库;-1:已删除;)", example = "1")
    private String status;

    @Schema(description = "负责人id", example = "18268")
    private Long userId;

    @Schema(description = "所属部门", example = "13858")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "14651")
    @NotNull(message = "文件id不能为空")
    private String fid;
    private String agentId;

}
