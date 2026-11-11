package cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ZkGrowDashboardRespVO {
    private Integer breedingDays;
    private BigDecimal currentWeight;
    private BigDecimal currentLength;
    private BigDecimal waterTemp;
    private BigDecimal feedRate;
    private BigDecimal sensorTemp;
    private BigDecimal actualFeedRate;
    /** 大模型推荐投饵率 % */
    private BigDecimal recommendedFeedRate;
    private BigDecimal recommendedFeedDiffPercent;

    private BigDecimal dissolvedOxygen;
    private BigDecimal ammonia;
    private BigDecimal nitrite;
    private BigDecimal ph;

    private BigDecimal weightGainPerDay;
    private BigDecimal lengthGainPerDay;
    private BigDecimal sgr;

    private Integer maxDays;

    /** 发育良好 / 正常 / 缓慢 */
    private String growthStatus;
    private String aiAnalysisSummary;
    private List<ZkGrowPondAiItemVO> pondAiItems;
    private Boolean needAlarm;

    private BigDecimal nutritionFactor;
    private String nutritionRemark;

    /** 生物性资产（吨） */
    private BigDecimal biologyAsset;

    private List<ZkGrowDailyRecordRespVO> predictions;
}
