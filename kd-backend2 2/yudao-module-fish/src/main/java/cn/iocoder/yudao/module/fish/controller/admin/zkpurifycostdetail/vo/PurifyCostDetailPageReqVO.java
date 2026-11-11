package cn.iocoder.yudao.module.fish.controller.admin.zkpurifycostdetail.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 成本明细记录分页 Request VO")
@Data
public class PurifyCostDetailPageReqVO extends PageParam {

    @Schema(description = "关联流程ID", example = "11619")
    private Long processId;
    private String recordNumber;

    @Schema(description = "成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)", example = "2")
    private String costType;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "发生日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] occurrenceDate;

    @Schema(description = "备注说明")
    private String notes;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "2238")
    private Long deptId;

}