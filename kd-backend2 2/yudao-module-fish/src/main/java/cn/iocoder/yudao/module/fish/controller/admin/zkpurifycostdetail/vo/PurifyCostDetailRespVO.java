package cn.iocoder.yudao.module.fish.controller.admin.zkpurifycostdetail.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 成本明细记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurifyCostDetailRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "27328")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "关联流程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11619")
    @ExcelProperty("关联流程ID")
    private Long processId;
    private String recordNumber;

    @Schema(description = "成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)")
    private String costType;

    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("金额")
    private BigDecimal amount;

    @Schema(description = "发生日期")
    @ExcelProperty("发生日期")
    private LocalDateTime occurrenceDate;

    @Schema(description = "备注说明")
    @ExcelProperty("备注说明")
    private String notes;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "2238")
    @ExcelProperty("所属部门")
    private Long deptId;

}