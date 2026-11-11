package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.warn;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-09-10
 * @描述
 */
@Data
@NoArgsConstructor
public class MessageDetail4IOT {

    private Integer test0; // 溶氧传感器通讯故障
    private Integer test1; // 溶氧故障
    private Integer test2; // 温度故障

}
