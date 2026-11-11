package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeedCoefficientVO {

    private BigDecimal feedCount;
    private BigDecimal accumulatedWeightGain;
    private Long sumDead;
    private BigDecimal initFishWeight;

}
