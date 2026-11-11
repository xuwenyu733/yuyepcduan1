package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishtype.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 鱼苗种类分页 Request VO")
@Data
public class ZkPondFishTypePageReqVO extends PageParam {

    @Schema(description = "存塘id", example = "11782")
    private Long pondId;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "鱼苗数量")
    private Long fishNum;

    @Schema(description = "鱼苗规格")
    private String fishSpec;

    @Schema(description = "统计年")
    private Integer staticYear;

    @Schema(description = "统计月份")
    private Integer staticMonth;

    @Schema(description = "统计日期")
    private Integer staticDay;

    @Schema(description = "鱼苗种类", example = "1")
    private String fishType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "24053")
    private Long deptId;

}