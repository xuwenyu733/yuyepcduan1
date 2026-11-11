package cn.iocoder.yudao.module.fish.controller.admin.zkgrowfarmingbatch.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 养殖批次新增/修改 Request VO")
@Data
public class ZkGrowFarmingBatchSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14643")
    private Long id;

    @Schema(description = "批次编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "批次编号不能为空")
    private String batchNo;

    @Schema(description = "鱼类品种ID", example = "11380")
    private Long speciesId;

    @Schema(description = "投苗日期")
    private LocalDateTime startDate;

    @Schema(description = "初始投放数量", example = "1884")
    private Integer initialCount;

    @Schema(description = "当前存栏数量", example = "27998")
    private Integer currentCount;

    @Schema(description = "状态 (1:进行中, 2:已收成)", example = "2")
    private Integer status;

    @Schema(description = "所属部门", example = "30899")
    private Long deptId;
    private Long tenantId;
    private String scientificName;

}