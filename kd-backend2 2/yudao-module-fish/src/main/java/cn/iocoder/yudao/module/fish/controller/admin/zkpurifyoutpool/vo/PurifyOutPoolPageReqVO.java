package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 出池登记分页 Request VO")
@Data
public class PurifyOutPoolPageReqVO extends PageParam {

    @Schema(description = "关联流程ID", example = "27999")
    private Long processId;
    private String recordNumber;

    @Schema(description = "出池时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] outPoolTime;

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "5553")
    private Long deptId;

}