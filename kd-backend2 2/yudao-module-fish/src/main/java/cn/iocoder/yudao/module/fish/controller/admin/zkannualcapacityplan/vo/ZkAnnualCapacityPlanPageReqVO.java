package cn.iocoder.yudao.module.fish.controller.admin.zkannualcapacityplan.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 年度产能计划分页 Request VO")
@Data
public class ZkAnnualCapacityPlanPageReqVO extends PageParam {

    @Schema(description = "计划年份 (如:2026)")
    private Integer planYear;

    @Schema(description = "关联的品种ID", example = "7851")
    private Long speciesId;

    @Schema(description = "品种名称冗余字段 (如:大西洋鲑鱼)", example = "王五")
    private String speciesName;

    @Schema(description = "当前产能/产量 (单位:吨)")
    private BigDecimal currentCapacity;

    @Schema(description = "目标产能 (单位:吨)")
    private BigDecimal targetCapacity;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "14878")
    private Long deptId;

}