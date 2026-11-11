package cn.iocoder.yudao.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2024-05-13
 * @描述
 */
@Getter
@AllArgsConstructor
public enum DeviceTypeEnum {

    PC("电脑", "pc", "")
    ,APP("手机", "app", "")
    ,MAC("原生安卓", "mac", "")

    ;

    private final String deviceName;
    private final String deviceType;
    private final String remark;
}
