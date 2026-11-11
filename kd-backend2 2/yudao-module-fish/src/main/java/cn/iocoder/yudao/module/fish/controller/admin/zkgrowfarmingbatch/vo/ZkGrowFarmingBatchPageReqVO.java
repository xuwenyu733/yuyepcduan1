package cn.iocoder.yudao.module.fish.controller.admin.zkgrowfarmingbatch.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 养殖批次分页 Request VO")
@Data
public class ZkGrowFarmingBatchPageReqVO extends PageParam {

    @Schema(description = "批次编号")
    private String batchNo;

    @Schema(description = "鱼类品种ID", example = "11380")
    private Long speciesId;

    @Schema(description = "投苗日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startDate;

    @Schema(description = "初始投放数量", example = "1884")
    private Integer initialCount;

    @Schema(description = "当前存栏数量", example = "27998")
    private Integer currentCount;

    @Schema(description = "状态 (1:进行中, 2:已收成)", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "30899")
    private Long deptId;
    private Long tenantId;
    private String scientificName;

}