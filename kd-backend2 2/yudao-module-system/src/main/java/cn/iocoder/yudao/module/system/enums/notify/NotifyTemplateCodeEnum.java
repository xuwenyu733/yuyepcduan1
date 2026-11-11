package cn.iocoder.yudao.module.system.enums.notify;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知模板code枚举
 *
 */
@Getter
@AllArgsConstructor
public enum NotifyTemplateCodeEnum {

    GENERAL_TEMPLATE("GENERAL_TEMPLATE", "通用通知模版"),

    OTHER_TEMPLATE("OTHER_TEMPLATE", "其他通知模版");

    private final String code;
    private final String name;

}
