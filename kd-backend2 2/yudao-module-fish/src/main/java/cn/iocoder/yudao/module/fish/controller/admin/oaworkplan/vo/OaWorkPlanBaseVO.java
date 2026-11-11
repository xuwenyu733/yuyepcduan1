package cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

/**
 * OA工作计划 Base VO，提供给添加、修改、详细的子 VO 使用
 */
@Data
public class OaWorkPlanBaseVO {

    @Schema(description = "计划类型：weekly-周计划, monthly-月计划, quarterly-季度计划, annual-年度计划", example = "weekly")
    private String type;

    @Schema(description = "计划标题", example = "第51周工作计划")
    private String title;

    @Schema(description = "计划周期", example = "2024-12-16 至 2024-12-22")
    private String period;

    @Schema(description = "工作内容（周计划为JSON格式，其他为文本）")
    private String content;

    @Schema(description = "状态：pending-待审核, approved-已批准, completed-已完成, draft-草稿, rejected-已拒绝", example = "pending")
    private String status;

    @Schema(description = "完成度（百分比，0-100）", example = "95")
    private Integer completionRate;

    @Schema(description = "质量评分（0-5分）", example = "4.5")
    private Double qualityScore;

    @Schema(description = "完成情况：completed-已完成, uncompleted-未完成", example = "completed")
    private String completionStatus;

    @Schema(description = "完成说明")
    private String completionComment;

    @Schema(description = "完成质量评价")
    private String qualityEvaluation;

    @Schema(description = "成本与效率评价")
    private String costEfficiencyEvaluation;

    @Schema(description = "评价打分（0-25分）")
    private Integer evaluationScore;

    @Schema(description = "评价人ID")
    private Long evaluatorId;

    @Schema(description = "评价人姓名")
    private String evaluatorName;

    @Schema(description = "评价时间")
    private LocalDateTime evaluationTime;

    @Schema(description = "提交日期")
    private LocalDateTime submitDate;

    @Schema(description = "审核人ID")
    private Long reviewerId;

    @Schema(description = "审核人姓名")
    private String reviewerName;

    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "审核意见")
    private String reviewComment;

    @Schema(description = "附件（JSON数组）")
    private String attachments;

    @Schema(description = "创建者ID（负责人）", example = "6144")
    private Long userId;

    @Schema(description = "创建者姓名")
    private String userName;

    @Schema(description = "所属部门ID", example = "13840")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

}

