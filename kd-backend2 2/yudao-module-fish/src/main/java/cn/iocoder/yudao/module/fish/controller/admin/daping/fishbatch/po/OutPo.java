package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class OutPo {

    private String outTime; // 转出时间
    private Long outQuantity; // 转出数量
    private String pondDetailNum;
    private BigDecimal outFishWeight; // 转入重量
    private BigDecimal outFishSpec; // 转出规格
    private BigDecimal outBaitCoefficient; // 转出饵料系数

    public OutPo(String outTime, Long outQuantity, BigDecimal outFishWeight, BigDecimal outFishSpec, BigDecimal outBaitCoefficient) {
        this.outTime = outTime;
        this.outQuantity = outQuantity;
        this.outFishWeight = outFishWeight;
        this.outFishSpec = outFishSpec;
        if(!EasyBigDecimalUtil.ge0(outBaitCoefficient)) {
            outBaitCoefficient = new BigDecimal("1.1");
        }
        this.outBaitCoefficient = outBaitCoefficient;
    }
}
