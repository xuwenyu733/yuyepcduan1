package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 出池登记 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurifyOutPoolRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "5379")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "关联流程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27999")
    @ExcelProperty("关联流程ID")
    private Long processId;
    private String recordNumber;

    @Schema(description = "出池时间")
    @ExcelProperty("出池时间")
    private LocalDateTime outPoolTime;

    @Schema(description = "出池重量(kg)")
    @ExcelProperty("出池重量(kg)")
    private BigDecimal outPoolWeight;

    @Schema(description = "出池数量", example = "25973")
    @ExcelProperty("出池数量")
    private Integer outPoolCount;

    @Schema(description = "规格等级")
    @ExcelProperty("规格等级")
    private String specification;

    @Schema(description = "客户名称", example = "李四")
    @ExcelProperty("客户名称")
    private String customerName;

    @Schema(description = "销售单价", example = "4887")
    @ExcelProperty("销售单价")
    private BigDecimal salesPrice;

    @Schema(description = "销售总额")
    @ExcelProperty("销售总额")
    private BigDecimal totalAmount;

    @Schema(description = "品质评价(优/良/中/差)")
    @ExcelProperty("品质评价(优/良/中/差)")
    private String fishQuality;

    @Schema(description = "检测人")
    @ExcelProperty("检测人")
    private String testOperator;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "5553")
    @ExcelProperty("所属部门")
    private Long deptId;

}