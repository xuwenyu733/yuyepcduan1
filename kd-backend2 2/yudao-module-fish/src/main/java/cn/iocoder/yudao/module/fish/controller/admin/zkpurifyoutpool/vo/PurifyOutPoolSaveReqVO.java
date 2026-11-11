package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 出池登记新增/修改 Request VO")
@Data
public class PurifyOutPoolSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "5379")
    private Long id;

    @Schema(description = "关联流程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27999")
    @NotNull(message = "关联流程ID不能为空")
    private Long processId;
    private String recordNumber;

    @Schema(description = "出池时间")
    private LocalDateTime outPoolTime;

    @Schema(description = "出池重量(kg)")
    private BigDecimal outPoolWeight;

    @Schema(description = "出池数量", example = "25973")
    private Integer outPoolCount;

    @Schema(description = "规格等级")
    private String specification;

    @Schema(description = "客户名称", example = "李四")
    private String customerName;

    @Schema(description = "销售单价", example = "4887")
    private BigDecimal salesPrice;

    @Schema(description = "销售总额")
    private BigDecimal totalAmount;

    @Schema(description = "品质评价(优/良/中/差)")
    private String fishQuality;

    @Schema(description = "检测人")
    private String testOperator;

    @Schema(description = "所属部门", example = "5553")
    private Long deptId;

}