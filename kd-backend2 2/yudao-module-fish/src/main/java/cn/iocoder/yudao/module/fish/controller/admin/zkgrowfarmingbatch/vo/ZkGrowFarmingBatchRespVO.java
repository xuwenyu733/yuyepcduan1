package cn.iocoder.yudao.module.fish.controller.admin.zkgrowfarmingbatch.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 养殖批次 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkGrowFarmingBatchRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14643")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "批次编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("批次编号")
    private String batchNo;

    @Schema(description = "鱼类品种ID", example = "11380")
    @ExcelProperty("鱼类品种ID")
    private Long speciesId;

    @Schema(description = "投苗日期")
    @ExcelProperty("投苗日期")
    private LocalDateTime startDate;

    @Schema(description = "初始投放数量", example = "1884")
    @ExcelProperty("初始投放数量")
    private Integer initialCount;

    @Schema(description = "当前存栏数量", example = "27998")
    @ExcelProperty("当前存栏数量")
    private Integer currentCount;

    @Schema(description = "状态 (1:进行中, 2:已收成)", example = "2")
    @ExcelProperty("状态 (1:进行中, 2:已收成)")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "30899")
    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;
    private String scientificName;

}
