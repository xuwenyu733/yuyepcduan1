package cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ZkGrowRealStatsDO {
    private Long pondId;
    private String batchNumber;
    private String pondNum;
    private String pondDetailNum;
    private LocalDateTime firstDay;
    private Integer breedingDays;
    private String fishSpec; // 当前体重
    private String initialWeightSpec; // 初始体重
    private BigDecimal avgBodyLength; // 当前体长
    private BigDecimal baitingRate; // 投饵率
    private BigDecimal temperature; // 温度
}
