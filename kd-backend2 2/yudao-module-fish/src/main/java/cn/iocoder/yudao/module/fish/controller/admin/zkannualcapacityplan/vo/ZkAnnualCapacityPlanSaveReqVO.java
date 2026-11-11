package cn.iocoder.yudao.module.fish.controller.admin.zkannualcapacityplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 年度产能计划新增/修改 Request VO")
@Data
public class ZkAnnualCapacityPlanSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "27665")
    private Long id;

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

    @Schema(description = "所属部门", example = "14878")
    private Long deptId;

}