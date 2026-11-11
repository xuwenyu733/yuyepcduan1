package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyprocess.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 优化流程记录分页 Request VO")
@Data
public class PurifyProcessPageReqVO extends PageParam {

    @Schema(description = "关联档案ID", example = "18194")
    private Long archiveId;
    private String poolNumber;
    private String purchaseOrderNo;
    private String category;

    @Schema(description = "流程单号")
    private String recordNumber;

    @Schema(description = "状态(farming:优化中, out_pool:已完成)", example = "1")
    private String status;

    @Schema(description = "开始确认时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "负责人")
    private String operator;

    @Schema(description = "累计死亡数量", example = "12094")
    private Integer totalDeathCount;

    @Schema(description = "累计死亡重量")
    private BigDecimal totalDeathWeight;

    @Schema(description = "累计出池数量", example = "28206")
    private Integer totalOutCount;

    @Schema(description = "累计出池重量")
    private BigDecimal totalOutWeight;

    @Schema(description = "当前存塘数量", example = "17283")
    private Integer currentStockCount;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "11727")
    private Long deptId;

    private BigDecimal mortalityRate; // 死亡率
    private BigDecimal scaleDropRate; // 掉秤率
}