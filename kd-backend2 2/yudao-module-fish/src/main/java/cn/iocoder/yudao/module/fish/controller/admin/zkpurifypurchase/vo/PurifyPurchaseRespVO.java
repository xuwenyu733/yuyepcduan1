package cn.iocoder.yudao.module.fish.controller.admin.zkpurifypurchase.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 采购记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurifyPurchaseRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "7498")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "采购单号")
    @ExcelProperty("采购单号")
    private String purchaseOrderNo;

    @Schema(description = "品类")
    @ExcelProperty("品类")
    private String category;

    @Schema(description = "来源（城市/地区）")
    @ExcelProperty("来源（城市/地区）")
    private String source;

    @Schema(description = "具体地址")
    @ExcelProperty("具体地址")
    private String detailedAddress;

    @Schema(description = "联系人")
    @ExcelProperty("联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    @ExcelProperty("联系电话")
    private String contactPhone;

    @Schema(description = "采购数量(kg)")
    @ExcelProperty("采购数量(kg)")
    private BigDecimal quantity;

    @Schema(description = "单价(元/kg)", example = "14539")
    @ExcelProperty("单价(元/kg)")
    private BigDecimal unitPrice;

    @Schema(description = "总金额(元)")
    @ExcelProperty("总金额(元)")
    private BigDecimal totalAmount;

    @Schema(description = "运输方式")
    @ExcelProperty("运输方式")
    private String transportMethod;

    @Schema(description = "采购日期")
    @ExcelProperty("采购日期")
    private LocalDateTime purchaseDate;

    @Schema(description = "采购负责人")
    @ExcelProperty("采购负责人")
    private String purchaseManager;

    @Schema(description = "规格-单尾重(kg)")
    @ExcelProperty("规格-单尾重(kg)")
    private BigDecimal avgWeight;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String notes;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "17553")
    @ExcelProperty("所属部门")
    private Long deptId;

}