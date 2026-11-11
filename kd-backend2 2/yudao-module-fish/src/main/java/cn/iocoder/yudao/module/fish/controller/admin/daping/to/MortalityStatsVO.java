package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MortalityStatsVO {

    private Long dayDead;
    private BigDecimal dayDeadPer;
    private Long sumDead;
    private BigDecimal sumDeadPer;
    private Long fishSum;

}
