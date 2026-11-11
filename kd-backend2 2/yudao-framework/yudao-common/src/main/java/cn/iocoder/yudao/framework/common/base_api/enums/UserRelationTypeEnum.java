package cn.iocoder.yudao.framework.common.base_api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户关联数据类型枚举
 * @创建人 lr
 * @创建时间 2024-05-13
 * @描述
 */
@Getter
@AllArgsConstructor
public enum UserRelationTypeEnum {

    GE_TUI("个推", 1)

    ;

    private final String name;
    private final Integer relationType;
}
