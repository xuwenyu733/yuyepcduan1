package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SalePo {

    private String saleTime; // 销售时间
    private Long saleFishNum; // 销售数量
    private BigDecimal saleFishSpec; // 销售规格
    private BigDecimal saleFishWeight; // 销售重量
    private String purchaseDirection; // 销售方向

    public SalePo(String saleTime, Long saleFishNum, BigDecimal saleFishWeight, BigDecimal saleFishSpec, String purchaseDirection) {
        this.saleTime = saleTime;
        this.saleFishNum = saleFishNum;
        this.saleFishSpec = saleFishSpec;
        this.saleFishWeight = saleFishWeight;
        this.purchaseDirection = purchaseDirection;
    }
}
