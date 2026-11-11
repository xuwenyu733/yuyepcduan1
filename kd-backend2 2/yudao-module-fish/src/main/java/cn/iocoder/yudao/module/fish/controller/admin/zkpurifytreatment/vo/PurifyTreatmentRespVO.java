package cn.iocoder.yudao.module.fish.controller.admin.zkpurifytreatment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 处理措施记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurifyTreatmentRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "149")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "关联流程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "835")
    @ExcelProperty("关联流程ID")
    private Long processId;
    private String recordNumber;

    @Schema(description = "处理日期")
    @ExcelProperty("处理日期")
    private LocalDateTime treatmentDate;

    @Schema(description = "措施类型(用药/水质调节等)", example = "2")
    @ExcelProperty("措施类型(用药/水质调节等)")
    private String type;

    @Schema(description = "措施内容")
    @ExcelProperty("措施内容")
    private String content;

    @Schema(description = "剂量")
    @ExcelProperty("剂量")
    private String dosage;

    @Schema(description = "操作人")
    @ExcelProperty("操作人")
    private String operator;

    @Schema(description = "备注说明")
    @ExcelProperty("备注说明")
    private String notes;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "6076")
    @ExcelProperty("所属部门")
    private Long deptId;

}