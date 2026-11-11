package cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - OA工作计划统计 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OaWorkPlanStatisticsVO {

    @Schema(description = "本月计划总数")
    private Long thisMonthCount;

    @Schema(description = "本月已完成数")
    private Long thisMonthCompleted;

    @Schema(description = "平均完成度（百分比）")
    private Integer avgCompletionRate;

    @Schema(description = "平均质量评分")
    private Double avgQualityScore;

    @Schema(description = "待审核计划数")
    private Long pendingReview;

}



