package cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 巡检智能分析报告 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkInspectionReportBaseVO {

    @Schema(description = "所属车间id", example = "13864")
    private Long workshopId;

    @Schema(description = "车间名称", example = "芋艿")
    private String workshopName;

    @Schema(description = "所属系统id", example = "26005")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "存塘系统名称", example = "李四")
    private String pondSystemName;

    @Schema(description = "所属存塘id", example = "22663")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "芋艿")
    private String pondName;

    @Schema(description = "智能体报告")
    private String aiAgentReport;

    @Schema(description = "人工报告是否同意不同意写原因分析")
    private String humanReport;

    @Schema(description = "对比分析人工和智能体报告")
    private String differentialAnalysis;

    @Schema(description = "整体结论")
    private String conclusion;

    @Schema(description = "整体评估")
    private String evaluation;

    @Schema(description = "负责人id", example = "31070")
    private Long userId;

    @Schema(description = "所属部门", example = "3110")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "23845")
    private String fid;
    private Long taskDetailId; // 巡检明细id(zk_task_detail.id)
    private String htmlUrl; // html路径
    private String pdfUrl; // pdf路径

}
