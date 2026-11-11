package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 风味优化统计数据 Response VO")
@Data
public class PurifyStatsRespVO {

    @Schema(description = "入池总重量", example = "1000.00")
    private BigDecimal totalInWeight;

    @Schema(description = "出池总重量", example = "950.00")
    private BigDecimal totalOutWeight;

    @Schema(description = "累计死亡数量", example = "50")
    private Integer totalDeathCount;

    @Schema(description = "累计死亡重量")
    private BigDecimal totalDeathWeight;

    @Schema(description = "累计掉秤重量")
    private BigDecimal totalWeightLoss;

    @Schema(description = "死亡率", example = "5.00")
    private BigDecimal deathRate;

    @Schema(description = "掉秤率")
    private BigDecimal weightLossRate;

    @Schema(description = "销售总额", example = "15000.00")
    private BigDecimal totalSalesAmount;

    @Schema(description = "总利润", example = "5000.00")
    private BigDecimal totalProfit;

    @Schema(description = "利润率", example = "33.33")
    private BigDecimal profitMargin;

    @Schema(description = "总投喂量")
    private BigDecimal totalFeedAmount;

    @Schema(description = "平均单价")
    private BigDecimal averagePrice;

    @Schema(description = "采购总成本")
    private BigDecimal totalPurchaseCost;

    @Schema(description = "人工总成本")
    private BigDecimal totalLaborCost;

    @Schema(description = "电费总成本")
    private BigDecimal totalElectricityCost;

    @Schema(description = "饲料总成本")
    private BigDecimal totalFeedCost;

    @Schema(description = "动保总成本")
    private BigDecimal totalHealthProductCost;

    @Schema(description = "总成本")
    private BigDecimal totalCost;

    @Schema(description = "品类分布数据 {label: count}")
    private Map<String, Long> categoryDistribution;

    @Schema(description = "规格对比数据 {spec: [inCount, outCount]}")
    private Map<String, List<Integer>> specComparison;

    @Schema(description = "死亡率趋势数据 [rate1, rate2, ...]")
    private List<BigDecimal> mortalityTrend;

    @Schema(description = "阶段性死亡率分析")
    private List<StageMortalityVO> stageMortalityAnalysis;

    @Schema(description = "批次散点图数据")
    private List<BatchScatterVO> batchScatterData;

    @Data
    public static class StageMortalityVO {
        private String recordNumber;
        private String category;
        private String poolNumber;
        private Integer inPoolCount;
        private LocalDateTime inPoolTime;
        private Integer day3Deaths;
        private BigDecimal day3Rate;
        private Integer day5Deaths;
        private BigDecimal day5Rate;
        private Integer day7Deaths;
        private BigDecimal day7Rate;
        private Integer totalDeaths;
        private BigDecimal totalRate;
    }

    @Data
    public static class BatchScatterVO {
        private String recordNumber;
        private String category;
        private BigDecimal weightLossRate;
        private BigDecimal mortalityRate;
        private BigDecimal inPoolWeight;
    }
}
