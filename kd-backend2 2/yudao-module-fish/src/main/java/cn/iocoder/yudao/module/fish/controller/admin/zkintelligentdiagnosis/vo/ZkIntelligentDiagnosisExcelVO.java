package cn.iocoder.yudao.module.fish.controller.admin.zkintelligentdiagnosis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 智能诊断 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkIntelligentDiagnosisExcelVO {

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

    @ExcelProperty("智能体分析报告")
    private String aiAgentReport;

    @ExcelProperty("人工判断是否同意不同意写病因分析")
    private String humanReport;

    @ExcelProperty("咨询专家后方案")
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

    @ExcelProperty("分析人")
    private String analyst;

    @ExcelProperty("专家")
    private String expert;

    @ExcelProperty("上传的图片")
    private String uploadedImages;
    private String batchNumber; // 批次编号
    private String fishTypes; // 鱼苗种类
    private String supplierName; // 来源
    private String feedType; // 投喂方式
    private Integer illNum; // 发病数量
    private String illName; // 病害名称
    private String illType; // 病害分类
    private String abnormalArea; // 异常部位
    private String abnormalContent; // 异常表现及描述
    private String rongYangNongDu; // 溶氧
    private String suanJianDu; // 酸碱度
    private String anDan; // 氨氮
    private String shuiWen; // 水温
    private String shuiWei; // 水位
    // private String fidImage; // 图片Fid

    private String imageUrls; // 图片地址(多个英文逗号隔开)
    private String videoUrls; // 视频地址(多个英文逗号隔开)

    /**
     * 状态(0:申请中;1:已批准;2:已使用;3:已评估;-1:已拒绝;)
     */
    private String status;
    /**
     * 流程实例
     */
    private String processInstanceId;
    private String userName;
    private String dissectionArea; // 解剖部位
}
