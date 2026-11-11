package cn.iocoder.yudao.module.fish.controller.admin.zkpurifypool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 鱼池管理 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurifyPoolRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "23951")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "池号")
    @ExcelProperty("池号")
    private String poolNumber;

    @Schema(description = "大小(m³)")
    @ExcelProperty("大小(m³)")
    private BigDecimal size;

    @Schema(description = "位置")
    @ExcelProperty("位置")
    private String location;

    @Schema(description = "负责人")
    @ExcelProperty("负责人")
    private String manager;

    @Schema(description = "状态(available:空闲, in_use:使用中, maintenance:维护中)", example = "1")
    @ExcelProperty("状态(available:空闲, in_use:使用中, maintenance:维护中)")
    private String status;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String notes;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "20371")
    @ExcelProperty("所属部门")
    private Long deptId;

}