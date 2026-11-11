package cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ZkGrowManualMeasureDO {
    private Long pondId;
    private BigDecimal avgBodyLength; // 对应SQL的avg_body_length (按用户标注：体重)
    private BigDecimal avgSpecs;      // 对应SQL的avg_specs (按用户标注：体长)
    private LocalDateTime createTime; // 打样时间
}
