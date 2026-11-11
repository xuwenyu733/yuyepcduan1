package cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset;

import lombok.Data;
import java.math.BigDecimal;

public class DashboardStatDO {

    // 需求 4.5：总资产仅统计生物性资产（鱼类），移除 feed/device 字段
    @Data
    public static class TotalAssets {
        private BigDecimal fishTotalWeight;
        private BigDecimal fishTotalMount;
        private BigDecimal fishTotalCount;
    }

    @Data
    public static class FishSpecies {
        private BigDecimal totalCount;
        private String fishName;
    }

    @Data
    public static class PondProductivity {
        private String pondDetailNum;
        private BigDecimal totalWeight;
        private BigDecimal totalMount;
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
        private Integer year;
        private Integer month;
        private BigDecimal totalFeedKg;
    }
}