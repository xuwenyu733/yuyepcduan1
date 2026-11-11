package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DaPingOverview3RespVO {

    private BigDecimal totalWorth;
    private List<BiologicalAssetWorthVO> biologicalAssets;
    private List<PondHealthStatusVO> healthList;
    private MortalityStatsVO mortalityStats;
    private PrecisionFeedingVO precisionFeeding;
    private List<GrowthCurveItemVO> growthCurve;
    private FeedCoefficientVO feedCoefficient;

}
