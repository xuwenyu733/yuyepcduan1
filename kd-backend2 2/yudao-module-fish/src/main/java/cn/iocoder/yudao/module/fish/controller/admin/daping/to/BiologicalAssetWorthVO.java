package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BiologicalAssetWorthVO {

    private String fishType;
    private String fishName;
    private BigDecimal worth;

}
