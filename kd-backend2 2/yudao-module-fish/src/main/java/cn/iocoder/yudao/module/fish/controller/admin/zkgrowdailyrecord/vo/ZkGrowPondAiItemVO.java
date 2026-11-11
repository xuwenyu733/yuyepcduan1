package cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ZkGrowPondAiItemVO {
    private Long pondId;
    private String pondName;
    private String pondDetailNum;
    private BigDecimal currentWeight;
    private BigDecimal predictedWeight;
    private BigDecimal deviationPercent;
    /** 发育良好 / 正常 / 缓慢 */
    private String growthStatus;
    private String remark;
}
