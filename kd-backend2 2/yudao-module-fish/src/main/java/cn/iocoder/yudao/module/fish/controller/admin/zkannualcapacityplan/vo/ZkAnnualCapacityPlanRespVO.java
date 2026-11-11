package cn.iocoder.yudao.module.fish.controller.admin.zkannualcapacityplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 年度产能计划 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkAnnualCapacityPlanRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "27665")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "计划年份 (如:2026)")
    @ExcelProperty("计划年份 (如:2026)")
    private Integer planYear;

    @Schema(description = "关联的品种ID", example = "7851")
    @ExcelProperty("关联的品种ID")
    private Long speciesId;

    @Schema(description = "品种名称冗余字段 (如:大西洋鲑鱼)", example = "王五")
    @ExcelProperty("品种名称冗余字段 (如:大西洋鲑鱼)")
    private String speciesName;

    @Schema(description = "当前产能/产量 (单位:吨)")
    @ExcelProperty("当前产能/产量 (单位:吨)")
    private BigDecimal currentCapacity;

    @Schema(description = "目标产能 (单位:吨)")
    @ExcelProperty("目标产能 (单位:吨)")
    private BigDecimal targetCapacity;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "14878")
    @ExcelProperty("所属部门")
    private Long deptId;

}