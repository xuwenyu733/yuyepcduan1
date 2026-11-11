package cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 巡检智能分析报告 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkInspectionReportRespVO extends ZkInspectionReportBaseVO {

    @Schema(description = "编号", required = true, example = "30321")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
