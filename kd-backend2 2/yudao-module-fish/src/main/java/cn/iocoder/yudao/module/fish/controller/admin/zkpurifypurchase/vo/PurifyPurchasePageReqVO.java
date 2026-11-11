package cn.iocoder.yudao.module.fish.controller.admin.zkpurifypurchase.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 采购记录分页 Request VO")
@Data
public class PurifyPurchasePageReqVO extends PageParam {

    @Schema(description = "采购单号")
    private String purchaseOrderNo;

    @Schema(description = "品类")
    private String category;

    @Schema(description = "来源（城市/地区）")
    private String source;

    @Schema(description = "具体地址")
    private String detailedAddress;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "采购数量(kg)")
    private BigDecimal quantity;

    @Schema(description = "单价(元/kg)", example = "14539")
    private BigDecimal unitPrice;

    @Schema(description = "总金额(元)")
    private BigDecimal totalAmount;

    @Schema(description = "运输方式")
    private String transportMethod;

    @Schema(description = "采购日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] purchaseDate;

    @Schema(description = "采购负责人")
    private String purchaseManager;

    @Schema(description = "规格-单尾重(kg)")
    private BigDecimal avgWeight;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "17553")
    private Long deptId;

}