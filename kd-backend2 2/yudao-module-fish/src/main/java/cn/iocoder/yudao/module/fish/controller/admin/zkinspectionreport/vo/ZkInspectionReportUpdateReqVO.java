package cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 巡检智能分析报告更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkInspectionReportUpdateReqVO extends ZkInspectionReportBaseVO {

    @Schema(description = "编号", required = true, example = "30321")
    @NotNull(message = "编号不能为空")
    private Long id;

}
