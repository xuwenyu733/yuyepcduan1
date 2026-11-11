package cn.iocoder.yudao.module.fish.controller.admin.zkintelligentdiagnosis.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 智能诊断分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkIntelligentDiagnosisPageReqVO extends PageParam {

    @Schema(description = "所属车间id", example = "28520")
    private Long workshopId;

    @Schema(description = "车间名称", example = "李四")
    private String workshopName;

    @Schema(description = "所属系统id", example = "6041")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "存塘系统名称", example = "芋艿")
    private String pondSystemName;

    @Schema(description = "所属存塘id", example = "31519")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "芋艿")
    private String pondName;

    @Schema(description = "智能体分析报告")
    private String aiAgentReport;

    @Schema(description = "人工判断是否同意不同意写病因分析")
    private String humanReport;

    @Schema(description = "咨询专家后方案")
    private String differentialAnalysis;

    @Schema(description = "整体结论")
    private String conclusion;

    @Schema(description = "整体评估")
    private String evaluation;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "12256")
    private Long userId;

    @Schema(description = "所属部门", example = "23439")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "25877")
    private String fid;

    @Schema(description = "分析人")
    private String analyst;

    @Schema(description = "专家")
    private String expert;

    @Schema(description = "上传的图片")
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
