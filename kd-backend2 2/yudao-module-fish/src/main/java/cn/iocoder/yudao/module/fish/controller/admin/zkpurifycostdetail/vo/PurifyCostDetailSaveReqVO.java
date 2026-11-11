package cn.iocoder.yudao.module.fish.controller.admin.zkpurifycostdetail.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 成本明细记录新增/修改 Request VO")
@Data
public class PurifyCostDetailSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "27328")
    private Long id;

    @Schema(description = "关联流程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11619")
    @NotNull(message = "关联流程ID不能为空")
    private Long processId;
    private String recordNumber;

    @Schema(description = "成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)不能为空")
    private String costType;

    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @Schema(description = "发生日期")
    private LocalDateTime occurrenceDate;

    @Schema(description = "备注说明")
    private String notes;

    @Schema(description = "所属部门", example = "2238")
    private Long deptId;

}