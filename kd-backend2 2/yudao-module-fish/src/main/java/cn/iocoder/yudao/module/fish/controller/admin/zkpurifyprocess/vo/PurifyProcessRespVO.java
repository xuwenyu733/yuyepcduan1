package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyprocess.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 优化流程记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurifyProcessRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19237")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "关联档案ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18194")
    @ExcelProperty("关联档案ID")
    private Long archiveId;
    private String poolNumber;
    private String purchaseOrderNo;
    private String category;

    @Schema(description = "流程单号")
    @ExcelProperty("流程单号")
    private String recordNumber;

    @Schema(description = "状态(farming:优化中, out_pool:已完成)", example = "1")
    @ExcelProperty("状态(farming:优化中, out_pool:已完成)")
    private String status;

    @Schema(description = "开始确认时间")
    @ExcelProperty("开始确认时间")
    private LocalDateTime startTime;

    @Schema(description = "负责人")
    @ExcelProperty("负责人")
    private String operator;

    @Schema(description = "累计死亡数量", example = "12094")
    @ExcelProperty("累计死亡数量")
    private Integer totalDeathCount;

    @Schema(description = "累计死亡重量")
    @ExcelProperty("累计死亡重量")
    private BigDecimal totalDeathWeight;

    @Schema(description = "累计出池数量", example = "28206")
    @ExcelProperty("累计出池数量")
    private Integer totalOutCount;

    @Schema(description = "累计出池重量")
    @ExcelProperty("累计出池重量")
    private BigDecimal totalOutWeight;

    @Schema(description = "当前存塘数量", example = "17283")
    @ExcelProperty("当前存塘数量")
    private Integer currentStockCount;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "11727")
    @ExcelProperty("所属部门")
    private Long deptId;

    private BigDecimal mortalityRate; // 死亡率
    private BigDecimal scaleDropRate; // 掉秤率
}