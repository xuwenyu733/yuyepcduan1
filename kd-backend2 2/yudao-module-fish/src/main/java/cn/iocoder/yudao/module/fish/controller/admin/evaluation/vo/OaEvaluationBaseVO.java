package cn.iocoder.yudao.module.fish.controller.admin.evaluation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

/**
 * OA考核评分 Base VO，提供给添加、修改、详细的子 VO 使用
 */
@Data
public class OaEvaluationBaseVO {

    @Schema(description = "用户ID", example = "6144")
    private Long userId;

    @Schema(description = "用户姓名", example = "张三")
    private String userName;

    @Schema(description = "部门ID", example = "13840")
    private Long deptId;

    @Schema(description = "部门名称", example = "养殖部")
    private String deptName;

    @Schema(description = "考核期（格式：yyyy-MM）", example = "2024-12")
    private String evaluationPeriod;

    @Schema(description = "工作计划得分（加权后）", example = "22.0")
    private BigDecimal workPlanScore;

    @Schema(description = "考勤得分（加权后）", example = "8.5")
    private BigDecimal attendanceScore;

    @Schema(description = "考试得分（加权后）", example = "18.4")
    private BigDecimal examScore;

    @Schema(description = "他人评分得分（加权后）", example = "21.5")
    private BigDecimal peerRatingScore;

    @Schema(description = "工作绩效得分（加权后）", example = "18.0")
    private BigDecimal workPerformanceScore;

    @Schema(description = "总分（0-100分）", example = "88.4")
    private BigDecimal totalScore;

    @Schema(description = "等级（A+, A, B+, B, C等）", example = "A")
    private String grade;

    @Schema(description = "工作计划原始得分", example = "88.0")
    private BigDecimal workPlanRawScore;

    @Schema(description = "考勤原始得分", example = "8.5")
    private BigDecimal attendanceRawScore;

    @Schema(description = "考试原始得分", example = "92.0")
    private BigDecimal examRawScore;

    @Schema(description = "他人评分原始得分", example = "86.0")
    private BigDecimal peerRatingRawScore;

    @Schema(description = "工作绩效原始得分", example = "26.0")
    private BigDecimal workPerformanceRawScore;

}



