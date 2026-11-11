package cn.iocoder.yudao.module.fish.controller.admin.zktask.to.params;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @创建人 lr
 * @创建时间 2025-08-06
 * @描述
 */
@Data
@NoArgsConstructor
public class ProofingParam {

    private Integer tableIndex;
    private BigDecimal tableBodyLength;
    private BigDecimal tableBodyWeight;

    public ProofingParam(Integer tableIndex, BigDecimal tableBodyLength, BigDecimal tableBodyWeight) {
        this.tableIndex = tableIndex;
        this.tableBodyLength = tableBodyLength;
        this.tableBodyWeight = tableBodyWeight;
    }
}
