package cn.iocoder.yudao.module.fish.controller.admin.deptworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 部门工作计划 Base VO，提供给添加、修改、详细的子 VO 使用
 */
@Data
public class DeptWorkPlanBaseVO {

    @Schema(description = "计划类型：weekly-周计划, monthly-月计划, quarterly-季度计划, annual-年度计划", example = "weekly")
    private String type;

    @Schema(description = "计划标题", example = "第51周工作计划")
    private String title;

    @Schema(description = "计划周期", example = "2024-12-16 至 2024-12-22")
    private String period;

    @Schema(description = "工作内容（周计划为文本，不需要每天设置）")
    private String content;

    @Schema(description = "完成度（百分比，0-100）", example = "95")
    private Integer completionRate;

    @Schema(description = "质量评分（0-5分）", example = "4.5")
    private Double qualityScore;

    @Schema(description = "提交日期")
    private LocalDateTime submitDate;

    @Schema(description = "附件（JSON数组）")
    private String attachments;

    @Schema(description = "创建者ID（部门负责人）", example = "6144")
    private Long userId;

    @Schema(description = "创建者姓名")
    private String userName;

    @Schema(description = "所属部门ID", example = "13840")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;
}

