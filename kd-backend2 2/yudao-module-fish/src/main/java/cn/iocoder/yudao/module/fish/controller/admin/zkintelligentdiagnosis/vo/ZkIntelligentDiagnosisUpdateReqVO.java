package cn.iocoder.yudao.module.fish.controller.admin.zkintelligentdiagnosis.vo;

import cn.iocoder.yudao.module.workflow.api.dto.TaskParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 智能诊断更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkIntelligentDiagnosisUpdateReqVO extends ZkIntelligentDiagnosisBaseVO {

    @Schema(description = "编号", required = true, example = "5788")
    @NotNull(message = "编号不能为空")
    private Long id;

    TaskParam taskParam;

}
