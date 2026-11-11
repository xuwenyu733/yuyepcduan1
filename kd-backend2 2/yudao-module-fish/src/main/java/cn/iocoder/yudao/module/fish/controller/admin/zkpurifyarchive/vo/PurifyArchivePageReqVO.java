package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 入池档案分页 Request VO")
@Data
public class PurifyArchivePageReqVO extends PageParam {

    @Schema(description = "关联采购记录ID", example = "9911")
    private Long purchaseRecordId;

    @Schema(description = "关联采购单号")
    private String purchaseOrderNo;

    @Schema(description = "品类")
    private String category;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "入池时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] inPoolTime;

    @Schema(description = "入池总重量(kg)")
    private BigDecimal inPoolWeight;

    @Schema(description = "规格-单尾重(kg)")
    private BigDecimal avgWeight;

    @Schema(description = "入池尾数", example = "19580")
    private Integer inPoolCount;

    @Schema(description = "池号")
    private String poolNumber;

    @Schema(description = "初始密度(kg/m³)")
    private BigDecimal density;

    @Schema(description = "优化等级(premium/wagyu/tuna)")
    private String optimizationLevel;

    @Schema(description = "检验报告文件名/URL")
    private String inspectionReport;

    @Schema(description = "初始溶氧(mg/L)")
    private BigDecimal initialOxygen;

    @Schema(description = "初始PH值")
    private BigDecimal initialPh;

    @Schema(description = "初始水温(℃)")
    private BigDecimal initialTemperature;

    @Schema(description = "外观评估")
    private String appearanceScore;

    @Schema(description = "活力评估")
    private String vitalityScore;

    @Schema(description = "健康状况", example = "2")
    private String healthStatus;

    private String specUniformity; //规格均匀度
    private String stressLevel; //应激程度

    @Schema(description = "预期存活率(%)")
    private BigDecimal expectedSurvivalRate;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "评估人")
    private String assessor;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "9400")
    private Long deptId;
    private String fid; // 文件id

    @Schema(description = "流程单号")
    private String recordNumber;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "时间范围")
    private String timeRange;

    @Schema(description = "最小死亡率")
    private BigDecimal minMortalityRate;
    @Schema(description = "最大死亡率")
    private BigDecimal maxMortalityRate;
    @Schema(description = "最小掉秤率")
    private BigDecimal minScaleDropRate;
    @Schema(description = "最大掉秤率")
    private BigDecimal maxScaleDropRate;

}