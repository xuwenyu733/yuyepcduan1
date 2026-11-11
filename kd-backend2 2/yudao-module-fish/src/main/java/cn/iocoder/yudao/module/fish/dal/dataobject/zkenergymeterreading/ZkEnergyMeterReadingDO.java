package cn.iocoder.yudao.module.fish.dal.dataobject.zkenergymeterreading;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 电度表抄表记录 DO（kWh；与经营成本/净化成本表并存）
 */
@TableName("zk_energy_meter_reading")
@KeySequence("zk_energy_meter_reading_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkEnergyMeterReadingDO extends BaseDO {

    @TableId
    private Long id;
    private String deviceId;
    private String deviceName;
    /** 设备类型：水泵/增氧机/温控器… */
    private String deviceType;
    private String workshopName;
    private String pondDetailNum;
    private String meterSn;
    private LocalDateTime readingTime;
    private BigDecimal kwhValue;
    private BigDecimal deltaKwh;
    private String remark;
    private Long deptId;
    private Long tenantId;
}
