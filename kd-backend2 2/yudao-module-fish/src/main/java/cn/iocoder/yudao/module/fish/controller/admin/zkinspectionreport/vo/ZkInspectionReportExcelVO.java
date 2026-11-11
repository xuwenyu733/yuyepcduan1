package cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 巡检智能分析报告 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkInspectionReportExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("所属车间id")
    private Long workshopId;

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("所属系统id")
    private Long pondSystemId;

    @ExcelProperty("存塘系统编号")
    private String pondNum;

    @ExcelProperty("存塘系统名称")
    private String pondSystemName;

    @ExcelProperty("所属存塘id")
    private Long pondId;

    @ExcelProperty("存塘编号")
    private String pondDetailNum;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("智能体报告")
    private String aiAgentReport;

    @ExcelProperty("人工报告是否同意不同意写原因分析")
    private String humanReport;

    @ExcelProperty("对比分析人工和智能体报告")
    private String differentialAnalysis;

    @ExcelProperty("整体结论")
    private String conclusion;

    @ExcelProperty("整体评估")
    private String evaluation;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;
    private Long taskDetailId; // 巡检明细id(zk_task_detail.id)
    private String htmlUrl; // html路径
    private String pdfUrl; // pdf路径

}
