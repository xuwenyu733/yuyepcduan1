package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GrowthCurveItemVO {

    private LocalDate staticDay;
    private BigDecimal fishGainCount;

}
