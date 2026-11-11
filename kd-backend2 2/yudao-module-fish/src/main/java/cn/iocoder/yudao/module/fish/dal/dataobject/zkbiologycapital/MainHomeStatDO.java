package cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital;

import lombok.Data;
import java.math.BigDecimal;

public class MainHomeStatDO {

    @Data
    public static class BiologyCapital {
        private String pondDetailNum;
        private BigDecimal totalMount;
    }

    @Data
    public static class SurvivalRate {
        private String pondDetailNum;
        private BigDecimal survivalRate;
    }

    @Data
    public static class PrecisionFeeding {
        private Integer year;
        private Integer month;
        private Integer day;
        private BigDecimal totalFeedKg;
    }

    @Data
    public static class FishGrowth {
        private String logDay;
        private BigDecimal avgFishSpec;
    }

    @Data
    public static class BaitCoefficient {
        private String pondDetailNum;
        private BigDecimal avgBaitCoefficient;
    }
}
