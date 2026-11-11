package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishtype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 鱼苗种类 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkPondFishTypeRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14325")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "存塘id", example = "11782")
    @ExcelProperty("存塘id")
    private Long pondId;

    @Schema(description = "批次编号")
    @ExcelProperty("批次编号")
    private String batchNumber;

    @Schema(description = "鱼苗数量")
    @ExcelProperty("鱼苗数量")
    private Long fishNum;

    @Schema(description = "鱼苗规格")
    @ExcelProperty("鱼苗规格")
    private String fishSpec;

    @Schema(description = "统计年")
    @ExcelProperty("统计年")
    private Integer staticYear;

    @Schema(description = "统计月份")
    @ExcelProperty("统计月份")
    private Integer staticMonth;

    @Schema(description = "统计日期")
    @ExcelProperty("统计日期")
    private Integer staticDay;

    @Schema(description = "鱼苗种类", example = "1")
    @ExcelProperty("鱼苗种类")
    private String fishType;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "24053")
    @ExcelProperty("所属部门")
    private Long deptId;

}