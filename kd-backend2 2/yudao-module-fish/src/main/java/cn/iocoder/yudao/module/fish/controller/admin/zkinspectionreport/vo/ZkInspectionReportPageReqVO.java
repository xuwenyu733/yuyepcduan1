package cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 巡检智能分析报告分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkInspectionReportPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "31070")
    private Long userId;

    @Schema(description = "所属部门", example = "3110")
    private Long deptId;

    /** 本部门+子部门（由 enhancerDeptIds / DeptScopeSupport 填充） */
    private List<Long> deptIds;

    private Long tenantId;

    @Schema(description = "文件id", example = "23845")
    private String fid;
    private Long taskDetailId; // 巡检明细id(zk_task_detail.id)
    private String htmlUrl; // html路径
    private String pdfUrl; // pdf路径

}
