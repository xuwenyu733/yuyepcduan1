package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrecisionFeedingVO {

    private BigDecimal totalWeight;
    private BigDecimal dayFeedCount;
    private BigDecimal feedRate;

}
