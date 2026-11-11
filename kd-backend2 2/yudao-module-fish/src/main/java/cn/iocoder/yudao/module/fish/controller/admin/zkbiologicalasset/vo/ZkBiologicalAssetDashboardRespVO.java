package cn.iocoder.yudao.module.fish.controller.admin.zkbiologicalasset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 生物资产评估仪表盘 Response VO")
@Data
public class ZkBiologicalAssetDashboardRespVO {

    @Schema(description = "总资产（仅生物性资产-鱼类）")
    private TotalAssets totalAssets;

    @Schema(description = "鱼类品种资产分布")
    private List<FishSpecies> fishBySpecies;

    @Schema(description = "各鱼池资产价值")
    private List<PondProductivity> pondProductivity;

    @Schema(description = "饲料消耗趋势")
    private List<FeedConsumption> feedConsumption;

    @Schema(description = "关键运营指标")
    private KeyPerformanceIndicators kpi;

    @Schema(description = "实际采用的日快照日期 yyyy-MM-dd（往期存档回溯）")
    private String snapshotDate;

    @Schema(description = "请求的截止日期 yyyy-MM-dd")
    private String asOfDate;

    @Schema(description = "请求的开始日期 yyyy-MM-dd（统计周期起）")
    private String startDate;

    @Data
    public static class KeyPerformanceIndicators {
        private BigDecimal avgLive;
        private BigDecimal feedConversionRatio;
        private BigDecimal waterRecyclingRate;
        private BigDecimal dailyWeightGain;
        private String estimatedHarvestTime;
    }

    // 需求 4.5：总资产仅展示生物性资产（鱼类），移除 feed/equipment 冗余字段
    @Data
    public static class TotalAssets {
        private BigDecimal fish;
        private BigDecimal total;
        private BigDecimal fishCount;
    }

    @Data
    public static class FishSpecies {
        private String name;
        private BigDecimal value;
        private BigDecimal quantity;
    }

    @Data
    public static class PondProductivity {
        private String name;
        private BigDecimal fish;
        private BigDecimal capacity;
        private String fishSpec;
        private Long fishNum;
        private String workshopName;
        private String pondSystemName;
        private String batchNumber;
        private String fishTypes;
        private String fishName;
    }

    @Data
    public static class FeedConsumption {
        private String month;
        private BigDecimal amount;
    }
}