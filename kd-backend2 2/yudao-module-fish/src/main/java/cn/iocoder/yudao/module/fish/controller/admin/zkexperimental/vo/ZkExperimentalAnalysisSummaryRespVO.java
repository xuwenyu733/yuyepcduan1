package cn.iocoder.yudao.module.fish.controller.admin.zkexperimental.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 实验数据分析汇总 Response VO")
@Data
public class ZkExperimentalAnalysisSummaryRespVO {

    @Schema(description = "进行中实验数")
    private Integer ongoingExperiments;

    @Schema(description = "平均增重率 %")
    private BigDecimal avgWeightGainRate;

    @Schema(description = "平均成活率 %")
    private BigDecimal avgSurvivalRate;

    @Schema(description = "预警实验数")
    private Integer warningCount;

    @Schema(description = "生长趋势")
    private List<GrowthTrendItem> growthTrend;

    @Schema(description = "成活率对比")
    private List<SurvivalCompareItem> survivalCompare;

    @Data
    public static class GrowthTrendItem {
        @Schema(description = "日期 yyyy-MM-dd")
        private String date;
        @Schema(description = "平均体重 g")
        private BigDecimal weight;
    }

    @Data
    public static class SurvivalCompareItem {
        @Schema(description = "实验编号")
        private String experimentNo;
        @Schema(description = "成活率 %")
        private BigDecimal rate;
    }

}
