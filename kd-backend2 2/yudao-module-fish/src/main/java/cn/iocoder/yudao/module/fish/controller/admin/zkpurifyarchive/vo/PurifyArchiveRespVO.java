package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 入池档案 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurifyArchiveRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "17041")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "关联采购记录ID", example = "9911")
    @ExcelProperty("关联采购记录ID")
    private Long purchaseRecordId;

    @Schema(description = "关联采购单号")
    @ExcelProperty("关联采购单号")
    private String purchaseOrderNo;

    @Schema(description = "品类")
    @ExcelProperty("品类")
    private String category;

    @Schema(description = "来源")
    @ExcelProperty("来源")
    private String source;

    @Schema(description = "入池时间")
    @ExcelProperty("入池时间")
    private LocalDateTime inPoolTime;

    @Schema(description = "入池总重量(kg)")
    @ExcelProperty("入池总重量(kg)")
    private BigDecimal inPoolWeight;

    @Schema(description = "规格-单尾重(kg)")
    @ExcelProperty("规格-单尾重(kg)")
    private BigDecimal avgWeight;

    @Schema(description = "入池尾数", example = "19580")
    @ExcelProperty("入池尾数")
    private Integer inPoolCount;

    @Schema(description = "池号")
    @ExcelProperty("池号")
    private String poolNumber;

    @Schema(description = "初始密度(kg/m³)")
    @ExcelProperty("初始密度(kg/m³)")
    private BigDecimal density;

    @Schema(description = "优化等级(premium/wagyu/tuna)")
    @ExcelProperty("优化等级(premium/wagyu/tuna)")
    private String optimizationLevel;

    @Schema(description = "检验报告文件名/URL")
    @ExcelProperty("检验报告文件名/URL")
    private String inspectionReport;

    @Schema(description = "初始溶氧(mg/L)")
    @ExcelProperty("初始溶氧(mg/L)")
    private BigDecimal initialOxygen;

    @Schema(description = "初始PH值")
    @ExcelProperty("初始PH值")
    private BigDecimal initialPh;

    @Schema(description = "初始水温(℃)")
    @ExcelProperty("初始水温(℃)")
    private BigDecimal initialTemperature;

    @Schema(description = "外观评估")
    @ExcelProperty("外观评估")
    private String appearanceScore;

    @Schema(description = "活力评估")
    @ExcelProperty("活力评估")
    private String vitalityScore;

    @Schema(description = "健康状况", example = "2")
    @ExcelProperty("健康状况")
    private String healthStatus;
    private String specUniformity; //规格均匀度
    private String stressLevel; //应激程度

    @Schema(description = "预期存活率(%)")
    @ExcelProperty("预期存活率(%)")
    private BigDecimal expectedSurvivalRate;

    @Schema(description = "风险等级")
    @ExcelProperty("风险等级")
    private String riskLevel;

    @Schema(description = "评估人")
    @ExcelProperty("评估人")
    private String assessor;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "9400")
    @ExcelProperty("所属部门")
    private Long deptId;
    private String fid; // 文件id

    // 记录查询扩展字段
    private String recordNumber; // 流程单号
    private String status; // 状态
    private Integer totalDeathCount; // 累计死亡数量
    private BigDecimal totalDeathWeight; // 累计死亡重量
    private Integer totalOutCount; // 累计出池数量
    private BigDecimal totalOutWeight; // 累计出池重量
    private Integer currentStockCount; // 当前存塘数量
    private BigDecimal deathRate; // 死亡率
    private BigDecimal weightLossRate; // 掉秤率

}