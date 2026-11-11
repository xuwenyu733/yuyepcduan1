package cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
@Getter
@AllArgsConstructor
public enum WarnTypeEnum {


    ENV("0", "环境告警")
    ,THING("1", "事故告警")
    ,ILL("2", "病虫告警")
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
