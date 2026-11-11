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
public enum StatisticsTypeEnum {

    NOT_HANDLED("未统计", "0", "")
    ,HAD_HANDLED("已统计", "1", "")

    ;

    private final String label;
    private final String value;
    private final String remark;
}
