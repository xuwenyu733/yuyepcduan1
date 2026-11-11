package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.device;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-09-10
 * @描述
 */
@Data
@NoArgsConstructor
public class SwitchStatus4IOT {

    private String bcx1Index; // 开关序号
    private String bcx1Out; // 开关状态

    public SwitchStatus4IOT(String bcx1Index, String bcx1Out) {
        this.bcx1Index = bcx1Index;
        this.bcx1Out = bcx1Out;
    }
}
