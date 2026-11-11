package cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "精准投喂推荐结果")
@Data
public class PrecisionFeedRecommendRespVO {

    private Long pondId;
    private String pondName;
    private Integer fishNum;
    private BigDecimal averageWeightG;
    private BigDecimal density;
    private String healthLevel;
    private BigDecimal healthScore;
    private Integer todayDeathCount;
    private BigDecimal waterVolume;

    private BigDecimal temperature;
    private BigDecimal dissolvedOxygen;
    private BigDecimal ammonia;
    private BigDecimal nitrite;
    private BigDecimal ph;
    private Boolean waterFromSensor;

    private BigDecimal totalBiomassKg;
    private BigDecimal baseFeedRate;
    private BigDecimal temperatureCoefficient;
    private BigDecimal doCoefficient;
    private BigDecimal waterQualityCoefficient;
    private BigDecimal feedingBehaviorCoefficient;
    private BigDecimal dailyFeedAmountKg;
    private BigDecimal singleFeedAmountKg;
    private Integer dailyFrequency;
    private String feedType;
    private String recommendation;
    private List<String> warnings;

    private BigDecimal todayActualFeedKg;
    private BigDecimal yesterdayFeedKg;
    private BigDecimal feedDiffPercent;
}
