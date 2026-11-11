package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
@Getter
@AllArgsConstructor
public enum DeviceWarnType4IOTEnum {


    OXYGEN_COMMUNICATION_WARN("0", "溶氧传感器通讯故障")
    ,OXYGEN_WARN("1", "溶氧故障")
    ,TEMPERATURE_WARN("2", "温度故障")
    ;

    /**
     * 状态值
     */
    private final String warnType;
    /**
     * 状态名
     */
    private final String warnInfo;

}
