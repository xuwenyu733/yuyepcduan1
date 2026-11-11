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
public enum Property4IOTEnum {


    OXYGEN("do_oxygen_", "溶氧传感器通讯故障", "do_oxygen_(\\d+)")
    ,TEMPERATURE("do_temp_", "溶氧故障", "do_temp_(\\d+)")
    ,SATURATION("do_oxyper_", "温度故障", "do_oxyper_(\\d+)")
    ,PH("ph_ph_", "ph", "ph_ph_(\\d+)")

    ,SWITCH("bcx1_", "开关", "bcx1_(\\d+)_out(\\d+)")

    ;

    /**
     * 状态值
     */
    private final String propertyEn;
    /**
     * 状态名
     */
    private final String propertyCh;
    private final String reg;

}
