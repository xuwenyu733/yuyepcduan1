package cn.iocoder.yudao.module.fish.controller.admin.zkissue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
* 问题总结 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkIssueBaseVO {

    @Schema(description = "问题标题", required = true)
    @NotNull(message = "问题标题不能为空")
    private String issueTitle;

    @Schema(description = "问题描述")
    private String issueContent;

    @Schema(description = "总结标题")
    private String summaryTitle;

    @Schema(description = "总结内容")
    private String summaryContent;

    @Schema(description = "状态", example = "2")
    private String status;

    @Schema(description = "附件")
    private String fids;

    @Schema(description = "流程实例", example = "27220")
    private String processInstanceId;

    @Schema(description = "记录人", example = "31703")
    private Long userId;

    @Schema(description = "所属部门", example = "3732")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "8247")
    @NotNull(message = "文件id不能为空")
    private String fid;
    private String agentId;

}
