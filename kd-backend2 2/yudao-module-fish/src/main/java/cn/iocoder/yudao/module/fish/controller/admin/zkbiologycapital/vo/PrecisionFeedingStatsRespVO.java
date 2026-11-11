package cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 精准投喂顶部统计 Response VO")
@Data
public class PrecisionFeedingStatsRespVO {

    @Schema(description = "鱼池总数")
    private Integer pondNum;

    @Schema(description = "总生物量")
    private BigDecimal totalWeight;

    @Schema(description = "今日投喂量")
    private BigDecimal feedCount;

    @Schema(description = "正常存塘")
    private Integer normalPond;

    @Schema(description = "预警存塘")
    private Integer preWarnPond;

    @Schema(description = "警报存塘")
    private Integer alarmPond;

    @Schema(description = "昨日投喂量")
    private BigDecimal yesterdayFeedCount;
    @Schema(description = "较昨日变化百分比")
    private BigDecimal yesterdayDiffPercent;
    @Schema(description = "平均规格 g")
    private BigDecimal avgSpec;
    @Schema(description = "平均密度 尾/m³")
    private BigDecimal avgDensity;

}
