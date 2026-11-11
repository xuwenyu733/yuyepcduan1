package cn.iocoder.yudao.module.fish.controller.admin.zkpurifytreatment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 处理措施记录新增/修改 Request VO")
@Data
public class PurifyTreatmentSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "149")
    private Long id;

    @Schema(description = "关联流程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "835")
    @NotNull(message = "关联流程ID不能为空")
    private Long processId;
    private String recordNumber;

    @Schema(description = "处理日期")
    private LocalDateTime treatmentDate;

    @Schema(description = "措施类型(用药/水质调节等)", example = "2")
    private String type;

    @Schema(description = "措施内容")
    private String content;

    @Schema(description = "剂量")
    private String dosage;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "备注说明")
    private String notes;

    @Schema(description = "所属部门", example = "6076")
    private Long deptId;

}